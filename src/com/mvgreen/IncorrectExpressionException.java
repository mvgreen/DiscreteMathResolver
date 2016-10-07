package com.mvgreen;

public class IncorrectExpressionException extends Throwable {

    String message;

    public IncorrectExpressionException(String message){
        this.message = message;
    }

}
