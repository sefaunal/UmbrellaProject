package com.sefaunal.umbrellachat.Response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * @author github.com/sefaunal
 * @since 2023-11-30
 */

@Getter
@Setter
public class ErrorResponse {
    private HttpStatus status;

    private String message;

    private LocalDateTime timestamp;

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
