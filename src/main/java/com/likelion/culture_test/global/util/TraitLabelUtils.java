package com.likelion.culture_test.global.util;

import com.likelion.culture_test.domain.survey.enums.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class TraitLabelUtils {

    private static final Map<Category, String[]> TRAIT_LABELS = Map.ofEntries(
            Map.entry(Category.WORK_CAPABILITY,
                    new String[]{"즉시 전력형", "성장 가능형"}),
            Map.entry(Category.CONFLICT_RESOLUTION,
                    new String[]{"직면형", "숙고형"}),
            Map.entry(Category.PERSONALITY_PREFERENCE,
                    new String[]{"혁신적 성향", "전통적 성향"}),
            Map.entry(Category.EVALUATION_CRITERIA,
                    new String[]{"객관적 자료형", "주관적 인상형"})
    );



    public static String getPositiveLabel(Category category) {
        return TRAIT_LABELS.getOrDefault(category, new String[]{"", ""})[0];
    }

    public static String getNegativeLabel(Category category) {
        return TRAIT_LABELS.getOrDefault(category, new String[]{"", ""})[1];
    }
}
