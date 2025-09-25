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
import com.rsi.agp.core.jmesa.filter.MtoImpuestoSbpFilter;
import com.rsi.agp.core.jmesa.sort.MtoImpuestoSbpSort;
import com.rsi.agp.dao.tables.sbp.MtoImpuestoSbp;


public interface IMtoImpuestoSbpService{
	
	public Collection<MtoImpuestoSbp> getMtoImpuestoSbpWithFilterAndSort(MtoImpuestoSbpFilter filter, MtoImpuestoSbpSort sort, int rowStart, int rowEnd) throws BusinessException;
	public int getConsultaMtoImpuestoSbpCountWithFilter(MtoImpuestoSbpFilter filter);
	public String getTablaMtoImpuestoSbp (HttpServletRequest request, HttpServletResponse response, MtoImpuestoSbp mtoMtoImpuestoSbp, String origenLlamada);
	public Map<String, Object> altaMtoImpuestoSbp(MtoImpuestoSbp mtoMtoImpuestoSbp) throws BusinessException;
	public void bajaMtoImpuestoSbp(Long id)throws BusinessException;
	public Map<String, Object> editaMtoImpuestoSbp(MtoImpuestoSbp mtoMtoImpuestoSbp) throws BusinessException;
	public MtoImpuestoSbp getMtoImpuestoSbp(Long idMtoImpuestoSbp)throws BusinessException;
	public Map<String, Object> replicar (BigDecimal planOrig, BigDecimal planDest) throws BusinessException;
}
