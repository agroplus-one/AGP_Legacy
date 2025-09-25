package com.rsi.agp.core.jmesa.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.dao.tables.comisiones.deudaAplazada.FicheroMult;

public interface IFicheroDeudaAplazadaService {

	

	String getTablaDeudaAplazada(HttpServletRequest request,
			HttpServletResponse response, FicheroMult ficheroMultBean,
			String origenLlamada);

}
