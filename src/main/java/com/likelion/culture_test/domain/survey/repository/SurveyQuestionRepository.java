package com.likelion.culture_test.domain.survey.repository;

import com.likelion.culture_test.domain.survey.entity.SurveyQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {

  Page<SurveyQuestion> findBySurveyId(Long surveyId, Pageable pageable);

  @Query("SELECT MAX(sq.displayOrder) FROM SurveyQuestion sq WHERE sq.survey.id = :surveyId")
  Optional<Integer> findMaxDisplayOrderBySurveyId(@Param("surveyId") Long surveyId);

  @Query("SELECT sq.question.id FROM SurveyQuestion sq WHERE sq.survey.id = :surveyId")
  List<Long> findQuestionIdsBySurveyId(@Param("surveyId") Long surveyId);

  void deleteBySurveyId(Long surveyId);
}
