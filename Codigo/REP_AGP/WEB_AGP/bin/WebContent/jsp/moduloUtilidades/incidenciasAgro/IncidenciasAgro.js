$(document).ready(function() {
	
	/* Pet. 57627 ** MODIF TAM (13.11.2019) ** Inicio */
	
	var tipoinc = document.getElementById('tipoinc').value;
	
	if (tipoinc == 'A' || tipoinc == 'R'){
		// ocultamos la lista de asuntos y mostramos la de motivos (Anulaciones y Rescisiones)
		document.getElementById("asunto").style.display = 'none';
		document.getElementById("nombAsunto").style.display='none';
		
		document.getElementById("nombMotivo").style.display='inline';
		document.getElementById("listmotivo").style.display = 'inline';

	}else{
		// ocultamos la lista de motivos y mostramos la de asuntos (Incidencias)
		document.getElementById("nombMotivo").style.display='none';
		document.getElementById("listmotivo").style.display = 'none';
		
		document.getElementById("asunto").style.display = 'inline';
		document.getElementById("nombAsunto").style.display='inline';
		
	}
	/* Pet. 57627 ** MODIF TAM (13.11.2019) ** Inicio */
	
	var URL = UTIL.antiCacheRand(document.getElementById("formIncidencias").action);
    document.getElementById("formIncidencias").action = URL;    
        
    Zapatec.Calendar.setup({
		firstDay : 1,
		weekNumbers : false,
		showOthers : true,
		showsTime : false,
		timeFormat : "24",
		step : 2,
		range : [ 1900.01, 2999.12 ],
		electric : false,
		singleClick : true,
		inputField : "fechaEnvioDesdeId",
		button : "btn_fechaEnvioDesde",
		ifFormat : "%d/%m/%Y",
		daFormat : "%d/%m/%Y",
		align : "Br"
	});

	Zapatec.Calendar.setup({
		firstDay : 1,
		weekNumbers : false,
		showOthers : true,
		showsTime : false,
		timeFormat : "24",
		step : 2,
		range : [ 1900.01, 2999.12 ],
		electric : false,
		singleClick : true,
		inputField : "fechaEnvioHastaId",
		button : "btn_fechaEnvioHasta",
		ifFormat : "%d/%m/%Y",
		daFormat : "%d/%m/%Y",
		align : "Br"
	});
    
	$('#formIncidencias').validate({	
		
		// taty
		//$('#panelAlertasValidacion').hide();
		
		errorLabelContainer: "#panelAlertasValidacion",
		
		onfocusout: function(element) {
			if ($('#formIncidencias:input[name="method"]').val() == "doConsulta") {
				this.element(element);
			}else{
				this.element(element);
			}
		},				
		 
		wrapper: "li",
		
		highlight: function(element, errorClass) {
		 	$("#campoObligatorio_" + element.id).show();
	    },
	    
		unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		},
		
		rules: {
			"codentidad":{validarEnt:true, digits: true, range: [0, 9999]},
			"oficina":{digits: true, range: [0, 9999]},
			"codplan":{digits: true, range: [0, 9999]},
			"codlinea":{digits: true, range: [0, 9999]},
			"fechaEnvioHastaId":{validaFechaEnvioHasta:['fechaEnvioDesdeId','fechaEnvioHastaId']}
		},
		messages: {
			"codentidad":{validarEnt:"La Entidad seleccionada no pertenece al grupo de Entidades del usuario", digits: "El campo Entidad s\u00F3lo puede contener d\u00EDgitos", range: "El campo Entidad s\u00F3lo puede contener d\u00EDgitos entre 0 y 9999"},
			"oficina":{digits: "El campo Oficina s\u00F3lo puede contener d\u00EDgitos", range: "El campo Oficina s\u00F3lo puede contener d\u00EDgitos entre 0 y 9999"},
			"codplan":{digits: "El campo Plan s\u00F3lo puede contener d\u00EDgitos", range: "El campo Plan s\u00F3lo puede contener d\u00EDgitos entre 0 y 9999"},
			"codlinea":{digits: "El campo L\u00EDnea s\u00F3lo puede contener d\u00EDgitos", range: "El campo L\u00EDnea s\u00F3lo puede contener d\u00EDgitos entre 0 y 9999"},
			"fechaEnvioHastaId":{validaFechaEnvioHasta:"La Fecha Hasta no puede ser anterior a la Fecha Desde"}
		}
	});
	
	showOrHideExportIcon();
	
});

//Pet. 50775 ** MODIF TAM (16.05.2018) ** Inicio //
jQuery.validator.addMethod("validarEnt", function(value, element, params) {  
	var codentidad = $('#entidad').val();
	if($('#grupoEntidades').val() == ""){

		return true;
	}else if (codentidad != ""){
		var grupoEntidades = $('#grupoEntidades').val().split(',');
		var encontrado = false;
		for(var i=0;i<grupoEntidades.length;i++){
			if(grupoEntidades[i] == codentidad){
				encontrado = true;
				break;
			}
		}
	}else{
		return true;
	}
		
	return 	encontrado;	
});	
//Pet. 50775 ** MODIF TAM (16.05.2018) ** Fin //

// comprueba que la fecha Envio Hasta sea posterior a la primera
jQuery.validator.addMethod("validaFechaEnvioHasta", function(value, element, params) {
	if(document.getElementById(params[1]).value != null && document.getElementById(params[1]).value != ""){
		//alert("params[0] " + document.getElementById(params[0]).value);
		//alert("params[1] " + document.getElementById(params[1]).value);
		if (document.getElementById(params[0]).value != document.getElementById(params[1]).value){
			return (!UTIL.fechaMayorOIgualQue(document.getElementById(params[0]), document.getElementById(params[1])));
		}else{
			return true;
		}		
	}else{
		return true;
	}
});



function comprobarCampos(incluirJmesa) {	
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('listaIncidenciasAgro');
	}
	
	var resultado = false;
	if (!resultado && $('#entidad').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#oficina').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#plan').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#linea').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#entmediadora').val() != ''){
		resultado = true;
	} 
	if (!resultado && $('#subentmediadora').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#delegacion').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#codestado').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#codestadoagro').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#nifcif').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#tiporef').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#idcupon').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#asunto').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#fechaEnvioDesdeId').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#fechaEnvioHastaId').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#referencia').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#numero').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#codusuario').val() != ''){
		resultado = true;
	}
	if (!resultado && $('#tipoinc').val() != ''){
		resultado = true;
	}
	
   	return resultado;
}

function consultarInicial() {
	
	$('#origenLlamada').val('');
	limpiaAlertas();		
	if (comprobarCampos(false)) {
		$('#formIncidencias input[name="method"]').val('doConsulta');	
		if($("#formIncidencias").valid()){
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$('#formIncidencias').submit();
		}
	} else {
		$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo o el contenido de alguno de los campos de filtro no es v\u00E1lido");
		$('#panelAlertasValidacion').show();
	}
}

function consultar() {
	$('#origenLlamada').val('');
	limpiaAlertas();	
	$('#formIncidencias input[name="method"]').val('doConsulta');
	if (comprobarCampos(true)) {
		onInvokeAction('listaIncidenciasAgro', 'filter');
	} else {
		$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo o el contenido de alguno de los campos de filtro no es v\u00E1lido");
		$('#panelAlertasValidacion').show();
	}
}

function borrar(id, tipo_inc) {

	$('#origenLlamada').val('');
	limpiaAlertas();
	var tipo_inc_aux ='';
	
	
	if (tipo_inc =="Incidencia"){
		tipo_inc_aux ='I';
	}else{
		tipo_inc_aux ='A';
	}
	
	var mensaje_aux = '';
	if (tipo_inc_aux =='I'){
		mensaje_aux = 'la incidencia';
	}else{
		mensaje_aux = 'la Anulaci\u00F3n/Rescisi\u00F3n';
	}
	
	if (confirm('Est\u00E1 a punto de eliminar ' + mensaje_aux +'. \u00BFEst\u00E1 seguro?')) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#formIncidencias input[name="method"]').val('doBorrar');
		$('#idincidencia').val(id);
		$('#tipoincBorrado').val(tipo_inc_aux);
		$('#formIncidencias').validate().cancelSubmit = true;
		$('#formIncidencias').submit();
	} 
}


function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('formIncidencias');
	$.get('utilidadesIncidencias.run?ajax=true&origenLlamada='
			+ frm.origenLlamada.value + '&fechaEnvioDesdeId='
			+ frm.fechaEnvioDesdeId.value + '&fechaEnvioHastaId='
			+ frm.fechaEnvioHastaId.value + '&' + parameterString, function(
			data) {
		$("#grid").html(data);
		showOrHideExportIcon();
	});
}

function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('formIncidencias');
	location.href = 'utilidadesIncidencias.run?ajax=false&excel=true&' + parameterString
				+ '&origenLlamada=' + frm.origenLlamada.value 
				+ '&fechaEnvioDesdeId=' + frm.fechaEnvioDesdeId.value 
				+ '&fechaEnvioHastaId='	+ frm.fechaEnvioHastaId.value;
}

function limpiar(){
	$('#formLimpiar').submit();				
}

function limpiaAlertas() {
	$('#panelAlertasValidacion').html('');
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	$("label[id^='campoObligatorio_']").each(function(){
		$(this).hide();
	});	
}

function marcar_todos(){
						
	var frm = document.getElementById('formIncidencias');			
	$("input[type=checkbox]").each(function() { 				        		    
    	$(this).attr('checked',true);
	});
	frm.checkTodo.value="true";
	frm.idsRowsChecked.value=frm.polizasString.value;
}
		
function desmarcar_todos() {
	
	var frm = document.getElementById('formIncidencias');
	if(frm.checkTodo.value =="true"){
		$("input[type=checkbox]").each(function() { 				        		    
    		$(this).attr('checked',false);
  		});
  		
	    frm.checkTodo.value="false";
	    frm.idsRowsChecked.value="";
	}
}

function editar(idInc){
	
	$('#incidenciaIdEditar').val(idInc);
	//(13.06.2018)
	$('#plan').val($('#plan').val());
	$('#linea').val($('#linea').val());
	$('#referencia').val($('#referencia').val());
	$('#nifcif').val($('#nifcif').val());
	$('#idcupon').val($('#idcupon').val());
	// (18.06.2018)- asignamos valor a los campos de consulta por que los utilizaremos para
	// el boton cancelar, ya que necesitamos los datos de consulta.
	$('#idincidenciaEditar').val(idInc);
	$('#entidadEditar').val($('#entidad').val());
	$('#oficinaEditar').val($('#oficina').val());
	$('#entmediadoraEditar').val($('#entmediadora').val());
	$('#subentmediadoraEditar').val($('#subentmediadora').val());
	$('#delegacionEditar').val($('#delegacion').val());
	$('#planEditar').val($('#plan').val());
	$('#lineaEditar').val($('#linea').val());
	$('#referenciaEditar').val($('#referencia').val());
	$('#numeroEditar').val($('#numero').val());
	$('#codestadoEditar').val($('#codestado').val());
	$('#codestadoagroEditar').val($('#codestadoagro').val());
	$('#nifcifEditar').val($('#nifcif').val());
	$('#tiporefEditar').val($('#tiporef').val());
	$('#idcuponEditar').val($('#idcupon').val());
	$('#asuntoEditar').val($('#asunto').val());
	$('#fechaEnvioDesdeIdEditar').val($('#fechaEnvioDesdeId').val());
	$('#fechaEnvioHastaIdEditar').val($('#fechaEnvioHastaId').val());
	$('#tipoincEditar').val($('#tipoinc').val());
	
	$('#formEditar').submit();
}	
		
function consultarAgroseguros(idInc) {
	$('#formConsultar').find('#idincidenciaConsulta').val(idInc);
	$('#entidadConsulta').val($('#entidad').val());
	$('#oficinaConsulta').val($('#oficina').val());
	$('#entmediadoraConsulta').val($('#entmediadora').val());
	$('#subentmediadoraConsulta').val($('#subentmediadora').val());
	$('#delegacionConsulta').val($('#delegacion').val());
	$('#planConsulta').val($('#plan').val());
	$('#lineaConsulta').val($('#linea').val());
	$('#referenciaConsulta').val($('#referencia').val());
	$('#numeroConsulta').val($('#numero').val());
	$('#codestadoConsulta').val($('#codestado').val());
	$('#codestadoagroConsulta').val($('#codestadoagro').val());
	$('#nifcifConsulta').val($('#nifcif').val());
	$('#tiporefConsulta').val($('#tiporef').val());
	$('#idcuponConsulta').val($('#idcupon').val());
	$('#asuntoConsulta').val($('#asunto').val());
	$('#fechaEnvioDesdeIdConsulta').val($('#fechaEnvioDesdeId').val());
	$('#fechaEnvioHastaIdConsulta').val($('#fechaEnvioHastaId').val());
	$('#codUsuarioConsulta').val($('#codusuario').val());
	$('#tipoincConsulta').val($('#tipoinc').val());
	
	$('#formConsultar').submit();
}

/* Pet. 57627 ** MODIF TAM (12.11.2019) ** Inicio */
function consultarAnulyResc(idInc) {

	$('#formConsulAnulyResc').find('#method').val("doConsultar");
	
	$('#formConsulAnulyResc').find('#idincidenciaConsulta').val(idInc);
	$('#formConsulAnulyResc').find('#idincidenciaConAyR').val(idInc);
	$('#entidadConAyR').val($('#entidad').val());
	$('#oficinaConAyR').val($('#oficina').val());
	$('#entmediadoraConAyR').val($('#entmediadora').val());
	$('#subentmediadoraConAyR').val($('#subentmediadora').val());
	$('#delegacionConAyR').val($('#delegacion').val());
	$('#planConAyR').val($('#plan').val());
	$('#lineaConAyR').val($('#linea').val());
	$('#referenciaConAyR').val($('#referencia').val());
	$('#numeroConAyR').val($('#numero').val());
	$('#codestadoConAyR').val($('#codestado').val());
	$('#codestadoagroConAyR').val($('#codestadoagro').val());
	$('#nifcifConAyR').val($('#nifcif').val());
	$('#tiporefConAyR').val($('#tiporef').val());
	$('#idcuponConAyR').val($('#idcupon').val());
	$('#asuntoConAyR').val($('#asunto').val());
	$('#fechaEnvioDesdeIdConAyR').val($('#fechaEnvioDesdeId').val());
	$('#fechaEnvioHastaIdConAyR').val($('#fechaEnvioHastaId').val());
	$('#codUsuarioConAyR').val($('#codusuario').val());
	$('#tipoincConAyR').val($('#tipoinc').val());
	
	$('#formConsulAnulyResc').submit();
}

function editarAnulyResc(idInc){	
	
	$('#formConsulAnulyResc').find('#method').val("doEditarAnulyResc");
	
	$('#formConsulAnulyResc').find('#idincidenciaConsulta').val(idInc);
	$('#formConsulAnulyResc').find('#idincidenciaConAyR').val(idInc);
	$('#entidadConAyR').val($('#entidad').val());
	$('#oficinaConAyR').val($('#oficina').val());
	$('#entmediadoraConAyR').val($('#entmediadora').val());
	$('#subentmediadoraConAyR').val($('#subentmediadora').val());
	$('#delegacionConAyR').val($('#delegacion').val());
	$('#planConAyR').val($('#plan').val());
	$('#lineaConAyR').val($('#linea').val());
	$('#referenciaConAyR').val($('#referencia').val());
	$('#numeroConAyR').val($('#numero').val());
	$('#codestadoConAyR').val($('#codestado').val());
	$('#codestadoagroConAyR').val($('#codestadoagro').val());
	$('#nifcifConAyR').val($('#nifcif').val());
	$('#tiporefConAyR').val($('#tiporef').val());
	$('#idcuponConAyR').val($('#idcupon').val());
	$('#asuntoConAyR').val($('#asunto').val());
	$('#fechaEnvioDesdeIdConAyR').val($('#fechaEnvioDesdeId').val());
	$('#fechaEnvioHastaIdConAyR').val($('#fechaEnvioHastaId').val());
	$('#codUsuarioConAyR').val($('#codusuario').val());
	$('#tipoincConAyR').val($('#tipoinc').val());

	$('#formConsulAnulyResc').submit();
}
/* Pet. 57627 ** MODIF TAM (12.11.2019) ** Fin */


function consultarListAgro(idInc) {
	
	$('#idincidenciaConsList').val(idInc);
	$('#entidadConsList').val($('#entidad').val());
	
	$('#oficinaConsList').val($('#oficina').val());
	$('#entmediadoraConsList').val($('#entmediadora').val());
	$('#subentmediadoraConsList').val($('#subentmediadora').val());
	$('#delegacionConsList').val($('#delegacion').val());
	$('#planConsList').val($('#plan').val());
	$('#lineaConsList').val($('#linea').val());
	$('#referenciaConsList').val($('#referencia').val());
	$('#numeroConsList').val($('#numero').val());
	$('#codestadoConsList').val($('#codestado').val());
	$('#codestadoagroConsList').val($('#codestadoagro').val());
	$('#nifcifConsList').val($('#nifcif').val());
	$('#tiporefConsList').val($('#tiporef').val());
	$('#idcuponConsList').val($('#idcupon').val());
	$('#asuntoConsList').val($('#asunto').val());
	$('#fechaEnvioDesdeIdConsList').val($('#fechaEnvioDesdeId').val());
	$('#fechaEnvioHastaIdConsList').val($('#fechaEnvioHastaId').val());
	$('#tipoincConsList').val($('#tipoinc').val());
	$('#formConsListAgro').submit();
}

function LanzarConsultaAgro(){
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('formIncidencias');
	location.href = 'listaIncidenciasAgro.run?method=doCargar&'
					+ '&entidadConsulta=' +frm.entidad.value;
		+ parameterString
				+ '&origenLlamada=' + frm.origenLlamada.value;
	
}

function crearNuevaIncidencia() {
	
	var frm = document.getElementById('formNuevaIncidencia');
	frm.codentidad.value = $('#entidad').val();
	frm.referenciaCons.value = $('#referencia').val();
	frm.oficina.value = $('#oficina').val();
	frm.entmediadora.value = $('#entmediadora').val();
	frm.subentmediadora.value = $('#subentmediadora').val();
	frm.delegacion.value = $('#delegacion').val();
	frm.codplan.value = $('#plan').val();
	frm.codlinea.value = $('#linea').val();
	frm.codestado.value = $('#codestado').val();
	frm.codestadoagro.value = $('#codestadoagro').val();
	frm.nifcifCons.value = $('#nifcif').val();
	frm.tiporef.value = $('#tiporef').val();
	frm.idcupon.value = $('#idcupon').val();
	frm.asunto.value = $('#asunto').val();
	frm.fechaEnvioDesdeId.value = $('#fechaEnvioDesdeId').val();
	frm.fechaEnvioHastaId.value = $('#fechaEnvioHastaId').val();
	frm.numIncidencia.value = $('#numero').val();
	frm.codUsuarioVolver.value = $('#codusuario').val();
	frm.tipoincVolver.value = $('#tipoinc').val();

	frm.submit();
}

function showOrHideExportIcon() {
	
	 var table = document.getElementById("listaIncidenciasAgro");
	    var matchingRow = table.querySelector("tr[id^='listaIncidenciasAgro']");

	    if (matchingRow) {
	        document.getElementById("divImprimir").style.display = "block";
	    } else {
	        document.getElementById("divImprimir").style.display = "none";
	    }
}

function exportToExcel(size) {
    var frm = document.getElementById('exportToExcel');
    frm.target="_blank";
    frm.submit();
}