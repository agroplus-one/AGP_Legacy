<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">

var baseDatos = "baseDatos";
var agroseguro = "agroseguro";
var clasePoliza = "poliza";
var claseAsegurado = "asegurado";
var clseIncidencia = "incidencia";
var clasAnexo = "anexo";
var clasesAgroseguro = ["poliza", "asegurado", "incidencia", "anexo"];
var clasesBaseDatos = ["poliza", "asegurado"];
var polizaSwitch = 'p';
var anexoSwitch = 'am';
var aseguradoSwitch = 'a';
var incidenciaSwitch = 'i;'

$(document).ready(function(){

    
	var URL = UTIL.antiCacheRand(document.getElementById("formAgregar").action);
	document.getElementById("formAgregar").action = URL;
	
    $("input[type=file]").filestyle({
		image: "jsp/img/boton_examinar.png",
		imageheight: 22,
		imagewidth: 82,
		width: 250
	});
	
	var origenEnv = $("#origenEnvio").val();
	var origenAcc = $("#origen").val();
	
	$('input:radio').click(function(){
		var valorSeleccionado = $(this).val();

		switch (origenEnv) {
		case baseDatos:
			if(valorSeleccionado == 'p') {
				habilitarCamposFormulario(clasePoliza, clasesBaseDatos, true);
				habilitarTipoReferencia();
			} else {
				habilitarCamposFormulario(claseAsegurado, clasesBaseDatos, true);
				deshabilitarTipoReferencia();
			} 
			break;
		case agroseguro:
			if(valorSeleccionado == 'p') {
				habilitarCamposFormulario(clasePoliza, clasesAgroseguro, true);
				habilitarTipoReferencia();
			} else if(valorSeleccionado == 'i'){
				habilitarCamposFormulario(clseIncidencia, clasesAgroseguro, true);
				deshabilitarTipoReferencia()
			} else if(valorSeleccionado == 'am'){
				habilitarCamposFormulario(clasAnexo, clasesAgroseguro, true);
				deshabilitarTipoReferencia()
			} else {
				habilitarCamposFormulario(claseAsegurado, clasesAgroseguro, true);
				deshabilitarTipoReferencia()
			}
			break;
		}
	});
	
	$("#formAgregar").validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		highlight: function(element, errorClass) {
			
			if(element.id=='plan'){
				var opcionBusqueda = $('input[name=tipoEnvio]:checked').val();
				if(typeof opcionBusqueda == 'undefined'){
				} else {
					if(opcionBusqueda == 'p'){
						$("#campoObligatorio_planPol").show();
					}else if (opcionBusqueda =='aseg'){
						$("#campoObligatorio_planAse").show();
					}else{
						$("#campoObligatorio_planPol").show();
					}
				}
			}else{
		 		$("#campoObligatorio_" + element.id).show();
			}
	    },
		unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
			if(element.id=='plan'){
				var opcionBusqueda = $('input[name=tipoEnvio]:checked').val();
				if(typeof opcionBusqueda == 'undefined'){
				} else {
					if(opcionBusqueda == 'p'){
						$("#campoObligatorio_planPol").hide();
					}else if (opcionBusqueda =='aseg'){
						$("#campoObligatorio_planAse").hide();
					}else{
						$("#campoObligatorio_planPol").hide();
					}
				}
			}else{
		 		$("#campoObligatorio_" + element.id).hide();
			}
		},
		rules: {
			<c:if test="${origenEnvio eq 'agroseguro'}">
				"incidencias.numincidencia": {
					<%--Pet.50775 ** MODIF TAM ** Resolución incidencia  
					<required: true,--%> 
					validarNumInc: true,
					digits: true, 
					range: [0, 999999]
				},
				"incidencias.anhoincidencia": {
					required: true, 
					digits: true, 
					range: [0, 9999]
				},
				"incidencias.idenvio": {
					required: true 
					//lettersonly: true
				},
			</c:if>
			<c:if test="${origenEnvio eq 'baseDatos'}">
				"codasunto" : {
					required: true
				},
			</c:if>
			<c:if test="${(origenEnvio eq 'agroseguro' && origen eq 'impresionIncidencias')}">
				"codasunto" : {
					required: true
				},
			</c:if>	
			"incidencias.observaciones": {
				maxlength: 145
			}, 
			"incidencias.codplan": {
				required: true, 
				digits: true, 
				range: [0, 9999]
			},
			"incidencias.referencia": {
				required: true
			},
			"incidencias.codlinea": {
				required: true, 
				digits: true, 
				range: [0, 9999]
			},
			"incidencias.nifaseg": {
				required: true
			},
			"file": {
				validaExtension: ['file'],
				validaLongitudFichero: ['file']
			}
		},
		messages: {
			<c:if test="${origenEnvio eq 'agroseguro'}">
				"incidencias.numincidencia": {
					validarNumInc: "El campo Número Inc. es obligatorio", 
					digits: "El campo Número Inc. sólo puede contener dígitos", 
					range: "El campo Número Inc. sólo puede contener dígitos entre 0 y 9999"
				},
				"incidencias.anhoincidencia": {
					required: "El campo Año es obligatorio", 
					digits: "El campo Año sólo puede contener dígitos", 
					range: "El campo Año sólo puede contener dígitos entre 0 y 9999"
				},
				"incidencias.idenvio": {
					required: "El campo Id. Envío es obligatorio"
					//lettersonly: "El Id. de Cupón sólo puede contener letras"
				},
			</c:if>
			<c:if test="${origenEnvio eq 'baseDatos'}">
				"codasunto" : {
					required: "El campo Asunto es obligatorio"
				},
			</c:if>
			<c:if test="${(origenEnvio eq 'agroseguro' && origen eq 'impresionIncidencias')}">
				"codasunto" : {
					required: "El campo Asunto es obligatorio"
				},
			</c:if>
			"incidencias.observaciones": {
				maxlength: "El campo Observaciones debe contener como máximo 145 caracteres, espacios incluidos"
			},
			"incidencias.codplan": {
				required: "El campo Plan es obligatorio", 
				digits: "El campo Plan sólo puede contener dígitos", 
				range: "El campo Plan sólo puede contener dígitos entre 0 y 9999"
			},
			"incidencias.referencia": {
				required: "El campo Referencia es obligatorio"
			},
			"incidencias.codlinea": {
				required: "El campo Linea es obligatorio", 
				digits: "El campo Línea sólo puede contener dígitos", 
				range: "El campo Línea sólo puede contener dígitos entre 0 y 9999"
			},
			"incidencias.nifaseg": {
				required: "El campo NIF/CIF es obligatorio"
			},
			"file": {
				validaExtension: "Extensión de fichero no valida",
				validaLongitudFichero: "El campo Fichero debe contener como máximo 50 caracteres"	
			}
		}
	});
	
	jQuery.validator.addMethod("validaExtension", function (value, element) {
		var extPermitida = false;
		var ext = element.value.split('.').pop();
		var arr = $('#extensiones').val().split('|');
		for (var i = 0; i < arr.length; i++) {
			if (ext == arr[i]) {
				extPermitida = true;
				break;
			}
		}
		return extPermitida;
	});
	
	jQuery.validator.addMethod("validaLongitudFichero", function (value, element) {
		var result = false;
		var nomfichero = element.value.split('\\').pop();
		if (nomfichero.length <= 50) {
			result = true;
		}
		return result;
	});

	jQuery.validator.addMethod("validaLongitudFichero", function (value, element) {
		var result = false;
		var nomfichero = element.value.split('\\').pop();
		if (nomfichero.length <= 50) {
			result = true;
		}
		return result;
	});

	jQuery.validator.addMethod("validarNumInc", function (value, element) {
		var result = false;
		var opcionBusqueda = $('input[name=tipoEnvio]:checked').val();
		
		if(typeof opcionBusqueda == 'undefined'){
			agregarALerta("Elige un tipo de envio");
		} else {
			if(opcionBusqueda == 'i'){
		    	if(document.getElementById("numincidencia").value == ""){
					return false;
				}else{
					return true;
				}
			}else{
				return true;
			}
		}
		return true;
	});
	
	// Marcamos la opción que se hubiera marcado.	
	var opcionSelec = document.getElementById("tipoEnvio").value;
	
    if (opcionSelec != ""){
    	if (opcionSelec == "p"){
    		jQuery('input:radio[name="tipoEnvio"]').filter('[value="p"]').attr('checked', true);
        }else if (opcionSelec == "aseg"){
        	jQuery('input:radio[name="tipoEnvio"]').filter('[value="aseg"]').attr('checked', true);
        }else if (opcionSelec == "i"){
        	jQuery('input:radio[name="tipoEnvio"]').filter('[value="i"]').attr('checked', true);
        }else if (opcionSelec == "am"){
       		jQuery('input:radio[name="tipoEnvio"]').filter('[value="am"]').attr('checked', true);
        }
    	
    	var valorSeleccionado = $('input[name=tipoEnvio]:checked').val();
    	
    	/* MODIF TAM (29.05.2018)*/
   		switch (origenEnv){
	   		case baseDatos:
	   			// MODIF TAM (12.06.2018)
	   			if (origenAcc == 'editarInc'){
	   				// bloqueamos todos los campos de la cabecera (poliza, asegurado, Incidencia Cupón)
	   				bloquearCamposCabeceraFormulario(clasePoliza, clasesAgroseguro);
	   				bloquearCamposCabeceraFormulario(clseIncidencia, clasesAgroseguro);
	   				bloquearCamposCabeceraFormulario(clasAnexo, clasesAgroseguro);
	   				bloquearCamposCabeceraFormulario(claseAsegurado, clasesAgroseguro);
	   				deshabilitarTipoReferencia();
	   			}else{
	   				if(valorSeleccionado == 'p') {
	    				habilitarCamposFormulario(clasePoliza, clasesBaseDatos, false);
	    				habilitarTipoReferencia();
	    			} else {
	    				habilitarCamposFormulario(claseAsegurado, clasesBaseDatos, false);
	    				deshabilitarTipoReferencia();
	    			} 	
	   			}
    			break;
    		case agroseguro:
    			if(valorSeleccionado == 'p') {
    				habilitarCamposFormulario(clasePoliza, clasesAgroseguro, false);
    				habilitarTipoReferencia();
    			} else if(valorSeleccionado == 'i'){
    				habilitarCamposFormulario(clseIncidencia, clasesAgroseguro, false);
    				deshabilitarTipoReferencia()
    			} else if(valorSeleccionado == 'am'){
    				habilitarCamposFormulario(clasAnexo, clasesAgroseguro, false);
    				deshabilitarTipoReferencia()
    			} else {
    				habilitarCamposFormulario(claseAsegurado, clasesAgroseguro, false);
    				deshabilitarTipoReferencia()
    			}
    		break;
    	}
   	}
   		
});

function habilitarTipoReferencia(){
	var origenAcc = $("#origen").val();
	
	if (origenAcc != 'consultaInc'){		
		$('#tiporef').removeAttr('disabled');
	}else{
		deshabilitarTipoReferencia()
	}
}

function deshabilitarTipoReferencia(){
	$('#tiporef').attr('disabled','disabled');
}

function bloquearCamposCabeceraFormulario(prefijo, clases){
	for(var i = 0; i < clases.length; i++){
		var e = clases[i]
		var temp = "input[class*='" + e + "']";
		$(temp).each(function(){
			$(this).attr('disabled','disabled');
		});
	}
	$('input[name=tipoEnvio]').attr('disabled', 'disabled');
}

function habilitarCamposFormulario(prefijo, clases, limpiar){
	if (limpiar == true){
		limpiarAlertas();
	}
	
	if (prefijo == 'asegurado'){
		$("#btn_linea").show();
	}else{
		$("#btn_linea").hide();

	}
	
	var origenAcc = $("#origen").val();
	if (origenAcc != 'consultaInc'){		
		for(var i = 0; i < clases.length; i++){
			var e = clases[i]
			var temp = "input[class*='" + e + "']";
			$(temp).each(function(){
				$(this).attr('disabled','disabled');
			});
		}
		var selector = "input[class*='" + prefijo + "']"; 
		$(selector).each(function(){
			$(this).removeAttr('disabled');
		});
	}else{
		for(var i = 0; i < clases.length; i++){
			var e = clases[i]
			var temp = "input[class*='" + e + "']";
			$(temp).each(function(){
				$(this).attr('disabled','disabled');
				
			});
		}
		var selector = "input[class*='" + prefijo + "']"; 
		$(selector).each(function(){
			$(this).attr('disabled', 'disabled');
		});
		
		$('input[name=tipoEnvio]').attr('disabled', 'disabled');
		deshabilitarTipoReferencia();
		
		limpiamosCamposConsulta(prefijo);
	}		
}

function limpiamosCamposConsulta(valSelecc){

	var ValTipoRef = document.getElementById("tiporef").value;
	if (valSelecc == clasePoliza){
		var clasesLimpiar = ["asegurado", "incidencia", "anexo"];
	}else if (valSelecc == claseAsegurado){
		var clasesLimpiar = ["poliza", "incidencia", "anexo"];
	}else if (valSelecc == clseIncidencia){
		var clasesLimpiar = ["poliza", "asegurado", "anexo"];
	}else if (valSelecc == clasAnexo){
		var clasesLimpiar = ["poliza", "asegurado", "incidencia"];
	}
	for(var i = 0; i < clasesLimpiar.length; i++){
		var e = clasesLimpiar[i]
		var temp = "input[class*='" + e + "']";
		$(temp).each(function(){
			$(this).attr('value','');
		});
	}
	
	if (valSelecc == clasePoliza){
		document.getElementById("tiporef").value = ValTipoRef;
	}
}


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
		// Añadimos el formVolver
		$('#formBorrar').append($('#idincidenciaConsulta'));
		$('#formBorrar').append($('#entidadConsulta'));
		$('#formBorrar').append($('#referenciaConsulta'));
		$('#formBorrar').append($('#oficinaConsulta'));
		$('#formBorrar').append($('#entmediadoraConsulta'));
		$('#formBorrar').append($('#subentmediadoraConsulta'));
		$('#formBorrar').append($('#delegacionConsulta'));
		$('#formBorrar').append($('#planConsulta'));
		$('#formBorrar').append($('#lineaConsulta'));
		$('#formBorrar').append($('#codestadoConsulta'));
		$('#formBorrar').append($('#codestadoagroConsulta'));
		$('#formBorrar').append($('#nifcifConsulta'));
		$('#formBorrar').append($('#tiporefConsulta'));
		$('#formBorrar').append($('#idcuponConsulta'));
		$('#formBorrar').append($('#asuntoConsulta'));
		$('#formBorrar').append($('#fechaEnvioDesdeIdConsulta'));
		$('#formBorrar').append($('#fechaEnvioHastaIdConsulta'));
		$('#formBorrar').append($('#numIncidenciaConsulta'));
		$('#formBorrar').append($('#codUsuarioConsulta'));
		// fin formVolver
		$('#formBorrar').submit();
	}
}

function agregar(){
	limpiarAlertas();
	var opcionBusqueda = $('input[name=tipoEnvio]:checked').val();
	if(typeof opcionBusqueda == 'undefined'){
		agregarALerta("Elige un tipo de envio");
	} else {
		//MODIF TAM (14.05.2018)
		if($("#formAgregar").valid() && validarFichero()){
			limpiarAlertas()
			$('#method').val('doAgregarDoc');
			$('#tipoEnvio').val(opcionBusqueda);
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			// Añadimos el formVolver
			$('#formAgregar').append($('#idincidenciaConsulta'));
			$('#formAgregar').append($('#entidadConsulta'));
			$('#formAgregar').append($('#referenciaConsulta'));
			$('#formAgregar').append($('#oficinaConsulta'));
			$('#formAgregar').append($('#entmediadoraConsulta'));
			$('#formAgregar').append($('#subentmediadoraConsulta'));
			$('#formAgregar').append($('#delegacionConsulta'));
			$('#formAgregar').append($('#planConsulta'));
			$('#formAgregar').append($('#lineaConsulta'));
			$('#formAgregar').append($('#codestadoConsulta'));
			$('#formAgregar').append($('#codestadoagroConsulta'));
			$('#formAgregar').append($('#nifcifConsulta'));
			$('#formAgregar').append($('#tiporefConsulta'));
			$('#formAgregar').append($('#idcuponConsulta'));
			$('#formAgregar').append($('#asuntoConsulta'));
			$('#formAgregar').append($('#fechaEnvioDesdeIdConsulta'));
			$('#formAgregar').append($('#fechaEnvioHastaIdConsulta'));
			$('#formAgregar').append($('#numIncidenciaConsulta'));
			$('#formAgregar').append($('#codUsuarioConsulta'));
			// fin formVolver
			$('#formAgregar').submit();
		}
	}
}

function validarFichero(){
	var fichero = $("#fichero").val();
	var nombreValido = false;
	var extensionValida = false;
	var existeFichero = fichero != "";
	var extension = "";
	if(existeFichero){
		var nombre = fichero.split("\\").pop();
		var nombreValido = validarNombre(nombre)
		extension = nombre.split(".").pop();
		var extensionValida = validarExtension(extension);
	}
	mostrarMensajesAlertaFichero(nombreValido, extensionValida, extension, existeFichero);
	return existeFichero && nombreValido && extensionValida;
}

function validarNombre(nombre){
	var nombreValido = true;
	if(nombre.length > 50){
		nombreValido = false;
	}
	return nombreValido
}

function validarExtension(extension){
	var extensionValida = false;
	var extensionesArray = $("#extensiones").val().split("|");
	for(var i = 0; i < extensionesArray.length; i++){
		if(extension.toLowerCase()== extensionesArray[i]){
			extensionValida = true;
			break;
		}
	}
	return extensionValida;
}

function mostrarMensajesAlertaFichero(nombreValido, extensionValida, extension, existeFichero){
	var listaExtenciones = $("#extensiones").val().split("|")
	var alertaNombre = "<li><label class=\"error\ style=\"DISPLAY: inline\">Nombre del documento mayor de 50 caracteres</label></li>";
	var alertaExtension = "<li><label class=\"error\ style=\"DISPLAY: inline\">"+ extension.toUpperCase() +" no es una extensión valida. Sólo se aceptan " + listaExtenciones.toString() + "</label></li>";
	var alertaFichero = "<li><label class=\"error\ style=\"DISPLAY: inline\">Por favor, añade un documento</label></li>";
	
	if(!nombreValido && !extensionValida && existeFichero){
		$("#panelAlertasValidacion").html(alertaNombre + alertaExtension);
	} else if(!nombreValido && existeFichero){
		$("#panelAlertasValidacion").html(alertaNombre);
	} else if(!extensionValida && existeFichero){
		$("#panelAlertasValidacion").html(alertaExtension);
	} else if(!existeFichero){
		$("#panelAlertasValidacion").html(alertaFichero);
	}
	$("#panelAlertasValidacion").show();
}



function enviar(){
	limpiarAlertas();
	var opcionBusqueda = $('input[name=tipoEnvio]:checked').val();
	if(typeof opcionBusqueda == 'undefined'){
		agregarALerta("Elige un tipo de envio");
	} else {
		if($("#formAgregar").valid() && hayDocumetos()){
			limpiarAlertas()
			$('#method').val('doEnviar');
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			// Añadimos el formVolver
			$('#formAgregar').append($('#idincidenciaConsulta'));
			$('#formAgregar').append($('#entidadConsulta'));
			$('#formAgregar').append($('#referenciaConsulta'));
			$('#formAgregar').append($('#oficinaConsulta'));
			$('#formAgregar').append($('#entmediadoraConsulta'));
			$('#formAgregar').append($('#subentmediadoraConsulta'));
			$('#formAgregar').append($('#delegacionConsulta'));
			$('#formAgregar').append($('#planConsulta'));
			$('#formAgregar').append($('#lineaConsulta'));
			$('#formAgregar').append($('#codestadoConsulta'));
			$('#formAgregar').append($('#codestadoagroConsulta'));
			$('#formAgregar').append($('#nifcifConsulta'));
			$('#formAgregar').append($('#tiporefConsulta'));
			$('#formAgregar').append($('#idcuponConsulta'));
			$('#formAgregar').append($('#asuntoConsulta'));
			$('#formAgregar').append($('#fechaEnvioDesdeIdConsulta'));
			$('#formAgregar').append($('#fechaEnvioHastaIdConsulta'));
			$('#formAgregar').append($('#numIncidenciaConsulta'));
			$('#formAgregar').append($('#codUsuarioConsulta'));
			// fin formVolver
			$('#formAgregar').submit();	
		}
	}
}

function hayDocumetos(){
	
	var hayDocumentos = $('#haydocumentos').val() == "haydocumentos";
	if(!hayDocumentos){
		agregarALerta("Es necesario agregar algún documento");
	}
	return hayDocumentos;
}

function agregarALerta(mesaje){
	$("#panelAlertasValidacion").html("<li><label class=\"error\ style=\"DISPLAY: inline\">"+mesaje+"</label></li>");
	$("#panelAlertasValidacion").show();
}

function volver(){
	$('#formVolver').submit();
}

function volverIncAgr(){
	$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	
	// MODIF TAM (27.07.2018)
	// Añadimos el formVolver
	$('#formVolverInc').append($('#idincidenciaConsulta'));
	$('#formVolverInc').append($('#entidadConsulta'));
	$('#formVolverInc').append($('#referenciaConsulta'));
	$('#formVolverInc').append($('#oficinaConsulta'));
	$('#formVolverInc').append($('#entmediadoraConsulta'));
	$('#formVolverInc').append($('#subentmediadoraConsulta'));
	$('#formVolverInc').append($('#delegacionConsulta'));
	$('#formVolverInc').append($('#planConsulta'));
	$('#formVolverInc').append($('#lineaConsulta'));
	$('#formVolverInc').append($('#codestadoConsulta'));
	$('#formVolverInc').append($('#codestadoagroConsulta'));
	$('#formVolverInc').append($('#nifcifConsulta'));
	$('#formVolverInc').append($('#tiporefConsulta'));
	$('#formVolverInc').append($('#idcuponConsulta'));
	$('#formVolverInc').append($('#asuntoConsulta'));
	$('#formVolverInc').append($('#fechaEnvioDesdeIdConsulta'));
	$('#formVolverInc').append($('#fechaEnvioHastaIdConsulta'));
	$('#formVolverInc').append($('#numIncidenciaConsulta'));
	$('#formVolverInc').append($('#codUsuarioConsulta'));
	// fin formVolver
	$('#formVolverInc').submit();
}

function volverImprimirInc(){
	$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$('#impresionIncidenciasMod').submit();
}
</script>


