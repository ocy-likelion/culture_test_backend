package com.likelion.culture_test.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "질문 수정 요청")
public record UpdateQuestionRequest(

    @NotBlank
    @Schema(description = "질문 내용", example = "비전공자이지만 기술에 대한 높은 관심과 질 높은 프로젝트 경험이 있다면, 전공자보다 더 높은 평가를 줄 수 있다고 생각한다.")
    String content,

    @Schema(description = "특성 ID (선택형인 경우 => null)", example = "2")
    Long propertyId,

    @NotNull
    @Schema(description = "선택형 질문 여부", example = "false")
    Boolean isSelective,

    @Valid
    @Schema(description = "선택지 목록  (리커트 질문의 경우 무시됨)")
    List<ChoiceRequest> choices
) {}
