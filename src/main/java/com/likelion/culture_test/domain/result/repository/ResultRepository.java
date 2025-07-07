package com.likelion.culture_test.domain.result.repository;

import com.likelion.culture_test.domain.result.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {


    Optional<Result> findByUserIdAndSurveyId(Long userId, Long surveyId);

}
