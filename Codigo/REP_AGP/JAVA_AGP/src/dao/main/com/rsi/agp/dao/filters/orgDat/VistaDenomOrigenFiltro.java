package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.orgDat.VistaDenomOrigen;

public class VistaDenomOrigenFiltro implements Filter 
{
	private Long lineaSeguroId;
	private String codmodulo;
    private BigDecimal codcultivo;
    private BigDecimal codvariedad;
    private BigDecimal codprovincia;
    private BigDecimal codcomarca;
    private BigDecimal codtermino;
    private Character subtermino;
    private BigDecimal codtipocapital;

    public VistaDenomOrigenFiltro(){}
    
	public VistaDenomOrigenFiltro(Long lineaSeguroId, String codmodulo,
			BigDecimal codcultivo, BigDecimal codvariedad,
			BigDecimal codprovincia, BigDecimal codcomarca,
			BigDecimal codtermino, Character subtermino,BigDecimal codtipocapital) {
		super();
		this.lineaSeguroId = lineaSeguroId;
		this.codmodulo = codmodulo;
		this.codcultivo = codcultivo;
		this.codvariedad = codvariedad;
		this.codprovincia = codprovincia;
		this.codcomarca = codcomarca;
		this.codtermino = codtermino;
		this.subtermino = subtermino;
		this.codtipocapital = codtipocapital;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(VistaDenomOrigen.class);
		criteria.add(getDenomOrigen());
		
		return criteria;		
	}
	
	private final Conjunction getDenomOrigen() {

		Conjunction c = Restrictions.conjunction();
		
		c.add(Restrictions.eq("codtipocapital", this.codtipocapital));
		
		/* PROPIEDADES DE MODULO */
		if (FiltroUtils.noEstaVacio(this.lineaSeguroId)) {
			c.add(Restrictions.eq("id.lineaseguroid", this.lineaSeguroId));
		}
	
		if (FiltroUtils.noEstaVacio(this.codmodulo)) {
			StringTokenizer tokens = new StringTokenizer(this.codmodulo,";");
			
			Disjunction dd = Restrictions.disjunction();
			
		    while(tokens.hasMoreTokens())
		    { 
		    	dd.add(Restrictions.eq("id.codmodulo",new String(tokens.nextToken())));
		    }
		    c.add(dd);
		}
		
		/* PROPIEDADES DE VARIEDAD */
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

		/* PROPIEDADES DE TERMINO */
		if(FiltroUtils.noEstaVacio(this.codprovincia)){
			c.add (				
					Restrictions.disjunction()
					.add(Restrictions.eq("codprovincia", this.codprovincia))
					.add(Restrictions.eq("codprovincia", new BigDecimal("99")))
			
			);
		}
		if(FiltroUtils.noEstaVacio(this.codcomarca)){
			c.add (
					
					Restrictions.disjunction()
					.add(Restrictions.eq("codcomarca", this.codcomarca))
					.add(Restrictions.eq("codcomarca", new BigDecimal("99")))
			);
		}
		if(FiltroUtils.noEstaVacio(this.codtermino)){
			c.add (
					
					Restrictions.disjunction()
					.add(Restrictions.eq("codtermino", this.codtermino))
					.add(Restrictions.eq("codtermino", new BigDecimal("999")))
			
			);
		}
		if(FiltroUtils.noEstaVacio(this.subtermino)){
			c.add (
					
					Restrictions.disjunction()
					.add(Restrictions.eq("subtermino", this.subtermino))
					.add(Restrictions.eq("subtermino", new Character('9')))
			
			);
		}

		return c;
	}

	public Long getLineaSeguroId() {
		return lineaSeguroId;
	}

	public void setLineaSeguroId(Long lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
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

	public BigDecimal getCodprovincia() {
		return codprovincia;
	}

	public void setCodprovincia(BigDecimal codprovincia) {
		this.codprovincia = codprovincia;
	}

	public BigDecimal getCodcomarca() {
		return codcomarca;
	}

	public void setCodcomarca(BigDecimal codcomarca) {
		this.codcomarca = codcomarca;
	}

	public BigDecimal getCodtermino() {
		return codtermino;
	}

	public void setCodtermino(BigDecimal codtermino) {
		this.codtermino = codtermino;
	}

	public Character getSubtermino() {
		return subtermino;
	}

	public void setSubtermino(Character subtermino) {
		this.subtermino = subtermino;
	}

	public BigDecimal getCodtipocapital() {
		return codtipocapital;
	}

	public void setCodtipocapital(BigDecimal codtipocapital) {
		this.codtipocapital = codtipocapital;
	}

}
