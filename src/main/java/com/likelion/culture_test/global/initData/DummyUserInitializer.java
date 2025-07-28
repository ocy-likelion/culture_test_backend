package com.likelion.culture_test.global.initData;

import com.likelion.culture_test.domain.user.entity.SsoProvider;
import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.domain.user.entity.UserRole;
import com.likelion.culture_test.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DummyUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        // 이미 120명 이상 있으면 더미 유저 안 넣음
        if (userRepository.count() >= 120) return;

        for (int i = 0; i < 120; i++) {
            userRepository.save(User.builder().build());
        }
    }
}
