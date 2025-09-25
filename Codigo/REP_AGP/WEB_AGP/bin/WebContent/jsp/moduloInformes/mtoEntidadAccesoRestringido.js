$(document).ready(function(){

    $('#main3').validate({	
		
		onfocusout: function(element) {
			if ( ($('#method').val() == "doEdita") || ($('#method').val() == "doAlta") ) {
				this.element(element);
			}
		},
								
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		
		rules: {
			"entidad.codentidad" : {required: true, digits: true},
	 		"accesoDisenador" : {required: true},
	 		"accesoGenerador" : {required: true}
	 		
		},
		messages: {
			"entidad.codentidad":{required: "El campo Entidad es obligatorio", digits: "El campo Entidad solo puede contener numeros"},
			"accesoDisenador":{required: "El campo Acceso al dise&ntilde;ador es obligatorio"},
			"accesoGenerador":{required: "El campo Acceso al generador es obligatorio"}
	 	}
	});
});

// Realiza la llamada ajax al servidor
function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    $.get('mtoEntidadesAccesoRestringido.run?ajax=true&' + parameterString, function(data) {
    $("#grid").html(data)});
} 


// Realiza la consulta con los datos introducidos en el filtro
function consultar(){
	// Oculta el botón de Modificar
	$("#btnModificar").hide();
	// Limpia la alertas previas
	limpiaAlertas();
	// Habilita el combo 'Acceso al generador'
	habilitarAccesoGenerador();
	// Comprueba qué campos se han informado y los incluye en el filtro
	comprobarCampos();
	// Llama al servidor para buscar
	onInvokeAction('entidadAccesoRestringido','filter');
}


// Comprueba qué campos se han informado y los incluye en el filtro
function comprobarCampos(){
 	
	jQuery.jmesa.removeAllFiltersFromLimit('entidadAccesoRestringido');
 	
 	if ($('#entidad').val() != ''){
   		jQuery.jmesa.addFilterToLimit('entidadAccesoRestringido','entidad.codentidad', $('#entidad').val());
 	}       
 	
 	if ($('#accesoDisenador').val() != '' && !isNaN ($('#accesoDisenador').val())){
   		jQuery.jmesa.addFilterToLimit('entidadAccesoRestringido','accesoDisenador', $('#accesoDisenador').val());
 	}
 	
 	if ($('#accesoGenerador').val() != '' && !isNaN ($('#accesoGenerador').val())){
   		jQuery.jmesa.addFilterToLimit('entidadAccesoRestringido','accesoGenerador', $('#accesoGenerador').val());
 	}
 	
}
 
// Limpia las alertas que se están mostrando actuamente 
function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
}	

// Limpiar el formulario y las alertas y muestra todos los registros
function limpiar(){
	limpiaAlertas();
	$("#id").val('');
	$("#entidad").val('');	
	$('#desc_entidad').val('');
	$('#accesoDisenador').val('');
	$('#accesoGenerador').val('');
	consultar();								
}

// Muestra en el formulario los datos del registro elegido y muestra el botón de modificar
function editar (id, entidad, desc_entidad, accesoDisenador, accesoGenerador){
	limpiaAlertas();
	$("#btnModificar").show();
	visualizar (id, entidad, desc_entidad, accesoDisenador, accesoGenerador);
}

// Muestra en el formulario los datos del registro elegido
function visualizar (id, entidad, desc_entidad, accesoDisenador, accesoGenerador){
	$("#id").val(id);
	$("#entidad").val(entidad);	
	$('#desc_entidad').val(desc_entidad);
	$('#accesoDisenador').val(accesoDisenador);
	$('#accesoGenerador').val(accesoGenerador);
	// Si el acceso al diseñador es 'Permitido', se deshabilita la opción de acceso al generador
	comprobarValor (accesoDisenador);
}

// Borra el registro seleccionado
function borrar(id) {
	$('#main3').validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar este registro?')){
		$("#id").val(id);
		$("#method").val("doBaja");
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main3").submit();	
	}	
}

// Da de alta el registro
function alta(){
	limpiaAlertas();
	
	// No se puede dar de alta la restricción si los dos accesos están permitidos
	if ($('#accesoDisenador').val() == $('#codPermitido').val() && $('#accesoGenerador').val() == $('#codPermitido').val()){
		$('#panelAlertasValidacion').html("No se puede dar de alta la restricción si se permiten los dos accesos");
		$('#panelAlertasValidacion').show();
 	}
 	else{
 		// Habilita el combo 'Acceso al generador'
		habilitarAccesoGenerador();
 		$("#id").val("");
    	$("#method").val("doAlta");
     	$('#main3').submit();
 	}
}	

// Modifica el registro seleccionado
function modificar(){
	
	limpiaAlertas();
	
	// No se puede modificar la restricción si los dos accesos están permitidos
	if ($('#accesoDisenador').val() == $('#codPermitido').val() && $('#accesoGenerador').val() == $('#codPermitido').val()){
		$('#panelAlertasValidacion').html("No se puede dar de alta la restricción si se permiten los dos accesos");
		$('#panelAlertasValidacion').show();
 	}
 	else{
 		// Habilita el combo 'Acceso al generador'
		habilitarAccesoGenerador();
    	$("#method").val("doEdita");
     	$('#main3').submit();
 	}
}	
	
/**
 * Si el valor recibido se corresponde con 'Permitido', se marca en el combo de 'Acceso al generador' el valor 
 * 'Permitido a Usuarios y Perfiles concretos'. En caso contrario se habilita el combo
 */	
function comprobarValor (valor) {
	
	// Si el valor recibido se corresponde con 'Permitido'
	if (valor == $('#codPermitido').val()) {
		$('#accesoGenerador').val($('#codPermitidoConcreto').val());
		$('#accesoGenerador').attr('disabled', true);
	}
	// Si no, se habilita el combo si estaba deshabilitado
	else if ($('#accesoGenerador').attr('disabled') == true) {
		$('#accesoGenerador').val('');
		$('#accesoGenerador').attr('disabled', false);
	}
}	

/**
 * Habilita el combo 'Acceso al generador'
 */
function habilitarAccesoGenerador () {
	$('#accesoGenerador').attr('disabled', false);
}	