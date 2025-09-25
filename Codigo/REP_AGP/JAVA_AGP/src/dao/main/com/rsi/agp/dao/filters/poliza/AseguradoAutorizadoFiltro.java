package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.core.webapp.util.StringUtils;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cpl.AseguradoAutorizado;

public class AseguradoAutorizadoFiltro implements Filter{

	private Long lineaseguroid;
	private String nifasegurado;
	private BigDecimal codcultivo;
	private BigDecimal codvariedad;
	private BigDecimal codgarantizado;
	
	public AseguradoAutorizadoFiltro() {
		super();
	}
	
	public AseguradoAutorizadoFiltro(Long lineaseguroid,String codmodulo,String nifasegurado,BigDecimal codcultivo,BigDecimal codvariedad,BigDecimal codgarantizado){
		this.lineaseguroid=lineaseguroid;
		this.nifasegurado=nifasegurado;
		this.codcultivo=codcultivo;
		this.codvariedad=codvariedad;
		this.codgarantizado=codgarantizado;	
	}
	
	public AseguradoAutorizadoFiltro(Long lineaseguroid, String nifasegurado){
		this.lineaseguroid = lineaseguroid;
		this.nifasegurado = nifasegurado;
		this.codcultivo = null;
		this.codvariedad = null;
		this.codgarantizado = null;	
	}
	
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(AseguradoAutorizado.class);
		
		if (this.lineaseguroid != null)
		{
			Criterion crit = Restrictions.eq("id.lineaseguroid", this.lineaseguroid);
			criteria.add(crit);
		}
		if (this.nifasegurado != null && !"".equalsIgnoreCase(this.nifasegurado))
		{
			Criterion crit = Restrictions.eq("nifasegurado", this.nifasegurado);
			criteria.add(crit);
		}
		if (this.codcultivo != null)
		{
			Criterion crit = Restrictions.eq("variedad.id.codcultivo", this.codcultivo);
			criteria.add(crit);
		}
		if (this.codvariedad != null)
		{
			Criterion crit = Restrictions.eq("variedad.id.codvariedad", this.codvariedad);
			criteria.add(crit);
		}
		if (this.codgarantizado != null)
		{
			Criterion crit = Restrictions.eq("codgarantizado", this.codgarantizado);
			criteria.add(crit);
		}
		
		criteria.add(Restrictions.allEq(getAutorizados()));
		
        return criteria;
	}
	
	private final Map<String, Object> getAutorizados() {

		final Map<String, Object> mapa = new HashMap<String, Object>();

		/* PROPIEDADES DE MODULO */
		if (FiltroUtils.noEstaVacio(this.lineaseguroid)) {
			mapa.put("id.lineaseguroid", this.lineaseguroid);
		}
		
		if (FiltroUtils.noEstaVacio(this.nifasegurado)) {
			mapa.put("nifasegurado", this.nifasegurado);
		}		
		
		/* PROPIEDADES DE PARCELA */
		if (FiltroUtils.noEstaVacio(this.codcultivo)) {
			mapa.put("id.codcultivo", this.codcultivo);
		}
		
		//COMENTADO POR ASF: LA VARIEDAD SI PUEDE SER '0' Y EL METOD DE FILTROUTILS NO LO ACEPTA!!!
		//if(FiltroUtils.noEstaVacio(this.codvariedad)){
		if(!StringUtils.nullToString(this.codvariedad).equals("")){
			mapa.put("id.codvariedad", this.codvariedad);
		}
		
		/* PROPIEDADES DE GRUPO TASA RIESGO */
		if (FiltroUtils.noEstaVacio(this.codgarantizado)) {
			mapa.put("id.codgarantizado", this.codgarantizado);
		}
		
		return mapa;
	}

	public Long getLineaseguroid() {
		return lineaseguroid;
	}

	public void setLineaseguroid(Long lineaseguroid) {
		this.lineaseguroid = lineaseguroid;
	}

	public String getNifasegurado() {
		return nifasegurado;
	}

	public void setNifasegurado(String nifasegurado) {
		this.nifasegurado = nifasegurado;
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

	public BigDecimal getCodgarantizado() {
		return codgarantizado;
	}

	public void setCodgarantizado(BigDecimal codgarantizado) {
		this.codgarantizado = codgarantizado;
	}	
	
}
