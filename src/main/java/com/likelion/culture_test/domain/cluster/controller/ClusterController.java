package com.likelion.culture_test.domain.cluster.controller;

import com.likelion.culture_test.domain.cluster.dto.ClusterResponseDto;
import com.likelion.culture_test.domain.cluster.service.ClusterService;
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
@RequestMapping("/api/v1/cluster")
@Tag(name = "군집 API", description = "군집 데이터 API")
public class ClusterController {

    private final ClusterService clusterService;

    @PostMapping("/result")
    public ResponseEntity<Void> receiveClusteredResult(@RequestBody ClusterResponseDto responseDto){
        log.info("클러스터링 상태: {}", responseDto.getStatus());
        log.info("라벨 수: {}", responseDto.getResult().getLabels().size());
        log.info("군집 중심점 개수: {}", responseDto.getResult().getCentroids().size());
        log.info("보낸 벡터 데이터들 처리 후 다시 받아오기?");
        clusterService.saveClustered(responseDto);
        return ResponseEntity.ok().build();
    }
}
