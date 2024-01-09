package com.sefaunal.apigateway.Response;

import lombok.Data;

import java.time.Instant;

/**
 * @author github.com/sefaunal
 * @since 2024-01-09
 */
@Data
public class GenericResponse {
    private int status;

    private String message;

    private Instant timestamp;

    public GenericResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now();
    }
}
