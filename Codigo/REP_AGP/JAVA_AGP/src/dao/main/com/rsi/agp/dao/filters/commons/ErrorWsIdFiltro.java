/*
 **************************************************************************************************
 *
 *  CReACION:
 *  ------------
 *
 * REFERENCIA  FECHA       AUTOR             DESCRIPCION
 * ----------  ----------  ----------------  ------------------------------------------------------
 * P000015034              Antonio Serrano  
 *
 **************************************************************************************************
 */
package com.rsi.agp.dao.filters.commons;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.commons.ErrorWs;
import com.rsi.agp.dao.tables.commons.ErrorWsAccion;

public class ErrorWsIdFiltro implements Filter {

	private BigDecimal codError;
	private Character catalogo;
	
	public BigDecimal getCodError() {
		return codError;
	}

	public void setCodError(BigDecimal codError) {
		this.codError = codError;
	}
	public Character getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(Character catalogo) {
		this.catalogo = catalogo;
	}


	/**
	 * Crea la consulta con el campo ocultar fijado a S
	 * @param codPlan
	 * @param codLinea
	 * @param codEntidad
	 * @param servicio
	 */
	public ErrorWsIdFiltro(BigDecimal codError, Character catalogo) {
		super();
		this.codError = codError;
		this.catalogo = catalogo;
	}

	@Override
	public Criteria getCriteria(Session session) { 
		Criteria criteria = session.createCriteria(ErrorWs.class);		
		criteria.add(Restrictions.eq("id.coderror", codError));
		criteria.add(Restrictions.eq("id.catalogo", catalogo));
		return criteria;
	}
}
