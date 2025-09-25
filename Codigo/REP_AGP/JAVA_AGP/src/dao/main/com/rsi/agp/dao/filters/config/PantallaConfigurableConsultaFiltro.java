package com.rsi.agp.dao.filters.config;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.config.PantallaConfigurable;


public class PantallaConfigurableConsultaFiltro implements Filter {
	
    private PantallaConfigurable pantallaConfigurable;
	
	public PantallaConfigurableConsultaFiltro(final PantallaConfigurable pantallaConfigurable){
		this.pantallaConfigurable = new PantallaConfigurable();
		
		this.pantallaConfigurable.setIdpantallaconfigurable(pantallaConfigurable.getIdpantallaconfigurable());
		this.pantallaConfigurable.setLinea(pantallaConfigurable.getLinea());
		this.pantallaConfigurable.setPantalla(pantallaConfigurable.getPantalla());
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(PantallaConfigurable.class);
		criteria.createAlias("linea", "l");		
		
		if (FiltroUtils.noEstaVacio(pantallaConfigurable)) 
		{
			if (FiltroUtils.noEstaVacio(pantallaConfigurable.getLinea()))
			{
				if(FiltroUtils.noEstaVacio(pantallaConfigurable.getLinea().getCodplan()))
				{
					criteria.add(Restrictions.eq("l.codplan", pantallaConfigurable.getLinea().getCodplan()));
				}
				
				if (FiltroUtils.noEstaVacio(pantallaConfigurable.getLinea().getCodlinea()))
				{
					criteria.add(Restrictions.eq("l.codlinea", pantallaConfigurable.getLinea().getCodlinea()));
				}
				
				if(FiltroUtils.noEstaVacio(pantallaConfigurable.getLinea().getLineaseguroid()) && (pantallaConfigurable.getLinea().getNomlinea() != "smsAviso"))
				{
					criteria.add(Restrictions.eq("l.lineaseguroid", pantallaConfigurable.getLinea().getLineaseguroid()));
				}
			}
			
			if (FiltroUtils.noEstaVacio(pantallaConfigurable.getPantalla()))
			{
				if(FiltroUtils.noEstaVacio(pantallaConfigurable.getPantalla().getIdpantalla()))
				{
					criteria.add(Restrictions.eq("pantalla.idpantalla", pantallaConfigurable.getPantalla().getIdpantalla()));
				}
			}
		}
		
		criteria.addOrder(Order.desc("l.codplan"));
		
		return criteria;
	}
	
}


