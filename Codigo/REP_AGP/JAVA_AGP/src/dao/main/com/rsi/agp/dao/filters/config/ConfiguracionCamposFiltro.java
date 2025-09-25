package com.rsi.agp.dao.filters.config;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.config.ConfiguracionCampo;

public class ConfiguracionCamposFiltro implements Filter {

	private boolean mostrarsiempre;
	private Long idPantallaConfigurable;

	public ConfiguracionCamposFiltro() {
	}

	public ConfiguracionCamposFiltro(boolean mostrarsiempre, Long idPantallaConfigurable) {
		this.mostrarsiempre = mostrarsiempre;
		this.idPantallaConfigurable = idPantallaConfigurable;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		Criteria criteria = sesion.createCriteria(ConfiguracionCampo.class);
		criteria.setProjection(Projections.property("id.codconcepto"));
		criteria.add(Restrictions.eq("id.idpantallaconfigurable", new BigDecimal(idPantallaConfigurable)));		
		criteria.add(Restrictions.eq("mostrarsiempre", mostrarsiempre ? "S" : "N"));
		return criteria;
	}
}