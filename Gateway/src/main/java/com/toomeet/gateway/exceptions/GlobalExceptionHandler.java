package com.toomeet.gateway.exceptions;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    @ExceptionHandler({CustomException.class})
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
                .message("ERROR::::::INTERNAL_SERVER_ERROR")
                .status(status)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }


    @ExceptionHandler({JwtException.class})
    public ResponseEntity<ErrorResponse> jwtExceptionHandler(JwtException exception) {
        logException(exception);

        HttpStatus status = HttpStatus.FORBIDDEN;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("TOKEN_ERROR: " + exception.getMessage())
                .status(status)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> decodeExceptionHandler(WebClientResponseException exception) {
        logException(exception);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("MICROSERVICE_ERROR: " + exception.toString())
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
//        log.error(e.toString());
        log.error(e.getMessage());
//        e.printStackTrace();
    }

}
