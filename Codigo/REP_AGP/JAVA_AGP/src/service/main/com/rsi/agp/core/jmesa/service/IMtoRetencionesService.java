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
import com.rsi.agp.core.jmesa.filter.MtoRetencionesFilter;
import com.rsi.agp.core.jmesa.sort.MtoRetencionesSort;

import com.rsi.agp.dao.tables.comisiones.DescuentosHistorico;
import com.rsi.agp.dao.tables.comisiones.Retencion;
import com.rsi.agp.dao.tables.commons.Usuario;

public interface IMtoRetencionesService {
	
	String getTablaRetenciones(HttpServletRequest request,
			HttpServletResponse response, Retencion retencionBean,
			String origenLlamada,List<BigDecimal> listaGrupoEntidades);
	Collection<Retencion> getRetencionesWithFilterAndSort(
			MtoRetencionesFilter filter, MtoRetencionesSort sort, int rowStart,
			int rowEnd) throws BusinessException;
	int getRetencionesCountWithFilter(MtoRetencionesFilter filter)
			throws BusinessException;
	Map<String, Object> validaAltaModificacion(Retencion retencionBean)throws Exception;
	
	void guardaRegistro(Retencion retencionBean)throws Exception;
	void borraRegistro(Retencion retencionBean) throws Exception;
}
