package com.rsi.agp.core.jmesa.dao.impl;

import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.CriteriaCommand;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.IGenericoFilter;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroIncidenciasUnificado;

public interface IIncidenciasComisionesUnificadoDao extends IGenericoDao {

	public Collection<FicheroIncidenciasUnificado> getIncidenciasFicheroUnificado(
			final CriteriaCommand filter, final CriteriaCommand sort)
			throws BusinessException;

	public void borraIncidencias(final Long idFicheroUnificado)
			throws DAOException;

	public void revisarIncidencia(final Long idIncidencia, final char estado) throws DAOException;

	public String getListaIdsTodos(final IGenericoFilter consultaFilter)
			throws DAOException;
}
