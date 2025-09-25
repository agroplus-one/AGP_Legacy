package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.commons.Termino;

public class TerminoFiltro1 implements Filter{
	
	private BigDecimal codProvincia;
	private BigDecimal codComarca;
	private BigDecimal codTermino;
	private String subtermino; // 1 carácter
	
	private Integer posicion;
	private String filtro;	
	private boolean soloNumero;
	
	public TerminoFiltro1() {
		super();
	}

	public TerminoFiltro1(BigDecimal codProvincia, BigDecimal codComarca, BigDecimal codTermino, 
			String subtermino){
		this.codProvincia  = codProvincia;
		this.codComarca  = codComarca;		
		this.codTermino    = codTermino;
		this.subtermino    = subtermino;
		this.filtro = "";
		this.soloNumero = true;
	}	
	
	public TerminoFiltro1(BigDecimal codProvincia, BigDecimal codComarca,
			Integer posicion, String filtro) {		
		this.codProvincia = codProvincia;
		this.codComarca = codComarca;
		this.posicion = posicion;
		this.filtro = filtro;
	}
	
	public TerminoFiltro1(BigDecimal codProvincia, BigDecimal codComarca
			, String filtro, boolean soloNumero) {		
		this.codProvincia = codProvincia;
		this.codComarca = codComarca;
		this.soloNumero= soloNumero;
		this.filtro = filtro;
	}
	

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(Termino.class);
		
		if(FiltroUtils.noEstaVacio(codProvincia) && !codProvincia.equals(new BigDecimal("0"))){
			criteria.add(Restrictions.eq("provincia.codprovincia",this.codProvincia));
		}
		
		if(FiltroUtils.noEstaVacio(codComarca) && !codComarca.equals(new BigDecimal("0"))){
			criteria.add(Restrictions.eq("comarca.id.codcomarca",this.codComarca));
		}
		
		if(this.codTermino != null){
			criteria.add(Restrictions.eq("id.codtermino",this.codTermino));
		}
		
		if(this.subtermino != null){
			criteria.add(Restrictions.eq("id.subtermino",this.subtermino));
		}
		if(!filtro.equals("")){		
			criteria.add(Restrictions.ilike("nomtermino", "%" + filtro + "%"));			
		}
		if (!soloNumero) {
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}
		criteria.addOrder(Order.asc("nomtermino"));
		
        return criteria;
	}
}