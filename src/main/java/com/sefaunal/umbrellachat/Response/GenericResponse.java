package com.sefaunal.umbrellachat.Response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * @author github.com/sefaunal
 * @since 2023-12-19
 */
@Getter
@Setter
public class GenericResponse {
    private HttpStatus status;

    private String message;

    private LocalDateTime timestamp;

    public GenericResponse(String message) {
        this.status = HttpStatus.OK;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
