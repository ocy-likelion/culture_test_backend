
package com.likelion.culture_test.global.security;


import com.likelion.culture_test.global.rq.Rq;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final Rq rq;



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println("🔥 CustomAuthenticationFilter 실행됨! URI = " + request.getMethod() + " " + request.getRequestURI());

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("⚙️ OPTIONS 요청 → 인증 필터 통과");
//            filterChain.doFilter(request, response);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }


        // 필터 제외 경로 설정 (swagger, 로그인 등)
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/") ||
                uri.startsWith("/oauth2/authorization") ||
                uri.startsWith("/login/oauth2/code") ||
                List.of("/api/auth/login", "/api/auth/register").contains(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        Rq.AuthTokens authTokens = rq.getAuthTokensFromRequest();
        if (authTokens == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 인증 시도
        Optional.ofNullable(rq.getUserByAccessToken(authTokens.accessToken()))
                .or(() -> {
                    System.out.println("⚠️ AccessToken 유효하지 않음 → RefreshToken으로 재시도");
                    return Optional.ofNullable(rq.refreshAccessTokenByRefreshToken(authTokens.refreshToken()));
                })
                .ifPresentOrElse(
                        rq::setLogin,
                        () -> System.out.println("❌ 인증 실패 → SecurityContext 미등록")
                );

        filterChain.doFilter(request, response);

    }
}
