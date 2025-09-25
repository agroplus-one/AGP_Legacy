package com.rsi.agp.dao.filters.config;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.rsi.agp.dao.filters.Filter;
import com.rsi.agp.dao.tables.masc.CampoMascara;

public class CampoMascaraFiltro implements Filter {

	private CampoMascara campoMascara;
	private List<BigDecimal> listaMascaras;

	public CampoMascaraFiltro() {
		super();
		campoMascara = new CampoMascara();
		listaMascaras = null;
	}

	public CampoMascaraFiltro(List<BigDecimal> listaMascaras) {
		super();
		this.campoMascara = null;
		this.listaMascaras = listaMascaras;
	}

	@Override
	public Criteria getCriteria(Session sesion) {
		boolean aliasMasc = false;
		boolean aliasAsoc = false;
		Criteria criteria = sesion.createCriteria(CampoMascara.class);
		if (campoMascara != null) {
			BigDecimal codtablacondicionado = campoMascara.getTablaCondicionado().getCodtablacondicionado();
			if (codtablacondicionado != null) {
				criteria.createAlias("tablaCondicionado", "tablaCondicionado");
				criteria.add(Restrictions.eq("tablaCondicionado.codtablacondicionado", codtablacondicionado));
			}
			BigDecimal codconceptomasc = campoMascara.getDiccionarioDatosByCodconceptomasc().getCodconcepto();
			if (codconceptomasc != null) {
				if (!aliasMasc) {
					criteria.createAlias("diccionarioDatosByCodconceptomasc", "diccionarioDatosByCodconceptomasc");
					aliasMasc = true;
				}
				criteria.add(Restrictions.eq("diccionarioDatosByCodconceptomasc.codconcepto", codconceptomasc));
			}
			BigDecimal codconceptoasoc = campoMascara.getDiccionarioDatosByCodconceptoasoc().getCodconcepto();
			if (codconceptoasoc != null) {
				if (!aliasAsoc) {
					criteria.createAlias("diccionarioDatosByCodconceptoasoc", "diccionarioDatosByCodconceptoasoc");
					aliasAsoc = true;
				}
				criteria.add(Restrictions.eq("diccionarioDatosByCodconceptoasoc.codconcepto", codconceptoasoc));
			}
		}
		if (listaMascaras != null && !listaMascaras.isEmpty()) {
			if (!aliasMasc) {
				criteria.createAlias("diccionarioDatosByCodconceptomasc", "diccionarioDatosByCodconceptomasc");
				aliasMasc = true;
			}
			if (!aliasAsoc) {
				criteria.createAlias("diccionarioDatosByCodconceptoasoc", "diccionarioDatosByCodconceptoasoc");
				aliasAsoc = true;
			}
			criteria.add(Restrictions.in("diccionarioDatosByCodconceptomasc.codconcepto", listaMascaras));
			criteria.setProjection(Projections.property("diccionarioDatosByCodconceptoasoc.codconcepto"));
		}
		return criteria;
	}

	public CampoMascara getCampoMascara() {
		return campoMascara;
	}

	public void setCampoMascara(CampoMascara campoMascara) {
		this.campoMascara = campoMascara;
	}

}
