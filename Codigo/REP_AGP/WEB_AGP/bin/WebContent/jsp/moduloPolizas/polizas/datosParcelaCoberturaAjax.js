/*** CREATED: T-Systems (07.10.2020) 
 *   PETICION: 63485
 ***/

function cargarCobertParcelas() {
		$.ajax({ 
			type : 'POST', 
			url : $('#isAnexo').val() == 'true' ? 'parcelasAnexoModificacion.html' : 'datosParcela.html',
			//url : 'datosParcela.html', 
			data : { 
					'method'  : 'doCargarCoberturasParcela',
					'lineaseguroid' : $('#lineaseguroid').val(),
					'claseId'       : $('#claseId').val(),
					'nifcif'        : $('#nifAsegurado').val(),
					'modoLectura'   : $('#modoLectura').val(),
					'parcela' : JSON.stringify(getParcelaJson()) 
				   }, 
			async : false, 
			dataType : 'json', 
			success : function(datos) {
				mostrarCoberturasParc(datos.listaCobParcelas, $('#modoLectura').val());
				$('#ajaxLoading_coberturas').hide();	
			}, 
			beforeSend : function() {
				$('#ajaxLoading_coberturas').show();	
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