package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.core.exception.BusinessException;
import com.rsi.agp.dao.models.GenericDao;
//import com.rsi.agp.dao.tables.poliza.Parcela;
import com.rsi.agp.dao.tables.anexo.Parcela;

public interface IUtilidadesParcelasAnexoDao extends GenericDao {
	public List getIdsParcelas(Parcela parcelaFiltro, String columna, String orden) throws BusinessException;
	
}

