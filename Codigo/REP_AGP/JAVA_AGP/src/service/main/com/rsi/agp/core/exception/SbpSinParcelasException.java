package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SbpSinParcelasException extends RuntimeException {
	
	private static final Log logger = LogFactory.getLog(SbpSinParcelasException.class);

	public SbpSinParcelasException() {
		super();
		logger.error("SbpSinParcelasException", this);
	}

	public SbpSinParcelasException(String message, Throwable cause) {
		super(message, cause);
		logger.error("SbpSinParcelasException. " + message, this);
	}

	public SbpSinParcelasException(String message) {
		super(message);
		logger.error("SbpSinParcelasException. " + message);
	}

	public SbpSinParcelasException(Throwable cause) {
		super(cause);
		logger.error("SbpSinParcelasException", cause);
	}
}
