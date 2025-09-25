/*
 **************************************************************************************************
 *
 *  CReACION:
 *  ------------
 *
 * REFERENCIA  FECHA       AUTOR             DESCRIPCION
 * ----------  ----------  ----------------  ------------------------------------------------------
 * P000015034 25-06-2010  Ernesto Laura      Filtro para consultas tabla RelacionTablasXML
 *
  **************************************************************************************************
 */
package com.rsi.agp.dao.filters.commons;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.commons.RelacionTablaXml;

public class RelacionTablasXmlFiltro implements Filter {
	private String tipoSc;
	private BigDecimal[] idsTablas;

	public String getTipoSc() {
		return tipoSc;
	}

	public void setTipoSc(String tipoSc) {
		this.tipoSc = tipoSc;
	}

	public BigDecimal[] getIdsTablas() {
		return idsTablas;
	}
	public void setIdsTablas(BigDecimal[] idsTablas) {
		this.idsTablas = idsTablas;
	}
	public void setIdsTablas(String[] idsTablas) {
		BigDecimal[] ids = new BigDecimal[idsTablas.length];
		for (int i = 0; i < idsTablas.length; i++)
			ids[i] = new BigDecimal(idsTablas[i].trim());
		this.idsTablas = ids;
	}
	
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(RelacionTablaXml.class).addOrder(Order.asc("xml"));
		
		//Se recorren los atributos del objeto filtro y se van añadiendo los "criterion" al criteria
		if (this.getIdsTablas()!=null){
			Criterion crit = Restrictions.in("numtabla", this.getIdsTablas());
			criteria.add(crit);
		}
		
		if (this.getTipoSc()!= null && !this.getTipoSc().equals("")){
			Criterion crit = Restrictions.eq("tiposc", this.getTipoSc());
			criteria.add(crit);
		}
		Order or = Order.desc("nombre");
		criteria.addOrder(or);
		return criteria;
	}	
}
