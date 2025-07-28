package com.likelion.culture_test.domain.user.controller;

import com.likelion.culture_test.domain.user.dto.UserResponseDto;
import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.domain.user.service.UserService;
import com.likelion.culture_test.global.resolver.LoginUser;
import com.likelion.culture_test.global.rq.Rq;
import com.likelion.culture_test.global.rsData.RsData;
import com.likelion.culture_test.global.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "OAuth API", description = "소셜 로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class OAuth2UserController {
    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자 본인의 정보를 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(@Parameter(hidden = true) @LoginUser User user) {
        return ResponseEntity.ok(userService.getMyInfo(user));
    }


    @Operation(summary = "로그아웃", description = "로그인한 사용자를 로그아웃 처리합니다.")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Parameter(hidden = true) @LoginUser User user) {
        userService.logout(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<Void> withdraw(@Parameter(hidden = true) @LoginUser User user) {
        userService.withdraw(user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/agree-terms")
    @Operation(summary = "약관 동의", description = "유저가 약관에 동의한 상태로 업데이트합니다.")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<UserResponseDto> agreeTerms(@Parameter(hidden = true) @LoginUser User user) {
        return ResponseEntity.ok(userService.agreeToTerms(user));
    }

}
