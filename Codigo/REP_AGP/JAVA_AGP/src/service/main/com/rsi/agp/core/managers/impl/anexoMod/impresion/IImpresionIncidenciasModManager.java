package com.rsi.agp.core.managers.impl.anexoMod.impresion;

import java.math.BigDecimal;
import java.util.Map;

public interface IImpresionIncidenciasModManager {
	
	public Map<String, Object> solicitarRelacionIncidencias (String referencia, BigDecimal plan, String realPath, String codUsuario);
	
	public Map<String, Object> imprimirPdfIncidencia(String realPath, String idCupon,String anio, String numero);

}
