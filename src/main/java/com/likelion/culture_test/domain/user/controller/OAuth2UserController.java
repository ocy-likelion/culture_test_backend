package com.likelion.culture_test.domain.user.controller;

import com.likelion.culture_test.domain.user.dto.UserResponseDto;
import com.likelion.culture_test.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OAuth API", description = "소셜 로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class OAuth2UserController {
    private final UserService userService;



    @Operation(summary = "내 정보 조회", description = "로그인한 사용자 본인의 정보를 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }
}
