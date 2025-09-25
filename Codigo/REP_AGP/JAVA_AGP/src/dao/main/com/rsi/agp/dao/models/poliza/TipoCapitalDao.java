package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;

import com.rsi.agp.dao.filters.cpl.TipoCapitalFiltro;
import com.rsi.agp.dao.models.BaseDaoHibernate;

public class TipoCapitalDao extends BaseDaoHibernate implements ITipoCapitalDao {

	@Override
	public boolean existeTipoCapital(BigDecimal codplan, BigDecimal codlinea, String codmodulo, BigDecimal codcultivo,
			BigDecimal codtipocapital) {

		try {
			TipoCapitalFiltro tcf = new TipoCapitalFiltro(codplan, codlinea, codmodulo, codcultivo, codtipocapital);
			return this.getObjects(tcf).size() > 0;
		} catch (Exception e) {
			logger.error("Error al comprobar el tipo de capital " + codtipocapital, e);
		}

		return false;
	}
}