package com.likelion.culture_test.domain.survey.repository;

import com.likelion.culture_test.domain.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
}
