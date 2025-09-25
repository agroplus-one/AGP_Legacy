package com.rsi.agp.batch.updateFechaEntradaVigor;

import java.math.BigDecimal;

public final class UpdateFEVConstants {

	private UpdateFEVConstants() {
	}

	// Estados polizas
	public static final BigDecimal ESTADO_POLIZA_PENDIENTE_VALIDACION        = new BigDecimal(1);
	public static final BigDecimal ESTADO_POLIZA_GRABACION_PROVISIONAL       = new BigDecimal(2);
	public static final BigDecimal ESTADO_POLIZA_GRABACION_DEFINITIVA        = new BigDecimal(3);
	public static final BigDecimal ESTADO_POLIZA_ANULADA                     = new BigDecimal(4);
	public static final BigDecimal ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR = new BigDecimal(5);
	public static final BigDecimal ESTADO_POLIZA_ENVIADA_ERRONEA             = new BigDecimal(7);
	public static final BigDecimal ESTADO_POLIZA_DEFINITIVA                  = new BigDecimal(8);
	public static final BigDecimal ESTADO_POLIZA_EMITIDA                     = new BigDecimal(14);
	
}
