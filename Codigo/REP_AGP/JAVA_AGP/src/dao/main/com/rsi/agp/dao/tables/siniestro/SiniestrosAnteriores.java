package com.rsi.agp.dao.tables.siniestro;

public class SiniestrosAnteriores {


	private String codriesgo;
	private String desriesgo;
	private String fechaocurrencia;
	
	public SiniestrosAnteriores(String codriesgo, String desriesgo, String fechaocurrencia) {
		super();
		this.codriesgo = codriesgo;
		this.desriesgo = desriesgo;
		this.fechaocurrencia = fechaocurrencia;
	}

	public String getCodriesgo() {
		return codriesgo;
	}
	public void setCodriesgo(String codriesgo) {
		this.codriesgo = codriesgo;
	}
	public String getDesriesgo() {
		return desriesgo;
	}
	public void setDesriesgo(String desriesgo) {
		this.desriesgo = desriesgo;
	}
	public String getFechaocurrencia() {
		return fechaocurrencia;
	}
	public void setFechaocurrencia(String fechaocurrencia) {
		this.fechaocurrencia = fechaocurrencia;
	}

}
