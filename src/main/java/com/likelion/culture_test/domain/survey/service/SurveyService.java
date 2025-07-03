package com.likelion.culture_test.domain.survey.service;

import com.likelion.culture_test.domain.survey.dto.QuestionResponse;
import com.likelion.culture_test.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

  private final SurveyRepository surveyRepository;

  public Page<QuestionResponse> getMainSurvey(Pageable pageable) {
    return null;
  }
}
