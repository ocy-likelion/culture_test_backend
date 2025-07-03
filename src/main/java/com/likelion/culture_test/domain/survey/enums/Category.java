package com.likelion.culture_test.domain.survey.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

  CANDIDATE_PROFILE("인재상"),
  WORK_STYLE("업무 방식"),
  COMMUNICATING("커뮤니케이션 능력");

  private final String description;
}
