package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.poliza.EstadoPoliza;

public class EstadoPolizaFilter implements Filter {

	private BigDecimal estadosPolizaExcluir[];
	
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(EstadoPoliza.class);
		criteria.add(Restrictions.not(Restrictions.eq("idestado", Constants.ESTADO_POLIZA_BAJA)));
					
		return criteria;
	}

	public BigDecimal[] getEstadosPolizaExcluir() {
		return estadosPolizaExcluir;
	}

	public void setEstadosPolizaExcluir(BigDecimal[] estadosPolizaExcluir) {
		this.estadosPolizaExcluir = estadosPolizaExcluir;
	}

	
	
}
