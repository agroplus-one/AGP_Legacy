$(document)
		.ready(
				function() {

					var URL = UTIL.antiCacheRand(document
							.getElementById("main3").action);
					document.getElementById("main3").action = URL;

					$('#main3').validate(
								{
									errorLabelContainer : "#panelAlertasValidacion",
									onfocusout : function(element) {
								},
							wrapper : "li", highlight : function(element,errorClass){
									$("#campoObligatorio_" + element.id).show();
							},
							unhighlight : function(element,errorClass){
									$("#campoObligatorio_" + element.id).hide();
							},
							rules : {
									"anyo" : {required : true, digits : true},
									"retencion" : {required : true, pctRetencion: true}
							},
							messages : {
								"anyo" : {required : "El campo Año es obligatorio",digits : "El campo Año sólo puede contener dígitos"},
								"retencion" : {required : "El campo Retención es obligatorio",pctRetencion: "El campo Retención debe contener un número entre 0 y 100"}								
							}
						});
					
					jQuery.validator.addMethod("pctRetencion", function(value, element, params) {
						var isvalid = false;
						value = value.replace(",",".");
						if(!isNaN(value)){
							if(value >=0 && value <= 100) //999.99
								isvalid = true;
							else
								isvalid = false;
						}else
							isvalid = false;
						return isvalid;
					});
					
					if ($("#anyo").val() != "") {
						
						if($('#origenLlamada').val()!= 'errorALta'){
							$("#btnAlta").hide();
							$("#btnModif").show();
						}else{
							$("#btnAlta").show();
							$("#btnModif").hide();
						}
					}

				});
	
/** limpia los campos del formulario * */
function limpiar() {
	$('#anyo').val('');
	$('#retencion').val('');
	$('#origenLlamada').val('menuGeneral');
	$("#main3").validate().cancelSubmit = true;
	$('#method').val('doConsulta');
	$('#main3').submit();

}

/** ALTA* */
function alta() {
	limpiaAlertas();
	$('#method').val('doAlta');
	$('#main3').submit();
}

/** CONSULTAR */
function consultarInicial() {	
	limpiaAlertas();
	var frm = document.getElementById('main3');
	if (validarConsulta()) {
		if (comprobarCampos(false)) {
			frm.method.value = 'doConsulta';
			frm.origenLlamada.value = 'primeraBusqueda';
			$("#main3").validate().cancelSubmit = true;
			$('#main3').submit();
		}// Si no, muestra el aviso
		else {
			avisoUnCampo();
		}
	}
}
function consultar() {	
	limpiaAlertas();

	if (validarConsulta()) {
		if (comprobarCampos(true)) {
			$("#btnModificar").hide();
			lanzarConsulta();
		}// Si no, muestra el aviso
		else {
			avisoUnCampo();
		}
	}
}

/** MODIFICACION * */
function modificar(anyo, retencion) {
	limpiaAlertas();
	//alert(anyo);
	$('#anyo').val(anyo);
	$('#retencion').val(retencion);
	// BOTONES
	$('#btnAlta').hide();
	$('#btnModif').show();
}

function editar() {
	limpiaAlertas()
	$('#method').val('doEdita');
	$('#main3').submit();	
	
}

/** BORRAR * */
function borrar(anyo, retencion) {
	$("#main3").validate().cancelSubmit = true;
	$('#idBorrar').val(anyo);
	$('#idRetencion').val(retencion);
	jConfirm('¿Está seguro de que desea dar de baja el registro seleccionado?',
			'Diálogo de Confirmación', function(r) {
				if (r) {
					$('#method').val('doBorrar');
					$('#main3').submit();
				}
			});
}

/** OTRAS FUNCIONES */
function limpiaAlertas() {
	$('#alerta').val("");
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');
	$("#panelAlertas").html('');
	$('#mensaje').val("");
	$("#panelMensajeValidacion").hide();
    $("#panelMensajeValidacion").html('');
}
// Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo() {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}

// Lanza la consulta de Retenciones
function lanzarConsulta() {
	// Llama al metodo que llama al servidor
	onInvokeAction('consultaRetenciones', 'filter');
	$('#listaIdsMarcados').val('');
	$('#marcaTodos').val('false');
	
}

function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	$.get('mtoRetenciones.run?ajax=true&' + decodeURIComponent(parameterString),
			function(data) {
				$("#grid").html(data);						
			});
}
function comprobarCampos(incluirJmesa) {

	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('consultaRetenciones');
	}
	var resultado = false;

	if ($('#anyo').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaRetenciones',
					'anyo', $('#anyo')
							.val());
		}
		resultado = true;
	}
	if ($('#retencion').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaRetenciones',
					'retencion', $('#retencion').val());
		}
		resultado = true;
	}
	return resultado;
}


jQuery.validator.addMethod("pctrangogenCM", function(value, element, params) {
	var isvalid = false;
	value = value.replace(",",".");
	if(!isNaN(value)){
		if(value >=0 && value <= 100)
			isvalid = true;
		else
			isvalid = false;
	}else
		isvalid = false;
	return isvalid;
});	 


function validarConsulta() {
	// Valida el campo 'anyo' si esta informado
	if ($('#anyo').val() != '') {
		//alert(('#anyo').val());
		var anyoOk = false;
		try {
			var auxAnyo = parseFloat($('#anyo').val());
			if (!isNaN(auxAnyo) && auxAnyo >=0) {				
					$('#anyo').val(auxAnyo);
					anyoOk = true;
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!anyoOk) {
			$('#panelAlertasValidacion').html("Valor para el año no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	// Valida el campo 'retencion' si esta informado
	if ($('#retencion').val() != '') {
		//alert(('#retencion').val());
		var retencionOk = false;
		try {
			var valor = parseFloat($('#retencion').val());
			if (!isNaN(valor)) {
					if(valor >=0 && valor <= 100){
						$('#retencion').val(valor);
						retencionOk = true;
					}else{
						$('#panelAlertasValidacion').html("El campo Retención debe contener un número entre 0 y 100");
						$('#panelAlertasValidacion').show();
						return false;
					}
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!retencionOk) {
			$('#panelAlertasValidacion').html("Valor para la retención no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	return true;
}

//*******************************************************************
	