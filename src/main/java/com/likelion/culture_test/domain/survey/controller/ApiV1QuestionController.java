package com.likelion.culture_test.domain.survey.controller;

import com.likelion.culture_test.domain.survey.dto.request.CreateQuestionRequest;
import com.likelion.culture_test.domain.survey.dto.response.QuestionResponse;
import com.likelion.culture_test.domain.survey.service.QuestionService;
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
  public RsData<QuestionResponse> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
    QuestionResponse response = questionService.create(request);
    return new RsData<>("200", "새로운 질문 생성 성공", response);
  }


  @DeleteMapping("/{questionId}")
  @Operation(summary = "질문 제거")
  public RsData<Void> deleteQuestion(@PathVariable Long questionId) {
    questionService.deleteById(questionId);
    return new RsData<>("204", "%d번 질문 제거 성공".formatted(questionId));
  }




}
