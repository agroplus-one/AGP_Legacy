package com.rsi.agp.core.exception;

import javax.xml.ws.WebServiceException;

/**
 * Exception para el servicio de validación del siniestro
 * 
 * @author T-Systems
 *
 */
public class SWConfirmacionSiniestroException extends WebServiceException {

	private static final long serialVersionUID = 1L;

	public SWConfirmacionSiniestroException() {
		super();
	}

	public SWConfirmacionSiniestroException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SWConfirmacionSiniestroException(String arg0) {
		super(arg0);
	}

	public SWConfirmacionSiniestroException(Throwable arg0) {
		super(arg0);
	}

}
