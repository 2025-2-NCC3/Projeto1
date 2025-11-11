package com.example.ifoodclone.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    private String token;
    private String message;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("is_admin")
    private boolean isAdmin;

    public String getToken() { return token; }
    public String getMessage() { return message; }
    public int getUserId() { return userId; }
    public boolean isAdmin() { return isAdmin; }

    public void setToken(String token) { this.token = token; }
    public void setMessage(String message) { this.message = message; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
}
