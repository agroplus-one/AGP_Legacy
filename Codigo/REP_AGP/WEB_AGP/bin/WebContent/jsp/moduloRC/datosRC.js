$(document).ready(function() {
	
	var URL = UTIL.antiCacheRand(document.getElementById("formDatosRC").action);
    document.getElementById("formDatosRC").action = URL;    
    
    $("#plan").change(function(event) { 
    	$("#codplan").val($("#plan").val());
    });
    $("#linea").change(function(event) { 
    	$("#codlinea").val($("#linea").val());
    });
    
    if ($("#plan").val() != '') {
    	$("#codplan").val($("#plan").val());
    }
	if ($("#linea").val() != '') {
		$("#codlinea").val($("#linea").val());
	}
	
	if ($("#id").val() != '') {
		$('#btnModificar').show();
	}
    
	$('#formDatosRC').validate({
		onfocusout: function(element) {
			if ($('#method').val() == "doAlta" || $('#method').val() == "doModificar") {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		highlight: function(element, errorClass) {
		 	$("#campoObligatorio_" + element.id).show();
	    },
		unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		},
		rules: {
			"linea.codplan":{required: true, digits: true, range: [0, 9999]},
			"linea.codlinea":{required: true, digits: true, range: [0, 999]},
			"subentidadMediadora.id.codentidad": {digits: true, rangelength:[1,4]},
	 		"subentidadMediadora.id.codsubentidad": {digits: true, rangelength:[1,4]},
			"especiesRC.codespecie":{required: true},
	 		"regimenRC.codregimen":{required: true},
	 		"sumaAseguradaRC.codsuma":{required: true},
	 		"tasa":{required: true, number: true, VALDECIMAL62: true},
	 		"franquicia":{required: true, digits: true, range: [0, 999999999]},
	 		"primaMinima":{required: true, digits: true, range: [0, 999999]}
		},
		 messages: {
			"linea.codplan":{required: "El campo Plan es obligatorio", digits: "El campo Plan sólo puede contener dígitos", range: "El campo Plan sólo puede contener dígitos entre 0 y 9999"},
			"linea.codlinea":{required: "El campo Línea es obligatorio", digits: "El campo Línea sólo puede contener dígitos", range: "El campo Línea sólo puede contener dígitos entre 0 y 999"},
			"subentidadMediadora.id.codentidad": {digits: "El campo Entidad Mediadora solo puede contener dígitos", rangelength: "El campo Entidad Mediadora debe contener entre 1 y 4 dígitos"},
	 		"subentidadMediadora.id.codsubentidad": {digits: "El campo Subentidad Mediadora solo puede contener dígitos", rangelength: "El campo Subentidad Mediadora debe contener entre 1 y 4 dígitos"},
			"especiesRC.codespecie":{required: "El campo Especie para RC es obligatorio"},
			"regimenRC.codregimen":{required: "El campo Régimen para RC es obligatorio"},
			"sumaAseguradaRC.codsuma":{required: "El campo Suma Asegurada es obligatorio"},
			"tasa":{required: "El campo Tasa es obligatorio", number: "El campo Tasa sólo puede contener dígitos", VALDECIMAL62: "El campo Tasa debe contener como máximo 6 dígitos de parte entera y 2 dígitos de parte decimal"},
			"franquicia":{required: "El campo Franquicia es obligatorio", digits: "El campo Franquicia sólo puede contener dígitos", range: "El campo Franquicia sólo puede contener dígitos entre 0 y 999999999"},
			"primaMinima":{required: "El campo Prima mínima es obligatorio", digits: "El campo Prima mínima sólo puede contener dígitos", range: "El campo Prima mínima sólo puede contener dígitos entre 0 y 999999"}
		}
	});
	
    $('#formDatosRCCM').validate({
		onfocusout: function(element) {
			if ($('#method').val() == "doCambioMasivo" ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#txt_mensaje_cm",
		wrapper: "li",
		highlight: function(element, errorClass) {
		 	$("#campoObligatorio_" + element.id).show();
	    },
		unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		},
		rules: {
	 		"tasaCM":{number: true, VALDECIMAL62: true},
	 		"franquiciaCM":{digits: true, range: [0, 999999999]},
	 		"primaMinimaCM":{digits: true, range: [0, 999999]}
		},
		 messages: {
			"tasaCM":{number: "El campo Tasa sólo puede contener dígitos", VALDECIMAL62: "El campo Tasa debe contener como máximo 6 dígitos de parte entera y 2 dígitos de parte decimal"},
			"franquiciaCM":{digits: "El campo Franquicia sólo puede contener dígitos", range: "El campo Franquicia sólo puede contener dígitos entre 0 y 999999999"},
			"primaMinimaCM":{digits: "El campo Prima mínima sólo puede contener dígitos", range: "El campo Prima mínima sólo puede contener dígitos entre 0 y 999999"}
		}
	});
    
    
    jQuery.validator.addMethod("VALDECIMAL62", function(value, element) {
    	if(value.length == 0)
    		return true;
    	else
    		return /^\d{0,6}([.]\d{1,2})?$/.test(value);
    });
    
    $(".panelCambioMasivo").draggable();
});



function isLineaSeleccionada() {
	if ($("#codplan").val() == '' || $("#codlinea").val() == '') {
		alert('Debe seleccionar un plan/línea.');
		return false;
	} else {
		return true;
	}
}

function comprobarCampos(incluirJmesa) {	
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('listaDatosRC');
	}
	var resultado = false;
   	if ($('#plan').val() != '' && /^([0-9])*$/.test($('#plan').val())){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaDatosRC', 'linea.codplan', $('#plan').val());
   		}
   		resultado = true;
   	}
   	if ($('#linea').val() != '' && /^([0-9])*$/.test($('#linea').val())){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaDatosRC', 'linea.codlinea', $('#linea').val());
   		}
   		resultado = true;
   	} 
   	if ($('#entmediadora').val() != '' && /^([0-9])*$/.test($('#linea').val())){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaDatosRC', 'subentidadMediadora.id.codentidad', $('#entmediadora').val());
   		}
   		resultado = true;
   	} 
   	if ($('#subentmediadora').val() != '' && /^([0-9])*$/.test($('#linea').val())){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaDatosRC', 'subentidadMediadora.id.codsubentidad', $('#subentmediadora').val());
   		}
   		resultado = true;
   	} 
   	if ($('#especiesRC').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaDatosRC', 'especiesRC.codespecie', $('#especiesRC').val());
   		}
   		resultado = true;
   	}
   	if ($('#regimenesRC').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaDatosRC', 'regimenRC.codregimen', $('#regimenesRC').val());
   		}
   		resultado = true;
   	}
   	if ($('#sumaAseguradaRC').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaDatosRC', 'sumaAseguradaRC.codsuma', $('#sumaAseguradaRC').val());
   		}
   		resultado = true;
   	}
   	if ($('#tasa').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaDatosRC', 'tasa', $('#tasa').val());
   		}
   		resultado = true;
   	}
   	if ($('#franquicia').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaDatosRC', 'franquicia', $('#franquicia').val());
   		}
   		resultado = true;
   	}
   	if ($('#primaMinima').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaDatosRC', 'primaMinima', $('#primaMinima').val());
   		}
   		resultado = true;
   	}
   	return resultado;
}

function consultarInicial () {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doConsulta');	
	if (comprobarCampos(false)) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#formDatosRC").validate().cancelSubmit = true;
		$('#formDatosRC').submit();
	} else {
		$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo o el contenido de alguno de los campos de filtro no es válido");
		$('#panelAlertasValidacion').show();
	}
}

function consultar() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doConsulta');
	if (comprobarCampos(true)) {
		onInvokeAction('listaDatosRC', 'filter');
	} else {
		$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo o el contenido de alguno de los campos de filtro no es válido");
		$('#panelAlertasValidacion').show();
	}
}

function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('formDatosRC');
	$.get('datosRC.run?ajax=true&origenLlamada=' + frm.origenLlamada.value
			+ '&' + parameterString, function(data) {
		$("#grid").html(data);
		comprobarChecks();
	});
}

function alta() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#id').val('');
	$('#method').val('doAlta');
	if ($('#formDatosRC').valid()) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#formDatosRC').submit();
	}
}

function limpiar() {
	limpiaAlertas();
	$('#id').val('');
	$('#plan').val('');
	$('#linea').val('');
	$('#codplan').val('');
	$('#codlinea').val('');
	$('#desc_linea').val('');
	$('#entmediadora').val('');
	$('#subentmediadora').val('');
	$('#especiesRC').val('');
	$('#regimenesRC').val('');
	$('#sumaAseguradaRC').val('');
	$('#tasa').val('');
	$('#franquicia').val('');
	$('#primaMinima').val('');
	$('#origenLlamada').val('menuGeneral');
	$('#method').val('doConsulta');	
	$('#btnConsultar').attr('href', 'javascript:consultarInicial();');	
	$('#btnAlta').show();
	$('#btnModificar').hide();
	$("#grid").html('');
}

function limpiarParaModificar(){
	$('#id').val('');
	$('#plan').val('');
	$('#linea').val('');
	$('#desc_linea').val('');
	$('#entmediadora').val('');
	$('#subentmediadora').val('');
	$('#especiesRC').val('');
	$('#regimenesRC').val('');
	$('#sumaAseguradaRC').val('');
	$('#tasa').val('');
	$('#franquicia').val('');
	$('#primaMinima').val('');
}

/**
 * Limpia las posibles alertas y mensajes que se estuviesen mostrando
 */ 
function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	$("label[id^='campoObligatorio_']").each(function(){
		$(this).hide();
	});	
}	

function borrar(id) {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doBorrar');
	$('#id').val(id);
	if (confirm('Está a punto de eliminar el dato para RC. ¿Está seguro?')) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#formDatosRC').validate().cancelSubmit = true;
		$('#formDatosRC').submit();
	} 
}

function editar(id, plan, linea, desclinea, entmediadora, subentmediadora, especiesRC, regimenesRC, sumaAseguradaRC, tasa, franquicia, primaMinima) {
	limpiarParaModificar()
	$('#id').val(id);
	$('#plan').val(plan);
	$('#linea').val(linea);
	$('#codplan').val(plan);
	$('#codlinea').val(linea);
	$('#desc_linea').val(desclinea);
	$('#entmediadora').val(entmediadora);
	$('#subentmediadora').val(subentmediadora);
	$('#especiesRC').val(especiesRC);
	$('#regimenesRC').val(regimenesRC);
	$('#sumaAseguradaRC').val(sumaAseguradaRC);
	$('#tasa').val(tasa);
	$('#franquicia').val(franquicia);
	$('#primaMinima').val(primaMinima);
	$('#btnModificar').show();
}

function editarSinEntidad(id, plan, linea, desclinea, especiesRC, regimenesRC, sumaAseguradaRC, tasa, franquicia, primaMinima) {
	limpiarParaModificar()
	$('#id').val(id);
	$('#plan').val(plan);
	$('#linea').val(linea);
	$('#codplan').val(plan);
	$('#codlinea').val(linea);
	$('#desc_linea').val(desclinea);
	$('#especiesRC').val(especiesRC);
	$('#regimenesRC').val(regimenesRC);
	$('#sumaAseguradaRC').val(sumaAseguradaRC);
	$('#tasa').val(tasa);
	$('#franquicia').val(franquicia);
	$('#primaMinima').val(primaMinima);
	$('#btnModificar').show();
}

function modificar() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doModificar');
	if ($('#formDatosRC').valid()) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#formDatosRC').submit();
	}
}

function replicar() {
	limpiaAlertas();
	if (validarLineaparaReplica($('#plan').val(), $('#linea').val(),
			$("#panelAlertasValidacion"))) {
		// hay que tener estas variables creadas en la jsp (<input type="hidden" ...
		$('#planreplica').val('');
		$('#lineareplica').val('');
		lupas.muestraTabla('LineaReplica', 'principio', '', '');
	}
}

function doReplicar(){
	// Se comprueba que el plan y linea a replicar no son vacíos antes de llamar
	// al servidor
	// Si son vacíos, se ha hecho click en un elemento de la lupa que no es un
	// registro (ordenación, etc.)
	if ($('#planreplica').val() != '' && $('#lineareplica').val() != '') {
		if (confirm('¿Desea replicar todos los Datos para RC para este Plan y Línea?')) {
			// Valida que el plan/linea origen y destino no son iguales
			if (replicaPlanLineaDiferentes($('#planreplica').val(), $('#plan')
					.val(), $('#lineareplica').val(), $('#linea').val())) {
				if ($('#planreplica').val() >= 2015) {
					$("#method").val("doReplicar");
					$('#formDatosRC').validate().cancelSubmit = true;
					$("#formDatosRC").submit();
				} else {
					$('#panelAlertasValidacion')
							.html(
									"El plan de destino debe de ser mayor o igual a 2015");
					$('#panelAlertasValidacion').show();
				}
			} else {
				$('#panelAlertasValidacion')
						.html(
								"El plan/linea origen no puede ser igual que el destino");
				$('#panelAlertasValidacion').show();
			}
		}
	}
}

function cambioMasivo() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doCambioMasivo');
	if($('#listaIdsMarcados').val() != '') {
		if (confirm('Está a punto de modificar uno o varios Datos para RC. ¿Está seguro?')) {
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$('#formDatosRC').validate().cancelSubmit = true;
			$('#formDatosRC').submit();
		} 
	} else {
		showPopUpAviso("Debe seleccionar como mínimo un Dato para RC.");
	}
}

function showPopUpAviso(mensaje){
	$('#txt_mensaje_aviso').html(mensaje);	
	$('#panelInformacion2').show();
	$('#popUpAvisos').show();
	$('#overlay').show();
}

function hidePopUpAviso(){
	$('#popUpAvisos').hide();
	$('#overlay').hide();
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