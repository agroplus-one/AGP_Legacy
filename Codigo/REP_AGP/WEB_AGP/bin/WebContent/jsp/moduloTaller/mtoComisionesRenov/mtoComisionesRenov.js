$(document).ready(function(){

	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
  
    if($('#origenLlamada').val()== 'doModificar'){
		$("#btnAlta").show();
		$("#btnModificar").show();
	}else{
		$("#btnAlta").show();
		$("#btnModificar").hide();
	}
    if ($('#btnModificar').is(":visible") ){
    	$('#codplan').attr('readonly', true);
		$('#codlinea').attr('readonly', true);
	}

	$('#main3').validate({					
	
		onfocusout: function(element) {
			var frm = document.getElementById('main3');
			if ( (frm.method.value == "doModificar") || (frm.method.value == "doAlta") ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		
		rules: {
			
			"codplan":{required: true,digits: true},
			"codlinea":{required: true,digits: true},
			"codentidad":{digits: true, rangelength : [ 3, 4 ], comprobarEntidad: ['entidad', 'entmediadora', 'subentmediadora'], grupoEnt : "La Entidad seleccionada no pertenece al grupo de Entidades del usuario"},
			"codentmed":{digits: true, minlength : 3, comprobarEntidad:['entidad', 'entmediadora', 'entmediadora']},
			"codsubmed":{digits: true, comprobarEntidad:['entmediadora', 'entidad', 'subentmediadora'] },
			"impDesde":{required: true,number: true, digits:true, range: [0, 99999999999], comprobarImportes: ['impDesde', 'impHasta']},
			"impHasta":{required: true,number: true, digits:true, range: [1, 99999999999], comprobarImportes: ['impDesde', 'impHasta']},
			"comision":{number: true, required : true, range : [ 0, 100 ]},
			"idgrupo":{required: true},
			"codmodulo":{required: true},
			"refimporte":{required: true}
		},
		 messages: {
			 "codentidad" : {
					digits : "El campo Entidad solo puede contener d&iacute;gitos",
					rangelength : "El campo Entidad debe contener entre 3 y 4 d&oacute;gitos",
					comprobarEntidad: "La combinaci&oacute;n de los campos Entidad, Entidad Mediador y Subentidad Mediadora es obligatoria", 
					grupoEnt : "La Entidad seleccionada no pertenece al grupo de Entidades del usuario"
				},
				"codplan" : {
					required : "El campo Plan es obligatorio",
					digits : "El campo Plan solo puede contener d&iacute;gitos",
					minlength : "El campo Plan debe contener 4 d&iacute;gitos"
				},
				"codlinea" : {
					required : "El campo L&iacute;nea es obligatorio",
					digits : "El campo L&iacute;nea debe contener d&iacute;gitos"
				},
				"codentmed" : {
					digits : "El campo Entidad mediadora solo puede contener d&iacute;gitos",
					minlength : "El campo Entidad mediadora debe contener 3 d&iacute;gitos",
					comprobarEntidad: "La combinaci&oacute;n de los campos Entidad, Entidad Mediador y Subentidad Mediadora es obligaria"
				},
				"codsubmed" : {
					required : "La combinaci&oacute;n de los campos Entidad, Entidad Mediador y Subentidad Mediadora es obligatoria",
					digits : "El campo Subentidad mediadora solo puede contener d&iacute;gitos",
					comprobarEntidad: "La combinaci&oacute;n de los campos Entidad, Entidad Mediador y Subentidad Mediadora es obligatoria"
				},
				"impDesde":{
					required : "El campo Valor desde es obligatorio",
					number:"El campo Valor Desde solo puede contener d&iacute;gitos", 
					digits:"El campo Valor desde debe contener como m&aacute;ximo 15 d&iacute;gitos de parte entera ",
					range: "El campo Importe M&iacute;nimo solo puede contener d&iacute;gitos entre 0 y 99999999999",
					comprobarImportes: "El campo Valor Desde debe ser menor o igual al campo Valor Hasta"
		 		},
		 		"impHasta":{
		 			required : "El campo Valor hasta es obligatorio",
					number:"El campo Valor hasta solo puede contener d&iacute;gitos", 
					digits:"El campo Valor hasta debe contener como m&aacute;ximo 15 d&iacute;gitos de parte entera ",
					range: "El campo Valor hasta  solo puede contener d&iacute;gitos entre 0 y 99999999999",
					comprobarImportes: "El campo Valor Desde debe ser menor o igual al campo Valor Hasta"
				},
				"comision" : {
					required : "El campo % comisi&oacute;n es obligatorio",
					range : "El campo % comisi&oacute;n debe contener un n&uacute;mero entre 1 y 100",
					digits: "El campo % comisi&oacute;n solo puede contener d&iacute;gitos"
				},
				"idgrupo" : {
					required : "El campo Grupo de Negocio es Obligatorio"
				},
				"codmodulo" : {
					required : "El campo M&oacute;dulo es Obligatorio"
				},
				"refimporte" : {
					required : "El campo Importe de Referencia es obligatorio"
				}		
	 	
		 }
	});
	jQuery.validator.addMethod("VALDECIMAL92", function(value, element) {
    	if(value.length == 0)
    		return true;
    	else
    		return /^\d{0,9}([.]\d{1,2})?$/.test(value);
    });
	
		
	jQuery.validator.addMethod("comprobarImportes", function(value, element, params) {
		return (this.optional(element) || precioMaximoMayor(document.getElementById(params[0]), document.getElementById(params[1]) ));
	});
	jQuery.validator.addMethod("comprobarEntidad", function(value, element, params) {
		return (this.optional(element) || ValidarEntidades(document.getElementById(params[0]), document.getElementById(params[1]), document.getElementById(params[2]) ));
	});
	jQuery.validator.addMethod("grupoEnt", function(value, element, params) {
		var codentidad = $('#entidad').val();
		var encontrado = false;
		if ($('#grupoEntidades').val() == "") {
			return true;
		} else if (codentidad != "") {
			var grupoEntidades = $('#grupoEntidades').val().split(',');
			for ( var i = 0; i < grupoEntidades.length; i++) {
				if (grupoEntidades[i] == codentidad) {
					encontrado = true;
					break;
				}
			}
		} else
			return true;
		return encontrado;
	});


		
});

function precioMaximoMayor(importeMinimo, importeMaximo) {
	var res = false;
	var min = importeMinimo.value;
	var max = importeMaximo.value;
		if(min.length > 0 && max.length > 0) {
				if (parseFloat(max) >= parseFloat(min)) {
			    	 res = true;
			    }
				else {
					res = false;
				}
		}
		else {
			res = true;
		}
	return res;
}

/* Hay que validar, que si uno de ellos esta informado, los otros dos tambien */
function ValidarEntidades(Entidad, EntMediadora, SubEntMediadora) {
	
	var res = false;
	var codEntidad = Entidad.value;
	var codEntMed = EntMediadora.value;
	var codEntSubMed = SubEntMediadora.value;
	
	// Si estan sin informar los 3 no pasa nada, es correcto 
	if (codEntidad == "" && codEntMed == "" && codEntSubMed == ""){
		res = true;
	}else{
		if (codEntidad == "" || codEntMed == "" || codEntSubMed == ""){
			res = false;
		}else{
			if (codEntidad != "" && codEntMed != "" && codEntSubMed != ""){
				res = true;
			}
		}
	}
	return res;
}	


/* ALTA DE UN NUEVO REGISTRO */
function alta() {
	limpiaAlertas();
	var frm = document.getElementById('main3');
	if ($("#main3").valid()){
		frm.method.value= "doAlta";
		$("#main3").submit();
	}
}   

/* MODIFICACION DE UN REGISTRO */
function modificar(id, codPlan, codLinea, desc_linea, entidad, nombEntidad, entMed, subEntMed, idGrupo, codModulo, refImporte, impDesde, impHasta, comision) {
	limpiaAlertas();
	
	var frm = document.getElementById('main3');
	
	/* Actualizamos los datos de la cabecera con los datos de la l&iacute;nea editada  */
	$("#id").val(id);
	$("#desc_entidad").val(nombEntidad);
	$("#desc_linea").val(desc_linea);
	$("#plan").val(codPlan);
	$("#linea").val(codLinea);
	$("#entidad").val(entidad);
	$("#entmediadora").val(entMed);
	$("#subentmediadora").val(subEntMed);
	$("#idgrupo").val(idGrupo);
	$("#codmodulo").val(codModulo);
	$("#refimporte").val(refImporte);
	$("#impDesde").val(impDesde);
	$("#impHasta").val(impHasta);
	$("#comision").val(comision);
	
	/* Guardamos los datos iniciales que editamos para luego comparar si se ha producido algun cambio */
	$("#idModif").val(id);
	$("#descEntidadModif").val(nombEntidad);
	$("#descLineaModif").val(desc_linea);
	$("#codPlanModif").val(codPlan);
	$("#codLineaModif").val(codLinea);
	$("#EntidadModif").val(entidad);
	$("#entMedModif").val(entMed);
	$("#subEntMedModif").val(subEntMed);
	$("#idGrupoModif").val(idGrupo);
	$("#codModuloModif").val(codModulo);
	$("#refImporteModif").val(refImporte);
	$("#impDesdeModif").val(impDesde);
	$("#impHastaModif").val(impHasta);
	$("#comisionModif").val(comision);
	
	/* mostramos botones */
	$("#btnConsultar").show();
	$("#btnModificar").show();
	$("#btnAlta").show();
}

function guardarModificaciones() {

	limpiaAlertas();
	$("#panelInformacion").hide();
	
	var frm = document.getElementById('main3');
	var submit = false;
	
	/* Comprobamos si se han realizado cambios */
	res = comprobarCambios();
	
	if (res == false){
		if(confirm('No se han detectado modificaciones \u00BFDesea continuar?')){
			if ($("#main3").valid()) {
				frm.method.value= "doModificar";
				$('#main3').attr('target', '');
				$('#main3').submit();
			}
		}
	}else{
		if ($("#main3").valid()) {
			if(confirm('\u00BFDesea continuar con la modificaci\u00F3n del registro?')){
				frm.method.value= "doModificar";
				$('#main3').attr('target', '');
				$('#main3').submit();
			}
		}
	}
	
}

function comprobarCambios(){
	var cambiosOK = false;
	
	var frm = document.getElementById('main3');
	
	if (frm.plan.value != document.getElementById('codPlanModif').value || 
		 frm.linea.value != document.getElementById('codLineaModif').value ||
		  frm.entmediadora.value != document.getElementById('entMedModif').value ||
		   frm.subentmediadora.value != document.getElementById('subEntMedModif').value ||
		    frm.idgrupo.value != document.getElementById('idGrupoModif').value ||
		     frm.codmodulo.value != document.getElementById('codModuloModif').value ||
  		      frm.entidad.value != document.getElementById('EntidadModif').value ||
		       frm.refimporte.value != document.getElementById('refImporteModif').value ||
		        frm.impDesde.value != document.getElementById('impDesdeModif').value ||
		         frm.impHasta.value != document.getElementById('impHastaModif').value ||
		          frm.comision.value != document.getElementById('comisionModif').value){
		cambiosOK = true;
	}else{
		cambiosOK = false;
	}
		
	return cambiosOK;
	
}

/* BORRADO DE REGISTRO */
function borrar(id, codPlan, codLinea, entidad, entMed, subEntMed, idGrupo, codModulo, refImporte, impDesde, impHasta) {
	
	limpiaAlertas();
	
	if(confirm('\u00BFEst\u00E1 seguro de que desea eliminar el registro seleccionado?')){
		$("#methodBorrar").val("doBorrar");
		$("#idBorrar").val(id);
		$("#codPlanB").val(codPlan);
		$("#codLineaB").val(codLinea);
		$("#entidadB").val(entidad);
		
		$("#entMedB").val(entMed);
		$("#subEntMedB").val(subEntMed);
		$("#idGrupoB").val(idGrupo);
		$("#codModuloB").val(codModulo);
		
		$("#refImporteB").val(refImporte);
		$("#impDesdeB").val(impDesde);
		$("#impHastaB").val(impHasta);		
		
		$("#codPlanBorrar").val($('#plan').val());
		$("#codLineaBorrar").val($('#linea').val());
		$("#entidadBorrar").val($('#entidad').val());
		$("#entMedBorrar").val($('#entmediadora').val());
		$("#subEntMedBorrar").val($('#subentmediadora').val());
		$("#idGrupoBorrar").val($('#idgrupo').val());
		$("#codModuloBorrar").val($('#codmodulo').val());
		$("#refImporteBorrar").val($('#refimporte').val());
		$("#impDesdeBorrar").val($('#impDesde').val());
		$("#impHastaBorrar").val($('#impHasta').val());
		$("#comisionBorrar").val($('#comision').val());
		
		$("#frmBorrar").submit();
	}	
}

function consultar(){
	
	limpiaAlertas(); 
	if (validarConsulta()){
		if (comprobarCampos(true)){
			$("#btnModificar").hide();
			lanzarConsulta ();
		}// Si no, muestra el aviso
		else {
			avisoUnCampo ();
		}
	}
}

//Lanza la consulta de fechas de contratacion
function lanzarConsulta() {
	// Llama al metodo que llama al servidor
	onInvokeAction('consultaComisionesRenov', 'filter');
}


//Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo () {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}
function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	//$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');	
	$("#panelAlertas").html('');
				 			
	}

function comprobarCampos(incluirJmesa){
	
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('consultaComisionesRenov');
	}
   	var resultado = false;
   	
   	/* PLAN */
   	if ($('#plan').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','codplan', $('#plan').val());
   		}
   		resultado = true;
   	}
   	/* LINEA */
   	if ($('#linea').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','codlinea', $('#linea').val());
   		}
   		resultado = true;
   	}
   	/* ENTIDAD */
   	if ($('#entidad').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','codentidad', $('#entidad').val());
   		}
   		resultado = true;
   	}
   	/* ENTIDAD MEDIADORA */
   	if ($('#entmediadora').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','codentmed', $('#entmediadora').val());
   		}
   		resultado = true;
   	}
   	/* SUBENTIDAD MEDIADORA */
   	if ($('#subentmediadora').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','codsubmed', $('#subentmediadora').val());
   		}
   		resultado = true;
   	}
   	/* GRUPO NEGOCIO */
   	if ($('#idgrupo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','idgrupo', $('#idgrupo').val());
   		}
   		resultado = true;
   	}
   	/* MODULO */
   	if ($('#codmodulo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','codmodulo', $('#codmodulo').val());
   		}
   		resultado = true;
   	}
   	/* REFERENCIA IMPORTE */
   	if ($('#refimporte').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','refimporte', $('#refimporte').val());
   		}
   		resultado = true;
   	}
   	/* IMPORTE DESDE */
   	if ($('#impDesde').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','impDesde', $('#impDesde').val());
   		}
   		resultado = true;
   	}

   	/* IMPORTE HASTA */
   	if ($('#impHasta').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','impHasta', $('#impHasta').val());
   		}
   		resultado = true;
   	}
   	
   	/* COMISION */
   	if ($('#comision').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaComisionesRenov','comision', $('#comision').val());
   		}
   		resultado = true;
   	}
   	
	return resultado;
	
}
 
//Limpia los valores introducidos y vuelve a la pantalla inicial
function limpiar () {
	limpiaAlertas();
	$("#panelInformacion").hide();
	$('#limpiar').submit();
}

function editar(plan,Linea){

	limpiaAlertas();
	
	var frm = document.getElementById('main3');
	
	frm.target="";
	frm.codplan.value = plan;
	frm.codlinea.value = Linea;
	$('#codplan').attr('readonly', true);
	$('#codlinea').attr('readonly', true);
	$('#nomblinea').attr('readonly', true);
	
	$('#btnModificar').show();
}


function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
   	var frm = document.getElementById('main3');
    $.get('mtoComisionesRenov.run?ajax=true&' + decodeURIComponent(parameterString), function(data) {
        $("#grid").html(data);
		});
}

/* Modif (07.03.2019) */
function replicar_old () {	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	comprobarCampos();	
	$('#main3').validate().cancelSubmit = true;
	//La funcion validarLineaparaReplica se encuentra en linea.js
	if(validarPlanLineaparaReplica($('#plan').val(), $('#linea').val(), $("#panelAlertasValidacion"))){
		$('#planreplica').val('');//hay que tener estas variables creadas en la jsp (<input type="hidden" ...
		$('#lineareplica').val('');
		lupas.muestraTabla('LineaReplica','principio', '', '');	
	}	
}

function validarPlanLineaparaReplica(codPlan, codLinea, panelAlertasValidacion){
	// Valida el campo 'Plan' si esta informado
	if (codPlan != ''){
		var planOk = false;
		try {
			var auxPlan =  parseFloat(codPlan);
			if(!isNaN(auxPlan) && codPlan.length == 4 && auxPlan > 0){
				codPlan=auxPlan;
				planOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!planOk) {			
			panelAlertasValidacion.html("Valor para el plan no v&aacute;lido");
			panelAlertasValidacion.show();
			return false;
		}
	}
	else{
		panelAlertasValidacion.html("Debe seleccionar un Plan v&aacute;lido para la r&eacute;plica.");
		panelAlertasValidacion.show();
		return false;
	}
	
	// Valida el campo 'Linea' si esta informado
	if (codLinea != ''){
		var lineaOk = false;
		try {
			var auxLinea =  parseFloat(codLinea);
			if(!isNaN(auxLinea) && auxLinea > 0){
				codLinea=auxLinea;
				lineaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!lineaOk) {
			panelAlertasValidacion.html("Valor para la l&iacute;nea no v&aacute;lido");
			panelAlertasValidacion.show();
			return false;
		}
	}
	else{
		panelAlertasValidacion.html("Debe seleccionar una L&iacute;nea v&aacute;lida para la r&eacute;plica.");
		panelAlertasValidacion.show();
		return false;
	}	
	return true;
	
}

function doReplicar(){
	// Se comprueba que el plan y linea a replicar no son vacios antes de llamar al servidor
	// Si son vacios, se ha hecho click en un elemento de la lupa que no es un registro (ordenacion, etc.)
	if ($('#planreplica').val() != '' && $('#lineareplica').val() != '' && $('#linea_re').val() != '' && $('#plan_re').val() != '') {

		if(confirm('\u00BFDesea replicar todas las Comisiones para este Plan y L\u00EDnea?')){
			// Valida que el plan/linea origen y destino no son iguales
			if (replicaPlanLineaDiferentes($('#planreplica').val(),$('#plan_re').val(), $('#lineareplica').val(),$('#linea_re').val() )) {
				
				var frm = document.getElementById('main3');
				
				$('#plan_orig').val($('#plan_re').val());	
				$('#linea_orig').val($('#linea_re').val());
				$('#plan_dest').val($('#planreplica').val());
				$('#linea_dest').val($('#lineareplica').val());
				
				frm.method.value= "doReplicar";
				
				$("#main3").submit();
			}else {
				$('#panelAlertasValidacion').html("El plan/linea origen no puede ser igual que el destino para la Replica");
				$('#panelAlertasValidacion').show();
				// cerramos la ventana de Replica
				$('#panelReplicaComisRenov').hide();
				$('#overlayReplicaComisRenov').hide();
			}
		}
	}else{
		$('#panelAlertasValidacion').html("Debe Insertar valores en Plan/Linea Destino y Plan/Linea Origen para la Replica.");
		$('#panelAlertasValidacion').show();
		// cerramos la ventana de Replica
		$('#panelReplicaComisRenov').hide();
		$('#overlayReplicaComisRenov').hide();
	}
}

function replicaPlanLineaDiferentes (planReplica, plan, lineaReplica, linea) {	
	if (planReplica == plan && lineaReplica == linea) return false
	else return true;
}
/* Modif (07.03.2019) */

/* Pruebas Tatiana (12.04.2019) */
function replicar() {
	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	comprobarCampos();
	$('#main3').validate().cancelSubmit = true;	
	$("#panelInformacion").show();
	$('#panelReplicaComisRenov').show();
	$('#overlayReplicaComisRenov').show();
}

function cerrarReplicaComisRenov() {
	limpiarReplicaComisRenov();
	$('#panelReplicaComisRenov').hide();
	$('#overlayReplicaComisRenov').hide();
}

function limpiarReplicaComisRenov() {
	$('#plan_re').val('');
	$('#linea_re').val('');
	$('#desc_linea_re').val('');
	$('#planreplica').val('');
	$('#lineareplica').val('');
	$('#desc_lineareplica').val('');
	
	$('#txt_mensaje_re').html("");
}

function aplicarReplicaComisRenov() {
	doReplicar();	
}
/* Fin Pruebas Tatiana (12.04.2019) */
 
function validarConsulta() {
	
	// Valida el campo 'entidad' si esta informado
	if ($('#plan').val() != ''){ 
		var entidadOk = false;
		try {
			var auxplan =  parseFloat($('#plan').val());
			if(!isNaN(auxplan)){
				$("#plan").val(auxplan);
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
	// Valida el campo 'codLinea' si esta informado		 	
 	if ($('#linea').val() != ''){ 
	 	var lineaOk = false;
	 	try {		 	
	 		var valor =  parseFloat($('#linea').val());
	 		if(!isNaN(valor)){
				$('#linea').val(valor);
				lineaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para el campo Linea no v&aacute;lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	return true;
}


function onInvokeExportAction(id) { 
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'mtoComisionesRenov.run?ajax=false&export=true&' + parameterString;
}
