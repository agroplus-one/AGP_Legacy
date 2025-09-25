package com.rsi.agp.core.jmesa.service.mtoinf;

import java.math.BigDecimal;

@SuppressWarnings("rawtypes")
public abstract class Estados implements IEstados, Comparable {

	@Override
	public int compareTo(Object o) {
		
		// Si el parametro es de tipo IEstadosComp
		if (o instanceof Estados) {
			
			// Si ocurre cualquier error, se devuelve 0
			try {
				Estados obj = (Estados) o;			
				BigDecimal o1 = new BigDecimal (this.getIdEstadoInformes());
				BigDecimal o2 = new BigDecimal (obj.getIdEstadoInformes());
				
				return o1.compareTo(o2);
			}
			catch (Exception e) {
				return 0;
			}
		}
		
		return 0;
	}

}
