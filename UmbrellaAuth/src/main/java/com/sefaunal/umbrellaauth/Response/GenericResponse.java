package com.sefaunal.umbrellaauth.Response;

import lombok.Data;
import java.time.Instant;

/**
 * @author github.com/sefaunal
 * @since 2023-12-19
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
