package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cgen.IGP;


public class VistaIGP307Filtro implements Filter {
	
	List<BigDecimal> lstValoresConcepto;
	
	public VistaIGP307Filtro() {
		super();
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(IGP.class);
		
		if(this.getLstValoresConcepto().size() > 0){
			criteria.add(Restrictions.in("codigp", this.getLstValoresConcepto()));
		}

		return criteria;
	}
	
	public List<BigDecimal> getLstValoresConcepto() {
		return lstValoresConcepto;
	}

	public void setLstValoresConcepto(List<BigDecimal> lstValoresConcepto) {
		this.lstValoresConcepto = lstValoresConcepto;
	}
	

}
