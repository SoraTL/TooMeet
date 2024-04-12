package com.toomet.chat.exceptions;


import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
        logException(exception);

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        Map<String, String> invalidMessage = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(
                        error -> invalidMessage.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(invalidMessage)
                .status(badRequest)
                .build();

        return new ResponseEntity<>(errorResponse, badRequest);
    }

    @ExceptionHandler({CustomException.class, ServletException.class, IOException.class})
    public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException exception) {
        logException(exception);

        HttpStatus status = exception.getStatus();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(status)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException exception) {
        logException(exception);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("ERROR::INTERNAL_SERVER_ERROR: " + exception.getMessage())
                .status(status)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> entityNotfoundExceptionHandler(EntityNotFoundException exception) {
        logException(exception);
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("ERROR:: NOT_FOUND_ENTITY_ERROR: " + exception.getMessage())
                .status(status)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler({SignatureException.class})
    public ResponseEntity<ErrorResponse> JwtException(SignatureException exception) {
        logException(exception);
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("TOKEN_ERROR: " + exception)
                .status(status)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    private void logException(Exception e) {
        System.out.println("\uD83D\uDCE2 \uD83D\uDCE2 \uD83D\uDCE2 LOI KIAAAAAAAAAAAAAAAAAAAAA: ");
        log.error(e.getMessage());
        log.error(e.toString());
        e.printStackTrace();
        System.out.println("\uD83D\uDC1E \uD83D\uDC1E \uD83D\uDC1E \uD83D\uDC1E \uD83D\uDC1E \uD83D\uDC1E");
    }


}
