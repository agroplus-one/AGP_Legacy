$(document).ready(function(){
	if ($('#linea').val() != ''){
		$('#desc_linea').val($('#nomLinea').val());
	}
	
	$('#desc_entidad').val($('#nomEntidad').val());
	$('#entidad').val($('#codEntidad').val());
	$('#oficina').val($('#codOficina').val());
	/* Pet.63473 ** MODIF TAM (20.12.2021) */ 
	$('#entmediadora').val($('#entMediadora').val());
	$('#subentmediadora').val($('#subEntmediadora').val());
	$('#delegacion').val($('#deleg').val());
	
	$('#desc_oficina').val($('#nomOficina').val());
	$('#plan').val($('#filtroPlan').val());
	$('#codusuario').val($('#filtroUsuario').val());
	// Para evitar el cacheo de peticiones al servidor
    var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
    var URL = UTIL.antiCacheRand(document.getElementById("seleccionForm").action);
    document.getElementById("seleccionForm").action = URL;
    var URL = UTIL.antiCacheRand(document.getElementById("utilidadesForm").action);
    document.getElementById("utilidadesForm").action = URL;
    var URL = UTIL.antiCacheRand(document.getElementById("informesForm").action);
    document.getElementById("informesForm").action = URL;
    var URL = UTIL.antiCacheRand(document.getElementById("simulacionForm").action);
    document.getElementById("simulacionForm").action = URL;
   
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
        inputField        : "fecEnvioId",
        button            : "btn_fechaenvio",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
	
	$('#main3').validate({					
			
		errorLabelContainer: "#panelAlertasValidacion",
				wrapper: "li",
		
		rules: {
		
		 	"oficina": {digits: true}
		},
		messages: {
		
		 	"oficina": { digits: "El campo oficina debe contener s�lo n�meros"}
		}
	});
		
	showOrHideExportIcon();
		
});

function validarConsulta(){
		
	if ($('#referencia').val() != ''){
	 	var referenciaOk = false;
	 	try {		 	
	 		var auxReferencia =  $('#referencia').val();
	 		var ref = auxReferencia.substr(0, 1);
	 		if(ref.match(/[a-zA-Z]/)){
				referenciaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!referenciaOk) {
			$('#panelAlertasValidacion').html("Valor para la P�liza no es v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}

	if ($('#entidad').val() != ''){
	 	var EntidadOk = false;
	 	try {		 	
	 		var auxEntidad =  parseFloat($('#entidad').val());
	 		if(!isNaN(auxEntidad)){
				$('#entidad').val(auxEntidad);
				EntidadOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!EntidadOk) {
			$('#panelAlertasValidacion').html("Valor para la Entidad no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	if ($('#oficina').val() != ''){
	 	var OficinaOk = false;
	 	try {		 	
	 		var auxOficina =  parseFloat($('#oficina').val());
	 		if(!isNaN(auxOficina)){
				$('#oficina').val(auxOficina);
				
				OficinaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!OficinaOk) {
			$('#panelAlertasValidacion').html("Valor para la Oficina no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
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
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("Valor para el plan no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
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
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para la l�nea no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	if ($('#clase').val() != ''){ 
	 	var claseOk = false;
	 	try {		 	
	 		var auxClase =  parseFloat($('#clase').val());
	 		if(!isNaN(auxClase)){
				$('#clase').val(auxClase);
				claseOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!claseOk) {
			$('#panelAlertasValidacion').html("Valor para la clase no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	if ($('#refPlzOmega').val() != ''){
	 	var EntidadOk = false;
	 	try {		 	
	 		var auxRefOmega =  parseFloat($('#refPlzOmega').val());
	 		if(!isNaN(auxRefOmega)){
				$('#refPlzOmega').val(auxRefOmega);
				EntidadOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!EntidadOk) {
			$('#panelAlertasValidacion').html("Valor para la referencia Omega no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	//DNF 29/11/2018
	if ($('#nSolicitud').val() != ''){
	 	var solicitudOk = false;
	 	try {		 	
	 		var auxNSol =  parseFloat($('#nSolicitud').val());
	 		if(!isNaN(auxNSol)){
				$('#nSolicitud').val(auxNSol);
				solicitudOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!solicitudOk) {
			$('#panelAlertasValidacion').html("Valor para el n�mero de solicitud no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	return true;
}

function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main3');
    // paso el valor filtrarDetalle desde ajax para su posterior filtrado en el combo detalle
    $.get('consultaPolizaSbp.run?ajax=true&filtrarDetalle='+frm.detalle.value+'&' + parameterString, function(data) {
        $("#tablaPolizasSbp").html(data)
        showOrHideExportIcon();
		});
}

function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'consultaPolizaSbp.run?ajax=false&excel=true&' + parameterString;
}

function validarGrupoEntidad(){
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
	}else
		return true;
	return 	encontrado;	
}

function editar(idPolSbp, idPolPpal, idPolCpl, estadoPpal, estadoCpl, incSbpComp,estadoSbp,idTipoEnvio){
	var frm = document.getElementById('simulacionForm');
		frm.method.value = 'doEditar';		
		frm.origenLlamada.value = 'edicionlistadoPolizasSbp';	
		frm.idPolSbp.value = idPolSbp;
		frm.idPolizaPpal.value = idPolPpal;
		frm.idPolizaCpl.value = idPolCpl;
	if (estadoSbp == 1){ // estado grabada provisional  
		$("#simulacionForm").submit(); // forzamos la llamada al servidor independientemente de la complementaria
	}else { //cualquier otro estado
		if(confirm('La p�liza de Sobreprecio pasar� a estado Provisional, �Desea Continuar?')){
			
			if (idTipoEnvio != 2){ //si es 2 es un suplemento y no hay que preguntar por la cpl
				$("#simulacionForm").submit(); // forzamos la llamada al servidor independientemente de la complementaria
			}else{
				$("#simulacionForm").submit();
			}
			
		}
	}
}

function borrar(id) {
	if(confirm('�Est� seguro de que desea eliminar la p�liza de Sobreprecio seleccionada?')){
		var frm = document.getElementById('simulacionForm');
		frm.method.value = 'doBaja';
		frm.idPolSbp.value = id;
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#simulacionForm").submit();	
	}	
}

function imprimir(id){
	var frm = document.getElementById('informesForm');
	frm.idPolizaSbp.value = id;
	frm.method.value = 'doInformePolizaSbp';
	$('#informesForm').attr('target', '_blank');
	$("#informesForm").submit();
}

function pasarADefinitiva(id,estadoPpal,estadoCpl,incSbpComp){
	if (estadoPpal == 8 || estadoPpal == 5 || estadoPpal == 3){
		if (estadoPpal == 8 && (estadoCpl == 1 || estadoCpl == 2) && incSbpComp == 'S'){
			if (estadoCpl == 1){
			 		var strEstadoCpl = "pendiente de Validaci�n";
			 	}else if (estadoCpl == 2){
			 		var strEstadoCpl = "Grabaci�n Provisional";
			 	}
			if(confirm('P�liza Complementaria con estado '+ strEstadoCpl + '. No es posible pasar a Grabaci�n Definitiva,\n\ �desea recalcular el Sobreprecio sin Complementaria?')){
				// Rec�lculo Sbp sin Cpl
				var frm = document.getElementById('simulacionForm');
				frm.method.value = 'doGrabacionDefinitivaSbpSinCpl';
				frm.idPolSbp.value = id;
				$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		    	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				frm.submit();
			}
		}else{ // pasar Sbp a definitiva
			var frm = document.getElementById('simulacionForm');
			frm.method.value = 'doGrabacionDefinitiva';
			frm.origenLlamada.value = 'listadoPolizasSbp';
			frm.idPolSbp.value = id;
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		    $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			frm.submit();
		}
	}else{ //estadoPpal != 8
		limpiaAlertas();
		$('#panelAlertasValidacion').html("No se puede pasar a Definitiva la p�liza de Sobreprecio hasta que su principal est� en Definitiva, Enviada pendiente de confirmaci�n o Enviada Correcta");
		$('#panelAlertasValidacion').show();
	}	
}

 
 
function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
}
 
 
 function consultar(){
 	var inputText2 =  parseFloat($('#oficina').val());
 	var inputText = document.getElementById('oficina');
 	if (!isNaN(inputText2) && inputText.value != ""){
 		while (inputText.value.length<4){
			inputText.value = '0'+inputText.value;
		}
	}
 	
 	/* Pet. 63473 ** MODIF TAM (21/12/2021) ** Inicio */
	var entMed = document.getElementById('entmediadora').value;
	$('#entMediadora').val(entMed);
	
	var subEntMed = document.getElementById('subentmediadora').value;
	$('#subEntmediadora').val(subEntMed);
	
	var deleg = document.getElementById('delegacion').value;
	$('#deleg').val(deleg);
	/* Pet. 63473 ** MODIF TAM (21/12/2021) ** Fin */
	
	limpiaAlertas();
	
	var validacion = validarGrupoEntidad();
	var valOfi=validarOficina();
	if (!validacion){
		$('#panelAlertasValidacion').html("La Entidad seleccionada no pertenece al grupo de Entidades del usuario");
		$('#panelAlertasValidacion').show();
	}else if(!valOfi){
		$('#panelAlertasValidacion').html("La Oficina seleccionada no pertenece al grupo de Oficinas del usuario");
		$('#panelAlertasValidacion').show();
	}else{
		if (validarConsulta()){
			if (!comprobarCampos(true)){
				$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
				$('#panelAlertasValidacion').show();
			} else {
				if ($('#linea').val() != '' || $('#plan').val() != ''){ //verificar si el Plan/linea permite Sbp
					verificarLineaSbp();
				}else{ 
					onInvokeAction('listadoPolizasSbp','filter');
				}
			}
		}
	}	
 }
 
 function comprobarCampos(incluirJmesa){
	 if (incluirJmesa) { 
		 jQuery.jmesa.removeAllFiltersFromLimit('listadoPolizasSbp');
	 }
   	var resultado = false;
   	
   	if ($('#entidad').val() != ''){
   		if (incluirJmesa) { 
   		jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.colectivo.tomador.id.codentidad', $('#entidad').val());
   		}
   		resultado = true;
   	}       	
   	if ($('#oficina').val() != ''){
   		if (incluirJmesa) { 
   		jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.oficina', $('#oficina').val());
   		}
   		resultado = true;
   	}
   	
   	/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
   	if ($('#entmediadora').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.colectivo.subentidadMediadora.id.codentidad', $('#entmediadora').val());
   		}
   		resultado = true;
   	}
   	if ($('#subentmediadora').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.colectivo.subentidadMediadora.id.codsubentidad', $('#subentmediadora').val());
   		}
   		resultado = true;
   	}
   	if ($('#delegacion').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.usuario.delegacion', $('#delegacion').val());
   		}
   		resultado = true;
   	}
   	/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */
   	if ($('#codusuario').val() != ''){
   		if (incluirJmesa) { 
   		jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.usuario.codusuario', $('#codusuario').val());
   		}
   		resultado = true;
   	} 
   	if ($('#plan').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.linea.codplan', $('#plan').val());
   		}
   		resultado = true;
   	}
   	if ($('#linea').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.linea.codlinea', $('#linea').val());
   		}
   			resultado = true;
   	}
   	if ($('#referencia').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','referencia', $('#referencia').val());
   		}
   		resultado = true;
   	}
   	if ($('#colectivo').val() != ''){
   		jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.colectivo.idcolectivo', $('#colectivo').val());
   		resultado = true;
   	}
   	if ($('#modulo').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.codmodulo', $('#modulo').val());
   		}
   		resultado = true;
   	}
  	if ($('#fecEnvioId').val() != ''){
  		if (incluirJmesa) { 
  			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','fechaEnvioSbp', $('#fecEnvioId').val());
  		}
  		resultado = true;
   	}
   	if ($('#nifCif').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.asegurado.nifcif', $('#nifCif').val());
   		}
   		resultado = true;
   	}
   	if ($('#incSbpCpl').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','incSbpComp', $('#incSbpCpl').val());
   		}
   		resultado = true;
   	}
   	if ($('#estadoPpal').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.estadoPoliza.idestado', $('#estadoPpal').val());
   		}
   		resultado = true;
   	}
   	if ($('#estadoCpl').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaCpl.estadoPoliza.idestado', $('#estadoCpl').val());
   		}
   		resultado = true;
   	}
   	if ($('#estadoSbp').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','estadoPlzSbp.idestado', $('#estadoSbp').val());
   		}
   		resultado = true;
   	}
   	if ($('#tipoEnvio').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','tipoEnvio.descripcion', $('#tipoEnvio').val());
   		}
   		resultado = true;
   	}
   	if ($('#detalle').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','errorPlzSbp.errorSbp.iderror', $('#detalle').val());
   		}
   		resultado = true;
   	}	         	
   	if ($('#clase').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.clase', $('#clase').val());
   		}
   		resultado = true;
   	}
   	if ($('#refPlzOmega').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','refPlzOmega', $('#refPlzOmega').val());
   		}
   		resultado = true;
   	}
   	//DNF 29/11/2018
   	if ($('#nSolicitud').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp',/*'polizaPpal.idpoliza'*/ 'nSolicitud', $('#nSolicitud').val());
   		}
   		resultado = true;
   	}
   	
   	/**
   	* P0073325 - RQ.10, RQ.11 y RQ.12
   	*/
   	if ($('#canalFirma').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp', 'gedDocPolizaSbp.canalFirma.idCanal', $('#canalFirma').val());
   		}
   		resultado = true;
   	}
   	if ($('#docFirmada').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoPolizasSbp', 'gedDocPolizaSbp.docFirmada', $('#docFirmada').val());
   		}
   		resultado = true;
   	}
   	
   	return resultado;
}
 
function mostrarErroresDetalle(mensaje){
	var arrayMensajes = mensaje.split(",");
    var msj ="";
    for(var i = 0; i < arrayMensajes.length; i++){
     	msj += arrayMensajes[i] + "<br>";
    }
 	$('#mensajeError').html(msj);
 	$('#panelInformacion').fadeIn('normal');
    $('#divMensajeError').fadeIn('normal');
    $('#overlay').show();
}
 
function cerrarPopUp(){
	$('#divMensajeError').fadeOut('normal');
	$('#overlay').hide();
}
 
function verificarLineaSbp(){
	var linea = $('#linea').val();
	var plan = $('#plan').val();
	$.ajax({
        url:          "simulacionSbp.html",
        data:         "method=ajax_verificarLineaSbp&linea="+linea+"&plan="+plan,
        async:        true,
        contentType:  "application/x-www-form-urlencoded",
        dataType:     "text",
        global:       false,
        ifModified:   false,
        processData:  true,
        error: function(objeto, quepaso, otroobj){
            alert("Error en la verificaci�n de la l�nea para Sobreprecio: " + quepaso);
        },
        success: function(errorLineaIncompatible){
        	if (errorLineaIncompatible != ""){ //plan/linea incompatible para Sbp
        		$('#panelAlertasValidacion').html(errorLineaIncompatible);
				$('#panelAlertasValidacion').show();
        	}else{ // plan/linea compatibles para Sbp
	            onInvokeAction('listadoPolizasSbp','filter');
	  	 	}
        },
        type: "GET"
    });
}

function consultarInicial () {
	$("#main3").validate().cancelSubmit = true;
	
	/* Pet. 63473 ** MODIF TAM (21/12/2021) ** Inicio */
	var entMed = document.getElementById('entmediadora').value;
	$('#entMediadora').val(entMed);
	
	var subEntMed = document.getElementById('subentmediadora').value;
	$('#subEntmediadora').val(subEntMed);
	
	var deleg = document.getElementById('delegacion').value;
	$('#deleg').val(deleg);
	/* Pet. 63473 ** MODIF TAM (21/12/2021) ** Fin */
	
	limpiaAlertas();
	//$('#main3').validate().cancelSubmit = true;
	var frm = document.getElementById('main3');
	if (validarConsulta()){	
		if (comprobarCampos(false)){
			var validacion = validarGrupoEntidad();		
			var valOfi=validarOficina();
			if (!validacion){
				$('#panelAlertasValidacion').html("La Entidad seleccionada no pertenece al grupo de Entidades del usuario");
				$('#panelAlertasValidacion').show();
			}else if(!valOfi){
				$('#panelAlertasValidacion').html("La Oficina seleccionada no pertenece al grupo de Oficinas del usuario");
				$('#panelAlertasValidacion').show();
			}else{		
				frm.origenLlamada.value= 'primeraBusqueda';
				//frm.submit();
				$("#main3").validate().cancelSubmit = true;
				$('#main3').submit();
			}		
		}// Si no, muestra el aviso
		else {
			avisoUnCampo ();
		}
	}
}

//Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo () {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}

function editar_eleccion_SiCpl(){
	$('#popUpEditarSbpConCpl').hide();
	$('#overlay').hide();
	$('#recalcularConCpl').val("true");
	$("#simulacionForm").submit();
}

function editar_eleccion_NoCpl(){
	$('#popUpEditarSbpConCpl').hide();
	$('#overlay').hide();
	$('#recalcularConCpl').val("false");
	$("#simulacionForm").submit();
}

function hidePopUpEditarSbpConCpl(){
	$('#popUpEditarSbpConCpl').hide();
	$('#overlay').hide();
}

function anularSbp (idSbp,referencia){
	if (confirm('�Est� seguro de que desea anular el sobreprecio?')){
		var frm = document.getElementById('simulacionForm');
		frm.method.value = 'doAnular';
		frm.idPolSbp.value = idSbp;
		frm.referenciaPol.value = referencia;
		$("#simulacionForm").submit();
	}
}


function showOrHideExportIcon() {
    var table = document.getElementById("listadoPolizasSbp");
    var matchingRow = table.querySelector("tr[id^='listadoPolizasSbp']");

    if (matchingRow) {
        document.getElementById("divImprimir").style.display = "block";
    } else {
        document.getElementById("divImprimir").style.display = "none";
    }
}


function exportToExcel() {
	
	var frm = document.getElementById('main3');
	frm.exportToExcel.value = "true";
	frm.target="_blank";
	frm.submit();
}

function altaSuplementoSbp(idPolizaSbp) {

	$.ajax({
	    url:          "suplementoSbp.html",
	    data:         "method=validaSuplemento&idPolizaSbp="+idPolizaSbp,
	    dataType:     "json",
	    async:        true,
	    error: function(objeto, quepaso, otroobj){
	        alert("Error en el alta del suplemento de Sobreprecio: " + quepaso);
	    },
	    success: function(datos){
	    		    	
	    	if (datos.validarSuplemento != "") {
	    		$('#panelAlertasValidacion').html(datos.validarSuplemento);
	    		$('#panelAlertasValidacion').show();
	    	} 
	    	else {
	    		var frm = document.getElementById('altaSuplementoSbp');
		  	 	frm.method.value ='doAltaSuplemento';
		  	 	frm.idPolizaSbp.value = idPolizaSbp;
		    	$('#altaSuplementoSbp').submit();
	    	}
	    },
	    type: "POST"
	});
}

function anularSuplementoSbp (refPolizaPrincipalSbp){
	if (confirm('¿Está seguro de que desea anular el suplemento?')){
		var frm = document.getElementById('anularSuplementoSbp');
		frm.method.value = 'doAnular';
		frm.refPolizaPrincipalSbp.value = refPolizaPrincipalSbp;
		$("#anularSuplementoSbp").submit();
	}
}
