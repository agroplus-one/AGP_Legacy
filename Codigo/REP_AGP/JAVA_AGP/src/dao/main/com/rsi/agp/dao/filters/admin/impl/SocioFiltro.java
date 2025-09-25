package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;


public class SocioFiltro implements Filter {

	private Socio socio;
	private Asegurado asegurado;

	public SocioFiltro(final Socio socio) {
		this.socio = socio;
	}

	public SocioFiltro(final Asegurado asegurado) {
		this.asegurado = asegurado;
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(Socio.class);

		if (FiltroUtils.noEstaVacio(socio)) {
			criteria.add(Restrictions.allEq(getMapaSocio()));		
			
			//DAA 17/05/2013
			if(FiltroUtils.noEstaVacio(socio.getBaja())&& (socio.getBaja()).equals('N')){
				criteria.add(Restrictions.disjunction().add(Restrictions.eq("baja",'N')).add(Restrictions.isNull("baja")));

			}
		}
		
		if (FiltroUtils.noEstaVacio(asegurado)) {
			final Long idAsegurado = asegurado.getId();
			if (FiltroUtils.noEstaVacio(idAsegurado)) 
			{
				Criterion crit = Restrictions.eq("id.idasegurado", idAsegurado);
				criteria.add(crit);
			}
		}	
		criteria.addOrder(Order.asc("nombre"));
		return criteria;
	}

	private final Map<String, Object> getMapaSocio() {
		final Map<String, Object> mapa = new HashMap<String, Object>();

		if (FiltroUtils.noEstaVacio(socio)) {
			final String nif = socio.getId().getNif();
			if (FiltroUtils.noEstaVacio(nif)) {
				mapa.put("id.nif", nif);
			}

			final Long idAsegurado = socio.getId().getIdasegurado();
			if (FiltroUtils.noEstaVacio(idAsegurado)) {
				mapa.put("id.idasegurado", idAsegurado);
			}
	
			final String nombre = socio.getNombre();
			if (FiltroUtils.noEstaVacio(nombre)) {
				mapa.put("nombre", nombre);
			}
	
			final String apellido1 = socio.getApellido1();
			if (FiltroUtils.noEstaVacio(apellido1)) {
				mapa.put("apellido1", apellido1);
			}
	
			final String apellido2 = socio.getApellido2();
			if (FiltroUtils.noEstaVacio(apellido2)) {
				mapa.put("apellido2", apellido2);
			}
	
			final String razonSocial = socio.getRazonsocial();
			if (FiltroUtils.noEstaVacio(razonSocial)) {
				mapa.put("razonsocial", razonSocial);
			}
	
			final BigDecimal regimenSocial = socio.getRegimensegsocial();
			
			if (null != regimenSocial) {
				mapa.put("regimensegsocial", regimenSocial);
			}
	
			final String numss = socio.getNumsegsocial();
			if (FiltroUtils.noEstaVacio(numss)) {
				mapa.put("numsegsocial", numss);
			}
			final String atp = socio.getAtp();
			if (FiltroUtils.noEstaVacio(atp)) {
				mapa.put("atp", atp);
			}
	
			final Character joven = socio.getJovenagricultor();
			if (FiltroUtils.noEstaVacio(joven)) {
				mapa.put("jovenagricultor", joven);
			}
			final String tipoidentificacion = socio.getTipoidentificacion();
			if (FiltroUtils.noEstaVacio(tipoidentificacion)) {
				mapa.put("tipoidentificacion", tipoidentificacion);
			}
		}
		return mapa;
	}
}
