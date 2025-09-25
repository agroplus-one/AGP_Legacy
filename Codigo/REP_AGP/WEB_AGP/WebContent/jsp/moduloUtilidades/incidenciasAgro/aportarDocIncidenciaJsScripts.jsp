$(document).ready(function(){
	
	var URL = UTIL.antiCacheRand(document.getElementById("formAgregar").action);
	document.getElementById("formAgregar").action = URL;

});

function limpiarAlertas(){
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	$("label[id^='campoObligatorio_']").each(function(){
		$(this).hide();
	});	
}

function borrar(idDoc, idInc){
	if(confirm('Está a punto de eliminar el documento. ¿Está seguro?')) {
		$('#idDocBorrar').val(idDoc);
		$('#idIncBorrar').val(idInc);
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#formBorrar').submit();
	}
}

function agregar(){
	var opcionBusqueda = $('input[name=tipoEnvio]:checked').val();
	if(typeof opcionBusqueda == 'undefined'){
		alert("Por favor, elige un tipo de envio");
	} else {
		if ($('#formAgregar').valid()) {
			$('#method').val('doAgregarDoc');
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$('#formAgregar').submit();
		}
	}
}

function enviar(){
	var opcionBusqueda = $('input[name=tipoEnvio]:checked').val();
	if(typeof opcionBusqueda == 'undefined'){
		alert("Por favor, elige un tipo de envio");
	} else {
		if ($('#formAgregar').valid()) {
			$('#method').val('doEnviar');
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$('#formAgregar').submit();
		}	
	}
}
