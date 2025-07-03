package com.likelion.culture_test.domain.survey.dto.response;

import com.likelion.culture_test.domain.survey.entity.Property;
import lombok.Builder;

@Builder
public record PropertyResponse(
    Long propertyId,
    String categoryName,
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
