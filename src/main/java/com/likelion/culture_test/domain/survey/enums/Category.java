package com.likelion.culture_test.domain.survey.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

  WORK_CAPABILITY("업무 능력"),
  CONFLICT_RESOLUTION("갈등 대응 방식"),
  PERSONALITY_PREFERENCE("성향 및 인성"),
  EVALUATION_CRITERIA("평가 기준");

  private final String description;
}
