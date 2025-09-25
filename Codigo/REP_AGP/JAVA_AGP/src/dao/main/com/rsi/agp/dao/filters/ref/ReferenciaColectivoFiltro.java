package com.rsi.agp.dao.filters.ref;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.ColectivoReferencia;

public class ReferenciaColectivoFiltro implements Filter {

	private Date fechaIni;
	private Date fechaFin;
	private boolean refLibres;
	private String referencia;
	private String referenciaIni;
	private String referenciaFin;

	public ReferenciaColectivoFiltro(final Date fechaIni, final Date fechaFin) {
		this.fechaIni = fechaIni;
		this.fechaFin = fechaFin;
	}
	public ReferenciaColectivoFiltro(final String referenciaIni, final String referenciaFin) {
		this.referenciaIni = referenciaIni;
		this.referenciaFin = referenciaFin;
	}

	public ReferenciaColectivoFiltro(final boolean refLibres) {
		this.refLibres = refLibres;
	}

	public ReferenciaColectivoFiltro(final String referencia) {
		this.referencia = referencia;
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(ColectivoReferencia.class);

		if (FiltroUtils.noEstaVacio(referenciaIni) && FiltroUtils.noEstaVacio(referenciaFin))
		{
			criteria.add(Restrictions.between("referencia", referenciaIni, referenciaFin));
		}		
		
		if (FiltroUtils.noEstaVacio(fechaIni)) {
			criteria.add(Restrictions.ge("fechaenvio", fechaIni));
		}
		if (FiltroUtils.noEstaVacio(fechaFin)) {
			criteria.add(Restrictions.le("fechaenvio", fechaFin));
		}
		if (refLibres) {
			criteria.add(Restrictions.isNull("fechaenvio"));
		}
		if (FiltroUtils.noEstaVacio(referencia)) {
			criteria.add(Restrictions.eq("referencia", referencia));
		}
		return criteria;
	}
}
