package com.likelion.culture_test.global.initData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.culture_test.domain.result.dto.AnswerDto;
import com.likelion.culture_test.domain.result.dto.ResultRequestDto;
import com.likelion.culture_test.domain.result.service.ResultService;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class NotProd2 {

    @Bean(name = "applicationRunner2")
    public ApplicationRunner applicationRunner(ResultService resultService) {
        return new ApplicationRunner() {
            @Transactional
            @Override
            public void run(ApplicationArguments args) throws Exception {

                ObjectMapper objectMapper = new ObjectMapper();
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/result_data.json");

                if (inputStream == null) {
                    throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
                }

                List<ResultRequestDto> responses = objectMapper
                    .readerForListOf(ResultRequestDto.class)
                    .readValue(inputStream);

                for (ResultRequestDto dto: responses){
                    for (AnswerDto answerDto : dto.answers()) {
                        Long questionId = answerDto.questionId();
                        Long choiceId = answerDto.choiceId();
                        if (choiceId > questionId * 5 || choiceId <= (questionId - 1) * 5) {
                            throw new Exception(dto.userId() + "의 " + questionId + "번 질문 에러");
                        }
                    }

                    ResultRequestDto HundredAddDto = new ResultRequestDto(
                            dto.userId() + 100,  // 기존 userId를 피하기 위한 조작
                            dto.surveyId(),
                            dto.answers()
                    );

                    resultService.processSurveyResultToLoad(HundredAddDto);
                }

                StringBuilder statsContent = new StringBuilder();
                statsContent.append("=== 질문별 선택지 통계 ===\n");
                statsContent.append("총 응답 수: ").append(responses.size()).append("\n\n");

                for (int questionId = 1; questionId <= 16; questionId++) {
                    final int currentQuestionId = questionId;

                    Map<Long, Long> choiceCountMap = responses.stream()
                        .flatMap(response -> response.answers().stream())
                        .filter(answer -> answer.questionId().equals((long) currentQuestionId))
                        .collect(Collectors.groupingBy(
                            answer -> answer.choiceId(),
                            Collectors.counting()
                        ));

                    List<Long> counts = choiceCountMap.keySet().stream()
                        .sorted()
                        .map(choiceId -> choiceCountMap.getOrDefault(choiceId, 0L))
                        .collect(Collectors.toList());

                    statsContent.append("Question ").append(questionId)
                        .append(": ").append(counts).append("\n");
                }

                statsContent.append("\n=== 특성별 선택지 통계 ===\n");

                List<Long> p1Counts = calculatePropertyCounts(responses, List.of(1, 2));
                statsContent.append("즉시 전력형 (P_1): ").append(p1Counts).append("\n");

                List<Long> p2Counts = calculatePropertyCounts(responses, List.of(3, 4));
                statsContent.append("성장 가능형 (P_2): ").append(p2Counts).append("\n");

                List<Long> p3Counts = calculatePropertyCounts(responses, List.of(5, 6));
                statsContent.append("직면형 (P_3): ").append(p3Counts).append("\n");

                List<Long> p4Counts = calculatePropertyCounts(responses, List.of(7, 8));
                statsContent.append("숙고형 (P_4): ").append(p4Counts).append("\n");

                List<Long> p5Counts = calculatePropertyCounts(responses, List.of(9, 10));
                statsContent.append("혁신적 성향 (P_5): ").append(p5Counts).append("\n");

                List<Long> p6Counts = calculatePropertyCounts(responses, List.of(11, 12));
                statsContent.append("전통적 성향 (P_6): ").append(p6Counts).append("\n");

                List<Long> p7Counts = calculatePropertyCounts(responses, List.of(13, 14));
                statsContent.append("객관적 자료형 (P_7): ").append(p7Counts).append("\n");

                List<Long> p8Counts = calculatePropertyCounts(responses, List.of(15, 16));
                statsContent.append("주관적 인상형 (P_8): ").append(p8Counts).append("\n");

                statsContent.append("\n=== 카테고리별 통계 ===\n");

                List<Long> workCapability = addCounts(p1Counts, reverseCounts(p2Counts));
                statsContent.append("업무 능력 (성장 가능형 ← → 즉시 전력형): ").append(workCapability).append("\n");

                List<Long> conflictResolution = addCounts(p3Counts, reverseCounts(p4Counts));
                statsContent.append("갈등 대응 방식 (숙고형 ← → 직면형): ").append(conflictResolution).append("\n");

                List<Long> personalityPreference = addCounts(p5Counts, reverseCounts(p6Counts));
                statsContent.append("성향/인성 (전통적 성향 ← → 혁신적 성향): ").append(personalityPreference).append("\n");

                List<Long> evaluationCriteria = addCounts(p7Counts, reverseCounts(p8Counts));
                statsContent.append("평가 기준 (주관적 인상형 ← → 객관적 자료형): ").append(evaluationCriteria).append("\n");

                try {
                    File dataDir = new File("data");
                    if (!dataDir.exists()) {
                        dataDir.mkdirs();
                    }

                    try (FileWriter writer = new FileWriter("data/question_stats.txt")) {
                        writer.write(statsContent.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private List<Long> calculatePropertyCounts(List<ResultRequestDto> responses, List<Integer> questionIds) {
                Map<Integer, Long> scoreSum = new java.util.HashMap<>();

                for (int score = 1; score <= 5; score++) {
                    scoreSum.put(score, 0L);
                }

                for (ResultRequestDto response : responses) {
                    for (int questionId : questionIds) {
                        response.answers().stream()
                            .filter(answer -> answer.questionId().equals((long) questionId))
                            .forEach(answer -> {
                                int displayOrder = getDisplayOrderFromChoiceId(answer.choiceId(), questionId);
                                scoreSum.put(displayOrder, scoreSum.get(displayOrder) + 1);
                            });
                    }
                }

                return List.of(scoreSum.get(1), scoreSum.get(2), scoreSum.get(3), scoreSum.get(4), scoreSum.get(5));
            }

            private int getDisplayOrderFromChoiceId(Long choiceId, int questionId) {
                int baseChoiceId = (questionId - 1) * 5;
                return (int)(choiceId - baseChoiceId);
            }

            private List<Long> reverseCounts(List<Long> counts) {
                return List.of(counts.get(4), counts.get(3), counts.get(2), counts.get(1), counts.get(0));
            }

            private List<Long> addCounts(List<Long> counts1, List<Long> counts2) {
                return List.of(
                    counts1.get(0) + counts2.get(0),
                    counts1.get(1) + counts2.get(1),
                    counts1.get(2) + counts2.get(2),
                    counts1.get(3) + counts2.get(3),
                    counts1.get(4) + counts2.get(4)
                );
            }
        };
    }
}
