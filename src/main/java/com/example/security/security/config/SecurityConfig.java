package com.example.security.security.config;

import com.example.security.security.config.oauth.PrincipalOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)  // @Secured 활성화, @PreAuthorize, @PostAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOAuth2UserService oauth2UserService;

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")  // login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인 진행
                .defaultSuccessUrl("/")
                /**
                 * 1. 코드받기(인증)
                 * 2. 엑세스 토큰(권한)
                 * 3. 사용자 프로필 정보를 가져옴
                 * 4-1. 가져온 정보 그대로 회원가입 or 4-2. 추가 정보(집주소 등) 입력하게 하고 회원가입
                 * Tip. 구글 로그인이 완료되면 (코드X, 엑세스토큰 + 사용자프로필정보 받게 됨)
                 */
                .and()
                .oauth2Login()
                .loginPage("/loginForm")    // 구글 로그인이 완료된 뒤의 후처리 필요
                .userInfoEndpoint()
                .userService(oauth2UserService)
        ;
    }
}
