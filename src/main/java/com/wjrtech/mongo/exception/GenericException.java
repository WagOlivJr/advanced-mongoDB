package com.wjrtech.mongo.exception;

import org.springframework.http.HttpStatus;

public class GenericException extends RuntimeException {

    HttpStatus status;
    public GenericException(String message, HttpStatus status) { // Tem que extender alguma Exception para ser aceito na classe GlobalExceptionHandler
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
