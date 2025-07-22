package com.likelion.culture_test.domain.result.repository;

import com.likelion.culture_test.domain.result.entity.Result;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {


    Optional<Result> findByUserIdAndSurveyId(Long userId, Long surveyId);

    List<Result> findByUserIdAndSurveyIdOrderByCreatedAtDesc(Long userId, Long surveyId);
    Optional<Result> findTopByUserIdAndSurveyIdOrderByCreatedAtDesc(Long userId, Long surveyId);


    List<Result> findAllByOrderByIdAsc();


}
