package com.likelion.culture_test.domain.survey.service;


import com.likelion.culture_test.domain.survey.dto.response.SurveyDetailResponse;
import com.likelion.culture_test.domain.survey.dto.response.SurveyQuestionResponse;
import com.likelion.culture_test.domain.survey.dto.response.SurveyResponse;
import com.likelion.culture_test.domain.survey.entity.Question;
import com.likelion.culture_test.domain.survey.entity.Survey;
import com.likelion.culture_test.domain.survey.entity.SurveyQuestion;
import com.likelion.culture_test.domain.survey.repository.SurveyQuestionRepository;
import com.likelion.culture_test.domain.survey.repository.SurveyRepository;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import com.likelion.culture_test.global.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

  private final SurveyRepository surveyRepository;
  private final SurveyQuestionRepository surveyQuestionRepository;
  private final QuestionService questionService;


  //@Cacheable(value = "surveys", key = "'main_survey_' + #pageable.pageNumber + '_' + #pageable.pageSize")
  public Page<SurveyQuestionResponse> getMainSurvey(Pageable pageable) {
    Survey survey = surveyRepository.findByIsMain(true)
        .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

    return surveyQuestionRepository.findBySurveyId(survey.getId(), pageable)
        .map(SurveyQuestionResponse::fromEntity);
  }


  public Page<SurveyResponse> findAllByPage(Pageable pageable) {
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
  public void create(String title, Boolean isMain) {
    if (isMain) {
      surveyRepository.updateAllMainToFalse();
    }

    Survey survey = Survey.builder()
        .title(title)
        .isMain(isMain)
        .build();
    surveyRepository.save(survey);
  }

  @Transactional
  public void deleteById(Long surveyId) {
    Survey survey = findById(surveyId);
    surveyRepository.delete(survey);
  }


  @Transactional
  public void update(Long surveyId, String title, boolean isMain) {
    Survey survey = findById(surveyId);

    if (isMain) {
      surveyRepository.updateAllMainToFalse();
    }

    survey.setTitle(title);
    survey.setMain(isMain);
  }


  @Transactional
  public void createSurveyQuestions(Long surveyId, List<Long> questionIds) {
    Survey survey = findById(surveyId);
    int maxNum = surveyQuestionRepository.findMaxDisplayOrderBySurveyId(surveyId).orElse(0);

    List<Long> existingQuestionIds = surveyQuestionRepository.findQuestionIdsBySurveyId(surveyId);
    List<Long> newQuestionIds = questionIds.stream()
        .filter(id -> !existingQuestionIds.contains(id))
        .toList();

    if (newQuestionIds.isEmpty()) {
      throw new CustomException(ErrorCode.NO_NEW_QUESTIONS);
    }

    List<SurveyQuestion> surveyQuestions = new ArrayList<>();
    for (Long questionId : newQuestionIds) {
      Question question = questionService.findById(questionId);
      surveyQuestions.add(
          SurveyQuestion.builder()
              .question(question)
              .survey(survey)
              .displayOrder(++maxNum)
              .build()
      );
    }

    surveyQuestionRepository.saveAll(surveyQuestions);
  }


  @Transactional
  public void updateSurveyQuestions(Long surveyId, List<Long> questionIds) {
    ValidationUtils.validateSurvey(surveyId, surveyRepository);
    surveyQuestionRepository.deleteBySurveyId(surveyId);

    createSurveyQuestions(surveyId, questionIds);
  }
}
