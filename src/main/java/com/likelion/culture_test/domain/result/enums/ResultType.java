package com.likelion.culture_test.domain.result.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultType {

    ABCD("결과 중심의 혁신가형"),
    ABCd("직관적인 돌격대장형"),
    ABcD("원칙을 중시하는 개혁가형"),
    ABcd("신념을 따르는 행동가형"),
    AbCD("통찰력 있는 실용주의자형"),
    AbCd("유연한 성과주의자형"),
    AbcD("데이터 기반의 안정가형"),
    Abcd("현실적인 조력가형"),
    aBCD("논리적인 비전가형"),
    aBCd("사람을 이끄는 선도자형"),
    aBcD("소신있는 원칙주의자형"),
    aBcd("사명감을 가진 리더형"),
    abCD("가능성을 탐구하는 분석가형"),
    abCd("잠재력을 키우는 멘토형"),
    abcD("신중한 절차주의자형"),
    abcd("안정 지향의 관계 전문가형"),

//    AbcD_s("실전분석형"),
//    AbcD_m("균형조율형"),
//    aBCd_s("도전혁신형"),
//    aBCd_m("성장유연형"),
    not_yet("미확정");
// AbcD-s
//AbcD-m
//aBCd-s
//aBCd-m
    private final String description;
}
