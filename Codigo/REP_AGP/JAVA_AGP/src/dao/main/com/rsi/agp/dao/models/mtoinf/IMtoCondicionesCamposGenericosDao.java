package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.CondicionCamposFilter;
import com.rsi.agp.core.jmesa.service.mtoinf.Estados;
import com.rsi.agp.core.jmesa.sort.CondicionCamposSort;
import com.rsi.agp.dao.filters.poliza.EstadoPolizaFilter;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfCondiciones;

@SuppressWarnings("rawtypes")
public interface IMtoCondicionesCamposGenericosDao extends GenericDao {

	
	public Collection<VistaMtoinfCondiciones> getCamposGenericosWithFilterSort (
			final CondicionCamposFilter filter,final CondicionCamposSort sort,final BigDecimal informeId , final int rowStart,
			final int rowEnd) throws DAOException ;  	
	
	
	public int getCamposGenericosCountWithFilter(final CondicionCamposFilter filter,final BigDecimal informeId) throws DAOException;

	
	public List<Estados> getEstados (Class clase);
	

	public boolean existeCondicion(final VistaMtoinfCondiciones vistaMtoinfCondiciones)
			 throws  DAOException;
	
	public List getEstadosPol(EstadoPolizaFilter estadoPolizaFilter); 
}
