package com.likelion.culture_test.domain.result.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResultHistoryDto {
    private Long id;

    private String ResultType;
    private LocalDate localDate;
    private String imageUrl;




}
