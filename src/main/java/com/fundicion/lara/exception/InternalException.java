package com.fundicion.lara.exception;

import lombok.Getter;


@Getter
public class InternalException extends RuntimeException {

	private String userMessage;

	public InternalException(String message) {
		super("");
		this.userMessage = message;
	}

	public InternalException(String userMessage, String message) {
		super(message);
		this.userMessage = userMessage;
	}

}
