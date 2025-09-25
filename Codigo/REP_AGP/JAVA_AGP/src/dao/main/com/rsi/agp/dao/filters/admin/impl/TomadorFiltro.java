package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.Tomador;


public class TomadorFiltro implements Filter {

	private Tomador tomador;
	private Integer posicion;
	private String filtro;
	private BigDecimal codEntidad;

	public TomadorFiltro(final Tomador tomador) {
		this.tomador = tomador;
	}

	public TomadorFiltro(final String filtro, final BigDecimal codEntidad) {
		this.filtro = filtro;
		this.codEntidad = codEntidad;
	}

	public TomadorFiltro(final Integer posicion, final String filtro, final BigDecimal codEntidad) {
		this.posicion = posicion;
		this.filtro = filtro;
		this.codEntidad = codEntidad;
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(Tomador.class);

		//Los codEntidad deben estar ordenados
		criteria.addOrder(Order.asc("id.codentidad"));
		
		if (tomador != null) {
			criteria.createAlias("localidad", "loc", CriteriaSpecification.LEFT_JOIN);
			criteria.add(Restrictions.allEq(getMapaTomador()));
		}
		if (FiltroUtils.noEstaVacio(posicion)) {
			criteria.addOrder(Order.asc("id.ciftomador"));
			criteria.setFirstResult(posicion);
			criteria.setMaxResults(FiltroUtils.getMaxVisoresResults());
		}
		if (FiltroUtils.noEstaVacio(filtro)) {
			criteria.add(Restrictions.ilike("razonsocial", "%" + filtro + "%"));
		}
		if (FiltroUtils.noEstaVacio(codEntidad)) {
			criteria.add(Restrictions.eq("id.codentidad", codEntidad));
		}

		return criteria;
	}

	private final Map<String, Object> getMapaTomador() {
		final Map<String, Object> mapa = new HashMap<String, Object>();

		final BigDecimal codEntidad = tomador.getId().getCodentidad();
		if (FiltroUtils.noEstaVacio(codEntidad)) {
			mapa.put("id.codentidad", codEntidad);
		}

		final String cif = tomador.getId().getCiftomador();
		if (FiltroUtils.noEstaVacio(cif)) {
			mapa.put("id.ciftomador", cif);
		}

		final String razonSocial = tomador.getRazonsocial();
		if (FiltroUtils.noEstaVacio(razonSocial)) {
			mapa.put("razonsocial", razonSocial);
		}

		final BigDecimal codProvincia = tomador.getLocalidad().getId().getCodprovincia();
		if (FiltroUtils.noEstaVacio(codProvincia)) {
			mapa.put("loc.id.codprovincia", codProvincia);
		}

		final String nomLocalidad = tomador.getLocalidad().getNomlocalidad();
		if (FiltroUtils.noEstaVacio(nomLocalidad)) {
			mapa.put("loc.nomlocalidad", nomLocalidad);
		}

		final BigDecimal cp = tomador.getCodpostal();
		if (FiltroUtils.noEstaVacio(cp)) {
			mapa.put("codpostal", cp);
		}

		final String telefono = tomador.getTelefono();
		if (FiltroUtils.noEstaVacio(telefono)) {
			mapa.put("telefono", telefono);
		}

		final String numVia = tomador.getNumvia();
		if (FiltroUtils.noEstaVacio(numVia)) {
			mapa.put("numvia", numVia);
		}

		final String movil = tomador.getMovil();
		if (FiltroUtils.noEstaVacio(movil)) {
			mapa.put("movil", movil);
		}

		final String email = tomador.getEmail();
		if (FiltroUtils.noEstaVacio(email)) {
			mapa.put("email", email);
		}
		
		final String email2 = tomador.getEmail2();
		if (FiltroUtils.noEstaVacio(email2)) {
			mapa.put("email2", email2);
		}
		
		final String email3 = tomador.getEmail3();
		if (FiltroUtils.noEstaVacio(email3)) {
			mapa.put("email3", email3);
		}

		final String piso = tomador.getPiso();
		if (FiltroUtils.noEstaVacio(piso)) {
			mapa.put("piso", piso);
		}

		final String bloque = tomador.getBloque();
		if (FiltroUtils.noEstaVacio(bloque)) {
			mapa.put("bloque", bloque);
		}

		final String escalera = tomador.getEscalera();
		if (FiltroUtils.noEstaVacio(escalera)) {
			mapa.put("escalera", escalera);
		}

		final String clave = tomador.getVia().getClave();
		if (FiltroUtils.noEstaVacio(clave)) {
			mapa.put("via.clave", clave);
		}

		final String domicilio = tomador.getDomicilio();
		if (FiltroUtils.noEstaVacio(domicilio)) {
			mapa.put("domicilio", domicilio);
		}

		final BigDecimal codLocalidad = tomador.getLocalidad().getId().getCodlocalidad();
		if (FiltroUtils.noEstaVacio(codLocalidad)) {
			mapa.put("loc.id.codlocalidad", codLocalidad);
		}

		final String sublocalidad = tomador.getLocalidad().getId().getSublocalidad();
		if (FiltroUtils.noEstaVacio(sublocalidad)) {
			mapa.put("loc.id.sublocalidad", sublocalidad);
		}

		return mapa;
	}
}
