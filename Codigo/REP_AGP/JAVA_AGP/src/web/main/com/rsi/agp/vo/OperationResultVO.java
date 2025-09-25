package com.rsi.agp.vo;

import java.util.ArrayList;

public class OperationResultVO {

	private String operacionRealizada = "";
	private ArrayList<String> messageOk = new ArrayList<String>();
	private ArrayList<String> messageErrors = new ArrayList<String>();
	private String codNuevaParcela = "";

	public String getOperacionRealizada() {
		return operacionRealizada;
	}

	public void setOperacionRealizada(String operacionRealizada) {
		this.operacionRealizada = operacionRealizada;
	}

	public ArrayList<String> getMessageOk() {
		return messageOk;
	}

	public void setMessageOk(ArrayList<String> messageOk) {
		this.messageOk = messageOk;
	}

	public ArrayList<String> getMessageErrors() {
		return messageErrors;
	}

	public void setMessageErrors(ArrayList<String> messageErrors) {
		this.messageErrors = messageErrors;
	}

	public String getCodNuevaParcela() {
		return codNuevaParcela;
	}

	public void setCodNuevaParcela(String codNuevaParcela) {
		this.codNuevaParcela = codNuevaParcela;
	}
}
