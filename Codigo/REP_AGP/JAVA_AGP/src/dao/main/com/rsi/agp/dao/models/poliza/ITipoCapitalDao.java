package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface ITipoCapitalDao extends GenericDao {

	/**
	 * Comprueba si existe el tipo de capital para los valores indicados en la vista
	 * VW_ORG_DATOS_TIPO_CAPITAL
	 */
	public boolean existeTipoCapital(BigDecimal codplan, BigDecimal codlinea, String codmodulo, BigDecimal codcultivo,
			BigDecimal codtipocapital);
}