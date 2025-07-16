package com.likelion.culture_test.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${custom.jwt.secretKey}")
    private String secret; // 최소 256bit (32자리 이상)

    // 만료 시간
    private final long ACCESS_TOKEN_EXPIRATION_MS = 1000 * 60 * 30;       // 30분
    private final long REFRESH_TOKEN_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 14; // 2주

    private Key key;

    @PostConstruct
    public void init(){
        this.key=Keys.hmacShaKeyFor(secret.getBytes());
    }


    // ✅ AccessToken 생성
    public String generateAccessToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ RefreshToken 생성
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    //토큰에서 사용자 id 추출
    public Long getUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            log.error("❌ 사용자 ID 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        log.info("🔍 validateToken() 호출됨, token = {}", token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.info("✅ 유효한 JWT 토큰");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("❌ JWT 토큰 유효성 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}
