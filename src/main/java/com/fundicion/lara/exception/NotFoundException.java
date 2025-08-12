package com.fundicion.lara.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class NotFoundException extends RuntimeException{
    private List<String> errors;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}
