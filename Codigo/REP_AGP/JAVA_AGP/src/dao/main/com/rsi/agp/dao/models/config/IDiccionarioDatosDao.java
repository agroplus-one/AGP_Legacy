package com.rsi.agp.dao.models.config;

import java.math.BigDecimal;
import java.util.Map;

import com.rsi.agp.core.report.anexoMod.RelacionEtiquetaTabla;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.RiesgoCubierto;

@SuppressWarnings("rawtypes")
public interface IDiccionarioDatosDao extends GenericDao {

	public Map<BigDecimal, RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaParcelas(
			final Long lineaseguroid);

	public Map<BigDecimal, RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaExplotaciones(
			final Long lineaseguroid);

	public Map<BigDecimal, RelacionEtiquetaTabla> getCodConceptoEtiquetaTablaCoberturas(
			final Long lineaseguroid);

	public RiesgoCubierto getRiesgosElegidos(final Long lineaseguroid,
			final String modulo, final int codriesgoCubierto);
	
	public String getTipoAsegurado(final BigDecimal idComparativa);
}