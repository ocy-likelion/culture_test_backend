package com.likelion.culture_test.domain.result.controller;

import com.likelion.culture_test.domain.result.dto.ResultRequestDto;
import com.likelion.culture_test.domain.result.service.ResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/result")
@Tag(name = "결과 API", description = "결과 데이터 API")
public class ResultController {

    private final ResultService resultService;

    @PostMapping
    public ResponseEntity<Void> submitSurveyResult(@RequestBody ResultRequestDto dto) {
        resultService.processSurveyResult(dto);
        return ResponseEntity.ok().build();
    }
}
