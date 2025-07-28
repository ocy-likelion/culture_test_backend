package com.likelion.culture_test.domain.user.repository;

import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.domain.user.entity.SsoProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialIdAndSsoProvider(String socialId, SsoProvider ssoProvider);
    Optional<User> findBySocialId(String socialId);
    boolean existsBySocialId(String socialId);


}
