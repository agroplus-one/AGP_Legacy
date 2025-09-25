package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AMG 27/03/14
 * Exception para la carga de la póliza a partir de la Copy
 */
public class CargaPolizaFromCopyOrPolizaException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(CargaPolizaFromCopyOrPolizaException.class);

	public CargaPolizaFromCopyOrPolizaException() {
		super();
		logger.fatal("[ValidacionAnexoModificacionException]", this);
	}

	public CargaPolizaFromCopyOrPolizaException(String message, Throwable cause) {
		super(message, cause);
		logger.fatal("[ValidacionAnexoModificacionException]  " + message + " --- " + cause, this);
	}

	public CargaPolizaFromCopyOrPolizaException(String message) {
		super(message);
		logger.fatal("[ValidacionAnexoModificacionException]  " + message, this);
	}

	public CargaPolizaFromCopyOrPolizaException(Throwable cause) {
		super(cause);
		logger.fatal("[ValidacionAnexoModificacionException]  " + cause, this);
	}

}