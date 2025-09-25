var poliza = 'poliza';
var asegurado = 'asegurado';
var inicialPoliza = 'p';
var inicialAsegurado = 'a';

$(document).ready(function(){
	
	$(document).ready(function(){
		var URL = UTIL.antiCacheRand($("#formConsulta").attr("action"));
		$("#formConsulta").attr("action", URL);
	});
	
	$("#formConsulta").validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		highlight: function(element, errorClass) {
		 	$("#campoObligatorio_" + element.id).show();
	    },
		unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		},
		rules: {
			"poliza_plan":{validarPlan:true, digits: true, range: [0, 9999]},
			"plan":{validarPlan:true, digits: true, range: [0, 9999]},
			"referencia":{validarRef:true},
			"linea":{validarLinea:true, digits: true, range: [0, 9999]},
			"nifcif":{validarNif: true}
		},
		messages: {
			"poliza_plan":{validarPlan: "El campo Plan es obligatorio", digits: "El campo Plan sólo puede contener dígitos", range: "El campo Plan sólo puede contener dígitos entre 0 y 9999"},
			"plan":{validarPlan: "El campo Plan es obligatorio", digits: "El campo Plan sólo puede contener dígitos", range: "El campo Plan sólo puede contener dígitos entre 0 y 9999"},
			"referencia":{validarRef: "El campo Referencia es obligatorio"},
			"linea":{validarLinea: "El campo Linea es obligatorio", digits: "El campo Línea sólo puede contener dígitos", range: "El campo Línea sólo puede contener dígitos entre 0 y 9999"},
			"nifcif":{validarNif: "El campo NIF/CIF es obligatorio"}
		}
	});
	
	$('input:radio').click(function() {
		limpiarAlertas();
	    if($(this).val() == inicialPoliza) {
	    	limpiarCamposFormulario(asegurado);
	    	habilitarCamposFormulario(poliza);
	   } else {
	    	limpiarCamposFormulario(poliza);
	    	habilitarCamposFormulario(asegurado);
	    }  
	});
});

//Pet. 50775 ** MODIF TAM (11.05.2018) ** Inicio //
jQuery.validator.addMethod("validarPlan", 
	function(value, element, params) {

		var opcionBusqueda = $('input[name=opcionBusqueda]:checked').val();
		
		if(typeof opcionBusqueda != 'undefined'){
	    	if($('#poliza_plan').val() == "" && $('#plan').val() == ""){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	  
	}
);	
jQuery.validator.addMethod("validarRef", 
	function(value, element, params) {

		var opcionBusqueda = $('input[name=opcionBusqueda]:checked').val();
	    
	    if(opcionBusqueda == 'p'){
	    	if($('#poliza_referencia').val() == ""){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
);	
jQuery.validator.addMethod("validarLinea", 
	function(value, element, params) {

		var opcionBusqueda = $('input[name=opcionBusqueda]:checked').val();

	    if(opcionBusqueda == 'a'){
	
	    	if($('#linea').val() == ""){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
);	
jQuery.validator.addMethod("validarNif", 
	function(value, element, params) {
		var opcionBusqueda = $('input[name=opcionBusqueda]:checked').val();
		
	    if(opcionBusqueda == 'a'){
	
	    	if($('#asegurado_nifcif').val() == ""){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
);	
//Pet. 50775 ** MODIF TAM (11.05.2018) ** Fin //

function habilitarCamposFormulario(prefijo){
	var selector = "input[class*='" + prefijo + "']";
	$(selector).each(function(){
		$(this).removeAttr('disabled');
	});
}

function limpiarCamposFormulario(prefijo){
	var selector = "input[class*='" + prefijo + "']";
	$(selector).each(function(){
		$(this).val('');
		$(this).attr('disabled','disabled');
	});
	
	if (prefijo =="asegurado"){
		$('input[name=nomlinea]').val('');
		$('input[name=nomlinea]').attr('disabled','disabled');
	}else{
		$('input[name=nomlinea]').removeAttr('disabled');
	}
}

function limpiarAlertas(){
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	
	// MODIF TAM (27.04.2018) - Resolución Incidencias 50775//
	$("#panelAlertasValidacion").html('');
	// MODIF TAM (27.04.2018) - Resolución Incidencias 50775//
	$("#panelAlertas").hide();
	$("label[id^='campoObligatorio_']").each(function(){
		$(this).hide();
	});	
}

function consultar(){
	
	var opcionBusqueda = $('input[name=opcionBusqueda]:checked').val();
	
	if(typeof opcionBusqueda == 'undefined'){
		// MODIF TAM (27.04.2018) - Resolución Incidencias 50775
		//alert("Por favor, elige un tipo de busqueda");
		$("#panelAlertasValidacion").html("Por favor, elige un tipo de búsqueda");
		$("#panelAlertasValidacion").show();
		// MODIF TAM (27.04.2018) - Resolución Incidencias 50775 - Fin
	} else {
		$("#panelAlertasValidacion").html('');
		//for (var element : document.forms['formConsulta'].elements) {

		if($("#formConsulta").valid()){
			// Modif Tam (Incidencias - 31.05.2018)* Inicio//
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			//Modif Tam (Incidencias - 31.05.2018)* Fin//
		
			$("#formConsulta").submit();
		}
	}	
}

// Va a la pantalla de relación de modificaciones e incidencias
function imprimir(idCupon,anio,numero){
	$("#idCuponImpresion").val(idCupon);
	$("#anio").val(anio);
	$("#numero").val(numero);
	var frm = document.getElementById('formImpresion');
	frm.target="_blank";
	$("#formImpresion").submit();
}

// Va a la pantalla de cálculo de modificación en modo lectura
function verDC(idCupon) {
	var tipobusqueda = $("input:radio[name='opcionBusqueda']:checked").val();
	$("#tipoBusquedaDC").val(tipobusqueda);
	
	if(tipobusqueda == inicialPoliza){
		$("#planDC").val($('#poliza_plan').val());
		$("#referenciaDC").val($('#poliza_referencia').val());
	} else {
		$("#planDC").val($('#plan').val());
		$("#lineaDC").val($('#linea').val());
		$("#nifcifDC").val($('#asegurado_nifcif').val());
	}
	
	/*** MODIF TAM (26.07.2018) **/
	/** Pasamos los datos del filtro de la ventana de Incidencias**/
	$('#formDistCoste').append($('#idincidenciaConsList'));
	$('#formDistCoste').append($('#entidadConsList'));
	$('#formDistCoste').append($('#referenciaConsList'));
	$('#formDistCoste').append($('#oficinaConsList'));
	$('#formDistCoste').append($('#entmediadoraConsList'));
	$('#formDistCoste').append($('#subentmediadoraConsList'));
	$('#formDistCoste').append($('#delegacionConsList'));
	$('#formDistCoste').append($('#planConsList'));
	$('#formDistCoste').append($('#lineaConsList'));
	$('#formDistCoste').append($('#codestadoConsList'));
	$('#formDistCoste').append($('#codestadoagroConsList'));
	$('#formDistCoste').append($('#nifcifConsList'));
	$('#formDistCoste').append($('#tiporefConsList'));
	$('#formDistCoste').append($('#idcuponConsList'));
	$('#formDistCoste').append($('#asuntoConsList'));
	$('#formDistCoste').append($('#fechaEnvioDesdeIdConsList'));
	$('#formDistCoste').append($('#fechaEnvioHastaIdConsulta'));
	$('#formDistCoste').append($('#fechaEnvioHastaIdConsList'));
	$('#formDistCoste').append($('#codUsuarioConsList'));
	/*** MODIF TAM (26.07.2018) **/
		
	$("#idCuponVerDC").val(idCupon);
	// Modif Tam (Incidencias - 31.05.2018)* Inicio//
	$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
    // Modif Tam (Incidencias - 31.05.2018)* Fin//

	$("#formDistCoste").submit();
}

function volver(){
	$('#formVolver').submit();
}

function aportarDocumentacion(anio, codAsunto, asunto, codigoDocAfectado, numeroInc, estado, idEnvio, 
		referencia, tipoPoliza, fechaEstado, numDocumentos){
	
	$("#anioDoc").val(anio);
	$("#asuntoDoc").val(asunto);
	$("#codAsuntoDoc").val(codAsunto);
	$("#codDocAfec").val(codigoDocAfectado);
	$("#numIncidenciaDoc").val(numeroInc);
	$("#estadoDoc").val(estado);
	$("#idEnvioDoc").val(idEnvio);
	$("#referenciaDoc").val(referencia);
	$("#tipoPolizaDoc").val(tipoPoliza);
	$("#fechaEstadoDoc").val(fechaEstado);
	$("#numDoc").val(numDocumentos);	
	var tipobusqueda = $("input:radio[name='opcionBusqueda']:checked").val();
	$("#tipoBusquedaDocVuelta").val(tipobusqueda);
	
	/*** MODIF TAM (26.07.2018) **/
	/** Pasamos los datos del filtro de la ventana de Incidencias**/
	$('#formDocumentacion').append($('#idincidenciaConsList'));
	$('#formDocumentacion').append($('#entidadConsList'));
	$('#formDocumentacion').append($('#referenciaConsList'));
	$('#formDocumentacion').append($('#oficinaConsList'));
	$('#formDocumentacion').append($('#entmediadoraConsList'));
	$('#formDocumentacion').append($('#subentmediadoraConsList'));
	$('#formDocumentacion').append($('#delegacionConsList'));
	$('#formDocumentacion').append($('#planConsList'));
	$('#formDocumentacion').append($('#lineaConsList'));
	$('#formDocumentacion').append($('#codestadoConsList'));
	$('#formDocumentacion').append($('#codestadoagroConsList'));
	$('#formDocumentacion').append($('#nifcifConsList'));
	$('#formDocumentacion').append($('#tiporefConsList'));
	$('#formDocumentacion').append($('#idcuponConsList'));
	$('#formDocumentacion').append($('#asuntoConsList'));
	$('#formDocumentacion').append($('#fechaEnvioDesdeIdConsList'));
	$('#formDocumentacion').append($('#fechaEnvioHastaIdConsList'));
		$('#formDocumentacion').append($('#codUsuarioConsList'));
	/*** MODIF TAM (26.07.2018) **/
	
	$("#formDocumentacion").submit();
}

