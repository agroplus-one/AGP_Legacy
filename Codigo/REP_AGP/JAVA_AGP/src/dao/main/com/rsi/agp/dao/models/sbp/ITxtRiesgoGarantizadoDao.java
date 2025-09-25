package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;

public interface ITxtRiesgoGarantizadoDao extends GenericDao{
	
	public String getTxtRiesgoGarantizado (BigDecimal codPlan);	
}
