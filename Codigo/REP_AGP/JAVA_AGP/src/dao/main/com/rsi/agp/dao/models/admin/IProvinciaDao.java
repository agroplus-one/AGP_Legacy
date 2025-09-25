package com.rsi.agp.dao.models.admin;

import java.math.BigDecimal;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

public interface IProvinciaDao extends GenericDao {
	
	public boolean checkProvinciaExists(BigDecimal codprovincia) throws DAOException;

}
