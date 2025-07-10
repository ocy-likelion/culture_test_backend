package com.likelion.culture_test.domain.survey.dto.response;

import com.likelion.culture_test.domain.survey.entity.Survey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "질문지 상세 정보")
@Builder
public record SurveyDetailResponse(

    SurveyResponse survey,

    List<SurveyQuestionResponse> questions
) {
  public static SurveyDetailResponse fromEntity(Survey survey) {
    List<SurveyQuestionResponse> questionList = survey.getSurveyQuestions().stream()
        .map(SurveyQuestionResponse::fromEntity).toList();

    return SurveyDetailResponse.builder()
        .survey(SurveyResponse.fromEntity(survey))
        .questions(questionList)
        .build();
  }
}
