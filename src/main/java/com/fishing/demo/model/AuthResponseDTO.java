package com.fishing.demo.model;

public class AuthResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private String username;

    public AuthResponseDTO(String token, String username) {
        this.token = token;
        this.username = username;
    }

    // getters & setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
