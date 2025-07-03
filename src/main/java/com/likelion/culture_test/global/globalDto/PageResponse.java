package com.likelion.culture_test.global.globalDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "커스텀 페이징 응답")
@Builder
public record PageResponse<T>(

    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean empty,
    int numberOfElements,
    List<T> content,
    boolean first,
    boolean last,
    long offset

) {
  public static <T> PageResponse<T> of(Page<T> page) {
    return PageResponse.<T>builder()
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .empty(page.isEmpty())
        .numberOfElements(page.getNumberOfElements())
        .content(page.getContent())
        .first(page.isFirst())
        .last(page.isLast())
        .offset(page.getPageable().getOffset())
        .build();
  }
}
