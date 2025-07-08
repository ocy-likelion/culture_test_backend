package com.likelion.culture_test.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "특성 수정 요청")
public record UpdatePropertyRequest(

    @NotBlank
    @Schema(description = "특성 카테고리명", example = "업무 능력")
    String categoryName,

    @NotBlank
    @Schema(description = "특성 이름", example = "성장 가능형")
    String propertyName
) {
}
