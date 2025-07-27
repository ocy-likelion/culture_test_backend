package com.likelion.culture_test.domain.cluster.service;

import com.likelion.culture_test.domain.cluster.dto.ClusterInfoDto;
import com.likelion.culture_test.domain.cluster.dto.ClusterResponseDto;
import com.likelion.culture_test.domain.cluster.entity.Centroid;
import com.likelion.culture_test.domain.cluster.entity.Cluster;
import com.likelion.culture_test.domain.cluster.entity.ClusterGeneration;
import com.likelion.culture_test.domain.cluster.repository.ClusterGenerationRepository;
import com.likelion.culture_test.domain.cluster.repository.ClusterRepository;
import com.likelion.culture_test.domain.result.entity.Result;
import com.likelion.culture_test.domain.result.enums.ResultType;
import com.likelion.culture_test.domain.result.repository.ResultRepository;
import com.likelion.culture_test.domain.survey.enums.Category;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.likelion.culture_test.domain.cluster.classfier.ResultTypeResolver;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClusterService {


    private final ClusterRepository clusterRepository;
    private final ClusterGenerationRepository clusterGenerationRepository;
    private final ResultRepository resultRepository;

    @Transactional
    public void saveClustered(ClusterResponseDto dto){

//        List<Integer> labels = dto.getResult().getLabels();
//        List<List<Double>> centroids = dto.getResult().getCentroids();

//        ClusterGeneration generation = clusterGenerationRepository.save(
//                new ClusterGeneration("v1", dto.getResult().getCentroids().size(), "KMeans 결과")
//        );


        ClusterGeneration generation = ClusterGeneration.builder()
                .version("v1")
                .clusterCount(dto.getResult().getCentroids().size())
                .description("KMeans 결과")
                .build();

        clusterGenerationRepository.save(generation);


        List<Cluster> savedClusters = new ArrayList<>();
        for (int label = 0; label < dto.getResult().getCentroids().size(); label++) {
            List<Double> centroid = dto.getResult().getCentroids().get(label);

            Cluster cluster = Cluster.builder()
                    .label(label)
                    .name("Cluster_" + label)
                    //.centroids(centroid) // List<Double>
                    .generation(generation)
                    .description("아직 설명 없음")
                    .build();

            List<Category> categories = List.of(
                    Category.WORK_CAPABILITY,
                    Category.CONFLICT_RESOLUTION,
                    Category.PERSONALITY_PREFERENCE,
                    Category.EVALUATION_CRITERIA
            );

            List<Centroid> centroidEntities = IntStream.range(0, centroid.size())
                    .mapToObj(i -> Centroid.builder()
                            .value(centroid.get(i))
                            .category(categories.get(i))
                            .cluster(cluster)
                            .build())
                    .collect(Collectors.toList());

            cluster.setCentroids(centroidEntities);

            // 1. 벡터 추출 (Centroid 값 4개)
            List<Double> vector = cluster.getCentroids().stream()
                    .filter(c -> c.getId() != null && c.getValue() != null)
                    .sorted(Comparator.comparing(Centroid::getId)) // 또는 category가 있다면 그것으로 정렬
                    .map(Centroid::getValue)
                    .collect(Collectors.toList());

            // 2. 값 중 하나라도 0이 있는지 체크
            if (vector.stream().anyMatch(v -> v == 0.0)) {
                cluster.setDescription("설명 없음"); // 또는 "미확정"
            } else {
                // 3. 값 기준으로 대소문자 문자열 만들고
                ResultType resultType = ResultTypeResolver.resolveResultType(centroidEntities);

                // 4. 해당 결과에 맞는 description 적용
                cluster.setDescription(resultType.getDescription());
            }


            savedClusters.add(clusterRepository.save(cluster));
        }







        List<Result> results = resultRepository.findAllByOrderByIdAsc(); // 또는 createdAt 기준 정렬도 가능

        int N = dto.getResult().getLabels().size();
        if (results.size() < N) {
            throw new CustomException(ErrorCode.RESULT_LABEL_COUNT_MISMATCH);
        }

        List<Result> recentResults = results.subList(results.size() - N, results.size()); // 끝 N개 자르기

        for (int i = 0; i < N; i++) {
            int label = dto.getResult().getLabels().get(i);
            Cluster cluster = savedClusters.get(label);

            Result result = recentResults.get(i);
            result.setCluster(cluster);
            resultRepository.save(result);
        }


    }

    private static final List<Category> orderedCategories = List.of(
            Category.WORK_CAPABILITY,
            Category.CONFLICT_RESOLUTION,
            Category.PERSONALITY_PREFERENCE,
            Category.EVALUATION_CRITERIA
    );

    public Cluster findMostSimilarClusterFromLatestGeneration(Map<Category, Double> categoryScores) {
//        ClusterGeneration latestGen = clusterGenerationRepository.findTopByOrderByIdDesc()
//                .orElseThrow(() -> new CustomException(ErrorCode.CLUSTER_GENERATION_NOT_FOUND));
//
//        List<Cluster> clusters = clusterRepository.findByGeneration(latestGen);
//
//        double[] userVector = orderedCategories.stream()
//                .mapToDouble(cat -> categoryScores.getOrDefault(cat, 0.0))
//                .toArray();
//
//        Cluster mostSimilar = null;
//        double minDistance = Double.MAX_VALUE;
//
//        for (Cluster cluster : clusters) {
//            List<Double> center = extractCenterVector(cluster); // 반드시 4개 실수값
//            double distance = 0.0;
//            for (int i = 0; i < center.size(); i++) {
//                distance += Math.pow(userVector[i] - center.get(i), 2);
//            }
//            distance = Math.sqrt(distance);
//
//            if (distance < minDistance) {
//                minDistance = distance;
//                mostSimilar = cluster;
//            }
//        }
//
//        return mostSimilar;

        try {
            ClusterGeneration latestGen = clusterGenerationRepository.findTopByOrderByIdDesc()
                    .orElseThrow(() -> new CustomException(ErrorCode.CLUSTER_GENERATION_NOT_FOUND));

            List<Cluster> clusters = clusterRepository.findByGeneration(latestGen);

            double[] userVector = orderedCategories.stream()
                    .mapToDouble(cat -> categoryScores.getOrDefault(cat, 0.0))
                    .toArray();

            Cluster mostSimilar = null;
            double minDistance = Double.MAX_VALUE;

            for (Cluster cluster : clusters) {
                List<Double> center = extractCenterVector(cluster); // 반드시 4개 실수값
                double distance = 0.0;
                for (int i = 0; i < center.size(); i++) {
                    distance += Math.pow(userVector[i] - center.get(i), 2);
                }
                distance = Math.sqrt(distance);

                if (distance < minDistance) {
                    minDistance = distance;
                    mostSimilar = cluster;
                }
            }

            return mostSimilar;
        }
        catch (CustomException e){
            log.warn("No ClusterGeneration found. Returning null cluster.");
            return null; // 못찾으면 null 반환

        }
    }

    public static List<Double> extractCenterVector(Cluster cluster) {
        Map<Category, Double> centroidMap = cluster.getCentroids().stream()
                .collect(Collectors.toMap(
                        Centroid::getCategory,
                        Centroid::getValue
                ));

        return orderedCategories.stream()
                .map(cat -> centroidMap.getOrDefault(cat, 0.0)) // 값 없으면 기본값 0.0
                .collect(Collectors.toList());
    }


    public ClusterInfoDto clusterPercentage(Long userId, Long surveyId){

        Optional<Result> recentResult = resultRepository.findTopByUserIdAndSurveyIdOrderByCreatedAtDesc(userId, surveyId);
        Cluster cluster;
        if (recentResult.isEmpty()) {
            throw new CustomException(ErrorCode.RESULT_NOT_FOUND);
//            Result result = recentResult.get();
//            cluster = result.getCluster();
        }

        cluster = recentResult.get().getCluster();
        if (cluster == null){
            throw new CustomException(ErrorCode.CLUSTER_NOT_FOUND);


        }

        long totalResults = resultRepository.count();
        long clusterResults = resultRepository.countByClusterId(cluster.getId());
        double percentage = 0.0;
        if (totalResults > 0) {
            percentage = (double) clusterResults / totalResults * 100;
            percentage = Math.round(percentage * 10) / 10.0;
        }





        return new ClusterInfoDto(cluster.getGeneration().getId(), cluster.getName(), percentage);


    }


}
