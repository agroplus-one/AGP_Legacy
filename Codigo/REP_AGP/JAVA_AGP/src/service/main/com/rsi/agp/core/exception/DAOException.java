package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DAOException extends Exception {

	private static final long serialVersionUID = -2619231251051147196L;
	
	private static final Log logger = LogFactory.getLog(DAOException.class);
	
	public DAOException() {
		
		super();
		logger.fatal("[DAOException]", this);
	}

	public DAOException(String message, Throwable cause) {
		super(message, cause);
		logger.fatal("[DAOException]  " + message + " --- " + cause, this);
	}

	public DAOException(String message) {
		super(message);
		logger.fatal("[DAOException]  " + message, this);
	}

	public DAOException(Throwable cause) {
		super(cause);
		logger.fatal("[DAOException]  " + cause, this);
	}
}
