package com.rsi.agp.core.jmesa.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitido;

public interface IFicheroEmitidosService {

	

	String getTablaEmitidos(HttpServletRequest request,
			HttpServletResponse response, ReciboEmitido emitido,
			String origenLlamada);

}
