package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.managers.impl.SeguimientoPolizaBean;
import com.rsi.agp.dao.models.GenericDao;

import es.agroseguro.seguimientoContratacion.Incidencia;

@SuppressWarnings("rawtypes")
public interface ISeguimientoPolizaDao extends GenericDao {
	
	public void actualizarPoliza(Long idPoliza, BigDecimal costeTomador, BigDecimal idEstadoAgro,
			Date fechaCambioEstado, String codUsuario, Integer plan) throws DAOException;
	
	public void actualizaRenovable(Long idPoliza, Long idRenov, BigDecimal idEstadoAgro, BigDecimal costeTomador,
			Integer plan, String codUsuario) throws DAOException;

	public void actualizarAnexos(List<Incidencia> lista, String codUsuario, SeguimientoPolizaBean seguimientoPolizaBean)
			throws DAOException;

	public void actualizarIncidencias(List<Incidencia> lista, String codUsuario,
			SeguimientoPolizaBean seguimientoPolizaBean) throws DAOException;

	public Date getFechaParamDesde();

	public void auditarLlamadaSW(final BigDecimal plan, final String referencia, final Date fechaDesde,
			final Date fechaHasta, final String usuario, final String xml) throws DAOException;

	public void createTmpBatchSeguimiento(final String nifAsegurado, final String nifTomador, final BigDecimal plan,
			final BigDecimal linea, final String referencia, final Character tipoRef, final BigDecimal entidad,
			final String oficina, final BigDecimal tipo, final String detalle, final String estado,
			final String colectivo) throws DAOException;
}
