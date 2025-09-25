package com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion;

import java.math.BigDecimal;

import org.apache.xmlbeans.XmlObject;

//import com.rsi.agp.core.managers.impl.anexoMod.impresion.RelacionIncidenciasResponse;

public interface ISolicitudReduccionCapManager {

	public SolicitudReduccionCapBean solicitarModificacion(String referencia, BigDecimal plan, String realPath,
			String codUsuario);

	public String anularCupon(Long id, String idCupon, String realPath, String codUsuario);
//
	public XmlObject getPolizaActualizadaFromCupon(String idCupon);
//
//	public XmlObject getPolizaActualizadaCplFromCupon(String idCupon);
//	
	public Long saveCupon (Long id, String idCupon, String referencia, String codUsuario, Long estadoCupon);
	
	/*public void insertarEnviosSWSolicitud(final String referencia,
			final BigDecimal plan, final String codUsuario,
			final SolicitudModificacionResponse response,
			final boolean isPolizaGanado); */
}
