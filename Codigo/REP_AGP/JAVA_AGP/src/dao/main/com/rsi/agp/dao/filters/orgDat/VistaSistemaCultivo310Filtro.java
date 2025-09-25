package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cgen.SistemaCultivo;


public class VistaSistemaCultivo310Filtro implements Filter {
	
	List<BigDecimal> lstCodSistemaCultivoClase;
	
	public VistaSistemaCultivo310Filtro() {
		super();
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(SistemaCultivo.class);
		if(this.getLstCodSistemaCultivoClase().size() > 0){
			criteria.add(Restrictions.in("codsistemacultivo", this.getLstCodSistemaCultivoClase()));
		}
		return criteria;
	}

	public List<BigDecimal> getLstCodSistemaCultivoClase() {
		return lstCodSistemaCultivoClase;
	}

	public void setLstCodSistemaCultivoClase(
			List<BigDecimal> lstCodSistemaCultivoClase) {
		this.lstCodSistemaCultivoClase = lstCodSistemaCultivoClase;
	}


}
