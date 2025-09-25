package com.rsi.agp.core.jmesa.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.dao.tables.poliza.Poliza;

public interface IPolizaActualizadaService {
	public String getTablaPolizas(HttpServletRequest request, HttpServletResponse response, Poliza poliza, String origenLlamada);
}
