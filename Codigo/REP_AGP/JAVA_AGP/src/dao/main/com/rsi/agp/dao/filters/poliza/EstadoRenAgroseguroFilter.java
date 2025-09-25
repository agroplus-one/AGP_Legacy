package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroseguro;

public class EstadoRenAgroseguroFilter implements Filter {

	private BigDecimal estadoRenAgroseguroExcluir[];
	
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(EstadoRenovacionAgroseguro.class);
		
		if(estadoRenAgroseguroExcluir != null){
			if(estadoRenAgroseguroExcluir.length > 0){
				criteria.add(Restrictions.not(Restrictions.in("idestado", estadoRenAgroseguroExcluir)));
			}
		}
		return criteria;
	}

	public BigDecimal[] getEstadosRenAgroseguroExcluir() {
		return estadoRenAgroseguroExcluir;
	}

	public void setEstadosRenAgroseguroExcluir(BigDecimal[] estadoRenAgroseguroExcluir) {
		this.estadoRenAgroseguroExcluir = estadoRenAgroseguroExcluir;
	}

	
	
}
