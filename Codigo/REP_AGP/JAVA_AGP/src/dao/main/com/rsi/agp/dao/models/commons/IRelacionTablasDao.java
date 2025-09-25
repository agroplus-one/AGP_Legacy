package com.rsi.agp.dao.models.commons;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

public interface IRelacionTablasDao extends GenericDao{
	
	List<BigDecimal> getLstCodtablacondicionado(String string) throws DAOException;	
	
}
