package com.likelion.culture_test.global.util;

import java.util.Set;

public class ScoreUtils {

    private static final int[] SCORES = {5, 3, 0, -3, -5};

    // 질문 ID가 역방향인지 여부 (직접 관리 or DB에서 관리도 가능)
    private static final Set<Long> REVERSED_QUESTIONS = Set.of(
            3L, 4L, 7L, 8L  // 예시로 반대 질문 ID들
    );

    public static int calculateScore(int displayOrder, Long questionId) {
        int index = displayOrder - 1; // displayOrder는 1~5, 인덱스는 0~4
        if (index < 0 || index >= SCORES.length) {
            throw new IllegalArgumentException("잘못된 displayOrder: " + displayOrder);
        }
        int score = SCORES[index];
        return REVERSED_QUESTIONS.contains(questionId) ? -score : score;
    }
}
