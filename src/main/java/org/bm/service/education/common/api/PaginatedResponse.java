package org.bm.service.education.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginatedResponse<T> {

    private final Instant requestTimestamp;
    private final Instant responseTimestamp;
    private final String path;
    private final int status;
    private final boolean success;

    private final List<T> data;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public static <T> PaginatedResponse<T> of(
            List<T> data,
            int page,
            int size,
            long totalElements,
            String path) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return PaginatedResponse.<T>builder()
                .requestTimestamp(Instant.now())
                .responseTimestamp(Instant.now())
                .path(path)
                .status(200)
                .success(true)
                .data(data)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(page < totalPages - 1)
                .hasPrevious(page > 0)
                .build();
    }

    public static <T> PaginatedResponse<T> of(
            List<T> data,
            int page,
            int size,
            long totalElements,
            String path,
            Instant requestTimestamp) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return PaginatedResponse.<T>builder()
                .requestTimestamp(requestTimestamp)
                .responseTimestamp(Instant.now())
                .path(path)
                .status(200)
                .success(true)
                .data(data)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(page < totalPages - 1)
                .hasPrevious(page > 0)
                .build();
    }
}
