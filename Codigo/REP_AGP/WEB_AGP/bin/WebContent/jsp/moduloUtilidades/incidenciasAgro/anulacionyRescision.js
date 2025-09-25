$(document).ready(function() {
	
	if ($('#tipoAyR').val() =="A" ) {
		document.getElementById('tipoAnu').checked = true;
	}else{
		if ($('#tipoAyR').val() =="R" ) { 
			document.getElementById('tipoResc').checked = true;
		}else{
			//alert ("No marcamos nada");
		}
	}
	
	if ($('#origenLlamada').val()=="Consulta"){
		document.getElementById('tipoResc').disabled="true";
		document.getElementById('tipoAnu').disabled="true";
		document.getElementById('plan').disabled="true";
		document.getElementById('linea').disabled="true";
		document.getElementById('desc_linea').disabled="true";
		document.getElementById("lupalinea").style.display='none';
		
		document.getElementById('referencia').disabled="true";
		document.getElementById('tiporef').disabled="true";
		document.getElementById('nifcif').disabled="true";
		document.getElementById('listmotivo').disabled="true";
		document.getElementById('btnEnviar').style.display='none';
	}else{ 
		if($('#origenLlamada').val()=="Volver"){
			Volver();
		}
	}

	
	var URL = UTIL.antiCacheRand(document.getElementById("formAnulyResc").action);
    document.getElementById("formAnulyResc").action = URL;    
        
	$('#formAnulyResc').validate({	
		
		errorLabelContainer: "#panelAlertasValidacion",
		
		onfocusout: function(element) {
			if ($('#formAnulyResc:input[name="method"]').val() == "doconsultaAnulResc") {
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
			"codplan":{digits: true, range: [0, 9999]},
			"codlinea":{digits: true, range: [0, 9999]}
		},
		messages: {
			"codPlan":{digits: "El campo Plan sólo puede contener dígitos", range: "El campo Plan sólo puede contener dígitos entre 0 y 9999"},
			"codlinea":{digits: "El campo Línea sólo puede contener dígitos", range: "El campo Línea sólo puede contener dígitos entre 0 y 9999"}
		}
	});
	
});


function comprobarCampos(incluirJmesa) {	

	var resultado = false;
	var radioSelect = $('input:radio[name=tipoAnuResc]:checked').val();
	var motivo_sele = $('#listmotivo').val();
	
	if (!resultado && (radioSelect != 'A' && radioSelect !='R')){
		resultado = true;
	}
	if (!resultado && $('#plan').val() == ''){
		resultado = true;
	}
	if (!resultado && $('#linea').val() == ''){
		resultado = true;
	}
	if (!resultado && $('#referencia').val() == ''){
		resultado = true;
	}
	if (!resultado && $('#tiporef').val() == ''){
		resultado = true;
	} 
	if (!resultado && $('#nifcif').val() == ''){
		resultado = true;
	}
	if (!resultado && $('#listmotivo').val() == ''){
		//if (radioSelect !='R'){
			resultado = true;	
		//}
	}
	return resultado;
   	
}

function Volver(){
	
	if ($('#ventanaVolver').val() == "impresionInc"){
		var frm = document.getElementById('VolverimpresionIncidenciasMod');
		frm.method.value = "doImprimirIncidencias";
		$('#VolverimpresionIncidenciasMod').attr('target', '_self');
		$("#VolverimpresionIncidenciasMod").submit();
	}else{ 
		if($('#ventanaVolver').val() == "Consulta" || $('#ventanaVolver').val() == "EditarAnuyResc"){
			$('#formVolverInc').submit();
		}else{
			$("#method").val("doVolver");
			$("#id").val("");
			$("#volverUtilidadesPol").submit();
		}
	}	
	
}

/* Verificamos si alguno de los valores iniciales han cambiado */
/* en cuyo caso limpiamos el idpoliza para consultarlo de nuevo */
function comprobarCambiosCamposIni() {	
	var resul_cambio = false;
	
	if (!resul_cambio && ($('#referenciaAyR').val() != $('#referencia').val() )){
		resul_cambio = true;
	}

	if (!resul_cambio && ($('#codPlanAyR').val() != $('#plan').val() )){
		resul_cambio = true;
	}
	
	if (!resul_cambio && ($('#codlineaAyR').val() != $('#linea').val() )){
		resul_cambio = true;
	}

	if (!resul_cambio && ($('#tiporefSel').val() != $('#tiporef').val() )){
		resul_cambio = true;
	}
	
	if (resul_cambio){
		$("#idPolizaAyR").val("");
		$("#idIncAyR").val("");
		
		/* Asignamos los valores que han vuelto a insertar en Pantalla */
		$("#codPlanAyR").val( $('#plan').val());
		$('#codlineaAyR').val($('#linea').val());
		$('#referenciaAyR').val($('#referencia').val());
		$('#tiporefSel').val($('#tiporef').val());
		
	}
   	
}

function enviarAnulyResc(){
	
	/* comprobamos si los campos iniciales se han cambiado */
	comprobarCambiosCamposIni();
	
	var origenLlamada = document.getElementById('origenLlamada');
	
	if (origenLlamada = 'EditarAnuyResc'){
		CargarParametrosVuelta();
	}
	
	var Result_comprobar = comprobarCampos();
	
	if (Result_comprobar == false) {
		$('#formAnulyResc input[name="method"]').val('doEnviarAnulyResc');
		$('#formAnulyResc input[name="motivoAyR"]').val($('#listmotivo').val());
		
		if ($('#idIncAyR').val() == ""){
			$('#idIncAyR').val($('#idincConsVuelta').val());	
		} 
		$('#nifCifAyR').val($('#nifcif').val());
				
		if($("#formAnulyResc").valid()){
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$('#formAnulyResc').submit();
		}
	}else {
		$('#panelAlertasValidacion').html("Debe informar todos los valores antes de Enviar la Anulación/Rescisión");
		$('#panelAlertasValidacion').show();
	}

}

function CargarParametrosVuelta(){
	
	$("#idincConsVuelta").val( $('#idincidenciaConsulta').val());
	$("#entidadConsVuelta").val( $('#entidadConsulta').val());
	$("#refConsVuelta").val( $('#referenciaConsulta').val());
	$("#oficinaConsVuelta").val( $('#oficinaConsulta').val());			
	$("#entmediadoraConsVuelta").val( $('#entmediadoraConsulta').val());
	$("#subentmediadoraConsVuelta").val( $('#subentmediadoraConsulta').val());
	$("#delegacionConsVuelta").val( $('#delegacionConsulta').val());
	$("#codplanConsVuelta").val( $('#planConsulta').val());
	$("#codlineaConsVuelta").val( $('#lineaConsulta').val());
	$("#codestadoConsVuelta").val( $('#codestadoConsulta').val());
	$("#codestadoagroConsVuelta").val( $('#codestadoagroConsulta').val());
    $("#nifcifConsVuelta").val( $('#nifcifConsulta').val());
    $("#tiporefConsVuelta").val( $('#tiporefConsulta').val());
    $("#idcuponConsVuelta").val( $('#idcuponConsulta').val()); 		
    $("#asuntoConsVuelta").val( $('#asuntoConsulta').val()); 		
    $("#fechaEnvioDesdeIdConsVuelta").val( $('#fechaEnvioDesdeIdConsulta').val()); 
    $("#fechaEnvioHastaIdConsVuelta").val( $('#fechaEnvioHastaIdConsulta').val()); 
    $("#numIncidenciaConsVuelta").val( $('#numIncidenciaConsulta').val()); 	
    $("#codusuarioConsVuelta").val( $('#codUsuarioConsulta').val()); 	
    $("#tipoincConsVuelta").val( $('#tipoincConsulta').val()); 		
			
}

