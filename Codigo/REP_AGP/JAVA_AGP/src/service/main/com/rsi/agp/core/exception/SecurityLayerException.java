package com.rsi.agp.core.exception;

public class SecurityLayerException extends Exception{

	public SecurityLayerException(){
		super();
	}
	public SecurityLayerException (String msg){
		super(msg);
	}
	public SecurityLayerException(String msg,Throwable cause){
		super(msg,cause);
	}
	public SecurityLayerException(Throwable cause){
		super(cause);
	}
}
