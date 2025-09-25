package com.rsi.agp.dao.models.commons;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface ICheckStatusDao extends GenericDao {

	void checkStatusBBDD() throws DAOException;
}
