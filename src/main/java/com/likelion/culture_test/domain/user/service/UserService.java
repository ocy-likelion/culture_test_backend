package com.likelion.culture_test.domain.user.service;

import com.likelion.culture_test.domain.user.dto.UserResponseDto;
import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.domain.user.repository.UserRepository;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import com.likelion.culture_test.global.rq.Rq;
import com.likelion.culture_test.global.security.SecurityUser;
import com.likelion.culture_test.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.likelion.culture_test.global.exceptions.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final Rq rq;

    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!(principal instanceof SecurityUser securityUser)){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = securityUser.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        return UserResponseDto.fromEntity(user);
    }


    @Transactional
    public void logout(String accessToken) {
        Long userId = jwtUtil.getUserId(accessToken);
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    @Transactional
    public void withdraw(User user) {
        userRepository.delete(user);
    }


    @Transactional
    public UserResponseDto agreeToTerms() {
        User user = rq.getUser();
        if (!user.isHasAgreedTerms()) {
            user.setHasAgreedTerms(true);
        }
        return UserResponseDto.fromEntity(user);
    }



}
