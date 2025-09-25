package com.rsi.agp.dao.filters.cpl;

import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.ConceptoCubiertoModulo;

public class ConceptoCubiertoModuloFiltro implements Filter{

	private Long lineaseguroid; 
	private String codmodulo; 
	
    public ConceptoCubiertoModuloFiltro(){
	             /*VOID*/
	}
	
	public ConceptoCubiertoModuloFiltro(Long lineaseguroid, String codmodulo){
		this.lineaseguroid = lineaseguroid;
		this.codmodulo = codmodulo;
	}
	
	public Criteria getCriteria(Session sesion){
		
		// linea seguro id
		Criteria criteria = sesion.createCriteria(ConceptoCubiertoModulo.class);
		//codmodulo = "1;P;2"; --> datos de prueba
		StringTokenizer token = new StringTokenizer(codmodulo, ";");
		
		// modulos
		
		Disjunction disjuntion = Restrictions.disjunction(); //OR
		while(token.hasMoreTokens()){  

			String codModulo = token.nextToken();
			Conjunction conjuntion = Restrictions.conjunction(); // AND
			conjuntion
			    .add(Restrictions.eq("id.lineaseguroid", lineaseguroid))
			    .add(Restrictions.eq("id.codmodulo", codModulo)
			);
			
			disjuntion.add(conjuntion);
	    } 
		
		criteria.add(disjuntion);
		criteria.createAlias("modulo", "modulo");
		criteria.addOrder(Order.asc("modulo.desmodulo"));
		
		return criteria;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public String getCodmodulo() {
		return codmodulo;
	}

	public void setCodmodulo(String codmodulo) {
		this.codmodulo = codmodulo;
	}
}
