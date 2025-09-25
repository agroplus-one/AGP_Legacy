package com.rsi.agp.batch.procesarPolizasRenovables;

import java.math.BigDecimal;

public final class PPRConstants {

	private PPRConstants() {
	}
	// Estados Renovacion Agroplus
	
	public static final int EST_AGPLUS_PEND_ASIGNAR_GASTOS = 1;
	public static final int EST_AGPLUS_GASTOS_ASIGNADOS = 2;
	public static final int EST_AGPLUS_ENVIADA_PENDIENTE_DE_CONFIRMAR = 3;
	public static final int EST_AGPLUS_ENVIADA_CORRECTA = 4;
	public static final int EST_AGPLUS_ENVIADA_ERRONEA = 5;	
	public static final String  DESC_ENVIADA_ERRONEA = "Error en datos de comisiones";
	public static final String  GRUPOSEGURO_G01 = "G01";
	public static final int PLAN_ACTUAL = 2015;
	
	// Estados Renovacion Agroseguro
	
	public static final int ES_AGSEGURO_BORRADOR_PRECARTERA = 1;
	public static final int ES_AGSEGURO_PRIMERA_COMUNICACION = 2;
	public static final int ES_AGSEGURO_COMUNICACION_DEFINITIVA = 3;
	public static final int ES_AGSEGURO_EMITIDA = 4;
	public static final int ES_AGSEGURO_RESCINDIDA = 5;
	public static final int ES_AGSEGURO_ANULADA = 6;
	
	// Estados polizas
	public static final BigDecimal ESTADO_POLIZA_PENDIENTE_VALIDACION        = new BigDecimal(1);
	public static final BigDecimal ESTADO_POLIZA_GRABACION_PROVISIONAL       = new BigDecimal(2);
	public static final BigDecimal ESTADO_POLIZA_GRABACION_DEFINITIVA        = new BigDecimal(3);
	public static final BigDecimal ESTADO_POLIZA_ANULADA                     = new BigDecimal(4);
	public static final BigDecimal ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR = new BigDecimal(5);
	public static final BigDecimal ESTADO_POLIZA_ENVIADA_ERRONEA             = new BigDecimal(7);
	public static final BigDecimal ESTADO_POLIZA_DEFINITIVA                  = new BigDecimal(8);
	
	public static final BigDecimal USO_POLIZA = BigDecimal.valueOf(31);
	public static final BigDecimal UBICACION_ANIMALES = BigDecimal.valueOf(33);
	public static final BigDecimal UBICACION_CAP_ASEG = BigDecimal.valueOf(31);
	public static final BigDecimal UBICACION_GRUPO_RAZA = BigDecimal.valueOf(29);
	public static final BigDecimal UBICACION_EXPLOTACION = BigDecimal.valueOf(27);
	public static final BigDecimal UBICACION_COBERTURA_DV = BigDecimal.valueOf(18);
	
	public static final int ESTADO_IMPORTACION_PDTE = 1;

	public static final int ESTADO_IMPORTACION_OK = 2;

	public static final int ESTADO_IMPORTACION_KO = 3;

	public static final int AGROSEGURO_DOC_ACEPTADO = 1;

	public static final int POLIZA_EXTERNA = 1;

	public static final String USUARIO_IMPORTACION = "GL023303";
	
}
