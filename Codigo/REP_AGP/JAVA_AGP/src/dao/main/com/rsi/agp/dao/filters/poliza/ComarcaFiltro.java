package com.rsi.agp.dao.filters.poliza;

import java.math.BigDecimal;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.poliza.Linea;
import com.rsi.agp.dao.tables.commons.Comarca;

public class ComarcaFiltro implements Filter{
	
	private Integer posicion;
	private String filtro;
	private BigDecimal codProvincia;
	private boolean soloNumero;
	
	public ComarcaFiltro(final BigDecimal codProvincia) {
		this.codProvincia=codProvincia;
		this.filtro = "";
		this.soloNumero = true;
	}
	
	public ComarcaFiltro(final Integer posicion, final String filtro, final BigDecimal codProvincia){
		this.posicion = posicion;
		this.filtro = filtro;
		this.codProvincia = codProvincia;
	}
	public ComarcaFiltro(final String filtro, final BigDecimal codProvincia,boolean soloNumero){
		this.filtro = filtro;
		this.codProvincia = codProvincia;
		this.soloNumero = soloNumero;
	}
	
	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(Comarca.class);
		
		criteria.addOrder(Order.asc("nomcomarca"));
		criteria.add(Restrictions.ne("id.codcomarca", new BigDecimal(99)));
		if(!filtro.equals("")){
			criteria.add(Restrictions.ilike("nomcomarca", "%" + filtro + "%"));			
		}		
		if(FiltroUtils.noEstaVacio(codProvincia) && !codProvincia.equals(new BigDecimal("0"))){
			 criteria.add(Restrictions.eq("provincia.codprovincia", codProvincia));
		}	 
		if (!soloNumero) {
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}
        return criteria;
	}
}
