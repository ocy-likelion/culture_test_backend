package com.likelion.culture_test.domain.survey.controller;

import com.likelion.culture_test.domain.survey.dto.request.CreateQuestionRequest;
import com.likelion.culture_test.domain.survey.dto.request.UpdateSurveyRequest;
import com.likelion.culture_test.domain.survey.dto.response.QuestionResponse;
import com.likelion.culture_test.domain.survey.service.QuestionService;
import com.likelion.culture_test.global.globalDto.PageResponse;
import com.likelion.culture_test.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
@Tag(name = "질문 관리 API", description = "질문 관리 API")
public class ApiV1QuestionController {

  private final QuestionService questionService;


  @GetMapping
  @Operation(summary = "전체 질문 목록")
  public RsData<PageResponse<QuestionResponse>> getQuestions(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<QuestionResponse> response = questionService.findAllByPage(pageable);

    return new RsData<>("200", "질문 목록 조회 성공", PageResponse.of(response));
  }


  @PostMapping()
  @Operation(summary = "질문 생성")
  public RsData<QuestionResponse> createQuestion(
      HttpServletRequest httpRequest, @Valid @RequestBody CreateQuestionRequest request) {
    log.info("=== POST /api/v1/questions 요청 받음 ===");
    log.info("Origin: {}", httpRequest.getHeader("Origin"));
    log.info("Host: {}", httpRequest.getHeader("Host"));
    log.info("Referer: {}", httpRequest.getHeader("Referer"));
    log.info("User-Agent: {}", httpRequest.getHeader("User-Agent"));
    log.info("Content-Type: {}", httpRequest.getHeader("Content-Type"));
    log.info("Remote Address: {}", httpRequest.getRemoteAddr());

    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      log.info("Header [{}]: {}", headerName, httpRequest.getHeader(headerName));
    }

    QuestionResponse response = questionService.create(request);
    return new RsData<>("201", "새로운 질문 생성 성공", response);
  }


  @DeleteMapping("/{questionId}")
  @Operation(summary = "질문 제거")
  public RsData<Void> deleteQuestion(@PathVariable Long questionId) {
    questionService.deleteById(questionId);
    return new RsData<>("204", "%d번 질문 제거 성공".formatted(questionId));
  }


  @PutMapping("/{questionId}")
  @Operation(summary = "질문 수정  (미완)")
  public RsData<QuestionResponse> updateQuestion(
      @PathVariable Long questionId, @Valid @RequestBody UpdateSurveyRequest request
  ) {
    QuestionResponse response = questionService.update(questionId, request);
    return new RsData<>("204", "%d번 질문 수정 성공".formatted(questionId), response);
  }

}
