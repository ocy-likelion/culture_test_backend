package com.likelion.culture_test.global.security;

import com.likelion.culture_test.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class SecurityUser implements OAuth2User, UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    public Long getId(){ return user.getId(); }

    // 사용자 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 필요 시 Role -> GrantedAuthority 매핑 가능
    }

    // 사용자 식별자 반환
    @Override
    public String getName() {
        return user.getSocialId(); // 또는 user.getId().toString()
    }

    // 카카오에서 받은 nickname 등 넘길 때
    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(
                "nickname", user.getNickname(),
                "profileImageUrl", user.getProfileImageUrl()
        );
    }

    @Override
    public String getUsername() {
        return user.getSocialId(); // 이 값이 null이면 안 됨!!
    }

    @Override
    public String getPassword() {
        return null; // 소셜 로그인은 비밀번호 없음
    }


}
