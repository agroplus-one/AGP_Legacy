package com.rsi.agp.dao.filters.config;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.org.Uso;



public class UsosFiltro implements Filter {

	private BigDecimal coduso;
	private String desuso;
	private List listaCodUsos;

	
	
	public UsosFiltro(List listaCodUsos) {		
		this.listaCodUsos = listaCodUsos;
	}



	public BigDecimal getCoduso() {
		return coduso;
	}



	public void setCoduso(BigDecimal coduso) {
		this.coduso = coduso;
	}



	public String getDesuso() {
		return desuso;
	}



	public void setDesuso(String desuso) {
		this.desuso = desuso;
	}



	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(Uso.class);
		
		//Se recorren los atributos del objeto filtro y se van añadiendo los "criterion" al criteria
		if (this.getDesuso()!=null){
			// eq --> where
			Criterion crit = Restrictions.eq("coduso", this.getDesuso());
			criteria.add(crit);
		}
		if(this.listaCodUsos!=null && this.listaCodUsos.size()>0){
			criteria.add(Restrictions.in("coduso", listaCodUsos));			
		}
		
		return criteria;
	}	
}