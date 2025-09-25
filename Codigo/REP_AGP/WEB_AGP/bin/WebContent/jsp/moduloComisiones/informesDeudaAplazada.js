$(document).ready(function(){
		 	
		 	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
			document.getElementById("main3").action = URL;  
			
			//inicializarFechas();

			//Inicializar los calendarios de los campos fecha
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
			    inputField        : "fechaemi",
			    button            : "btn_fechaemisionrecibo",
			    ifFormat          : "%d/%m/%Y",
			    daFormat          : "%d/%m/%Y",
			    align             : "Br"			        	        
			});
			
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
			    inputField        : "fechaacep",
			    button            : "btn_fechaaceptacion",
			    ifFormat          : "%d/%m/%Y",
			    daFormat          : "%d/%m/%Y",
			    align             : "Br"			        	        
			});
			
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
			    inputField        : "fechacie",
			    button            : "btn_fechacierre",
			    ifFormat          : "%d/%m/%Y",
			    daFormat          : "%d/%m/%Y",
			    align             : "Bl"			        	        
			});
			
			$('#main3').validate({					
				
				errorLabelContainer: "#panelAlertasValidacion",
				wrapper: "li",
				
				rules: {
					"codentidad": {grupoEnt: true}
				},
				messages: {
					"codentidad": { grupoEnt: "La Entidad seleccionada no pertenece al grupo de Entidades del usuario"}
				}
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
			
	        showOrHideExportIcon();
});

function inicializarFechas(){
	// inizializamos fechaefecto del popup baja
	var fecha = new Date();
	var dia = fecha.getDate();
	var mes = fecha.getMonth();
	var mes = mes + 1;
	var anio = fecha.getFullYear();
	if (dia < 10){
		dia = "0"+ dia;
	}
	var fecFormateada = dia + "/" + mes + "/" + anio;
	$('#fechaemision').val(fecFormateada);
	$('#fechaaceptacion').val(fecFormateada);
	$('#fechacierre').val(fecFormateada);
}

function consultar(){
	//alert($('#mayorIgual2015').val());
	limpiaAlertas();
	if (validarConsulta()) {
		$('#origenLlamada').val('informeDeudaAplazada');
		//alert($('#origenLlamada').val());
		$('#method').val('doConsulta');
		$("#main3").submit();	
	}
}

function limpiar(){
	$("#plan").val('');
	$("#linea").val('');
	$("#desc_linea").val('');
	$("#idcolectivo").val('');
	$("#recibo").val('');
	$("#fase").val('');
	$("#fechaemi").val('');
	$("#fechaacep").val('');
	$("#fechacie").val('');
	$("#ciftomador").val('');
	$("#nombretomador").val('');
	$("#referenciapoliza").val('');
	if($('#perfil').val() == "0" || $('#perfil').val() == "5"){
		$('#entidad').val('');
		$("#desc_entidad").val('');
	}
	if ( ($('#perfil').val() == '1' && $('#externo').val() == '0') || ($('#perfil').val() != '1') ) {
		$("#entmediadora").val('');
		$("#subentmediadora").val('');
	}
//	$('#panelAlertasValidacion').html("");
//	$("#panelAlertasValidacion").hide();
	limpiaAlertas();
	$('#origenLlamada').val('menuGeneral');
	//if($('#mayorIgual2015').val()== 'true'){
	//alert($('#origenLlamada').val());
	$('#method').val('doConsulta');
	$("#main3").submit();	
}

function limpiaAlertas() {
	$('#alerta').val("");
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();

	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');
	$("#panelAlertas").html('');

}

function validarConsulta(){
	// Valida el campo 'entidad' si esta informado
	if ($('#entidad').val() != '') {
		var entidadOk = false;
		try {
			var auxentidad = parseFloat($('#entidad').val());
			if (!isNaN(auxentidad)) {
				if (valor<0){
					entidadOk = false;
				}else{
					$('#entidad').val(auxentidad);
					entidadOk = true;
				}
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!entidadOk) {
			$('#panelAlertasValidacion')
					.html("Valor para la entidad no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	// Valida el campo 'plan' si esta informado
	if ($('#plan').val() != '') {
		var planOk = false;
		try {
			var valor = parseFloat($('#plan').val());
			if (!isNaN(valor)) {
				//alert(valor);
				if (valor<0){
					planOk = false;
				}else{	
					$('#plan').val(valor);planOk = true;
				}
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Plan no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	

	
	// Valida el campo 'Linea' si esta informado
	if ($('#linea').val() != '') {
		var lineaOk = false;
		try {
			var valor = parseFloat($('#linea').val());
			if (!isNaN(valor)) {
				if (valor<0){
					lineaOk = false;
				}else{
					$('#linea').val(valor);
					lineaOk = true;
				}
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo l�nea no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	// Valida el campo 'entidadMediadora' si esta informado
	if ($('#entmediadora').val() != '') {
		var entmediadoraOk = false;
		try {
			var valor = parseFloat($('#entmediadora').val());
			if (!isNaN(valor)) {
				if (valor<0){
					entmediadoraOk = false;
				}else{
					$('#entmediadora').val(valor);
					entmediadoraOk = true;
				}
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!entmediadoraOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Entidad Mediadora no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	// Valida el campo 'subentmediadora' si esta informado
	if ($('#subentmediadora').val() != '') {
		var subentmediadoraOk = false;
		try {
			var valor = parseFloat($('#subentmediadora').val());
			if (!isNaN(valor)) {
				if (valor<0){
					subentmediadoraOk = false;
				}else{
					$('#subentmediadora').val(valor);
					subentmediadoraOk = true;
				}
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!subentmediadoraOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Subentidad Mediadora no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	// Valida el campo 'colectivo' si esta informado
	if ($('#idcolectivo').val() != '') {
		var idcolectivoOk = false;
		try {
			var valor = parseFloat($('#idcolectivo').val());
			if (!isNaN(valor)) {
				if (valor<0){
					idcolectivoOk = false;
				}else{
					$('#idcolectivo').val(valor);
					idcolectivoOk = true;
				}
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!idcolectivoOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Colectivo no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	// Valida el campo 'recibo' si esta informado
	if ($('#recibo').val() != '') {
		var reciboOk = false;
		try {
			var valor = parseFloat($('#recibo').val());
			if (!isNaN(valor)) {
				if (valor<0){
					reciboOk = false;
				}else{
					$('#recibo').val(valor);
					reciboOk = true;
				}
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!reciboOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Recibo no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	// Valida el campo 'fase' si esta informado
	if ($('#fase').val() != '') {
		var faseOk = false;
		try {
			var valor = parseFloat($('#fase').val());
			if (!isNaN(valor)) {
				if (valor<0){
					faseOk = false;
				}else{
					$('#fase').val(valor);
					faseOk = true;
				}
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!faseOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo fase no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	if (
			$('#entidad').val() == '' && 
			$('#plan').val() == '' && 
			$('#linea').val() == '' &&
			$('#entmediadora').val() == '' && 
			$('#subentmediadora').val() == '' && 
			$('#idcolectivo').val() == '' &&
			$('#recibo').val() == '' && 
			$('#fase').val() == '' && 
			$('#fechaemi').val() == '' &&
			$('#fechaacep').val() == '' && 
			$('#fechacie').val() == ''
		){
			
			$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
			$('#panelAlertasValidacion').show();
			return false;
	}

	return true;
}
	
function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	$.get('informesDeudaAplazada.run?ajax=true&mayorIgual2015=' + $('#mayorIgual2015').val() + '&' +decodeURIComponent(parameterString),
			function(data) {
				$("#grid").html(data);
		        showOrHideExportIcon();
			});
}

function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'informesDeudaAplazada.run?ajax=false&'  + parameterString;
}

/*function validarEntidadParaReplica(){
	var error="";
	var entidad= $('#entidad').val();
	if(entidad ==""){
		error="Debe seleccionar una entidad v�lida para la r�plica.";
	}else if(!/^([0-9])*$/.test(entidad)){
		error="Formato de entidad erroneo. Compruebe el valor del campo";
	}else{
		if ( $('#perfil').val()== 5){
			var listaEntCRM = $('#grupoEntidades').val();
			if (listaEntCRM.indexOf(entidad) < 0) {
				error="No tiene permisos para realizar una r�plica sobre esta entidad.";
			} 
		}
	}
	return error;
}*/

function showOrHideExportIcon() {
    var table = document.getElementById("consultaDeudaAplazada");
    var matchingRow = table.querySelector("tr[id^='consultaDeudaAplazada']");

    if (matchingRow) {
        document.getElementById("divImprimir").style.display = "block";
    } else {
        document.getElementById("divImprimir").style.display = "none";
    }
}

function exportToExcel(size) {
    var frm = document.getElementById('exportToExcel');
    frm.target="_blank";
    frm.submit();
}

