package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidadesHistorico;
import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;

@SuppressWarnings("rawtypes")
public interface IPolizasPctComisionesDao extends GenericDao {

	List getParamsGen(Long lineaseguroid, BigDecimal entMed, BigDecimal subentMed) throws Exception;

	List getParamsGen(Long lineaseguroid, BigDecimal entMed, BigDecimal subentMed, Date fechaRenovacion)
			throws Exception;

	Object[] getComisionesESMed(Long lineaseguroid, BigDecimal entMed, BigDecimal subentMed, BigDecimal codLinea,
			BigDecimal codPlan, Date fechaRenovacion) throws Exception;

	public Descuentos getDescuentos(BigDecimal entidad, String oficina, BigDecimal entMed, BigDecimal subentMed,
			BigDecimal delegacion, BigDecimal codPlan, BigDecimal codLinea) throws Exception;

	public Descuentos getDescuentos(BigDecimal entidad, List<BigDecimal> codOficinas, BigDecimal entMed,
			BigDecimal subentMed, BigDecimal delegacion, BigDecimal codPlan, BigDecimal codLinea) throws Exception;

	void updateDescuento(Long idpoliza, BigDecimal long1) throws Exception;

	void insertHistoricoDescuento(Poliza poliza, BigDecimal pctDescuento, Usuario usu) throws Exception;

	public void updateRecargo(Long idpoliza, BigDecimal recargo) throws Exception;

	public String getDescripcionGrupoNegocio(Character codGrupoNeg) throws DAOException;

	public void updatePctComs(PolizaPctComisiones pctComs) throws Exception;

	public void updateDescuento(BigDecimal descuento, Long idPolizaPctComision) throws Exception;

	public void updateRecargo(BigDecimal recargo, Long idPolizaPctComision) throws Exception;

	public void updateDescuentoAnexo(BigDecimal descuento, Long idAnexo, Character grupoNegocio) throws Exception;

	public void updateRecargoAnexo(BigDecimal recargo, Long idAnexo, Character grupoNegocio) throws Exception;

	public void updatePorcentajesAnexo(es.agroseguro.iTipos.Gastos gas, Long idAnexo) throws Exception;

	CultivosEntidadesHistorico getUltimoHistoricoComision(final int plan, final int linea, final int entMed,
			final int subEntMed, final Calendar fecha, final String gn) throws Exception;

	public void updatePctComsMaxCalculada(final Poliza poliza) throws DAOException;
}
