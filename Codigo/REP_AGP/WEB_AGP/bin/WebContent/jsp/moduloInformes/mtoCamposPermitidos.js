$(document).ready(function(){
	
	habilitaVista();
	if ($('#origenLlamada').val() == 'borrar'){
		$('#origenLlamada').val('');
	}
	if ($('#idCampoPermitido').val() != null && $('#idCampoPermitido').val() != ''){
		var modif = document.getElementById('btnModificar');
		modif.style.display = "";
	}
    var URL = UTIL.antiCacheRand(document.getElementById("main").action);
    document.getElementById("main").action = URL;
    $('#main').validate({	
		
		onfocusout: function(element) {
			if ( ($('#method').val() == "doEdita") || ($('#method').val() == "doAlta") ) {
				this.element(element);
			}
		},
								
		errorLabelContainer: "#panelAlertasValidacion",
				wrapper: "li",
		
		rules: {
			"vistaCampo.tablaOrigen":{required: true},
			"vistaCampo.nombre":{required: true},
			"vistaCampo.tipo":{required: true}
		},
		messages: {
			"vistaCampo.tablaOrigen":{required: "El campo TablaOrigen es obligatorio"},
			"vistaCampo.nombre":{required: "El campo Campo es obligatorio"},
			"vistaCampo.tipo":{required: "El campo Tipo es obligatorio"}
	 	}
	});
});

function habilitaVista(){
	$('#tablaOrigen').val($('#idVista').val());
}


function onInvokeAction(id) {
	limpiaAlertas();
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main');
    $.get('mtoCamposPermitidos.run?ajax=true&origenLlamada='+frm.origenLlamada.value+'&descripcion='+frm.descripcion.value+'&tablaOrigen='+frm.tablaOrigen.value+'&' + parameterString, function(data) {
        $("#grid").html(data)
 			});
}

function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
}

function limpiar(){
	limpiaAlertas();
	var frm = document.getElementById('main');
	$('#origenLlamada').val('menuGeneral');
	$("#btnModificar").hide();	
	$('#descripcion').val('');
	$('#tablaOrigen').val('');
	$('#campo').val('');
	$('#tipo').val('');
	var modif = document.getElementById('btnAlta');
	modif.style.display = "";
	obj = document.getElementById('tipo');
	obj.disabled = false;
	consultar();
}

function consultar(){
	$('#origenLlamada').val('');
	var modif = document.getElementById('btnModificar');
	modif.style.display = "none";
	limpiaAlertas();
	comprobarCampos();
	onInvokeAction('mtoCamposPermitidos','filter');
}

function comprobarCampos(){
	jQuery.jmesa.removeAllFiltersFromLimit('mtoCamposPermitidos');
 	if ($('#descripcion').val() != ''){
   		//jQuery.jmesa.addFilterToLimit('mtoCamposPermitidos','descripcion', $('#descripcion').val());
 	}
 	if ($('#tablaOrigen').val() != ''){
		//jQuery.jmesa.addFilterToLimit('mtoCamposPermitidos','vistaCampo.tablaOrigen', $('#tablaOrigen').val());
 	}
 	if ($('#campo').val() != ''){
		jQuery.jmesa.addFilterToLimit('mtoCamposPermitidos','vistaCampo.nombre', $('#campo').val());
 	} 
 	if ($('#tipo').val() != ''){
		jQuery.jmesa.addFilterToLimit('mtoCamposPermitidos','vistaCampo.vistaCampoTipo.idtipo', $('#tipo').val());
 	}
}

function alta(){
	$('#origenLlamada').val('');
	limpiaAlertas();
    var frm = document.getElementById('main');
    frm.method.value = 'doAlta';
    actualizarParametros($('#idVistaCampo').val(),$('#tablaOrigen').val());
 	$('#main').submit();
   
}

function modificar(){
	$('#origenLlamada').val('');
	limpiaAlertas();
	var frm = document.getElementById('main');
	frm.method.value = 'doEdita';
	actualizarParametros();
	$('#main').submit();
	
}	

function actualizarParametros(idVistaCampo, idVista){
	// actualizamos parametros a los hidden
	$('#idVistaCampo').val(idVistaCampo);
	$('#idVista').val(idVista);
}

function editar(idCamp, descripcion, idVistaCampo, idVista, tablaOrigen, tipo, campo){
	limpiaAlertas();
	$("#idCampoPermitido").val(idCamp);	
	$('#descripcion').val(descripcion);
	$('#tablaOrigen').val(idVista);
	$('#campo').val(campo);
	$("#tipo").val(tipo);

	var modif = document.getElementById('btnModificar');
	modif.style.display = "";
	var modif = document.getElementById('btnAlta');
	modif.style.display = "";
	actualizarParametros(idVistaCampo, idVista);
	actualizaTipo();
}

function visualizar(idCamp, descripcion, idVistaCampo, idVista, tablaOrigen, tipo, campo){
	limpiaAlertas();
	$("#idCampoPermitido").val('');
	$('#descripcion').val(descripcion);
	$('#tablaOrigen').val(idVista);
	$('#campo').val(campo);
	$("#tipo").val(tipo);
	var modif = document.getElementById('btnModificar');
	modif.style.display = "none";
	var modif = document.getElementById('btnAlta');
	modif.style.display = "none";
	actualizarParametros(idVistaCampo, idVista);
	actualizaTipo();
}




function actualizaTipo(){
	obj = document.getElementById('tipo');
	obj.disabled = true;
	if ($('#campo').val() == ''){
		$("#tipo").val('');
	}
}

function vaciaCampo(){
	$('#campo').val('');
	obj = document.getElementById('tipo');
	obj.disabled = false;
	$("#tipo").val('');
}


function borrar(id){
	$('#origenLlamada').val('');
	$('#main').validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar este Campo Permitido?')){
		var frm = document.getElementById('main');
		frm.method.value = 'doBaja';
		frm.tOrigen.value = frm.tablaOrigen.value;
		frm.idCampoPermitido.value = id;				
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#main").submit();	
	}	
}