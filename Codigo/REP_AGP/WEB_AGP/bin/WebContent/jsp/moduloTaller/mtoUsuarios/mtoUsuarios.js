$(document).ready(function(){
	
	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
    
    if ($('#alerta').val()==""){
	    if ($('#btnModificar').is(":visible") ){
	    	var externo = $('#externo').val();
	    	deshabilitaCampos(externo);
		}
    }else{
    	if ($('#origenLlamada').val() == "doAlta"){
    		$('#btnModificar').hide();
    	}
    }
    if ($('#btnModificar').is(":visible") ){
    	$('#externo').attr('disabled', true);
    }
    
	$('#main3').validate({
	
		onfocusout: function(element) {
			var frm = document.getElementById('main3');
			if ( (frm.method.value == "doEditar") || (frm.method.value == "doAlta") ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		highlight: function(element, errorClass) {
		 	$("#campoObligatorio_" + element.id).show();
	     },
		 unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		 },
		rules: {
			"codusuario":{required: true},
			"nombreusu":{required: true},
			"tipousuario": {required: true},
			"oficina.id.codentidad":{required: true,digits: true},
			"oficina.id.codoficina":{required: true,digits: true},
			"subentidadMediadora.id.codentidad":{required: true,digits: true},
			"subentidadMediadora.id.codsubentidad":{required: true,digits: true},
			"delegacion":{required: true,digits: true},
			"cargaPac":{required: true},
			"email":{email: true},
			"financiar":{required: true},
			"impMinFinanciacion":{number: true, VALDECIMAL92:true, range: [0, 999999999.99]},
			"impMaxFinanciacion":{number: true, VALDECIMAL92:true, range: [0, 999999999.99], comprobarImportes: ['impMinFinanciacion', 'impMaxFinanciacion']},
			"fechaLimite":{dateITA: true, validaFechaLimite:['fechaLimiteId']}
		},
		 messages: {
		 	"codusuario":{required: "El campo Usuario es obligatorio"},
		 	"nombreusu":{required: "El campo Nombre usuario es obligatorio"},
		 	"tipousuario": {required: "El campo Perfil es obligatorio"},
		 	"oficina.id.codentidad": {required: "El campo Entidad es obligatorio",digits: "El campo Entidad s\u00F3lo puede contener d\u00EDgitos"},
		 	"oficina.id.codoficina":{required: "El campo Oficina es obligatorio",digits: "El campo Oficina s\u00F3lo puede contener d\u00EDgitos"},
		 	"subentidadMediadora.id.codentidad":{required: "El campo Entidad Mediadora es obligatorio",digits: "El campo Entidad Mediadora s\u00F3lo puede contener d\u00EDgitos"},
			"subentidadMediadora.id.codsubentidad":{required: "El campo Subentidad Mediadora es obligatorio",digits: "El campo Subentidad Mediadora s\u00F3lo puede contener d\u00EDgitos"},
			"delegacion":{required: "El campo Delegacion es obligatorio",digits: "El campo Delegacion s\u00F3lo puede contener d\u00EDgitos"},
			"cargaPac":{required: "El campo Carga PAC es obligatorio"},
			"email":{email: "El campo E-mail no contiene un e-mail v\u00E1lido"},
			"financiar":{required: "El campo Financiar es obligatorio"},
			"impMinFinanciacion":{number:"El campo Importe M\u00EDnimo s\u00F3lo puede contener d\u00EDgitos", VALDECIMAL92: "El campo Importe M\u00EDnimo debe contener como m\u00E1ximo 9 d\u00EDgitos de parte entera un punto y 2 d\u00EDgitos de parte decimal", range: "El campo Importe M\u00EDnimo s\u00F3lo puede contener d\u00EDgitos entre 0 y 999999999,99"}, 
			"impMaxFinanciacion":{number:"El campo Importe M\u00E1ximo s\u00F3lo puede contener d\u00EDgitos", VALDECIMAL92: "El campo Importe M\u00E1ximo debe contener como m\u00E1ximo 9 d\u00EDgitos de parte entera un punto y 2 d\u00EDgitos de parte decimal", range: "El campo Importe M\u00E1ximo s\u00F3lo pued contener d\u00EDgitos entre 0 y 999999999,99", comprobarImportes: "El campo Importe M\u00EDnimo debe ser menor o igual al campo Importe M\u00E1ximo"},
			"fechaLimite":{dateITA: "El campo Fecha L\u00EDmite no contiene una fecha v\u00E1lida", validaFechaLimite:"El campo Fecha L\u00EDmite debe ser igual o posterior a la fecha actual"}
		 }
	});
	

    $('#main').validate({
    	errorLabelContainer: "#txt_mensaje_cm",
    	wrapper: "li",
    	onfocusout: function(element) {
			var frm = document.getElementById('main');
			//if ( (frm.method.value == "doEditar") || (frm.method.value == "doAlta") ) {
				this.element(element);
			//}
		},
		highlight: function(element, errorClass) {
		 	$("#campoObligatorio_" + element.id).show();
	     },
		 unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		 },
    	rules: {
			"tipousuario_cm": {digits: true},
			"entidad_cm":{digits: true},
			"oficina_cm":{digits: true},
			"entmediadora_cm":{digits: true},
			"subentmediadora_cm":{digits: true},
			"delegacion_cm":{digits: true},
			"email_cm":{email: true},
			"impMinFinanciacion_cm":{number: true, VALDECIMAL92:true, range: [0, 999999999.99]},
			"impMaxFinanciacion_cm":{number: true, VALDECIMAL92:true, range: [0, 999999999.99], comprobarImportes: ['impMinFinanciacion_cm', 'impMaxFinanciacion_cm']},
			"fechaLimite_cm":{dateITA: true, validaFechaLimite:['fechaLimite_cm']}
		},
		 messages: {
		 	"tipousuario_cm": {digits: "El campo Entidad s\u00F3lo puede contener d\u00EDgitos"},
		 	"entidad_cm": {digits: "El campo Entidad s\u00F3lo puede contener d\u00EDgitos"},
		 	"oficina_cm":{digits: "El campo Oficina s\u00F3lo puede contener d\u00EDgitos"},
		 	"entmediadora_cm":{digits: "El campo Entidad Mediadora s\u00F3lo puede contener d\u00EDgitos"},
			"subentmediadora_cm":{digits: "El campo Subentidad Mediadora s\u00F3lo puede contener d\u00EDgitos"},
			"delegacion_cm":{digits: "El campo Delegaci\u00F3n s\u00F3lo puede contener d\u00EDgitos"},
			"email_cm":{email: "El campo E-mail no contiene un e-mail v\u00E1lido"},
			"impMinFinanciacion_cm":{number:"El campo Importe M\u00EDnimo s\u00F3lo puede contener d\u00EDgitos", VALDECIMAL92: "El campo Importe M\u00EDnimo debe contener como m\u00E1ximo 9 d\u00EDgitos de parte entera un punto y 2 d\u00EDgitos de parte decimal", range: "El campo Importe M\u00EDnimo s\u00F3lo puede contener d\u00EDgitos entre 0 y 999999999,99"},
			"impMaxFinanciacion_cm":{number:"El campo Importe M\u00E1ximo s\u00F3lo puede contener d\u00EDgitos", VALDECIMAL92: "El campo Importe M\u00E1ximo debe contener como m\u00E1ximo 9 d\u00EDgitos de parte entera un punto y 2 d\u00EDgitos de parte decimal", range: "El campo Importe M\u00E1ximo s\u00F3lo puede contener d\u00EDgitos entre 0 y 999999999,99", comprobarImportes: "El campo Importe M\u00EDnimo debe ser menor o igual al campo Importe M\u00E1ximo"},
			"fechaLimite_cm":{dateITA: "El campo Fecha L\u00EDmite no contiene una fecha v\u00E1lida", validaFechaLimite: "La Fecha l\u00EDmite debe ser igual o posterior a la fecha actual"}
		 }
    	
    });
	
	jQuery.validator.addMethod("VALDECIMAL92", function(value, element) {
    	if(value.length == 0)
    		return true;
    	else
    		return /^\d{0,9}([.]\d{1,2})?$/.test(value);
    });
	
	jQuery.validator.addMethod("validaFechaLimite", function(value, element) {
		return (this.optional(element) || UTIL.fechaMayorOIgualQueFechaActual(element.value));
	});
	
	jQuery.validator.addMethod("comprobarImportes", function(value, element, params) {
		return (this.optional(element) || precioMaximoMayor(document.getElementById(params[0]), document.getElementById(params[1])));
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
        inputField        : "fechaLimiteId",
        button            : "btn_fechaLimite",
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
        inputField        : "fechaLimite_cm",
        button            : "btn_fechaLimite_cm",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"
  	});
	
});

function precioMaximoMayor(importeMinimo, importeMaximo) {
var res = false;
var min = importeMinimo.value;
var max = importeMaximo.value;
	if(min.length > 0 && max.length > 0) {
			if (parseFloat(max) >= parseFloat(min)) {
		    	 res = true;
		    }
			else {
				res = false;
			}
	}
	else {
		res = true;
	}
return res;
}




function consultarInicial () {
	limpiaAlertas(); 
	var frm = document.getElementById('main3');
	if (validarConsulta()){
		if (comprobarCampos(false)){
			frm.method.value = 'doConsulta';
			frm.origenLlamada.value= 'primeraBusqueda';
			$("#main3").validate().cancelSubmit = true;
			$('#main3').submit();
		}// Si no, muestra el aviso
		else {
			avisoUnCampo ();
		}
	}
}
function consultar(){
	limpiaAlertas();
	
	$('#marcaTodos').val('false');
	$('#listaIdsMarcados').val('');
	$("input[type=checkbox]").each(function(){
		$(this).attr('checked',false);
	});
	
	
	if (validarConsulta()){
		if (comprobarCampos(true)){
			$("#btnModificar").hide();
			lanzarConsulta ();
		}// Si no, muestra el aviso
		else {
			avisoUnCampo ();
		}
	}
}

//Lanza la consulta de fechas de contratacion
function lanzarConsulta () {
	// Llama al metodo que llama al servidor
	onInvokeAction('consultaUsuarios','filter');
}

//Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo () {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}

function limpiaAlertas() {
	$('#alerta').val("");
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');	
	$("#panelAlertas").html('');
	
	$('#listaIdsMarcados').val('');
	
	}

function comprobarCampos(incluirJmesa){
	
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('consultaUsuarios');
	}
   	var resultado = false;
   	
   	if ($('#codusuario').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','codusuario', $('#codusuario').val());
   		}
   		resultado = true;
   	}
   	if ($('#nombreusu').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','nombreusu', $('#nombreusu').val());
   		}
   		resultado = true;
   	}
   	if ($('#tipousuario').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','tipousuario', $('#tipousuario').val());
   		}
   		resultado = true;
   	}
   	if ($('#entidad').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','oficina.id.codentidad', $('#entidad').val());
   		}
   		resultado = true;
   	}
   	if ($('#oficina').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','oficina.id.codoficina', $('#oficina').val());
   		}
   		resultado = true;
   	}
   	if ($('#entmediadora').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','subentidadMediadora.id.codentidad', $('#entmediadora').val());
   		}
   		resultado = true;
   	}
	if ($('#subentmediadora').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','subentidadMediadora.id.codsubentidad', $('#subentmediadora').val());
   		}
   		resultado = true;
   	}
	if ($('#delegacion').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','delegacion', $('#delegacion').val());
   		}
   		resultado = true;
   	}
	if ($('#externo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','externo', $('#externo').val());
   		}
   		resultado = true;
   	}
	if ($('#cargaPac').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','cargaPac', $('#cargaPac').val());
   		}
   		resultado = true;
   	}
	if ($('#email').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','email', $('#email').val());
   		}
   		resultado = true;
   	}
	if ($('#financiar').val() != '') {
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','financiar', $('#financiar').val());
   		}
   		resultado = true;
   	}
	if ($('#impMinFinanciacion').val() != '') {
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','impMinFinanciacion', $('#impMinFinanciacion').val());
   		}
   		resultado = true;
   	}
	if ($('#impMaxFinanciacion').val() != '') {
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','impMaxFinanciacion', $('#impMaxFinanciacion').val());
   		}
   		resultado = true;
   	}
	if ($('#fechaLimiteId').val() != '') {
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaUsuarios','fechaLimite', $('#fechaLimiteId').val());
   		}
   		resultado = true;
   	}	
	return resultado;
}
function deshabilitaCampos(externo){
	
	// si es interno todos los campos deshabilitados excepto e-s mediadora y delegacion
	if (externo==0){
		$('#tipousuario').attr('disabled', true);
		$('#entidad').attr('readonly', true);
		$('#oficina').attr('readonly', true);
		$('#desc_entidad').attr('readonly', true);
		$('#desc_oficina').attr('readonly', true);
		$('#email').attr('readonly', true);
		$('#externo').attr('disabled', true);
	}else{
		$('#tipousuario').attr('disabled', false);
		$('#entidad').attr('readonly', false);
		$('#oficina').attr('readonly', false);
		$('#desc_entidad').attr('readonly', false);
		$('#desc_oficina').attr('readonly', false);
		$('#email').attr('readonly', false);
		$('#externo').attr('disabled', true);
	}
	//el codusuario no se puede modificar
	//$('#codusuario').attr('readonly', true);
	
	
}
function subirRegistro(codusuario,tipousuario,codentidad,
		codoficina,entMedia,subEntMedia,delegacion,nombreusu,nomEntidad,
		nomOfi,nomSubEntMed,externo,cargaPac,email,financiar,importeMinimo,importeMaximo,fechaLimite) {
	
	var frm = document.getElementById('main3');
	frm.codusuario.value = codusuario;
	frm.nombreusu.value = nombreusu;
	frm.entidad.value = codentidad;
	frm.oficina.value = codoficina;
	frm.entmediadora.value = entMedia;
	frm.subentmediadora.value = subEntMedia;
	frm.delegacion.value = delegacion;
	frm.desc_entidad.value = nomEntidad;
	frm.desc_oficina.value = nomOfi;
	frm.desc_subentmediadora.value = nomSubEntMed;
	
	/* Al subir el registro: Si en el select solo tenemos 3 opciones es que
	 * el usuario que esta logado en la aplicacion es externo, por lo que al
	 * subir el regitro si el perfil es distinto de 1 o 3,debera seleccionar "todos"
	 */
	frm.externo.value = externo;
	frm.externoIni.value = externo;
	frm.cargaPac.value = cargaPac;
	frm.email.value = email;
	frm.financiar.value = financiar;
	frm.impMinFinanciacion.value = importeMinimo;
	frm.impMaxFinanciacion.value = importeMaximo;
	frm.fechaLimiteId.value = fechaLimite;
	if (frm.tipousuario.length == 3){
		
		if (tipousuario!= 1 && tipousuario!=3){
			frm.tipousuario.value=""; // Todos
		
		}else{
			frm.tipousuario.value = tipousuario;
		}
	}else{
		
		frm.tipousuario.value = tipousuario;
	}
}

//Limpia los valores introducidos y vuelve a la pantalla inicial
function limpiar () {
	$('#marcaTodos').val('false');
	$('#listaIdsMarcados').val('');
	$("input[type=checkbox]").each(function(){
		$(this).attr('checked',false);
	});
	
	$('#limpiar').submit();
}

function editar(codusuario,tipousuario,codentidad,
		codoficina,entMedia,subEntMedia,delegacion,nombreusu,nomEntidad,
		nomOfi,nomSubEntMed,externo,cargaPac,email,financiar,importeMinimo,importeMaximo,fechaLimite){

	limpiaAlertas();
	
	var frm = document.getElementById('main3');
	frm.codusuario.value = codusuario;
	frm.nombreusu.value = nombreusu;
	frm.tipousuario.value = tipousuario;
	frm.entidad.value = codentidad;
	frm.oficina.value = codoficina;
	frm.entmediadora.value = entMedia;
	frm.subentmediadora.value = subEntMedia;
	frm.delegacion.value = delegacion;
	frm.desc_entidad.value = nomEntidad;
	frm.desc_oficina.value = nomOfi;
	frm.desc_subentmediadora.value = nomSubEntMed;
	frm.perfilIni.value = tipousuario;
	frm.externo.value = externo;
	frm.externoIni.value = externo;
	frm.cargaPac.value = cargaPac;
	frm.email.value = email;
	frm.financiar.value = financiar;
	frm.impMinFinanciacion.value = importeMinimo;
	frm.impMaxFinanciacion.value = importeMaximo;
	frm.fechaLimiteId.value = fechaLimite;
	deshabilitaCampos(externo);
	
	frm.codusuInicial.value = codusuario;
	$('#btnModificar').show();
}


function modificar() {
	limpiaAlertas();
	var frm = document.getElementById('main3');
	if (frm.codusuInicial.value == frm.codusuario.value) {
		var submit = false;
				if(frm.externo.value != 0) {
					if (frm.tipousuario.value != 1 && frm.tipousuario.value != 3) {
						if(frm.tipousuario.value != '') {
							$('#panelAlertasValidacion').html("El tipo de perfil s\u00F3lo puede ser 1 o 3");
							$('#panelAlertasValidacion').show();
						}
						else {
							submit = true;
						}
					}
					else {
						submit = true;
					}
				}
				else {
					submit = true;
				}
		
		if (submit == true) {
			if ($('#main3').valid()) {
				$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
				$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				$('#tipousuario').attr('disabled', false);
				$('#externo').attr('disabled', false);
			}	
			frm.method.value = "doEditar";
			$('#main3').submit();

		}
	} else {
		$('#panelAlertasValidacion').html(
				"El c\u00F3digo de usuario no puede modificarse");
		$('#panelAlertasValidacion').show();
	}
}
function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
   	var frm = document.getElementById('main3');
    $.get('mtoUsuarios.run?ajax=true&' + parameterString, function(data) {
        $("#grid").html(data);
        comprobarChecks();
		});
}


function borrar(codUsuario) {
	
	limpiaAlertas();
	
	if(confirm('\u00BFEst\u00E1 seguro de que desea eliminar el usuario seleccionado?')){
		$("#methodBorrar").val("doBorrar");
		$("#codUsuarioBorrar").val(codUsuario);
		$("#frmBorrar").submit();
	}	
}

 
function alta() {
	limpiaAlertas();
	
	var frm = document.getElementById('main3');
	if ($("#main3").valid()){
		if(frm.externo.value != 1) {
			$('#panelAlertasValidacion').html("El tipo de usuario s\u00F3lo puede ser externo");
			$('#panelAlertasValidacion').show();
		}
		else {
				if (frm.tipousuario.value!= 1 && frm.tipousuario.value!= 3){
					$('#panelAlertasValidacion').html("El tipo de perfil s\u00F3lo puede ser 1 o 3");
					$('#panelAlertasValidacion').show();
				}else{
					frm.method.value= "doAlta";
					$("#main3").submit();					
				}
			}
	}
}   

function validarConsulta() {
	
	
	// Valida el campo 'entidad' si esta informado
	if ($('#entidad').val() != ''){
		var entidadOk = false;
		try {
			var auxentidad =  parseFloat($('#entidad').val());
			if(!isNaN(auxentidad)){
				$('#entidad').val(auxentidad);
				entidadOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!entidadOk) {
			$('#panelAlertasValidacion').html("Valor para la entidad no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	// Valida el campo 'codoficina' si esta informado		 	
 	if ($('#oficina').val() != ''){ 
 		
	 	var oficinaOk = false;
	 	try {		 	
	 		var valor =  parseFloat($('#oficina').val());
	 		if(!isNaN(valor)){
				$('#oficina').val(valor);
				oficinaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!oficinaOk) {
			$('#panelAlertasValidacion').html("Valor para el campo oficina no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
 	// Valida el campo 'entidadMediadora' si esta informado		 	
 	if ($('#entmediadora').val() != ''){ 
	 	var entmediadoraOk = false;
	 	try {		 	
	 		var valor =  parseFloat($('#entmediadora').val());
	 		if(!isNaN(valor)){
				$('#entmediadora').val(valor);
				entmediadoraOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!entmediadoraOk) {
			$('#panelAlertasValidacion').html("Valor para el campo Entidad Mediadora no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
 	// Valida el campo 'codoficina' si esta informado		 	
 	if ($('#subentmediadora').val() != ''){ 
	 	var subentmediadoraOk = false;
	 	try {		 	
	 		var valor =  parseFloat($('#subentmediadora').val());
	 		if(!isNaN(valor)){
				$('#subentmediadora').val(valor);
				subentmediadoraOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!subentmediadoraOk) {
			$('#panelAlertasValidacion').html("Valor para el campo Subentidad Mediadora no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
 	
 	// Valida el campo 'codoficina' si esta informado		 	
 	if ($('#delegacion').val() != ''){ 
	 	var delegacionOk = false;
	 	try {		 	
	 		var valor =  parseFloat($('#delegacion').val());
	 		if(!isNaN(valor)){
				$('#delegacion').val(valor);
				delegacionOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!delegacionOk) {
			$('#panelAlertasValidacion').html("Valor para el campo Delegaci�n no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
 	
 	if($('#impMinFinanciacion').val() != '') {
 		var importeMinimoOk = false;
 		try {		 	
	 		var valor =  parseFloat($('#impMinFinanciacion').val());
	 		if(!isNaN(valor)){
				$('#impMinFinanciacion').val(valor);
				importeMinimoOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!importeMinimoOk) {
			$('#panelAlertasValidacion').html("Valor para el Importe m�nimo no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
 	}
 	
 	if($('#impMaxFinanciacion').val() != '') {
 		var importeMaximoOk = false;
 		try {		 	
	 		var valor =  parseFloat($('#impMaxFinanciacion').val());
	 		if(!isNaN(valor)){
				$('#impMaxFinanciacion').val(valor);
				importeMaximoOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!importeMaximoOk) {
			$('#panelAlertasValidacion').html("Valor para el Importe m\u00E1ximo no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
 	}
 	
 	if($('#fechaLimiteId').val() != '') {
 		var fechaLimite = validaFechaddMMYYYY($('#fechaLimiteId').val());
 		if(fechaLimite) {
 			return true;
 		}
 		else {
 			$('#panelAlertasValidacion').html("Valor para la fecha l\u00EDmite no v\u00E1lida");
			$('#panelAlertasValidacion').show();
 			return false;
 		}
 	}
 	
	return true;
}

function incrementarFechaLimite() {
	var listaIdsMarcados = $('#listaIdsMarcados').val();
	if(listaIdsMarcados.length > 0) {
		jConfirm('Se va a proceder a sumar un a\u00F1o a la fecha l\u00EDmite para financiaci\u00F3n de <U>TODOS</U> los registros seleccionados. \u00BFDesea continuar?', 'Di\u00E1logo de Confirmaci\u00F3n', function(r) {
			if (r) {
				var frm = document.getElementById('frmIncrementarFecha');
				$("#method").val("doIncrementarFecha");
				$("#listaIdsMarcados_ifecha").val(listaIdsMarcados);
				
				$.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Se estan actualizando los registros, espere por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			      $.blockUI({
			          overlayCSS: { backgroundColor: '#525583'},
			          baseZ: 2000
			    });
				
				$("#frmIncrementarFecha").submit();
			}
		});
	}
	else {
		showPopUpAviso("Debe seleccionar como m\u00EDnimo un Usuario.");
	}
}
