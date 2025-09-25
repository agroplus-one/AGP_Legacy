$(document).ready(function(){
		
	var URL = UTIL.antiCacheRand($("#main").attr("action"));
	$("#main").attr("action", URL);

	$('#main').validate({
		 errorLabelContainer: "#panelAlertasValidacion",
		 wrapper: "li",
		 rules: {
		 	"seleccionOrigen": {required: true}
		 },
		 messages: {
		 	"seleccionOrigen": {required: "Debe seleccionar un origen de datos"}
		 }
	});
});

/**
 * Funci�n para volver a la pantalla de elecci�n de comparativas/m�dulos
 */
function volver() {
	$("#main").validate().cancelSubmit = true;
	$("#main").attr("action", UTIL.antiCacheRand("seleccionPoliza.html"));
	
	$("#operacion").val('');
	$("#main").submit();
}

/**
 * Funci�n para continuar con la tramitaci�n de la p�liza sin cargar ninguna parcela.
 */
function continuarSinCargar() {
	
	$("#main").attr("action", UTIL.antiCacheRand("seleccionPoliza.html"));
	$("#main").validate().cancelSubmit = true;
	$("#operacion").val('listParcelas');
	$("#main").submit();
}

//DAA 23/05/2012
function cargar() {
	var txt = $('input:radio[name=seleccionOrigen]:checked').val();
   	if(txt == "doSituacionAct"){
       	//$.blockUI.defaults.message = '<h4> Procesando petici�n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   		//$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
   		//Petici�n ajax para saber si mostramos la pregunta para el recalculo.
   		comprobarNumCopys(false);
   	}
   	else if (txt == "doNoCargar"){
   		continuarSinCargar();
   	}
   	// MPM 07/11/2012
   	// Si se ha seleccionado 'P�liza original del plan anterior' se muestra la pregunta del rec�lculo
   	// 23/04/14 TMR Petici�n ajax para saber si mostramos la pregunta para el recalculo.
   	else if (txt == "doPolizaAnterior") {
   		comprobarNumCopys(true);
   	} else if (txt == "doParcelasCsv") {
   		$('#overlay').show();
    	$("#popupRecalcular").show();
   	}
   	// MPM - P20779 - Al cargar parcelas de PAC siempre se pregunta si se quiere recalcular precio y producci�n
   	// De momento se comenta, ya que al llamar a SW de rec�lculo se producen errores, debido a que las parcelas cargadas de PAC no 
   	// tienen hoja-n�mero
   	/*else if (txt == "doParcelasPac") {
   		$('#overlay').show();
    	$("#popupRecalcular").show();
   	}*/
   	else if (txt != undefined){
   		continuar('no');
	}else{
		$("#main").valid();
	}
}

/**
 * Funci�n para comprobar si existe m�s de una copy para los datos del asegurado, plan y l�nea, en cuyo caso habr� que mostrar
 * un listado con las p�lizas disponibles para cargar la que el usuario elija.
 */
function comprobarNumCopys(polAnterior){
	$.ajax({
		url: "cargaParcelasController.html",
		data: "method=doGetNumCopys&idpoliza="+$('#idpoliza').val()+"&nifasegurado="+$('#nifasegurado').val()+"&codplan="+$('#codplan').val()+"&codlinea="+$('#codlinea').val()+"&polAnterior="+polAnterior,
		async: true,
		dataType: "json",
		error: function(nomObjeto, quepaso, otroobj){
			$.unblockUI();
			alert("Se produjo un error al recuperar los datos " + nomObjeto + ", " + quepaso + ", " + otroobj);
		},
		success: function(datos){
			continuarCargaCopy(datos.preguntarRecalculo);
		},
		beforeSend: function(){
			$.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		},
		type: "POST"
	});
}

function continuarCargaCopy(preguntarRecalculo){
	
	if (preguntarRecalculo == true){
		//Mostramos la pregunta de recalcular y continuamos
		$.unblockUI();
		$('#overlay').show();
    	$("#popupRecalcular").show();
	}
	else{
		//Hay que mostrar la pantalla de elecci�n de copy a cargar
		continuar('no');
	}
}

function continuar(recalcular){
	$("#popupRecalcular").hide();
    $('#overlay').hide();
    $("#recalcular").val(recalcular);
    
	//ponemos de nuevo la capa por si se quit� para poder responder a la pregunta
	$.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });

 	if($("#main").valid()){
	    $("#main").attr("action", UTIL.antiCacheRand("cargaParcelasController.html"));
		$("#method").val($('input:radio[name=seleccionOrigen]:checked').val());
		$('#main').submit();
	}
}