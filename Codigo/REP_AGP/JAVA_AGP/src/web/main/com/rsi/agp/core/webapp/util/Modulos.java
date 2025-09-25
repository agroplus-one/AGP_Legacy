package com.rsi.agp.core.webapp.util;

import java.util.ArrayList;
import java.util.HashMap;

public class Modulos 
{
	private String moduloId;
	private String moduloDesc;
	private ArrayList<Fila> fila;
	private HashMap<String, FilaSelecModulos> filasMezcladas;
	

	public String getModuloId() {
		return moduloId;
	}

	public void setModuloId(String moduloId) {
		this.moduloId = moduloId;
	}

	public String getModuloDesc() {
		return moduloDesc;
	}

	public void setModuloDesc(String moduloDesc) {
		this.moduloDesc = moduloDesc;
	}

	public ArrayList<Fila> getFila() {
		return fila;
	}

	public void setFila(ArrayList<Fila> fila) {
		this.fila = fila;
	}

	public HashMap<String, FilaSelecModulos> getFilasMezcladas() {
		return filasMezcladas;
	}

	public void setFilasMezcladas(HashMap<String, FilaSelecModulos> filasMezcladas) {
		this.filasMezcladas = filasMezcladas;
	}
}
