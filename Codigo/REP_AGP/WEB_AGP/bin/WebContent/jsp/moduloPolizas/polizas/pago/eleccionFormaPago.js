$(document).ready(function(){
	muestraDatosCuenta();
	muestraDatosPaneles();
	asignaIbanAseguradoAPagoManual();
	
	if ($('#modoLectura').val() != 'modoLectura'){
		// Calendario de Fecha pago
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
	        inputField        : "tx_fechaPago",
	        button            : "btn_fechaPago",
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
	        inputField        : "fecha1",
	        button            : "btn_fechaprimerpagomodificado",
	        ifFormat          : "%d-%m-%Y",
	        daFormat          : "%d-%m-%Y",
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
	        inputField        : "fechasegundopago1",
	        button            : "btn_fechasegundopagomodificado",
	        ifFormat          : "%d-%m-%Y",
	        daFormat          : "%d-%m-%Y",
	        align             : "Br"
      	});      	
      	
		if($('#idLineaContratSuperior2019').val() != "true"){
			if($('#isLineaGanado').val()=="true"|| $('#permiteEnvIban').val()=="true"){
			
				if($('#isPolizaFinanciada').val()=="true"){
					activaDomiciliacion(true);
					$('#envioIBANAgr').val('S');
					$('#envioIBANAgr').attr('checked', true);
					habilitaTitularDomic("A");
				}else{
					activaDomiciliacion($('#envioIBANAgr'));
					habilitaTitularDomic($('#destinatario').val());	
				}
			}
		}
		
		// si es modo edicion y hay datos ya guardado de pago, el porcentaje primer pago es 100 y la fecha primer pago es menor que la actual, la fecha sera fecha actual.
		if($('#tipoPagoGuardado').val()=="cargoEnCuenta" || $('#tipoPagoGuardado').val()=="manual"){			
			if ((parseInt($('#pctprimerpagocliente').val()) == 100) && (UTIL.fechaMenorOIgualQueFechaActual($('#fecha1').val()))){
				var fechaActual = new Date();
				fechaFormateada = getFormattedDate(fechaActual);
				$('#fecha1').val(fechaFormateada);
			} 
		}
		// Pet. 22208 ** Inicio - Tatiana (20.02.2018) //
		
		if ($('#pagada').val()=='S'){
			
			// Bloqueamos los datos de Domiciliacion agroseguro
			$('#domiciliacionAgroId').attr("disabled", true);
			
			// Bloqueamos los datos de Pago manual
			$("#datosCuentaId").attr("disabled", true);
						
			// Bloqueamos los datos del pago en Cuenta
			$('#pagoManualId').attr("disabled", true);
			disabledPagoManual(true);
			
			//Bloqueamos los datos de Forma de Pago cliente
			// MODIF TAM (10.04.2018) - Resolucion de Incidencias * I *
			//$("#divFormaPagoCliente").css("display",'none');
			$("#divFormaPagoCliente").css("display",'');
			disabledFormaPagoCliente(true);			
			// MODIF TAM (10.04.2018) - Resolucion de Incidencias * F *

			// ocultamos la lupa del banco Dettino
			$("#lupaBancoDestino").hide();
			$("#btn_fechaPago").hide();
			
			if($('#tipoPagoGuardado').val()=="cargoEnCuenta" ){//0 Cargo en cuenta
				$("#datosCuentaId").attr("checked", true);
			}else if($('#tipoPagoGuardado').val()=="manual"){//1 Pago manual
				$('#pagoManualId').attr("checked", true);
			}
		} 
		
		// Pet. 54046 ** MODIF TAM (16.07.2018) ** Inicio //
		// Si se trata de una poliza Complementaria los campos de envio de Iban a Agroseguro 
		//deberian tener los mismos valores que la poliza principal y no ser modificables. //
		if($('#esPolPrincipal').val()!="true"){
			
			if($('#idLineaContratSuperior2019').val() != "true"){
				
				//mostramos los datos de la principal si el check esta marcado
				if ($('#indEnvIbanCpl').val()=='S'){
					$('#envioIBANAgr').attr('checked', true);
					activaDomiciliacion($('#envioIbanAgro'));
					habilitaTitularDomic($('#destinatario').val());
					$(".literalC").show();
	
				}else{
	  			    $(".literalC").hide();
				}
				
				
				$('#envioIBANAgr').attr('disabled', true);
				$('#titularCta').attr('disabled', true);
				$('#destinatario').attr('disabled', true);
			}else{
				/* Si la poliza es Complementaria  y la principal tiene el indicador de EnvIban activado, lo activamos pr
				/* por defecto */
				if ($('#indEnvIbanCpl').val()=='S'){
					$("#domiciliacionAgroId").attr("checked", true); //radiobutton de domiciliacion agroseguro
				}
			}
		}
		
		// el nuevo botonn de Sw Confirmacion solo estaria visible si el campo de SwConfirmacion
		// esta con una 'S'
		if ($("#swConfirmacion").val() =='S'){
		   $("#btnSWConfirmacion").show();
		}else{
		   $("#btnSWConfirmacion").hide();
		}		
		// Pet. 22208 ** Fin - Tatiana (20.02.2018) //	
		
		if($('#tipoPagoGuardado').val()!='cargoEnCuenta') {
			$("#divFormaPagoCliente").css("display",'none');
		}
	}else{  // MODO LECTURA
		
		disabledPagoManual(true);
		if($('#tipoPagoGuardado').val()=='cargoEnCuenta'){// Cargo en cuenta
			$("#divFormaPagoCliente").css("display",'');
			
			frm = document.getElementById('pasarADefinitiva');
			frm.datosCuentaId.checked = true;
			
			
			var cManual = document.getElementById("tablaPagoManual");
			cManual.style.display = "none";	
			$(".ccOficina").show();
		}else if($('#tipoPagoGuardado').val()=='manual'){//1 Pago manual
			$("#divCargoCuentaCompleto").html("<table id='tablaNoDisponible' align='center' style='height:100px;'><tr><td class='literal' align='center'></td><td class='literal'>"+
			"<input type='hidden' name='fecha1' id='fecha1'/><input type='hidden' name='fechasegundopago1' id='fechasegundopago1'/><input type='hidden' name='pctprimerpagocliente' id='pctprimerpagocliente'/>"+
			"<input type='hidden' name='pctsegundopagocliente' id='pctsegundopagocliente'/><input type='hidden' name='importeSegundoPagoCliente' id='importeSegundoPagoCliente'/><td></tr></table>");

			$(".ccOficina").hide();
			
			/* ESC-6483 ** MODIF TAM (10/12/2019) */
			frm = document.getElementById('pasarADefinitiva');
			frm.pagoManualId.checked = true;
			/* ESC-6483 ** MODIF TAM (10/12/2019) Fin */
			
		}else{			
			var manual = document.getElementById("tablaPagoManual");
			manual.style.display = "none";			
			var cCta = document.getElementById("tablaCargoCta");
			if(cCta != null){
				cCta.style.display = "none";
			}
			$(".ccOficina").hide();
		}

		$("input[type=radio]").each(function(){
			$(this).attr('disabled',true);
		});
		
		if($('#isLineaGanado').val()=="true"|| $('#permiteEnvIban').val()=="true"){
			activaDomiciliacion($('#envioIBANAgr'));
			habilitaTitularDomic($('#destinatario').val());			
			 $(".literalC").attr('disabled', true);
			 //$("sumarc").attr('disabled', true);
			 $('#envioIBANAgr').attr('disabled', true);
			 $('#titularCta').attr('disabled', true);
		}
		
		//en modo lectura muestro los botones imprimir e imprimir reducida
		$("#btnImprimirReducida").css("display",'');
		$("#btnImprimir").css("display",'');
	}
	
	if ($('#bancoDestino').val()!=""){
		var valFormat = formatCerosIzda($('#bancoDestino').val(), "000", 4);
		$('#bancoDestino').val(valFormat);
		
	}	

	function getFormattedDate(date) {
		  var year = date.getFullYear();
		  var month = (1 + date.getMonth()).toString();
		  month = month.length > 1 ? month : '0' + month;
		  var day = date.getDate().toString();
		  day = day.length > 1 ? day : '0' + day;
		  return day + '/' + month + '/' + year;
	}
	
	$('#pasarADefinitiva').validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		rules: {
			// atributos forma pago manual
			"banco":{required: true,digits:true},
			"importePago":{required:true,number:true},
			"fecha" : {required:true,dateITA: true,validaFechaPagoManual:['tx_fechaPago']},
			// atributos comunes a ambas formas de pago
			"oficina":{required:true,digits:true}		
		},
		messages: {
			// atributos forma pago manual
			"banco":{required: "El campo Banco Destino es obligatorio",digits: "El campo Banco Destino solo puede contener d\u00EDgitos"},
			"importePago":{required: "El campo Importe es obligatorio", number:"El campo Importe es de tipo num\u00E9rico decimal"},
			"fecha" : {required:"El campo Fecha Pago es Obligatorio.", dateITA: "El formato del campo Fecha Pago pago es dd/mm/YYYY",validaFechaPagoManual:"La Fecha Pago no puede ser posterior a la fecha actual"},
			// atributos comunes a ambas formas de pago
			"oficina" : {required:"El campo Oficina Propietaria es Obligatorio.", digits: "El campo Oficina Propietaria solo puede contener d\u00EDgitos"}			
		}
	});
	
	$('#validacionPagoCuenta').validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		rules: {
			//atributos forma pago cliente
			"pctprimerpagoVal":{required: true,digits:true, range: [1,100]},
			"pctsegundopagoVal":{required: function(element) {return parseInt($("#pctprimerpagocliente").val()) < 100;},digits:true, range: [1,99], comprobarPagos: ['pctprimerpagocliente','pctsegundopagocliente']},
			"fecha1Val":{fechaRellena: ['pctprimerpagocliente','fecha1'],dateITA: true,validaFechaPrimerPagoCliente:['fecha1'],validaFechaLimiteUsuario:['fecha1','fechalimiteUsuario']},
			"fechasegundopago1Val":{fechaRellena: ['pctsegundopagocliente','fechasegundopago1'],dateITA: true,validaFechaPago:['fecha1','fechasegundopago1']},
			// atributos comunes a ambas formas de pago
			"oficinaVal":{required:true,digits:true}
			
		},
		messages: {
			//atributos forma pago cliente
			"pctprimerpagoVal":{required: "El campo % Primer pago es obligatorio",digits: "El campo % Primer pago solo puede contener d\u00EDgitos", range: "El campo % Primer pago debe contener un n\u00FAmero entre 1 y 100"},
			"pctsegundopagoVal":{required: "El campo % Segundo pago es obligatorio",digits: "El campo % Segundo pago solo puede contener d\u00EDgitos",  range: "El campo % Segundo pago debe contener un n\u00FAmero entre 1 y 99", comprobarPagos: "La suma de los pagos debe ser igual a 100"},
			"fecha1Val": {fechaRellena:"El campo Fecha Primer Pago es Obligatorio",dateITA: "El formato del campo Fecha Primer Pago es dd/mm/YYYY",validaFechaPrimerPagoCliente:"La Fecha Primer Pago Cliente no puede ser inferior a la fecha actual", validaFechaLimiteUsuario: "La fecha de pago est\u00E1 fuera del l\u00EDmite establecido"},
			"fechasegundopago1Val":{fechaRellena:"El campo Fecha Segundo Pago es Obligatorio",dateITA: "El formato del campo Fecha Segundo Pago es dd/mm/YYYY",validaFechaPago:"La Fecha Segundo Pago no puede ser inferior o igual a la Fecha Primer Pago"},
			// atributos comunes a ambas formas de pago
			"oficinaVal" : {required:"El campo Oficina Propietaria es Obligatorio.", digits: "El campo Oficina Propietaria solo puede contener d\u00EDgitos"}
		}
	});
	
		
	jQuery.validator.addMethod("validaFechaPagoManual", function(value, element) {
		return (this.optional(element) || UTIL.fechaMenorOIgualQueFechaActual(element.value));
	});
	
	jQuery.validator.addMethod("validaFechaPrimerPagoCliente", function(value, element) {
		return (this.optional(element) || UTIL.fechaMayorOIgualQueFechaActual(element.value));
	});
	
	//comprueba que la suma del porcentaje de los pagos es 100
	//params--> los pagos
	jQuery.validator.addMethod("comprobarPagos", function(value, element, params) {	
	  return (this.optional(element) || parseInt($('#'+params[0]).val())+parseInt($('#'+params[1]).val()) == 100);
	});
	
	// comprueba que la fecha segundo pago sea posterior a la primera
	jQuery.validator.addMethod("validaFechaPago", function(value, element, params) {
		if (document.getElementById(params[1]).value != null && document.getElementById(params[1]).value != ""){
			return (!UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1])));
		}else{
			return true;
		}
	});
	
	
	// comprueba que si se ha metido valor en el % del primer pago, la fecha se rellene
	jQuery.validator.addMethod("fechaRellena", function(value, element, params) {
		
		if ($('#'+params[0]).val() != null && $('#'+params[0]).val() !=""){ // si el % esta relleno
			  if ($('#'+params[1]).val() != null && $('#'+params[1]).val() !=""){ //si la fecha esta rellena
				  return true ;
			  }else{ // si la fecha esta vacia
				  return  false ;
			  }
		  }
		  return  true ;
	});
	
	jQuery.validator.addMethod("validaFechaLimiteUsuario", function(value, element,params) {
		var frm = document.getElementById('pasarADefinitiva');
		var fecha1 = document.getElementById(params[0]).value;
		var fecha2 = document.getElementById("fechasegundopago1").value;
		var fechaLimite = document.getElementById(params[1]).value;
		
		
		var primerPag = $('#pctprimerpagocliente').val();
		var pctPrimPagoCol = frm.pctPrimerPColectivo.value;
		if (parseInt(primerPag) == 100 ||(primerPag == pctPrimPagoCol)){
			return true; //NO FINANCIADA
		}else{
			if (fechaLimite != null && fechaLimite !=""){ // si la fecha limite no esta vacia
				if (fecha1 != null && fecha1 !=""){ //si la fecha1 no esta vacia
					if (!UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1]))){
						if (fecha2 != null && fecha2 !=""){ //si la fecha2 no esta vacia
							return (!UTIL.fechaMayorOIgualQue(document.getElementById("fechasegundopago1"), document.getElementById(params[1])));
						}
						return true; //fecha2 vacia
					}else{
						return false;
					}
				}else{
					return true; //fecha1 vacia
				}		
			}else{
				return true; //fechaLimite vacia
			}
		}
	});
	
	
	
	// comprueba que la fecha segundo pago CLIENTE sea posterior a la primera
	jQuery.validator.addMethod("validaFechaPago", function(value, element, params) {
		if (document.getElementById(params[1]).value != null && document.getElementById(params[1]).value != ""){
			return (!UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1])));
		}else{
			return true;
		}
	});
	
  	calculaImportePrimerPago();
  	calculaImporteSegundoPago();
  	
  	
  	/*DNF 07/03/2021 PET.70105.FII. Si la poliza es linea contratacio super a 2021 y complementario se deshabilitan los combos*/
  	//deshabilitarOpcFormaPago();
  	/*FIN DNF 07/03/2021 PET.70105.FII*/
});

function deshabilitarOpcFormaPago(){
	
	var esPpal = $('#esPolPrincipal').val();
	var lineaContrataSup2021 = $('#lineaContrataSup2021').val();
	
	if(esPpal != "true" && lineaContrataSup2021 == "true"){
		
		disabledPagoManual(true);
		disabledFormaPagoCliente(true);
		
		$('#domiciliacionAgroId').attr("disabled", true);
		$('#datosCuentaId').attr("disabled", true);
		$('#pagoManualId').attr("disabled", true);
	}
}

/** Pet. 22208 ** MODIF TAM (27.02.2018) ** Inicio ***/
/** Incluimos una nueva funcion para solicitar la autorizacion del cliente para enviar el SW de alta*/
function confirmaSW(){
	
	jConfirm(' <strong>Atenci\u00F3n!</strong> Se va a confirmar la p\u00F3liza con Agroseguro y no podr\u00E1 ser modificada a posteriori.'+
			' <center>\u00BFEst\u00E1 seguro de que desea continuar?</center>', 'Di\u00E1logo de Confirmaci\u00F3n', function(r) {
		 if (r == true){
			 blockUIPasoADefinitiva();
			 ajax_check_Cpl_Sbp(true);
	     }
	});
}


// operaciones en Forma pago cliente
function calculaImportePrimerPago(){				
	var pct = document.getElementById('pctprimerpagocliente').value;
	var importe = document.getElementById('importeTotal').value;
	if (pct != '' && importe != '') {
		pct =  document.getElementById('pctprimerpagocliente').value;
		if(isNaN(eval(pct*importe/100))){
			document.getElementById('importePrimerPagoCliente').value = '0';
		}else{
			document.getElementById('importePrimerPagoCliente').value = eval(trunc(pct*importe/100,3));
		} 
	}
}

function calculaImporteSegundoPago(){
	var pct = document.getElementById('pctsegundopagocliente').value;
	var importe = document.getElementById('importeTotal').value;
	if (pct != '' && importe != '') {
		pct =  document.getElementById('pctsegundopagocliente').value;
		if(isNaN(eval(pct*importe/100))){
			document.getElementById('importeSegundoPagoCliente').value = '0';
		}else{
			document.getElementById('importeSegundoPagoCliente').value = eval(trunc(pct*importe/100,3));
		}
	}else if (pct == ''){
		document.getElementById('importeSegundoPagoCliente').value = '';
		document.getElementById('fechasegundopago1').value = '';
		
	}
}

function trunc(num, ndec) { 
  var fact = Math.pow(10, ndec); // 10 elevado a ndec 

  /* Se desplaza el punto decimal ndec posiciones,se trunca el numero y se vuelve a colocar el punto decimal en su sitio. */ 
  return parseInt(num * fact) / fact; 
}

function rellenaPctSegundoPago(pctPrimerPago){
	if (pctPrimerPago.value < 100){
		$('#pctsegundopagocliente').val(100 - pctPrimerPago.value);
		calculaImporteSegundoPago();
	}else if (pctPrimerPago.value == 100){
		$('#pctsegundopagocliente').val('');
		$('#fechasegundopago1').val('');
		$('#importeSegundoPagoCliente').val('');
	}
	
}

function rellenaPctPrimerPago(pctSegundoPago){
	if (pctSegundoPago.value <= 100){
		$('#pctprimerpagocliente').val(100 - pctSegundoPago.value);
		calculaImportePrimerPago();
	}
}


// Fin operaciones forma pago cliente

function muestraDatosCuenta(){
	$("#divFormaPagoCliente").css("display",'');

	if ($.trim($('#numeroCuenta').val())==""){
		$('#lbl_valor_IBAN').html("<span> El asegurado no tiene cuenta asignada</span>");
		
	}else{
		var iban=$('#iban').val() + $('#ccc').val();
		var formatCta= separaCuenta( $('#ccc').val());
		$('#lbl_valor_IBAN').html("<span>    " + $('#numeroCuenta').val() + "</span>");		
	}
	disabledPagoManual(true);
	$(".ccOficina").show();	
}

function muestraDatosPaneles(){

	if ($.trim($('#numeroCuenta').val()) == ''){
		$('#lbl_sup_valor_IBAN').html("<span> El asegurado no tiene cuenta asignada</span>");		
	}else{
		$('#lbl_sup_valor_IBAN').html("<span>    " + $('#numeroCuenta').val() + "</span>");
		$('#lbl_sup_costeTomador').html("<span>    " + $('#costeTomador').val() + " &euro;</span>");
		$('#lbl_sup_imp_1a_fraccion').html("<span>    " + $('#importe1').val() + " &euro;</span>");
	}
	if ($('#lbl_sup_valor_IBAN2').length > 0) {
		if ($.trim($('#numeroCuenta2').val()) == ''){
			$('#lbl_sup_valor_IBAN2').html("<span> El asegurado no tiene cuenta asignada</span>");		
		} else {
			$('#lbl_sup_valor_IBAN2').html("<span>" + $('#numeroCuenta2').val() + "</span>");
		}
	}
	
	if ($('#destDomicAux').val() == 'A'){
		$('#lbl_sup_valor_destinatarioDomiciliacion').html("<span>    " + "Asegurado" + "</span>");
		$('#lbl_sup_valor_titularCuenta').html("<span>    " + "" + "</span>");
	}else if($('#destDomicAux').val() == 'T'){
		$('#lbl_sup_valor_destinatarioDomiciliacion').html("<span>    " + "Tomador" + "</span>");
		$('#lbl_sup_valor_titularCuenta').html("<span>    " + "" + "</span>");
	}else if($('#destDomicAux').val() == 'O'){
		$('#lbl_sup_valor_destinatarioDomiciliacion').html("<span>    " + "Otros" + "</span>");
		$('#lbl_sup_valor_titularCuenta').html("<span>    " + $('#idValorTitularCuenta').val() + "</span>");
	}else{
		$('#lbl_sup_valor_destinatarioDomiciliacion').html("<span>    " + "" + "</span>");
		$('#lbl_sup_valor_titularCuenta').html("<span>    " + "" + "</span>");
	}
	

	$('#lbl_fec_primer_pago_col').html("<span>    " + $('#fechaPrimerPago').val() + "</span>");
	$('#lbl_fec_segundo_pago_col').html("<span>    " + $('#fechaSegundoPago').val() + "</span>");
	
	var impTotal = $('#importe1').val();
	$('importeTotal').val(impTotal);
	document.getElementById('importeTotal').value = impTotal;
	
	//si la fecha de la linea de contratacion es superior al 1 Marzo de 2019
	if($('#idLineaContratSuperior2019').val() == "true") {		
		habilitarDeshabilitarPagoManual2019();
		habilitarDeshabilitarCargoCuenta2019();
		habilitarDeshabilitarEnvioIban2019();
		
	} else {
		habilitarDeshabilitarPagoManual();
		habilitarDeshabilitarCargoCuenta();
		habilitarDeshabilitarEnvioIban();
	}
}

function asignaIbanAseguradoAPagoManual(){
	if($('#iban').val()=='' && $.trim($('#numeroCuenta').val())!=''){
		$('#iban').val($('#numeroCuenta').val().substring(0,4));
		$('#cuenta1').val($('#numeroCuenta').val().substring(5,9));
		$('#cuenta2').val($('#numeroCuenta').val().substring(10,14));
		$('#cuenta3').val($('#numeroCuenta').val().substring(15,19));
		$('#cuenta4').val($('#numeroCuenta').val().substring(20,24));
		$('#cuenta5').val($('#numeroCuenta').val().substring(25,29));
	}
	if($('#iban2').val()=='' && $.trim($('#numeroCuenta2').val())!=''){
		$('#iban2').val($('#numeroCuenta2').val().substring(0,4));
		$('#cuenta6').val($('#numeroCuenta2').val().substring(5,9));
		$('#cuenta7').val($('#numeroCuenta2').val().substring(10,14));
		$('#cuenta8').val($('#numeroCuenta2').val().substring(15,19));
		$('#cuenta9').val($('#numeroCuenta2').val().substring(20,24));
		$('#cuenta10').val($('#numeroCuenta2').val().substring(25,29));
	}
}

function muestraDomiciliacionAgroseguro() {
	$("#divFormaPagoCliente").css("display",'none');
	disabledPagoManual(true);
}

function muestraDatosPagoManual() {
	$("#divFormaPagoCliente").css("display",'none');
	disabledPagoManual(false);
	$(".ccOficina").hide();
}

function disabledPagoManual(bool){
	$('#bancoDestino').attr('disabled', bool);
	$('#importePago').attr('disabled', bool);
	$('#tx_fechaPago').attr('disabled', bool);
	
	$('#iban').attr('disabled', bool);
	$('#cuenta1').attr('disabled', bool);
	$('#cuenta2').attr('disabled', bool);
	$('#cuenta3').attr('disabled', bool);
	$('#cuenta4').attr('disabled', bool);
	$('#cuenta5').attr('disabled', bool);
	$('#iban2').attr('disabled', bool);
	$('#cuenta6').attr('disabled', bool);
	$('#cuenta7').attr('disabled', bool);
	$('#cuenta8').attr('disabled', bool);
	$('#cuenta9').attr('disabled', bool);
	$('#cuenta10').attr('disabled', bool);	
	$('#btn_fechaPago').attr('disabled', bool);
	$('#lupaBancoDestino').attr('disabled', bool);	
	$('#destinatario').attr('disabled', bool);
	$('#titularCta').attr('disabled', bool);
	
	// Pet. 54046
	// Si se trata de una poliza complementaria, aunque pulsemos pago manual, 
	// los campos de destinatario y titular cuento no deben habilitarse
	if($('#esPolPrincipal').val()!="true"){
		$('#destinatario').attr('disabled', true);
		$('#titularCta').attr('disabled', true);
	}
	
	// Se ha seleccioinado 'Cargo en cuenta', hay que volver a cargar en 'Destinatario domiciliacon' y 'Titular de la cuenta'
	// los datos asignados al asegurado, por si han sido modificados manualmente por el usuario
	if (bool) {
		$('#destinatario').val($('#destDomicAux').val());
		$('#titularCta').val($('#titularDomicAux').val());
	}
}

// Pet. 22208 ** MODIF TAM (10.04.2018) - Resolucion Incidencias *I*
function disabledFormaPagoCliente(bool){
	
	$('#pctprimerpagocliente').attr('disabled', bool || $('#financiarOK').val() != 'true');
	$('#importePrimerPagoCliente').attr('disabled', bool || $('#financiarOK').val() != 'true');
	$('#fecha1').attr('disabled', bool || $('#financiarOK').val() != 'true');
	
	$('#pctsegundopagocliente').attr('disabled', bool || $('#financiarOK').val() != 'true');
	$('#fechasegundopago1').attr('disabled', bool || $('#financiarOK').val() != 'true');
	$('#importeSegundoPagoCliente').attr('disabled', bool || $('#financiarOK').val() != 'true');	
	
	$('#btn_fechaprimerpagomodificado').attr('disabled', bool || $('#financiarOK').val() != 'true');
	$('#btn_fechasegundopagomodificado').attr('disabled', bool || $('#financiarOK').val() != 'true');

}

function aplicarFormaPago(swConfirmacion){
	$('#panelAlertasValidacion').html("");
	$('#panelAlertasValidacion').hide();	
	var frm = document.getElementById('pasarADefinitiva');
	var validaciones = true;
	var varTitularCta = validaTitularCuenta();
	
	/*****/
	if($('#envioIBANAgr').attr("checked")){
		$('#indEnvIbanAux').val('S');
	}else{
		$('#indEnvIbanAux').val('N');
	}
	/****/	
	
	if (frm.pagoManualId.checked == true) {// pago manual
		
		if($('#pasarADefinitiva').valid()) {

			if($('#esPolPrincipal').val() != "true") {				
				var f = true;
			} else {
				var ibanccc = $('#iban').val() + frm.cuenta1.value + frm.cuenta2.value + 
					frm.cuenta3.value + frm.cuenta4.value + frm.cuenta5.value;
				var f = validarIBAN(ibanccc);
			}
			
			var existeBanco = existeBancoDestinoAjax();		
		
			if (f == false || existeBanco == false || varTitularCta==false){
				if (f == false){
					$('#panelAlertasValidacion').html("El Iban tiene un formato incorrecto");
				}
				if (existeBanco == false){
					$('#panelAlertasValidacion').html("El Banco Destino no existe");
				}
				if(varTitularCta==false){
					$('#panelAlertasValidacion').html("El titular de la cuenta no puede estar vac\u00EDo para el destinatario seleccionado.");
				}
				
			 	$('#panelAlertasValidacion').show();
			 	validaciones = false;
			}
	
			if (validaciones == true){
				
					
				if ($('#importe1').val() != $('#importePago').val()){
					jConfirm('El importe introducido no coincide con el importe de la p\u00F3liza \u00BFDesea continuar?', '', function(r) {
					    if (r == true){
					    	uneCuenta();
					    	muestraPopUpConfirmacion();
					    }
					});
				}else{
					uneCuenta();
					muestraPopUpConfirmacion();
				}			
			}
		}
	}else if (frm.datosCuentaId.checked == true){// pago cuenta		
		//compruebo importe entre minimo y maximo del usuario

		var impMin = frm.impMinimoUsuario.value;
		var impMax = frm.impMaximoUsuario.value;	 
		var impTotal = frm.importe1.value;
		var cumple =true;
		var primerPag = $('#pctprimerpagocliente').val();
		var pctPrimPagoCol = frm.pctPrimerPColectivo.value;
		if (parseInt(primerPag) == 100 ||(primerPag == pctPrimPagoCol)){
			cumple = true;
		}else{
			if (impMin != null && impMin !="" && impMin !="undefined" && !isNaN(impMin)){
				if (parseFloat(impTotal) < parseFloat(impMin)){
					cumple = false;
				}
			}
			if (impMax != null && impMax !="" && impMax !="undefined" && !isNaN(impMin)){
				if (parseFloat(impTotal) > parseFloat(impMax)){
					cumple = false;
				}
			}
		}
		if (!cumple){
			$('#panelAlertasValidacion').html("El importe de la p\u00F3liza est\u00E1 fuera del l\u00EDmite establecido");
			$('#panelAlertasValidacion').show();
		}
				
		//rellenamos los datos de pagoCuenta para la validacion
		rellenaDatosPagoCuenta();
		
		
		if(cumple && $('#validacionPagoCuenta').valid()){
			if ($.trim($('#numeroCuenta').val())==""){
				$('#panelAlertasValidacion').html("Debe seleccionar pago manual");
			 	$('#panelAlertasValidacion').show();
			}else if(varTitularCta==false){
				$('#panelAlertasValidacion').html("El titular de la cuenta no puede estar vac\u00EDo para el destinatario seleccionado.");
				$('#panelAlertasValidacion').show();
			}else{			
				if(validaFormaPagoAjax() ){
					muestraPopUpConfirmacion();
				}else{
					$('#panelAlertasValidacion').show();
				}	
			}
		}
	}else if($('#idLineaContratSuperior2019').val() == "true"){
		if(frm.domiciliacionAgroId.checked == true){
			muestraPopUpConfirmacion();
		} else {
			$('#panelAlertasValidacion').html("Debe seleccionar una opci\u00F3n");
		 	$('#panelAlertasValidacion').show();
		}	
	}else{
		$('#panelAlertasValidacion').html("Debe seleccionar una opci\u00F3n");
	 	$('#panelAlertasValidacion').show();
	}
	
	/*****/
	if($('#envioIBANAgr').attr("checked")){
		$('#indEnvIbanAux').val('S');
	}else{
		$('#indEnvIbanAux').val('N');
	}
	/****/	

	

}

function rellenaDatosPagoCuenta(){
	frm = document.getElementById('validacionPagoCuenta');
	frm.pctprimerpagoVal.value = $("#pctprimerpagocliente").val();
	$('#pctsegundopagoVal').val($('#pctsegundopagocliente').val());
	$('#fecha1Val').val($('#fecha1').val());
	$('#fechasegundopago1Val').val($('#fechasegundopago1').val());
	$('#oficinaVal').val($('#oficina').val());
}

function existeBancoDestinoAjax(){	
		var frm = document.getElementById('pasarADefinitiva');
		var bancoDestino = frm.bancoDestino.value;
		var existe;
		$.ajax({
        url:          "pagoPoliza.html",
        data:         "operacion=doValidaBancoDestinoAjax&bancoDestino="+bancoDestino,
        async:        false,
        contentType:  "application/x-www-form-urlencoded",
        dataType:     "text",
        global:       false,
        ifModified:   false,
        processData:  true,
        error: function(objeto, quepaso, otroobj){
        	$.unblockUI();
            alert("Error al solicitar la hora del servidor: " + quepaso);
        },
        success: function(datos){
        	if (datos == 'true'){ //banco correcto
        		existe = true;
					            	
        	}else{ // el banco no existe
        		$.unblockUI();
        		existe = false;
        	}
        },
        type: "GET"
    });
		
		return existe;
}



function uneCuenta(){
	$('#ccc').val($('#cuenta1').val()+$('#cuenta2').val()+$('#cuenta3').val()+$('#cuenta4').val()+$('#cuenta5').val());
	$('#ccc2').val($('#cuenta6').val()+$('#cuenta7').val()+$('#cuenta8').val()+$('#cuenta9').val()+$('#cuenta10').val());
	
}
function separaCuenta(cccBanco){
	var nuevaCuenta = cccBanco.substring(0,4)+' '+cccBanco.substring(4,8)+' '+cccBanco.substring(8,12)+' '+cccBanco.substring(12,16)+' '+cccBanco.substring(16,20);
	return nuevaCuenta;	
}


function ajax_check_Cpl_Sbp(swConfirmacion){
	var frm = document.getElementById('pasarADefinitiva');
	frm.actualizarSbp.value ="";
	var idpoliza = frm.idpoliza.value;
	
	$.ajax({
		url:          "validacionesUtilidades.html",
		data:         "method=do_Ajax_check_Cpl_Sbp&idPoliza="+idpoliza,
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
				$.unblockUI();
				if(confirm('La p\u00F3liza de Sobreprecio asociada a esta p\u00F3liza va a ser actualizada, \u00BFDesea continuar?')){
					frm.actualizarSbp.value ="true";
					continuarPasoDefinitiva(frm, swConfirmacion);
				}
			}else{
				continuarPasoDefinitiva(frm, swConfirmacion);
			}
		},
		type: "GET"
	});
}


function continuarPasoDefinitiva(frm, swConfirmacion){
		// Pet. 22208 ** MODIF TAM (01.03.2018) ** Inicio //		
		document.getElementById('swConfirmacion').value = swConfirmacion;		
		frm.resultadoValidacion.value ="true";
		frm.submit();	
}


function blockUIPasoADefinitiva () {
	$.blockUI.defaults.message = '<h4> Realizando el paso a definitiva.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}

function activaDomiciliacion(check){
	
	if($(check).attr('checked') == false){
		 $(".literalC").hide();
		 $('indEnvIbanAux').val('N');
	}else{
		 $(".literalC").show();
		 $('indEnvIbanAux').val('S');
	}
	if ($('#permiteEnvIban').val()=="true"){
		habilitaTitularDomic($('#destinatario').val());
	}
}

function habilitaTitularDomic(valor){
	if(valor!="O"){
		$('#titularCta').val('');
		$('#titularCta').attr('disabled', true);
	}else{
		// Solo se habilita el titular si esta marcado 'Pago manual' como forma de pago
		if ($('input:radio[name=tipoPago]:checked').val() == 1) {
			$('#titularCta').attr('disabled', false);
		}
	}
}

function validaTitularCuenta(){
	var res=true;
	if($('#isLineaGanado').val()=="true"|| $('#permiteEnvIban').val()=="true"){
		if($('#envioIBANAgr').val()=='S'){
			if($('#destinatario').val()=="O" && ($('#titularCta').val()==null ||$('#titularCta').val()=="")){
				res=false;
			}
		}
	}
	return res;	
}

function volver(){
	var frm = document.getElementById('frmVolver');
	if($('#esPolPrincipal').val()!="true"){
		frm.method.value="doVerImportesCpl";
		frm.action="consultaDetallePoliza.html";
	}
	frm.submit();

}

function verPagos(){
	var frm = document.getElementById('frmCcOficina');
	frm.submit();
}

function formatCerosIzda(valor, ceros, longitud){
	var res=(ceros + valor).slice (-longitud);
	return res; 
}

function validaFormaPagoAjax() {	
	var idpoliza = $('#idpolizaDef').val();	
	var ent= $('#cuenta1').val();
	if (ent == ""){
		ent = ($('#numeroCuenta').val().substring(5,9));
	}
	var valida = false;
	$.ajax({
				url : "pagoPoliza.html",
				data : "operacion=ValidaEntidadPermitidaAjax&idpoliza=" + idpoliza +"&ent="+ent,
				async : false,
				contentType : "application/x-www-form-urlencoded",
				dataType : "json",
				global : true,
				ifModified : false,
				processData : true,
				cache : false,
				error : function(objeto, quepaso, otroobj) {
					alert("Error al guardar los datos: " + quepaso + ": " + otroobj);
				},
				success : function(datos) {
					if (datos.mensaje == "errorTipoPago") {
						$.unblockUI();
						$('#panelAlertasValidacion').html("Debe seleccionar Forma de Pago para pasar a Definitiva");
						valida = false;
					} else if (datos.mensaje == "errorCuenta") {
						$.unblockUI();
						$('#panelAlertasValidacion').html("La E-S Mediadora de la p\u00F3liza no permite el cargo en cuenta a la entidad seleccionada");					
						valida = false;
					} else if (datos.mensaje == "errorManual") {
						$.unblockUI();
						$('#panelAlertasValidacion').html("Falta la informaci\u00F3n de Pago Manual");				
						valida = false;
					} else {
						$('#panelAlertasValidacion').html("");
						valida = true;
					}
				},
				type : "POST"
			});
	return valida;
}

//**** NUEVAS FUNCIONES HABILITAR/DESHABILITAR FORMAS DE PAGO ****/
function habilitarDeshabilitarPagoManual(){
	var perfilUsu =$('#perfilUsu').val();
	var per = perfilUsu.substring(0,1);
	var int = perfilUsu.substring(1,2);
	
	
	if (per == '0' || per == '5' || (per == '1')){
		/* Habilitamos el radio Button, pero deshabilitamos las opciones */
		$('#pagoManualId').attr("disabled", false);
		if($('#pagoManualId').attr("checked")){
			disabledPagoManual(false);	
		}else{
			disabledPagoManual(true);
		}
	/* ESC-7622 ** MODIF DNF (17.12.2019) */	
	/* ANHADO EL PERFIL 2*/		
	}else if (per == '3' || per == '2'){
		
		if(per == '3' && int == 'E'){
			/* Habilitamos el radio Button, pero deshabilitamos las opciones */
			$('#pagoManualId').attr("disabled", false);
			if($('#pagoManualId').attr("checked")){
				disabledPagoManual(false);	
			}else{
				disabledPagoManual(true);
			}
		}
		else{
			/* para perfil 3 Interno habilitamos si la oficina lo permite ... (DNF) Y PARA EL PERFIL 2*/
			if($('#mpPagoM').val() == "true") {
				$(".ccOficina").hide();
				if ($('#mpPagoC').val() == "true"){
					disabledPagoManual(true);
					
				}
			}
			else if($('#mpPagoM').val() == "false") {
				if($('#mpPagoC').val() != "true") {
					$(".ccOficina").hide();
				}
			}
		}			
	}
}

function habilitarDeshabilitarCargoCuenta(){
	var perfilUsu =$('#perfilUsu').val();
	var per = perfilUsu.substring(0,1);
	var int = perfilUsu.substring(1,2);
	
	if (per == '0' || per == '5' || (per == '1' && int == 'I')){
		/* Para estos perfiles Cargo en cuenta siempre habilitado */
		disabledFormaPagoCliente(false);
	/* ESC-7622 ** MODIF DNF (17.12.2019) */	
	/* ANHADO EL PERFIL 2*/		
	}else if ((per =='1' && int == 'E') || (per == '3') || (per == '2')){
		/* para el Perfil 1 Externo hay que validar si la E-S Mediadora lo permite */
		if ($('#mpPagoC').val() == "true"){
			disabledFormaPagoCliente(false);
		}else{
			disabledFormaPagoCliente(true);
			$("#divFormaPagoCliente").css("display",'none');
		}
			
	}
}

function habilitarDeshabilitarEnvioIban(){
	var perfilUsu =$('#perfilUsu').val();
	var per = perfilUsu.substring(0,1);
	var int = perfilUsu.substring(1,2);
	
	if($('#isLineaGanado').val()=="true"|| $('#permiteEnvIban').val()=="true"){
		
		if($('#isPolizaFinanciada').val()=="true"){
			activaDomiciliacion(true);
			$('#envioIBANAgr').attr('checked', true);
			$('#envioIBANAgr').val('S');
			habilitaTitularDomic("A");
		}else{
			
			if (per == '0' || per == '5' || per == '1' ){
				/* Para estos perfiles Cargo en cuenta siempre habilitado */
				activaDomiciliacion(true);
			/* ESC-7622 ** MODIF DNF (17.12.2019) */	
			/* AёADO EL PERFIL 2*/	
			}else if ((per == '3') || (per == '2')){
				
				if($('#caracterEnvioIbanAgroseguro').val() == 'N'){
					activaDomiciliacion(false);
					$('#envioIBANAgr').attr('disabled', true);
					$('#titularCta').attr('disabled', true);
					$('#destinatario').attr('disabled', true);
					
					$('#envioIBANAgr').attr('checked', false);
					$('#envioIBANAgr').val('N');

				}else{
					activaDomiciliacion(true);
					if ($('#caracterEnvioIbanAgroseguro').val() == 'O'){

						$('#envioIBANAgr').attr('checked', true);
						$('#envioIBANAgr').val('S');

						$('#envioIBANAgr').attr('disabled', true);
						$('#titularCta').attr('disabled', true);
						$('#destinatario').attr('disabled', true);

						$('#titularCta').attr('disabled', true);
						$('#destinatario').attr('disabled', true);

						habilitaTitularDomic("A");
					}
				}
			}
		}
	}
}

function habilitarDeshabilitarPagoManual2019(){
	var perfilUsu =$('#perfilUsu').val();
	var per = perfilUsu.substring(0,1);
	var int = perfilUsu.substring(1,2);
	
	if (per == '0' || per == '5' || (per == '1')){
		/* Habilitamos el radio Button, pero deshabilitamos las opciones */
		$('#pagoManualId').attr("disabled", false);
		if($('#pagoManualId').attr("checked")){//si el radiobutton pago manual esta checkeado
			disabledPagoManual(false);
		}else{
			disabledPagoManual(true);
		}
	/* PTC-5729 ** MODIF TAM (03.05.2019) */	
	}else if (per == '3' || per == '2'){
		/* Si se trata del perfil 3 Externo el Pago Manual se permite siempre y en cualquier caso */
		if ( per == 3 && int == 'E'){
			//habilitado
			$('#pagoManualId').attr("disabled", false);
			if($('#pagoManualId').attr("checked")){//si el radiobutton pago manual esta checkeado
				disabledPagoManual(false);
			}else{
				disabledPagoManual(true);
			}
		}else{
		/* Fin PTC-5729 */	
			/* Obligatorio en colectivo */
			if($('#caracterEnvioIbanAgroseguro').val() == 'O'){
				$('#pagoManualId').attr("disabled", true);
				disabledPagoManual(true);
			}else{
				//habilitado si la oficina lo permite
				if($('#mpPagoM').val() == "true") {
					//habilitado
					$('#pagoManualId').attr("disabled", false);
					if($('#pagoManualId').attr("checked")){//si el radiobutton pago manual esta checkeado
						disabledPagoManual(false);
					}else{
						disabledPagoManual(true);
					}
				}else{
					$('#pagoManualId').attr("disabled", true);
					disabledPagoManual(true);
				}
			}
		}
	}
}
function habilitarDeshabilitarCargoCuenta2019(){	
	var perfilUsu =$('#perfilUsu').val();
	var per = perfilUsu.substring(0,1);
	var int = perfilUsu.substring(1,2);
	
	if (per == '0' || per == '5' || (per == '1' && int == 'I')){
		/* Para estos perfiles Cargo en cuenta siempre habilitado */
		disabledFormaPagoCliente(false);
	}else if (per =='1' && int == 'E'){
		/* para el Perfil 1 Externo hay que validar si la E-S Mediadora lo permite */
		if ($('#mpPagoC').val() == "true"){
			disabledFormaPagoCliente(false);
		}else{
			disabledFormaPagoCliente(true);
			$("#divFormaPagoCliente").css("display",'none');
		}	
		/* ESC-7622 ** MODIF DNF (17.12.2019) */	
		/* ANHADO EL PERFIL 2*/	
	}else if (per == '3' || per == '2'){
		//valor del colectivo
		if($('#caracterEnvioIbanAgroseguro').val() == 'O'){
			$("#datosCuentaId").attr("disabled", true); //radiobutton de cargo en cuenta bloqueado	
			disabledFormaPagoCliente(true);
			$("#divFormaPagoCliente").css("display",'none');
		}else{
			if ($('#mpPagoC').val() == "true"){
				//si la E-S Med lo permite
				$("#datosCuentaId").attr("disabled", false); //radiobutton de cargo en cuenta- habilitado	
				disabledFormaPagoCliente(false);
			}else{
				$("#datosCuentaId").attr("disabled", true); //radiobutton de cargo en cuenta bloqueado	
				disabledFormaPagoCliente(true);
				$("#divFormaPagoCliente").css("display",'none');
			}
		}
	}
}
function habilitarDeshabilitarEnvioIban2019(){	
	var perfilUsu =$('#perfilUsu').val();
	var per = perfilUsu.substring(0,1);
	var int = perfilUsu.substring(1,2);
	
	if($('#isLineaGanado').val()=="true"|| $('#permiteEnvIban').val()=="true"){
		
		if($('#isPolizaFinanciada').val()=="true"){
			$('#domiciliacionAgroId').attr("checked", true); //radiobutton de domiciliacion agroseguro marcado
		}else{
			if (per == '0' || per == '5' || (per == '1' &&  int == 'I') ){
				/* Para estos perfiles envio iban siempre habilitado */
				$('#domiciliacionAgroId').attr("disabled", false); //radiobutton de domiciliacion agroseguro habilitado
				if($('#caracterEnvioIbanAgroseguro').val() == 'O'){
					$('#domiciliacionAgroId').attr("checked", true); //radiobutton de domiciliacion agroseguro marcado		
				}
			/* ESC-7622 ** MODIF DNF (17.12.2019) */	
			/* ANHADO EL PERFIL 2*/
			}else if((per == '1' && int == 'E') || (per == '3') || (per == '2')){
				
				/* Obligatorio */
				if($('#caracterEnvioIbanAgroseguro').val() == 'O'){
					$('#domiciliacionAgroId').attr("disabled", false); //radiobutton de domiciliacion agroseguro habilitado
					$('#domiciliacionAgroId').attr("checked", true); //radiobutton de domiciliacion agroseguro marcado
				/* Opcional */	
				}else if($('#caracterEnvioIbanAgroseguro').val() == 'S'){
					//habilitado
					$('#domiciliacionAgroId').attr("disabled", false); //radiobutton de domiciliacion agroseguro
				/* No Domiciliar */	
				}else{//valor N bloqueado
					$('#domiciliacionAgroId').attr("checked", false); //radiobutton de domiciliacion agroseguro marcado
					$('#domiciliacionAgroId').attr("disabled", true); //radiobutton de domiciliacion agroseguro bloqueado
				}
			}
		}
	}	
}

/* PET.70105.FII DNF 23/02/2021 */
function imprimir() {
	var frm = document.getElementById('imprimirPoliza');
	frm.method.value = 'doImprimirPolizas';
	frm.imprimirReducida.value = 'false';
	frm.target = "_blank";
	frm.submit();
}

function imprimirReducida() {
	var frm = document.getElementById('imprimirPoliza');
	frm.method.value = 'doImprimirPolizas';
	frm.imprimirReducida.value = 'true';
	frm.target = "_blank";
	frm.submit();
}

function generarUrlGrabFormaPago(){

	var urlStr = "";
	var frm = document.getElementById('pasarADefinitiva');	
	
	var oficina                     = $('#oficina').val();
	var importe1                    = $('#importe1').val();
	var indEnvIbanAux               = $('#indEnvIbanAux').val();
	var idDestinatarioDomiciliacion = $('#idDestinatarioDomiciliacion').val();
	var idValorTitularCuenta        = $('#idValorTitularCuenta').val();
	var ibanAux                     = $('#ibanAux').val();
	var ibanAux2                    = $('#ibanAux2').val();
	var fecha                       = $('#fecha').val();
	var importePago                 = $('#importePago').val();
	var fecha1                      = $('#fecha1').val();
	var fechasegundopago1           = $('#fechasegundopago1').val();
	var formapago           		= $('#formapago').val();
	var ccc							= $('#ccc').val();
	var ccc2						= $('#ccc2').val();
	var fOK				            = $('#financiarOK').val();
	
	urlStr = "&oficina="+oficina
	+"&importe1="+importe1
	+"&indEnvIbanAux="+indEnvIbanAux
	+"&idDestinatarioDomiciliacion="+idDestinatarioDomiciliacion 
	+"&idValorTitularCuenta="+idValorTitularCuenta 
	+"&ibanAux="+ibanAux
	+"&ibanAux2="+ibanAux2
	+"&fecha="+fecha 
	+"&importePago="+importePago 
	+"&fecha1="+fecha1
	+"&fechasegundopago1="+fechasegundopago1
	+"&formapago="+formapago
	+"&ccc="+ccc
	+"&ccc2="+ccc2
	+"&fOK="+fOK;
	
	if (frm.datosCuentaId.checked == true){
		
		var pctprimerpagocliente      = $('#pctprimerpagocliente').val();
		var fecha1                    = $('#fecha1').val();
		var importePrimerPagoCliente  = $('#importePrimerPagoCliente').val();
		var pctsegundopagocliente     = $('#pctsegundopagocliente').val();
		var fechasegundopago1         = $('#fechasegundopago1').val();
		var importeSegundoPagoCliente = $('#importeSegundoPagoCliente').val();
		
		urlStr = urlStr + "&tipoPago=0&pctprimerpagocliente="+pctprimerpagocliente
			+"&fecha1="+fecha1
			+"&importePrimerPagoCliente="+importePrimerPagoCliente
			+"&pctsegundopagocliente="+pctsegundopagocliente 
			+"&fechasegundopago1="+fechasegundopago1 
			+"&importeSegundoPagoCliente="+importeSegundoPagoCliente;
			
	}else if (frm.pagoManualId.checked == true) {
		
		var banco = $('#bancoDestino').val();
		var importe = $('#importePago').val();
		var fecha = $('#tx_fechaPago').val();
		
		urlStr = urlStr + "&tipoPago=1&bancoDestino="+banco+"&importePago="+importe+"&fechaPago="+fecha;
		
	}else if(frm.domiciliacionAgroId.checked == true){	
		urlStr = urlStr + "&tipoPago=2";
	}
	
	return urlStr;
}

function grabacionProvisional(id){

	var validacionesGrabProv = validacionesGrabPro();
	if(validacionesGrabProv == "true"){
	
		$.blockUI.defaults.message = '<h4> Grabando en Provisional.<BR>Espere un momento, por favor...  <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
		
		var urlStr = generarUrlGrabFormaPago();
		
		$.ajax({
			url : "eleccionFormaPago.html",
			data : "method=grabacionProvisional&idpoliza="+id+"&grProvisional=true"+urlStr,
			async : true,
			contentType : "application/x-www-form-urlencoded",
			dataType : "json",
			cache: false,
			global : false,
			ifModified : false,
			processData : true,
			error : function(objeto, quepaso, otroobj) {
				$.unblockUI();
				alert("Error al pasar la p\u00F3liza a estado provisional: " + quepaso);
			},
			success : function(datos) {
				$.unblockUI();
				$("#panelMensajeValidacion").html(datos.mensaje);
				$("#panelMensajeValidacion").show();
				if(datos.mostrarBtnDef=="SI"){
					$('#btnGrabacionProvisional').hide();
					$('#btnGrabacionDefinitiva').show();
				}
				if(datos.grProvisionalOK=="true"){
					$("#grProvisionalOK").val(datos.grProvisionalOK);
					$("#grProvisionalOK_da").val(datos.grProvisionalOK);
				
					if(datos.periodoFracc!=""){
						$("#periodoFracc").val(datos.periodoFracc);
					}
					grabacionProvisionalOk();
				}
				
				
				if(datos.grabProv == "true"){
					
					$('#btnImprimir').show();
					$('#btnImprimirReducida').show();
					$('#btnSWConfirmacion2021').show();
					$('#btnGrProvisional').hide();
					
					disabledPagoManual(true);
					disabledFormaPagoCliente(true);
					$('#domiciliacionAgroId').attr("disabled", true);
					$('#datosCuentaId').attr("disabled", true);
					$('#pagoManualId').attr("disabled", true);
				}
				
			},
			type : "GET"
		});
	}
}

function swConfirmacionLineaContraSup2021(){
	var frm = document.getElementById('pasarADefinitiva');
	frm.methodPasarADef.value = "doPasarADefPol";
	
	if (frm.pagoManualId.checked == true) {// PAGO MANUAL
					
		if ($('#importe1').val() != $('#importePago').val()){
			jConfirm('El importe introducido no coincide con el importe de la p\u00F3liza \u00BFDesea continuar?', '', function(r) {
			    if (r == true){		    	
			    	uneCuenta();
			    	muestraPopUpConfirmacion();		    	
			    }
			});
		}else{
			uneCuenta();
			muestraPopUpConfirmacion();
		}		
	}else if (frm.datosCuentaId.checked == true || frm.domiciliacionAgroId.checked == true){// PAGO EN CUENTA O DOMICILIACION AGROSEGURO	
		muestraPopUpConfirmacion();					
	}
}

/*PET.70105.FII DNF 04/03/2021 validamos si la linea contratacion es superior a 2021 que tenga asignada cuenta para el cobro de siniestros*/
function validarExisteCuentaAsignadaSiniestros(){
	
	if($('#lineaContrataSup2021').val() == 'true'){
		if ($('#lbl_sup_valor_IBAN2').length > 0) {
			if ($.trim($('#numeroCuenta2').val()) == ''){
				return false;
			} 
		}
	}
	return true;
}	
/*fin PET.70105.FII DNF 04/03/2021*/

//function aplicarFormaPago(swConfirmacion){
function validacionesGrabPro(){
	$('#panelAlertasValidacion').html("");
	$('#panelAlertasValidacion').hide();	
	var frm = document.getElementById('pasarADefinitiva');
	var validaciones = true;
	var varTitularCta = validaTitularCuenta();
	
	/*****/
	if($('#envioIBANAgr').attr("checked")){
		$('#indEnvIbanAux').val('S');
	}else{
		$('#indEnvIbanAux').val('N');
	}
	/****/	
	
	if (frm.pagoManualId.checked == true) {// pago manual
		
		if($('#pasarADefinitiva').valid()) {

			if($('#esPolPrincipal').val() != "true") {				
				var f = true;
			} else {
				var ibanccc = $('#iban').val() + frm.cuenta1.value + frm.cuenta2.value + 
					frm.cuenta3.value + frm.cuenta4.value + frm.cuenta5.value;
				var f = validarIBAN(ibanccc);
			}
			
			var existeBanco = existeBancoDestinoAjax();	
			var valExisCuentAsigSin = validarExisteCuentaAsignadaSiniestros();
		
			if (f == false || existeBanco == false || varTitularCta==false || valExisCuentAsigSin==false){
				if (f == false){
					$('#panelAlertasValidacion').html("El Iban tiene un formato incorrecto");
				}
				if (existeBanco == false){
					$('#panelAlertasValidacion').html("El Banco Destino no existe");
				}
				if(varTitularCta==false){
					$('#panelAlertasValidacion').html("El titular de la cuenta no puede estar vac\u00EDo para el destinatario seleccionado.");
				}
				if(valExisCuentAsigSin==false){
					$('#panelAlertasValidacion').html("El asegurado no tiene cuenta asignada");
				}
				
			 	$('#panelAlertasValidacion').show();
			 	validaciones = false;
			}

			if (validaciones == true){
				
				return "true";

				
			}
		}
	}else if (frm.datosCuentaId.checked == true){// pago cuenta		

		var impMin = frm.impMinimoUsuario.value;
		var impMax = frm.impMaximoUsuario.value;	 
		var impTotal = frm.importe1.value;
		
		var cumple =true;
		var primerPag = $('#pctprimerpagocliente').val();
		var pctPrimPagoCol = frm.pctPrimerPColectivo.value;
		if (parseInt(primerPag) == 100 ||(primerPag == pctPrimPagoCol)){
			cumple = true;
			
		}else{
			
			if (impMin != null && impMin !="" && impMin !="undefined" && !isNaN(impMin)){
				if (parseFloat(impTotal) < parseFloat(impMin)){
					
					cumple = false;
				}
			}
			if (impMax != null && impMax !="" && impMax !="undefined" && !isNaN(impMin)){
				if (parseFloat(impTotal) > parseFloat(impMax)){
					
					cumple = false;
				}
			}
		}
		if (!cumple){
			$('#panelAlertasValidacion').html("El importe de la p\u00F3liza est\u00E1 fuera del l\u00EDmite establecido");
			$('#panelAlertasValidacion').show();
		}
				
		//rellenamos los datos de pagoCuenta para la validacion
		rellenaDatosPagoCuenta();
		
		
		if(cumple && $('#validacionPagoCuenta').valid()){
			
			var valExiCueAsig = validarExisteCuentaAsignadaSiniestros();
			
			if ($.trim($('#numeroCuenta').val())==""){
				$('#panelAlertasValidacion').html("Debe seleccionar pago manual");
			 	$('#panelAlertasValidacion').show();
			}else if(varTitularCta==false){
				$('#panelAlertasValidacion').html("El titular de la cuenta no puede estar vac\u00EDo para el destinatario seleccionado.");
				$('#panelAlertasValidacion').show();
			}else if(valExiCueAsig==false)	{
				$('#panelAlertasValidacion').html("El asegurado no tiene cuenta asignada");
				$('#panelAlertasValidacion').show();
			}else{			
				if(validaFormaPagoAjax() ){
					
					return "true";

				}else{
					$('#panelAlertasValidacion').show();
				}	
			}
		}
	}else if($('#idLineaContratSuperior2019').val() == "true"){
		if(frm.domiciliacionAgroId.checked == true){
			
			var valExiCuenAsigSin = validarExisteCuentaAsignadaSiniestros();
			
			if (valExiCuenAsigSin==false){
				
				$('#panelAlertasValidacion').html("El asegurado no tiene cuenta asignada");
				$('#panelAlertasValidacion').show();
				
			}else{
				return "true";
			}
		}else{
			$('#panelAlertasValidacion').html("Debe seleccionar una opci\u00F3n");
		 	$('#panelAlertasValidacion').show();
		}
	}else{
		$('#panelAlertasValidacion').html("Debe seleccionar una opci\u00F3n");
	 	$('#panelAlertasValidacion').show();
	}
	
	/*****/
	if($('#envioIBANAgr').attr("checked")){
		$('#indEnvIbanAux').val('S');
	}else{
		$('#indEnvIbanAux').val('N');
	}
	/****/	
	
	return "false";

}

/* fin PET.70105.FII DNF 23/02/2021 */