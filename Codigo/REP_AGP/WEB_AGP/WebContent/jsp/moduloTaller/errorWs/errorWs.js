$(document).ready(function(){

	var URL = UTIL.antiCacheRand($("#main3").attr("action"));
	$("#main3").attr("action", URL);
	
    if ($('#id').val() != null && $('#id').val() != ''){
		$('#btnModificar').show();
	}

	$('#main3').validate({					
	
		onfocusout: function(element) {
			if ( ($('#method').val() == "doEditar") || ($('#method').val() == "doAlta") ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		rules: {
			"errorWs.id.catalogo":{required: true, maxlength: 1},
			"errorWs.id.coderror":{required: true, digits: true, maxlength: 3},
			"linea.codplan":{required: true, digits: true, minlength: 4},
	 		"linea.codlinea":{required: true, digits: true, comprobarLineaGenerica:[''] },
	 		"servicio":{required: true},
	 		"ocultar":{required: true},
	 		"errorWs.descripcion" :{required: true},
	 		"errorWs.errorWsTipo.codigo" :{required: true},
	 		"listaPerfiles" : {forzarPerfiles:['']},
	 		"entidad.codentidad" : {digits: true}
	 		
		},
		messages: {
			"errorWs.id.catalogo":{required: "El campo Cat\u00E1logo de Error es obligatorio", maxlength: "El campo Cat\u00E1logo de Error debe contener 1 d\u00EDgitos como m\u00E1ximo"},
			"errorWs.id.coderror":{required: "El campo Error es obligatorio", digits: "El campo Error s\u00F3lo puede contener d\u00EDgitos", maxlength: "El campo Error debe contener 3 d\u00EDgitos como m\u00E1ximo"},
			"linea.codplan":{required: "El campo Plan es obligatorio", digits: "El campo Plan s\u00F3lo puede contener d\u00EDgitos", minlength: "El campo Plan debe contener 4 d\u00EDgitos"},
	 		"linea.codlinea":{required: "El campo L\u00EDnea es obligatorio", digits: "El campo L\u00EDnea debe contener d\u00EDgitos", comprobarLineaGenerica: "El campo Entidad no debe estar informado si se ha indicado la l\u00EDnea gen\u00E9rica"}, 
	 		"servicio":{required: "El campo Servicio es obligatorio"},
	 		"ocultar":{required: "El campo Ocultar es obligatorio"},
	 		"errorWs.descripcion" : {required: "El campo Descripci\u00F3n del error es obligatorio"},
	 		"errorWs.errorWsTipo.codigo" : {required: "El campo Tipo es obligatorio"},
	 		"listaPerfiles" : {forzarPerfiles: "El campo Forzar es obligatorio"},
	 		"entidad.codentidad" : {digits: "El campo Entidad s\u00F3lo puede contener d\u00EDgitos"}
	 	}
	});
	
	jQuery.validator.addMethod("forzarPerfiles", function(value, element) {
    	return perfilObligatorio();
    });
	
	jQuery.validator.addMethod("comprobarLineaGenerica", function(value, element) {
    	return comprobarLineaGen();
    });
	
});

function perfilObligatorio() {
	var perfOblig = true;
	if($('#ocultar').val() == 'N' && $('#listaPerfiles').val() == null) {
		perfOblig = false;
	}
	return perfOblig;
}

function comprobarLineaGen() {
	var lineaGen = true;
	if($('#linea').val() == 999 && $('#entidad').val() != '') {
		lineaGen = false;
	}
	return lineaGen;
}

function consultarInicial(){
	//limpiaAlertas();
	var frm = document.getElementById('main3');
	//if (validarConsulta()){
		if (comprobarCampos(false)){
			frm.method.value = 'doConsulta';
			frm.origenLlamada.value= 'primeraBusqueda';			
			$("#main3").validate().cancelSubmit = true;
			$('#main3').submit();
		}// Si no, muestra el aviso
		else {
			avisoUnCampo ();
		}
	//}
}

function consultar(){

	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	$('#btnModificar').hide();
	
	//DAA 08/02/2013
	$('#marcaTodos').val('false');
	$('#listaIdsMarcados').val('');
	$("input[type=checkbox]").each(function(){
		$(this).attr('checked',false);
	});
	
	//Llamamos al metodo comprobarCampos para anhadir los valores al "limit"
	var alMenosUnCampo = comprobarCampos(true);
	
	if(alMenosUnCampo){
		onInvokeAction('consultaErrorWsAccion','filter');
	}else{
		avisoUnCampo();
	}
}

//Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo() {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}

function validarErrorWsAjax(){
	var existe=false;
	var idErrorWs =   $('#coderror').val();
	var idCatalogWs = $('#catalogo').val();
	
	$.ajax({
    url:          "errorWsAccion.run",
    data:         "method=doValidarErrorWsAjax&idErrorWs="+idErrorWs+"&idCatalogWs="+idCatalogWs+"&",
    async:        false,
    contentType:  "application/x-www-form-urlencoded",
    dataType:     "json",
    global:       false,
    ifModified:   false,
    processData:  true,
    cache:        false,
    error: function(objeto, quepaso, otroobj){
    	
        alert("Error al guardar los datos: " + quepaso);
    },
    success: function(datos){    	
    	
    	if (datos.mensaje =="true"){
    		existe = true;
		} else if (datos.mensaje =="false"){
			existe = false;
		}
    	else{ 
			existe = false;
    	}
    },
    type: "GET"
	});
	return existe;
}


function comprobarCampos(incluirJmesa){
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('consultaErrorWsAccion');
	}
	var resultado = false;
	
	/* Pet. 63481 ** MODIF TAM (12.05.2021) ** Inicio */
	if ($('#catalogo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaErrorWsAccion','errorWs.id.catalogo', $('#catalogo').val());
   		}
   		resultado = true;
   	}
	/* Pet. 63481 ** MODIF TAM (12.05.2021) ** Fin */
	
   	if ($('#coderror').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaErrorWsAccion','errorWs.id.coderror', $('#coderror').val());
   		}
   		resultado = true;
   	}
   	if ($('#desc_error').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaErrorWsAccion','errorWs.descripcion', $('#desc_error').val());
   		}
   		resultado = true;
   	}
   	if ($('#codigoTipo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaErrorWsAccion','errorWs.errorWsTipo.codigo', $('#codigoTipo').val());
   		}
   		resultado = true;
   	}
   	if ($('#plan').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaErrorWsAccion','linea.codplan', $('#plan').val());
   		}
   		resultado = true;
   	}
   	if ($('#linea').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaErrorWsAccion','linea.codlinea', $('#linea').val());
   		}
   		resultado = true;
   	}
   	if ($('#servicio').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaErrorWsAccion','servicio', $('#servicio').val());
   		}
   		resultado = true;
   	}
   	if ($('#ocultar').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaErrorWsAccion','ocultar', $('#ocultar').val());
   		}
   		resultado = true;
   	}
   	if ($('#entidad').val() != '') {
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaErrorWsAccion','entidad.codentidad', $('#entidad').val());
   		}
   		resultado = true;
   	}
   	
	if ($("#listaPerfiles").val() != null && $("#listaPerfiles").val() != '') {
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaErrorWsAccion','codErrorPerfiles', $("#listaPerfiles").val().toString());
   		}
   		resultado = true;
   	}
   	
	return resultado;
}
    	
function limpiar(){
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	$('#btnModificar').hide();
	
	$('#plan').val('');
	$('#desc_linea').val('');
	$('#linea').val('');
	$('#coderror').val('');
	$('#servicio').val('');
	$('#ocultar').val('');
	$('#listaPerfiles').val('');
	$('#entidad').val('');
	$('#desc_entidad').val('');
	$('#codigoTipo').val('');
	$('#desc_error').val('');
	$('#id').val('');
	
	/* Pet. 63481 ** MODIF TAM (12.05.2021) ** Inicio*/
	$('#catalogo').val('');
	
	//DAA 12/02/2013
	$('#marcaTodos').val('false');
	$('#listaIdsMarcados').val('');
	$("input[type=checkbox]").each(function(){
		$(this).attr('checked',false);
	});
	
	//jQuery.jmesa.removeAllFiltersFromLimit('consultaErrorWsAccion');
	//onInvokeAction('consultaErrorWsAccion','clear');
	$("#origenLlamada").val('menuGeneral');
	$('#main3').validate().cancelSubmit = true;
	$("#main3").submit();
}

function editar(id,catalogo, linea,desc_linea,plan,codEntidad,nomentidad,codError,servicio,ocultar,cadenaErroresPerfiles,descError,codigoTipo){
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	var frm = document.getElementById('main3');
	
	frm.target="";
	frm.id.value=id;
	
	frm.linea.value = linea;
	frm.desc_linea.value = desc_linea;
	frm.plan.value = plan;
	if(codEntidad !== 'null' && codEntidad.length !== undefined){
		frm.entidad.value = codEntidad;
		frm.desc_entidad.value = nomentidad;
	}
	/* Pet. 63481 ** MODIF TAM (14.05.2021) ** Inicio */
	frm.catalogo.value = catalogo;
	
	frm.coderror.value =codError;
	frm.servicio.value = servicio;
	frm.ocultar.value = ocultar;
	frm.desc_error.value = descError;
	frm.codigoTipo.value = codigoTipo;
	$("#listaPerfiles").val(cadenaErroresPerfiles.split(","));
	$('#btnModificar').show();
}

function modificar(){
	comprobarCampos();
	
	$("#nuevoError").val('false');
	
	//Si en coderror no hay un numero, no llamamos al ajax (ya saltaria la validacion en el submit)
	if(!isNaN($("#coderror").val())){

		var existe = validarErrorWsAjax();
		var settings = $('#main3').validate().settings;

		if(!existe){
			settings.rules['errorWs.descripcion'] = {required: true};
			settings.rules['errorWs.errorWsTipo.codigo'] = {required: true};
			$("#nuevoError").val('true');//Se tendra que dar de alta tambien el error
			
		}else{
			settings.rules['errorWs.descripcion'] = {required: false};
			settings.rules['errorWs.errorWsTipo.codigo'] = {required: false};
		}
	}
	
	$("#method").val("doEditar");
	$('#main3').submit();
}


function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
   	var frm = document.getElementById('main3');
    $.get('errorWsAccion.run?ajax=true&' + decodeURIComponent(parameterString), function(data) {
        $("#grid").html(data);
        comprobarChecks();
		});
}

function borrar(id) {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	$('#main3').validate().cancelSubmit = true;
	if(confirm('\u00BFEst\u00E1 seguro de que desea eliminar el registro seleccionado?')){
		$("#method").val("doBorrar");
		$("#id").val(id);
		$("#main3").submit();
	}	
}

function alta() {
	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	comprobarCampos();

	$("#nuevoError").val('false');
	//Si en coderror no hay un numero, no llamamos al ajax (ya saltari la validacion en el submit)
	if($("#coderror").val() != '') {

		var existe = validarErrorWsAjax();
		var settings = $('#main3').validate().settings;

		if(!existe){
			settings.rules['errorWs.descripcion'] = {required: true};
			settings.rules['errorWs.errorWsTipo.codigo'] = {required: true};
			$("#nuevoError").val('true');//Se tendra que dar de alta tambien el error
			
		}else{
			settings.rules['errorWs.descripcion'] = {required: false};
			settings.rules['errorWs.errorWsTipo.codigo'] = {required: false};
		}
	}
	
	$("#method").val("doAlta");
	$('#main3').submit();
}

function replicar() {
	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	comprobarCampos();
	$('#main3').validate().cancelSubmit = true;	
		$('#panelReplicaErrores').show();
		$('#overlayReplicaErrores').show();
}

// Devuelve un boolean indicando si los plan/linea origen y destino de la replica son diferentes
function planLineaDiferentes() {
	if ($('#planreplica').val() == $('#plan_re').val() && $('#lineareplica').val() == $('#linea_re').val() && $('#servicio_re').val() == $('#servicio_re_2').val()) return false
	else return true;
}

//Devuelve un boolean indicando si ambos servicios estan informados en caso de haber indicado alguno
function comprobacionServicios() {
	if ($('#servicio_re').val() == '' && $('#servicio_re_2').val() == '' ) {
		return true;
	}
	else {
		if($('#servicio_re').val() != '') {
			if($('#servicio_re_2').val() != '') {
				return true;
			}
			else {
				return false;
			}			
		}
		else if($('#servicio_re_2').val() != '') {
			if($('#servicio_re').val() != '') {
				return true;
			}
			else {
				return false;
			}
		}
	}
}

function doReplicar() {
	// Se comprueba que el plan y linea a replicar no son vacios antes de llamar al servidor
	// Si son vacios, se ha hecho click en un elemento de la lupa que no es un registro (ordenacion, etc.)
	if(validarLineaseguroidOrigen() && validarLineaseguroidDestino()) {
		if(comprobacionServicios()) {
			// Valida que el plan/linea origen y destino no son iguales
			if(planLineaDiferentes()) {
				if(confirm('\u00BFDesea replicar todos los errores webservice para este Plan y L\u00EDnea?')) {
					
					$("#method").val("doReplicar");
					$('#plan_orig').val($('#plan_re').val());
					$('#linea_orig').val($('#linea_re').val());
					$('#plan_dest').val($('#planreplica').val());
					$('#linea_dest').val($('#lineareplica').val());
					$('#servicio_orig').val($('#servicio_re').val());
					$('#servicio_dest').val($('#servicio_re_2').val());					
					$("#main2").submit();
					cerrarReplicaErrores();
				}
			}
			else {
				$('#txt_mensaje_re').html("El plan/l\u00EDnea y servicio origen no puede ser igual que el destino");
			}
		}
		else {
			$('#txt_mensaje_re').html("Ambos servicios deben estar indicados");
		}
	}
}

function validarLineaseguroidOrigen() {
	// Valida el campo 'Plan' si esta informado
	if ($('#plan_re').val() != '') {
		var planOk = false;
		try {
			var auxPlan =  parseFloat($('#plan_re').val());
			if(!isNaN(auxPlan) && $('#plan_re').val().length == 4 && auxPlan > 0) {
				$('#plan_re').val(auxPlan);
				planOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!planOk) {
			$('#txt_mensaje_re').html("Valor para el plan origen no v\u00E1lido");
			return false;
		}
	}
	else{
		$('#txt_mensaje_re').html("Debe introducir un plan origen");
		return false;
	}
	
	// Valida el campo 'Linea' si esta informado
	if ($('#linea_re').val() != '') {
		var lineaOk = false;
		try {
			var auxLinea =  parseFloat($('#linea_re').val());
			if(!isNaN(auxLinea) && auxLinea > 0) {
				$('#linea_re').val(auxLinea);
				lineaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!lineaOk) {
			$('#txt_mensaje_re').html("Valor para la l\u00EDnea origen no v\u00E1lido");
			//$('#panelAlertasValidacion').show();
			return false;
		}
	}
	else{
		$('#txt_mensaje_re').html("Debe seleccionar una l\u00EDnea origen");
		return false;
	}	
	return true;
	
}

function validarLineaseguroidDestino() {
	// Valida el campo 'Plan' si esta informado
	if ($('#planreplica').val() != '') {
		var planOk = false;
		try {
			var auxPlan =  parseFloat($('#planreplica').val());
			if(!isNaN(auxPlan) && $('#planreplica').val().length == 4 && auxPlan > 0) {
				$('#planreplica').val(auxPlan);
				planOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!planOk) {
			$('#txt_mensaje_re').html("Valor para el plan destino no v\u00E1lido");
			return false;
		}
	}
	else{
		$('#txt_mensaje_re').html("Debe introducir un plan destino");
		return false;
	}
	
	// Valida el campo 'Linea' si esta informado
	if ($('#lineareplica').val() != '') {
		var lineaOk = false;
		try {
			var auxLinea =  parseFloat($('#lineareplica').val());
			if(!isNaN(auxLinea) && auxLinea > 0) {
				$('#lineareplica').val(auxLinea);
				lineaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!lineaOk) {
			$('#txt_mensaje_re').html("Valor para la l\u00EDnea destino no v\u00E1lido");
			//$('#panelAlertasValidacion').show();
			return false;
		}
	}
	else{
		$('#txt_mensaje_re').html("Debe seleccionar una l\u00EDnea destino");
		return false;
	}	
	return true;
}

// DAA 12/02/2013 listaCheckId, comprobarChecks, marcarTodos, cambioMasivo

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
}	

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

function comprobarBotonOcultar() {
	if( $('#ocultar_cm').val() == 'S' ) {
		$('#listaPerfiles_cm').attr('disabled', true);
	}
	else {
		$('#listaPerfiles_cm').attr('disabled', false);
	}
}

function cambioMasivo(){
	limpiarCambioMasivoErrorWs();
	var listaIdsMarcados = $('#listaIdsMarcados').val();
	if(listaIdsMarcados.length>0){
		$('#listaIdsMarcados_cm').val(listaIdsMarcados);
		$('#overlayCambioMasivo').show();
		$('#panelCambioMasivoErrorWs').show();
	}
	else{
		 showPopUpAviso("Debe seleccionar como m\u00EDnimo un Error.");
	}	
}

// popup aviso
function showPopUpAviso(mensaje){
	$('#txt_mensaje_aviso').html(mensaje);
	
	$('#panelInformacion').show();
	$('#popUpAvisos').show();
	$('#overlayCambioMasivo').show();
}
 function hidePopUpAviso(){
	$('#popUpAvisos').hide();
	$('#overlayCambioMasivo').hide();
}

//popupCambioMasivo
function cerrarCambioMasivoErrorWs(){
	limpiarCambioMasivoErrorWs();
	$('#panelCambioMasivoErrorWs').hide();
	$('#overlayCambioMasivo').hide();
}

//popupReplicaErrores
function cerrarReplicaErrores() {
	limpiarReplicaErrores();
	$('#panelReplicaErrores').hide();
	$('#overlayReplicaErrores').hide();
}

function limpiarCambioMasivoErrorWs() {
	$('#ocultar_cm').val('');
	$('#listaPerfiles_cm').val('');
	$('#listaPerfiles_cm').attr('disabled', false);
	$('#txt_mensaje_cm').html("");
}

function limpiarReplicaErrores() {
	$('#plan_re').val('');
	$('#linea_re').val('');
	$('#desc_linea_re').val('');
	$('#servicio_re').val('');
	$('#planreplica').val('');
	$('#lineareplica').val('');
	$('#desc_lineareplica').val('');
	$('#servicio_re_2').val('');
	
	$('#txt_mensaje_re').html("");
}

function aplicarCambioMasivoErrorWs() {
	//&& $('#perfil_cm_sel').val() == '-1'
	if($('#ocultar_cm').val() == '') {
		$('#txt_mensaje_cm').html("* Debe seleccionar alg\u00FAn elemento del campo ocultar");
	}else{
		//$('#perfil_cm').val($('#perfil_cm_sel').val());
		if($('#ocultar_cm').val() == 'N' && $('#listaPerfiles_cm').val() == null) {
			$('#txt_mensaje_cm').html("* Debe seleccionar alg\u00FAn perfil");
		}
		else {
			$('#main').submit();
			cerrarCambioMasivoErrorWs();
		}
	}
}

function aplicarReplicaErrores() {
	doReplicar();	
}