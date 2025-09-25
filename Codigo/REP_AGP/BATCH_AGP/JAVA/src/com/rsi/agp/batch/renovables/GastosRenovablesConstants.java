package com.rsi.agp.batch.renovables;

public final class GastosRenovablesConstants {

	private GastosRenovablesConstants() {
	}
	// Estados Renovacion Agroplus
	
	public static final int EST_AGPLUS_PEND_ASIGNAR_GASTOS = 1;
	
	public static final int EST_AGPLUS_GASTOS_ASIGNADOS = 2;

	public static final int EST_AGPLUS_ENVIADA_PENDIENTE_DE_CONFIRMAR = 3;

	public static final int EST_AGPLUS_ENVIADA_CORRECTA = 4;

	public static final int EST_AGPLUS_ENVIADA_ERRONEA = 5;
	
	public static final String  DESC_ENVIADA_ERRONEA = "Error en datos de comisiones";
}
