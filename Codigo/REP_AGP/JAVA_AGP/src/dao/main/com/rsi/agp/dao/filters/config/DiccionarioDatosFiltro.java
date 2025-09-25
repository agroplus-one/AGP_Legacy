package com.rsi.agp.dao.filters.config;


import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.org.DiccionarioDatos;

public class DiccionarioDatosFiltro implements Filter {
	
	private List<DiccionarioDatos> lista;
	
	
	public DiccionarioDatosFiltro() {	
	}
	public DiccionarioDatosFiltro(List<DiccionarioDatos> lista) {	
		this.lista = lista;
	}



	public Criteria getCriteria(Session sesion) {
	
		Criteria criteria = sesion.createCriteria(DiccionarioDatos.class);
		criteria.addOrder(Order.asc("codconcepto"));
		
		if(lista != null && !lista.isEmpty()){
			criteria.setProjection(Projections.projectionList()
					.add(Projections.alias(Projections.property("codconcepto"), "concepto" ))
					.add(Projections.alias(Projections.property("longitud"), "longitud" ))
					.add(Projections.alias(Projections.property("decimales"), "decimales" ))
					.add(Projections.alias(Projections.property("tipoNaturaleza.codtiponaturaleza"), "tipo" ))
					
			);
		}
		
		return criteria;
		
	}

}
