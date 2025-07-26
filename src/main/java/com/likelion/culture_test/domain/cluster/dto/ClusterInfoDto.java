package com.likelion.culture_test.domain.cluster.dto;
// 자신이 속한 군집 유형이 전체 군집의 몇프로를 차지하고 있는지 반환하는 API 추가
public record ClusterInfoDto(Long GenerationId, String ClusterName, Double percentage) {
}
