package com.rsi.agp.dao.models.poliza;

import java.math.BigDecimal;
import java.util.List;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;

public interface IRiesgoCubiertoModuloDao extends GenericDao {
	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModulo(Long lineaseguroid, String codmodulo);
	public List<RiesgoCubiertoModulo> getRiesgosCubiertosModuloPoliza(ModuloId id);
	public String getDescRiesgoCubModulo(Long lineaseguroid, String codmodulo, BigDecimal codRiesgoCub) throws Exception;
	public String getfilaRiesgoCubModulo(Long lineaseguroid, String codmodulo, BigDecimal cPmodulo,BigDecimal codRiesgoCub) throws Exception;
	public String getDescCalcIndem(int valor) throws Exception;
	public String getDescMinIndem(int valor) throws Exception;
	public String getDescDatoVarRiesgo(BigDecimal bigDecimal) throws Exception;
	/* Pet. 63485-Fase II ** MODIF TAM (21.09.2020) ** Inicio */
	public List<RiesgoCubiertoModulo> getRiesgosCubModuloCalcRendimiento(Long lineaseguroid, String codmodulo) throws Exception;
	
}
