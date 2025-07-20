package com.likelion.culture_test.domain.cluster.classfier;

import com.likelion.culture_test.domain.cluster.entity.Centroid;
import com.likelion.culture_test.domain.result.enums.ResultType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResultTypeResolver {

    public static ResultType resolveResultType(List<Centroid> centroids) {
        if (centroids == null || centroids.size() != 4) {
            return ResultType.not_yet;
        }

        List<Centroid> mutableCentroids = new ArrayList<>(centroids);

        // Centroid ID 기준 정렬
        mutableCentroids.sort(Comparator.comparing(c -> c.getCategory().ordinal()));

        // Category 순서 보장돼 있다고 가정 (업무능력, 갈등대응, 성향및인성, 평가기준)
        StringBuilder code = new StringBuilder();

        for (int idx = 0; idx < centroids.size(); idx++) {
            double value = centroids.get(idx).getValue();

            if (value == 0.0) {
                return ResultType.not_yet;
            }

            // 순서에 따라 알파벳 결정

            char traitLetter = switch (idx) {
                case 0 -> 'A';
                case 1 -> 'B';
                case 2 -> 'C';
                case 3 -> 'D';
                default -> throw new IllegalStateException("Unexpected index");
            };

            if (value > 0) {
                code.append(traitLetter); // 대문자
            } else {
                code.append(Character.toLowerCase(traitLetter)); // 소문자
            }
        }

        try {
            return ResultType.valueOf(code.toString());
        } catch (IllegalArgumentException e) {
            return ResultType.not_yet; // 혹시 정의 안 된 코드면 fallback
        }
    }
}
