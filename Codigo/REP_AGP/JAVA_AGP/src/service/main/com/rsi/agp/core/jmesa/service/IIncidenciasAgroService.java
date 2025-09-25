package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.IncidenciasAgroFilter;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;


public interface IIncidenciasAgroService {

	public String getTablaIncidenciasAgro(final HttpServletRequest request,
			final HttpServletResponse response, final VistaIncidenciasAgro incidencias,
			final String origenLlamada) throws BusinessException;
	
	public Collection<EstadosInc> getEstadosInc() throws BusinessException;
	
	public int getIncidenciasAgroCountWithFilter(final IncidenciasAgroFilter filter, Date fechaEnvioDesde, Date fechaEnvioHasta)
			throws BusinessException;
	
	public void borrarIncidencia(final Long incidenciaId) throws BusinessException;

	public String cargarNombreEntidad(BigDecimal codEntidad) throws BusinessException;
	public String cargarNombreOficina(BigDecimal codOficina, BigDecimal codEntidad)	throws BusinessException;
	public String cargarNombreLinea(BigDecimal codLinea) throws BusinessException;

	public List<VistaIncidenciasAgro> getAllFilteredAndSorted() throws DAOException;
}
