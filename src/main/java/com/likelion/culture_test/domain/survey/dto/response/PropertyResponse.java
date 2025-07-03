package com.likelion.culture_test.domain.survey.dto.response;

import com.likelion.culture_test.domain.survey.entity.Property;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "질문/선택지에 연결된 특성 정보")
@Builder
public record PropertyResponse(

    @Schema(description = "특성 ID", example = "1")
    Long propertyId,

    @Schema(description = "특성 카테고리명", example = "커뮤니케이션")
    String categoryName,

    @Schema(description = "특성 이름", example = "개인주의 vs 공동체")
    String propertyName

) {
  public static PropertyResponse fromEntity(Property property) {
    if (property == null) return null;

    return PropertyResponse.builder()
        .propertyId(property.getId())
        .categoryName(property.getCategory().getDescription())
        .propertyName(property.getName())
        .build();
  }
}
