package com.likelion.culture_test.domain.user.controller;

import com.likelion.culture_test.domain.user.dto.UserResponseDto;
import com.likelion.culture_test.domain.user.service.UserService;
import com.likelion.culture_test.global.rq.Rq;
import com.likelion.culture_test.global.rsData.RsData;
import com.likelion.culture_test.global.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OAuth API", description = "소셜 로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class OAuth2UserController {
    private final UserService userService;
    private final Rq rq;
    private final JwtUtil jwtUtil;

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자 본인의 정보를 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }

    @Operation(summary = "로그아웃", description = "로그인한 사용자를 로그아웃 처리합니다.")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        Rq.AuthTokens tokens = rq.getAuthTokensFromRequest();

        if (tokens.accessToken() == null || !jwtUtil.validateToken(tokens.accessToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        userService.logout(tokens.accessToken());

        rq.deleteCookie("accessToken");
        rq.deleteCookie("refreshToken");
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("로그아웃 성공");
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴")
    public RsData<String> withdraw() {
        userService.withdraw(rq.getUser());
        rq.removeRefreshToken(); // 쿠키 제거
        rq.removeAccessToken();
        return RsData.of("200", "회원 탈퇴가 완료되었습니다.");
    }

}
