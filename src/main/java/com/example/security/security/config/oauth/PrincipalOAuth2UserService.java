package com.example.security.security.config.oauth;

import com.example.security.security.config.auth.PrincipalDetails;
import com.example.security.security.config.oauth.provider.FacebookUserInfo;
import com.example.security.security.config.oauth.provider.GoogleUserInfo;
import com.example.security.security.config.oauth.provider.NaverUserInfo;
import com.example.security.security.config.oauth.provider.Oauth2UserInfo;
import com.example.security.security.model.User;
import com.example.security.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    // 함수 종료시 @AuthenticationPrincipal 이 만들어짐
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("getAccessToken : {}", userRequest.getClientRegistration());
        log.info("getAccessToken : {}", userRequest.getAccessToken());
        log.info("getAttributes : {}", super.loadUser(userRequest).getAttributes());

        /**
         * {getAttributes}
         * sub=101610285939673525081, => username = "google_101610285939673525081"
         * name=승빵님, given_name=님, family_name=승빵,
         * picture=https://lh3.googleusercontent.com/a-/AOh14GiSH8uOWlpoqhwushsmCY1_LUQcvROmFJe67LzPWQ=s96-c,
         * email=jsy11101@gmail.com, email_verified=true, locale=ko
         */

        OAuth2User oauth2User = super.loadUser(userRequest);
        System.out.println("oauth2User.getAttributes() = " + oauth2User.getAttributes());
        
        Oauth2UserInfo oauth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            log.info("구글 로그인 요청");
            oauth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            log.info("페이스북 로그인 요청");
            oauth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            log.info("네이버 로그인 요청");
            oauth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
        }else {
            System.out.println("우린 구글 페이스북만 지원합니다.");
        }

        String provider = oauth2UserInfo.getProvider();
        String providerId = oauth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String email = oauth2UserInfo.getEmail();
        //String password = bCryptPasswordEncoder.encode("비밀번호");
        String role = "ROLE_USER";

        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = User.builder()
                    .username(username)
                    .password("password")
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(user);
        }

        return new PrincipalDetails(user, oauth2User.getAttributes());
    }
}
