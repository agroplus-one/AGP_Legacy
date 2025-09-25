var idPolizaAccion;

$(document).ready(function(){
	$.ajaxSetup({ cache: false });
	
	$("#grid").displayTagAjax();
	
    var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
    var URL = UTIL.antiCacheRand(document.getElementById("print").action);
    document.getElementById("print").action = URL;
    document.getElementById("entidad").focus();    
    
   	check_checks($('#idsRowsChecked').val());

	if($('#displaypopUpAmbCont').val() == "true"){
	    $('#overlay').show();
	    $('#popUpAmbitoContratacion').show();
	}
	
    
    
    Zapatec.Calendar.setup({
		firstDay : 1,
		weekNumbers : false,
		showOthers : true,
		showsTime : false,
		timeFormat : "24",
		step : 2,
		range : [ 1900.01, 2999.12 ],
		electric : false,
		singleClick : true,
		inputField : "fechaEnvioDesdeId",
		button : "btn_fechaEnvioDesde",
		ifFormat : "%d/%m/%Y",
		daFormat : "%d/%m/%Y",
		align : "Br"
	});
	
	Zapatec.Calendar.setup({
		firstDay : 1,
		weekNumbers : false,
		showOthers : true,
		showsTime : false,
		timeFormat : "24",
		step : 2,
		range : [ 1900.01, 2999.12 ],
		electric : false,
		singleClick : true,
		inputField : "fechaEnvioHastaId",
		button : "btn_fechaEnvioHasta",
		ifFormat : "%d/%m/%Y",
		daFormat : "%d/%m/%Y",
		align : "Br"
	});
	
	Zapatec.Calendar.setup({
		firstDay : 1,
		weekNumbers : false,
		showOthers : true,
		showsTime : false,
		timeFormat : "24",
		step : 2,
		range : [ 1900.01, 2999.12 ],
		electric : false,
		singleClick : true,
		inputField : "fechaVigorDesdeId",
		button : "btn_fechaVigorDesde",
		ifFormat : "%d/%m/%Y",
		daFormat : "%d/%m/%Y",
		align : "Br"
	});
	
	Zapatec.Calendar.setup({
		firstDay : 1,
		weekNumbers : false,
		showOthers : true,
		showsTime : false,
		timeFormat : "24",
		step : 2,
		range : [ 1900.01, 2999.12 ],
		electric : false,
		singleClick : true,
		inputField : "fechaVigorHastaId",
		button : "btn_fechaVigorHasta",
		ifFormat : "%d/%m/%Y",
		daFormat : "%d/%m/%Y",
		align : "Br"
	});

	Zapatec.Calendar.setup({
		firstDay : 1,
		weekNumbers : false,
		showOthers : true,
		showsTime : false,
		timeFormat : "24",
		step : 2,
		range : [ 1900.01, 2999.12 ],
		electric : false,
		singleClick : true,
		inputField : "fechaPagoDesdeId",
		button : "btn_fechaPagoDesde",
		ifFormat : "%d/%m/%Y",
		daFormat : "%d/%m/%Y",
		align : "Br"
	});
	
	Zapatec.Calendar.setup({
		firstDay : 1,
		weekNumbers : false,
		showOthers : true,
		showsTime : false,
		timeFormat : "24",
		step : 2,
		range : [ 1900.01, 2999.12 ],
		electric : false,
		singleClick : true,
		inputField : "fechaPagoHastaId",
		button : "btn_fechaPagoHasta",
		ifFormat : "%d/%m/%Y",
		daFormat : "%d/%m/%Y",
		align : "Br"
	});
    
	$('#main3').validate({					
	
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		
		rules: {
			"colectivo.tomador.id.codentidad": {grupoEnt: true},
		 	"oficina": {digits: true},
		 	"oficina": {grupoOfic: true},
		 	"fechaEnvioHasta":{validaFechaEnvioHasta:['fechaEnvioDesdeId','fechaEnvioHastaId']},
		 	"fechaVigorHasta":{validaFechaVigorHasta:['fechaVigorDesdeId','fechaVigorHastaId']},
		 	"fechaPagoHasta":{validaFechaPagoHasta:['fechaPagoDesdeId','fechaPagoHastaId']},
		 	"idpoliza": {digits:true}
		},
		messages: {
			"colectivo.tomador.id.codentidad": { grupoEnt: "La Entidad seleccionada no pertenece al grupo de Entidades del usuario"},
		 	"oficina": { digits: "El campo oficina debe contener s\u00F3lo n\u00FAmeros"},
			"oficina": {grupoOfic: "La oficina seleccionada no pertenece al grupo de oficinas del usuario"},
			"fechaEnvioHasta":{validaFechaEnvioHasta:"La Fecha Envio Hasta no puede ser anterior a la Fecha Env\u00EDo Desde"},
			"fechaVigorHasta":{validaFechaVigorHasta:"La Fecha Vigor Hasta no puede ser anterior a la Fecha Vigor Desde"},
			"fechaPagoHasta":{validaFechaPagoHasta:"La Fecha Pago Hasta no puede ser anterior a la Fecha Pago Desde"},
			"idpoliza":{ digits: "El campo N\u00BA Sol. debe contener s\u00F3lo n\u00FAmeros"}
		}
	});
	
	// comprueba que la fecha Envio Hasta sea posterior a la primera
	jQuery.validator.addMethod("validaFechaEnvioHasta", function(value, element, params) {
		if(document.getElementById(params[1]).value != null && document.getElementById(params[1]).value != ""){
			if (document.getElementById(params[0]).value != document.getElementById(params[1]).value){
				return (!UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1])));
			}else{
				return true;
			}		
		}else{
			return true;
		}
	});
	
	// comprueba que la fecha Vigor Hasta sea posterior a la primera
	jQuery.validator.addMethod("validaFechaVigorHasta", function(value, element, params) {
		if(document.getElementById(params[1]).value != null && document.getElementById(params[1]).value != ""){
			//alert(document.getElementById(params[1]).value);
			if (document.getElementById(params[0]).value != document.getElementById(params[1]).value){
				return (!UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1])));
			}else{
				return true;
			}	
		}else{
			return true;
		}
	});
	
	// comprueba que la fecha Pago Hasta sea posterior a la primera
	jQuery.validator.addMethod("validaFechaPagoHasta", function(value, element, params) {
		if(document.getElementById(params[1]).value != null && document.getElementById(params[1]).value != ""){
			//alert(document.getElementById(params[1]).value);
			if (document.getElementById(params[0]).value != document.getElementById(params[1]).value){
				return (!UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1])));
			}else{
				return true;
			}	
		}else{
			return true;
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
	});	
	
	jQuery.validator.addMethod("grupoOfic", function(value, element, params) { 
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
	});	
	
});

function closePopUpAmbitoCont(){
    $('#popUpAmbitoContratacion').hide();
	$('#overlay').hide();
}

function accion(operacion, idPoliza){

    $('#actualizarSbp').val("");	
	if (operacion == 'verAcuseRecibo')
	{
		var frm = document.getElementById('main3');
		frm.operacion.value = operacion;
		frm.polizaOperacion.value = idPoliza;
		frm.submit();
	}
	else if((operacion == 'recibos' || operacion == 'siniestros' || operacion == 'siniestrosGan') || 
	         operacion == 'anexoModificacion' || 
	         operacion == 'reduccionCapital'  || 
	         operacion == 'imprimir'  || 
	         confirm('\u00BFSeguro que desea modificar el estado de la p\u00F3liza?')){
	         
	    if(operacion == "pasarDefinitiva"){
	    	
	        //showPopUpAvisoPasoDefinitiva('popUpPasarDefinIconRow');
	    	

		        idPolizaAccion = idPoliza;
		        var plan = $("input[name=checkParcela_"+idPoliza+"]").val().split('#')[8];
		        
		        if(plan<2015){
		    		ajax_check_Cpl_Sbp();	
		    	}else{
		    		//$('#overlay').hide();
		    		//blockUIPasoADefinitiva();
		    		ajax_muestraDatosAval(idPoliza);
		    	}		    
				//ajax_check_Cpl_Sbp();				
		}else if(operacion == "pasarDefinitivaCpl"){
	    
	    	//showPopUpAvisoPasoDefinitiva('popUpPasarDefinIconRowCpl');
	        idPolizaAccion = idPoliza;
	        var plan = $("input[name=checkParcela_"+idPoliza+"]").val().split('#')[8];
		        
		    if(plan<2015){
		    	aceptarPopUpPasoDefinitiva ('popUpPasarDefinIconRowCpl');
		    }else{
		    	ajax_muestraDatosAval(idPoliza);
		    }
		
		/* Pet. 43417 ** MODIF TAM (17.09.2021) */    
		}else if(operacion == "siniestrosGan"){    
			
		    var frm = document.getElementById('SiniestrosGanado');
			frm.idPolizaSinGan.value = idPoliza;
			frm.method.value ='doConsulta'; //nombre metodo del controlador SiniestrosGanadoController
			$('#SiniestrosGanado').submit(); //formulario
	         
	    }else{    
			var frm = document.getElementById('main3');	
			frm.operacion.value = operacion;
			frm.polizaOperacion.value = idPoliza;
				
			if (operacion == 'imprimir'){
				frm.target="_blank";
			} 
			else {
				frm.target="";
			}
			frm.submit();
		}
	}
	
}

function hidePopUpPasarDefinitivaIconRow(){
    $('#popUpPasarDefinitivaIconRow').hide();
    $('#overlay').hide();
}

function getIdPoliza(cadena){
	var idPoliza = "";
	cadena = cadena.substring(0,cadena.length-1);
	cadena=cadena.split("#");
	return cadena[0];
}

function grabarPolDefMultiple(){
	var list = "";
	var isOk = true;
	var noMultSaeca = true;
	var permisoUserCargoEnCta = true;
	$('#filtro').val('consulta');
	$("input[type=checkbox]").each(function(){
		if($(this).attr('checked')){
//			alert($("#idsRowsChecked").val());
//			alert($(this).attr('name'));
			if($(this).attr('name')!='checkTodo'){
				list = list + $(this).val();
			}
		}
	});  

	if(list.length > 0){
		var valores = list.split("|");
		for(i=0;i<valores.length-1;i++){
			var estado = valores[i].split("#")[1];
			if(estado != 2){
				isOk = false;
				break;
			}
		}
		
		if(isOk){
			for(i=0;i<valores.length-1;i++){
				var idpol = valores[i].split("#")[0];
				var res = isPagoCCPermitido(idpol);
				if(!res){
					permisoUserCargoEnCta = false;
					break;
				}
			}
			
			// Si el usuario tiene permiso para elegir la forma de pago 'Cargo en cuenta' continua
			if(permisoUserCargoEnCta){
				$('#esSaecaUnica').val('');
				noMultSaeca = ajax_check_Multiple_Financiada();
				if(noMultSaeca){
					list = list.substring(0,list.length - 1);
					$('#listGrabDefPolizas').val(list);
					if (confirm('\u00BFSeguro que desea modificar el estado de la(s) p\u00F3liza(s)?')){
						// LLAMADA POR AJAX PARA VER SI ALGUNA POLIZA TIENE CPL EN SBP
						if($('#esSaeca').val() != null && $('#esSaeca').val() != ""){
							/*alert("Abro el dialogo de aval");*/
							list = list.split("#");
							$('#idpoliza').val(list[0]);
							idPolizaAccion = list[0];
							if(list[7] == "C"){
								$('#esCpl').val("true");
							}else{
								$('#esCpl').val("false");
							}
							ajax_muestraDatosAval(list[0]);
						}else{
							ajax_check_Multiple_Cpl_Sbp();
						}
						
					}
				}else{
					$('#divAviso').show();
					$('#txt_info_saeca').show();
					$('#overlay').show();
				}
			}else{
				$('#divAviso').show();
				$('#txt_info_check_grabDefUser').show();
				$('#overlay').show();
			}
				
			
		}else{
			$('#divAviso').show();
			$('#txt_info_gp').show();
			$('#overlay').show();
		}
	}else{
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}
}

function submitPasoDefinitiva(){
    $('#main3 #operacion').val('MultiGrabDef');
	$('#main3').submit();
}

/*
 * @param: serverTime: hora actual del servidor
 * @param: popUp: nombre del popUp
 */
function ajaxHandler_showPopUpAvisoPasoDefinitiva(serverTime, popUp){
	var MENSAJE_ENVIO_MANANA    = "Env\u00EDo fuera de plazo, la p\u00F3liza se enviara en el d\u00EDa de ma\u00F1ana.";
	var MENSAJE_ENVIO_HOY       = "Env\u00EDo dentro de plazo, la p\u00F3liza se enviara en el d\u00EDa de hoy.";
	var LIMIT_TIME_ENVIO_POLIZA = "16:40:00";
	
	if(CompararHoras(serverTime, LIMIT_TIME_ENVIO_POLIZA)){
		if(popUp == "popUpPasarDefinitivaBoton"){
			showPopUpAviso(MENSAJE_ENVIO_MANANA, popUp);
		}else if(popUp == "popUpPasarDefinIconRow"){
			showPopUpAviso(MENSAJE_ENVIO_MANANA, popUp);
		}else if(popUp == "popUpPasarDefinIconRowCpl"){
			showPopUpAviso(MENSAJE_ENVIO_MANANA, popUp);
		}
		
		if(popUp == "popUpPasarDefinitivaBoton"){
			showPopUpAviso(MENSAJE_ENVIO_HOY, popUp);
		}else if(popUp == "popUpPasarDefinIconRow"){
			showPopUpAviso(MENSAJE_ENVIO_HOY, popUp);
		}else if(popUp == "popUpPasarDefinIconRowCpl"){
			showPopUpAviso(MENSAJE_ENVIO_HOY, popUp);
		}
	}
}

function aceptarPopUpPasoDefinitiva(popUp){
	$('#filtro').val('consulta');
	
	if(popUp == "popUpPasarDefinitivaBoton"){
		var frm = document.getElementById('pasarADefinitivaMultiple');
		frm.resultadoValidacion.value ="true";
		// Se copian al formulario de paso a definitiva multiple los ids de poliza del formulario principal
		frm.idsRowsChecked.value = $('#idsRowsChecked').val();
		frm.fpago.value = $('#seleccionPago').val();
		frm.target="_blank";
		frm.submit();
	}
	else if(popUp == "popUpPasarDefinIconRow"){
		// Muestra el aviso de paso a definitiva
		blockUIPasoADefinitiva();
		
		var frm = document.getElementById('pasarADefinitiva');
		frm.action.value = "pasoADefinitiva.html";
		frm.method.value="doPasarADefinitiva";
		frm.idpoliza.value = idPolizaAccion;
		frm.resultadoValidacion.value ="true";
		frm.target="";
		//frm.fpago.value = $('#tx_fechaPago').val();
		frm.submit();
		
	}
	else if(popUp == "popUpPasarDefinIconRowCpl"){
		// Muestra el aviso de paso a definitiva
		blockUIPasoADefinitiva();
		
		var frm = document.getElementById('pasarADefinitiva');
		frm.idpoliza.value = idPolizaAccion;
		frm.resultadoValidacion.value ="true";
		frm.esCpl.value ="true";
		frm.fpago.value = $('#seleccionPago').val();
		frm.target="";
		frm.submit();
	}
}

function ajax_check_Cpl_Sbp(){
	var frm2 = document.getElementById('pasarADefinitiva');
	frm2.actualizarSbp.value ="";
	$.ajax({
		url:          "validacionesUtilidades.html",
		data:         "method=do_Ajax_check_Cpl_Sbp&idPoliza="+idPolizaAccion,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "text",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			alert("Error al comprobar las p\u00F3lizas de sobreprecio: " + quepaso);
		},
		success: function(datos){
			if (datos == 'true'){
				if(confirm('La p\u00F3liza de Sobreprecio asociada a esta p\u00F3liza va a ser actualizada, \u00BFDesea Continuar?')){
					var frm2 = document.getElementById('pasarADefinitiva');
					frm2.actualizarSbp.value ="true";
					aceptarPopUpPasoDefinitiva('popUpPasarDefinIconRow');
				}
			}else{
				aceptarPopUpPasoDefinitiva('popUpPasarDefinIconRow');
			}
		},
		type: "GET"
	});
}

function ajax_check_Multiple_Cpl_Sbp(){
	var frm2 = document.getElementById('pasarADefinitivaMultiple');
	frm2.actualizarSbp.value ="";
	var frm = document.getElementById('main3');
	var ids = $('#idsRowsChecked').val();
	
	$.ajax({
		url:          "validacionesUtilidades.html",
		data:         "method=do_Ajax_check_Multiple_Cpl_Sbp&idsRowsChecked="+ids,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "text",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			alert("Error al comprobar las p\u00F3lizas de sobreprecio: " + quepaso);
		},
		success: function(datos){
			if (datos == 'true'){
				// tiene Cpl asociada
				if(confirm('La p\u00F3liza de Sobreprecio asociada de cada p\u00F3liza se actualizar\u00E1, \u00BFDesea Continuar?')){
					var frm2 = document.getElementById('pasarADefinitivaMultiple');
					frm2.actualizarSbp.value ="true";
					aceptarPopUpPasoDefinitiva ('popUpPasarDefinitivaBoton');
				}
			}else{
				aceptarPopUpPasoDefinitiva ('popUpPasarDefinitivaBoton');
			}
		},
		type: "GET"
	});
}

function ajax_check_Multiple_Financiada(){
	var ids = $('#idsRowsChecked').val();
	var succeed;
	
	$.ajax({
		url:          "validacionesUtilidades.html",
		data:         "method=do_Ajax_check_Multiple_Financiadas&idsRowsChecked="+ids,
		async:        false,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "text",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			alert("Error al comprobar la financiaci\u00F3n de las p\u00F3lizas: " + quepaso);
		},
		success: function(datos){
			datos = datos.split("_");
			if (datos[0] == 'true'){
				// tiene mas de una poliza financiada SAECA seleccionada o una con financiacion SAECA seleccionada y el resto no
				/*alert("Tiene mas de una financiada SAECA");*/
				succeed = false;
			}else{
				/*alert("Tiene una o ninguna financiada SAECA");*/
				succeed = true;
				$('#esSaeca').val(datos[1])
			}
		},
		type: "GET"
	});
	return succeed;
}


	function validaFormaPagoAjax(idPoliza){
		
		var frm = document.getElementById('pasarADefinitiva');
		var valida=false;
		$.ajax({
	    url:          "pagoPoliza.html",
	    data:         "operacion=validaFormaPagoAjax&idpoliza="+idPoliza+"&",
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
	    	
	    	if (datos.mensaje =="errorTipoPago"){ 
	    		$('#panelAlertasValidacion').html("Debe seleccionar Forma de Pago para pasar a Definitiva");
   			 	valida = false;
			} else if (datos.mensaje =="errorCuenta"){ 
				$('#panelAlertasValidacion').html("La E-S Mediadora de la p\u00F3liza no permite el cargo en cuenta a la entidad seleccionada");
   			 	valida = false;
			} else if (datos.mensaje =="errorManual"){ 
				$('#panelAlertasValidacion').html("Falta la informaci\u00F3n de Pago Manual");
   			 	valida = false;
			}
	    	else{ 
				$('#panelAlertasValidacion').html("");
				valida = true;
        	}
	    },
	    type: "GET"
		});
		return valida;
	}
	
	
function isPagoCCPermitido(idPoliza){
	
	var frm = document.getElementById('pasarADefinitiva');
	var valida=false;
	$.ajax({
    url:          "pagoPoliza.html",
    data:         "operacion=isPagoCCPermitidoAjax&idpoliza="+idPoliza+"&",
    async:        false,
    contentType:  "application/x-www-form-urlencoded",
    dataType:     "json",
    global:       false,
    ifModified:   false,
    processData:  true,
    cache:        false,
    error: function(objeto, quepaso, otroobj){
    	
        alert("Error al validar si el usuario tiene permisos para el cargo en cuenta en el paso a definitiva: " + quepaso);
    },
    success: function(datos){    	
    	
    	valida =datos.esPermitido;
    },
    type: "GET"
	});
	return valida;
}
	

// MPM - 08/05/12

function grabarDefFueraContratacion(idPoliza){
	// Cierra el pop up de aviso de fuera de contratacion
	closePopUpAmbitoCont();
	// Muestra el aviso de paso a definitiva
	blockUIPasoADefinitiva();
	var frm = document.getElementById('pasarADefinitiva');
	frm.resultadoValidacion.value ="true";
	frm.grabFueraContratacion.value = 'grabarFueraContratacion';
	frm.idpoliza.value = idPoliza;
	frm.target="";
	frm.submit();
}

function grabarDefFueraContratacionMult(listPolizas){
	// Muestra el aviso de paso a definitiva
	blockUIPasoADefinitiva();
	var frm = document.getElementById('pasarADefinitivaMultiple');
	frm.resultadoValidacion.value ="true";
	frm.idsRowsChecked.value = listPolizas;
	frm.grabFueraContratacion.value = 'grabarFueraContratacion';
	frm.submit();
}
//TMR 08-08-2012
function marcatodosPopUp(){
	
	$('#tableNoDefinitiva input[type=checkbox]').each( function() { 				        		    
    	$(this).attr('checked',true);
	});
	
	$("#btn_forzarDef").css("display",'');
}
function desmarcatodosPopUp(){
	
	$('#tableNoDefinitiva input[type=checkbox]').each( function() { 				        		    
    	$(this).attr('checked',false);
	});
	$("#btn_forzarDef").css("display",'none');
	
}
function muestraOcultaBoton(){
	var mostrar =0;
	
	$('#tableNoDefinitiva input[type=checkbox]').each( function() { 				        		    
    	if($(this).attr('checked')){
    		mostrar = mostrar +1;
    	}
    });
    if (mostrar>0){
    	$("#btn_forzarDef").css("display",'');
    }else{
    	$("#btn_forzarDef").css("display",'none');
    	$("#marcaTodosDefMul").attr('checked',false);
    }
}
function getChecksSelDefMult(){
	var list = "";
	$('#tableNoDefinitiva input[type=checkbox]').each( function() { 
 		if($(this).attr('checked')){
			list = list + ";" + $(this).val();
		}
	});
	 var frm = document.getElementById('main3');
	 frm.listGrabDefPolizas.value = list;
	 
	 
}
//TMR 08-08-2012

/*
* @param: popUp: nombre del popUp
*/
function showPopUpAvisoPasoDefinitiva(popUp){
	$.ajax({
		url:          "ajaxCommon.html",
		data:         "operacion=ajax_getServerTime",
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "text",
		global:       true,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			alert("Error al solicitar la hora del servidor: " + quepaso);
		},
		success: function(datos){
			ajaxHandler_showPopUpAvisoPasoDefinitiva(datos, popUp)
		},
		type: "GET"
	});
}

/**
 * compare time1 to time2
 * @return true if time1 > time2
 */
function CompararHoras(time1, time2) {
	var result = false;
	var auxTime1  = time1.split(":");
	var auxTime2  = time2.split(":");
	var horas1    = parseInt(auxTime1[0],10);
	var minutos1  = parseInt(auxTime1[1],10);
	var horas2    = parseInt(auxTime2[0],10);
	var minutos2  = parseInt(auxTime2[1],10);
	
	// Comparar
	if (horas1 > horas2 || (horas1 == horas2 && minutos1 > minutos2)){
		result = true;
	}else{
		result = false;
	}
	
	return result;
}

/*
* handler event click button aceptar in popUp
*/
function aceptarPopUpPanelAvisos(){
	$('#main3 #operacion').val('MultiGrabDef');
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

function validar(){
	var frm = document.getElementById('main3');
	frm.operacion.value = 'cambioOficinaValidacion';
	var list = "";
	var idPoliza = "";
	
	$('#main3').submit();
}

function limpiarPaneles(){
	$("#panelInformacion").hide();
	$("#panelInformacion").html('');
}

// DAA 11/06/2012 Realiza el Cambio de Oficina de todas las polizas seleccionadas en el listado
function operacionOficina(){
	limpiarPaneles();
	$('#filtro').val('consulta');
	var frm = document.getElementById('main3');
	var ids = frm.idsRowsChecked.value;
	
	// Compruebo si tenemos check seleccionados, si no muestra mensaje
	if (ids != ''){
		// Comprueba si las polizas seleccionadas se pueden cambiar de oficina
		ajaxCheckCambioOficinaMultiple(ids);
	}else {
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}
}

function ajaxCheckCambioOficinaMultiple(ids){
	$.ajax({
		url:          "validacionesUtilidades.html",
		data:         "method=doAjaxCheckCambioOficinaMultiple&idsRowsChecked="+ids,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "text",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			alert("Error al comprobar las oficinas de las p\u00F3lizas: " + quepaso);
		},
		success: function(datos){
			if (datos == 'false'){
				// No se puede hacer el cambio (distintas oficinas)
				// Muestra el mensaje de aviso
				$('#divAviso').show();
				$('#txt_info_DistintaEnt').show();
				$('#overlay').show();
			}
			else if (datos == 'false2') {
				// No se puede hacer el cambio (polizas anuladas)
				// Muestra el mensaje de aviso
				$('#divAviso').show();
				$('#txt_info_pol_anuladas').show();
				$('#overlay').show();
			}
			else{
				// Las polizas se pueden cambiar de oficina
				var frm = document.getElementById('main3');
				frm.entCambioOficina.value = datos;
				frm.operacion.value = 'cambioOficina';
				lupas.muestraTabla('CambiarOficina','principio', '', '');
			}
		},
		type: "GET"
	});
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

function comprobarCampos(){
	var resultado = false;
	if (!resultado && $('#entidad').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#oficina').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#codusuario').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#plan').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#linea').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#poliza').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#colectivo').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#dc').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#modulo').val() != ''){
		resultado = true;
	}
	/*
	var prueba = $('#fecEnvioId').val();
	if (!resultado && ($('#fecEnvioId').val() != '') || (!typeof prueba === "undefined")){
		resultado = true;
	}*/
	if (!resultado && $('#nifcif').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#estadoP').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#seleccionRC').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#seleccionSTR').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#seleccionMOD').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#nombre').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#clase').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#estadosPolizaPago').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#entmediadora').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#subentmediadora').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#delegacion').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#seleccionPago').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#seleccionRnv').val() != ''){
		resultado = true;
	}
	
	if (!resultado && $('#seleccionFinanciada').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#seleccionIBAN').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#seleccionRyD').val() != ''){
		resultado = true;
	}
	/*
	if (!resultado && $('#fecVigorId').val() != ''){
		resultado = true;
	}*/
	
	if (!resultado && $('#fechaEnvioDesdeId').val() != ''){
		resultado = true;
	}
	
	if (!resultado && $('#fechaEnvioHastaId').val() != ''){
		resultado = true;
	}
	
	if (!resultado && $('#fechaVigorDesdeId').val() != ''){
		resultado = true;
	}
	
	if (!resultado && $('#fechaVigorHastaId').val() != ''){
		resultado = true;
	}
	
	if (!resultado && $('#fechaPagoDesdeId').val() != ''){
		resultado = true;
	}
	
	if (!resultado && $('#fechaPagoHastaId').val() != ''){
		resultado = true;
	}
	
	if (!resultado && $('#nsol').val() != ''){
		resultado = true;
	}
	 /*   P0073325 - RQ.04, RQ.05 y RQ.06  Inicio */
	if (!resultado && $('#canalFirma').val() != ''){
		resultado = true;
	}
	
	if (!resultado && $('#docFirmada').val() != ''){
		resultado = true;
	}
	/*   P0073325 - RQ.04, RQ.05 y RQ.06  Fin */
	return resultado;
}

function consultar(){		
	var frm = document.getElementById('main3');
	
	if (!comprobarCampos()){
		$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
		$('#panelAlertasValidacion').show();
	} else {
		
		desmarcar_todos();
		
		   
		//$('#operacion').val("consultar");
		$('#main3 #operacion').val("consultar");
		$('#filtro').val("consultar");
	     frm.target="";
	     $('#main3').submit();
	}				
}

function limpiar(){
	     $('#filtro').val('');
	     
	if ($('#perfil').val()!=0){
	

		
		setDefaultEntidad();
	}else{
	
	 $('#entidad').val('');  
		
	}
	desmarcar_todos();
	$('#vieneDeLimpiar').val('true');
	$('#estadosPolizaPago').val('');
	$('#oficina').val('');
	$('#desc_oficina').val('');
	$('#codusuario').val('');
	$('#plan').val('');
	$('#linea').val('');
	$('#desc_linea').val('');
	$('#poliza').val('');
	$('#colectivo').val('');
	$('#dc').val('');
	$('#modulo').val('');
	$('#fecEnvioId').val('');
	$('#fecVigorId').val('');
	$('#nifcif').val('');
	$('#estadoP').selectOptions('');
	$('#seleccionRC').selectOptions('');
	$('#seleccionSTR').selectOptions('');
	$('#seleccionMOD').selectOptions('');
	$('#seleccionPago').selectOptions('');
	$('#seleccionRnv').selectOptions('');	
	$('#seleccionFinanciada').selectOptions('');
	$('#seleccionRyD').selectOptions('');
	$('#ref').val('');
	$('#nombre').val('');
	$('#clase').val('');
	$('#main3 #operacion').val("");
	$('#desc_entidad').val("");
	$('#entmediadora').val("");
	$('#subentmediadora').val("");
	$('#delegacion').val("");
	$('#seleccionPago').val("");
	$('#main3').attr('target', '');
	$('#fechaEnvioDesdeId').val("");
	$('#fechaEnvioHastaId').val("");
	$('#fechaVigorDesdeId').val("");
	$('#fechaVigorHastaId').val("");
	$('#fechaPagoDesdeId').val("");
	$('#fechaPagoHastaId').val("");
	$('#nsol').val("");
	/*   P007335 - RQ.04, RQ.05 y RQ.06  Inicio */
	$('#canalFirma').selectOptions('');
	$('#docFirmada').selectOptions('');
	/*   P007335 - RQ.04, RQ.05 y RQ.06  Inicio */
	$('#main3').submit();				
}



function imprimir(size, formato) {
	
	var frm = document.getElementById('main3');
	
	// MPM - 05/09/12
	// Si el numero de registros a imprimir es menor al permitido, se lanza el informe en ventana nueva
	if(size < $("#numRegImpresion").val()){
		frm.target="_blank";
		frm.formato.value = formato;
		frm.operacion.value = 'imprimirInforme';
		frm.submit();
	}
	// Si no, muestra el mensaje y no lanza el informe
	else {
		$("#panelAlertasValidacion").html($("#alertaImpresion").val());
		$("#panelAlertasValidacion").show();
	}
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
	$('#txt_info_DistintaEnt').hide();
	$('#txt_info_check_multiple').hide();
	$('#overlay').hide();
}

function showPopUpCambioUsuario(){
 	$('#divCambioUsuario').fadeIn('normal');
	$('#overlay').show();
}

function cerrarPopUpUsuario(){
	$('#divCambioUsuario').fadeOut('normal');
	$('#overlay').hide();
	$('#campoObligatorio_usuarioNuevo').hide();
	$('#cambioUsuarioPopUpError').hide();
}

function cambiarUsuario(){
	var list = "";
	var idPoliza = "";
	var frm = document.getElementById('main3');
	 
	if ($("#inputUsuario").val() != ''){
		$("#usuarioNuevo").val($("#inputUsuario").val());
		frm.operacion.value = 'cambiarUsuario'
		$('#filtro').val('consulta');
		$("#main3").submit();
	} else {
		$('#campoObligatorio_usuarioNuevo').show();
		$('#cambioUsuarioPopUpError').show();
	}
}

function eliminar(idPoliza){
	if(confirm('\u00BFEst\u00E1 seguro de que desea eliminar la p\u00F3liza seleccionada?')){
		var frm = document.getElementById('main3');
		frm.operacion.value='eliminar';
		$('#filtro').val('consulta');
		$("#idPolizaDelete").val(idPoliza);
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main3").submit();
	}
}

// MPM - 05/06/12 - INICIO
// Realiza el borrado de todas las polizas seleccionadas en el listado
function borradoMasivoPolizas() {
	var ids = $('#idsRowsChecked').val();
	ajaxCheckBorradoMultiple(ids);
}

// Realiza la llamada al controlador encargado de validar si las polizas indicadas se pueden borrar
function ajaxCheckBorradoMultiple(ids){
	
	$.ajax({
		url:          "validacionesUtilidades.html",
		data:         "method=doAjaxCheckBorradoMultiple&idsRowsChecked="+ids,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "text",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			alert("Error al comprobar el estado de las p\u00F3lizas que se quiere borrar: " + quepaso);
		},
		success: function(datos){
			// Las polizas se pueden borrar
			if (datos == 'true'){
				// Se solicita confirmacion para el borrado de las polizas
				if (confirm('\u00BFSeguro que desea eliminar la(s) p\u00F3liza(s) seleccionada(s)?')){
					var frm = document.getElementById('main3');
					frm.operacion.value = 'borradoMasivo';
					frm.filtro.value = 'borradoMasivo';
					$('#main3').submit();
				}				
			}
			// Alguna de las polizas no se puede borrar
			else{
				// Muestra el mensaje de aviso
				$('#divAviso').show();
				$('#txt_info_bm').show();
				$('#overlay').show();
			}
		},
		type: "GET"
	});
}
// MPM - 05/06/12 - FIN

//* Mantener checks *//
function onClickInCheck2(idCheck){
	if(idCheck){
		var __aux = idCheck.split("_");
		var __ids = $('#idsRowsChecked').val();
		if (document.getElementById(idCheck).checked == true){
			addCheck2(__ids, __aux[1]);
		}else{
			subtractCheck2(__ids, __aux[1]);
		}
	}
}

function addCheck2(ids, check){
	if(ids != null){
		ids = ids + ";" + check;
	}
	
	var frm = document.getElementById('main3');
	frm.idsRowsChecked.value = ids;
	var frm2 = document.getElementById('pasarADefinitivaMultiple');
	frm2.idsRowsChecked.value = ids;
}

function subtractCheck2(ids, check){
	var newList = "";
	var frm = document.getElementById('main3');
	
	// DAA 14/05/2012
	if(frm.checkTodo.value=="true"){
		frm.checkTodo.value="";
		var frmcheck = document.getElementById('frmcheck');
		frmcheck.checkTodo.checked=false;
		ids=frm.idsRowsChecked.value;
	}
	
	if(ids != null){
		var array_ids = ids.split(';');
		for(var i = 0; i < array_ids.length; i++){
			if(array_ids[i] != check && array_ids[i] != "" && array_ids[i] != null && array_ids[i] != undefined){
				newList = newList + array_ids[i] + ";";
			}
		}
	}
	
	var frm = document.getElementById('main3');
	frm.idsRowsChecked.value = newList;
	var frm2 = document.getElementById('pasarADefinitivaMultiple');
	frm2.idsRowsChecked.value = newList;
}

function check_checks(ids){
	if(ids != null){
		var array_ids = ids.split(';');
		for(var i = 0; i < array_ids.length; i++){
			var idCheck = "checkParcela_" + array_ids[i];
			$('#' + idCheck).attr('checked',true);
		}
	}
}

function altaSbp(){
	var list = "";
	var contador = 0;
	$('#filtro').val('consulta');
	$("input[type=checkbox]").each(function(){
		if($(this).attr('checked')){
			list = list + $(this).val();
			contador++;
		}
	});
	
	if(contador > 0){
		if (contador > 1){
			// hay mas de una poliza marcada
			$('#divAviso').show();
			$('#txt_info_check_multiple').show();
			$('#overlay').show();
		}else{ 
			// solo una poliza marcada
			list = list.substring(0,list.length - 1);
			var valores = list.split("|");
			var id = valores[0].split("#")[0];
			var estado = valores[0].split("#")[1];
			var tipo = valores[0].split("#")[7];
			var estadoCorrectoSbp = false;
			if (tipo == 'P'){
				if(estado == 2 || estado == 3 || estado == 5 || estado == 8){
					estadoCorrectoSbp = true;
				}
			}else{
				estadoCorrectoSbp = true;
			}
			if(estadoCorrectoSbp){ 
				//estado correcto, llamada a ajax
				ajax_AltaSbp(id, tipo, estado);
			}else{ 
				// estado incorrecto de la poliza Ppal para Sbp
				$('#panelAlertasValidacion').html("El estado de la p\u00F3liza principal no permite Sobreprecio");
				$("#panelAlertasValidacion").show();
			}
		}
	}else{
		// no hay polizas seleccionadas
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}
}

function ajax_AltaSbp(id, tipo, estado){
	$.ajax({
	    url:          "simulacionSbp.html",
	    data:         "method=ajax_buscarPolAsocYValidar&idPoliza="+id+"&tipoPoliza="+tipo+"&validarSbp=true",
	    dataType:     "json",
	    async:        true,
	    error: function(objeto, quepaso, otroobj){
	        alert("Error al validar la poliza para Sobreprecio: " + quepaso);
	    },
	    success: function(datos){
	    	
	    	if (datos.idPolizaSbp != ""){ // tiene poliza Sbp
	    		var form = document.getElementById('ListadoPolSbpForm');
	    		form.idPolizaSbp.value = datos.idPolizaSbp;
	    		$('#ListadoPolSbpForm').submit();
	    	}else{
	    		
	        	if (datos.mensajeError != ""){
	        		$('#panelAlertasValidacion').html(datos.mensajeError);
	        		$("#panelAlertasValidacion").show();
	        	}else{
	        		var estadoCorrectoSbp = true;
		            var frm = document.getElementById('altaPolizaSbp');
			  	 	if (tipo == 'P'){
				  	 	frm.idPolizaPpal.value = id;
				  	 	frm.idEstadoPpal.value = estado;
				  	 	frm.idPolizaCpl.value  = datos.idPoliza;
				  	 	frm.idEstadoCpl.value  = datos.estado;
				  	}else{
				  		estadoCorrectoSbp = false;
				  		frm.idPolizaPpal.value = datos.idPoliza;
				  	 	frm.idEstadoPpal.value = datos.estado;
				  	 	frm.idPolizaCpl.value  = id;
				  	 	frm.idEstadoCpl.value  = estado;
				  	 	if(frm.idEstadoPpal.value == 2 || frm.idEstadoPpal.value == 3 || frm.idEstadoPpal.value == 5 || frm.idEstadoPpal.value == 8){
	 						estadoCorrectoSbp = true;
	 					}
				  	}
				  	
			  	 	if (estadoCorrectoSbp){
				  	 	if (frm.idPolizaCpl.value != ""){ //tenemos poliza complementaria
				  	 		if (frm.idEstadoCpl.value != 4){
				  	 			// Si la principal esta contratada y la complementaria esta en estado 'Enviada pendiente de confirmacion'
			  	 				// o 'Enviada correcta' no se pregunta y se incluye la complementaria en el sobreprecio
				  	 			if ((frm.idEstadoCpl.value == 8 || frm.idEstadoCpl.value == 5) && frm.idEstadoPpal.value == 8){
				  	 				alta_eleccion_Si();
				  	 			}else{
				  	 				var msj = "\u00BFDesea incluir los datos de la p\u00F3liza complementaria en el Sobreprecio?";
				  	 				//Activar Popup incluir Cpl en Sbp
				  	 				$('#txt_mensaje_eleccionCplEnSbp').html(msj);
				  	 				$('#panelInformacion').show();
				  	 				$('#popUpAltaSbp').show();
		 	     					$('#overlay').show();
				  	 			}
				  	 		}else{ // Cpl anulada
				  	 			$.blockUI.defaults.message = '<h4> Calculando P\u00F3liza de Sobreprecio.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		       					$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		       					frm.method.value ='doAlta';
			  	 				$('#altaPolizaSbp').submit();
				  	 		}
			  	 		}else{ // no tiene Cpl
			  	 			$.blockUI.defaults.message = '<h4> Calculando P\u00F3liza de Sobreprecio.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       					$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	       					frm.method.value ='doAlta';
		  	 				$('#altaPolizaSbp').submit();
			  	 		}
			  	 	}else{// estado incorrecto de la Poliza Ppal para Sbp
			  	 		$('#panelAlertasValidacion').html("El estado de la p\u00F3liza principal no permite Sobreprecio");
						$("#panelAlertasValidacion").show();
			  	 	}
	  	 		}
	  	 	}
	    },
	    type: "POST"
	});
}

//MPM - 08/05/12
function blockUIPasoADefinitiva () {
	$.blockUI.defaults.message = '<h4> Realizando el paso a definitiva.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}

// DAA 14/05/2012 marcar_todo; desmarcar_todo; cambia cursor;
function marcar_todos(){
						
	var frm = document.getElementById('main3');			
	var frm2 = document.getElementById('pasarADefinitivaMultiple');
	$("input[type=checkbox]").each(function() { 				        		    
    	$(this).attr('checked',true);
	});
	frm.checkTodo.value="true";
	frm.idsRowsChecked.value=frm.polizasString.value;
	frm2.idsRowsChecked.value=frm.polizasString.value;
}
		
function desmarcar_todos() {
	
	var frm = document.getElementById('main3');
	var frm2 = document.getElementById('pasarADefinitivaMultiple');
	if(frm.checkTodo.value =="true"){
		$("input[type=checkbox]").each(function() { 				        		    
    		$(this).attr('checked',false);
  		});
  		
	    frm.checkTodo.value="false";
	    frm.idsRowsChecked.value="";
	    frm2.idsRowsChecked.value="";
	}
}

function cambia_cursor(){
	document.body.style.cursor = "wait";
}

function alta_eleccion_Si(){
	$('#popUpAltaSbp').hide();
	$('#overlay').hide();
	var frm2 = document.getElementById('altaPolizaSbp');
	frm2.incSbpComp.value = "S";
	frm2.method.value ='doAlta';
	$('#altaPolizaSbp').submit();
}

function alta_eleccion_No(){
	$('#popUpAltaSbp').hide();
	$('#overlay').hide();
	var frm = document.getElementById('altaPolizaSbp');
	frm.incSbpComp.value = "N";
	frm.method.value ='doAlta';
	$.blockUI.defaults.message = '<h4> Calculando P\u00F3liza de Sobreprecio.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$('#altaPolizaSbp').submit();
}

function cambiarOficina(){
	var frm = document.getElementById('main3');
	if(frm.operacion.value != null && frm.operacion.value == "cambioOficina"){
    	if (document.getElementById('codoficinaCO').value !=""){
			$('#main3').submit();
		}
	 }
}
function verDetallePoliza(idpoliza, esganado){
	var frm = document.getElementById('consultaDetallePoliza');
	frm.idpoliza.value = idpoliza;
	frm.modoLectura.value='modoLectura';
	if(esganado=='false'){
		//var frm = document.getElementById('consultaDetallePoliza');
		//frm.idpoliza.value = idpoliza;
		frm.method.value ='doListaParcelas';
		//$('#consultaDetallePoliza').submit();
	}else{
		frm.method.value ='doPantallaListaExplotaciones';
		frm.operacion.value='listExplotaciones';
		frm.action ='listadoExplotaciones.html';		
	}
	$('#consultaDetallePoliza').submit();
}

function editar(idpoliza,estado,tipo){
	
	if (estado == 3){
		if(confirm('La p\u00F3liza va a pasar a estado Pendiente de ' +
				'Validaci\u00F3n. No se enviar\u00E1 a Agroseguro hasta que se guarde como Definitiva. \u00BFDesea Continuar?')){
		// *** LLAMADA POR AJAX PARA SABER SI TIENE SBP
		$.ajax({
		            url:          "simulacionSbp.html",
		            data:         "method=ajax_buscarPolAsocYValidar&idPoliza="+idpoliza+"&tipoPoliza="+tipo+"&validarSbp=false",
		            dataType:     "json",
		            async:        true,
		            error: function(objeto, quepaso, otroobj){
		                alert("Error al validar la poliza para Sobreprecio: " + quepaso);
		            },
		            success: function(datos){
		            	if (datos.idPolizaSbp != "" && datos.estadoPolSbp == 2){ // tiene poliza Sbp y su estado es grab definitiva
				  	 			if(confirm('La p\u00F3liza de Sobreprecio asociada en estado Definitiva se borrar\u00E1, \u00BFdesea continuar?')){
							        $.blockUI.defaults.message = '<h4> Redirigiendo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
				       			    $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
									$("#borrarPolizaSbp").val("true");		
									$("#operacion").val("editar");
									$("#idpoliza").val(idpoliza);
									$("#seleccionPoliza").submit();
				  	 			}
				  	 	}else{
					        $.blockUI.defaults.message = '<h4> Redirigiendo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		       			    $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
							var frm = document.getElementById('editaPolizaUtilidades');
							frm.idpoliza.value = idpoliza;
							frm.method.value ='doEditaPoliza';
							$('#editaPolizaUtilidades').submit();
				  	 	}
				  	},
		            type: "POST"
		    });
		}
	}else{
		var frm = document.getElementById('editaPolizaUtilidades');
		frm.idpoliza.value = idpoliza;
		frm.method.value ='doEditaPoliza';
		$('#editaPolizaUtilidades').submit();
	}
}

function addcapaEspera(){			   
	  $.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>'; 
      $.blockUI({  
          overlayCSS: { backgroundColor: '#525583'}, 
          baseZ: 2000 
      }); 
} 
//FIN DNF 10/08/2020 PET.63485 



function editarPolCpl(idpoliza,estado){
	var frm = document.getElementById('editaPolizaUtilidades');
	if (estado == 3){
		if(confirm('La p\u00F3liza pasar\u00E1 a estado pendiente de validaci\u00F3n, \u00BFDesea Continuar?')){
			frm.idpoliza.value = idpoliza;
			frm.method.value ='doEditaPoliza';
			$('#editaPolizaUtilidades').submit();
		
		}
	}else{
		frm.idpoliza.value = idpoliza;
		frm.method.value ='doEditaPoliza';
		$('#editaPolizaUtilidades').submit();
		
	}
}
function verDetallePolizaCpl(idpoliza){
	var frm = document.getElementById('polizaCompl');
	frm.method.value='doConsulta';
	frm.modoLecturaCpl.value='modoLectura';
	frm.idpolizaCpl.value =idpoliza;
	$('#polizaCompl').submit();
}

//DAA 28/05/2013
function pagoMasivo(){
	if($('#idsRowsChecked').val() != ""){
		compruebaPolizasDefinitivasPagoMasivo_ajax();
	}else{
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}
}
//TMR 11/06/2013
function cambiaClase(){
	if($('#idsRowsChecked').val() != ""){
		validaPolCorrectasCambioClaseMasivo_ajax();
	}else{
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}
	
}
//DAA 11/07/2013
function alertaCargaPac(){
	$('#panelAlertasValidacion').html("No se pueden realizar acciones sobre la p\u00F3liza. Cargando parcelas de la PAC"); 
	$('#panelAlertasValidacion').show(); 
}

function showPopUpMasivosUtilidades(){	
	if($('#idsRowsChecked').val() != ""){
		$('#txt_mensaje_aviso_3').hide();
		$('#popUpMasivosUtilidades').show();
		$('#overlay').show();
	}else{
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}	
}

function hidePopMasivosUtilidades(){
	noCheckedRadioCambio('selCambio');
	$('#overlay').hide();
	$('#popUpMasivosUtilidades').hide();
}

function aceptarPopMasivosUtilidades(){
	var res=getValueRadioChecked('selCambio');
	
	switch(res) {
		case '1'://Cambio masivo borrado
		   hidePopMasivosUtilidades();
    	   borradoMasivoPolizas();
	       break;
	    case '2'://Cambio masivo Pago
	       hidePopMasivosUtilidades();
	       pagoMasivo();
	       break;
	    case '3'://Cambio masivo Grabacion Definitiva
	    	hidePopMasivosUtilidades();
	    	grabarPolDefMultiple();
	    	break;
	    case '4'://Cambio Oficina
	    	hidePopMasivosUtilidades();
	    	operacionOficina();
	    	break;
	    case '5'://Cambio Clase
	    	hidePopMasivosUtilidades();
	    	cambiaClase();
	    	break;
	    case '6'://Cambio de Usuario
	    	hidePopMasivosUtilidades();
	    	showPopUpCambioUsuario();
	    	break;	    	
	    case '7'://Cambio de Titular
	    	hidePopMasivosUtilidades();
	    	operacionCambiarTitular();
	    	break;
	    case '8'://Cambio de Modulo
	    	hidePopMasivosUtilidades();
	    	operacionCambiarModulo();
	    	break;
	    case '9':// Informes Detalle poliza
            hidePopMasivosUtilidades();
            operacionInformeDetalle('poliza');
            break;
        case '10':// Informes Detalle Sit. Act.
            hidePopMasivosUtilidades();
            operacionInformeDetalle('situacion_actualizada');
            break;
	    default:
	    	$('#txt_mensaje_aviso_3').html("Debe seleccionar una opci\u00F3n");
	    	$('#txt_mensaje_aviso_3').show();
			break;
	} 
}

function getValueRadioChecked(nombreColeccionRadios){
	var radioCambio=document.getElementsByName(nombreColeccionRadios);
	var i;
	var res=0;
	for (i = 0; i < radioCambio.length; i++) {
	    if (radioCambio[i].checked) {
	        res=radioCambio[i].value;
	        break;
	    }
	}
	return res;
}

function noCheckedRadioCambio(nombreColeccionRadios){
	var radioCambio=document.getElementsByName(nombreColeccionRadios);
	var i;	
	for (i = 0; i < radioCambio.length; i++) {
	    if (radioCambio[i].checked) {
	        radioCambio[i].checked=false;	        
	    }
	}	
}

function seguimientoPolizaJs(idpoliza){
	
	var frm = document.getElementById('seguimientoPoliza');
	frm.idpoliza.value = idpoliza;
	frm.method.value ='doSeguimiento'; //nombre metodo del controlador SeguimientoPolizaController
	$('#seguimientoPoliza').submit(); //formulario
	
}

function actualizarSeguimientoPoliza(idpoliza){
	var frm = document.getElementById('seguimientoPoliza');
	frm.idpoliza.value = idpoliza;
	frm.method.value ='doActualizar';
	$('#seguimientoPoliza').submit();
}

function volverSeguimientoPoliza() {
	var frm = document.getElementById('main');
	frm.method.value ='doVolver';
	$("#main").submit();
}

//DNF PET-63482 15/04/2021
function showPopUpImportacionPoliza(){	
	
	//vaciamos campos
	$('#plan_ip').val("");
	$('#poliza_ip').val("");
	$('#usuario_ip').val("");
	$('#tipoRefPoliza_ip').val("");
	
	// limpiamos alertas y ocultamos el panel de error para la primera vez que se accede
	$('#panelAlertasValidacion_ip').html("");
	$('#panelAlertasValidacion_ip').hide();
	
	$('#panelImportacionPoliza').show();
}
function cancelar_ip(){
	// limpiamos alertas
	$('#panelAlertasValidacion_ip').html("");
	$('#panelAlertasValidacion_ip').hide();
	
	$.unblockUI();

	//vaciamos campos
	$('#plan_ip').val("");
	$('#poliza_ip').val("");
	$('#usuario_ip').val("");
	$('#tipoRefPoliza_ip').val("");
	
	$('#panelImportacionPoliza').hide();
}
function aplicar_ip(){
	
	var referencia = $('#poliza_ip').val();
	var plan       = $('#plan_ip').val(); 
	var usuario    = $('#usuario_ip').val();
	var tipoRefPoliza = $('#tipoRefPoliza_ip').val().toUpperCase();
	
	$('#panelAlertasValidacion_ip').html("");
	
	if(null == referencia || "" == referencia || null == plan || "" == plan || null == usuario || "" == usuario
			|| null == tipoRefPoliza || "" == tipoRefPoliza){
		$('#panelAlertasValidacion_ip').html("Debe rellenar todos los campos.");
		$('#panelAlertasValidacion_ip').show();
	}else if(tipoRefPoliza != 'P' && tipoRefPoliza != 'C'){
		$('#panelAlertasValidacion_ip').html("Debe introducir un tipo de referencia valido.");
		$('#panelAlertasValidacion_ip').show();
	}else{
		$('#panelAlertasValidacion_ip').hide();
		
		/* Defecto 35 ** MODIF TAM (08.09.2021) ***/
		/* deshabilitamos el bot�n Aplicar, para que no le puedan dar mas de una vez, hasta que no termine la primera ejecuci�n*/
		$('#btnAplicar_ip').hide();
		var resValidacion = validacionesImpPol(plan, referencia, usuario, tipoRefPoliza);
	}
}
function validacionesImpPol(plan, referencia, usuario, tipoRefPoliza){
	//validar si la poliza existe en BBDD
	//en SeleccionPolizaManager hay este metodo public final Poliza getPolizaById(final Long idPoliza)
	//creamos una llamada ajax
			
	$.ajax({
		url:          "importacionPolizas.html",
		data:         "method=doIniciarImportacionPoliza&plan=" +plan+ "&referencia=" +referencia+ "&usuario=" +usuario+ "&tipoRefPoliza=" +tipoRefPoliza,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			$.unblockUI();
			alert("Error al iniciar la importacion de la poliza: " + quepaso , 'Error');
		},
		success: function(resultado){
			/* Defecto 35 ** MODIF TAM (08.09.2021) ***/
			/* Volvemos a habilitarlo */
			$('#btnAplicar_ip').show();
			$.unblockUI();
			
			if (resultado.alert != null && resultado.alert != ""){
				var alert = resultado.alert;
				$('#panelAlertasValidacion_ip').html(alert);
				$('#panelAlertasValidacion_ip').show();
				
			}else{
				if (resultado.dato == 'OK'){
					$('#panelImportacionPoliza').hide();
					
					$("#panelMensajeValidacion").html("Poliza cargada correctamente");
					$("#panelMensajeValidacion").show();
				}
					
			}
		},
		beforeSend : function() {
			
			$.blockUI.defaults.message = '<h4><BR>Se procede al alta de la Poliza . Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583'  } });
			
		},
		type: "GET"
	});	
}

function irPortalMediadorPoliza(referencia) {
	$.ajax({
		type : 'POST',
		url : 'portalMedAgroseguro.html',
		data : {
				'method'     : 'doPortalMediadorPoliza',
				'referencia' : referencia
			   },
		async : true,
		dataType : 'json',
		success : function(datos) {
			if (datos.errorMsgs.length > 0) {
				var errorMsg = '';
				for (i = 0; i < datos.errorMsgs.length; i++) {
					errorMsg += datos.errorMsgs[i] + '<br/>';
				}
				if (errorMsg != '') {
					$('#panelAlertasValidacion').html(errorMsg);
					$('#panelAlertasValidacion').show();
				}	
			} else {
				window.open(datos.portalMedUrl, '_blank');
			}
		},
		error : function(jqXHR, exception) {
			if (jqXHR.status === 0) {
	            msg = 'Verifique la conexi\u00F3n.';
	        } else if (jqXHR.status == 404) {
	            msg = 'P\u00E1gina no encontrada [404].';
	        } else if (jqXHR.status == 500) {
	            msg = 'Error interno del servidor [500].';
	        } else if (exception === 'parsererror') {
	            msg = 'Fallo en el tratamiento del JSON esperado.';
	        } else if (exception === 'timeout') {
	            msg = 'Tiempo de espera agotado.';
	        } else if (exception === 'abort') {
	            msg = 'Petici\u00F3n Ajax cancelada.';
	        } else {
	            msg = 'Error no esperado: ' + jqXHR.responseText;
	        }
			$('#panelAlertasValidacion').html(msg);
			$('#panelAlertasValidacion').show();
		}
	});
}

//Limpia todas las alertas mostradas
function limpiaAlertas () {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
}