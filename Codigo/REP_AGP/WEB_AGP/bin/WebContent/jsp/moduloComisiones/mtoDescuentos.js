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
										wrapper : "li", highlight : function(element,errorClass) 
											{
												$("#campoObligatorio_" + element.id).show();
											},
										unhighlight : function(element,errorClass) 
											{
												$("#campoObligatorio_" + element.id).hide();
											},
										rules : 
											{
												"subentidadMediadora.entidad.codentidad" : 
												{
													required : true, digits : true
												},
												"oficina.id.codoficina" : 
												{
													digits : true
												},
											"subentidadMediadora.id.codentidad" : {
												required : true,
												digits : true
											},
											"subentidadMediadora.id.codsubentidad" : {
												required : true,
												digits : true,
												range : [ 0, 99 ]
											},
											"delegacion" : {
												digits : true
											},
											"pctDescMax" : {
												required : true,
												range : [ 0, 100 ]
											},
											"linea.codplan" : {
												required : true,
												range : [ 2015, 9999 ]
											},
											"linea.codlinea" : {
												required : true
											},
											"permitirRecargo" : {
												required : true
											},
											"verComisiones" : {
												required : true
											}

										},
										messages : {
											"subentidadMediadora.entidad.codentidad" : {
												required : "El campo Entidad es obligatorio",
												digits : "El campo Entidad sólo puede contener dígitos"
											},
											"oficina.id.codoficina" : {
												digits : "El campo Oficina sólo puede contener dígitos"
											},
											"subentidadMediadora.id.codentidad" : {
												required : "El campo Entidad Mediadora es obligatorio",
												digits : "El campo Entidad Mediadora sólo puede contener dígitos"
											},
											"subentidadMediadora.id.codsubentidad" : {
												required : "El campo Subentidad Mediadora es obligatorio",
												digits : "El campo Subentidad Mediadora sólo puede contener dígitos",
												range : "Subentidad Mediadora no válida"
											},
											"delegacion" : {
												digits : "El campo Delegación sólo puede contener digitos"
											},
											"pctDescMax" : {
												required : "El campo % Descuento Máximo es obligatorio.",
												range : "El campo % Descuento Máximo debe contener un número entre 0 y 100"
											},
											"linea.codplan" : {
												required : "El campo Plan es obligatorio.",
												range : "El campo Plan debe ser mayor o igual a 2015"
											},
											"linea.codlinea" : {
												required : "El campo linea es obligatorio."
											},
											"permitirRecargo" : {
												required : "El campo Permitir Recargos es obligatorio"
											},
											"verComisiones" : {
												required : "El campo Ver Comisiones es obligatorio."
											}
										}
									});

					if ($("#id").val() != "") {
						
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
	$('#id').val('');
	$('#entidad').val('');
	$('#desc_entidad').val('');
	$('#oficina').val('');
	$('#desc_oficina').val('');
	$('#entmediadora').val('');
	$('#subentmediadora').val('');
	$('#delegacion').val('');
	$('#pctDescMax').val('');
	$('#desc_linea').val('');
	$('#plan').val('');
	$('#linea').val('');
	$('#permitirRecargo').val('');
	$('#verComisiones').val('');
	// alert($('#verComisiones').val());
	$('#origenLlamada').val('menuGeneral');
	$("#main3").validate().cancelSubmit = true;
	$('#method').val('doConsulta');
	$('#main3').submit();

}

/** ALTA* */
function alta() {
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
function modificar(id, codentidad, codoficina, entMedia, subEntMedia,
		delegacion, nomEntidad, nomOfi, pctDescMax, codplan, codlinea,
		permitirRecargo, verComisiones, nomlinea) {
	$('#id').val(id);
	$('#entidad').val(codentidad);

	$('#entmediadora').val(entMedia);
	$('#subentmediadora').val(subEntMedia);
	if (delegacion != "Todas") {
		$('#delegacion').val(delegacion);
	}
	$('#desc_entidad').val(nomEntidad);

	if (codoficina == -1) {// como si fuera valor nulo. No lo mostranos
		$('#oficina').val('');
		$('#desc_oficina').val('');
	} else {
		$('#oficina').val(codoficina);
		$('#desc_oficina').val(nomOfi);
	}

	$('#pctDescMax').val(pctDescMax);
	$('#plan').val(codplan);
	$('#linea').val(codlinea);
	$('#desc_linea').val(nomlinea);
	$('#permitirRecargo').val(permitirRecargo);
	$('#verComisiones').val(verComisiones);

	// BOTONES
	$('#btnAlta').hide();
	$('#btnModif').show();
}
function editar() {
	/*if ($('#oficina').val == '-1') {// como si fuera valor nulo. No lo mostranos
		$('#oficina').val('');
		$('#desc_oficina').val('');
	}*/
	$('#method').val('doEdita');
	$('#main3').submit();	
	
}

/** BORRAR * */
function borrar(id, codentidad, codoficina, entMedia, subEntMedia, delegacion,
		nomEntidad, nomOfi, pctDescMax, codplan, codlinea, permitirRecargo,
		verComisiones) {
	$("#main3").validate().cancelSubmit = true;
	$('#id').val(id);
	$('#entidad').val(codentidad);	
	$('#entmediadora').val(entMedia);
	$('#subentmediadora').val(subEntMedia);
	$('#delegacion').val(delegacion);
	$('#desc_entidad').val(nomEntidad);
	
	
	if (codoficina == -1) {// como si fuera valor nulo. No lo mostranos
		$('#oficina').val('');
		$('#desc_oficina').val('');
	} else {
		$('#oficina').val(codoficina);
		$('#desc_oficina').val(nomOfi);
	}
		
	$('#pctDescMax').val(pctDescMax);
	$('#plan').val(codplan);
	$('#linea').val(codlinea);
	$('#permitirRecargo').val(permitirRecargo);
	$('#verComisiones').val(verComisiones);

	
	jConfirm('¿Está seguro de que desea dar de baja el registro seleccionado?',
			'Diálogo de Confirmación', function(r) {
				if (r) {
					$('#oficina').val(codoficina);
					$('#desc_oficina').val(nomOfi);
					$('#method').val('doBorrar');
					$('#id').val(id);
					$('#main3').submit();
				}
			});
}

/** CONSULTAR HISTORICO * */
function consultarHistorico(id, codentidad, codoficina, entMedia, subEntMedia,
		delegacion, nomEntidad, nomOfi, pctDescMax, codplan, codlinea,
		permitirRecargo, verComisiones, nomLinea) {
	$("#main3").validate().cancelSubmit = true;
	$('#id').val(id);
	$('#entidadH').val(codentidad);
	$('#oficinaH').val(codoficina);
	$('#entmediadoraH').val(entMedia);
	$('#subentmediadoraH').val(subEntMedia);
	$('#nomEntidadH').val(nomEntidad);
	$('#nomOficinaH').val(nomOfi);
	$('#delegacionH').val(delegacion);
	$('#pctDescMaxH').val(pctDescMax);
	$('#permitirRecargoH').val(permitirRecargo);
	$('#verComisionesH').val(verComisiones);	
	$('#planH').val(codplan);
	$('#lineaH').val(codlinea);
	$('#nomlineaH').val(nomLinea);
	   
	$('#method').val("doConsultarHistorico");
	$('#main3').submit();
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

}
// Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo() {
	$('#panelAlertasValidacion').html(
			"Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}

// Lanza la consulta de fechas de contratacion
function lanzarConsulta() {
	// Llama al metodo que llama al servidor
	onInvokeAction('consultaDescuentos', 'filter');
	$('#listaIdsMarcados').val('');
	$('#marcaTodos').val('false');
	
}

function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	$.get('mtoDescuentos.run?ajax=true&' + decodeURIComponent(parameterString),
			function(data) {
				$("#grid").html(data);				
				comprobarChecks();				
			});
}
function comprobarCampos(incluirJmesa) {

	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('consultaDescuentos');
	}
	var resultado = false;

	if ($('#entidad').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaDescuentos',
					'subentidadMediadora.entidad.codentidad', $('#entidad')
							.val());
		}
		resultado = true;
	}
	if ($('#oficina').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaDescuentos',
					'oficina.id.codoficina', $('#oficina').val());
		}
		resultado = true;
	}
	if ($('#entmediadora').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaDescuentos',
					'subentidadMediadora.id.codentidad', $('#entmediadora')
							.val());
		}
		resultado = true;
	}
	if ($('#subentmediadora').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaDescuentos',
					'subentidadMediadora.id.codsubentidad', $(
							'#subentmediadora').val());
		}
		resultado = true;
	}
	if ($('#delegacion').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaDescuentos', 'delegacion',
					$('#delegacion').val());
		}
		resultado = true;
	}
	if ($('#pctDescMax').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaDescuentos', 'pctDescMax',
					$('#pctDescMax').val());
		}
		resultado = true;
	}

	if ($('#permitirRecargo').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaDescuentos',
					'permitirRecargo', $('#permitirRecargo').val());
		}
		resultado = true;
	}

	if ($('#verComisiones').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaDescuentos',
					'verComisiones', $('#verComisiones').val());
		}
		resultado = true;
	}

	if ($('#plan').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaDescuentos',
					'linea.codplan', $('#plan').val());
		}
		resultado = true;
	}

	if ($('#linea').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaDescuentos',
					'linea.codlinea', $('#linea').val());
		}
		resultado = true;
	}

	return resultado;
}

function validarConsulta() {
	// Valida el campo 'entidad' si esta informado
	if ($('#entidad').val() != '') {
		var entidadOk = false;
		try {
			var auxentidad = parseFloat($('#entidad').val());
			if (!isNaN(auxentidad)) {
				$('#entidad').val(auxentidad);
				entidadOk = true;
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!entidadOk) {
			$('#panelAlertasValidacion')
					.html("Valor para la entidad no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	// Valida el campo 'codoficina' si esta informado
	if ($('#oficina').val() != '') {

		var oficinaOk = false;
		try {
			var valor = parseFloat($('#oficina').val());
			if (!isNaN(valor)) {
				$('#oficina').val(valor);
				oficinaOk = true;
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!oficinaOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo oficina no válido");
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
				$('#entmediadora').val(valor);
				entmediadoraOk = true;
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!entmediadoraOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Entidad Mediadora no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	// Valida el campo 'codoficina' si esta informado
	if ($('#subentmediadora').val() != '') {
		var subentmediadoraOk = false;
		try {
			var valor = parseFloat($('#subentmediadora').val());
			if (!isNaN(valor)) {
				$('#subentmediadora').val(valor);
				subentmediadoraOk = true;
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!subentmediadoraOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Subentidad Mediadora no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}

	// Valida el campo 'delegacion' si esta informado
	if ($('#delegacion').val() != '') {
		var delegacionOk = false;
		try {
			var valor = parseFloat($('#delegacion').val());
			if (!isNaN(valor)) {
				$('#delegacion').val(valor);
				delegacionOk = true;
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!delegacionOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Delegación no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	// Valida el campo 'pctDescMax' si esta informado
	if ($('#pctDescMax').val() != '') {
		var pctDescMaxOk = false;
		try {
			var valor = parseFloat($('#pctDescMax').val());
			if (!isNaN(valor)) {
				$('#pctDescMax').val(valor);
				pctDescMaxOk = true;
			}
		} catch (ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if (!pctDescMaxOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Descuento Máximo no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	return true;
}


//REPLICAR DATOS DE4 PLAN-LINEA ****************************
function replicar () {	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	comprobarCampos();	
	$('#main3').validate().cancelSubmit = true;
	var errorEntidad = validarEntidadParaReplica();
	if(errorEntidad==""){
	//La función validarLineaparaReplica se encuentra en linea.js
		if(validarLineaparaReplica($('#plan').val(), $('#linea').val(), $("#panelAlertasValidacion"))){
			$('#planreplica').val('');//hay que tener estas variables creadas en la jsp (<input type="hidden" ...
			$('#lineareplica').val('');
			lupas.muestraTabla('LineaReplica','principio', '', '');}		
	}else{
		$('#panelAlertasValidacion').html(errorEntidad);
		$('#panelAlertasValidacion').show();
	}
}

function validarEntidadParaReplica(){
	var error="";
	var entidad= $('#entidad').val();
	if(entidad ==""){
		error="Debe seleccionar una entidad válida para la réplica.";
	}else if(!/^([0-9])*$/.test(entidad)){
		error="Formato de entidad erroneo. Compruebe el valor del campo";
	}else{
		if ( $('#perfil').val()== 5){
			var listaEntCRM = $('#grupoEntidades').val();
			if (listaEntCRM.indexOf(entidad) < 0) {
				error="No tiene permisos para realizar una réplica sobre esta entidad.";
			} 
		}
	}
	return error;
}





function doReplicar(){
	// Se comprueba que el plan y linea a replicar no son vacíos antes de llamar al servidor
	// Si son vacíos, se ha hecho click en un elemento de la lupa que no es un registro (ordenación, etc.)
	if ($('#planreplica').val() != '' && $('#lineareplica').val() != '') {
		if(confirm('¿Desea replicar todos los descuentos para este Plan y Línea?')){
			// Valida que el plan/linea origen y destino no son iguales
			if (replicaPlanLineaDiferentes($('#planreplica').val(),$('#plan').val(), $('#lineareplica').val(),$('#linea').val() )) {
				if($('#planreplica').val()>=2015){
					$("#entidadreplica").val( $('#entidad').val());
					$("#method").val("doReplicar");
					$("#main3").submit();
				}else{
					$('#panelAlertasValidacion').html("El plan de destino debe de ser mayor o igual a 2015");
					$('#panelAlertasValidacion').show();
				}
			}else {
				$('#panelAlertasValidacion').html("El plan/linea origen no puede ser igual que el destino");
				$('#panelAlertasValidacion').show();
			}
		}
	}
}

//**********************************************************

//CAMBIO MASIVO *****************************************************
function marcarTodos(){
	if($('#checkTodos').attr('checked')==true){
		
		var listaIdsTodos = $("#listaIdsTodos").val();
		$('#listaIdsMarcados').val(listaIdsTodos);
		$('#marcaTodos').val('true');
		comprobarChecks();
	}
	else{		
		$('#listaIdsMarcados').val('');
		$('#marcaTodos').val('false');
		$("input[type=checkbox]").each(function(){
			$(this).attr('checked',false);
		});
	}		
}

function comprobarChecks(){	
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
		$('#checkTodos').attr('checked',true);
	}else{
		$('#checkTodos').attr('checked',false);
	}
	//alert($('#listaIdsMarcados').val());
}

function listaCheckId(id){
	var listaIdsMarcados = "";
	var listaFinalIds = "";
	var cadena=[];
	
	if($('#check_' + id).attr('checked')==true){
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
	//alert($('#listaIdsMarcados').val());
}

function cambioMasivo(){
	//alert("cambioMasivo");
	//limpiarCambioMasivo();
	var listaIdsMarcados = $('#listaIdsMarcados').val();
	if(listaIdsMarcados.length>0){
		$('#listaIdsMarcados_cm').val(listaIdsMarcados);
		$('#overlayCambioMasivo').show();
		$('#panelCambioMasivo').show();
	}
	else{
		 showPopUpAviso("Debe seleccionar al menos un Descuento.");
	}	
}

function limpiarCambioMasivo(){
	//alert("limpiarCambioMasivo");
	$('#pctDescMax_cm').val('');
	$('#permitirRecargo_cm').val('');	
	$('#verComisiones_cm').val('');
	$('#txt_mensaje_cm').html("");
}


// popup aviso
//popup aviso
function showPopUpAviso(mensaje){
	$('#txt_mensaje_aviso').html(mensaje);	
	$('#panelInformacion2').show();
	$('#popUpAvisos').show();
	$('#overlayCambioMasivo').show();
}
function hidePopUpAviso(){
	$('#popUpAvisos').hide();
	$('#overlayCambioMasivo').hide();
}

//popupCambioMasivo
function cerrarCambioMasivo(){
	//alert("cerrarCambioMasivo");
	limpiarCambioMasivo();
	$('#panelCambioMasivo').hide();
	$('#overlayCambioMasivo').hide();
}


function aplicarCambioMasivo(){
	if($('#pctDescMax_cm').val() == ''&& $('#permitirRecargo_cm').val() == '' && $('#verComisiones_cm').val() == ''){
		$('#txt_mensaje_cm').html("* Debe seleccionar al menos un cambio");
	}else{
		if(validarPorcentajeMax_cm()){
			//$('#perfil_cm').val($('#perfil_cm_sel').val());
			$('#frmCambioMasivo').submit();
			cerrarCambioMasivo();
		}
	}
		
}

function validarPorcentajeMax_cm(){
	var pctDescMaxOk = false;
	var mensaje="";
	
	if($('#pctDescMax_cm').val() == ''){return true};
	
	if (validaCaracteresPorcentajeMax_cm()){
		if(validaRangoPorcentajeMaximo()){
			pctDescMaxOk=true;
		}else{
			mensaje="El campo % Descuento Máximo debe contener un número entre 0 y 100";
		}
	}else{
		mensaje="Valor para el campo Descuento Máximo no válido";
	}
	
	if(!pctDescMaxOk){
		//showPopUpAviso(mensaje);
		$('#txt_mensaje_cm').html(mensaje);
	}
	return pctDescMaxOk;
}

function validaCaracteresPorcentajeMax_cm(){
	
	var pctDescMaxOk = false;
	if ($('#pctDescMax_cm').val() != '') {
		try {			
			var valor = parseFloat($('#pctDescMax_cm').val());
			if (!isNaN(valor)) {
				$('#pctDescMax_cm').val(valor);				
				pctDescMaxOk = true;
			}
		} catch (ex) {
			//alert("validaCaracteresPorcentajeMax_cm parsefloat catch");
		}
	}else{
		pctDescMaxOk = true;
	}
	return pctDescMaxOk;
	
}	

function validaRangoPorcentajeMaximo(){
	var pctDescMaxOk = false;
	var valor = parseFloat($('#pctDescMax_cm').val());
	if(valor >0 && valor<=100){
		pctDescMaxOk=true;
	}
	return pctDescMaxOk;	
}


//*******************************************************************
	