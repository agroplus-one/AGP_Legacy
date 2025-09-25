package com.rsi.agp.batch.cargaPolizasRenovables;

public final class CPRConstants {

	private CPRConstants() {
	}
	// Estados Renovacion Agroplus
	
	public static final int EST_AGPLUS_PEND_ASIGNAR_GASTOS = 1;
	
	public static final int EST_AGPLUS_GASTOS_ASIGNADOS = 2;

	public static final int EST_AGPLUS_ENVIADA_PENDIENTE_DE_CONFIRMAR = 3;

	public static final int EST_AGPLUS_ENVIADA_CORRECTA = 4;

	public static final int EST_AGPLUS_ENVIADA_ERRONEA = 5;
	
	// estados Renovacion Agroseguro
	
	public static final int EST_AGROSEGURO_BORRADOR_PRECARTERA = 1;
	public static final int EST_AGROSEGURO_PRECARTERA_PRECALCULADA = 8;
	public static final int EST_AGROSEGURO_PRECARTERA_GENERADA = 9;
		
	public static final String  DESC_ENVIADA_ERRONEA = "Error en datos de comisiones";
	public static final String  GRUPOSEGURO_G01 = "G01";
	public static final int PLAN_ACTUAL = 2015;
	public static final String GRUPO_NEGOCIO = "2";
}
