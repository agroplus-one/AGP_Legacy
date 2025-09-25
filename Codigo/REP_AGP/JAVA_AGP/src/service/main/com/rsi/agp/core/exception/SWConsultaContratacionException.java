package com.rsi.agp.core.exception;

import javax.xml.ws.WebServiceException;

/**
 * Exception para el servicio de consulta del estado de la contratación
 * 
 * @author T-Systems
 *
 */
public class SWConsultaContratacionException extends WebServiceException {

	private static final long serialVersionUID = 1L;

	public SWConsultaContratacionException() {
		super();
	}

	public SWConsultaContratacionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SWConsultaContratacionException(String arg0) {
		super(arg0);
	}

	public SWConsultaContratacionException(Throwable arg0) {
		super(arg0);
	}

}
