package com.likelion.culture_test.domain.user.controller;


import com.likelion.culture_test.domain.user.entity.SsoProvider;
import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.domain.user.entity.UserRole;
import com.likelion.culture_test.domain.user.repository.UserRepository;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import com.likelion.culture_test.global.rq.Rq;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Profile("local") // 이건 dev 환경에서만 작동하도록
public class DevOAuth2Controller {

    private final Rq rq;
    private final UserRepository userRepository;

    @PostMapping("/dev-login")
    public Map<String, Object> devLogin(HttpServletResponse response) {
        // ✅ 더미 유저 객체 생성 (DB 저장 X)
        User dummyUser = userRepository.findBySocialId("dev-kakao-9999")
                .orElseGet(() -> userRepository.save(User.builder()
                        .nickname("USER_DEV")
                        .ssoProvider(SsoProvider.KAKAO)
                        .socialId("dev-kakao-9999")
                        .role(UserRole.USER)
                        .profileImageUrl("http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg")
                        .build()));


        // ✅ JWT 쿠키 발급 + 인증 처리
        rq.makeAuthCookies(dummyUser);  // accessToken + refreshToken 쿠키 발급
        rq.setLogin(dummyUser);         // SecurityContext 등록

        return Map.of(
                "message", "✅ 더미 유저로 로그인 성공!",
                "userId", dummyUser.getId(),
                "nickname", dummyUser.getNickname()
        );
    }
}
