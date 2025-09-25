package com.rsi.agp.core.jmesa.dao.impl;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.core.jmesa.dao.IGenericoDao;
import com.rsi.agp.core.jmesa.filter.gan.ClaseDetalleGanadoFilter;

public interface IClaseDetalleGanadoDao extends IGenericoDao {
	
	public String getlistaIdsTodos(ClaseDetalleGanadoFilter consultaFilter);

	public Boolean existeClaseDetalleGanado(ClaseDetalleGanadoFilter consultaFilter, Long id);

	@SuppressWarnings("rawtypes")
	public List getListaClase(Class clase, String[] parametros, Object[] valores, String orden) throws DAOException;
}
