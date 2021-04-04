package com.dhis2.vaccinecheck.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dhis2.vaccinecheck.MainActivity;
import com.dhis2.vaccinecheck.R;
import com.dhis2.vaccinecheck.data.service.ActivityStarter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private Disposable disposable;

     TextInputEditText serverUrlEditText ;
     TextInputEditText usernameEditText ;
     TextInputEditText passwordEditText ;
     MaterialButton loginButton ;
     ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        serverUrlEditText = findViewById(R.id.urlText);
         usernameEditText = findViewById(R.id.usernameText);
        passwordEditText = findViewById(R.id.passwordText);
         loginButton = findViewById(R.id.loginButton);
        loadingProgressBar = findViewById(R.id.loginProgressBar);

        loginViewModel.getLoginFormState().observe(this, loginFormState-> {
            if (loginFormState == null) {
                return;
            }
                loginButton.setEnabled(loginFormState.isDataValid());
            if(loginFormState.getServerUrlError() != null){
                serverUrlEditText.setError(getString(loginFormState.getServerUrlError()));
            }
                if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
                if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult-> {
           if (loginResult == null) {
                return;
            }
           loadingProgressBar.setVisibility(View.GONE);
           if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
           if (loginResult.getSuccess() != null) {
               ActivityStarter.startActivity(this, MainActivity.getMainActivityIntent(this),true);
            }
            setResult(Activity.RESULT_OK);
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(
                        serverUrlEditText.getText().toString(),
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        serverUrlEditText.addTextChangedListener(afterTextChangedListener);
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return false;
            }
        });

        loginButton.setOnClickListener(v -> login());

    }

    private void login() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String serverUrl = serverUrlEditText.getText().toString();

        disposable = loginViewModel
                .login(username, password, serverUrl)
                .doOnTerminate(() -> loginButton.setVisibility(View.VISIBLE))
                .subscribe(u -> {}, t -> {});
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public static Intent getLoginActivityIntent(Context context){
        return new Intent(context, LoginActivity.class);
    }
}