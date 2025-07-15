package com.likelion.culture_test.domain.result.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record CategoryScoreWithCreatedAtDto(LocalDateTime createdAt, Map<String, Double> categoryScores) {
}
