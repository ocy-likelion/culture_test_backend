package com.likelion.culture_test.global.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // Survey
  BACKUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 오류가 발생했습니다."),


  // 공통 에러
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
  INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "타입이 올바르지 않습니다."),
  ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "엔티티를 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");


  private final HttpStatus httpStatus;
  private final String message;
}
