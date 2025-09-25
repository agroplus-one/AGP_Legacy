package com.rsi.agp.dao.tables.cpl;

public class ModuloFilaVinculacionView {
	private boolean elegida;
	private int vincFila;
	private boolean vincElegida;
	private int grupoVinculacion;
	
	public boolean isElegida() {
		return elegida;
	}
	public void setElegida(boolean elegida) {
		this.elegida = elegida;
	}
	public int getVincFila() {
		return vincFila;
	}
	public void setVincFila(int vincFila) {
		this.vincFila = vincFila;
	}
	public boolean isVincElegida() {
		return vincElegida;
	}
	public void setVincElegida(boolean vincElegida) {
		this.vincElegida = vincElegida;
	}
	public int getGrupoVinculacion() {
		return grupoVinculacion;
	}
	public void setGrupoVinculacion(int grupoVinculacion) {
		this.grupoVinculacion = grupoVinculacion;
	}
	
}
