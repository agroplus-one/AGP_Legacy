package com.rsi.agp.dao.models.cesp;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.filters.commons.PlanFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;

@SuppressWarnings("unchecked")
public class ModuloCompatibleDao extends BaseDaoHibernate implements IModuloCompatibleDao{

	@Override
	public List getLineas(BigDecimal codPlan) throws BusinessException {

		return null;
	}

	@Override
	public List getModulosCompatibles() throws BusinessException {

		return null;
	}

	@Override
	public List getPlanes(){
		return this.getObjects(new PlanFiltro());
	}

}

