package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.renovables.PolizaRenovableEstadoEnvioIBAN;

public class EstadoRenEnvioIBANFilter implements Filter {

	private BigDecimal estadoRenAEnvioIBANExcluir[];
	
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(PolizaRenovableEstadoEnvioIBAN.class);
		
		if(estadoRenAEnvioIBANExcluir != null){
			if(estadoRenAEnvioIBANExcluir.length > 0){
				criteria.add(Restrictions.not(Restrictions.in("codigo", estadoRenAEnvioIBANExcluir)));
			}
		}
		return criteria;
	}

	public BigDecimal[] getEstadoRenAEnvioIBANExcluir() {
		return estadoRenAEnvioIBANExcluir;
	}

	public void setEstadoRenAEnvioIBANExcluir(
			BigDecimal[] estadoRenAEnvioIBANExcluir) {
		this.estadoRenAEnvioIBANExcluir = estadoRenAEnvioIBANExcluir;
	}



	
	
}
