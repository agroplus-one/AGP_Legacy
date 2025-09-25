package com.rsi.agp.core.jmesa.dao;

import java.io.Serializable;
import java.util.Collection;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface IGenericoDao extends GenericDao {
	public Collection<Serializable> getWithFilterAndSort(
			final CriteriaCommand filter, final CriteriaCommand sort, final int rowStart,
			final int rowEnd) throws BusinessException;
	
	public int getCountWithFilter(final CriteriaCommand filter);

}
