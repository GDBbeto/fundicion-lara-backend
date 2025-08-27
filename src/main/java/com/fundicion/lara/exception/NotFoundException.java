package com.fundicion.lara.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{

    private String userMessage;

    public NotFoundException(String message) {
        super("");
        this.userMessage = message;
    }

    public NotFoundException(String userMessage, String message) {
        super(message);
        this.userMessage = userMessage;
    }

}
