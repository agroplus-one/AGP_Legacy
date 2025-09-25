$(document).ready(function(){
					
	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
	document.getElementById("main3").action = URL;
			 				
	$('#main3').validate({
		onfocusout: function(element) {
			if ( ($('#method').val() == "doEditar") || ($('#method').val() == "doAlta") ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		rules: {
			"linea.codplan":{required: true, digits: true, minlength: 4},
			"linea.codlinea":{required: true, digits: true, minlength: 3},
			"primaMinima":{required: true, range: [0.01, 999999.99]}
		},
		messages: {
			"linea.codplan":{required: "El campo Plan es obligatorio", digits: "El campo Plan sólo puede contener dígitos", minlength: "El campo Plan debe contener 4 dígitos"},
			"linea.codlinea":{required: "El campo Línea es obligatorio", digits: "El campo Línea debe contener dígitos"},
			"primaMinima":{required: "El campo Prima Mínima es obligatorio", range: "El campo Prima Mínima debe contener un número entre 0,01 y 999999,99"}
		}
	});
});
			  
function consultar(){
 	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	if (validarCamposConsulta()) {
		comprobarCampos();
		onInvokeAction('consultaPrimaMinimaSbp','filter');
	}					
}
		 	
// Comprueba que los valores del formulario son correctos antes de consultar
function validarCamposConsulta () {
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
	
	// Valida el campo 'Prima mínim' si esta informado		 	
	if ($('#primaMinima').val() != ''){ 
		var primaMinimaOk = false;
		try {
			var primaMinima =  parseFloat($('#primaMinima').val().replace(",","."));
			if(!isNaN(primaMinima)){
				$('#primaMinima').val(primaMinima);
				primaMinimaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validación muestra el mensaje
		if (!primaMinimaOk) {
			$('#panelAlertasValidacion').html("Valor para la prima mínima no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	return true;
}
		    
function alta(){
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
    var frm = document.getElementById('main3');
    frm.method.value = 'doAlta';
 	$('#main3').submit();
}
		     
function editar(id,linea,plan,nomLinea, primaMinima){
 
 	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	var frm = document.getElementById('main3');
	
	frm.target="";
	frm.idPrimaMinimaSbp.value=id;
	frm.plan.value = plan;
	frm.linea.value = linea;
	frm.desc_linea.value = nomLinea; 
	frm.primaMinima.value = primaMinima;		

	var modif = document.getElementById('btnModificar');
	modif.style.display = "";				
}	
			
function modificar(){
			
	var frm = document.getElementById('main3');
	frm.target="";
	$("#panelInformacion").hide();
	frm.method.value = 'doEditar';	
	$('#main3').submit();		
}		     		 			 
			
	         
function comprobarCampos(){
	jQuery.jmesa.removeAllFiltersFromLimit('consultaPrimaMinimaSbp');
 	var resultado = false;
	
 	if ($('#plan').val() != ''){
	jQuery.jmesa.addFilterToLimit('consultaPrimaMinimaSbp','linea.codplan', $('#plan').val());
	resultado = true;
 	}       	
 	if ($('#linea').val() != ''){
	jQuery.jmesa.addFilterToLimit('consultaPrimaMinimaSbp','linea.codlinea', $('#linea').val());
	resultado = true;
 	}
 	if ($('#primaMinima').val() != ''){
		jQuery.jmesa.addFilterToLimit('consultaPrimaMinimaSbp','primaMinima', $('#primaMinima').val());
     		resultado = true;
     	} 

     	return resultado;
 }
	        			
function limpiar(){

	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	$("#btnModificar").hide();	
	
	$('#plan').val('');
	$('#linea').val('');
	$('#desc_linea').val('');
	$('#primaMinima').val('');
	
	$('#main3').attr('target', '');
	
	jQuery.jmesa.removeAllFiltersFromLimit('consultaPrimaMinimaSbp');
	onInvokeAction('consultaPrimaMinimaSbp','clear');								
				
 }
			 
			 
function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main3');
    $.get('primaMinimaSbp.run?ajax=true&' + parameterString, function(data) {
        $("#grid").html(data)
	});
}

function borrar(id) {
	$('#main3').validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar esta Prima Mínima de Sobreprecio?')){
		var frm = document.getElementById('main3');
		frm.method.value = 'doBaja';
		frm.idPrimaMinimaSbp.value = id;
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main3").submit();
	}
}