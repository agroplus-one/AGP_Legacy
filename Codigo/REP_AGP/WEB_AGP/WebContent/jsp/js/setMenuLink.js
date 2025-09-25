    
    
function getRand(){
   return encodeURI(parseInt(Math.random()*99999999) + "_" + (new Date).getTime());
}
  
// modifica los href del menu principal de la aplicacion  
$(document).ready(function(){
	
	   // menu principal
	   var cargaAsegurado = document.getElementById("a_menu_cargaAsegurado");
	   var seleccionPoliza = document.getElementById("a_menu_seleccionPoliza");
	   var cargaColectivo = document.getElementById("a_menu_cargaColectivo");
	   var tomador = document.getElementById("a_menu_tomador");
	   var colectivo = document.getElementById("a_menu_colectivo");
	   var asegurado = document.getElementById("a_menu_asegurado");
	   var utilidadesPoliza = document.getElementById("a_menu_utilidadesPoliza");
	   var utilidadesSiniestros = document.getElementById("a_menu_utilidadesSiniestros");
	   var utilidadesReduccionCapital = document.getElementById("a_menu_utilidadesReduccionCapital");
	   var utilidadesAnexoModificacion = document.getElementById("a_menu_anexoModificacionUtilidades");
	   var utilidadesIncidencias = document.getElementById("a_menu_utilidadesIncidencias");
	   var utilidadesXML = document.getElementById("a_menu_utilidadesXML");
	   var moduloTaller = document.getElementById("a_menu_moduloTaller");
	   var gge = document.getElementById("a_menu_mantenimientoGEE");
	   var cc = document.getElementById("a_menu_mantenimientoComisionesCultivo");
	   var reglamento = document.getElementById("a_menu_mantenimientoReglamentos");
	   var importacionReci = document.getElementById("a_menu_mantenimientoimportacionRecibos");
	   var importacionReg = document.getElementById("a_menu_mantenimientoimportacionReglamentos");
	   var importacionImp = document.getElementById("a_menu_mantenimientoimportacionImpagos");
	   var cierre = document.getElementById("a_menu_mantenimientoCierre");
	   var cargaClase = document.getElementById("a_menu_cargaClase");
	   var sobreprecio = document.getElementById("a_menu_mantenimiento_sbp_sobreprecio");
	   var fechasContratacionSbp = document.getElementById("a_menu_mantenimiento_sbp_periodo_cont");
	   var tasas  = document.getElementById("a_menu_mantenimiento_sbp_tasas");
	   var impuestos = document.getElementById("a_menu_mantenimiento_sbp_impuestos");
	   var consulta_para_sbp = document.getElementById("a_menu_Confeccion_Poliza_Sopreprecio");
	   var primaMinima_para_sbp = document.getElementById("a_menu_mantenimiento_sbp_prima_minima");
	   var tasa_para_sbp = document.getElementById("a_menu_mantenimiento_sbp_tasas");
	   var consulta_de_sbp = document.getElementById("a_menu_Consulta_Sopreprecio");
	   var mtoInformes = document.getElementById("a_menu_mantenimiento_informes");
	   var mtoCamposPermitidos = document.getElementById("a_menu_mantenimiento_campos_permitidos");
	   var mtoCamposCalculados = document.getElementById("a_menu_mantenimiento_campos_calculados");
	   var mtoOperadores = document.getElementById("a_menu_mantenimiento_operadores");
	   var generacion = document.getElementById("a_menu_generacion_informes");
	   var entAcceso = document.getElementById("a_menu_mantenimiento_ent_acceso_restringido");
	   var mtoImpuestoSbp = document.getElementById("a_menu_mantenimiento_sbp_impuestos");
	   var informesRecibos = document.getElementById("a_menu_InformesRecibos");
	   var informesComisiones = document.getElementById("a_menu_InformesComisiones");
	   var mantenimientoDescuentos = document.getElementById("a_menu_mantenimientoDescuentos");
	   var paramGen = document.getElementById("a_menu_mantenimientoParametrosGenerales");
	   var polizasRenovables = document.getElementById("a_menu_polizas_renovables");
	   var informesImpagados= document.getElementById("a_menu_InformesImpagados");
	   var informesImpagados2015= document.getElementById("a_menu_InformesImpagados2015");
	   var informesDeudaAplazada= document.getElementById("a_menu_InformesDeudaAplazada");
	   var informesComisiones2015 =document.getElementById("a_menu_InformesComisiones2015");
	   var mantenimientoRetenciones= document.getElementById("a_menu_mantenimientoRetenciones");
	   	   
	   //1
	   if(cargaAsegurado != null)
	       cargaAsegurado.href = "asegurado.html?origenLlamada=menuGeneral&cargaAseg=cargaAseg&rand=" + getRand();
	   //2
	   if(seleccionPoliza != null)
	       seleccionPoliza.href = "seleccionPoliza.html?rand=" + getRand();
	   //3
	   if(tomador != null)
	       tomador.href = "tomador.html?rand=" + getRand();
	   //4
	   if(colectivo != null)
	       colectivo.href = "colectivo.html?rand=" + getRand();
	   //5
	   if(asegurado != null)
	       asegurado.href = "asegurado.html?origenLlamada=menuGeneral&rand=" + getRand();
	   //6
	   if(utilidadesPoliza != null)
	       utilidadesPoliza.href = "utilidadesPoliza.html?origenLlamada=menuGeneral&rand=" + getRand(); 
	   // 7
	   if(utilidadesSiniestros != null)
	       utilidadesSiniestros.href = "utilidadesSiniestros.run?origenLlamada=menuGeneral&rand=" + getRand();
	   // 8   
	   if(utilidadesReduccionCapital != null)
	       utilidadesReduccionCapital.href = "utilidadesReduccionCapital.run?origenLlamada=menuGeneral&rand=" + getRand();
	   // 9
	   if(utilidadesAnexoModificacion != null)
	       utilidadesAnexoModificacion.href = "anexoModificacionUtilidades.run?origenLlamada=menuGeneral&rand=" + getRand();

	   if(utilidadesIncidencias != null)
	   	   utilidadesIncidencias.href = "utilidadesIncidencias.run?origenLlamada=menuGeneral&rand=" + getRand();
	   
	   if(utilidadesXML != null)
		   utilidadesXML.href = "utilidadesXML.run?origenLlamada=menuGeneral&rand=" + getRand();
	   
	   // 10
	   if(moduloTaller != null)
	       moduloTaller.href = "menu.html?OP=taller&rand=" + getRand();
	   // 11
	   if(cargaColectivo != null)
	       cargaColectivo.href = "cargaColectivo.html?origenLlamada=cargaColectivos&rand=" + getRand();
	   //12
	   if(gge != null)
	       gge.href = "gge.html?primeraConsulta=true&limpiar=limpiar&rand=" + getRand();
	   //13
	   if(cc != null){
	    	cc.href = "comisionesCultivos.html?primeraConsulta=true&rand=" + getRand();
	    }
	   //14
	   if(reglamento != null)
	       reglamento.href = "reglamento.html?rand=" + getRand();
	   //15
	   if(importacionReci != null)
	       importacionReci.href = "importacionComisiones.html?tipo=C&rand=" + getRand();
	   //16
	   if(importacionReg != null)
	       importacionReg.href = "importacionComisiones.html?tipo=R&rand=" + getRand();
	   //17
	   if(importacionImp != null)
	       importacionImp.href = "importacionComisiones.html?tipo=I&rand=" + getRand();
	   //18
	   if(cierre != null)
	       cierre.href = "cierre.html?rand=" + getRand();
	   //19
	   if(cargaClase != null)
	       cargaClase.href = "cargaClase.run?origenLlamada=cicloPoliza&rand=" + getRand();    
	   //20
	   if (sobreprecio != null){
	   	 sobreprecio.href = "sobreprecioSbp.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }
	   if (fechasContratacionSbp != null){
		   fechasContratacionSbp.href= "periodoContSbp.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }
	   if (tasas != null){
		   tasas.href= "tasasSbp.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }
	   if (impuestos != null){
		   impuestos.href= "mtoImpuestoSbp.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }
	   //21
	   if (consulta_para_sbp != null){
	   	 consulta_para_sbp.href = "consultaPolSbp.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }   
	   
	   //22
	   if (primaMinima_para_sbp != null){
	   	 primaMinima_para_sbp.href = "primaMinimaSbp.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }  
	   
	   //23
	   if (tasa_para_sbp != null){
	   	 tasa_para_sbp.href = "tasasSbp.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }
	   
	   //24
	   if (consulta_de_sbp != null){
	   	 consulta_de_sbp.href = "consultaPolizaSbp.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }
	   
	   //25
	   if (mtoInformes != null){
	   	 mtoInformes.href = "mtoInformes.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }
	   //26
	   if (mtoCamposPermitidos != null){
	   	 mtoCamposPermitidos.href = "mtoCamposPermitidos.run?origenLlamada=menuGeneral&rand=" + getRand();
	   } 
	   //27
	   if (mtoCamposCalculados != null){
	   	 mtoCamposCalculados.href = "mtoCamposCalculados.run?origenLlamada=menuGeneral&rand=" + getRand();
	   } 
	   //28
	   if (mtoOperadores != null){
	   	 mtoOperadores.href = "mtoOperadoresCampos.run?origenLlamada=menuGeneral&rand=" + getRand();
	   } 
	   //29
	   if (generacion != null){
	   	 generacion.href = "generacionInforme.run?origenLlamada=menuGeneral&rand=" + getRand();
	   } 
	    //29
	   if (entAcceso != null){
	   	 entAcceso.href = "mtoEntidadesAccesoRestringido.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }
	   //30
	   if (mtoImpuestoSbp != null){
	   	 mtoImpuestoSbp.href = "mtoImpuestoSbp.run?origenLlamada=menuGeneral&rand=" + getRand();
	   }
	   //31
	   if(informesRecibos != null){
		   informesRecibos.href = "informesRecibos.run?origenLlamada=menuGeneral&rand=" + getRand();  
	   }
	   //32
	   if(mantenimientoDescuentos != null){
		   mantenimientoDescuentos.href = "mtoDescuentos.run?origenLlamada=menuGeneral&rand=" + getRand();  
	   }
	   //33
	   if(polizasRenovables != null){
		   polizasRenovables.href = "polizasRenovables.run?origenLlamada=menuGeneral&rand=" + getRand();  
	   }
	   
	   if(informesImpagados !=null){
		   informesImpagados.href = "informesImpagados.run?origenLlamada=menuGeneral&mayorIgual2015=false&rand=" + getRand();  
	   }
	   		   
	   if(informesImpagados2015 != null){
		   informesImpagados2015.href = "informeImpagadosUnificado.run?origenLlamada=menuGeneral&rand=" + getRand();  
	   }
	   if(informesDeudaAplazada != null){
		   informesDeudaAplazada.href = "informesDeudaAplazada.run?origenLlamada=menuGeneral&rand=" + getRand();  
	   }
	   if(mantenimientoRetenciones != null){
		   mantenimientoRetenciones.href = "mtoRetenciones.run?origenLlamada=menuGeneral&rand=" + getRand();  
	   }
	   
	   
	   // menu taller
	   var cargaPAC = document.getElementById("a_menuTaller_cargaPAC");
	   var referencia = document.getElementById("a_menuTaller_referencia");
	   var referenciaColectivo = document.getElementById("a_menuTaller_referencia_colectivo");
	   var activacionLineas = document.getElementById("a_menuTaller_activacionLineas");
	   var parametrizacion = document.getElementById("a_menuTaller_parametrizacion");
	   var pantallasConfigurables = document.getElementById("a_menuTaller_pantallasConfigurables");
	   var camposMascara = document.getElementById("a_menuTaller_camposMascara");
	   var relacionCampos = document.getElementById("a_menuTaller_relacionCampos");
	   var historico = document.getElementById("a_menuTaller_historico");
	   var importaciones = document.getElementById("a_menuTaller_importaciones");
	   var importacionesBatch = document.getElementById("a_menuTaller_importaciones_batch");
	   var errorWsAccion = document.getElementById("a_menuTaller_errorWsAccion");
	   var claseMto = document.getElementById("a_menuTaller_claseMto");
	   var cpmTipoCapital = document.getElementById("a_menuTaller_cpmTipoCapital");
	   var pagoManual = document.getElementById("a_menuTaller_pagoManual");
	   var mtoZonas = document.getElementById("a_menuTaller_mtoZonas");
	   var mtoUsuarios = document.getElementById("a_menuTaller_mtoUsuarios");
	   var importeFrac = document.getElementById("a_menuTaller_fracc");
	  
	   //1
	   if(cargaPAC != null)
	       cargaPAC.href = "cargaPAC.html?rand=" + getRand();
	   //2
	   if(referencia != null)
	       referencia.href = "referencia.html?rand=" + getRand();
	   //3
	   if(activacionLineas != null)
	       activacionLineas.href = "activacionlineas.html?rand=" + getRand();
	   //4
	   if(parametrizacion != null)
	       parametrizacion.href = "parametrizacion.html?rand=" + getRand();
	   //5
	   if(pantallasConfigurables != null)
	       pantallasConfigurables.href = "pantallasConfigurables.html?operacion=inicializar&rand=" + getRand();

	   // 7
	   if(camposMascara != null)
	       camposMascara.href = "camposMascara.html?rand=" + getRand();
	   // 8
	   if(relacionCampos != null)
	       relacionCampos.href = "relacionCampos.html?rand=" + getRand();
	   // 9
	   if(historico != null)
	       historico.href = "importacion.html?operacion=historico&rand=" + getRand();
	   // 10
	   if(importaciones != null)
	       importaciones.href = "importacion.html?rand=" + getRand();
	    
	   if(importacionesBatch != null)
	       importacionesBatch.href = "cargasCondicionado.run?origenLlamada=menuGeneral&rand=" + getRand(); 
	   // 11
	   if(errorWsAccion != null)
	       errorWsAccion.href = "errorWsAccion.run?origenLlamada=menuGeneral&rand=" + getRand();
	   // 12
	   if(claseMto != null)
	       claseMto.href = "claseMto.run?origenLlamada=menuGeneral&rand=" + getRand();
	   // 13
	   if(referenciaColectivo != null)
	       referenciaColectivo.href = "referenciaColectivo.html?rand=" + getRand();
	   // 14  
	   if(cpmTipoCapital != null)
	       cpmTipoCapital.href = "cpmTipoCapital.run?origenLlamada=menuGeneral&rand=" + getRand();
	   // 15  
	   if(pagoManual != null)
		   pagoManual.href = "pagoManual.run?origenLlamada=menuGeneral&rand=" + getRand();
	   /* Pet. 63701 ** MODIF TAM (04.06.2021) ** Inicio  */
	   // 16  
	   if(mtoZonas != null)
		   mtoZonas.href = "mtoZonas.run?origenLlamada=menuGeneral&rand=" + getRand();
	   /* Pet. 63701 ** MODIF TAM (04.06.2021) ** Fin  */
	   //17
	   if(mtoUsuarios != null)
		   mtoUsuarios.href = "mtoUsuarios.run?origenLlamada=menuGeneral&rand=" + getRand();
	   //18
	   if(paramGen != null)
		   paramGen.href = "comisionesCultivos.html?method=doConsultaParam&rand=" + getRand();
	   //19
	   if(importeFrac != null)
		   importeFrac.href = "mtoImportesFrac.run?origenLlamada=menuGeneral&rand=" + getRand(); 
	   
	   if(informesComisiones2015!=null)
		   informesComisiones2015.href= "informesComisiones2015.run?origenLlamada=menuGeneral&rand=" + getRand();
});   

function irPortalMediador() {
	$.ajax({
		type : 'POST',
		url : 'portalMedAgroseguro.html',
		data : {
				'method'     : 'doPortalMediador'
			   },
		async : true,
		dataType : 'json',
		success : function(datos) {
			if (datos.errorMsgs.length > 0) {
				var errorMsg = '';
				for (i = 0; i < datos.errorMsgs.length; i++) {
					errorMsg += datos.errorMsgs[i] + '\n';
				}
				if (errorMsg != '') {
					alert(errorMsg);
				}	
			} else {
				window.open(datos.portalMedUrl, '_blank');
			}
		},
		error : function(jqXHR, exception) {
			if (jqXHR.status === 0) {
	            msg = 'Verifique la conexi\u00F3n.';
	        } else if (jqXHR.status == 404) {
	            msg = 'P\u00E1gina no encontrada [404].';
	        } else if (jqXHR.status == 500) {
	            msg = 'Error interno del servidor [500].';
	        } else if (exception === 'parsererror') {
	            msg = 'Fallo en el tratamiento del JSON esperado.';
	        } else if (exception === 'timeout') {
	            msg = 'Tiempo de espera agotado.';
	        } else if (exception === 'abort') {
	            msg = 'Petici\u00F3n Ajax cancelada.';
	        } else {
	            msg = 'Error no esperado: ' + jqXHR.responseText;
	        }
			alert(msg);
		}
	});
}