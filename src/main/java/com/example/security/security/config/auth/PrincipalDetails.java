package com.example.security.security.config.auth;

import com.example.security.security.config.auth.dto.SessionUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
 * 로그인 진행이 완료되면 시큐리티 session을 만들어준다. (Security ContextHolder)
 * 오브젝트 타입 => Authentication 타입 객체
 * Authentication 안에 User 정보가 있어야 됨
 * User 오브젝트타입 => UserDetails 타입 객체
 *
 * Spring Security => Authentication => UserDetails(PrincipalDetails)
 */

// UserDetails 일반 로그인, OAuth2User 구글 로그인
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private SessionUser user;
    private Map<String, Object> attributes;

    // 일반 로그인
    public PrincipalDetails(SessionUser user) {
        this.user = user;
    }

    // OAuth 로그인
    public PrincipalDetails(SessionUser user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 해당 User 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // ex) 1년동안 회원이 로그인을 안할경우 휴면 계정으로 변경
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }
}
