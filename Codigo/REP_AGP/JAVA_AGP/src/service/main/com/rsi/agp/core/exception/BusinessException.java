package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BusinessException extends Exception {

	private static final long serialVersionUID = 3298768276459760575L;

	private static final Log logger = LogFactory.getLog(BusinessException.class);

	public BusinessException() {
		super();
		logger.fatal("[BusinessException]", this);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
		logger.fatal("[BusinessException]  " + message + " --- " + cause, this);
	}

	public BusinessException(String message) {
		super(message);
		logger.fatal("[BusinessException]  " + message, this);
	}

	public BusinessException(Throwable cause) {
		super(cause);
		logger.fatal("[BusinessException]  " + cause, this);
	}

}
