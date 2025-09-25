package com.rsi.agp.core.jmesa.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.impl.IIncidenciasComisionesUnificadoDao;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroIncidenciasUnificado;
import com.rsi.agp.dao.tables.comisiones.unificado.FicheroUnificado;
import com.rsi.agp.dao.tables.commons.Usuario;

public interface IIncidenciasComisionesUnificadoService extends IGetTablaService {

	public List<FicheroIncidenciasUnificado> gestListaIncidencias(
			FicheroIncidenciasUnificado ficheroIncidenciasUnificadoBean, IIncidenciasComisionesUnificadoDao dao)
			throws BusinessException;

	public boolean cargarFichero(FicheroUnificado fichero, Date fechaAceptacionFichero,
			IIncidenciasComisionesUnificadoDao dao) throws BusinessException;

	public void verificarTodos(FicheroUnificado fichero, IIncidenciasComisionesUnificadoDao dao)
			throws BusinessException;

	public void revisarIncidencia(final Long idIncidencia, final char estado,
			final IIncidenciasComisionesUnificadoDao dao) throws BusinessException;

	public void recargarFichero(FicheroUnificado fichero, Usuario usuario, HttpServletRequest request,
			final IIncidenciasComisionesUnificadoDao dao) throws BusinessException;;
}