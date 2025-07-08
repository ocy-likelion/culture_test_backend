package com.likelion.culture_test.domain.survey.service;

import com.likelion.culture_test.domain.survey.dto.response.PropertyResponse;
import com.likelion.culture_test.domain.survey.entity.Property;
import com.likelion.culture_test.domain.survey.enums.Category;
import com.likelion.culture_test.domain.survey.repository.PropertyRepository;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyService {

  private final PropertyRepository propertyRepository;


  public Property findById(Long propertyId) {
    return propertyRepository.findById(propertyId)
        .orElseThrow(() -> new CustomException(ErrorCode.PROPERTY_NOT_FOUND));
  }


  public List<PropertyResponse> findAll() {
    return propertyRepository.findAll().stream().map(PropertyResponse::fromEntity).toList();
  }


  @Transactional
  public PropertyResponse create(String categoryName, String propertyName) {
    Category category = getCategory(categoryName);
    Property property = Property.builder()
        .category(category)
        .name(propertyName)
        .build();

    Property savedProperty = propertyRepository.save(property);
    return PropertyResponse.fromEntity(savedProperty);
  }


  @Transactional
  public void deleteById(Long propertyId) {
    Property property = findById(propertyId);
    propertyRepository.delete(property);
  }


  @Transactional
  public PropertyResponse update(Long propertyId, String categoryName, String propertyName) {
    Category category = getCategory(categoryName);
    Property property = findById(propertyId);
    property.setCategory(category);
    property.setName(propertyName);

    return PropertyResponse.fromEntity(property);
  }


  public Category getCategory(String categoryName) {
    if (categoryName == null || categoryName.trim().isEmpty()) {
      throw new CustomException(ErrorCode.INVALID_CATEGORY_NAME);
    }

    return Arrays.stream(Category.values())
        .filter(category ->
          category.getDescription().equals(categoryName.trim()) || category.name().equals(categoryName.toUpperCase())
        )
        .findFirst()
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CATEGORY_NAME));
  }


  public List<String> getCategoryNames() {
    return Arrays.stream(Category.values())
        .map(Category::getDescription).toList();
  }
}
