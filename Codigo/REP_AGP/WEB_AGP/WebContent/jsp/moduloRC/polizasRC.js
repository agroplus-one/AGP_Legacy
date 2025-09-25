$(document).ready(function(){

	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL; 
	
	Zapatec.Calendar.setup({
        firstDay          : 1,
        weekNumbers       : false,
        showOthers        : true,
        showsTime         : false,
        timeFormat        : "24",
        step              : 2,
        range             : [1900.01, 2999.12],
        electric          : false,
        singleClick       : true,
        inputField        : "fecenviorcId",
        button            : "btn_fecenviorc",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
		
	$('#main3').validate({
		onfocusout: function(element) {
			if ($('#method').val() == "doConsulta") {
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
			"entidad":{digits: true, range: [0, 9999]},
			"oficina":{digits: true, range: [0, 9999]},
			"plan":{digits: true, range: [0, 9999]},
			"linea":{digits: true, range: [0, 999]},
			"clase":{digits: true, range: [0, 999]},
			"refcolectivo":{digits: true, range: [0, 9999999]},
			"refomega":{digits: true, range: [0, 999999999]},
			"nsolicitud":{digits: true, range: [0, 999999999999999]}
		},
		 messages: {
			"entidad":{digits: "El campo Entidad sólo puede contener dígitos", range: "El campo Entidad sólo puede contener dígitos entre 0 y 9999"},
			"oficina":{digits: "El campo Oficina sólo puede contener dígitos", range: "El campo Oficina sólo puede contener dígitos entre 0 y 9999"},
			"plan":{digits: "El campo Plan sólo puede contener dígitos", range: "El campo Plan sólo puede contener dígitos entre 0 y 9999"},
			"linea":{digits: "El campo Línea sólo puede contener dígitos", range: "El campo Línea sólo puede contener dígitos entre 0 y 999"},
			"clase":{digits: "El campo Clase sólo puede contener dígitos", range: "El campo Clase sólo puede contener dígitos entre 0 y 999"},
			"refcolectivo":{digits: "El campo Colectivo sólo puede contener dígitos", range: "El campo Colectivo sólo puede contener dígitos entre 0 y 999"},
			"refomega":{digits: "El campo Ref. Omega sólo puede contener dígitos", range: "El campo Ref. Omega sólo puede contener dígitos entre 0 y 999999999"},
			"nsolicitud":{digits: "El campo Nº Solicitud sólo puede contener digitos", range: "El campo Nº Solicitud sólo puede contener digitos entre 0 y 999999999999999"}
		}
	});
});

jQuery.validator.addMethod("grupoEnt", function(value, element, params) { 
	var codentidad = $('#entidad').val(); 
	if($('#grupoEntidades').val() == ""){
		return true;
	}else if (codentidad != ""){
		var grupoEntidades = $('#grupoEntidades').val().split(',');
		var encontrado = false;
		for(var i=0;i<grupoEntidades.length;i++){
			if(grupoEntidades[i] == codentidad){
				encontrado = true;
				break;
			}
		}
	}else
		return true;
	return 	encontrado;	
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
 	var inputText2 =  parseFloat($('#oficina').val());
 	var inputText = document.getElementById('oficina');
 	if (!isNaN(inputText2) && inputText.value != ""){
 		while (inputText.value.length<4){
			inputText.value = '0'+inputText.value;
		}
	}
	
	limpiaAlertas();
 	
	var entidadValida = validarGrupoEntidad();
	var oficinaValida = validarOficina();
	
	if(!entidadValida){
		$('#panelAlertasValidacion').html("La Entidad seleccionada no pertenece al grupo de Entidades del usuario");
		$('#panelAlertasValidacion').show();
	} else if(!oficinaValida) {
		$('#panelAlertasValidacion').html("La Oficina seleccionada no pertenece al grupo de Oficinas del usuario");
		$('#panelAlertasValidacion').show();
	} else {
		$('#method').val('doConsulta');
		if (comprobarCampos(true)) {
			onInvokeAction('listaPolizasRC', 'filter');
		} else {
			$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo o el contenido de alguno de los campos de filtro no es válido");
			$('#panelAlertasValidacion').show();
		}
	}
}

function limpiar(){
	$('#main3').attr('target', '');
	$('#limpiar').submit();
}

function borrar(idPoliza) {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doBorrar');
	$('#idpoliza').val(idPoliza);
	$('#nsolicitud').val(idPoliza);
	if (confirm('Está a punto de eliminar la póliza para RC. ¿Está seguro?')) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main3').validate().cancelSubmit = true;
		$('#main3').submit();
	} 
}

function simulacion(idPoliza) {
	$('#method').val('doImprimir');
	$('#idpoliza').val(idPoliza);
	var nsolicitud = $('#nsolicitud').val();
	$('#nsolicitud').val(idPoliza);
	$('#main3').attr('target', '_blank');
	$('#main3').submit();
	$('#idpoliza').val('');
	$('#nsolicitud').val(nsolicitud);
}

function imprimirCondiciones(plan, especieRC) {
	$('#method').val('imprimirCondiciones');
	$('#plan').val(plan);
	$('#especierc').val(especieRC);
	$('#main3').attr('target', '_blank');
	$('#main3').submit();
}

function definitiva(idPoliza, usuario) {
	limpiaAlertas();
	if (confirm('Está a punto de pasar a definitiva la póliza para RC. ¿Está seguro?')) {
		valoresAnularDefinitiva(idPoliza, usuario, 'doPasoDefinitiva');
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main3').validate().cancelSubmit = true;
		$('#main3').submit();
	}
}

function anular(idPoliza, usuario) {
	limpiaAlertas();
	if (confirm('Está a punto de anular la póliza para RC. ¿Está seguro?')) {
		valoresAnularDefinitiva(idPoliza, usuario, 'doAnular');
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main3').validate().cancelSubmit = true;
		$('#main3').submit();
	} 
}

function valoresAnularDefinitiva(idPoliza, usuario, accion){
	$('#origenLlamada').val('');
	$('#method').val('doPasoDefinitiva');
	$('#idpoliza').val(idPoliza);
	$('#nsolicitud').val(idPoliza);
	$('#usuario').val(usuario);
}

function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	//$('#adviceFilter').html("<img src='jsp/img/ajax-loading.gif' align='absmiddle'>");
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	//var frm = $('#main3').val();
	var frm = document.getElementById('main3');
	$.get('listadoRCGanado.run?ajax=true&origenLlamada=' + frm.origenLlamada.value
			+ '&' + parameterString, function(data) {
		$("#grid").html(data);
	});
}

function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'listadoRCGanado.run?ajax=false&excel=true&' + parameterString;
}

function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	$("label[id^='campoObligatorio_']").each(function(){
		$(this).hide();
	});	
}

function comprobarCampos(incluirJmesa) {	
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('listaPolizasRC');
	}
	var resultado = false;
   	if($('#entidad').val() != '' && /^([0-9])*$/.test($('#entidad').val())){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'entidad', $('#entidad').val());
   		}
   		resultado = true;
   	}
   	if($('#oficina').val() != '' && /^([0-9])*$/.test($('#oficina').val())){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'oficina', $('#oficina').val());
   		}
   		resultado = true;
   	}
   	if($('#usuario').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'usuario', $('#usuario').val());
   		}
   		resultado = true;
   	}
   	if($('#plan').val() != '' && /^([0-9])*$/.test($('#plan').val())){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'plan', $('#plan').val());
   		}
   		resultado = true;
   	}
   	if($('#linea').val() != '' && /^([0-9])*$/.test($('#linea').val())){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'linea', $('#linea').val());
   		}
   		resultado = true;
   	}
   	if($('#clase').val() != '' && /^([0-9])*$/.test($('#clase').val())){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'clase', $('#clase').val());
   		}
   		resultado = true;
   	}
   	if($('#refpoliza').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'refpoliza', $('#refpoliza').val());
   		}
   		resultado = true;
   	}
   	if($('#refcolectivo').val() != '' && /^([0-9])*$/.test($('#refcolectivo').val())){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'refcolectivo', $('#refcolectivo').val());
   		}
   		resultado = true;
   	}
   	if($('#estadopol').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'estadopol', $('#estadopol').val());
   		}
   		resultado = true;
   	}
   	if($('#modulo').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'modulo', $('#modulo').val());
   		}
   		resultado = true;
   	}
   	if($('#nifcif').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'nifcif', $('#nifcif').val());
   		}
   		resultado = true;
   	}
   	if($('#fecenviorcId').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'fecenviorc', $('#fecenviorcId').val());
   		}
   		resultado = true;
   	}
   	if($('#estadorc').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'estadorc', $('#estadorc').val());
   		}
   		resultado = true;
   	}
   	if($('#errorrc').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'errorrc', $('#errorrc').val());
   		}
   		resultado = true;
   	}
   	if($('#refomega').val() != '' && /^([0-9])*$/.test($('#refomega').val())){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'refomega', $('#refomega').val());
   		}
   		resultado = true;
   	}
   	if($('#nsolicitud').val() != '' && /^([0-9])*$/.test($('#nsolicitud').val())){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listaPolizasRC', 'nsolicitud', $('#nsolicitud').val());
   		}
   		resultado = true;
   	}
   	return resultado;
}

function validarGrupoEntidad(){
	var codentidad = $('#entidad').val();
	if($('#grupoEntidades').val() == ""){
		return true;
	} else if (codentidad != ""){
		var grupoEntidades = $('#grupoEntidades').val().split(',');
		var encontrado = false;
		for(var i=0;i<grupoEntidades.length;i++){
			if(grupoEntidades[i] == codentidad){
				encontrado = true;
				break;
			}
		}
	} else {
		return true;
	}
	return 	encontrado;	
}

function validarOficina(){
	var codoficina = $('#oficina').val();
	if($('#grupoOficinas').val() == ""){
		return true;
	}else if (codoficina != ""){
		var grupoOficinas = $('#grupoOficinas').val().split(',');
		var encontrado = false;
		for(var i=0;i<grupoOficinas.length;i++){
			if(grupoOficinas[i] == codoficina){
				encontrado = true;
				break;
			}
		}
	} else {
		return true;
	}
	return 	encontrado;	
}