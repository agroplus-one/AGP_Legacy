package com.rsi.agp.batch.updateEstadosPolRenovables;

public final class UPRConstants {

	private UPRConstants() {
	}
	
	// Estados Renovación Agroseguro
	
	public static final int ES_AGSEGURO_BORRADOR_PRECARTERA = 1;
	public static final int ES_AGSEGURO_PRIMERA_COMUNICACION = 2;
	public static final int ES_AGSEGURO_COMUNICACION_DEFINITIVA = 3;
	public static final int ES_AGSEGURO_EMITIDA = 4;
	public static final int ES_AGSEGURO_RESCINDIDA = 5;
	public static final int ES_AGSEGURO_ANULADA = 6;
	public static final int ES_AGSEGURO_PRECARTERA_PRECALCULADA = 8;
	public static final int ES_AGSEGURO_PRECARTERA_GENERADA = 9;

	// Estados Renovación Agroplus
	
	public static final int ES_AGPLUS_PEND_ASIGNAR_GASTOS = 1;
	public static final int ES_AGPLUS_GASTOS_ASIGNADOS = 2;
	public static final int ES_AGPLUS_ENVIADA_PENDIENTE_DE_CONFIRMAR = 3;
	public static final int ES_AGPLUS_ENVIADA_CORRECTA = 4;
	public static final int ES_AGPLUS_ENVIADA_ERRONEA = 5;
	
	
	
	public static final String  DESC_ENVIADA_ERRONEA = "Error en datos de comisiones";
	public static final String  GRUPOSEGURO_G01 = "G01";
}

