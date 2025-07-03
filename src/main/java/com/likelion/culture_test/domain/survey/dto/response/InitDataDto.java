package com.likelion.culture_test.domain.survey.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record InitDataDto(
    @JsonProperty("properties") List<PropertyDto> properties,
    @JsonProperty("surveys") List<SurveyDto> surveys
) {
  public record PropertyDto(
      @JsonProperty("key") String key,
      @JsonProperty("name") String name,
      @JsonProperty("category") String category
  ) {}

  public record SurveyDto(
      @JsonProperty("title") String title,
      @JsonProperty("isMain") boolean isMain,
      @JsonProperty("questions") List<QuestionDto> questions
  ) {}

  public record QuestionDto(
      @JsonProperty("content") String content,
      @JsonProperty("isSelective") boolean isSelective,
      @JsonProperty("property") String property,
      @JsonProperty("choices") List<ChoiceDto> choices
  ) {}

  public record ChoiceDto(
      @JsonProperty("displayOrder") int displayOrder,
      @JsonProperty("content") String content,
      @JsonProperty("property") String property
  ) {}
}
