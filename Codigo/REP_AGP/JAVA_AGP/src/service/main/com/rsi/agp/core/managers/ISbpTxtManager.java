package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.util.HashMap;

public interface ISbpTxtManager {
	
	/**
	 * Obtiene los txt descriptivos del informe de la Poliza de Sbp
	 * @param codPlan
	 * @param parametros 
	 */
	public HashMap<String, Object> getTxtInformePolizaSbp (BigDecimal codPlan);
	

}
