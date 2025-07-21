package com.likelion.culture_test.domain.result.dto;

import java.util.List;

public record VectorBatchRequest(int clusterNum, List<List<Double>> vectors) {
}
