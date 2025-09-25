package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.renovables.EstadoRenovacionAgroplus;

public class EstadoRenAgroplusFilter implements Filter {

	private BigDecimal estadoRenAgroplusExcluir[];
	
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(EstadoRenovacionAgroplus.class);
		
		if(estadoRenAgroplusExcluir != null){
			if(estadoRenAgroplusExcluir.length > 0){
				criteria.add(Restrictions.not(Restrictions.in("idestado", estadoRenAgroplusExcluir)));
			}
		}
		return criteria;
	}

	public BigDecimal[] getEstadosRenAgroplusExcluir() {
		return estadoRenAgroplusExcluir;
	}

	public void setEstadosRenAgroplusExcluir(BigDecimal[] estadoRenAgroplusExcluir) {
		this.estadoRenAgroplusExcluir = estadoRenAgroplusExcluir;
	}

	
	
}
