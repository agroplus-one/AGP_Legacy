package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DocCondicionesRCException extends Exception {

	private static final long serialVersionUID = 3256240433393258082L;
	private static final Log LOGGER = LogFactory.getLog(DocCondicionesRCException.class);

	public DocCondicionesRCException() {
		super();
		LOGGER.fatal("[BusinessException]", this);
	}

	public DocCondicionesRCException(String message, Throwable cause) {
		super(message, cause);
		LOGGER.fatal("[BusinessException]", this);
	}

	public DocCondicionesRCException(String message) {
		super(message);
		LOGGER.fatal("[BusinessException]", this);
	}

	public DocCondicionesRCException(Throwable cause) {
		super(cause);
		LOGGER.fatal("[BusinessException]", this);
	}
	
	

}
