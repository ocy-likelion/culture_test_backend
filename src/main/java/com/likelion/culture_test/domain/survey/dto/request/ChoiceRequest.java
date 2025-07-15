package com.likelion.culture_test.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "선택지 생성 정보 (관리자용 API로, 분리 불필요)")
public record ChoiceRequest(

    @NotBlank
    @Schema(description = "선택지 내용", example = "능력 중시")
    String content,

    @NotNull
    @Schema(description = "선택지 순서", example = "1")
    Integer displayOrder,

    @Schema(description = "선택지별 특성 ID", example = "2")
    Long propertyId
) {}
