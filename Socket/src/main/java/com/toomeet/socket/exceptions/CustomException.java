package com.toomeet.socket.exceptions;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CustomException extends RuntimeException {
    protected HttpStatus status;

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    protected CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
