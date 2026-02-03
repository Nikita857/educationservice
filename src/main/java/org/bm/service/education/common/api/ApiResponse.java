package org.bm.service.education.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final Instant requestTimestamp;
    private final Instant responseTimestamp;
    private final String path;
    private final int status;
    private final boolean success;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> ok(T data, String path) {
        return ApiResponse.<T>builder()
                .requestTimestamp(Instant.now())
                .responseTimestamp(Instant.now())
                .path(path)
                .status(200)
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data, String path, Instant requestTimestamp) {
        return ApiResponse.<T>builder()
                .requestTimestamp(requestTimestamp)
                .responseTimestamp(Instant.now())
                .path(path)
                .status(200)
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String path) {
        return ApiResponse.<T>builder()
                .requestTimestamp(Instant.now())
                .responseTimestamp(Instant.now())
                .path(path)
                .status(201)
                .success(true)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> error(int status, String message, String path) {
        return ApiResponse.<Void>builder()
                .requestTimestamp(Instant.now())
                .responseTimestamp(Instant.now())
                .path(path)
                .status(status)
                .success(false)
                .message(message)
                .build();
    }

    public static ApiResponse<Void> error(int status, String message, String path, Instant requestTimestamp) {
        return ApiResponse.<Void>builder()
                .requestTimestamp(requestTimestamp)
                .responseTimestamp(Instant.now())
                .path(path)
                .status(status)
                .success(false)
                .message(message)
                .build();
    }
}
