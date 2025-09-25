package com.rsi.agp.core.util;

import java.math.BigDecimal;

public class ConstantsSbp {
	 
	// Errores de validacion para el alta de polizas de sobreprecio
	public static final String ERROR_VAL_TASAS       	    = "error.validacion.tasas.cargadas";
	public static final String ERROR_VAL_IMPUESTOS     	    = "error.validacion.impuestos.cargados";
	public static final String ERROR_VAL_SBP_CULT    	    = "error.validacion.sobreprecio.cultivo";
	public static final String ERROR_VAL_PER_CONT    	    = "error.validacion.periodo.contratacion";
	public static final String ERROR_VAL_PRI_MINM    	    = "error.validacion.prima.minima";
	public static final String ERROR_VAL_PLZ_SINI    	    = "error.validacion.poliza.siniestro";
	public static final String ERROR_VAL_FUERA_PER_CONT     = "error.validacion.fuera.periodo.contratacion";
	public static final String ERROR_VAL_ESTADO_POL         = "error.validacion.definitiva.estado.incorrecto";
	public static final String ERROR_LINEA_INCOMPATIBLE     = "error.validacion.linea.incompatible";
	public static final String ERROR_CULTIVOS_INCOMPATIBLES = "error.validacion.cultivos.incompatibles";
	public static final String ERROR_CULTIVOS_FUERA_PER_CONT = "error.validacion.cultivos.fuera.periodo.contratacion";
	public static final String ERROR_SITACT_SIN_PARCELAS 	= "error.sit_act.sin_parcelas";
	
	// Errores de validacion para consulta de polizas para sobreprecio
	public static final String ERROR_LINEASEGUROID_INCOMPATIBLE = "error.validacion.lineaseguroid.incompatible";
	public static final String ERROR_LINEA_CODLINEA_INCOMPATIBLE = "error.validacion.linea.codLinea.incompatible";
	public static final String ERROR_LINEA_CODPLAN_INCOMPATIBLE = "error.validacion.linea.codPlan.incompatible";
	
	// Estados poliza
	public static final BigDecimal PENDIENTE_VALIDACION  = new BigDecimal(1);
	public static final BigDecimal GRABACION_PROVISIONAL = new BigDecimal(2);
	public static final BigDecimal GRABACION_DEFINITIVA  = new BigDecimal(3);
	public static final BigDecimal ANULADA    			 = new BigDecimal(4);
	public static final BigDecimal ENVIADA_CORRECTA    	 = new BigDecimal(8);
	
	// Estados poliza Sbp
	public static final BigDecimal ESTADO_GRAB_PROV             = new BigDecimal(1);
	public static final BigDecimal ESTADO_GRAB_DEF      		= new BigDecimal(2);
	public static final BigDecimal ESTADO_PENDIENTE_ACEPTACION 	= new BigDecimal(3);
	public static final BigDecimal ESTADO_ENVIADA_ERRONEA 		= new BigDecimal(4);
	public static final BigDecimal ESTADO_ENVIADA_CORRECTA 		= new BigDecimal(5);
	public static final BigDecimal ESTADO_ANULADA 				= new BigDecimal(6);
	public static final BigDecimal ESTADO_SIMULACION 			= new BigDecimal(0);
	
	//Mensajes para el alta/edicion/borrado/provisional/definitiva de polizas de sobreprecio
	public static final String MSJ_GRAB_PROV_OK    			="mensaje.grabacion.provisional.ok";
	public static final String ALERT_GRAB_PROV_KO  			="alerta.grabacion.provisional.ko";
	public static final String MSJ_GRAB_DEF_OK     			="mensaje.grabacion.definitiva.ok"; 
	public static final String ALERT_GRAB_DEF_KO   			="alerta.grabacion.definitiva.ko";
	public static final String MSJ_EDITAR_OK       			="mensaje.editar.ok";
	public static final String ALERT_EDITAR_KO     			="alerta.editar.ko";
	public static final String MSJ_BORRAR_OK       			="mensaje.borrar.ok";
	public static final String ALERT_BORRAR_KO     			="alerta.borrar.ko";
	public static final String ALERT_GRAB_DEF_COPY_PLZ  	="alerta.grabacion.definitiva.poliza.copy";
	public static final String MSJ_GRAB_DEF_SIN_CPL  		="mensaje.grabacion.definitiva.SinCpl";
	public static final String MSJ_GRAB_DEF_CON_CPL			="mensaje.grabacion.definitiva.ConCpl";
	public static final String ALERT_ERROR_CALCULO_SBP  	="alerta.calculo.sobreprecio";
	public static final String ALERT_NO_TASAS_SBP  	        ="alerta.tasas.parcelas.sobreprecio";
	public static final String MSJ_RECALCULAR_SBP			="mensaje.recalcular.sbp";
	public static final String ALERT_ANULADA_OK  			="alerta.anulada.ok";
	public static final String ALERT_ANULADA_KO 			="alerta.anulada.ko";
	public static final String ALERT_SUPLEMENTO_ANULADO_OK  ="alerta.suplemento.anulado.ok";
	public static final String ALERT_SUPLEMENTO_ANULADO_KO  ="alerta.suplemento.anulado.ko";

	//Mensajes mantenimiento de tablas de sobreprecio
	
	// FECHA  CONTRATACION	
	public static final String ERROR_FECHA_CONTRATACION_LINEASEG_NO_EXISTE 	="alerta.FechaContratacion.ko.lineaseguroid.noExiste";
	public static final String ERROR_FECHA_CONTRATACION_YA_EXISTE 			="alerta.FechaContratacion.ko.fechaContratacion.existe";
	public static final String MSJ_BORRAR_FECHA_CONTRATACION_OK	  			="mensaje.borrarFechaContratacion.ok";
	public static final String ALERT_BORRAR_FECHA_CONTRATACION_KO 			="alerta.borrarFechaContratacion.ko";
	public static final String MSJ_EDITA_FECHA_CONTRATACION_OK	  			="mensaje.editaFechaContratacion.ok";
	public static final String ALERT_EDITA_FECHA_CONTRATACION_KO  			="alerta.editaFechaContratacion.ko";
	public static final String MSJ_ALTA_FECHA_CONTRATACION_OK     			="mensaje.altaFechaContratacion.ok";
	public static final String ALERT_ALTA_FECHA_CONTRATACION_KO   			="alerta.altaFechaContratacion.ko";
	public static final String ERROR_FECHA_CONTRATACION_LINEA_CULTIVO_KO   	="alerta.altaFechaContratacion.linea.cultivo.ko";
	

	// PRIMA MINIMA
	public static final String MSJ_BORRAR_PRIMA_MINIMA_OK	      	="mensaje.borrarPrimaMinima.ok";
	public static final String ALERT_BORRAR_PRIMA_MINIMA_KO       	="alerta.borrarPrimaMinima.ko";
	public static final String MSJ_GRAB_PRIMA_MINIMA_OK    		  	="mensaje.grabacionPrimaMinima.ok";
	public static final String ALERT_GRAB_PRIMA_MINIMA_KO  		 	="alerta.grabacion.PrimaMinima.ko";
	public static final String MSJ_EDITAR_PRIMA_MINIMA_OK    	  	="mensaje.editarPrimaMinima.ok";
	public static final String ALERT_EDITAR_PRIMA_MINIMA_KO  	  	="alerta.editarPrimaMinima.ko";	
	public static final String ALERT_GRAB_PRIMA_MIN_LINEASEG_KO   	="alerta.grabacion.PrimaMinima.lineaSeguro.ko";	
	public static final String ALERT_GRAB_PRIMA_MIN_PRIMA_EXISTE_KO ="alerta.grabacion.PrimaMinima.existe.ko";
	
	// SOBREPRECIO
	public static final String MSJ_ALTA_SOBREPRECIO_OK            	="mensaje.altaSobreprecio.ok";
	public static final String ALERT_ALTA_SOBREPRECIO_KO          	="alerta.altaSobreprecio.ko";
	public static final String MSJ_EDITA_SOBREPRECIO_OK	          	="mensaje.editaSobreprecio.ok";
	public static final String ALERT_EDITA_SOBREPRECIO_KO         	="alerta.editaSobreprecio.ko";
	public static final String MSJ_BORRAR_SOBREPRECIO_OK	      	="mensaje.borrarSobreprecio.ok";
	public static final String ALERT_BORRAR_SOBREPRECIO_KO        	="alerta.borrarSobreprecio.ko";
	public static final String MSJ_SOBREPRECIO_DUPLICADO_KO    		="mensaje.sobreprecio.duplicado.ko";
	public static final String ERROR_SOBREPRECIO_PLAN_LINEA_KO 		="alerta.Sobreprecio.planLinea.ko";	
	public static final String ERROR_SOBREPRECIO_CULTIVO_KO 		="alerta.Sobreprecio.ko.cultivo.noExiste";
	public static final String ERROR_SOBREPRECIO_TIPO_CAPITAL_KO    ="alerta.Sobreprecio.ko.tipoCapital.noExiste";
	public static final String MSJ_SBP_RECALCULADO			      	="mensaje.Sobreprecio.recalculado";
	
	public static final String ERROR_SOBREPRECIO_PROVINCIA_KO 		= "alerta.Sobreprecio.ko.provincia.noexiste";
	public static final String ERROR_SOBREPRECIO_PLAN_LINEA_VACIO	= "alerta.Sobreprecio.planLinea.vacio";
	
	//Llamadas a SW para la creacion de sobreprecio
	public static final String MSJ_SOBREPRECIO_SW_OK	      		= "mensaje.sobreprecio.sw.OK";
	public static final String ALERT_SOBREPRECIO_SW_KO				= "alerta.sobreprecio.sw.KO";
	public static final String ALERT_SOBREPRECIO_SW_DEFINITIVA_KO	= "alerta.sobreprecio.sw.definitiva.KO";
	
	// TASAS 
	public static final String ERROR_LINEASEGUROID_NO_EXISTE 	= "alerta.Tasa.ko.lineaseguroid.noexiste";
	public static final String ERROR_PROVINCIA_NO_EXISTE 		= "alerta.Tasa.ko.provincia.noexiste";
	public static final String ERROR_TASA_YA_EXISTE 			= "alerta.Tasa.ko.tasa.existe";
	public static final String ERROR_GENERAL 					= "alerta.Tasa.ko";
	public static final String MSJ_BORRAR_TASA_OK 				= "mensaje.borrarTasa.ok";
	public static final String ALERT_BORRAR_TASA_KO 			= "alerta.borrarTasa.ko";
	
	
	//Mensajes para el alta/edicion/borrado/de dato informe
	public static final String MSJ_ALTA_DATOS_INFORME_OK     			="mensaje.datoInforme.alta.ok";
	public static final String ALERT_ALTA_DATOS_INFORME_KO   			="alerta.datosinforme.alta.ko";
	public static final String MSJ_BORRAR_DATOS_INFORME_OK       		="mensaje.datosinforme.baja.ok";
	public static final String ALERT_BORRAR_DATOS_INFORME_KO     		="alerta.datosinforme.baja.ko";
	public static final String MSJ_MODIFICAR_DATOS_INFORME_OK 			="mensaje.datosinforme.modificacion.ok";
	public static final String ALERT_MODIFICAR_DATOS_INFORME_KO     	="alerta.datosinforme.modificacion.ko";
	public static final String ALERT_CONSULTAR_DATOS_INFORME_KO     	="alerta.datosinforme.consultar.ko";
	
	//codigos impuestos sobreprecio
 	public static final String ARBITRIO = "AR1";
 	public static final String CONSORCIO = "CV4";
 	public static final String LIQ_ENTIDADES = "DG1";
 	public static final String IPS = "IPS";
	
 	// Tipo de envio
 	public static final BigDecimal TIPO_ENVIO_PRINCIPAL = new BigDecimal (1);
 	public static final BigDecimal TIPO_ENVIO_SUPLEMENTO = new BigDecimal (2);
 	
 	//Tipo Poliza SBP
 	public static final String TIPO_PPAL = "ppal";
 	public static final String TIPO_CPL = "cpl";
 	public static final String TIPO_SIT_ACTUAL = "sitActual";
 	public static final String TIPO_SIT_ACTUAL_CON_CPL = "sitActual_Cpl";

 	// Incluir complementaria en sbp
 	public static final String INCL_SBP_NO = "N";
 	public static final String INCL_SBP_SI = "S";
 	
 	// Generar suplemento
 	public static final String EXISTE_SUPLEMENTO_PENDIENTE_RESPUESTA 	= "error.validacion.suplemento";
 	public static final String NO_HAY_CAMBIOS_EN_LAS_PARCELAS 			= "alerta.sin.cambios.parcelas";
	public static final String MSJ_SUPL_GRAB_DEF_OK     				="mensaje.suplemento.grabacion.definitiva.ok"; 
	public static final String ALERT_SUPL_GRAB_DEF_KO   				="alerta..suplemento.grabacion.definitiva.ko";
	public static final String ALERT_ERROR_CALCULO_SUPLEMENTO_SBP  		="alerta.calculo.suplemento.sobreprecio";
	public static final String MSJ_ALTA_SUPL_SOBREPRECIO_OK  			="mensaje.altaSuplemento.ok";

}
