package com.rsi.agp.dao.filters.cgen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cgen.TipoSubvencionEnesa;

public class TipoSubvencionEnesaFiltro implements Filter
{
	private ArrayList<BigDecimal> codigosSubvenciones;
	private String tipoidentificacion;
	private BigDecimal nivelDeclaracion;
	private Character nivelDependencia;
	
	@Override
	public Criteria getCriteria(Session sesion) 
	{
		Criteria criteria = sesion.createCriteria(TipoSubvencionEnesa.class);
		
		if (this.codigosSubvenciones != null && this.codigosSubvenciones.size() > 0) {
			Criterion crit = Restrictions.in("codtiposubvenesa", this.getCodigosSubvenciones());
			criteria.add(crit);	
		}
		//Cogemos solo las declarables
		Criterion crit = Restrictions.eq("declarable", new Character('S'));
		criteria.add(crit);
		//Cogemos solo las NO excluyentes  --> Este campo lo han eliminado??
		/*
		crit = Restrictions.eq("subvexcluyente", new Character('N'));
		criteria.add(crit);
		*/
		//Cogemos solo las que sean de nivel de declaracion poliza
		if (nivelDeclaracion != null) {
			crit = Restrictions.eq("niveldeclaracion", nivelDeclaracion);
			criteria.add(crit);
		}
		//Segun el tipo de identificacion traemos diferentes niveles de dependencia
		if (tipoidentificacion != null) {
			if("NIF".equals(tipoidentificacion)) {
				crit = Restrictions.disjunction()
					.add(Restrictions.eq("niveldependencia", new Character('S'))
			    )
			    	.add(Restrictions.isNull("niveldependencia")
				);
					
			} else if("CIF".equals(tipoidentificacion)) {
				crit = Restrictions.disjunction()
				.add(Restrictions.eq("niveldependencia", new Character('J'))
				).add(Restrictions.isNull("niveldependencia"));
					
			}
			criteria.add(crit);	
		} else {
			//para nivel de dependencia
			if (nivelDependencia != null) {
				crit = Restrictions.eq("niveldependencia", nivelDependencia);
				criteria.add(crit);
			}
		}
		
		return criteria;
	}

	public ArrayList<BigDecimal> getCodigosSubvenciones() {
		return codigosSubvenciones;
	}

	public void setCodigosSubvenciones(ArrayList<BigDecimal> codigosSubvenciones) {
		this.codigosSubvenciones = codigosSubvenciones;
	}

	public String getTipoidentificacion() {
		return tipoidentificacion;
	}

	public void setTipoidentificacion(String tipoidentificacion) {
		this.tipoidentificacion = tipoidentificacion;
	}

	public BigDecimal getNivelDeclaracion() {
		return nivelDeclaracion;
	}

	public void setNivelDeclaracion(BigDecimal nivelDeclaracion) {
		this.nivelDeclaracion = nivelDeclaracion;
	}

	public Character getNivelDependencia() {
		return nivelDependencia;
	}

	public void setNivelDependencia(Character nivelDependencia) {
		this.nivelDependencia = nivelDependencia;
	}
	
}
