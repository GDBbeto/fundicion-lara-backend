package com.fundicion.lara.exception;

import lombok.Getter;


@Getter
public class ConflictException extends RuntimeException{

	private String userMessage;

	public ConflictException(String message) {
		super("");
		this.userMessage = message;
	}

	public ConflictException(String userMessage, String message) {
		super(message);
		this.userMessage = userMessage;
	}

}
