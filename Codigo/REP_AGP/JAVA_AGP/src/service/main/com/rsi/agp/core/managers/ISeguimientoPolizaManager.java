package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.exception.SeguimientoServiceException;
import com.rsi.agp.core.managers.impl.SeguimientoPolizaBean;
import com.rsi.agp.dao.tables.poliza.Poliza;

public interface ISeguimientoPolizaManager {

	public List<SeguimientoPolizaBean> getPolizasSeguimiento(String codUsuario,
			String realPath) throws DAOException;

	public SeguimientoPolizaBean getInfoPoliza(final Poliza poliza, final String codUsuario, final String realPath)
			throws DAOException;

	public void actualizarPoliza(Long idPoliza, SeguimientoPolizaBean seguimientoPolizaBean,
			String codUsuario) throws DAOException;
	
	public void actualizarRenovable(SeguimientoPolizaBean seguimientoPolizaBean, Long idRenov, String codUsuario)
			throws DAOException;

	public Poliza getPoliza(Long idPoliza) throws DAOException;
	
	public void createTmpBatchSeguimiento(final String nifAsegurado,
			final String nifTomador, final BigDecimal plan,
			final BigDecimal linea, final String referencia,
			final Character tipoRef, final BigDecimal entidad,
			final String oficina, final BigDecimal tipo, final String detalle,
			final String estado, final String colectivo) throws DAOException;
	
	public String getDistribucionCostes(final Long idPoliza, final String realPath) throws DAOException, SeguimientoServiceException;
}