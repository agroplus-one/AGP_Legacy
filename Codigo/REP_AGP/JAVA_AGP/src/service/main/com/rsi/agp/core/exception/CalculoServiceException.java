package com.rsi.agp.core.exception;

import javax.xml.ws.WebServiceException;

/**
 * Exception para el servicio de calculo
 * 
 * @author T-Systems
 *
 */
public class CalculoServiceException extends WebServiceException {

	  private static final long serialVersionUID = 1L;

	public CalculoServiceException() {
		super();
	}

	public CalculoServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CalculoServiceException(String arg0) {
		super(arg0);
	}

	public CalculoServiceException(Throwable arg0) {
		super(arg0);
	}

}
