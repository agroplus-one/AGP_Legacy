package com.rsi.agp.dao.filters.config;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;

public class OrganizacionInformacionFiltro implements Filter{

	private Long lineaseguroid;
	private Long coduso;
	
	public OrganizacionInformacionFiltro() {}
	
	public OrganizacionInformacionFiltro(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}	

	public OrganizacionInformacionFiltro(Long lineaseguroid, Long coduso) {
		
		this.lineaseguroid = lineaseguroid;
		this.coduso = coduso;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(OrganizadorInformacion.class );
		
		criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
		
		if(this.coduso == null){			
			//Proyectamos por coduso y añadimos el distinct al setresult		
			criteria.setProjection(Projections.distinct(Projections.property("id.coduso")));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		}else{
			criteria.add(Restrictions.eq("id.coduso",new BigDecimal(coduso)));
			criteria.setProjection(Projections.distinct(Projections.property("id.codubicacion")));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		}
	
		
		return criteria;
	}

}
