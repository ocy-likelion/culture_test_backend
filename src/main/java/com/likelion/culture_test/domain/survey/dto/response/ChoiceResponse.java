package com.likelion.culture_test.domain.survey.dto.response;

import com.likelion.culture_test.domain.survey.entity.Choice;
import lombok.Builder;

@Builder
public record ChoiceResponse(
    Long choiceId,
    String content
) {
  public static ChoiceResponse fromEntity(Choice choice) {
    return ChoiceResponse.builder()
        .choiceId(choice.getId())
        .content(choice.getContent())
        .build();
  }
}
