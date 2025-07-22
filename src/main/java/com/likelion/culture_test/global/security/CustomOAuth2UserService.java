package com.likelion.culture_test.global.security;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

import com.likelion.culture_test.domain.user.entity.SsoProvider;
import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase(Locale.ROOT);

        SsoProvider provider = SsoProvider.valueOf(registrationId);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String socialId = null;
        String nickname = null;
        String profileImageUrl = null;

        if (provider == SsoProvider.KAKAO) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            socialId = registrationId + "_" + oAuth2User.getName();
            nickname = (String) profile.get("nickname");
            profileImageUrl = (String) profile.get("profile_image_url");
        }else if (provider == SsoProvider.GOOGLE) {
            socialId = registrationId + "_" + (String) attributes.get("sub"); // ex: GOOGLE_123456789
            nickname = (String) attributes.get("name");
            profileImageUrl = (String) attributes.get("picture");
        }
        final String finalSocialId = socialId;
        final String finalNickname = nickname;
        final String finalProfileImageUrl = profileImageUrl;

        // 유저 조회 또는 생성
        User user = userRepository.findBySocialId(finalSocialId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .socialId(finalSocialId)
                            .nickname(finalNickname)
                            .profileImageUrl(finalProfileImageUrl)
                            .ssoProvider(provider)
                            .build();
                    return userRepository.save(newUser);
                });

        return new SecurityUser(user);
    }
}
