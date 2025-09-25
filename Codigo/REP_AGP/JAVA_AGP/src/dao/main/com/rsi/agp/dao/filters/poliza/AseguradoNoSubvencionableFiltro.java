package com.rsi.agp.dao.filters.poliza;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cgen.AseguradoNoSubvencionable;

public class AseguradoNoSubvencionableFiltro implements Filter {

	private String nifasegurado;

	public AseguradoNoSubvencionableFiltro() {
		super();
	}

	public AseguradoNoSubvencionableFiltro(String nifasegurado) {
		this.nifasegurado = nifasegurado;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(AseguradoNoSubvencionable.class);

		if (this.nifasegurado != null
				&& !"".equalsIgnoreCase(this.nifasegurado)) {
			Criterion crit = Restrictions.eq("id.nifasegurado",
					this.nifasegurado);
			criteria.add(crit);
		}
		
        return criteria;
	}

	public String getNifasegurado() {
		return nifasegurado;
	}

	public void setNifasegurado(String nifasegurado) {
		this.nifasegurado = nifasegurado;
	}

}
