package com.example.security.security.config.auth.dto;

import com.example.security.security.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String role;

    public SessionUser(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}