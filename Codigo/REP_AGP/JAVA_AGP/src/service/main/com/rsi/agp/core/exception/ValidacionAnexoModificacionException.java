package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DAA 08/05/12
 * Exception para la validacion del Anexo de Modificacion
 */
public class ValidacionAnexoModificacionException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(ValidacionAnexoModificacionException.class);

	public ValidacionAnexoModificacionException() {
		super();
		logger.fatal("[ValidacionAnexoModificacionException]", this);
	}

	public ValidacionAnexoModificacionException(String message, Throwable cause) {
		super(message, cause);
		logger.fatal("[ValidacionAnexoModificacionException]  " + message + " --- " + cause, this);
	}

	public ValidacionAnexoModificacionException(String message) {
		super(message);
		logger.fatal("[ValidacionAnexoModificacionException]  " + message, this);
	}

	public ValidacionAnexoModificacionException(Throwable cause) {
		super(cause);
		logger.fatal("[ValidacionAnexoModificacionException]  " + cause, this);
	}

}