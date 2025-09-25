package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.Cultivo;

@SuppressWarnings("rawtypes")
public interface ICultivoDao extends GenericDao {
	
	/**
	 * Devuelve el cultivo si existe para el plan/línea y código de cultivo indicados
	 * @param codPlan
	 * @param codLinea
	 * @param codCultivo
	 * @return Cultivo o null si no existe
	 */
	public Cultivo getCultivo (BigDecimal codPlan, BigDecimal codLinea, BigDecimal codCultivo);
}