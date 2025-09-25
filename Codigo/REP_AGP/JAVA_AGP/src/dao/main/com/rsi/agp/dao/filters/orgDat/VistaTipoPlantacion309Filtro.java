package com.rsi.agp.dao.filters.orgDat;

import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.orgDat.VistaTipoPlantacion309;

public class VistaTipoPlantacion309Filtro implements Filter{

	private Long lineaSeguroId;
	private String codmodulo;
    private BigDecimal codcultivo;
    private BigDecimal codvariedad;
    private BigDecimal codtipocapital;
    private List<BigDecimal> listCultivos;
    private List<BigDecimal> listVariedades;
    private BigDecimal sistemaCultivo;
    
    
	public VistaTipoPlantacion309Filtro() {		
	}

	public VistaTipoPlantacion309Filtro(Long lineaSeguroId, String codmodulo,
			BigDecimal codcultivo, BigDecimal codvariedad,
			BigDecimal codprovincia, BigDecimal codcomarca,
			BigDecimal codtermino, Character subtermino, BigDecimal codtipocapital) {
		
		this.lineaSeguroId = lineaSeguroId;
		this.codmodulo = codmodulo;
		this.codcultivo = codcultivo;
		this.codvariedad = codvariedad;
		this.codtipocapital = codtipocapital;
	}
	
	public void setListas(  List<BigDecimal> listCultivos, List<BigDecimal> listVariedades){
		this.listCultivos = listCultivos;
		this.listVariedades = listVariedades;
	}



	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(VistaTipoPlantacion309.class);
		criteria.add(getTipoPlantacion());
		
		return criteria;
	}
	
	private final Conjunction getTipoPlantacion() {
		Conjunction c = Restrictions.conjunction();
		
		c.add(
				Restrictions.disjunction()
				.add(Restrictions.eq("codtipocapital",  this.codtipocapital))
				.add(Restrictions.isNull("codtipocapital")));
		
		/* PROPIEDADES DE MODULO */
		if (FiltroUtils.noEstaVacio(this.lineaSeguroId)) {
			c.add(Restrictions.eq("id.lineaseguroid", new BigDecimal(this.lineaSeguroId)));
		}
		
		
		if (FiltroUtils.noEstaVacio(this.sistemaCultivo)) {
			c.add(Restrictions.eq("id.codsistemacultivo", this.sistemaCultivo));
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

		
		

		if(listCultivos != null && !listCultivos.contains(new BigDecimal("999"))){
			if(listCultivos.size() > 0){
				listCultivos.add(new BigDecimal("999"));
			}
			c.add(Restrictions.in("id.codcultivo", listCultivos));
		}
		if(FiltroUtils.noEstaVacio(this.codcultivo) && !listCultivos.contains(new BigDecimal("999"))){
			c.add(				
					Restrictions.disjunction()
					.add(Restrictions.eq("id.codcultivo", this.codcultivo))
					.add(Restrictions.eq("id.codcultivo", new BigDecimal("999")))
				);
		}
		
		if(listVariedades != null && !listVariedades.contains(new BigDecimal("999"))){
			if(listVariedades.size() > 0){
				listVariedades.add(new BigDecimal("999"));
			}
			c.add(Restrictions.in("id.codvariedad", listVariedades));
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

	public BigDecimal getCodtipocapital() {
		return codtipocapital;
	}

	public void setCodtipocapital(BigDecimal codtipocapital) {
		this.codtipocapital = codtipocapital;
	}

	public BigDecimal getSistemaCultivo() {
		return sistemaCultivo;
	}

	public void setSistemaCultivo(BigDecimal sistemaCultivo) {
		this.sistemaCultivo = sistemaCultivo;
	}

	
}
