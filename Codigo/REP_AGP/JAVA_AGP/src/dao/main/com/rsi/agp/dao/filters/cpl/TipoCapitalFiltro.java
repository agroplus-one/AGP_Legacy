package com.rsi.agp.dao.filters.cpl;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.util.Constants;
import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.orgDat.VistaTipoCapital;

public class TipoCapitalFiltro implements Filter {
	
	private BigDecimal codplan;
    private BigDecimal codlinea;
    private String codmodulo;
    private BigDecimal codcultivo;
    private BigDecimal codtipocapital;
    
    public TipoCapitalFiltro (BigDecimal codplan, BigDecimal codlinea, String codmodulo, BigDecimal codcultivo, BigDecimal codtipocapital) {
    	this.codplan = codplan;
    	this.codlinea = codlinea;
    	this.codmodulo = codmodulo;
    	this.codcultivo = codcultivo;
    	this.codtipocapital = codtipocapital;
    }

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(VistaTipoCapital.class);
		
		if(FiltroUtils.noEstaVacio(this.codplan)) criteria.add(Restrictions.eq("id.codplan", this.codplan));
		if(FiltroUtils.noEstaVacio(this.codlinea)) criteria.add(Restrictions.eq("id.codlinea", this.codlinea));
		// Si el modulo esta informado y no es todos los modulos (99999)
		if(FiltroUtils.noEstaVacio(this.codmodulo) && !Constants.TODOS_MODULOS.equals(this.codmodulo)) {
			criteria.add(Restrictions.eq("id.codmodulo", this.codmodulo));
		}
		// Si el cultivo esta informado y no es todos los cultivos (999)
		if(FiltroUtils.noEstaVacio(this.codcultivo) && !StringUtils.nullToString(this.codcultivo).equals(Constants.TODOS_CULTIVOS)) {
			criteria.add(Restrictions.eq("id.codcultivo", this.codcultivo));
		}
		if(!StringUtils.nullToString(this.codtipocapital).equals("")) 
			criteria.add(Restrictions.eq("id.codtipocapital", this.codtipocapital));
		
		return criteria;
	}

	public BigDecimal getCodplan() {
		return codplan;
	}

	public void setCodplan(BigDecimal codplan) {
		this.codplan = codplan;
	}

	public BigDecimal getCodlinea() {
		return codlinea;
	}

	public void setCodlinea(BigDecimal codlinea) {
		this.codlinea = codlinea;
	}

	public String getCodmodulo() {
		return codmodulo;
	}

	public void setCodmodulo(String codmodulo) {
		this.codmodulo = codmodulo;
	}

	public BigDecimal getCodcultivo() {
		return codcultivo;
	}

	public void setCodcultivo(BigDecimal codcultivo) {
		this.codcultivo = codcultivo;
	}

	public BigDecimal getCodtipocapital() {
		return codtipocapital;
	}

	public void setCodtipocapital(BigDecimal codtipocapital) {
		this.codtipocapital = codtipocapital;
	}

}
