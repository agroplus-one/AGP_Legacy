$(document).ready(function(){
	
	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
	
    if ($('#id').val() != null && $('#id').val() != ''){
		$('#btnModificar').show();
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
		
		rules: {
			"codplan":{required: true, digits: true, minlength: 4},
			"impuestoSbp.codigo":{required: true},
			"baseSbp.base":{required: true},
			"valor":{required: true, number: true, range: [0.00,100.00]}	  	
		 },
		 messages: {
		 	"codplan":{required: "El campo Plan es obligatorio", digits: "El campo Plan s�lo puede contener d�gitos", minlength: "El campo Plan debe contener 4 d�gitos"},
		 	"impuestoSbp.codigo":{required: "El campo Impuesto es obligatorio"},
		 	"baseSbp.base":{required: "El campo Base es obligatorio"},
		 	"valor":{required: "El campo Valor es obligatorio", range: "El campo Valor debe contener un porcentaje valido"}
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
	onInvokeAction('consultaMtoImpuestoSbp','filter');
}

//Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo () {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}
function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');	
	$("#panelAlertas").html('');
				 			
	}

function comprobarCampos(incluirJmesa){
	
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('consultaMtoImpuestoSbp');
	}
   	var resultado = false;
   	
   	if ($('#codplan').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaMtoImpuestoSbp','codplan', $('#codplan').val());
   		}
   		resultado = true;
   	}
   	if ($('#codimpuesto').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaMtoImpuestoSbp','impuestoSbp.codigo', $('#codimpuesto').val());
   		}
   		resultado = true;
   	}
   	if ($('#nomimpuesto').val() != ''){
   		if (incluirJmesa) {
   			jQuery.jmesa.addFilterToLimit('consultaMtoImpuestoSbp','impuestoSbp.descripcion', $('#nomimpuesto').val());
   		}
   		resultado = true;
   	}
	if ($('#nombase').val() != ''){
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaMtoImpuestoSbp','baseSbp.base', $('#nombase').val());
		}
		resultado = true;
   	}
	if ($('#valor').val() != ''){
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('consultaMtoImpuestoSbp','valor', $('#valor').val());
		}
		resultado = true;
   	}
   	
	return resultado;
}
 
//Limpia los valores introducidos y vuelve a la pantalla inicial
function limpiar () {
	$('#limpiar').submit();
}

function editar(id,codplan,codimpuesto,nomimpuesto,nombase,valor){

	limpiaAlertas();
	
	var frm = document.getElementById('main3');
	
	frm.target="";
	frm.id.value=id;
	frm.codplan.value = codplan;
	frm.codimpuesto.value = codimpuesto;
	frm.nomimpuesto.value = nomimpuesto;	
	frm.nombase.value = nombase;
	frm.valor.value = valor;
	$('#btnModificar').show();
}


function modificar(){
	limpiaAlertas();
	var frm = document.getElementById('main3');
	frm.method.value= "doEditar";
	$('#main3').submit();
}

function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
   	var frm = document.getElementById('main3');
    $.get('mtoImpuestoSbp.run?ajax=true&' + decodeURIComponent(parameterString), function(data) {
        $("#grid").html(data);
		});
}


function borrar(id) {
	
	limpiaAlertas();
	
	if(confirm('�Est� seguro de que desea eliminar el registro seleccionado?')){
		$("#methodBorrar").val("doBorrar");
		$("#idBorrar").val(id);
		$("#frmBorrar").submit();
	}	
}

 
function alta() {
	limpiaAlertas();
	var frm = document.getElementById('main3');
	frm.method.value= "doAlta";
	$("#main3").submit();	
}   


function replicar(){
	
	limpiaAlertas();
	//$('#main3').validate().cancelSubmit = true;
	if(validarPlan()){
		$('#planreplica').val('');
		showPopUpReplicarPlan();
	}
}

function validarPlan(){
	// Valida el campo 'Plan' si esta informado
	if ($('#codplan').val() != ''){
		var planOk = false;
		try {
			var auxPlan =  parseFloat($('#codplan').val());
			if(!isNaN(auxPlan) && $('#codplan').val().length == 4 && auxPlan > 0){
				$('#codplan').val(auxPlan);
				planOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("Valor para el plan no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	else{
		$('#panelAlertasValidacion').html("Debe introducir un plan");
		$('#panelAlertasValidacion').show();
		return false;
	}
	return true;
	
}
function validarConsulta() {
	
	// Valida el campo 'Plan' si esta informado
	if ($('#codplan').val() != ''){
		var planOk = false;
		try {
			var auxPlan =  parseFloat($('#codplan').val());
			if(!isNaN(auxPlan) && $('#codplan').val().length == 4){
				$('#codplan').val(auxPlan);
				planOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!planOk) {
			$('#panelAlertasValidacion').html("Valor para el plan no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	// Valida el campo 'valor' si esta informado		 	
 	if ($('#valor').val() != ''){ 
	 	var valorOk = false;
	 	try {		 	
	 		var valor =  parseFloat($('#valor').val().replace(",","."));
	 		if(!isNaN(valor)){
				$('#valor').val(valor);
				valorOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validaci�n muestra el mensaje
		if (!valorOk) {
			$('#panelAlertasValidacion').html("Valor para el campo valor no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	
	
	return true;
}