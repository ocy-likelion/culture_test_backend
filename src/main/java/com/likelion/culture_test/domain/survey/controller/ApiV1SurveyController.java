package com.likelion.culture_test.domain.survey.controller;

import com.likelion.culture_test.domain.survey.dto.QuestionResponse;
import com.likelion.culture_test.domain.survey.service.SurveyService;
import com.likelion.culture_test.global.globalDto.PageResponse;
import com.likelion.culture_test.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/surveys")
@Tag(name = "설문조사 데이터 API", description = "설문 조사 데이터 조회 API")
public class ApiV1SurveyController {

  private final SurveyService surveyService;

  @GetMapping("/main")
  @Operation(summary = "테스트 대상 설문 조사 조회")
  public RsData<PageResponse<QuestionResponse>> getMainSurvey(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "3") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "displayOrder"));
    Page<QuestionResponse> response = surveyService.getMainSurvey(pageable);

    return new RsData<>("200", "설문조사 조회 성공", PageResponse.of(response));
  }
}
