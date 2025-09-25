package com.rsi.agp.dao.filters.cpl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cpl.IncompatibilidadTipoSubvDeclarable;

public class IncompatibilidadTipoSubvDeclarableFiltro implements Filter
{
	private java.lang.Long lineaseguroid;
    private BigDecimal codtiposubvenesa;
    private BigDecimal codtiposubvinc;
    private List<BigDecimal> listaSubvInc;
    
	@Override
	public Criteria getCriteria(Session sesion) 
	{
		Criteria criteria = sesion.createCriteria(IncompatibilidadTipoSubvDeclarable.class);
		
		if (this.lineaseguroid != null)
		{
			Criterion crit = Restrictions.eq("id.lineaseguroid", this.getLineaseguroid());
			criteria.add(crit);
		}
		
		if (this.codtiposubvenesa != null)
		{
			Criterion crit = Restrictions.eq("id.codtiposubvenesa", this.getCodtiposubvenesa());
			criteria.add(crit);
		}
		
		if (this.getCodtiposubvinc() != null)
		{
			Criterion crit = Restrictions.eq("id.codtiposubvinc", this.getCodtiposubvinc());
			criteria.add(crit);
		}
		
		if (this.getListaSubvInc() != null && this.getListaSubvInc().size() > 0)
		{
			Criterion crit = Restrictions.in("id.codtiposubvinc", this.getListaSubvInc());
			criteria.add(crit);
		}
		return criteria;
	}

	public java.lang.Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(java.lang.Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public BigDecimal getCodtiposubvenesa() {
		return codtiposubvenesa;
	}

	public void setCodtiposubvenesa(BigDecimal codtiposubvenesa) {
		this.codtiposubvenesa = codtiposubvenesa;
	}

	public BigDecimal getCodtiposubvinc() {
		return codtiposubvinc;
	}

	public void setCodtiposubvinc(BigDecimal codtiposubvinc) {
		this.codtiposubvinc = codtiposubvinc;
	}

	public List<BigDecimal> getListaSubvInc() {
		return listaSubvInc;
	}

	public void setListaSubvInc(List<BigDecimal> listaSubvInc) {
		this.listaSubvInc = listaSubvInc;
	}
	
}
