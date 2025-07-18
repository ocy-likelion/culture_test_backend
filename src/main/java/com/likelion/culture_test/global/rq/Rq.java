package com.likelion.culture_test.global.rq;

import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.domain.user.repository.UserRepository;
import com.likelion.culture_test.global.security.SecurityUser;
import com.likelion.culture_test.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 인증 객체 등록
    public void setLogin(User user) {
        System.out.println("🔐 SecurityContext 등록: " + user.getNickname());
        SecurityUser userDetails = new SecurityUser(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 현재 로그인 유저 조회
    public User getActor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(p -> p instanceof SecurityUser)
                .map(p -> (SecurityUser) p)
                .map(SecurityUser::getUser)
                .orElse(null);
    }

    // 쿠키 발급 (기본: HttpOnly + Secure)
    public void setCookie(String name, String value, boolean httpOnly, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(true);
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    // 쿠키에서 값 조회
    public String getCookieValue(String name) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(c -> c.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    // 쿠키 삭제
    public void deleteCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        response.addCookie(cookie);
    }

    // AccessToken + RefreshToken 쿠키 발급
    public void makeAuthCookies(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // DB에 refreshToken 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        setCookie("accessToken", accessToken, false, 60 * 30);           // 일반 쿠키
        setCookie("refreshToken", refreshToken, true, 60 * 60 * 24 * 14); // HttpOnly
    }

    // 요청에서 토큰 꺼내기
    public record AuthTokens(String refreshToken, String accessToken) {}

    public AuthTokens getAuthTokensFromRequest() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return new AuthTokens(null, authHeader.substring(7));
        }

        String refreshToken = getCookieValue("refreshToken");
        String accessToken = getCookieValue("accessToken");

        return new AuthTokens(refreshToken, accessToken);
    }


//    //로그 찍어서 확인
//    public User getUserByAccessToken(String accessToken) {
//        System.out.println("🔑 accessToken 확인: " + accessToken);
//
//        boolean valid = jwtUtil.validateToken(accessToken);
//        System.out.println("✅ accessToken 유효함? " + valid);
//
//        if (accessToken == null || !valid) return null;
//
//        Long userId = jwtUtil.getUserId(accessToken);
//        System.out.println("👤 AccessToken → userId: " + userId);
//
//        return userRepository.findById(userId)
//                .map(user -> {
//                    System.out.println("✅ DB에서 user 조회 성공: " + user.getNickname());
//                    return user;
//                })
//                .orElseGet(() -> {
//                    System.out.println("❌ userId에 해당하는 유저 없음");
//                    return null;
//                });
//    }

    public User getUserByAccessToken(String accessToken) {
        if (accessToken == null || !jwtUtil.validateToken(accessToken)) return null;

        Long userId = jwtUtil.getUserId(accessToken);
        return userRepository.findById(userId).orElse(null);
    }

    // RefreshToken 기반 AccessToken 재발급
    public User refreshAccessTokenByRefreshToken(String refreshToken) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) return null;

        Long userId = jwtUtil.getUserId(refreshToken);
        User user = userRepository.findById(userId).orElse(null);

        if (user == null || !refreshToken.equals(user.getRefreshToken())) return null;

        String newAccessToken = jwtUtil.generateAccessToken(userId);
        setCookie("accessToken", newAccessToken, false, 60 * 30);

        return user;
    }
//    //로그 찍어서 확인
//    public User refreshAccessTokenByRefreshToken(String refreshToken) {
//
//
//        System.out.println("🔁 refreshToken 시도: " + refreshToken);
//        boolean valid = jwtUtil.validateToken(refreshToken);
//        System.out.println("✅ refreshToken 유효함? " + valid);
//        if (!valid) return null;
//
//
//        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) return null;
//
//        Long userId = jwtUtil.getUserId(refreshToken);
//        System.out.println("👤 refreshToken → userId = " + userId);
//        User user = userRepository.findById(userId).orElse(null);
//        if (user == null) {
//            System.out.println("❌ 해당 user 없음");
//            return null;
//        }
//
//        if (!refreshToken.equals(user.getRefreshToken())) {
//            System.out.println("❌ DB 저장된 refreshToken과 일치하지 않음");
//            return null;
//        }
//
//        if (user == null || !refreshToken.equals(user.getRefreshToken())) return null;
//
//        // 새 AccessToken 발급
//        String newAccessToken = jwtUtil.generateAccessToken(userId);
//        System.out.println("✅ refresh 성공 → AccessToken 재발급");
//        setCookie("accessToken", newAccessToken, false, 60 * 30); // 갱신된 accessToken 쿠키로
//
//        return user;
//    }
}
