// Para evitar el cacheo de peticiones al servidor
$(document).ready(function(){
	
	showOrHideExportIcon();

// Calendario de Fecha de Da�o
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
        inputField        : "fechadanioId",
        button            : "btn_fecha",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
  	
 // Calendario de Fecha de Env�o 
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
  	


// Calendario de Fecha de Env�o de P�liza
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
        inputField        : "fechaEnvioPolId",
        button            : "btn_fechaEnvioPol",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
 
 //P0079361 nuevos calendarios
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
     inputField        : "fechadanioIdHasta",
     button            : "btn_fechaHasta",
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
     inputField        : "fechaEnvioIdHasta",
     button            : "btn_fechaEnvioHasta",
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
     inputField        : "fechaEnvioPolIdHasta",
     button            : "btn_fechaEnvioPolHasta",
     ifFormat          : "%d/%m/%Y",
     daFormat          : "%d/%m/%Y",
     align             : "Br"			        	        
	});
 //P0079361 nuevos calendarios
  	
  	// Validaci�n del formulario
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

showOrHideExportIcon();
  	
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
		
// Lanza la consulta de ReduccionCapital
function lanzarConsulta () {

	// Llama al m�todo que llama al servidor
	onInvokeAction('listadoReduccionCapital','filter');
}

// M�todo que realiza la llamada al servidor para listar los ReduccionCapital
function onInvokeAction(id) {
	// Muestra la imagen de carga
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	
	//Lanza la llamada al servidor
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main3');
    $.get('utilidadesReduccionCapital.run?ajax=true&' 
    	//P0079361
    		+ '&fechadanioId='+ frm.fechadanioId.value
    		+ '&fechadanioIdHasta=' + frm.fechadanioIdHasta.value
    		+ '&fechaEnvioId=' + frm.fechaEnvioId.value
    		+ '&fechaEnvioIdHasta=' + frm.fechaEnvioIdHasta.value
    		+ '&fechaEnvioPolId=' + frm.fechaEnvioPolId.value
    		+ '&fechaEnvioPolIdHasta=' + frm.fechaEnvioPolIdHasta.value
    		+ '&ftpNumCupon=' + $.trim($("#tipoEnvioId").val())
    		//+ '&estado=' + frm.estadoCuponId.value
    		+ '&' +
    		
		decodeURIComponent(parameterString), function(data) {$("#grid").html(data)});
		//+ parameterString, function(data) {$("#grid").html(data)});
    	//P0079361
    showOrHideExportIcon();
}	

// M�todo que realiza la llamada al servidor para generar el informe de ReduccionCapital
function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'utilidadesReduccionCapital.run?ajax=false&' + parameterString;
}


// Comprueba si alg�n campo del formulario est� informado
// Si el par�metro es true, cada campo que est� informado se incluye en el filter de jmesa
function comprobarCampos(incluirJmesa){

 	if (incluirJmesa) {
 		jQuery.jmesa.removeAllFiltersFromLimit('listadoReduccionCapital');
 	}
   	
   	var resultado = false;
   	
   	if ($('#entidad').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','codentidad', $('#entidad').val());
   		}
   		resultado = true;
   	}  
   	
   	if ($('#oficina').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','oficina', $('#oficina').val());
   		}	       		
   		resultado = true;
   	}  
   	
   	if ($('#codusuario').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','codusuario', $('#codusuario').val());
   		}
   		resultado = true;
   	}   
   	
   	if ($('#plan').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','codplan', $('#plan').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#linea').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','codlinea', $('#linea').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#poliza').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','referencia', $('#poliza').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#nifcif').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','nifcif', $('#nifcif').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	//P0079361
   	if ($('#riesgo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','codriesgo', $('#riesgo').val());
   		}	       		
   		resultado = true;
   	} 
   	//P0079361
   	
   	if ($('#nombre').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','nombre', $('#nombre').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#estado').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','idestado', $('#estado').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	//P0079361 change calendarios
   	if ($('#fechadanioId').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','fdanios', $('#fechadanioId').val());
   		}	       		
   		resultado = true;
   	}
   	
   	if ($('#fechaEnvioId').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','fenv', $('#fechaEnvioId').val());
   		}	       		
   		resultado = true;
   	}
   	
   	if ($('#fechaEnvioPolId').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','fenvpol', $('#fechaEnvioPolId').val());
   		}	       		
   		resultado = true;
   	}
   	//P0079361 change calendarios
   	
   	//P0079361 nuevos calendarios
   	if ($('#fechadanioIdHasta').val() != ''){       		
   		resultado = true;
   	}
   	
   if ($('#fechaEnvioIdHasta').val() != ''){	       		
   		resultado = true;
   	}
   	
   if ($('#fechaEnvioPolIdHasta').val() != ''){	       		
   		resultado = true;
   	}
   	//P0079361 nuevos calendarios
   	
   	if ($('#delegacion').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','delegacion', $('#delegacion').val());
   		}
   		resultado = true;
   	}   
   	if ($('#entmediadora').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','entmediadora', $('#entmediadora').val());
   		}	       		
   		resultado = true;
   	} 
	if ($('#subentmediadora').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','subentmediadora', $('#subentmediadora').val());
   		}	       		
   		resultado = true;
   	}
	
	//P0079361
	if ($('#tipoEnvioId').val() != ''){	       		
   		resultado = true;
   		/*if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','idcupon', $('#tipoEnvioId').val());
   		}*/
   	}
	
	if ($('#estadoCuponId').val() != ''){
		//dudas con if interno       		
   		resultado = true;
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoReduccionCapital','estado', $("#estadoCuponId option:selected").text());
   		}
   	}
	//P0079361
	
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
		
		// Si ha habido error en la validaci�n muestra el mensaje
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
		
		// Si ha habido error en la validaci�n muestra el mensaje
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
		
		// Si ha habido error en la validaci�n muestra el mensaje
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
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para la l&iacute;nea no v&aacute;lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	//P0079361
	const tiempoTranscurrido = Date.now();
	const hoy = new Date(tiempoTranscurrido);
	
	//salvo parada en validacion damos fechas por ok
	var fechasDanioOK = true;
	var fechasEnvioOK = true;
	var fechasEnvioPolOK = true;
	
	if ($('#fechaEnvioId').val() != '' && $('#fechaEnvioIdHasta').val() != ''){ 
	 	try {		 	
	 		const dateINI = convertToDate($('#fechaEnvioId').val());
	 		const dateFIN = convertToDate($('#fechaEnvioIdHasta').val());
	 		
	 		fechasEnvioOK = validacionFechasDesdeHasta(dateINI,dateFIN,"La 'Fecha Env&iacute;o' incial no puede ser superior a la fecha fin");
		}
		catch (ex) {}
	}else if ($('#fechaEnvioId').val() == '' && $('#fechaEnvioIdHasta').val() != ''){ 
	 	try {		 	
	 		const dateINI = convertToDate('01/01/1900');
	 		const dateFIN = convertToDate($('#fechaEnvioIdHasta').val());
	 		
	 		fechasEnvioOK = validacionFechasDesdeHasta(dateINI,dateFIN,"La 'Fecha Env&iacute;o' incial no puede ser superior a la fecha fin");
		}
		catch (ex) {}
	}else if ($('#fechaEnvioId').val() != '' && $('#fechaEnvioIdHasta').val() == ''){ 
	 	try {		 	
	 		const dateINI = convertToDate($('#fechaEnvioId').val());
	 		const dateFIN = convertToDate(hoy.toLocaleDateString());
	 		
	 		fechasEnvioOK = validacionFechasDesdeHasta(dateINI,dateFIN,"La 'Fecha Env&iacute;o' incial no puede ser superior a la fecha fin");
		}
		catch (ex) {}
	}
	
	if ($('#fechadanioId').val() != '' && $('#fechadanioIdHasta').val() != ''){ 
	 	try {		 	
	 		const dateINI = convertToDate($('#fechadanioId').val());
	 		const dateFIN = convertToDate($('#fechadanioIdHasta').val());
	 		
	 		fechasDanioOK = validacionFechasDesdeHasta(dateINI,dateFIN,"La 'Fecha Da&ntilde;o' incial no puede ser superior a la fecha fin");
		}
		catch (ex) {}
	}else if($('#fechadanioId').val() == '' && $('#fechadanioIdHasta').val() != ''){
		try {		 	
	 		const dateINI = convertToDate('01/01/1900');
	 		const dateFIN = convertToDate($('#fechadanioIdHasta').val());

	 		fechasDanioOK = validacionFechasDesdeHasta(dateINI,dateFIN,"La 'Fecha Da&ntilde;o' incial no puede ser superior a la fecha fin");
		}
		catch (ex) {}
	}else if($('#fechadanioId').val() != '' && $('#fechadanioIdHasta').val() == ''){
		try {		 	
	 		const dateINI = convertToDate($('#fechadanioId').val());
	 		const dateFIN = convertToDate(hoy.toLocaleDateString());

	 		fechasDanioOK = validacionFechasDesdeHasta(dateINI,dateFIN,"La 'Fecha Da&ntilde;o' incial no puede ser superior a la fecha fin");
		}
		catch (ex) {}
	}
	
	if ($('#fechaEnvioPolId').val() != '' && $('#fechaEnvioPolIdHasta').val() != ''){ 
	 	try {		 	
	 		const dateINI = convertToDate($('#fechaEnvioPolId').val());
	 		const dateFIN = convertToDate($('#fechaEnvioPolIdHasta').val());
	 		
	 		fechasEnvioPolOK = validacionFechasDesdeHasta(dateINI,dateFIN,"La 'Fecha Env&iacute;o P&oacute;liza' incial no puede ser superior a la fecha fin");
		}
		catch (ex) {}
	}else if ($('#fechaEnvioPolId').val() == '' && $('#fechaEnvioPolIdHasta').val() != ''){ 
	 	try {		 	
	 		const dateINI = convertToDate('01/01/1900');
	 		const dateFIN = convertToDate($('#fechaEnvioPolIdHasta').val());
	 		
	 		fechasEnvioPolOK = validacionFechasDesdeHasta(dateINI,dateFIN,"La 'Fecha Env&iacute;o P&oacute;liza' incial no puede ser superior a la fecha fin");
		}
		catch (ex) {}
	}else if ($('#fechaEnvioPolId').val() != '' && $('#fechaEnvioPolIdHasta').val() == ''){ 
	 	try {		 	
	 		const dateINI = convertToDate($('#fechaEnvioPolId').val());
	 		const dateFIN = convertToDate(hoy.toLocaleDateString());
	 		
	 		fechasEnvioPolOK = validacionFechasDesdeHasta(dateINI,dateFIN,"La 'Fecha Env&iacute;o P&oacute;liza' incial no puede ser superior a la fecha fin");
		}
		catch (ex) {}
	}
	
	if(!fechasDanioOK || !fechasEnvioOK || !fechasEnvioPolOK){
		return false;
	}
	//P0079361

	return true;
}

function convertToDate(dateString) {
    //"dd/MM/yyyy" string to Date
    var d = dateString.split("/");
    var dat = new Date(d[2] + '/' + d[1] + '/' + d[0]);
    return dat;     
}

function validacionFechasDesdeHasta(fechaIni,fechaFin,msg){
	if(fechaIni>fechaFin){
		var msgAvisoACtual = $('#panelAlertasValidacion').html();
		$('#panelAlertasValidacion').html(msgAvisoACtual + msg + "<br>");
		$('#panelAlertasValidacion').show();
		return false;
	}
	return true;
}

// Funcion Especial para omitir primera busqueda
function omitirPrimeraBusqueda () {
	//P0079361
	$('#limpiar').submit();
	document.getElementById("main3").reset();

	$("#tipoEnvioId").val(" ");
	consultarInicial();
	
	//P0079361
}


// Realiza la llamada al controlador para obtener los ReduccionCapital que se ajusten al filtro de b�squeda
function consultar () {
	console.log("Se muestra el panel de resultados");
	$("#tipoEnvioId").val($.trim($("#tipoEnvioId").val()));
	// Limpia las alertas anteriores
 	limpiaAlertas();	 	
 	// Comprueba si hay alg�n campo del filtro que est� informado
 	if (comprobarCampos(true)) {
 		// Valida los campos antes de la consulta
 		if (validarCamposConsulta ()) {		 		
 			// Lanza la consulta
			lanzarConsulta();
			
			//$('#omitirPrimeraBusquedaBox').show();
			//$('#divImprimir').show();
			
			mostrarElementConDelay($('#omitirPrimeraBusquedaBox'), 1000)
		    .then((mensaje) => {
		        console.log(mensaje);
		    })
		    .catch((error) => {
		        console.log(error);
		    });
			
			mostrarElementConDelay($('#divImprimir'), 1000)
		    .then((mensaje) => {
		        console.log(mensaje);
		    })
		    .catch((error) => {
		        console.log(error);
		    });
			
			//$('#divImprimir').show();
	 	}
 	}
 	// Si no, muestra el aviso
 	else {
		avisoUnCampo ();
	}
}

function mostrarElementConDelay(elemento, tiempo){
	//por atractivo visual
	return new Promise((resolve, reject) => {
        if (!elemento) {
            reject('Elemento no encontrado');
            return;
        }

        setTimeout(() => {
            elemento.show();
            resolve('Elemento visible');
        }, tiempo);
    });
}

// Realiza la primera llamada al controlador para obtener los ReduccionCapital que se ajusten al filtro de b�squeda
// Esta llamada no incluye los filtros de b�squeda en jmesa
function consultarInicial () {
	// Limpia las alertas anteriores
 	limpiaAlertas();	 	
 	// Comprueba si hay alg�n campo del filtro que est� informado
 	//P0079361
	if (comprobarCampos (false)) {
	//P0079361
		// Valida los campos antes de la consulta
 		if (validarCamposConsulta ()) {		 		
 			// Lanza la consulta
 			//P0079361
 			//se comenta para no tener un valor de primeraBusqueda y que se mande a nulo que es cuando funciona
			//$('#primeraBusqueda').val('primeraBusqueda');
 			$('#primeraBusqueda').val(null);
			//P0079361
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
	//P0079361
	$("#panelAlertasValidacion").html("");
	//P0079361
	$("#panelAlertas").hide();
}

// Limpia los valores introducidos y vuelve a la pantalla inicial
function limpiar () {
	//P0079361
	$('#limpiar').submit();
	//falta identificar otros forms para limpiar todo ya que no borra 2024
	//falta borrar las fechas de hasta del form
	document.getElementById("main3").reset();

	$("#entidad").val("");
	$("#desc_entidad").val("");
	$("#oficina").val("");
	$("#desc_oficina").val("");
	$("#entmediadora").val("");
	$("#subentmediadora").val("");
	$("#delegacion").val("");
	$("#plan").val("");
	$("#linea").val("");
	$("#desc_linea").val("");
	$("#poliza").val("");
	$("#nifcif").val("");
	$("#nombre").val("");
	$("#riesgo").val("");
	$("#estado").val("");
	$("#estadoCuponId").val("");
	$("#tipoEnvioId").val("");
	$("#fechadanioId").val("");
	$("#fechadanioIdHasta").val("");
	$("#fechaEnvioId").val("");
	$("#fechaEnvioIdHasta").val("");
	$("#fechaEnvioPolId").val("");
	$("#fechaEnvioPolIdHasta").val("");
	
	console.log("PASA POR LIMPIAR DE LISTADO RC");
	$("#omitirPrimeraBusquedaBox").hide();
	//P0079361
}


function showOrHideExportIcon() {
    var table = document.getElementById("listadoReduccionCapital");
    //ESC-33003
    if(table){ //saber que la tabla existe, tras haber realizado consulta previa que es cuando carga correctamente y se puede obtener el selector
    	var matchingRow = table.querySelector("tr[id^='listadoReduccionCapital']");
	    if (matchingRow) {
	        document.getElementById("divImprimir").style.display = "block";
	    } else {
	        document.getElementById("divImprimir").style.display = "none";
	    }
    }
  //ESC-33003
}

function exportToExcel(size) {
	//P0079361
	//limpieza previa campos para filtrado excel
	$("#estadoCuponRC").val("");
	$("#tipoEnvioRC").val("");
	$("#fEEnvio").val("");
	$("#fEEnvioHasta").val("");
	$("#fEdanio").val("");
	$("#fEdanioHasta").val("");
	$("#fEEnvioPol").val("");
	$("#fEEnvioPolHasta").val("");
	
	if ($('#estadoCuponId').val() != ''){
		$("#estadoCuponRC").val($("#estadoCuponId option:selected").text());
	}
	
	if ($('#tipoEnvioId').val() != ''){	 
		$("#tipoEnvioRC").val($('#tipoEnvioId').val());
	}
	
	if ($('#fechadanioId').val() != ''){
		$("#fEdanio").val($('#fechadanioId').val());
   	}
   	
   	if ($('#fechaEnvioId').val() != ''){
   		$("#fEEnvio").val($('#fechaEnvioId').val());
   	}
   	
   	if ($('#fechaEnvioPolId').val() != ''){
   		$("#fEEnvioPol").val($('#fechaEnvioPolId').val());
   	}

   	if ($('#fechadanioIdHasta').val() != ''){       		
   		$("#fEdanioHasta").val($('#fechadanioIdHasta').val());
   	}
   	
   	if ($('#fechaEnvioIdHasta').val() != ''){	       		
	   $("#fEEnvioHasta").val($('#fechaEnvioIdHasta').val());
   	}
   	
   	if ($('#fechaEnvioPolIdHasta').val() != ''){	       		
	   $("#fEEnvioPolHasta").val($('#fechaEnvioPolIdHasta').val());
   	}

	//P0079361
    var frm = document.getElementById('exportToExcel');
    frm.target="_blank";
    frm.submit();
}

//P0079361
function indicarFechaActual(){
	const hoy = new Date();
    
    const dia = String(hoy.getDate()).padStart(2, '0');
    const mes = String(hoy.getMonth() + 1).padStart(2, '0');
    const anio = hoy.getFullYear();

    const fechaFormateada = dia + '/' + mes + '/' + anio;
    
    console.log("FECHA HASTA DEFAULT: " + fechaFormateada);
    
    return fechaFormateada;
}
//P0079361
