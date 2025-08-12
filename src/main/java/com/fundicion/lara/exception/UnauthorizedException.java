package com.fundicion.lara.exception;

import lombok.Getter;

import java.util.List;


@Getter
public class UnauthorizedException extends RuntimeException {

	private List<String> errors;

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(String message, List<String> errors) {
		super(message);
		this.errors = errors;
	}
}