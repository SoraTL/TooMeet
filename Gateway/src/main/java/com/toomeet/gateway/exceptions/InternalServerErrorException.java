package com.toomeet.gateway.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends CustomException {
    public InternalServerErrorException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
