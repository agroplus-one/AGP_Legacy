package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AMG 27/03/14
 * Exception para la carga de la póliza actualizada a partir del cupon de un anexo
 */
public class CargaPolizaActualizadaDelCuponException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(CargaPolizaActualizadaDelCuponException.class);

	public CargaPolizaActualizadaDelCuponException() {
		super();
		logger.fatal("[ValidacionAnexoModificacionException]", this);
	}

	public CargaPolizaActualizadaDelCuponException(String message, Throwable cause) {
		super(message, cause);
		logger.fatal("[ValidacionAnexoModificacionException]  " + message + " --- " + cause, this);
	}

	public CargaPolizaActualizadaDelCuponException(String message) {
		super(message);
		logger.fatal("[ValidacionAnexoModificacionException]  " + message, this);
	}

	public CargaPolizaActualizadaDelCuponException(Throwable cause) {
		super(cause);
		logger.fatal("[ValidacionAnexoModificacionException]  " + cause, this);
	}

}