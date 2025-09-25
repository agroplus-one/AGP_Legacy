/**
 * Incluye dinamicamente la configuracion de los campos variables
 */
function configDVFields() {
	// Campos que estan en mascaras o son obligatorios
	var dvArr = $('#mustFillDVs').val().replace('[', '').replace(']', '')
			.split(',');
	$('input[id^="cod_cpto_"]:not([type="checkbox"]):not([id$="lupa_factores"])').each(function(){
		var codConcepto = $(this).attr('id').replace('cod_cpto_', '');
		var activeField = dvArr.includes(codConcepto);
		if (activeField) {
			var label = $('label[for="cod_cpto_' + codConcepto + '"]').html();
			// SE ELIMINA LA VALIDACION DE OBLIGATORIEDAD POR ORDEN EXPRESA DE RGA
			// CORREO 'RE: GDLD-50776 - PTC-6488 - Cambiar tecnología de pantalla de alta de parcelas (sustituir Flex por HTML) - Funcionalidades' DEL 19/11/2020
			//$(this).rules('add', {
			//	required : function(element){return !sigPacSWValidation && !irPanelCapAsegValidation;},
			//	messages : {
			//		required : 'El campo ' + label + ' es obligatorio.'
			//	}
			//});
			$(this).removeAttr('disabled');	
			// Al cambiar estos campos se limpia el precio
			$(this).change(function() {
				$('#precio').val('');
				// SE INCLUYE LIMPIEZA DE PRODUCCION POR ORDEN EXPRESA DE RGA
				// CORREO 'RE: GDLD-50776 - PTC-6488 - PP línea 300/2020' DEL 19/11/2020
				$('#produccion').val($('#produccion').attr('disabled') ? '0' : '');
			});	
		} else {
			// SE ELIMINA LA VALIDACION DE OBLIGATORIEDAD POR ORDEN EXPRESA DE RGA
			// CORREO 'RE: GDLD-50776 - PTC-6488 - Cambiar tecnología de pantalla de alta de parcelas (sustituir Flex por HTML) - Funcionalidades' DEL 19/11/2020
			//$(this).rules('remove');
			$(this).attr('disabled', 'disabled');
		}
		// TIPO FECHA (O LUPA FECHA FIN GARANTIAS)
		if($(this).attr('tipoDV') == 3 || codConcepto == '134') {
			$(this).rules('add', {
				dateITA : true,
				messages : {
					dateITA : 'El campo ' + label + ' tiene formato incorrecto.'
				}
			});
		}
		// TIPO LUPA
		if($(this).attr('tipoDV') == 6) {
			if (activeField) {
				$('#des_cpto_' + codConcepto).removeAttr('disabled');
				$('#des_cpto_' + codConcepto).attr('readonly', 'readonly');
				$('#lupa_cpto_' + codConcepto).show();
			} else {
				$('#des_cpto_' + codConcepto).attr('disabled', 'disabled');
				$('#des_cpto_' + codConcepto).removeAttr('readonly');
				$('#lupa_cpto_' + codConcepto).hide();
			}
		}
		
		// TIPO CHECKBOX MULTIPLE
		if($(this).attr('tipoDV') == 7) {
			$('input[type="checkbox"][id^="cod_cpto_' + codConcepto + '"]').each(function(){
				if (activeField) {
					$(this).removeAttr('disabled');
				} else {
					$(this).attr('disabled', 'disabled');
				}
			});
		}
	});

	// Campos con calendario
	$('input[type="button"][id^="btn_cod_cpto_"][class="miniCalendario"]').each(function(){
		var campo = this.id.replace('btn_', '');
		// Inicializamos el calendario
		Zapatec.Calendar.setup({
			firstDay : 1,
			weekNumbers : false,
			showOthers : true,
			showsTime : false,
			timeFormat : '24',
			step : 2,
			range : [ 1900.01, 2999.12 ],
			electric : false,
			singleClick : true,
			inputField : campo,
			button : this.id,
			ifFormat : '%d/%m/%Y',
			daFormat : '%d/%m/%Y',
			align : 'Br'
		});
	});
}

function actualizaCptoFactores(cpto) {
	$('#cod_cpto_lupa_factores').val(cpto);
	$('#valor_lupa_factores').val($('#cod_cpto_' + cpto).val());
	$('#desc_lupa_factores').val('');
}

function copiaMarcadoFactoresValor () { 
	if ($('#valor_lupa_factores').val().length > 0) {
		var cpto = $('#cod_cpto_lupa_factores').val();
		var valor = $('#valor_lupa_factores').val();
		$('#cod_cpto_' + cpto).val(valor);
	}
}

function copiaMarcadoFactoresDesc () { 
	if ($('#desc_lupa_factores').val().length > 0) {
		var cpto = $('#cod_cpto_lupa_factores').val();
		var desc = $('#desc_lupa_factores').val();
		$('#des_cpto_' + cpto).val(desc);
	}
}

function actualizaHdnTipo7(codConcepto) {
	var hdnValue = '';
	$('#divCapAseg :input:checked:[type="checkbox"]:[id^="cod_cpto_' + codConcepto + '"]').each(function() {
		hdnValue += $(this).val() + ' ';
	});
	$('#cod_cpto_' + codConcepto).val($.trim(hdnValue));
}

function actualizaChksTipo7(codConcepto) {
	var valArr = $('#cod_cpto_' + codConcepto).val().split(' ');
	$('#divCapAseg :input:[type="checkbox"]:[id^="cod_cpto_' + codConcepto + '"]').each(function() {
		$(this).attr('checked', valArr.includes($(this).val()));
	});
}

function loadType7Fields() {
	$('#divCapAseg :input:[type="hidden"]:[tipodv="7"]').each(function() {
		actualizaChksTipo7($(this).attr('id').replace('cod_cpto_', ''));
	});
}

function loadDesCptosDvs() {
	// CAMPOS VARIABLES DE TIPO LUPA
	$('#divCapAseg :input:[id^="cod_cpto_"]:[tipodv="6"]').each(function() {
		var codCpto = $(this).attr('id').replace('cod_cpto_', '');
		// EXCEPTO LA FECHA FIN DE GARANTIAS
		if (codCpto != '134' && $(this).val() != '') {
			getDatosConcepto(codCpto, $(this).val());
		}		
	});
}