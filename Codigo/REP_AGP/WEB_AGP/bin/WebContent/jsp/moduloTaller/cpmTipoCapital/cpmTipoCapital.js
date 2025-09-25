$(document).ready(function(){
	
	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
    
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
        inputField        : "fechafingarantia",
        button            : "btn_fechafingarantia",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
	
	var URL = UTIL.antiCacheRand($("#main3").attr("action"));
	$("#main3").attr("action", URL);
	
    if ($('#id').val() != null && $('#id').val() != ''){
		$('#btnModificar').show();
	}

	$('#main3').validate({					
	
		onfocusout: function(element) {
			if ( ($('#method').val() == "doEditar") || ($('#method').val() == "doAlta") ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		
		rules: {
			"cultivo.linea.codplan":{required: true, digits: true, minlength: 4},
	 		"cultivo.linea.codlinea":{required: true, digits: true},
	 		"modulo":{required: true},	 
	 		"cultivo.id.codcultivo":{required: true, digits: true},
	 		"tipoCapital.codtipocapital":{required: true, digits: true, maxlength: 3},
	 		"conceptoPpalModulo.codconceptoppalmod":{required: true, digits: true},
	 		"sistemaCultivo.codsistemacultivo":{digits: true},
	 		"fechafingarantia":{dateITA: true} 
		},
		messages: {
			"cultivo.linea.codplan":{required: "El campo Plan es obligatorio", digits: "El campo Plan solo puede contener digitos", minlength: "El campo Plan debe contener 4 digitos"},
	 		"cultivo.linea.codlinea":{required: "El campo Linea es obligatorio", digits: "El campo Linea debe contener digitos"},
	 		"modulo":{required: "El campo Modulo es obligatorio"},
	 		"cultivo.id.codcultivo":{required: "El campo Cultivo es obligatorio", digits: "El campo Cultivo debe contener digitos"},
	 		"tipoCapital.codtipocapital":{required: "El campo Tipo Capital es obligatorio", 
	 									  digits: "El campo Tipo Capital debe contener digitos"},
	 		"conceptoPpalModulo.codconceptoppalmod":{required: "El campo CPM es obligatorio", digits: "El campo CPM debe contener digitos"},
	 		"sistemaCultivo.codsistemacultivo":{digits: "El campo Sist. Cultivo debe contener digitos"},
	 		"fechafingarantia":{dateITA: "El formato del campo Fecha Fin de Garantia es dd/mm/YYYY"}
	 	}
	});
		
});

function consultar(){

	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	$('#btnModificar').hide();
	
	//Llamamos al método comprobarCampos para añadir los valores al "limit"
	comprobarCampos();
	onInvokeAction('consultaCPMTipoCapital','filter');	
}


function comprobarCampos(){
	jQuery.jmesa.removeAllFiltersFromLimit('consultaCPMTipoCapital');
   	var resultado = false;
   	
   	if ($('#plan').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCPMTipoCapital','cultivo.linea.codplan', $('#plan').val());
   		resultado = true;
   	}
   	if ($('#linea').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCPMTipoCapital','cultivo.linea.codlinea', $('#linea').val());
   		resultado = true;
   	}
   	if ($('#codmodulo').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCPMTipoCapital','modulo', $('#codmodulo').val());
   		resultado = true;
   	}
   	if ($('#capital').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCPMTipoCapital','tipoCapital.codtipocapital', $('#capital').val());
   		resultado = true;
   	}
  	if ($('#cpm').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCPMTipoCapital','conceptoPpalModulo.codconceptoppalmod', $('#cpm').val());
   		resultado = true;
   	}
   	if ($('#cultivo').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCPMTipoCapital','cultivo.id.codcultivo', $('#cultivo').val());
   		resultado = true;
   	}
   	if ($('#sistemaCultivo').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCPMTipoCapital','sistemaCultivo.codsistemacultivo', $('#sistemaCultivo').val());
   		resultado = true;
   	}
   	if ($('#fechaFinG').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCPMTipoCapital','fechafingarantia', $('#fechaFinG').val());
   		resultado = true;
   	}
   	if ($('#cicloCultivo').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCPMTipoCapital','cicloCultivo.codciclocultivo', $('#cicloCultivo').val());
   		resultado = true;
   	}
   	
	return resultado;
}
  	
function limpiar(){
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	$('#btnModificar').hide();	
	
	$('#plan').val('');
	$('#desc_linea').val('');
	$('#linea').val('');
	$('#codmodulo').val('');
	$('#capital').val('');
	$('#desc_capital').val('');
	$('#cpm').val('');
	$('#cultivo').val('');
	$('#desc_cultivo').val('');
	$('#sistemaCultivo').val('');
	$('#dessistemaCultivo').val('');
	$('#fechaFinG').val('');
	$('#desc_cpm').val('');
	$('#cicloCultivo').val('');
	jQuery.jmesa.removeAllFiltersFromLimit('consultaCPMTipoCapital');
	onInvokeAction('consultaCPMTipoCapital','clear');			
}
	
function editar(id,linea,desc_linea,plan,codmodulo,cpm,capital,desc_capital,cultivo,desc_cultivo,sistemaCultivo,dessistemaCultivo,fechaFinG,codCicloCultivo,desCicloCultivo){

	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	var frm = document.getElementById('main3');
	
	frm.target="";
	frm.id.value=id;
	frm.plan.value = plan;
	frm.linea.value = linea;
	frm.desc_linea.value = desc_linea;
	frm.codmodulo.value =codmodulo;
	frm.capital.value = capital;
	frm.desc_capital.value = desc_capital;
	frm.cpm.value = cpm;
	frm.cultivo.value = cultivo;
	frm.desc_cultivo.value = desc_cultivo;
	frm.sistemaCultivo.value = sistemaCultivo;
	frm.dessistemaCultivo.value = dessistemaCultivo;
	frm.fechaFinG.value = fechaFinG;
	frm.cicloCultivo.value = codCicloCultivo;
	frm.desc_cicloCultivo.value =desCicloCultivo;
	$('#btnModificar').show();
	
}


function modificar(){
	comprobarCampos();
	$("#method").val("doEditar");	
	$('#main3').submit();
}

function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
   	var frm = document.getElementById('main3');
    $.get('cpmTipoCapital.run?ajax=true&' + decodeURIComponent(parameterString), function(data) {
        $("#grid").html(data);
		});
}


function borrar(id) {
	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
		$("#methodBorrar").val("doBorrar");
		$("#idBorrar").val(id);
		$("#frmBorrar").submit();
	}	
}

 
function alta() {
	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	comprobarCampos();
	$("#method").val("doAlta");
	$("#main3").submit();	
}   


function replicar(){
	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
	comprobarCampos();
	$('#main3').validate().cancelSubmit = true;
	if(validarLineaseguriod()){
		$('#planreplica').val('');
		$('#lineareplica').val('');
		lupas.muestraTabla('LineaReplica','principio', '', '');
	}
}



// Devuelve un boolean indicando si los plan/linea origen y destino de la réplica son diferentes
function planLineaDiferentes () {	
	if ($('#planreplica').val() == $('#plan').val() && $('#lineareplica').val() == $('#linea').val()) return false
	else return true;
}


function doReplicar(){
	// Se comprueba que el plan y linea a replicar no son vacíos antes de llamar al servidor
	// Si son vacíos, se ha hecho click en un elemento de la lupa que no es un registro (ordenación, etc.)
	if ($('#planreplica').val() != '' && $('#lineareplica').val() != '') {
		if(confirm('¿Desea replicar todos los CPM - Tipo Capital para este Plan y Linea?')){
			// Valida que el plan/linea origen y destino no son iguales
			if (planLineaDiferentes()) {
				$("#method").val("doReplicar");
				$("#main3").submit();			
			}
			else {
				$('#panelAlertasValidacion').html("El plan/linea origen no puede ser igual que el destino");
				$('#panelAlertasValidacion').show();
			}
		}
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
			$('#panelAlertasValidacion').html("Valor para la linea no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	else{
		$('#panelAlertasValidacion').html("Debe seleccionar una linea");
		$('#panelAlertasValidacion').show();
		return false;
	}	
	return true;
	
}

/**
 * Comprueba que el plan y la línea están informados
 * @returns {Boolean}
 */
function validarLinea () {
	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	
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
			$('#panelAlertasValidacion').html("El campo Plan es obligatorio para mostrar la ventana");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	else {
			$('#panelAlertasValidacion').html("El campo Plan es obligatorio para mostrar la ventana");
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
			$('#panelAlertasValidacion').html("El campo Linea es obligatorio para mostrar la ventana");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	else {
			$('#panelAlertasValidacion').html("El campo Linea es obligatorio para consultar Cultivo");
			$('#panelAlertasValidacion').show();
			return false;				
	}
	
	return true;
}