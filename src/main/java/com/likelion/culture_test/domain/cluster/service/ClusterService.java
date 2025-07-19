package com.likelion.culture_test.domain.cluster.service;

import com.likelion.culture_test.domain.cluster.dto.ClusterResponseDto;
import com.likelion.culture_test.domain.cluster.entity.Cluster;
import com.likelion.culture_test.domain.cluster.entity.ClusterGeneration;
import com.likelion.culture_test.domain.cluster.repository.ClusterGenerationRepository;
import com.likelion.culture_test.domain.cluster.repository.ClusterRepository;
import com.likelion.culture_test.domain.result.entity.Result;
import com.likelion.culture_test.domain.result.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

        log.info("클러스터어쩌고");
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
                    .centroid(centroid) // List<Double>
                    .generation(generation)
                    .description("설명 없음")
                    .build();

            savedClusters.add(clusterRepository.save(cluster));
        }
        log.info("클러스터끝");


        // 라벨과 Result 매칭 (예: 가장 최근 Result들 순서대로)
//        List<Result> results = resultRepository.findAllByLatestSomeLogic(); // 가장 최근 것 5개 등
//
//        for (int i = 0; i < results.size(); i++) {
//            int label = dto.getResult().getLabels().get(i);
//            Cluster cluster = savedClusters.get(label);
//
//            Result result = results.get(i);
//            result.setCluster(cluster);
//            resultRepository.save(result);
//        }

        List<Result> results = resultRepository.findAllByOrderByIdAsc(); // 또는 createdAt 기준 정렬도 가능

        int N = dto.getResult().getLabels().size();
        if (results.size() < N) {
            throw new IllegalStateException("저장된 결과 수가 라벨 수보다 적습니다.");
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


}
