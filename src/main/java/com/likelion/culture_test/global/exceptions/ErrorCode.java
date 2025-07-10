package com.likelion.culture_test.global.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // Survey
  BACKUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 오류가 발생했습니다."),
  SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 설문지를 찾을 수 없습니다."),
  NO_NEW_QUESTIONS(HttpStatus.BAD_REQUEST, "이미 모두 추가된 질문입니다."),
  INVALID_QUESTIONS_CHANGE(HttpStatus.BAD_REQUEST, "해당 설문지에 없는 질문에 대한 요청이 포함되었습니다."),
  INVALID_QUESTIONS_COUNT(HttpStatus.BAD_REQUEST, "요청에 대한 질문의 수가 잘못되었습니다."),

  // Question
  QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 질문을 찾을 수 없습니다."),
  SELECTIVE_QUESTION_NEEDS_CHOICES(HttpStatus.BAD_REQUEST, "선택형 질문의 경우 최소 보기가 2개 이상이여야 합니다."),
  SELECTIVE_CHOICE_NEEDS_PROPERTY(HttpStatus.BAD_REQUEST, "선택형 질문의 경우 각 보기가 특성을 가져야 합니다."),
  LIKERT_QUESTION_NEEDS_PROPERTY(HttpStatus.BAD_REQUEST, "리커트형 질문의 경우 질문에 특성이 존재해야 합니다."),

  // Property
  PROPERTY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 특성을 찾을 수 없습니다."),
  INVALID_CATEGORY_NAME(HttpStatus.NOT_FOUND, "입력하신 카테고리명은 잘못된 카테고리명입니다."),


  // 공통 에러
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
  INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "타입이 올바르지 않습니다."),
  ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "엔티티를 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

  QUESTION_CHOICE_MISMATCH(HttpStatus.BAD_REQUEST, "선택지와 질문이 일치하지 않습니다."),

  INVALID_DISPLAY_ORDER(HttpStatus.BAD_REQUEST, "잘못된 설문 문항 번호 값입니다."),

  RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "설문 결과를 찾을 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
