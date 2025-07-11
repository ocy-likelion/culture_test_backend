package com.likelion.culture_test.domain.result.service;

import com.likelion.culture_test.domain.result.dto.*;
import com.likelion.culture_test.domain.result.entity.Result;
import com.likelion.culture_test.domain.result.entity.ResultDetail;
import com.likelion.culture_test.domain.result.repository.ResultDetailRepository;
import com.likelion.culture_test.domain.result.repository.ResultRepository;
import com.likelion.culture_test.domain.survey.entity.Choice;
import com.likelion.culture_test.domain.survey.entity.Survey;
import com.likelion.culture_test.domain.survey.enums.Category;
import com.likelion.culture_test.domain.survey.repository.ChoiceRepository;
import com.likelion.culture_test.domain.survey.repository.SurveyRepository;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import com.likelion.culture_test.global.util.ScoreUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final ResultDetailRepository resultDetailRepository;
    private final WebClient webClient;

    public void processSurveyResult(ResultRequestDto dto) {
        Survey survey = surveyRepository.findById(dto.surveyId())
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));


        List<AnswerDto> answers = dto.answers();

        List<Long> questionIds = answers.stream()
                .map(AnswerDto::questionId)
                .distinct()
                .sorted()
                .toList();

        List<Object[]> maxDisplayOrderList = choiceRepository.findMaxDisplayOrderByQuestionIds(questionIds);
        Map<Long, Integer> questionMaxDisplayOrderMap = new HashMap<>();
        for (Object[] row : maxDisplayOrderList) {
            Long questionId = (Long) row[0];
            Integer maxDisplayOrder = ((Number) row[1]).intValue();
            questionMaxDisplayOrderMap.put(questionId, maxDisplayOrder);
        }

        Map<Long, Integer> questionOffsetMap = new HashMap<>();
        int offset = 0;
        for (Long qId : questionIds) {
            questionOffsetMap.put(qId, offset);
            offset += questionMaxDisplayOrderMap.getOrDefault(qId, 5); // 기본 5, 안전장치
        }

        List<Long> calculatedChoiceIds = new ArrayList<>();
        for (AnswerDto answer : answers) {
            Long questionId = answer.questionId();
            int displayOrder = answer.choiceId().intValue();  // 여기 choiceId에 displayOrder가 담겨있다고 가정

            int base = questionOffsetMap.getOrDefault(questionId, 0);
            long calcChoiceId = base + displayOrder;
            calculatedChoiceIds.add(calcChoiceId);
        }


        // choiceId만 추출
//        List<Long> choiceIds = dto.answers().stream()
//                .map(AnswerDto::choiceId)
//                .toList();

        // choice 객체 전부 가져옴
        List<Choice> choices = choiceRepository.findAllById(calculatedChoiceIds);

        // 분야별 점수 누적
        Map<String, List<Integer>> fieldScoreMap = new HashMap<>();


        // 결과 저장용 Result 엔티티 생성
        Result result = Result.builder()
                .userId(dto.userId())
                .survey(survey)
                .field("") // 나중에 채움
                .cluster(null)
                .build();

        resultRepository.save(result);

//        List<ResultDetail> existingDetails = resultDetailRepository.findByUserIdAndSurveyId(dto.userId(), dto.surveyId());
//        resultDetailRepository.deleteAll(existingDetails);

        for (int i = 0; i < choices.size(); i++) {
            Choice choice = choices.get(i);
            Long expectedQuestionId = dto.answers().get(i).questionId();

            if (!choice.getQuestion().getId().equals(expectedQuestionId)) {
                throw new CustomException(ErrorCode.QUESTION_CHOICE_MISMATCH);
            }

            String fieldName = choice.getProperty().getName(); // 분야 이름
            int score = ScoreUtils.calculateScore(choice.getDisplayOrder(), choice.getQuestion().getProperty().getId());

            fieldScoreMap
                    .computeIfAbsent(fieldName, k -> new ArrayList<>())
                    .add(score);

            // ResultDetail 엔티티 생성 및 저장
            ResultDetail detail = ResultDetail.builder()
                    .result(result)
                    .question(choice.getQuestion())
                    .choice(choice)
                    .property(choice.getProperty())
                    .score((double) score)
                    .build();

            resultDetailRepository.save(detail);
        }

        // 분야 이름 고정 순서 정렬
        List<String> sortedFields = new ArrayList<>(fieldScoreMap.keySet());
        Collections.sort(sortedFields); // 알파벳 정렬 or 커스텀 정렬도 가능

        // 분야별 평균
        List<Double> vector = sortedFields.stream()
                .map(field -> {
                    List<Integer> scores = fieldScoreMap.get(field);
                    return scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
                })
                .toList();

        String field = vector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        result.setField(field);

//        Result result = Result.builder()
//                .userId(dto.userId())
//                .survey(survey)
//                .field(field)
//                .cluster(null) // 추후 군집 분석 후 설정
//                .build();

        resultRepository.save(result);
    }



    public List<ResultDetailResponseDto> getUserAnswersDto(ResultQueryDto dto) {
        return resultDetailRepository.findWithDetailsByUserIdAndSurveyId(dto.getUserId(), dto.getSurveyId()).stream()
                .map(rd -> new ResultDetailResponseDto(
                        rd.getQuestion().getId(),
                        rd.getQuestion().getContent(),
                        rd.getChoice().getId(),
                        rd.getChoice().getContent(),
                        rd.getProperty().getName(),
                        rd.getScore()
                ))
                .toList();
    }

    public Map<String, Double> getScoreByCategory(ResultQueryDto dto) {
        List<Object[]> rows = resultDetailRepository.aggregateScoreByProperty(dto.getUserId(), dto.getSurveyId());
        Map<String, Double> map = new HashMap<>();
        for (Object[] row : rows) {
            Category category = (Category) row[0];
            Double score = (Double) row[1];
            map.put(category.name(), score);
            // map.put(category.getDescription(), score);
        }
        return map;
    }

    public List<ResultDetail> getUserAnswers(ResultQueryDto dto) {
        return resultDetailRepository.findByUserIdAndSurveyId(dto.getUserId(), dto.getSurveyId());
    }


    public List<Double> getVectorByUserAndSurvey(Long userId, Long surveyId) {
        ResultQueryDto dto = new ResultQueryDto(userId, surveyId);
        Map<String, Double> scoreMap = getScoreByCategory(dto); // 이미 존재하는 메서드 사용

        return Arrays.stream(Category.values())
                .map(cat -> scoreMap.getOrDefault(cat.name(), 0.0))
                .toList();
    }



    public void sendVectorToFastApi(Long userId, Long surveyId, List<Double> vector) {
        VectorRequestDto requestDto = new VectorRequestDto(userId, surveyId, vector);

        webClient.post()
                .uri("/receive/vector/test")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.err.println("전송 실패: " + e.getMessage()))
                .subscribe();
    }

    public List<List<Double>> getVectorsByCreatedAt(Long userId, Long surveyId) {
        List<Result> results = resultRepository.findByUserIdAndSurveyIdOrderByCreatedAtDesc(userId, surveyId);

        return results.stream().map(result -> {
            List<ResultDetail> details = resultDetailRepository.findByResult(result);

            Map<String, List<Double>> categoryToScores = new HashMap<>();
            for (ResultDetail detail : details) {
                String category = detail.getProperty().getCategory().name();
                categoryToScores
                        .computeIfAbsent(category, k -> new ArrayList<>())
                        .add(detail.getScore());
            }



            return Arrays.stream(Category.values())
                    .map(cat -> categoryToScores.getOrDefault(cat.name(), List.of(0.0)).stream()
                            .mapToDouble(Double::doubleValue).average().orElse(0.0))
                    .toList();
        }).toList();
    }

    public List<CategoryScoreWithCreatedAtDto> getCategoryScoresByCreatedAt(Long userId, Long surveyId) {
        List<Result> results = resultRepository.findByUserIdAndSurveyIdOrderByCreatedAtDesc(userId, surveyId);

        return results.stream().map(result -> {
            List<ResultDetail> details = resultDetailRepository.findByResult(result);

            Map<String, List<Double>> categoryToScores = new HashMap<>();
            for (ResultDetail detail : details) {
                String category = detail.getProperty().getCategory().name();
                categoryToScores
                        .computeIfAbsent(category, k -> new ArrayList<>())
                        .add(detail.getScore());
            }

            Map<String, Double> categoryScores = new HashMap<>();
            for (Map.Entry<String, List<Double>> entry : categoryToScores.entrySet()) {
                categoryScores.put(
                        entry.getKey(),
                        entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
                );
            }

            return new CategoryScoreWithCreatedAtDto(result.getCreatedAt(), categoryScores);
        }).toList();
    }


    public List<Double> getLatestVector(Long userId, Long surveyId) {
        Result latest = resultRepository.findTopByUserIdAndSurveyIdOrderByCreatedAtDesc(userId, surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESULT_NOT_FOUND));

        List<ResultDetail> details = resultDetailRepository.findByResult(latest);

        Map<String, List<Double>> categoryToScores = new HashMap<>();
        for (ResultDetail detail : details) {
            String category = detail.getProperty().getCategory().name();
            categoryToScores
                    .computeIfAbsent(category, k -> new ArrayList<>())
                    .add(detail.getScore());
        }

        return Arrays.stream(Category.values())
                .map(cat -> categoryToScores.getOrDefault(cat.name(), List.of(0.0)).stream()
                        .mapToDouble(Double::doubleValue).average().orElse(0.0))
                .toList();


    }

    public CategoryScoreWithCreatedAtDto getLatestCategoryScores(Long userId, Long surveyId) {
        Result latest = resultRepository.findTopByUserIdAndSurveyIdOrderByCreatedAtDesc(userId, surveyId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESULT_NOT_FOUND));

        List<ResultDetail> details = resultDetailRepository.findByResult(latest);

        Map<String, List<Double>> categoryToScores = new HashMap<>();
        for (ResultDetail detail : details) {
            String category = detail.getProperty().getCategory().name();
            categoryToScores
                    .computeIfAbsent(category, k -> new ArrayList<>())
                    .add(detail.getScore());
        }

        Map<String, Double> categoryScores = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : categoryToScores.entrySet()) {
            categoryScores.put(
                    entry.getKey(),
                    entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
            );
        }

        return new CategoryScoreWithCreatedAtDto(latest.getCreatedAt(), categoryScores);
    }




    public void sendAllVectorsToFastApi() {
        List<Result> results = resultRepository.findAll();

        List<List<Double>> vectors = results.stream().map(result -> {
            List<ResultDetail> details = resultDetailRepository.findByResult(result);

            Map<String, List<Double>> categoryToScores = new HashMap<>();
            for (ResultDetail detail : details) {
                String category = detail.getProperty().getCategory().name();
                categoryToScores
                        .computeIfAbsent(category, k -> new ArrayList<>())
                        .add(detail.getScore());
            }

            return Arrays.stream(Category.values())
                    .map(cat -> categoryToScores.getOrDefault(cat.name(), List.of(0.0)).stream()
                            .mapToDouble(Double::doubleValue).average().orElse(0.0))
                    .toList();
        }).toList();

        webClient.post()
                .uri("/receive/vector/batch")
                .bodyValue(vectors)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> log.error("전체 벡터 전송 실패: {}", e.getMessage()))
                .subscribe();
    }













}
