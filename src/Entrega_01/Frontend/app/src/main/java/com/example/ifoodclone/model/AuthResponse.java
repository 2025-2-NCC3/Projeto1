package com.example.ifoodclone.model;

public class AuthResponse {
    private String token;
    private String nome;
    private String email;

    public String getToken() { return token; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }

    public void setToken(String token) { this.token = token; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
}
