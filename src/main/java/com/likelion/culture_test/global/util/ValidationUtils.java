package com.likelion.culture_test.global.util;

import com.likelion.culture_test.domain.survey.repository.PropertyRepository;
import com.likelion.culture_test.domain.survey.repository.QuestionRepository;
import com.likelion.culture_test.domain.survey.repository.SurveyRepository;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;

public class ValidationUtils {

  public static void validateSurvey(Long id, SurveyRepository repository) {
    if (!repository.existsById(id)) {
      throw new CustomException(ErrorCode.SURVEY_NOT_FOUND);
    }
  }

  public static void validateQuestion(Long id, QuestionRepository repository) {
    if (!repository.existsById(id)) {
      throw new CustomException(ErrorCode.QUESTION_NOT_FOUND);
    }
  }

  public static void validateProperty(Long id, PropertyRepository repository) {
    if (!repository.existsById(id)) {
      throw new CustomException(ErrorCode.PROPERTY_NOT_FOUND);
    }
  }
}
