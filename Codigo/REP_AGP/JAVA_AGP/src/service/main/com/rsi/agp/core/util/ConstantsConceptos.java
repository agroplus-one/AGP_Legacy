package com.rsi.agp.core.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConstantsConceptos {

	// Constantes de codigos concepto asociados a los campos de base de datos
	public static final int CODCPTO_PRODUCCION = 68;
	public static final int CODCPTO_NIF_SOCIO = 98;
	public static final int CODCPTO_CARACT_EXPLOTACION = 106;
	public static final int CODCPTO_DENOMORIGEN = 107;
	public static final int CODCPTO_DENSIDAD = 109;
	public static final int CODCPTO_DESTINO = 110;
	public static final int CODCPTO_EDAD = 111;
	public static final int CODCPTO_FECHA_RECOLEC = 112;
	public static final int CODCPTO_FECSIEMBRA = 113;
	public static final int CODCPTO_GASTOS_SALVAMENTO = 114;
	public static final int CODCPTO_TIPMARCOPLANT = 116;
	public static final int CODCPTO_UNIDADES = 117;
	public static final int CODCPTO_NUMARBOLES = 117;
	public static final int CODCPTO_NUMCEPAS = 117;
	public static final int CODCPTO_PCT_FRANQUICIA = 120;
	public static final int CODCPTO_MINIMO_INDEMNIZABLE = 121;
	public static final int CODCPTO_SISTCULTIVO = 123;
	public static final int CODCPTO_MEDIDA_PREVENTIVA = 124;
	public static final int CODCPTO_TIPOCAPITAL = 126;
	public static final int CODCPTO_SISTCOND = 131;
	public static final int CODCPTO_SUBV_DEC_PARC = 132;
	public static final int CODCPTO_PRACTCULT = 133;
	public static final int CODCPTO_FEC_FIN_GARANT = 134;
	public static final int CODCPTO_EST_FENOLOGICO_F_GARANT = 135;
	public static final int CODCPTO_DIAS_DUR_MAX_GARANT = 136;
	public static final int CODCPTO_MES_DUR_MAX_GARANT = 137;
	public static final int CODCPTO_FEC_INI_GARANT = 138;
	public static final int CODCPTO_EST_FENOLOGICO_I_GARANT = 139;
	public static final int CODCPTO_DIAS_INI_GARANT = 140;
	public static final int CODCPTO_MES_INI_GARANT = 141;
	public static final int CODCPTO_ROTACION = 144;
	public static final int CODCPTO_PERIODO_GARANT = 157;
	public static final int CODCPTO_DANHOS_CUBIERTOS = 169;
	public static final int CODCPTO_TIPO_FRANQUICIA = 170;
	public static final int CODCPTO_TIPO_PLANTACION = 173;
	public static final int CODCPTO_CALCULO_INDEMNIZACION = 174;
	public static final int CODCPTO_GARANTIZADO = 175;
	public static final int CODCPTO_SUPERFICIE = 258;
	public static final int CODCPTO_CAPITAL_ASEGURADO = 362;
	public static final int CODCPTO_RIESGO_CUBIERTO_ELEGIDO = 363;
	public static final int CODCPTO_FRUTOS_CAIDOS = 426;
	public static final int CODCPTO_TIPO_RENDIMIENTO = 502;
	public static final int CODCPTO_SISTEMA_PRODUCCION = 616;
	public static final int CODCPTO_NUMANIOSPODA = 617;
	public static final int CODCPTO_CICLOCULTIVO = 618;
	public static final int CODCPTO_REDUCCION_RDTOS = 620;
	public static final int CODCPTO_SISTEMA_PROTECCION = 621;
	public static final int CODCPTO_TIPOTERRENO = 752;
	public static final int CODCPTO_TIPOMASA = 753;
	public static final int CODCPTO_PENDIENTE = 754;
	public static final int CODCPTO_IGP = 765;
	public static final int CODCPTO_METROS_LINEALES = 766;
	public static final int CODCPTO_METROS_CUADRADOS = 767;
	public static final int CODCPTO_VALOR_FIJO = 768;
	public static final int CODCPTO_TIPOINSTAL = 778;
	public static final int CODCPTO_MATCUBIERTA = 873;
	public static final int CODCPTO_EDAD_CUBIERTA = 874;
	public static final int CODCPTO_MATESTRUCTURA = 875;
	public static final int CODCPTO_EDAD_ESTRUCTURA = 876;
	public static final int CODCPTO_COD_CERTIFICADO = 879;
	public static final int CODCPTO_ANHOS_DESCORCHE = 944;
	public static final int CODCPTO_CONTROL_OFICIAL_LECHERO = 1045;
	public static final int CODCPTO_PUREZA = 1046;
	public static final int CODCPTO_PROD_ANUAL_MEDIA = 1047;
	public static final int CODCPTO_SISTEMA_ALMACENAMIENTO = 1048;
	public static final int CODCPTO_EMPRESA_GESTORA = 1049;
	public static final int CODCPTO_CONDICIONES_ESP_CONTRA = 1050;
	public static final int CODCPTO_IGPDO = 1051;
	public static final int CODCPTO_TIPO_GANADERIA = 1052;
	public static final int CODCPTO_ALOJAMIENTO = 1053;
	public static final int CODCPTO_NUM_ANIM_ACUM_DESDE = 1055;
	public static final int CODCPTO_NUM_ANIM_ACUM_HASTA = 1056;
	public static final int CODCPTO_EXCP_CONTRATACION_EXPL = 1063;
	public static final int CODCPTO_AUTORIZACION_ESPECIAL = 1064;
	public static final int CODCPTO_TIPO_ASEG_GAN = 1079;
	public static final int CODCPTO_EXCP_CONTRATACION_PLZ = 1111;
	public static final int CODCPTO_ADAP_RIESGO_TC = 1125;	

	// Constantes para los tipos asociados a los campos de base de datos
	public static final Class<BigDecimal> NUMARBOLES_CLASS = BigDecimal.class;
	public static final Class<Character> MEDPREV_CLASS = Character.class;
	public static final Class<Date> FECSIEMBRA_CLASS = Date.class;
	public static final Class<BigDecimal> NUMCEPAS_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> SISTCULTIVO_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> SISTPROD_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> EDAD_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> DESTINO_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> TIPOPLANTACION_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> PRACTCULT_CLASS = BigDecimal.class;
	public static final Class<Date> FECFGARANT_CLASS = Date.class;
	public static final Class<BigDecimal> SISTCOND_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> CICLOCULTIVO_CLASS = BigDecimal.class;
	public static final Class<Date> FRECOL_CLASS = Date.class;
	public static final Class<BigDecimal> TIPMARCOPLANT_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> NUMANIOSPODA_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> SISTPROT_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> ROTACION_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> DENOMORIGEN_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> IGP_CLASS = BigDecimal.class;
	public static final Class<Character> RCUBELEG_CLASS = Character.class;
	public static final Class<BigDecimal> METROS2_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> TIPOINSTAL_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> MATCUBIERTA_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> TIPOTERRENO_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> TIPOMASA_CLASS = BigDecimal.class;
	public static final Class<BigDecimal> PENDIENTE_CLASS = BigDecimal.class;

	public static final Map<String, Integer> CODCPTO_DATOS_VARIABLES_PAC = Collections
			.unmodifiableMap(new HashMap<String, Integer>() {
				private static final long serialVersionUID = 1758629596551667500L;
				{
					put(NUMARBOLES_FIELD, CODCPTO_NUMARBOLES);
					put(MEDPREV_FIELD, CODCPTO_MEDIDA_PREVENTIVA);
					put(FECSIEMBRA_FIELD, CODCPTO_FECSIEMBRA);
					put(NUMCEPAS_FIELD, CODCPTO_NUMCEPAS);
					put(SISTCULTIVO_FIELD, CODCPTO_SISTCULTIVO);
					put(SISTPROD_FIELD, CODCPTO_SISTEMA_PRODUCCION);
					put(EDAD_FIELD, CODCPTO_EDAD);
					put(DESTINO_FIELD, CODCPTO_DESTINO);
					put(TIPOPLANTACION_FIELD, CODCPTO_TIPO_PLANTACION);
					put(PRACTCULT_FIELD, CODCPTO_PRACTCULT);
					put(FECFGARANT_FIELD, CODCPTO_FEC_FIN_GARANT);
					put(SISTCOND_FIELD, CODCPTO_SISTCOND);
					put(CICLOCULTIVO_FIELD, CODCPTO_CICLOCULTIVO);
					put(FRECOL_FIELD, CODCPTO_FECHA_RECOLEC);
					put(TIPMARCOPLANT_FIELD, CODCPTO_TIPMARCOPLANT);
					put(NUMANIOSPODA_FIELD, CODCPTO_NUMANIOSPODA);
					put(SISTPROT_FIELD, CODCPTO_SISTEMA_PROTECCION);
					put(ROTACION_FIELD, CODCPTO_ROTACION);
					put(DENOMORIGEN_FIELD, CODCPTO_DENOMORIGEN);
					put(IGP_FIELD, CODCPTO_IGP);
					put(RCUBELEG_FIELD, CODCPTO_RIESGO_CUBIERTO_ELEGIDO);
					put(METROS2_FIELD, CODCPTO_METROS_CUADRADOS);
					put(TIPOINSTAL_FIELD, CODCPTO_TIPOINSTAL);
					put(MATCUBIERTA_FIELD, CODCPTO_MATCUBIERTA);
					put(TIPOTERRENO_FIELD, CODCPTO_TIPOTERRENO);
					put(TIPOMASA_FIELD, CODCPTO_TIPOMASA);
					put(PENDIENTE_FIELD, CODCPTO_PENDIENTE);
				}
			});

	public static final Map<String, Class<?>> TIPO_DATOS_VARIABLES_PAC = Collections
			.unmodifiableMap(new HashMap<String, Class<?>>() {
				private static final long serialVersionUID = 1758629596551667500L;
				{
					put(NUMARBOLES_FIELD, NUMARBOLES_CLASS);
					put(MEDPREV_FIELD, MEDPREV_CLASS);
					put(FECSIEMBRA_FIELD, FECSIEMBRA_CLASS);
					put(NUMCEPAS_FIELD, NUMCEPAS_CLASS);
					put(SISTCULTIVO_FIELD, SISTCULTIVO_CLASS);
					put(SISTPROD_FIELD, SISTPROD_CLASS);
					put(EDAD_FIELD, EDAD_CLASS);
					put(DESTINO_FIELD, DESTINO_CLASS);
					put(TIPOPLANTACION_FIELD, TIPOPLANTACION_CLASS);
					put(PRACTCULT_FIELD, PRACTCULT_CLASS);
					put(FECFGARANT_FIELD, FECFGARANT_CLASS);
					put(SISTCOND_FIELD, SISTCOND_CLASS);
					put(CICLOCULTIVO_FIELD, CICLOCULTIVO_CLASS);
					put(FRECOL_FIELD, FRECOL_CLASS);
					put(TIPMARCOPLANT_FIELD, TIPMARCOPLANT_CLASS);
					put(NUMANIOSPODA_FIELD, NUMANIOSPODA_CLASS);
					put(SISTPROT_FIELD, SISTPROD_CLASS);
					put(ROTACION_FIELD, ROTACION_CLASS);
					put(DENOMORIGEN_FIELD, DENOMORIGEN_CLASS);
					put(IGP_FIELD, IGP_CLASS);
					put(RCUBELEG_FIELD, RCUBELEG_CLASS);
					put(METROS2_FIELD, METROS2_CLASS);
					put(TIPOINSTAL_FIELD, TIPOINSTAL_CLASS);
					put(MATCUBIERTA_FIELD, MATCUBIERTA_CLASS);
					put(TIPOTERRENO_FIELD, TIPOTERRENO_CLASS);
					put(TIPOMASA_FIELD, TIPOMASA_CLASS);
					put(PENDIENTE_FIELD, PENDIENTE_CLASS);
				}
			});

	// CONSTANTES PARA LA CARGA DE LA PAC
	// Constantes para los campos de base de datos
	public static final String NUMARBOLES_FIELD = "numarboles";
	public static final String MEDPREV_FIELD = "medprev";
	public static final String FECSIEMBRA_FIELD = "fecSiembra";
	public static final String NUMCEPAS_FIELD = "numcepas";
	public static final String SISTCULTIVO_FIELD = "sistcultivo";
	public static final String SISTPROD_FIELD = "sistprod";
	public static final String EDAD_FIELD = "edad";
	public static final String DESTINO_FIELD = "destino";
	public static final String TIPOPLANTACION_FIELD = "tipoplantacion";
	public static final String PRACTCULT_FIELD = "practcult";
	public static final String FECFGARANT_FIELD = "fecFGarant";
	public static final String SISTCOND_FIELD = "sistcond";
	public static final String CICLOCULTIVO_FIELD = "ciclocultivo";
	public static final String FRECOL_FIELD = "frecol";
	public static final String TIPMARCOPLANT_FIELD = "tipmarcoplant";
	public static final String NUMANIOSPODA_FIELD = "numaniospoda";
	public static final String SISTPROT_FIELD = "sistprot";
	public static final String ROTACION_FIELD = "rotacion";
	public static final String DENOMORIGEN_FIELD = "denomorigen";
	public static final String IGP_FIELD = "igp";
	public static final String RCUBELEG_FIELD = "rcubeleg";
	public static final String METROS2_FIELD = "metros2";
	public static final String TIPOINSTAL_FIELD = "tipoinstal";
	public static final String MATCUBIERTA_FIELD = "matcubierta";
	public static final String TIPOTERRENO_FIELD = "tipoterreno";
	public static final String TIPOMASA_FIELD = "tipomasa";
	public static final String PENDIENTE_FIELD = "pendiente";
}
