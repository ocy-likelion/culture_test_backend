package com.likelion.culture_test.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import com.likelion.culture_test.global.rq.Rq;
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


    private final ObjectMapper objectMapper;
    private final Rq rq;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 소셜 로그인 완료 후 사용자 정보 꺼내기
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = securityUser.getUser();

        //쿠키발급 및 SecurityContext 로그인 처리
        rq.makeAuthCookies(user);
        rq.setLogin(user);

        String redirectUrl = request.getParameter("state");

        if(redirectUrl==null || redirectUrl.isEmpty()){
            throw new CustomException(ErrorCode.OAUTH_REDIRECT_URL_NOT_FOUND);
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"message\": \"리다이렉트 URL(state 파라미터)이 존재하지 않습니다.\"}");
//            return;
        }

        response.sendRedirect(redirectUrl);

    }
}
