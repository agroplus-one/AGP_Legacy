package com.rsi.agp.dao.filters.cpl;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.SubvencionesGrupo;

public class SubvencionesGrupoFiltro implements Filter{
	
	private BigDecimal plan;
	private BigDecimal grupo;

	public SubvencionesGrupoFiltro() {
	}

	public SubvencionesGrupoFiltro(BigDecimal plan, BigDecimal grupo) {
		this.plan = plan;
		this.grupo = grupo;
	}

	public Criteria getCriteria(Session sesion){
		Criteria criteria = sesion.createCriteria(SubvencionesGrupo.class);
		
		if (this.plan != null){
			criteria.add(Restrictions.eq("id.plan", this.plan));
		}
		
		if (this.grupo != null){
			criteria.add(Restrictions.eq("id.gruposubv", this.grupo));
		}
		criteria.setProjection(Projections.property("id.codtiposubv"));
		return criteria;
	}

	public BigDecimal getPlan() {
		return plan;
	}

	public void setPlan(BigDecimal plan) {
		this.plan = plan;
	}

	public BigDecimal getGrupo() {
		return grupo;
	}

	public void setGrupo(BigDecimal grupo) {
		this.grupo = grupo;
	}


}
