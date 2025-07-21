package com.likelion.culture_test.global.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.likelion.culture_test.global.util.Empty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
public class RsData<T> {

  public static final RsData<Empty> OK = new RsData<>("200-1", "OK", new Empty());

  @NonNull
  private final String resultCode;
  @NonNull
  private final String msg;

  private final T data;

  public RsData(String resultCode, String msg) {
    this(resultCode, msg, null);
  }

  @JsonIgnore
  public int getStatusCode() {
    return Integer.parseInt(resultCode.split("-")[0]);
  }

  @JsonIgnore
  public boolean isSuccess() {
    return getStatusCode() < 400;
  }

  @JsonIgnore
  public boolean isFail() {
    return !isSuccess();
  }

  public static <T> RsData<T> of(String resultCode, String msg) {
    return new RsData<>(resultCode, msg, null);
  }

  public static <T> RsData<T> of(String resultCode, String msg, T data) {
    return new RsData<>(resultCode, msg, data);
  }
}
