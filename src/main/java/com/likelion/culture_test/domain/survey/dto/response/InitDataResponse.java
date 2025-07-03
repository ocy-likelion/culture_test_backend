package com.likelion.culture_test.domain.survey.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InitDataResponse {

  @JsonProperty("surveyId")
  private Long surveyId;

  @JsonProperty("isMain")
  private boolean isMain;

  @JsonProperty("title")
  private String title;

  @JsonProperty("questions")
  private List<QuestionDto> questions;

  @Getter
  @NoArgsConstructor
  public static class QuestionDto {
    @JsonProperty("displayOrder")
    private int displayOrder;

    @JsonProperty("questionId")
    private Long questionId;

    @JsonProperty("category")
    private String category;

    @JsonProperty("property")
    private String property;

    @JsonProperty("isSelective")
    private boolean isSelective;

    @JsonProperty("content")
    private String content;

    @JsonProperty("choices")
    private List<ChoiceDto> choices;
  }

  @Getter
  @NoArgsConstructor
  public static class ChoiceDto {
    @JsonProperty("displayOrder")
    private int displayOrder;

    @JsonProperty("content")
    private String content;
  }
}
