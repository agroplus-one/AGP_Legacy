package com.rsi.agp.core.util;

import java.math.BigDecimal;

public class ConstantsInf {
	
	// -------------------------------
	// -- MANTENIMIENTO DE INFORMES --
	// -------------------------------	
	// Códigos de formato de exportación de informes
	public static final int COD_FORMATO_PDF = 2;
	public static final int COD_FORMATO_XLS = 3;
	public static final int COD_FORMATO_HTML = 1;
	public static final int COD_FORMATO_CSV = 0;
	public static final int COD_FORMATO_TXT = 4;
	
	// Etiquetas para el formato de exportación de informes
	public static final String FORMATO_PDF = "PDF";
	public static final String FORMATO_XLS = "XLS";
	public static final String FORMATO_HTML = "HTML";
	public static final String FORMATO_CSV = "CSV";
	public static final String FORMATO_TXT = "TXT";
	
	// Códigos para la orientación de las páginas del informe
	public static final int COD_ORIENTACION_VERTICAL = 0;
	public static final int COD_ORIENTACION_HORIZONTAL = 1;
	
	// Códigos para el campo 'Cuenta'
	public static final int COD_CUENTA_NO = 0;
	public static final int COD_CUENTA_SI = 1;
	
	// Etiquetas para el formato de exportación de informes
	public static final String ORIENTACION_VERTICAL = "VERTICAL";
	public static final String ORIENTACION_HORIZONTAL = "HORIZONTAL";
	
	// Códigos para la visibilidad del informe
	public static final int COD_VISIBILIDAD_TODOS = 0;
	public static final int COD_VISIBILIDAD_PERFIL = 1;
	public static final int COD_VISIBILIDAD_USUARIOS = 2;
	public static final int COD_VISIBILIDAD_ENTIDADES_NO = 0;
	public static final int COD_VISIBILIDAD_ENTIDADES_SI = 1;
	
	// Etiquetas para la visibilidad del informe
	public static final String VISIBILIDAD_TODOS = "Todos";
	public static final String VISIBILIDAD_PERFIL = "Perfil";
	public static final String VISIBILIDAD_USUARIOS = "Usuario(s)";
	public static final String VISIBILIDAD_ENTIDADES = "Entidad(es)";
	
	// Caracter separador de items para las lupas de usuarios y entidades
	public static final String CHR_SEPARADOR_LUPAS = "#";
	
	// ----------------------------------------------
	// -- MANTENIMIENTO DE REGISTROS DEL INFORME --
	// ----------------------------------------------
	// DAA 20/02/2013 Maximo de registros permitidos para el excel
	public static final int MAX_REG_EXCEL = 65535;
	
	// ------------------------------------------------------------
	// -- MANTENIMIENTO DE CAMPOS PERMITIDOS Y CAMPOS CALCULADOS --
	// ------------------------------------------------------------
	// Códigos de formato de campos permitidos tipo fecha
	public static final int COD_FORMATO_FECHA_DDMMYYYY = 0; // Para fechas en formato día/mes/año
	public static final int COD_FORMATO_FECHA_YYYYMMDD = 1; // Para fechas en formato año/mes/día
	
	
	// Códigos de formato de campos permitidos y calculados tipo numérico	
	public static final int COD_FORMATO_NUM_NNNN = 2;		// Para numéricos sin decimales y sin separador de miles
	public static final int COD_FORMATO_NUM_N_NNN = 3;		// Para numéricos sin decimales y con separador de miles
	public static final int COD_FORMATO_NUM_NNNN_DD = 4;	// Para numéricos con decimales y sin separador de miles
	public static final int COD_FORMATO_NUM_N_NNN_DD = 5;	// Para numéricos con decimales y con separador de miles.
	public static final int COD_FORMATO_NUM_N_NNN_DD_RIGHT = 6;	// Para numéricos con decimales y con separador de miles.Alineacion izq.
	public static final int COD_FORMATO_NUM_NNNN_DD_RIGHT = 7;	// Para numéricos con decimales y sin separador de miles.Alineacion izq.
	
	
	
	// Formato de campos permitidos tipo fecha
	public static final String FORMATO_FECHA_DDMMYYYY = "DD/MM/YYYY"; // Para fechas en formato día/mes/año
	public static final String FORMATO_FECHA_YYYYMMDD = "YYYY/MM/DD"; // Para fechas en formato año/mes/día
	public static final String FORMATO_FECHA_YYYYMMDD_HHMMSS = "yyyyMMdd_HHmmss";

	// Formato de campos permitidos y calculados tipo numérico	
	public static final String FORMATO_NUM_NNNN = "NNNN";			// Para numéricos sin decimales y sin separador de miles
	public static final String FORMATO_NUM_N_NNN = "N.NNN";			// Para numéricos sin decimales y con separador de miles
	public static final String FORMATO_NUM_NNNN_DD = "NNNN,DD";		// Para numéricos con decimales y sin separador de miles
	public static final String FORMATO_NUM_N_NNN_DD = "N.NNN,DD";	// Para numéricos con decimales y con separador de miles.
	
	// Códigos para totalizar por campo
	public static final int COD_TOTALIZA_NO = 0;
	public static final int COD_TOTALIZA_SUMA = 1;
	public static final BigDecimal COD_BIG_TOTALIZA_NO = new BigDecimal(0);
	public static final BigDecimal COD_BIG_TOTALIZA_SUMA = new BigDecimal(1);
	public static final BigDecimal COD_BIG_TOTALIZA_CUENTA = new BigDecimal(2);
	
	// Códigos para indicar si se hace el total por grupo
	public static final int COD_TOTAL_POR_GRUPO_NO = 0;
	public static final int COD_TOTAL_POR_GRUPO_SI = 1;
	public static final BigDecimal COD_BIG_TOTAL_POR_GRUPO_NO = new BigDecimal(0);
	public static final BigDecimal COD_BIG_TOTAL_POR_GRUPO_SI = new BigDecimal(1);
	
	
	// Códigos para los operadores aritméticos posibles para los campos calculados
	public static final int COD_OPERADOR_ARIT_SUMA = 0;
	public static final int COD_OPERADOR_ARIT_RESTA = 1;
	public static final int COD_OPERADOR_ARIT_MULT = 2;
	public static final int COD_OPERADOR_ARIT_DIV = 3;
	
	// Etiquetas para los operadores aritméticos posibles para los campos calculados
	public static final String OPERADOR_ARIT_SUMA = "+";
	public static final String OPERADOR_ARIT_RESTA = "-";
	public static final String OPERADOR_ARIT_MULT = "*";
	public static final String OPERADOR_ARIT_DIV = "/";
	
	// ---------------------------------
	// -- MANTENIMIENTO DE OPERADORES --
	// ---------------------------------
	// Códigos para los operadores de base de datos posibles para los campos
	public static final int COD_OPERADOR_CAD_CONTIENEN 		 = 0;
	public static final int COD_OPERADOR_BD_CAD_EMPIEZAN_POR = 1;
	public static final int COD_OPERADOR_BD_CAD_TERMINAN_POR = 2;
	public static final int COD_OPERADOR_BD_CONTENIDO_EN 	 = 3;
	public static final int COD_OPERADOR_BD_ENTRE 			 = 4;
	public static final int COD_OPERADOR_BD_IGUAL 			 = 5;
	public static final int COD_OPERADOR_BD_MAYOR_IGUAL_QUE  = 6;
	public static final int COD_OPERADOR_BD_MAYOR_QUE 		 = 7;
	public static final int COD_OPERADOR_BD_MENOR_IGUAL_QUE  = 8;
	public static final int COD_OPERADOR_BD_MENOR_QUE 		 = 9;
	
	// Etiquetas para los operadores de base de datos posibles para los campos
	public static final String OPERADOR_BD_CAD_CONTIENEN 	= "Cadenas que contienen";
	public static final String OPERADOR_BD_CAD_EMPIEZAN_POR = "Cadenas que empiezan con";
	public static final String OPERADOR_BD_CAD_TERMINAN_POR = "Cadenas que terminan con";
	public static final String OPERADOR_BD_CONTENIDO_EN 	= "Contenido en";
	public static final String OPERADOR_BD_ENTRE 			= "Entre";
	public static final String OPERADOR_BD_IGUAL 			= "Igual";
	public static final String OPERADOR_BD_MAYOR_IGUAL_QUE 	= "Mayor o igual que";
	public static final String OPERADOR_BD_MAYOR_QUE 		= "Mayor que";
	public static final String OPERADOR_BD_MENOR_IGUAL_QUE 	= "Menor o igual que";
	public static final String OPERADOR_BD_MENOR_QUE 		= "Menor que";
	
	
	// --------------------------------------------------------
	// -- MANTENIMIENTO DE CLASIFICACIÓN Y RUPTURA DE CAMPOS --
	// --------------------------------------------------------
	// Códigos para la ordenación de los campos
	public static final BigDecimal COD_ORDENACION_ASC = new BigDecimal(0);
	public static final BigDecimal COD_ORDENACION_DESC = new BigDecimal(1);
	
	// Etiquetas para la ordenación de los campos
	public static final String ORDENACION_ASC = "ASCENDENTE";
	public static final String ORDENACION_DESC = "DESCENDENTE";
	
	// Códigos para la ruptura/agrupación por campos
	public static final BigDecimal COD_RUPTURA_NO = new BigDecimal(0);
	public static final BigDecimal COD_RUPTURA_SI = new BigDecimal(1);
	
	
	// ------------------------------------------
	// -- GESTIÓN DE VISTAS Y CAMPOS DE VISTAS --
	// ------------------------------------------
	// Código que indica si una vista o un campo de una vista es visible en la aplicación
	public static final BigDecimal VISIBLE_NO = new BigDecimal(0);
	public static final BigDecimal VISIBLE_SI = new BigDecimal(1);
	
	// Códigos de los posibles tipos de campos de las vistas
	public static final int CAMPO_TIPO_NUMERICO = 2;
	public static final int CAMPO_TIPO_TEXTO = 0;
	public static final int CAMPO_TIPO_FECHA = 1;
	
	// Valores de los posibles tipos de campos de las vistas
	public static final String CAMPO_TIPO_NUMERICO_STR = "Num&eacute;rico";
	public static final String CAMPO_TIPO_TEXTO_STR = "Alfanum&eacute;rico";
	public static final String CAMPO_TIPO_FECHA_STR = "Fecha";
	
	// -------------------------------------------------------
	// -- MANTENIMIENTO DE ENTIDADES CON ACCESO RESTRINGIDO --
	// -------------------------------------------------------
	// Códigos para permitir o denegar el acceso
	public static final BigDecimal ACCESO_DENEGADO = new BigDecimal(0);
	public static final BigDecimal ACCESO_PERMITIDO = new BigDecimal(1);
	public static final BigDecimal ACCESO_PERMITIDO_USU_PER_CONCRETOS = new BigDecimal(2);
	
	// ----------------------------------------------
	// -- MANTENIMIENTO DE CONDICIONES DEL INFORME --
	// ----------------------------------------------
	// Códigos de origenes de datos
	public static final int OD_VALOR_LIBRE = 0;
	public static final int OD_ESTADO_POLIZA = 1;
	public static final int OD_ESTADO_SINIESTRO = 2;
	public static final int OD_ESTADO_ANEXO_MOD = 3;
	public static final int OD_ESTADO_ANEXO_RED = 4;
	//Pet. 50777 (02.04.2019) //
	public static final int OD_ESTADO_AGRO = 5;
	public static final int OD_ESTADO_AGRO_AM = 6;
	
	// ----------------------------
	// -- GENERACIÓN DE INFORMES --
	// ----------------------------
	// Códigos que indica si se filtra por el campo o no
	public static final BigDecimal FILTRO_NO = new BigDecimal(0);
	public static final BigDecimal FILTRO_SI = new BigDecimal(1);
	
	// ---------------------
	// -- INFORME RECIBOS --
	// ---------------------
	public static final BigDecimal OCULTO_NO = new BigDecimal(0);
	
	// --------------------------------------------------
	// -- MENSAJES PARA LOS MANTENIMIENTOS DE INFORMES --
	// --------------------------------------------------
	
	// Clave de properties correspondiente al esquema donde están los orígenes de datos para generar informes
	public static final String ESQUEMA_GENERACION_INFORMES	= "esquema.generacion.informes";
	
	// Clave de properties correspondiente al esquema donde están los orígenes de datos para generar informes
	public static final String MAX_COLUMNAS_PDF_VERTICAL	= "maxcolumnas.pdf.vertical";
	public static final String MAX_COLUMNAS_PDF_HORIZONTAL	= "maxcolumnas.pdf.horizontal";
	
	// Mantenimiento de campos permitidos
	public static final String MSG_CAMPOPERMITIDO_ALTA_OK	= "mensaje.campoPermitido.alta.ok";
	public static final String MSG_CAMPOPERMITIDO_MODIF_OK	= "mensaje.campoPermitido.modificacion.ok";
	public static final String MSG_CAMPOPERMITIDO_BAJA_OK	= "mensaje.campoPermitido.baja.ok";
	public static final String ALERTA_CAMPOPERMITIDO_ALTA_KO	= "alerta.campoPermitido.alta.ko";
	public static final String ALERTA_CAMPOPERMITIDO_ALTA_EXISTE_KO	= "alerta.campoPermitido.alta.existe.ko";
	public static final String ALERTA_CAMPOPERMITIDO_ABREVIADO_EXISTE_KO = "alerta.campoPermitido.abreviado.existe.ko";
	public static final String ALERTA_CAMPOPERMITIDO_MODIF_KO	= "alerta.campoPermitido.modificacion.ko";
	public static final String ALERTA_CAMPOPERMITIDO_BAJA_KO	= "alerta.campoPermitido.baja.ko";
	public static final String ALERTA_CAMPOPERMITIDO_BAJA_DEPENDENCIAS_KO	= "alerta.campoPermitido.baja.dependencias.ko";
	
	// Mantenimiento de operadores
	public static final String MSG_OPERADOR_ALTA_OK		= "mensaje.operador.alta.ok";
	public static final String MSG_OPERADOR_MODIF_OK	= "mensaje.operador.modificacion.ok";
	public static final String MSG_OPERADOR_BAJA_OK		= "mensaje.operador.baja.ok";
	public static final String ALERTA_OPERADOR_ALTA_KO	= "alerta.operador.alta.ko";
	public static final String ALERTA_OPERADOR_ALTA_EXISTE_KO = "alerta.operador.alta.existe.ko";
	public static final String ALERTA_OPERADOR_MODIF_KO	= "alerta.operador.modificacion.ko";
	public static final String ALERTA_OPERADOR_CONDICION_EXISTE_KO = "alerta.operador.condicion.existe.ko";
	public static final String ALERTA_OPERADOR_BAJA_KO	= "alerta.operador.baja.ko";
	
	// Mantenimiento de campos calculados
	public static final String MSG_CAMPOCALCULADO_ALTA_OK	= "mensaje.campoCalculado.alta.ok";
	public static final String MSG_CAMPOCALCULADO_MODIF_OK	= "mensaje.campoCalculado.modificacion.ok";
	public static final String MSG_CAMPOCALCULADO_BAJA_OK	= "mensaje.campoCalculado.baja.ok";
	public static final String ALERTA_CAMPOCALCULADO_BAJA_KO_EXISTE_INFORME	= "alerta.campoCalculado.baja.ko.existeinforme";
	public static final String ALERTA_CAMPOCALCULADO_ALTA_KO	= "alerta.campoCalculado.alta.ko";
	public static final String ALERTA_CAMPOCALCULADO_MODIF_KO	= "alerta.campoCalculado.modificacion.ko";
	public static final String ALERTA_CAMPOCALCULADO_BAJA_KO	= "alerta.campoCalculado.baja.ko";
	public static final String ALERTA_CAMPOCALCULADO_EXISTE_ALTA_KO = "alerta.campoCalculado.alta.existe.ko";
	
	
	// Mantenimiento de informes
	public static final String MSG_INFORME_ALTA_OK	= "mensaje.informe.alta.ok";
	public static final String MSG_INFORME_MODIF_OK	= "mensaje.informe.modificacion.ok";
	public static final String MSG_INFORME_BAJA_OK	= "mensaje.informe.baja.ok";
	public static final String ALERTA_INFORME_ALTA_KO	= "alerta.informe.alta.ko";
	public static final String ALERTA_INFORME_ALTA_EXISTE_KO	= "alerta.informe.alta.existe.ko";
	public static final String ALERTA_INFORME_MODIF_KO	= "alerta.informe.modificacion.ko";
	public static final String ALERTA_INFORME_BAJA_KO	= "alerta.informe.baja.ko";
	public static final String ALERTA_INFORME_DUPLICAR_OK	= "alerta.informe.duplicar.ok";
	public static final String ALERTA_INFORME_DUPLICAR_KO	= "alerta.informe.duplicar.ko";
	
	
	// Mantenimiento de datos del informe
	public static final String MSG_DATOSINFORME_ALTA_OK	= "mensaje.datoInforme.alta.ok";
	public static final String MSG_DATOSINFORME_MODIF_OK	= "mensaje.datoInforme.modificacion.ok";
	public static final String MSG_DATOSINFORME_MODIF_ORDEN_OK	= "mensaje.datoInforme.modificacion.orden.ok";
	public static final String MSG_DATOSINFORME_BAJA_OK	= "mensaje.datoInforme.baja.ok";
	public static final String ALERTA_DATOSINFORME_EXISTE_KO	= "alerta.datoInforme.alta.existe.ko";
	public static final String ALERTA_DATOSINFORME_BAJA_KO_EXISTE_CONDICION	= "alerta.datoInforme.baja.ko.existecondicion";
	public static final String ALERTA_DATOSINFORME_ALTA_KO	= "alerta.datoInforme.alta.ko";
	public static final String ALERTA_DATOSINFORME_MODIF_KO	= "alerta.datoInforme.modificacion.ko";
	public static final String ALERTA_DATOSINFORME_MODIF_ORDEN_KO	= "alerta.datoInforme.modificacion.orden.ko";
	public static final String ALERTA_DATOSINFORME_BAJA_KO	= "alerta.datoInforme.baja.ko";
	public static final String ALERT_CONSULTAR_DATOS_INFORME_KO  ="alerta.datoInforme.consultar.ko";
	public static final String ALERT_BORRAR_DATOS_INFORME_KO     		="alerta.datoInforme.baja.ko";
	
	// Mantenimiento de condiciones del informe
	
	public static final String MSG_CONDICIONINFORME_ALTA_OK	= "mensaje.condicionInforme.alta.ok";
	public static final String MSG_CONDICIONINFORME_MODIF_OK	= "mensaje.condicionInforme.modificacion.ok";
	public static final String MSG_CONDICIONINFORME_BAJA_OK	= "mensaje.condicionInforme.baja.ok";
	public static final String ALERTA_CONDICIONINFORME_EXISTE_KO	= "alerta.condicionInforme.alta.existe.ko";
	public static final String ALERTA_CONDICIONINFORME_ALTA_KO	= "alerta.condicionInforme.alta.ko";
	public static final String ALERTA_CONDICIONINFORME_MODIF_KO	= "alerta.condicionInforme.modificacion.ko";
	public static final String ALERTA_CONDICIONINFORME_BAJA_KO	= "alerta.condicionInforme.baja.ko";
	public static final String CODIGO_CONDICIONES_SQL_CAMPOS_CALCULADOS 	= "1";
	public static final String CODIGO_CONDICIONES_SQL_CAMPOS_PERMITIDOS	= "2";
	
	
	// Mantenimiento de condiciones del informe
	public static final String MSG_CLASIFRUPTURAINFORME_ALTA_OK	= "mensaje.clasifRupturaInforme.alta.ok";
	public static final String MSG_CLASIFRUPTURAINFORME_MODIF_OK	= "mensaje.clasifRupturaInforme.modificacion.ok";
	public static final String MSG_CLASIFRUPTURAINFORME_BAJA_OK	= "mensaje.clasifRupturaInforme.baja.ok";
	public static final String ALERTA_CLASIFRUPTURAINFORME_BAJA_KO	= "alerta.clasifRupturaInforme.baja.ko";
	public static final String ALERTA_CLASIFRUPTURAINFORME_ALTA_KO	= "alerta.clasifRupturaInforme.alta.ko";
	public static final String ALERTA_CLASIFRUPTURAINFORME_MODIF_KO	= "alerta.clasifRupturaInforme.modificacion.ko";
	public static final String ALERTA_CLASIFRUPTURAINFORME_EXISTE_KO	= "alerta.clasifRupturaInforme.existe.ko";
	
	// Mantenimiento de entidades con acceso restringido
	public static final String MSG_ENT_ACCESO_RESTRINGIDO_ALTA_OK	= "mensaje.entAccesoRestrig.alta.ok";
	public static final String MSG_ENT_ACCESO_RESTRINGIDO_MODIF_OK	= "mensaje.entAccesoRestrig.modificacion.ok";
	public static final String MSG_ENT_ACCESO_RESTRINGIDO_BAJA_OK	= "mensaje.entAccesoRestrig.baja.ok";
	public static final String ALERTA_ENT_ACCESO_RESTRINGIDO_BAJA_KO	= "alerta.entAccesoRestrig.baja.ko";
	public static final String ALERTA_ENT_ACCESO_RESTRINGIDO_ALTA_KO	= "alerta.entAccesoRestrig.alta.ko";
	public static final String ALERTA_ENT_ACCESO_RESTRINGIDO_MODIF_KO	= "alerta.entAccesoRestrig.modificacion.ko";
	public static final String ALERTA_ENT_ACCESO_RESTRINGIDO_ALTA_EXISTE_KO	= "alerta.entAccesoRestrig.alta.existe.ko";
	public static final String ALERTA_ENT_ACCESO_RESTRINGIDO_ENT_NO_EXISTE	= "alerta.entAccesoRestrig.ent.no.existe";
	
	//Tipo campo
	public static final BigDecimal CAMPO_CALCULADO = new BigDecimal(1);
	public static final BigDecimal CAMPO_PERMITIDO = new BigDecimal(2);
	
	public static final BigDecimal NOTA_INF_AGRO = new BigDecimal(1);
	public static final BigDecimal NOTA_INF_GAN = new BigDecimal(2);
	public static final BigDecimal NOTA_INF_RC = new BigDecimal(3);
	public static final BigDecimal NOTA_INF_SBP = new BigDecimal(4);
	public static final BigDecimal NOTA_INF_AGRO_AV = new BigDecimal(5);
	public static final BigDecimal NOTA_INF_GAN_AV = new BigDecimal(6);
	public static final BigDecimal NOTA_INF_RC_AV = new BigDecimal(7);
	public static final BigDecimal NOTA_INF_SBP_AV = new BigDecimal(8);
}
