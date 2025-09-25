package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;

public interface ISistemaCultivoDao extends GenericDao {
	
	/**
	 * Comprueba si existe el sistema de cultivo para el código indicado
	 * @param codSistCult
	 * @return
	 */
	public boolean existeSistemaCultivo (BigDecimal codSistCult);

}
