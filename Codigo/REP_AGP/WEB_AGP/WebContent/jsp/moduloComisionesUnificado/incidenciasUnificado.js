function consultar(){		
	var frm = document.getElementById('main3');
	frm.target="";		
	$('#method').val('doConsulta');
	$('#origenLlamada').val('consultar');
	$('#main3').submit();
}

function cargar(){
	var frm = document.getElementById('main3');
	frm.target="";
	$('#method').val('doCargar');
	$('#main3').submit();
}


function verificar(){
	var frm = document.getElementById('main3');
	frm.target="";
	$('#method').val('doVerificar');
	$.blockUI.defaults.message = '<h4> Procesando petici\u00F3n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$('#main3').submit();
}

function limpiar(){
	$('#colectivo').val('');
	$('#linea').val('');
	$('#plan').val('');
	$('#subentidad').val('');
	$('#oficina').val('');
	$('#poliza').val('');
	$('#estado').selectOptions('');
	$('#mensaje').val('');
	$('#refpoliza').val('');
	$('#fase').val('');
	$('#esMed_colectivo').val('');
	consultar();
}

function redirigir(numpagina){
	var frm = document.getElementById('main3');
	frm.target="";
	$('#method').val('doRedirigir');
	$('#pagina').val(numpagina);
	if (numpagina==5){ //colectivos
		if ((frm.colectivo.value != "")&& (frm.linea.value != "")){
			$('#main3').submit();
		}else{
			showMensajeError('Debe seleccionar alg�n registro para poder llamar al mantenimiento de Colectivos.');
		}
	}else{
		$('#main3').submit();
	}
}

//function imprimir(size)	{
function imprimir()	{
	var frm = document.getElementById('main3');
	//if(size < ${numRegImpresion}){
		frm.target="_blank";
	//}				
	frm.method.value = 'doImprimir';
	frm.submit();		
}

function showMensajeError(mensaje){
	var arrayMensajes = mensaje.split("|");
    var msj ="";
    for(var i = 0; i < arrayMensajes.length; i++){
     	msj += arrayMensajes[i] + "<br>";
    }
 	$('#mensajeError').html(msj);
    $('#divMensajeError').fadeIn('normal');
    $('#overlay').show();
 }

function cerrarPopUp(){
     $('#divMensajeError').fadeOut('normal');
     $('#overlay').hide();
 }
 
function getTitulo(){
	var tipo = $('#tipofichero').val();
	var titulo = "";
	if (tipo == 'I'){
		titulo = "Revisi\00F3un de incidencias de impagados 2015+";
	}else if(tipo == 'C' || tipo == 'U'){
		titulo = "Revisi\u00F3n de incidencias de comisiones 2015+";
	}else if(tipo == 'D'){
		titulo = "Revisi\u00F3n de incidencias de deuda aplazada";
	}
	else{
		titulo = "Revisi\u00F3n de incidencias";
	}
	
	$("#titulo").html(titulo);
}

function volver(){
	//var tipo = $("#tipofichero").val();
	$(window.location).attr('href', 'importacionComisionesUnificadas.html?rand=' + UTIL.getRand() +'&tipo=' + $('#tipofichero').val() + '&idFicheroUnificado=' + $('#idFicheroUnificado').val() + '&origenLlamada=consultar'); 		
}

$(document).ready(function() {
	getTitulo();
	
	if ($('#estadoFichero').val() == 'Correcto' || $('#estadoFichero').val() == 'Aviso'){
		$('#btnCargar').attr('disabled',false);
	}else{
		$('#btnCargar').attr('disabled',true);
	}
	
	$('#main3').validate({
		errorLabelContainer: "#panelAlertasValidacion",
		onfocusout: function(element) {
   			if(($('#method').val() == "doConsulta") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
				this.element(element);
			}
		},
		wrapper: "li",
		highlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).show();
	    },
		unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		},  	
		rules: {
			"linea.codlinea":{digits:true}					 	
		},
		messages: {
			"linea.codlinea":{digits: "El campo l�nea s�lo puede contener d�gitos"}					 	
		}				
	});
});

function modificar (id, linea, idColectivo, esMed_fichero, oficina,fase, refPoliza, estado, mensaje, esMed_col, plan){
	$('#colectivo').val(idColectivo);
	$('#linea').val(linea);
	$('#plan').val(plan);
	$('#subentidad').val(esMed_fichero);
	if(oficina!="null"){
		$('#oficina').val(oficina);
	}
	
	$('#fase').val(fase);
	$('#refpoliza').val(refPoliza);
	$('#estado').val(estado);
	$('#esMed_colectivo').val(esMed_col);
	
}

function marcaCombo (combo, valor){
	for (var i=0; i<combo.length; i++){
		if (combo[i].value == valor){
			combo[i].selected = 'true';
		}					
	}
}

function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	$.get('incidenciasUnificado.run?ajax=true&idFicheroUnificado=' + $("#idFicheroUnificado").val() + '&'  +decodeURIComponent(parameterString),
		function(data) {
			$("#grid").html(data);
			comprobarChecks();
		});
}

function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'incidenciasUnificado.run?ajax=false&' + parameterString;
}

function revisar(id){
	if(confirm('��Est� seguro de que desea marcar el registro como revisado?')){
		$('#method').val('doRevisarIncidencia');
		$('#idIncidencia').val(id);
		$.blockUI.defaults.message = '<h4> Marcando la incidencia como revisada.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main3').submit();
	}
}

function marcarTodos() {
	if ($('#checkTodos').length) {
		if($('#checkTodos').attr('checked')==true) {
			var listaIdsTodos = $("#listaIdsTodos").val();
			$('#listaIdsMarcados').val(listaIdsTodos);
			$('#marcaTodos').val('true');
			comprobarChecks();
		} else {
			$('#listaIdsMarcados').val('');
			$('#marcaTodos').val('false');
			$("input[type=checkbox]").each(function(){
				$(this).attr('checked',false);
			});
		}
	}
}

function comprobarChecks() {
	var listaIdsMarcados = $('#listaIdsMarcados').val();
	var cadena=[];
	cadena= listaIdsMarcados.split(",");
	if (listaIdsMarcados.length>0){
		$("input[type=checkbox]").each(function(){
			if( $("#marcaTodos").val() == "true" ){
				if($(this).attr('id') != "checkTodos"){
					$(this).attr('checked',true);
				}
			}
			else{
				for (var i=0;i<cadena.length -1;i++){
					var idcheck = "check_" + cadena[i];
					if($(this).attr('id') == idcheck){
						$(this).attr('checked',true);
					}
				}
			}
		});
	}
	if($('#marcaTodos').val()=="true"){
		if ($('#checkTodos').length) $('#checkTodos').attr('checked', true);
	}else{
		if ($('#checkTodos').length) $('#checkTodos').attr('checked', false);
	}
}

function listaCheckId(id) {
	var listaIdsMarcados = "";
	var listaFinalIds = "";
	var cadena=[];
	
	if($('#check_' + id).attr('checked') == true) {
		listaIdsMarcados = $('#listaIdsMarcados').val() + id +",";
		$('#listaIdsMarcados').val(listaIdsMarcados);
	}else{
		listaIdsMarcados = $('#listaIdsMarcados').val();
		cadena= listaIdsMarcados.split(",");
		
		for (var i=0;i<cadena.length -1;i++){
			if(cadena[i]!=id){
				listaFinalIds = listaFinalIds + cadena[i] + ",";
			}		
		}
		$('#listaIdsMarcados').val(listaFinalIds);
		$('#marcaTodos').val('false');
		comprobarChecks();	
	}
}


function recargarFichero() {
	jConfirm('\u00BFEst\u00E1 seguro de que desea recargar el fichero?',
			'Di\u00E1logo de Confirmaci\u00F3n', function(r) {
				if (r) {
					revisar();
				}
			});
}

function revisar() {
	var frm = document.getElementById('main3');
	frm.target = "";
	$('#method').val('doRecargar');
	$.blockUI.defaults.message = '<h4> Procesando petici\u00F3n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({
		overlayCSS : {
			backgroundColor : '#525583'
		}
	});
	$('#main3').submit();
}
