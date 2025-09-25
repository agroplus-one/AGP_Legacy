$(document).ready(function(){
	
	// Para evitar el cacheo de peticiones al servidor
	var URL = UTIL.antiCacheRand($("#main").attr("action"));
	$("#main").attr("action", URL);
	
	if ($('#origenLlamada').val() != null && ($('#origenLlamada').val() == 'alta'|| $('#origenLlamada').val() == 'modificar' )){
		$('#btnModificar').show();
	}
	
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
			"linea.codplan": {required: true, digits: true},
			"linea.codlinea":{required: true, digits: true},
		 	"linea.nomlinea":{required: false},
		 	"subentidadMediadora.id.codentidad": {required: false, digits: true},
		 	"subentidadMediadora.id.codsubentidad": {required: false, digits: true},
		 	"importe": {required: true, digits: true},
		 	"pctRecargo": {required: true}
		},
		messages: {
			"linea.codplan": {required: "El campo plan es obligatorio", digits: "El campo Plan sólo puede contener dígitos"},
			"linea.codlinea":{required: "El campo linea es obligatorio", digits: "El campo código linea sólo puede contener dígitos"},
			"subentidadMediadora.id.codentidad":{digits: "El campo código entidad sólo puede contener dígitos"},
			"subentidadMediadora.id.codsubentidad":{digits: "El campo código subentidad sólo puede contener dígitos"},
			"importe": {required: "El campo Import. mín. fraccionamiento es obligatorio", digits: "El campo Import. mín. fraccionamiento sólo puede contener dígitos"},
			"pctRecargo": {required: "El campo % Recargo es obligatorio", digits: "El campo % Recargo sólo puede contener dígitos"}
		}			
	});
})

function consultar(){
	$("#panelAlertasValidacion").hide();
	$("#panelInformacion").hide();
	$("#main").validate().cancelSubmit = true;
	$('#method').val("doConsulta");
	$("#origenLlamada").val('consultar');
	if(validaNumerico("plan", "Plan") && validaNumerico("linea", "Línea") ){
		
			if(validaNumerico("entmediadora", "Entidad Mediadora") && validaNumerico("subentmediadora", "Subentidad Mediadora")){
				$('#main').submit();
			}
		
	}
	
}

function limpiar(){
	$("#id").val('');
	$("#plan").val('');
	$("#linea").val('');
	$("#desc_linea").val('');			
	$("#val_fracc").val('');
	$("#entmediadora").val('');
	$("#subentmediadora").val('');
	$("#tipoFracc").val('');
	$("#val_recargo").val('');
	$('#btnModificar').hide();
	// importeFrac.href = "mtoImportesFrac.run?origenLlamada=menuGeneral&rand=" + getRand(); 
	//location.href = 'utilidadesSiniestros.run?ajax=false&' + parameterString;
	//consultar();
	location.href = "mtoImportesFrac.run?origenLlamada=menuGeneral&rand=" + getRand();
}

function baja(id, codplan, codlinea, nomlinea, importe, tipo, recargo){
	$("#main").validate().cancelSubmit = true;

	jConfirm('¿Está seguro de que desea eliminar el registro seleccionado?','Diálogo de Confirmación', function(r) {
		if (r==true){
			$("#id").val(id);
			/*$('#plan').val(codplan);
			$('#linea').val(codlinea);
			$('#desc_linea').val(nomlinea);
			$('#val_fracc').val(importe);
			$('#tipoFracc').val(tipo);
			$('#val_recargo').val(recargo);*/
			$('#method').val("doBaja");
			$('#main').submit();
		}
	});
}

function validaPlan(){
	if ($('#plan').val()>= 2015 || $('#plan').val() == '') {
		return true;
	}
	else {
		return false
	}
}

function validaTipo(){
	if ($('#tipoFracc').val() != '') {
		return true
	}
	else{
		return false
	}
}

function validaRecargo(){
	if ($('#tipoFracc').val() == '1') {
		return true
	}
	else {
		if($('#val_recargo').val() == '' || $('#val_recargo').val() == 0){
			$('#val_recargo').val(0)
			return true
		}
		else{
			return false
		}
		
	}
}

function validaValRecargo(){
	if($('#val_recargo').val() >= 0 && $('#val_recargo').val() <= 100){
		return true	
	}
	else{
		return false
	}
}


function validaSubEntidad(){
	if($('#subentmediadora').val() == '' && $('#entmediadora').val() !=''){
		$('#panelAlertasValidacion').html("Si introduce entidad mediadora es obligatoria la subentidad mediadora");
		$('#panelAlertasValidacion').show();
		return false;
	}
	if($('#subentmediadora').val() != '' && $('#entmediadora').val() ==''){
	
		$('#panelAlertasValidacion').html("Si introduce subentidad mediadora es obligatoria la entidad mediadora");
		$('#panelAlertasValidacion').show();
	
		return false;	
	}
	else{
		return true;
	}
}

function showPctRecargo() {
	if($('#tipoFracc').val() == '1'){
		$('#val_recargo').attr('disabled', false);
	}
	else{
		$('#val_recargo').attr('disabled', true);
		$('#val_recargo').val(''); 
	}
}
//Devuelve un booleano indicando si el valor del campo correspondiente al id 'valId' es númerico y en caso negativo
//muestra un mensaje incluyendo la descripción del campo indicado por 'desc'
function validaNumerico (valId, desc) {

	if ($('#' + valId).val() != ''){
		var valorOk = false;
		try {
			if(!isNaN($('#' + valId).val())) valorOk = true;
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!valorOk) {
			$('#panelAlertasValidacion').html("Valor para el campo '" + desc + "' no válido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	
	return true;
}

function modificar(id, codplan, codlinea, nomlinea, codEntidad, codSubEntidad, importe, tipo, recargo){
	$("#id").val(id);
	$("#plan").val(codplan);
	$("#linea").val(codlinea);
	$("#desc_linea").val(nomlinea);
	if(null!=codEntidad){
		$("#entmediadora").val(codEntidad);
	}else{
		$("#entmediadora").val('');
	}
	if(null!=codSubEntidad){
		$("#subentmediadora").val(codSubEntidad);
	}else{
		$("#subentmediadora").val('');
	}
		
	$("#val_fracc").val(importe);
	$("#tipoFracc").val(tipo);
	if(recargo!=null){
		$("#val_recargo").val(recargo);
	}
	showPctRecargo();
	if(validaSubEntidad()){
		$('#btnModificar').show();
	}
}

function alta(){
	$("#panelAlertasValidacion").hide();
	$("#panelInformacion").hide();
	if (validaPlan()){
		if(validaTipo()){
			if(validaRecargo()){
				if (validaValRecargo()){
					if(validaSubEntidad()){
						$('#method').val("doAlta");
						$('#origenLlamada').val("alta");
						$('#main').submit();
					}
				}else{
					$('#panelAlertasValidacion').html("El valor de %Recargo ha de estar entre el 0% y 100%");
					$('#panelAlertasValidacion').show();
				}
			}else {
				$('#panelAlertasValidacion').html("El tipo de fraccionamiento SAECA no admite %Recargo distinto de 0%");
				$('#panelAlertasValidacion').show();
			}
		}else{
			$('#panelAlertasValidacion').html("Ha de seleccionar un tipo");
			$('#panelAlertasValidacion').show();
		}
		}else{
		$('#panelAlertasValidacion').html("El valor de Plan debe ser mayor o igual a 2015");
		$('#panelAlertasValidacion').show();
	}
}

function editar(id, codplan, codlinea, nomlinea, importe, tipo, recargo){
	$("#panelAlertasValidacion").hide();
	$("#panelInformacion").hide();
	if (validaPlan()){
		if(validaTipo()){
			if(validaRecargo()){
				if (validaValRecargo()){
						if(validaSubEntidad()){
							$('#method').val("doEdita");
							$('#main').submit();
						
					}
				}else{
					$('#panelAlertasValidacion').html("El % Recargo no puede superar el 100%");
					$('#panelAlertasValidacion').show();
				}
			}
			else {
				$('#panelAlertasValidacion').html("El tipo de fraccionamiento SAECA no admite % Recargo");
				$('#panelAlertasValidacion').show();
			}
		}
		else{
			$('#panelAlertasValidacion').html("Ha de seleccionar un tipo");
			$('#panelAlertasValidacion').show();
		}
		
	}else{
		$('#panelAlertasValidacion').html("El valor de Plan debe ser mayor o igual a 2015");
		$('#panelAlertasValidacion').show();
	}
}

function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main');
	comprobarCampos();
	$.get('mtoImportesFrac.run?ajax=true&origenLlamada=paginacion&' +decodeURIComponent(parameterString),
			function(data) {
				$("#grid").html(data);
			});
}

function comprobarCampos() {
	jQuery.jmesa.removeAllFiltersFromLimit('consultaFracc');
	var resultado = false;

	if ($('#id').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaFracc', 'id', $(
				'#id').val());
		resultado = true;
	}
	if ($('#plan').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaFracc', 'linea.codplan', $(
				'#plan').val());
		resultado = true;
	}
	if ($('#linea').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaFracc', 'linea.codlinea', $(
				'#linea').val());
		resultado = true;
	}
	if ($('#entmediadora').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaFracc', 'subentidadMediadora.id.codentidad', $(
				'#entmediadora').val());
		resultado = true;
	}
	if ($('#subentmediadora').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaFracc', 'subentidadMediadora.id.codsubentidad', $(
				'#subentmediadora').val());
		resultado = true;
	}
		
	if ($('#val_fracc').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaFracc', 'importe', $('#val_fracc')
				.val());
		resultado = true;
	}
	if ($('#tipoFracc').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaFracc', 'tipo', $(
				'#tipoFracc').val());
		resultado = true;
	}
	if ($('#val_recargo').val() != '') {
		jQuery.jmesa.addFilterToLimit('consultaFracc', 'pctRecargo', $(
				'#val_recargo').val());
		resultado = true;
	}
	return resultado;
}



