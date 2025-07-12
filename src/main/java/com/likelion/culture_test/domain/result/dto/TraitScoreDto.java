package com.likelion.culture_test.domain.result.dto;

public record TraitScoreDto(String positiveLabel,
                            String negativeLabel,
                            double positivePercent,
                            double negativePercent,
                            double rawScore) {
}
