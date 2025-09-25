package com.rsi.agp.dao.filters.cesp;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cgen.ConceptoPpalModulo;

public class ConceptoPpalFiltro implements Filter {
	
	private BigDecimal codconceptoppalmod;
	
	public ConceptoPpalFiltro () {
		super();
	}
	
	public ConceptoPpalFiltro (BigDecimal codconceptoppalmod) {
		this.codconceptoppalmod = codconceptoppalmod; 
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		
		Criteria criteria = sesion.createCriteria(ConceptoPpalModulo.class);
		
		if (FiltroUtils.noEstaVacio(this.codconceptoppalmod)) {
			criteria.add(Restrictions.eq("codconceptoppalmod", this.codconceptoppalmod));
		}
		
		return criteria;
	}

}
