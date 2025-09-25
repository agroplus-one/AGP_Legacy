$(document).ready(function() {	
	
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
        inputField        : "tx_fechaocurrencia",
        button            : "btn_fecha_ocurrencia",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});		      	
  	
  	$('#main').validate({
  		 errorLabelContainer: "#panelAlertasValidacion",
		 wrapper: "li",
		    					 					 
		  onfocusout: function(element) {
			if ( ($('#method').val() == "doGuarda") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
				this.element(element);
			}
		 },
		 highlight: function(element, errorClass) {
		 	$("#campoObligatorio_"+element.id).show();
		 },
		 unhighlight: function(element, errorClass) {
		 	$("#campoObligatorio_"+element.id).hide();
		 },
		 rules: {				 	
	 		
	 		"codriesgo" : {required: true},
	 		"fechaocurrencia" : {required: function(element){return  $('#sl_riesgo_siniestros').val() != '04';}, dateITA: true,validaFechaOcurrencia:['fechaocurrencia'],validaFechaOcurrencia6meses:['fechaocurrencia']},
	 		"observaciones" :{required: function(element){return  $('#sl_riesgo_siniestros').val() == 0;}, maxlength: 120},
	 		
	 		"clavevia": {required: true, lettersonly: true, minlength: 2},
	 		"direccion": {required: true, letterswithbasicpunc: true, invalidChars: true},
	 		"numvia": {required: true},
	 		"piso" : {invalidChars: true},
	 		"bloque" : {invalidChars: true},
	 		"escalera" : {invalidChars: true},
	 						 		
	 		"nombre": {requiredDatos: true, letterswithwhitespace: true, digits: false, number: false, maxlength: 20},
	 		"apellido1": {requiredDatos: true, letterswithwhitespace: true, digits: false, number: false},
	 		"apellido2": {letterswithwhitespace: true, digits: false, number: false},
	 		"razonsocial": {requiredDatos: true, lettersnumberswhitespacecolonpoint: true},
	 							 	
		 	"codprovincia": {required: true, digits: true, number: false},
		 	"codlocalidad": {required: true, digits: true, number: false},
		 	"sublocalidad": {required: true, digits: true, number: false},					 	
		 	"codpostalstr": {required: true, minlength: 5, digits: true, notregex: "000$",cp:true},
		 	
		 	"telefono1": {required: false, digits: true, minlength: 9, maxlength: 9},
		 	"telefono2": {required: false, digits: true, minlength: 9, maxlength: 9},
		 	"telefono3": {required: false, digits: true, minlength: 9, maxlength: 9},
		 	
		 	"telefonoFijoAsegurado": {required: false, digits: true, minlength: 9, maxlength: 9},
		 	"telefonoMovilAsegurado": {required: false, digits: true, minlength: 9, maxlength: 9},
		 	"emailAsegurado": {email: true}
	 	},
	 	messages: {
	 		
	 	 	"codriesgo" : {required: "El campo Riesgo Siniestro es obligatorio"},
	 	 	"fechaocurrencia" : {required:"El campo Fecha Ocurrencia es obligatorio", dateITA: "El formato del campo Fecha Ocurrencia pago es dd/mm/YYYY",validaFechaOcurrencia:"La Fecha Ocurrencia no puede ser posterior a la fecha actual",validaFechaOcurrencia6meses:"La Fecha Ocurrencia no puede ser anterior a seis meses"},
	 	 	"observaciones" : {required:"El campo Observaciones es obligatorio", maxlength: "El campo Observaciones no puede tener m�s de 120 caracteres"},
	 	 	
	 	 	"clavevia": {required: "El campo V�a es obligatorio", lettersonly: "El campo V�a no puede contener d�gitos", minlength: "El campo V�a debe contener 2 letras"},
		 	"direccion": {required: "El campo Domicilio es obligatorio", letterswithbasicpunc: "El campo Domicilio no puede contener d�gitos", invalidChars: "El campo Domicilio no puede contener ciertos caracteres"},
		 	"numvia": {required: "El campo N�mero es obligatorio"},
		 	"piso" : {invalidChars: "El campo Piso contiene caracteres no v�lidos .,:;<>[]%!?()+*_="},
		 	"bloque" : {invalidChars: "El campo Bloque contiene caracteres no v�lidos .,:;<>[]%!?()+*_="},
		 	"escalera" : {invalidChars: "El campo Escalera contiene caracteres no v�lidos .,:;<>[]%!?()+*_="},
		 	
			"nombre": {requiredDatos: "Nombre: Son obligatorios los campos Nombre y Primer Apellido o la Raz�n Social", letterswithwhitespace: "El campo Nombre no puede contener d�gitos",number: "El campo Nombre no puede contener d�gitos", digits: "El campo Nombre no puede contener d�gitos", maxlength: "El campo Nombre no puede tener m�s de 20 caracteres."},
	 	 	"apellido1": {requiredDatos: "Apellido1: Son obligatorios los campos Nombre y Primer Apellido o la Raz�n Social", letterswithwhitespace: "El campo Primer Apellido no puede contener d�gitos ni caracteres no v�lidos", number: "El campo Primer Apellido no puede contener d�gitos", digits: "El campo Primer Apellido no puede contener d�gitos"},
	 	 	"apellido2": {letterswithwhitespace: "El campo Segundo Apellido no puede contener d�gitos",number: "El campo Segundo Apellido no puede contener d�gitos", digits: "El campo Segundo Apellido no puede contener d�gitos"},
	 	 	"razonsocial": {requiredDatos: "Raz�n Social: Son obligatorios los campos Nombre y Primer Apellido o la Raz�n Social", lettersnumberswhitespacecolonpoint: "El campo Raz�n Social no puede contener caracteres especiales"},
		
		 	"codprovincia": {required: "El campo Provincia es obligatorio", digits: "El campo Provincia s�lo puede contener d�gitos"},
		 	"codlocalidad": {required: "El campo Localidad es obligatorio", digits: "El campo Localidad s�lo puede contener d�gitos"},
		 	"sublocalidad": {required: "El campo Sublocalidad es obligatorio", digits: "El campo Sublocalidad s�lo puede contener d�gitos"},
		 	"codpostalstr": {required: "El campo C�digo Postal es obligatorio", minlength: "El campo C�digo Postal debe contener 5 d�gitos", digits: "El campo C�digo Postal s�lo puede contener d�gitos", notregex:"El campo C�digo Postal no tiene el formato correcto", cp: "El campo C�digo Postal no tiene el formato correcto"},
		 	
		 	"telefono1": {minlength: "El campo Tel�fono 1 debe contener 9 d�gitos", digits: "El campo Tel�fonoo s�lo puede contener d�gitos"},
		 	"telefono2": {minlength: "El campo Tel�fono 2 debe contener 9 d�gitos", digits: "El campo Tel�fono s�lo puede contener d�gitos"},
		 	"telefono3": {minlength: "El campo Tel�fono 3 debe contener 9 d�gitos", digits: "El campo Tel�fono s�lo puede contener d�gitos"},
	 	
		 	"telefonoFijoAsegurado": {minlength: "El campo Tel�fono Fijo Asegurado debe contener 9 d�gitos", digits: "El campo Tel�fono s�lo puede contener d�gitos"},
		 	"telefonoMovilAsegurado": {minlength: "El campo Tel�fono M�vil Asegurado debe contener 9 d�gitos", digits: "El campo Tel�fono s�lo puede contener d�gitos"},
		 	"emailAsegurado": {email: "El formato del campo E-mail Asegurado no es correcto"}
	 	}
	});
  	
  	jQuery.validator.addMethod("cp", function(value, element, params) { 					
		var codprov = $('#provincia').val();
		var codpostal = value;		
		if(codprov.length == 1){
			codprov = "0" + codprov;
		}						
		return 	codprov == codpostal.substring(0,2);	
	});		
	

  	jQuery.validator.addMethod("invalidChars", function(value, element, params) { 
		var validado = true;
		var invalidChars = ".,:;<>[]%!?()+*_=";
		for(var i = 0; i< value.length;i++){
			if(invalidChars.indexOf(value.charAt(i))!= -1){							
				validado = false;
				break;
			}
		}
		return (this.optional(element) || validado);	
	});
	
	jQuery.validator.addMethod("requiredDatos", function(value, element, params) { 
		var nombre = $('#tx_nombre').val();
		var apellido1 = $('#tx_apellido1').val();
		var razonsocial = $('#tx_razonsocial').val();
		var validado = false;					
		
		if (nombre != '' || apellido1 != '' || razonsocial != ''){						
			if (nombre != '' && apellido1 != '' && razonsocial == ''){
				validado = true;
			} else if (nombre == '' && apellido1 == '' && razonsocial != ''){
				validado = true;
			}
		} 					
		return validado;	
	});
	
	//TMR 23/05/2012
	jQuery.validator.addMethod("validaFechaOcurrencia", function(value, element) {
		return (this.optional(element) || UTIL.fechaMenorOIgualQueFechaActual(element.value));
	});
	
	jQuery.validator.addMethod("validaFechaOcurrencia6meses", function(value, element) {
		return (this.optional(element) || UTIL.fechaAnterior6Meses(element.value));
	});

});

function continuar(){
	
	$("#panelInformacion").hide();
	// comprobamos si existen datos en persona juridica y f�sica a la vez y mostramos aviso
	var nombre = $('#tx_nombre').val();
	var apellido1 = $('#tx_apellido1').val();
	var apellido2 = $('#tx_apellido2').val();
	var razonsocial = $('#tx_razonsocial').val();			
	
	if ((nombre != '' || apellido1 != '' || apellido2 !='') && razonsocial != ''){
		$('#panelAlertasValidacion').html("Seleccione entre persona f�sica o jur�dica como persona de contacto");
		$('#panelAlertasValidacion').show();
	}else{ 						
		$("#method").val("doGuarda");
		if($('#main').valid()){
			$.blockUI.defaults.message = '<h4> Grabando los datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$("#main").submit();
		}
	}
}  

// MPM - 04/07/2012
// Redirige a la p�gina de listado de parcelas de siniestro sin hacer validaciones ni guardar
function continuarSoloLectura(){				 
	$("#panelInformacion").hide();
	$("#listadoParcelas").submit();
}  

function limpiar(){
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	
	$("#sl_riesgo_siniestros").selectOptions("");			
	$("#tx_fecha_ocurrencia").val('');	
	$("#tx_observaciones").val('');	
	$("#tx_nombre").val('');	
	$("#tx_apellido1").val('');	
	$("#tx_apellido2").val('');	
	$("#tx_razonsocial").val('');	
	$("#via").val('');	
	$("#desc_via").val('');	
	$("#desc_provincia").val('');	
	$("#desc_localidad").val('');
	$("#tx_direccion").val('');	
	$("#tx_numero").val('');	
	$("#tx_piso").val('');	
	$("#tx_bloque").val('');	
	$("#tx_escalera").val('');
	$("#provincia").val('');
	$("#localidad").val('');
	$("#sublocalidad").val('');
	$("#cp").val('');
	$("#tx_telefono1").val('');
	$("#tx_telefono2").val('');
	$("#tx_telefono3").val('');
	
	$("#tx_telefono_fijo_asegurado").val('');
	$("#tx_telefono_movil_asegurado").val('');
	$("#emailAsegurado").val('');
	
}

function volver(){



	if ($('#fromUtilidades').val() == 'false') {
		$("#volverListado").submit();
	}
	else {
		$(window.location).attr('href', 'siniestros.html?idPoliza='+$('#idPoliza').val());
	} 
}

//funcion que valida un elemento independientemente, si no jquery no lo hace
function validarElemento(elemento){
	$("#main").validate().element(elemento);  
}
