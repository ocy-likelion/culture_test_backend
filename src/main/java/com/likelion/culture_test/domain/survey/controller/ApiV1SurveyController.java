package com.likelion.culture_test.domain.survey.controller;

import com.likelion.culture_test.domain.survey.dto.request.CreateSurveyRequest;
import com.likelion.culture_test.domain.survey.dto.response.QuestionResponse;
import com.likelion.culture_test.domain.survey.dto.response.SurveyDetailResponse;
import com.likelion.culture_test.domain.survey.dto.response.SurveyResponse;
import com.likelion.culture_test.domain.survey.service.DataInitializerService;
import com.likelion.culture_test.domain.survey.service.SurveyService;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import com.likelion.culture_test.global.globalDto.PageResponse;
import com.likelion.culture_test.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/surveys")
@Tag(name = "설문 조사 API", description = "설문 조사 API")
public class ApiV1SurveyController {

  private final SurveyService surveyService;
  private final DataInitializerService dataInitializerService;

  @GetMapping("/main")
  @Operation(summary = "⭐ 메인 설문 조사 조회")
  public RsData<PageResponse<QuestionResponse>> getMainSurvey(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "3") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "displayOrder"));
    Page<QuestionResponse> response = surveyService.getMainSurvey(pageable);

    return new RsData<>("200", "설문조사 조회 성공", PageResponse.of(response));
  }


  @GetMapping("/sync")
  @Operation(summary = "설문 조사 데이터 파일 백업 저장")
  public RsData<Void> getMainSurvey() {
    try {
      dataInitializerService.backupData();
      return new RsData<>("204", "설문조사 백업(data/backup.json) 저장 성공");

    } catch (IOException e) {
      throw new CustomException(ErrorCode.BACKUP_FAILED);
    }
  }


  @GetMapping
  @Operation(summary = "설문 조사지 목록")
  public RsData<PageResponse<SurveyResponse>> getSurveys(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<SurveyResponse> response = surveyService.findSurveys(pageable);

    return new RsData<>("200", "설문조사지 목록 조회 성공", PageResponse.of(response));
  }


  @GetMapping("/{surveyId}")
  @Operation(summary = "설문 조사지 상세 조회")
  public RsData<SurveyDetailResponse> getSurveyDetail(@PathVariable Long surveyId) {
    SurveyDetailResponse response = surveyService.getSurveyDetail(surveyId);
    return new RsData<>("200", "설문조사지 상세 조회 성공", response);
  }


  @PostMapping()
  @Operation(summary = "설문 조사지 생성")
  public RsData<Void> createSurvey(@Valid @RequestBody CreateSurveyRequest request) {
    surveyService.createSurvey(request.title(), request.isMain());
    return new RsData<>("200", "새로운 설문조사지 생성 성공");
  }


  @DeleteMapping("/{surveyId}")
  @Operation(summary = "설문 조사지 제거")
  public RsData<Void> deleteSurvey(@PathVariable Long surveyId) {
    surveyService.deleteById(surveyId);
    return new RsData<>("204", "%d번 질문지 제거 및 질문 연결 해제 성공".formatted(surveyId));
  }
}
