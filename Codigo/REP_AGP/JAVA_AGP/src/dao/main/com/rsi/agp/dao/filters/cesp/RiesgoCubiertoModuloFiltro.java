package com.rsi.agp.dao.filters.cesp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.filters.FiltroUtils;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;

public class RiesgoCubiertoModuloFiltro implements Filter {

	private Long lineaSeguroId;
	private String codModulo;
	private BigDecimal codRiesgoCubierto;

	public RiesgoCubiertoModuloFiltro(final Long lineaSeguroId, final String codModulo, final BigDecimal codRiesgoCubierto) {
		this.lineaSeguroId = lineaSeguroId;
		this.codModulo = codModulo;
		this.codRiesgoCubierto = codRiesgoCubierto;
	}

	@Override
	public Criteria getCriteria(final Session sesion) {
		final Criteria criteria = sesion.createCriteria(RiesgoCubiertoModulo.class);

		criteria.add(Restrictions.allEq(getIds()));
		
        return criteria;
	}
	
	private final Map<String, Object> getIds() {

		final Map<String, Object> mapa = new HashMap<String, Object>();

		/* PROPIEDADES DE MODULO */
		final Long lineaseguroid = this.lineaSeguroId;
		if (FiltroUtils.noEstaVacio(lineaSeguroId)) {
			mapa.put("id.lineaseguroid",lineaseguroid);
		}
	
		final String codmodulo = this.codModulo;
		if (FiltroUtils.noEstaVacio(codmodulo)) {
			mapa.put("id.codmodulo", codmodulo);
		}
		
		final BigDecimal codriesgocubierto = this.codRiesgoCubierto;
		if (FiltroUtils.noEstaVacio(codriesgocubierto)) {
			mapa.put("riesgoCubierto.id.codriesgocubierto", codriesgocubierto);
		}
		
		return mapa;
	}
	

}