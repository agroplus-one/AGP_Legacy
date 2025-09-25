package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.core.exception.DAOException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;

@SuppressWarnings("rawtypes")
public interface IConfiguracionCamposDao extends GenericDao {
	
	public List<ConfiguracionCampo> getDatosVariablesCargaParcelasMarcados(Long lineaseguroid, List<BigDecimal> lstCodConceptos,String grupoSeguro) throws DAOException;

}
