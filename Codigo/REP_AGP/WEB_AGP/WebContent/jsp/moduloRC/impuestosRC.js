$(document).ready(function(){
	
	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL; 
    
    if ($("#id").val() != '') {
		$('#btnModificar').show();
	}
	
	$('#main3').validate({
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
			"codPlan":{required: true, digits: true, range: [0, 9999]},
			"impuestoSbp.codigo": {required: true},
			"valor":{required: true, range: [0.01,100.00]},
	 		"baseSbp.base":{required: true}
		},
		 messages: {
			"codPlan":{required: "El campo Plan es obligatorio", digits: "El campo Plan sólo puede contener dígitos", range: "El campo Plan sólo puede contener dígitos entre 0 y 9999"},
			"impuestoSbp.codigo": {required: "El campo Código Impuesto es obligatorio"},
			"valor":{required: "El campo Valor es obligatorio", digits: "El campo Valor sólo puede contener dígitos", range: "El campo Valor sólo puede contener dígitos entre 0 y 100"},
			"baseSbp.base":{required: "El campo Base es obligatorio"}
		}
	});
});


function consultarInicial() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doConsulta');	
	if (comprobarCampos(false)) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main3").validate().cancelSubmit = true;
		$('#main3').submit();
	} else {
		$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo o el contenido de alguno de los campos de filtro no es válido");
		$('#panelAlertasValidacion').show();
	}
}

function consultar() {
	limpiaAlertas();
	$('#method').val('doConsulta');
	if (comprobarCampos(true)) {
		onInvokeAction('listaImpuestosRC', 'filter');
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
	var frm = document.getElementById('main3');
	$.get('impuestosRC.run?ajax=true&origenLlamada=' + frm.origenLlamada.value
			+ '&' + parameterString, function(data) {
		$("#grid").html(data);
	});
}

function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	$("label[id^='campoObligatorio_']").each(function(){
		$(this).hide();
	});	
}

function alta() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#id').val('');
	$('#method').val('doAlta');
	if ($('#main3').valid()) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main3').submit();
	}
}

function limpiar() {
	limpiaAlertas();
	$('#id').val('');
	$('#codplan').val('');
	$('#codimpuesto').val('');
	$('#nomimpuesto').val('');
	$('#valor').val('');
	$('#nombase').val('');
	$('#origenLlamada').val('menuGeneral');
	$('#method').val('doConsulta');	
	$('#btnConsultar').attr('href', 'javascript:consultarInicial();');	
	$('#btnAlta').show();
	$('#btnModificar').hide();
	$("#grid").html('');
}

function comprobarCampos(incluirJmesa) {	
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('listaImpuestosRC');
	}
	var resultado = false;
   	if($('#codplan').val() != '' && /^([0-9])*$/.test($('#codplan').val())){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaImpuestosRC', 'codPlan', $('#codplan').val());
   		}
   		resultado = true;
   	}
   	if($('#codimpuesto').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaImpuestosRC', 'impuestoSbp.codigo', $('#codimpuesto').val());
   		}
   		resultado = true;
   	}
   	if($('#nomimpuesto').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaImpuestosRC', 'impuestoSbp.descripcion', $('#nomimpuesto').val());
   		}
   		resultado = true;
   	}
   	if($('#valor').val() != '' && /^([0-9])*$/.test($('#valor').val())){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaImpuestosRC', 'valor', $('#valor').val());
   		}
   		resultado = true;
   	}
   	if($('#nombase').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaImpuestosRC', 'baseSbp.base', $('#nombase').val());
   		}
   		resultado = true;
   	}
   	return resultado;
}

function editar(id, plan, codimpuesto, nomimpuesto, valor, nombase) {
	$('#id').val(id);
	$('#codplan').val(plan);
	$('#codimpuesto').val(codimpuesto);
	$('#nomimpuesto').val(nomimpuesto);
	$('#valor').val(valor);
	$('#nombase').val(nombase);
	$('#btnModificar').show();
}

function borrar(id) {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doEliminar');
	$('#id').val(id);
	if (confirm('Está a punto de eliminar el impuesto para RC. ¿Está seguro?')) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main3').validate().cancelSubmit = true;
		$('#main3').submit();
	} 
}

function modificar() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doModificar');
	if ($('#main3').valid()) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main3').submit();
	}
}