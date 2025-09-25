package com.rsi.agp.dao.models.cpl;

import java.util.List;

import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.CaracteristicaModulo;
import com.rsi.agp.dao.tables.cpl.RiesgoCubiertoModulo;
import com.rsi.agp.dao.tables.cpl.gan.RiesgoCubiertoModuloGanado;

@SuppressWarnings("rawtypes")
public interface ICaracteristicasModuloDao extends GenericDao {

	public List<CaracteristicaModulo> getCaracteristicasModulo(RiesgoCubiertoModulo rcm);
	public List<CaracteristicaModulo> getCaracteristicasModulo(RiesgoCubiertoModuloGanado rcm);
}