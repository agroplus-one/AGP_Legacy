package com.rsi.agp.dao.models.mtoinf;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.ClasificacionRupturaCamposGenericosFilter;
import com.rsi.agp.core.jmesa.sort.ClasificacionRupturaCamposGenericosSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.mtoinf.VistaMtoinfClasificacionRuptura;

public interface IMtoClasificacionRupturaCamposGenericosDao extends GenericDao {

	
	public int getClasificacionRupturaCountWithFilter(final ClasificacionRupturaCamposGenericosFilter filter, final BigDecimal informeId) ;
	public List<VistaMtoinfClasificacionRuptura> getListaClasificacionRuptura() throws DAOException; 
	public Collection<VistaMtoinfClasificacionRuptura> getClasificacionRupturaWithFilterAndSort (final ClasificacionRupturaCamposGenericosFilter filter, final ClasificacionRupturaCamposGenericosSort sort, final BigDecimal informeId, int rowStart,
				final int rowEnd) throws DAOException;
	public boolean existeDatosClasificacionRuptura(VistaMtoinfClasificacionRuptura clasificacionRuptura) throws DAOException;

}
