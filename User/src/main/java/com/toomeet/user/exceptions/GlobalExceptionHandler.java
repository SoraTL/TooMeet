package com.toomeet.user.exceptions;


import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.Arrays;
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


    private void logException(Exception e) {
        log.error(Arrays.toString(e.getStackTrace()));
        log.error(e.getMessage());

    }


}
