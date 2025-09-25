package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;

public class CaracteristicaExplotacionFiltro implements Filter{

	private Long lineaseguroid;
	
	public CaracteristicaExplotacionFiltro(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}	

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(OrganizadorInformacion.class );
		
		criteria.add(Restrictions.eq("id.lineaseguroid", lineaseguroid));
		criteria.add(Restrictions.eq("id.coduso",new BigDecimal(31)));
		criteria.add(Restrictions.eq("id.codubicacion",new BigDecimal(18)));
		criteria.add(Restrictions.eq("id.codconcepto",new BigDecimal(106)));
		
		return criteria;
	}

}
