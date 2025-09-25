package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.commons.Termino;

public interface ITerminoDao extends GenericDao {
	
	public Termino getTermino(BigDecimal codprovincia, BigDecimal codcomarca, BigDecimal codtermino, Character subtermino) throws DAOException;

}
