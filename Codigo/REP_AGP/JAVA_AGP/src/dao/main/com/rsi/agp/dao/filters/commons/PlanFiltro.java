/*
**************************************************************************************************
*
*  CReACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034              Miguel Granadino  
*
 **************************************************************************************************
*/
package com.rsi.agp.dao.filters.commons;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.poliza.Linea;

public class PlanFiltro implements Filter {

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(Linea.class);
		criteria.setProjection(
				Projections.distinct(
						Projections.projectionList().add(Projections.property("codplan"))
				)
		);
		
		criteria.addOrder(Order.asc("codplan"));
		
		return criteria;
	}
}
