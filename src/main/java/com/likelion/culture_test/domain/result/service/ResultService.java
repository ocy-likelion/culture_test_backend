package com.likelion.culture_test.domain.result.service;

import com.likelion.culture_test.domain.cluster.entity.Cluster;
import com.likelion.culture_test.domain.cluster.service.ClusterService;
import com.likelion.culture_test.domain.result.dto.*;
import com.likelion.culture_test.domain.result.entity.Result;
import com.likelion.culture_test.domain.result.entity.ResultDetail;
import com.likelion.culture_test.domain.result.enums.ResultType;
import com.likelion.culture_test.domain.result.repository.ResultDetailRepository;
import com.likelion.culture_test.domain.result.repository.ResultRepository;
import com.likelion.culture_test.domain.survey.entity.Choice;
import com.likelion.culture_test.domain.survey.entity.Survey;
import com.likelion.culture_test.domain.survey.enums.Category;
import com.likelion.culture_test.domain.survey.repository.ChoiceRepository;
import com.likelion.culture_test.domain.survey.repository.SurveyRepository;
import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import com.likelion.culture_test.global.resolver.LoginUser;
import com.likelion.culture_test.global.util.ScoreUtils;
import com.likelion.culture_test.global.util.TraitLabelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
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
    private final ClusterService clusterService;
    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;


    @Transactional
    public void processSurveyResult(ResultRequestDto dto) {
        Survey survey = surveyRepository.findById(dto.surveyId())
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        List<Long> choiceIds = dto.answers().stream()
                .map(AnswerDto::choiceId)
                .toList();

        List<Choice> choices = choiceRepository.findAllById(choiceIds);

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


    @Transactional
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
// 이거를 과거 테스트 내역용으로
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

    @Transactional
    public AnalysisResponseDto getLatestCategoryScores(Long userId, Long surveyId) {
        Optional<Result> resOpt = resultRepository.findTopByUserIdAndSurveyIdOrderByCreatedAtDesc(userId, surveyId);
        // 해당값이 없으면 .orElseThrow(() -> new CustomException(ErrorCode.RESULT_NOT_FOUND));
        // 를 하는 기존코드 대신 프론트로 대기 상태라는 표시로 대체

        String initialImageUrl = "/images/default.png";
        if (resOpt.isEmpty()){
            return new AnalysisResponseDto(ResultType.not_yet.getDescription(), "done", List.of(), ResultType.not_yet.getDetailDescription(), initialImageUrl);
        }



        Result latest = resOpt.get();


        List<ResultDetail> details = resultDetailRepository.findByResult(latest);



        Map<Category, Double> avgByCategory = details.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getProperty().getCategory(),
                        Collectors.averagingDouble(ResultDetail::getScore)
                ));
        List<TraitItemDto> items = new ArrayList<>();

        for (var e : avgByCategory.entrySet()) {
            Category cat = e.getKey();
            double raw = e.getValue();

            int leftScore = (int) Math.round((raw + 2) / 4 * 100);
            int rightScore = 100 - leftScore;

//            int leftScore = (int) Math.round(leftRatio);
//            int rightScore = 100 - leftScore;

            String leftType = TraitLabelUtils.getPositiveLabel(cat);
            String rightType = TraitLabelUtils.getNegativeLabel(cat);



            items.add(new TraitItemDto(
                    cat.getDescription(),
                    new TraitSideDto(leftType,  leftScore),
                    new TraitSideDto(rightType, rightScore)
            ));



        }


        Cluster cluster = clusterService.findMostSimilarClusterFromLatestGeneration(avgByCategory);
        String description = (cluster != null && cluster.getDescription() != null)
                ? cluster.getDescription()
                : ResultType.not_clusterd.getDescription(); // ❗ 군집화 전 상태 처리

        ResultType resultType = Arrays.stream(ResultType.values())
                .filter(rt -> rt.getDescription().equals(description))
                .findFirst()
                .orElse(ResultType.not_yet);  // 혹은 not_clusterd 등 fallback 설정
        String detailDescription = resultType.getDetailDescription();





        latest.setCluster(cluster);
        resultRepository.save(latest);

        String imageName = extractLastWord(description) + ".png";
        String imagePath = "src/main/resources/static/images/" + imageName;
        String imageUrl;

        File file = new File(imagePath);
        if (file.exists()) {
            imageUrl = "/images/" + imageName;
        } else {
            imageUrl = "/images/default.png";
        }



        return new AnalysisResponseDto(description, "done", items, detailDescription, imageUrl);
    }



    @Transactional
    public void sendAllVectorsToFastApi(int clusterNum) {
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
        log.info("보내기 직전 생성된 벡터ㅇㅇㅇㅇㅇㅇ 수: {}", vectors.size());

        VectorBatchRequest vectorBatchRequest = new VectorBatchRequest(clusterNum, vectors);
        webClient.post()
                .uri("/receive/vector/batch")
                .bodyValue(vectorBatchRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> log.error("전체 벡터 전송 실패: {}", e.getMessage()))
                .subscribe();
        log.info("{}{}", fastApiBaseUrl, "/receive/vector/batch");
        //.block();

    }



    private ResultType decideResultType(List<TraitItemDto> items) {

        int[] flag = { -1, -1, -1, -1 };   // A,B,C,D 순

        for (TraitItemDto item : items) {
            int left  = item.left().score();
            int right = item.right().score();

            if (left == right) continue;         // tie → 그대로 -1 (미결정)

            if (item.label().equals(Category.WORK_CAPABILITY.getDescription())) {
                flag[0] = left > right ? 1 : 0;
            } else if (item.label().equals(Category.CONFLICT_RESOLUTION.getDescription())) {
                flag[1] = left > right ? 1 : 0;
            } else if (item.label().equals(Category.PERSONALITY_PREFERENCE.getDescription())) {
                flag[2] = left > right ? 1 : 0;
            } else if (item.label().equals(Category.EVALUATION_CRITERIA.getDescription())) {
                flag[3] = left > right ? 1 : 0;
            }

        }

        // 하나라도 미결정(-1) 이면 not_yet
        for (int f : flag) if (f == -1) return ResultType.not_yet;

        char[] code = {
                flag[0] == 1 ? 'A' : 'a',
                flag[1] == 1 ? 'B' : 'b',
                flag[2] == 1 ? 'C' : 'c',
                flag[3] == 1 ? 'D' : 'd'
        };
        String key = new String(code);  // 예: "AbcD"

        return ResultType.valueOf(key); // 반드시 존재, 없으면 예외 → not_yet
    }

    public List<ResultHistoryDto> getResultHistoryByUserId(Long userId) {
        List<Result> results = resultRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return results.stream()
                .map(result -> new ResultHistoryDto(
                        result.getId(),
                        result.getCluster() != null ? result.getCluster().getDescription() : ResultType.not_clusterd.getDescription(),
                        result.getCreatedAt().toLocalDate()
                ))
                .collect(Collectors.toList());
    }



    @Transactional
    public AnalysisResponseWithNicknameDto getCategoryScoresByResultId(Long resultId, @LoginUser User user) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESULT_NOT_FOUND));

        List<ResultDetail> details = resultDetailRepository.findByResult(result);

        String initialImageUrl = "/images/default.png";




        if (details.isEmpty()) {
            AnalysisResponseDto analysisResponseDto = new AnalysisResponseDto(ResultType.not_yet.getDescription(), "done", List.of(), ResultType.not_yet.getDetailDescription(), initialImageUrl);
            return new AnalysisResponseWithNicknameDto(analysisResponseDto, user.getNickname());
        }

        Map<Category, Double> avgByCategory = details.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getProperty().getCategory(),
                        Collectors.averagingDouble(ResultDetail::getScore)
                ));

        List<TraitItemDto> items = new ArrayList<>();

        for (Map.Entry<Category, Double> entry : avgByCategory.entrySet()) {
            Category category = entry.getKey();
            double rawAvg = entry.getValue();

            int leftScore = (int) Math.round((rawAvg + 2) / 4 * 100);
            int rightScore = 100 - leftScore;

            String leftLabel = TraitLabelUtils.getPositiveLabel(category);
            String rightLabel = TraitLabelUtils.getNegativeLabel(category);

            items.add(new TraitItemDto(
                    category.getDescription(),
                    new TraitSideDto(leftLabel, leftScore),
                    new TraitSideDto(rightLabel, rightScore)
            ));
        }

        Cluster cluster = result.getCluster();

        String description = (cluster != null && cluster.getDescription() != null)
                ? cluster.getDescription()
                : ResultType.not_clusterd.getDescription(); // ❗ 군집화 전 상태 처리

        ResultType resultType = Arrays.stream(ResultType.values())
                .filter(rt -> rt.getDescription().equals(description))
                .findFirst()
                .orElse(ResultType.not_yet);  // 혹은 not_clusterd 등 fallback 설정
        String detailDescription = resultType.getDetailDescription();

        String imageName = extractLastWord(description) + ".png";
        String imagePath = "src/main/resources/static/images/" + imageName;
        String imageUrl;

        File file = new File(imagePath);
        if (file.exists()) {
            imageUrl = "/images/" + imageName;
        } else {
            imageUrl = "/images/default.png";
        }

        AnalysisResponseDto analysisResponseDto = new AnalysisResponseDto(description, "done", items, detailDescription, imageUrl);


        return new AnalysisResponseWithNicknameDto(analysisResponseDto, user.getNickname());
    }

    private String extractLastWord(String sentence) {
        if (sentence == null || sentence.isBlank()) return "default";
        String[] words = sentence.trim().split(" ");
        return words[words.length - 1];
    }



    @Transactional
    public void processSurveyResultToLoad(ResultRequestDto dto) {
        Survey survey = surveyRepository.findById(dto.surveyId())
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_NOT_FOUND));

        List<Long> choiceIds = dto.answers().stream()
                .map(AnswerDto::choiceId)
                .toList();

        List<Choice> choices = choiceRepository.findAllById(choiceIds);

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






























}
