package com.likelion.culture_test.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "설문지의 질문 목록 업데이트 요청 (순서변경/제거)")
public record UpdateSurveyQuestionsRequest(
    @NotNull
    @Size(min = 0, message = "질문 목록은 null일 수 없습니다.")
    @Schema(
        description = "순서변경/제거할 질문 ID 목록. 기존 배열에서 빠지는 질문 자동 제거",
        example = "[6, 1, 2, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 16, 15]"
    )
    List<Long> questionIds
) {}

