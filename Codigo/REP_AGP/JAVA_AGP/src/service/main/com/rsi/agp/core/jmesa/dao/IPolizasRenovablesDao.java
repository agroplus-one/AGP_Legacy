package com.rsi.agp.core.jmesa.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.PolizasRenovablesFilter;
import com.rsi.agp.core.jmesa.sort.PolizasRenovablesSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.GastosRenovacion;
import com.rsi.agp.dao.tables.renovables.PolizaRenovable;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableValidacionEnvioIBAN;
import com.rsi.agp.dao.tables.renovables.VistaPolizaRenovable;

@SuppressWarnings("rawtypes")
public interface IPolizasRenovablesDao extends GenericDao {

	public Collection<VistaPolizaRenovable> getPolRenovablesWithFilterAndSort(final PolizasRenovablesFilter filter,
			final PolizasRenovablesSort sort, final int rowStart, final int rowEnd, final String fecCargaIni,
			final String fecCargaFin, final String fecRenoIni, final String fecRenoFin, final String fecEnvioIBANIni,
			final String fecEnvioIBANFin, final String grupoNegocio, final String estAgroplus) throws BusinessException;

	public int getPolRenovablesCountWithFilter(final PolizasRenovablesFilter filter, String fecCargaIni,
			String fecCargaFin, String fecRenoIni, String fecRenoFin, String fecEnvioIBANIni, String fecEnvioIBANFin,
			String grupoNegocio, String estAgroplus);

	public String getlistaIdsTodos(PolizasRenovablesFilter consultaFilter, String fecCargaIni, String fecCargaFin,
			String fecRenoIni, String fecRenoFin, String fecEnvioIBANIni, String fecEnvioIBANFin, String grupoNegocio,
			String estAgroplus);

	public boolean validacionesPreviasEnvioIBAN(List<String> lstCadenasIds, boolean marcar, String usuario, int total)
			throws Exception;

	public Object[] recogerParametrosPolizaRenovable(String idPolRen);

	public Long comprobarAsegurado(String nifCif, BigDecimal codEntidad, BigDecimal codEntMed, BigDecimal subEntMed);

	public Integer comprobarCuentasAsegurado(Long idAsegurado, BigDecimal codLinea);

	public BigDecimal recogerSecuenciaValidaEnvioIBAN();

	boolean modificarEstadoEnvioIBAN(List<String> lstCadenasIds, String estado, String usuario) throws Exception;

	public void updatePolRenEnvioIBANHisEstados(String id, String estado, String usuario);

	public List<PolizaRenovableValidacionEnvioIBAN> getPolRenValidacionEnvioIBAN(String idErroresIBAN);

	public String getAcuseReciboGastos(Long idPolRen);

	public int getCountPlzGastosMasivo(List<String> idsPoliza) throws DAOException;

	PolizaRenovable getPolizaById(Long idPoliza) throws DAOException;

	public Long getLineaSeguroId(Long plan, Long linea);

	EstadoRenovacionAgroplus getEstadorenovacionAgroplus(Long codigo) throws DAOException;

	void cambioMasivo(GastosRenovacion gr, PolizaRenovable polRen, EstadoRenovacionAgroplus estAgpGastosAsignados,
			EstadoRenovacionAgroplus estAgpPendienteAsigGastos, String usuario) throws Exception;

	public List<GruposNegocio> getGruposNegocio(final boolean listaGenerico);

	GastosRenovacion getGastosRenovacionById(Long id) throws DAOException;

	public List<String> getListaIdsRenovables(List<String> lstCadenasIds);

	public Collection<VistaPolizaRenovable> getPolizasRenovablesWithFilterAndSort(PolizasRenovablesFilter consultaFilter,
			PolizasRenovablesSort consultaSort, int i, int j) throws BusinessException;
}
