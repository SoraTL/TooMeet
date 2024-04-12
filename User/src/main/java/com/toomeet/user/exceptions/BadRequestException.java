package com.toomeet.user.exceptions;


import org.springframework.http.HttpStatus;

public class BadRequestException extends CustomException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
