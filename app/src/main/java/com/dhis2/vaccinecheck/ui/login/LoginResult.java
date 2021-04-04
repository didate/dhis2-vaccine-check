package com.dhis2.vaccinecheck.ui.login;

import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.user.User;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private User success;

    @Nullable
    private String error;

    LoginResult(@Nullable String error) {
        this.error = error;
    }

    LoginResult(@Nullable User success) {
        this.success = success;
    }

    @Nullable
    User getSuccess() {
        return success;
    }

    @Nullable
    String getError() {
        return error;
    }
}