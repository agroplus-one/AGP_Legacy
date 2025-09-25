var isAccionCalcular = false;
var isAccionIrPanelGruposRaza = false;

/**
 * Validaciones del formulario
 */
$(document).ready(function() {
	//Se capturan los eventos de teclado
//	 var URL = UTIL.antiCacheRand(document.getElementById("datosExplotaciones").action);
//	 document.getElementById("datosExplotaciones").action = URL;
	limpiarAlertas();    
	var contador = 0;
	
	//Deshabilita campos de texto en modoLectura
	if($('#modoLectura').val()=='modoLectura'){		
		formulario = document.getElementById("datosExplotaciones");
		deshabiltaTextBox(formulario);
	}else{
		if(datosExplotaciones.codtipocapital.value!=null && datosExplotaciones.codtipocapital.value!=''){
			//alert(datosExplotaciones.codtipocapital.value);
			comprobarInputNumAnimales(datosExplotaciones.codtipocapital.value, 'datosExplotaciones');
		}
	}
	//Compatibilidad internet explorer 5
	document.onkeydown = function(e) {
		var evento = e || window.event;
	    switch (evento.keyCode) {
	        case 34:
	        	contador++;
	            $(document).keypress(irPanelGrupoRaza());
	            break;
	        case 33:
	        	if(contador > 0){
	        		contador++;
	        		$(document).keydown(irPanelDatosIdentificativos());
	        	}
	            break;
	    }
	};

	// Se sobreescribe la css de blockUI para que al deshabilitar el panel de grupos de raza no se muestre el cursor de espera y lo haga el de por defecto
	$.blockUI.defaults.css.cursor = 'default';
	$.blockUI.defaults.overlayCSS.cursor = 'default';
	
	// Comprobar si tiene coberturas
	//alert($('#tieneCoberturas').val());
	//alert("modoLectura: "+$('#modoLectura').val())
	if ($('#tieneCoberturas').val() != 'true'){		
		$('#contenedorCoberturas').hide();
	}

	// Deshabilitar el panel de grupos de raza
	$('#divGrupoRaza').block({message:null});
	
	$( "#datosExplotaciones" ).validate({
		  errorLabelContainer : "#panelAlertasValidacion",
		  wrapper : "li", highlight : function(element,errorClass) {
				$("#campoObligatorio_" + element.id).show();
			},
		  unhighlight : function(element,errorClass) {
				$("#campoObligatorio_" + element.id).hide();
			},
		  rules: {
			  "termino.id.codprovincia": {required: true, digits: true, isValidProvincia: true}, // Obligatorio y num�rico
			  "termino.id.codcomarca": {required: true, digits: true, isValidComarca:true}, // Obligatorio y num�rico
			  "termino.id.codtermino": {required: true, digits: true, isValidTermino:true}, // Obligatorio y num�rico
			  "termino.id.subtermino": {required: function (element){return (!$('#subtermino').val().length>0)}}, // Obligatorio
			  // Obligatorio si est� informado el campo 'Latitud' y num�rico
			  "latitud":  {digits: true, required: function (element){return ($('#longitud').val().length>0)}},
			  // Obligatorio si est� informad el campo 'Longitud' y num�rico
			  "longitud": {digits: true, required: function (element){return ($('#latitud').val().length>0)}},
			  // Obligatorio si no est� informado el campo 'Sigla'
			  "rega": {required: function (element){return ($('#sigla').val().length==0)}},
			  // Obligatorio si no est� informado el campo 'REGA'
			  "sigla": {required: function (element){return ($('#rega').val().length==0)}},
			  "subexplotacion": {digits: true}, // Num�rico
			  "especie": {required: true, digits: true, isValidEspecie: true}, // Obligatorio y num�rico
			  "regimen": {required: true, digits: true, isValidRegimen: true}, // Obligatorio y num�rico
			  
			  // A partir de aqu� s�lo se valida si el panel 'Grupos de raza' est� habilitado
			  "grupoRazas[0].codgruporaza": {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true, isValidGrupoRaza: true}, // Obligatorio y num�rico
			  "grupoRazas[0].codtipocapital": {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true, isValidTipoCapital: true}, // Obligatorio y num�rico
			  "grupoRazas[0].codtipoanimal": {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true, isValidTipoAnimal: true}, // Obligatorio y num�rico
			  "grupoRazas[0].numanimales": {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true}, // Obligatorio y num�rico
			  
			  // El precio s�lo se valida si el panel est� habilitado y la acci�n no es 'Calcular'
			  "grupoRazas[0].precioAnimalesModulos[0].precio": {required: validarPrecio, range: [0, 9999999.9999]} // Obligatorio, num�rico de 7 d�gitos en la parte entera y 4 en la decimal
		  },
		  messages: {
			  "termino.id.codprovincia":  {required : "El campo Provincia es obligatorio", 
				  						   digits: "El campo Provincia s&oacute;lo puede contener d&iacute;gitos",
				  						   isValidProvincia: "El c�digo de provincia no est� asociado a la clase seleccionada."},
			  "termino.id.codcomarca":  {required : "El campo Comarca es obligatorio", 
					  					   digits: "El campo Comarca s&oacute;lo puede contener d&iacute;gitos",
					  					 isValidComarca:"El c�digo de comarca no est� asociado a la clase seleccionada."},
			  "termino.id.codtermino":  {required : "El campo T&eacute;rmino es obligatorio", 
						  				   digits: "El campo T&eacute;rmino s&oacute;lo puede contener d&iacute;gitos",
						  				 isValidTermino:"El c�digo del t�rmino no est� asociado a la clase seleccionada."},
			  "termino.id.subtermino":  {required : "El campo Subt&eacute;rmino es obligatorio"},
			  "latitud":  { digits: "El campo Latitud s&oacute;lo puede contener d&iacute;gitos", 
				  			required : "El campo Latitud es obligatorio si se informa el campo Longitud"},
	  		  "longitud":  { digits: "El campo Longitud s&oacute;lo puede contener d&iacute;gitos", 
		  					 required : "El campo Longitud es obligatorio si se informa el campo Latitud"},
		  	  "rega":  {required : "El campo REGA es obligatorio si no est&aacute; informado el campo Sigla"},
		  	  "sigla":  {required : "El campo Sigla es obligatorio si no est&aacute; informado el campo REGA"},
		  	  "subexplotacion":  {digits : "El campo Subexplotaci&oacute;n s&oacute;lo puede contener d&iacute;gitos"},
		  	  "especie":  {required : "El campo Especie es obligatorio", 
		  		  		   digits: "El campo Especie s&oacute;lo puede contener d&iacute;gitos",
		  		  		   isValidEspecie: "El c�digo de especie no est� asociado a la clase seleccionada."},
		  	  "regimen":  {required : "El campo R&eacute;gimen es obligatorio", 
			  		  	   digits: "El campo R&eacute;gimen s&oacute;lo puede contener d&iacute;gitos",
			  		  	   isValidRegimen:"El c�digo de r�gimen manejo no est� asociado a la clase seleccionada."},
  		  	  "grupoRazas[0].codgruporaza":  {required : "El campo Grupo de raza es obligatorio", 
	  		  	   		   						 digits: "El campo Grupo de raza s&oacute;lo puede contener d&iacute;gitos",
	  		  	   		   						 isValidGrupoRaza: "El c�digo de grupo de raza no est� asociado a la clase seleccionada."},
	  		  "grupoRazas[0].codtipocapital":  {required : "El campo Tipo de Capital es obligatorio", 
 		  	   		   						 	   digits: "El campo Tipo de Capital s&oacute;lo puede contener d&iacute;gitos",
 		  	   		   						isValidTipoCapital: "El c�digo de tipo de capital no est� asociado a la clase seleccionada."},	  	
			  "grupoRazas[0].codtipoanimal":   {required : "El campo Tipo de Animal es obligatorio", 
				 	   							   digits: "El campo Tipo de Animal s&oacute;lo puede contener d&iacute;gitos",
				 	   							isValidTipoAnimal: "El c�digo de tipo de animal no est� asociado a la clase seleccionada."},
			  "grupoRazas[0].numanimales":   {required : "El campo N&uacute;mero es obligatorio", digits: "El campo N&uacute;mero s&oacute;lo puede contener d&iacute;gitos"},
			  "grupoRazas[0].precioAnimalesModulos[0].precio":   {required : "El campo Precio es obligatorio", range: "El campo Precio debe estar entre 0 y 9999999.9999"}	
		  }
		});
	
	
	
	jQuery.validator.addMethod("isValidNumAnimales", function(value, element, params) { 		
		if(!isAccionIrPanelGruposRaza){
			return isValidNumAnimales();
		}else{
			return true;
		}					
	});
	
	jQuery.validator.addMethod("isValidProvincia", function(value, element, params) { 		
		return isValidProvincia();			
	});
	
	jQuery.validator.addMethod("isValidComarca", function(value, element, params) { 		
		return isValidComarca();			
	});
	
	jQuery.validator.addMethod("isValidTermino", function(value, element, params) { 		
		return isValidTermino();			
	});
	
	jQuery.validator.addMethod("isValidEspecie", function(value, element, params) { 		
		return isValidEspecie();			
	});	
	
	jQuery.validator.addMethod("isValidRegimen", function(value, element, params) { 		
		return isValidRegimen();			
	});	
	
	jQuery.validator.addMethod("isValidGrupoRaza", function(value, element, params) { 	
		if(!isAccionIrPanelGruposRaza){
			return isValidGrupoRaza();
		}else{
			return true;
		}			
	});	
	
	jQuery.validator.addMethod("isValidTipoAnimal", function(value, element, params) { 		
		if(!isAccionIrPanelGruposRaza){
			return isValidTipoAnimal();		
		}else{
			return true;
		}
	});	
	
	jQuery.validator.addMethod("isValidTipoCapital", function(value, element, params) { 
		if(!isAccionIrPanelGruposRaza){
			return isValidTipoCapital();
		}else{
			return true;
		}
					
	});	
	
		comprobarSiBorrarFormularioGrupoRaza();
		comprobarRegistroUnico();
		desplazar();
		esAlta();
		vueltaGuardarYReplicarGR();
		comprobarChecksCoberturas("datosExplotaciones");
	});	
		



/**
 * Si los datos del formulario son v�lidos, da de alta la explotaci�n y regresa a la pantalla con los mismos datos precargados
 */
function guardarReplicar() {
	limpiarAlertas();
	if (grabaChekCob(document.datosExplotaciones)){
		var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotaciones);
		//alert("guardar-cobexistentes: "+cobExistentes);
		$('#coberturas').val(cobExistentes);
		
		if (checkGrValues()) {
			$('#datosExplotaciones').validate().cancelSubmit = true;
			$('#accion').val('saveDatosIdentificativos');
			$('#methodDE').val('doGuardarReplicar');
			$('#datosExplotaciones').submit();
		}
		else{
			$("#datosExplotaciones").validate();
			if ($( "#datosExplotaciones" ).valid()) {
				$('#methodDE').val('doGuardarReplicar');
				$('#datosExplotaciones').submit();
			}
		}
	}
}

/**
 * Si los datos del formulario son v�lidos, da de alta la explotaci�n y regresa a la pantalla con los datos en blanco
 */
function guardarNuevo () {
	limpiarAlertas();
	if (grabaChekCob(document.datosExplotaciones)){
		//alert(" COBERTURAS guardarNuevo OK");
		var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotaciones);
		//alert("guardar-cobexistentes: "+cobExistentes);
		$('#coberturas').val(cobExistentes);
		
		if (checkGrValues()) {
			$('#datosExplotaciones').validate().cancelSubmit = true;
			$('#accion').val('saveDatosIdentificativos');
			$('#methodDE').val('doGuardarNuevo');
			$('#datosExplotaciones').submit();
		}
		else{
			$("#datosExplotaciones").validate();
			if ($( "#datosExplotaciones" ).valid()) {
				$('#methodDE').val('doGuardarNuevo');
				$('#datosExplotaciones').submit();
			}
		}
	}
}

/**
 * Si los datos del formulario son v�lidos, da de alta la explotaci�n y regresa a la pantalla de listado de explotaciones
 */
function guardarSalir () {
	limpiarAlertas();
	if (grabaChekCob(document.datosExplotaciones)){
		//alert(" COBERTURAS guardarSalir OK");
		var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotaciones);
		//alert("guardar-cobexistentes: "+cobExistentes);
		$('#coberturas').val(cobExistentes);
		
		if (checkGrValues()) {
			$('#accionRestaurar').val($('#accion').val());
			$('#accion').val('saveDatosIdentificativos');
			$('#methodDE').val('doGuardarVolver');
			$('#datosExplotaciones').submit();
			$('#datosExplotaciones').validate().cancelSubmit = false;
			$('#accion').val($('#accionRestaurar').val());
		}
		else{
			$("#datosExplotaciones").validate();
			if ($("#datosExplotaciones").valid()) {
				$('#methodDE').val('doGuardarVolver');
				$('#datosExplotaciones').submit();
			}
		}
	}
}

function checkGrValues () {
	if ($("#codgrupoRaza").val() == "" && $("#codtipocapital").val() == ""
			&& $("#codtipoanimal").val() == "" && $("#numanimales").val() == ""
			&& $("#precio").val() == "" && $('#gr tr').length > 1 && $("#accion").val() != "editar"){
		
		var settings = $('#datosExplotaciones').validate().settings;
		
		settings.rules['grupoRazas[0].codgruporaza'] = {required: false, digits: true}, // No Obligatorio
		settings.rules['grupoRazas[0].codtipocapital'] = {required: false, digits: true}, // No Obligatorio
		settings.rules['grupoRazas[0].codtipoanimal'] = {required: false, digits: true}, // No Obligatorio
		settings.rules['grupoRazas[0].numanimales'] = {required: false, digits: true},
		settings.rules['grupoRazas[0].precioAnimalesModulos[0].precio'] =  {required: false};
		return true;
	}
	else{
		return false;
	}
}

/**
 * Si los datos del formulario son v�lidos, da de alta la explotaci�n
 */
function guardarGrupoRaza(botonOrigen) {
	limpiarAlertas();
//	alert($("#numanimales").val());
	if (grabaChekCob(document.datosExplotaciones)){
		var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotaciones);
		//alert("guardar-cobexistentes: "+cobExistentes);
		$('#coberturas').val(cobExistentes);
		//alert($('#isCoberturas').val());
		$("#datosExplotaciones").validate();
		if ($( "#datosExplotaciones" ).valid()) {
			$('#botonGuardarGR').val(botonOrigen);
			$('#methodDE').val('doGuardarGrupoRaza');
			$('#datosExplotaciones').submit();
			$('#accion').val('');
		}
	}
	
}

/**
 * Comprueba si los datos del panel 'Datos Identificativos' son correctos. 
 * En caso afirmativo habilita el panel 'Grupos de raza' y deshabilita el panel 'Datos identificativos'
 */
function irPanelGrupoRaza () {
	isAccionIrPanelGruposRaza = true;
	$("#datosExplotaciones").validate();
	if ($( "#datosExplotaciones" ).valid()) {
		$('#divGrupoRaza').unblock();
		$('#datosIdentificativos').block({message:null});
	}

	isAccionIrPanelGruposRaza = false;
}

/**
 * Pide confirmaci�n al usuario para deshabilitar el panel 'Grupos de raza' borrando todos los datos y habilitar el panel 'Datos Identificativos'
 */
function irPanelDatosIdentificativos () { 
	if($('#modoLectura').val()!='modoLectura'){	
		if (confirm ("Se van a borrar todos los datos del grupo de raza. �Desea continuar?")) {
		    $('#divGrupoRaza :input:not([id="codModuloPrecio"])').val("");
			$('#divGrupoRaza').block({message:null});
			$('#datosIdentificativos').unblock();
		}
	}else{
		$('#divGrupoRaza').block({message:null});
		$('#datosIdentificativos').unblock();
	}	
}

/**
 * Calcula el precio de la explotaci�n actual
 */

function calcularPrecio () {
	limpiarAlertas();

	// Indica que no hay que validar el campo 'Precio' para el c�lculo y lo limpia
	isAccionCalcular = true;
	$('#precio').val('');
	// Validar que el formulario es correcto antes de obtener el precio (menos el propio campo Precio)
	if ($( "#datosExplotaciones" ).valid()) {
		muestraCapaEspera ("Calculando");
		// Solicitud ajax para el c�lculo de precio
		$.ajax({
			url:          "datosExplotaciones.html",
			data:         "method=doCalcularPrecio" + crearCadenaParametros(),
			async:        true,
			contentType:  "application/x-www-form-urlencoded",
			dataType:     "text",
			global:       false,
			ifModified:   false,
			processData:  true,
			cache:		  false,
			error: function(objeto, quepaso, otroobj){
				$.unblockUI();	
				alert("Error obtener el precio de la explotaci�n: " + quepaso);
			},
			success: function(datos){
				$.unblockUI();
				alertaPreciosDiferentes(datos);
				// Recoge el array de precios obtenidos y carga datos en el popup de precios por m�dulo				
				tienePrecio = cargarPopUpPreciosPorModulo(eval(datos));
				// Si no se ha encontrado precio se muestra un mensaje indic�ndolo
				if (!tienePrecio) {
					alert ('No se ha encontrado precio para los datos indicados')
				}
				comprobarChecksCoberturas("datosExplotaciones");
			},
			type: "POST"
		});
	}
	
	// Vuelve a establecer el campo 'Precio' como obligatorio
	isAccionCalcular = false;	
}

/**
 * Crea la cadena de par�metros y valores necesarios en la llamada para el c�lculo de precio de la explotaci�n
 * @returns {String}
 */
function crearCadenaParametros () {
	//alert("isCoberturas:"+ $('#isCoberturas').val());
	var cadena = "&idpoliza=" + $('#idPoliza').val() + "&codProvincia=" + $('#provincia').val() + "&codComarca=" + $('#comarca').val();
	    cadena+= "&codTermino=" + $('#termino').val() + "&subtermino=" + $('#subtermino').val() + "&especie=" + $('#especie').val();
	    cadena+= "&latitud=" + $('#latitud').val() + "&longitud=" + $('#longitud').val() + "&rega=" + $('#rega').val();
	    cadena+= "&sigla=" + $('#sigla').val() + "&subexplotacion=" + $('#subexplotacion').val();
	    cadena+= "&regimen=" + $('#regimen').val() + "&grupoRaza=" + $('#codgrupoRaza').val() + "&tipoCapital=" + $('#codtipocapital').val();
	    cadena+= "&tipoAnimal=" + $('#codtipoanimal').val()+ "&numanimales=" + $('#numanimales').val()+ "&isCoberturas=" + $('#isCoberturas').val()+ "&idExplotacion=" + $('#idExplotacion').val();	
	// coberturas existentes
	    //alert("crearCadenaParametros");
	var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotaciones,true);   
    cadena+= "&cobExistentes=" + cobExistentes;
	    
	// Datos variables
	$("input[name^='dvCpto_']").each(function () {
		if ($(this).val() != '') {
			cadena+= "&" + $(this).attr('name') + "=" + $(this).val();
		}
    });
	    
	return cadena;
}

/**
 * Redirecci�n al listado de explotaciones
 */
function volver () {
	$('#listadoExplotaciones').submit();
}


/**
 * Elimina el grupo de raza de explotaci�n seleccionado
 */
function eliminarGrupoRazaExplotacion(id, codgruporaza, nomgruporaza, codtipocapital, nomtipocapital, codtipoanimal, nomtipoanimal, numanimales, idinput) {
	limpiarAlertas();	
		$('#codgrupoRaza').val(codgruporaza);
		$('#desGrupoRaza').val(nomgruporaza);
		$('#codtipocapital').val(codtipocapital);
		$('#desTipoCapital').val(nomtipocapital);
		$('#codtipoanimal').val(codtipoanimal);
		$('#desTipoAnimal').val(nomtipoanimal);
		var idinput = '#' + idinput;
		var precio = $(idinput).val();
		$('#precio').val(precio);
		$('#numanimales').val(numanimales);
		jConfirm('�Est� seguro de que desea eliminar el registro seleccionado?','Di�logo de Confirmaci�n', function(r) {
			if (r==true){
				$('#codgrupoRaza').val(codgruporaza);
				$('#desGrupoRaza').val(nomgruporaza);
				$('#codtipocapital').val(codtipocapital);
				$('#desTipoCapital').val(nomtipocapital);
				$('#codtipoanimal').val(codtipoanimal);
				$('#desTipoAnimal').val(nomtipoanimal);
				var idinput = '#' + idinput;
				var precio = $(idinput).val();
				$('#precio').val(precio);
				$('#numanimales').val(numanimales);
				$('#accion').val('borrar');
				$('#gruporazaid').val(id);
				guardarGrupoRaza();
			}
		});
}

function limpiarPrecio(){
	$('#precio').val("");
}

function deshabiltaTextBox(formulario){
	CamposImput = formulario.getElementsByTagName("input"); 
	for(var i=0; i< CamposImput.length; i++) { 
		 // Si el tipo de campo es una caja de texto
		 if(CamposImput[i].type == "text" || CamposImput[i].type == "checkbox") { 
			 CamposImput[i].disabled=true;		 
		 }
	}
}


/**
 * Muestra la capa de espera con el mensaje recibido como par�metro
 * @param msg
 * @returns
 */
function muestraCapaEspera (msg) {
	$.blockUI.defaults.message = '<h4> ' + msg + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}

function actualizaCptoFactores (cpto) {
	$('#cod_cpto_lupa_factores').val(cpto);
	$('#valor_lupa_factores').val($('#cod_cpto_' + cpto).val());
	$('#desc_lupa_factores').val('');
}

function copiaMarcadoFactoresValor () { 

	if ($('#valor_lupa_factores').val().length>0) {
		var cpto = $('#cod_cpto_lupa_factores').val();
		var valor = $('#valor_lupa_factores').val();
		$('#cod_cpto_' + cpto).val(valor);
	}
}

function copiaMarcadoFactoresDesc () { 

	if ($('#desc_lupa_factores').val().length>0) {
		var cpto = $('#cod_cpto_lupa_factores').val();
		var desc = $('#desc_lupa_factores').val();
		$('#des_cpto_' + cpto).val(desc);
	}
}
