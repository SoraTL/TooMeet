package com.toomeet.gateway.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends CustomException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
