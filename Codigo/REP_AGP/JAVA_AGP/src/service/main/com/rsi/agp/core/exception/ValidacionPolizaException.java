package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ASF 17/06/13
 * Exception para la validacion general de pólizas.
 */
public class ValidacionPolizaException  extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(ValidacionPolizaException.class);

	public ValidacionPolizaException() {
		super();
		logger.fatal("[ValidacionPolizaException]", this);
	}

	public ValidacionPolizaException(String message, Throwable cause) {
		super(message, cause);
		logger.fatal("[ValidacionPolizaException]  " + message + " --- " + cause, this);
	}

	public ValidacionPolizaException(String message) {
		super(message);
		logger.fatal("[ValidacionPolizaException]  " + message, this);
	}

	public ValidacionPolizaException(Throwable cause) {
		super(cause);
		logger.fatal("[ValidacionPolizaException]  " + cause, this);
	}
}
