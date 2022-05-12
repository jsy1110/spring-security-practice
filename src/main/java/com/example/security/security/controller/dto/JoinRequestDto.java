package com.example.security.security.controller.dto;

import lombok.Data;

@Data
public class JoinRequestDto {

    private final String username;
    private final String password;
    private final String email;

    private String nickname;
    private String picture;
    private String role;
}
