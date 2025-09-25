$(document)
		.ready(
				function() {

					var URL = UTIL.antiCacheRand(document
							.getElementById("main3").action);
					document.getElementById("main3").action = URL;

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
						inputField : "fechaCargaIni",
						button : "btn_fechaCargaIni",
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
						inputField : "fechaCargaFin",
						button : "btn_fechaCargaFin",
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
						inputField : "fechaRenoIni",
						button : "btn_fechaRenoIni",
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
						inputField : "fechaRenoFin",
						button : "btn_fechaRenoFin",
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
						inputField : "fechaEnvioIBANIni",
						button : "btn_fechaEnvioIBANIni",
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
						inputField : "fechaEnvioIBANFin",
						button : "btn_fechaEnvioIBANFin",
						ifFormat : "%d/%m/%Y",
						daFormat : "%d/%m/%Y",
						align : "Br"
					});
					
					$('#main3')
							.validate(
									{
										errorLabelContainer : "#panelAlertasValidacion",
										onfocusout : function(element) {

										},
										wrapper : "li",
										highlight : function(element,
												errorClass) {
											$("#campoObligatorio_" + element.id)
													.show();
										},
										unhighlight : function(element,
												errorClass) {
											$("#campoObligatorio_" + element.id)
													.hide();
										},
										rules : {
											"fechaCargaFin":{validaFechaPago:['fechaCargaIni','fechaCargaFin']},
											"fechaEnvioIBANFin":{validaFechaEnvioIBAN:['fechaEnvioIBANIni','fechaEnvioIBANFin']}
										},
										messages : {

											"id.codentidad" : {
												required : "El campo Entidad es obligatorio",
												digits : "El campo Entidad solo puede contener d�gitos"
											},
											"oficina.id.codoficina" : {
												digits : "El campo Oficina solo puede contener d�gitos"
											},
											"subentidadMediadora.id.codentidad" : {
												required : "El campo Entidad Mediadora es obligatorio",
												digits : "El campo Entidad Mediadora s�lo puede contener d�gitos"
											},
											"subentidadMediadora.id.codsubentidad" : {
												required : "El campo Subentidad Mediadora es obligatorio",
												digits : "El campo Subentidad Mediadora s�lo puede contener d�gitos",
												range : "Subentidad Mediadora no v�lida"
											},
											"delegacion" : {
												digits : "El campo Delegaci�n s�lo puede contener digitos"
											},
											"pctDescMax" : {
												required : "El campo % Descuento M�ximo es obligatorio.",
												range : "El campo % Descuento M�ximo debe contener un n�mero entre 0 y 100"
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
											},
											"fechasegundopago1":{validaFechaPago:"La Fecha de carga Hasta no puede ser anterior a la Fecha de pago Desde"},
											"fechasegundopago2":{validaFechaEnvioIBAN:"La Fecha de envio IBAN Hasta no puede ser anterior a la Fecha de pago Desde"}
										}
									});
					
					// comprueba que la fecha segundo pago sea posterior a la primera
					jQuery.validator.addMethod("validaFechaPago", function(value, element, params) {
						if(document.getElementById(params[1]).value != null && document.getElementById(params[1]).value != ""){
							//alert(document.getElementById(params[1]).value);
							return (!UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1])));
						}else{
							return true;
						}
					});
					
					// comprueba que la fecha Haste de envioIban sea posterior a la primera
					jQuery.validator.addMethod("validaFechaEnvioIBAN", function(value, element, params) {
						if(document.getElementById(params[1]).value != null && document.getElementById(params[1]).value != ""){
							//alert(document.getElementById(params[1]).value);
							return (!UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1])));
						}else{
							return true;
						}
					});
					
					//Oculta las columnas de Entidad y subentidad mediadora
					$( "td[width='0%']" ).hide();
					
					showOrHideExportIcon();

				});

/** limpia los campos del formulario * */
function limpiar() {
	location.href = "polizasRenovables.run?origenLlamada=menuGeneral&rand=" + getRand();
}


function consultarInicial() {
	
	limpiaAlertas(); 
	var frm = document.getElementById('main3');
	var validacion = validarGrupoEntidad();
	if(!validacion){
		$('#panelAlertasValidacion').html("La Entidad seleccionada no pertenece al grupo de Entidades del usuario");
		$('#panelAlertasValidacion').show();
	}else{
		if(validaFechaCarga()){
			//alert("validaFechaCarga fuera:"+validaFechaCarga);
			$('#panelAlertasValidacion').html("La Fecha Hasta no puede ser anterior o igual a la Fecha Desde");
			$('#panelAlertasValidacion').show();
		}else{
			if(validarConsulta()){
				if(comprobarCampos(false)){
					
					frm.origenLlamada.value= 'primeraBusqueda';
					$("#main3").validate().cancelSubmit = true;
					//alert("consultar inicial");
					//lanzarConsulta();
					
					var s0 = document.getElementById('estadoRenovacionAgroplus');
					frm.estAgroplus.value  = s0.value;
					var s1 = document.getElementById('estadoRenovacionAgroseguro');
					frm.estAgroseguro.value  = s1.value;
					var s2 = document.getElementById('polizaRenovableEstadoEnvioIBAN');
					frm.estEnvioIBAN.value  = s2.value;
					var s3 = document.getElementById('polRenGrupoNegocio');
					frm.grupoNegocio.value  = s3.value;
					
					$('#main3').submit();
				}// Si no, muestra el aviso
				else {
					avisoUnCampo();
				}
			}
		}
	}
}

function consultar() {
	
	limpiaAlertas();
	var validacion = validarGrupoEntidad();
	
	if(!validacion){
		$('#panelAlertasValidacion').html("La Entidad seleccionada no pertenece al grupo de Entidades del usuario");
		$('#panelAlertasValidacion').show();
	}else{
		if(validaFechaCarga()){
			//alert("validaFechaCarga fuera:"+validaFechaCarga);
			$('#panelAlertasValidacion').html("La Fecha Hasta no puede ser anterior o igual a la Fecha Desde");
			$('#panelAlertasValidacion').show();
		}else{
			if(validarConsulta()) {	
				if(comprobarCampos(true)) {
					// $("#btnModificar").hide();
					lanzarConsulta();
				}// Si no, muestra el aviso
				else {
					avisoUnCampo();
				}
			}
		}
	}
}

function validaFechaCarga(){
	var frm = document.getElementById('main3');
	var fechaCargaI     = frm.fechaCargaIni;
	var fechaCargaF     = frm.fechaCargaFin;
	var fechaRenI       = frm.fechaRenoIni;
	var fechaRenF       = frm.fechaRenoFin;
	var fechaEnvioIBANI = frm.fechaEnvioIBANIni;
	var fechaEnvioIBANF = frm.fechaEnvioIBANFin;
	var res = false;
	if(fechaCargaI.value != "" && fechaCargaF.value != ""){
		//alert("validando fechas CARGA: "+fechaCargaI.value+ " "+fechaCargaF.value);
		res = UTIL.fechaMayorQue(fechaCargaI, fechaCargaF);
		//alert(res);
	}
	if(res == false && fechaRenI.value != "" && fechaRenF.value != ""){
		//alert("res dentro:"+res);
		//alert("validando fechas REN: "+fechaRenI.value+ " "+fechaRenF.value);
		res = UTIL.fechaMayorQue(fechaRenI, fechaRenF);
	}
	if(res == false && fechaEnvioIBANI.value != "" && fechaEnvioIBANF.value != ""){
		//alert("res dentro:"+res);
		//alert("validando fechas REN: "+fechaRenI.value+ " "+fechaRenF.value);
		res = UTIL.fechaMayorQue(fechaEnvioIBANI, fechaEnvioIBANF);
	}
	return res;
}


/** OTRAS FUNCIONES */
function limpiaAlertas() {
	
	//$('#alerta').val("");
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();

	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');
	$("#panelAlertas").html('');

}
// Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo() {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}

// Lanza la consulta de fechas de contratacion
function lanzarConsulta() {
	// Llama al metodo que llama al servidor

	onInvokeAction('polRenovables', 'filter');
	$('#listaIdsMarcados').val('');
	
}

function onInvokeAction(id) {
	
	try {
		var to = document.getElementById("adviceFilter");
		to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	}
	catch(e) {}
	
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	$.get('polizasRenovables.run?ajax=true&fechaCargaIni='
			+ frm.fechaCargaIni.value + '&fechaCargaFin='
			+ frm.fechaCargaFin.value + '&fechaRenoIni='
			+ frm.fechaRenoIni.value + '&fechaRenoFin='
			+ frm.fechaRenoFin.value + '&fechaEnvioIBANIni='
			+ frm.fechaEnvioIBANIni.value + '&fechaEnvioIBANFin='
			+ frm.fechaEnvioIBANFin.value + '&grupoNegocio='
			+ frm.polRenGrupoNegocio.value + '&estAgroplus='
			+ frm.estadoRenovacionAgroplus.value + '&'
			+ decodeURIComponent(parameterString), function(data) {
		$("#grid").html(data);
		
		showOrHideExportIcon();
	    
		comprobarChecks();
		
	});
}

function showOrHideExportIcon() {
	
	 var table = document.getElementById("consultaInformeComisionesUnificado");
	    var matchingRow = table.querySelector("tr[id^='consultaInformeComisionesUnificado']");

	    if (matchingRow) {
	        document.getElementById("divImprimir").style.display = "block";
	    } else {
	        document.getElementById("divImprimir").style.display = "none";
	    }
}


function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	
	location.href = 'polizasRenovables.run?ajax=true&fechaCargaIni='+frm.fechaCargaIni.value+'&fechaCargaFin='+frm.fechaCargaFin.value+'&fechaRenoIni='+frm.fechaRenoIni.value+'&fechaRenoFin='+frm.fechaRenoFin.value+'&fechaEnvioIBANIni='+frm.fechaEnvioIBANIni.value+'&fechaEnvioIBANFin='+frm.fechaEnvioIBANFin.value+'&grupoNegocio='+frm.polRenGrupoNegocio.value+'&estAgroplus='+frm.estadoRenovacionAgroplus.value+'&export=true&'  + parameterString;
}

function comprobarCampos(incluirJmesa) {

	if(incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('polRenovables');
	}
	var resultado = false;

	if($('#entidad').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'codentidad', $('#entidad').val());
		}
		resultado = true;
	}
	
	if($('#entmediadora').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'codentidadmed', $('#entmediadora').val());
		}
		resultado = true;
	}
	
	if($('#subentmediadora').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'codsubentmed', $('#subentmediadora').val());
		}
		resultado = true;
	}
	
	if($('#tomador').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'nifTomador', $('#tomador').val());
		}
		resultado = true;
	}
	if($('#plan').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'plan', $('#plan').val());
		}
		resultado = true;
	}
	if($('#linea').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'linea', $('#linea').val());
		}
		resultado = true;
	}
	if($('#referencia').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'referencia', $('#referencia').val());
		}
		resultado = true;
	}
	if($('#colectivo').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'refcol', $('#colectivo').val());
		}
		resultado = true;
	}

	if($('#nifAsegurado').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'nifAsegurado', $('#nifAsegurado').val());
		}
		resultado = true;
	}
		
	if($('#estadoRenovacionAgroplus').val() != '') {
		resultado = true;
	}
		
	if($('#estadoRenovacionAgroseguro').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'estagroseguro', $('#estadoRenovacionAgroseguro').val());
		}
		resultado = true;
	}
	
	if($('#polizaRenovableEstadoEnvioIBAN').val() != '') {
		if(incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('polRenovables', 'estadoIban', $('#polizaRenovableEstadoEnvioIBAN').val());
		}
		resultado = true;
	}
	
	// fechas
	if($('#fechaCargaIni').val() != '' || $('#fechaCargaFin').val() != '' || $('#fechaRenoIni').val() != '' || $('#fechaRenoFin').val() != '' || $('#fechaEnvioIBANIni').val() != '' || $('#fechaEnvioIBANFin').val() != '') {
		resultado = true;
	}
	
	//grupoNegocio
	if($('#polRenGrupoNegocio').val() != '') {
		resultado = true;
	}
	return resultado;
}

function validarGrupoEntidad(){
	var codentidad = $('#entidad').val();
	if($('#grupoEntidades').val() == ""){
		return true;
	}else if(codentidad != ""){
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

function validarConsulta() {
	// Valida el campo 'entidad' si esta informado
	

	if($('#entidad').val() != '') {
		var entidadOk = false;
		try {
			var auxentidad = parseFloat($('#entidad').val());
			if(!isNaN(auxentidad)) {
				$('#entidad').val(auxentidad);
				entidadOk = true;
			}

		} catch(ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if(!entidadOk) {
			//alert(entidadOk);
			$('#panelAlertasValidacion').html("Valor para la entidad no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}

	// Valida el campo 'Tomador' si esta informado
	if($('#tomador').val() != '') {
		var tomadorOk = true;
		/*
		try {
			var valor = parseFloat($('#tomador').val());
			if(!isNaN(valor)) {
				$('#tomador').val(valor);
				tomadorOk = true;
			}
		} catch(ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if(!tomadorOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Tomador no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
		*/
	}
	// Valida el campo 'plan' si esta informado
	if($('#plan').val() != '') {
		var planOk = false;
		try {
			var valor = parseFloat($('#plan').val());
			if(!isNaN(valor)) {
				$('#plan').val(valor);
				planOk = true;
			}
		} catch(ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if(!planOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo Plan no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}

	// Valida el campo 'linea' si esta informado
	if($('#linea').val() != '') {
		var lineaOk = false;
		try {
			var valor = parseFloat($('#linea').val());
			if(!isNaN(valor)) {
				$('#linea').val(valor);
				lineaOk = true;
			}
		} catch(ex) {
		}

		// Si ha habido error en la validacion muestra el mensaje
		if(!lineaOk) {
			$('#panelAlertasValidacion').html(
					"Valor para el campo L�nea no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	// Valida el campo 'poliza' si esta informado
	if($('#referencia').val() != ''){
	 	var referenciaOk = false;
	 	try {		 	
	 		var auxReferencia =  $('#referencia').val();
	 		
	 		
	 		var ref = auxReferencia.substr(0, 1);
	 		if(ref.match(/[a-zA-Z]/)){
				referenciaOk = true;
			}
		}
		catch(ex) {}
	}
	
	// Valida el campo 'colectivo' si esta informado
	if($('#colectivo').val() != '') {
		var colectivoOk = true;
	}

	// Valida el campo 'NIF/CIF Asegurado' si esta informado
	if($('#nifAsegurado').val() != '') {
		var nifAseguradoOk = true;
	}
	
	
	// Valida el campo 'estadosRenAgroplus' si esta informado
	if($('#estadoRenovacionAgroplus').val() != '') {
		var estadosRenAgroplusOk = true;
	}
	
	// Valida el campo 'estadosRenAgroseguro' si esta informado
	if($('#estadoRenovacionAgroseguro').val() != '') {
		var estadosRenAgroseguroOk = true;
	}
	
	// Valida el campo 'polizaRenovableEstadoEnvioIBAN' si esta informado
	if($('#polizaRenovableEstadoEnvioIBAN').val() != '') {
		var polizaRenEnvioIBANOk = true;
	}
	
	// Valida el campo 'polizaRenovableEstadoEnvioIBAN' si esta informado
	if($('#polRenGrupoNegocio').val() != '') {
		var polRengrupoNegocioOk = true;
	}
	return true;
}


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


//GASTOS MASIVOS*************************************************



function getRowsChecks(){
	 
 var result; 
 var rows             = $('#listaIdsMarcados').find('tr').get();
 var localizacion     = "";
 var aux_localizacion = "";
 var count_cm         = 0;
 var result           = true;
 var checked_poliza = ""; // ejemplo: codigo_parcela1@@codigo_parcela2 ...
 
 for(i = 0, j = rows.length; i < j; ++i){     
	      cells = rows[i].getElementsByTagName('td');
	      if(cells.length > 0){ 
		       if(cells[0].innerHTML.indexOf("P@P") != -1){            
                 var check2 = $(cells[15]).children("input:first");
                 if($(check2).attr('checked')){                        
                     if(count_cm == 0){  
                   	  checked_poliza = ($(cells[0]).children("#idRow_cm")).attr('value');                               
                     }else{
                   	  checked_poliza = checked_poliza + "@@" + ($(cells[0]).children("#idRow_cm")).attr('value');
                     }
                     count_cm ++
                 }
		       }
	       }
}

return checked_poliza;
}

function getUbicacionPrimeraPolizaChecked(){
 var rows             = $('#listaIdsMarcados').find('tr').get();
 var localizacion     = "";
 var aux_localizacion = "";
 var count_cm         = 0;
 var result           = true;
 var checked_poliza = "";
 
 for(i = 0, j = rows.length; i < j; ++i)
 {     
	      cells = rows[i].getElementsByTagName('td');
	      if(cells.length > 0){ 
		       if(cells[0].innerHTML.indexOf("P@P") != -1) {            
                 var aaa2   = $(cells[0]).children("#localizacion_cm") 
                 var check2 = $(cells[15]).children("input:first");
                 if($(check2).attr('checked')) {                                                    
                         return $(aaa2).val();
                 }
		       }
	       }
}//for
}	 





function validateFormCambioMasivo(){
return $('#main3').valid();
} 
function havePolizaSele(){

var itemsChecked = $('#listaIdsMarcados').val();
if(itemsChecked.length > 0)
  return true;
else
  return false;
}   





function cerrarPopUpAvisos(){
 	$('#divAviso').hide();
 	$('#divAcuse').hide();
 	$('#overlay').hide();
 }



 function abrirPopUpGastosMasivo(isPerfil0){
 	$('#overlay').show();
 	var ids = $('#listaIdsMarcados').val();
 	if(ids !=null && ids.length>0) {
 		ajaxCheckGastosMasivo(ids,isPerfil0);
 	 	
 	
 	}else{
 		$('#panelInformacion2').html("No hay p�lizas seleccionadas.");
		$('#panelInformacion2').show();
 		$('#txt_info').hide();
 		$('#divAviso').show();
 		$('#panelInfor').show();	
 		$('#txt_info_none').show();
 	}
 }

 function cerrarPopUpGastosMasivo(){
 	$('#divRenovablesMasivo').hide();
 	$('#overlay').hide();
 }
 
 
 /* Pet. 63482 ** MODIF TAM (16.04.2021) ** Inicio */
 function abrirPopUpAltaRenovable(){
		$('#panelAlertasValidacion').html("");
		$('#panelAlertasValidacion').hide();
		$('#panelAlertasValidacion_altaPolizaRen').html("");
		$('#panelAlertasValidacion_altaPolizaRen').hide();
		
		/* Limpiamos el contenido de los campos de la ventana */
		$('#plan_renov').val('');
		$('#linea_renov').val('');
		$('#referencia_renov').val('');
		$('#desc_linea_renov').val('');
		
		$('#divAltaPolizaRenovable').show();
 }
 
 function cerrarPopUpAltaRenovable(){
	 	$.unblockUI();
	 	$('#divAltaPolizaRenovable').hide();
 }

 function doAtaPolizaRenovable(){
	if ($('#plan_renov').val() == '' ||  $('#linea_renov').val() == '' || $('#referencia_renov').val() == '') {
		//cerrarPopUpAltaRenovable();
		$('#panelAlertasValidacion_altaPolizaRen').html("Debe rellenar todos los datos para proceder al alta.");
		$('#panelAlertasValidacion_altaPolizaRen').show();
	} else {
		
		var plan = $('#plan_renov').val();
		var linea =  $('#linea_renov').val();
		var ref =$('#referencia_renov').val();
		
		/* Defecto 35 ** MODIF TAM (08.09.2021) ***/
		/* deshabilitamos el bot�n Aplicar, para que no le puedan dar mas de una vez, hasta que no termine la primera ejecuci�n*/
		$('#btnAplicar_ren').hide();
		
		ajaxAltaPolizasRenovables(plan, linea, ref);
	 	 	
	 }
		
 }
 
 function ajaxAltaPolizasRenovables(plan, linea, ref){
	//validar si la poliza existe en BBDD
	//en SeleccionPolizaManager hay este metodo public final Poliza getPolizaById(final Long idPoliza)
	//creamos una llamada ajax
			
	$.ajax({
		url:           "polizasRenovables.run",
		data:         "method=doAltaPolizaRenovable&plan_renov=" +plan+ "&linea_renov=" +linea+ "&referencia_renov=" +ref,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		success: function(resultado){
			/* Defecto 35 ** MODIF TAM (08.09.2021) ***/
			/* Volvemos a habilitarlo */
			$('#btnAplicar_ren').show();

			$.unblockUI();
			if (resultado.alert != null && resultado.alert != ""){
				var alert = resultado.alert;
				$('#panelAlertasValidacion_altaPolizaRen').html(alert);
				$('#panelAlertasValidacion_altaPolizaRen').show();
			}else{
				if (resultado.dato == 'OK'){
					//resoluci�n de Incidencias
					$('#plan_ip').val("");
					$('#poliza_ip').val("");
					$('#usuario_ip').val("");
					$('#tipoRefPoliza_ip').val("");

					$('#divAltaPolizaRenovable').hide();
					$("#panelMensajeValidacion").html("Poliza cargada correctamente");
					$("#panelMensajeValidacion").show();
				}
					
			}
		},
		beforeSend : function() {
			$.blockUI.defaults.message = '<h4><BR>Se procede al alta de la p�liza Renovable. Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583'  } });
		},
		error: function(objeto, quepaso, otroobj){
			alert("Error al iniciar la importacion de la poliza: " + quepaso , 'Error');
		},

		type: "GET"
	});
 }
 
 /* Pet. 63482 ** MODIF TAM (16.04.2021) ** Fin */
 
//Realiza la llamada al controlador encargado de validar si las polizas indicadas se pueden cambiar el gasto
 function ajaxCheckGastosMasivo(ids,isPerfil0){
 	
 	$.ajax({
 		url:          "polizasRenovables.run",
 		data:         "method=doAjaxCheckGastosMasivo&idPlz="+ids,
 		async:        true,
 		contentType:  "application/x-www-form-urlencoded",
 		dataType:     "text",
 		global:       false,
 		ifModified:   false,
 		processData:  true,
 		error: function(objeto, quepaso, otroobj){
 			alert("Error al comprobar el estado de las p�lizas que se quieren cambiar su gasto: " + quepaso);
 		},
 		success: function(datos){
 			// Las polizas se pueden borrar
 			if (datos == 'true'){
 				$('#comisionMasivo').val("");
 				$('#txt_info').show();
 				$('#divRenovablesMasivo').show();
 		 		$('#panelAlertasValidacion_d').html("");
 		 		$('#panelAlertasValidacion_d').hide();
 		 		if (isPerfil0){
 		 			$('#isPerfil0').val("true");
 		 			$('#cambioMasivoTitulo').html("Asignar Comisiones - Perfil 0");
 		 		}else{
 		 			$('#isPerfil0').val("false");
 		 			$('#cambioMasivoTitulo').html("Asignar Comisiones");
 		 		}
 		 		
 		 		//alert($('#isPerfil0').val());
 		 		
 		 		$('#divRenovablesMasivo').show();	
 		 		
 			}			
 			else{ // Alguna de las polizas no se puede modificar
 				// Muestra el mensaje de aviso
 				$('#panelInformacion2').html("No se pueden asignar gastos a p�lizas en estado 'Comunicaci�n definitiva' o 'Emitida'.");
 				$('#panelInformacion2').show();
 				$('#txt_info').hide();
 		 		$('#divAviso').show();
 		 		$('#panelInfor').show();	
 		 		$('#txt_info_none').show();
 			}
 		},
 		type: "GET"
 	});
 }
 
 //******************************************** POPUPs ENVIO IBAN ********************************************
 function abrirPopUpEnvioIBAN(){
	 	$('#overlay').show();
	 	jQuery("#selEnvioIBAN1").attr('checked', true);
	 	var ids = $('#listaIdsMarcados').val();
	 	if(ids !=null && ids.length>0) {
	 	 	$('#divRenEnvioIBAN').show();
	 		$('#panelAlertasValidacion_envioIBAN').html("");
	 		$('#panelAlertasValidacion_envioIBAN').hide();
	 		$('#divRenEnvioIBAN').show();
	 	
	 	}else{
	 		$('#txt_info').hide();
	 		$('#panelInformacion2').html("No hay p�lizas seleccionadas.");
			$('#panelInformacion2').show();
	 		$('#divAviso').show();
	 		$('#panelInfor').show();
	 		$('#txt_info_none').show();
	 	}
 }

 function cerrarPopUpEnvioIBAN(){
 	$('#divRenEnvioIBAN').hide();
 	$('#divRenResEnvioIBAN').hide();
 	$('#overlay').hide();
 }
 
 function continuarEnvioIBAN(){
    $('#divRenEnvioIBAN').hide();
	$('#idPlzEnvioIBAN').val($('#listaIdsMarcados').val())
	$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	cargaFechas_eIBAN();
	hidePopUpAviso()
	$('#frmRenEnvioIBAN').submit();
 }
 function continuarResEnvioIBAN(){
    $('#divRenResEnvioIBAN').hide();
	$('#idsResPlzEnvioIBAN').val($('#listaIdsMarcados').val())
	$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	cargaFechas_res_IBAN();
	hidePopUpAviso()
	var frm = document.getElementById('frmRenResEnvioIBAN');
	frm.method.value="doModificarEstadoEnvioIBAN";
	frm.submit();
}

function verErroresEnvioIBAN(){
	var frm = document.getElementById('frmVerErroresEnvioIBAN');
	frm.method.value="doVerErroresValidacionEnvioIBAN";
	frm.target="_blank";
	frm.submit();
} 
 
 function cargaFechas_eIBAN(){
	var frm    = document.getElementById('main3');
	var frm_e = document.getElementById('frmRenEnvioIBAN');
	frm_e.fecCargaIni_e.value     = frm.fechaCargaIni.value;
	frm_e.fecCargaFin_e.value     = frm.fechaCargaFin.value;
	frm_e.fecRenoIni_e.value      = frm.fechaRenoIni.value;
	frm_e.fecRenoFin_e.value      = frm.fechaRenoFin.value;
	frm_e.fecEnvioIBANIni_e.value = frm.fechaEnvioIBANIni.value;
	frm_e.fecEnvioIBANFin_e.value = frm.fechaEnvioIBANFin.value;
	frm_e.estadoRenovacionAgroplus_e.value = frm.estadoRenovacionAgroplus.value;
	frm_e.polRenGrupoNegocio_e.value = frm.polRenGrupoNegocio.value;	
 }
 
 function cargaFechas_AltaRen(){
		var frm    = document.getElementById('main3');
		var frm_e = document.getElementById('frmRenEnvioIBAN');
		frm_e.fecCargaIni_e.value     = frm.fechaCargaIni.value;
		frm_e.fecCargaFin_e.value     = frm.fechaCargaFin.value;
		frm_e.fecRenoIni_e.value      = frm.fechaRenoIni.value;
		frm_e.fecRenoFin_e.value      = frm.fechaRenoFin.value;
		frm_e.fecEnvioIBANIni_e.value = frm.fechaEnvioIBANIni.value;
		frm_e.fecEnvioIBANFin_e.value = frm.fechaEnvioIBANFin.value;
		frm_e.estadoRenovacionAgroplus_e.value = frm.estadoRenovacionAgroplus.value;
		frm_e.polRenGrupoNegocio_e.value = frm.polRenGrupoNegocio.value;	
	 }
 
 function cargaFechas_res_IBAN(){
	var frm    = document.getElementById('main3');
	var frm_res = document.getElementById('frmRenResEnvioIBAN');
	frm_res.fecCargaIni_res.value     = frm.fechaCargaIni.value;
	frm_res.fecCargaFin_res.value     = frm.fechaCargaFin.value;
	frm_res.fecRenoIni_res.value      = frm.fechaRenoIni.value;
	frm_res.fecRenoFin_res.value      = frm.fechaRenoFin.value;
	frm_res.fecEnvioIBANIni_res.value = frm.fechaEnvioIBANIni.value;
	frm_res.fecEnvioIBANFin_res.value = frm.fechaEnvioIBANFin.value;
	frm_res.estadoRenovacionAgroplus_res.value = frm.estadoRenovacionAgroplus.value;
	frm_res.polRenGrupoNegocio_res.value = frm.polRenGrupoNegocio.value;
}
 
 function abrirPopUpResEnvioIBAN(){
 	$('#overlay').show();
 	$('#divRenResEnvioIBAN').show();
	$('#panelAlertasValidacion_res_envioIBAN').html("");
	$('#panelAlertasValidacion_res_envioIBAN').hide();
	$('#divRenResEnvioIBAN').show();
}
 
 function verAcuseRecibo(idPolRen){
	$.ajax({
        url:          "polizasRenovables.run",
        data:         "method=doVerAcuseReciboGastos&idPolRen="+idPolRen,
        async:        true,
        contentType:  "application/x-www-form-urlencoded",
        dataType:     "text",
        global:       false,
        ifModified:   false,
        processData:  true,
        error: function(objeto, quepaso, otroobj){
            alert("Error al visualizar el acuse de recibo: " + quepaso);
        },
        success: function(acuseRecibo){
        	if (acuseRecibo != ""){ // mostrar acuse en Popup
        		$('#overlay').show();
        		$('#txt_info_acuse').html(acuseRecibo);
		 		$('#txt_info').hide();
		 		$('#panelInformacion2').show();
		 		$('#divAcuse').show();
		 		$('#panelInfor').show();
		 		$('#txt_info_acuse').show();
        	}
        },
        type: "GET"
    });
}
 
 // FIN ******************************************** POPUPs ENVIO IBAN ********************************************
 
 function comprobarChecks(){	
	var listaIdsMarcados = $('#listaIdsMarcados').val();
	var cadena=[];
	cadena= listaIdsMarcados.split(",");
	
	if(listaIdsMarcados.length>0){
		$("input[type=checkbox]").each(function(){
			if( $("#marcaTodos").val() == "true" ){
				if($(this).attr('id') != "checkTodos"){
					$(this).attr('checked',true);
				}
			}
			else{
				for(var i=0;i<cadena.length -1;i++){
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
			
			for(var i=0;i<cadena.length -1;i++){
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
	
	function cargaFechas_cm(){
		var frm    = document.getElementById('main3');
		var frm_cm = document.getElementById('frmRenovablesMasivo');
		frm_cm.fecCargaIni_cm.value     = frm.fechaCargaIni.value;
		frm_cm.fecCargaFin_cm.value     = frm.fechaCargaFin.value;
		frm_cm.fecRenoIni_cm.value      = frm.fechaRenoIni.value;
		frm_cm.fecRenoFin_cm.value      = frm.fechaRenoFin.value;
		frm_cm.fecEnvioIBANIni_cm.value = frm.fechaEnvioIBANIni.value;
		frm_cm.fecEnvioIBANFin_cm.value = frm.fechaEnvioIBANFin.value;
		frm_cm.estadoRenovacionAgroplus_cm.value = frm.estadoRenovacionAgroplus.value;
		frm_cm.polRenGrupoNegocio_cm.value = frm.polRenGrupoNegocio.value;
	}
	
	
	
	function actualizarPorcentajeComisiones(){
		if(validaCampo()){		
			if(validaRangoComisionMasivo()){
				$('#idPlz').val($('#listaIdsMarcados').val())
				$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				cargaFechas_cm();
				hidePopUpAviso()
				$('#frmRenovablesMasivo').submit();
			}
		}
	}
	
	 function hidePopUpAviso(){
			$('#divRenovablesMasivo').hide();
			$('#overlayCambioMasivo').hide();
			$('#overlay').hide();
	}
	
	function validaRangoComisionMasivo(){
		
		if(parseFloat($('#comisionMasivo').val())>= 0 && parseFloat($('#comisionMasivo').val()) <= 90){
			return true;
		}
		else{
			$('#panelAlertasValidacion_d').html("El valor de '% Comisi&oacute;n' debe estar entre 0 y 90");
			$('#panelAlertasValidacion_d').show();
			return false;	
		}
		
	}


	function validaCampo(){
		if($('#comisionMasivo').val() != ''){ 
			var comisionMasivoOk = false;
		 	try {		 	
		 		var auxComisionMasivo =  $('#comisionMasivo').val();
		 		if(!isNaN(auxComisionMasivo)){
		 			$('#comisionMasivo').val(auxComisionMasivo);
		 			comisionMasivoOk = true;
				}
			}
			catch(ex) {
			}
			
			// Si ha habido error en la validaci�n muestra el mensaje
			if(!comisionMasivoOk) {
				$('#panelAlertasValidacion_d').html("Valor para '% Comisi&oacute;n' no v&aacute;lido");
				$('#panelAlertasValidacion_d').show();
				return false;
			}
			
		}
		else{
			$('#panelAlertasValidacion_d').html("El campo '% Comisi&oacute;n' es obligatorio");
			$('#panelAlertasValidacion_d').show();
			return false;
		}
		
		return true
		
	}
	
	function showOrHideExportIcon() {
		
		 var table = document.getElementById("polRenovables");
		 var matchingRow = table.querySelector("tr[id^='polRenovables']");

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