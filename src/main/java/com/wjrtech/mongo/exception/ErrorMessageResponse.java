package com.wjrtech.mongo.exception;

import lombok.Getter;

@Getter
public class ErrorMessageResponse {
    private String mensagemDeErro;

    public ErrorMessageResponse(String errorMessage){
        this.mensagemDeErro = errorMessage;
    }

}
