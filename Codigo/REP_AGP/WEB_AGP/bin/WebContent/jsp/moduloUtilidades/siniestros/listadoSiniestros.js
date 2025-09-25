// Para evitar el cacheo de peticiones al servidor
$(document).ready(function(){

	showOrHideExportIcon();
	
// Calendario de Fecha de Ocurrencia
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
        inputField        : "fechaocurrenciaId",
        button            : "btn_fechaocurrencia",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
 
 // Calendario de Fecha de Firma
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
        inputField        : "fecfirmasiniestroId",
        button            : "btn_fecfirmasiniestro",
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
		
// Lanza la consulta de siniestros
function lanzarConsulta () {

	// Llama al m�todo que llama al servidor
	onInvokeAction('listadoSiniestros','filter');
}

// M�todo que realiza la llamada al servidor para listar los siniestros
function onInvokeAction(id) {
	// Muestra la imagen de carga
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	
	//Lanza la llamada al servidor
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main3');
    $.get('utilidadesSiniestros.run?ajax=true&' + parameterString, function(data) {$("#grid").html(data); showOrHideExportIcon();});
}	

// M�todo que realiza la llamada al servidor para generar el informe de siniestros
function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'utilidadesSiniestros.run?ajax=false&' + parameterString;
}


// Comprueba si alg�n campo del formulario est� informado
// Si el par�metro es true, cada campo que est� informado se incluye en el filter de jmesa
function comprobarCampos(incluirJmesa){
 	
 	if (incluirJmesa) {
 		jQuery.jmesa.removeAllFiltersFromLimit('listadoSiniestros');
 	}
   	
   	var resultado = false;
   	
   	if ($('#entidad').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','codentidad', $('#entidad').val());
   		}
   		resultado = true;
   	}  
   	
   	if ($('#oficina').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','oficina', $('#oficina').val());
   		}	       		
   		resultado = true;
   	}  
   	
   	if ($('#codusuario').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','codusuario', $('#codusuario').val());
   		}
   		resultado = true;
   	}   
   	if ($('#delegacion').val() != ''){
   		if (incluirJmesa) { 
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','delegacion', $('#delegacion').val());
   		}
   		resultado = true;
   	}   
   	
   	if ($('#plan').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','codplan', $('#plan').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#linea').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','codlinea', $('#linea').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#poliza').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','referencia', $('#poliza').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#nifcif').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','nifcif', $('#nifcif').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#riesgo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','codriesgo', $('#riesgo').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#nombre').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','nombre', $('#nombre').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#estado').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','idestado', $('#estado').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#fechaocurrenciaId').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','focurr', $('#fechaocurrenciaId').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#fecfirmasiniestroId').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','ffirma', $('#fecfirmasiniestroId').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#fechaEnvioId').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','fenv', $('#fechaEnvioId').val());
   		}	       		
   		resultado = true;
   	} 
   	
   	if ($('#fechaEnvioPolId').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','fenvpol', $('#fechaEnvioPolId').val());
   		}	       		
   		resultado = true;
   	} 
   	if ($('#fechaEnvioPolId').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','fenvpol', $('#fechaEnvioPolId').val());
   		}	       		
   		resultado = true;
   	} 
   	if ($('#entmediadora').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','entmediadora', $('#entmediadora').val());
   		}	       		
   		resultado = true;
   	} 
	if ($('#subentmediadora').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','subentmediadora', $('#subentmediadora').val());
   		}	       		
   		resultado = true;
   	}
	
	if ($('#numerosiniestro').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('listadoSiniestros','numerosiniestro', $('#numerosiniestro').val());
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
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!entidadOk) {
			$('#panelAlertasValidacion').html("Valor para la entidad no v�lido");
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
			$('#panelAlertasValidacion').html("Valor para la oficina no v�lido");
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
			$('#panelAlertasValidacion').html("Valor para el plan no v�lido");
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
			$('#panelAlertasValidacion').html("Valor para la l�nea no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}

	return true;
}


// Realiza la llamada al controlador para obtener los siniestros que se ajusten al filtro de b�squeda
function consultar () {
	// Limpia las alertas anteriores
 	limpiaAlertas();	 	
 	// Comprueba si hay alg�n campo del filtro que est� informado
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

// Realiza la primera llamada al controlador para obtener los siniestros que se ajusten al filtro de b�squeda
// Esta llamada no incluye los filtros de b�squeda en jmesa
function consultarInicial () {
	// Limpia las alertas anteriores
 	limpiaAlertas();	 	
 	// Comprueba si hay alg�n campo del filtro que est� informado
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

// Redirige a la pantalla de visualizaci�n del siniestro en modo solo lectura
function informacion (idSiniestro, idPoliza) {
	$.blockUI.defaults.message = '<h4> Procesando petici�n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });       		
	$("#idSiniestro").val(idSiniestro);
	$("#idPoliza").val(idPoliza);
	$("#modoLectura").val("true");
	$("#method").val("doEdita");
	$("#formulario").submit();
}

// Realiza la llamada al servidor para mostrar el informe correspondiente al siniestro
function imprimir (idSiniestro) {
	var f = document.getElementById ("print");
	f.idSiniestro.value = idSiniestro;
	$('#print').attr('target', '_blank');
	$("#print").submit();
}

//DAA 13/11/12 
function editar (idSiniestro, idPoliza, estado) {
	if(estado!=5){
			doEditar(idSiniestro, idPoliza);
	}
	else{
		if(confirm('El Siniestro pasar� a estado Provisional, �Desea Continuar?')){
			doEditar(idSiniestro, idPoliza);
		}
	}			

}
// Redirige a la pantalla de visualizaci�n del siniestro en modo edici�n
function doEditar (idSiniestro, idPoliza) {
	
	$.blockUI.defaults.message = '<h4> Procesando petici�n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });       		
	$("#idSiniestro").val(idSiniestro);
	$("#idPoliza").val(idPoliza);
	$("#modoLectura").val("");
	$("#method").val("doEdita");
	$("#formulario").submit();
}


// Borra el siniestro seleccionado
function borrar (idSiniestro, idPoliza) {	
	if(confirm('�Est� seguro de que desea eliminar el siniestro seleccionado?')){		
		$.blockUI.defaults.message = '<h4> Eliminando el siniestro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });     		
		$("#idSiniestro").val(idSiniestro);
		$("#idPoliza").val(idPoliza);
		/* Pet. 63473 ** MODIF TAM (30.11.2021) ** Inicio */
		/*$("#method").val("doBaja");*/
		$("#method").val("doEliminar");
		/* Pet. 63473 ** MODIF TAM (30.11.2021) ** Fin */
		$("#formulario").submit();
	}
}

/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Inicio */
function bajaSiniestro(idSiniestro){
	if(confirm('�Est� seguro de que desea dar de Baja el Siniestro seleccionado?')){
		
		$.blockUI.defaults.message = '<h4> Dando de Baja el Siniestro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		
		$("#method").val("doBajaSiniestro");
		$("#idSiniestro").val(idSiniestro);	
		$("#formulario").submit();
	}
}
/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Fin */


// Pasa a definitiva el siniestro seleccionado
function pasarADefinitiva (idSiniestro, idPoliza) {
	if(confirm('�Est� seguro de que desea pasar a definitiva el siniestro seleccionado?')){
		$.blockUI.defaults.message = '<h4> Pasando a definitiva el siniestro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
		$("#idSin").val(idSiniestro);
		$("#idPol").val(idPoliza);
		$("#method").val("doPasarDefinitiva");
		$("#formulario").submit();
	}
}

// Redirige a la pantalla de errores de validaci�n del siniestro seleccionado
function verErrores (idSiniestro) {
		$("#idSiniestro").val(idSiniestro);
		$("#method").val("doVerRecibo");
		$("#formulario").submit();
}

function pdfParteSiniestro(serieSiniestro, numSiniestro, idPoliza, idSiniestro, numeroSiniestro){		
	
	var frm=document.getElementById('frmPdfParte');
	frm.serieSiniestro.value  = serieSiniestro;
	frm.numSiniestro.value    = numSiniestro;
	frm.idPoliza_parte.value  = idPoliza;  
	frm.idSiniestro.value     = idSiniestro;
	frm.numeroSiniestro.value = numeroSiniestro;
	frm.target="_blank";
	frm.submit();       		       		
}

function verDetalleLineaSiniestro (serieSiniestro, numSiniestro, idSiniestro, idPoliza){ 
	
	$.ajax({
	
		url: "siniestrosInformacion.html?method=doVerDetalleSiniestro&serieSiniestro="+serieSiniestro+"&numSiniestro="+numSiniestro+"&idSiniestro="+idSiniestro+"&idPoliza="+idPoliza,
		data: "",
		async:true,
		dataType: "json",
		error: function(objeto, quepaso, otroobj){
			$.unblockUI();
			llamadaErrorMensaje(objeto);
		},
		success: function(datos){
			$.unblockUI();
			pintarTabla(datos.listaS);
			$('#listaSiniestros').val(datos.listaS);
			$('#panelInformacion').show();
			$('#panelInformacionSiniestro').show();
			$('#overlay').show();
		},
		beforeSend: function(){
	        $.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		        $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		},
		type: "GET"
		
	});
}

function llamadaErrorMensaje(objeto){

	var mensaje =  objeto.responseText ;
	var prueba = mensaje.split("}{");
	var definitivo = prueba[0] + "}";
	
	var json = jQuery.parseJSON( definitivo ).alerta;

	$('#panelInformacionSiniestro').show();
	$('#panelInformacion').empty();
	$('#panelInformacion').append(json);
	$('#panelInformacion').show();
	$('#overlay').show(); 
}

function formatearFecha(fecha){
	var hoy = fecha.split(" ");
	var fecha = hoy[2] +" "+ hoy[1] + " "+ hoy[5];
	var fechaVar = new Date(fecha);
	
	var dia = fechaVar.getDate();
	var mes = fechaVar.getMonth() +1;
	var ano = fechaVar.getFullYear();
	
	
	if(mes < 10){
		mes = "0" + mes;
	}
	return dia + "/" + mes + "/" + ano;
}

/* recorremos los datos y generamos la tabla dinamicamente para pasarsela al popup*/ 
function pintarTabla(listaS){
	
	
	var reg ="";
	var cabecera = "<table style='width:90%; border-collapse:collapse;' class='LISTA'><tr><th class='cblistaImg'></th><th style='text-align:center' class='cblistaImg'> F.Ocurrencia </th> <th style='text-align:center' class='cblistaImg'> Riesgo </th> <th style='text-align:center' class='cblistaImg'> Situaci�n </th> <th style='text-align:center' class='cblistaImg'> Serie </th><th style='text-align:center' class='cblistaImg'> N�mero </th></tr>";
	var final = "</table>";
	
	for (var i = 0; i < listaS.length; i++){
		var itemSiniestro = listaS[i];
		
		
		reg = reg +"<tr> " +
		
		"<td style='text-align:center' class='literal'> " +
			"<a href='javascript:pdfParteSiniestro(" + itemSiniestro.serie + "," + itemSiniestro.numsiniestro + "," + itemSiniestro.idpoliza + "," + itemSiniestro.id + "," + itemSiniestro.numerosiniestro + ")'" + ">" +
				"<img src='jsp/img/displaytag/imprimir_poliza_modificada.png' alt='Pdf - Parte del siniestro'> "+
			"</a> " +
		"</td>" +
		"<td style='text-align:center' class='literal'>" + formatearFecha(itemSiniestro.focurr) +
		"</td>"+
		"<td style='text-align:center' class='literal'>" + itemSiniestro.codriesgo +" - "+ itemSiniestro.desriesgo       + "</td>" +
		"<td style='text-align:center' class='literal'>" + itemSiniestro.idestado  +" - "+ itemSiniestro.descestado      + "</td>" +
		"<td style='text-align:center' class='literal'>" + itemSiniestro.serie           + "</td>" +
		"<td style='text-align:center' class='literal'>" + itemSiniestro.numerosiniestro + "</td></tr> ";
	}
	// pintamos la tabla en el popup
	

	$('#datosPop').html(cabecera+reg+final);
	
}

function showOrHideExportIcon() {
    var table = document.getElementById("listadoSiniestros");
    var matchingRow = table.querySelector("tr[id^='listadoSiniestros']");

    if (matchingRow) {
        document.getElementById("divImprimir").style.display = "block";
    } else {
        document.getElementById("divImprimir").style.display = "none";
    }
}

function exportToExcel(size) {
    var frm = document.getElementById('exportToExcel');
    frm.target="_blank";
    frm.submit();
}
