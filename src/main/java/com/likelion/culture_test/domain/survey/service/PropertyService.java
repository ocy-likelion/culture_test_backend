package com.likelion.culture_test.domain.survey.service;

import com.likelion.culture_test.domain.survey.entity.Property;
import com.likelion.culture_test.domain.survey.repository.PropertyRepository;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
