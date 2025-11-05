package com.example.ifoodclone.model;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;

    public RegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getNome() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
