package com.rsi.agp.dao.filters.cesp.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.cesp.PeriodoGarantiaCe;

public class PeriodoGarantiaFiltro implements Filter {

	private PeriodoGarantiaCe periodoGarantiaCe;
	
	public PeriodoGarantiaFiltro(final PeriodoGarantiaCe periodoGarantiaCe){
		this.periodoGarantiaCe = periodoGarantiaCe;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(PeriodoGarantiaCe.class);

		criteria.add(Restrictions.allEq(getMapaCriterios()));

		return criteria;
	}

	private final Map<String, Object> getMapaCriterios() {
		final Map<String, Object> mapa = new HashMap<String, Object>();

		final BigDecimal codLineaBase = periodoGarantiaCe.getLinea().getCodlinea();
		if (noEstaVacio(codLineaBase)) {
			mapa.put("codlinea", codLineaBase);
		}

		final BigDecimal codCultivo = periodoGarantiaCe.getCultivo().getId().getCodcultivo();
		if (noEstaVacio(codCultivo)) {
			mapa.put("codcultivo", codCultivo);
		}
		final Date fechaInicio = periodoGarantiaCe.getFechaini();
		if (noEstaVacio(fechaInicio)) {
			mapa.put("fechaini", fechaInicio);
		}
		final Date fechaFinal = periodoGarantiaCe.getFechafin();
		if (noEstaVacio(fechaFinal)) {
			mapa.put("fechafin", fechaFinal);
		}
		final Character estadoFenologicoInicio = periodoGarantiaCe.getEstadofenologicoini();
		if (noEstaVacio(estadoFenologicoInicio)) {
			mapa.put("estadofenologicoini", estadoFenologicoInicio);
		}
		final Character estadoFenologicoFinal = periodoGarantiaCe.getEstadofenologicofin();
		if (noEstaVacio(estadoFenologicoFinal)) {
			mapa.put("estadofenologicofin", estadoFenologicoFinal);
		}
		final BigDecimal numMesesInicio = periodoGarantiaCe.getNummesesini();
		if (noEstaVacio(numMesesInicio)) {
			mapa.put("nummesesini", numMesesInicio);
		}
		final BigDecimal numMesesFinal = periodoGarantiaCe.getNummesesfin();
		if (noEstaVacio(numMesesFinal)) {
			mapa.put("nummesesfin", numMesesFinal);
		}
		final BigDecimal numDiasInicio = periodoGarantiaCe.getNumdiasini();
		if (noEstaVacio(numDiasInicio)) {
			mapa.put("numdiasini", numDiasInicio);
		}
		final BigDecimal numDiasFinal = periodoGarantiaCe.getNumdiasfin();
		if (noEstaVacio(numDiasFinal)) {
			mapa.put("numdiasfin", numDiasFinal);
		}

		return mapa;
	}

	public final boolean noEstaVacio(final Object object) {
		boolean resultado = false;
		if (null != object) {
			if (object instanceof BigDecimal) {
				if (!new BigDecimal("0").equals((BigDecimal) object)) {
					resultado = true;
				}
			} else if (object instanceof Character) {
				if(!object.toString().equals("0")) {
					resultado = true;
				}
			} else if (object instanceof Date) {
				if (!new Date().equals((Date) object)) {
					resultado = true;
				}
			}
		}
		return resultado;
	}

}
