package com.rsi.agp.core.jmesa.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.comisiones.recibosEmitidos.ReciboEmitidoAplicacion;

public interface IFicheroEmitidosApliService {

	

	String getTablaEmitidosApli(HttpServletRequest request,
			HttpServletResponse response, ReciboEmitidoAplicacion emitidoApli,
			String origenLlamada);

	ReciboEmitidoAplicacion getDatosEmitidos(Long idEmitidos)
			throws BusinessException;

}
