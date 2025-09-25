package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.orgDat.VistaCertificadoEstructura;

public class VistaCertificadoEstructuraFiltro implements Filter {
	
    Long lineaseguroid;
    private String codmodulo;
    private BigDecimal codcultivo;
    private BigDecimal codvariedad;
    private BigDecimal codcertificadoinstal;
    private String descripcion;
    private BigDecimal codtipocapital;
    
    
    public VistaCertificadoEstructuraFiltro() {
    	super();
    }

    
    @Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(VistaCertificadoEstructura.class);
		criteria.add(getMaterialEstructura());
		
		return criteria;		
	}
	
	private final Conjunction getMaterialEstructura() {
		Conjunction c = Restrictions.conjunction();

		if (FiltroUtils.noEstaVacio(this.lineaseguroid)) {
			c.add(Restrictions.eq("id.lineaseguroid", new BigDecimal(this.lineaseguroid.toString())));
		}

		
        /*
		if (FiltroUtils.noEstaVacio(this.codmodulo)) {
			StringTokenizer tokens = new StringTokenizer(this.codmodulo,";");
		
		Disjunction dd = Restrictions.disjunction();
		
		while(tokens.hasMoreTokens())
		{ 
			dd.add(Restrictions.eq("id.codmodulo",new String(tokens.nextToken())));
			}
			c.add(dd);
		}
		
		*/
		
		if (this.codtipocapital != null)
		{
			c.add(
		    Restrictions.disjunction()		
				.add(Restrictions.eq("id.codtipocapital", this.codtipocapital))
				.add(Restrictions.eq("id.codtipocapital", new BigDecimal("999")))
			);
		}
		
		if(FiltroUtils.noEstaVacio(this.codcultivo)){
			c.add(				
			Restrictions.disjunction()
			.add(Restrictions.eq("id.codcultivo", this.codcultivo))
			.add(Restrictions.eq("id.codcultivo", new BigDecimal("999")))
			
			);
		}
		
		//COMENTADO POR ASF: LA VARIEDAD SI PUEDE SER '0' Y EL METOD DE FILTROUTILS NO LO ACEPTA!!!
		//if(FiltroUtils.noEstaVacio(this.codvariedad)){
		if(!StringUtils.nullToString(this.codvariedad).equals("")){
			c.add (
			Restrictions.disjunction()
			.add(Restrictions.eq("id.codvariedad", this.codvariedad))
			.add(Restrictions.eq("id.codvariedad", new BigDecimal("999")))
			
			);
		}

		return c;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}


	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
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


	public BigDecimal getCodvariedad() {
		return codvariedad;
	}


	public void setCodvariedad(BigDecimal codvariedad) {
		this.codvariedad = codvariedad;
	}


	public BigDecimal getCodcertificadoinstal() {
		return codcertificadoinstal;
	}


	public void setCodcertificadoinstal(BigDecimal codcertificadoinstal) {
		this.codcertificadoinstal = codcertificadoinstal;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public BigDecimal getCodtipocapital() {
		return codtipocapital;
	}


	public void setCodtipocapital(BigDecimal codtipocapital) {
		this.codtipocapital = codtipocapital;
	}
	
	
}
