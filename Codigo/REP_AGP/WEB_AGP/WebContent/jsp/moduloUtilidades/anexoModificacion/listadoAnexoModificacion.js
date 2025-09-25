// Para evitar el cacheo de peticiones al servidor
$(document).ready(function(){
	

// Calendario de Fecha de Envio 
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
        inputField        : "fechaEnvioId",
        button            : "btn_fechaEnvio",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
  	
  	
// Validacion del formulario
$('#main3').validate({					
			
		errorLabelContainer: "#panelAlertasValidacion", 
		
		wrapper: "li",
		
		rules: {
			"poliza.colectivo.tomador.id.codentidad": {grupoEnt: true}
		},
		messages: {
			"poliza.colectivo.tomador.id.codentidad": { grupoEnt: "La Entidad seleccionada no pertenece al grupo de Entidades del usuario"}
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
		
// Lanza la consulta de Anexos de modificacion
function lanzarConsulta () {

	// Llama al metodo que llama al servidor
	onInvokeAction('listadoAnexoModificacion','filter');
}

// Metodo que realiza la llamada al servidor para listar los anexos
function onInvokeAction(id) {
	// Muestra la imagen de carga
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	
	//Lanza la llamada al servidor
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main3');
    $.get('anexoModificacionUtilidades.run?ajax=true&' + parameterString, function(data) {$("#grid").html(data)});
}	

// Metodo que realiza la llamada al servidor para generar el informe de anexos
function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'anexoModificacionUtilidades.run?ajax=false&' + parameterString;
}


// Comprueba si algun campo del formulario esta informado
// Si el parametro es true, cada campo que esta informado se incluye en el filter de jmesa
function comprobarCampos(incluirJmesa){
	
	
	
 	if (incluirJmesa) {
 		jQuery.jmesa.removeAllFiltersFromLimit('listadoAnexoModificacion');
 	} 	 
   	
   	var resultado = false;
   	
   	if ($('#entidad').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.colectivo.tomador.id.codentidad', $('#entidad').val());
   		}
   		resultado = true;
   	}  
   	
   	if ($('#oficina').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.oficina', $('#oficina').val());
   		}	       		
   		resultado = true;
   	}  
   	
   	if ($('#codusuario').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.usuario.codusuario', $('#codusuario').val());
   		}	       		
   		resultado = true;
   	}  
   	
   	if ($('#plan').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.linea.codplan', $('#plan').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#linea').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.linea.codlinea', $('#linea').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#poliza').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.referencia', $('#poliza').val());
   		}	       		
   		resultado = true;
   	}
   	
   	if ($('#tipo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.tipoReferencia', $('#tipo').val());
   		}	       		
   		resultado = true;
   	}  
   	
   	if ($('#nifcif').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.asegurado.nifcif', $('#nifcif').val());
   		}	       		
   		resultado = true;
   	} 
   	
    if ($('#fullName').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.asegurado.fullName', $('#fullName').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#estado').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','estado.idestado', $('#estado').val());
   		}	       		
   		resultado = true;
   	} 
   
   	if ($('#fechaEnvioId').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','fechaEnvioAnexo', $('#fechaEnvioId').val());
   		}	       		
   		resultado = true;
   	} 
   	tipoEnvId = $.trim ($('#tipoEnvioId').val());
   	if (tipoEnvId != ''){
   		if (incluirJmesa) {		
   			if (tipoEnvId == 'FTP') {
   				jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','tipoEnvio', tipoEnvId);
   			}else{
   				jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','tipoEnvio', 'SW');
   				jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','cupon.idcupon', tipoEnvId);
   			}
   		}	       		
   		resultado = true;
   	}
   	
   	if ($('#estadoCupon').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','cupon.estadoCupon.id', $('#estadoCuponId').val());
   		}	       		
   		resultado = true;
   	} 
   	if ($('#entmediadora').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.colectivo.subentidadMediadora.id.codentidad', $('#entmediadora').val());
   		}	       		
   		resultado = true;
   	} 
	if ($('#subentmediadora').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.colectivo.subentidadMediadora.id.codsubentidad', $('#subentmediadora').val());
   		}	       		
   		resultado = true;
   	}
	if ($('#delegacion').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoAnexoModificacion','poliza.usuario.delegacion', $('#delegacion').val());
   		}
   		resultado = true;
   	}   
   	
   	
   	
   	return resultado; 	
}

// Comprueba que los valores del formulario son correctos antes de consultar
function validarCamposConsulta () {

	// ENTIDAD
	if ($('#entidad').val() != ''){ 
	 	var entidadOk = false;
	 	try {		 	
	 		var auxEntidad =  parseFloat($('#entidad').val());
	 		if(!isNaN(auxEntidad)){
				$('#entidad').val(auxEntidad);
				entidadOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!entidadOk) {
			$('#panelAlertasValidacion').html("Valor para la entidad no v&aacute;lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	// OFICINA
	if ($('#oficina').val() != ''){ 
	 	var oficinaOk = false;
	 	
	 	try {		 	
	 		var auxOficina =  parseFloat($('#oficina').val());
	 		if(!isNaN(auxOficina)){
				$('#oficina').val(auxOficina);
				/*var inputText = document.getElementById('oficina');
				while (inputText.value.length<4){
					inputText.value = '0'+inputText.value;
				}*/
				oficinaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!oficinaOk) {
			$('#panelAlertasValidacion').html("Valor para la oficina no v&aacute;lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}

	// PLAN
 	if ($('#plan').val() != ''){
	 	var planOk = false;
	 	
	 	try {		 	
	 		var auxPlan =  parseFloat($('#plan').val());
	 		if(!isNaN(auxPlan) && $('#plan').val().length == 4){
				$('#plan').val(auxPlan);
				planOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("Valor para el plan no v&aacute;lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	// LINEA
	if ($('#linea').val() != ''){ 
	 	var lineaOk = false;
	 	try {		 	
	 		var auxLinea =  parseFloat($('#linea').val());
	 		if(!isNaN(auxLinea)){
				$('#linea').val(auxLinea);
				lineaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para la l&iacute;nea no v&aacute;lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}

	return true;
}


// Realiza la llamada al controlador para obtener los Anexos modificacion que se ajusten al filtro de busqueda
function consultar () {
	
	// Limpia las alertas anteriores
 	limpiaAlertas();	 	
 	// Comprueba si hay algun campo del filtro que esta informado
 	if (comprobarCampos(true)) {
 		// Valida los campos antes de la consulta
 		if (validarCamposConsulta ()) {		 		
 			// Lanza la consulta
	 		lanzarConsulta ();
	 	}
 	}
 	// Si no, muestra el aviso
 	else {
		avisoUnCampo ();
	}
}

// Realiza la primera llamada al controlador para obtener los Anexos modificacion l que se ajusten al filtro de busqueda
// Esta llamada no incluye los filtros de busqueda en jmesa
function consultarInicial () {
	
	// Limpia las alertas anteriores
 	limpiaAlertas();	 	
 	// Comprueba si hay algun campo del filtro que esta informado
	if (comprobarCampos (false)) {
		// Valida los campos antes de la consulta
 		if (validarCamposConsulta ()) {		 		
 			// Lanza la consulta
			$('#primeraBusqueda').val('primeraBusqueda');
			$('#main3').submit();
	 	}
	}
	// Si no, muestra el aviso
	else {
		avisoUnCampo ();
	}
}

// Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo () {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}

// Limpia todas las alertas mostradas
function limpiaAlertas () {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
}

// Limpia los valores introducidos y vuelve a la pantalla inicial
function limpiar () {
	$('#limpiar').submit();
}

// Muestra la situacion actualizada de la poliza tras llamar al WS de Agroseguro
function verSituacionActual (idAnexo,ref, plan, tipoRef) {
	var frm = document.getElementById('polizaActualizada');
	frm.idAnexoPolActualizada.value = idAnexo;
	frm.refPolizaPlzAct.value = ref;
	frm.codPlanPlzAct.value = plan;
	/* Pet. 57626 ** MODIF TAM (30.06.2020) -> Pasamos tambien el tipoRef para la nueva llamada de la Situacion Actualizada*/
	frm.tipoRefPlzAct.value = tipoRef;
	frm.aleatorio.value= Math.random();
	frm.action = frm.action + '?rand=' + frm.aleatorio.value;
	$('#polizaActualizada').attr('target', '_blank');

	$('#polizaActualizada').submit();
}

