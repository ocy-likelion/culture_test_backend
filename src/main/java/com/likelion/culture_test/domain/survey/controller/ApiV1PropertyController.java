package com.likelion.culture_test.domain.survey.controller;

import com.likelion.culture_test.domain.survey.dto.request.CreatePropertyRequest;
import com.likelion.culture_test.domain.survey.dto.request.UpdatePropertyRequest;
import com.likelion.culture_test.domain.survey.dto.response.PropertyResponse;
import com.likelion.culture_test.domain.survey.dto.response.QuestionResponse;
import com.likelion.culture_test.domain.survey.service.PropertyService;
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

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/properties")
@Tag(name = "특성 관리 API", description = "특성 관리 API")
public class ApiV1PropertyController {

  private final PropertyService propertyService;
  private final QuestionService questionService;


  @GetMapping
  @Operation(summary = "전체 특성 목록")
  public RsData<List<PropertyResponse>> getProperties() {
    List<PropertyResponse> response = propertyService.findAll();
    return new RsData<>("200", "특성 목록 조회 성공", response);
  }


  @PostMapping()
  @Operation(summary = "특성 생성")
  public RsData<PropertyResponse> createProperty(@Valid @RequestBody CreatePropertyRequest request) {
    PropertyResponse response = propertyService.create(request.categoryName(), request.propertyName());
    return new RsData<>("201", "새로운 특성 생성 성공", response);
  }


  @DeleteMapping("/{propertyId}")
  @Operation(summary = "특성 제거")
  public RsData<Void> deleteProperty(@PathVariable Long propertyId) {
    propertyService.deleteById(propertyId);
    return new RsData<>("204", "%d번 특성 제거 성공".formatted(propertyId));
  }


  @PutMapping("/{propertyId}")
  @Operation(summary = "특성 수정")
  public RsData<PropertyResponse> updateProperty(
      @PathVariable Long propertyId, @Valid @RequestBody UpdatePropertyRequest request
  ) {
    PropertyResponse response = propertyService.update(propertyId, request.categoryName(), request.propertyName());
    return new RsData<>("204", "%d번 특성 수정 성공".formatted(propertyId), response);
  }


  @GetMapping("/{propertyId}/questions")
  @Operation(summary = "해당 특성의 질문 목록  (페이징)")
  public RsData<PageResponse<QuestionResponse>> getQuestionsByProperty(
      @PathVariable Long propertyId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<QuestionResponse> response = questionService.findQuestionsByProperty(propertyId, pageable);
    return new RsData<>("200", "해당 특성의 질문 목록 조회 성공", PageResponse.of(response));
  }


  @GetMapping("/categories")
  @Operation(summary = "전체 카테고리 이름 목록")
  public RsData<List<String>> getCategories() {
    List<String> response = propertyService.getCategoryNames();
    return new RsData<>("200", "전체 카테고리 이름 목록 조회 성공", response);
  }
}
