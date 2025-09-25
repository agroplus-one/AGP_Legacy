$(document).ready(function(){
	var URL = UTIL.antiCacheRand(document.getElementById("main8").action);
	document.getElementById("main8").action = URL;  
	if ($("#tipo").val() == 'C' || $("#tipo").val() == 'U')  {
		$("#btnCargaAutomatica").css("display", "inline");
	}
//Titulo
getTitulo();
//----------------------------
//Validaciones
$('#main8').validate({
	errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
	 highlight: function(element, errorClass) {
	 	$("#campoObligatorio_" + element.id).show();
	     },
		 unhighlight: function(element, errorClass) {
		$("#campoObligatorio_" + element.id).hide();
		 },  	
	 rules: {
	 	"file": {validarFile: true},
	 	"fechaCarga":{dateITA: true},
	 	"fechaEmision":{dateITA: true},
	 	"fechaAceptacion":{dateITA: true},
	 	"fechaCierre":{dateITA: true}
	 },
	 messages: {
	 	"file": {validarFile: "El nombre del fichero no puede tener mas de 30 caracteres"},
	 	"fechaCarga":{ dateITA: "El formato del campo Fecha de Carga debe ser dd/mm/YYYY"},
	 	"fechaEmision":{dateITA: "El formato del campo Fecha de Emisi\u00F3n debe ser dd/mm/YYYY"},
	 	"fechaAceptacion":{dateITA: "El formato del campo Fecha de Aceptaci\u00F3n debe ser dd/mm/YYYY"},
	 	"fechaCierre":{dateITA: "El formato del campo Fecha de Cierre debe ser dd/mm/YYYY"}
	 }

});

jQuery.validator.addMethod("validarFile", function(value, element, params) { 
	//return $('.file').val().split('\\').pop().length <= 5;	
	return (value.split('\\').pop().length <= 30);
});
$("#upload_bar").show();
$("#upload_bar").progressBar({barImage: 'jsp/img/progressbg_black.gif',boxImage: 'jsp/img/progressbar.gif', value: 0});

//-----------------------------
	
$("input[type=file]").filestyle({ 
		image: "jsp/img/boton_examinar.png",
		imageheight : 22,
		imagewidth : 82,
		width : 250
	});
	
	
//Fechas
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
        inputField        : "fechaCarga",
        button            : "btn_fecha_carga",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
});	
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
    inputField        : "fechaAceptacion",
    button            : "btn_fecha_aceptacion",
    ifFormat          : "%d/%m/%Y",
    daFormat          : "%d/%m/%Y",
    align             : "Br"			        	        
	});
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
    inputField        : "fechaCierre",
    button            : "btn_fecha_cierre",
    ifFormat          : "%d/%m/%Y",
    daFormat          : "%d/%m/%Y",
    align             : "Br"			        	        
	});
});
	//----------------------------
function getTitulo(){
	var tipo = $("#tipo").val();
	var titulo = "";
	if (tipo == 'I'){
		titulo = "Importacion ficheros de impagados 2015+";
	}else if(tipo == 'C' || tipo == 'U'){
		titulo = "Importacion ficheros de comisiones 2015+";
	}else if(tipo == 'D'){
		titulo = "Importacion ficheros de deuda aplazada";
	}
	else{
		titulo = "Importacion ficheros";
	}
	
	$("#titulo").html(titulo);
}

function ajaxFileUpload(){
	if ($('#main8').valid()){
		$('#progressbar_popup').fadeIn('normal');
		$('#overlay').show();
		showUploadProgress();
		$.ajaxFileUpload({
				url:'importacionComisionesUnificadas.html?method=doCargar&tipo='+	$('#tipo').val() , 
				secureuri:false,
				fileElementId:'file',
				dataType: 'json',
				success: function (data, status){
					$('#idFichero').val(data.id);
				},
				error: function (data, status, e){
					
				}
		});
	}
}   


function cargaAutomatica() {
	
	jConfirm('\u00BFEst\u00E1 seguro de que desea cargar el \u00FAltimo fichero disponible?', 'Di\u00E1logo de Confirmaci\u00F3n', function(r) {
		if (r){
			$('#progressbar_popup').fadeIn('normal');
			$('#overlay').show();
			showUploadProgress();
			$.ajax({
					url:'cargaComisiones.html?tipo='+$("#tipo").val(), 
					secureuri:false,
					fileElementId:'file',
					dataType: 'json',
					success: function (data, status){
						$('#idFichero').val(data.id);
					},
					error: function (data, status, e){
						
					},
			});
		}
	});
}


function showUploadProgress() {
	var uploadInfo = function getUploadInfo() {
		$.ajax({
			url: 'subidaInfo.html',
			type: "POST",
			cache: false,
			dataType: "json",
			async: false,
			success: function(msg){
				if(msg.result == 'UPLOADING') {
					var progress = msg.progress;
					$("#upload_bar").progressBar(progress);
				} else if(msg.result == 'DONE') {
					$("#upload_bar").progressBar(msg.progress);
					clearInterval(uploadInfoHandle);
					$("#uploadOK").show();
					if (msg.texto) {
						$('#panelInformacionImportacion').html(msg.texto);
					}
					$('#panelInformacionImportacion').show();
					$('#mensaje').hide();
					$('#divbot').show();
				} else if(msg.result == 'WARN') {
					$("#upload_bar").progressBar(msg.progress);
					clearInterval(uploadInfoHandle);
					$("#uploadOK").show();
					$('#panelInformacionImportacion').css("color","orange");
					$('#panelInformacionImportacion').html('Importaci\u00F3n completada con errores');
					$('#panelInformacionImportacion').show();
					$('#mensaje').hide();
					$('#divbot').show();
				} else if(msg.result == 'FAILED'){
					$("#uploadKO").show();
					clearInterval(uploadInfoHandle);
					$('#panelAlertasImportacion').show();
					$('#mensaje').hide();
					$('#divbot').show();
				} else if(msg.result == 'DUPLICADO'){
					$("#uploadKO").show();
					clearInterval(uploadInfoHandle);
					$('#panelAlertasImportacion').html('Fichero importado duplicado');
					$('#panelAlertasImportacion').show();
					$('#mensaje').hide();
					$('#divbot').show();
				}
			}
		});
	};
	var uploadInfoHandle = setInterval(uploadInfo, 2000);

}


function cerrarPopUp(){

		var id 	= $('#idFichero').val();

		if(id==""){
			//Es porque ha habido un error
			consultar();
			
		}else{
			//Si ha devuelto id, es que se ha procesado correctamente
 		var tipo 	= $("#tipo").val();
 		var estadoFichero 	= $("#estadoFichero").val();
 		//En realidad, tipo y estadoFichero no son relevantes
 		//revisar(id, tipo, estadoFichero);
 		revisar(id);
		}
 }
 
function cerrarPopUpError(){
 	$('#progressbar_popup').fadeOut('normal');
	$('#overlay').hide();
}

function cerrarPopUpContenido(){
    $('#divContenidoFichero').fadeOut('normal');
	$('#overlay').hide();
}

function consultar(){
	var frm = document.getElementById('main8');
	frm.target="";
	//$("#main8").validate().cancelSubmit = false;
	$('#method').val('doConsulta');
	$('#origenLlamada').val('consultar');
	frm.limpiar.value = "limpiar";
	/*$('#main8').submit();*/
	frm.submit();	
}


//function addFilterToLimit(){
//	jQuery.jmesa.removeAllFiltersFromLimit('consultaFicheroUnificado');
//	var tipo = $("#tipo").val();
//   	
//   	if ($('#nombreFichero').val() != ''){
//   		jQuery.jmesa.addFilterToLimit('consultaFicheroUnificado','nombreFichero', $('#nombreFichero').val());   		   		
//   	}
//   	jQuery.jmesa.addFilterToLimit('consultaFicheroUnificado','tipoFichero', tipo)
//	
//			
//	if ($('#tx_fecha_carga').val() != ''){
//		jQuery.jmesa.addFilterToLimit('consultaFicheroUnificado','fechaCarga', $('#tx_fecha_carga').val());
//	}
//	if ($('#tx_fecha_aceptacion').val() != ''){
//		jQuery.jmesa.addFilterToLimit('consultaFicheroUnificado','fechaAceptacion', $('#tx_fecha_aceptacion').val());
//	}
//	   
//	if ($('#estado').val() != ''){
//		jQuery.jmesa.addFilterToLimit('consultaFicheroUnificado','estado', $('#estado').val());
//	}
//	 
//	if ($('#tx_fecha_cierre').val() != ''){
//		jQuery.jmesa.addFilterToLimit('consultaFicheroUnificado','fechaCierre', $('#tx_fecha_cierre').val());
//	}
//   	
//}

function revisar(id){
	$('#progressbar_popup').hide();
	$('#overlay').hide();
	//addFilterToLimit();
	var url="incidenciasUnificado.run?origenLlamada=revisarFicheroUnificado&idFicheroUnificado="+id+"&rand=" + getRand();
	location.href = url;
	
//	$('#revisar_method').val('doConsulta');
//	$('#revisar_idFichero').val(id);
//	$('#revisarForm').submit();				
}


function borrar(id){
	if(confirm('¿Est00E1 seguro de que desea eliminar el registro seleccionado?')){
		$('#method').val('doBorrarFichero');
		$('#idFichero').val(id);
		$.blockUI.defaults.message = '<h4> Eliminando el fichero seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#main8').submit();
	}
}

function descargar(id){
	$('#verFich_method').val('doDescargarFichero');
	$('#verFich_idFichero').val(id);
	$('#verFich').submit();				
}


function limpiar(){	
	$("#file").val('');
	$("#nombreFichero").val('');
	$("#estado").val('');
	$("#fechaCarga").val('');
	$("#fechaAceptacion").val('');
	$("#fechaCierre").val('');
		
	limpiaAlertas();
	$('#origenLlamada').val('menuGeneral');
	$('#method').val('doConsulta');
	$("#main8").submit();	
}

function limpiaAlertas() {
	$('#alerta').val("");
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();

	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');
	$("#panelAlertas").html('');

}

function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main8');
	$.get('importacionComisionesUnificadas.html?ajax=true&tipo=' + $("#tipo").val() + '&'  +decodeURIComponent(parameterString),
		function(data) {
			$("#grid").html(data);
		});
}
