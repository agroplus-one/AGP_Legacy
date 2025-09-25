package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;

import com.rsi.agp.dao.models.GenericDao;

@SuppressWarnings("rawtypes")
public interface IComparativaSitActDao extends GenericDao {
	
	public String getDesGarantizado(BigDecimal codGarantizado);

	public String getDesCalcIndem(BigDecimal codcalculo);

	public String getDesPctFranquicia(BigDecimal codpctfranquiciaeleg);

	public String getDesMinIndem(BigDecimal pctminindem);

	public String getDesTipoFranqIndem(String codtipofranquicia);

	public String getDesCapitalAseg(BigDecimal pctcapitalaseg);

	public BigDecimal getFilaModulo(Long lineaseguroid, String codModulo, int codconcepto,
			BigDecimal codConceptopplaMod, BigDecimal codRiesgoCub);

	public BigDecimal getFilaModuloGanado(Long lineaseguroid, String codModulo,
			BigDecimal codConceptopplaMod, BigDecimal codRiesgoCub);
}
