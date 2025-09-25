// SEMAFOROS PARA VALIDACION PARCIAL DE FORMULARIO SEGUN ACCION
// VER DECLARACION DE required EN DEFINICION DE $('#datosParcela').validate
var sigPacSWValidation = false;
var irPanelCapAsegValidation = false;
var isAlreadySaved = false;

$(document).ready(function() {
	
	Array.prototype.includes = function(obj) {
		for (var i = 0; i < this.length; i++) {
			if (obj == $.trim(this[i])) {
				return true;
			}
		}
		return false;
	};
	
	switchCabecera(false);
	
	// Se sobreescribe la css de blockUI para que al deshabilitar el panel de grupos de raza no se muestre el cursor de espera y lo haga el de por defecto
	$.blockUI.defaults.css.cursor = 'default';
	$.blockUI.defaults.overlayCSS.cursor = 'default';
	
	if($('#modoLectura').val() == 'modoLectura') {
		
		$('#datosParcela :input').attr('disabled', 'disabled');	
		
	} else {
		
		document.onkeydown = function(e) {
			var evento = e || window.event;
		    switch (evento.keyCode) {
		        case 34:
		        	if(!$('#divDatosIdent').attr('isBlocked')) {
		        		$(document).keypress(irPanelCapAseg());
		        	}
		            break;
		        case 33:
		        	if($('#divDatosIdent').attr('isBlocked')) {
		        		$(document).keydown(irPanelDatIdent());
		        	}
		            break;
		        default:
		        	break;
		    }
		};
		
		$('#datosParcela').validate({
			errorLabelContainer : '#panelAlertasValidacion',
			wrapper : 'li', highlight : function(element,errorClass) {
				$('#campoObligatorio_' + element.id).show();
			},
			unhighlight : function(element,errorClass) {
				$('#campoObligatorio_' + element.id).hide();
			},
			rules: {
				'cultivo': {required: true, digits: true, isValidCultivo:   true},
				'variedad': {required: function(element){return !sigPacSWValidation}, digits: true, isValidVariedad:  true},
				'provinciaSigpac': {required: true, digits: true}, 
				'terminoSigpac': {required: true, digits: true}, 
				'agregadoSigpac': {required: true, digits: true}, 
				'zonaSigpac': {required: true, digits: true}, 
				'poligonoSigpac': {required: true, digits: true}, 
				'parcelaSigpac': {required: true, digits: true}, 
				'recintoSigpac': {required: function(element){return !sigPacSWValidation}, digits: true}, 
				'codProvincia': {required: function(element){return !sigPacSWValidation}, digits: true, isValidProvincia: true},
				'codComarca': {required: function(element){return !sigPacSWValidation}, digits: true, isValidComarca:   true},
				'codTermino': {required: function(element){return !sigPacSWValidation}, digits: true, isValidTermino:   true},
				'codSubTermino': {required: function(element){return !sigPacSWValidation && $('#subtermino').val().length == 0}, regex: '[a-zA-Z0-9\\s]'},
				'capitalAsegurado.codtipoCapital': {required: function(element){return !sigPacSWValidation && !irPanelCapAsegValidation;}, digits: true}, 
				'capitalAsegurado.superficie': {required: function(element){return !sigPacSWValidation && !irPanelCapAsegValidation;}, number: true},
				// SE ELIMINA LA VALIDACION DE LIMITES DE PRODUCCION POR ORDEN EXPRESA DE RGA
				// CORREO 'RE: GDLD-50776 - PTC-6488 - PP línea 300/2020' DEL 19/11/2020
				//'capitalAsegurado.produccion': {required: function(element){return !sigPacSWValidation && !irPanelCapAsegValidation;}, number: true, min: function(element){return $('#produccion_limMin').val().length ? $('#produccion_limMin').val() : 0;}, max: function(element){return $('#produccion_limMax').val().length ? $('#produccion_limMax').val() : 999999999.99;}},
				'capitalAsegurado.produccion': {required: function(element){return !sigPacSWValidation && !irPanelCapAsegValidation;}, number: true, min: 0, max: 999999999.99},
				'capitalAsegurado.precio': {required: function(element){return !sigPacSWValidation && !irPanelCapAsegValidation;}, number: true, min: function(element){return $('#precio_limMin').val().length ? $('#precio_limMin').val() : 0;}, max: function(element){return $('#precio_limMax').val().length ? $('#precio_limMax').val() : 999999.9999;}}
			},
			messages: {
				'cultivo': {required : 'El campo Cultivo es obligatorio.', 
						    digits: 'El campo Cultivo s&oacute;lo puede contener d&iacute;gitos.',
						    isValidCultivo: 'El c&oacute;digo de cultivo no est&aacute; asociado a la clase seleccionada.'},
				'variedad': {required : 'El campo Variedad es obligatorio.', 
						     digits: 'El campo Variedad s&oacute;lo puede contener d&iacute;gitos.',
						     isValidVariedad: 'El c&oacute;digo de variedad no est&aacute; asociado a la clase seleccionada.'},
				'provinciaSigpac': {required : 'El campo Provincia (SIGPAC) es obligatorio.', 
								   	digits: 'El campo Provincia (SIGPAC) s&oacute;lo puede contener d&iacute;gitos.'},	
			   	'terminoSigpac': {required : 'El campo T&eacute;rmino (SIGPAC) es obligatorio.', 
				   				  digits: 'El campo T&eacute;rmino (SIGPAC) s&oacute;lo puede contener d&iacute;gitos.'},
			   	'agregadoSigpac': {required : 'El campo Agregado es obligatorio.', 
				   				   digits: 'El campo Agregado s&oacute;lo puede contener d&iacute;gitos.'},
			   	'zonaSigpac': {required : 'El campo Zona es obligatorio.', 
				   			   digits: 'El campo Zona s&oacute;lo puede contener d&iacute;gitos.'},
			   	'poligonoSigpac': {required : 'El campo Pol&iacute;gono es obligatorio.', 
				   				   digits: 'El campo Pol&iacute;gono s&oacute;lo puede contener d&iacute;gitos.'},
			   	'parcelaSigpac': {required : 'El campo Parcela es obligatorio.', 
			   					  digits: 'El campo Parcela s&oacute;lo puede contener d&iacute;gitos.'},
			   	'recintoSigpac': {required : 'El campo Recinto es obligatorio.', 
			   					  digits: 'El campo Recinto s&oacute;lo puede contener d&iacute;gitos.'},
				'codProvincia': {required : 'El campo Provincia es obligatorio.', 
					   		     digits: 'El campo Provincia s&oacute;lo puede contener d&iacute;gitos.',
					   		     isValidProvincia: 'El c&oacute;digo de provincia no est&aacute; asociado a la clase seleccionada.'},
				'codComarca': {required : 'El campo Comarca es obligatorio.', 
					   	       digits: 'El campo Comarca s&oacute;lo puede contener d&iacute;gitos.',
					   	       isValidComarca:'El c&oacute;digo de comarca no est&aacute; asociado a la clase seleccionada.'},
				'codTermino': {required : 'El campo T&eacute;rmino es obligatorio.', 
						       digits: 'El campo T&eacute;rmino s&oacute;lo puede contener d&iacute;gitos.',
						       isValidTermino:'El c&oacute;digo del t&eacute;rmino no est&aacute; asociado a la clase seleccionada.'},
				'codSubTermino': {required: 'El campo Subt&eacute;rmino es obligatorio.',
								  regex: 'Valor incorrecto en el campo Subt&eacute;rmino.'},
				'capitalAsegurado.codtipoCapital': {required : 'El campo Tipo Capital es obligatorio.', 
													digits: 'El campo Tipo Capital s&oacute;lo puede contener d&iacute;gitos.'},
				'capitalAsegurado.superficie': {required : 'El campo Superficie es obligatorio.', 
												number: 'Valor incorrecto en el campo Superficie.'},
				'capitalAsegurado.produccion': {required : 'El campo Producci&oacute;n es obligatorio.', 
												number: 'Valor incorrecto en el campo Producci&oacute;n.',
												range: 'Valor fuera de rango en el campo Producci&oacute;n.',
												min: 'El campo Producci&oacute;n es inferior al valor m&iacute;nimo permitido.',
												max: 'El campo Producci&oacute;n excede el valor m&aacute;ximo permitido.'},
				'capitalAsegurado.precio': {required : 'El campo Precio es obligatorio.', 
											number: 'Valor incorrecto en el campo Precio.',
											min: 'El campo Precio es inferior al valor m&iacute;nimo permitido.',
											max: 'El campo Precio excede el valor m&aacute;ximo permitido.'}
			  }
			});
		
	    jQuery.validator.addMethod('isValidCultivo', function(value, element, params) { 		
			return isValidCultivo();			
		});
	
		jQuery.validator.addMethod('isValidVariedad', function(value, element, params) { 		
			return isValidVariedad();			
		});
		
		jQuery.validator.addMethod('isValidProvincia', function(value, element, params) { 		
			return isValidProvincia();			
		});
		
		jQuery.validator.addMethod('isValidComarca', function(value, element, params) { 		
			return isValidComarca();			
		});
		
		jQuery.validator.addMethod('isValidTermino', function(value, element, params) { 		
			return isValidTermino();			
		});	
	
		if($('#tipoParcela').val() == 'P') {
			configDVFields();
			// Deshabilitar el panel de tipos de capital
			$('#divCapAseg').block({message:null});			
			$('#nomParcela').focus();
		} else {
			getCamposMascara();
			// Deshabilitar el panel de datos identificativos y sus campos
			$('#divDatosIdent').block({message:null});		
			$('#divDatosIdent :input').attr('disabled', 'disabled');	
			$('#produccion').rules('remove');
			$('#produccion').attr('disabled', 'disabled');	
			$('#produccion').val(0);
			$('#capital').focus();
		}	
		
		if ($('#capital').val() != '') {		
			getDatosConcepto('126', $('#capital').val());
		}
		
		if ($('#idSigParcela').val() == '') {		
			$('#btnGuardarSig').hide();
		}
	}
	
	// Comprobar si tiene coberturas
	if ($('#tieneCoberturas').val() == 'true') {		
		cargarCobertParcelas();
		$('#contenedorCoberturas').show();
		grabaChekCobParcela();		
	} else {
		$('#contenedorCoberturas').hide();
	}
	
	loadType7Fields();
	
	loadDesCptosDvs();
});

/**
 * Redireccion al listado de parcelas
 */
function volver() {
	if ($('#modoLectura').val() != 'modoLectura') {
		jConfirm('Al salir se perder\u00E1n los datos introducidos. \u00BFDesea continuar?', 'Solicitud de confirmaci\u00F3n', function(r) {
			if (r) $('#listadoParcelas').submit();
		});
	} else {
		$('#listadoParcelas').submit();
	}
}

/**
 * Limpia los valores del formulario
 */
function limpiar() {
	limpiarAlertas();
	jConfirm('Se perder\u00E1n los datos introducidos. \u00BFDesea continuar?', 'Solicitud de confirmaci\u00F3n', function(r) {
		if (r) {
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
	});
}

function limpiarDatosIdent() {
	$('#codParcela').val('');
	$('#divDatosIdent :input').val('');
}

function limpiarCapAseg() {
	/* MODIF TAM (30.10.2020) */
	/* De momento no lo ocultamos, por que en Ganado no se oculta, depende de la parcela, no del Capital Asegurado */
	/*$('#contenedorCob').html('');*/
	/*$('#coberelegFS').hide();*/
	$('#idCapitalAsegurado').val('');
	$('#divCapAseg :input:[type="text"]').val('');
	$('#divCapAseg :input:[type="hidden"]').val('');
	$('#divCapAseg :input:[type="checkbox"]').attr('checked', false);
	$('#produccion_listProducciones').val('');
	$('#produccion_limMin').val('');
	$('#produccion_limMax').val('');
	$('#precio_listPrecios').val('');
	$('#precio_limMin').val('');
	$('#precio_limMax').val('');
	marcarFilaTC();
}

/**
 * Comprueba si los datos del panel 'Datos Identificativos' son correctos. En
 * caso afirmativo habilita el panel 'Capital Asegurado' y deshabilita el panel
 * 'Datos identificativos'
 */
function irPanelCapAseg() {
	limpiarAlertas();
	if ($('#modoLectura').val() != 'modoLectura') {
		irPanelCapAsegValidation = true;
		if ($('#datosParcela').valid()) {
			validaDatosIdent(function() {
				getCamposMascara();
				$('#divCapAseg').unblock();
				$('#divDatosIdent').attr('isBlocked', 'true');
				$('#divDatosIdent').block({
					message : null
				});
				$('#capital').focus();				
			});
		}	
		irPanelCapAsegValidation = false;
	}
}

/**
 * Pide confirmacion al usuario para deshabilitar el panel 'Capital Asegurado'
 * borrando todos los datos y habilitar el panel 'Datos Identificativos'
 */
function irPanelDatIdent() {
	limpiarAlertas();
	if ($('#modoLectura').val() != 'modoLectura') {
		jConfirm('Al cambiar de bloque se perder\u00E1n los datos introducidos. \u00BFDesea continuar?', 'Solicitud de confirmaci\u00F3n', function(r) {
			if (r) {
				limpiarCapAseg();
				$('#divCapAseg').block({
					message : null
				});
				$('#divCapAseg').removeAttr('isBlocked');
				$('#divDatosIdent').unblock();
				isAlreadySaved = false;
			}
		});
	} 
}

function limpiarAlertas() {
	$('#panelAlertasValidacion').html('');
	$('#panelAlertasValidacion').hide();
	$('label[id*="campoObligatorio_"]').each(function() {
		$(this).hide();
	});
	$('#panelMensajeValidacion').html('');
	$('#panelMensajeValidacion').hide();
}

function isValidCultivo() {
	var res = true;
	if ($('#valorRestriccion_CultivoIN').val() != ''  && $('#cultivo').val() != '') {
		var lista = limpiaLista($('#valorRestriccion_CultivoIN'));
		if (lista.length > 0) {
			res = lista.includes($('#cultivo').val());
		}
	}
	return res;
}

function isValidVariedad() {
	var res = true;
	if ($('#valorRestriccion_VariedadIN').val() != '' && $('#variedad').val() != '') {
		var lista = limpiaLista($('#valorRestriccion_VariedadIN'));
		if (lista.length > 0) {
			res = lista.includes($('#variedad').val());
		}
	}
	return res;
}

function isValidProvincia() {
	var res = true;
	if ($('#valorRestriccion_ProvinciaIN').val() != '' && $('#provincia').val() != '') {
		var lista = limpiaLista($('#valorRestriccion_ProvinciaIN'));
		if (lista.length > 0) {
			res = lista.includes($('#provincia').val());
		}
	}
	return res;
}

function isValidComarca() {
	var res = true;
	if ($('#valorRestriccion_ComarcaIN').val() != '' && $('#comarca').val() != '') {
		var lista = limpiaLista($('#valorRestriccion_ComarcaIN'));
		if (lista.length > 0) {
			res = lista.includes($('#comarca').val());
		}
	}
	return res;
}

function isValidTermino() {
	var res = true;
	if ($('#valorRestriccion_TerminoIN').val() != '' && $('#termino').val() != '') {
		var lista = limpiaLista($('#valorRestriccion_TerminoIN'));
		if (lista.length > 0) {
			res = lista.includes($('#termino').val());
		}
	}
	return res;
}

/**
 * quita los parentesis de la cadena con la lista y devuelve un array
 */
function limpiaLista(strlista) {
	var cadena1 = strlista.val().replace('(', '');
	var cadena2 = cadena1.replace(')', '');
	var lista = cadena2.split(',');
	return lista;
}

function editarTC(idCapitalAsegurado) {

	limpiarAlertas();
	$.blockUI.defaults.message = '<h4>Obteniendo datos de Tipo de Capital.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });	
	// Se lanza con delay para permitit a la UI hacer el bloqueo antes de la llamada sincrona
	setTimeout(function(){
		var json = getDatosTC(idCapitalAsegurado);
		if (json) {
			limpiarCapAseg();
			setCapAsegFromJson(json);
			marcarFilaTC(idCapitalAsegurado);
			/* Incidencia RGA (10.12.2020) ** Inicio - Pet. 63485- FII*/
			cargarCobertParcelas();
			/* Incidencia RGA (10.12.2020) ** Fin - Pet. 63485- FII*/
		}
		$.unblockUI();
	}, 1000);	
	isAlreadySaved = false;
}

function duplicarTC(idCapitalAsegurado) {
	limpiarAlertas();
	if ($('#modoLectura').val() != 'modoLectura') {
		$.blockUI.defaults.message = '<h4>Obteniendo datos de Tipo de Capital.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });	
		// Se lanza con delay para permitit a la UI hacer el bloqueo antes de la llamada sincrona
		setTimeout(function(){
			var json = getDatosTC(idCapitalAsegurado);
			if (json) {
				limpiarCapAseg();
				setCapAsegFromJson(json);
				$('#idCapitalAsegurado').val('');
				marcarFilaTC();
			}
			$.unblockUI();
		}, 1000);	
		isAlreadySaved = false;
	}
}

function muestraRangoPrecio() {
	if ($('#precio_listPrecios').val().length) {
		abrirVisorRangoPrecios($('#precio_listPrecios').val());
	} else {
		$('#visor_PrecioProduccion').hide();
		$('#panelMensajeValidacion').html('Para consultar el rango de precios debe primero calcular.');
		$('#panelMensajeValidacion').show();
	}
}

function muestraRangoProduccion() {
	if ($('#produccion_rdtosLibres').val() == 'true') {
		$('#visor_PrecioProduccion').hide();
		$('#panelMensajeValidacion').html('Parcela sin l&iacute;mites de rendimientos.');
		$('#panelMensajeValidacion').show();
	} else {
		if ($('#produccion_listProducciones').val().length) {		
			abrirVisorRangoProducciones($('#produccion_listProducciones').val());
		} else {
			$('#visor_PrecioProduccion').hide();
			$('#panelMensajeValidacion').html('Para consultar el rango de producciones debe primero calcular.');
			$('#panelMensajeValidacion').show();
		}
	}
}

function guardarReplicar() {
	guardarTC(savePandRep, function() {
		// Solo lanzamos la replica de la parcela si no hay errores
		// Ya que esta funcion solo se llama como callback en el guardarTC
		// cuando no hay fallo en el boton 'guardar y replicar' 
		$.blockUI.defaults.message = '<h4>Replicando Parcela.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		duplicarParcela(function() {
			var json = getDatosParcela($('#codParcela').val());
			if (json) {
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
				setParcelaFromJson(json);
				/* Incidencia RGA (10.12.2020) ** Inicio - Pet. 63485- FII*/
				cargarCobertParcelas();
				/* Incidencia RGA (10.12.2020) ** Fin - Pet. 63485- FII*/
			}
			$.unblockUI();			
		});			
	});
}

function guardarIrSiguiente() {
	guardarTC(savePandNext, function() {
		// Solo lanzamos la carga de la siguiente parcela si no hay errores
		// Ya que esta funcion solo se llama como callback en el guardarTC
		// cuando no hay fallo en el boton 'guardar y siguiente' 
		$.blockUI.defaults.message = '<h4>Obteniendo datos de Parcela.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		// Se lanza con delay para permitit a la UI hacer el bloqueo antes de la llamada sincrona
		setTimeout(function(){
			var json = getDatosParcela($('#idSigParcela').val());
			if (json) {
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
				setParcelaFromJson(json);
			}
			$.unblockUI();
		}, 1000);	
	});
}

function crearFilaTablaTC(capAseg) {
	if ($('#tcListEmptyTr').length) $('#tcListEmptyTr').hide();
	var trHtml = '<tr id="tcListTr' + capAseg.id + '">';
	trHtml += '<td class="literalborde">\r\n';
	trHtml += '<a href="javascript:editarTC(' + capAseg.id + ')"><img src="jsp/img/displaytag/edit.png" alt="Editar" title="Editar"/></a>\r\n';
	trHtml += '<a href="javascript:duplicarTC(' + capAseg.id + ')"><img src="jsp/img/displaytag/duplicar.png" alt="Duplicar" title="Duplicar"/></a>\r\n';
	trHtml += '<a href="javascript:borrarTC(' + capAseg.id + ')"><img src="jsp/img/displaytag/delete.png" alt="Eliminar" title="Eliminar"/></a>';					
	trHtml += '</td>';
	trHtml += '<td class="literalborde">' + capAseg.desTipoCapital + '</td>';
	trHtml += '<td class="literalborde">' + capAseg.superficie + '</td>';
	trHtml += '<td class="literalborde">' + capAseg.produccion + '</td>';
	trHtml += '<td class="literalborde">' + capAseg.precio + '</td>';
	trHtml += '</tr>';
	$('#tcList tr:last').after(trHtml);	
}

function marcarFilaTC(idCapAseg) {
	$('#tcList tr[id^=tcListTr]').each(function() {
		var idRow = $(this).attr('id').replace('tcListTr', '');
		$(this).find('td').each(function() {
			if (idRow == idCapAseg) {
				$(this).removeClass('literalborde');
				$(this).addClass('literalbordeAzul');
			} else {
				$(this).removeClass('literalbordeAzul');
				$(this).addClass('literalborde');
			}
		});
	});
}

function setPrecio(listPrecios) {	
	var precioMin = null;
	var precioMax = null;
	if (listPrecios == null) {
		$('#precio_listPrecios').val('');
	} else {		
		$('#precio_listPrecios').val(JSON.stringify(listPrecios));		
		$.each(listPrecios, function(index, value) {
			precioMin = (precioMin == null || value.limMin < precioMin) ? value.limMin : precioMin;
			precioMax = (precioMax == null || value.limMax > precioMax) ? value.limMax : precioMax;
		});
	}	
	$('#precio_limMin').val(precioMin);
	$('#precio_limMax').val(precioMax);
	$('#precio').val(precioMax == null ? 0 : precioMax);
}

function setProduccion(listProducciones) {
	if ($('#tipoParcela').val() == 'P' && !$('#produccion').attr('disabled')) {
		var prodMin = null;
		var prodMax = null;
		if (listProducciones == null) {
			$('#produccion_listProducciones').val('');
		} else {
			$('#produccion_listProducciones').val(JSON.stringify(listProducciones));
			$.each(listProducciones, function(index, value) {
				prodMin = (prodMin == null || value.limMin < prodMin) ? value.limMin : prodMin;
				prodMax = (prodMax == null || value.limMax > prodMax) ? value.limMax : prodMax;
			});
		}
		$('#produccion_limMin').val(prodMin);
		$('#produccion_limMax').val(prodMax);
		$('#produccion').val(prodMax);
	}
}

function calcular() {
	limpiarAlertas();
	if ($('#modoLectura').val() != 'modoLectura') {
		calculoPrecioProduccion();
		cargarCobertParcelas();
		isAlreadySaved = false;
	}
}
