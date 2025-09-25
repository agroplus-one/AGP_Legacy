function cambiarOficina(){}
		
$(document).ready(function(){

	// Para evitar el cacheo de peticiones al servidor
	var URL = UTIL.antiCacheRand($("#main").attr("action"));
	$("#main").attr("action", URL);
	
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
        inputField        : "fechabaja",
        button            : "btn_fechabaja",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
	
	
	$('#main').validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		onfocusout: function(element) {
   				if(($('#method').val() == "doAlta" || $('#method').val() == "doEdita") && !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
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
			"entidad.codentidad": {required: true, digits: true, rangelength:[3,4]},
			"id.codentidad":{required: true, digits: true, comprobarEntMediadora:true},
		 	"id.codsubentidad":{required: true, digits: true,range: [0,99]},
		 	"tipoidentificacion": {required: true},
		 	"cargoCuenta": {required: true},
			"codpostal": {required: true, minlength: 5, digits: true, notregex: "000$"},
			"nifcif": {required: function(element){return $('#tipoIdentificacion').val() !='';}, CIFNIF: true},
			"nombre": {required: function(element){return $('#tipoIdentificacion').val() == 'NIF' || $('#tipoIdentificacion').val() == 'NIE';}, letterswithwhitespace: true},
			"apellido1": {required: function(element){return $('#tipoIdentificacion').val() == 'NIF' || $('#tipoIdentificacion').val() == 'NIE';}, letterswithwhitespace: true},
			"apellido2": {required: function(element){return $('#tipoIdentificacion').val() == 'NIF' || $('#tipoIdentificacion').val() == 'NIE';}, letterswithwhitespace: true},
			"nomsubentidad": {required: function(element){return $('#tipoIdentificacion').val() == 'CIF';},  lettersnumberswhitespacecolonpoint: true},
			"forzarRevisionAM": {required: true},
			"calcularRcGanado": {required: true},
			"swConfirmacion":{required: true},
			"indGastosAdq":{required: true},
			"email":{email: true},
			"email2":{email: true},
			"firmaTableta":{required: true}
		},
		messages: {
			"entidad.codentidad": {required: "El campo Entidad es obligatorio", digits: "El campo Entidad s�lo puede contener d�gitos", rangelength: "El campo Entidad debe contener entre 3 y 4 d�gitos"},
			"id.codentidad":{required: "El campo Entidad mediadora es obligatorio", digits: "El campo Entidad mediadora s�lo puede contener d�gitos", comprobarEntMediadora:"Los 3 �ltimos digitos de entidad mediadora no coinciden con los de entidad"},
			"id.codsubentidad":{required: "El campo Subentidad mediadora es obligatorio", digits: "El campo Subentidad mediadora s�lo puede contener d�gitos",range: "Subentidad mediadora no v�lida"},
			"codpostal": {required: "El campo C�digo Postal es obligatorio", minlength: "El campo C�digo Postal debe contener 5 d�gitos", digits: "El campo C�digo Postal s�lo puede contener d�gitos", notregex:"El campo C�digo Postal no tiene el formato correcto"},
			"tipoidentificacion": {required: "El campo Tipo ident. es obligatorio"},
			"cargoCuenta":{required: "El campo Cargo en cuenta es obligatorio"},
			"nifcif": {required: "El campo CIF/NIF/NIE Asegurado es obligatorio", CIFNIF: "El campo CIF/NIF/NIE Asegurado tiene un formato incorrecto"},
			"nombre": {required: "El campo Nombre es obligatorio", letterswithwhitespace: "El campo Nombre no puede contener d�gitos ni caracteres especiales"},
			"apellido1": {required: "El campo Primer Apellido es obligatorio", letterswithwhitespace: "El campo 1� Apellido no puede contener d�gitos ni caracteres especiales"},
			"apellido2": {required: "El campo Segundo Apellido es obligatorio", letterswithwhitespace: "El campo 2� Apellido no puede contener d�gitos ni caracteres especiales"},
			"nomsubentidad": {required: "El campo Raz�n Social es obligatorio", lettersnumberswhitespacecolonpoint: "El campo Raz�n Social no puede contener caracteres especiales"},
			"forzarRevisionAM":{required: "El campo Forzar revisi�n A.M.y R.C. es obligatorio"},
			"calcularRcGanado":{required: "El campo Calcular RC Ganado es obligatorio"},
			"swConfirmacion":{required: "El campo SW Confirmacion es obligatorio"},
			"indGastosAdq":{required: "El campo Con Gastos de adquisición es obligatorio"},
			"email":{email: "El campo E-mail no contiene un e-mail v\u00E1lido"},
			"email2":{email: "El campo E-mail2 no contiene un e-mail v\u00E1lido"},
			"firmaTableta":{required: "El campo Firma en tableta es obligatorio"}

		}			
	});			
	
	jQuery.validator.addMethod("CIFNIF", function(value, element) { 
		return (this.optional(element) || generales.validaCifNif($("#tipoIdentificacion").val(), value));
	});		
	
	jQuery.validator.addMethod("comprobarEntMediadora", function(value, element) { 				
		return (this.optional(element) || value.substr(1) == $("#entidad").val().substr(1));
	});
	
});		

function cifnifseleccionado(){	
	if (document.getElementById('tipoIdentificacion').value == 'NIF' || document.getElementById('tipoIdentificacion').value == 'NIE'){ 
		var varActivas = new Array('nombre','apellido1','apellido2');
		for (var i = 0; i < varActivas.length; i++){
			document.getElementById(varActivas[i]).disabled = false;								
		}
		var varInactivas = new Array('razonsocial');
		for (var i = 0; i < varInactivas.length; i++){
			document.getElementById(varInactivas[i]).value = '';
			document.getElementById(varInactivas[i]).disabled = true;
		}
	}else{
		if (document.getElementById('tipoIdentificacion').value == 'CIF'){
			var varActivas = new Array('razonsocial');
			for (var i = 0; i < varActivas.length; i++){
				document.getElementById(varActivas[i]).disabled = false;
			}
			
			var varInactivas = new Array('nombre','apellido1','apellido2');
			
			for (var i = 0; i < varInactivas.length; i++){
				document.getElementById(varInactivas[i]).value = '';
				document.getElementById(varInactivas[i]).disabled = true;
			}
		}
	}
}

function modificar (codEntidad,nomEntidad,codEntidadMediadora,nomEntidadMediadora,
					codSubentidadMediadora,descTipoMediador,tipoId,nifCif,pagodirecto,
					nombre,apellido1,apellido2,nomSubentidadMediadora,
					codigoPostal,cargoCuenta,iban,forzarRevisionAM,calcularRcGanado,swConfirmacion,indGastosAdq,email,email2, firmaTableta){
	    $("#panelAlertasValidacion").hide();
					
		$("#entidad").val(codEntidad);
		$("#desc_entidad").val(nomEntidad);
		$("#entmediadora").val(codEntidadMediadora);
		$("#desc_entmediadora").val(nomEntidadMediadora);
		$("#subentmediadora").val(codSubentidadMediadora);
		$("#desc_tipomediador").val(descTipoMediador);	
		$("#nifcif").val(nifCif);		
		$("#nombre").val(nombre);
		$("#apellido1").val(apellido1);
		$("#apellido2").val(apellido2);
		$("#razonsocial").val(nomSubentidadMediadora);
		$("#cp").val(codigoPostal);
		$("#tipoIdentificacion").val(tipoId);
		$("#cargoCuenta").val(cargoCuenta);
		$("#forzarRevisionAM").val(forzarRevisionAM);
		$("#calcularRcGanado").val(calcularRcGanado);
		$("#entmediadora").attr("readonly",true);
		$("#subentmediadora").attr("readonly",true);	
		$("#swConfirmacion").val(swConfirmacion);
		$("#indGastosAdq").val(indGastosAdq);
		$("#email").val(email);
		$("#email2").val(email2);
		$("#firmaTableta").val(firmaTableta);
		
		if(pagodirecto == '1')
			$("#pagodirecto").attr("checked",true);
		else
			$("#pagodirecto").attr("checked",false);
		
		cifnifseleccionado();	
		
		$('#fechabaja').attr('disabled','true');	
		$('#btn_fechabaja').attr('disabled','true');	
		
		//BOTONES
		$('#btnAlta').hide();					
		$('#btnModificar').show();
		$('#btnModificar').show();
		
		if($("#cargoCuenta").val()=='1'){
			$('#btnEntidadesCargoCuenta').show();
		}
		$("#iban").val(iban);
		$('#cuenta1').val(iban.substring(0,4));
		$('#cuenta2').val(iban.substring(4,8));
		$('#cuenta3').val(iban.substring(8,12));
		$('#cuenta4').val(iban.substring(12,16));
		$('#cuenta5').val(iban.substring(16,20));
		$('#cuenta6').val(iban.substring(20,24));
		
}

function consultar(){
	$("#main").validate().cancelSubmit = true;
	$('#method').val("doConsulta");
	$('#main').submit();			
}   

function limpiarFiltro(){
	$("#entidad").val('');
	$("#desc_entidad").val('');
	$("#entmediadora").val('');			
	$("#subentmediadora").val('');
	$("#desc_tipomediador").val('');	
	$("#nifcif").val('');		
	$("#nombre").val('');
	$("#apellido1").val('');
	$("#apellido2").val('');
	$("#razonsocial").val('');
	$("#cp").val('');
	$("#tipoIdentificacion").val('');
	$("#pagodirecto").attr("checked",false);
	$("#cargoCuenta").val('');	
	$("#fechabaja").val('');	
	$("#cuenta1").val('');
	$("#cuenta2").val('');
	$("#cuenta3").val('');
	$("#cuenta4").val('');
	$("#cuenta5").val('');
	$("#cuenta6").val('');
	$("#iban").val('');
	$("#forzarRevisionAM").val('');
	$("#calcularRcGanado").val('');
	$("#swConfirmacion").val('');
	$("#indGastosAdq").val('');
	$("#email").val('');
	$("#email2").val('');
	$("#firmaTableta").val('');
	
	consultar();
}


function ajaxCheckBaja(entidad,subentidad){
	$.ajax({
		url:          "subentidadMediadora.html",
		data:         "method=verificarSubentidad&entidad=" + entidad+"&subentidad="+subentidad,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			jAlert("Error al realizar la baja de la Subentidad: " + quepaso , 'Error');
		},
		success: function(resultado){
			if (resultado.alert != null && resultado.alert != ""){
				jAlert(resultado.alert, 'Error');
			}else{
				switch(parseInt(resultado.datos)){
					case 0: // NO tiene usuarios asociados
						baja(entidad,subentidad,true);
						break;
					case 1: // SI tiene usuarios asociados
						jConfirm(' <strong>�Atenci�n! </strong> La Subentidad que est� intentando dar de baja tiene usuarios asociados.'+
								'<br> La baja implicar� que los usuarios asociados no podr�n acceder a la aplicaci�n.<br><center>�Desea continuar con el proceso?</center>',
								'Di�logo de Confirmaci�n', function(r) {
							if (r == true){
								baja(entidad,subentidad,false);
							}
						});
						
					default:
					  	break;
				}
			}
		},
		type: "GET"
	});
}


function baja(entidad,subentidad,mostrarAviso){
	$("#main").validate().cancelSubmit = true;
	if (mostrarAviso){
		jConfirm('�Est� seguro de que desea eliminar el registro seleccionado?','Di�logo de Confirmaci�n', function(r) {
			if (r==true){
				$('#entmediadora').val(entidad);
				$('#subentmediadora').val(subentidad);
				$('#method').val("doBaja");
				$('#main').submit();
			}
		});
	}else{
		$('#entmediadora').val(entidad);
		$('#subentmediadora').val(subentidad);
		$('#method').val("doBaja");
		$('#main').submit();
	}
}

function deshacerBaja(entidad,subentidad){
	$("#main").validate().cancelSubmit = true;	
		$('#entmediadora').val(entidad);
		$('#subentmediadora').val(subentidad);
		$('#method').val("doDeshacerBaja");
		$('#main').submit();
}


function alta(){
	limpiaAlertas();
	$("#panelAlertasValidacion").hide();
	if (validaEntidad()){
		if (comprobarIBAN()){
				var pagoDirecto = document.getElementById("pagodirecto").checked;
				if (!pagoDirecto || (pagoDirecto && document.getElementById('iban').value != '')){
					$('#method').val("doAlta");
					$('#main').submit();
				}else{
					$('#panelAlertasValidacion').html("El IBAN es obligatorio con el pago directo a mediador marcado");
					$('#panelAlertasValidacion').show();
				}
		}else{
			$('#panelAlertasValidacion').html("El IBAN es incorrecto");
			$('#panelAlertasValidacion').show();
		}
	}else{
		$('#panelAlertasValidacion').html("El c�digo de la Entidad Mediadora debe ser el mismo que el de la Entidad");
		$('#panelAlertasValidacion').show();
	}
}
function validaEntidad(){
	if ($('#entidad').val()== 4000 || $('#entidad').val()== 5000 || 
			$('#entidad').val()== 6000 || $('#entidad').val()== 7000
			|| $('#entidad').val()== 8000){
		if ($('#entidad').val() != $('#entmediadora').val()){
			return false;
		}
	} 
	return true;
	
}
function editar(){
	limpiaAlertas();
	$("#panelAlertasValidacion").hide();
	$('#panelInfo_adicional').hide();
	if (validaEntidad()){
		if (comprobarIBAN()){
			var pagoDirecto = document.getElementById("pagodirecto").checked;
			if (!pagoDirecto || (pagoDirecto && document.getElementById('iban').value != '')){
				$('#method').val("doEdita");
				$('#main').submit();
			}else{
				$('#panelAlertasValidacion').html("El IBAN es obligatorio con el pago directo a mediador marcado");
				$('#panelAlertasValidacion').show();
			}
		}else{
			$('#panelAlertasValidacion').html("El IBAN es incorrecto");
			$('#panelAlertasValidacion').show();
		}
	}else{
		$('#panelAlertasValidacion').html("El c�digo de la Entidad Mediadora debe ser el mismo que el de la Entidad");
		$('#panelAlertasValidacion').show();
	}
	
}

function comprobarIBAN(){
	// La funcion validar esta en el iban.js
	 if (document.getElementById('iban').value != ''){
		 var f = validarIBAN($("#iban").val());
		 if (f == false){
			 return false;
		 }
		 return true;
	 }else{
		 return true;
	 }
}


function limpiaAlertas() {
		$('#alerta').val("");
		$("#panelInformacion").hide();
		$("#panelAlertasValidacion").hide();
		$("#panelAlertas").hide();
		$("#panelInformacion").html('');
		$("#panelAlertasValidacion").html('');
		$("#panelAlertas").html('');
		$('#mensaje').val("");
		$("#panelMensajeValidacion").hide();
	    $("#panelMensajeValidacion").html('');
}	 
	 
function returnBack()
{
	$('#methodVolver').val('doConsulta');
	if (document.getElementById('tipoFichero')!= null)
	{
		var tipo = document.getElementById('tipoFichero').value;
		$('#revisar_tipoFichero').val(tipo);
	}
	if (document.getElementById('idFichero') != null){
		var idFichero = document.getElementById('idFichero').value;
		$('#revisar_idFichero').val(idFichero);
		
	}
	$('#revisarForm').submit();				
	
}

function showPopUpEntidadesCargoCuenta(){
	
	var codEntidad		= $('#entidad').val();
	var codEntidadMed 	= $('#entmediadora').val();
	var codSubentidad 	= $('#subentmediadora').val();
	
	$('#entidadCargoCuenta').val('');
	$('#entidadCargoCuenta_desc').val('');
	
	if(codEntidadMed!='' && codSubentidad!=''){
		$('#listaEntidadesCargoCuenta').empty();
		cargarListaEntidadesCargoCuentaAjax(codEntidad, codEntidadMed, codSubentidad);
		$('#panelInfo_adicional').html('');
		$('#panelInfo_adicional').hide();
		$('#panelAlertas_cargoCuenta').html('');
		$('#panelAlertas_cargoCuenta').hide();
	}else{
		//No deber�a
	}
}

function cerrarPopUpEntidadesCargoCuenta(){
	$('#divEntidadesCargoCuenta').fadeOut('normal');
	$('#overlayESMediadoras').hide();
}

function comprobarMostrarBotonCargoCuenta(){
	//Si el bot�n modificar est� oculto, entonces no hay que hacer nada siquiera
	if($('#btnModificar').css('display') == 'none' ){
		$('#btnEntidadesCargoCuenta').hide();
	}else{
		if($('#cargoCuenta').val()=='1'){
			$('#btnEntidadesCargoCuenta').show();
		}else{
			$('#btnEntidadesCargoCuenta').hide();
		}
	}
}

function addEntidadCargoCuenta(){
	
	var entidadCargoCuenta = $('#entidadCargoCuenta').val();
	var entidadCargoCuenta_desc = $('#entidadCargoCuenta_desc').val();
	var entidadCargoCuentaFull = entidadCargoCuenta + " - " + entidadCargoCuenta_desc;
	
	if(entidadCargoCuenta!='' && entidadCargoCuenta_desc!=''){
		var options = $('#listaEntidadesCargoCuenta').attr('options'); 
		options[options.length] = new Option(entidadCargoCuentaFull, entidadCargoCuenta, true, true);
		$('#entidadCargoCuenta').val('');
		$('#entidadCargoCuenta_desc').val('');
		$('#panelAlertas_cargoCuenta').html('');
		$('#panelAlertas_cargoCuenta').hide();
	}else{
		$('#panelAlertas_cargoCuenta').html("Debe completar los datos de entidad");
		$('#panelAlertas_cargoCuenta').show();
	}
}

function deleteEntidadCargoCuenta(){

	//String separado por comas
	var arraySeleccionados	= $("#listaEntidadesCargoCuenta").val();
	var codEntidadPrincipal	= $('#entidad').val();
	var incluyeEntidadPrincipal = false;
	
	if(arraySeleccionados != null){
		for(var i in arraySeleccionados){
			if(codEntidadPrincipal==arraySeleccionados[i]){
				incluyeEntidadPrincipal = true;
				break;
			}
		}
		
		if(incluyeEntidadPrincipal){
			$('#panelAlertas_cargoCuenta').html("No se puede borrar la entidad principal: " + codEntidadPrincipal);
			$('#panelAlertas_cargoCuenta').show();
		}else{
			$("#listaEntidadesCargoCuenta option:selected").remove();
			$('#panelAlertas_cargoCuenta').html('');
			$('#panelAlertas_cargoCuenta').hide();
		}
	}
}

function cargarListaEntidadesCargoCuentaAjax(codEntidad, codEntidadMed, codSubentidad){
	$.ajax({
		    url: "subentidadMediadora.html?method=doCargarListaEntidadesCargoCuenta&codEntidad="+codEntidad+"&codEntidadMed="+codEntidadMed+"&codSubentidad="+codSubentidad,
			data: "",
			async: true,
		    dataType: "json",
		    cache: false,
		    error: function(objeto, quepaso, otroobj){
                alert("Error al cargar la lista de entidades de cargo a cuenta: " + quepaso);
            },
            success: function(datos){
            	rellenarSelectEntidadesCargoCuenta(datos.listaEntidadesCargoCuenta);
            	$('#divEntidadesCargoCuenta').fadeIn('normal');
            	$('#overlayESMediadoras').show();
            },
            type: "GET"
		    
		});
}

function rellenarSelectEntidadesCargoCuenta(listaEntidadesCargoCuenta){
	
	if(listaEntidadesCargoCuenta!=null){
		for(var i in listaEntidadesCargoCuenta){
			
			var elemento = listaEntidadesCargoCuenta[i];
			var elementoDividido = elemento.split('|');
			var descripcionEntidad = elementoDividido[0] + " - " + elementoDividido[1];
			var options = $('#listaEntidadesCargoCuenta').attr('options'); 
			options[options.length] = new Option(descripcionEntidad, elementoDividido[0]);//, true, true);
		}
	}else{
		//
	}
}


function botonAceptarEntidadesCargoCuenta(){
	
	jConfirm('�Est� seguro de que desea guardar los registros seleccionados?','Di�logo de Confirmaci�n', function(r) {
		if (r==true){
			var entidadesCargoCuenta = "";
			$("#listaEntidadesCargoCuenta option").each(function(){
				entidadesCargoCuenta = entidadesCargoCuenta + ($(this).attr('value')) + "|";
			});
			
			var codEntidad		= $('#entidad').val();
			var codEntidadMed 	= $('#entmediadora').val();
			var codSubentidad 	= $('#subentmediadora').val();
			
			//cadenaSeleccionados contiene la lista de entidades de cargo a cuenta separadas por "|"
			cambiarEntidadesCargoCuentaAjax(codEntidad, codEntidadMed, codSubentidad, entidadesCargoCuenta);
		}
	});
}

function cambiarEntidadesCargoCuentaAjax(codEntidad, codEntidadMed, codSubentidad, entidadesCargoCuenta){
	$.ajax({
	    url: "subentidadMediadora.html?method=doGuardarListaEntidadesCargoCuenta&codEntidad="+codEntidad+"&codEntidadMed="+codEntidadMed+"&codSubentidad="+codSubentidad+"&entidadesCargoCuenta="+entidadesCargoCuenta,
		data: "",
		async:true,
	    dataType: "json",
	    cache: false,
	    error: function(objeto, quepaso, otroobj){
            alert("Error al guardar la lista de entidades de cargo a cuenta: " + quepaso);
        },
        success: function(datos){
        	$('#divEntidadesCargoCuenta').fadeOut('normal');
        	$('#overlayESMediadoras').hide();
    		$('#panelInfo_adicional').html("Datos de entidades de cargo en cuenta guardados");
    		$('#panelInfo_adicional').show();
        },
        type: "GET"
	});
}

function cambiaZIndexOverlay(){
	//$('#overlay').css('position', 'relative');
	$('#overlay').css('z-index', 1005);
}

