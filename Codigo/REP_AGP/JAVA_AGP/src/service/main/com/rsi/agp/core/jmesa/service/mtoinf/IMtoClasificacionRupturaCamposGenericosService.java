package com.rsi.agp.core.jmesa.service.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ClasificacionRupturaCamposGenericosFilter;
import com.rsi.agp.core.jmesa.sort.ClasificacionRupturaCamposGenericosSort;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfClasificacionRuptura;

public interface IMtoClasificacionRupturaCamposGenericosService {

	public int getConsultaClasificacionRupturaGenericoCountWithFilter(ClasificacionRupturaCamposGenericosFilter filter,BigDecimal informeId);
	public Collection<VistaMtoinfClasificacionRuptura> getClasificacionRupturaGenericoWithFilterAndSort(
	ClasificacionRupturaCamposGenericosFilter filter, ClasificacionRupturaCamposGenericosSort sort,BigDecimal informeId, int rowStart,
						int rowEnd) throws BusinessException;
	public VistaMtoinfClasificacionRuptura getClasificacionRuptura(Long idCamposCalculados)
				throws BusinessException;
	public Map<String, Object> bajaClasificacionRuptura(
						VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura) throws BusinessException ;
	public Map<String, Object> altaClasificacionRupturaGenerico (VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura) throws BusinessException;
	public Map<String, Object> modificarClasificacionRupturaGenerico(VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura) throws BusinessException;
	public String getTablaClasificacionRuptura (HttpServletRequest request, HttpServletResponse response, VistaMtoinfClasificacionRuptura vistaMtoinfClasificacionRuptura, String origenLlamada);
	
}
