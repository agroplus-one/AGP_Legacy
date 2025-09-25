package com.rsi.agp.core.jmesa.dao.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.renovables.ColectivosRenovacion;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;

@SuppressWarnings("rawtypes")
public interface IImportacionPolRenovableDao  extends GenericDao {
	
	boolean existePolRenovable(Long codPlan, Long codLinea, String refPolizaRen) throws Exception;
	public void grabaAuditoriaSWPolRenovable(Long codPlan, Long codLinea, String referencia, String codUsuario, String xml) throws DAOException;
	public ColectivosRenovacion obtenerColectivoPlanAnt(final String refColectivo, BigDecimal codPlan, BigDecimal codLinea ) throws DAOException;
	public ColectivosRenovacion obtenerColectivoRen(final String refColectivo) throws DAOException;
	public boolean guardarColectivoRen (final ColectivosRenovacion colHbmRen) throws DAOException;
	public Map<String, BigDecimal[]> getParamsComis(Session sessionAux, BigDecimal codLinea, Long codPlan, Long entMed,
			Long subEntMed, Date fechaRenov, boolean batch) throws Exception;
	public EstadoRenovacionAgroplus getEstadoPolRenAgroplus(final Session sessionAux, final boolean batch) throws Exception;
	public EstadoRenovacionAgroseguro getEstadoPolRenAgroseguro(final Long estAgroseguro, final Session sessionAux, final boolean batch) throws Exception ;
	public Long  getLineaseguroIdfromPlanLinea(final Session sessionAux,final Long codPlan, final Long codLinea, final boolean batch);
	public void guardaXml(final PolizaRenovable polizaHbm, final String xmlText, final Session sessionAux, final boolean batch);
	public void actualizarHistorico(final PolizaRenovable polizaHbm, EstadoRenovacionAgroseguro estadoAgroSeguro, EstadoRenovacionAgroplus estadoAgroplus, 
			   List<Character> listGN, final Session sessionAux, final boolean batch, final String usuario);
	public boolean guardarPolizaRen (final PolizaRenovable polHbmRen, final Session sessionAux, final boolean batch) throws DAOException;	
	
	/* Cambio de Alcance Nº2 ** P0063482 ** MODIF TAM (08.06.2021) ** Inicio */
	public ColectivosRenovacion obtenerColectivoPolPpal(final String refPolizaRen, BigDecimal codPlan, BigDecimal codLinea ) throws DAOException;
	public ColectivosRenovacion obtenerColectivoRenovPlanAnt(String refPoliza, BigDecimal codPlan, BigDecimal codLinea) throws DAOException;
}
