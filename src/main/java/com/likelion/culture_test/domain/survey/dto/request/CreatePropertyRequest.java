package com.likelion.culture_test.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "특성 생성 요청")
public record CreatePropertyRequest(

    @NotBlank
    @Schema(description = "특성 카테고리명", example = "업무 능력")
    String categoryName,

    @NotBlank
    @Schema(description = "특성 이름", example = "즉시 전력형")
    String propertyName
) {
}
