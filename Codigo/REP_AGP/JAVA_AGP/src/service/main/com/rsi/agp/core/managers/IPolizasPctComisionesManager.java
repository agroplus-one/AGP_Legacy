package com.rsi.agp.core.managers;

import java.math.BigDecimal;
import java.util.Map;

import com.rsi.agp.core.webapp.util.FluxCondensatorObject;
import com.rsi.agp.core.webapp.util.VistaImportes;
import com.rsi.agp.dao.tables.commons.Usuario;
import com.rsi.agp.dao.tables.poliza.Poliza;
import com.rsi.agp.dao.tables.poliza.PolizaPctComisiones;

public interface IPolizasPctComisionesManager {

	Map<String, Object> validaComisiones(Poliza poliza, Usuario usuario) throws Exception;

	void savePolPctComs(PolizaPctComisiones ppc) throws Exception;
	
	public FluxCondensatorObject dameComisiones(FluxCondensatorObject flux, Poliza pol, Usuario usu,BigDecimal primaNeta);
	
	public VistaImportes dameComisiones(VistaImportes flux, Poliza pol, Usuario usu);
	
	public Map<String, String> obtenerDesgloseComisiones(final PolizaPctComisiones ppc, BigDecimal primaNeta, final BigDecimal pctComsCalculado);
}
