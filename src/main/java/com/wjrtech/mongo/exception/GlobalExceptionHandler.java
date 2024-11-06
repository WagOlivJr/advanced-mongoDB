package com.wjrtech.mongo.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> errors.add(error.getDefaultMessage()));
        ErrorMessageListResponse errorMessageListResponse = new ErrorMessageListResponse(errors);
        return new ResponseEntity<>(errorMessageListResponse, HttpStatus.BAD_REQUEST);
    }


    @Override
//    @ExceptionHandler(HttpMessageNotReadableException.class) // Atenção: pelo fato desse metodo estar sobrescrevendo o metodo da classe ResponseEntityExceptionHandler,
//     a anotação que indica que é um metodo de tratamento de exceção não deve existir aqui
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
        ex.getStackTrace();
        ErrorMessageResponse errorMessage = new ErrorMessageResponse("corpo da requisição mal formatado.");
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ErrorMessageResponse> handleGenericException(GenericException ex) {
        ErrorMessageResponse errorMessage = new ErrorMessageResponse(ex.getMessage());
        return new ResponseEntity<>(errorMessage, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleException(Exception ex) {
        ex.printStackTrace();
        ErrorMessageResponse errorMessage = new ErrorMessageResponse("Erro Interno.");
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
