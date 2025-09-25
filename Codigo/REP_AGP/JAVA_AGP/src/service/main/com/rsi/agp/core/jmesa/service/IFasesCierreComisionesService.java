package com.rsi.agp.core.jmesa.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.dao.tables.comisiones.unificado.FasesCerradas;



public interface IFasesCierreComisionesService {

	String getTablaFasesCierre(HttpServletRequest request,
			HttpServletResponse response, FasesCerradas fase, String origenLlamada);

}
