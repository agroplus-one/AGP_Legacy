$(document).ready(function(){
	// Para evitar el cacheo de peticiones al servidor
	$("#main3").attr("action", UTIL.antiCacheRand($("#main3").attr("action")));
	//var url1 = UTIL.antiCacheRand($("#retornarAOrigenDatos").attr("action"));
	//$("#retornarAOrigenDatos").attr("action", url1);
});

//Función para volver a la elección de la carga de parcelas
function volver(){
	$('#retornarAOrigenDatos').submit();
}

//Funcion para las operaciones con el listado
function onInvokeAction(id) {
	/*var to = $("#adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
      
    $.get('polizaActualizada.run?ajax=true&idPoliza='+$("#idpoliza").val()+'&' + parameterString, function(data) {
		$("#grid").html(data)		
	});*/
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	//var frm = document.getElementById('main');
	
	$.get('polizaActualizada.run?ajax=true&origenLlamada=paginacion&nifasegurado='+$("#nifasegurado").val()+'&codplan='+$("#codplan").val()+'&codlinea='+$("#codlinea").val()+'&idpoliza='+$("#idpoliza").val()+'&'+decodeURIComponent(parameterString),
			function(data) {
				$("#grid").html(data);

			});
}

//Función para lanzar la carga de parcelas de la póliza seleccionada en el listado
function cargar(){
	$('#overlay').hide();
	var valorRadio = $('input:radio[name=idRadios]:checked').val();
	
	if (valorRadio != "" && valorRadio != undefined){
		//Guardamos el valor marcado en el listado de polizas
		$("#idPolSeleccionada").val(valorRadio);
		
		//Preguntamos al usuario si desea recalcular precio y producción.
		$('#overlay').show();
    	$("#popupRecalcular").show();
	}
	else{
		//Muestra el popup
		$("#divAviso").show();
		$('#txt_info').show();
    	$('#overlay').show();
	}
}

//Función para cerrar la ventana emergente que se muestra cuando hay errores
function cerrarPopUp(){
	$('#divAviso').fadeOut('normal');
	$('#txt_info').hide();
	$('#overlay').hide();
}

//Método para continuar con la carga de parcelas una vez preguntado al usuario si desea recalcular
function continuar(recalcular){
	//Ocultar el popup y guardar el valor elegido
	$("#popupRecalcular").hide();
    $('#overlay').hide();
    $("#recalcular").val(recalcular);
    
    //Mostrar capa para bloquear la pantalla
	$.blockUI.defaults.message = '<h4> Realizando cálculos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
	
	if ($("#descargarCopy").val() == "false"){
		$("#method").val('doPolizaAnteriorElegida');
	}
	else{
		$("#method").val('doSituacionActElegida');
	}
	$('#main3').submit();
}
