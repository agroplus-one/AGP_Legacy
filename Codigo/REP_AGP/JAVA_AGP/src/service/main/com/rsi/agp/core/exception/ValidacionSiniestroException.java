package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TMR 04/06/13
 * Exception para la validacion de siniestros
 */
public class ValidacionSiniestroException  extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(ValidacionSiniestroException.class);

	public ValidacionSiniestroException() {
		super();
		logger.fatal("[ValidacionSiniestroException]", this);
	}

	public ValidacionSiniestroException(String message, Throwable cause) {
		super(message, cause);
		logger.fatal("[ValidacionSiniestroException]  " + message + " --- " + cause, this);
	}

	public ValidacionSiniestroException(String message) {
		super(message);
		logger.fatal("[ValidacionSiniestroException]  " + message, this);
	}

	public ValidacionSiniestroException(Throwable cause) {
		super(cause);
		logger.fatal("[ValidacionSiniestroException]  " + cause, this);
	}
}
