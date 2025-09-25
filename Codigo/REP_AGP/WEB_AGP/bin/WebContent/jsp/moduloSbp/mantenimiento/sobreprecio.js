$(document).ready(function(){
			
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
	 		"cultivo.id.codcultivo":{required: true, digits: true},
	 		"tipoCapital.codtipocapital":{required: true, digits: true,comprobarTipoCapital: ['tipoCapital.codtipocapital']},	 		
	 		"precioMinimo":{required: true, range: [0,99999.9999]},		
	 		"precioMaximo":{required: true, range: [0,99999.9999], comprobarPrecio: ['precioMinimo', 'precioMaximo']}		 			 						 		
		},
		messages: {
			"linea.codplan":{required: "El campo Plan es obligatorio", digits: "El campo Plan sólo puede contener dígitos", minlength: "El campo Plan debe contener 4 dígitos"},
	 		"linea.codlinea":{required: "El campo Línea es obligatorio", digits: "El campo Línea debe contener dígitos"},
	 		"provincia.codprovincia":{required: "El campo Provincia es obligatorio", digits: "El campo Código de Provincia debe contener dígitos"},
	 		"cultivo.id.codcultivo":{required: "El campo Cultivo es obligatorio", digits: "El campo Cultivo debe contener dígitos"},
	 		"tipoCapital.codtipocapital":{required: "El campo Tipo Capital es obligatorio", digits: "El campo Tipo Capital debe contener dígitos", comprobarTipoCapital:"El campo Tipo Capital no puede ser 999"},
	 		"precioMinimo":{required: "El campo Precio Minimo es obligatorio", range: "El campo Precio Minimo debe contener un número entre 0 y 99999,9999"},
	 		"precioMaximo":{required: "El campo Precio Maximo es obligatorio", range: "El campo Precio Maximo debe contener un número entre 0 y 99999,9999", comprobarPrecio:"El Precio Minimo debe ser menor al Precio Maximo"}
	 	}
	});
			//comprueba los precios
			//params--> array que contiene los precios
			jQuery.validator.addMethod("comprobarPrecio", function(value, element, params) {
				return (this.optional(element) || precioMaximoMayor(document.getElementById(params[0]), document.getElementById(params[1])));
	});			
			
			jQuery.validator.addMethod("comprobarTipoCapital", function(value, element, params) {
				if($('#capital').val() != '999'){
					return true;
				}else{
					return false;
				}
			
				
	});
		
});

function consultarInicial () {
	limpiaAlertas(); 
	
	var frm = document.getElementById('main3');
	if (validarConsulta()){
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
	if (validarConsulta()){
		if (comprobarCampos(true)){
			$('#btnModificar').hide();
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

//Lanza la consulta de fechas de contratacion
function lanzarConsulta () {
	// Llama al metodo que llama al servidor
	onInvokeAction('consultaSobreprecioSbp','filter');
}
function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');	
	$("#panelAlertas").html('');
				 			
	}

function validarLinea(){
			
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("El campo Plan es obligatorio para consultar Cultivo");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	else {
			$('#panelAlertasValidacion').html("El campo Plan es obligatorio para consultar Cultivo");
			$('#panelAlertasValidacion').show();
			return false;				
	}
	
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para la línea no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	else {
			$('#panelAlertasValidacion').html("El campo Línea es obligatorio para consultar Cultivo");
			$('#panelAlertasValidacion').show();
			return false;				
	}
	
	if (planOk && lineaOk) {
		
		$('#panelAlertasValidacion').html("");
		$('#panelAlertasValidacion').hide();
		
		return true;
	}
	else{
		return false;
	}
}

function insertaDatosPlanLinea(){
	$('#codplan').val($('#plan').val());
	$('#codlinea').val($('#linea').val());
}

function validarConsulta(){

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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("Valor para el plan no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para la línea no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!provOk) {
			$('#panelAlertasValidacion').html("Valor para la provincia no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	if ($('#cultivo').val() != ''){ 
	 	var cultivoOk = false;
	 	try {		 	
	 		var auxCultivo =  parseFloat($('#cultivo').val());
	 		if(!isNaN(auxCultivo)){
				$('#cultivo').val(auxCultivo);
				cultivoOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validación muestra el mensaje
		if (!cultivoOk) {
			$('#panelAlertasValidacion').html("Valor para el cultivo no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	if ($('#capital').val() != ''){ 
	 	var capitalOk = false;
	 	try {		 	
	 		var auxCapital =  parseFloat($('#capital').val());
	 		if(!isNaN(auxCapital)){
				$('#capital').val(auxCapital);
				capitalOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci&oacute;n muestra el mensaje
		if (!capitalOk) {
			$('#panelAlertasValidacion').html("Valor para el capital no v&aacute;lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	if ($('#precioMinimo').val() != ''){ 
	 	var sbpOk = false;
	 	try {		 	
	 		var auxSbp =  parseFloat($('#precioMinimo').val());
	 		if(!isNaN(auxSbp)){
				$('#precioMinimo').val(auxSbp);
				sbpOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validación muestra el mensaje
		if (!sbpOk) {
			$('#panelAlertasValidacion').html("Valor para el Precio Mínimo no válido");
			$('#panelAlertasValidacion').show();
				return false;
			}
		}
	if ($('#precioMaximo').val() != ''){ 
	 	var sbpOk = false;
	 	try {		 	
	 		var auxSbp =  parseFloat($('#precioMaximo').val());
	 		if(!isNaN(auxSbp)){
				$('#precioMaximo').val(auxSbp);
				sbpOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validación muestra el mensaje
		if (!sbpOk) {
			$('#panelAlertasValidacion').html("Valor para el Precio Maximo no válido");
			$('#panelAlertasValidacion').show();
				return false;
			}
		}
		
		return true;
}
 
function comprobarCampos(incluirJmesa){
	
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('consultaSobreprecioSbp');
	}
	
   	var resultado = false;
   	
   	if ($('#plan').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaSobreprecioSbp','linea.codplan', $('#plan').val());
   		}
   		resultado = true;
   	}
   	if ($('#linea').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaSobreprecioSbp','linea.codlinea', $('#linea').val());
   		}
   		resultado = true;
   	}
   	if ($('#provincia').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaSobreprecioSbp','provincia.codprovincia', $('#provincia').val());
   		}
  		resultado = true;
   	}
   	if ($('#cultivo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaSobreprecioSbp','cultivo.id.codcultivo', $('#cultivo').val());
   		}
  		resultado = true;
   	}
   	if ($('#capital').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaSobreprecioSbp','tipoCapital.codtipocapital', $('#capital').val());
   		}
  		resultado = true;
   	}
   	if ($('#precioMinimo').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaSobreprecioSbp','precioMinimo', $('#precioMinimo').val());
   		}
       	resultado = true;
	}
	if ($('#precioMaximo').val() != ''){
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaSobreprecioSbp','precioMaximo', $('#precioMaximo').val());
		}
       	resultado = true;
	}
	return resultado;
}

//Limpia los valores introducidos y vuelve a la pantalla inicial
function limpiar () {
	$('#limpiar').submit();
}  	


function editar(id,linea,plan,nomLinea,codProvincia,nomProvincia,codCultivo,nomCultivo,precioMinimo,precioMaximo,codTipoCapital,descTipoCapital){

	var frm = document.getElementById('main3');
//	alert(codTipoCapital);
//	alert(descTipoCapital);
	frm.target="";
	frm.id.value=id;
	frm.plan.value = plan;
	frm.linea.value = linea;
	frm.desc_linea.value =nomLinea; 		
	frm.provincia.value = codProvincia;		
	frm.desc_provincia.value = nomProvincia;
	frm.cultivo.value = codCultivo;		
	frm.desc_cultivo.value = nomCultivo;	
	frm.precioMinimo.value = precioMinimo;
	frm.precioMaximo.value = precioMaximo;
	frm.capital.value = codTipoCapital;		
	frm.desc_capital.value = descTipoCapital;
	$('#btnModificar').show();
}

function modificar(){
	//comprobarCampos();
	var frm = document.getElementById('main3');
	frm.method.value = "doEditar";
	$('#main3').submit();
}

function borrar(id) {
	var frm = document.getElementById('main3');
	$('#main3').validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar el Sobreprecio seleccionado?')){
		frm.method.value = "doBorrar";
		$("#id").val(id);
		$("#main3").submit();
	}	
} 

function alta() {
	limpiaAlertas()
	var frm = document.getElementById('main3');
	frm.method.value = "doAlta";
	$("#main3").submit();	
} 

function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main3');
    $.get('sobreprecioSbp.run?ajax=true&' + parameterString, function(data) {
        $("#grid").html(data)
		});
}

function precioMaximoMayor(precioMinimo, precioMaximo){
	var res = false;
	var min = precioMinimo.value;
	var max = precioMaximo.value;

    if (max >= min){
    	 res = true;
    }
    return res;	  
}

function replicar(){
	limpiaAlertas()
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	//comprobarCampos();
	//$('#main3').validate().cancelSubmit = true;
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("Valor para el plan no válido");
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
		
		// Si ha habido error en la validación muestra el mensaje
		if (!lineaOk) {
			$('#panelAlertasValidacion').html("Valor para la línea no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	else{
		$('#panelAlertasValidacion').html("Debe seleccionar una línea");
		$('#panelAlertasValidacion').show();
		return false;
	}	
	return true;
	
}

// Devuelve un boolean indicando si los plan/linea origen y destino de la réplica son diferentes
function planLineaDiferentes () {	
	if ($('#planreplica').val() == $('#plan').val() && $('#lineareplica').val() == $('#linea').val()) return false
	else return true;
}


function doReplicar(){
	// Se comprueba que el plan y linea a replicar no son vacíos antes de llamar al servidor
	// Si son vacíos, se ha hecho click en un elemento de la lupa que no es un registro (ordenación, etc.)
	var frm = document.getElementById('main3');
	
	if ($('#planreplica').val() != '' && $('#lineareplica').val() != '') {
		if(confirm('¿Desea replicar todos las Sobreprecios para este Plan y Línea?')){
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