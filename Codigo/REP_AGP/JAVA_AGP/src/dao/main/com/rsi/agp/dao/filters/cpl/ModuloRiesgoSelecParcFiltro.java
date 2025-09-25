package com.rsi.agp.dao.filters.cpl;

import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.Modulo;

public class ModuloRiesgoSelecParcFiltro implements Filter{

	private Long lineaseguroid; 
	private String codmodulo; 
	
    public ModuloRiesgoSelecParcFiltro(){
	             /*VOID*/
	}
	
	public ModuloRiesgoSelecParcFiltro(Long lineaseguroid, String codmodulo){
		this.lineaseguroid = lineaseguroid;
		this.codmodulo = codmodulo;
	}
	
	public Criteria getCriteria(Session sesion){
		
		// linea seguro id
		Criteria criteria = sesion.createCriteria(Modulo.class);
		//codmodulo = "1;P;2"; --> datos de prueba
		StringTokenizer token = new StringTokenizer(codmodulo, ";");
		
		// modulos
		if(token.countTokens() > 0){
			Disjunction disjuntion = Restrictions.disjunction(); //OR
			while(token.hasMoreTokens()){  
	
				String codModulo = token.nextToken();
				Conjunction conjuntion = Restrictions.conjunction(); // AND
				conjuntion
				    .add(Restrictions.eq("linea.lineaseguroid", lineaseguroid))
				    .add(Restrictions.eq("id.codmodulo", codModulo)
				);
				
				disjuntion.add(conjuntion);
		    } 
			
			criteria.add(disjuntion);
		}else{
			criteria.add(Restrictions.eq("id.codmodulo","-1"));
		}
		return criteria;
	}
}
