  // Para evitar el cacheo de peticiones al servidor
$(document).ready(function(){
    var fmain = document.getElementById("main");
    var faseguradoForm = document.getElementById("aseguradoForm");

    if(fmain != null){
        var URL = UTIL.antiCacheRand(document.getElementById("main").action);
         document.getElementById("main").action = URL;
     } 
    
     if(faseguradoForm != null){
         var URL = UTIL.antiCacheRand(document.getElementById("aseguradoForm").action);
         document.getElementById("aseguradoForm").action = URL;
     } 
     if ($('#showPopupPolAsegurados').val() == "true") {
    	  $('#overlay').show();
    	  $('#divAseguradoPol').show();
     }
	  
    if ($('#ccc').val() != ""){
		 	$('#cuenta1').val($('#ccc').val().substring(0,4));
			$('#cuenta2').val($('#ccc').val().substring(4,8));
			$('#cuenta3').val($('#ccc').val().substring(8,12));
			$('#cuenta4').val($('#ccc').val().substring(12,16));
			$('#cuenta5').val($('#ccc').val().substring(16,20));
	 }
    
    if ($('#ccc2').val() != ""){
	 	$('#cuenta6').val($('#ccc2').val().substring(0,4));
		$('#cuenta7').val($('#ccc2').val().substring(4,8));
		$('#cuenta8').val($('#ccc2').val().substring(8,12));
		$('#cuenta9').val($('#ccc2').val().substring(12,16));
		$('#cuenta10').val($('#ccc2').val().substring(16,20));
    }
    
    if ($('#idDatoAsegurado').val() != ""){
    	generales.botonesModificacion();
    }
	
    
    if ($('#titCuenta').val() != ""){
    	$('#titularCuenta').val($('#titCuenta').val());
    }	
    
    if ($('#destDomic').val() != ""){
    	$('#destinatarioDomiciliacion').val($('#destDomic').val());
    }	
    
	$('#main').validate({
		 errorLabelContainer: "#panelAlertasValidacion",
		 wrapper: "div",
		 
		 onfocusout: function(element) {
		 },
		 highlight: function(element, errorClass) {
		 	$("#campoObligatorio_"+element.id).show();
		 },
		 unhighlight: function(element, errorClass) {
		 	$("#campoObligatorio_"+element.id).hide();
		 },
	 	rules: {	
	 		"lineaCondicionado.codlinea": {required:true, digits: true},
	 		"iban": {required:true,validaIban:true,validaIbanFormato:true},
	 		"iban2": {required:true,validaIban2:true,validaIbanFormato2:true},
	 		"destinatarioDomiciliacion": {required:true},
	 		"titularCuenta": {validaTitularCuenta:true}
	 		
	 	},
	 	messages: {
	 		"destinatarioDomiciliacion": {required:"El campo 'Destinatario de la domiciliaci\u00F3n' es obligatorio"},
	 		"lineaCondicionado.codlinea": {required:"El campo L\u00EDnea es obligatorio", digits: "El L\u00EDnea solo puede contener d\u00EDgitos"},
	 		"iban": {required:"El campo Iban pago prima es obligatorio", validaIban:"El n\u00FAmero de cuenta es obligatorio", validaIbanFormato:"El IBAN para el pago de la prima es incorrecto"},
	 		"iban2": {required:"El campo Iban cobro siniestros es obligatorio", validaIban2:"El n\u00FAmero de cuenta es obligatorio", validaIbanFormato2:"El IBAN para el cobro de siniestros es incorrecto"},
	 		"titularCuenta": {validaTitularCuenta:"El campo 'Titular de la cuenta' es obligatorio si se elige 'Otros' como destinatario de la domiciliaci\u00F3n"}
	 	}
});
	jQuery.validator.addMethod("validaIban", function(value,
			element, params) {

		if ($('#iban').val() == "" || $('#cuenta1').val() == ""
				|| $('#cuenta2').val() == ""
				|| $('#cuenta3').val() == ""
				|| $('#cuenta4').val() == ""
				|| $('#cuenta5').val() == "") {

			return false;
		} else {
			return true;
		}
	});	
	jQuery.validator.addMethod("validaIban2", function(value,
			element, params) {

		if ($('#iban2').val() == "" || $('#cuenta6').val() == ""
				|| $('#cuenta7').val() == ""
				|| $('#cuenta8').val() == ""
				|| $('#cuenta9').val() == ""
				|| $('#cuenta10').val() == "") {

			return false;
		} else {
			return true;
		}
	});	
	


	jQuery.validator.addMethod("validaIbanFormato", function(
		value, element, params) {
		var ibanccc = document.getElementById('iban').value
				+ document.getElementById('cuenta1').value
				+ document.getElementById('cuenta2').value
				+ document.getElementById('cuenta3').value
				+ document.getElementById('cuenta4').value
				+ document.getElementById('cuenta5').value
		// La funcion validar esta en el iban.js
		var f = validarIBAN(ibanccc);
		if (f == false) {
			return false;
		}
		return true;

	});
	

	jQuery.validator.addMethod("validaIbanFormato2", function(
		value, element, params) {
		var ibanccc = document.getElementById('iban2').value
				+ document.getElementById('cuenta6').value
				+ document.getElementById('cuenta7').value
				+ document.getElementById('cuenta8').value
				+ document.getElementById('cuenta9').value
				+ document.getElementById('cuenta10').value
		// La funcion validar esta en el iban.js
		var f = validarIBAN(ibanccc);
		if (f == false) {
			return false;
		}
		return true;
	
	});
	
	jQuery.validator.addMethod("validaTitularCuenta", function(value, element, params) {
		//alert("valido titualarcuenta titularCuenta: "+$('#titularCuenta').val());
		var frm    = document.getElementById("main");
		if(frm.destinatarioDomiciliacion.value=='O' && $('#titularCuenta').val() == ''){		
				return false;
		}else{
			return true;
		}
	});
	
	comprobarDestinatario();
});

function modificar(id, idAsegurado, linea, cuenta, iban, destinatario, titular, cuenta2, iban2)
{
	$('#panelAlertasValidacion').html("");
	$('#panelAlertasValidacion').hide();
	$('#idDatoAsegurado').val(id);
	$('#idAsegurado').val(idAsegurado);
	if (linea == '999'){
		$('#lineaGen').val(linea);
	}else{
		$('#lineaGen').val("");
	}
	$('#lineaCondicionado').val(linea);
	$('#iban').val(iban);
	$('#cuenta1').val(cuenta.substring(0,4));
	$('#cuenta2').val(cuenta.substring(4,8));
	$('#cuenta3').val(cuenta.substring(8,12));
	$('#cuenta4').val(cuenta.substring(12,16));
	$('#cuenta5').val(cuenta.substring(16,20));
	$('#cuentaEditada').val(iban+cuenta);
	$('#destinatarioDomiciliacion').val(destinatario);
	$('#titularCuenta').val(titular);
	$('#iban2').val(iban2);
	$('#cuenta6').val(cuenta2.substring(0,4));
	$('#cuenta7').val(cuenta2.substring(4,8));
	$('#cuenta8').val(cuenta2.substring(8,12));
	$('#cuenta9').val(cuenta2.substring(12,16));
	$('#cuenta10').val(cuenta2.substring(16,20));
	$('#cuentaEditada2').val(iban2+cuenta2);
	generales.botonesModificacion();
	comprobarDestinatario();	
}

function baja (id, codlinea){
	jConfirm('\u00BFEst\u00E1 seguro de que desea eliminar el registro seleccionado?', 'Di\u00E1logo de Confirmaci\u00F3n', function(r) {
		if (r){
			$('#idDatoAsegurado').val(id);
			$('#lineaCondicionadoBaja').val(codlinea);
			$('#method').val("doBaja");
			$("#main").validate().cancelSubmit = true;
			$('#main').submit();
		}
	});
}

function enviar(method){
	var enviarForm=false;
    var frm    = document.getElementById("main");
	frm.method.value = method;
	if (method == "doAlta" || method == "doModificar"){
    	if ($("#main").valid()){
    		if ($('#lineaGen').val() == "999"){
    			if ($('#lineaGen').val() == $('#lineaCondicionado').val()){
    			enviarForm=true;
	    		}else{
	    			$('#panelAlertasValidacion').html("La l\u00EDnea gen\u00E9rica no puede modificarse");
	    			$('#panelAlertasValidacion').show();
	    			document.getElementById('lineaCondicionado').focus();
	    		}
    		}else{
    			enviarForm=true;
    		}

    	}
	}else{ // doConsultar o doLimpiar
		if (method == "doLimpiar"){
			$('#iban').val('');
			$('#cuenta1').val('');
			$('#cuenta2').val('');
			$('#cuenta3').val('');
			$('#cuenta4').val('');
			$('#cuenta5').val('');
			$('#iban2').val('');
			$('#cuenta6').val('');
			$('#cuenta7').val('');
			$('#cuenta8').val('');
			$('#cuenta9').val('');
			$('#cuenta10').val('');
		}
		$('#lineaGen').val("");
		enviarForm=true;
	}
	if (enviarForm){
		$.blockUI.defaults.message = '<h4> Procesando petici\u00F3n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		frm.submit();
	}
	
}

function comprobarDestinatario(){
	var frm = document.getElementById("main");
	//alert(frm.destinatarioDomiciliacion.value);
	
	if (frm.destinatarioDomiciliacion.value=='O'){
		
		$('#titularCuenta').attr("disabled", false);
	}else{
		$('#titularCuenta').val('');
		$('#titularCuenta').attr("disabled", true);
	}
}
