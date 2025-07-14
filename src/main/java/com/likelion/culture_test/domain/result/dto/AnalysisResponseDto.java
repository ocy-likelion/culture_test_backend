package com.likelion.culture_test.domain.result.dto;

import java.util.List;

public record AnalysisResponseDto(String status, List<TraitItemDto> result) {
}
