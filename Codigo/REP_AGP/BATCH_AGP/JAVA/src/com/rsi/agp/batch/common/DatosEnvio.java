package com.rsi.agp.batch.common;

public class DatosEnvio {
	
	private String tipoEnvio;
	private String idEnvio;
	private String directorioCorreduria;
	
	public DatosEnvio(){
		
	}
	
	public DatosEnvio(String tipoEnvio, String idEnvio){
		this.tipoEnvio = tipoEnvio;
		this.idEnvio = idEnvio;
	}

	public DatosEnvio(String tipoEnvio, String idEnvio, String directorioCorreduria) {
		this.tipoEnvio = tipoEnvio;
		this.idEnvio = idEnvio;
		this.directorioCorreduria = directorioCorreduria;
	}

	public String getTipoEnvio() {
		return tipoEnvio;
	}

	public void setTipoEnvio(String tipoEnvio) {
		this.tipoEnvio = tipoEnvio;
	}

	public String getIdEnvio() {
		return idEnvio;
	}

	public void setIdEnvio(String idEnvio) {
		this.idEnvio = idEnvio;
	}

	public String getDirectorioCorreduria() {
		return directorioCorreduria;
	}

	public void setDirectorioCorreduria(String directorioCorreduria) {
		this.directorioCorreduria = directorioCorreduria;
	}
	
	

}
