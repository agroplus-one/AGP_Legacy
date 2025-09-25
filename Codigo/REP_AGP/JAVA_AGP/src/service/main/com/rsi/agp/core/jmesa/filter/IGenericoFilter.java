package com.rsi.agp.core.jmesa.filter;

import org.hibernate.Criteria;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public interface IGenericoFilter extends CriteriaCommand {
	
	public void addFilter(String property, Object value);

	public void addFilter(String property, Object value, String operador);

	public void clear();

	public void buildCriteria(Criteria criteria, String property, Object value, String tipo);

	public void buildCriteria(Criteria criteria, String property, Object value, String tipo, String operador);
}
