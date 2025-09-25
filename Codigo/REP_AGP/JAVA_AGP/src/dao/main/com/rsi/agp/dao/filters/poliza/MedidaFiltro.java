package com.rsi.agp.dao.filters.poliza;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cpl.Medida;

public class MedidaFiltro implements Filter {

	private Long lineaseguroid;
	private String nifasegurado;
	
	public MedidaFiltro() {
		super();
	}

	public MedidaFiltro(Long lineaseguroid, String nifasegurado) {
		super();
		this.lineaseguroid = lineaseguroid;
		this.nifasegurado = nifasegurado;
	}
	
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(Medida.class);

		if (FiltroUtils.noEstaVacio(this.lineaseguroid)){
			Criterion crit = Restrictions.eq("id.lineaseguroid",
					this.lineaseguroid);
			criteria.add(crit);
		}
		
		if (this.nifasegurado != null
				&& !"".equalsIgnoreCase(this.nifasegurado)) {
			Criterion crit = Restrictions.eq("id.nifasegurado",
					this.nifasegurado);
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

	public String getNifasegurado() {
		return nifasegurado;
	}

	public void setNifasegurado(String nifasegurado) {
		this.nifasegurado = nifasegurado;
	}

}
