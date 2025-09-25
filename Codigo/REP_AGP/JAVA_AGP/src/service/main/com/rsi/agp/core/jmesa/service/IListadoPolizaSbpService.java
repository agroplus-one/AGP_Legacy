package com.rsi.agp.core.jmesa.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.jmesa.facade.TableFacade;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.jmesa.filter.ListadoPolizaSbpFilter;
import com.rsi.agp.core.jmesa.sort.ListadoPolizaSbpSort;
import com.rsi.agp.dao.tables.sbp.PolizaSbp;

public interface IListadoPolizaSbpService {
	public String html(TableFacade tableFacade, String perfil);
	public Collection<PolizaSbp> getListadoPolizasSbpWithFilterAndSort(
			ListadoPolizaSbpFilter filter, ListadoPolizaSbpSort sort, int rowStart, int rowEnd , String filtrarDetalle) throws BusinessException;
	public int getListadoPolizaSbpCountWithFilter(ListadoPolizaSbpFilter filter, String filtrarDetalle);
	public void setDataAndLimitVariables(TableFacade tableFacade, String filtrarDetalle, List<BigDecimal> grupoEntidades,List<BigDecimal> grupoOficinas);
	
	public List<PolizaSbp> getAllFilteredAndSorted() throws BusinessException;
		
}
