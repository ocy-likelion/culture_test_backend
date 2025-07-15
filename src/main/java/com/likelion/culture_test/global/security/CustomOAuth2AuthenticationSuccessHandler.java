package com.likelion.culture_test.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.culture_test.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 소셜 로그인 완료 후 사용자 정보 꺼내기
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        // JWT 발급
        String jwt = jwtUtil.generateToken(securityUser.getUser().getId());

        var tokenResponse = Map.of(
                "token", jwt,
                "message", "로그인 성공"
        );


        // json으로 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
    }
}
