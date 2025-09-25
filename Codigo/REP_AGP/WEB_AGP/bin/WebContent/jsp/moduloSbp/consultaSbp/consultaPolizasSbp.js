$(document).ready(function(){
	$('#codusuario').val($('#filtroUsuario').val());		
    var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
    
    /* Pet.63473 ** MODIF TAM (20.12.2021) */ 
	$('#entmediadora').val($('#entMediadora').val());
	$('#subentmediadora').val($('#subEntmediadora').val());
	$('#delegacion').val($('#deleg').val());
	
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
		},
		messages: {
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
	})	

	
});



function consultarInicial () {
	limpiaAlertas(); 
	var frm = document.getElementById('main3');
	
	/* Pet. 63473 ** MODIF TAM (21/12/2021) ** Inicio */
	var entMed = document.getElementById('entmediadora').value;
	$('#entMediadora').val(entMed);
	
	var subEntMed = document.getElementById('subentmediadora').value;
	$('#subEntmediadora').val(subEntMed);
	
	var deleg = document.getElementById('delegacion').value;
	$('#deleg').val(deleg);
	/* Pet. 63473 ** MODIF TAM (21/12/2021) ** Fin */
	
	if (validarConsulta()){
		if (comprobarCampos(false)){		
			var validacion = validarGrupoEntidad();	
			var validacionOficina=validarOficina();
			if (!validacion){
				$('#panelAlertasValidacion').html("La Entidad seleccionada no pertenece al grupo de Entidades del usuario");
				$('#panelAlertasValidacion').show();
			}else if(!validacionOficina){
				$('#panelAlertasValidacion').html("La Oficina seleccionada no pertenece al grupo de Oficinas del usuario");
				$('#panelAlertasValidacion').show();
			
		   }else{			
				frm.origenLlamada.value= 'primeraBusqueda';
				$("#main3").validate().cancelSubmit = true;
				$('#main3').submit();
			}
		}// Si no, muestra el aviso
		else {
			avisoUnCampo ();
		}
	}
}


function consultar(){
	
	limpiaAlertas(); 
	if (validarConsulta()){
		if (comprobarCampos(true)){
			lanzarConsulta ();
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

//Muestra la situación actualizada de la póliza tras llamar al WS de Agroseguro
function verSituacionActual (ref, plan, tipoRef) {
	//$('#idAnexoPolActualizada').val(idAnexo);
	$('#refPolizaPlzAct').val(ref);
	$('#codPlanPlzAct').val(plan);	
	/* Pet. 57626 ** MODIF TAM (30.06.2020) -> Pasamos también el tipoRef para la nueva llamada de la Situación Actualizada*/
	$('#tipoRefPlzAct').val(tipoRef);
	$('#polizaActualizada').submit();
}

var idPolizaAccion;

function closePopUpAmbitoCont(){
	$('#popUpAmbitoContratacion').hide();
	$('#overlay').hide();
}
	
/*
* handler event click button aceptar in popUp
*/
function aceptarPopUpPanelAvisos(){
	$('#operacion').val('MultiGrabDef');
	$('#main3').submit();
}

/*
*  show popup
*/
function showPopUpAviso(mensaje, popUp){
	if(popUp == "popUpPasarDefinitivaBoton"){
		$('#txt_mensaje_aviso_1').html(mensaje);
		$('#popUpAvisos').show();
		$('#overlay').show();
	}else if(popUp == "popUpPasarDefinIconRow"){
		$('#txt_mensaje_aviso_2').html(mensaje);
		$('#popUpPasarDefinitivaIconRow').show();
		$('#overlay').show();
	}else if(popUp == "popUpPasarDefinIconRowCpl"){
		$('#txt_mensaje_aviso_3').html(mensaje);
		$('#popUpPasarDefinitivaIconRowCpl').show();
		$('#overlay').show();
	}
}

/**
* hide popup
*/
function hidePopUpAviso(popUp){
	if(popUp == "popUpPasarDefinitivaBoton"){
		$('#popUpAvisos').hide();
	}else if(popUp == "popUpPasarDefinIconRow"){
		$('#popUpPasarDefinitivaIconRow').hide();
	}else if(popUp == "popUpPasarDefinIconRowCpl"){
		$('#popUpPasarDefinitivaIconRowCpl').hide();
	}else if(popUp == "popUpAltaSbp"){
		$('#popUpAltaSbp').hide();
	}
	$('#overlay').hide();
}

function setEntidad(entidad){
	document.getElementById('entidad').value = entidad;
}
				 			
function compruebaFiltro(){
 	var resultado = false;
 	if (!resultado && $('#entidad').val() != ''){
 		resultado = true;
 	}   
 	if (!resultado){
 		$('#panelAlertasValidacion').html("Es necesario filtrar por la entidad");
		$('#panelAlertasValidacion').show();
		return false;
 	} else{
 		return true;
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
	
	/* Pet. 63473 ** MODIF TAM (21/12/2021) ** Inicio */
	var entMed = document.getElementById('entmediadora').value;
	$('#entMediadora').val(entMed);
	
	var subEntMed = document.getElementById('subentmediadora').value;
	$('#subEntmediadora').val(subEntMed);
	
	var deleg = document.getElementById('delegacion').value;
	$('#deleg').val(deleg);
	/* Pet. 63473 ** MODIF TAM (21/12/2021) ** Fin */

	
	$('#linea').val()
	if (!isNaN(inputText2) && inputText.value != ""){
		while (inputText.value.length<4){
			inputText.value = '0'+inputText.value;
		}
	}
	limpiaAlertas();
	var validacion = validarGrupoEntidad();
	var validacionOficina=validarOficina();
	if (!validacion){
		$('#panelAlertasValidacion').html("La Entidad seleccionada no pertenece al grupo de Entidades del usuario");
		$('#panelAlertasValidacion').show();
	}else if(!validacionOficina){
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
					onInvokeAction('consultaPolizasSbp','filter');
				}
			}
		}
	}	
}
 
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!referenciaOk) {
			$('#panelAlertasValidacion').html("Valor para la Póliza no es válido");
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!EntidadOk) {
			$('#panelAlertasValidacion').html("Valor para la Entidad no válido");
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!OficinaOk) {
			$('#panelAlertasValidacion').html("Valor para la Oficina no válido");
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("Valor para el plan no válido");
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para la línea no válido");
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!claseOk) {
			$('#panelAlertasValidacion').html("Valor para la clase no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	return true;
}
 
function comprobarCampos(incluirJmesa){
	if(incluirJmesa){
		jQuery.jmesa.removeAllFiltersFromLimit('consultaPolizasSbp');
	}
	var resultado = false;
   	if ($('#entidad').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','colectivo.tomador.id.codentidad', $('#entidad').val());
   		}
   		//alert("entidad");
   		resultado = true;
   	}       	
   	if ($('#oficina').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','oficina', $('#oficina').val());
   		}
   		//alert("oficina");
   		resultado = true;
   	}
   	/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Inicio */
   	if ($('#entmediadora').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','colectivo.subentidadMediadora.id.codentidad', $('#entmediadora').val());
   		}
   		resultado = true;
   	}
   	if ($('#subentmediadora').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','colectivo.subentidadMediadora.id.codsubentidad', $('#subentmediadora').val());
   		}
   		resultado = true;
   	}
   	if ($('#delegacion').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','usuario.delegacion', $('#delegacion').val());
   		}
   		resultado = true;
   	}
   	/* Pet. 63473 ** MODIF TAM (20.12.2021) ** Fin */
   	
   	if ($('#codusuario').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','usuario.codusuario', $('#codusuario').val());
   		}
   		//alert("usuario");
   		resultado = true;
   	} 
   	if ($('#plan').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','linea.codplan', $('#plan').val());
   		}
   		//alert("plan");
   		resultado = true;
   	}
   	if ($('#linea').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','linea.codlinea', $('#linea').val());
   		}
   		//alert("linea");
   		resultado = true;
   	}
   	if ($('#referencia').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','referencia', $('#referencia').val());
   		}
   		//alert("referencia");
   		resultado = true;
   	}
   	if ($('#colectivo').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','colectivo.idcolectivo', $('#colectivo').val());
   		}
   		//alert("colectivo");
   		resultado = true;
   	}
   	if ($('#modulo').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','codmodulo', $('#modulo').val());
   		}
   		//alert("modulo");
   		resultado = true;
   	}
  	if ($('#fecEnvioId').val() != ''){
  		if(incluirJmesa){
  			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','fechaenvio', $('#fecEnvioId').val());
  		}
  		//alert("fecenvioid");
   		resultado = true;
   	}
   	if ($('#nifcif').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','asegurado.nifcif', $('#nifcif').val());
   		}
   		//alert("nifcif");
   		resultado = true;
   	}
   	if ($('#nombreAseg').val() != ''){
   		//jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','asegurado.nombre', $('#nombreAseg').val());
   		//alert("nombreAseg");
   		resultado = true;
   	}
   	if ($('#estadoP').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','estadoPoliza.idestado', $('#estadoP').val());
   		}
   		//alert("estado");
   		resultado = true;
   	}
   	if ($('#clase').val() != ''){
   		if(incluirJmesa){
   			jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','clase', $('#clase').val());
   		}
   		//alert("clase");
   		resultado = true;
   	}
   	
   	return resultado;
}



function limpiar(){
	var frm = document.getElementById('main3');
	limpiaAlertas();
	
	//$('#entidad').val(frm.codEnt.value);
	//jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','colectivo.tomador.id.codentidad', $('#entidad').val());
	$('#desc_entidad').val(frm.descEnt.value);
	if (frm.perfil.value == 3){
		$('#oficina').val(frm.codOfi.value);
		//jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','oficina', $('#oficina').val());
	}else{
		$('#oficina').val('');
	}
	$('#plan').val(frm.filtroPlan.value);
	//jQuery.jmesa.addFilterToLimit('consultaPolizasSbp','linea.codplan', $('#plan').val());
	
	$('#desc_oficina').val('');
	$('#codusuario').val('');
	$('#linea').val('');
	$('#desc_linea').val('');
	$('#referencia').val('');
	$('#colectivo').val('');
	$('#dc').val('');
	$('#modulo').val('');
	$('#fecEnvioId').val('');
	$('#nifcif').val('');
	$('#estadoP').selectOptions('');
	$('#ref').val('');
	$('#nombreAseg').val('');
	$('#clase').val('');
	$('#operacion').val("");
	$('#codusuario').val(frm.filtroUsuario.value);
	//jQuery.jmesa.addFilterToLimit('listadoPolizasSbp','polizaPpal.usuario.codusuario', $('#codusuario').val());
	// antes estaba lo siguiente
	//consultar();
	// hahora hago submit al form de limpiar
	$('#limpiar').submit();
}

	 
function volver() {
	$(window.location).attr('href', 'menu.html?OP=ppal');
}
	
function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main3');  
    $.get('consultaPolSbp.run?ajax=true&nombreAseg='+frm.nombreAseg.value+'&' + parameterString, function(data) {
        $("#grid").html(data)
	});
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
	 
function verAcuseRecibo(idpoliza){	
	$("#accion").val("doVerRecibo");
	$("#polizaOperacion").val(idpoliza);
	$("#main3").submit();
}

function AvisoErroresPoliza(){
	$('#divAviso').fadeIn('normal');
}

function cerrarPopUp(){
	$('#divAviso').fadeOut('normal');
	$('#txt_info_gp').hide();
	$('#txt_info_none').hide();
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
            alert("Error en la verificación de la línea para Sobreprecio: " + quepaso);
        },
        success: function(errorLineaIncompatible){
        	if (errorLineaIncompatible != ""){ //linea incompatible para Sbp
        		$('#panelAlertasValidacion').html(errorLineaIncompatible);
				$('#panelAlertasValidacion').show();
        	}else{ // plan/linea compatibles para Sbp
	            onInvokeAction('consultaPolizasSbp','filter');
	  	 	}
        },
        type: "GET"
    });
}
			
function alta(id,estado,tipo){
	$.ajax({
        url:          "simulacionSbp.html",
        data:         "method=ajax_buscarPolAsocYValidar&idPoliza="+id+"&tipoPoliza="+tipo+"&validarSbp=false",
        dataType:     "json",
        async:        true,
        error: function(objeto, quepaso, otroobj){
            alert("Error en el alta de la póliza de Sobreprecio: " + quepaso);
        },
        success: function(datos){
        	if (datos.idPolizaSbp != ""){ // tiene poliza Sbp
    			var form = document.getElementById('ListadoPolSbpForm');
    			form.idPolizaSbp.value = datos.idPolizaSbp;
    			$('#ListadoPolSbpForm').submit();
        	}else{ // no tiene Sbp
	            var frm = document.getElementById('altaPolizaSbp');
		  	 	frm.method.value ='doAlta';
		  	 	if (tipo == 'P'){
			  	 	frm.idPolizaPpal.value = id;
			  	 	frm.idEstadoPpal.value = estado;
			  	 	frm.idPolizaCpl.value = datos.idPoliza;
			  	 	frm.idEstadoCpl.value = datos.estado;
			  	}else{
			  		frm.idPolizaPpal.value = datos.idPoliza;
			  	 	frm.idEstadoPpal.value = datos.estado;
			  	 	frm.idPolizaCpl.value = id;
			  	 	frm.idEstadoCpl.value = estado;
			  	}
		  	 	if (frm.idPolizaCpl.value != ""){ //tenemos poliza complementaria
		  	 		if (frm.idEstadoCpl.value != 4){
		  	 			if((frm.idEstadoCpl.value == 8 || frm.idEstadoCpl.value == 5) && frm.idEstadoPpal.value == 8){
		  	 				alta_eleccion_Si()
		  	 			}else{
			  	 			var msj = "¿Desea incluir los datos de la póliza complementaria en el Sobreprecio?";
		  	 				//Abrir Popup incluir Cpl en Sbp
		  	 				$('#txt_mensaje_eleccionCplEnSbp').html(msj);
		  	 				$('#panelInformacion').show();
		  	 				$('#popUpAltaSbp').show();
	 						$('#overlay').show();
		  	 			}
		  	 		}else{ // Cpl anulada
		  	 			$.blockUI.defaults.message = '<h4> Calculando Póliza de Sobreprecio.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       					$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	  	 				$('#altaPolizaSbp').submit();
		  	 		}
		  	 	}else{ // no hay Cpl
	  	 			$.blockUI.defaults.message = '<h4> Calculando Póliza de Sobreprecio.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	  	 			$('#altaPolizaSbp').submit();
	  	 		}
	  	 	}
        },
        type: "POST"
    });
}
		
function alta_eleccion_Si(){
	$('#popUpAltaSbp').hide();
	$('#overlay').hide();
	var frm = document.getElementById('altaPolizaSbp');
	frm.incSbpComp.value = "S";
	$.blockUI.defaults.message = '<h4> Calculando Póliza de Sobreprecio.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$('#altaPolizaSbp').submit();
}

function alta_eleccion_No(){
	$('#popUpAltaSbp').hide();
	$('#overlay').hide();
	var frm = document.getElementById('altaPolizaSbp');
	frm.incSbpComp.value = "N";
	$.blockUI.defaults.message = '<h4> Calculando Póliza de Sobreprecio.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$('#altaPolizaSbp').submit();
}
		
function imprimir(id,estado,tiporef){
	if ((estado == 1) || (estado == 2)){
		if (tiporef == 'P'){
			var frm = document.getElementById('printBorrador');
			frm.StrImprimirReducida.value = 'false';
			frm.idPolizaPrint.value = id;
			frm.method.value = 'doInformePoliza';
			$('#printBorrador').attr('target', '_blank');
			$("#printBorrador").submit();
		}else{
			var frm = document.getElementById('printBorrador');
			frm.idPolizaPrint.value = id;
			frm.method.value = 'doInformePolizaComplementaria';		
			$('#printBorrador').attr('target', '_blank');
			$("#printBorrador").submit();
		}
	}else{
		var frm = document.getElementById('print');
		frm.method.value = 'doImprimirCopyOSituacionOrigen';
		frm.idPoliza.value = id;
		frm.target="_blank";
		frm.submit();
	}
}

function borrar(id) {
	if(confirm('¿Está seguro de que desea eliminar la póliza de Sobreprecio asociada?')){
		var frm = document.getElementById('simulacionForm');
		frm.method.value = 'doBaja';
		frm.idPolSbp.value = id;
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#simulacionForm").submit();	
	}	
}

function editar(id,codPlan,codLinea,referenciaSbp,idPoliza){
	var frm = document.getElementById('ListadoPolSbpForm');
	frm.method.value = 'doEditar';
	frm.idPolizaSbp.value = id;
	frm.codPlan.value = codPlan;
	frm.codLinea.value = codLinea;
	frm.idPolizaSeleccion.value = idPoliza;
	frm.referenciaSbp.value = referenciaSbp;
	$("#ListadoPolSbpForm").submit();
}	 