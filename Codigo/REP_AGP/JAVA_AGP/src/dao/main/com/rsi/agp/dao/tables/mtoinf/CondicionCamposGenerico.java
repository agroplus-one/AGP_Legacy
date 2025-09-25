package com.rsi.agp.dao.tables.mtoinf;


public class CondicionCamposGenerico {
	
	private CondicionCamposPermitidos condicionCamposPermitidos;
	
	private CondicionCamposCalculados condicionCamposCalculados;

	public CondicionCamposPermitidos getCondicionCamposPermitidos() {
		return condicionCamposPermitidos;
	}

	public void setCondicionCamposPermitidos(
			CondicionCamposPermitidos condicionCamposPermitidos) {
		this.condicionCamposPermitidos = condicionCamposPermitidos;
	}

	public CondicionCamposCalculados getCondicionCamposCalculados() {
		return condicionCamposCalculados;
	}

	public void setCondicionCamposCalculados(
			CondicionCamposCalculados condicionCamposCalculados) {
		this.condicionCamposCalculados = condicionCamposCalculados;
	}

}
