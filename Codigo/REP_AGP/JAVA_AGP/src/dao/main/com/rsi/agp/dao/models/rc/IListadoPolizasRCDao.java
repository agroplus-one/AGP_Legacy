package com.rsi.agp.dao.models.rc;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.gan.VistaPolizasRCFilter;
import com.rsi.agp.core.jmesa.sort.VistaPolizasRCSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.rc.ErroresRC;
import com.rsi.agp.dao.tables.rc.EstadosRC;
import com.rsi.agp.dao.tables.rc.PermisosPerfilRC;
import com.rsi.agp.dao.tables.rc.VistaPolizasRC;

public interface IListadoPolizasRCDao extends GenericDao {
	
	int getVistaPolizasRCCountWithFilter(final VistaPolizasRCFilter filter)
			throws DAOException;

	List<VistaPolizasRC> getVistaPolizasRCWithFilterAndSort(
			final VistaPolizasRCFilter filter, final VistaPolizasRCSort sort,
			final int rowStart, final int rowEnd) throws DAOException;

	String getlistaIdsTodos(final VistaPolizasRCFilter filter) throws DAOException;
	
	List<EstadosRC> getEstadosRC() throws DAOException;
	
	List<EstadoPoliza> getEstadoPoliza() throws DAOException;
	
	List<ErroresRC> getErroresRC() throws DAOException;
	
	String getNombreEntidad(BigDecimal codEntidad);
	
	String getNombreOficina(BigDecimal codOficina, BigDecimal codEntidad);
	
	PermisosPerfilRC getPermisosRC(BigDecimal perfilUsuario);
}
