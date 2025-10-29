package com.api.pos_backend.shared.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("content")
    private List<T> content;

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("size")
    private Integer size;

    @JsonProperty("total_elements")
    private Long totalElements;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("has_next")
    private Boolean hasNext;

    @JsonProperty("has_previous")
    private Boolean hasPrevious;

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    public static <T> PageResponse<T> fromPage(Page<T> page, String message, int code) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .message(message)
                .code(code)
                .build();
    }

    public static <T> PageResponse<T> single(T content, String message, int code) {
        return PageResponse.<T>builder()
                .content(List.of(content))
                .message(message)
                .code(code)
                .build();
    }

    public static <T> PageResponse<T> of(List<T> content, String message, int code) {
        return PageResponse.<T>builder()
                .content(content)
                .message(message)
                .code(code)
                .build();
    }

    public static <T> PageResponse<T> empty(Page<T> pag, String message, int code) {
        return PageResponse.<T>builder()
                .content(List.of())
                .message(message)
                .code(code)
                .page(pag.getNumber())
                .size(pag.getSize())
                .totalElements(pag.getTotalElements())
                .totalPages(pag.getTotalPages())
                .hasNext(pag.hasNext())
                .hasPrevious(pag.hasPrevious())
                .build();
    }


}
