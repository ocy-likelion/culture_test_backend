//package com.likelion.culture_test.global.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//public class CustomAuthenticationFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        // jwt꺼내고 인증처리
//        filterChain.doFilter(request, response); // 다음 필터로
//    }
//}
package com.likelion.culture_test.global.security;

import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.domain.user.repository.UserRepository;
import com.likelion.culture_test.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // "Bearer " 이후 토큰 추출
            try {
                // 토큰 유효성 검증
                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.getUserIdFromToken(token);

                    User user = userRepository.findById(userId)
                            .orElseThrow(()-> new RuntimeException("사용자 없음"));

                    SecurityUser securityUser = new SecurityUser(user);

                    // 인증 객체 생성 및 SecurityContext에 등록
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(securityUser, null,securityUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // JwtUtil.validateToken 또는 getUserIdFromToken에서 예외 발생 시
                // ex) MalformedJwtException, ExpiredJwtException, SignatureException 등
                logger.warn("Invalid JWT Token: ", e); // 로그를 남겨서 디버깅에 활용
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답
                return; // 필터 체인 중단
            }
        }

        filterChain.doFilter(request, response);
    }
}
