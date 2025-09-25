package com.rsi.agp.core.exception;

import javax.xml.ws.WebServiceException;

/**
 * Exception para el servicio de validacion
 * 
 * @author T-Systems
 *
 */
public class ValidacionServiceException extends WebServiceException {

	  private static final long serialVersionUID = 1L;

	public ValidacionServiceException() {
		super();
	}

	public ValidacionServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ValidacionServiceException(String arg0) {
		super(arg0);
	}

	public ValidacionServiceException(Throwable arg0) {
		super(arg0);
	}

}
