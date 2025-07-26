package com.likelion.culture_test.domain.cluster.controller;

import com.likelion.culture_test.domain.cluster.dto.ClusterInfoDto;
import com.likelion.culture_test.domain.cluster.dto.ClusterResponseDto;
import com.likelion.culture_test.domain.cluster.service.ClusterService;
import com.likelion.culture_test.domain.user.entity.User;
import com.likelion.culture_test.global.resolver.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cluster")
@Tag(name = "군집 API", description = "군집 데이터 API")
public class ClusterController {

    private final ClusterService clusterService;

    @PostMapping("/result")
    public ResponseEntity<Void> receiveClusteredResult(@RequestBody ClusterResponseDto responseDto){
        System.out.println("==== 컨트롤러 진입 확인 ====");
        log.info("클러스터링 상태: {}", responseDto.getStatus());
        log.info("라벨 수: {}", responseDto.getResult().getLabels().size());
        log.info("라벨 : {}", responseDto.getResult().getLabels());
        log.info("군집 중심점 개수: {}", responseDto.getResult().getCentroids().size());
        log.info("군집 중심점: {}", responseDto.getResult().getCentroids());
        log.info("보낸 벡터 데이터들 처리 후 다시 받아오기?");
        clusterService.saveClustered(responseDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "방금 나온 해당 결과가 속한 몇번째 돌린 군집 세대에 해당하는지, 군집 명, 전체에서의 퍼센티지 반환")
    @GetMapping("/percentage")
    public ResponseEntity<ClusterInfoDto> getClusterPercentage(
            @Parameter(hidden = true) @LoginUser User user,
            @RequestParam Long surveyId
    ) {

        Long userId = user.getId();
        ClusterInfoDto dto = clusterService.clusterPercentage(userId, surveyId);
        return ResponseEntity.ok(dto);
    }
}
