package com.rsi.agp.core.jmesa.sort;

import org.jmesa.limit.Limit;

import com.rsi.agp.core.jmesa.dao.CriteriaCommand;

public interface IGenericoSort extends CriteriaCommand {
	
	public void addSort(String property, String order);

	public void getConsultaSort(Limit limit);

	public void clear();
}
