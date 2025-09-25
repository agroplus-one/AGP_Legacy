package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.dao.IMtoZonasDao;
import com.rsi.agp.core.jmesa.filter.MtoZonasFilter;
import com.rsi.agp.core.jmesa.sort.MtoZonasSort;
import com.rsi.agp.dao.tables.commons.Zona;
/**
 * @author U028975 (T-Systems)
 * GDLD-63701 - Mantenimiento de Zonas
 */

public interface IMtoZonasService {
	
	public Map<String, Object> altaZona(Zona zonaBean)throws BusinessException;
	public Map<String, Object> editaZona(Zona zonaBean,HttpServletRequest request)throws BusinessException;
	public Map<String, Object> borraZona(Zona zonaBean)throws BusinessException;
	public String getTablaZonas(HttpServletRequest request, HttpServletResponse response, Zona zonaBusqueda, String origenLlamada);
	public int getZonasCountWithFilter(MtoZonasFilter filter)throws BusinessException;
	public Collection<Zona> getZonasWithFilterAndSort(MtoZonasFilter filter, MtoZonasSort sort, int rowStart, int rowEnd) throws BusinessException;
	public List<Zona> getAllFilteredAndSorted() throws BusinessException;
	public String getNombEntidad(BigDecimal codEntidad);
	void setMtoZonasDao(IMtoZonasDao mtoZonasDao);
	
	
}
