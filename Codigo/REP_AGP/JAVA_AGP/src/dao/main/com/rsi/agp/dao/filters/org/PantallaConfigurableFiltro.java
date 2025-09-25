package com.rsi.agp.dao.filters.org;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;

public class PantallaConfigurableFiltro implements Filter {
	private Long idpantallaconfigurable;
	private Long idpantalla;
	private Long lineaseguroid;
	
	public PantallaConfigurableFiltro (Long idpantallaconfigurable, Long idpantalla, Long lineaseguroid)
	{
		this.idpantallaconfigurable = idpantallaconfigurable;
		this.idpantalla = idpantalla;
		this.lineaseguroid = lineaseguroid;
	}
	
	public Criteria getCriteria(Session sesion) 
	{
		Criteria criteria = sesion.createCriteria(PantallaConfigurable.class);
		criteria.createAlias("linea", "l");
		
		if (FiltroUtils.noEstaVacio(idpantallaconfigurable)){
			criteria.add(Restrictions.eq("idpantallaconfigurable", idpantallaconfigurable));
		}
		if(FiltroUtils.noEstaVacio(idpantalla)){
			criteria.add(Restrictions.eq("pantalla.idpantalla", idpantalla));
		}
		if (FiltroUtils.noEstaVacio(lineaseguroid)){
			criteria.add(Restrictions.eq("l.lineaseguroid", lineaseguroid));
		}
		
		criteria.addOrder(Order.desc("l.codplan"));
		
		return criteria;
	}

	public Long getIdpantallaconfigurable() {
		return idpantallaconfigurable;
	}

	public void setIdpantallaconfigurable(Long idpantallaconfigurable) {
		this.idpantallaconfigurable = idpantallaconfigurable;
	}

	public Long getIdpantalla() {
		return idpantalla;
	}

	public void setIdpantalla(Long idpantalla) {
		this.idpantalla = idpantalla;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}
}
