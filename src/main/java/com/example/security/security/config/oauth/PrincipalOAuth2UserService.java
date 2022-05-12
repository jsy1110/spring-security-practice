package com.example.security.security.config.oauth;

import com.example.security.security.config.auth.PrincipalDetails;
import com.example.security.security.config.auth.dto.OAuthAttributes;
import com.example.security.security.config.auth.dto.SessionUser;
import com.example.security.security.domain.user.User;
import com.example.security.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    // 함수 종료시 @AuthenticationPrincipal 이 만들어짐
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        /**
         * {getAttributes}
         * sub=101610285939673525081, => username = "google_101610285939673525081"
         * name=승빵님, given_name=님, family_name=승빵,
         * picture=https://lh3.googleusercontent.com/a-/AOh14GiSH8uOWlpoqhwushsmCY1_LUQcvROmFJe67LzPWQ=s96-c,
         * email=jsy11101@gmail.com, email_verified=true, locale=ko
         */

        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oauth2User.getAttributes());
        User user = saveOrUpdate(attributes);

        SessionUser sessionUser = new SessionUser(user);
        httpSession.setAttribute("user", sessionUser);

        return new PrincipalDetails(sessionUser, oauth2User.getAttributes());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByUsername(attributes.getUsername())
                .map(entity -> entity.update(attributes.getNickname(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
