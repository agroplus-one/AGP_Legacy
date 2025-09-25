package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.limit.Limit;
import org.w3._2005._05.xmlmime.Base64Binary;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.PolizasRenovablesFilter;
import com.rsi.agp.dao.tables.cgen.GruposNegocio;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableEstadoEnvioIBAN;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableValidacionEnvioIBAN;
import com.rsi.agp.dao.tables.renovables.VistaPolizaRenovable;

public interface IPolizasRenovablesService {
	
	String getTablaPolRenovables(HttpServletRequest request,
			HttpServletResponse response, VistaPolizaRenovable polizaRenovableBean,
			String origenLlamada,List<Long> listaGrupoEntidades, Usuario usuario,List <GruposNegocio> gruposNegocio);

	public int getPolRenovablesCountWithFilter(PolizasRenovablesFilter filter,String fecCargaIni,String fecCargaFin,String fecRenoIni,String fecRenoFin,
			String fecEnvioIBANIni,String fecEnvioIBANFin,String grupoNegocio, String estAgroplus) throws BusinessException;

	List<EstadoRenovacionAgroplus> getEstadosRenAgroplus(
			BigDecimal[] bigDecimals);
	List<EstadoRenovacionAgroseguro> getEstadosRenAgroseguro(
			BigDecimal[] bigDecimals);
	List<PolizaRenovableEstadoEnvioIBAN> getEstadosRenEnvioIBAN(
			BigDecimal[] bigDecimals);
			
	List<GruposNegocio> getGruposNegocio();
	
	public String getlistaIdsTodos(PolizasRenovablesFilter consultaFilter,String fecCargaIni,String fecCargaFin,String fecRenoIni,String fecRenoFin,
			String fecEnvioIBANIni,String fecEnvioIBANFin,String grupoNegocio, String estAgroplus);
			
	public Map<String, String> cambioMasivo(String listaIdsMarcados_cm, String comisionMasiva, String usuario, String isPerfil0) throws DAOException;
	
	public VistaPolizaRenovable getCambioMasivoBeanFromLimit(Limit consultaPlzRenovables_LIMIT);
	
	public String getNombreEntidad(Long codigo);
	
	public String getNombreTomador(Long codigoEntidad, String cifTomador);

	Map<String, String> validarEnvioIBAN(List<String> lstCadenasIds,boolean marcar, String codusuario, List<String> lstPlzRenov);
	
	public Map<String, String> modificarEstadoEnvioIBAN(List<String> lstCadenasIds, boolean marcar, String usuario, List<String> lstPlzRenov);

	public Map<String, List<PolizaRenovableValidacionEnvioIBAN>> getPolRenValidacionEnvioIBAN(String idErroresIBAN);

	String getAcuseReciboGastos(Long parseLong);

	Base64Binary imprimirProrroga(String planWs, String referenciaWs, String valorWs, String realPath) throws Exception;
	
	public boolean validarPolizasGastosMasivo(String ids);
	
	public List<String> getListaIdsRenovables (List<String> lstCadenasIds);

	public HashMap<String, Object> getAltaRenovableBeanFromLimit(Limit consultaPlzRenovables_LIMIT);
	
	public List<VistaPolizaRenovable> getAllFilteredAndSorted(HttpServletRequest request) throws BusinessException;
}
