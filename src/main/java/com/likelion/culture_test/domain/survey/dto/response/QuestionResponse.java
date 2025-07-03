package com.likelion.culture_test.domain.survey.dto.response;

import com.likelion.culture_test.domain.survey.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "질문 정보")
@Builder
public record QuestionResponse(

    @Schema(description = "질문 ID", example = "23")
    Long questionId,

    @Schema(description = "질문 내용", example = "협업은 누가 맡았든 필요한 사람이 먼저 나서는 유연성이 핵심이라고 본다.")
    String content,

    PropertyResponse property,

    @Schema(description = "선택형 질문 여부", example = "false")
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
