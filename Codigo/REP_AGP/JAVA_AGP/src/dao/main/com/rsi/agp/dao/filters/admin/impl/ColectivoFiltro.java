package com.rsi.agp.dao.filters.admin.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Order;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.admin.Colectivo;

public class ColectivoFiltro implements Filter {

	private Colectivo colectivo;
	private BigDecimal codentidad;
	private String ciftomador;
	private Long lineaseguroid;

	public ColectivoFiltro(final Colectivo colectivo) {
		this.colectivo = colectivo;
	}

	public ColectivoFiltro(final BigDecimal codentidad, final String ciftomador) {
		this.codentidad = codentidad;
		this.ciftomador = ciftomador;
	}
	
	public ColectivoFiltro(final BigDecimal codentidad, final String ciftomador, final Long lineaseguroid) {
		this.codentidad = codentidad;
		this.ciftomador = ciftomador;
		this.lineaseguroid = lineaseguroid;
	}
	
	public ColectivoFiltro(final BigDecimal codEntidad, final String cifTomador, final Long lineaSeguroId, Colectivo colectivo)
	{
		this.codentidad = codEntidad;
		this.ciftomador = cifTomador;
		this.lineaseguroid = lineaSeguroId;
		this.colectivo = colectivo; 
	}

	@Override
	public final Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(Colectivo.class);

		criteria.createAlias("linea", "lin");
		criteria.createAlias("subentidadMediadora", "SM");
		criteria.createAlias("tomador", "tom");
		criteria.addOrder(Order.asc("tom.entidad.codentidad"));
		criteria.addOrder(Order.asc("SM.entidadMediadora.codentidad"));
//		criteria.addOrder(Order.asc("SM.id.codentidad"));		
		criteria.addOrder(Order.desc("lin.codplan"));
		criteria.addOrder(Order.asc("lin.codlinea"));
		// los que no esten dados de baja
		criteria.add(Restrictions.isNull("SM.fechabaja"));
		if (FiltroUtils.noEstaVacio(colectivo)) {
			criteria.add(Restrictions.allEq(getMapaColectivo()));

			final String nomColectivo = colectivo.getNomcolectivo();
			if (FiltroUtils.noEstaVacio(nomColectivo)) {
				criteria.add(Restrictions.ilike("nomcolectivo", "%".concat(nomColectivo).concat("%")));
			}
			
			final Character activo = colectivo.getActivo();
			if (FiltroUtils.noEstaVacio(activo)) {
				criteria.add(Restrictions.eq("activo", activo));
			}
		}
		if (FiltroUtils.noEstaVacio(codentidad)) {
			criteria.add(Restrictions.eq("tomador.id.codentidad", codentidad));
		}
		if (FiltroUtils.noEstaVacio(ciftomador)) {
			criteria.add(Restrictions.eq("tomador.id.ciftomador", ciftomador));
		}
		if (FiltroUtils.noEstaVacio(lineaseguroid)) {
			criteria.add(Restrictions.eq("lin.lineaseguroid", lineaseguroid));
		}

		return criteria;
	}

	private final Map<String, Object> getMapaColectivo() {
		final Map<String, Object> mapa = new HashMap<String, Object>();

		final Long id = colectivo.getId();
		if (FiltroUtils.noEstaVacio(id)) {
			mapa.put("id", id);
		}

		final BigDecimal codEntidad = colectivo.getTomador().getId().getCodentidad();
		if (FiltroUtils.noEstaVacio(codEntidad)) {
			mapa.put("tomador.id.codentidad", codEntidad);
		}

		final BigDecimal codPlan = colectivo.getLinea().getCodplan();
		if (FiltroUtils.noEstaVacio(codPlan)) {
			mapa.put("lin.codplan", codPlan);
		}
		final BigDecimal codLinea = colectivo.getLinea().getCodlinea();
		if(FiltroUtils.noEstaVacio(codLinea)){
			mapa.put("lin.codlinea", codLinea);
		}

		final Long lineaSeguroId = colectivo.getLinea().getLineaseguroid();
		if (FiltroUtils.noEstaVacio(lineaSeguroId)) {
			mapa.put("lin.lineaseguroid", lineaSeguroId);
		}

		final String cifTomador = colectivo.getTomador().getId().getCiftomador();
		if (FiltroUtils.noEstaVacio(cifTomador)) {
			mapa.put("tomador.id.ciftomador", cifTomador);
		}

		final String idColectivo = colectivo.getIdcolectivo();
		if (FiltroUtils.noEstaVacio(idColectivo)) {
			mapa.put("idcolectivo", idColectivo);
		}
		
		final String dc = colectivo.getDc();
		if (FiltroUtils.noEstaVacio(dc)) {
			mapa.put("dc", dc);
		}

		final BigDecimal entMediadora = colectivo.getSubentidadMediadora().getId().getCodentidad();
		if (FiltroUtils.noEstaVacio(entMediadora)) {
			mapa.put("subentidadMediadora.id.codentidad", entMediadora);
		}

		final BigDecimal subEntMediadora = colectivo.getSubentidadMediadora().getId().getCodsubentidad();
		if (FiltroUtils.noEstaVacio(subEntMediadora)) {
			mapa.put("subentidadMediadora.id.codsubentidad", subEntMediadora);
		}

		final Character activo = colectivo.getActivo();
		if (FiltroUtils.noEstaVacio(activo)) {
			mapa.put("activo", activo);
		}

		final BigDecimal pctPrimerPago= colectivo.getPctprimerpago();
		if (FiltroUtils.noEstaVacio(pctPrimerPago)) {
			mapa.put("pctprimerpago", pctPrimerPago);
		}

		final Date fechaPrimerPago = colectivo.getFechaprimerpago();
		if (FiltroUtils.noEstaVacio(fechaPrimerPago)) {
			mapa.put("fechaprimerpago", fechaPrimerPago);
		}

		final BigDecimal pctSegundoPago = colectivo.getPctsegundopago();
		if (FiltroUtils.noEstaVacio(pctSegundoPago)) {
			mapa.put("pctsegundopago", pctSegundoPago);
		}

		final Date fechaSegundoPago = colectivo.getFechasegundopago();
		if (FiltroUtils.noEstaVacio(fechaSegundoPago)) {
			mapa.put("fechasegundopago", fechaSegundoPago);
		}

		return mapa;
	}

}
