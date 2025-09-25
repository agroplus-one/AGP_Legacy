package com.rsi.agp.dao.filters.commons;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.SubentidadMediadora;
import com.rsi.agp.dao.tables.poliza.ImporteFraccionamiento;
import com.rsi.agp.dao.tables.poliza.Linea;

public class ImporteFraccFiltro implements Filter{
	
    private Linea linea;
    private SubentidadMediadora subentidadMediadora;

    public ImporteFraccFiltro () {
		super();
	}
    
	public ImporteFraccFiltro(Linea linea) {
		this.linea = linea;
	}
	
	public ImporteFraccFiltro(Long lineaseguroid) {
		this.linea.setLineaseguroid(lineaseguroid);
	}
	public ImporteFraccFiltro(Linea linea,SubentidadMediadora subentidadMediadora) {
		this.linea = linea;
		this.subentidadMediadora=subentidadMediadora;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(ImporteFraccionamiento.class);
		
		criteria.createAlias("linea", "linea");
		
		if (FiltroUtils.noEstaVacio(this.linea)) {
			criteria.add(Restrictions.eq("linea.codlinea",this.linea.getCodlinea()));
			criteria.add(Restrictions.eq("linea.codplan",this.linea.getCodplan()));
		}
		
		if (FiltroUtils.noEstaVacio(this.subentidadMediadora)) {
			criteria.add(Restrictions.eq("subentidadMediadora.id.codentidad",this.subentidadMediadora.getId().getCodentidad()));
			criteria.add(Restrictions.eq("subentidadMediadora.id.codsubentidad",this.subentidadMediadora.getId().getCodsubentidad()));
		}
	
		return criteria;
	}

	public Linea getLinea() {
		return linea;
	}

	public void setLinea(Linea linea) {
		this.linea = linea;
	}

	public SubentidadMediadora getSubentidadMediadora() {
		return subentidadMediadora;
	}

	public void setSubentidadMediadora(SubentidadMediadora subentidadMediadora) {
		this.subentidadMediadora = subentidadMediadora;
	}

}
