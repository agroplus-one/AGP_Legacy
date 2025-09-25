package com.rsi.agp.dao.models.config;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.masc.CampoMascara;

@SuppressWarnings("rawtypes")
public interface ICampoMascaraDao extends GenericDao {
	
	public boolean existeCampoMascara(CampoMascara campoMascara);
}