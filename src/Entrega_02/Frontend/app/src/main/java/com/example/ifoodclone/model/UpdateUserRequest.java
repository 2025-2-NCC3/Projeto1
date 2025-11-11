package com.example.ifoodclone.model;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    @SerializedName("username")
    @Nullable
    private String username;

    @SerializedName("email")
    @Nullable
    private String email;

    @SerializedName("password")
    @Nullable
    private String password;

    public UpdateUserRequest() {}

    public UpdateUserRequest(@Nullable String username,
                             @Nullable String email,
                             @Nullable String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Nullable public String getUsername() { return username; }
    public void setUsername(@Nullable String username) { this.username = username; }

    @Nullable public String getEmail() { return email; }
    public void setEmail(@Nullable String email) { this.email = email; }

    @Nullable public String getPassword() { return password; }
    public void setPassword(@Nullable String password) { this.password = password; }
}
