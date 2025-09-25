function doWorkCalculo() {
	document.getElementById('main').operacion.value='calcular';
	var countCalculables = 0;
	var countTotales = 0;
	var inputs = document.getElementsByTagName('input');
	for (var i=0; i < inputs.length; i++) {
		var isCalculable = inputs[i].getAttribute('name').indexOf('calculable') != -1;
		if (isCalculable)
			countTotales++;
		if (isCalculable && inputs[i].getAttribute('value').indexOf('true') != -1) {
			document.getElementById('validComps').value = document.getElementById('validComps').value + inputs[i].getAttribute('name') + '|';
			countCalculables++;
		}
	}
	if (countCalculables > 0 && countCalculables != countTotales) {
		if(confirm('S\u00F3lo se enviar\u00E1n las comparativas sin errores de rechazo, \u00BFest\u00E1 seguro?')) { 
			$.blockUI.defaults.message = '<h4> Realizando el C\u00E1lculo de Importes.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 				
			doSubmit('main');
		}
	} else {
		$.blockUI.defaults.message = '<h4> Realizando el C\u00E1lculo de Importes.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 				
		doSubmit('main');
	}
}

function doSubmit(formName) {
	if(formName == 'consulta' && document.getElementById('main').origenllamada.value == 'pago') {
		formName = formName + 'Rev';
	}
	document.getElementById(formName).submit();
}

/**
* Redirige a la pantalla de datos de parcela
*/
function updateParcela(codPoliza, numhoja, numparcela){
    $('#numhojaDP').val(numhoja);
    $('#numparcelaDP').val(numparcela);
	$('#idpolizaDP').val(codPoliza);
	$('#datosParcela').submit();
}

function updateExplotacion(codPoliza, numExplotacion){	    
	$('#idPolizaExplotaciones').val(codPoliza);
	$('#numexplotacion').val(numExplotacion);	
	$('#datosExplotacion').submit();
}

function imprimirErr(id){
	var div;
	var imp;
	div = document.getElementById(id);//seleccionamos el objeto
	imp = window.open(""); //damos un titulo
	imp.document.open();     //abrimos
	imp.document.write('<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />');
	imp.document.write('<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />');
	imp.document.write('<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />');
	imp.document.write(div.innerHTML);//agregamos el objeto
	imp.document.write('<script language=\"javascript\">var elements = document.getElementsByTagName(\"a\");'+
	'for(i=0;i<elements.length;i++){'+
	'elements [i].onclick = function() {return false;}}'+
	'</'+ 'script>');	
	imp.document.close();
}

// Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio //
function imprimirReducida() {
	var frm = document.getElementById('pasarADefinitiva');
	frm.method.value = 'doImprimirPoliza';
	frm.imprimirReducida.value = 'true';
	frm.target = "_blank";
	frm.submit();	
}

// ejecuta el boton de imprimir cuando el swConfirmacion == true.
function imprimirswConfirm(){
	var frm = document.getElementById('imprimir');
	frm.operacion.value = 'imprimirPoliza';
	frm.target="_blank";
	frm.submit();
} 

function salirSwConfirmacion(){	
	var frm = document.getElementById('imprimir');
	frm.operacion.value = 'salir';
	frm.target="_self";
	frm.submit();
}

// Pet. 22208 ** MODIF TAM (02.03.2018) ** Fin //

// Vuelve al listado de utilidades
function volverUtilidades () {
	var frm = document.getElementById ('volver');
	frm.submit();
}

function grabarDefFueraContratacion(idPoliza){
	// Muestra el aviso de paso a definitiva
	blockUIPasoADefinitiva();
	var frm = document.getElementById('pasarADefinitiva');
	frm.resultadoValidacion.value ="true";
	frm.grabFueraContratacion.value = 'grabarFueraContratacion';
	frm.idpoliza.value = idPoliza;
	frm.target="";
	frm.submit();
}

function polizaFirmada(idPoliza){
	var frm = document.getElementById('pasarADefinitiva');
	frm.docFirmada.value = 'S';
	frm.idpoliza.value = idPoliza;
	frm.target="";
	frm.submit();
}

function volverListado () {
	$('#volverUtilidades').submit();
}

function blockUIPasoADefinitiva () {
	$.blockUI.defaults.message = '<h4> Realizando el paso a definitiva.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}

function volverComparativas() {
	$('#formComparativas').submit();
}

function xmlValidation(filaComparativa){
	var frm = document.getElementById('formUtilidades');
	frm.filaComparativa.value = filaComparativa;
	frm.method.value = 'doGetXMLValidacion';
	frm.submit();
}

function uploadDocAndSign() {
	$.blockUI.defaults.message = '<h4> Generando documentaci\u00F3n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$.ajax({
		type : 'POST',
		url : 'firmaTableta.html',
		data : {
				'method'      : 'doUploadDocGed',
				'idInternoPe' : $('#idInternoPe').val(),
				'idPoliza'    : $('#idpoliza').val(),
				'codUsuario'  : $('#codUsuario').val()
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
				$('#idDocumentum').val(datos.idDocumentum);
				var ruleData = {
						NOMBRE_DOC:'',
						RUTA_DOC:'',
						NOMBRE_DOC_AMPL:(datos.prodTecnico == 2009 ? 'SEGPRP' + datos.idDocumentum : 'SEGAGP' + datos.idDocumentum),
						RUTA_DOC_AMPL:'/aplicaciones/Seguros/pdf_tmp/',
						LINEA_PANTALLA_1:'RGA - CONTRATACION-SOLICITUD',
						LINEA_PANTALLA_2:datos.idPoliza,
						LINEA_PANTALLA_3:'',
						LINEA_PANTALLA_4:'Conforme a las condiciones del Contrato cuya informacion previa y copia se ha entregado',
						IND_TABLETA:'N',
						IND_TABLETA_2:'N',
						IND_FO_FD:'S',
						ACTN_CD:'EMITIR DOCUMENTO',
						COD_IDIOMA:'01',
						ID_EXT_PE_001:datos.nifCifAseg,
						ID_INTERNO_PE_001:datos.idInternoPe,
						NOMBRE_PERSONA_001:datos.nomAseg,
						TIPO_INTERVINIENTE_001:'TOMADOR',
						CLAVE_ANUL_DI:'EMISION SEGUROS: ' + datos.prodTecnico + '-' + datos.idPoliza,
						DI_TEXT_ARG_1:'10058',
						NOMBRE_DOC_SAL:'',
						RUTA_DOC_SAL:'/aplicaciones/Seguros/pdf/',
						NOMB_DOC_SAL_AMPL:'',
						RUTA_DOC_SAL_AMPL:'',
						IND_SOPOR_DURA:'S',
						IND_AC_RVIA:'S',
						COD_DOC:'GW',
						DESC_PRODUCTO:'Agroplus Alta de poliza',
						PRODUCTO:datos.prodTecnico,
						RGA_APLICACION_ORIGEN_V:'SG',
						COD_NRBE_EN_001:'0198'
				};
				modalConfiguration = new ModalConfiguration('Firma: ' + datos.nomAseg, datos.nifCifAseg);
				var ruleInfo = new RuleInfo('RGA_FIRMA_EXTERNA_SEGUROS_NAV', ruleData);
				ApiNtfUtils.initRuleLaunch(modalConfiguration, ruleInfo, 'firmaResponse');			
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

var firmaResponse = function(response) {
	var frm = document.getElementById('pasarADefinitiva');
	var idPol = frm.idpoliza.value;
	$('#docFirmada').val('N');
	if (response && idPol) {
		if (response.RETORNO) {
			if (response.RETORNO == '00001') {
				$('#firmaDiferida').val(response.IND_FD_FIRMA);
				$('#docFirmada').val('S');
				grabarDefFueraContratacion(idPol);
				polizaFirmada(idPol);
			} else if (response.RETORNO == '10003') {
				$.ajax({
					type : 'POST',
					url : 'firmaTableta.html',
					data : {
							'method'      : 'doUploadDocGed',
							'idInternoPe' : $('#idInternoPe').val(),
							'idPoliza'    : $('#idpoliza').val(),
							'codUsuario'  : $('#codUsuario').val(),
							'errorFirma'  : 'S'
						   },
					async : true,
					dataType : 'json',
					success : function(datos) {
						alert('Operaci\u00F3n Cancelada', 'Firma en Tableta');
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
				
			} else {
				$('#panelAlertasValidacion').html('Error devuelto por el componente de firma en tableta: ' + response.RTRN_CD);
				$('#panelAlertasValidacion').show();
			}			
		} else {
			$.ajax({
				type : 'POST',
				url : 'firmaTableta.html',
				data : {
						'method'      : 'doUploadDocGed',
						'idInternoPe' : $('#idInternoPe').val(),
						'idPoliza'    : $('#idpoliza').val(),
						'codUsuario'  : $('#codUsuario').val(),
						'errorFirma'  : 'S'
					   },
				async : true,
				dataType : 'json',
				success : function(datos) {
					$('#panelAlertasValidacion').html('No se encuentra la respuesta del componente de firma en tableta.');
					$('#panelAlertasValidacion').show();
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
	} else {
		$.ajax({
			type : 'POST',
			url : 'firmaTableta.html',
			data : {
					'method'      : 'doUploadDocGed',
					'idInternoPe' : $('#idInternoPe').val(),
					'idPoliza'    : $('#idpoliza').val(),
					'codUsuario'  : $('#codUsuario').val(),
					'errorFirma'  : 'S'
				   },
			async : true,
			dataType : 'json',
			success : function(datos) {
				$('#panelAlertasValidacion').html('Error insesperado en la llamada a la firma en tableta.');
				$('#panelAlertasValidacion').show();
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
};