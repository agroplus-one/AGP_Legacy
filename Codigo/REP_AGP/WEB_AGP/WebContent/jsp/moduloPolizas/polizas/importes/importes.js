$(document).ready(
	function() {
		
		var aaSPol = document.getElementById("consulta");
		aaSPol.href = "seleccionPoliza.html?rand=" + UTIL.getRand();

		if ($('#displaypopUpAmbCont').val() == "true") {
			$('#overlay').show();
			$('#popUpAmbitoContratacion').show();
		}

		if ($('#muestraBotonDescuentos').val() == "true") {
			$('#btnDescuentos').show();
		}
		if ($('#muestraBotonRecargos').val() == "true") {
			$('#btnRecargos').show();
		}

		$('#btnGrDefinitiva').hide();
		$('#btnImprimir').hide();
		if ($('#btnImprimirReducida').length) $('#btnImprimirReducida').hide();
		$('#btnSalir').hide();

		if ($('#grProvisionalOK').val() == "true") {

			//$('#btnRevProdPrecio').hide();//ESC-13385 DNF 13/04/2021 OCULTO EL BOTON, ES ALGO PROVISIONAL, COMENTO SUS REFERENCIAS PARA EVITAR NULOS
			$('#btnVolver').show();
			$('#btnGrProvisional').hide();
			$('#btnGrDefinitiva').show();
			$('#btnImprimir').show();
			if ($('#btnImprimirReducida').length) $('#btnImprimirReducida').show();
			$('#btnSalir').hide();
			
		}

		if ($('#grDefinitivaOK').val() == "true") {
			$('#btnPagos').hide();
			//$('#btnRevProdPrecio').hide();//ESC-13385 DNF 13/04/2021 OCULTO EL BOTON, ES ALGO PROVISIONAL, COMENTO SUS REFERENCIAS PARA EVITAR NULOS
			$('#btnVolver').hide();
			$('#btnGrProvisional').show();
			$('#btnGrDefinitiva').hide();
			$('#btnImprimir').show();
			if ($('#btnImprimirReducida').length) $('#btnImprimirReducida').show();
			$('#btnSalir').show();
			$('#btnPagosLectura').hide();
		}

		if (($('#tieneSubvenciones').val() == "false")
				&& ($('#grProvisionalOK').val() != "true")
				&& ($('#grDefinitivaOK').val() != "true")
				&& ($('#pagosIncompletos').val() != "true")) {
			$('#panelInfo').show();
			$('#overlay').show();
		}

		if ($('#modoLectura').val() != 'modoLectura') {
			$('#btnPagosLectura').hide();
			if (($('#grProvisionalOK').val() != "true")
					&& ($('#grDefinitivaOK').val() != "true")) {
//						$('#btnVolver').show();
				//$('#btnRevProdPrecio').show();//ESC-13385 DNF 13/04/2021 OCULTO EL BOTON, ES ALGO PROVISIONAL, COMENTO SUS REFERENCIAS PARA EVITAR NULOS
				$('#btnGrProvisional').show();
			}
		} else {
			$("input[type=radio]").each(function() {
				$(this).attr('disabled', 'disabled');
			});

			// Mostrar botones
			$('#btnSalir').show();
			$('#btnImprimir').show();
			if ($('#btnImprimirReducida').length) $('#btnImprimirReducida').show();

			// el boton de C/C-oficina ya no aparece en importes sino en eleccion forma de pago
//					if ($('#mpPagoC').val() == "true") {
//						$('#btnPagosLectura').show();
//					}

			// Si viene vacio es porque no hay registro de pago
			if ($('#mpPagoC').val() != "" && $('#mpPagoM').val() != "") {
				$('#btnFormaPagoLectura').show();
			}
		

			// Ocultar botones					   				
			$('#btnPagos').hide();
			$('#btnGrabar').hide();
//					$('#btnVolver').hide();
			//$('#btnRevProdPrecio').hide();//ESC-13385 DNF 13/04/2021 OCULTO EL BOTON, ES ALGO PROVISIONAL, COMENTO SUS REFERENCIAS PARA EVITAR NULOS
			$('#btnGrProvisional').hide();
			$('#btnGrDefinitiva').hide();
			$('#btnImprimirComp').hide();
		}


		if ($('#mpPagoC').val() == "true"
				&& $('#modoLectura').val() != 'modoLectura') {
			$('#btnPagos').show();
		}
});
	
function agregarCeroAlMes(mes){
	mes < 10 ? "0" + mes : mes;
}

/*
 * @param: serverTime: hora actual del servidor
 * @param: popUp: nombre del popUp
 */
function ajaxHandler_showPopUpAvisoPasoDefinitiva(serverTime) {
	var MENSAJE_ENVIO_MANHANA = "Env\u00EDo fuera de plazo, la p\u00F3liza se enviara en el d\u00EDa de ma\u00F1ana.";
	var MENSAJE_ENVIO_HOY = "Env\u00EDo dentro de plazo, la p\u00F3liza se enviara en el d\u00EDa hoy.";
	var LIMIT_TIME_ENVIO_POLIZA = "16:40:00";

	if (CompararHoras(serverTime, LIMIT_TIME_ENVIO_POLIZA)) {
		showPopUpAviso(MENSAJE_ENVIO_MANHANA);
	} else {
		showPopUpAviso(MENSAJE_ENVIO_HOY);
	}
}

function closePopUpAmbitoCont() {
	$('#popUpAmbitoContratacion').hide();
	$('#overlay').hide();
}

function aceptarPopUpPasoDefinitiva() {
	hidePopUpAviso();
	grabarDef();
}

function ajax_check_Cpl_Sbp(plan) {
	var frm = document.getElementById('main');
	var id = frm.idpoliza.value;
	$.ajax({
		url : "validacionesUtilidades.html",
		data : "method=do_Ajax_check_Cpl_Sbp&idPoliza=" + id,
		async : true,
		contentType : "application/x-www-form-urlencoded",
		dataType : "text",
		global : false,
		ifModified : false,
		processData : true,
		error : function(objeto, quepaso, otroobj) {
			alert("Error al solicitar la hora del servidor: "
					+ quepaso);
		},
		success : function(datos) {
			//alert(datos);
			if (datos == 'true') { //tiene Cpl
				if (confirm('La p\u00F3liza de Sobreprecio asociada a esta p\u00F3liza va a ser actualizada, \u00BFDesea Continuar?')) {
					$('#actualizarSbp').val("true");
					//showPopUpAvisoPasoDefinitiva();
					aceptarPopUpPasoDefinitiva('popUpPasarDefinIconRow');
				}
			} else { // no tiene Cpl
				//showPopUpAvisoPasoDefinitiva()
				aceptarPopUpPasoDefinitiva('popUpPasarDefinIconRow');							
			}
		},
		type : "GET"
	});
}
	
	
	

/*
 * @param: popUp: nombre del popUp
 */
function showPopUpAvisoPasoDefinitiva() {
	$.ajax({
		url : "ajaxCommon.html",
		data : "operacion=ajax_getServerTime",
		async : true,
		contentType : "application/x-www-form-urlencoded",
		dataType : "text",
		global : true,
		ifModified : false,
		processData : true,
		error : function(objeto, quepaso, otroobj) {
			alert("Error al solicitar la hora del servidor: " + quepaso);
		},
		success : function(datos) {
			ajaxHandler_showPopUpAvisoPasoDefinitiva(datos)
		},
		type : "GET"
	});
}

/**
 * compare time1 to time2
 * @return true if time1 > time2
 */
function CompararHoras(time1, time2) {
	var result = false;
	var auxTime1 = time1.split(":");
	var auxTime2 = time2.split(":");
	var horas1 = parseInt(auxTime1[0], 10);
	var minutos1 = parseInt(auxTime1[1], 10);
	var horas2 = parseInt(auxTime2[0], 10);
	var minutos2 = parseInt(auxTime2[1], 10);

	// Comparar
	if (horas1 > horas2 || (horas1 == horas2 && minutos1 > minutos2)) {
		result = true;
	} else {
		result = false;
	}
	return result;
}

/*
 *  show popup
 */
function showPopUpAviso(mensaje) {
	$('#txt_mensaje_aviso_1').html(mensaje);
	$('#overlay').show();
	$('#popUpAvisos').show();
}

/**
 * hide popup
 */
function hidePopUpAviso() {
	$('#popUpAvisos').hide();
	$('#overlay').hide();
}

function grabarDef() {
	//var frm = document.getElementById('grDefinitiva');
	//frm.operacion.value = 'grabardefinitiva';
	//validamos si ha elegido una de las dos opciones de pago y si la cuenta pertenece a la entidad
	if (!validaFormaPagoAjax()) {
		$('#panelAlertasValidacion').show();
	} else {
		// MPM 03/05/12
		var frm = document.getElementById('pasarADefinitiva');
		frm.method.value = 'doPasarADefinitiva';
		frm.resultadoValidacion.value = "true";
		frm.target = "_self";
		$.blockUI.defaults.message = '<h4>Pasando la p\u00F3liza a definitiva.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({
			overlayCSS : {
				backgroundColor : '#525583'
			}
		});
		frm.submit();
	}
}

function validaFormaPagoAjax() {
	var frm = document.getElementById('pasarADefinitiva');
	var idpoliza = frm.idpoliza.value;
	var valida = false;
	$.ajax({
				url : "pagoPoliza.html",
				data : "operacion=validaFormaPagoAjax&idpoliza=" + idpoliza
						+ "&",
				async : false,
				contentType : "application/x-www-form-urlencoded",
				dataType : "json",
				global : false,
				ifModified : false,
				processData : true,
				cache : false,
				error : function(objeto, quepaso, otroobj) {

					alert("Error al guardar los datos: " + quepaso);
				},
				success : function(datos) {

					if (datos.mensaje == "errorTipoPago") {
						$('#panelAlertasValidacion')
								.html(
										"Debe seleccionar Forma de Pago para pasar a Definitiva");
						valida = false;
					} else if (datos.mensaje == "errorCuenta") {
						$('#panelAlertasValidacion').html(
								"La E-S Mediadora de la p\u00F3liza no permite el cargo en cuenta a la entidad seleccionada");
						valida = false;
					} else if (datos.mensaje == "errorManual") {
						$('#panelAlertasValidacion').html(
								"Falta la informaci\u00F3n de Pago Manual");
						valida = false;
					} else {
						$('#panelAlertasValidacion').html("");
						valida = true;
					}
				},
				type : "GET"
			});
	return valida;
}

function grabarDefFueraContratacion() {
	closePopUpAmbitoCont();
	//var frm = document.getElementById('grDefinitiva');
	//frm.operacion.value = 'grabardefinitiva';
	// MPM 03/05/12
	var frm = document.getElementById('pasarADefinitiva');
	frm.method.value = 'doPasarADefinitiva';
	frm.grabFueraContratacion.value = 'grabarFueraContratacion'
	frm.target = "_self";
	$.blockUI.defaults.message = '<h4>Pasando la p\u00F3liza a definitiva.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({
		overlayCSS : {
			backgroundColor : '#525583'
		}
	});
	frm.submit();
}

// Reemplaza todas las ocurrencias de oldValue por newValue en cadena
function replaceAll(cadena, oldValue, newValue) {

	if (cadena == null)
		return "";

	while (cadena.search(oldValue) > -1) {
		cadena = cadena.replace(oldValue, newValue);
	}

	return cadena;

}

// Monta la url a la que se llamara en la funcion ajax
function getUrlGuardarDistCoste() {
	var frm = document.getElementById('grProvisional');

	url = "&operacion=grabarDistCoste";
	url += "&idpoliza=" + frm.idpoliza.value;
	url += "&modSeleccionado="
			+ replaceAll(frm.modSeleccionado.value, '%', '_');
	url += "&noRevPrecioProduccion=" + frm.noRevPrecioProduccion.value;
	url += "&importeSeleccionado=" + frm.importeSeleccionado.value;
	url += "&idEnvio=" + frm.idEnvio.value;
	
	url += "&netoTomadorFinanciadoAgr=" + frm.netoTomadorFinanciadoAgr.value;
	url += "&enviarIBANFinanciadoAgr=" + frm.enviarIBANFinanciadoAgr.value;
	url += "&totalCosteTomadorAFinanciar=" + frm.totalCosteTomadorAFinanciar_grP.value;
	url += "&rand="
			+ encodeURI(parseInt(Math.random() * 99999999) + "_"
					+ (new Date).getTime());
	url += "&selectedSumaAseg=" + frm.selectedSumaAseg.value;
	url += "&fechaEfectoRC=" + frm.fechaEfectoRC.value;
	
	return url;
}

function muestraCapaEspera (msg) {
	//$.blockUI.defaults.message = '<h4> Grabando los importes1.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	//$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	
}

// Realiza la llamada ajax para guardar la distribucion de costes de la poliza
function ajaxGuardarDistCoste(redireccion, callback) {
	//muestraCapaEspera ("Grabando");
	$.ajax({
				url : "webservices.html",
				data : getUrlGuardarDistCoste(),
				async : false,
				contentType : "application/x-www-form-urlencoded",
				dataType : "json",
				global : false,
				ifModified : false,
				processData : true,
				error : function(objeto, quepaso, otroobj) {
					$.unblockUI();
					alert("Error al guardar la distribuci\u00F3n de costes de la p\u00F3liza: "
							+ quepaso);
				},
				success : function(resultado) {
					// Si el resultado es diferente de null ha habido algun error en el guardado
					if (resultado != null) {
						$.unblockUI();
					}
					// Si el guardado ha sido correcto se redirecciona
					else {
						$.unblockUI();
						if(redireccion != 'continuar'){
							document.getElementById(redireccion).submit();
						} else {
							callback();
						}
					}
				},
				type : "GET"
			});
}

/*PET.70105.FII DNF 22/02/2021 boton continuar*/
function continuarPpal(plan,checkRevision, formulario, campo, idpoliza, importeFin) {
	if(importeFin != null){
		$('#netoTomadorFinanciadoAgr').val(importeFin);
	}
	if ($('#esAgr').val() == 'false'){
		$('#esAgrSend').val($('#esAgr').val());
		$('#esSaecaVal').val(importeFin);
	}
	
	var hiddens = document.importes.idEnvioComp;
	var radios = document.importes.modElegido;
	var seleccionado = false;
	for ( var i = 0; i < radios.length; i++) {
		
		
		if (radios[i].checked == true) {
			
			document.grabar.modSeleccionado.value = radios[i].value;
			document.grabar.idEnvio.value = document.importes.elements['idEnv' + (i)].value;
			document.grabar.importeSeleccionado.value = document.importes.elements['importeC' + (i)].value;
			document.irAPagos.modSeleccionado.value = radios[i].value;
			document.irAPagos.idEnvio.value = document.importes.elements['idEnv'+ (i)].value;
			document.irAPagos.importeSeleccionado.value = document.importes.elements['importeC'	+ (i)].value;
			document.grProvisional.modSeleccionado.value = radios[i].value;
			document.grProvisional.idEnvio.value = document.importes.elements['idEnv'+ (i)].value;
			document.grProvisional.importeSeleccionado.value = document.importes.elements['importeC' + (i)].value;
			
			// MPM - Se controla que, cuando una poliza no se pueda financiar, el valor de 'totalCosteTomadorAFinanciar' + (i) sea nulo
			try {
				document.grProvisional.totalCosteTomadorAFinanciar_grP.value= document.importes.elements['totalCosteTomadorAFinanciar' + (i)].value;
			}
			catch (exc) {
				document.grProvisional.totalCosteTomadorAFinanciar_grP.value= '';
			}
			
			if ($('#esAgr').val() == 'true'){
				document.grProvisional.netoTomadorFinanciadoAgr.value =document.importes.elements['netoTomadorFinanciadoAgr'+ (i)].value;;
				document.grProvisional.enviarIBANFinanciadoAgr.value =document.importes.elements['enviarIBANFinanciadoAgr'+ (i)].value;;
			}
			
			seleccionado = true;
			break;
		}
	}
		
	// Si no se ha seleccionado ningun modulo
	if (!seleccionado) {
		alert("Debe seleccionar un m\u00F3dulo para continuar...");
	} else {
		
		
		if (validaRCGanado()) {
			
			if (checkRevision == 'noRevision') {
				document.grabar.noRevPrecioProduccion.value = 'true';
			}
			
			if (formulario == 'grProvisional' || formulario == 'grabar' || formulario == 'continuar') {
				// Se lanza la llamada ajax para guardar la distribucion de costes
				ajaxGuardarDistCoste(formulario, function() { showPopUpAval(idpoliza, plan); });
			} else {
				showPopUpAval(idpoliza, plan);
			}
		}		
	}
}
/* fin PET.70105.FII DNF 22/02/2021 boton continuar*/


function getModuleAndSave(checkRevision, formulario, campo, idpoliza, importeFin) {
	if(importeFin != null){
		$('#netoTomadorFinanciadoAgr').val(importeFin);
	}
	if ($('#esAgr').val() == 'false'){
		$('#esAgrSend').val($('#esAgr').val());
		$('#esSaecaVal').val(importeFin);
	}
	
	var hiddens = document.importes.idEnvioComp;
	var radios = document.importes.modElegido;
	var seleccionado = false;
	for ( var i = 0; i < radios.length; i++) {
		
		
		if (radios[i].checked == true) {
			
			document.grabar.modSeleccionado.value = radios[i].value;
			document.grabar.idEnvio.value = document.importes.elements['idEnv' + (i)].value;
			document.grabar.importeSeleccionado.value = document.importes.elements['importeC' + (i)].value;
			document.irAPagos.modSeleccionado.value = radios[i].value;
			document.irAPagos.idEnvio.value = document.importes.elements['idEnv'+ (i)].value;
			document.irAPagos.importeSeleccionado.value = document.importes.elements['importeC'	+ (i)].value;
			document.grProvisional.modSeleccionado.value = radios[i].value;
			document.grProvisional.idEnvio.value = document.importes.elements['idEnv'+ (i)].value;
			document.grProvisional.importeSeleccionado.value = document.importes.elements['importeC' + (i)].value;
			
			// MPM - Se controla que, cuando una poliza no se pueda financiar, el valor de 'totalCosteTomadorAFinanciar' + (i) sea nulo
			try {
				document.grProvisional.totalCosteTomadorAFinanciar_grP.value= document.importes.elements['totalCosteTomadorAFinanciar' + (i)].value;
			}
			catch (exc) {
				document.grProvisional.totalCosteTomadorAFinanciar_grP.value= '';
			}
			
			if ($('#esAgr').val() == 'true'){
				document.grProvisional.netoTomadorFinanciadoAgr.value =document.importes.elements['netoTomadorFinanciadoAgr'+ (i)].value;;
				document.grProvisional.enviarIBANFinanciadoAgr.value =document.importes.elements['enviarIBANFinanciadoAgr'+ (i)].value;;
			}
			
			seleccionado = true;
			break;
		}
	}
		
	// Si no se ha seleccionado ningun modulo
	if (!seleccionado) {
		alert("Debe seleccionar un m\u00F3dulo para continuar...");
	} else {
		
		if (validaRCGanado()) {
		
			//$.blockUI.defaults.message = '<h4> Grabando los importes2.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			//$.blockUI({
			//	overlayCSS : {
			//		backgroundColor : '#525583'
			//	}
			//});
			if (checkRevision == 'noRevision') {
				document.grabar.noRevPrecioProduccion.value = 'true';
			}
			
			if (formulario == 'grProvisional' || formulario == 'grabar') {
				// Se lanza la llamada ajax para guardar la distribucion de costes
				ajaxGuardarDistCoste(formulario);
			}
			// Cualquier otra accion
			else {
				generales.enviarForm(formulario, campo, idpoliza);
			}
		}
	}
}

function formatearFecha(fecha){
	var dia = fecha.getDate();
	var mes = fecha.getMonth() + 1;
	var ano = fecha.getFullYear();
	if(mes < 10){
		mes = "0" + mes;
	}
	return dia + "/" + mes + "/" + ano;
}

function validarFechaEfectoRC(){
	var fechaValida = true;
	var calcularRC = $('#selectedSumaAseg').val() != -1;
	var hayFecha = $('#fechaEfectoId').val().length > 0;
	if(!hayFecha){
		alert('La Fecha de Efecto no puede estar en blanco, es obligatoria');
		fechaValida = false;
	}
	if(hayFecha && calcularRC){
		var hoy = new Date();
		hoy.setHours(0,0,0,0);
		var hoyMas11Meses = hoyMasOnceMeses();
		var fechaEfecto = transformarFechaEfecto($('#fechaEfectoId').val())
		if(fechaEfecto.getTime() > hoyMas11Meses){
			alert('La Fecha de Efecto es superior a once meses contando desde hoy');
			fechaValida = false;
		} else if (fechaEfecto.getTime() < hoy.getTime()) {
			alert('La Fecha de Efecto no puede ser anterior al d\u00EDa de hoy');
			fechaValida = false;
		}else {
			var fechaFinal = formatearFecha(fechaEfecto)
			$('#fechaEfectoRC').val(fechaFinal);
		}
	}
	return fechaValida;
}

function hoyMasOnceMeses(){
	var hoy = new Date();
	hoy.setHours(0,0,0,0);
	var hoyMas11Meses = hoy.setMonth(hoy.getMonth() + 11);
	return hoyMas11Meses;
}

function transformarFechaEfecto(fechaEfectoStr){
	var partesFecha = fechaEfectoStr.split("/");
	return new Date(partesFecha[2], partesFecha[1] - 1, partesFecha[0]);
}

function validaRCGanado() {
	var result = true;
	if ($('#radioSumaAseg') && $('#radioSumaAseg').length > 0) {
		if(!$('input[name=radioSumaAseg]:checked').val()) {
			alert('Debe seleccionar una simulaci\u00F3n para la RC de Ganado para continuar...');
			result = false;
		} else {
			$('#selectedSumaAseg').val($('input[name=radioSumaAseg]:checked').val());
			result = validarFechaEfectoRC();
		}
	}
	return result;
}

function salirModoLectura() {
	$(window.location).attr('href', 'seleccionPoliza.html');
}

function salir() {

	var frm = document.getElementById('pasarADefinitiva');
	if ($('#modoLectura').val() == 'modoLectura') {
		if ($('#vieneDeUtilidades').val() == 'true') {
			var URL = UTIL
					.antiCacheRand('utilidadesPoliza.html?operacion=volver');
		} else {
			var URL = UTIL
					.antiCacheRand('seleccionPoliza.html?operacion=volver');
		}
		$("#pasarADefinitiva").attr("action", URL);
	} else {

		frm.method.value = 'doSalir';
		//frm.grabacionDefinitiva.value = '';
		//frm.grDefinitivaOK.value = '';
	}
	frm.target = "_self";
	frm.submit();
}

function cerrarPopUp() {
	$('#panelInfo').hide();
	$('#overlay').hide();
}

function imprimir() {
	var frm = document.getElementById('pasarADefinitiva');
	frm.method.value = 'doImprimirPoliza';
	frm.imprimirReducida.value = 'false';
	frm.target = "_blank";
	frm.submit();
}

function imprimirReducida() {
	var frm = document.getElementById('pasarADefinitiva');
	frm.method.value = 'doImprimirPoliza';
	frm.imprimirReducida.value = 'true';
	frm.target = "_blank";
	frm.submit();
}

function imprimirComparativas() {
	var frm = document.getElementById('importes');
	frm.method.value = 'doInformeComparativas';
	frm.target = "_blank";
	frm.action = "informes.html";
	frm.submit();
}
	

	
function showdata(id) {
	
	var elementosFieldset=document.getElementsByTagName('fieldset');
	var campos=[];
	for(var i=0; i< elementosFieldset.length; i++) {
		var elemento=elementosFieldset[i];
		var name=elemento.getAttribute('name');			
		if(name!=null && name.indexOf(id)>-1){
			campos.push(elementosFieldset[i]);
		}			 
	}
	for(var i=0; i< campos.length; i++) {
		if (campos[i].style.display == '') {
			campos[i].style.display = 'none';
			$('#btnDesglose' + id).text("Mostrar Desglose");
		} else {
			campos[i].style.display = '';				
			$('#btnDesglose' + id).text("Ocultar Desglose");
		}
	}

}

function eligeMenu() {
	if ($('#vieneDeUtilidades').val() == 'true') {
		SwitchMenu('sub4');
	} else {
		SwitchMenu('sub3');
	}
}
function datosPago() {
	$('#consultaDetallePoliza').submit();

}

function financiar(importeTomador, financiacionSeleccionada, pctMinFinanc) {

		var codModulo = $.trim(financiacionSeleccionada.split("|")[1]);
		var frm = document.getElementById('main');
		var idPoliza = frm.idpoliza.value;
		cargarListaCondicionesFraccAjax(idPoliza, codModulo);
		
		var frm = document.getElementById('frmCalculoFinanciacion');
		frm.costeTomador_cf.value = importeTomador;		
		frm.pctMinFinanc_cf.value=pctMinFinanc;
		
		frm.financiacionSeleccionada_cf.value = financiacionSeleccionada;
		if ($("#porcentajeCheck").is(':checked') == false && $("#importeCheck").is(':checked') == false && $("#importeAvalCheck").is(':checked')== false){
			$('#porcentajeCheck').attr('checked', 'checked');
			$('#porcentajeCosteTomador_txt').removeAttr('disabled');
			$('#importeFinanciar_txt').attr('disabled', 'disabled');
			$('#importeAval_txt').attr('disabled', 'disabled');
		}
		$('#costeTomador_lb').html(importeTomador);
		$('#totalCosteTomadorAFinanciar_cf').val(importeTomador);
		$('#panelCalculofinanciacion').show();
		$('#overlay').show();
	}
	
//El parametro "importeTomador" tomara el valor del costeTotalTomador cuando este no sea nulo o vacio
function financiarLectura(importeTomador, financiacionSeleccionada, periodoFracc, valorOpcionFracc, opcionFracc) {
	$('#costeTomador_lb').html(importeTomador);
	$('#condicionesFraccionamiento').val(periodoFracc);
	
	switch (parseInt(opcionFracc)) {
	    case 0:
	    	$('#porcentajeCheck').attr('checked', 'checked');
			$('#porcentajeCosteTomador_txt').val(valorOpcionFracc);
	        break;
	    case 1:
	    	$('#importeCheck').attr('checked', 'checked');
			$('#importeFinanciar_txt').val(valorOpcionFracc);
			break;
	    case 2:
	    	$('#importeAvalCheck').attr('checked', 'checked');
			$('#importeAval_txt').val(valorOpcionFracc);
			break;
	}

	/* $('#panelInformacion').find("input, select").attr('disabled', 'disabled'); */
	$('#condicionesFraccionamiento').attr('disabled', 'disabled');
	$('#porcentajeCheck').attr('disabled', 'disabled');
	$('#importeCheck').attr('disabled', 'disabled');
	$('#importeAvalCheck').attr('disabled', 'disabled');
	$('#porcentajeCosteTomador_txt').attr('disabled', 'disabled');
	$('#importeFinanciar_txt').attr('disabled', 'disabled');
	$('#importeAval_txt').attr('disabled', 'disabled');
	$('#btnCalcular_da').hide();
	$('#panelCalculofinanciacion').show();
	$('#overlay').show();
}
	
function showPopUpAval(idpoliza, plan) {	
	if(plan<2015){
		ajax_check_Cpl_Sbp();	
	}else{
		if ($('#vieneDeUtilidades').val() != 'true') {
			document.getElementById('frmDatosAval').cicloPoliza.value="cicloPoliza";
		}
		
		ajax_muestraDatosAval(idpoliza);
	}
}
	
function cargarListaCondicionesFraccAjax(idPoliza, codModulo){
	$.ajax({
        url: "validacionesUtilidades.html",
        data: "method=doCargarListaCondicionesFraccAjax&idPoliza="+idPoliza+"&codModulo="+codModulo,
		async: false,
	    dataType: "json",
	    cache: false,
	    error: function(objeto, quepaso, otroobj){
            alert("Error al cargar la lista de condiciones de fraccionamiento: " + quepaso);
        },
        success: function(datos){
        	rellenarSelectCondicionesFraccAjax(datos.listaCondicionesFracc);
        },
        type: "GET"
	    
	});
}

function rellenarSelectCondicionesFraccAjax(listaCondicionesFracc){
	//Primero lo vacio
	$('#condicionesFraccionamiento').find('option').remove();
	
	if(listaCondicionesFracc!=null){
		for(var i in listaCondicionesFracc){
			
			var elemento = listaCondicionesFracc[i];
			var options = $('#condicionesFraccionamiento').attr('options'); 
			options[options.length] = new Option(elemento, elemento);
		}
	}else{
		//
	}
}

function showFormaPago(){
	var frm = document.getElementById("frmDatosAval");
	frm.method.value = "doMostrar";
	frm.action="eleccionFormaPago.html";
	frm.submit();
}

function descargarCondicionesRC(){
	$('#imprimirCondicionesRC').attr('target', '_blank');
	$('#imprimirCondicionesRC').submit();
}

function imprimirPolizaRC(){
	$('#imprimirPolizaRC').attr('target', '_blank');
	$('#imprimirPolizaRC').submit();
}

/* ESC-15883 ** MODIF TAM (16.11.2021) ** Inicio */
/* Comprobamos primero si hay xml que devolver, en caso de que haya se lanza llamada
 * y en caso contrario se muestra mensaje de error por pantalla
 */
function descargarXmlCalculo(filaComparativa, islineaGanado, idPoliza) {
	var frm = document.getElementById('formUtilidades');
	var tipo = "CALC";
	if(islineaGanado == "true") {
		frm.filaComparativa.value = $.trim(filaComparativa.split("|")[0]);
	} else {
		frm.filaComparativa.value = $.trim(filaComparativa.split("|")[7]);
	}	
	
	if (!validaXmlAjax(filaComparativa, islineaGanado,idPoliza, tipo)) {
		$('#panelAlertasValidacion').html("No se ha encontrado XML de C\u00E1lculo");
		$('#panelAlertasValidacion').show();
	} else {
		$('#panelAlertasValidacion').hide();
		frm.method.value = 'doGetXMLCalculo';
		frm.submit();
	}
}

function descargarXmlValidacion(filaComparativa, islineaGanado, idPoliza) {
	var frm = document.getElementById('formUtilidades');
	var tipo = "VAL";

	
	if(islineaGanado == "true") {
		frm.filaComparativa.value = $.trim(filaComparativa.split("|")[0]);
	} else {
		frm.filaComparativa.value = $.trim(filaComparativa.split("|")[7]);
	}
	/* ESC-15883 ** MODIF TAM (16.11.2021) ** Inicio */
	/* Comprobamos primero si hay xml que devolver, en caso de que haya se lanza llamada
	 * y en caso contrario se muestra mensaje de error por pantalla
	 */
	if (!validaXmlAjax(filaComparativa, islineaGanado, idPoliza, tipo)) {
		$('#panelAlertasValidacion').html("No se ha encontrado XML de Validacion");
		$('#panelAlertasValidacion').show();
		
	} else {
		$('#panelAlertasValidacion').hide();
		frm.method.value = 'doGetXMLValidacion';
		frm.submit();
	}
	/* ESC-15883 ** MODIF TAM (16.11.2021) ** Fin */
}

function validaFormaPagoAjax() {
	var frm = document.getElementById('pasarADefinitiva');
	var idpoliza = frm.idpoliza.value;
	var valida = false;
	$.ajax({
				url : "pagoPoliza.html",
				data : "operacion=validaFormaPagoAjax&idpoliza=" + idpoliza
						+ "&",
				async : false,
				contentType : "application/x-www-form-urlencoded",
				dataType : "json",
				global : false,
				ifModified : false,
				processData : true,
				cache : false,
				error : function(objeto, quepaso, otroobj) {

					alert("Error al guardar los datos: " + quepaso);
				},
				success : function(datos) {

					if (datos.mensaje == "errorTipoPago") {
						$('#panelAlertasValidacion')
								.html(
										"Debe seleccionar Forma de Pago para pasar a Definitiva");
						valida = false;
					} else if (datos.mensaje == "errorCuenta") {
						$('#panelAlertasValidacion').html(
								"La E-S Mediadora de la p\u00F3liza no permite el cargo en cuenta a la entidad seleccionada");
						valida = false;
					} else if (datos.mensaje == "errorManual") {
						$('#panelAlertasValidacion').html(
								"Falta la informaci\u00F3n de Pago Manual");
						valida = false;
					} else {
						$('#panelAlertasValidacion').html("");
						valida = true;
					}
				},
				type : "GET"
			});
	return valida;
}

/* ESC-15883 ** MODIF TAM (16.11.2021) ** Inicio */
/* Nueva Funcion para comprobar si hay xml para devolver antes de mostrar el xml */
function validaXmlAjax(filaComparativa, islineaGanado, idPoliza, tipo){
	var valida = false;
	if(islineaGanado == "true") {
		filaComparativa = $.trim(filaComparativa.split("|")[0]);
	} else {
		filaComparativa = $.trim(filaComparativa.split("|")[7]);
	}	
	
	$.ajax({
        url: "utilidadesXML.run",
        data: "method=doValidarXMLAjax&idPoliza="+idPoliza+"&filaComparativa="+filaComparativa+"&valor="+tipo,
        async:        false,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
		},
		success: function(resultado){
			if (resultado.alert != null && resultado.alert != ""){
				valida = false;
			}else{
				valida = true;
			}
		},
		beforeSend : function() {
		},
		type: "GET"
	});
	
	return valida;		
}
	