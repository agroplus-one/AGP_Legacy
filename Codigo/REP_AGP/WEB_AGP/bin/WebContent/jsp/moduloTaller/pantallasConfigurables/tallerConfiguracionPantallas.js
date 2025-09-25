$(document).ready(function() {
	$('#controlesPantalla').validate({
		errorLabelContainer : '#panelAlertasValidacion',
		wrapper : 'li', 
		highlight : function(element, errorClass) {
			
		},
		unhighlight : function(element, errorClass) {
			
		},
		rules: {
			'txDisAncho': {required: true, digits: true},
			'txDisAlto': {required: true, digits: true}
		},
		messages: {
			'txDisAncho': {required : 'El campo Ancho es obligatorio.', 
					       digits:    'El campo Ancho s&oacute;lo puede contener d&iacute;gitos.'},
	        'txDisAncho': {required : 'El campo Alto es obligatorio.', 
		                   digits:    'El campo Alto s&oacute;lo puede contener d&iacute;gitos.'}
		}
	});	
	cambiarUso();	
	getControlesPantalla($('#idPantalla').val());
});

function limpiarAlertas() {
	$('#panelAlertasValidacion').html('');
	$('#panelAlertasValidacion').hide();
	$('label[id*="campoObligatorio_"]').each(function() {
		$(this).hide();
	});
	$('#panelMensajeValidacion').html('');
	$('#panelMensajeValidacion').hide();
}

function volver() {
	jConfirm('Al salir se perder\u00E1n los cambios no guardados. \u00BFDesea continuar?', 'Solicitud de confirmaci\u00F3n', function(r) {
		if (r) window.location.href = 'pantallasConfigurables.html?operacion=volver';
	});
}

function cambiarUso() {
	limpiarAlertas();
	var selectedUso = $('#slctUsos').find(':selected').val();
	if (selectedUso == -1) {
		$('#componentesContainer').html('');
	} else {
		getArbolComponentes($('#lineaseguroid').val(), selectedUso);
	}
}

function createArbolComponentes(jsonData, selectedUso) {
	var treeHtml = '<table width="100%" border="0" cellpadding="0" cellspacing="0">';
	for (i = 0; i < jsonData.length; i++) {
		treeHtml += '<tr onmouseover="this.style.backgroundColor=\'#DDDDDD\';" onmouseout="this.style.backgroundColor=\'white\';" onclick="arbolComponentesFolderClick(' + i + ')">';
		treeHtml += '<td width="18px"><img src="jsp/img/flecha1.gif" id="imgFlecha1' + i + '" />';
		treeHtml += '<img src="jsp/img/flecha2.gif" id="imgFlecha2' + i + '" style="display:none;" /></td>';
		treeHtml += '<td width="18px"><img src="jsp/img/folderclose.gif" id="imgFolderClose' + i + '" />';
		treeHtml += '<img src="jsp/img/folderopen.gif" id="imgFolderOpen' + i + '" style="display:none;" /></td>';
		treeHtml += '<td><label class="literal">' + jsonData[i].desUbicacion + ' (' + jsonData[i].conceptos.length + ')</label></td>';		
		treeHtml += '</tr>';
		for (j = 0; j < jsonData[i].conceptos.length; j++) {
			treeHtml += '<tr id="trConcepto' + i + '_' + j + '" style="display:none;" onmouseover="this.style.backgroundColor=\'#DDDDDD\';" onmouseout="this.style.backgroundColor=\'white\';">';
			treeHtml += '<td>&nbsp;</td><td>&nbsp;</td><td>';
			if (jsonData[i].conceptos[j].datoVariable == 'S') {
				treeHtml += '<div style="cursor:hand;" draggable="true" ondragstart="drag(event, '
						+ jsonData[i].conceptos[j].codConcepto
						+ ', '
						+ jsonData[i].codUbicacion
						+ ', '
						+ selectedUso
						+ ');"><img src="jsp/img/document.png" /><label class="literal" style="color:#FF0000"><strong>'
						+ jsonData[i].conceptos[j].nomConcepto
						+ '</strong></label></div>';		
			} else {
				treeHtml += '<img src="jsp/img/document.png" /><label class="literal">' + jsonData[i].conceptos[j].nomConcepto + '</label>';		
			}	
			treeHtml += '</td></tr>';
		}
	}
	treeHtml += '</table>';
	return treeHtml;
}

function arbolComponentesFolderClick(nodeIndex) {
	limpiarAlertas();
	$('#imgFlecha1' + nodeIndex).toggle();
	$('#imgFlecha2' + nodeIndex).toggle();
	$('#imgFolderClose' + nodeIndex).toggle();
	$('#imgFolderOpen' + nodeIndex).toggle();
	$('tr:[id^="trConcepto' + nodeIndex + '_"]').each(function() {
		$(this).toggle();
	});
}

function createControlPantalla(jsonObj, parent, selected) {
	var $controlContainer = $('<div/>', {
		className: 'detalI',
		id: 'divControl_' + jsonObj.codConcepto,
		'isSelected': selected,
		'nombre': jsonObj.nombre,
		'codTipoNaturaleza': jsonObj.codTipoNaturaleza,
		'desTipoNaturaleza': jsonObj.desTipoNaturaleza,
		'tamanio': jsonObj.tamanio,
		'ubicacion_codigo': jsonObj.ubicacion_codigo,
		'ubicacion_descripcion': jsonObj.ubicacion_descripcion,
		'tabla_asociada': jsonObj.tabla_asociada,
		'ocurrencia': jsonObj.multiple,
		'descripcion': jsonObj.descripcion,
		'mostrar': jsonObj.mostrar,
		'mostrarCarga': jsonObj.mostrarCarga,
		'idorigendedatos': jsonObj.idorigendedatos,
		'idtipo': jsonObj.idtipo,
		'deshabilitado': jsonObj.deshabilitado,
		'valorCargaPac': jsonObj.valorCargaPac,
		'codConcepto': jsonObj.codConcepto,
		'codUso': jsonObj.codUso,
		css: {			
			position: 'absolute',
			verticalAlign: 'top',
			top: jsonObj.y,
			left: jsonObj.x,
			border: (selected ? '1px solid red' : 'none'),
			cursor: 'hand'
		},
		click: function() {
			$('#controlsContainer').find('div[id^="divControl_"]').each(function() {
				$(this).css('border', 'none');
				$(this).removeAttr('isSelected');
			});
			$(this).css('border', '1px solid red');
			$(this).attr('isSelected', true);
			cargarDatosUbicacion($(this).attr('nombre'), $(this).attr('desTipoNaturaleza'),
					parseInt($(this).attr('tamanio')), parseInt($(this).attr('ubicacion_codigo')),
					$(this).attr('ubicacion_descripcion'), parseInt($(this).attr('tabla_asociada')), 
					$(this).attr('ocurrencia'), $(this).attr('descripcion'));
			var etiqueta = $controlContainer.find('label').text();
			var ancho = $controlContainer.find('[isControlPpal=true]').css('width').replace('px', '');
			var alto = $controlContainer.find('[isControlPpal=true]').css('height').replace('px', '');
			cargarDatosDisenho(etiqueta, ancho, alto, $(this).attr('mostrar'), $(this).attr('mostrarCarga'), 
					parseInt($(this).attr('idorigendedatos')), parseInt($(this).attr('idtipo')), 
					$(this).attr('deshabilitado'), $(this).attr('valorCargaPac'));
		}
	});
	$('<label/>', {
		text: jsonObj.etiqueta		
	}).appendTo($controlContainer);
	createTipoControl(jsonObj, $controlContainer);
	$('<input/>', {
		type: 'checkbox',
		'controlSelector': true,
		css: {
			marginLeft: 10,
			display: 'inline'
		}
	}).appendTo($controlContainer);
	$('<img/>', {
		src: 'jsp/img/displaytag/delete.png',
		height: 12,
		width: 12,
		css: {
			marginLeft: 5,
			cursor: 'hand',
			display: 'inline'
		},
		click: function() {
			jConfirm('Va a eliminar un componente. \u00BFDesea continuar?', 'Solicitud de confirmaci\u00F3n', function(r) {
				if (r) {
					$controlContainer.remove();
					limpiaDatosUbicacion();
					limpiaDatosDisenho();
				}
			});
			
		}
	}).appendTo($controlContainer);
	$controlContainer.draggable({
		containment: 'parent'
	});
	parent.append($controlContainer);
	if (selected) $controlContainer.click();
}

function createTipoControl(jsonObj, parent) {
	var $fieldContainer = $('<div/>', {
		css: {
			display: 'inline'
		}
	});	
	switch (jsonObj.idtipo) {
	case 1: // Cuadro de texto
		$('<input/>', {
			type: 'text',
			disabled: 'disabled',
			isControlPpal: true,
			css: {
				width: jsonObj.ancho,
				height: jsonObj.alto,
				marginLeft: 10
			}
		}).appendTo($fieldContainer);
		break;
	case 3: // Cuadro de fecha
		$('<input/>', {
			type: 'text',
			disabled: 'disabled',
			isControlPpal: true,
			css: {
				width: jsonObj.ancho,
				height: jsonObj.alto,
				marginLeft: 10
			}
		}).appendTo($fieldContainer);
		$('<img/>', {
			src: 'jsp/img/calendar.gif',
			css: {
				marginLeft: 5
			}
		}).appendTo($fieldContainer);
		break;
	case 4: // Cuadro de texto multilinea
		$('<textarea/>', {
			disabled: 'disabled',
			isControlPpal: true,
			css: {
				width: jsonObj.ancho,
				height: jsonObj.alto,
				resize: 'none',
				marginLeft: 10
			}
		}).appendTo($fieldContainer);
		break;
	case 5: // Lista seleccionable multiple
		$('<select/>', {
			disabled: 'disabled',
			isControlPpal: true,
			css: {
				width: jsonObj.ancho,
				height: jsonObj.alto,
				marginLeft: 10
			}
		}).appendTo($fieldContainer);
		break;
	case 6: // Lupa desplegable
		if (jsonObj.codConcepto == 134) { // TRATAMIENTO ESPECIFICO FECHA FIN GARANTIAS [134]
			$('<input/>', {
				type: 'text',
				disabled: 'disabled',
				isControlPpal: true,
				css: {
					width: 100,
					height: jsonObj.alto,
					marginLeft: 10
				}
			}).appendTo($fieldContainer);
		} else {
			$('<input/>', {
				type: 'text',
				disabled: 'disabled',
				css: {
					width: 50,
					height: jsonObj.alto,
					marginLeft: 10
				}
			}).appendTo($fieldContainer);
			$('<input/>', {
				type: 'text',
				disabled: 'disabled',
				isControlPpal: true,
				css: {
					width: jsonObj.ancho,
					height: jsonObj.alto,
					marginLeft: 10
				}
			}).appendTo($fieldContainer);
		}		
		$('<img/>', {
			src: 'jsp/img/magnifier.png',
			css: {
				marginLeft: 5
			}
		}).appendTo($fieldContainer);
		break;
	case 7: // Lista selec. multi. checkable
		var $subDiv = $('<div/>', {
				isControlPpal: true,
				css: {
					width: jsonObj.ancho,
					height: jsonObj.alto,
					border: '1px #CCCCCC solid',
					overflowX: 'hidden',
					overflowY: 'scroll'
				}
			});
		$('<input/>', {
			type: 'checkbox',
			disabled: 'disabled'
		}).appendTo($subDiv);
		$('<p/>', {
			text: 'Elemento',
			css: {
				marginLeft: 5,
				display: 'inline'
			}
		}).appendTo($subDiv);
		$('<br/>', {}).appendTo($subDiv);		
		$('<input/>', {
			type: 'checkbox',
			disabled: 'disabled'
		}).appendTo($subDiv);
		$('<p/>', {
			text: 'Elemento',
			css: {
				marginLeft: 5,
				display: 'inline'
			}
		}).appendTo($subDiv);
		$('<p/>', {
			text: '\'Elemento\' se repite tantas veces como indique el origen de datos asociado.',
			css: {
				marginTop: 5,
				display: 'block'
			}
		}).appendTo($subDiv);
		$subDiv.appendTo($fieldContainer);
		break;
	default:
		$('<label/>', {
			text: 'Tipo Campo no esperado'		
		}).appendTo($fieldContainer);
		break;
	}
	parent.append($fieldContainer);
}

function limpiaDatosUbicacion() {
	cargarDatosUbicacion('', '', '', '', '', '', '', '');
}

function cargarDatosUbicacion(nombre, tipo, tamanio, ubicacion_codigo,
		ubicacion_descripcion, tabla_asociada, ocurrencia, descripcion) {
	$('#txtNomCampo').val(nombre);
	$('#txtUbTipo').val(tipo);
	$('#txtUbTamano').val(tamanio);
	$('#txtUbNumero').val(ubicacion_codigo);
	$('#txtUbDatos').val(ubicacion_descripcion);
	$('#txtUbDestino').val(tabla_asociada);
	$('#txtOcurrencia').val(ocurrencia);
	$('#txaUbExplicacion').val(descripcion);
}

function limpiaDatosDisenho() {
	cargarDatosDisenho('', '', '', 'N', 'N', '', '', 'N', '');
}

function cargarDatosDisenho(etiqueta, ancho, alto, mostrar, mostrarCarga,
		idorigendedatos, idtipo, deshabilitado, valorCargaPac) {	
	$('#txtDisNomMostrado').val(etiqueta);
	$('#txDisAncho').val(ancho);
	$('#txDisALto').val(alto);
	if ('S' == mostrar) {
		$('#chk_mostrarSiempre').attr('checked', 'checked');
	} else {
		$('#chk_mostrarSiempre').removeAttr('checked');
	}
	if ('S' == mostrarCarga) {
		$('#chk_mostrarCargaPac').attr('checked', 'checked');
	} else {
		$('#chk_mostrarCargaPac').removeAttr('checked');
	}
	$('#cmbOrigenDatos').val(idorigendedatos);
	$('#cmbDisTipo').val(idtipo);
	if ('S' == deshabilitado) {
		$('#chk_campoDeshabilitado').attr('checked', 'checked');
	} else {
		$('#chk_campoDeshabilitado').removeAttr('checked');
	}
	$('#txtValorCargaPac').val(valorCargaPac);
}

function cambiaDatosControl() {
	limpiarAlertas();
	var $controlContainer = $('#controlsContainer').find('div[isSelected=true]');
	if ($controlContainer.length > 0) {
		$controlContainer.find('label').text($('#txtDisNomMostrado').val().toUpperCase());
		$controlContainer.attr('mostrar', ($('#chk_mostrarSiempre').attr('checked') ? 'S' : 'N'));
		$controlContainer.attr('mostrarCarga', ($('#chk_mostrarCargaPac').attr('checked') ? 'S' : 'N'));
		$controlContainer.attr('idorigendedatos', parseInt($('#cmbOrigenDatos').val()));
		$controlContainer.attr('deshabilitado', ($('#chk_campoDeshabilitado').attr('checked') ? 'S' : 'N'));
		if ($('#txtUbTipo').val() == 'DATE' && $('#txtValorCargaPac').val() != '' && !$('#txtValorCargaPac').val().match(/^(0?[1-9]|[12][0-9]|3[01])[\/](0?[1-9]|1[012])[\/]\d{4}$/)) {
			jAlert('Formato incorrecto en el campo Valor carga PAC/COPY (dd/mm/yyyy)');
			$('#txtValorCargaPac').val('');
		}
		$controlContainer.attr('valorCargaPac', $('#txtValorCargaPac').val());
	}
}

function cambiaTipoControl() {
	limpiarAlertas();
	if ($('#cmbDisTipo').val() != -1) {
		var $controlContainer = $('#controlsContainer').find('div[isSelected=true]');
		if ($controlContainer.length > 0) {
			var y = $controlContainer.css('top');
			var x = $controlContainer.css('left');
			var codConcepto = $controlContainer.attr('codConcepto');
			var codTipoNaturaleza = $controlContainer.attr('codTipoNaturaleza');
			var ocurrencia = $controlContainer.attr('ocurrencia');
			var codUso = $controlContainer.attr('codUso');
			$controlContainer.remove();	
			createControlPantalla({	
				'codConcepto' : codConcepto,
				'codTipoNaturaleza' : codTipoNaturaleza,
				'codUso' : codUso,
				'y': y,
				'x': x,
				'nombre': $('#txtNomCampo').val(),
				'desTipoNaturaleza': $('#txtUbTipo').val(),
				'tamanio': parseInt($('#txtUbTamano').val()),
				'ubicacion_codigo': parseInt($('#txtUbNumero').val()),
				'ubicacion_descripcion': $('#txtUbDatos').val(),
				'tabla_asociada': parseInt($('#txtUbDestino').val()),
				'multiple': ocurrencia,
				'descripcion': $('#txaUbExplicacion').val(),
				'etiqueta': $('#txtDisNomMostrado').val(),
				'ancho': parseInt($('#txDisAncho').val()),
				'alto': parseInt($('#txDisALto').val()),				
				'mostrar': ($('#chk_mostrarSiempre').attr('checked') ? 'S' : 'N'),
				'mostrarCarga': ($('#chk_mostrarCargaPac').attr('checked') ? 'S' : 'N'),
				'idorigendedatos': parseInt($('#cmbOrigenDatos').val()),
				'idtipo': parseInt($('#cmbDisTipo').val()),
				'deshabilitado': ($('#chk_campoDeshabilitado').attr('checked') ? 'S' : 'N'),
				'valorCargaPac': $('#txtValorCargaPac').val()
			}, $('#controlsContainer'), true);
		}
	}
}

function modificarAncho() {
	limpiarAlertas();
	if ($('#txDisAncho').valid()) {
		$('#txDisAncho').val(parseInt($('#txDisAncho').val()));
		modificarAspectoControl($('#txDisAncho').val(), 1);
	}
}

function modificarAlto() {
	limpiarAlertas();
	if ($('#txDisALto').valid()) {
		$('#txDisALto').val(parseInt($('#txDisALto').val()));
		modificarAspectoControl($('#txDisALto').val(), 2);
	}
}

function modificarAspectoControl(valor, aspecto) {
	var aspecto_ancho = 1;
	var aspecto_alto = 2;
	$('#controlsContainer').find('div[isSelected=true]').find('[isControlPpal=true]').each(function() {
		if (aspecto == aspecto_ancho) {
			$(this).css('width', valor);
		} else if (aspecto == aspecto_alto) {
			$(this).css('height', valor);
		}	
	});
}

function igualarVertical() {
	limpiarAlertas();
	igualarCampos(1);
}

function igualarHorizontal() {
	limpiarAlertas();
	igualarCampos(2);
}

function igualarCampos(tipo) {
	var igualar_x = 1;
	var igualar_y = 2;
	var valor = null;
	// PRIMERA ITERACION: OBTENEMOS EL VALOR REFERENCIA
	$('#controlsContainer').find('input:checked[controlSelector=true]').each(function() {
		var valorActual = null;
		if (tipo == igualar_x) {
			valorActual = parseInt($(this).parent().css('left').replace('px', ''));
		} else {
			valorActual = parseInt($(this).parent().css('top').replace('px', ''));
		}
		if (valor == null || valorActual < valor) {
			valor = valorActual;
		}	
	});
	// SEGUNDA ITERACION: IGUALAMOS
	$('#controlsContainer').find('input:checked[controlSelector=true]').each(function() {
		if (tipo == igualar_x) {
			$(this).parent().css('left', valor);
		} else {			
			$(this).parent().css('top', valor);
		}
		$(this).removeAttr('checked');
	});
}

function cleanBox_click() {
	limpiarAlertas();
	jConfirm('Se eliminar\u00E1n los componentes seleccionados. \u00BFDesea continuar?', 'Solicitud de confirmaci\u00F3n', function(r) {		
		if (r) {
			var debeLimpiar = false;
			$('#controlsContainer').find('input:checked[controlSelector=true]').each(function() {
				$(this).parent().remove();
				debeLimpiar ^= $(this).parent().attr('isSelected');
			});
			if (debeLimpiar) {
				limpiaDatosDisenho();
				limpiaDatosUbicacion();
			}
		}
	});	
}

function limpiar() {
	limpiarAlertas();
	jConfirm('Se eliminar\u00E1n todos los componentes. \u00BFDesea continuar?', 'Solicitud de confirmaci\u00F3n', function(r) {
		if (r) {
			$('#controlsContainer').find('div').each(function() {
				$(this).remove();
			});
			limpiaDatosDisenho();
			limpiaDatosUbicacion();
		}
	});		
}

function reloadData() {
	limpiarAlertas();
	$('#controlsContainer').find('div').each(function() {
		$(this).remove();
	});
	limpiaDatosDisenho();
	limpiaDatosUbicacion();
	getControlesPantalla($('#idPantalla').val());
}

function drag(event, codConcepto, codUbicacion, codUso) {
	event.dataTransfer.setData("codConcepto", codConcepto);
	event.dataTransfer.setData("codUbicacion", codUbicacion);
	event.dataTransfer.setData("codUso", codUso);
}

function allowDrop(event) {
	event.preventDefault();
}

function anhadirControl(event) {
	event.preventDefault();
	var codConcepto = event.dataTransfer.getData("codConcepto");	
	var codUbicacion = event.dataTransfer.getData("codUbicacion");
	var codUso = event.dataTransfer.getData("codUso");
	var top = event.clientY - $('#controlsContainer').offset().top;
	var left = event.clientX - $('#controlsContainer').offset().left;
	anhadirControlPantalla($('#lineaseguroid').val(), codConcepto, codUbicacion, codUso, top, left);
	limpiarAlertas();
}

function guardar() {
	limpiarAlertas();
	var jsonCampos = [];
	$('#controlsContainer').find('div[id^="divControl_"]').each(function() {
		var jsonCampo = {
				'codConcepto'       : $(this).attr('codConcepto'),
				'ubicacion_codigo'  : $(this).attr('ubicacion_codigo'),
				'codUso'            : $(this).attr('codUso'),
				'idorigendedatos'   : $(this).attr('idorigendedatos'),
				'idtipo'            : $(this).attr('idtipo'),
				'x'                 : $(this).css('left').replace('px', ''),
				'y'                 : $(this).css('top').replace('px', ''),
				'etiqueta'          : $(this).find('label').text(),
				'ancho'             : $(this).find('[isControlPpal=true]').css('width').replace('px', ''),
				'alto'              : $(this).find('[isControlPpal=true]').css('height').replace('px', ''),
				'mostrar'           : $(this).attr('mostrar'),
				'mostrarCarga'      : $(this).attr('mostrarCarga'),
				'deshabilitado'     : $(this).attr('deshabilitado'),
				'valorCargaPac'     : $(this).attr('valorCargaPac')
		};
		jsonCampos.push(jsonCampo);
	});
	guardarPantalla($('#lineaseguroid').val(), $('#idPantalla').val(), jsonCampos);
}