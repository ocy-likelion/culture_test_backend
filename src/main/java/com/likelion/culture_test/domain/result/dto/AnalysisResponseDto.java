package com.likelion.culture_test.domain.result.dto;
import com.likelion.culture_test.domain.result.enums.ResultType;
import java.util.List;

public record AnalysisResponseDto(ResultType resultType, String status, List<TraitItemDto> result) {
}
