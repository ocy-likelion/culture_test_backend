package com.likelion.culture_test.domain.result.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultType {

    AbcD_s("실전분석형"),
    AbcD_m("균형조율형"),
    aBCd_s("도전혁신형"),
    aBCd_m("성장유연형"),
    not_yet("미확정");
// AbcD-s
//AbcD-m
//aBCd-s
//aBCd-m
    private final String description;
}
