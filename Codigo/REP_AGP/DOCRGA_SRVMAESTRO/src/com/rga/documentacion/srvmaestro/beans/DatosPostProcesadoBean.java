package com.rga.documentacion.srvmaestro.beans;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class DatosPostProcesadoBean {
	
	private List<DetallePostProcesadoBean> principal;

	private ArrayList<List<DetallePostProcesadoBean>> duplicados;
	
	public void addDuplicadoCopia (int numCopia, int numDuplicado, DetallePostProcesadoBean duplicado) {
		duplicados.get(numCopia).add(numDuplicado, duplicado);
	}
	
	public void addDuplicadoCopia (int numCopia, DetallePostProcesadoBean duplicado) {
		if ((duplicados.size()-1)<(numCopia)) {
			duplicados.add(new ArrayList<DetallePostProcesadoBean>());
		}
		duplicados.get(numCopia).add(duplicado);
	}
	
	public List<DetallePostProcesadoBean> getDuplicado (int numCopia) {
		return duplicados.get(numCopia);
	}
	
	public DetallePostProcesadoBean getDuplicadoCopia (int numCopia, int numDuplicado) {
		return duplicados.get(numCopia).get(numDuplicado);
	}

}
