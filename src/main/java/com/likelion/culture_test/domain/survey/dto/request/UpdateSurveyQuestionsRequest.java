package com.likelion.culture_test.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "설문지의 질문 목록 업데이트 요청 (순서변경/제거)")
public record UpdateSurveyQuestionsRequest(
    @NotNull
    @Size(min = 0, message = "질문 목록은 null일 수 없습니다.")
    List<Long> questionIds
) {}
