package com.likelion.culture_test.domain.result.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScoreByPropertyDto {

    private String propertyName;
    private Double totalScore;
}
