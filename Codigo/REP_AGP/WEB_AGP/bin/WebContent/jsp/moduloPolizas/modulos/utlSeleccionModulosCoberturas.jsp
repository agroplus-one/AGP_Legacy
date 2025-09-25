<script type="text/javascript">
	/* Lista de módulos utilizada para las validaciones */
	var listaModulos = [];
	/* Lista de riesgos elegidas almacenadas en BBDD */
	var listaRiesgosElegidos = [];
	/* Lista de condiciones coberturas elegidas */
	var listaCondicionesCoberturasElegidas = [];
	/* Lista de vinculaciones entre filas */
	var listaVinculaciones = [];
	/* Lista de riesgos cubiertos elegible */
	var listaRiesgosCubiertosElegibles = [];
	/* Lista de coberturas básicas y elegibles */
	var listaCoberturasBasicasElegibles = [];
	/* Lista de coberturas RyD */
	var listaRyDElegibles = [];
	/* Tipolodia del asegurado */
	var tipologiaAsegurado = "";

	if (!String.prototype.startsWith) {
	    String.prototype.startsWith = function(searchString, position){
	      return this.substr(position || 0, searchString.length) === searchString;
	  };
	}
	
	
	if (!Array.prototype.includes) {
		Array.prototype.includes = function(obj) {
			for (var i = 0; i < this.length; i++) {
				if (obj == this[i]) {
					return true;
				}
			}
			return false;
		};
	}

	/*
	 ***                                                  ***
	 *** VALIDACIONES PREVIAS AL GUARDADO DE COMPARATIVAS ***
	 ***                                                  ***
	 */
	$(document).ready(
			function() {
				var URL = UTIL.antiCacheRand(document
						.getElementById("formComparativas").action);
				document.getElementById("formComparativas").action = URL;

				$('#formComparativas').validate({
					onclick : false,
					onfocusout : false,
					onkeyup : false,
					errorLabelContainer : "#panelAlertasValidacion",
					wrapper : "li",
					highlight : function(element, errorClass) {
						if (!renovablesElegidos())
							$(".campoObligatorioRenov").show();
						if (!tipologiaAsegElegido()) {
							$(".campoObligatorioTipologia").show();
						}
					},
					unhighlight : function(element, errorClass) {
						if (renovablesElegidos())
							$(".campoObligatorioRenov").hide();
						if (tipologiaAsegElegido()) {
							$(".campoObligatorioTipologia").hide();
						}
					},
					rules : {
						"modRenovable" : {
							renovablesElegidos : true
						},
						"tipologiaAseg" : {
							tipologiaAsegElegido : true
						},
						"comboElegible" : {
							elegiblesPorRiesgoElegidos : true
						},
						"vincRiesgos" : {
							vinculaciones : true
						},
						"msgBasicasElegibles" : {
							coberturasBasicasElegibles : true
						}
					}
				});

				var anexoId = $('#anexoModificacionId').val();
				if (anexoId == null || anexoId == 'undefined') {
					mostrarComparativasVisiblesInicial();
				}
			});

	jQuery.validator.addMethod("renovablesElegidos", renovablesElegidos,
			"Debe indicar si el módulo es renovable o no");
	jQuery.validator.addMethod("elegiblesPorRiesgoElegidos",
			elegiblesPorRiesgoElegidos,
			"Debe indicar los valores elegibles para la cobertura");
	jQuery.validator.addMethod("vinculaciones", vinculaciones,
			getMsgVinculaciones);
	jQuery.validator.addMethod("tipologiaAsegElegido", tipologiaAsegElegido,
			"Debe indicar la tipología del asegurado");
	jQuery.validator.addMethod("coberturasBasicasElegibles",
			coberturasBasicasElegibles, getMsgBasicasElegibles);

	// muestra las comparativas que han de mostrarse al inicio
	function mostrarComparativasVisiblesInicial() {
		// mostrar comparativas visibles al principio, mostramos la primera de cada módulo			
		var maxComp = parseInt($('#maxComparativas').val());
		// recorremos los modulos
		var modulos = $('#modulos').val();
		var modArray = modulos.split(',');
		$('#contComparativas').val(modArray.length);
		for (m = 0; m < modArray.length; m++) {
			if (modArray[m] != '') {
				for (i = 2; i <= maxComp; i++) {
					$('#compDIV_' + modArray[m] + '_' + i).hide();
				}
			}
		}
		// Ademas de las primeras por cada modulo, Se muestran las visibles por BBDD si existen
		var visibles = $('#compVisiblesBBDD').val();
		if (visibles != '') {
			var visArray = visibles.split(':');
			for (v = 0; v < visArray.length; v++) {
				$('#compDIV_' + visArray[v]).show();
			}
			var mostradas = dameCompMostradas();
			var mostradasArr = mostradas.split(',');
			$('#contComparativas').val(mostradasArr.length - 1);
		}
		ActivarBotonDuplicaMaxComp();
	}

	function dameCamposVinculados(idPadre) {
		var camposVincStr = '';
		var camposBloqVincAgriStr = $('#camposBloqVincAgri').val();
		if (camposBloqVincAgriStr != '') {
			var camposBloqVincAgriArr = camposBloqVincAgriStr.split(',');
			var camposBloqVincAgriArr = camposBloqVincAgriStr.split(',');
			for (var k = 0; k < camposBloqVincAgriArr.length; k++) {
				var camposVincArr = $.trim(camposBloqVincAgriArr[k]).replace(
						'[', '').replace(']', '').split('#');
				if (idPadre == ('select_' + camposVincArr[0])) {
					camposVincStr = camposVincArr[1];
				}
			}
		}
		return camposVincStr;
	}

	function dameValorVinculacion(idCampoVinc, valorPadre) {
		var numItems = 0;
		var result = '';
		var vinculacionesAgriArr = $('#listaVinculacionesAgri').val()
				.split(',');
		for (var k = 0; k < vinculacionesAgriArr.length; k++) {
			var valoresVincArr = $.trim(vinculacionesAgriArr[k]).replace('[',
					'').replace(']', '').split('#');
			if (valoresVincArr[0] == idCampoVinc) {
				var valoresArr = $.trim(valoresVincArr[1]).split('|');
				for (var l = 0; l < valoresArr.length; l++) {
					var valores = $.trim(valoresArr[l]).split(':');
					if (valorPadre == valores[0]) {
						if (++numItems < 2) {
							result = valores[1];
						} else {
							result.startsWith('DD') ? result += '_'
									+ valores[1] : result = 'DD' + result + '_'
									+ valores[1];
						}
					}
				}
			}
		}
		return result;
	}

	/* Pet. 63485 ** MODIF TAM (29.07.2020) ** Inicio */
	function validarVinculados(objId, rcElegible) {
		var camposVincStr = dameCamposVinculados(objId);
		if (camposVincStr != '') {
			var valorCampo = $('#' + objId).val();
			var nombreCampoVincArr = $.trim(camposVincStr).split(':');
			for (var i = 0; i < nombreCampoVincArr.length; i++) {
				var options = document.getElementById('select_'
						+ nombreCampoVincArr[i]).options;
				if (valorCampo == '') {
					$('#select_' + nombreCampoVincArr[i]).val('');
					$('#select_' + nombreCampoVincArr[i]).attr('disabled',
							'disabled');
					// Habilitamos todas las options					
					for (var m = 0; m < options.length; m++) {
						options[m].style.color = 'black';
					}
				} else {
					var valorVinc = dameValorVinculacion(nombreCampoVincArr[i],
							valorCampo);
					if (valorVinc.startsWith('DD')) {
						$('#select_' + nombreCampoVincArr[i]).val('');
					} else {
						if (valorVinc != '') {
							$('#select_' + nombreCampoVincArr[i])
									.val(valorVinc);
						}
					}
					// Marcamos las options no validas	
					var valoresValidosArr = valorVinc.replace('DD', '').split(
							'_');
					for (var m = 0; m < options.length; m++) {
						options[m].style.color = valoresValidosArr
								.includes(options[m].value) ? 'black' : 'red';
					}
					$('#select_' + nombreCampoVincArr[i])
							.removeAttr('disabled');
					$('#select_' + nombreCampoVincArr[i]).change();
				}
				validarVinculados('select_' + nombreCampoVincArr[i], rcElegible);
			}
		}
		if (rcElegible != '') {
			var rcElegibleElegido = $('#' + rcElegible).attr('checked');
			if (!rcElegibleElegido) {
				$('#' + objId).val('');
				$('#' + objId).attr('disabled', 'disabled');
			}
		}
	}

	function bloquearCamposconVinculacion() {
		var camposBloqVincAgri = $('#camposBloqVincAgri').val();
		if (camposBloqVincAgri != '') {
			var listacamposVinc = camposBloqVincAgri.split(',');
			for (i = 0; i < listacamposVinc.length; i++) {
				var camposVinc = $.trim(listacamposVinc[i]).replace('[', '')
						.replace(']', '').split('#');
				if (camposVinc.length > 1) {
					var nombrecampo = camposVinc[0];
					var nombrecampoVincStr = camposVinc[1];
					//1.- Hay que comprobar si el campo madre está informado				   
					var valorMadre = $('#select_' + nombrecampo).val();
					var nombrecampoVinc = nombrecampoVincStr.split(':');
					for (j = 0; j < nombrecampoVinc.length; j++) {
						if (valorMadre == '') {
							//2.- Si no está informado habrá que bloquear los campos vinculados.
							$('#select_' + nombrecampoVinc[j]).val('');
							$('#select_' + nombrecampoVinc[j]).attr('disabled',
									'disabled');
						} else {
							//3.- Marcamos las opciones incorrectas
							var valorVinc = dameValorVinculacion(
									nombrecampoVinc[j], valorMadre);
							var options = document.getElementById('select_'
									+ nombrecampoVinc[j]).options;
							var valoresValidosArr = valorVinc.replace('DD', '')
									.split('_');
							for (var m = 0; m < options.length; m++) {
								options[m].style.color = valoresValidosArr
										.includes(options[m].value) ? 'black'
										: 'red';
							}
						}
					}
				}
			}
		}
	}
	/* Pet. 63485 ** MODIF TAM (29.07.2020) ** Fin */

	function habilitarCamposComparativa(codModulo, contComp) {
		idCombo = (codModulo + '_' + contComp);
		//deshabilitamos selects y ponemos su valor vacío
		$("select[id^='select_" + idCombo + "']").each(function() {
			$(this).removeAttr('disabled');
		});
		$("input[id^='check_" + idCombo + "']").each(function() {			
			$(this).click();
			$(this).attr('checked', false);
		});
	}

	function deshabilitarBotonesComparativa(contComp, codModulo) {
		idCombo = (codModulo + '_' + contComp);
		//deshabilitamos selects y ponemos su valor vacío
		$("select[id^='select_" + idCombo + "']").each(function() {
			$(this).val('');
			$(this).attr('disabled', 'disabled');
		});
		//deshabilitamos checks
		$("input[id^='check_" + idCombo + "']").each(function() {
			if ($(this).attr('checked')) {
				$(this).attr('checked', false);
			}
		});
		//vaciamos renovable
		var combo = $("select[id='modRenovable_" + idCombo + "']").val();
		// Si este valor no es 0 o 1, no se supera la validación
		if (combo == '0' || combo == '1') {
			$("select[id='modRenovable_" + idCombo + "']").val('');
		}
		//vaciamos tipología asegurado			
		var comboTipologia = $("select[id='tipologiaAsegurado_" + idCombo
				+ "']");
		if (comboTipologia.length > 0) {
			// Existe, por lo que hay que validar que se ha elegido algún valor
			var valor = comboTipologia.val().length;
			if (valor > 0) {
				$("select[id='tipologiaAsegurado_" + idCombo + "']").val('');
			}
		}
	}

	function ActivarBotonDuplicaMaxComp() {
		var maxComp = parseInt($('#maxComparativas').val());
		var modArray = ($('#modulos').val()).split(',');
		for (m = 0; m < modArray.length; m++) {
			if (modArray[m] != '') {
				for (i = 0; i <= maxComp; i++) {
					var compMod = modArray[m] + '_' + i;
					$('#botonDuplica_' + compMod + '').hide();
				}
			}
		}
		//ocultar boton duplicar y dejar él último por módulo
		var mostradas = dameCompMostradas();
		var mostradasArr = mostradas.split(',');
		var mod = 0;
		var comp = 1;
		//SI SOLO HAY UN MODULO, COJO EL ULTIMO VALOR DE mostradasArr
		if (modArray.length == 1) {
			var ultValor = mostradasArr.length - 1;
			var arrayAux = mostradasArr[ultValor].split('_');
			mod = arrayAux[0];
			comp = arrayAux[1];
			//con estos valores muestro el boton
			var modcompp = mod + '_' + comp;
			$('#botonDuplica_' + modcompp + '').show();
		} else { //SI HAY MÁS DE UN MODULO
			//recorro los modulos
			for (md = 0; md < modArray.length; md++) {
				for (m = 0; m < mostradasArr.length; m++) {
					if (mostradasArr[m] != '') {
						var mArr2 = mostradasArr[m].split('_');
						if (modArray[md] == mArr2[0]) { //si estoy evaluando el modulo que toca
							//guardo los valores
							mod = mArr2[0];
							comp = mArr2[1];
						} else {
							if (modArray[md] <= mArr2[0]) {
								//significa que las variables guardadas son las buenas
								var modcompp = mod + '_' + comp;
								$('#botonDuplica_' + modcompp + '').show();
							}
						}
						//el ultimo caso no se muestra, para ello añado la siguiente condicion
						var ultimociclo = mostradasArr.length - 1;
						if (m == ultimociclo) {
							var modcompUltima = mod + '_' + comp;
							$('#botonDuplica_' + modcompUltima + '').show();
						}
					}
				}
			}
		}
	}

	/*
	Comprueba si para todos los módulos elegidos se ha elegido la tipología del asegurado
	 */
	function tipologiaAsegElegido() {
		var mostradas = dameCompMostradas();
		var mostradasArr = mostradas.split(',');
		for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los de los divs mostrados.
			if (mostradasArr[m] != '') {
				// Obtiene el combo de tipología de asegurado del módulo
				var combo = $("select[id='tipologiaAsegurado_"
						+ mostradasArr[m] + "']");
				// Comprueba si el combo existe en la pantalla (si aplica la tipología de asegurado)
				if (combo.length > 0) {
					// Existe, por lo que hay que validar que se ha elegido algún valor
					var valor = combo.val().length;
					if (valor <= 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/*
	Comprueba si para todos los módulos elegidos se ha indicado si es renovable o no
	 */
	function renovablesElegidos() {
		var mostradas = dameCompMostradas();
		var mostradasArr = mostradas.split(',');
		for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los selects de los divs mostrados.
			if (mostradasArr[m] != '') {
				// Obtiene valor del combo asociado al actual check marcado
				var combo = $(
						"select[id='modRenovable_" + mostradasArr[m] + "']")
						.val();
				// Si este valor no es 0 o 1, no se supera la validación
				if (combo != '0' && combo != '1') {
					return false
				}
			}
		}
		return true;
	}

	/*
	Comprueba si todos los valores elegibles de 'Condiciones de cobertura' se han elegido
	 */
	function elegiblesPorRiesgoElegidos() {
		var validacion = true
		var mostradas = dameCompMostradas();
		var mostradasArr = mostradas.split(',');
		for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los selects de los divs mostrados.
			/* Para todos los combos habilitados en los apartados 'Condiciones coberturas' */
			if (mostradasArr[m] != '') {
				$("select[id^='select_" + mostradasArr[m] + "']:enabled").each(
						function() {
							idCombo = $(this).attr('id');
							idCampoObligatorio = idCombo.replace('select_',
									'campoObligatorioSelect_');
							if ($(this).val().length == 0) {
								// Mostrar * rojos	
								$("[id^=" + idCampoObligatorio + "]").each(
										function() {
											$(this).show();
										});

								validacion = false;
							} else {
								// Oculta * rojos
								$("[id^=" + idCampoObligatorio + "]").each(
										function() {
											$(this).hide();
										});
							}
						});
			}
		}
		return validacion;
	}

	/*
	Comprueba las vinculaciones entre riesgos cubiertos
	 */
	function vinculaciones() {
		$('#msgVinculaciones').val('');
		var filaActual = 0;
		var filaOk = true;
		var maxGrupo = 0;
		// recojo el total de filas
		var filas = dameFilasVinc();
		// PRIMERO buscamos comparativas mostradas
		var mostradas = dameCompMostradas();
		var mostradasArr = mostradas.split(',');
		for (r = 0; r < mostradasArr.length; r++) { // solo recorremos los selects de los divs mostrados.
			if (mostradasArr[r] != '') {
				var cVigente = mostradasArr[r];
				// SEGUNDO recorremos cada fila de vinculacion
				var filasArr = filas.split(',');
				for (f = 0; f < filasArr.length; f++) {
					maxGrupo = dameMaxGrupoVinc(filasArr[f]);
					filaActual = filasArr[f];
					filaOk = true;
					// TERCERO recorremos la fila por cada grupo - si un grupo está OK se termina la revisión
					for (gr = 1; gr < maxGrupo + 1; gr++) {
						cumpleGr = true;
						var cumpleGr = recorreVincFilaGrupo(filasArr[f], gr,
								cVigente);
						if (cumpleGr) {
							filaOk = true;
							break;
						} else {
							filaOk = false;
						}
					}
					if (!filaOk) {
						generaMsgVincGrupos(filaActual,
								mostradasArr.length - 1, cVigente);
						return false;
					}
				}
			}
		}// fin mostradaas
		return true;
	}

	/*
	Recorremos las vinculaciones entre riesgos cubiertos por fila y grupo
	 */
	function recorreVincFilaGrupo(fi, gr, cVigente) {
		for (i = 0; i < listaVinculaciones.length; i++) {
			var v = listaVinculaciones[i];
			var comp_Mod = v.modulo + '_' + v.comparativa;
			if (v.grupoVinculacion == gr && v.fila == fi
					&& comp_Mod == cVigente) {
				var v = listaVinculaciones[i];
				idCheck = 'check_' + v.modulo + '_' + v.comparativa + '_'
						+ v.fila;
				// comprobamos si cumple para ese idCheck, comparativa y mod.
				var cumple = busqueda(idCheck, v, comp_Mod);
				if (!cumple) {
					return false;
				}
			}
		}
		return true;
	}

	/*
		Buscamos un idcheck y comprobamos para esa comparativa y módulo si está correcto segun vinculación entre riesgos cubiertos
	 */
	function busqueda(idCheck, v, comp_Mod) {
		var mostradas = dameCompMostradas();
		var mostradasArr = mostradas.split(',');
		for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los selects de los divs mostrados.
			if (mostradasArr[m] == comp_Mod) {
				// Si el estado del marcado del check corresponde con el del objeto 'v.elegida', hay que validar
				if (($("input[id^='" + idCheck + "']").attr('checked')) == v.elegida) {
					// Compone el id del check correspondiente a la vinculación y obtiene el objeto
					idCheckVinculado = 'check_' + v.modulo + '_'
							+ v.comparativa + '_' + v.vincFila;
					// Primero se comprueba que el check vinculado existe en pantalla
					if (($("input[id^='" + idCheckVinculado + "']").length > 0)) {
						// Si el estado del marcado del check vinculado no corresponde con el del objeto 'v.vincElegida' la validación no es correcta
						if (($("input[id^='" + idCheckVinculado + "']")
								.attr('checked')) != v.vincElegida) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/*
		Devuelve el total de filas separadas por comas
	 */
	function dameFilasVinc() {
		var filaTemp = 0;
		var filas = "";
		var primera = true;
		var comp = 0;
		for (i = 0; i < listaVinculaciones.length; i++) {
			var v = listaVinculaciones[i];
			if (v.fila != filaTemp) {
				filaTemp = v.fila;
				if (primera) {
					comp = v.comparativa;
					filas = filas + v.fila;
					primera = false;
				} else {
					if (v.comparativa != comp) {
						break;
					}
					filas = filas + "," + v.fila;
				}
			}
		}
		return filas;
	}

	function dameMaxGrupoVinc(fila) {
		var maxGrupo = 0;
		for (i = 0; i < listaVinculaciones.length; i++) {
			var v = listaVinculaciones[i];
			if (v.fila == fila && v.grupoVinculacion > maxGrupo) {
				maxGrupo = v.grupoVinculacion;
			}
		}
		return maxGrupo;
	}

	/*
	Genera el mensaje de error en validación de una fila para vinculaciones entre Riesgos
	 */
	function generaMsgVincGrupos(fila, totalComparativas, cVigente) {
		var comp = 0;
		var iniComp = 1;
		var maxGrupo = dameMaxGrupoVinc(fila);
		var mensajeTemp = "";
		if (totalComparativas > 1) {
			var compModArr = cVigente.split('_');
			mensajeTemp = "En la comparativa " + compModArr[0] + ": ";
		}
		var primera = true;
		for (gr = 1; gr < maxGrupo + 1; gr++) {
			for (i = 0; i < listaVinculaciones.length; i++) {
				var v = listaVinculaciones[i];
				if (v.fila == fila && v.comparativa == iniComp
						&& v.grupoVinculacion == gr) {
					if (gr < 2) { // grupo 1
						if (primera) {
							iniComp = v.comparativa;
							iniMod = v.modulo;
							comp = v.comparativa;
							mensajeTemp = mensajeTemp + generaMs(v);
							primera = false;
						} else {
							mensajeTemp = mensajeTemp + generaMs2(v, ' y ');
						}
					} else { // grupo >1
						if (primera) {
							mensajeTemp = mensajeTemp
									+ generaMs2(v, ' o bien ');
							primera = false;
						} else {
							mensajeTemp = mensajeTemp + generaMs2(v, ' y ');
						}
					}
				}
			}
			primera = true;
		}//cambio grupo			
		if (mensajeTemp != '') {
			$('#msgVinculaciones').val(mensajeTemp);
		}
	}

	/*
	Genera la 1a parte del mensaje de error en validación de Vinculaciones entre Riesgos
	 */
	function generaMs(v) {
		var no = "";
		var vincNo = "";
		if (!v.elegida)
			no = " no";
		if (!v.vincElegida)
			vincNo = " no";
		msg = "Si" + no + " se elige el riesgo '" + v.descFila
				+ "' es obligatorio" + vincNo + " elegir '"
				+ getDescRiesgo(v.vincFila) + "'";
		return msg;
	}

	/*
	Genera la 2a parte del mensaje de error en validación de Vinculaciones entre Riesgos 
	 */
	function generaMs2(v, text) {
		var no = "";
		var vincNo = "";
		if (!v.elegida)
			no = " no";
		if (!v.vincElegida)
			vincNo = " no";
		msg = text + vincNo + " elegir '" + getDescRiesgo(v.vincFila) + "'";
		return msg;
	}

	/*
	Devuelve el contenido del hidden donde se almacena el mensaje resultante de la validación de vinculaciones entre riesgos
	 */
	function getMsgVinculaciones() {
		return $('#msgVinculaciones').val();
	}

	/*
	Comprueba si todas las coberturas básicas y elegibles se han elegido
	 */
	function coberturasBasicasElegibles() {
		$('#msgBasicasElegibles').val('');
		for (i = 0; i < listaCoberturasBasicasElegibles.length; i++) {
			var c = listaCoberturasBasicasElegibles[i];
			var comp_Mod = c.modulo + '_' + c.comparativa;
			var mostradas = dameCompMostradas();
			var mostradasArr = mostradas.split(',');
			for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los selects de los divs mostrados.
				if (mostradasArr[m] == comp_Mod) {
					var idCheck = "check_" + c.modulo + '_' + c.comparativa
							+ "_" + c.fila + "_" + c.cpm + "_" + c.rc + "_"
							+ c.rce + "_" + c.filaComp;
					if ($("input[id^='" + idCheck + "']").attr('checked') == false) {
						generaMsgCoberturasBasicasElegibles(c);
						return false;
					}
				}
			}
		}
		return true;
	}

	/*
	Comprueba si todas las coberturas RyD se han elegido
	 */
	function existenRyDelegibles() {
		for (i = 0; i < listaRyDElegibles.length; i++) {
			var c = listaRyDElegibles[i];
			var comp_Mod = c.modulo + '_' + c.comparativa;
			var mostradas = dameCompMostradas();
			var mostradasArr = mostradas.split(',');
			for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los selects de los divs mostrados.
				if (mostradasArr[m] == comp_Mod) {
					var idCheck = "check_" + c.modulo + '_' + c.comparativa
							+ "_" + c.fila + "_" + c.cpm + "_" + c.rc + "_"
							+ c.rce + "_" + c.filaComp;
					if ($("input[id^='" + idCheck + "']").attr('checked') == false) {
						msg = "Es obligatorio eligir el riesgo '" + c.descFila
								+ "'";
						$('#panelAlertasValidacion').html(msg);
						$('#panelAlertasValidacion').show();
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	Genera el mensaje de validación de coberturas básicas elegibles
	 */
	function generaMsgCoberturasBasicasElegibles(c) {
		msg = "Es obligatorio eligir el riesgo '" + c.descFila
				+ "' ya que es una cobertura básica y elegible";
		$('#msgBasicasElegibles').val(msg);
	}

	/*
	Devuelve el contenido del hidden donde se almacena el mensaje resultante de la validación de coberturas básicas elegibles
	 */
	function getMsgBasicasElegibles() {
		return $('#msgBasicasElegibles').val();
	}

	/*
	Devuelve la descripción del riesgo correspondiente a la fila del módulo indicada como parámetro
	 */
	function getDescRiesgo(fila) {
		for (j = 0; j < listaRiesgosCubiertosElegibles.length; j++) {
			if (listaRiesgosCubiertosElegibles[j].codfila == fila) {
				return listaRiesgosCubiertosElegibles[j].desRC;
			}
		}
		return "";
	}

	/*
	 ***                     ***
	 *** FIN DE VALIDACIONES ***
	 ***                     ***
	 */
	function creaInfoOpcionesRenovables() {
		// Borra el contenido del input que almacena las opciones renovable seleccionadas
		$('#renovElegidas').val('');
		// Borra el contenido del input que almacena la tipología de asegurado elegida
		$('#tipologiaElegida').val('');
		var mostradas = dameCompMostradas();
		var mostradasArr = mostradas.split(',');
		for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los selects de los divs mostrados.
			if (mostradasArr[m] != '') {
				var combo = $(
						"select[id='modRenovable_" + mostradasArr[m] + "']")
						.val();
				// Si el valor elegido para renovables es un numérico (0 - No, 1 - Sí) se añade al código del módulo
				// para guardar la combinaciones elegidas
				if (isFinite(combo)) {
					var mod = mostradasArr[m].substr(0, getPosition(
							mostradasArr[m], '_', 1));// nos quedamos con el num de comparativa
					var comp = mostradasArr[m].substr(getPosition(
							mostradasArr[m], '_', 1) + 1, getPosition(
							mostradasArr[m], '_', 2));// nos quedamos con el modulo
					$('#renovElegidas').val(
							$('#renovElegidas').val() + mod + "#" + comp + "#"
									+ combo);

					// Si existe el combo de tipología de asegurado y el valor es correcto, se añade a la combinación del módulo renovable
					var comboTipologia = $("select[id='tipologiaAsegurado_"
							+ mostradasArr[m] + "']");
					if (comboTipologia.length > 0) {
						if (comboTipologia.val().length > 0) {
							$('#renovElegidas').val(
									$('#renovElegidas').val() + "#"
											+ comboTipologia.val());
						}
					}
					$('#renovElegidas').val($('#renovElegidas').val() + ",");
				}
				else {
					var mod = mostradasArr[m].substr(0, getPosition(
							mostradasArr[m], '_', 1));
					var comboTipologia = $("select[id='tipologiaAseguradoAnex_"
							+ mod + "']");
					if (comboTipologia.length > 0) {
						if (comboTipologia.val().length > 0) {
							$('#renovElegidas').val(
									$('#renovElegidas').val() + "#"
											+ comboTipologia.val());
						}
					}
					$('#renovElegidas').val($('#renovElegidas').val() + ",");
				}
			}
		}
	}

	function creaInfoCoberturasElegidas() {
		var mostradas = dameCompMostradas();
		var mostradasArr = mostradas.split(',');
		// Borra el contenido del input que almacena las coberturas elegidas
		$('#coberturasElegidas').val('');
		/* Recorre los checks de riesgos elegibles y almacena los datos correspondientes en el input */
		var strCoberturas = '';
		/* Almacena las claves de las comparativas con elegibles */
		var elegibles = [];
		for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los selects de los divs mostrados.
			if (mostradasArr[m] != '') {
				$("input[id^='check_" + mostradasArr[m] + "']:enabled").each(
						function() {
							id = ($(this).attr('id'));
							cadena = (id.substring(id.indexOf('_') + 1));
							if ($(this).attr('checked')) {
								cadena += '_-1#';
							} else {
								cadena += '_-2#';
							}
							strCoberturas += cadena;
							if (!elegibles.includes(mostradasArr[m]))
								elegibles.push(mostradasArr[m]);
						});
			}
		}
		/* Recorre los combos habilitados del apartado de condiciones de cobertura y almacena los datos correspondientes en el input */
		for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los selects de los divs mostrados.
			if (mostradasArr[m] != '') {
				$("select[id^='select_" + mostradasArr[m] + "']:enabled").each(
						function() {
							id = ($(this).attr('id'));
							valOption = ($(this).find(":selected").text());
							cadena = (id.substring(id.indexOf('_') + 1)) + '_'
									+ $(this).val() + '_' + valOption + '#';
							strCoberturas += cadena;
							if (!elegibles.includes(mostradasArr[m]))
								elegibles.push(mostradasArr[m]);
						});
			}
		}

		/* Revisamos los mostrados para obtener la cobertura única de las comparativas sin elegibles */
		for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los selects de los divs mostrados.
			if (mostradasArr[m] != '' && !elegibles.includes(mostradasArr[m])) {
				$('input[id^=cptSinElegibles_' + mostradasArr[m] + ']').each(
						function() {
							strCoberturas += $(this).val() + '#';
						});
			}
		}
		$('#coberturasElegidas').val(strCoberturas);
	}

	/* Habilita/deshabilita los combos del mismo módulo/fila al check marcado/desmarcado */
	function gestElegiblesAsociados(check, comp, mod, fila) {
		idCombo = "select_" + mod + "_" + comp + "_" + fila;
		idCampoObligatorio = idCombo.replace('select_',
				'campoObligatorioSelect_');
		$("select[id^='" + idCombo + "']").each(function() {
			/* Si se ha marcado el check, se habilita el combo */
			if (check.checked) {
				$(this).removeAttr('disabled');
			}
			/* Si se ha desmarcado el check, se deshabilita el combo y se elige la opción vacía */
			else {
				$(this).val('');
				$(this).attr('disabled', 'disabled');
				// Se ocultan los * rojos por si hubiera de validaciones previas
				// Oculta * rojos
				$("[id^=" + idCampoObligatorio + "]").each(function() {
					$(this).hide();
				});
			}
		});
	}

	/* Selecciona en pantalla las coberturas almacenadas en BBDD */
	function cargarElegiblesGuardados() {
		
		/* ESC-14981 ** MODIF TAM (06.09.2021) ** Inicio */
		/* Selecciona los riesgos elegidos */
		for (i = 0; i < listaRiesgosElegidos.length; i++) {
			var elegible = listaRiesgosElegidos[i].split(':');
			// Si se ha elegido se marca
			if (elegible[1] == -1) {
				// Se hace dos veces el set de checked a true para que:
				// la primera vez para la funcion click lo coja como marcado
				// como en la propia deficicion del click está cambiar el checked
				// es necesario volverlo a setear
				$("input[id='" + elegible[0] + "']").attr('checked', true);
				$("input[id='" + elegible[0] + "']").click();
				$("input[id='" + elegible[0] + "']").attr('checked', true);
			}
		}
		/* Selecciona los valores de condiciones coberturas elegidos */
		for (i = 0; i < listaCondicionesCoberturasElegidas.length; i++) {
			
			var elegible = listaCondicionesCoberturasElegidas[i].split(':');
			
			/* ESC-14981 ** MODIF TAM (06.09.2021) ** Inicio */
   		    /* En la consulta de Polizas de ACM o de pólizas Cargadas Manualmente, para evitar el error de las pólizas que puedan tener diferente valor en fila 
			   comparativa buscamos el nombre del select */

			if ($('#modoLectura').val() == 'modoLectura') {
				
				var eleg = elegible[0].split('_');
				var elegible_sinFilaComp = eleg[0] + "_" + eleg[1] + "_" + eleg[2] + "_" +
										   eleg[3] + "_" + eleg[4] + "_" + eleg[5] + "_" + eleg[6];

				$("select[id^='select_'").each(function() {
					var nomb_Sele = $(this).attr('id');
					
					var nombSel = nomb_Sele.split('_');
					var nomb_Sele_sinFilaComp = nombSel[0] + "_" + nombSel[1] + "_" + nombSel[2] + "_" +
												nombSel[3] + "_" + nombSel[4] + "_" + nombSel[5] + "_" + nombSel[6];
					
					if (elegible_sinFilaComp == nomb_Sele_sinFilaComp){
						$("select[id='" + nomb_Sele + "']").val(elegible[1]);
					}
					
				});
				
			}else{
				/* ESC-14981 ** MODIF TAM (06.09.2021) ** Fin */	
				$("select[id='" + elegible[0] + "']").val(elegible[1]);	
			}
			
			if (tipologiaAsegurado != "") {
				var elegible = tipologiaAsegurado.split(':');
				$("select[id='" + elegible[0] + "']").val(elegible[1]);	
			}
			
		}
	}

	// Marca en el cuadro del anexo las coberturas elegidas en cuadro de la póliza		
	function cargaElegidosPolizaEnAnexo() {
		$("input:disabled[type=checkbox]").each(
				function() {
					id = ($(this).attr('id'));
					id = ($(this).attr('id')).substring(($(this).attr('id'))
							.indexOf('0_') + 1);
					id = "check_1" + id;
					var check = $("input[id='" + id + "']");
					check.click();
				});
		$("select:disabled[id^='plz_']").each(
				function() {
					id = ($(this).attr('id')).substring(($(this).attr('id'))
							.indexOf('_') + 1);
					id = ($(this).attr('id')).substring(($(this).attr('id'))
							.indexOf('0_') + 1);
					id = "select_1" + id;
					if ($(this).val().length > 0) {
						$("select[id='" + id + "']").removeAttr('disabled');
						$("select[id='" + id + "']").val($(this).val());
					}
				});
	}

	function continuar(verDuplicadas, validarAnexo) {
		if ($('#modoLectura').val() == 'modoLectura') {
			var frmComp = document.getElementById('formComparativas');
			var frm = document.getElementById('consultaDetallePoliza');
			frm.idpoliza.value = frmComp.idpoliza.value;
			frm.method.value = 'doVerImportes';
			frm.submit();
		} else {
			if ($("#formComparativas").valid()) {
				creaInfoOpcionesRenovables();
				creaInfoCoberturasElegidas();
				var calculoRC = calcularRC();
				var comprobacionNumComparativas = comprobarNumComparativas();
				var comprobacionCombiDatosVin = checkCombinacionesDatosVincu();
				if (!existenRyDelegibles() && calculoRC
						&& comprobacionNumComparativas
						&& comprobacionCombiDatosVin) {
					if (!existenComparativasDuplicadas(verDuplicadas)) {
						bloquearPantalla(); //           !!!!!!!!!!!!!!COMENTAR PARA PRUEBAS
						if (validarAnexo) {
							$("#formComparativas #validarAnexo").val("true");
						}
						$("#formComparativas").submit(); // !!!!!!!!!!!!!!COMENTAR PARA PRUEBAS
					}
				}
			}
		}
	}

	function isValorVinculadoValid(vincId, padreId) {
		var valorElegido = $('#select_' + vincId).val();
		var isValorVincValid = (valorElegido == '');
		var listaVinculacionesAgriStr = $('#listaVinculacionesAgri').val();
		if (listaVinculacionesAgriStr != ''
				&& !$('#select_' + vincId).attr('disabled')) {
			var valorPadre = $('#select_' + padreId).val();
			if (valorPadre == '') {
				isValorVincValid = (valorElegido == '');
			} else {
				var listaVinculacionesAgriArr = listaVinculacionesAgriStr
						.split(',');
				for (l = 0; l < listaVinculacionesAgriArr.length; l++) {
					var valoresVincArr = $.trim(listaVinculacionesAgriArr[l])
							.replace('[', '').replace(']', '').split('#');
					if (valoresVincArr[0] == vincId) {
						var valoresArr = $.trim(valoresVincArr[1]).split('|');
						for (m = 0; m < valoresArr.length; m++) {
							var valores = $.trim(valoresArr[m]).split(':');
							if (valorPadre == valores[0]) {
								isValorVincValid |= valorElegido == valores[1];
							}
						}
					}
				}
			}
		}
		return isValorVincValid;
	}

	/* Pet. 63485 ** DNF (05/08/2020) ** Inicio */
	/* Función que comprueba que las combinaciones elegidas son válidas según lo que se indique 
	 * en el XML sobre datos vinculados.*/
	function checkCombinacionesDatosVincu() {
		var result = true;
		var camposBloqVincAgriStr = $('#camposBloqVincAgri').val();
		if (camposBloqVincAgriStr != '') {
			var mostradasArr = dameCompMostradas().split(',');
			var camposBloqVincAgriArr = camposBloqVincAgriStr.split(',');
			for (var i = 0; i < mostradasArr.length; i++) {
				if (mostradasArr[i] != '') {
					for (var j = 0; j < camposBloqVincAgriArr.length; j++) {
						var camposVincArr = $.trim(camposBloqVincAgriArr[j])
								.replace('[', '').replace(']', '').split('#');
						var nombreCampoPadre = camposVincArr[0];
						if (nombreCampoPadre.startsWith(mostradasArr[i])
								&& camposVincArr[1] != '') {
							var nombreCampoVincArr = $.trim(camposVincArr[1])
									.split(':');
							for (var k = 0; k < nombreCampoVincArr.length; k++) {
								var aux = isValorVinculadoValid(
										nombreCampoVincArr[k], nombreCampoPadre);
								aux ? $(
										'#campoObligatorioSelect_'
												+ nombreCampoVincArr[k]).hide()
										: $(
												'#campoObligatorioSelect_'
														+ nombreCampoVincArr[k])
												.show();
								result &= aux;
							}
						}
					}
				}
			}
		}
		if (!result) {
			$('#panelAlertasValidacion')
					.html(
							"El valor seleccionado en el campo elegible no es correcto");
			$('#panelAlertasValidacion').show();
		}
		return result;
	}

	/*PET.63485 DNF funcion para comprobar que el numero de comparativas no exceda del máximo permitido*/
	function comprobarNumComparativas() {
		if ($('#contComparativas').val() <= $('#maxComparativas').val()) {
			return true;
		} else {
			$('#panelAlertasValidacion').html(
					"Ha excedido el numero máximo de comparativas permitidas");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	/*FIN PET.63485 DNF*/
	function calcularRC() {
		if ($('#calcularRC') && $('#calcularRC').val() != '') {
			var calcular = $('#calcularRC').val() == 'true';
			var regimenVacio = $('#codRegimenRC').val() == "";
			if ((calcular && !regimenVacio) || !calcular) {
				return true;
			} else {
				$('#panelAlertasValidacion').html(
						"Es obligatorio elegir el Regimen para RC");
				$('#panelAlertasValidacion').show();
				return false;
			}
		} else if ($('#calcularRC')) {
			$('#panelAlertasValidacion').html(
					"Es obligatorio indicar si se desea calcular RC de ganado");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}

	function volver() {
		$("#formVolver").submit();
	}

	/* Pet. 63485 ** MODIF TAM (17.07.2020) ** Inicio */
	function volverParcela() {
		var frmComp = document.getElementById('formComparativas');
		var frm = document.getElementById('formVolverParcelas');
		frm.idpolizaVol.value = frmComp.idpoliza.value;
		$("#formVolverParcelas").submit();
	}

	function descargarxml(codModulo) {
		var frmComp = document.getElementById('formComparativas');
		var frm = document.getElementById('formVerFich');
		frm.idpolizaFich.value = frmComp.idpoliza.value;
		frm.codModuloFich.value = codModulo;
		$('method').val('doDescargarFichero');
		$('#formVerFich').submit();
	}
	/* Pet. 63485 ** MODIF TAM (17.07.2020) ** Fin */

	function bloquearPantalla() {
		$.blockUI.defaults.message = '<h4> Validando la póliza<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({
			overlayCSS : {
				backgroundColor : '#525583'
			}
		});
	}

	function deshabiltaCampos(formulario) {
		CamposInput = formulario.getElementsByTagName("input");
		for (var i = 0; i < CamposInput.length; i++) {
			// Si el tipo de campo es una caja de texto
			if (CamposInput[i].type == "text"
					|| CamposInput[i].type == "checkbox") {
				CamposInput[i].disabled = true;
			}
		}
		CamposSelect = formulario.getElementsByTagName("select");
		for (var i = 0; i < CamposSelect.length; i++) {
			CamposSelect[i].disabled = true;
		}
	}

	function eligeMenu() {
		if ($('#vieneDeUtilidades').val() == 'true') {
			SwitchMenu('sub4');
		} else {
			SwitchMenu('sub3');
		}
	}

	function existenComparativasDuplicadas(verDuplicadas) {
		if (verDuplicadas) {
			var modulos = $('#modulos').val();
			var arrM = modulos.split(',');
			var contComp = parseInt($('#contComparativas').val());
			if (contComp != arrM.length) {
				var mostradas = dameCompMostradas();
				var mostradasArr = mostradas.split(',');
				for (m = 0; m < mostradasArr.length; m++) { // solo recorremos los selects de los divs mostrados.
					if (mostradasArr[m] != '') {
						var existeIgual = existeIdentica(mostradasArr[m],
								mostradasArr);
						if (existeIgual) {
							jAlert("Existen comparativas iguales", 'Error');
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	// comprueba si la comparativa pasada como pararametro -> aRevisar ( comparativa_modulo) tiene alguna otra comparativa identica a ella
	// se revisa el combo renovables y si éste es diferente se revisan las coberturas.
	function existeIdentica(aRevisar, mostradasArr) {
		var mArr2 = aRevisar.split('_');
		var mod = mArr2[0];
		for (h = 0; h < mostradasArr.length; h++) { // solo recorremos los selects de los divs mostrados.
			if (mostradasArr[h] != '') {
				var mArr2T = mostradasArr[h].split('_');
				var modT = mArr2T[0];
				if ((mod == modT) && (aRevisar != mostradasArr[h])) {
					var resultadoRen = revisarRenovElegidas(aRevisar,
							mostradasArr[h]);
					if (resultadoRen) {// si son iguales revisamos las coberturas
						var resultadoCob = revisarCobElegidas(aRevisar,
								mostradasArr[h]);
						if (resultadoCob) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	// comprueba si las renovables(si es que existen) del revIni (comparativa_modulo) son iguales que las del revFin (comparativa_modulo)
	function revisarRenovElegidas(revIni, revFin) {
		if ($('#renovElegidas').val() != '') {
			var renovArr = ($('#renovElegidas').val()).split(',');
			var revInix = '';
			var revFinx = '';
			for (var i = 0; i < renovArr.length; i++) {
				if (renovArr[i] != '') {
					var renArr = renovArr[i].split('#');
					var compR = renArr[0];
					var modR = renArr[1];
					var renR = renArr[2];
					if (revIni == (renArr[0] + "_" + renArr[1])) {
						revInix = renArr[2];
					}
					if (revFin == (renArr[0] + "_" + renArr[1])) {
						revFinx = renArr[2];
					}
				}
			}
			if (revInix == revFinx) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	// comprueba si las coberturas del revIni (comparativa_modulo) son iguales que las del revFin (comparativa_modulo)
	function revisarCobElegidas(revIni, revFin) {
		var cobs = $('#coberturasElegidas').val();
		var cobsArr = cobs.split('#');
		var strIni = '';
		var strFin = '';
		for (c = 0; c < cobsArr.length; c++) {
			if (cobsArr[c] != null && cobsArr[c] != '') {
				var comp_Mod = cobsArr[c].substr(0, getPosition(cobsArr[c],
						'_', 2));// nos quedamos con el num de comparativa
				if (comp_Mod == revIni) {
					strIni = strIni
							+ "@"
							+ cobsArr[c].substr(
									getPosition(cobsArr[c], '_', 2),
									cobsArr[c].length);
				}
				if (comp_Mod == revFin) {
					strFin = strFin
							+ "@"
							+ cobsArr[c].substr(
									getPosition(cobsArr[c], '_', 2),
									cobsArr[c].length);
				}
			}
		}
		if (strIni == strFin) {
			return true;
		} else {
			return false;
		}
	}

	function getPosition(string, subString, index) {
		return string.split(subString, index).join(subString).length;
	}

	// devuelve las comparativas mostradas en formato "comparativa_modulo" separados por comas
	function dameCompMostradas() {
		var maxComp = parseInt($('#maxComparativas').val());
		var mostrados = "";
		$("div[id*='compDIV_']").each(
				function() {
					idCombo = $(this).attr('id');
					if ($(this).is(':visible')) {
						var divArray = idCombo.split('_');
						if (divArray[1] != "0") {
							mostrados = mostrados + "," + divArray[1] + "_"
									+ divArray[2];
						}
					}
				});
		return mostrados;
	}

	$(document).ready(
			function() {
				cargarElegiblesGuardados();
				/* Pet. 63485 ** MODIF TAM (29.07.2020) ** Inicio */
				/* Por defecto se bloquean los campos que tienen vinculación de datos */
				bloquearCamposconVinculacion();
				/* Pet. 63485 ** MODIF TAM (29.07.2020) ** Fin */
				if ($('#modoLectura').val() == 'modoLectura'
						|| $('#modoLectura').val() == 'true') {
					var frm = document.getElementById('formComparativas');
					deshabiltaCampos(frm);
				}
			});
</script>