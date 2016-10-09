package com.mvgreen.resolver;

public class IncorrectExpressionException extends Throwable {

    private String message;

    public IncorrectExpressionException(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
