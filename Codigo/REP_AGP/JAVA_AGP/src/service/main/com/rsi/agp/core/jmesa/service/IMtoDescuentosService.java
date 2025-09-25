package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmesa.limit.Limit;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.MtoDescuentosFilter;
import com.rsi.agp.core.jmesa.sort.MtoDescuentosSort;
import com.rsi.agp.dao.tables.comisiones.Descuentos;
import com.rsi.agp.dao.tables.comisiones.DescuentosHistorico;
import com.rsi.agp.dao.tables.commons.Usuario;

public interface IMtoDescuentosService {
	
	String getTablaDescuentos(HttpServletRequest request,
			HttpServletResponse response, Descuentos descuentosBean,
			String origenLlamada,List<BigDecimal> listaGrupoEntidades);
	Collection<Descuentos> getDescuentosWithFilterAndSort(
			MtoDescuentosFilter filter, MtoDescuentosSort sort, int rowStart,
			int rowEnd) throws BusinessException;
	int getDescuentosCountWithFilter(MtoDescuentosFilter filter)
			throws BusinessException;
	Map<String, Object> validaAltaModificacion(Descuentos descuentosBean)throws Exception;
	
	void guardaRegistro(Descuentos descuentosBean)throws Exception;
	void borraRegistro(Descuentos descuentosBean) throws Exception;
	void guardaHistorico(Descuentos db,BigDecimal operacion,String usuario) throws Exception;
	ArrayList<DescuentosHistorico> consultaHistorico(Long id)throws Exception;
	public Map<String, Object> replicar (BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest, BigDecimal lineaDest,String CodUsuario, BigDecimal entidadReplica) throws BusinessException;
	public String getlistaIdsTodos(MtoDescuentosFilter mtoDescuentosFilter);
	public Map<String, String> cambioMasivo(String listaIdsMarcados_cm, Descuentos descuentosBean,  Usuario usuario) throws DAOException;
	public Descuentos getCambioMasivoBeanFromLimit(Limit consultaDescuentos_LIMIT);
}
