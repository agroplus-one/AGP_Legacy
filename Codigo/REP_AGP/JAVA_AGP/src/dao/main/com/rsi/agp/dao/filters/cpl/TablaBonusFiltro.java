package com.rsi.agp.dao.filters.cpl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.TablaBonus;
import com.rsi.agp.dao.tables.cpl.TablaBonusId;

public class TablaBonusFiltro implements Filter {

	TablaBonusId id;
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(TablaBonus.class);
		
		if (this.id != null)
		{
			if (this.id.getLineaseguroid() != null)
			{
				Criterion crit = Restrictions.eq("id.lineaseguroid", this.id.getLineaseguroid());
				criteria.add(crit);	
			}
			if (this.id.getCodhistorialasegurado() != null)
			{
				Criterion crit = Restrictions.eq("id.codhistorialasegurado", this.id.getCodhistorialasegurado());
				criteria.add(crit);
			}			
		}
		return criteria;
	}
	public TablaBonusId getId() {
		return id;
	}
	public void setId(TablaBonusId id) {
		this.id = id;
	}

}
