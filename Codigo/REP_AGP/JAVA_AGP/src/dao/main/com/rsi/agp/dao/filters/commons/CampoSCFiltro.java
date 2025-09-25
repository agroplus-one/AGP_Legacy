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

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.org.OrganizadorInformacion;

public class CampoSCFiltro implements Filter {

	Long lineaseguroid  = null;
	BigDecimal uso            = null;
	BigDecimal ubicacion      = null;

	public CampoSCFiltro(Long lineaseguroid, BigDecimal uso, BigDecimal ubicacion) {
		this.lineaseguroid  = lineaseguroid;
		this.uso            = uso;
		this.ubicacion      = ubicacion;
	}

    
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(OrganizadorInformacion.class);
		criteria.add(Restrictions.eq("id.lineaseguroid", this.lineaseguroid));
		criteria.add(Restrictions.eq("uso.coduso", this.uso));
		criteria.add(Restrictions.eq("ubicacion.codubicacion", this.ubicacion));
		return criteria;
	}
}
