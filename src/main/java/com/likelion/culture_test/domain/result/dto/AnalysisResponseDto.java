package com.likelion.culture_test.domain.result.dto;
import com.likelion.culture_test.domain.result.enums.ResultType;
import java.util.List;

public record AnalysisResponseDto(String resultType, String status, List<TraitItemDto> result, String resultTypeDetail) {
}//이거의 리절트타입을 군집화결과에 대한 string? text값으로 변경
