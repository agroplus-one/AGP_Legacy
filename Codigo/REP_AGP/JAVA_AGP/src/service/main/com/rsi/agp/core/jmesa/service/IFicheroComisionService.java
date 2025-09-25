package com.rsi.agp.core.jmesa.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.dao.tables.comisiones.comisiones.Comision;

public interface IFicheroComisionService {

	

	String getTablaComisiones(HttpServletRequest request,
			HttpServletResponse response, Comision comisionBean,
			String origenLlamada);

}
