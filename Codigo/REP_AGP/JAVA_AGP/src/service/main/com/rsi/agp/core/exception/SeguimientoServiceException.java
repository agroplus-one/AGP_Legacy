package com.rsi.agp.core.exception;

import javax.xml.ws.WebServiceException;

public class SeguimientoServiceException extends WebServiceException {

   private static final long serialVersionUID = 1L;

	public SeguimientoServiceException() {
		super();
	}

	public SeguimientoServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SeguimientoServiceException(String arg0) {
		super(arg0);
	}

	public SeguimientoServiceException(Throwable arg0) {
		super(arg0);
	}

}
