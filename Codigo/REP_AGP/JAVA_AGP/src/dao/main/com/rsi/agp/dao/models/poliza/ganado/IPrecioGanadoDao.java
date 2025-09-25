package com.rsi.agp.dao.models.poliza.ganado;

import java.util.List;

import com.rsi.agp.core.exception.PrecioGanadoException;
import com.rsi.agp.dao.models.GenericDao;
import com.rsi.agp.dao.tables.cpl.gan.MascaraPrecioGanado;
import com.rsi.agp.dao.tables.cpl.gan.PrecioGanado;

public interface IPrecioGanadoDao extends GenericDao {
	public PrecioGanado getPrecioExplotacion(Object explotacion, String codModulo, PrecioGanado dvBean) throws PrecioGanadoException;
	public List<MascaraPrecioGanado> getMascarasPrecioExplotacion(Object ex, String codModulo);
}