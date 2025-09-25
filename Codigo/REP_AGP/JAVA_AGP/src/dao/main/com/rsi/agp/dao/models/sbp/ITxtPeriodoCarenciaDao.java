package com.rsi.agp.dao.models.sbp;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;

public interface ITxtPeriodoCarenciaDao extends GenericDao{
	
	public String getTxtPeriodoCarencia (BigDecimal codPlan);	
}
