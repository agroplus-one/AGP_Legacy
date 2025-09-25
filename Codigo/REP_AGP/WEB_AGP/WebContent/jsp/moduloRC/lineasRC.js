$(document).ready(function() {
	
	var URL = UTIL.antiCacheRand(document.getElementById("formLineasRC").action);
    document.getElementById("formLineasRC").action = URL;    
    
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
    
    $('#formLineasRC').validate({
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
			"codespecie":{required: true, digits: true, range: [0, 999]},
			"codregimen":{required: true, digits: true, range: [0, 999]},
			"codtipocapital":{required: true, digits: true, range: [0, 999]},
			"especiesRC.codespecie":{required: true}
		},
		 messages: {
			"linea.codplan":{required: "El campo Plan es obligatorio", digits: "El campo Plan sólo puede contener dígitos", range: "El campo Plan sólo puede contener dígitos entre 0 y 9999"},
			"linea.codlinea":{required: "El campo Línea es obligatorio", digits: "El campo Línea sólo puede contener dígitos", range: "El campo Línea sólo puede contener dígitos entre 0 y 999"},
			"codespecie":{required: "El campo Especie es obligatorio", digits: "El campo Especie sólo puede contener dígitos", range: "El campo Especie sólo puede contener dígitos entre 0 y 999"},
			"codregimen":{required: "El campo Régimen es obligatorio", digits: "El campo Régimen sólo puede contener dígitos", range: "El campo Régimen sólo puede contener dígitos entre 0 y 999"},
			"codtipocapital":{required: "El campo Tipo de Capital es obligatorio", digits: "El campo Tipo de Capital sólo puede contener dígitos", range: "El campo Tipo de Capital sólo puede contener dígitos entre 0 y 999"},
			"especiesRC.codespecie":{required: "El campo Especie para RC es obligatorio"}
		}
	});
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
		jQuery.jmesa.removeAllFiltersFromLimit('listaLineasRC');
	}
	var resultado = false;
   	if ($('#plan').val() != '' && /^([0-9])*$/.test($('#plan').val())){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaLineasRC', 'linea.codplan', $('#plan').val());
   		}
   		resultado = true;
   	}
   	if ($('#linea').val() != '' && /^([0-9])*$/.test($('#linea').val())){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaLineasRC', 'linea.codlinea', $('#linea').val());
   		}
   		resultado = true;
   	}
   	if ($('#especie').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaLineasRC', 'codespecie', $('#especie').val());
   		}
   		resultado = true;
   	}
   	if ($('#regimen').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaLineasRC', 'codregimen', $('#regimen').val());
   		}
   		resultado = true;
   	}
	if ($('#capital').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaLineasRC', 'codtipocapital', $('#capital').val());
   		}
   		resultado = true;
   	}
	if ($('#especiesRC').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaLineasRC', 'especiesRC.codespecie', $('#especiesRC').val());
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
		$("#formLineasRC").validate().cancelSubmit = true;
		$('#formLineasRC').submit();
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
		onInvokeAction('listaLineasRC', 'filter');
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
	var frm = document.getElementById('formLineasRC');
	$.get('lineasRC.run?ajax=true&origenLlamada=' + frm.origenLlamada.value
			+ '&' + parameterString, function(data) {
		$("#grid").html(data);
	});
}

function alta() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#id').val('');
	$('#method').val('doAlta');
	if ($('#formLineasRC').valid()) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#formLineasRC').submit();
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
	$('#especie').val('');
	$('#desc_especie').val('');
	$('#regimen').val('');
	$('#desc_regimen').val('');
	$('#capital').val('');
	$('#desc_capital').val('');
	$('#especiesRC').val('');
	$('#origenLlamada').val('menuGeneral');
	$('#method').val('doConsulta');	
	$('#btnConsultar').attr('href', 'javascript:consultarInicial();');	
	$('#btnAlta').show();
	$('#btnModificar').hide();
	$("#grid").html('');
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
	if (confirm('Está a punto de eliminar la línea para RC. ¿Está seguro?')) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#formLineasRC').validate().cancelSubmit = true;
		$('#formLineasRC').submit();
	} 
}

function editar(id, plan, linea, desclinea, codespecie, descespecie,
		codregimen, descregimen, codtipocapital, desctipocapital, especiesRC) {
	$('#id').val(id);
	$('#plan').val(plan);
	$('#linea').val(linea);
	$('#codplan').val(plan);
	$('#codlinea').val(linea);
	$('#desc_linea').val(desclinea);
	$('#especie').val(codespecie);
	$('#desc_especie').val(descespecie);
	$('#regimen').val(codregimen);
	$('#desc_regimen').val(descregimen);
	$('#capital').val(codtipocapital);
	$('#desc_capital').val(desctipocapital);
	$('#especiesRC').val(especiesRC);
	$('#btnModificar').show();
}

function modificar() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doModificar');
	if ($('#formLineasRC').valid()) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#formLineasRC').submit();
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
		if (confirm('¿Desea replicar todos las Líneas para RC para este Plan y Línea?')) {
			// Valida que el plan/linea origen y destino no son iguales
			if (replicaPlanLineaDiferentes($('#planreplica').val(), $('#plan')
					.val(), $('#lineareplica').val(), $('#linea').val())) {
				if ($('#planreplica').val() >= 2015) {
					$("#method").val("doReplicar");
					$('#formLineasRC').validate().cancelSubmit = true;
					$("#formLineasRC").submit();
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