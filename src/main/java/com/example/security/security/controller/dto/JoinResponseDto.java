package com.example.security.security.controller.dto;

import com.example.security.security.domain.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JoinResponseDto {
    private final Long id;
    private final String username;
    private final String email;
    private final String phone;

    public JoinResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
    }
}
