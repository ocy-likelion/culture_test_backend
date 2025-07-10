package com.likelion.culture_test.domain.result.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@AllArgsConstructor
@Setter
public class VectorRequestDto {

    private Long userId;
    private Long surveyId;
    private List<Double> vector;
}
