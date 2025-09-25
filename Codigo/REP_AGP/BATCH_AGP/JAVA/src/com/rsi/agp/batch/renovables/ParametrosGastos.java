package com.rsi.agp.batch.renovables;

import java.util.List;

import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;

import es.agroseguro.serviciosweb.contratacionrenovaciones.ParametrosGastosPolizaRenovable;
import es.agroseguro.tipos.Gastos;

public class ParametrosGastos extends ParametrosGastosPolizaRenovable{
	
	public void setListaGastos(List<Gastos> lstGastos) {
		this.gastosGrupoNegocio = lstGastos;
	}
	
}
