package com.likelion.culture_test.domain.user.entity;

import com.likelion.culture_test.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name="sso_provider")
    private SsoProvider ssoProvider;

    @Column(name="social_id")
    private String socialId;

    @Column(name="nickname")
    private String nickname;

    @Column(name="profile_image_url")
    private String profileImageUrl;

    @Column(name = "refresh_token", unique = true)
    private String refreshToken;





}
