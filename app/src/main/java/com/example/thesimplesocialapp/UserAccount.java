package com.example.thesimplesocialapp;

public class UserAccount {
    private String profUrl;
    private String username;
    private String servername;
    private String token;

    public UserAccount(String profUrl, String username, String servername, String token) {
        this.profUrl = profUrl;
        this.username = username;
        this.servername = servername;
        this.token = token;
    }

    public String getProfUrl() {
        return profUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getServername() {
        return servername;
    }

    public String getToken() {
        return token;
    }
}
