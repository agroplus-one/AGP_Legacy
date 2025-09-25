package com.rsi.agp.core.util.exception;

public class RestWSException extends Exception {

	private static final long serialVersionUID = -5973567676359545495L;

	public RestWSException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestWSException(String message) {
		super(message);
	}
}
