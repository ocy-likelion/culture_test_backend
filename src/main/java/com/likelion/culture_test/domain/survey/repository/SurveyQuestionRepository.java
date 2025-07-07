package com.likelion.culture_test.domain.survey.repository;

import com.likelion.culture_test.domain.survey.entity.Question;
import com.likelion.culture_test.domain.survey.entity.SurveyQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {

  Page<SurveyQuestion> findBySurveyId(Long surveyId, Pageable pageable);

  void deleteByQuestion(Question question);
}
