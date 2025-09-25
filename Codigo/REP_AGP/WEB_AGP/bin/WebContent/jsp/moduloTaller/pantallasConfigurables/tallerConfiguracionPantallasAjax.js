function getArbolComponentes(lineaseguroid, uso) {
	$.ajax({
		type : 'POST',
		url : 'tallerConfiguracionPantallas.html',
		data : {
				'method'        : 'doArbolComponentes',
				'lineaseguroid' : lineaseguroid,
				'uso'           : uso
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
				$('#componentesContainer').html(createArbolComponentes(datos.estructuraUso, uso));
			}
		},
		beforeSend : function() {
			$('#ajaxLoading_componentesContainer').show();
			$('#componentesContainer').hide();					
		},
		complete : function() {
			$('#ajaxLoading_componentesContainer').hide();
			$('#componentesContainer').show();
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

function getControlesPantalla(idPantalla) {
	$.ajax({
		type : 'POST',
		url : 'tallerConfiguracionPantallas.html',
		data : {
				'method'     : 'doControlesPantalla',
				'idPantalla' : idPantalla
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
				$.each(datos.pantallaConfigurableVO.listCampos, function(i, item) {
					createControlPantalla(item, $('#controlsContainer'), false);
				});
			}
		},
		beforeSend : function() {
			$('#ajaxLoading_controlsContainer').show();
			$('#controlsContainer').hide();					
		},
		complete : function() {
			$('#ajaxLoading_controlsContainer').hide();
			$('#controlsContainer').show();
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

function anhadirControlPantalla(lineaseguroid, codConcepto, codUbicacion, codUso, top, left) {
	$.ajax({
		type : 'POST',
		url : 'tallerConfiguracionPantallas.html',
		data : {
				'method'        : 'doAnhadirControlPantalla',
				'lineaseguroid' : lineaseguroid,
				'codConcepto'   : codConcepto,
				'codUbicacion'  : codUbicacion,
				'codUso'        : codUso,
				'top' 		    : top,
				'left'          : left
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
				createControlPantalla(datos.campoPantallaConfigurableVO, $('#controlsContainer'), true);
			}
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

function guardarPantalla(lineaseguroid, idPantalla, jsonCampos) {
	$.ajax({
		type : 'POST',
		url : 'tallerConfiguracionPantallas.html',
		data : {
				'method'        : 'doGuardarPantalla',
				'lineaseguroid' : lineaseguroid,
				'idPantalla'    : idPantalla,
				'jsonCampos'    : JSON.stringify(jsonCampos)
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
				$('#panelMensajeValidacion').html('Pantalla guardada correctamente.');
				$('#panelMensajeValidacion').show();
			}
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