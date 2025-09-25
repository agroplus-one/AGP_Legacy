package com.rsi.agp.dao.filters.config;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.config.RelacionCampo;
import com.rsi.agp.dao.tables.poliza.Linea;

public class RelacionCamposFiltro implements Filter {

	private Linea linea;
	private BigDecimal coduso;
	
	@Override
	public Criteria getCriteria(Session sesion) {
		final Criteria criteria = sesion.createCriteria(RelacionCampo.class);
		criteria.createAlias("linea", "l");
		criteria.createAlias("uso", "u");
		
		if (FiltroUtils.noEstaVacio(linea))
		{
			if (FiltroUtils.noEstaVacio(linea.getLineaseguroid()))
			{
				Criterion crit = Restrictions.eq("l.lineaseguroid", linea.getLineaseguroid());
				criteria.add(crit);
			}
			else
			{
				if (FiltroUtils.noEstaVacio(linea.getCodplan()))
				{
					Criterion crit = Restrictions.eq("l.codplan", linea.getCodplan());
					criteria.add(crit);	
				}
				if (FiltroUtils.noEstaVacio(linea.getCodlinea()))
				{
					Criterion crit = Restrictions.eq("l.codlinea", linea.getCodlinea());
					criteria.add(crit);
				}
			}			
		}
		
		if (FiltroUtils.noEstaVacio(coduso))
		{
			Criterion crit = Restrictions.eq("u.coduso", this.coduso);
			criteria.add(crit);
		}
		
		return criteria;
	}

	public Linea getLinea() {
		return linea;
	}

	public void setLinea(Linea linea) {
		this.linea = linea;
	}

	public BigDecimal getCoduso() {
		return coduso;
	}

	public void setCoduso(BigDecimal coduso) {
		this.coduso = coduso;
	}

}
