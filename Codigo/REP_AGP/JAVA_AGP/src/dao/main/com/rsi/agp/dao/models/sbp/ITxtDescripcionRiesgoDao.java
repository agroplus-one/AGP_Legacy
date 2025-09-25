package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;

public interface ITxtDescripcionRiesgoDao extends GenericDao{
	
	public String getTxtDescRiesgo (BigDecimal codPlan);	
}
