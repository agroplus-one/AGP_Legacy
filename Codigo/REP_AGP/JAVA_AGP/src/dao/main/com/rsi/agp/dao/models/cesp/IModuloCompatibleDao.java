package com.rsi.agp.dao.models.cesp;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.models.GenericDao;


@SuppressWarnings("unchecked")
public interface IModuloCompatibleDao extends GenericDao {
	public List getPlanes() throws BusinessException;
	public List getLineas(BigDecimal codPlan) throws BusinessException;
	public List getModulosCompatibles() throws BusinessException;

}
