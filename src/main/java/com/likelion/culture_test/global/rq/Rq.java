package com.likelion.culture_test.global.rq;

import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.domain.user.repository.UserRepository;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import com.likelion.culture_test.global.security.SecurityUser;
import com.likelion.culture_test.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.Optional;

import static com.likelion.culture_test.global.exceptions.ErrorCode.USER_NOT_FOUND;

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

    private String extractAccessToken() {
        String bearerToken = request.getHeader("Authorization");
        System.out.println(">>> Authorization 헤더: " + bearerToken);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            System.out.println(">>> Authorization에서 추출 성공");
            return bearerToken.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        System.out.println(">>> 쿠키 배열: " + Arrays.toString(cookies));

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(">>> 쿠키 이름: " + cookie.getName() + ", 값: " + cookie.getValue());
                if (cookie.getName().equals("accessToken")) {
                    System.out.println(">>> accessToken 쿠키에서 추출 성공");
                    return cookie.getValue();
                }
            }
        }

        // 3. 실패 로그
        System.out.println(">>> accessToken 추출 실패");
        return null;
    }

    public Long getUserIdFromToken() {
        String token = extractAccessToken();

        if (token == null || token.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        return jwtUtil.validateTokenAndGetUserId(token);
    }




    public User getUser() {
        Long userId = getUserIdFromToken(); // 예: JWT에서 추출
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }


    //리프레쉬 토큰 제거
    public void removeRefreshToken() {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    //어세스 토큰 제거
    public void removeAccessToken() {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

}
