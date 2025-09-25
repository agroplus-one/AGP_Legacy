package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jmesa.limit.Limit;
import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CPMTipoCapitalFilter;
import com.rsi.agp.core.jmesa.sort.CPMTipoCapitalSort;
import com.rsi.agp.dao.tables.cpm.CPMTipoCapital;



public interface ICPMTipoCapitalService{
	
	public Collection<CPMTipoCapital> getCPMTipoCapitalWithFilterAndSort(CPMTipoCapitalFilter filter, CPMTipoCapitalSort sort, int rowStart, int rowEnd) throws BusinessException;
	public int getConsultaCPMTipoCapitalCountWithFilter(CPMTipoCapitalFilter filter);
	public String getTablaCPMTipoCapital (HttpServletRequest request, HttpServletResponse response, CPMTipoCapital cpmTipoCapital, String origenLlamada);
	public Map<String, Object> altaCPMTipoCapital(CPMTipoCapital cpmTipoCapital) throws BusinessException;
	public void bajaCPMTipoCapital(Long id)throws BusinessException;
	public Map<String, Object> editaCPMTipoCapital(CPMTipoCapital cpmTipoCapital) throws BusinessException;
	public CPMTipoCapital getCPMTipoCapital(Long idCPMTipoCapital)throws BusinessException;
	public Map<String, Object> replicar (BigDecimal planOrig, BigDecimal lineaOrig, BigDecimal planDest, BigDecimal lineaDest) throws BusinessException;
}
