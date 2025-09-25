package com.rsi.agp.dao.filters.poliza;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.Asegurado;
import com.rsi.agp.dao.tables.admin.Socio;
import com.rsi.agp.dao.tables.cpl.SubvencionEnesa;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.SubvencionSocio;


public class SocioSubvencionFiltro implements Filter {

	private Socio socio;
	private SubvencionEnesa subvEnesa;
	private Poliza poliza;
	private Asegurado asegurado;

	public SocioSubvencionFiltro()
	{ }
	
	public SocioSubvencionFiltro(final Socio socio) {
		this.socio = socio;
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(SubvencionSocio.class);

		if (FiltroUtils.noEstaVacio(asegurado))
		{
			criteria.add(Restrictions.allEq(getMapaAseguradoSubvencion()));
		}
		if (FiltroUtils.noEstaVacio(socio)) 
		{
			criteria.add(Restrictions.allEq(getMapaSocioSubvencion()));
		}
		if (FiltroUtils.noEstaVacio(subvEnesa)) 
		{
			criteria.add(Restrictions.eq("subvencionEnesa.id.codtiposubvenesa", subvEnesa.getTipoSubvencionEnesa().getCodtiposubvenesa()));
		}
		if (FiltroUtils.noEstaVacio(poliza))
		{
			criteria.add(Restrictions.eq("poliza.idpoliza", poliza.getIdpoliza()));
		}

		return criteria;
	}

	private final Map<String, Object> getMapaSocioSubvencion() {
		final Map<String, Object> mapa = new HashMap<String, Object>();

		final String nif = socio.getId().getNif();
		if (FiltroUtils.noEstaVacio(nif)) {
			mapa.put("socio.id.nif", nif);
		}

		final Long idAsegurado = socio.getId().getIdasegurado();
		if (FiltroUtils.noEstaVacio(idAsegurado)) {
			mapa.put("socio.id.idasegurado", idAsegurado);
		}

		return mapa;
	}
	
	private Map<String, Object> getMapaAseguradoSubvencion() {
		Map<String, Object> mapa = new HashMap<String, Object>();
				
		if (asegurado.getId() != null)
		{
			mapa.put("socio.asegurado.id",asegurado.getId());
		}
		
		return mapa;
	}

	public Socio getSocio() {
		return socio;
	}

	public void setSocio(Socio socio) {
		this.socio = socio;
	}

	public SubvencionEnesa getSubvEnesa() {
		return subvEnesa;
	}

	public void setSubvEnesa(SubvencionEnesa subvEnesa) {
		this.subvEnesa = subvEnesa;
	}

	public Poliza getPoliza() {
		return poliza;
	}

	public void setPoliza(Poliza poliza) {
		this.poliza = poliza;
	}

	public Asegurado getAsegurado() {
		return asegurado;
	}

	public void setAsegurado(Asegurado asegurado) {
		this.asegurado = asegurado;
	}
}
