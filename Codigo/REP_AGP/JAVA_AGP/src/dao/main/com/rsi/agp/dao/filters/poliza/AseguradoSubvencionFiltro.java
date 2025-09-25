package com.rsi.agp.dao.filters.poliza;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.rsi.agp.dao.filters.Filter;


public class AseguradoSubvencionFiltro implements Filter {

//	private AseguradoSubvencion aseguradoSubvencion;
//
//	public AseguradoSubvencionFiltro(final AseguradoSubvencion aseguradoSubvencion) {
//		this.aseguradoSubvencion = aseguradoSubvencion;
//	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		return null;
//		final Criteria criteria = sesion.createCriteria(AseguradoSubvencion.class);
//
//		if (FiltroUtils.noEstaVacio(aseguradoSubvencion)) {
//			criteria.add(Restrictions.allEq(getMapaAseguradoSubvencion()));
//			criteria.addOrder(Order.asc("subvencion.idsubvencion"));
//		}
//
//		return criteria;
	}

//	private final Map<String, Object> getMapaAseguradoSubvencion() {
//		final Map<String, Object> mapa = new HashMap<String, Object>();
//
//		final BigDecimal codEntidad = aseguradoSubvencion.getAsegurado().getId().getCodentidad();
//		if (FiltroUtils.noEstaVacio(codEntidad)) {
//			mapa.put("asegurado.id.codentidad", codEntidad);
//		}
//
//		final String discriminante = aseguradoSubvencion.getAsegurado().getId().getDiscriminante();
//		if (FiltroUtils.noEstaVacio(discriminante)) {
//			mapa.put("asegurado.id.discriminante", discriminante);
//		}
//
//		final String nifcif = aseguradoSubvencion.getAsegurado().getId().getNifcif();
//		if (FiltroUtils.noEstaVacio(nifcif)) {
//			mapa.put("asegurado.id.nifcif", nifcif);
//		}
//
//		return mapa;
//	}
}
