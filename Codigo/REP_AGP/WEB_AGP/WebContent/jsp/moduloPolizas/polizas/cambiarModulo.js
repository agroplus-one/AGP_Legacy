function operacionCambiarModulo() {
	limpiarPaneles();
	var list = "";
	var contador = 0;
	$('#filtro').val('consulta');
	$("input[type=checkbox]").each(function() {
		if($(this).attr('checked')){
			list = list + $(this).val();
			contador++;
		}
	});
	if(contador > 0) {
		if (contador > 1){// hay mas de una poliza marcada			
			$('#divAviso').show();
			$('#txt_info_check_multiple').show();
			$('#overlay').show();
		} else {// solo una poliza marcada
			if (confirm('\u00BFDesea realizar el cambio de m\u00F3dulo de la p\u00F3liza selecccionada?')) {
				cambioModulo_ajax();
			}
		}
	} else {// no hay polizas seleccionadas		
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}	
}

function cambioModulo_ajax() {
	$.ajax({
		type : 'POST',
		url : 'cambiarModulo.html',
		data : {
				'method'   : 'doCambioModulo',
				'idPoliza' : $("#idsRowsChecked").val().replace(';', '')
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
				$('#panelMensajeValidacion').html('P\u00F3liza cambiada correctamente al m\u00F3dulo ' + datos.moduloDestino);
				$('#panelMensajeValidacion').show();
			}
		},
		beforeSend : function() {
			$.blockUI.defaults.message = '<h4>Cambiando M&oacute;dulo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });	
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