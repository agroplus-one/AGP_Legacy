package com.rsi.agp.core.exception;

import javax.xml.ws.WebServiceException;

/**
 * Exception para el servicio de contratacion
 * 
 * @author T-Systems
 *
 */
public class ConfirmacionServiceException extends WebServiceException {

	  private static final long serialVersionUID = 1L;

	public ConfirmacionServiceException() {
		super();
	}

	public ConfirmacionServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ConfirmacionServiceException(String arg0) {
		super(arg0);
	}

	public ConfirmacionServiceException(Throwable arg0) {
		super(arg0);
	}

}
