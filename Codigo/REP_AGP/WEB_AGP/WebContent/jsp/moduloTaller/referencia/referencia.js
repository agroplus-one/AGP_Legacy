var numRegAInsertar = 0;
var temporizador;
		
$(document).ready(function(){
     $("#main").attr("action", UTIL.antiCacheRand($("#main").attr("action"))); 	            

	 $("#main").validate({			
		 
		onfocusout: false,
		
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",	  				 
		rules: {
			"referenciaIni":{required: true, valReferencia: true},
			"referenciaFin":{required: true, valReferencia: true, valIntervalo: true}
		},
		messages: {
			"referenciaIni":{required: "El campo de inicio de referencias es obligatorio", valReferencia:"El campo de inicio de referencias no tiene formato correcto"},
			"referenciaFin":{required: "El campo de fin de referencias es obligatorio", 
						    valReferencia: "El campo de fin de referencias no tiene formato correcto",
						    valIntervalo: "El campo de inicio de referencias debe ser menor que del de fin"}
		}  
	});

});

 jQuery.validator.addMethod("valReferencia", function(value, element, params) {
		// Comprueba que el primer caracter es una letra en mayúsculas
		if (value.charAt(0).match (/[A-Z]/g) == null) return false;
		// Comprueba que los siguientes caracteres son números
		if (value.substr(1).match (/\d/g) == null) return false;
		return true;
	});		
 
 jQuery.validator.addMethod("valIntervalo", function(value, element, params) {
		// Comprueba que el primer caracter es una letra en mayúsculas
		numIni = $('#referenciaIni').val();
		// Comprueba que los siguientes caracteres son números
		if (eval (value.substr(1)) > eval (numIni.substr(1))) {
			numRegAInsertar = (value.substr(1) - numIni.substr(1)) + 1;
			return true;
		}					
		
		return false;
	});		

function generar(method) { 
	
		ocultarAvisos();
	
		preparaTxt ('referenciaIni');
		preparaTxt ('referenciaFin');
		
		// Valida el formulario
		$('#main').validate();
		// Si es correcto lanza la petición para generar las referencias
		if ($('#main').valid()) {
			generarAjax ();
			$.blockUI.defaults.message = '<h4><div>Generando referencias. <img src="jsp/img/ajax-loading.gif"/></br>' +
									     '<div id="numInsertadas"></div></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			iniciarTemporizador();
		}
}

// Pasa a mayúsculas y elimina los espacios en blanco del texto del elemento pasado como parámetro
function preparaTxt (nombre) {
	var str = $('#' + nombre).val();
	if (str != null && str != 'undefined') {
		str = $.trim (str.toUpperCase());					
		$('#' + nombre).val(str);
	}
}

function iniciarTemporizador() {
	temporizador = window.setInterval(function () {getNumInsertados ()}, 10000);
}

function pararTemporizador () {
	try {
		window.clearInterval (temporizador);
	}
	catch (e) {}
}

//Llamada ajax para obtener el número de referencias insertadas hasta el momento
function generarAjax () {
	$.ajax({
		url: "referencia.html?referenciaIni=" + $('#referenciaIni').val() + "&referenciaFin=" + $('#referenciaFin').val(),
		data: "method=doGenerar",
		async:true,
		dataType: "json",
		success: function(datos){
			$.unblockUI();
			pararTemporizador();
    		// Ejecución correcta
			if (datos.cod == "0") {
				$('#panelInformacion').html(datos.msg);
				$('#panelInformacion').show();
				$('#referenciaIni').val('');
				$('#referenciaFin').val('');
				$('#refLibres').html(datos.refLibres);
				$('#ultimaRef').html(datos.ultimaRef);
			}
			// Ejecución con errores
			else {				
				$('#panelAlertasValidacion').html(datos.msg);
				$('#panelAlertasValidacion').show();
			}
		},					
		type: "POST"
	});
}

// Llamada ajax para obtener el número de referencias insertadas hasta el momento
function getNumInsertados () {
	$.ajax({
		url: "referencia.html" ,
		data: "method=doAjaxGetInsertados",
		async:true,
		dataType: "json",
		success: function(datos){				    					    		
    		$('#numInsertadas').html('Insertadas ' + datos.numInsertados +' de ' + numRegAInsertar);
    		// Si se ha finalizado la carga y se llega a este punto significa que el 'success' de la función 'generarAjax'
    		// ha fallado (probablemente por timeout), por lo que se recarga la página para mostrar los cambios
    		if (datos.finalizado.valueOf() == "true") {
    			window.location = "referencia.html?aviso=1";
    		}
		},				           
		type: "POST"
	});
}

// Borra y oculta los paneles de avisos
function ocultarAvisos () {
	$('#panelInformacion').empty();
	$('#panelInformacion').hide();
	$('#panelAlertasValidacion').empty();
	$('#panelAlertasValidacion').hide();
}