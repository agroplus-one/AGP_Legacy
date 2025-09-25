package com.rsi.agp.dao.models.inc;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.IncidenciasAgroFilter;
import com.rsi.agp.core.jmesa.sort.IncidenciasSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.inc.EstadosInc;
import com.rsi.agp.dao.tables.inc.VistaIncidenciasAgro;

@SuppressWarnings("rawtypes")
public interface IIncidenciasAgroDao extends GenericDao {

	int getIncidenciasAgroCountWithFilter(final IncidenciasAgroFilter filter, Date fechaEnvioDesde,
			Date fechaEnvioHasta) throws DAOException;

	Collection<VistaIncidenciasAgro> getIncidenciasAgroWithFilterAndSort(final IncidenciasAgroFilter filter,
			final IncidenciasSort sort, final int rowStart, final int rowEnd, Date fechaEnvioDesde,
			Date fechaEnvioHasta) throws DAOException;

	String getlistaIdsTodos(final IncidenciasAgroFilter consultaFilter, final Date fechaEnvioDesde,
			final Date fechaEnvioHasta) throws DAOException;

	Collection<EstadosInc> getEstadosInc() throws DAOException;

	String getNombreEntidad(final BigDecimal codEntidad) throws DAOException;

	String getNombreOficina(final BigDecimal codOficina, final BigDecimal codEntidad) throws DAOException;

	String getNombreLinea(final BigDecimal codLinea) throws DAOException;

	BigDecimal getDCPoliza(String referencia, Character tipoRef, BigDecimal codPlan, BigDecimal codLinea);

	boolean getEstadoAsegurado(BigDecimal plan, BigDecimal linea, String refPoliza, String nifcif);

}
