package com.likelion.culture_test.domain.result.controller;


import com.likelion.culture_test.domain.result.dto.ResultDetailResponseDto;
import com.likelion.culture_test.domain.result.dto.ResultQueryDto;
import com.likelion.culture_test.domain.result.entity.ResultDetail;
import com.likelion.culture_test.domain.result.service.ResultDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results/details")
@RequiredArgsConstructor
public class ResultDetailController {

    private final ResultDetailService resultDetailService;

    @GetMapping("/user/{userId}/survey/{surveyId}/scores")
    public Map<String, Double> getScores(@PathVariable(name = "userId") Long userId, @PathVariable(name = "surveyId") Long surveyId) {
        ResultQueryDto dto = new ResultQueryDto(userId, surveyId);
        return resultDetailService.getScoreByProperty(dto);
    }

    @GetMapping("/user/{userId}/survey/{surveyId}/answers")
    public List<ResultDetailResponseDto> getAnswers(@PathVariable(name = "userId") Long userId, @PathVariable(name = "surveyId") Long surveyId) {
        ResultQueryDto dto = new ResultQueryDto(userId, surveyId);
        return resultDetailService.getUserAnswersDto(dto);
    }
}
