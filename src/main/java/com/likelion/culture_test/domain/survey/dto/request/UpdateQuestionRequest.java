package com.likelion.culture_test.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "질문 수정 요청")
public record UpdateQuestionRequest(

    @NotBlank
    @Schema(description = "질문 내용", example = "협업은 누가 맡았든 필요한 사람이 먼저 나서는 유연성이 핵심이라고 본다.")
    String content,

    @Schema(description = "특성 ID (선택형인 경우 => null)", example = "1")
    Long propertyId,

    @NotNull
    @Schema(description = "선택형 질문 여부", example = "false")
    Boolean isSelective,

    @Valid
    @Schema(description = "선택지 목록  (리커트 질문의 경우 무시됨)")
    List<ChoiceRequest> choices
) {

  @Schema(description = "선택지 생성 정보 (관리자용 API로, 분리 불필요)")
  public record ChoiceRequest(

      @NotBlank
      @Schema(description = "선택지 내용", example = "매우 그렇다")
      String content,

      @NotNull
      @Schema(description = "선택지 순서", example = "1")
      Integer displayOrder,

      @Schema(description = "선택지별 특성 ID", example = "2")
      Long propertyId
  ) {}
}
