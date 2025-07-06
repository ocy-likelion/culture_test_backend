package com.likelion.culture_test.domain.result.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResultDetailResponseDto {

    private Long questionId;
    private String questionContent;
    private Long choiceId;
    private String choiceContent;
    private String propertyName;
    private Double score;
}
