package com.rsi.agp.dao.filters.log;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.log.TipoImportacion;

public class TipoImportacionFilter implements Filter {
	private Long idTipoImportacion;
	private String descripcion;
	private String ubicacionRaiz;
	
	public Criteria getCriteria (Session sesion)
	{
		Criteria criteria = sesion.createCriteria(TipoImportacion.class);
		
		if (this.idTipoImportacion != null)
		{
			Criterion crit = Restrictions.eq("idTipoImportacion", this.idTipoImportacion);
			criteria.add(crit);		
		}
		
		if (this.descripcion != null && !this.descripcion.equalsIgnoreCase(""))
		{
			Criterion crit = Restrictions.eq("descripcion", this.descripcion);
			criteria.add(crit);
		}
		
		if (this.ubicacionRaiz != null && !this.ubicacionRaiz.equalsIgnoreCase(""))
		{
			Criterion crit = Restrictions.eq("ubicacionraiz", this.ubicacionRaiz);
			criteria.add(crit);
		}
		
		return criteria;
	}

	public Long getIdTipoImportacion() {
		return idTipoImportacion;
	}

	public void setIdTipoImportacion(Long idTipoImportacion) {
		this.idTipoImportacion = idTipoImportacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getUbicacionRaiz() {
		return ubicacionRaiz;
	}

	public void setUbicacionRaiz(String ubicacionRaiz) {
		this.ubicacionRaiz = ubicacionRaiz;
	}

}
