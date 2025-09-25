// 06/02/2015 informesRecibos2015.js

$(document).ready(function(){
	
	controlEntidad();
	controlMediadoras();
	
	//:::::::::::::::::::::::::::::::::: Validacion del formulario ::::::::::::::::::::::::::::::::://
	
	$('#main3').validate({					
		onfocusout: function(element) {
			if ( ($('#method').val() == "doGenerar")  ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		rules: {
			"fase": {invalidChars: true, validaCondicionCompleta:['fase','condiFase'], validaIntervalo:['fase','condiFase'], validaLongitud:['fase','4'], validaDigits: true},
			"refColectivo": {invalidChars: true, validaCondicionCompleta:['refColectivo','condiRefColectivo'], validaIntervalo:['refColectivo','condiRefColectivo'], validaLongitud:['refColectivo','7']},
			"linea": {invalidChars: true, validaCondicionCompleta:['linea','condiLinea'], validaIntervalo:['linea','condiLinea'], validaLongitud:['linea','3'], validaDigits: true},
			"plan": {invalidChars: true, validaCondicionCompleta:['plan','condiPlan'], validaIntervalo:['plan','condiPlan'], validaLongitud:['plan','4'], validaDigits: true},
			"entidad": {invalidChars: true, validaCondicionCompleta:['entidad','condiEntidad'], validaIntervalo:['entidad','condiEntidad'], validaLongitud:['entidad','4'], validaDigits: true, validaEntidad:true},
			"oficina": {invalidChars: true, validaCondicionCompleta:['oficina','condiOficina'], validaIntervalo:['oficina','condiOficina'], validaLongitud:['oficina','4'], validaDigits: true},
			"entidadMed": {invalidChars: true, validaCondicionCompleta:['entidadMed','condiEntidadMed'], validaIntervalo:['entidadMed','condiEntidadMed'], validaLongitud:['entidadMed','4'], validaDigits: true},
			"subent": {invalidChars: true, validaCondicionCompleta:['subent','condiSubent'], validaIntervalo:['subent','condiSubent'], validaLongitud:['subent','4'], validaDigits: true},
			"fechaEmision": {invalidChars: true, validaCondicionCompleta:['fechaEmision','condiFechaEmision'], validaIntervaloFechas:['fechaEmision','condiFechaEmision'], validaFecha: true},
			"recibo":{invalidChars: true, validaCondicionCompleta:['recibo','condiRecibo'], validaIntervalo:['recibo','condiRecibo'], validaLongitud:['recibo','7'], validaDigits: true},
			"refPoliza": {invalidChars: true, validaCondicionCompleta:['refPoliza','condiRefPoliza'], validaIntervalo:['refPoliza','condiRefPoliza'], validaLongitud:['refPoliza','7'], validaCodRef: true},
			"nifAseg": {invalidChars: true, validaCondicionCompleta:['nifAseg','condiNifAseg'], validaIntervalo:['nifAseg','condiNifAseg'], validaLongitud:['nifAseg','9'], comprobarNif: true},
			"idInforme": {validaChecks: true}
		},
		messages: {
			"fase": {invalidChars: "El campo Número fase contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para el Número fase", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Número fase no puede contener intervalos para la condición elegida";else return "El campo Número fase contiene un intervalo incorrecto"}, validaLongitud: "Cada número de fase debe contener 4 dígitos como máximo", validaDigits: "El campo Número fase debe ser numérico" },
			"refColectivo": {invalidChars: "El campo Colectivo contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para el Colectivo", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Colectivo no puede contener intervalos para la condición elegida";else return "El campo Colectivo contiene un intervalo incorrecto"}, validaLongitud: "Cada colectivo debe contener 7 dígitos como máximo"},
			"linea": {invalidChars: "El campo Línea contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para la Línea", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Línea no puede contener intervalos para la condición elegida";else return "El campo Línea contiene un intervalo incorrecto"}, validaLongitud: "Cada línea debe contener 3 dígitos como máximo", validaDigits: "El campo Linea debe ser numérico" },
			"plan": {invalidChars: "El campo Plan contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para el Plan", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Plan no puede contener intervalos para la condición elegida";else return "El campo Plan contiene un intervalo incorrecto"}, validaLongitud: "Cada plan debe contener 4 dígitos como máximo", validaDigits: "El campo Plan debe ser numérico" },
			"entidad": {invalidChars: "El campo Entidad contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para la Entidad", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Entidad no puede contener intervalos para la condición elegida";else return "El campo Entidad contiene un intervalo incorrecto"}, validaLongitud: "Cada entidad debe contener 4 dígitos como máximo", validaDigits: "El campo Entidad debe ser numérico", validaEntidad: "La Entidad no pertenece al grupo de entidades del usuario" },
			"oficina": {invalidChars: "El campo Oficina contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para la Oficina", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Oficina no puede contener intervalos para la condición elegida";else return "El campo Oficina contiene un intervalo incorrecto"}, validaLongitud: "Cada oficina debe contener 4 dígitos como máximo", validaDigits: "El campo Oficina debe ser numérico" },
			"entidadMed": {invalidChars: "El campo Entidad med. contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para la Entidad med.", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Entidad med. no puede contener intervalos para la condición elegida";else return "El campo Entidad med. contiene un intervalo incorrecto"}, validaLongitud: "Cada entidad med. debe contener 4 dígitos como máximo", validaDigits: "El campo Entidad med. debe ser numérico"},
			"subent": {invalidChars: "El campo Subentidad med. contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para la Subentidad med.", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Subentidad med. no puede contener intervalos para la condición elegida";else return "El campo Subentidad med. contiene un intervalo incorrecto"}, validaLongitud: "Cada subentidad med. debe contener 4 dígitos como máximo", validaDigits: "El campo Subentidad med. debe ser numérico"},
			"fechaEmision": {invalidChars: "El campo Fecha emisión contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para Fecha de emisión", validaIntervaloFechas: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Fecha emisión no puede contener intervalos para la condición elegida";else return "El campo Fecha emisión contiene un intervalo incorrecto"}, validaFecha: "La fecha introducida no es correcta"},
			"recibo": {invalidChars: "El campo Número recibo contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para el Número recibo", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Número recibo no puede contener intervalos para la condición elegida";else return "El campo Número recibo contiene un intervalo incorrecto"}, validaLongitud: "Cada número de recibo debe contener 7 dígitos como máximo", validaDigits: "El campo Número recibo debe ser numérico"},
			"refPoliza": {invalidChars: "El campo Ref. Póliza contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para la Ref. Póliza", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo Ref. Póliza no puede contener intervalos para la condición elegida";else return "El campo Ref. Póliza contiene un intervalo incorrecto"}, validaLongitud: "Cada Ref. Póliza debe contener 7 caracteres", validaCodRef: "El formato de la Ref. Póliza no es correcto"},
			"nifAseg": {invalidChars: "El campo NIF contiene caracteres no válidos, los valores deben ir separados por ','", validaCondicionCompleta: "Debe completar la condición para el Nif", validaIntervalo: function (value){if(document.getElementById(value[1]).value =="5")return "El campo NIF no puede contener intervalos para la condición elegida";else return "El campo NIF fase contiene un intervalo incorrecto"}, validaLongitud: "Cada NIF debe contener 9 dígitos como máximo", comprobarNif: "El NIF introducido no es válido"},
			"idInforme": {validaChecks: "Debe seleccionar al menos un campo"}
	 	}
	});
	
	// comprueba los caracteres introducidos
	jQuery.validator.addMethod("invalidChars", function(value, element, params) { 
		var validado = true;
		var invalidChars = ".:;<>-[]%!?()+*_=&";
		for(var i = 0; i< value.length;i++){
			if(invalidChars.indexOf(value.charAt(i))!= -1){							
				validado = false;
				$('#method').val('');
				break;
			}
		}
		if(validado == false){
			$('#method').val('');
		}
		return validado;	
	});
	
	// comprueba que esten rellenos tanto el combo de la condicion como el input 
	jQuery.validator.addMethod("validaCondicionCompleta", function(value, element, params) {
		return  validaCondicionCompleta(document.getElementById(params[0]), document.getElementById(params[1]));
	});
	
	// comprueba que el numero de valores introducidos sean acordes con la condicion elegida
	jQuery.validator.addMethod("validaIntervalo", function(value, element, params) {
		return validaIntervalo(document.getElementById(params[0]), document.getElementById(params[1]));
	});
	
	// comprueba que el numero de valores introducidos sean acordes con la condicion elegida (versión para fechas)
	jQuery.validator.addMethod("validaIntervaloFechas", function(value, element, params) {
		return validaIntervaloFechas(document.getElementById(params[0]), document.getElementById(params[1]));
	});
	
	// comprueba el tamaño de los valores introducidos
	jQuery.validator.addMethod("validaLongitud", function(value, element, params) {
		return validaLongitud(document.getElementById(params[0]), params[1], params[0]);
	});
	
	// comprueba que sean numericos los valores introducidos
	jQuery.validator.addMethod("validaDigits", function(value, element) {
		return (this.optional(element) || validaDigits(value));
	});
	
	// comprueba el formato de la referencia de la poliza
	jQuery.validator.addMethod("validaCodRef", function(value, element) {
		return (this.optional(element) || validaCodRef(value));
	});
	
	// comprueba el valor de la fecha introducida
	jQuery.validator.addMethod("validaFecha", function(value, element) {
		return (this.optional(element) || validaFecha(value));
	});
	
	// comprueba para el intervalo de las fechas que la primera no sea mayor que la segunda
	jQuery.validator.addMethod("validaEntreFechas", function(value, element, params) {
		return validaEntreFechas(document.getElementById(params[0]), document.getElementById(params[1]));
	});
	
	// comprueba que la entidad pertenezca al grupo de entidades en el caso del perfil 5 
	jQuery.validator.addMethod("validaEntidad", function(value, element) {
		return (this.optional(element) || validaEntidad(value));
	});
	
	// comprueba que al menos un check este marcado
	jQuery.validator.addMethod("validaChecks", function(value, element) {
		var validado = false;
		$("input[type=checkbox]").each(function(){ 				 					        
	     	if($(this).attr('checked')== true){
	     		validado = true;
	     		return false;
	     	} 	
		});
		if(validado == false){
			$('#method').val('');
		}
		return validado;
	});
	
	
	// comprueba que el NIF introducido sea valido
	jQuery.validator.addMethod("comprobarNif", function(value, element) {
		return (this.optional(element) || validaNif(value));
	});
	
	//************************************************************************************//
	
	function validaCondicionCompleta(input, select){
		if(input.value != "" && select.value == ""){
			$('#method').val('');
			return false;
		}else{
			if(input.value == "" && select.value != ""){
				$('#method').val('');
				return false;
			}else{
				return true;
			}
		}
	}
	
	function validaIntervalo(input, select){
		var cadena = (input.value).split(',');
		var validado = true;
		//Validacion de condicion Igual
		if(select.value == "5"){
			if(cadena.length > 1){
				$('#method').val('');
				validado = false;
			}
		}
		//Validacion de condicion Entre, ademas de validar que el primer valor sea menor que el segundo
		if(select.value == "4"){
			if(cadena.length == 1 || cadena.length > 2){
				$('#method').val('');
				validado = false;
			}else{
				if(primerValorMayor($.trim(cadena[0]),$.trim(cadena[1])) == true){
					$('#method').val('');
					validado = false;
				}
			}
		}
		if(validado == false){
			$('#method').val('');
		}
		return validado;
	}
	
	function validaIntervaloFechas(input, select){
		var cadena = (input.value).split(',');
		var validado = true;
		//Validacion de condicion Igual
		if(select.value == "5"){
			if(cadena.length > 1){
				$('#method').val('');
				validado = false;
			}
		}
		//Validacion de condicion Entre, ademas de validar que el primer valor sea menor que el segundo
		if(select.value == "4"){
			if(cadena.length == 1 || cadena.length > 2){
				validado = false;
			
			}else{
				var fechaIni = $.trim(cadena[0]);
				var fechaFin = $.trim(cadena[1]);
				
				if(esPrimeraFechaMayorOIgualQue(fechaIni, fechaFin)){
					validado = false;
				}else{
					validado = true;
				}
			}
		}
		if(validado == false){
			$('#method').val('');
		}
		return validado;
	}
	
	function esPrimeraFechaMayorOIgualQue(fec0, fec1){ 

	    var bRes = false;
	    var sDia0 = fec0.substr(0, 2); 
	    var sMes0 = fec0.substr(3, 2); 
	    var sAno0 = fec0.substr(6, 4); 
	    var sDia1 = fec1.substr(0, 2); 
	    var sMes1 = fec1.substr(3, 2); 
	    var sAno1 = fec1.substr(6, 4);
	    
	    //si el año de la primera fecha es mayor que el de la segunda
	    if (sAno0 > sAno1){
	    	 bRes = true;
	    }	  
	    else {
		   if (sAno0 == sAno1){
              //si son del mismo año y el mes de la primera es mayor que el de la segunda
              if (sMes0 > sMes1){
              	 bRes = true;
              }
		      else { 
		   	   if (sMes0 == sMes1){
		          //si el mes es el mismo y el dia de la primera es mayor que el de la segunda
		          if (sDia0 >= sDia1){
		        	bRes = true;
		          }
		   	   } 	 
		     } 
		   } 
	    } 
	    return bRes; 
	}
	
	function primerValorMayor(primero, segundo){
		var mayor = false;
		if(parseInt(primero) > parseInt(segundo)){
			mayor = true ;
		}
		return mayor;
	}
	
	function validaLongitud(input, maxlength, campo){
	
		var cadena = (input.value).split(',');
		var validado = true;
		if(input.value != ""){
			for(var i=0; i<cadena.length; i++){
				//caso especial para la ref poliza.
				if(campo == "refPoliza"){
					if($.trim(cadena[i]).length != maxlength){
						validado = false;
						break;
					}
				}else{
					if($.trim(cadena[i]).length > maxlength){
						validado = false;
						break;
					}
				}
			}	
		}	
		if(validado == false){
			$('#method').val('');
		}
		return validado;
	}
	
	function validaDigits(input){
		var validado = true;
		var cadena = input.split(',');
		var validDigits = "1234567890"
		
		for(var i=0; i<cadena.length; i++){
			var digito = ($.trim(cadena[i])).split('');
			for(var j=0; j<digito.length; j++){
				if(validDigits.indexOf(digito[j])==-1){
					validado = false
					break;
				}
			}
			if(validado == false){
				break;
			}
		}
		if(validado == false){
			$('#method').val('');
		}
		return validado;
	}
	
	function validaFecha(input){
		var validado = false;
		var re = /^\d{1,2}\/\d{1,2}\/\d{4}$/;
		var cadena = input.split(',');
		for(var i=0; i<cadena.length; i++){
			var value = $.trim(cadena[i]);
			if( re.test(value)){
				var adata = value.split('/');
				var gg = parseInt(adata[0],10);
				var mm = parseInt(adata[1],10);
				var aaaa = parseInt(adata[2],10);
				var xdata = new Date(aaaa,mm-1,gg);
				if ( ( xdata.getFullYear() == aaaa ) && ( xdata.getMonth () == mm - 1 ) && ( xdata.getDate() == gg ) ){
					validado = true;
				}else{
					validado = false;
					break;
				}	
			} else{
				validado = false;
				break;
			}	
		}
		if(validado == false){
			$('#method').val('');
		}
		return validado;
	}
	
	function validaCodRef(input){
		var validado = true;
		$('#refPoliza').val(input.toUpperCase());
		var cadena = (input.toUpperCase()).split(',');
		for(var i=0; i<cadena.length; i++){
			// Comprueba que el primer caracter es una letra en mayusculas
			if (($.trim(cadena[i])).charAt(0).match (/[A-Z]/g) == null){
				validado = false;
				break;
			}
			// Comprueba que los siguientes caracteres son numeros
			var digitos = ($.trim(cadena[i])).substr(1).split('');
			for(var j=0; j<digitos.length; j++){
				if (digitos[j].match (/\d/g) == null){
					validado = false;
					break;
				}
			}
			if(validado == false){
				break;
			}
		}
		if(validado == false){
			$('#method').val('');
		}
		return validado;
	}
	
	function validaNif(input){
		var validado    = true;
		var cadena = input.split(',');
		for(var i=0; i<cadena.length; i++){
			var validadoNIF = true;
			var validadoCIF = true;
			var validadoNIE = true;
			validadoNIF = generales.validaCifNif("NIF", $.trim(cadena[i]));
			if (validadoNIF == false)
				validadoCIF = generales.validaCifNif("CIF", $.trim(cadena[i]));
			if (validadoCIF == false)
				validadoNIE = generales.validaCifNif("NIE", $.trim(cadena[i]));			
			if (validadoNIF == false && validadoCIF == false && validadoNIE == false){
				validado = false;
				break;
			}
		}
		if(validado == false){
			$('#method').val('');
		}
		return validado;
	}
	
	function validaEntidad(entidad){
		var encontrado = true;
		var codEnt = entidad.split(',');
		var codEntValid = ($('#lstCodEntidades').val()).split(',');
		if($('#perfil').val() == '5'){
			
			for(var i=0; i<codEnt.length; i++){				
				for (var j=0; j<codEntValid.length; j++){
					if($.trim(codEnt[i]) == $.trim(codEntValid[j])){
						encontrado = true;
						break;
					}else{
						encontrado = false;
					}
				}
				if(encontrado == false){
					break;
				}
			}
			if(encontrado == false){
				$('#method').val('');
			}
		}
		return encontrado;
	}
	
	//:::::::::::::::::::::::::::::::::: Fin Validacion del formulario ::::::::::::::::::::::::::::::::://
});

function generar(){
	$("#datosDe").val($('input:radio[name=mostrarDatos]:checked').val());
   	$("#method").val('doGenerar');
	$("#main3").attr('target', '_blank');
	$("#main3").submit();
}

function limpiar(){
	$('#panelAlertasValidacion').html('');
	$('#panelAlertasValidacion').hide();
	$("#formatoPDF").attr('checked',true);
	$("input[type=text]").each(function(){ 				 					        
     	$(this).val('');
	});
	$("select").each(function(){ 				 					        
     	$(this).val('');
	});
	$('#checkTodo').attr('checked',false);
	desmarcar_todos();
	controlEntidad();
	controlMediadoras();
}

function marcar_todos(){
	$("input[type=checkbox]").each(function(){ 				 					        
	     	$(this).attr('checked',true);
	}); 
}

function desmarcar_todos(){
	$("input[type=checkbox]").each(function(){ 				 					        
     	$(this).attr('checked',false);
    }); 
}

function desmarcaCheckTodo(check){
	if($(check).attr('checked') == false){
		$('#checkTodo').attr('checked',false);
	}		
}

function controlEntidad(){
	var lstCodEntidades = $('#lstCodEntidades').val();
	$('#entidad').val(lstCodEntidades);
	
	if($('#perfil').val() == '1'){
		$('#entidad').attr('readonly',true);
	}else{
		if($('#perfil').val() == '5'){
			$('#condiEntidad').val('3');
		}
		
	}
	
}

function controlMediadoras(){
	if(($('#perfil').val() == '1') && ($('#esExterno').val() == '1')){
		$('#entidadMed').val($('#entMed').val());
		$('#subent').val($('#subEntMed').val());
		
		$('#entidad').attr('readonly',true);
		$('#entidadMed').attr('readonly',true);
		$('#subent').attr('readonly',true);
	}	
}