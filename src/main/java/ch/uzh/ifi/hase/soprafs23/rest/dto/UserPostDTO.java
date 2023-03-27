package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.Date;

public class UserPostDTO {

    private String username;
    private String password; // 'name'
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
