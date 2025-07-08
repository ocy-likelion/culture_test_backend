package com.likelion.culture_test.domain.survey.service;


import com.likelion.culture_test.domain.survey.dto.response.QuestionResponse;
import com.likelion.culture_test.domain.survey.dto.response.SurveyDetailResponse;
import com.likelion.culture_test.domain.survey.dto.response.SurveyResponse;
import com.likelion.culture_test.domain.survey.entity.Survey;
import com.likelion.culture_test.domain.survey.entity.SurveyQuestion;
import com.likelion.culture_test.domain.survey.repository.SurveyQuestionRepository;
import com.likelion.culture_test.domain.survey.repository.SurveyRepository;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

  private final SurveyRepository surveyRepository;
  private final SurveyQuestionRepository surveyQuestionRepository;


  public Page<QuestionResponse> getMainSurvey(Pageable pageable) {
    Survey survey = findMainSurvey()
        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

    Page<SurveyQuestion> surveyQuestions =
        surveyQuestionRepository.findBySurveyId(survey.getId(), pageable);

    return surveyQuestions.map(surveyQuestion ->
        QuestionResponse.fromEntity(surveyQuestion.getQuestion())
    );
  }


  public Optional<Survey> findMainSurvey() {
    return surveyRepository.findByIsMain(true);
  }


  public Page<SurveyResponse> findSurveys(Pageable pageable) {
    return surveyRepository.findAll(pageable).map(SurveyResponse::fromEntity);
  }


  public Survey findById(Long surveyId) {
    return surveyRepository.findById(surveyId)
        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));
  }


  public SurveyDetailResponse getSurveyDetail(Long surveyId) {
    Survey survey = findById(surveyId);
    return SurveyDetailResponse.fromEntity(survey);
  }


  @Transactional
  public void createSurvey(String title, Boolean isMain) {
    if (isMain) {
      surveyRepository.updateAllMainToFalse();
    }

    Survey survey = Survey.builder()
        .title(title)
        .isMain(isMain)
        .build();
    surveyRepository.save(survey);
  }


  public void deleteById(Long surveyId) {
    Survey survey = findById(surveyId);
    surveyRepository.delete(survey);
  }
}
