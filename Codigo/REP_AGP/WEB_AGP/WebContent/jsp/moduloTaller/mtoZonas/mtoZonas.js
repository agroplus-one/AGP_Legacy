$(document).ready(function(){
    $("#btnModificar").hide();
    $('#main3').validate({
        onfocusout: function(element) {
            var frm = document.getElementById('main3');
            if ( (frm.method.value == "doEditar") || (frm.method.value == "doAlta") ) {
                this.element(element);
            }
        },
        errorLabelContainer: "#panelAlertasValidacion",
        wrapper: "li",
        highlight: function(element, errorClass) {
             $("#campoObligatorio_" + element.id).show();
         },
         unhighlight: function(element, errorClass) {
            $("#campoObligatorio_" + element.id).hide();
         },
        rules: {
            "id.codentidad":{required: true},
            "id.codzona":{required: true},
            "nomzona": {required: true}
        },
         messages: {
             "id.codentidad":{required: "El campo Entidad es obligatorio"},
             "id.codzona":{required: "El campo C�digo Zona  es obligatorio"},
             "nomzona": {required: "El campo Nombre Zona es obligatorio"}
         }
    });
    
    
    showOrHideExportIcon();

    
});

function alta() {
	limpiaAlertas();
	var frm = document.getElementById('main3');
	if ($("#main3").valid()){
		frm.method.value= "doAlta";
		$("#main3").submit();
	}
}

function limpiarDesc(){
	if ($('#codzona').val() == ""){
		$('#nomzona').val("");
	}
}

  

function modificar() {
	limpiaAlertas();
	var frm = document.getElementById('main3');

	if ($('#main3').valid()) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });

		frm.method.value = "doEditar";
		$('#main3').submit();

	}
}

function alta() {
	limpiaAlertas();
	var frm = document.getElementById('main3');
	if ($("#main3").valid()){
		frm.method.value= "doAlta";
		$("#main3").submit();
	}
}

function limpiar () {
	
	$('#entidad').val("");
	$('#desc_entidad').val("");
	$('#codzona').val("");
	$('#nomzona').val("");
	
	$("#limpiar").submit();
	
}

// Consultar Zona
function consultar(){
	
	limpiaAlertas(); 
	var frm = document.getElementById('main3');
	if (validarConsulta()){
		$("#btnModificar").hide();
		
		var validar = validarFiltroConsulta();

		if (validar){
			frm.method.value= "doConsulta";
			frm.origenLlamada.value= "primeraBusqueda";
			$("#main3").validate().cancelSubmit = true;
			$("#main3").submit();
		}else {
			avisoUnCampo ();
		}
	}
}

//Borrar Zona
function borrar(codEntidad, codZona) {
	limpiaAlertas();
	
	if(confirm('�Est� seguro de que desea eliminar la zona seleccionada?')){
		$("#methodBorrar").val("doBorrar");
		$("#codEntidadBorrar").val(codEntidad);
		$("#codZonaBorrar").val(codZona);
		$("#frmBorrar").submit();
	}	
}

// Editar Zona
function editar(codentidad, nombentidad, codzona, nomzona) {
	limpiaAlertas();
	
	var frm = document.getElementById('main3');
	
	frm.entidad.value = codentidad;
	frm.desc_entidad.value = nombentidad;
	frm.codzona.value = codzona;
	frm.nomzona.value = nomzona;
	
	frm.codentidadInicial.value = codentidad;
	frm.codzonaInicial.value = codzona;
	
	$('#btnModificar').show();
}

//Muestra el aviso que indica que hay que filtrar por al menos un campo
function avisoUnCampo () {
	$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo");
	$('#panelAlertasValidacion').show();
}

function validarConsulta() {
	// Valida el campo 'entidad' si esta informado
	if ($('#entidad').val() != ''){
		var entidadOk = false;
		try {
			var auxentidad =  parseFloat($('#entidad').val());
			if(!isNaN(auxentidad)){
				$('#entidad').val(auxentidad);
				entidadOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!entidadOk) {
			$('#panelAlertasValidacion').html("Valor para la entidad no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	// Valida el campo 'codzona' si esta informado		 	
 	if ($('#codzona').val() != ''){ 
 		
	 	var codzonaOk = false;
	 	try {		 	
	 		var valor =  parseFloat($('#codzona').val());
	 		if(!isNaN(valor)){
				$('#codzona').val(valor);
				codzonaOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validacion muestra el mensaje
		if (!codzonaOk) {
			$('#panelAlertasValidacion').html("Valor para el campo Codigo de Zona no v�lido");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
 	
	return true;
}

function validarFiltroConsulta() {
	
	var entidadOK = false;
	var codzonaOK = false;
	var nomzonaOK = false;
	// Valida el campo 'entidad' si esta informado
	if ($('#entidad').val() != ''){
		entidadOK = true;
	}
	
	if ($('#codzona').val() != ''){
		codzonaOK = true;
	}
	
	if ($('#nomzona').val()!= ''){
		nomzonaOK = true;
	}
	
	if (nomzonaOK == true || codzonaOK == true  || entidadOK ==true){
		return true;
	}else{
		return false;
	}
}
function limpiaAlertas() {
	$('#alerta').val("");
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');	
	$("#panelAlertas").html('');
	
	$('#listaIdsMarcados').val('');
	
}

function onInvokeAction(id) {
    
    var to=document.getElementById("adviceFilter");
    to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
       var frm = document.getElementById('main3');
    $.get('mtoZonas.run?ajax=true&' + decodeURIComponent(parameterString), function(data) {
        $("#grid").html(data);
        //comprobarChecks();
        showOrHideExportIcon();
        });
}

function showOrHideExportIcon() {
    var table = document.getElementById("consultaZonas");
    var matchingRow = table.querySelector("tr[id^='consultaZonas']");

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
