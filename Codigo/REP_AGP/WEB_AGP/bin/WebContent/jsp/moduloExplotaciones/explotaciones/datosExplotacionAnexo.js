var isAccionCalcular = false;
var isAccionIrPanelGruposRaza = false;

/**
 * Validaciones del formulario
 */
$(document).ready(function() {
	limpiarAlertas();
	//Se capturan los eventos de teclado
	var contador = 0;
	
	if(datosExplotacionesAnexo.codtipocapital.value!=null && datosExplotacionesAnexo.codtipocapital.value!=''){
		comprobarInputNumAnimales(datosExplotacionesAnexo.codtipocapital.value, 'datosExplotacionesAnexo');
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
	if ($('#tieneCoberturas').val() != 'true'){		
		$('#contenedorCoberturas').hide();
	}
	
	// Deshabilitar el panel de grupos de raza
	$('#divGrupoRaza').block({message:null});
	
	$("#datosExplotacionesAnexo").validate({
		  errorLabelContainer : "#panelAlertasValidacion",
		  wrapper : "li", highlight : function(element,errorClass) {
				$("#campoObligatorio_" + element.id).show();
			},
		  unhighlight : function(element,errorClass) {
				$("#campoObligatorio_" + element.id).hide();
			},
		  rules: {
			  "termino.id.codprovincia": {required: true, digits: true, isValidProvincia: true}, // Obligatorio y numerico
			  "termino.id.codcomarca": {required: true, digits: true, isValidComarca:true}, // Obligatorio y numerico
			  "termino.id.codtermino": {required: true, digits: true, isValidTermino:true}, // Obligatorio y numerico
			  "termino.id.subtermino": {required: function (element){return (!$('#subtermino').val().length>0)}}, // Obligatorio
			  // Obligatorio si esta informado el campo 'Latitud' y numerico
			  "latitud":  {digits: true, required: function (element){return ($('#longitud').val().length>0)}},
			  // Obligatorio si esta informad el campo 'Longitud' y numerico
			  "longitud": {digits: true, required: function (element){return ($('#latitud').val().length>0)}},
			  // Obligatorio si no esta informado el campo 'Sigla'
			  "rega": {required: function (element){return ($('#sigla').val().length==0)}},
			  // Obligatorio si no esta informado el campo 'REGA'
			  "sigla": {required: function (element){return ($('#rega').val().length==0)}},
			  "subexplotacion": {digits: true}, // Numerico
			  "especie": {required: true, digits: true, isValidEspecie: true}, // Obligatorio y numerico
			  "regimen": {required: true, digits: true, isValidRegimen: true}, // Obligatorio y numerico
			  
			  // A partir de aqui solo se valida si el panel 'Grupos de raza' esta habilitado
			  "grupoRazaAnexos[0].codgruporaza": {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true, isValidGrupoRaza: true}, // Obligatorio y numerico
			  "grupoRazaAnexos[0].codtipocapital": {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true, isValidTipoCapital: true}, // Obligatorio y numerico
			  "grupoRazaAnexos[0].codtipoanimal": {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true, isValidTipoAnimal: true}, // Obligatorio y numerico
			  "grupoRazaAnexos[0].numanimales": {required: function (element){return !isAccionIrPanelGruposRaza},digits: true}, // Obligatorio y numerico
			  
			  // El precio solo se valida si el panel esta habilitado y la accion no es 'Calcular'
			  "grupoRazaAnexos[0].precioAnimalesModuloAnexos[0].precio": {required: validarPrecio, range: [0, 9999999.9999]} // Obligatorio, numerico de 7 digitos en la parte entera y 4 en la decimal
		  },
		  messages: {
			  "termino.id.codprovincia":  {required : "El campo Provincia es obligatorio", 
				  						   digits: "El campo Provincia s&oacute;lo puede contener d&iacute;gitos"},
			  "termino.id.codcomarca":  {required : "El campo Comarca es obligatorio", 
					  					   digits: "El campo Comarca s&oacute;lo puede contener d&iacute;gitos"},
			  "termino.id.codtermino":  {required : "El campo T&eacute;rmino es obligatorio", 
						  				   digits: "El campo T&eacute;rmino s&oacute;lo puede contener d&iacute;gitos"},
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
						   isValidEspecie: "El c\u00F3digo de especie no est\u00E1 asociado a la clase seleccionada."},
		  	  "regimen":  {required : "El campo R&eacute;gimen es obligatorio", 
			  		  	   digits: "El campo R&eacute;gimen s&oacute;lo puede contener d&iacute;gitos",
			  		  	   isValidRegimen:"El c\u00F3digo de r\u00E9gimen manejo no est\u00E1 asociado a la clase seleccionada."},
  		  	  "grupoRazaAnexos[0].codgruporaza":  {required : "El campo Grupo de raza es obligatorio", 
	  		  	   		   						 digits: "El campo Grupo de raza s&oacute;lo puede contener d&iacute;gitos",
	  		  	   		   						 isValidGrupoRaza: "El c\u00F3digo de grupo de raza no est\u00E1 asociado a la clase seleccionada."},
	  		  "grupoRazaAnexos[0].codtipocapital":  {required : "El campo Tipo de Capital es obligatorio", 
 		  	   		   						 	   digits: "El campo Tipo de Capital s&oacute;lo puede contener d&iacute;gitos",
 		  	   		   						isValidTipoCapital: "El c\u00F3digo de tipo de capital no est\u00E1 asociado a la clase seleccionada."},
			  "grupoRazaAnexos[0].codtipoanimal":   {required : "El campo Tipo de Animal es obligatorio", 
				 	   							   digits: "El campo Tipo de Animal s&oacute;lo puede contener d&iacute;gitos",
				 	   							isValidTipoAnimal: "El c\u00F3digo de tipo de animal no est\u00E1 asociado a la clase seleccionada."},
			  "grupoRazaAnexos[0].numanimales":   {required : "El campo N&uacute;mero es obligatorio", digits: "El campo N&uacute;N&uacute; s&oacute;lo puede contener d&iacute;gitos"},
			  "grupoRazaAnexos[0].precioAnimalesModuloAnexos[0].precio":   {required : "El campo Precio es obligatorio", range: "El campo Precio debe estar entre 0 y 9999999.9999"}	
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
	comprobarSiForzarEdicionGrupoRaza();		
	ajustarAntesDeGuardar();	
	vueltaGuardarYReplicarGR();
	comprobarChecksCoberturas("datosExplotacionesAnexo");
});	

		
/**
 * Si los datos del formulario son validos, da de alta la explotacion y regresa a la pantalla con los mismos datos precargados
 */
function guardarReplicar(){
	limpiarAlertas();
	if (grabaChekCob(document.datosExplotacionesAnexo)){
		//alert(" COBERTURAS guardarSalir OK");
		var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotacionesAnexo);
		//alert("guardar-cobexistentes: "+cobExistentes);
		$('#coberturas').val(cobExistentes);
		
		
		var accion = $('#accion').val();
		if(anularValidacionGrupoRaza()){
			desactivarValidacionesGrupoRaza();
			$('#accion').val("guardarDatosIdentificativos");
		}
		
		$("#datosExplotacionesAnexo").validate();
		if ($("#datosExplotacionesAnexo").valid()) {
			$('#methodDE').val('doGuardarReplicar');
			$('#datosExplotacionesAnexo').submit();
		}
		activarValidacionesGrupoRaza();
		$('#accion').val(accion);
	}
}

/**
 * Si los datos del formulario son validos, da de alta la explotacion y regresa a la pantalla con los datos en blanco
 */
function guardarNuevo(){
	limpiarAlertas();
	if (grabaChekCob(document.datosExplotacionesAnexo)){
		var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotacionesAnexo);
		$('#coberturas').val(cobExistentes);
		
		var accion = $('#accion').val();
		if(anularValidacionGrupoRaza()){
			$('#accion').val("guardarDatosIdentificativos");
		}
			
		$("#datosExplotacionesAnexo").validate();
		if ($("#datosExplotacionesAnexo").valid()) {
			$('#methodDE').val('doGuardarNuevo');
			$('#datosExplotacionesAnexo').submit();
		}
		$('#accion').val(accion);
	}
}

/**
 * Si los datos del formulario son validos, da de alta la explotacion y regresa a la pantalla de listado de explotaciones
 */
function guardarSalir(){
	limpiarAlertas();
	if (grabaChekCob(document.datosExplotacionesAnexo)){
		var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotacionesAnexo);
		$('#coberturas').val(cobExistentes);
		
		var accion = $('#accion').val();
		if(anularValidacionGrupoRaza()){
			desactivarValidacionesGrupoRaza();
			$('#accion').val("guardarDatosIdentificativos");
		}
	
		$("#datosExplotacionesAnexo").validate();
		if ($("#datosExplotacionesAnexo").valid()) {
			$('#methodDE').val('doGuardarVolver');
			$('#datosExplotacionesAnexo').submit();
		}
		activarValidacionesGrupoRaza();
		$('#accion').val(accion);
	}
}
	
//Si el formulario de grupo raza esta vacio y la explotacion tiene al menos un grupo de raza, devuelve false para saltarnos las validaciones
function anularValidacionGrupoRaza(){
	var validar = false;
	var primerGrupoRaza = $("#iconoEditar_1").val();
	
	if(typeof(primerGrupoRaza)==='undefined'){
		//Significa que no hay ningun grupo raza -> seguimos obligando a que complete el grupo de raza
	}else{
		if($("#codgrupoRaza").val()=='' && $("#codtipocapital").val()=='' && $("#codtipoanimal").val()=='' &&
				$("#numanimales").val()=='' && $("#precio").val()==''){
			validar = true;
		}
	}
	return validar;
}

/**
 * Si los datos del formulario son validos, da de alta la explotacion
 */
function guardarGrupoRaza(botonOrigen){
	limpiarAlertas();
	if (grabaChekCob(document.datosExplotacionesAnexo)){
		var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotacionesAnexo);
		$('#coberturas').val(cobExistentes);
		
		$("#datosExplotacionesAnexo").validate();
		if ($("#datosExplotacionesAnexo").valid()) {
			$('#botonGuardarGR').val(botonOrigen);
			$('#methodDE').val('doGuardarGrupoRazaAnexo');
			$('#datosExplotacionesAnexo').submit();
			$('#accion').val('');
		}
	}
}

/**
 * Comprueba si los datos del panel 'Datos Identificativos' son correctos. 
 * En caso afirmativo habilita el panel 'Grupos de raza' y deshabilita el panel 'Datos identificativos'
 */
function irPanelGrupoRaza(){
	isAccionIrPanelGruposRaza = true;
	$("#datosExplotacionesAnexo").validate();
	if ($("#datosExplotacionesAnexo").valid()) {
		$('#divGrupoRaza').unblock();
		$('#datosIdentificativos').block({message:null});
		$('#codgrupoRaza').focus();
	}
	isAccionIrPanelGruposRaza = false;
}

/**
 * Pide confirmacion al usuario para deshabilitar el panel 'Grupos de raza' borrando todos los datos y habilitar el panel 'Datos Identificativos'
 */
function irPanelDatosIdentificativos(){ 
	var soloLectura = $('#listadoExplotacionesAnexo #modoLectura').val();
	 if(typeof(soloLectura)==='undefined'){
		if (confirm ("Se van a borrar todos los datos del grupo de raza. \u00BFDesea continuar?")) {
			$('#divGrupoRaza :input:not([id="codModuloPrecio"])').val("");
			$('#divGrupoRaza').block({message:null});
			$('#datosIdentificativos').unblock();
			$('#accion').val('');
			$('#provincia').focus();
		}
	 }else{
			$('#divGrupoRaza').block({message:null});
			$('#datosIdentificativos').unblock();
	 }
}

/**
 * Calcula el precio de la explotacion actual
 */

function calcularPrecio(){
	limpiarAlertas();
	
	// Indica que no hay que validar el campo 'Precio' para el calculo y lo limpia
	isAccionCalcular = true;
	$('#precio').val('');	
	// Validar que el formulario es correcto antes de obtener el precio (menos el propio campo Precio)
	if ($("#datosExplotacionesAnexo").valid()) {
		muestraCapaEspera ("Calculando");
		// Solicitud ajax para el calculo de precio
		$.ajax({
			url:          "datosExplotacionesAnexo.html",
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
				alert("Error obtener el precio de la explotacion: " + quepaso);
			},
			success: function(datos){
				$.unblockUI();
				alertaPreciosDiferentes(datos);
				// Recoge el array de precios obtenidos y carga datos en el popup de precios por modulo				
				tienePrecio = cargarPopUpPreciosPorModulo(eval(datos));
				// Si no se ha encontrado precio se muestra un mensaje indicandolo
				if (!tienePrecio) {
					alert ('No se ha encontrado precio para los datos indicados')
				}
				if(datos.errorPrecio){
					pintarPopupErrores(datos);
				}
			},
			type: "POST"
		});
	}
	
	// Vuelve a establecer el campo 'Precio' como obligatorio
	isAccionCalcular = false;
}

/**
 * Crea la cadena de parametros y valores necesarios en la llamada para el calculo de precio de la explotacion
 * @returns {String}
 */
function crearCadenaParametros() {
	var frm = document.getElementById('datosExplotacionesAnexo');
	var cadena = "&idPoliza=" + $('#idPoliza').val() + "&codProvincia=" + $('#provincia').val() + "&codComarca=" + $('#comarca').val();
	    cadena+= "&codTermino=" + $('#termino').val() + "&subtermino=" + $('#subtermino').val() + "&especie=" + $('#especie').val();
	    /* ESC-17260 **/
		 // coberturas existentes
		var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotacionesAnexo,true);
		cadena+= "&cobExistentes=" + cobExistentes;
		// Datos variables
		$("input[name^='dvCpto_']").each(function() {
			if ($(this).val() != '') {
				cadena+= "&" + $(this).attr('name') + "=" + $(this).val();
			}
	    });
		/* ESC-17260 **/
	    cadena+= "&regimen=" + $('#regimen').val() + "&grupoRaza=" + $('#codgrupoRaza').val() + "&tipoCapital=" + $('#codtipocapital').val();
	    cadena+= "&tipoAnimal=" + $('#codtipoanimal').val()+"&codModulo=" + $('#codmodulo').val()+ "&numanimales=" + $('#numanimales').val();
	    cadena+= "&latitud=" + $('#latitud').val() + "&longitud=" + $('#longitud').val() + "&rega=" + $('#rega').val();
	    cadena+= "&sigla=" + $('#sigla').val() + "&subexplotacion=" + $('#subexplotacion').val();
	    cadena+= "&isCoberturas=" + $('#isCoberturas').val()+ "&idExplotacion=" + $('#idExplotacion').val()+ "&idPoliza=" + $('#idPoliza').val()+ "&anexoModificacionId=" + frm.anexoModificacionId.value; // anexoModificacionId
	    cadena+= "&numeroExp=" + frm.numero.value;
	    

	return cadena;
}

/**
 * Redireccion al listado de explotaciones
 */
function volver() {
	var formDatos = document.forms['datosExplotacionesAnexo'];
	var formListado = document.forms['listadoExplotacionesAnexo'];

	formListado['anexoModificacionId'].value = formDatos['anexoModificacionId'].value;
	formListado.submit();
}


/**
 * Elimina el grupo de raza de explotacion seleccionado
 */
function eliminarGrupoRazaExplotacion(id, codgruporaza, nomgruporaza, codtipocapital, nomtipocapital, codtipoanimal, nomtipoanimal, numanimales, idinput){
	limpiarAlertas();
	if (grabaChekCob(document.datosExplotacionesAnexo)){
		var cobExistentes = montarCoberturasExplotacionNew(document.datosExplotacionesAnexo);
		$('#coberturas').val(cobExistentes);
		jConfirm('\u00BFEst\u00E1 seguro de que desea eliminar el registro seleccionado?','Di\u00E1logo de Confirmaci\u00F3n', function(r) {
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
				$('#methodDE').val('doGuardarGrupoRazaAnexo');
				$('#datosExplotacionesAnexo').validate().cancelSubmit = true;
				$('#datosExplotacionesAnexo').submit();
				$('#accion').val('');
			}
		});
	}
}



//Operaciones de ajuste de campos necesarios antes del guardado para eliminar el id y establecer el tipo de modificacion
function ajustarAntesDeGuardar(){
	rellenarTipoModificacion();
}

function rellenarTipoModificacion(){
	
	if($("#datosExplotacionesAnexo #idExplotacion").val()==''){
		//Si esta vacio es que es un alta
		$("#datosExplotacionesAnexo #tipoModificacion").val('A');
	}else{
		//Si el tipoModificacion esta vacio, debemos marcarlo como modificacion. Si no, se mantiene
		if($("#datosExplotacionesAnexo #tipoModificacion").val()==''){
			$("#datosExplotacionesAnexo #tipoModificacion").val('M');
		}
	}
}

function activarValidacionesGrupoRaza(){
	
	var settings = $('#datosExplotacionesAnexo').validate().settings;
	
	settings.rules['grupoRazaAnexos[0].codgruporaza'] = {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true, isValidGrupoRaza: true}; // Obligatorio y numerico
	settings.rules['grupoRazaAnexos[0].codtipocapital'] = {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true, isValidTipoCapital: true}; // Obligatorio y numerico
	settings.rules['grupoRazaAnexos[0].codtipoanimal'] = {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true, isValidTipoAnimal: true}; // Obligatorio y numerico
	settings.rules['grupoRazaAnexos[0].numanimales'] = {required: function (element){return !isAccionIrPanelGruposRaza}, digits: true}; // Obligatorio y numerico
	settings.rules['grupoRazaAnexos[0].precioAnimalesModuloAnexos[0].precio'] = {required: validarPrecio, range: [0, 9999999.9999]}; // O
}

function desactivarValidacionesGrupoRaza(){
	
	var settings = $('#datosExplotacionesAnexo').validate().settings;
	
	settings.rules['grupoRazaAnexos[0].codgruporaza'] = {required: false};
	settings.rules['grupoRazaAnexos[0].codtipocapital'] = {required: false};
	settings.rules['grupoRazaAnexos[0].codtipoanimal'] = {required: false};
	settings.rules['grupoRazaAnexos[0].numanimales'] = {required: false};
	settings.rules['grupoRazaAnexos[0].precioAnimalesModuloAnexos[0].precio'] = {required: false};
}

/**
 * Muestra la capa de espera con el mensaje recibido como parametro
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