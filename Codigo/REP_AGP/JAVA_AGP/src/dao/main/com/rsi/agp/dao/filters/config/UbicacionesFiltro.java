package com.rsi.agp.dao.filters.config;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.org.Ubicacion;

public class UbicacionesFiltro implements Filter{

	private List listCodUbic;	
	
	public UbicacionesFiltro() {	}

	public UbicacionesFiltro(List listCodUbic) {
		this.listCodUbic = listCodUbic;
	}



	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(Ubicacion.class);
		
		if(listCodUbic != null){
			criteria.add(Restrictions.in("codubicacion", listCodUbic));
		}
		
		return criteria;
	}

}
