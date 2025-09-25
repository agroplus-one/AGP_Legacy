package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.sbp.ErrorSbp;
import com.rsi.agp.dao.tables.sbp.EstadoPlzSbp;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;
import com.rsi.agp.dao.tables.sbp.Sobreprecio;

public interface IConsultaSbpManager {

	/**
	 * Busca las pólizas de sobreprecio que se ajusten al filtro indicado
	 * @param polizaSbp Objeto que encapsula el filtro para la búsqueda de pólizas de sobreprecio
	 * @return Listado de pólizas de sobreprecio
	 */
	public List<PolizaSbp> consultarPolizasSbp (PolizaSbp polizaSbp);
	
	/**
	 * Limpia el filtro de búsqueda anterior y lista todas las pólizas de sobreprecio
	 * @param request Objeto request
	 * @return Listado de pólizas de sobreprecio
	 */
	public List<PolizaSbp> limpiar (HttpServletRequest request);
	
	public List<EstadoPoliza> getEstadosPoliza(BigDecimal estadosPolizaExcluir[]);
	public List<EstadoPlzSbp> getEstadosPolizaSbp(BigDecimal estadosPolizaExcluir[]);
	public List<ErrorSbp> getDetalleErroresSbp(BigDecimal detalleErroresExcluir[]);
	public List<EstadoPoliza> getEstadosPolizaPpal(List<EstadoPoliza> lstEstados);
	public List<Sobreprecio> getLineasSobrePrecio();
	public String borrarPolizaSbpByPoliza (Poliza poliza, Usuario usuario, String realPath);
	public Boolean getListaPolizasSbp (Poliza poliza, boolean complementaria);
	/**
	 * Devuelve el listado de pólizas asociados a los ids pasados como parámetro
	 * @param listIdPolizas Lista de ids de póliza separados por ';'
	 * @return Lista de pólizas
	 */
	public List<Poliza> getListObjPolFromString(String listIdPolizas) throws Exception;
	public Map<Long, List<BigDecimal>>  getCultivosPorLineaseguroid(BigDecimal maxPlan);
}
