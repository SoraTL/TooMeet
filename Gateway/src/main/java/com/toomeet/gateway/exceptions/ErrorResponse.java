package com.toomeet.gateway.exceptions;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ErrorResponse {
    private HttpStatus status;
    private Object message;
}
