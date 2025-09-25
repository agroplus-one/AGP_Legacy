package com.rsi.agp.dao.filters.cesp.impl;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cesp.GrupoAseguradoCe;

public class GrupoAseguradoFiltro implements Filter {

    private String codgrupoaseg;
    private BigDecimal bonifrecprimas;
    private BigDecimal bonifrecrdtomax;

	public GrupoAseguradoFiltro() {
		super();
	}

	public GrupoAseguradoFiltro(final String codgrupoaseg, final BigDecimal bonifrecprimas,
			final BigDecimal bonifrecrdtomax) {
		super();
		this.codgrupoaseg = codgrupoaseg;
		this.bonifrecprimas = bonifrecprimas;
		this.bonifrecrdtomax = bonifrecrdtomax;
	}
	
	
	//Creamos este constructor a partir de un objeto del Bean que contiene los datos recogidos de la web
	public GrupoAseguradoFiltro(final GrupoAseguradoCe grupoAsegurado){
		super();
		this.codgrupoaseg = grupoAsegurado.getCodgrupoaseg();
		this.bonifrecprimas = grupoAsegurado.getBonifrecprimas();
		this.bonifrecrdtomax = grupoAsegurado.getBonifrecrdtomax();
	}
	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(GrupoAseguradoCe.class); //en este criterio le pasamos la clase en que tabla buscar hibernate
		if(FiltroUtils.noEstaVacio(codgrupoaseg)){
			criteria.add(Restrictions.eq("codgrupoaseg", codgrupoaseg));
		}
		if(FiltroUtils.noEstaVacio(bonifrecprimas)){
			criteria.add(Restrictions.eq("bonifrecprimas", bonifrecprimas));
		}
		if(FiltroUtils.noEstaVacio(bonifrecrdtomax)){
			criteria.add(Restrictions.eq("bonifrecrdtomax", bonifrecrdtomax));
		}


		return criteria;
	}

}
