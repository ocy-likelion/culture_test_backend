package com.likelion.culture_test.domain.result.dto;

import java.util.List;

public record ResultRequestWithoutUserDto(Long surveyId,
                                          List<AnswerDto> answers) {
}
