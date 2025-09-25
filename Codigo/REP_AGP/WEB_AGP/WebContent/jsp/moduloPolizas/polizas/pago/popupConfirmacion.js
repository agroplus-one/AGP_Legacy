function muestraPopUpConfirmacion(){
	if ($('#codTerminal').val() && $('#firmaTableta').val() == 1) {
		jConfirm('\u00BFDesea firmar la documentaci\u00F3n en tableta?', '', function(r) {
		    if (r == true){
		    	checkaseguradoIris();
		    } else {
		    	$('#panelConfirmacion').show();
				$('#overlay').show();
		    }
		});
	} else {
		$('#panelConfirmacion').show();
		$('#overlay').show();
	}
}

function cerrarPopUpConfirmacion(){
	$('#panelConfirmacion').hide();
	$('#overlay').hide();
}

function checkaseguradoIris() {
	$('#panelAlertasValidacion').html('');
	$('#panelAlertasValidacion').hide();
	$('#panelMensajeValidacion').html('');
	$('#panelMensajeValidacion').hide();
	$.blockUI.defaults.message = '<h4> Verificando el asegurado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$.ajax({
		type : 'POST',
		url : 'firmaTableta.html',
		data : {
				'method'           : 'doCheckAseguradoIris',
				'codigoEntidad'    : $('#codEntidad').val(),
				'idExternoPersona' : $('#nifCifAseg').val(),
				'tipoPersona'      : $('#tipoPersona').val(),
				'codUsuario'       : $('#codUsuario').val(),
				'codTerminal'      : $('#codTerminal').val()
			   },
		async : true,
		dataType : 'json',
		success : function(datos) {
			if (datos.errorMsgs.length > 0) {
				var errorMsg = '';
				for (i = 0; i < datos.errorMsgs.length; i++) {
					errorMsg += datos.errorMsgs[i] + '<br/>';
				}
				$('#panelAlertasValidacion').html(errorMsg);
				$('#panelAlertasValidacion').show();
			} else {
				if (datos.listaAsegs.length == 1) {
					$('#idInternoPe').val(datos.listaAsegs[0].idInternoPe);
					// ACTUALIZAMOS LOS CAMPOS DE DOC ENTREGADA
					$('#notaPreviaInput').val("true");
					$('#IPIDInput').val("true");
					$('#RGPDInput').val("true");
					// Y CONTINUAMOS
					confirmaSW();
				} else if (datos.listaAsegs.length == 0) {
					jConfirm('No existe asegurado en IRIS.\u00BFDesea continuar la contrataci\u00F3n sin firmar en tableta?', '', function(r) {
					    if (r == true){
					    	$('#panelConfirmacion').show();
							$('#overlay').show();
					    } else {
					    	jAlert('Realice el alta de asegurado en IRIS antes de continuar con la contrataci\u00F3n con firma en tableta.', 'Aviso');
					    }
					});
				} else {
					mostrarListadoAsegurados(datos.listaAsegs);
				}				
			}
		},
		complete : function() {
			$.unblockUI();
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
			$('#panelAlertasValidacion').html(msg);
			$('#panelAlertasValidacion').show();
		}
	});
}

/*Comprueba si los 3 check de confirmacion y si los 3 estan pulsados muestra el boton de Enviar*/
function compruebaChecks(){
	if( $('#notaPreviaId').attr('checked') && $('#IPIDId').attr('checked') && $('#RGPDId').attr('checked')) {
		$('#botonEnviarId').show();
	} else {
		$('#botonEnviarId').hide();
	}
	
	/* Guardamos los valores en los input correspondientes a los check*/
	if( $('#notaPreviaId').attr('checked') ){
		$('#notaPreviaInput').val("true");
	} else {
		$('#notaPreviaInput').val("false");
	}
	
	if( $('#IPIDId').attr('checked') ){
		$('#IPIDInput').val("true");
	} else {
		$('#IPIDInput').val("false");
	}
	
	if( $('#RGPDId').attr('checked') ){
		$('#RGPDInput').val("true");
	} else {
		$('#RGPDInput').val("false");
	}
}