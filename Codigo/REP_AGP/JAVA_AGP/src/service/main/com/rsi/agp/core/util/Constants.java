package com.rsi.agp.core.util;

import java.math.BigDecimal;

public class Constants { 

	//Tipos l?nea
	public enum TipoLinea{AGR, GAN};
	public static final String GRUPO_SEGURO_AGRICOLA = "A01";
	public static final String GRUPO_SEGURO_GANADO = "G01";
	
	// Estados polizas
	public static final BigDecimal ESTADO_POLIZA_BAJA							= new BigDecimal(0);
	public static final BigDecimal ESTADO_POLIZA_PENDIENTE_VALIDACION			= new BigDecimal(1);
	public static final BigDecimal ESTADO_POLIZA_GRABACION_PROVISIONAL      	= new BigDecimal(2);
	public static final BigDecimal ESTADO_POLIZA_GRABACION_DEFINITIVA       	= new BigDecimal(3);
	public static final BigDecimal ESTADO_POLIZA_ANULADA                     	= new BigDecimal(16);
	public static final BigDecimal ESTADO_POLIZA_ENVIADA_PENDIENTE_CONFIRMAR 	= new BigDecimal(5);
	public static final BigDecimal ESTADO_POLIZA_ENVIADA_ERRONEA             	= new BigDecimal(7);
	public static final BigDecimal ESTADO_POLIZA_DEFINITIVA                  	= new BigDecimal(8);
	public static final BigDecimal ESTADO_POLIZA_BORRADOR_PRECARTERA			= new BigDecimal(11);
	public static final BigDecimal ESTADO_POLIZA_PRIMERA_COMUNICACION			= new BigDecimal(12);
	public static final BigDecimal ESTADO_POLIZA_COMUNICACION_DEFINITIVA		= new BigDecimal(13);
	public static final BigDecimal ESTADO_POLIZA_EMITIDA						= new BigDecimal(14);
	public static final BigDecimal ESTADO_POLIZA_RESCINDIDA						= new BigDecimal(15);
	public static final BigDecimal ESTADO_POLIZA_CON_GASTOS_SIN_RENOVACION		= new BigDecimal(17);
	public static final BigDecimal ESTADO_POLIZA_PRECARTERA_PRECALCULADA		= new BigDecimal(18);
	public static final BigDecimal ESTADO_POLIZA_PRECARTERA_GENERADA			= new BigDecimal(19);
	 
	// Estados de pago de polizas
	public static final Long POLIZA_NO_PAGADA        = new Long(0);
	public static final Long POLIZA_PAGADA           = new Long(1);
	public static final Long POLIZA_PDT_CONFIRMACION = new Long(2);
	public static final Long POLIZA_ERROR            = new Long(3);
	public static final Long POLIZA_TRAMITACION      = new Long(4);
	
	//abreviaturas Estados de pago de polizas
	public static final String POLIZA_NO_PAGADA_TXT        = "NO";
	public static final String POLIZA_PAGADA_TXT           = "SI";
	public static final String POLIZA_PDT_CONFIRMACION_TXT = "PC";
	public static final String POLIZA_ERROR_TXT            = "ER";
	public static final String POLIZA_TRAMITACION_TXT      = "TR";
	
	// Estados de pago de pagos
	public static final BigDecimal PAGO_CORRECTO               = new BigDecimal(0);
	public static final BigDecimal PAGO_INCIDENCIA             = new BigDecimal(1);
	public static final BigDecimal PAGO_ERRONEO                = new BigDecimal(2);
	public static final BigDecimal PAGO_NO_PROCESADO           = new BigDecimal(3);
	public static final BigDecimal PAGO_REGISTRO_NP            = new BigDecimal(4);
	public static final BigDecimal PAGO_ERROR_GRAVE            = new BigDecimal(5);
	public static final BigDecimal PAGO_ERROR_LEVE_ALTA        = new BigDecimal(6);
	public static final BigDecimal PAGO_PLZ_DUPLICADA          = new BigDecimal(98);
	public static final BigDecimal PAGO_ENTIDAD_NO_IRIS        = new BigDecimal(99);
	public static final BigDecimal PAGO_NO_PAGADA_MANUAL       = new BigDecimal(-1);
	public static final BigDecimal PAGO_PENDIENTE_CONFIRMACION = new BigDecimal(-2);
	
	// Perfiles usuarios
	public static final String PERFIL_USUARIO_ADMINISTRADOR       = "AGR-0";
	public static final String PERFIL_USUARIO_SEMIADMINISTRADOR   = "AGR-5";
	public static final String PERFIL_USUARIO_SERVICIOS_CENTRALES = "AGR-1";
	public static final String PERFIL_USUARIO_JEFE_ZONA           = "AGR-2";
	public static final String PERFIL_USUARIO_OFICINA             = "AGR-3";
	public static final String PERFIL_USUARIO_OTROS               = "AGR-4";
	
	public static final int COD_PERFIL_0       = 0;
	public static final int COD_PERFIL_1       = 1;
	public static final int COD_PERFIL_2       = 2;
	public static final int COD_PERFIL_3       = 3;
	public static final int COD_PERFIL_4       = 4;
	public static final int COD_PERFIL_5       = 5;
	
	public static final BigDecimal PERFIL_0 = new BigDecimal(COD_PERFIL_0);
	public static final BigDecimal PERFIL_1 = new BigDecimal(COD_PERFIL_1);
	public static final BigDecimal PERFIL_3 = new BigDecimal(COD_PERFIL_3);
	public static final BigDecimal PERFIL_5 = new BigDecimal(COD_PERFIL_5);	
	
	// Tipo de identificacion para personas: NIF o CIF.
	public static final String TIPO_IDENTIFICACION_NIF = "NIF";
	public static final String TIPO_IDENTIFICACION_NIE = "NIE";
	public static final String TIPO_IDENTIFICACION_CIF = "CIF";
	
    // TIPOS DE POLIZA
	public static final Character MODULO_POLIZA_COMPLEMENTARIO = new Character('C');
	public static final Character MODULO_POLIZA_PRINCIPAL = new Character('P');
	
	// Posibles Estados del acuse de Recibo obtenido de Agroseguro 
	public static final int ACUSE_RECIBO_ESTADO_CORRECTO    = 1;
	public static final int ACUSE_RECIBO_ESTADO_RECHAZADO   = 2;
	public static final int ACUSE_RECIBO_ESTADO_ACEPTADO_PDTE_REV_ADM = 3;
	
	// Tipos de error en el acuse de Recibo obtenido de Agroseguro
	public static final int  ACUSE_RECIBO_ERROR_RECHAZO = 1;
	public static final int  ACUSE_RECIBO_ERROR_TRAMITE = 2;
	public static final int  ACUSE_RECIBO_ERROR_INFORMATIVO = 3;
	
	// Posibles EStados de Reducci?n de Capital
	public static final Short REDUCCION_CAPITAL_ESTADO_BORRADOR        		= 1; // idem GRABADO en db
	public static final Short REDUCCION_CAPITAL_ESTADO_ENVIADO         		= 2;
	public static final Short REDUCCION_CAPITAL_ESTADO_RECIBIDO_CORRECTO 	= 3;
	public static final Short REDUCCION_CAPITAL_ESTADO_ENVIADO_ERRONEO 		= 4;
	public static final Short REDUCCION_CAPITAL_ESTADO_DEFINITIVO      		= 5;
	
	// Tipos de Servicio Web
	public static final String WS_VALIDACION         = "validacion";
	public static final String WS_CALCULO            = "calculo";
	public static final String WS_CONFIRMACION       = "confirmacion";
	public static final String WS_CARACT_EXPLOTACION = "caractExplotacion";
	public static final String WS_PASAR_DEFINITIVA   = "PD";
	public static final String WS_VALIDACION_AM      = "validacion_am";
	public static final String WS_VALIDACION_SN      = "validacion_sn";
	public static final String WS_FINANCIACION       = "financiacion";
	public static final String WS_RENDIMIENTO        = "rendimiento";
	
	// Posibles Estados Anexo de modificacion
	public static final BigDecimal ANEXO_MODIF_ESTADO_BORRADOR 	 = new BigDecimal(1);
	public static final BigDecimal ANEXO_MODIF_ESTADO_ENVIADO 	 = new BigDecimal(2);
	public static final BigDecimal ANEXO_MODIF_ESTADO_CORRECTO	 = new BigDecimal(3);
	public static final BigDecimal ANEXO_MODIF_ESTADO_ERROR 	 = new BigDecimal(4);
	public static final BigDecimal ANEXO_MODIF_ESTADO_DEFINITIVO = new BigDecimal(5);
	
	public static final String ANEXO_MODIF_ESTADO_ERROR_DESC = "Recibido error";
	public static final String ANEXO_MODIF_ESTADO_CORRECTO_DESC	 ="Recibido correcto";
	
	
	// Posibles Estados del cupon de anexo de modificacion y RC 
	public static final Long AM_CUPON_ESTADO_ABIERTO = new Long(1);
	public static final Long AM_CUPON_ESTADO_CADUCADO = new Long(2);
	public static final Long AM_CUPON_ESTADO_ERROR = new Long(3);
	public static final Long AM_CUPON_ESTADO_ERROR_RECHAZADO = new Long(4);
	public static final Long AM_CUPON_ESTADO_ERROR_TRAMITE = new Long(5);
	public static final Long AM_CUPON_ESTADO_CONFIRMADO_TRAMITE = new Long(6);
	public static final Long AM_CUPON_ESTADO_CONFIRMADO_APLICADO = new Long(7);
	
	// Posibles Estados del cupon de anexo de modificacion 
	public static final String AM_CUPON_ESTADO_ABIERTO_S = "Abierto";
	public static final String AM_CUPON_ESTADO_CADUCADO_S = "Caducado";
	public static final String AM_CUPON_ESTADO_ERROR_S = "Error";
	public static final String AM_CUPON_ESTADO_ERROR_RECHAZADO_S = "Error-Rechazado";
	public static final String AM_CUPON_ESTADO_ERROR_TRAMITE_S = "Error-Tr�mite";
	public static final String AM_CUPON_ESTADO_ERROR_TR_S = "Error-Tr";
	public static final String AM_CUPON_ESTADO_CONFIRMADO_TRAMITE_S = "Confirmado-Tr�mite";
	public static final String AM_CUPON_ESTADO_CONFIRMADO_TR_S = "Confirmado-Tr";
	public static final String AM_CUPON_ESTADO_CONFIRMADO_APLICADO_S = "Confirmado-Aplicado";
	// Tipos de envio para AM
	public static final String ANEXO_MODIF_TIPO_ENVIO_FTP = "FTP";
	public static final String ANEXO_MODIF_TIPO_ENVIO_SW = "SW";
	
	// Posibles Estados de Siniestros
	public static final Short SINIESTRO_ESTADO_PROVISIONAL          	= 1; // idem GRABADO en bd 
	public static final Short SINIESTRO_ESTADO_ENVIADO_PDT_ACEPTACION   = 2;
	public static final Short SINIESTRO_ESTADO_ENVIADO_CORRECTO 		= 3;
	public static final Short SINIESTRO_ESTADO_ENVIADO_ERROR    		= 4;
	public static final Short SINIESTRO_ESTADO_DEFINITIVO        		= 5;
	
	// caso particular siniestro por ws sin numero 
	public static final BigDecimal SINIESTRO_WS_SIN_NUMERO = new BigDecimal(-1);
	
	// Modos
	public static final String MODO_VISUALIZACION = "visualizacion";
	public static final String MODO_EDICION       = "edicion";	
	
	//Valor m?nimo para los tipos de capital de instalaciones
	public static final BigDecimal TIPOCAPITAL_INSTALACIONES_MINIMO = new BigDecimal(100);
	
	// Para el Resumen del Valor Asegurable del informe de situaci?n actualizada
	public static final String RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION = "CAPITALES PRODUCCION";
	public static final String RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES = "CAPITALES UNIDADES";
	public static final String RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS = "CAPITALES METROS CUADRADOS";
	public static final String RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES = "CAPITALES METROS LINEALES";
	public static final String RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE = "CAPITALES SUPERFICIE";
	
	public static final String RESUMEN_VALOR_ASEGURABLE_CAPITALES_PRODUCCION_KG = "Kg.";
	public static final String RESUMEN_VALOR_ASEGURABLE_CAPITALES_UNIDADES_UDS = "Uds.";
	public static final String RESUMEN_VALOR_ASEGURABLE_CAPITALES_MCUADRADOS_M2 = "m2.";
	public static final String RESUMEN_VALOR_ASEGURABLE_CAPITALES_MLINEALES_ML = "m.";
	public static final String RESUMEN_VALOR_ASEGURABLE_CAPITALES_SUPERFICIE_HA = "Ha.";
	
	public static final String RESUMEN_VALOR_ASEGURABLE_SANGRIA_DESGLOSE = "     ";
	
	// C?digo de cultivo para indicar todos los cultivos
	public static final String TODOS_CULTIVOS = "999";
	// C?digo de m?dulo para indicar todos los m?dulos
	public static final String TODOS_MODULOS = "99999";
	
	//Constantes para comprobar si en una parcela se debe rellenar el sigpac
	public static final BigDecimal USO_POLIZA = new BigDecimal(31);
	public static final BigDecimal UBICACION_SIGPAC = new BigDecimal(22);
	public static final BigDecimal PROVINCIA_SIGPAC = new BigDecimal(77);
	//Constantes para comprobar si en una parcela se debe rellenar la identificaci?n catastral
	public static final BigDecimal UBICACION_CATASTRAL = new BigDecimal(14);
	
	//Constante para controlar el maximo de numero de parcelas por hoja
	public static final int MAX_NUM_HOJA = 999;
	
	//Constante para controlar el maximo de numero de parcelas por hoja
	public static final int MAX_NUM_ELEM_OPERATOR_IN = 1000;
	
	// Constante AUTORIZACION CONTRATACIO?N
	public static final BigDecimal USO_AUT_CONTRATACION = new BigDecimal(15);
	
	//Facturacion
	public static final String FACTURA_CONSULTA = "C";
	public static final String FACTURA_IMPRESION = "I";
	
	//Cargas condicionado 
	public static final String COND_URL = "condicionado.url";
	public static final String COND_USER = "condicionado.user";
	public static final String COND_PASS = "condicionado.pass";
	public static final String COND_DIR = "condicionado.directorio";
	public static final String MENSAJE_FICHEROS_OK = "mensaje.ficheros.ok";
	public static final String ALERTA_PLAN_KO = "alerta.plan.ok";
 	public static final String ALERTA_LINEA_KO = "alerta.plan.ko";
 	public static final String ALERTA_FTP = "alerta.ftp";
 	public static final String ALERTA_TABLAS = "alerta.tablas";
 	public static final String MENSAJE_BORRADO_FICHERO_OK ="mensaje.borrado.ok";
 	public static final String ALERTA_BORRADO_FICHERO_KO ="mensaje.borrado.ko";
 	public static final String MENSAJE_BORRADO_CONDICIONADO_OK ="mensaje.borrado.condicionado.ok";
 	public static final String ALERTA_BORRADO_CONDICIONADO_KO ="mensaje.borrado.condicionado.ko";
 	public static final String ALERTA_EDITA_CONDICIONADO_KO ="mensaje.edita.condicionado.ko";
 	public static final String ALERTA_TIPO = "alerta.tipo.ko";
 	public static final String MENSAJE_CARGA_CERRRADA_OK = "mensaje.carga.cerrada.ok";
 	public static final String ALERTA_CARGA_CERRRADA_KO = "alerta.carga.cerrada.ko";
 	public static final BigDecimal ESTADO_CARGA_ABIERTA = new BigDecimal(2);
 	public static final BigDecimal ESTADO_CARGA_CERRADA = new BigDecimal(3);
 	public static final BigDecimal ESTADO_CARGA_CARGADA = new BigDecimal(1);
 	public static final BigDecimal ESTADO_CARGA_ERROR = new BigDecimal(4);
 	
 	public static final BigDecimal USO_RENDIMIENTOS = new BigDecimal(2);
 	
 	//Subvencion 20
 	public static final BigDecimal SUBVENCION20 = new BigDecimal(20);
 	//Subvencion 30
 	public static final BigDecimal SUBVENCION30 = new BigDecimal(30);
 	
 	//Subvencion 15: Entidades Asociativas
 	public static final BigDecimal SUBVENCION_ENTIDADES_ASOCIATIVAS = new BigDecimal(15);
 	//Subvencion 10: Joven Agricultor/Ganadero Hombre
 	public static final BigDecimal SUBVENCION_JOVEN_HOMBRE = new BigDecimal(10);
 	//Subvencion 11: Joven Agricultora/Ganadera Mujer
 	public static final BigDecimal SUBVENCION_JOVEN_MUJER = new BigDecimal(11);
 			
 			//Subvencion 73: Agricultor/Ganadero Profesional
 	public static final BigDecimal SUBVENCION_AG_GR_PROFESIONAL = new BigDecimal(73);
 	
 	//Conceptos principales del m?dulo para secano y regad?o.
 	public static final BigDecimal CONCEPTO_PPAL_MODULO_SECANO = new BigDecimal(27);
 	public static final BigDecimal CONCEPTO_PPAL_MODULO_REGADIO = new BigDecimal(26);
	
 	// Tipos de parcela
 	public static final Character TIPO_PARCELA_PARCELA = new Character ('P');
 	public static final Character TIPO_PARCELA_INSTALACION = new Character ('E');
 	
 	public static final String ORDEN_ASCENDENTE = "asc";
 	public static final String ORDEN_DESCENDENTE = "desc";
 	
 	public static final BigDecimal TIPOCAPITAL_REFORESTACION = new BigDecimal(12);
	public static final BigDecimal TIPOCAPITAL_PLANTONES = new BigDecimal(1);
	public static final BigDecimal TIPOCAPITAL_PRODUCCION_M2 = new BigDecimal(7);
	public static final BigDecimal TIPOCAPITAL_PRODUCCION_UDS = new BigDecimal(4);
 	
 	// Activaci?n de condicionado
 	// Tipo SC de tablasXml
 	public static final String ORGANIZADOR = "ORG";
 	public static final String COND_GENERAL = "GEN";
 	public static final String COND_PLAN_LINEA = "CPL";
 	
 	// l?nea activa
 	public static final String LINEA_ACTIVA_SI = "SI";
 	public static final String LINEA_ACTIVA_NO = "NO";
 	public static final String LINEA_ACTIVA_BLOQUEADA = "BL";
 	// l?nea importada
 	public static final String LINEA_IMPORTADA_OK = "IMPORTADO";
 	public static final String LINEA_IMPORTADA_INCOMPLETA = "INCOMPLETO";
 	public static final String LINEA_IMPORTADA_ERROR = "ERROR";
 	
 	//C?digo de l?nea gen?rica
 	public static final BigDecimal CODLINEA_GENERICA = new BigDecimal("999"); 
 	
 	// Riesgos elegidos
 	public static final String RIESGO_ELEGIDO_SI = "-1";
 	public static final String RIESGO_ELEGIDO_NO = "-2";
 	
 	// Pac cargada
 	public static final Character PAC_CARGADA_SI = new Character ('S');
 	public static final Character PAC_CARGADA_NO = new Character ('N');
 	public static final Character PAC_PROCESO_CARGA = new Character ('X');
 	public static final Character PAC_CARGADA_PDTE = new Character (' ');
 	
 	public static final Character ALTA = new Character('A');
 	public static final Character MODIFICACION = new Character('M');
 	public static final Character BAJA = new Character('B');
 	
	// Caso particular para la lista de ids de parcelas de anexo
 	public static final Character TODOSMENOSBAJA = new Character('T'); 	

 	//PARA LA NOTA INFORMATIVA DE CR ALMENDRALEJO
	public static final BigDecimal CODENTIDAD_CR_ALMENDRALEJO = new BigDecimal(3001);
	
	// Titulos informes A.M WS
	public static final String TITULO_SITUACION_ACTUAL_POLIZA = "SITUACION ACTUAL POLIZA";
	public static final String TITULO_ANEXO_MODIFICACION_PPL = "ANEXO MODIFICACION";
	public static final String TITULO_ANEXO_MODIFICACION_CPL = "ANEXO MODIFICACION COMPLEMENTARIA";
	public static final String ES_RENOVABLE = "Seguro Renovable";
	public static final String NO_ES_RENOVABLE = "Seguro No Renovable";
	
	public static final Character CHARACTER_S = 'S';
	public static final Character CHARACTER_N = 'N';
	
	public static final String TXT_FECFGARANT = "FECHA FIN GARANTIA";
	public static final String TXT_RIESGOCUBELEG = "RIESGO CUBIERTO";
	
	// TIPOS DE DATOS EN LA NOTA INFORMATIVA DE LA PoLIZA
	public static final int TIPO_NOTA_INFO_TITULO = 0;
	public static final int TIPO_NOTA_INFO_SUBTITULO = 1;
	public static final int TIPO_NOTA_INFO_TEXTO = 2;
	public static final int TIPO_NOTA_INFO_IMAGEN_SUP_IZQ = 3;
	public static final int TIPO_NOTA_INFO_IMAGEN_SUP_DER = 4;
	public static final int TIPO_NOTA_INFO_IMAGEN_INF_IZQ = 5;
	public static final int TIPO_NOTA_INFO_IMAGEN_INF_DER = 6;
	public static final int TIPO_NOTA_INFO_TEXTO_LATERAL_IZQ = 7;
	
	// CONEXION DE USUARIOS EXTERNOS
	public static final BigDecimal USUARIO_EXTERNO = new BigDecimal(1);
	public static final BigDecimal USUARIO_INTERNO = new BigDecimal(0);
	// constante que se a?ade a tipoUsuario para discernir si es externo o interno
	public static final BigDecimal NUMERO_DIEZ = new BigDecimal("10");
	// Codigo de entidad de RGA
	public static final BigDecimal CODENT_RGA = new BigDecimal(9996);
    
	public static final BigDecimal PAGO_MANUAL = new BigDecimal(1);
	public static final BigDecimal CARGO_EN_CUENTA = new BigDecimal(0);
	public static final BigDecimal DOMICILIACION_AGRO = new BigDecimal(2);
	
	public static final BigDecimal VALOR_1 = new BigDecimal(1);
	public static final BigDecimal VALOR_0 = new BigDecimal(0);
	public static final String VALOR_SI = "SI";
	public static final String VALOR_NO = "NO";
	public static final BigDecimal TODAS_SUBENTMED = new BigDecimal(9999);
	
	public static final BigDecimal PERFIL_1_EXT_INFORMES = new BigDecimal(6);
	public static final BigDecimal PERFIL_3_EXT_INFORMES = new BigDecimal(7);
	
	public static final BigDecimal ALTA_DESCUENTO = new BigDecimal(0);
	public static final BigDecimal MOD_DESCUENTO = new BigDecimal(1);
	public static final BigDecimal BAJA_DESCUENTO = new BigDecimal(2);
	
	public static final BigDecimal PLAN_2015 = new BigDecimal(2015);

	public static final Long ESTADO_POL_PCT_COMISINOES_SIN_CAMBIOS = new Long(0);
	public static final Long ESTADO_POL_PCT_COMISINOES_REVISADA    = new Long(1);
	public static final Long ESTADO_POL_PCT_COMISINOES_RESTAURADA  = new Long(2);
	
	//Descuento o recargo en la administraci?n de colectivos
	public static final int TIPO_DESC   = 0;
	public static final int TIPO_RECARG = 1;
	
	//Mantenimiento de descuentos: Indica si se puede aplicar recargos sobre la p?liza asociada 
	public static final int PERMITIR_RECARGO_NO =0;//No
	public static final int PERMITIR_RECARGO_SI =1;//Si
	public static final String PERMITIR_RECARGO_NO_TXT ="No";//No
	public static final String PERMITIR_RECARGO_SI_TXT ="SI";//Si
	
	
	//Mantenimiento de descuentos: Indica si se puede mostrar los datos de comisiones de la p?liza asociada 
	public static final int VER_COMISIONES_NO =0;//Ninguna
	public static final int VER_COMISIONES_ENTIDAD =1;//Entidad
	public static final int VER_COMISIONES_ENTIDAD_MEDIADORA =2;//E-S Mediadora
	public static final int VER_COMISIONES_TODAS =3;//Todas
	
	public static final String VER_COMISIONES_NO_TXT ="Ninguna";
	public static final String VER_COMISIONES_ENTIDAD_TXT ="Entidad";
	public static final String VER_COMISIONES_ENTIDAD_MEDIADORA_TXT ="E-S Mediadora";
	public static final String VER_COMISIONES_TODAS_TXT ="Todas";
	
	
	//Todas las oficinas
	public static final BigDecimal TODAS_OFICINAS = new BigDecimal(9999);
	
	//Ninguna oficina. Ponemos el valor -1 para no incumplir las restricciones de base de datos
	public static final BigDecimal SIN_OFICINA = new BigDecimal(-1);
	public static final String SIN_OFICINA_NOMBRE="Todas";
	
	// Formas de pago
	public final static Character FORMA_PAGO_ALCONTADO = 'C';
	public static final Character FORMA_PAGO_FINANCIADO = 'F';
	
	public static final Long PLZ_RENOV_AGP_PENDIENTE_ASIG_GASTOS = 1L;
	public static final Long PLZ_RENOV_AGP_GASTOS_ASIGNADOS = 2L;	
	
	//Origen llamada
	public static final String ORIGEN_LLAMADA_MENU_GENERAL = "menuGeneral";
	public static final String ORIGEN_LLAMADA_EDITAR = "editar";
	public static final String ORIGEN_LLAMADA_ALTA="alta";
	public static final String ORIGEN_LLAMADA_MODIFICAR="modificar";
	public static final String ORIGEN_LLAMADA_CONSULTAR="consultar";
	public static final String ORIGEN_LLAMADA_BORRAR="borrar";
	public static final String ORIGEN_LLAMADA_PAGINACION="paginacion";
	public static final String ORIGEN_LLAMADA_INFORME_IMPAGADOS = "informeImpagados";
	public static final String ORIGEN_LLAMADA_INFORME_IMPAGADOS_2015 = "informeImpagados2015";//2015 o mayor
	
	public static final BigDecimal PROVINCIA_GENERICA = new BigDecimal (99);
	public static final BigDecimal COMARCA_GENERICA = new BigDecimal (99);
	public static final BigDecimal TERMINO_GENERICO = new BigDecimal (999);
	public static final Character SUBTERMINO_GENERICO = new Character ('9');
	
	// Identificadores de las pantallas configurables
	public static final Long PANTALLA_EXPLOTACIONES = 101L;
	public static final Long PANTALLA_POLIZA        = 7L;
	public static final Long PANTALLA_INSTALACION   = 9L;
	
		
	
	// Financiaci?n
	public static final int FINANCIACION_AGROSEGURO = 1;
	public static final int FINANCIACION_SAECA = 0;
	
	// Subvenciones socios Ganado
	public static final BigDecimal CARACT_ASEGURADO_PERSONA_JURIDICA = new BigDecimal ("3");
	
	//TODOS
	public static final Integer TODAS_ESPECIES=new Integer(999);
	public static final Integer TODOS_REGIMEN_MANEJO=new Integer(999);
	public static final Integer TODOS_GRUPOS_RAZAS=new Integer(999);
	public static final Integer TODOS_TIPO_ANIMAL=new Integer(999);
	public static final Integer TODAS_TIPOS_CAPITAL=new Integer(999);
	
	//Claves estandar mapa parametros
	public static final String KEY_ALERTA = "alerta";
	public static final String KEY_MENSAJE = "mensaje";
	public static final String KEY_DATOS_VARIABLES = "listaDV";
	public static final String KEY_ORIGEN_LISTADO_ANX_MOD = "vieneDeListadoAnexosMod";
	
	//Claves para la importaci?n de ficheros de comisiones
	public static final String ESTADO_AJAX_DONE = "DONE";
	public static final String ESTADO_AJAX_WARN = "WARN";
	public static final String ESTADO_AJAX_ERROR_DUPLICADO = "DUPLICADO";
	public static final String ESTADO_AJAX_ERROR_GENERICO = "FAILED";
	public static final String CLAVE_ID_FICHERO = "idFichero";
	public static final String CLAVE_ID_ERROR = "error";
	public static final Long WARN_ID_VALIDACION = -1L; //En validaci?n
	public static final Long WARN_ID_TABLA_INFORMES = -2L; //En inserci?n tabla de informes
	
	// Claves de ficheros de Comisiones
	public final static int FICHERO_COMISIONES=1;
	public final static int FICHERO_IMPAGADOS=2;
	public final static int FICHERO_REGLAMENTO=3;
	public final static int FICHERO_EMITIDOS=4;
	public final static int FICHERO_DEUDA=5;
	public final static int FICHERO_UNIFICADO_GASTOS_RECIBOS_EMITIDOS=6;
	public final static int FICHERO_UNIFICADO_GASTOS_RECIBOS_IMPAGADOS=7;
	public final static int FICHERO_UNIFICADO_GASTOS_RECIBOS_DEUDA_APLAZADA=8;
	public final static int FICHERO_UNIFICADO_GASTOS_ENTIDAD_UNIFICADO=9;
	public final static Character FICHERO_UNIFICADO_ESTADO_CARGADO = new Character('X');
	public final static Character FICHERO_UNIFICADO_ESTADO_AVISO= new Character('A');
	public final static Character FICHERO_UNIFICADO_ESTADO_ERRONEO= new Character('E');
	public final static Character FICHERO_UNIFICADO_ESTADO_CORRECTO= new Character('C');
	
	public final static Character COLECTIVO_ACTIVO= new Character('1');
	public final static Character COLECTIVO_NO_ACTIVO= new Character('0');
	
	public final static Integer COLECTIVO_AGRO_OK = Integer.valueOf(1);
	public final static Integer COLECTIVO_AGRO_KO = Integer.valueOf(2);
	public final static Integer COLECTIVO_AGRO_RECH = Integer.valueOf(3);
	
	// Estados AGROSEGURO polizas renovables
	public static final BigDecimal ES_POL_REN_AGSEGURO_BORRADOR_PRECARTERA       = new BigDecimal(1);
	public static final BigDecimal ES_POL_REN_AGSEGURO_PRIMERA_COMUNICACION      = new BigDecimal(2);
	public static final BigDecimal ES_POL_REN_AGSEGURO_COMUNICACION_DEFINITIVA   = new BigDecimal(3);
	public static final BigDecimal ES_POL_REN_AGSEGURO_EMITIDA 					 = new BigDecimal(4);
	public static final BigDecimal ES_POL_REN_AGSEGURO_RESCINDIDA 				 = new BigDecimal(5);
	public static final BigDecimal ES_POL_REN_AGSEGURO_ANULADA 					 = new BigDecimal(6);
	public static final BigDecimal ES_POL_REN_AGSEGURO_CON_GASTOS_SIN_RENOVACION = new BigDecimal(7);
	public static final BigDecimal ES_POL_REN_AGSEGURO_PRECARTERA_PRECALCULADA   = new BigDecimal(8);
	public static final BigDecimal ES_POL_REN_AGSEGURO_PRECARTERA_GENERADA       = new BigDecimal(9);
	
	// Estados AGROPLUS polizas renovables
	public static final BigDecimal ES_POL_REN_AGROPLUS_PEND_ASIGNAR_GASTOS       = new BigDecimal(1);
	public static final BigDecimal ES_POL_REN_AGROPLUS_GASTOS_ASIGNADOS          = new BigDecimal(2);
	public static final BigDecimal ES_POL_REN_AGROPLUS_ENVIADA_PEND_CONFIRMAR    = new BigDecimal(3);
	public static final BigDecimal ES_POL_REN_AGROPLUS_ENVIADA_CORRECTA 		 = new BigDecimal(4);
	public static final BigDecimal ES_POL_REN_AGROPLUS_ENVIADA_ERRONEA   		 = new BigDecimal(5);

	// Estados envio IBAN polizas renovables
	public static final BigDecimal ES_POL_REN_ENVIO_IBAN_NO        = new BigDecimal(1);
	public static final BigDecimal ES_POL_REN_ENVIO_IBAN_PREPARADO = new BigDecimal(2);
	public static final BigDecimal ES_POL_REN_ENVIO_IBAN_ENVIADO   = new BigDecimal(3);
	public static final BigDecimal ES_POL_REN_ENVIO_IBAN_CORRECTO  = new BigDecimal(4);
	public static final BigDecimal ES_POL_REN_ENVIO_IBAN_ERRONEO   = new BigDecimal(5);
	
	public static final int DIAS_FINANCIADA = 15;
	public static final Character GRUPO_NEGOCIO_GENERICO = new Character('9');
	public static final Character GRUPO_NEGOCIO_VIDA = new Character('1');
	public static final Character GRUPO_NEGOCIO_RYD = new Character('2');
	//Tipos de carga de explotaciones
	public static final Integer ID_CARGA_EXPLOT_AGR = null;			//	Para p?lizas de l?neas de agr?cola.
	public final static Integer ID_CARGA_EXPLOT_GAN_ANTERIORES=	-1;		//	Para p?lizas de l?neas de ganado dadas de alta antes de la implementaci?n de este cambio.
	public final static Integer ID_CARGA_EXPLOT_NO_CARGAR_NINGUNA= 0; 	//	Para p?lizas de l?neas de ganado en las cuales se ha elegido en el alta la opci?n ?No cargar ninguna? en la pantalla de ?Carga de explotaciones.?
	public final static Integer ID_CARGA_EXPLOT_SISTEMA_TRADICIONAL=1;	//	Para p?lizas de l?neas de ganado en las cuales se ha elegido en el alta la opci?n ?P?liza anterior del sistema tradicional? en la pantalla de ?Carga de explotaciones.?
	public final static Integer ID_CARGA_EXPLOT_SITUACION_ACTUALIZADA=2;//	Para p?lizas de l?neas de ganado en las cuales se ha elegido en el alta la opci?n ?Situaci?n actualizada de Agroseguro? en la pantalla de ?Carga de explotaciones.?
	public final static Integer ID_CARGA_EXPLOT_ORIGINAL=	3;			//	Para p?lizas de l?neas de ganado en las cuales se ha elegido en el alta la opci?n ?P?liza original de los 3 ?ltimos planes? en la pantalla de ?Carga de explotaciones.?
	public final static Integer ID_CARGA_EXPLOT_EXISTENTE=	4;			//	Para p?lizas de l?neas de ganado en las cuales se ha elegido en el alta la opci?n ?P?liza existente del plan actual? en la pantalla de ?Carga de explotaciones.?

	public final static Long TIPO_RDTO_MAXIMO=3L;
	public final static Long TIPO_RDTO_HISTORICO=1L;
	public final static Long TIPO_RDTO_HISTORICO_SIN_ACTUALIZAR=2L;
	public final static Long TIPO_RDTO_SIN_RENDIMIENTO_ASIGNADO=4L;
	public final static Long TIENE_RDTO_HISTORICO=1L;
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	public static final String ORGANISMO_ENESA = "0";
	public static final int ASEGURADO_SUBVENCIONABLE = 0;
	
	// Origen de llamadas para la obtencion de datos asgurado en agroseguro
	public static final String ORIGEN_CARGA = "carga";
	public static final String ORIGEN_ADMIN = "admin";
	
	public static final String VALIDACION = "VA";
	public static final String PASAR_DEFINITIVA = "PD";
	public static final String ANEXO_MODIFICACION = "AM";
	public static final String SINIESTRO = "SN";
	
	public static final BigDecimal LINEA_415 = new BigDecimal("415");
	public static final Integer CAPITAL_ASEGURADO_TIPO_15 = 15;
	
	public static final BigDecimal CERO = new BigDecimal("0");
	public static final BigDecimal CIEN = new BigDecimal("100");
	
	public final static Integer DOC_NO_VISIBLE = new Integer("0");
	public final static Integer DOC_VISIBLE = new Integer("1");
	
	public static final BigDecimal ESTADO_RC_SIMULACION = new BigDecimal("0");
	
	public static final BigDecimal ESTADO_INC_LIMBO = new BigDecimal("9");
	
	public static final int EST_GED_GENERANDO = -1;
	public static final int EST_GED_PDTE = 0;
	public static final int EST_GED_OK = 1;
	public static final int EST_GED_ERROR = 2;
	
	public final static String ENTIDAD_C616 = "C616";
	
	
	
	public final static Long TIPO_MEDIADOR_OPERADOR_BANCA_SEGURO = 1L;
	public final static Long TIPO_MEDIADOR_COLABORADOR_EXTERNO = 2L;
	public static final String RAZON_SOCIAL_RGA_MEDIACION = "RGA Mediacion, Operador de Banca Seguros Vinculado, S.A.";

	public final static Long CANAL_FIRMA_PDTE = 1L;
	public final static Long CANAL_FIRMA_PAPEL = 2L;
	public final static Long CANAL_FIRMA_TABLETA = 3L;
	public final static Long CANAL_FIRMA_DIFERIDA = 4L;
	

	public static final String STRING_S = String.valueOf(CHARACTER_S);
	public static final String STRING_N = String.valueOf(CHARACTER_N);
	
	public static final String STRING_NA = "N/A";
	
	//P0079361
	public static final String STR_A = "A";
	public static final String STR_EMPTY = "";
	public static final String STR_FECHA_INI = "01/01/1900";
	public static final String REDUCCION_CAPITAL = "RC";
	
	public static final String STR_fdaniosHasta="fdaniosHasta";
	public static final String STR_fenvHasta="fenvHasta";
	public static final String STR_fenvpolHasta="fenvpolHasta";
	
	public static final String STR_ESTADO_KEY = "estado";
	public static final Long POLIZA_VALOR_VACIO = new Long(0);
	//P0079361
}