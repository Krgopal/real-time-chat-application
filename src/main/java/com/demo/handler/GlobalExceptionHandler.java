package com.demo.handler;

import com.demo.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.ws.rs.core.Response;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleException(CustomException ex) {
        return new ResponseEntity<>(ex.getExceptionInfo(), getHttpStatusUsingResponseStatus(ex.getStatus()));
    }

    public HttpStatus getHttpStatusUsingResponseStatus(Response.Status status) {
        switch (status) {
            case UNAUTHORIZED :
                return HttpStatus.UNAUTHORIZED;
            case NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case CONFLICT:
                return HttpStatus.CONFLICT;
            case BAD_REQUEST:
                return HttpStatus.BAD_REQUEST;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
