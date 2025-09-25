/*$(function(){
	
	// --------------------------------------------------
	//         paginacion por AJAX
	// --------------------------------------------------
	$("#grid").displayTagAjax();
}).ajaxSend(function(){
	
	//checkSeleccionados();
}).ajaxComplete(function() {
   	changeColorRow();
   	check_checks($('#idsCapAsegRowsChecked').val());
   	$('#sel').text(numero_check_seleccionados());
   	checkTodos();
});*/

    
/**

 * Realiza la llamal al SW de validacion previo a la confirmacion del cupun
 * @param idCupon
 */
function enviar () {
	muestraCapaEspera ("Validando el A.M");
	//alert("ciclo vida anexo parcelas - idCupon: "+$("#idCupon").val()+ " idAnexo: "+ $("#idAnexoModificacion").val());
	validacionesPreviasEnvioAjax($("#idCupon").val(),$("#idAnexoModificacion").val(),$("#hayCambiosDatosAsegurado").val());
}

function validacionesPreviasEnvioAjax(idCupon,idAnexo, hayCambiosDatosAsegurado){
	
	$.ajax({
            url: "validacionesAnexoAjax.html",
            data: "method=doValidacionesPreviasEnvio&idAnexo="+idAnexo+"&hayCambiosDatosAsegurado="+hayCambiosDatosAsegurado,
            async:true,
            cache: false,
            beforeSend: function(objeto){
            },
            complete: function(objeto, exito){
            },
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            error: function(objeto, quepaso, otroobj){
                alert("Error al comprobar la coherencia de los datos variables de las parcelas: " + quepaso);
            },
            global: true,
            ifModified: false,
            processData:true,
            success: function(datos){
            	
            	if(datos.validacionesPreviasEnvio.valueOf() == "true"){
            		$("#idCuponValidar").val(idCupon);
            		$("#validarAnexo").submit();
            	}else{
            		quitaCapaEspera ();
            		//$('#panelInformacion').hide();
            		$('#panelAlertasValidacion').html(datos.mensaje);
            		$('#panelAlertasValidacion').show();
            	}
            	
            },
            type: "POST"
        });
}
		
/**
 * Muestra la capa de espera con el mensaje recibido como parametro
 * @param msg
 * @returns
 */
function muestraCapaEspera (msg) {
	$.blockUI.defaults.message = '<h4> ' + msg + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}

//Funcion para Imprimir un anexo de modificacion
function imprimir(){

	var frm = document.getElementById('imprimirAnexo');
	frm.idCuponImprimir.value = $("#idCuponStr").val();
	frm.action = frm.action + '?rand=' + Math.random();
	$('#imprimirAnexo').attr('target', '_blank');
	$("#imprimirAnexo").submit();
	
}

/* Pet. 78877 ** MODIF TAM (25.10.2021) ** Inicio */
function calcRdtoOrientativo(){ 
	
   var itemsChecked = $('#idsRowsChecked').val();
   
   if(itemsChecked.length > 0){
		$.blockUI.defaults.message = '<h4> Calculando rendimiento Orientativo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#method').val('doCalcRdtoOrientativo');
		$('#main3').submit();
		
	}else{
		showPopUpAviso("Debe seleccionar como m�nimo una parcela.");
	}
}   
/* Pet. 78877 ** MODIF TAM (25.10.2021) ** Fin **/


/**
 * Elimina la capa de espera
 */
function quitaCapaEspera () {
	$.unblockUI();
}

function calculoRdtoHist(){
	
   var itemsChecked = $('#idsRowsChecked').val();
   if(itemsChecked.length > 0){
		$.blockUI.defaults.message = '<h4> Calculando rendimiento.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#method').val('doCalculoRdtoHist');
		$('#main3').submit();
		
	}else{
		showPopUpAviso("Debe seleccionar como m�nimo una parcela.");
	}
}
	