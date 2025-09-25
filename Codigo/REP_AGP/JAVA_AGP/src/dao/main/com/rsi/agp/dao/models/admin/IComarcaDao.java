package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface IComarcaDao extends GenericDao {

	public boolean checkComarcaExists(BigDecimal codcomarca, BigDecimal codprovincia) throws DAOException;
}
