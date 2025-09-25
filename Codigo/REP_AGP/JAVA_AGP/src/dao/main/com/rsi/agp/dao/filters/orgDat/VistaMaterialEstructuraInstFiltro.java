package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cgen.MaterialEstructura;


public class VistaMaterialEstructuraInstFiltro implements Filter {
	
	List<BigDecimal> lstValoresConceptoFactor;
	
	public List<BigDecimal> getLstValoresConceptoFactor() {
		return lstValoresConceptoFactor;
	}

	public void setLstValoresConceptoFactor(
			List<BigDecimal> lstValoresConceptoFactor) {
		this.lstValoresConceptoFactor = lstValoresConceptoFactor;
	}

	public VistaMaterialEstructuraInstFiltro() {
		super();
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(MaterialEstructura.class);
		
		if(lstValoresConceptoFactor != null && lstValoresConceptoFactor.size() > 0){
			criteria.add(Restrictions.in("codmaterialestructura", lstValoresConceptoFactor)); 	
		}
		
		return criteria;
	}


}
