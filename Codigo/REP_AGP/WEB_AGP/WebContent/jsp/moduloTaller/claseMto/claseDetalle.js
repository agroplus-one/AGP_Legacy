$(document).ready(function(){
			
    var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
    
    if ($('#id').val() != null && $('#id').val() != ''){
		$('#btnModificar').show();
	}
	$('#modulo').focus();
 
	$('#main3').validate({					
		
		onfocusout: function(element) {
			if ( ($('#method').val() == "doEditar") || ($('#method').val() == "doAlta") ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		
		rules: {
			"codmodulo":{required: true},
			"cicloCultivo.codciclocultivo": {digits: true},
			"sistemaCultivo.codsistemacultivo": {digits: true},
			"cultivo.id.codcultivo":{required: true, digits: true},
	 		"variedad.id.codvariedad":{required: true, digits: true},
	 		"tipoCapital.codtipocapital": {digits: true},
			"codprovincia":{required: true, digits: true},
		 	"codcomarca":{required: true, digits: true},
		 	"codtermino":{required: true, digits: true},
		 	"subtermino":{subterminoOK:true} ,
		 	"tipoPlantacion.codtipoplantacion": {digits: true}
		},
		messages: {
			"codmodulo":{required: "El campo Módulo es obligatorio "},
			"cicloCultivo.codciclocultivo":{digits: "El campo Ciclo de Cultivo sólo puede contener dígitos"},
			"sistemaCultivo.codsistemacultivo":{digits: "El campo Sistema de Cultivo sólo puede contener dígitos"},
			"cultivo.id.codcultivo":{required: "El campo Cultivo es obligatorio", digits: "El campo Cultivo sólo puede contener dígitos"},
	 		"variedad.id.codvariedad":{required: "El campo Variedad es obligatorio", digits: "El campo Variedad sólo puede contener dígitos"},
	 		"tipoCapital.codtipocapital": {digits: "El campo Tipo Capital sólo puede contener dígitos"},
	 		"codprovincia":{required: "El campo Provincia es obligatorio", digits: "El campo Provincia sólo puede contener dígitos"},
	 		"codcomarca":{required: "El campo Comarca es obligatorio", digits: "El campo Comarca sólo puede contener dígitos"},
		 	"codtermino":{required: "El campo Término es obligatorio ", digits: "El campo Término sólo puede contener dígitos"},
		 	"subtermino":{required: "El campo Subtérmino es obligatorio", subterminoOK: "El campo Subtérmino sólo puede contener una letra, un número o un espacio en blanco"},
		 	"tipoPlantacion.codtipoplantacion": {digits: "El campo Tipo Plantación sólo puede contener dígitos"}
	 	}
	});
	
	jQuery.validator.addMethod("subterminoOK", function(value, element, params) {
		 		return ($('#subtermino').val().match(/^[\w ]+$/) != null);	
		 	}
	);
		
});

function consultar(){

	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	$("#vieneDeConsultar").val('true');
		
	//DAA 08/02/2013
	$('#marcaTodos').val('false');
	$('#listaIdsMarcados').val('');
	$("input[type=checkbox]").each(function(){
		$(this).attr('checked',false);
	});
	
	
	//Llamamos al método comprobarCampos para añadir los valores al "limit"
	comprobarCampos();
	onInvokeAction('consultaClaseDetalle','filter');	
}

function comprobarCampos(){
	jQuery.jmesa.removeAllFiltersFromLimit('consultaClaseDetalle');
   	var resultado = false;
   	
   	if ($('#modulo').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','codmodulo', $('#modulo').val());
   		resultado = true;
   	}
   	if ($('#cicloCultivo').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','cicloCultivo.codciclocultivo', $('#cicloCultivo').val());
   		resultado = true;
   	}
   	if ($('#sistemaCultivo').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','sistemaCultivo.codsistemacultivo', $('#sistemaCultivo').val());
   		resultado = true;
   	}
   	if ($('#cultivo').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','cultivo.id.codcultivo', $('#cultivo').val());
   		resultado = true;
   	}
   	if ($('#variedad').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','variedad.id.codvariedad', $('#variedad').val());
   		resultado = true;
   	}
   	if ($('#provincia').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','codprovincia', $('#provincia').val());
   		resultado = true;
   	}
   	if ($('#comarca').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','codcomarca', $('#comarca').val());
   		resultado = true;
   	}
   	if ($('#termino').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','codtermino', $('#termino').val());
   		resultado = true;
   	}
   	if ($('#subtermino').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','subtermino', $('#subtermino').val());
   		resultado = true;
   	}
   	if ($('#capital').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','tipoCapital.codtipocapital', $('#capital').val());
   		resultado = true;
   	}
   	if ($('#tplantacion').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalle','tipoPlantacion.codtipoplantacion', $('#tplantacion').val());
   		resultado = true;
   	}
	return resultado;
}
    	
function limpiar(){
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	$('#btnModificar').hide();
	
	$('#modulo').val('');
	$('#cicloCultivo').val('');
	$('#desciclocultivo').val('');
	$('#sistemaCultivo').val('');
	$('#dessistemaCultivo').val('');
	$('#cultivo').val('');
	$('#desc_cultivo').val('');
	$('#variedad').val('');
	$('#desc_variedad').val('');
	$('#provincia').val('');
	$('#desc_provincia').val('');
	$('#comarca').val('');
	$('#desc_comarca').val('');
	$('#termino').val('');
	$('#desc_termino').val('');
	$('#subtermino').val('');
	$('#capital').val('');
	$('#desc_capital').val('');
	$('#tplantacion').val('');
	$('#desc_tplantacion').val('');
	
	$("#vieneDeConsultar").val('true');
	
	//DAA 08/02/2013
	$('#marcaTodos').val('false');
	$('#listaIdsMarcados').val('');
	$("input[type=checkbox]").each(function(){
		$(this).attr('checked',false);
	});

	jQuery.jmesa.removeAllFiltersFromLimit('consultaClaseDetalle');
	onInvokeAction('consultaClaseDetalle','clear');			
}

function editar(id,modulo,cicloCultivo,sistemaCultivo,cultivo,variedad,provincia,comarca,termino,subtermino,
				desCicloCultivo,desSistCult,desCultivo,desVariedad,capital,desCapital,tPlantacion,desTPlantacion){
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	var frm = document.getElementById('main3');
	frm.target="";
	frm.id.value=id;
	frm.modulo.value=modulo;
	frm.cicloCultivo.value = cicloCultivo;
	frm.sistemaCultivo.value = sistemaCultivo;
	frm.cultivo.value =cultivo; 		
	frm.variedad.value = variedad;
	frm.provincia.value = provincia;
	frm.comarca.value = comarca;
	frm.termino.value = termino;
	frm.subtermino.value = subtermino;
	frm.desciclocultivo.value = desCicloCultivo;
	frm.dessistemaCultivo.value = desSistCult;
	frm.desc_cultivo.value = desCultivo;
	frm.desc_variedad.value = desVariedad;
	frm.capital.value = capital;
	frm.desc_capital.value = desCapital;
	frm.tplantacion.value = tPlantacion;
	frm.desc_tplantacion.value = desTPlantacion;
	
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
    $.get('claseDetalle.run?origenLlamada='+$('#origenLlamada').val()+'&vieneDeConsultar='+$('#vieneDeConsultar').val()+'&detalleid='+$('#detalleid').val()+ '&ajax=true&' + parameterString, function(data) {
        $("#grid").html(data);
        comprobarChecks();
		});
}

function borrar(id) {
	$('#main3').validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar el detalle?')){
		$("#method").val("doBorrar");
		$("#id").val(id);
		$("#main3").submit();
	}	
}
function alta() {
	comprobarCampos();
	$("#method").val("doAlta");
	$("#main3").submit();	
}

//DAA 05/02/13 cambioMasivo(), listaCheckId(), marcarTodos(), comprobarChecks
function cambioMasivo(){
	limpiarCambioMasivoClaseDetalle();
	var listaIdsMarcados = $('#listaIdsMarcados').val();
	
	if(listaIdsMarcados.length>0){
		$('#listaIdsMarcados_cm').val(listaIdsMarcados);
		$('#overlayCambioMasivo').show();
		$('#panelCambioMasivoClaseDetalle').show();
	}
	else{
		 $("#panelInformacion").show();
		 showPopUpAviso("Debe seleccionar como mínimo un detalle.");
	}	
}

function listaCheckId(id){
	var listaIdsMarcados = "";
	var listaFinalIds = "";
	var cadena=[];
	
	if($('#check_' + id).attr('checked')==true){
		listaIdsMarcados = $('#listaIdsMarcados').val() + id +",";
		$('#listaIdsMarcados').val(listaIdsMarcados);
	}else{
		listaIdsMarcados = $('#listaIdsMarcados').val();
		cadena= listaIdsMarcados.split(",");
		
		for (var i=0;i<cadena.length -1;i++){
			if(cadena[i]!=id){
				listaFinalIds = listaFinalIds + cadena[i] + ",";
			}		
		}
		$('#listaIdsMarcados').val(listaFinalIds);
		$('#marcaTodos').val('false');
		comprobarChecks();	
	}
}

function marcarTodos(){
	if($('#checkTodos').attr('checked')==true){
		var listaIdsTodos = $("#listaIdsTodos").val();
		$('#listaIdsMarcados').val(listaIdsTodos);
		$('#marcaTodos').val('true');
		comprobarChecks();	
	}
	else{
		$('#listaIdsMarcados').val('');
		$('#marcaTodos').val('false');
		$("input[type=checkbox]").each(function(){
			$(this).attr('checked',false);
		});
	}	
		
}
   
function comprobarChecks(){

	var listaIdsMarcados = $('#listaIdsMarcados').val();
	var cadena=[];
	cadena= listaIdsMarcados.split(",");
	
	if (listaIdsMarcados.length>0){
		$("input[type=checkbox]").each(function(){
			if( $("#marcaTodos").val() == "true" ){
				if($(this).attr('id') != "checkTodos"){
					$(this).attr('checked',true);
				}
			}
			else{
				for (var i=0;i<cadena.length -1;i++){
					var idcheck = "check_" + cadena[i];
					if($(this).attr('id') == idcheck){
						$(this).attr('checked',true);
					}			
				}	
			}
		});
	}
	if($('#marcaTodos').val()=="true"){
		$('#checkTodos').attr('checked',true);
	}else{
		$('#checkTodos').attr('checked',false);
	}
}	
	