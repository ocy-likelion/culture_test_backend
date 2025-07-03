package com.likelion.culture_test.domain.survey.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "질문 응답")
public record QuestionResponse(

    @Schema(description = "질문 ID", example = "23")
    Long questionId
) {
}
