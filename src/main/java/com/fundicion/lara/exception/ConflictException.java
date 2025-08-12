package com.fundicion.lara.exception;

import lombok.Getter;

import java.util.List;


@Getter
public class ConflictException extends RuntimeException{
	private List<String> errors;

	public ConflictException(String message) {
		super(message);
	}

	public ConflictException(String message, List<String> errors) {
		super(message);
		this.errors = errors;
	}
}
