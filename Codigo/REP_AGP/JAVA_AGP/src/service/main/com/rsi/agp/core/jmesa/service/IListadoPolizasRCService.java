package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.gan.VistaPolizasRCFilter;
import com.rsi.agp.core.jmesa.sort.VistaPolizasRCSort;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.rc.ErroresRC;
import com.rsi.agp.dao.tables.rc.EstadosRC;
import com.rsi.agp.dao.tables.rc.VistaPolizasRC;

public interface IListadoPolizasRCService {

	String getTablaPolizasRC(final HttpServletRequest req,
			final HttpServletResponse res, final VistaPolizasRC vistaPolizasRC, final BigDecimal perfilUsuario) throws BusinessException;

	List<VistaPolizasRC> getVistaPolizasRCWithFilterAndSort(
			final VistaPolizasRCFilter filter, final VistaPolizasRCSort sort,
			int rowStart, int rowEnd) throws BusinessException;

	public int getVistaPolizasRCCountWithFilter(
			final VistaPolizasRCFilter filter) throws BusinessException;

	List<EstadosRC> getEstadosRC() throws BusinessException;

	List<EstadoPoliza> getEstadoPoliza() throws BusinessException;

	List<ErroresRC> getErroresRC() throws BusinessException;

	void borrarPoliza(final Long polizaId) throws BusinessException;

	void pasarDefinitiva(final BigDecimal polizaId, final String codUsuario)
			throws BusinessException;

	void anular(final BigDecimal polizaId, final String codUsuario)
			throws BusinessException;
	
	String cargarNombreEntidad(BigDecimal codEntidad) throws BusinessException;
	
	String cargarNombreOficina(BigDecimal codOficina, BigDecimal codEntidad) throws BusinessException;
}
