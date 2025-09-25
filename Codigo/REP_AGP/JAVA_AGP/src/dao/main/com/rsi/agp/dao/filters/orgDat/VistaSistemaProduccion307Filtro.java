package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cgen.SistemaProduccion;


public class VistaSistemaProduccion307Filtro implements Filter {
	
	List<BigDecimal> lstValoresConceptoFactor;

	public List<BigDecimal> getLstValoresConceptoFactor() {
		return lstValoresConceptoFactor;
	}

	public void setLstValoresConceptoFactor(
			List<BigDecimal> lstValoresConceptoFactor) {
		this.lstValoresConceptoFactor = lstValoresConceptoFactor;
	}

	public VistaSistemaProduccion307Filtro() {
		super();
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(SistemaProduccion.class);
		if(this.getLstValoresConceptoFactor().size() > 0){
			criteria.add(Restrictions.in("codsistemaproduccion", this.getLstValoresConceptoFactor()));
		}
		return criteria;
	}


}
