package com.rsi.agp.core.jmesa.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.tables.comisiones.comisiones.ComisionAplicacion;

public interface IFicheroComisionApliService {

	String getTablaComisionesApli(HttpServletRequest request,
			HttpServletResponse response, ComisionAplicacion comisionApli,
			String origenLlamada);

	ComisionAplicacion getDatosComisiones(Long idComision) throws BusinessException;

	String getMarcaCondComisionesApli(Long idComisionApli) throws BusinessException;

	String getMarcaCondComisiones(Long idComision)throws BusinessException;

}
