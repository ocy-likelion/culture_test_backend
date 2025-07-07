package com.likelion.culture_test.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "질문지 생성")
public record CreateSurveyRequest(

    @Schema(description = "설문지 제목 및 버전", example = "인사담당자 성향 테스트 (v1.0)")
    @NotNull
    String title,

    @Schema(description = "테스트 대상 설문지 여부", example = "false")
    @NotNull
    Boolean isMain
) {
}
