package com.rsi.agp.dao.models.comisiones;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.comisiones.CultivosEntidades;
import com.rsi.agp.dao.tables.comisiones.CultivosSubentidades;
import com.rsi.agp.dao.tables.comisiones.DetalleComisionEsMediadora;
import com.rsi.agp.dao.tables.poliza.Linea;

@SuppressWarnings("rawtypes")
public interface ICultivosEntidadesDao extends GenericDao {

	public List<CultivosEntidades> getLastCultivosEntidades(boolean evitar2015) throws DAOException;

	public List<CultivosEntidades> getLastCultivosEntidades(BigDecimal year) throws DAOException;

	public List<CultivosEntidades> listComisionesCultivosEntidades(CultivosEntidades cultivosEntidadesBean)
			throws DAOException;

	public Linea getLineaseguroId(BigDecimal codlinea, BigDecimal codplan) throws DAOException;

	public Long comisionesAsociadas(Linea linea) throws DAOException;

	public Integer existeRegistro(CultivosEntidades cultivosEntidadesBean) throws DAOException;

	public Map<String, Object> cambioMasivo(String listaIds, CultivosEntidades cultivosEntidadesBean);

	public CultivosEntidades getCultivoEntidadByPlanLinea(BigDecimal codplan, BigDecimal codlinea) throws DAOException;
	
	public List<CultivosEntidades> getCultivosEntidadByPlanLinea(BigDecimal codplan, BigDecimal codlinea) throws DAOException;

	public boolean existeComisionMaxima(String idplan, String idlinea) throws DAOException;

	public List<DetalleComisionEsMediadora> getListDetallePct(String codPlan, BigDecimal ent, BigDecimal subEnt,
			BigDecimal lineaseguoId, BigDecimal codLin, BigDecimal codLineatemp) throws DAOException;

	public List<GruposNegocio> getGruposNegocio() throws DAOException;

	public List<CultivosSubentidades> getCultivosSubentidades(BigDecimal codPlan, BigDecimal entMed,
			BigDecimal subEntMed, BigDecimal codLin) throws DAOException;

	public List<Linea> getListLineasParamsGenByPlan(String codPlan);

	public void replicarCultivos(BigDecimal plan_origen, BigDecimal linea_origen, BigDecimal plan_destino,
			BigDecimal linea_destino) throws DAOException;
}
