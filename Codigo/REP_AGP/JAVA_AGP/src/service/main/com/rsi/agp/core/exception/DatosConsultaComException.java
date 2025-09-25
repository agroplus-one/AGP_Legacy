package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DatosConsultaComException extends RuntimeException {
	
	private static final Log logger = LogFactory.getLog(DatosConsultaComException.class);

	public DatosConsultaComException() {
		super();
		logger.error("DatosConsultaComException", this);
	}

	public DatosConsultaComException(String message, Throwable cause) {
		super(message, cause);
		logger.error("DatosConsultaComException. " + message, this);
	}

	public DatosConsultaComException(String message) {
		super(message);
		logger.error("DatosConsultaComException. " + message);
	}

	public DatosConsultaComException(Throwable cause) {
		super(cause);
		logger.error("DatosConsultaComException", cause);
	}
	
	
	
}
