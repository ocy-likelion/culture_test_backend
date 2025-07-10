package com.likelion.culture_test.domain.survey.dto.response;

import com.likelion.culture_test.domain.survey.entity.Survey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "설문지 정보")
@Builder
public record SurveyResponse(

    @Schema(description = "설문지 ID", example = "2")
    Long surveyId,

    @Schema(description = "설문지 제목", example = "인사담당자 성향 테스트 (v1.0)")
    String title,

    @Schema(description = "메인 설문지 여부", example = "false")
    boolean isMain
) {
  public static SurveyResponse fromEntity(Survey survey) {
    return SurveyResponse.builder()
        .surveyId(survey.getId())
        .title(survey.getTitle())
        .isMain(survey.isMain())
        .build();
  }
}
