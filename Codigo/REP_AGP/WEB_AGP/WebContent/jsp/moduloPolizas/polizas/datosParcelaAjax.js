var saveTC = 1;
var saveTCandRep = 2;
var savePandExit = 3;
var savePandNew = 4;
var savePandRep = 5;
var savePandNext = 6;

function doSigPac2Agro() {
	limpiarAlertas();
	if ($('#modoLectura').val() != 'modoLectura') {
		sigPacSWValidation = true;
		if ($('#datosParcela').valid()) {
			$.ajax({
				type : 'POST',
				url : 'datosParcela.html',
				data : {
						'method'     : 'doSigPac2Agro',
						'codParcela' : $('#codParcela').val(),
						'prov'       : $('#provinciaSigpac').val(),
						'term'       : $('#terminoSigpac').val(),
						'agr'        : $('#agregadoSigpac').val(),
						'zona'       : $('#zonaSigpac').val(),
						'pol'        : $('#poligonoSigpac').val(),
						'parc'       : $('#parcelaSigpac').val(),
						'codPlan'    : $('#codplan').val(),
						'codLinea'   : $('#codlinea').val(),
						'codCultivo' : $('#cultivo').val()
					   },
				async : true,
				dataType : 'json',
				success : function(datos) {
					if (datos.length == 1) {
						if (datos[0].swErrorMsg) {
							$('#panelAlertasValidacion').html(datos[0].swErrorMsg);
							$('#panelAlertasValidacion').show();
						} else {
							$('#provincia').val(datos[0].codProvincia);
							$('#desc_provincia').val(datos[0].nomProvincia);
							$('#comarca').val(datos[0].codComarca);
							$('#desc_comarca').val(datos[0].nomComarca);
							$('#termino').val(datos[0].codTermino);
							$('#desc_termino').val(datos[0].nomTermino);
							$('#subtermino').val(datos[0].codSubTermino);
						}
					} else {
						$('#provincia').val(datos[0].codProvincia);
						$('#desc_provincia').val(datos[0].nomProvincia);
						$('#comarca').val(datos[0].codComarca);
						$('#desc_comarca').val(datos[0].nomComarca);
						var oldVal = $('#valorRestriccion_TerminoIN').val();
						var newVal = '';
						for (i = 0; i < datos.length; i++) {
							newVal += datos[i].codTermino + ','
						}
						$('#valorRestriccion_TerminoIN').val(newVal);
						lupas.muestraTabla('TerminoIN','principio', '', '');
						$('#valorRestriccion_TerminoIN').val(oldVal);
					}
				},
				beforeSend : function() {
					$('#sigpacBtn').hide();
					$('#ajaxLoading_sigpac').show();
					$('#provincia').val('');
					$('#desc_provincia').val('');
					$('#comarca').val('');
					$('#desc_comarca').val('');
					$('#termino').val('');
					$('#desc_termino').val('');
					$('#subtermino').val('');						
				},
				complete : function() {
					$('#sigpacBtn').show();
					$('#ajaxLoading_sigpac').hide();
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
		sigPacSWValidation = false;
	}
}

function validaDatosIdent(callback) {
	$.ajax({
		type : 'POST',
		url : 'datosParcela.html',
		data : {
				'method'        : 'doValidaDatosIdent',
				'lineaseguroid' : $('#lineaseguroid').val(),
				'claseId'       : $('#claseId').val(),
				'cultivo'       : $('#cultivo').val(),
				'variedad'      : $('#variedad').val(),
				'provincia'     : $('#provincia').val(),
				'comarca'       : $('#comarca').val(),
				'termino'       : $('#termino').val(),
				'subtermino'    : $('#subtermino').val(),
				'idpoliza'      : $('#isAnexo').val() == 'true' ? $('#idPoliza').val() : $('#idpoliza').val(),
				'nifcif'        : $('#nifAsegurado').val()
			   },
		async : true,
		dataType : 'json',
		success : function(datos) {
			if (datos.isValid) {
				callback();
			} else {
				var errorMsg = '';
				for (i = 0; i < datos.errorMsgs.length; i++) {
					errorMsg += datos.errorMsgs[i] + '<br/>';
				}
				$('#panelAlertasValidacion').html(errorMsg);
				$('#panelAlertasValidacion').show();
			}
		},
		beforeSend : function() {
			$('#btnPanelDatIdent').hide();
			$('#ajaxLoading_datIdent').show();
			$('#panelAlertasValidacion').hide();
			$('#panelAlertasValidacion').html('');						
		},
		complete : function() {
			$('#btnPanelDatIdent').show();
			$('#ajaxLoading_datIdent').hide();
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

/**
 * Si los datos del formulario son validos, da de alta el capital asegurado
 * (y la parcela si no existiera). En funcion del parametro deja o no los datos
 * del capital asegurado cargados en la pantalla
 */
function duplicarParcela(callback) {
	if ($('#modoLectura').val() != 'modoLectura') {
		$.ajax({
			type : 'POST',
			url : $('#isAnexo').val() == 'true' ? 'parcelasAnexoModificacion.html' : 'datosParcela.html',
			data : {
					'method'    : 'doDuplicarAjax',
					'idParcela' : $('#codParcela').val()
				   },
			async : false,
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
					$('#codParcela').val(datos.codParcela);	
					if (callback) {
						callback();
					}
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
}

/**
 * Si los datos del formulario son validos, da de alta el capital asegurado
 * (y la parcela si no existiera). En funcion del parametro deja o no los datos
 * del capital asegurado cargados en la pantalla
 */
function guardarTC(action, callback) {
	limpiarAlertas();	
	var htmlCobParcelas = $('#contenedorCob').html();
	guardarValoresCobParcelas();
	if ($('#modoLectura').val() != 'modoLectura') {
		var debeValidar = !isAlreadySaved || (isAlreadySaved && (action == saveTC || action == saveTCandRep)); 
		var isValid = !debeValidar || (debeValidar && $('#datosParcela').valid());																								
		if (isValid) {
			if (action == saveTC || action == saveTCandRep) {
				$.blockUI.defaults.message = '<h4>Guardando Tipo de Capital.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			} else {
				$.blockUI.defaults.message = '<h4>Guardando Parcela.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			}
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			// Se lanza con delay para permitit a la UI hacer el bloqueo antes de la llamada sincrona
			setTimeout(function(){			
				$.ajax({
					type : 'POST',
					url : $('#isAnexo').val() == 'true' ? 'parcelasAnexoModificacion.html' : 'datosParcela.html',
					data : {
							'method'         : 'doGuardarTC',
							'lineaseguroid'  : $('#lineaseguroid').val(),
							'claseId'        : $('#claseId').val(),
							'nifcif'         : $('#nifAsegurado').val(),
							'parcela'        : JSON.stringify(getParcelaJson()),
							'isAlreadySaved' : isAlreadySaved
						   },
					async : false,
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
							$('#desc_cultivo').val(datos.parcela.desCultivo);
							$('#desc_variedad').val(datos.parcela.desVariedad);
							$('#desc_provincia').val(datos.parcela.desProvincia);
							$('#desc_comarca').val(datos.parcela.desComarca);
							$('#desc_termino').val(datos.parcela.desTermino);						
							if (action == saveTC || action == saveTCandRep) {
								if ($('#idCapitalAsegurado').val() != '') {
									$('#tcList tr:[id="tcListTr' + datos.parcela.capitalAsegurado.id + '"]').remove();									
								}
								crearFilaTablaTC(datos.parcela.capitalAsegurado);
								marcarFilaTC();
								if (action == saveTCandRep) {
									$('#desc_capital').val(datos.parcela.capitalAsegurado.desTipoCapital);
									$('#idCapitalAsegurado').val('');
								} else {
									limpiarCapAseg();
									if ($('#tipoParcela').val() == 'E') {
										$('#produccion').val(0);
									}
									$('#contenedorCob').html(htmlCobParcelas);
									montarCoberturasParcelasNew(document.datosParcela);
									isAlreadySaved = true;
								}
								$('#codParcela').val(datos.parcela.codParcela);						
								$('#panelMensajeValidacion').html('Tipo Capital guardado correctamente.');
								$('#panelMensajeValidacion').show();
							} else {
								if (action == savePandExit) {
									$('#listadoParcelas').submit();
								} else {
									var msg = 'Parcela guardada correctamente.';
									if (action == savePandNext) {
										msg += ' Se muestran los datos de la siguiente parcela.';
									} else if (action == savePandRep) {
										$('#codParcela').val(datos.parcela.codParcela);	
									} else {
										limpiarCapAseg();	
										if ($('#tipoParcela').val() == 'P') {
											$('#tcList tr:[id^="tcListTr"]').remove();
											$('#tcListEmptyTr').show();
											limpiarDatosIdent();
											$('#divCapAseg').block({
												message : null
											});
											$('#divCapAseg').removeAttr('isBlocked');
											$('#divDatosIdent').unblock();
										}	
									}
									$('#panelMensajeValidacion').html(msg);
									$('#panelMensajeValidacion').show();
								}
							}	
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
				$.unblockUI();
				if (callback) {
					callback();
				}
			}, 1000);		
		} else {
			isAlreadySaved = false;
		}
	}
}

/**
 * Si los datos del formulario son validos, da de alta el capital asegurado
 * (y la parcela si no existiera). En funcion del parametro deja o no los datos
 * del capital asegurado cargados en la pantalla
 */
function borrarTC(idCapitalAsegurado) {
	limpiarAlertas();
	if ($('#modoLectura').val() != 'modoLectura') {
		jConfirm('Se va a borrar el capital asegurado. \u00BFDesea continuar?', 'Solicitud de confirmaci\u00F3n', function(r) {
			if (r) {
				$.ajax({
					type : 'POST',
					url : $('#isAnexo').val() == 'true' ? 'parcelasAnexoModificacion.html' : 'datosParcela.html',
					data : {
							'method'             : 'doBorrarTC',
							'idCapitalAsegurado' : idCapitalAsegurado
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
							$('#tcList tr:[id="tcListTr' + idCapitalAsegurado + '"]').remove();
							if ($('#idCapitalAsegurado').val() == idCapitalAsegurado) {
								limpiarCapAseg();
							}	
							if ($('#tcList tr:[id^="tcListTr"]').length == 0) {
								$('#tcListEmptyTr').show();
							}
							$('#panelMensajeValidacion').html('Tipo Capital borrado correctamente.');
							$('#panelMensajeValidacion').show();	
							isAlreadySaved = false;
						}
					},
					beforeSend : function() {
						$.blockUI.defaults.message = '<h4>Eliminando Tipo de Capital.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
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
		});
	}
}

function getDatosTC(idCapitalAsegurado) {
	var capAsegJson;
	$.ajax({
		type : 'POST',
		url : $('#isAnexo').val() == 'true' ? 'parcelasAnexoModificacion.html' : 'datosParcela.html',
		data : {
				'method'             : 'doObtenerDatosTC',
				'idCapitalAsegurado' : idCapitalAsegurado
			   },
		async : false,
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
				capAsegJson = datos.capAseg;
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
	return capAsegJson;
}


function getCamposMascara() {
	$.ajax({
		type : 'POST',
		url : 'datosParcela.html',
		data : {
				'method'         : 'doCamposMascara',
				'lineaseguroid'  : $('#lineaseguroid').val(),
				'listCodModulos' : $('#listCodModulos').val(),
				'parcela'        : JSON.stringify(getParcelaJson())
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
				$('#mustFillDVs').val(datos.mustFillDVs);
				configDVFields();
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

function calculoPrecioProduccion() {
	
	$.ajax({
		type : 'POST',
		url : 'datosParcela.html',
		data : {
				'method'  : 'doCalculoPrecioProduccion',
				'lineaseguroid'  : $('#lineaseguroid').val(),
				'listCodModulos' : $('#listCodModulos').val(),
				'parcela' : JSON.stringify(getParcelaJson()),
				'idAnexo': $('#idAnexo').val()
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
				if (datos.conceptosVacios != null) {
					$.each(datos.conceptosVacios, function(index, value) {
						switch (value) {
						case 126:
							//TIPO CAPITAL
							$('#campoObligatorio_capital').show();
							break;
						case 258:
							//SUPERFICIE
							$('#campoObligatorio_superficie').show();
							break;
						default:
							$('#campoObligatorio_cod_cpto_' + value).show();
							break;
						}							
					});
				}				
			} 
			$('#produccion_rdtosLibres').val(datos.rdtosLibres);
			setPrecio(datos.conceptosVacios == null ? datos.listPrecios : null);
			setProduccion(datos.conceptosVacios == null ? datos.listProducciones : null);
		},
		beforeSend : function() {
			$.blockUI.defaults.message = '<h4>Obteniendo precio y producción.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
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

function getDatosParcela(idParcela) {
	var parcelaJson;
	$.ajax({
		type : 'POST',
		url : $('#isAnexo').val() == 'true' ? 'parcelasAnexoModificacion.html' : 'datosParcela.html',
		data : {
				'method'      : 'doObtenerDatosParcela',
				'idpoliza'    : $('#idpoliza').val(),
				'idParcela'   : idParcela,
				'listaIdsStr' : $('#listaIdsStr').val()
			   },
		async : false,
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
				parcelaJson = datos.parcela;
				$('#idSigParcela').val(datos.idSigParcela);
				if ($('#idSigParcela').val() == '') {		
					$('#btnGuardarSig').hide();
				}
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
	return parcelaJson;
}

function getDatosConcepto(codConcepto, valor, event) {
	if(valor != '' && (codConcepto == '126' || (event == null || (event != null && event.eventCaller != 'lupa')))) {
		$.ajax({
			type : 'POST',
			url : 'datosParcela.html',
			data : {
					'method'         : 'doObtenerDatosConcepto',
					'lineaseguroid'  : $('#lineaseguroid').val(),
					'listCodModulos' : $('#listCodModulos').val(),
					'codConcepto'    : codConcepto,
					'valor'          : valor
				   },
			async : true,
			dataType : 'json',
			success : function(datos) {
				switch (codConcepto) {
				case '126':
					//TIPO CAPITAL
					$('#desc_capital').val(datos.desCpto);
					if ($('#tipoParcela').val() == 'P') {
						if (datos.cptoAsociadoTC == '68' || valor == '') {
							$('#produccion').rules('add', {
								required: function(element){return !sigPacSWValidation && !irPanelCapAsegValidation;}, 
								number: true, 
								// SE ELIMINA LA VALIDACION DE LIMITES DE PRODUCCION POR ORDEN EXPRESA DE RGA
								// CORREO 'RE: GDLD-50776 - PTC-6488 - PP línea 300/2020' DEL 19/11/2020
								min: 0,
								max: 999999999.99,
								messages : {
									required : 'El campo Producción es obligatorio.', 
									number: 'Valor incorrecto en el campo Producción.',
									range: 'Valor fuera de rango en el campo Producción.',
									min: 'El campo Producción es inferior al valor mínimo permitido.',
									max: 'El campo Producción excede el valor máximo permitido.'
								}
							});
							$('#produccion').removeAttr('disabled');
						} else {
							$('#produccion').rules('remove');
							$('#produccion').attr('disabled', 'disabled');	
							$('#produccion').val(0);
						}
					} else {
						if (datos.cptoAsociadoTC == '258' || valor == '') {
							$('#superficie').rules('add', {
								required: function(element){return !sigPacSWValidation && !irPanelCapAsegValidation;}, 
								number: true,
								messages : {
									required : 'El campo Superficie es obligatorio.', 
									number: 'Valor incorrecto en el campo Superficie.'
								}
							});
							$('#superficie').removeAttr('disabled');	
						} else {
							$('#superficie').rules('remove');
							$('#superficie').attr('disabled', 'disabled');	
							$('#superficie').val(0);
						}	
						$('#produccion').rules('remove');
						$('#produccion').attr('disabled', 'disabled');	
						$('#produccion').val(0);
					}				
					break;
				default:
					$('#des_cpto_' + codConcepto).val(datos.desCpto);
					break;
				}			
			}
		});
	}
}

function getParcelaJson() {	
	var jsonObj = {};	
	jsonObj['idpoliza'] = $('#isAnexo').val() == 'true' ? $('#idPoliza').val() : $('#idpoliza').val();
	jsonObj['idanexo'] = $('#isAnexo').val() == 'true' ? $('#idAnexo').val() : '';
	jsonObj['codParcela'] = $('#codParcela').val();
	jsonObj['tipoParcela'] = $('#tipoParcela').val();
	var datIdent = {};
	$('#divDatosIdent :input').each(function() {
		if ($(this).attr('id') == 'nomParcela') {
			var encodedVal = encodeURI($(this).val());
			datIdent[$(this).attr('id')] = encodedVal;
		} else {
			datIdent[$(this).attr('id')] = $(this).val();
		}
	});
	jsonObj['datIdent'] = datIdent;
	var capAseg = {};
	capAseg['id'] = $('#idCapitalAsegurado').val();
	capAseg['tipoCapital'] = $('#capital').val();
	capAseg['superficie'] = $('#superficie').val();
	capAseg['produccion'] = $('#produccion').val();
	capAseg['precio'] = $('#precio').val();
	var dvs = {};
	$('#divCapAseg :input:[id^="cod_cpto_"]:not([type="checkbox"]):not([id$="lupa_factores"])').each(function() {
		if ($(this).attr('disabled') != 'disabled') {
			dvs[$(this).attr('id')] = $(this).val();
		}
	});
	capAseg['dvs'] = dvs;	
	jsonObj['capitalAsegurado'] = capAseg;

	/* Coberturas parcelas */
	/* Pet.50776_63485-Fase II ** MODIF TAM (26.10.2020) ** Inicio */
	var riesgoCub = {};	
	if ($('#tieneCoberturas').val() == 'true') {		
		var contador = 1;
		$('#contenedorCob :input[id^="riesg_cub_"]').each(function() {
			var datos = $(this).attr('value');
			var listaDatos = datos.split('|');
			var isChecked = +$(this).attr('checked');			
			if (isChecked == true) {
				riesgoCub['riesgo_'+contador] = "363|-1|"+$('#lineaseguroid').val() + "|" + datos;
				contador = contador + 1;
			} else {
				riesgoCub['riesgo_'+contador] = "363|-2|"+$('#lineaseguroid').val() + "|" + datos;
				contador = contador + 1;
			}			
		});
		
		/* Tenemos que recorrer los select para comprobar que se ha seleccionado valor */
		$("select[id*='seleccionDatVar_']").each(function(){
			
			var valorSeleccionado = $(this).val();
			
			/* Comprobamos si el ultimo valor es X-> en cuyo caso querra decir que no tiene valor seleccionado */
			
			var valSel = valorSeleccionado.charAt(valorSeleccionado.length - 1);
	
			if (valSel != 'X'){
				riesgoCub['datVariable_'+contador] = "-1|"+$('#lineaseguroid').val() + "|" + valorSeleccionado;
				contador = contador + 1;
			}else{
				riesgoCub['datVariable_'+contador] = "-2|"+$('#lineaseguroid').val() + "|" + valorSeleccionado;
				contador = contador + 1;
			}			
		});
	}
	jsonObj['riesgoCub'] = riesgoCub;
	/* Pet.50776_63485-Fase II ** MODIF TAM (26.10.2020) ** Fin */
	
	return jsonObj;
}

function setParcelaFromJson(json) { 
	$('#codParcela').val(json.codParcela); 
	$('#tipoParcela').val(json.tipoParcela); 
	$('#nomParcela').val(json.nombreParcela);	 
	$('#cultivo').val(json.cultivo); 
	$('#desc_cultivo').val(json.desCultivo); 
	$('#variedad').val(json.variedad); 
	$('#desc_variedad').val(json.desVariedad);	 
	$('#provinciaSigpac').val(json.provinciaSigpac); 
	$('#terminoSigpac').val(json.terminoSigpac); 
	$('#agregadoSigpac').val(json.agregadoSigpac); 
	$('#zonaSigpac').val(json.zonaSigpac); 
	$('#poligonoSigpac').val(json.poligonoSigpac); 
	$('#parcelaSigpac').val(json.parcelaSigpac); 
	$('#recintoSigpac').val(json.recintoSigpac);	 
	$('#provincia').val(json.codProvincia); 
	$('#desc_provincia').val(json.desProvincia); 
	$('#comarca').val(json.codComarca); 
	$('#desc_comarca').val(json.desComarca); 
	$('#termino').val(json.codTermino); 
	$('#desc_termino').val(json.desTermino); 
	$('#subtermino').val(json.codSubTermino); 
	setCapAsegFromJson(json.capitalAsegurado); 
	$.each(json.capitalesAsegurados, function(index, value) { 
		crearFilaTablaTC(value); 
	}); 
	marcarFilaTC(json.capitalAsegurado.id); 
} 


function setCapAsegFromJson(json) {
	$('#idCapitalAsegurado').val(json.id);
	$('#capital').val(json.codtipoCapital);
	if ($('#capital').val() != '') {		
		getDatosConcepto('126', $('#capital').val());
	}
	$('#desc_capital').val(json.desTipoCapital);
	$('#superficie').val(json.superficie);
	$('#produccion').val(json.produccion);
	$('produccion_listProducciones').val(json.listProducciones);
	$('#precio').val(json.precio);
	$('precio_listPrecios').val(json.listPrecios);
	$('#divCapAseg :input:[id^="cod_cpto_"]:not([type="checkbox"]):not([id$="lupa_factores"])').each(function() {
		var jsonVal = '';
		var codCpto = $(this).attr('id').replace('cod_cpto_', '');
		$.each(json.datosVariablesParcela, function(index, value) {
			if (value.valor != null && value.valor != '' && value.valor != 'null' && codCpto == value.codconcepto) {
				jsonVal = value.valor;
			}
		});
		$(this).val(jsonVal);
		if ($(this).attr('tipoDV') == 6 && codCpto != 134) {
			// TIPO LUPA EXCEPTO FECHA FIN GARANTIAS [134]
			getDatosConcepto(codCpto, jsonVal);
		}
		if ($(this).attr('tipoDV') == 7) {
			// TIPO CHECKBOX MULTIPLE
			actualizaChksTipo7(codCpto);
		}
	});
}