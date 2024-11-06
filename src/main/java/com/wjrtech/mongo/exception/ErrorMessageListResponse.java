package com.wjrtech.mongo.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorMessageListResponse {

    private List<String> message;
//    String errorMessage;
    public ErrorMessageListResponse(List<String> errors) {
        this.message = errors;
    }

//    public ErrorMessageListResponse(String error) {
//        this.errorMessage = error;
//    }
//
//    public Object getErrorMessages() {
//        if (!errorMessagesList.isEmpty()){
//            return this.errorMessagesList;
//        }
//        return this.errorMessage;
//    }
}
