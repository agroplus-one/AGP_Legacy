package com.rsi.agp.dao.models.commons;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.BaseDaoHibernate;

public class CheckStatusDao extends BaseDaoHibernate implements ICheckStatusDao {

	@Override
	public void checkStatusBBDD() throws DAOException{
		this.obtenerSession().createSQLQuery("select 1 from dual").uniqueResult();
	}
}
