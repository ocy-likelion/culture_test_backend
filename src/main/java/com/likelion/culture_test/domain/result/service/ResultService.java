package com.likelion.culture_test.domain.result.service;

import com.likelion.culture_test.domain.result.dto.ResultRequestDto;
import com.likelion.culture_test.domain.result.entity.Result;
import com.likelion.culture_test.domain.result.repository.ResultRepository;
import com.likelion.culture_test.domain.survey.entity.Choice;
import com.likelion.culture_test.domain.survey.entity.Survey;
import com.likelion.culture_test.domain.survey.repository.ChoiceRepository;
import com.likelion.culture_test.domain.survey.repository.SurveyRepository;
import com.likelion.culture_test.global.util.ScoreUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.likelion.culture_test.domain.result.dto.AnswerDto; // ✅ import 필요

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResultService {

    private final SurveyRepository surveyRepository;
    private final ChoiceRepository choiceRepository;
    private final ResultRepository resultRepository;

    public void processSurveyResult(ResultRequestDto dto) {
        Survey survey = surveyRepository.findById(dto.surveyId())
                .orElseThrow(() -> new RuntimeException("설문 없음"));

        // choiceId만 추출
        List<Long> choiceIds = dto.answers().stream()
                .map(AnswerDto::choiceId)
                .toList();

        // choice 객체 전부 가져옴
        List<Choice> choices = choiceRepository.findAllById(choiceIds);

        // 분야별 점수 누적
        Map<String, List<Integer>> fieldScoreMap = new HashMap<>();

        for (int i = 0; i < choices.size(); i++) {
            Choice choice = choices.get(i);
            Long expectedQuestionId = dto.answers().get(i).questionId();

            if (!choice.getQuestion().getId().equals(expectedQuestionId)) {
                throw new IllegalArgumentException("선택지와 질문이 일치하지 않음");
            }

            String fieldName = choice.getProperty().getName(); // 분야 이름
            int score = ScoreUtils.calculateScore(choice.getDisplayOrder(), expectedQuestionId);

            fieldScoreMap
                    .computeIfAbsent(fieldName, k -> new ArrayList<>())
                    .add(score);
        }

        // 분야 이름 고정 순서 정렬
        List<String> sortedFields = new ArrayList<>(fieldScoreMap.keySet());
        Collections.sort(sortedFields); // 알파벳 정렬 or 커스텀 정렬도 가능

        // 분야별 평균
        List<Double> vector = sortedFields.stream()
                .map(field -> {
                    List<Integer> scores = fieldScoreMap.get(field);
                    return scores.stream().mapToInt(i -> i).average().orElse(0.0);
                })
                .toList();

        String field = vector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        Result result = Result.builder()
                .userId(dto.userId())
                .survey(survey)
                .field(field)
                .cluster(null) // 추후 군집 분석 후 설정
                .build();

        resultRepository.save(result);
    }
}
