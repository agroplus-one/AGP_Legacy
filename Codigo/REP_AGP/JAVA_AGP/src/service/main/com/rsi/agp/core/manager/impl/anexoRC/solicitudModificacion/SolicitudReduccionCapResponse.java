package com.rsi.agp.core.manager.impl.anexoRC.solicitudModificacion;

import com.rsi.agp.core.manager.impl.anexoRC.PolizaActualizadaRCResponse;

public class SolicitudReduccionCapResponse extends PolizaActualizadaRCResponse {
	private es.agroseguro.seguroAgrario.cuponModificacion.CuponModificacionDocument cuponModificacion;

	public es.agroseguro.seguroAgrario.cuponModificacion.CuponModificacionDocument getCuponModificacion() {
		return cuponModificacion;
	}

	public void setCuponModificacion(
			es.agroseguro.seguroAgrario.cuponModificacion.CuponModificacionDocument cuponModificacion) {
		this.cuponModificacion = cuponModificacion;
	}
}
