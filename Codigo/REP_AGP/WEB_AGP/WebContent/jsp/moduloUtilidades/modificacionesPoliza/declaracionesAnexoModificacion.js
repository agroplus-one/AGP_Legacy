$(document).ready(function(){
	var URL = UTIL.antiCacheRand($("#main").attr("action"));
	$("#main").attr("action", URL);
});

function volver() {
	$("#method").val("doVolver");
	$("#id").val("");
	$("#main").submit();
}

function comprobarAltaAnexo(isCpl, isCupon, idPoliza, idEstadoPlz){
	
	$.ajax({
        url: "declaracionesModificacionPoliza.html",
        data: "method=doComprobarAlta&codPlan="+$("#codplan").val() + "&codLinea="+$("#codlinea").val() + "&idPoliza="+idPoliza + "&idEstadoPlz="+idEstadoPlz,
        async:true,
        beforeSend: function(objeto){
        },
        cache: false,
        complete: function(objeto, exito){
        },
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        error: function(objeto, quepaso, otroobj){
            alert("Error: " + quepaso);
        },
        global: true,
        ifModified: false,
        processData:true,
        success: function(datos){
        	if (datos.objeto == "sinGastos") {
        		$('#panelAlertasValidacion').html("Los datos de mantenimiento para el plan/l\u00EDnea son incorrectos. Por favor, p\u00F3ngase en contacto con RGA");
        		$('#panelAlertasValidacion').show();
        	}
        	else if(datos.objeto == "true"){
        		redirigirAlta (isCpl, isCupon);
        	}
        	else if(datos.objeto == "false"){
        		if(confirm('Est\u00E1 intentado dar de alta un anexo sobre una p\u00F3liza con un plan que no es el \u00FAltimo en contratacion. \u00BFEst\u00E1 seguro?')){
        			redirigirAlta (isCpl, isCupon);
        		}
        	}
        	else {
        		$('#panelAlertasValidacion').html("Ha ocurrido un error en la validaciones previas al alta del anexo");
        		$('#panelAlertasValidacion').show();
        	}
        },
        type: "POST"
    });
}

/**
 * Redirige al metodo de alta correspondiente dependiendo del tipo de poliza y del tipo de alta de AM
 * @param isCpl
 * @param isCupon
 */
function redirigirAlta (isCpl, isCupon) {
	if (isCupon) {
		altaAnexoSW();
	}
	else {
		if(isCpl == false)
			alta();
		else
			altaCpl();
	}
}


function alta() {
	$("#method").val("doEdita");
	$("#id").val("");
	$.blockUI.defaults.message = '<h4> Procesando petici&oacute;n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$("#main").submit();
} 

function altaCpl() {
	$("#methodCpl").val("doAlta");
	copiarInfCuponACpl ();
	$.blockUI.defaults.message = '<h4> Procesando petici&oacute;n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$("#main3").submit();
}

/**
 * Copia los datos del del cupon del formulario de la principal al de la complementaria
 */
function copiarInfCuponACpl () {
	$("#idCuponNumCpl").val($("#idCuponNum").val());
	$("#idCuponCpl").val($("#idCupon").val());
	$("#idCuponPpalPrevioCpl").val($("#idCuponPpalPrevio").val());
	$("#estadoCuponPpalPrevioCpl").val($("#estadoCuponPpalPrevio").val());
	$("#idCuponCplPrevioCpl").val($("#idCuponCplPrevio").val());
	$("#estadoCuponCplPrevioCpl").val($("#estadoCuponCplPrevio").val());
	
	$("#idEstadoPlzPpalAgroseguroCpl").val($("#idEstadoPlzPpalAgroseguro").val());
	$("#estadoPlzPpalAgroseguroCpl").val($("#estadoPlzPpalAgroseguro").val());
}

//DAA 16/11/12
function editar(idAnexo, estado){
	if(estado!=5){
		doEditar(idAnexo);
	}
	else{
		if(confirm('El Anexo Mod. pasar\u00E1 a estado Provisional, \u00BFDesea continuar?')){
			doEditar(idAnexo);
		}
	}
}

function doEditar(idAnexo) {
	$("#method").val("doEdita");
	$("#id").val(idAnexo);
	$.blockUI.defaults.message = '<h4> Procesando petici&oacute;n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$("#main").submit();
}


//DAA 16/11/12
function editarCpl(idAnexo, estado){
	if(estado!=5){
		doEditarCpl(idAnexo);
	}
	else{
		if(confirm('El Anexo Mod. pasar\u00E1 a estado Provisional, \u00BFDesea continuar?')){
			doEditarCpl(idAnexo);
		}
	}
}

function doEditarCpl(idAnexo){
	$("#methodCpl").val("doConsulta");
	$("#idAnexoCpl").val(idAnexo);
	$.blockUI.defaults.message = '<h4> Procesando petici&oacute;n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$("#main3").submit();
}

function pasarDefinitivo(idAnexo){
	
	var idcupon = null;
	var porFtp = true;
	var esCpl  = false;
	muestraCapaEspera ("Validando el A.M");
	validacionesPreviasEnvioAjax(idcupon,idAnexo,porFtp,esCpl);
}

function pasarDefinitivoCpl(idAnexo){
	
	// validar si tiene modificaciones el anexo cpl
	var idcupon = null;
	var porFtp = true;
	var esCpl  = true;
	muestraCapaEspera ("Validando el A.M");
	validacionesPreviasEnvioAjax(idcupon,idAnexo,porFtp,esCpl);
}

function ver(idAnexo){
    $("#method").val("doVisualiza");
	$("#id").val(idAnexo);
	$.blockUI.defaults.message = '<h4> Procesando petici&oacute;n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$("#main").submit();  
}

function verCpl(idAnexo){
    $("#methodCpl").val("doVisualiza");
	$("#idAnexoCpl").val(idAnexo);
	$.blockUI.defaults.message = '<h4> Procesando petici&oacute;n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$("#main3").submit();  
}

function eliminar(idAnexo) {
	if(confirm('\u00BFEst\u00E1 seguro de que desea eliminar el registro seleccionado?')) {
		$("#method").val("doBaja");
		$("#id").val(idAnexo);
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main").submit();
	}
}

function imprimir(idAnexo) {
	var frm = document.getElementById('print');
	frm.idAnexo.value = idAnexo;
	frm.action = frm.action + '?rand=' + Math.random();
	$('#print').attr('target', '_blank');
	$("#print").submit();
} 

function imprimirCpl(idAnexo) {
	var frm = document.getElementById('printCpl');
	frm.idAnexoCompl.value = idAnexo;
	frm.action = frm.action + '?rand=' + Math.random();
	$('#printCpl').attr('target', '_blank');
	$("#printCpl").submit();
}
/**
 * Para todos los cupones excepto los confirmado-aplicado
 * @param cuponId
 * @param idAnexo
 * @param referencia
 */
function imprimirSw(cuponId,idAnexo,referencia)	{
	var frm = document.getElementById('imprimirAnexo');
	frm.refPoliza.value = referencia;
	frm.idCuponImprimir.value = cuponId;
	frm.idImprimir.value = idAnexo;
	frm.methodImprimir.value = "doImprimirAnexoPpal";
	frm.action = frm.action + '?rand=' + Math.random();
	$('#imprimirAnexo').attr('target', '_blank');
	$("#imprimirAnexo").submit();
} 

/**
 * para cupones SW en estado confirmado-aplicado se imprime el pdfIncidencias (llamada al ws)
 * @param idCupon
 */
function imprimirSwPDFIncidencia(idCupon){
	var frm = document.getElementById('impresionIncidenciasMod');
	frm.method.value = "doImprimirPdf";
	frm.idCuponImpresion.value = idCupon;
	frm.action = frm.action + '?rand=' + Math.random();
	$('#impresionIncidenciasMod').attr('target', '_blank');
	$("#impresionIncidenciasMod").submit();
}

function imprimirCplSw(cuponId,idAnexo,referencia)	{
	var frm = document.getElementById('imprimirAnexo');
	frm.refPoliza.value = referencia;
	frm.idCuponImprimir.value = cuponId;
	frm.idImprimir.value = idAnexo;
	frm.methodImprimir.value = "doImprimirAnexoCpl";
	$('#imprimirAnexo').attr('target', '_blank');
	$("#imprimirAnexo").submit();
}

function verAcuseRecibo(idAnexo){	
	$("#method").val("doVerRecibo");
	$("#id").val(idAnexo);
	$("#main").submit();
}

function verPolizaActualizada(){
	$("#polizaActualizada").submit();
}

function verRelacionModificaciones(){
	var frm = document.getElementById('impresionIncidenciasMod');
	frm.method.value = "doImprimirIncidencias";
	$('#impresionIncidenciasMod').attr('target', '_self');
	$("#impresionIncidenciasMod").submit();
}

/**
 * Realiza la llamada al SW de Solicitud de Modificacion y muestra el popup informativo con los datos recibidos
 */
function altaAnexoSW () {
	
	// Muestra la capa informativa
	muestraCapaEspera ("Solicitando alta de A.M");
	
	var frm = document.getElementById('main');
	// Realiza la llamada al SW
	$.ajax({
		url:          UTIL.antiCacheRand("solicitudModificacion.html"),
		data:         "method=doSolicitudModificacion&referencia=" + frm.refPoliza.value + "&codPlan=" + $("#codplan").val(),
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			
			quitaCapaEspera ();
			alert("Error en la llamda al SW de Solicitud de Modificacion: " + quepaso);
		},
		success: function(resultado){
			quitaCapaEspera ();
			procesarRespuestaAlta (resultado);
		},
		type: "GET"
	});	
}

/**
 * Procesa la respuesta de la solicitud de modificacion
 * @param resultado
 */
function procesarRespuestaAlta (resultado) {
	
	// Si ha habido algun error en la llamada al SW
	if (resultado.error != '') {
		
		alert (resultado.error);
	}
	// Si la llamada ha sido correcta
	else {
		// Inserta los datos necesarios en el formulario
		$("#idCuponNum").val(resultado.id);
		$("#idCupon").val(resultado.idCupon); 
		$("#idCuponPpalPrevio").val(resultado.modifPpalCupon);
		$("#idCuponCplPrevio").val(resultado.modifCplCupon);
		$("#estadoCuponPpalPrevio").val(resultado.modifPpalIdEstado);
		$("#idEstadoPlzPpalAgroseguro").val(resultado.idEstadoPpal); 		
		$("#estadoPlzPpalAgroseguro").val(resultado.estadoPpal);
		
		// Rellena el mensaje de estado de contratacion con los datos recibidos del SW
		$("#estadoPpal").html(resultado.estadoPpal);
		// Si hay poliza complementaria
		if (resultado.estadoCpl != '') { 
			$("#tr_estadoCpl").show();
			$("#estadoCpl").html(resultado.estadoCpl);
			// Si hay complementaria se rellena el input del formulario correspondiente al id de estado de la cpl
			$("#idEstadoPlzCplAgroseguro").val(resultado.idEstadoCpl);
		}
		// Si hay modificaciones previas
		if (resultado.modifPpalCupon != '' || resultado.modifCplCupon != '') {
			$("#noModPrev").hide();
			$("#siModPrev").show();
			$("#tablaModPrev").show();
			
			if (resultado.modifPpalCupon != '') {
				$("#trModPrevPpal").show();
				$("#tdModPrevPpalCupon").html (resultado.modifPpalCupon);
				$("#tdModPrevPpalEstado").html (resultado.modifPpalEstado);
			}
		
			if (resultado.modifCplCupon != '') {
				$("#trModPrevCpl").show();
				$("#tdModPrevCplCupon").html (resultado.modifCplCupon);
				$("#tdModPrevCplEstado").html (resultado.modifCplEstado);
			}
		}
		// Si la poliza asociada es una renovable en estado 'Precartera generada', 'Precartera precalculada' o 'Primera comunicacion'
		// el mensaje en el popUp de 'Estado de la contratacion' sera 'Estado de la Renovacion en Agroseguro'. 
		// En cualquier otro estado sera 'Estado de la Poliza en Agroseguro'
		if ($("#idEstadoPlzAsociada").val() == 12 || $("#idEstadoPlzAsociada").val() == 18 || $("#idEstadoPlzAsociada").val() == 19) {
			$("#idMsgPoliza").html("Renovaci&oacute;n");
		}
		else {
			$("#idMsgPoliza").html("P&oacute;liza");
		}
		
		// Muestra el mensaje
		mostrarMsgContratacion();
	}
		
}


function continuarAltaAM () {
	
	// Oculta y limpia el mensaje de estado de contratacion
	ocultarMsgContratacion ();
	limpiarMsgContratacion ();
	
	// Si este hidden esta relleno significa que se esta renovando un cupon caducado
	// Hay que llamar al metodo de edicion del AM
	if ($('#idAnexoCaducado').val() != '') {
		doEditar($('#idAnexoCaducado').val());
	}
	else {
		// Comprueba si la poliza es principal o complementaria para llamar a la funcion correspondiente
		if ($('#tipoReferencia').val() == 'P') {
			alta(); 
		}
		else {
			altaCpl();
		}
	}
}

/**
 * Realiza la llamada al SW de Anulacion de Cupon
 */
function cancelarCupon () {
	
	// Oculta y limpia el mensaje de estado de contratacion
	ocultarMsgContratacion ();
	limpiarMsgContratacion ();
	
	// Muestra la capa informativa
	muestraCapaEspera ("Cancelando el cupon");
	
	// Realiza la llamada al SW
	$.ajax({
		url:          "solicitudModificacion.html?id=" + $("#idCuponNum").val() + "&idCupon=" + $("#idCupon").val(),
		data:         "method=doAnularCupon",
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			// Se borra el campo del anexo caducado para hacer posible que se de un alta nueva 
			$("#idAnexoCaducado").val('');
			
			quitaCapaEspera ();
			alert("Error en la llamda al SW de Anulacion de Cupon: " + quepaso);
		},
		success: function(resultado){
			// Se borra el campo del anexo caducado para hacer posible que se de un alta nueva 
			$("#idAnexoCaducado").val('');
			
			quitaCapaEspera ();
			$("#idCuponNum").val('');
			$("#idCupon").val('');
			procesarRespuestaAnulacion (resultado);
		},
		type: "GET"
	});	
}

/**
 * Realiza la llamal al SW de validacion previo a la confirmacion del cupon
 * @param idCupon
 */
function validarAMCupon (idCupon,idAnexo) {
	// Muestra la capa informativa
	muestraCapaEspera ("Validando el A.M");
	var esFtp = false;
	var esCpl = null;
	validacionesPreviasEnvioAjax(idCupon,idAnexo,esFtp,esCpl);
}


function validacionesPreviasEnvioAjax(idCupon,idAnexo,esFtp,esCpl){
	
	$.ajax({
            url: "validacionesAnexoAjax.html",
            data: "method=doValidacionesPreviasEnvio&idAnexo="+idAnexo,
            async:true,
            cache: false,
            beforeSend: function(objeto){
            },
            complete: function(objeto, exito){
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            error: function(objeto, quepaso, otroobj){
                alert("Error al comprobar la coherencia de los datos variables de las explotaciones: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){            	
            	if(datos.validacionesPreviasEnvio.valueOf() == "true"){
            		if (esFtp){
            			if (esCpl){
            				$("#methodCpl").val("doPasarDefinitiva");
            			    $("#idAnexoCpl").val(idAnexo);
            			    $("#main3").submit();
            			}else{
            				$("#method").val("doPasarDefinitiva");
            			    $("#id").val(idAnexo);
            			    $("#main").submit();
            			}
            		}else{
            			$("#idCuponValidar").val(idCupon);
            			$("#validarAnexo").submit();		
            		}
            	}else{
            		quitaCapaEspera ();
            		$('#panelInformacion').hide();
            		$('#panelAlertasValidacion').html(datos.mensaje);
            		$('#panelAlertasValidacion').show();
            	}
            	
            },
            type: "POST"
        });
}


/**
 * Procesa la respuesta de la anulacion del cupon
 * @param resultado
 */
function procesarRespuestaAnulacion (resultado) {
	alert (resultado.msg);
}

/**
 * Muestra el mensaje de estado de la contratacion
 */
function mostrarMsgContratacion (){
    $('#panelEstadoContratacion').show();
    $('#overlay').show();
} 

/**
 * Oculta el mensaje de estado de la contratacion
 */
function ocultarMsgContratacion (){
    $('#panelEstadoContratacion').hide();
    $('#overlay').hide();
}   

/**
 * Limpia y oculta los datos variables del mensaje de estado de la contratacion
 */
function limpiarMsgContratacion () {
	
	// Limpiar
	$("#estadoPpal").empty();
	$("#estadoCpl").empty();
	$("#tdModPrevPpalCupon").empty();
	$("#tdModPrevPpalEstado").empty();
	$("#tdModPrevCplCupon").empty();
	$("#tdModPrevCplEstado").empty();
	
	// Ocultar
	$("#tr_estadoCpl").hide();
    $("#siModPrev").hide();
    $("#tablaModPrev").hide();
    $("#trModPrevPpal").hide();
    $("#trModPrevCpl").hide();
}

/**
 * Realiza la llamada para redirigir a la pantalla de visualizacion de acuse de recibo de confirmacion
 * @param idAnexo
 */
function verAcuseConfirmacion (idAnexo, idPoliza, idCupon) {
	$("#idAnexoAcuse").val(idAnexo);
	$("#idPolizaAcuse").val(idPoliza);
	$("#idCuponAcuse").val(idCupon);
	$("#acuseReciboConfirmacion").submit();
}

/**
 * Muestra la capa de espera con el mensaje recibido como parametro
 * @param msg
 * @returns
 */
function muestraCapaEspera (msg) {
	$.blockUI.defaults.message = '<h4> ' + msg + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}

/**
 * Elimina la capa de espera
 */
function quitaCapaEspera () {
	$.unblockUI();
}

//P0079361
//function recuperarListaMotivos(){
//	//TODO 
//	//cambiar contenido de peticion AJAX al correspondiente
//	//debera pasar por un nuevo metodo doXXXAccion en Java que llame al Servicio Web de consulta de la contratacion
//	$.ajax({
//        url: "declaracionesModificacionPoliza.html",
//        data: "method=doComprobarAlta&codPlan="+$("#codplan").val() + "&codLinea="+$("#codlinea").val() + "&idPoliza="+idPoliza + "&idEstadoPlz="+idEstadoPlz,
//        async:true,
//        beforeSend: function(objeto){
//        },
//        cache: false,
//        complete: function(objeto, exito){
//        },
//        contentType: "application/x-www-form-urlencoded",
//        dataType: "json",
//        error: function(objeto, quepaso, otroobj){
//            alert("Error: " + quepaso);
//        },
//        global: true,
//        ifModified: false,
//        processData:true,
//        success: function(datos){
//        	if (datos.objeto == "sinGastos") {
//        		$('#panelAlertasValidacion').html("Los datos de mantenimiento para el plan/l\u00EDnea son incorrectos. Por favor, p\u00F3ngase en contacto con RGA");
//        		$('#panelAlertasValidacion').show();
//        	}
//        	else if(datos.objeto == "true"){
//        		redirigirAlta (isCpl, isCupon);
//        	}
//        	else if(datos.objeto == "false"){
//        		if(confirm('Est\u00E1 intentado dar de alta un anexo sobre una p\u00F3liza con un plan que no es el \u00FAltimo en contratacion. \u00BFEst\u00E1 seguro?')){
//        			redirigirAlta (isCpl, isCupon);
//        		}
//        	}
//        	else {
//        		$('#panelAlertasValidacion').html("Ha ocurrido un error en la validaciones previas al alta del anexo");
//        		$('#panelAlertasValidacion').show();
//        	}
//        },
//        type: "POST"
//    });
//}
//P0079361

