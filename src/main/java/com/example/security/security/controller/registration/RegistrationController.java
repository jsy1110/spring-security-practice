package com.example.security.security.controller.registration;

import com.example.security.security.controller.dto.JoinRequestDto;
import com.example.security.security.controller.dto.JoinResponseDto;
import com.example.security.security.domain.user.User;
import com.example.security.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @GetMapping("/certifyForm")
    public String certifyForm() {
        return "certifyForm";
    }

    @PostMapping("/join")
    @ResponseBody
    public Long join(JoinRequestDto userDto) {

        String encodePassword = bCryptPasswordEncoder.encode(userDto.getPassword());
        User user = new User(userDto);
        user.registration(encodePassword);

        return userRepository.save(user).getId();
    }

    @PostMapping("/guest/certification")
    @Transactional
    @ResponseBody
    public JoinResponseDto certify(String email, String phone) {
        User user = userRepository.findByEmail(email).orElseThrow (
                () -> {
                    throw new RuntimeException("유저를 찾을 수 없습니다.");
                });
        user.certifyPhone(phone);

        System.out.println("user = " + user);

        return new JoinResponseDto(user);
    }
}
