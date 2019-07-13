package org.lonewolf2110.models.entity;

public class RequestEntity {
    private String username, password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RequestEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
