package com.rsi.agp.core.jmesa.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.comisiones.deudaAplazada.DeudaAplazadaAplicacion;

public interface IFicheroDeudaAplazadaApliService {

	

	String getTablaDeudaAplazadaApli(HttpServletRequest request,
			HttpServletResponse response, DeudaAplazadaAplicacion deudaAplazadaApli,
			String origenLlamada);

	DeudaAplazadaAplicacion getDatosDeudaAplazada(Long idDeudaAplazada)
			throws BusinessException;

}
