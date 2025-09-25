package com.rsi.agp.dao.filters.cpl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModuloId;

public class CaracteristicasModuloFiltro implements Filter{
	private CaracteristicaModuloId id;
	private boolean soloElegibles;
	private final Character[] eleg = {'E'};
    
    @Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(CaracteristicaModulo.class);
		
		if (this.id != null)
		{
			if (this.id.getLineaseguroid() != null)
			{
				Criterion crit = Restrictions.eq("id.lineaseguroid", this.getId().getLineaseguroid());
				criteria.add(crit);
			}		
			if (this.id.getCodmodulo() != null)
			{
				Criterion crit = Restrictions.eq("id.codmodulo", this.getId().getCodmodulo());
				criteria.add(crit);
			}
			if (this.getId().getColumnamodulo() != null)
			{
				Criterion crit = Restrictions.eq("id.columnamodulo", this.getId().getColumnamodulo());
				criteria.add(crit);
			}
			if (this.id.getFilamodulo() != null)
			{
				Criterion crit = Restrictions.eq("id.filamodulo", this.getId().getFilamodulo());
				criteria.add(crit);
			}
		}
		
		if (this.soloElegibles)
		{
			Criterion crit = Restrictions.in("tipovalor", eleg);
			criteria.add(crit);
		}
		Order or = Order.asc("datovinculado");
		criteria.addOrder(or);
		
		return criteria;
    }

	public CaracteristicaModuloId getId() {
		return id;
	}

	public void setId(CaracteristicaModuloId id) {
		this.id = id;
	}

	public boolean isSoloElegibles() {
		return soloElegibles;
	}

	public void setSoloElegibles(boolean soloElegibles) {
		this.soloElegibles = soloElegibles;
	}

}
