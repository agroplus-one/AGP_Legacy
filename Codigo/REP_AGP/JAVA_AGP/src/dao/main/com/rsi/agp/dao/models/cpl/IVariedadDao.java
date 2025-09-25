package com.rsi.agp.dao.models.cpl;

import java.math.BigDecimal;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.Variedad;

public interface IVariedadDao extends GenericDao {
	
	public Variedad getVariedad(Long lineaseguroid, BigDecimal codcultivo, BigDecimal codvariedad) throws DAOException;

}
