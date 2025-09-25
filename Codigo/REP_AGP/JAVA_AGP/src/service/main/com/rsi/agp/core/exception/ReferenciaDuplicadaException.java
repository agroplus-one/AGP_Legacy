package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReferenciaDuplicadaException extends RuntimeException {
	
	private static final Log logger = LogFactory.getLog(ReferenciaDuplicadaException.class);

	public ReferenciaDuplicadaException() {
		super();
		logger.error("ReferenciaDuplicadaException", this);
	}

	public ReferenciaDuplicadaException(String message, Throwable cause) {
		super(message, cause);
		logger.error("ReferenciaDuplicadaException." + message, cause);
	}

	public ReferenciaDuplicadaException(String message) {
		super(message);
		logger.error("ReferenciaDuplicadaException." + message);
	}

	public ReferenciaDuplicadaException(Throwable cause) {
		super(cause);
		logger.error("ReferenciaDuplicadaException", cause);
	}
	
}
