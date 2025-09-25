package com.rsi.agp.core.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DistribucionCostesException extends RuntimeException {
	
	private static final Log logger = LogFactory.getLog(DistribucionCostesException.class);

	public DistribucionCostesException() {
		super();
		logger.error("DistribucionCostesException", this);
	}

	public DistribucionCostesException(String message, Throwable cause) {
		super(message, cause);
		logger.error("DistribucionCostesException. " + message, this);
	}

	public DistribucionCostesException(String message) {
		super(message);
		logger.error("DistribucionCostesException. " + message);
	}

	public DistribucionCostesException(Throwable cause) {
		super(cause);
		logger.error("DistribucionCostesException", cause);
	}
	
	
	
}
