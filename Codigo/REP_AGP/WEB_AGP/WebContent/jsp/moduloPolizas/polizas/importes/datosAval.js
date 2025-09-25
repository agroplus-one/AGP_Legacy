$(document).ready(function(){
	$("#panelDatosAval_Gral").draggable();	
	$('#panelAlertasValidacion_da').html("");
	$('#panelAlertasValidacion_da').hide();
});	


function validar_da(){
	
	$('#panelAlertasValidacion_da').html("");
	var resVal = validaFrmDatosAval();
	if(resVal==""){
		$('#overlay').hide();
		$('#panelDatosAval_Gral').hide();

		var idpol = document.getElementById("idpoliza");			
		
//		if ($('#pdComplementaria').length ==0) {
			//ajax_check_Cpl_Sbp();
			var frm = document.getElementById("frmDatosAval");
			frm.method.value = "doGuardarDatosAval";
			frm.action="eleccionFormaPago.html";
			
			frm.submit();			
//		}
//		else {
//			continuarPDCpl();
//		}
		
	}else{
		$('#panelAlertasValidacion_da').html(resVal);
		$('#panelAlertasValidacion_da').show();
	}
	
	
}



function isNullOrEmpty(str) { 
	return (!str || /^\s*$/.test(str));
}

function valorEntero9Decimal4(value) {
	return /^\d{0,9}([.]\d{1,4})?$/.test(value);
}	

function validaNumeroAval(value){
	return /^\d*$/.test(value);  
}

function cancelar_da(){
	// limpiamos alertas
	$('#panelAlertasValidacion_da').html("");
	$('#panelAlertasValidacion_da').hide();
	// vaciamos campos
	
	$('#importe_da').val("");
	$('#numero_da').val("");
		
	// cerramos div "inferiores"
	$('#panelDatosAval_Gral').hide();
	//$('#panelDatosAval').hide();
    $('#overlay').hide();
}

function validaFrmDatosAval(){
	var numAval = document.getElementById("numero_da").value;
	if(isNullOrEmpty(numAval)){
		return "El campo n\u00FAmero de aval es obligatorio.";
	}
	if(!validaNumeroAval(numAval)){
		return "El campo n\u00FAmero de aval debe de ser un valor num\u00E9rico entero.";
	}
	var impAval = document.getElementById("importe_da").value;
	if(isNullOrEmpty(impAval)){
		return "El campo importe del aval es obligatorio.";
	}
	if(!valorEntero9Decimal4(impAval)){
		return "El campo importe del aval debe de ser un campo num\u00E9rico de hasta 9 d\u00EDgitos en su parte entera y 4 en la decimal";
	}
	return "";
}

function ajax_muestraDatosAval(id) {
	
	$.ajax({
				url : "validacionesUtilidades.html",
				data : "method=do_Ajax_muestraDatosAval&idPoliza=" + id,
				async : false,
				contentType : "application/x-www-form-urlencoded",
				dataType : "text",
				global : false,
				ifModified : false,
				processData : true,
				cache : false,
				error : function(objeto, quepaso, otroobj) {
					alert("Error al solicitar si se muestran datos de aval: "
							+ quepaso);
				},
				success : function(datos) {
					if (datos == 'true') { //tiene financiacion y es mayor= de 2015
						var frm = document.getElementById('frmDatosAval');
						frm.idpoliza.value = id;
						$('#overlay').show();
						$('#panelDatosAval_Gral').show();	
					} else {
						// No hay que mostrar el popup - Se continua con el paso a definitiva
//						if ($('#pdComplementaria').length ==0) {
							//ajax_check_Cpl_Sbp();
							var frm = document.getElementById("frmDatosAval");
							frm.method.value = "doMostrar";
							frm.action="eleccionFormaPago.html";	
							frm.idpoliza.value = id;
							//frm.grProvisionalOK.value=;
							frm.submit();			
//						}
//						else {
//							continuarPDCpl();
//						}
					}
				},
				type : "GET"
			});
}
