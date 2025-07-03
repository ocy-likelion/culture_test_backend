package com.likelion.culture_test.domain.survey.dto.response;

import com.likelion.culture_test.domain.survey.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "질문 응답")
@Builder
public record QuestionResponse(

    @Schema(description = "질문 ID", example = "23")
    Long questionId,

    String content,

    PropertyResponse property,

    boolean isSelective,

    List<ChoiceResponse> choices
) {
  public static QuestionResponse fromEntity(Question question) {
    List<ChoiceResponse> choiceList = question.getChoices().stream()
        .map(ChoiceResponse::fromEntity).collect(Collectors.toList());

    return QuestionResponse.builder()
        .questionId(question.getId())
        .content(question.getContent())
        .property(PropertyResponse.fromEntity(question.getProperty()))
        .isSelective(question.isSelective())
        .choices(choiceList)
        .build();
  }
}
