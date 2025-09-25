// Para evitar el cacheo de peticiones al servidor
$(document).ready(function(){
	
	$("input[type=file]").filestyle({
		image: "jsp/img/boton_examinar.png",
		imageheight : 22,
		imagewidth : 82,
		width : 250
	});
	
	$('#desc_linea').val($('#nomLinea').val());
	$('#desc_entidad').val($('#nomEntidad').val());
    var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
    
    	        
	$('#main3').validate({			
	
			onfocusout: function(element) {
				var frm = document.getElementById('main3');   
				if ( (frm.method.value == "doEditar") || (frm.method.value == "doAlta") ) {
					this.element(element);
				}
			},		
		  
			errorLabelContainer: "#panelAlertasValidacion",
			wrapper: "li",
			
			rules: {
				"linea.codplan":{required: true, digits: true, minlength: 4},
		 		"linea.codlinea":{required: true, digits: true},
		 		"provincia.codprovincia":{required: true, digits: true},
		 		"comarca.id.codcomarca":{required: true, digits: true},
		 		"cultivo.id.codcultivo":{required: true, digits: true},
		 		"tasaIncendio":{required: true, range: [0,999999.99]},
		 		"tasaPedrisco":{required: true, range: [0,999999.99]}
		 		
			},
			messages: {
				"linea.codplan":{required: "El campo Plan es obligatorio", digits: "El campo Plan s\u00F3lo puede contener d\u00EDgitos", minlength: "El campo Plan debe contener 4 d\u00EDgitos"},
		 		"linea.codlinea":{required: "El campo L\u00EDnea es obligatorio", digits: "El campo L\u00EDnea s\u00F3lo puede contener d\u00EDgitos"},
		 		"provincia.codprovincia":{required: "El campo Provincia es obligatorio", digits: "El campo Provincia s\u00F3lo puede contener d\u00EDgitos"},
		 		"comarca.id.codcomarca":{required: "El campo Comarca es obligatorio", digits: "El campo Comarca s\u00F3lo puede contener d\u00EDgitos"},
		 		"cultivo.id.codcultivo":{required: "El campo Cultivo es obligatorio", digits: "El campo Cultivo s\u00F3lo puede contener d\u00EDgitos"},
		 		"tasaIncendio":{required: "El campo Tasa de incendio es obligatorio", range: "El campo Tasa de incendio debe contener un n\u00FAmero entre 0 y 999999,99"},
		 		"tasaPedrisco":{required: "El campo Tasa de pedrisco es obligatorio", range: "El campo Tasa de pedrisco debe contener un n\u00FAmero entre 0 y 999999,99"}
			}
		});			
		
	$('#frmImportar').validate({
			errorLabelContainer: "#panelAlertasValidacionFile",
			wrapper: "li",	  				 
			rules: {
				"file":{valFicheroTasas:true}
			},
			messages: {
				"file":{valFicheroTasas:"Debe seleccionar un archivo con una extensi\u00F3n v\u00E1lida"}
			}  
	});
	
	jQuery.validator.addMethod("valFicheroTasas", function(value, element, params) {
		var name = $('#file').val();
		if (name == '') {
			return false;
		} else {
			name = name.split('\\');
			var aux1 =(name[name.length-1]).substring((name[name.length-1].length - 3),(name[name.length-1].length));
			if ((aux1 == 'CSV')||(aux1 == 'csv')){
				return true;
			} else {
				return false;
			}
		}
	});
});

function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main3');
    $.get('tasasSbp.run?ajax=true&' + parameterString, function(data) {
        $("#grid").html(data)
	});
}		

function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'consultaPolizaSbp.run?ajax=false&' + parameterString;
}						

// Carga los datos de la tasa seleccionada en el formulario
function editar(id, plan, linea, provincia, tasaIncendio, tasaPedrisco, lineaseguroid, 
				nomlinea, desc_provincia, comarca, desc_comarca, cultivo, desc_cultivo ){
	limpiaAlertas();
	
	$("#btnModificar").show();
	
	$('#id').val(id);
	$('#plan').val(plan);	
	$('#linea').val(linea);			
	$('#provincia').val(provincia);
	$('#tasaIncendio').val(tasaIncendio);
	$('#tasaPedrisco').val(tasaPedrisco);
	$('#lineaseguroid').val(lineaseguroid);			
	$('#desc_linea').val(nomlinea);
	$('#desc_provincia').val(desc_provincia);
	$('#comarca').val(comarca);
	$('#desc_comarca').val(desc_comarca);
	$('#cultivo').val(cultivo);
	$('#desc_cultivo').val(desc_cultivo);
}

// Realiza la llamada al metodo de edicion de la tasa
function modificar(){
	limpiaAlertas();
	var frm = document.getElementById('main3');
	frm.method.value = "doEditar";
	$('#main3').submit();						
}

// Realiza la validacion de los datos introducidos en el formulario y si es correcta da de alta la tasa
function alta () {
	limpiaAlertas(); 
	var frm = document.getElementById('main3');
	frm.method.value = "doAlta";
	$('#main3').submit();
}

// Borra la tasa con el id indicado por parometro
function borrar(id) {
	var frm = document.getElementById('main3');
	$('#main3').validate().cancelSubmit = true;
	limpiaAlertas();
	if(confirm('\u00BFEst\u00E1 seguro de que desea eliminar la tasa de Sobreprecio seleccionada?')){
		
		$('#id').val(id);
		frm.method.value = "doBorrar";
		$('#main3').attr('target', '');
		$('#main3').submit();
	}	
}				

function limpiar () {
	$('#limpiar').submit();
}

function consultarInicial () {
	
	limpiaAlertas(); 
	var frm = document.getElementById('main3');
	if (validarCamposConsulta()){
		if (comprobarCampos(false)){
			
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
	if (validarCamposConsulta()){
		if (comprobarCampos(true)){
			$("#btnModificar").hide();
			lanzarConsulta ();
		}// Si no, muestra el aviso
		else {
			avisoUnCampo ();
		}
	}
}
//Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo () {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}
 
 // Lanza la busqueda con el filtro introducido en el formulario
 function lanzarConsulta () {		 			 	
 	//comprobarCampos();			
	onInvokeAction('tasasSbp','filter');
 }

 
 // Comprueba que los valores del formulario son correctos antes de consultar
 function validarCamposConsulta () {
 
 	// Valida el campo 'Plan' si esta informado
 	if ($('#plan').val() != ''){
		 	var planOk = false;
		 	
		 	try {		 	
		 		var auxPlan =  parseFloat($('#plan').val());
		 		if(!isNaN(auxPlan) && $('#plan').val().length == 4){
					$('#plan').val(auxPlan);
					planOk = true;
				}
			}
			catch (ex) {}
			
			// Si ha habido error en la validacion muestra el mensaje
			if (!planOk) {
				$('#panelAlertasValidacion').html("Valor para el plan no v\u00E1lido");
				$('#panelAlertasValidacion').show();
				return false;
			}
		}
		
		// Valida el campo 'Linea' si esta informado
 		if ($('#linea').val() != ''){ 
		 	var lineaOk = false;
		 	try {		 	
		 		var auxLinea =  parseFloat($('#linea').val());
		 		if(!isNaN(auxLinea)){
					$('#linea').val(auxLinea);
					lineaOk = true;
				}
			}
			catch (ex) {}
			
			// Si ha habido error en la validacion muestra el mensaje
			if (!lineaOk) {
				$('#panelAlertasValidacion').html("Valor para la l\u00EDnea no v\u00E1lido");
				$('#panelAlertasValidacion').show();
				return false;
			}
		}
		
		// Valida el campo 'Provincia' si esta informado
		if ($('#provincia').val() != ''){ 
		 	var provOk = false;
		 	try {		 	
		 		var auxProv =  parseFloat($('#provincia').val());
		 		if(!isNaN(auxProv)){
					$('#provincia').val(auxProv);
					provOk = true;
				}
			}
			catch (ex) {}
			
			// Si ha habido error en la validacion muestra el mensaje
			if (!provOk) {
				$('#panelAlertasValidacion').html("Valor para la provincia no v\u00E1lido");
				$('#panelAlertasValidacion').show();
				return false;
			}
		}
 			 	
 	// Valida el campo 'Tasas de incendio' si esta informado		 	
 	if ($('#tasaIncendio').val() != ''){ 
	 	var tasaIncOk = false;
	 	try {		 	
	 		var tasaIncendio =  parseFloat($('#tasaIncendio').val().replace(",","."));
	 		if(!isNaN(tasaIncendio)){
				$('#tasaIncendio').val(tasaIncendio);
				tasaIncOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!tasaIncOk) {
			$('#panelAlertasValidacion').html("Valor para la tasa de incendio no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
							
	// Valida el campo 'Tasas de pedrisco' si esta informado
	if ($('#tasaPedrisco').val() != ''){
		var tasaPedOk = false;
	 	try {		 	
	 		var tasaPedrisco =  parseFloat($('#tasaPedrisco').val().replace(",","."));
	 		if(!isNaN(tasaPedrisco)){
				$('#tasaPedrisco').val(tasaPedrisco);
				tasaPedOk = true;
			}
		}
		catch (ex) {}
		
		if (!tasaPedOk) {
			$('#panelAlertasValidacion').html("Valor para la tasa de pedrisco no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}								
	}
	
	return true;
 }
 
 function comprobarCampos(incluirJmesa){
	
	 if (incluirJmesa) {
		 jQuery.jmesa.removeAllFiltersFromLimit('tasasSbp');	       		       		       	
	 }
	
	var resultado = false;
   	if ($('#plan').val() != ''){	       		 
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('tasasSbp','linea.codplan', $('#plan').val());
   		}
   		resultado = true;
   	}
   	if ($('#linea').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('tasasSbp','linea.codlinea', $('#linea').val());	 
   		}
   		resultado = true;
   	}	
   	if ($('#provincia').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('tasasSbp','comarca.id.codprovincia', $('#provincia').val());
   		}
   		resultado = true;
   	} 
   	if ($('#comarca').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('tasasSbp','comarca.id.codcomarca', $('#comarca').val());
   		}
   		resultado = true;
   	}
   	if ($('#cultivo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('tasasSbp','cultivo.id.codcultivo', $('#cultivo').val());
   		}
   		resultado = true;
   	}
   	if ($('#tasaIncendio').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('tasasSbp','tasaIncendio', $('#tasaIncendio').val());
   		}
   		resultado = true;
   	}
   	if ($('#tasaPedrisco').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('tasasSbp','tasaPedrisco', $('#tasaPedrisco').val());
   		}
   		resultado = true;
   	} 
   	return resultado;
}

function bloqueaInputs(){
	var frm = document.getElementById('main3');
}

function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');	
	$("#panelAlertas").html('');
				 			
	}

function replicar(){
	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	if(validarLineaseguriod()){
		$('#planreplica').val('');
		$('#lineareplica').val('');
		lupas.muestraTabla('LineaReplica','principio', '', '');
	}
}

function validarLineaseguriod(){
	// Valida el campo 'Plan' si esta informado
	if ($('#plan').val() != ''){
		var planOk = false;
		try {
			var auxPlan =  parseFloat($('#plan').val());
			if(!isNaN(auxPlan) && $('#plan').val().length == 4 && auxPlan > 0){
				$('#plan').val(auxPlan);
				planOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("Valor para el plan no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	else{
		$('#panelAlertasValidacion').html("Debe introducir un plan");
		$('#panelAlertasValidacion').show();
		return false;
	}
	
	// Valida el campo 'Linea' si esta informado
	if ($('#linea').val() != ''){
		var lineaOk = false;
		try {
			var auxLinea =  parseFloat($('#linea').val());
			if(!isNaN(auxLinea) && auxLinea > 0){
				$('#linea').val(auxLinea);
				lineaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para la linea no v\u00E1lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	else{
		$('#panelAlertasValidacion').html("Debe seleccionar una l\u00EDnea");
		$('#panelAlertasValidacion').show();
		return false;
	}	
	return true;
	
}

// Devuelve un boolean indicando si los plan/linea origen y destino de la replica son diferentes
function planLineaDiferentes () {	
	if ($('#planreplica').val() == $('#plan').val() && $('#lineareplica').val() == $('#linea').val()) return false
	else return true;
}


function doReplicar(){
	// Se comprueba que el plan y linea a replicar no son vacios antes de llamar al servidor
	// Si son vacios, se ha hecho click en un elemento de la lupa que no es un registro (ordenacion, etc.)
	var frm = document.getElementById('main3');
	
	if ($('#planreplica').val() != '' && $('#lineareplica').val() != '') {
		if(confirm('\u00BFDesea replicar todas las tasas para este Plan y L\u00EDnea?')){
			// Valida que el plan/linea origen y destino no son iguales
			if (planLineaDiferentes()) {				
				frm.method.value = "doReplicar";
				$('#main3').validate().cancelSubmit = true;
				$("#main3").submit();			
			}
			else {
				$('#panelAlertasValidacion').html("El plan/linea origen no puede ser igual que el destino");
				$('#panelAlertasValidacion').show();
			}
		}
	}
}

// DAA 26/04/2013
function importar(){
	limpiaPanelAlertasValidacionFile();
	$('#frmImportar').validate().cancelSubmit = true;
	showPopUpImportarTasas();
}

function limpiaPanelAlertasValidacionFile(){
	$("#file").val('');
	limpiaPanelAlertasValidacion();
}


function limpiaPanelAlertasValidacion() {
	$("#panelAlertasValidacionFile").hide();
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
}

function showPopUpImportarTasas(){
	$('#divImportarTasas').fadeIn('normal');
	$('#overlay').show();
}

function cerrarPopUpImportarTasas(){
	$('#divImportarTasas').fadeOut('normal');
	$('#overlay').hide();
}

function doImportar(){	
	limpiaPanelAlertasValidacion();
	if ($('#frmImportar').valid()){		
		$.blockUI.defaults.message = '<h4> Importando archivo de tasas.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#frmImportar').submit();
		cerrarPopUpImportarTasas();
	}
}