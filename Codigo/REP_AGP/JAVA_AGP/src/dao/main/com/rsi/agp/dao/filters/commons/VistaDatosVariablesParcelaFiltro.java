package com.rsi.agp.dao.filters.commons;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.commons.VistaDatosVariablesParcela;

public class VistaDatosVariablesParcelaFiltro implements Filter {

	private BigDecimal codconcepto;
	private Long lineaseguroid;
	private BigDecimal codigo;

	public VistaDatosVariablesParcelaFiltro () {
		super();
	}

	public VistaDatosVariablesParcelaFiltro(final BigDecimal codconcepto, final Long lineaseguroid, final BigDecimal codigo) {
		this.codconcepto = codconcepto;
		this.lineaseguroid = lineaseguroid;
		this.codigo = codigo;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		
		Criteria criteria = sesion.createCriteria(VistaDatosVariablesParcela.class);

		if (!StringUtils.nullToString(codconcepto).equals("")) {
			criteria.add(Restrictions.eq("id.codconcepto", this.codconcepto));
		}
		
		if (!StringUtils.nullToString(lineaseguroid).equals("")) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("id.lineaseguroid", this.lineaseguroid))
					   .add(Restrictions.eq("id.lineaseguroid", 0L)));
		}
		
		if (!StringUtils.nullToString(codigo).equals("")) {
			criteria.add(Restrictions.eq("id.codigo", this.codigo));
		}
		
		return criteria;
	}

	public BigDecimal getCodconcepto() {
		return codconcepto;
	}

	public void setCodconcepto(BigDecimal codconcepto) {
		this.codconcepto = codconcepto;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public BigDecimal getCodigo() {
		return codigo;
	}

	public void setCodigo(BigDecimal codigo) {
		this.codigo = codigo;
	}

	
}
