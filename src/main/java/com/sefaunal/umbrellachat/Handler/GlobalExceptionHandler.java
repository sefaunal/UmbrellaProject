package com.sefaunal.umbrellachat.Handler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sefaunal.umbrellachat.Response.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author github.com/sefaunal
 * @since 2023-11-30
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // Get the validation errors from the exception
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        // Build the error response
        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorMessages.add(fieldError.getField() + " " + fieldError.getDefaultMessage());
        }
        GenericResponse GenericResponse = new GenericResponse(400, "Validation Error(s): " + errorMessages);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GenericResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GenericResponse> handleCredentialException(BadCredentialsException ex) {
        GenericResponse GenericResponse = new GenericResponse(403, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(GenericResponse);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<GenericResponse> handleTokenExpiredException(TokenExpiredException ex) {
        GenericResponse GenericResponse = new GenericResponse(403, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(GenericResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> handleGeneralException(Exception ex) {
        GenericResponse GenericResponse = new GenericResponse(500, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericResponse);
    }
}
