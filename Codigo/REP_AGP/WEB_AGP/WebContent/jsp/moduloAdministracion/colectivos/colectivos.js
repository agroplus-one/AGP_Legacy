$(document)
		.ready(
				function() {

					//Para evitar el cacheo de peticiones al servidor
					var URL = UTIL.antiCacheRand($("#main").attr("action"));
					$("#main").attr("action", URL);

					// Inicializar los calendarios de los campos fecha
					Zapatec.Calendar.setup({
						firstDay : 1,
						weekNumbers : false,
						showOthers : true,
						showsTime : false,
						timeFormat : "24",
						step : 2,
						range : [ 1900.01, 2999.12 ],
						electric : false,
						singleClick : true,
						inputField : "fechaIni",
						button : "btn_fechaIni",
						ifFormat : "%d/%m/%Y",
						daFormat : "%d/%m/%Y",
						align : "Br"
					});

					Zapatec.Calendar.setup({
						firstDay : 1,
						weekNumbers : false,
						showOthers : true,
						showsTime : false,
						timeFormat : "24",
						step : 2,
						range : [ 1900.01, 2999.12 ],
						electric : false,
						singleClick : true,
						inputField : "fechaFin",
						button : "btn_fechaFin",
						ifFormat : "%d/%m/%Y",
						daFormat : "%d/%m/%Y",
						align : "Br"
					});

					Zapatec.Calendar.setup({
						firstDay : 1,
						weekNumbers : false,
						showOthers : true,
						showsTime : false,
						timeFormat : "24",
						step : 2,
						range : [ 1900.01, 2999.12 ],
						electric : false,
						singleClick : true,
						inputField : "fechaEfecto",
						button : "btn_fechaEfecto",
						ifFormat : "%d/%m/%Y",
						daFormat : "%d/%m/%Y",
						align : "Br"
					});

					$('#panelInformacionColectivos').hide();
					// valor por defecto del tipo de descuento/Recargo
					var ii = 4;// cualquier valor diferente a 0, 1, 2
					var radios = document.main.tipoDescRecarg;
					for ( var i = 0, iLen = radios.length; i < iLen; i++) {
						if (radios[i].checked == true) {
							ii = i;
							// $("#tipoDescRecarg").attr('checked',true);
							break;
						}
					}
					if (ii == 4) {
						$("#tipoDescRecarg").attr('checked', true);
					} else {
						radios[ii].checked = true;
					}
					

					// $("#tipoDescRecarg").attr('checked',true);
					// Validaciones del formulario
					$('#main')
							.validate(
									{
										errorLabelContainer : "#panelAlertasValidacion",
										wrapper : "div",
										onfocusout : function(element) {
											if (($('#operacion').val() == "alta" || +$(
											'#operacion').val() == "modificar")
													&& !this.checkable(element)
													&& (element.name in this.submitted || !this
															.optional(element))) {
												this.element(element);
											} else {
												if ($('#grupoEntidades').val() != "") {
													if (element.name == "tomador.id.codentidad") {
														if ($("#main")
																.validate()
																.element(
																		element)) {
															$('#operacion')
																	.val(
																			"consultar");
															$('#btnConsultar')
																	.attr(
																			'disabled',
																			'');
														} else {
															$('#operacion')
																	.val(
																			"consultar");
															$('#btnConsultar')
																	.attr(
																			'disabled',
																			'true');
														}
													}
												}
											}
										},
										highlight : function(element,
												errorClass) {
											$("#campoObligatorio_" + element.id)
													.show();
										},
										unhighlight : function(element,
												errorClass) {
											$("#campoObligatorio_" + element.id)
													.hide();
										},
										rules : {
											"tomador.id.codentidad" : {
												required : true,
												digits : true,
												rangelength : [ 3, 4 ],
												grupoEnt : true
											},
											"linea.codplan" : {
												required : true,
												digits : true,
												minlength : 4
											},
											"linea.codlinea" : {
												required : true,
												digits : true
											},
											"tomador.id.ciftomador" : {
												required : true,
												minlength : 9,
												comprobarCIF : true
											},
											"nomcolectivo" : {
												required : true
											},
											"subentidadMediadora.id.codentidad" : {
												required : true,
												digits : true,
												minlength : 3
											},
											"subentidadMediadora.id.codsubentidad" : {
												required : true,
												digits : true
											},
											"pctprimerpago" : {
												required : true,
												range : [ 1, 100 ]
											},
											"fechaprimerpago" : {
												dateITA : true,
												comprobarFechaIni : true
											},
											"fechasegundopago" : {
												required : function(element) {
													return parseInt($(
															"#pctprimerpago")
															.val()) < 100;
												},
												dateITA : true,
												comprobarFechaFin : [
														'fechaIni', 'fechaFin' ]
											},
											"fechaefecto" : {
												dateITA : true
											},
											"cccEntidad" : {
												checkCCCentidad : true,
												validarCRM : true
											},
											"ccc" : {
												required : true
											},
											"pctDescRecarg" : {
												isDigitPctDescuento : true,
												isDigitPctRecargo : true,
												requiredPctDescuento : true,
												requiredPctRecargo : true,
												comprobarPctDescuento : true,
												comprobarPctRecargo : true
											},											
											"envioIbanAgro" : {
												required : true
											}
										},
										messages : {
											"tomador.id.codentidad" : {
												required : "El campo Entidad es obligatorio",
												digits : "El campo Entidad s\u00F3lo puede contener d\u00EDgitos",
												rangelength : "El campo Entidad debe contener entre 3 y 4 d\u00EDgitos",
												grupoEnt : "La Entidad seleccionada no pertenece al grupo de Entidades del usuario"
											},
											"linea.codplan" : {
												required : "El campo Plan es obligatorio",
												digits : "El campo Plan s\u00F3lo puede contener d\u00EDgitos",
												minlength : "El campo Plan debe contener 4 d\u00EDgitos"
											},
											"linea.codlinea" : {
												required : "El campo L&iacutenea es obligatorio",
												digits : "El campo L\u00EDnea debe contener d\u00EDgitos"
											},
											"tomador.id.ciftomador" : {
												required : "El campo CIF Tomador es obligatorio",
												minlength : "El campo CIF Tomador debe contener 9 d\u00EDgitos",
												comprobarCIF : "Formato del CIF incorrecto"
											},
											"nomcolectivo" : {
												required : "El campo Nombre colectivo es obligatorio"
											},
											"subentidadMediadora.id.codentidad" : {
												required : "El campo Entidad mediadora es obligatorio",
												digits : "El campo Entidad mediadora s\u00F3lo puede contener d\u00EDgitos",
												minlength : "El campo Entidad mediadora debe contener 3 d\u00EDgitos"
											},
											"subentidadMediadora.id.codsubentidad" : {
												required : "El campo Subentidad mediadora es obligatorio",
												digits : "El campo Subentidad mediadora s\u00F3lo puede contener d\u00EDgitos"
											},
											"pctprimerpago" : {
												required : "El campo % Primer pago es obligatorio",
												range : "El campo % Primer pago debe contener un n\u00FAmero entre 1 y 100"
											},
											"fechaprimerpago" : {
												dateITA : "El formato del campo Fecha primer pago es dd/mm/YYYY",
												comprobarFechaIni : "La Fecha primer pago debe ser superior o igual a la fecha actual"
											},
											"fechasegundopago" : {
												required : "El campo Fecha segundo pago es obligatorio",
												dateITA : "El formato del campo Fecha segundo pago es dd/mm/YYYY",
												comprobarFechaFin : "La Fecha segundo pago debe ser superior a la Fecha primer pago"
											},
											"fechaefecto" : {
												dateITA : "El formato del campo Fecha de efecto es dd/mm/YYYY"
											},
											"cccEntidad" : {
												checkCCCentidad : "La entidad de la Cuenta Bancaria no coincide con la del Colectivo",
												validarCRM : "El n\u00FAmero de entidad de la cuenta no pertenece al grupo CRM"
											},
											"ccc" : {
												required : "El campo IBAN es obligatorio"
											},
											"pctDescRecarg" : {
												isDigitPctDescuento : "El campo Descuento debe contener d\u00EDgitos.",
												isDigitPctRecargo : "El campo Recargo debe contener d\u00EDgitos.",
												requiredPctDescuento : "El campo % de descuento es obligatorio cuando esta opci\u00F3n se encuentra seleccionada.",
												requiredPctRecargo : "El campo % de recargo es obligatorio cuando esta opci\u00F3n se encuentra seleccionada.",
												comprobarPctDescuento : "Para el campo descuento, el valor debe de estar comprendido entre 0.1 y 100.",
												comprobarPctRecargo : "Para el campo recargo, el valor debe de estar comprendido entre 0.1 y 999."
											},
											"envioIbanAgro" : {
												required : "El campo Enviar IBAN es obligatorio"
											}
										}
									});

					

					// Mostrar/ocultar botones iniciales				
					if ($("#id").val() != "") {
						$('#operacion').val("modificar");
						$("#btnAlta").hide();
						$("#btnModificar").show();
						
						/* Pet. 57625 (PTC-5729) ** MODIF TAM (17.05.2019)*/
						/* Si el botón Modificar está visible el campo activo debe estar bloqueado */
						$('#activo').attr('disabled', true);
						
						$("#btn_fechaEfecto").show();
						habilitaDescRec();
						// DAA 09/08/2013 Solo desbloqueamos el campo colectivo
						// y el dc si el perfil es 0
						if ($("#perfil").val() != 0) {
							$('#colectivo').attr('disabled', 'disabled');
							$('#dc').attr('disabled', 'disabled');
						}
					}else{
						forzarHabilitarDescRec();
					}

					unificarCcc();
					if ($('#estadoInforme').val() == "informeOK") {
						verificarInforme();
					}
					
					var operacion = $('#operacion').val();

					$('.pagelinks').find('a').each(function(){
						var originalUrl = $(this).attr('href');
						if(originalUrl.indexOf('operacion=baja') > -1) {
							$(this).attr('href', originalUrl.replace('baja','consultar'));
						}
					});
				});


jQuery.validator.addMethod("grupoEnt", function(value, element, params) {
	var codentidad = $('#entidad').val();
	var encontrado = false;
	if ($('#grupoEntidades').val() == "") {
		return true;
	} else if (codentidad != "") {
		var grupoEntidades = $('#grupoEntidades').val().split(',');
		for ( var i = 0; i < grupoEntidades.length; i++) {
			if (grupoEntidades[i] == codentidad) {
				encontrado = true;
				break;
			}
		}
	} else
		return true;
	return encontrado;
});

// /comprueba el CIF
jQuery.validator.addMethod("comprobarCIF", function(value, element) {
	return (this.optional(element) || generales.validaCifNif("CIF", value));
});

// checkCCCentidad
jQuery.validator.addMethod("checkCCCentidad", function(value, element) {
	if ($('#cccEntidad').val() != ''
			&& ($("#isCRM").val() == "false" && $('#cccEntidad').val() != $(
					'#entidad').val())) {
		return false;
	} else {
		return true;
	}
});

// DAA validar el grupo de entidades CRM
jQuery.validator.addMethod("validarCRM", function(value, element) {

	var listaEntCRM = $('#listaEntCRM').val();
	var cccEntidad = $('#cccEntidad').val();
	if ($('#isCRM').val() == "true" && listaEntCRM.indexOf(cccEntidad) < 0) {
		return false;
	} else {
		return true;
	}

});

// comprueba CCC
jQuery.validator.addMethod("comprobarCCC", function(value, element) {
	if ($('#cccEntidad').val() != '' && $('#cccOficina').val() != ''
			&& $('#cccDc').val() != '' && $('#cccCuenta').val() != '') {
		return true;
	} else {
		return false;
	}
});

// comprueba la fecha inicial con la del sistema
// element--> la fechaIni
jQuery.validator.addMethod("comprobarFechaIni", function(value, element) {
	return (this.optional(element) || UTIL
			.fechaMayorOIgualQueFechaActual(element.value));
});

// MPM - 07/11/2012
// Si la fecha es menor que la fecha actual se borra, ya que al ir vacio al
// servidor se pondra la fecha actual por defecto
jQuery.validator.addMethod("comprobarFechaIniBorrar", function(value, element) {
	if (!UTIL.fechaMayorOIgualQueFechaActual(element.value)) {
		element.value = '';
	}

	// Devuelve true para indicar que no hay que mostrar el mensaje de error
	return true;
});

// comprueba la fecha inicial y la final entre ellas
// params--> array que contiene las fechas
jQuery.validator.addMethod("comprobarFechaFin",
		function(value, element, params) {
			return (this.optional(element)
					|| $.trim($("#pctsegundopago").val()) == '' || !UTIL
					.fechaMayorOIgualQue(document.getElementById(params[0]),
							document.getElementById(params[1])));
		});

// comprueba que la suma del porcentaje de los pagos es 100
// params--> los pagos
jQuery.validator.addMethod("comprobarPagos", function(value, element, params) {
	return (this.optional(element) || parseInt($('#' + params[0]).val())
			+ parseInt($('#' + params[1]).val()) == 100);
});

jQuery.validator.addMethod("comprobarPctDescuento", function(value, element,
		params) {
	var isvalid = true;
	var radios = document.main.tipoDescRecarg;
	for ( var i = 0, iLen = radios.length; i < iLen; i++) {
		if (radios[i].checked == true) {
			tipoDescRecag = radios[i].value;
			// alert(tipoDescRecag);
			break;
		}
	}

	if (null != value && value != "") {
		value = value.replace(",", ".");
		if (tipoDescRecag == "0") {
			if (value > 0 && value <= 100) {
				// alert("Decuento entre 0 y 100 - OK");
				isvalid = true;
			} else {
				// alert("Decuento NO entre 0 y 100 - NO OK");
				isvalid = false;
			}
		}
	}

	return isvalid;

});

jQuery.validator.addMethod("comprobarPctRecargo", function(value, element,
		params) {
	var isvalid = true;
	var radios = document.main.tipoDescRecarg;
	for ( var i = 0, iLen = radios.length; i < iLen; i++) {
		if (radios[i].checked == true) {
			tipoDescRecag = radios[i].value;
			// alert(tipoDescRecag);
			break;
		}
	}

	if (null != value && value != "") {
		value = value.replace(",", ".");
		if (tipoDescRecag == "1") {
			if (value > 0 && value <= 999) {
				// alert("Decuento entre 0 y 100 - OK");
				isvalid = true;
			} else {
				// alert("Decuento NO entre 0 y 100 - NO OK");
				isvalid = false;
			}
		}
	}

	return isvalid;

});

jQuery.validator.addMethod("requiredPctDescuento", function(value, element,
		params) {
	// alert("aki estoyyy2");
	var isvalid = true;
	var radios = document.main.tipoDescRecarg;
	for ( var i = 0, iLen = radios.length; i < iLen; i++) {
		if (radios[i].checked == true) {
			tipoDescRecag = radios[i].value;
			// alert(tipoDescRecag);
			break;
		}
	}
	// alert("requiredPctDescuento");
	if (null != tipoDescRecag && tipoDescRecag == "0") {
		if (null == value || value == "") {
			// alert("Descuento/ Recargo sin valor");
			isvalid = false;
		} else {
			// alert("Descuento/ Recargo con valor");
			isvalid = true;
		}
	}
	return isvalid;
});

jQuery.validator.addMethod("requiredPctRecargo", function(value, element,
		params) {
	// alert("aki estoyyy2");
	var isvalid = true;
	var radios = document.main.tipoDescRecarg;
	for ( var i = 0, iLen = radios.length; i < iLen; i++) {
		if (radios[i].checked == true) {
			tipoDescRecag = radios[i].value;
			// alert(tipoDescRecag);
			break;
		}
	}
	// alert("requiredPctRecargo");
	if (null != tipoDescRecag && tipoDescRecag == "1") {
		if (null == value || value == "") {
			// alert("Descuento/ Recargo sin valor");
			isvalid = false;
		} else {
			// alert("Descuento/ Recargo con valor");
			isvalid = true;
		}
	}

	return isvalid;
});

jQuery.validator.addMethod("isDigitPctDescuento", function(value, element,
		params) {
	var isvalid = true;
	var tipoDescRecag = "";
	var radios = document.main.tipoDescRecarg;
	for ( var i = 0, iLen = radios.length; i < iLen; i++) {
		if (radios[i].checked == true) {
			tipoDescRecag = radios[i].value;
			// alert(tipoDescRecag);
			break;
		}
	}
	if (null != tipoDescRecag && tipoDescRecag == "0") {

		value = value.replace(",", ".");
		// alert("isDigitPctDescRecarg - Descuento o Recargo");
		// alert(value);
		isvalid = !isNaN(value);
		// alert(isvalid);
	}
	return isvalid;
});

jQuery.validator.addMethod("isDigitPctRecargo",
		function(value, element, params) {
			var isvalid = true;
			var tipoDescRecag = "";
			var radios = document.main.tipoDescRecarg;
			for ( var i = 0, iLen = radios.length; i < iLen; i++) {
				if (radios[i].checked == true) {
					tipoDescRecag = radios[i].value;
					// alert(tipoDescRecag);
					break;
				}
			}
			if (null != tipoDescRecag && tipoDescRecag == "1") {

				value = value.replace(",", ".");
				// alert("isDigitPctDescRecarg - Descuento o Recargo");
				// alert(value);
				isvalid = !isNaN(value);
				// alert(isvalid);
			}
			return isvalid;
		});

function modificar(id, codEntidad, plan, linea, cifTomador, idColectivo,
		dcColectivo, nomColectivo, entMediadora, subEntMediadora, activo,
		colCalculo, primerPago, segundoPago, fecPrimerPago, fecSegundoPago,
		desc_entidad, desc_linea, desc_tomador, desc_entmediadora,
		desc_subentmediadora, fecCambio, fecEfecto, cccCompleta, iban,
		tx_observaciones, isCRM, bajaLogica, tipoDescRecarg, pctDescRecarg, envioIbanAgro) {
	// document.write(tipoDescRecarg);
	
	limpiaAlertas();
	/* Limpiamos descripciones cada vez q seleccionamos una fila */
	$("#desc_entidad").val(desc_entidad);
	$("#desc_linea").val(desc_linea);
	$("#desc_tomador").val(desc_tomador);
	$("#desc_entmediadora").val(desc_entmediadora);
	$("#desc_subentmediadora").val(desc_subentmediadora);
	
	/* Pet. 57625 (PTC-5729) ** MODIF TAM (17.05.2019)*/
	/* En modificación el campo de activo siempre bloqueado para todos los perfiles */
	$('#activo').attr('disabled', true);
	
	var frm = document.getElementById('main');
	frm.target = "";
	frm.id.value = id;
	frm.entidad.value = codEntidad;
	frm.entidad.readOnly = 'true';
	frm.plan.value = plan;
	frm.linea.value = linea;
	frm.tomador.value = cifTomador;
	frm.colectivo.value = idColectivo;
	frm.dc.value = dcColectivo;
	// DAA 09/08/2013 Solo desbloqueamos el campo colectivo y el dc si el perfil
	// es 0
	if (frm.perfil.value != 0) {
		frm.colectivo.disabled = true;
		frm.dc.disabled = true;
	}
	frm.nomcolectivo.value = nomColectivo;
	frm.entmediadora.value = entMediadora;
	frm.subentmediadora.value = subEntMediadora;
	if ($('#perfil').val() == "0") {
		var comboActivo = frm.activo;
		for ( var i = 0; i < comboActivo.length; i++) {
			if (comboActivo.options[i].value == activo) {
				comboActivo.options[i].selected = true;
			}
		}
	}else{
		if (activo == '1'){
			document.getElementById('display_activo').innerText = ' SI';
			frm.activo.value = 1;
		}else{
			document.getElementById('display_activo').innerText = ' NO';
			frm.activo.value = 0;
		}
	}
	
	/* PTC-5385 ** MODIF TAM (07.11.2018) ** Inicio */
	var comboEnvioIban = frm.envioIbanAgro;
	for ( var i = 0; i < comboEnvioIban.length; i++) {
		if (comboEnvioIban.options[i].value == envioIbanAgro) {
			comboEnvioIban.options[i].selected = true;
		}
	}
	frm.envioIbanAgro.value = envioIbanAgro;
	/* PTC-3585 ** MODIF TAM (07.11.2018) ** Fin */
	

	frm.pctdescuentocol.value = colCalculo;
	frm.pctprimerpago.value = primerPago;
	frm.pctsegundopago.value = segundoPago;
	frm.fechaIni.value = fecPrimerPago;
	frm.fechaFin.value = fecSegundoPago;
	frm.fechaCambio.value = fecCambio;
	frm.fechaEfecto.value = fecEfecto;
	frm.iban.value = iban;
	$('#ccc').val(cccCompleta);
	muestraCuenta();
	frm.tx_observaciones.value = tx_observaciones;
	$("#isCRM").val(isCRM);

	$("#btnConsultar").show();
	$("#btn_fechaEfecto").show();

	$("#fechaIni").rules("remove", "comprobarFechaIni");

	$('#validar').val("true");
	// Botones
	$("#btnAlta").hide();
	habilitaDescRec();
	if (bajaLogica == "true") {
		// No permitimos modificar el registro ni realizamos las validaciones
		$("#btnModificar").hide();
		
		/* Pet. 57625 (PTC-5729) ** MODIF TAM (20.05.2019)*/
		/* En modificación el campo de activo siempre bloqueado para todos los perfiles */
		$('#activo').attr('disabled', false);
		
		habilitaDescRec();
		$("#main").validate().cancelSubmit = true;
		// Limpiamos los posibles asteriscos que hayan quedado
		$("input[type=text]").each(function() {
			$("#campoObligatorio_" + this.id).hide();
		});
		$("input[type=hidden]").each(function() {
			$("#campoObligatorio_" + this.id).hide();
		});
	} else {
		$("#operacion").val("modificar");
		$("#btnModificar").show();
		
		/* Pet. 57625 (PTC-5729) ** MODIF TAM (20.05.2019)*/
		/* En modificación el campo de activo siempre bloqueado para todos los perfiles */
		$('#activo').attr('disabled', true);
		
		
		habilitaDescRec();
		$("#main").valid();
	}
	// $("#operacion").val("modificar");

	var radios = document.main.tipoDescRecarg;
	for ( var i = 0, iLen = radios.length; i < iLen; i++) {
		if (tipoDescRecarg == "null" || tipoDescRecarg == "") {// Ninguno
			radios[0].checked = true;// Ninguno
			radios[1].checked = false;// Descuento
			radios[2].checked = false;// Recargo
		} else if (tipoDescRecarg == "0") {// Descuento
			radios[0].checked = false;
			radios[1].checked = true;
			radios[2].checked = false;
		} else if (tipoDescRecarg == "1") { // Recargo
			radios[0].checked = false;
			radios[1].checked = false;
			radios[2].checked = true;
		}

	}

	$("#pctDescRecarg").val(pctDescRecarg);
}

function enviar(operacion) {
	document.getElementById('pctsegundopago').disabled = false;
	document.getElementById('fechaFin').disabled = false;

	if (operacion == 'alta' || operacion == 'modificar') {
		// si el primer pago es 100 o mas, no envia la fechaFin para que no de
		// error de campo obligatorio
		// el campo activo solo debe ser NO por defecto en un alta
		if (operacion == 'alta') {
			document.getElementById('activo').value = 0;
		}
		if (document.getElementById('pctprimerpago').value >= 100) {
			document.getElementById('pctsegundopago').disabled = true;
			document.getElementById('fechaFin').disabled = true;
			document.getElementById('fechaFin').value = '';
			generales.enviar(operacion, 'entidad', 'plan', 'linea', 'tomador',
					'colectivo', 'dc', 'nomcolectivo', 'entmediadora',
					'subentmediadora', 'activo', 'pctprimerpago',
					'pctsegundopago', 'fechaIni');
		} else if ((document.getElementById('pctprimerpago').value == '')
				|| (document.getElementById('pctprimerpago').value <= 0)) {
			generales.enviar(operacion, 'entidad', 'plan', 'linea', 'tomador',
					'colectivo', 'dc', 'nomcolectivo', 'entmediadora',
					'subentmediadora', 'activo', 'pctprimerpago', 'fechaIni');
			document.getElementById('pctsegundopago').disabled = true;
			document.getElementById('fechaFin').disabled = true;
			document.getElementById('fechaFin').value = '';
		} else {
			// se envian todos los datos
			generales.enviar(operacion, 'entidad', 'plan', 'linea', 'tomador',
					'colectivo', 'dc', 'nomcolectivo', 'entmediadora',
					'subentmediadora', 'activo', 'pctprimerpago',
					'pctsegundopago', 'fechaIni', 'fechaFin');
		}
	} else {
		generales.enviar(operacion, 'entidad', 'plan', 'linea', 'tomador',
				'colectivo', 'dc', 'nomcolectivo', 'entmediadora',
				'subentmediadora', 'activo', 'pctprimerpago', 'pctsegundopago',
				'fechaIni', 'fechaFin');
	}
}

function enviarForm(operacion, id) {
	jConfirm('\u00BFEst\u00E1s seguro de que desea eliminar el registro seleccionado?', 'Di\u00E1logo de Confirmaci\u00F3n', function(r) {
		
		if (r){
		$("#main").validate().cancelSubmit = true;
		$('#id').val(id);
		$('#operacion').val(operacion);
		$('#main').submit();
		}
	});
}

function registrarColectivo(idColectivo) {	
	$.blockUI.defaults.message = '<h4> Registrando colectivo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$("#main").validate().cancelSubmit = true;
	$('#id').val(idColectivo);
	$('#operacion').val('registrarColectivo');
	$('#main').submit();
}


function alta() {
	limpiaAlertas();
	var frm = document.getElementById('main');
	frm.target = "";
	var submit = false;
	$("#panelInformacion").hide();
	if ($("#main").valid()) {
		var f = validarIBAN($("#ccc").val());
		if (f == false) {
			$('#panelAlertasValidacion').html(
					"El Iban tiene un formato incorrecto");
			$('#panelAlertasValidacion').show();
		} else {
			submit = true;
		}
	}
	if (submit == true) {
		$('#activo').val(0);
		$('#id').val('');
		$('#operacion').val("alta");
		$('#main').submit();
	}
}

function guardarModificaciones() {
	$("#fechaIni").rules("remove", "comprobarFechaIni");
	limpiaAlertas();
	$("#panelInformacion").hide();
	$("#operacion").val("modificar");
	var submit = false;
	if ($("#main").valid()) {
		var f = validarIBAN($("#ccc").val());
		if (f == false) {
			$('#panelAlertasValidacion').html(
					"El Iban tiene un formato incorrecto");
			$('#panelAlertasValidacion').show();
		} else {
			muestraCapaEspera ("Guardando cambios");
			//habilitamos los campos para que el objeto recoja el valor
			var radios = document.main.tipoDescRecarg;
			for ( var i = 0, iLen = radios.length; i < iLen; i++) {
				radios[i].disabled = false;
			}
			document.main.pctDescRecarg.disabled = false;
			submit = true;
		}
	}
	if (submit == true) {
		$('#main').attr('target', '');
		$('#dc').attr('disabled', '');
		$('#colectivo').attr('disabled', '');
		$('#main').submit();
	}
}

function calculaPctPagos() {
	var primerPago = $('#pctprimerpago').val();
	if (primerPago > 0) {
		var segundoPago = (100 - primerPago);
		if (segundoPago != 0) {
			if (isNaN(segundoPago) || segundoPago < 1) {
				$('#pctsegundopago').val("");
			} else {
				$('#pctsegundopago').val(segundoPago);
			}
		} else {
			$('#pctsegundopago').val("");
		}
	} else {
		$('#pctsegundopago').val("");
	}
}

function consultar() {
	var frm = document.getElementById('main');
	frm.colectivo.disabled = false;
	frm.dc.disabled = false;
	frm.target = "";
	$("#id").val('');
	$("#main").validate().cancelSubmit = true;
	$('#operacion').val("consultar");
	// Borramos los valores para que no los tenga en cuenta en la consulta
	// $('#tipoDescRecarg').val(null);
	// $('#pctDescRecarg').val(null);
	$('#main').submit();
}

function consultarHistorico(id) {
	$('#idHistorico').val(id);
	$('#historicoForm').submit();
}

function limpiarDatos() {
	if ($('#perfil').val() == "0" || $('#perfil').val() == "5") {
		$('#entidad').val('');
	}

	$('#desc_entidad').val('');
	$('#desc_linea').val('');
	$('#desc_tomador').val('');
	$('#desc_entmediadora').val('');
	$('#desc_subentmediadora').val('');
	$('#plan').val('');
	$('#linea').val('');
	$('#tomador').val('');
	$('#domicilio').val('');
	$('#colectivo').val('');
	$('#dc').val('');
	$('#nomcolectivo').val('');
	$('#entmediadora').val('');
	$('#subentmediadora').val('');
	//$('#activo').selectOptions('');
	$('#activo').val('');
	
	$('#pctsegundopago').val('');
	$('#pctdescuentocol').val('');
	$('#pctprimerpago').val('');
	$('#fechaIni').val('');
	$('#fechaFin').val('');
	$('#id').val('');
	$('#lineaseguroid').val('');
	$('#fechaCambio').val('');
	$('#fechaEfecto').val('');
	$('#ccc').val('');
	$('#iban').val('');
	$('#cuenta1').val('');
	$('#cuenta2').val('');
	$('#cuenta3').val('');
	$('#cuenta4').val('');
	$('#cuenta5').val('');
	$('#tx_observaciones').val('');
	
	$('#envioIbanAgro').val('');
	//consultar();
	var frm = document.getElementById('main');
	frm.colectivo.disabled = false;
	frm.dc.disabled = false;
	frm.target = "";
	$("#id").val('');
	$("#main").validate().cancelSubmit = true;
	$('#operacion').val("limpiar");
	$('#main').submit();
	
}

// funcion que valida un elemento independientemente, si no jquery no lo hace
function validarElemento(elemento) {
	$("#main").validate().element(elemento);
}

function imprimir(size) {
	var frm = document.getElementById('main');
	$("#main").validate().cancelSubmit = true;
	frm.target = "_blank";
	$('#operacion').val("imprimir");
	$('#main').submit();
}

function verificarInforme() {
	imprimirAlta($('#idCol').val(), $('#repreNombre').val(), $('#repreAp1')
			.val(), $('#repreAp2').val(), $('#repreNif').val(),
			$('#cccEntidad').val(), $('#cccOficina').val(), $('#cccDc').val(),
			$('#cccCuenta').val(), $('#colectivo').val(), $('#dc').val(), true);
}

function imprimirAlta(id, repreNombre, repreAp1, repreAp2, repreNif,
		cccEntidad, cccOficina, cccDc, cccCuenta, idColectivo, dcColectivo,
		deAlta) {

	if ($('#estado').val() == "modificar" || $('#estado').val() == "alta") {
		repreNombre = $('#repreNombre').val();
		repreAp1 = $('#repreAp1').val();
		repreAp2 = $('#repreAp2').val();
		repreNif = $('#repreNif').val();
	}

	// Comprueba si el tomador tiene informados los datos del representante
	var datosRepLegal = comprobarDatosRepresentanteLegal(repreNombre, repreAp1,
			repreAp2, repreNif);
	// Comprueba si el colectivo tiene informados los datos de la cuenta y del
	// id de colectivo
	var datosColectivo = comprobarDatosColectivo(cccEntidad, cccOficina, cccDc,
			cccCuenta, idColectivo, dcColectivo);
	// Borra los mensajes de validacion de impresion anteriores
	$('#panelAlertasValidacion').hide();

	// Comprueba si falta algun dato que impida la impresion del informe de
	// colectivo
	msg = "";
	if (!datosRepLegal && !datosColectivo) {
		msg = "Para poder imprimir el informe es necesario completar los datos del Colectivo y del Tomador";
	} else if (!datosRepLegal) {
		msg = "Para poder imprimir el informe es necesario completar los datos del Tomador";
	} else if (!datosColectivo) {
		msg = "Para poder imprimir el informe es necesario completar los datos del Colectivo";
	}

	// Si no ha habido ningun error se muestra el informe de impresion del
	// colectivo
	if (datosRepLegal && datosColectivo) {
		$('#panelInformacionColectivos').hide();
		$('#panelInformacion').show();
		continuarImprimirAlta(id);
	} else {
		// Si viene del alta de colectivo
		if (deAlta) {
			$('#panelInformacion').html(
					$('#panelInformacion').html() + "</br>" + msg);
		}
		// Si la impresion se ha pinchado en el listado de colectivos
		else {
			$('#panelInformacion').hide();
			$('#panelInformacionColectivos').html(msg);
			$('#panelInformacionColectivos').show();
		}
	}
}

function habilitaDescRec() {
	var radios = document.main.tipoDescRecarg;
	if ($('#operacion').val() == "alta") {
		for ( var i = 0, iLen = radios.length; i < iLen; i++) {
			radios[i].disabled = false;
		}
		document.main.pctDescRecarg.disabled = false;
	} else {
		for ( var i = 0, iLen = radios.length; i < iLen; i++) {
			radios[i].disabled = true;
		}
		document.main.pctDescRecarg.disabled = true;
	}
}

function forzarHabilitarDescRec() {
	var radios = document.main.tipoDescRecarg;
	for ( var i = 0, iLen = radios.length; i < iLen; i++) {
		radios[i].disabled = false;
	}
	document.main.pctDescRecarg.disabled = false;
}

function onClicktipoDescRecarg() {
	var radios = document.main.tipoDescRecarg;
	var ii = 4;
	for ( var i = 0, iLen = radios.length; i < iLen; i++) {
		if (radios[i].checked == true) {
			ii = i;
			break;
		}
	}
	if (ii == 4 || ii == 0) {
		document.main.pctDescRecarg.value = "";
		document.main.pctDescRecarg.disabled = true;
	} else {
		document.main.pctDescRecarg.disabled = false;
	}

}

function controlDesctoRecargo(){
	if($('#id').val()==''){
		var radios = document.main.tipoDescRecarg;
		if($('#plan').val()>=2015){		
			document.main.pctDescRecarg.disabled = false;
			for ( var i = 0, iLen = radios.length; i < iLen; i++) {
				radios[i].disabled = false;
			}
		}else{
			document.main.tipoDescRecarg[0].checked=true;
			document.main.pctDescRecarg.value = "";
			document.main.pctDescRecarg.disabled = true;
			for ( var i = 0, iLen = radios.length; i < iLen; i++) {
			radios[i].disabled = true;
			}
		}		
	}
}

function continuarImprimirAlta(id) {
	$('#idImprimir').val(id);
	if ($('#estado').val() == "alta") {
		$('#estado').val("modificar");
		$('#imprimirAlta').fadeIn('normal');
		$('#overlay').show();
	} else {
		imprimirAltaOK();
	}
}

/**
 * Realiza la llamada al controlador para generar el informe del colectivo
 */
function imprimirAltaOK() {
	cerrarPopUp();
	$('#imprimir').submit();
}

function comprobarDatosRepresentanteLegal(repreNombre, repreAp1, repreAp2,
		repreNif) {
	var datosRepLegal = false;
	if (repreNombre != "" && repreAp1 != "" && repreAp2 != "" && repreNif != "") {
		datosRepLegal = true;
	}

	return datosRepLegal;
}

function comprobarDatosColectivo(cccEntidad, cccOficina, cccDc, cccCuenta,
		idColectivo, dcColectivo) {
	var datosColectivo = false;
	if (cccEntidad != "" && cccOficina != "" && cccDc != "" && cccCuenta != ""
			&& idColectivo != "" && dcColectivo != "") {
		datosColectivo = true;
	}

	return datosColectivo;
}

function cerrarPopUp() {
	$('#imprimirAlta').fadeOut('normal');
	$('#overlay').hide();
}

function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	$('#panelInformacionColectivos').hide();
}

function volver() { 
	if($("#procedencia").val()=="incidenciasComisionesUnificadas"){
		 $(window.location).attr('href', 'incidenciasUnificado.run?rand=' + UTIL.getRand() + 
					'&origenLlamada=colectivos'+
					'&method=doConsulta');
	}else{
		 $(window.location).attr(
				'href', 'incidencias.html?rand=' + UTIL.getRand() + '&idFichero='
					+ $('#idFicheroComisiones').val() + '&tipo='
					+ $('#tipoFicheroComisiones').val() + '&method=doConsulta');
	}
}

	 		


function retardo() {
	if (contador < tiempo) {
		contador++;
		setTimeout('retardo()', 100000);
	}
}

function delay(milisegundos) {
	for (i = 0; i <= milisegundos; i++) {
		setTimeout('return 0', 1);

	}
}

function validarSiNumero(numero) {
	if (!/^([0-9])*$/.test(numero)) {
		return false;
	} else {
		return true;
	}
}

function unificarCcc() {
	var CCCcompleta = $('#iban').val() + $('#cuenta1').val()
			+ $('#cuenta2').val() + $('#cuenta3').val() + $('#cuenta4').val()
			+ $('#cuenta5').val();
	$('#ccc').val(CCCcompleta);
}
function muestraCuenta() {
	if ($('#ccc').val() != null) {
		$('#cuenta1').val($('#ccc').val().substring(4, 8));
		$('#cuenta2').val($('#ccc').val().substring(8, 12));
		$('#cuenta3').val($('#ccc').val().substring(12, 16));
		$('#cuenta4').val($('#ccc').val().substring(16, 20));
		$('#cuenta5').val($('#ccc').val().substring(20, 24));
	}
}

function MuestraActivo() {
	if ($('#perfil').val() != "0") {
		var frm = document.getElementById('main');
		//alert($('#activo').val());
		
		if ($('#activo').val() == '1'){
			document.getElementById('display_activo').innerText = ' SI';
			frm.activo.value = 1;
		}else if ($('#activo').val() == '0'){
			document.getElementById('display_activo').innerText = ' NO';
			frm.activo.value = 0;
		}else{
			document.getElementById('display_activo').innerText = '   ';
		}
		
	}
}

/**
 * Muestra la capa de espera con el mensaje recibido como parametro
 * @param msg
 * @returns
 */
function muestraCapaEspera (msg) {
	$.blockUI.defaults.message = '<h4> ' + msg + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}