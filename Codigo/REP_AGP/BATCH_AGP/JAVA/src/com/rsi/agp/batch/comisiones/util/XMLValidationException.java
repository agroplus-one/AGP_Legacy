package com.rsi.agp.batch.comisiones.util;

public class XMLValidationException extends Exception{

	private static final long serialVersionUID = 1L;

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
