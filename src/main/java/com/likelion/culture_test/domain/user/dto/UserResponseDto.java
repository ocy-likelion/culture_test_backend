package com.likelion.culture_test.domain.user.dto;

import com.likelion.culture_test.domain.user.entity.SsoProvider;
import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.domain.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String nickname;
    private String profileImageUrl;
    private UserRole role;
    private SsoProvider ssoProvider;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .ssoProvider(user.getSsoProvider())
                .build();
    }
}
