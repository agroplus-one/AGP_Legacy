package com.rsi.agp.dao.filters.commons;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.poliza.Linea;

public class LineasFiltro implements Filter {

	private BigDecimal codPlan;
	private BigDecimal codLinea;
	private Long lineaSeguroId;
	private Integer posicion;
	private String filtro;
	private String estado;
	private String activado;
	private Date fxActivacion;

	public LineasFiltro () {
		super();
	}

	public LineasFiltro(final String filtro, final BigDecimal codPlan) {
		this.filtro = filtro;
		this.codPlan = codPlan;
	}

	public LineasFiltro(final Integer posicion, final String filtro, final BigDecimal codPlan) {
		this.posicion = posicion;
		this.filtro = filtro;
		this.codPlan = codPlan;
	}

	public LineasFiltro(BigDecimal codPlan, BigDecimal codLinea) {
		this.codPlan = codPlan;
		this.codLinea = codLinea;
	}

	public BigDecimal getCodPlan() {
		return codPlan;
	}

	public void setCodPlan(BigDecimal codPlan) {
		this.codPlan = codPlan;
	}


	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(Linea.class);

		if (FiltroUtils.noEstaVacio(this.codPlan)) {
			criteria.add(Restrictions.eq("codplan",this.codPlan));
		}
		if (FiltroUtils.noEstaVacio(this.codLinea)) {
			criteria.add(Restrictions.eq("codlinea",this.codLinea));
		}
		if (FiltroUtils.noEstaVacio(this.lineaSeguroId))
		{
			criteria.add(Restrictions.eq("lineaseguroid", this.lineaSeguroId));
		}
		if (FiltroUtils.noEstaVacio(this.estado))
		{
			criteria.add(Restrictions.eq("estado", this.estado));
		}
		if (FiltroUtils.noEstaVacio(this.activado))
		{
			criteria.add(Restrictions.eq("activo", this.activado));
		}
		if (FiltroUtils.noEstaVacio(this.getFxActivacion()))
		{
			criteria.add(Restrictions.eq("fechaactivacion", this.getFxActivacion()));
		}
		if (FiltroUtils.noEstaVacio(posicion)) {
			criteria.addOrder(Order.asc("nomlinea"));
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}
		if (FiltroUtils.noEstaVacio(filtro)) {
			criteria.add(Restrictions.ilike("nomlinea", "%" + filtro + "%"));
		}
		
		criteria.addOrder(Order.asc("codlinea"));
		
		return criteria;
	}

	public BigDecimal getCodLinea() {
		return codLinea;
	}

	public void setCodLinea(BigDecimal codLinea) {
		this.codLinea = codLinea;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getActivado() {
		return activado;
	}

	public void setActivado(String activado) {
		this.activado = activado;
	}

	public Date getFxActivacion() {
		return fxActivacion;
	}

	public void setFxActivacion(Date fxActivacion) {
		this.fxActivacion = fxActivacion;
	}

	public Long getLineaSeguroId() {
		return lineaSeguroId;
	}

	public void setLineaSeguroId(Long lineaSeguroId) {
		this.lineaSeguroId = lineaSeguroId;
	}
}
