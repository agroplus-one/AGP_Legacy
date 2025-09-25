package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;

@SuppressWarnings("rawtypes")
public interface IConceptoPpalDao extends GenericDao {
	/**
	 * 
	 * @param codConceptoPpal
	 * @return
	 */
	public boolean existeConceptoPpal (BigDecimal codConceptoPpal);
	
	public ConceptoPpalModulo getConceptoPpal(BigDecimal codConceptoPpal) throws Exception;
}
