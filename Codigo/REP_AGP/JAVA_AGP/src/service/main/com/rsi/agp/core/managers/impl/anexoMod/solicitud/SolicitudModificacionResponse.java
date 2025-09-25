package com.rsi.agp.core.managers.impl.anexoMod.solicitud;

import com.rsi.agp.core.managers.impl.anexoMod.PolizaActualizadaResponse;

public class SolicitudModificacionResponse extends PolizaActualizadaResponse {
	private es.agroseguro.seguroAgrario.cuponModificacion.CuponModificacionDocument cuponModificacion;

	public es.agroseguro.seguroAgrario.cuponModificacion.CuponModificacionDocument getCuponModificacion() {
		return cuponModificacion;
	}

	public void setCuponModificacion(
			es.agroseguro.seguroAgrario.cuponModificacion.CuponModificacionDocument cuponModificacion) {
		this.cuponModificacion = cuponModificacion;
	}
}
