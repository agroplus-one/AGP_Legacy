$(document)
		.ready(
				function() {

					var URL = UTIL.antiCacheRand(document
							.getElementById("main3").action);
					document.getElementById("main3").action = URL;

					if ($('#id').val() != null && $('#id').val() != '') {
						$('#btnModificar').show();
					}

					$('#main3')
							.validate(
									{

										onfocusout : function(element) {
											if (($('#method').val() == "doEditar")
													|| ($('#method').val() == "doAlta")) {
												this.element(element);
											}
										},
										errorLabelContainer : "#panelAlertasValidacion",
										wrapper : "li",

										rules : {
											"clase" : {
												required : true,
												digits : true,
												maxlength : 3
											},
											"linea.codplan" : {
												required : true,
												digits : true,
												minlength : 4
											},
											"linea.codlinea" : {
												required : true,
												digits : true
											},
											"descripcion" : {
												required : true
											},
											"maxpolizas" : {
												required : true,
												digits : true
											},
											"rdtoHistorico":{
												required : true
											},
											"comprobarRce":{
												required : true
											}

										},
										messages : {
											"clase" : {
												required : "El campo Clase es obligatorio",
												digits : "El campo Clase s�lo puede contener d�gitos",
												maxlength : "El campo Clase debe contener como m�ximo 3 d�gitos"
											},
											"linea.codplan" : {
												required : "El campo Plan es obligatorio",
												digits : "El campo Plan s�lo puede contener d�gitos",
												minlength : "El campo Plan debe contener 4 d�gitos"
											},
											"linea.codlinea" : {
												required : "El campo L�nea es obligatorio",
												digits : "El campo L�nea s�lo puede contener d�gitos"
											},
											"descripcion" : {
												required : "El campo Descripci�n es obligatorio"
											},
											"maxpolizas" : {
												required : "El campo M�ximo de polizas es obligatorio",
												digits : "El campo Max.P�lizas s�lo puede contener d�gitos"
											},
											"rdtoHistorico":{
												required : "El campo Rdto. Historico es obligatorio"
											},
											"comprobarRce":{
												required : "El campo Incluir Riesgo Cubierto Elegido es obligatorio"
											}
										}
									});

				});

function consultar() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();

	// Llamamos al metodo comprobarCampos para a�adir los valores al "limit"
	comprobarCampos();
	onInvokeAction('consultaClaseMto', 'filter');
}

function comprobarCampos() {
	jQuery.jmesa.removeAllFiltersFromLimit('consultaClaseMto');
	var resultado = false;

	if ($('#plan').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaClaseMto', 'linea.codplan', $(
				'#plan').val());
		resultado = true;
	}
	if ($('#linea').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaClaseMto', 'linea.codlinea', $(
				'#linea').val());
		resultado = true;
	}
	if ($('#clase').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaClaseMto', 'clase', $('#clase')
				.val());
		resultado = true;
	}
	if ($('#maxpolizas').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaClaseMto', 'maxpolizas', $(
				'#maxpolizas').val());
		resultado = true;
	}
	if ($('#comprobarAac').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaClaseMto', 'comprobarAac', $(
				'#comprobarAac').val());
		resultado = true;
	}

	if ($('#rdtoHistorico').val() !=undefined && $('#rdtoHistorico').val() != '') {		
		jQuery.jmesa.addFilterToLimit('consultaClaseMto', 'rdtoHistorico', $(
				'#rdtoHistorico').val());
		resultado = true;
	}
	if ($('#comprobarRce').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaClaseMto', 'comprobarRce', $(
				'#comprobarRce').val());
		resultado = true;
	}
	
	return resultado;
}
// DAA 25/01/2013
function limpiarCarga() {
	$('#desc_linea').val('');
	$('#clase').val('');
	$('#descripcion').val('');
	$('#maxpolizas').val('');
	$('#comprobarAac').val('');
	$('#comprobarRce').val('');
	consultar();
}

function limpiar() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	$('#btnModificar').hide();
	$('#plan').val('');
	$('#linea').val('');
	$('#desc_linea').val('');
	$('#clase').val('');
	$('#descripcion').val('');
	$('#maxpolizas').val('');
	$('#comprobarAac').val('');
	$('#rdtoHistorico').val('');
	$('#comprobarRce').val('');
	

	jQuery.jmesa.removeAllFiltersFromLimit('consultaClaseMto');
	onInvokeAction('consultaClaseMto', 'clear');
}

function editar(id, linea, desc_linea, plan, clase, descripcion, maxpolizas,
		comprobarAac,rdtoHistorico,comprobarRce) {

	var frm = document.getElementById('main3');

	frm.target = "";
	frm.id.value = id;
	frm.linea.value = linea;
	frm.desc_linea.value = desc_linea;
	frm.plan.value = plan;
	frm.clase.value = clase;
	frm.maxpolizas.value = maxpolizas;
	frm.descripcion.value = descripcion;
	frm.comprobarAac.value = comprobarAac;
	frm.rdtoHistorico.value = rdtoHistorico;
	frm.comprobarRce.value = comprobarRce;
	$('#btnModificar').show();
}

function modificar() {
	comprobarCampos();
	$("#method").val("doEditar");
	$("#origenLlamada").val("editar");
	$('#main3').submit();
}

function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	// DAA 25/01/2013
	$.get('cargaClase.run?ajax=true&descripcion=' + frm.descripcion.value + '&'
			+ parameterString + '&origenLlamada=' + frm.origenLlamada.value,
			function(data) {
				$("#grid").html(data)
			});
}

function borrar(id) {
	$('#main3').validate().cancelSubmit = true;
	if (confirm('Se borrar�n la clase y todos sus detalles. �Est� seguro de que desea eliminar?')) {
		$("#method").val("doBorrar");
		$("#id").val(id);
		$("#main3").submit();
	}
}
function alta() {
	comprobarCampos();
	$("#method").val("doAlta");
	$("#main3").submit();
}

function detalle(id, linea, plan, lineaseguroid, clase, descripcion, esLineaGanado, fechaInicioContratacion) {
	
	
	if(esLineaGanado=='0'){
		$('#detalleid').val(id);
		$('#detalleplan').val(plan);
		$('#detallelinea').val(linea);
		$('#detallelineaseguroid').val(lineaseguroid);
		$('#detalleclase').val(clase);
		$('#detalledesc').val(descripcion);
		$('#esLineaGanado').val(esLineaGanado);
		$('#fechaInicioContratacion').val(fechaInicioContratacion);
		$("#main").submit();	
	}else{
		$('#detalleganadoid').val(id);
		$('#detalleganadoplan').val(plan);
		$('#detalleganadolinea').val(linea);
		$('#detalleganadolineaseguroid').val(lineaseguroid);
		$('#detalleganadoclase').val(clase);
		$('#detalleganadodesc').val(descripcion);
		$('#esLineaGanadoSG').val(esLineaGanado);
		$('#fechaInicioContratacionGan').val(fechaInicioContratacion);

		document.getElementById("methodSG").value = "doConsulta";
		//$('#method').val("doConsulta");
		$("#mainGanado").submit();
		
		
	}
}

function replicar() {
	comprobarCampos();
	$('#main3').validate().cancelSubmit = true;
	if (validarLineaseguriod()) {
		$('#planreplica').val('');
		$('#lineareplica').val('');
		lupas.muestraTabla('LineaReplica', 'principio', '', '');
	}
}

function doReplicar() {
	// DAA 17/12/2012 Si clase = null replicaran todas las clases para ese plan
	// linea
	// Se comprueba que el plan y linea a replicar no son vacios antes de llamar
	// al servidor
	// Si son vacios, se ha hecho click en un elemento de la lupa que no es un
	// registro (ordenacion, etc.)
	var mensaje = '';
	if ($("#clase").val() != '') {
		mensaje = '�Desea replicar la clase ' + $("#clase").val()
				+ ' y sus detalles?';
	} else {
		mensaje = '�Desea replicar todas las clases y sus detalles para este Plan y L�nea?'
	}

	if ($('#planreplica').val() != '' && $('#lineareplica').val() != '') {
		if (planLineaDiferentes()) {
			if (confirm(mensaje)) {
				$("#method").val("doReplicar");
				$("#main3").submit();
			}
		} else {
			$('#panelAlertasValidacion').html(
					"El plan/l�nea origen no puede ser igual que el destino");
			$('#panelAlertasValidacion').show();
		}
	}
}

function validarLineaseguriod() {
	// Valida el campo 'Plan' si esta informado
	if ($('#plan').val() != '') {
		var planOk = false;
		try {
			var auxPlan = parseFloat($('#plan').val());
			if (!isNaN(auxPlan) && $('#plan').val().length == 4 && auxPlan > 0) {
				$('#plan').val(auxPlan);
				planOk = true;
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("Valor para el plan no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	} else {
		$('#panelAlertasValidacion').html("Debe introducir un plan");
		$('#panelAlertasValidacion').show();
		return false;
	}

	// Valida el campo 'Linea' si esta informado
	if ($('#linea').val() != '') {
		var lineaOk = false;
		try {
			var auxLinea = parseFloat($('#linea').val());
			if (!isNaN(auxLinea) && auxLinea > 0) {
				$('#linea').val(auxLinea);
				lineaOk = true;
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para la l�nea no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	} else {
		$('#panelAlertasValidacion').html("Debe seleccionar una l�nea");
		$('#panelAlertasValidacion').show();
		return false;
	}
	return true;

}

// Devuelve un boolean indicando si los plan/linea origen y destino de la
// replica son diferentes
function planLineaDiferentes() {
	if ($('#planreplica').val() == $('#plan').val()
			&& $('#lineareplica').val() == $('#linea').val())
		return false
	else
		return true;
}
// DAA 25/01/2013
function cargarClase(idClase) {
	$('#id').val('');
	$('#plan').val('');
	$('#linea').val('');
	$('#desc_linea').val('');
	$('#clase').val('');
	$('#descripcion').val('');
	$("#method").val("doCarga");
	$("#id").val(idClase);
	var frm = document.getElementById('main3');
	frm.target = "";
	$("#main3").validate().cancelSubmit = true;
	if (frm.vieneDeCicloPoliza.value == "true") {
		document.getElementById("main3").action = UTIL
				.antiCacheRand("cargaClase.run");
	}
	$("#main3").submit();
}

// DAA 25/01/2013
function coberturas(idlinea) {
	$("#method").val("doCoberturas");
	$("#id").val(idlinea);
	$("#main3").validate().cancelSubmit = true;
	var frm = document.getElementById('main3');
	if (frm.vieneDeCicloPoliza.value == "true") {
		document.getElementById("main3").action = UTIL
				.antiCacheRand("cargaClase.run");
	}
	$("#main3").submit();
}
