package com.likelion.culture_test.domain.survey.dto.response;

import com.likelion.culture_test.domain.survey.entity.Choice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "질문의 선택지 정보")
@Builder
public record ChoiceResponse(

    @Schema(description = "선택지 ID", example = "101")
    Long choiceId,

    @Schema(description = "선택지 내용", example = "매우 그렇다")
    String content

) {
  public static ChoiceResponse fromEntity(Choice choice) {
    return ChoiceResponse.builder()
        .choiceId(choice.getId())
        .content(choice.getContent())
        .build();
  }
}
