package com.rsi.agp.core.exception;

import javax.xml.ws.WebServiceException;

/**
 * Exception para el servicio de calculo
 * 
 * @author T-Systems
 *
 */
public class CaractExplotacionServiceException extends WebServiceException {

	  private static final long serialVersionUID = 1L;

	public CaractExplotacionServiceException() {
		super();
	}

	public CaractExplotacionServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CaractExplotacionServiceException(String arg0) {
		super(arg0);
	}

	public CaractExplotacionServiceException(Throwable arg0) {
		super(arg0);
	}

}
