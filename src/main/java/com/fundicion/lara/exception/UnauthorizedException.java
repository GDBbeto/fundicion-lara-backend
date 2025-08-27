package com.fundicion.lara.exception;

import lombok.Getter;


@Getter
public class UnauthorizedException extends RuntimeException {

	private String userMessage;

	public UnauthorizedException(String message) {
		super("");
		this.userMessage = message;
	}

	public UnauthorizedException(String userMessage, String message) {
		super(message);
		this.userMessage = userMessage;
	}

}