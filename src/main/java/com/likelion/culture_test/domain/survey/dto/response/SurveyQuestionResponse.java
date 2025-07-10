package com.likelion.culture_test.domain.survey.dto.response;

import com.likelion.culture_test.domain.survey.entity.Question;
import com.likelion.culture_test.domain.survey.entity.SurveyQuestion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "설문지-질문 정보")
@Builder
public record SurveyQuestionResponse(

    @Schema(description = "설문지 ID", example = "1")
    Long surveyId,

    @Schema(description = "질문 ID", example = "15")
    Long questionId,

    @Schema(description = "질문 배치 순서", example = "3")
    int displayOrder,

    @Schema(description = "질문 내용", example = "협업은 누가 맡았든 필요한 사람이 먼저 나서는 유연성이 핵심이라고 본다.")
    String content,

    PropertyResponse property,

    @Schema(description = "선택형 질문 여부", example = "false")
    boolean isSelective,

    List<ChoiceResponse> choices
) {
  public static SurveyQuestionResponse fromEntity(SurveyQuestion surveyQuestion) {
    Question question = surveyQuestion.getQuestion();

    List<ChoiceResponse> choiceList = question.getChoices().stream()
        .map(ChoiceResponse::fromEntity).collect(Collectors.toList());

    return SurveyQuestionResponse.builder()
        .surveyId(surveyQuestion.getSurvey().getId())
        .questionId(question.getId())
        .displayOrder(surveyQuestion.getDisplayOrder())
        .content(question.getContent())
        .property(PropertyResponse.fromEntity(question.getProperty()))
        .isSelective(question.isSelective())
        .choices(choiceList)
        .build();
  }
}
