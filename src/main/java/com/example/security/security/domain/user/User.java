package com.example.security.security.domain.user;

import com.example.security.security.controller.dto.JoinRequestDto;
import com.example.security.security.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String picture;

    private String phone;

    private String role;

    @Builder
    public User(String username, String password, String nickname, String email, String picture, String phone, String role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.picture = picture;
        this.phone = phone;
        this.role = role;
    }

    public User(JoinRequestDto userDto) {
        this.username = userDto.getUsername();
        this.password = userDto.getPassword();
        this.nickname = userDto.getNickname();
        this.email = userDto.getEmail();
        this.picture = userDto.getPicture();
        this.role = userDto.getRole();
    }

    public User update(String nickname, String picture) {
        this.nickname = nickname;
        this.picture = picture;

        return this;
    }

    public void registration(String password) {
        this.password = password;
        this.role = "ROLE_GUEST";
    }

    public void certifyPhone(String phone) {
        this.phone = phone;
        this.role = "ROLE_USER";
    }

}
