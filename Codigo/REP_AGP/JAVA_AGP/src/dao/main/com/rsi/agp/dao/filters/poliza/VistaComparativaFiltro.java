package com.rsi.agp.dao.filters.poliza;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.poliza.VistaComparativas;

public class VistaComparativaFiltro implements Filter {
	
	private Long lineaseguroid;
	private String codmodulo;
	private Character elegible;

	public VistaComparativaFiltro() {
		super();
	}
	
	public VistaComparativaFiltro(final ModuloId moduloId) {
			this.lineaseguroid = new Long(moduloId.getLineaseguroid());
			this.codmodulo = moduloId.getCodmodulo();
	}
	 
	public VistaComparativaFiltro(final Long idLinea, final String codmodulo) {
			this.lineaseguroid = idLinea;
			this.codmodulo = codmodulo;
	}
	 
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(VistaComparativas.class);
		
		if (this.lineaseguroid != null)
		{
			Criterion crit = Restrictions.eq("id.lineaseguroid", this.lineaseguroid);
			criteria.add(crit);
		}
		if (this.codmodulo != null && !"".equalsIgnoreCase(this.codmodulo))
		{
			Criterion crit = Restrictions.eq("id.codmodulo", this.codmodulo);
			criteria.add(crit);
		}
		if (this.elegible != null)
		{
			Criterion crit = Restrictions.eq("id.elegible", this.elegible);
			criteria.add(crit);
		}
		return criteria;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public String getCodmodulo() {
		return codmodulo;
	}

	public void setCodmodulo(String codmodulo) {
		this.codmodulo = codmodulo;
	}

	public Character getElegible() {
		return elegible;
	}

	public void setElegible(Character elegible) {
		this.elegible = elegible;
	}

}
