package com.likelion.culture_test.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "질문지에 질문 배치 추가 요청")
public record BatchQuestionRequest(
    @NotNull
    @Size(min = 1, message = "최소 1개 이상의 질문 ID가 필요합니다.")
    @Schema(
        description = "추가할 질문 ID 목록. 기존 질문과 중복되지 않는 새로운 질문들만 추가 (중복 자동 제거)",
        example = "[3, 4, 5, 6]"
    )
    List<Long> questionIds
) {
}
