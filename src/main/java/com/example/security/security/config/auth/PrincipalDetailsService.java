package com.example.security.security.config.auth;

import com.example.security.security.config.LoginUser;
import com.example.security.security.config.auth.dto.SessionUser;
import com.example.security.security.model.User;
import com.example.security.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 시큐리티 설정에서 loginProcessingUrl("/login")
 * login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어있는 loadUserByUsername 함수 실행
 */

@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 함수 종료시 @AuthenticationPrincipal 이 만들어짐
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("no username");
        }

        SessionUser loginUser = new SessionUser(user);

        return new PrincipalDetails(loginUser);
    }
}
