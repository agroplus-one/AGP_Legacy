package com.rsi.agp.core.exception;

public class XMLValidationException extends Exception{

	public XMLValidationException() {
	}
	public XMLValidationException(String message) {
		super(message);
	}
	public XMLValidationException(Throwable cause) {
		super(cause);
	}
	public XMLValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
