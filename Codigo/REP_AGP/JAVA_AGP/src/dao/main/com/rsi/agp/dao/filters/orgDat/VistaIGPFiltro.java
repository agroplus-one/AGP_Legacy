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
import com.rsi.agp.dao.tables.orgDat.VistaIGP;

public class VistaIGPFiltro implements Filter{

	private Long lineaSeguroId;
	private String codmodulo;
    private BigDecimal codcultivo;
    private BigDecimal codvariedad;
    private BigDecimal codprovincia;
    private List<BigDecimal> listProvincias;
    
	public VistaIGPFiltro() {
	}

	public VistaIGPFiltro(Long lineaSeguroId, String codmodulo,
			BigDecimal codcultivo, BigDecimal codvariedad,
			BigDecimal codprovincia) {
		this.lineaSeguroId = lineaSeguroId;
		this.codmodulo = codmodulo;
		this.codcultivo = codcultivo;
		this.codvariedad = codvariedad;
		this.codprovincia = codprovincia;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(VistaIGP.class);
		criteria.add(getIGP());
		
		return criteria;
	}
	
	public void setListas(List<BigDecimal> listProvincias){
		this.listProvincias = listProvincias;
	}
	
	private final Conjunction getIGP() {
		Conjunction c = Restrictions.conjunction();
		
		/* PROPIEDADES DE MODULO */
		if (FiltroUtils.noEstaVacio(this.lineaSeguroId)) {
			c.add(Restrictions.eq("id.lineaseguroid", new BigDecimal(this.lineaSeguroId)));
		}
	
		if (FiltroUtils.noEstaVacio(this.codmodulo)) {
			StringTokenizer tokens = new StringTokenizer(this.codmodulo,";");
			
			Disjunction dd = Restrictions.disjunction();
			
		    while(tokens.hasMoreTokens())
		    { 
		    	dd.add(Restrictions.eq("id.codmodulo",new String(tokens.nextToken())));
		    }
//		    Añadimos modulo generico
		    dd.add(Restrictions.eq("id.codmodulo","99999"));
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

		
		
		
		if(listProvincias != null && !listProvincias.contains(new BigDecimal("99"))){
			if(listProvincias.size() > 0){
				listProvincias.add(new BigDecimal("99"));
			}
			c.add(Restrictions.disjunction()
				.add(Restrictions.in("id.codprovincia", listProvincias))
				.add(Restrictions.isNull("id.codprovincia"))
			);
			
		}
		if(FiltroUtils.noEstaVacio(this.codprovincia)){
			c.add (				
				Restrictions.disjunction()
				.add(Restrictions.eq("id.codprovincia", this.codprovincia))
				.add(Restrictions.eq("id.codprovincia", new BigDecimal("99")))
				.add(Restrictions.isNull("id.codprovincia"))
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

	
}
