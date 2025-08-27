package com.fundicion.lara.exception;

import lombok.Getter;


@Getter
public class BadRequestException extends RuntimeException {

	private String userMessage;

	public BadRequestException(String message) {
		super("");
		this.userMessage = message;
	}

	public BadRequestException(String userMessage, String message) {
		super(message);
		this.userMessage = userMessage;
	}

}
