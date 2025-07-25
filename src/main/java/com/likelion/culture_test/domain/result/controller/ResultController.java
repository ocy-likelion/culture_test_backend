package com.likelion.culture_test.domain.result.controller;

import com.likelion.culture_test.domain.result.dto.*;
import com.likelion.culture_test.domain.result.service.ResultService;
import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.global.resolver.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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


    @Operation(summary = "특정 유저가 특정 설문 조사지 안의 각 문항들에 답해서 제출하기")
    @PostMapping("/submit")
    public ResponseEntity<Void> submitSurveyResult(@Parameter(hidden = true) @LoginUser User user, @RequestBody ResultRequestWithoutUserDto resultRequestWithoutUserDto) { // @RequestBody ResultRequestDto dto

        Long userId = user.getId();
        log.info("현재 로그인된 유저의 id : " + userId);
        log.info("현재 로그인된 유저의 nickname : " + user.getNickname());

        ResultRequestDto dto = new ResultRequestDto(userId, resultRequestWithoutUserDto.surveyId(), resultRequestWithoutUserDto.answers());
        resultService.processSurveyResult(dto); //
        return ResponseEntity.ok().build();
    }



    @Operation(summary = "특정 유저의 특정 설문지 결과의 분야별 수치 조회 (확인 용)")
    @GetMapping("/detail/{userId}/survey/{surveyId}/scores")
    public Map<String, Double> getScores(@PathVariable(name = "userId") Long userId, @PathVariable(name = "surveyId") Long surveyId) {
        ResultQueryDto dto = new ResultQueryDto(userId, surveyId);
        return resultService.getScoreByCategory(dto);
    }

    @Operation(summary = "특정 유저가 특정 설문지를 풀 때 몇 번에 어느 문항을 답하였는지 기록 조회 (개발 용)")
    @GetMapping("/detail/{userId}/survey/{surveyId}/answers")
    public List<ResultDetailResponseDto> getAnswers(@PathVariable(name = "userId") Long userId, @PathVariable(name = "surveyId") Long surveyId) {
        ResultQueryDto dto = new ResultQueryDto(userId, surveyId);
        return resultService.getUserAnswersDto(dto);
    }

    @Operation(summary = "특정 유저의 설문 결과를 벡터 형태로 반환 (Fast api 서버로 전송할 군집화 용")
    @GetMapping("/vector/{userId}/survey/{surveyId}")
    public List<Double> getVector(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "surveyId") Long surveyId
    ) {
        // return resultService.getVectorByUserAndSurvey(userId, surveyId);

        List<Double> vector = resultService.getVectorByUserAndSurvey(userId, surveyId);
        resultService.sendVectorToFastApi(userId, surveyId, vector);

        return vector;
    }



    @Operation(summary = "생성일 기준 전체 설문 결과 벡터만 조회 (군집화 전송용)")
    @GetMapping("/history/vector/{userId}/survey/{surveyId}")
    public List<List<Double>> getVectorHistory(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "surveyId") Long surveyId
    ) {// 아직 보내는 기능은 안함
        return resultService.getVectorsByCreatedAt(userId, surveyId);
    }

    @Operation(summary = "생성일 기준 전체 설문 결과 카테고리별 수치 조회 (개발용)")
    @GetMapping("/history/scores/{userId}/survey/{surveyId}")
    public List<CategoryScoreWithCreatedAtDto> getScoreHistory(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "surveyId") Long surveyId
    ) {
        return resultService.getCategoryScoresByCreatedAt(userId, surveyId);
    }

    @Operation(summary = "가장 최근 설문 결과 벡터값 조회 (군집화 전송 용)")
    @GetMapping("/latest/vector/{userId}/survey/{surveyId}")
    public List<Double> getLatestVector(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "surveyId") Long surveyId
    ) {
        List<Double> vector = resultService.getLatestVector(userId, surveyId);
        resultService.sendVectorToFastApi(userId, surveyId, vector); // 전송 포함
        return vector;
    }

    @Operation(summary = "설문 응답 제출 후 결과 백분율 반환")
    @GetMapping("/latest/scoresAndPercentages/{userId}/survey/{surveyId}")
    public AnalysisResponseDto getLatestScore(
            @Parameter(hidden = true) @LoginUser User user,
            @PathVariable(name = "surveyId") Long surveyId
    ) {
        Long userId = user.getId();
        log.info("현재 로그인된 유저의 id : " + userId);
        log.info("현재 로그인된 유저의 nickname : " + user.getNickname());
        return resultService.getLatestCategoryScores(userId, surveyId);
    }


// 위의 벡터 값 하나씩 보내는 메서드는 보내는 동시에 어떤 값 보내졌나 확인용으로 반환하니까 getmapping  근데 이거를 전부다 조회하기 힘드니 그냥 보내기만 하고 post
    @Operation(summary = "현재 데이터베이스 내 전체 결과 벡터값을 FastAPI 서버로 전송 (일괄 처리, 해당 엔드포인드는 보내는 동시에 조회하는 거(이런것들은 get으로함) 말고 보내는 작업만 하니까 post, )")
    @PostMapping("/batch/vector/all/{clusterNum}")
    public ResponseEntity<Void> sendAllVectors(@PathVariable(name = "clusterNum") int clusterNum) {
        resultService.sendAllVectorsToFastApi(clusterNum);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 유저의 결과 기록들 최신순")
    @GetMapping("/history/{userId}")
    public List<ResultHistoryDto> getResultHistory(@PathVariable(name ="userId") Long userId) {
        return resultService.getResultHistoryByUserId(userId);
    }

    @Operation(summary = "특정 결과 건의 백분율과 군집화된 유형 반환")
    @GetMapping("/analysis/{resultId}")
    public AnalysisResponseDto getAnalysisByResultId(@PathVariable(name ="resultId") Long resultId) {
        return resultService.getCategoryScoresByResultId(resultId);
    }

    @Operation(summary = "초기 데이터베이스 적재용")
    @PostMapping("/initialLoad/")
    public ResponseEntity<Void> submitSurveyResultToLoad(@RequestBody ResultRequestDto dto) { // @RequestBody ResultRequestDto dto
        resultService.processSurveyResultToLoad(dto); //
        return ResponseEntity.ok().build();
    }









}
