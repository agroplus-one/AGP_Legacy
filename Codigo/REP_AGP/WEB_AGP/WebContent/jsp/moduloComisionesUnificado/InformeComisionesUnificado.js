$(document)
		.ready(
				function() {
					var URL = UTIL.antiCacheRand(document
							.getElementById("main3").action);
					document.getElementById("main3").action = URL;

					var form = document.getElementById("main3");
					var perfil = form.perfil.value;
					var externo = form.externo.value;

					$('#main3')
							.validate(
									{
										onfocusout : function(element) {
											if (($('#method').val() == "doConsulta")) {
												this.element(element);
											}
										},
										errorLabelContainer : "#panelAlertasValidacion",
										wrapper : "li",
										rules : {
											"fase" : {
												validaDigits : true
											},
											"idcolectivo" : {
												validaDigits : true
											},
											"linea" : {
												validaDigits : true
											},
											"plan" : {
												validaDigits : true
											},
											"entmediadora" : {
												validaDigits : true
											},
											"subentmediadora" : {
												validaDigits : true
											},
											"oficina" : {
												validaDigits : true
											},
											"referencia" : {
												invalidChars : true
											},
											"nifcif" : {
												invalidChars : true,
												nif : true
											},
											"recibo" : {
												validaDigits : true
											},
											"entreFechaCarga" : {
												invalidChars : true,
												validaCondicionCompleta : [
														'entreFechaCarga',
														'opcionfechaCarga' ],
												validaIntervaloFechas : [
														'entreFechaCarga',
														'opcionfechaCarga' ],
												validaFecha : true
											},
											"entreFechaEmisionRecibo" : {
												invalidChars : true,
												validaCondicionCompleta : [
														'entreFechaEmisionRecibo',
														'opcionfechaEmisionRecibo' ],
												validaIntervaloFechas : [
														'entreFechaEmisionRecibo',
														'opcionfechaEmisionRecibo' ],
												validaFecha : true
											},
											"entreFechaAceptacion" : {
												invalidChars : true,
												validaCondicionCompleta : [
														'entreFechaAceptacion',
														'opcionfechaAceptacion' ],
												validaIntervaloFechas : [
														'entreFechaAceptacion',
														'opcionfechaAceptacion' ],
												validaFecha : true
											},
											"entreFechaCierre" : {
												invalidChars : true,
												validaCondicionCompleta : [
														'entreFechaCierre',
														'opcionfechaCierre' ],
												validaIntervaloFechas : [
														'entreFechaCierre',
														'opcionfechaCierre' ],
												validaFecha : true
											},
											"entreFechaVigor" : {
												invalidChars : true,
												validaCondicionCompleta : [
														'entreFechaVigor',
														'opcionfechaVigor' ],
												validaIntervaloFechas : [
														'entreFechaVigor',
														'opcionfechaVigor' ],
												validaFecha : true
											},
											"codentidad" : {
												invalidChars : true,
												validaDigits : true
											}
										},
										messages : {
											"fase" : {
												validaDigits : "El campo N�mero fase debe ser num�rico"
											},
											"idcolectivo" : {
												validaDigits : "El campo Colectivo debe ser num�rico"
											},
											"linea" : {
												validaDigits : "El campo Linea debe ser num�rico"
											},
											"plan" : {
												validaDigits : "El campo Plan debe ser num�rico"
											},
											"entmediadora" : {
												validaDigits : "El campo Entidad med. debe ser num�rico"
											},
											"subentmediadora" : {
												validaDigits : "El campo Sub Entidad med. debe ser num�rico"
											},
											"oficina" : {
												validaDigits : "El campo Oficina debe ser num�rico"
											},
											"referencia" : {
												invalidChars : "El campo Ref. P�liza contiene caracteres no v�lidos"
											},
											"nifcif" : {
												invalidChars : "El campo NIF contiene caracteres no v�lidos",
												nif : "El NIF introducido no es v�lido"
											},
											"recibo" : {
												validaDigits : "El campo Recibo debe ser num�rico"
											},
											"entreFechaCarga" : {
												invalidChars : "El campo Fecha de carga contiene caracteres no v�lidos, los valores deben ir separados por ','",
												validaCondicionCompleta : "Debe completar la condici�n para Fecha de carga",
												validaIntervaloFechas : function(
														value) {
													if (document
															.getElementById(value[1]).value == "eq")
														return "El campo Fecha de carga no puede contener intervalos para la condici�n elegida";
													else
														return "El campo Fecha de carga contiene un intervalo incorrecto"
												},
												validaFecha : "La fecha introducida no es correcta"
											},
											"entreFechaEmisionRecibo" : {
												invalidChars : "El campo Fecha de emisi�n contiene caracteres no v�lidos, los valores deben ir separados por ','",
												validaCondicionCompleta : "Debe completar la condici�n para Fecha de emisi�n",
												validaIntervaloFechas : function(
														value) {
													if (document
															.getElementById(value[1]).value == "eq")
														return "El campo Fecha de emisi�n no puede contener intervalos para la condici�n elegida";
													else
														return "El campo Fecha de emisi�n contiene un intervalo incorrecto"
												},
												validaFecha : "La fecha introducida no es correcta"
											},
											"entreFechaAceptacion" : {
												invalidChars : "El campo Fecha de aceptaci�n contiene caracteres no v�lidos, los valores deben ir separados por ','",
												validaCondicionCompleta : "Debe completar la condici�n para Fecha de aceptaci�n",
												validaIntervaloFechas : function(
														value) {
													if (document
															.getElementById(value[1]).value == "eq")
														return "El campo Fecha aceptaci�n no puede contener intervalos para la condici�n elegida";
													else
														return "El campo Fecha de aceptaci�n contiene un intervalo incorrecto"
												},
												validaFecha : "La fecha introducida no es correcta"
											},
											"entreFechaCierre" : {
												invalidChars : "El campo Fecha de cierre contiene caracteres no v�lidos, los valores deben ir separados por ','",
												validaCondicionCompleta : "Debe completar la condici�n para Fecha de cierre",
												validaIntervaloFechas : function(
														value) {
													if (document
															.getElementById(value[1]).value == "eq")
														return "El campo Fecha de cierre no puede contener intervalos para la condici�n elegida";
													else
														return "El campo Fecha de cierre contiene un intervalo incorrecto"
												},
												validaFecha : "La fecha introducida no es correcta"
											},
											"entreFechaVigor" : {
												invalidChars : "El campo Fecha de vigor contiene caracteres no v�lidos, los valores deben ir separados por ','",
												validaCondicionCompleta : "Debe completar la condici�n para Fecha de vigor",
												validaIntervaloFechas : function(
														value) {
													if (document
															.getElementById(value[1]).value == "eq")
														return "El campo Fecha de vigor no puede contener intervalos para la condici�n elegida";
													else
														return "El campo Fecha de vigor contiene un intervalo incorrecto"
												},
												validaFecha : "La fecha introducida no es correcta"
											},
											"codentidad" : {
												invalidChars : "El campo Entidad contiene caracteres no v�lidos",
												validaDigits : "El campo Entidad debe ser num�rico"
											}
										}
									});

					// comprueba los caracteres introducidos
					jQuery.validator.addMethod("invalidChars", function(value,
							element) {
						var validado = true;
						var invalidChars = ".:;<>-[]%!?()+*_=&";
						for ( var i = 0; i < value.length; i++) {
							if (invalidChars.indexOf(value.charAt(i)) != -1) {
								validado = false;
								$('#method').val('');
								break;
							}
						}
						if (validado == false) {
							$('#method').val('');
						}
						return validado;
					});

					jQuery.validator.addMethod("validaDigits", function(value,
							element) {
						return (this.optional(element) || validaDigits(value));
					});


					jQuery.validator.addMethod("nif", function(value, element) {
						return (this.optional(element) || validaNif(value));
					});

					jQuery.validator.addMethod("validaCondicionCompleta",
							function(value, element, params) {
								return validaCondicionCompleta(document
										.getElementById(params[0]), document
										.getElementById(params[1]));
							});

					jQuery.validator.addMethod("validaIntervaloFechas",
							function(value, element, params) {
								return validaIntervaloFechas(document
										.getElementById(params[0]), document
										.getElementById(params[1]));
							});

					jQuery.validator.addMethod("validaFecha", function(value,
							element) {
						return (this.optional(element) || validaFecha(value));
					});
					
					showOrHideExportIcon();
				});

function validaNif(input) {
	var validado = true;
	var cadena = input.split(',');
	for ( var i = 0; i < cadena.length; i++) {
		validado = generales.validaCifNif("NIF", $.trim(cadena[i]));
		validado2 = generales.validaCifNif("CIF", $.trim(cadena[i]));

		if (validado == false && validado2 == false) {
			break;
		}
	}
	if (validado == false && validado2 == false) {
		$('#method').val('');
		return false;
	} else {
		return true;
	}

}

function validaCondicionCompleta(input, select) {
	if (input.value != "" && select.value == "") {
		$('#method').val('');
		return false;
	} else {
		if (input.value == "" && select.value != "") {
			$('#method').val('');
			return false;
		} else {
			return true;
		}
	}
}

function validaDigits(input) {
	var validado = true;
	var cadena = input.split(',');
	var validDigits = "1234567890"

	for ( var i = 0; i < cadena.length; i++) {
		var digito = ($.trim(cadena[i])).split('');
		for ( var j = 0; j < digito.length; j++) {
			if (validDigits.indexOf(digito[j]) == -1) {
				validado = false
				break;
			}
		}
		if (validado == false) {
			break;
		}
	}
	if (validado == false) {
		$('#method').val('');
	}
	return validado;
}

function validaIntervaloFechas(input, select) {
	var cadena = (input.value).split(',');
	var validado = true;
	// Validacion de condicion Igual
	if (select.value == "eq") {
		if (cadena.length > 1) {
			$('#method').val('');
			validado = false;
		}
	}
	// Validacion de condicion Entre, ademas de validar que
	// el primer valor sea menor que el segundo
	if (select.value == "lt-gt") {
		if (cadena.length == 1 || cadena.length > 2) {
			validado = false;

		} else {
			var fechaIni = $.trim(cadena[0]);
			var fechaFin = $.trim(cadena[1]);

			if (esPrimeraFechaMayorOIgualQue(fechaIni, fechaFin)) {
				validado = false;
			} else {
				validado = true;
			}
		}
	}
	if (validado == false) {
		$('#method').val('');
	}
	return validado;
}

function esPrimeraFechaMayorOIgualQue(fec0, fec1) {

	var bRes = false;
	var sDia0 = fec0.substr(0, 2);
	var sMes0 = fec0.substr(3, 2);
	var sAno0 = fec0.substr(6, 4);
	var sDia1 = fec1.substr(0, 2);
	var sMes1 = fec1.substr(3, 2);
	var sAno1 = fec1.substr(6, 4);

	// si el a�o de la primera fecha es mayor que el de la
	// segunda
	if (sAno0 > sAno1) {
		bRes = true;
	} else {
		if (sAno0 == sAno1) {
			// si son del mismo a�o y el mes de la primera
			// es mayor que el de la segunda
			if (sMes0 > sMes1) {
				bRes = true;
			} else {
				if (sMes0 == sMes1) {
					// si el mes es el mismo y el dia de la
					// primera es mayor que el de la segunda
					if (sDia0 >= sDia1) {
						bRes = true;
					}
				}
			}
		}
	}
	return bRes;
}

function validaFecha(input) {
	var validado = false;
	var re = /^\d{1,2}\/\d{1,2}\/\d{4}$/;
	var cadena = input.split(',');
	for ( var i = 0; i < cadena.length; i++) {
		var value = $.trim(cadena[i]);
		if (re.test(value)) {
			var adata = value.split('/');
			var gg = parseInt(adata[0], 10);
			var mm = parseInt(adata[1], 10);
			var aaaa = parseInt(adata[2], 10);
			var xdata = new Date(aaaa, mm - 1, gg);
			if ((xdata.getFullYear() == aaaa) && (xdata.getMonth() == mm - 1)
					&& (xdata.getDate() == gg)) {
				validado = true;
			} else {
				validado = false;
				break;
			}
		} else {
			validado = false;
			break;
		}
	}
	if (validado == false) {
		$('#method').val('');
	}
	return validado;
}

function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	$.get('informesComisiones2015.run?ajax=true&'
			+ decodeURIComponent(parameterString), function(data) {
		$("#grid").html(data);
		showOrHideExportIcon();
		
	});
}

function onInvokeExportAction(id) {
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'informesComisiones2015.run?ajax=false&' + parameterString;
}

function validaEntidad(entidad) {
	// alert($('#perfil').val());
	var encontrado = true;
	var codEnt = entidad.split(',');
	var codEntValid = ($('#grupoEntidades').val()).split(',');
	if ($('#perfil').val() == '5') {

		for ( var i = 0; i < codEnt.length; i++) {
			for ( var j = 0; j < codEntValid.length; j++) {
				if ($.trim(codEnt[i]) == $.trim(codEntValid[j])) {
					encontrado = true;
					break;
				} else {
					encontrado = false;
				}
			}
			if (encontrado == false) {
				break;
			}
		}
		if (encontrado == false) {
			$('#method').val('');
		}
	}
	return encontrado;
}

// }

function validaIntervalo(input, select) {
	var cadena = (input.value).split(',');
	var validado = true;
	// Validacion de condicion Igual
	if (select.value == "5") {
		if (cadena.length > 1) {
			$('#method').val('');
			validado = false;
		}
	}
	// Validacion de condicion Entre, ademas de validar que el primer valor sea
	// menor que el segundo
	if (select.value == "4") {
		if (cadena.length == 1 || cadena.length > 2) {
			$('#method').val('');
			validado = false;
		} else {
			if (primerValorMayor($.trim(cadena[0]), $.trim(cadena[1])) == true) {
				$('#method').val('');
				validado = false;
			}
		}
	}
	if (validado == false) {
		$('#method').val('');
	}
	return validado;
}

function primerValorMayor(primero, segundo) {
	var mayor = false;
	if (parseInt(primero) > parseInt(segundo)) {
		mayor = true;
	}
	return mayor;
}

//:::::::::::::::::::::::::::::::::: Fin Validacion del formulario ::::::::::::::::::::::::::::::::://

function limpiaAlertas() {
	$('#alerta').val("");
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();

	$("#panelInformacion").html('');
	$("#panelAlertasValidacion").html('');
	$("#panelAlertas").html('');

}

function consultar() {
	var frm = document.getElementById('main3');
	frm.target = "";
	limpiaAlertas();
	if (validarConsulta() && $('#main3').valid()) {
		frm.method.value = "doConsulta";
		$('#origenLlamada').val('consultar');
		frm.submit();
	}
}

function validarConsulta() {
	if ($('#entidad').val() != '') {
		if (!validaEntidad($('#entidad').val())) {
			$('#panelAlertasValidacion')
					.html(
							"La Entidad no pertenece al grupo de entidades del usuario");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	if ($('#entidad').val() == '' && $('#oficina').val() == ''
			&& $('#entmediadora').val() == ''
			&& $('#subentmediadora').val() == '' && $('#plan').val() == ''
			&& $('#linea').val() == '' && $('#refPoliza').val() == ''
			&& $('#idcolectivo').val() == '' && $('#nifAsegurado').val() == ''
			&& $('#recibo').val() == '' && $('#fase').val() == ''
			&& $('#entreFechaCarga').val() == ''
			&& $('#entreFechaEmisionRecibo').val() == ''
			&& $('#entreFechaAceptacion').val() == ''
			&& $('#entreFechaCierre').val() == ''
			&& $('#entreFechaVigor').val() == '') {

		$('#panelAlertasValidacion').html(
				"Es necesario filtrar al menos por un campo");
		$('#panelAlertasValidacion').show();
		return false;
	}

	return true;
}

function limpiar() {
	$('#panelAlertasValidacion').html('');
	$('#panelAlertasValidacion').hide();

	$('#oficina').val('');
	$('#desc_oficina').val('');
	$('#entmediadora').val('');
	$('#subentmediadora').val('');
	$('#plan').val('');
	$('#linea').val('');
	$('#desc_linea').val('');
	$('#refPoliza').val('');
	$('#idcolectivo').val('');
	$('#nifAsegurado').val('');
	$('#recibo').val('');
	$('#fase').val('');
	$('#entreFechaCarga').val('');
	$('#entreFechaEmisionRecibo').val('');
	$('#entreFechaAceptacion').val('');
	$('#entreFechaCierre').val('');
	$('#opcionfechaCarga').val('');
	$('#opcionfechaEmisionRecibo').val('');
	$('#opcionfechaAceptacion').val('');
	$('#opcionfechaCierre').val('');
	$('#opcionfechaVigor').val('');
	$('#entreFechaVigor').val('');

	if ($('#perfil').val() != '0') {
		$('#codigosEntidadPerfil5').val('');
		$('#opcionEntidad').val('');

	}

	if ($('#perfil').val() != '5') {
		$('#entidad').val('');
		$('#desc_entidad').val('');
	} else {
		$('#codigosEntidadPerfil5').val('');
		$('#opcionEntidad').val('');
	}
	$('#origenLlamada').val('menuGeneral');
	$('#method').val('doConsulta');
	$("#main3").submit();
}

function showOrHideExportIcon() {
    var table = document.getElementById("consultaInformeComisionesUnificado");
    var matchingRow = table.querySelector("tr[id^='consultaInformeComisionesUnificado']");

    if (matchingRow) {
        document.getElementById("divImprimir").style.display = "block";
    } else {
        document.getElementById("divImprimir").style.display = "none";
    }
}

function exportToExcel(size) {
    var frm = document.getElementById('exportToExcel');
    frm.target="_blank";
    frm.submit();
}