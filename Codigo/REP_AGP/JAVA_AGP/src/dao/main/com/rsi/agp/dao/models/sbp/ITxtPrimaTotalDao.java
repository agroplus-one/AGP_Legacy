package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;

public interface ITxtPrimaTotalDao extends GenericDao{
	
	public String getTxtPrimaTotal (BigDecimal codPlan);	
}
