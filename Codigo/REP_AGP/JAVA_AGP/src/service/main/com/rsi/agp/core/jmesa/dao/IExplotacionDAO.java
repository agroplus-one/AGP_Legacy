package com.rsi.agp.core.jmesa.dao;

import java.util.List;

import com.rsi.agp.core.exception.DAOException;

public interface IExplotacionDAO extends IGenericoDao { 
	
	public List<Long> obtenerExplotacionesConVariosGruposRaza(final Long idPoliza) throws DAOException;
}
