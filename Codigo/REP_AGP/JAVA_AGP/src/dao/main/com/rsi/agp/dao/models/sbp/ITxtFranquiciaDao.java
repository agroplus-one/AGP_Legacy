package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;

public interface ITxtFranquiciaDao extends GenericDao{
	
	public String getTxtFranquicia (BigDecimal codPlan);	
}
