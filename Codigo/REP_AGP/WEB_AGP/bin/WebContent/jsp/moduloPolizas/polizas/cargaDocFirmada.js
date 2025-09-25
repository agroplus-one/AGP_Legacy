$(document).ready(
		function() {
			
			$.validator.addMethod('filesize', function(value, element, param) {
			    // param = size (en bytes) 
			    // element = element to validate (<input>)
			    // value = value of the element (file name)
			    return this.optional(element) || (element.files[0].size <= param) 
			});
			
			$('#frmCargaDocFirmada').validate({			
				onfocusout: false,
				errorLabelContainer: "#panelAlertasValidacionCargaDocFirmada",  				 
				rules: {
					"file":{required: true, accept: "pdf", filesize: 15728640}
				},
				messages: {
					"file":{required: "Debe seleccionar un archivo", accept:"El archivo no tiene una extensi\u00F3n v\u00E1lida", filesize:"Tamaño excedido de 15mb"}
				}  
			});	

		});


function ajaxFileUpload(){	
	if ($('#frmCargaDocFirmada').valid()){
		
			
			$('#divCargaDocFirmada').fadeOut('normal');
			$.blockUI.defaults.message = '<h4> Subiendo documentaci\u00F3n a GED.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
   			
			
   			$.ajaxFileUpload({
					url:'cargaDocFirmada.html?method=doCargaDocFirmada', 
					data:{
						idPoliza: frmCargaDocFirmada.idPoliza.value, 
						origenPoliza: frmCargaDocFirmada.origenPoliza.value
					},
					secureuri:false,
					fileElementId:'file',
					dataType: 'json',
					success: function (data){
						
						if (data.alerta != null){
							var str = data.alerta;
							
							$('#panelAlertasValidacionCargaDocFirmada').html(str);
							$('#panelAlertasValidacionCargaDocFirmada').show();

						} else {
							$("#btnImportarFirma").hide();
							$("#btnCancelarFirma").hide();
							var str = data.mensaje;
							$('#panelMensajeValidacionCargaDocFirmada').html(str);
							$('#panelMensajeValidacionCargaDocFirmada').show();
						}
						$.unblockUI();
						$('#divCargaDocFirmada').fadeIn('normal');

					},				
					error : function(jqXHR, exception) {
						if (jqXHR.status === 0) {
				            msg = 'Verifique la conexi\u00F3n.';
				        } else if (jqXHR.status == 404) {
				            msg = 'P\u00E1gina no encontrada [404].';
				        } else if (jqXHR.status == 500) {
				            msg = 'Error interno del servidor [500].';
				        } else if (exception === 'parsererror') {
				            msg = 'Fallo en el tratamiento del JSON esperado.';
				        } else if (exception === 'timeout') {
				            msg = 'Tiempo de espera agotado.';
				        } else if (exception === 'abort') {
				            msg = 'Petici\u00F3n Ajax cancelada.';
				        } else {
				            msg = 'Error no esperado: ' + jqXHR.responseText;
				        }
						
						$('#panelMensajeValidacionCargaDocFirmada').html(msg);
						$('#panelMensajeValidacionCargaDocFirmada').show();
						$.unblockUI();
						$('#divCargaDocFirmada').fadeIn('normal');
					}
			});
		
	}
}

//FUNCIONES PARA LA IMPORTACION DEL FICHERO
function cleanPopUpCargaDocFirmada(){
	
	$("#tablaInformacion").html("<div style=\"display: inline;padding-right: 1em;\"><span class=\"literal\">Fichero a importar:</span></div><input type='file' class='dato'  id='file' name='file' size='40' onchange='javascript:limpiaPanelAlertasCargaDocFirmada();'/>");	
	$("input[type=file]").filestyle({
		image: "jsp/img/boton_examinar.png",
		imageheight : 22,
		imagewidth : 82,
		width : 250
	});
	
	limpiaPanelAlertasCargaDocFirmada();
	limpiaPanelMensajeCargaDocFirmada();
}

function cerrarPopUpCargaDocFirmada(){
	cleanPopUpCargaDocFirmada()
	
	$("#btnImportarFirma").show();
	$("#btnCancelarFirma").show();
	
	$("#infoPopUpDocFirmada").empty();
	$('#divCargaDocFirmada').fadeOut('normal');
	$('#overlay').hide();
	
	consultar();
}

function limpiaPanelAlertasCargaDocFirmada(){
	$("#panelAlertasValidacionCargaDocFirmada").empty();
	$("#panelAlertasValidacionCargaDocFirmada").hide();
}

function limpiaPanelMensajeCargaDocFirmada(){
	$("#panelMensajeValidacionCargaDocFirmada").empty();
	$("#panelMensajeValidacionCargaDocFirmada").hide();
}

function showPopUpCargaDocFirmada(){
	cleanPopUpCargaDocFirmada()
  	var frm = document.getElementById('frmCargaDocFirmada');
	$('#divCargaDocFirmada').fadeIn('normal');
	$('#overlay').show();
}

/**
* P0073325 - RQ.10, RQ.11 y RQ.12
*/
function cargaDocFirmada(idPoliza, referencia, plan, modulo, origenPoliza) {

	var strIdPoliza = "";
	var strRef = "";
	var strPlan = "";
	var strModulo = "";
	
	if (idPoliza != null && idPoliza != "") {
		strIdPoliza = idPoliza
		frmCargaDocFirmada.idPoliza.value = idPoliza;
	}
	
	if (referencia != null && referencia != "") {
		strRef = referencia
	}
	
	if (plan != null && plan != "") {
		strPlan = plan
	}
	
	if (modulo != null && modulo != "") {
		strModulo = modulo
	}
	
	if (origenPoliza != null && origenPoliza != "") {
		frmCargaDocFirmada.origenPoliza.value = origenPoliza;
	}
	
	$("#infoPopUpDocFirmada").append("<table style=\"margin: 1em auto 1em auto;\"><tbody><tr><td><span class=\"literal\">P\u00F3liza: "+strRef+"</span></td><td><span class=\"literal\">Plan: "+strPlan+"</span></td><td><span class=\"literal\">Tipo ref: "+strModulo+"</span></td></tr></tbody></table>");

	limpiaPanelAlertasCargaDocFirmada();
	limpiaPanelMensajeCargaDocFirmada()
	showPopUpCargaDocFirmada();
}