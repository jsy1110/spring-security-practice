package com.example.security.security.controller;

import com.example.security.security.config.LoginUser;
import com.example.security.security.config.auth.PrincipalDetails;
import com.example.security.security.config.auth.dto.SessionUser;
import com.example.security.security.model.User;
import com.example.security.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 통합했으므로 해당 세션 사용
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {  // DI
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        System.out.println("authentication.getPrincipal() = " + principalDetails.getUser());
//        System.out.println("userDetails = " + userDetails.getUsername());

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            log.info("Session 있음 : {}", user.getEmail());
        }

        return "세션 정보 확인";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth) {  // DI
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());
        System.out.println("oauth.getAttributes() = " + oauth.getAttributes());
        return "OAuth 세션 정보 확인";
    }

    @GetMapping("/user/welcome")
    public @ResponseBody String welcome(
            @AuthenticationPrincipal UserDetails userDetails,
            @LoginUser SessionUser user) {  // DI

        if (user != null) {
            log.info("Session 있음 : {}", user.getEmail());
        }

        return user.getNickname() + "님 환영합니다.";
    }

    @GetMapping("/session")
    public @ResponseBody SessionUser getSession(@LoginUser SessionUser user) {
        return user;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails.getUser() = " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    /**
     * SecurityConfig 설정 후 Controller 정상적으로 작동함
     */
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encodePassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encodePassword);

        userRepository.save(user);
        System.out.println("??");
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터정보";
    }

}
