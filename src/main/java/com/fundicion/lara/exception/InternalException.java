package com.fundicion.lara.exception;

import lombok.Getter;

import java.util.List;


@Getter
public class InternalException extends RuntimeException {
	private List<String> errors;

	public InternalException(String message) {
		super(message);
	}

	public InternalException(String message, List<String> errors) {
		super(message);
		this.errors = errors;
	}
}
