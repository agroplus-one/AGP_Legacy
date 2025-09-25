package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.MtoImpuestoSbpFilter;
import com.rsi.agp.core.jmesa.sort.MtoImpuestoSbpSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.sbp.ImpuestoSbp;
import com.rsi.agp.dao.tables.sbp.MtoImpuestoSbp;


public interface IMtoImpuestoSbpDao extends GenericDao{
	
	public Collection<MtoImpuestoSbp> getMtoImpuestoSbpWithFilterAndSort(MtoImpuestoSbpFilter filter, MtoImpuestoSbpSort sort, int rowStart, int rowEnd) throws BusinessException;
	
	public int getConsultaMtoImpuestoSbpCountWithFilter(final MtoImpuestoSbpFilter filter);
	
	public String replicar (BigDecimal origen, BigDecimal destino) throws DAOException;

	public List getImpuestoSbpWithFilter(MtoImpuestoSbp mtoImpuestoSbp);

	public List getBaseSbpWithFilter(MtoImpuestoSbp mtoImpuestoSbp);

	public int numRegistrosIguales(MtoImpuestoSbp mtoImpuestoSbp);
	
		

}
