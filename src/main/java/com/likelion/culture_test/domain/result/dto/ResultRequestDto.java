package com.likelion.culture_test.domain.result.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResultRequestDto(
        Long userId,
        Long surveyId,
        List<AnswerDto> answers
) {}
