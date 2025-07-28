package com.livroponto.model;

public class JwtResponse {
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    // Opcionalmente, se usar frameworks que precisam, pode colocar o setter também
    public void setToken(String token) {
        this.token = token;
    }
}
