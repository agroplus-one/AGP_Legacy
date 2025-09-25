package com.rsi.agp.core.jmesa.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.filter.MtoZonasFilter;
import com.rsi.agp.core.jmesa.sort.MtoZonasSort;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Zona;


public interface IMtoZonasDao extends GenericDao{

	boolean existeZona(Zona zonaBean)throws DAOException;
	boolean esZonaConOficina(Zona zonaBean) throws DAOException;
	public Collection<Zona> getZonasWithFilterAndSort( final MtoZonasFilter filter, final MtoZonasSort sort, final int rowStart, final int rowEnd) throws BusinessException;
	public String getlistaIdsTodos(MtoZonasFilter consultaFilter);
	public int getZonasCountWithFilter(MtoZonasFilter filter);
	public String getNombEntidad(BigDecimal codEntidad);
	public void borrarZona(Zona zonaBean) throws DAOException;
	public void modificarZona(Zona zonaBean, BigDecimal codEntIni, BigDecimal codZonaIni) throws DAOException;
	

}
