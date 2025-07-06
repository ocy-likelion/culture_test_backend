package com.likelion.culture_test.domain.result.controller;

import com.likelion.culture_test.domain.result.dto.ResultDetailResponseDto;
import com.likelion.culture_test.domain.result.dto.ResultQueryDto;
import com.likelion.culture_test.domain.result.dto.ResultRequestDto;
import com.likelion.culture_test.domain.result.service.ResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/result")
@Tag(name = "결과 API", description = "결과 데이터 API")
public class ResultController {

    private final ResultService resultService;


    @PostMapping("/submit")
    public ResponseEntity<Void> submitSurveyResult(@RequestBody ResultRequestDto dto) {
        resultService.processSurveyResult(dto);
        return ResponseEntity.ok().build();
    }




    @GetMapping("/detail/{userId}/survey/{surveyId}/scores")
    public Map<String, Double> getScores(@PathVariable(name = "userId") Long userId, @PathVariable(name = "surveyId") Long surveyId) {
        ResultQueryDto dto = new ResultQueryDto(userId, surveyId);
        return resultService.getScoreByProperty(dto);
    }

    @GetMapping("/detail/{userId}/survey/{surveyId}/answers")
    public List<ResultDetailResponseDto> getAnswers(@PathVariable(name = "userId") Long userId, @PathVariable(name = "surveyId") Long surveyId) {
        ResultQueryDto dto = new ResultQueryDto(userId, surveyId);
        return resultService.getUserAnswersDto(dto);
    }
}
