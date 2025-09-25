package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author etroitin
 *
 */
public class RegistrarColectivoException  extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(RegistrarColectivoException.class);

	public RegistrarColectivoException() {
		super();
		logger.fatal("[RegistrarColectivoException]", this);
	}

	public RegistrarColectivoException(String message, Throwable cause) {
		super(message, cause);
		logger.fatal("[RegistrarColectivoException]  " + message + " --- " + cause, this);
	}

	public RegistrarColectivoException(String message) {
		super(message);
		logger.fatal("[RegistrarColectivoException]  " + message, this);
	}

	public RegistrarColectivoException(Throwable cause) {
		super(cause);
		logger.fatal("[RegistrarColectivoException]  " + cause, this);
	}
}
