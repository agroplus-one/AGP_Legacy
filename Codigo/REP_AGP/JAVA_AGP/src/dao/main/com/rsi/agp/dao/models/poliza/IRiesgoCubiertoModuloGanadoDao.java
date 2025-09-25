package com.rsi.agp.dao.models.poliza;

import java.util.List;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.ModuloId;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;

public interface IRiesgoCubiertoModuloGanadoDao extends GenericDao {
	public List<RiesgoCubiertoModuloGanado> getRiesgosCubiertosModuloGanado (ModuloId id);
	public boolean hayComparativasElegibles (String codModulo, Long lineaseguroid);
}
