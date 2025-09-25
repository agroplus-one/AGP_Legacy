////****************** INCREMENTOS **************************

//funcion que incrementa el capital asegurado de la parcela.
function incrementarGeneral(filtrarXcultivo) {

	var incr = 0;
	var frm = document.getElementById("main3");

	// Recuperamos el incremento y el tipo de incremento que se va aplicar
	radioSelect = $("input[name='incr2']:checked").val();
	if (radioSelect == "pa") {
		incr = parseFloat($('#txt_incrPaGe').val().replace(",", "."));
		tipo = "pa";
	} else if (radioSelect == "ha") {
		incr = parseFloat($('#txt_incrHaGe').val().replace(",", "."));
		tipo = "ha";
	} else if (radioSelect == "kha") {
		incr = parseFloat($('#txt_incrKilosHaGe').val().replace(",", "."));
		tipo = "kha";
	}
	$('#tipoInc').val(tipo);
	$('#incrGen').val(incr);							

	// validamos lo que el usuario a introducido
	if (validateIncrFormGeneral()) {
		if (filtrarXcultivo == 'true') {
			// Comprobamos si se ha filtrado por cultivo
			if (comprobarFiltroPorCultivo()) {
				$('#method').val('doIncrementar');
				$('#main3').attr('target', '');
				$('#main3').submit();

			} else {
				$('#panelAlertasValidacion')
						.html(
								"Debe filtrar por Cultivo para poder aplicar el incremento general");
				$('#panelAlertasValidacion').show();
			}
		} else {
			$('#method').val('doIncrementar');
			$('#main3').attr('target', '');
			$('#main3').submit();
		}

	}
}
// Comprueba si se ha filtrado por cultivo para incrementar
function comprobarFiltroPorCultivo() {
	var isValid = false;
	var table = document.getElementById('parcelasCpl');
	if (table) {
		var rowCount = table.rows.length - 1;
		var cultivo = table.rows[1].cells[6].innerHTML;

		for ( var i = 1; i < rowCount; i++) {
			var cell = table.rows[i].cells[6].innerHTML;
			if (cultivo == cell) {
				isValid = true;
			} else {
				isValid = false;
				break;
			}
		}
	}
	return isValid;
}
// valida que el incremento introducido es valido
function validateIncrFormGeneral() {
	var isValid = true;
	var radioSelect = $("input[name='incr2']:checked").val();
	var val_incrHa = $('#txt_incrHaGe').val();
	var val_incrPa = $('#txt_incrPaGe').val();
	var val_incrkha = $('#txt_incrKilosHaGe').val();

	// kr por parcela
	if (radioSelect == "pa" && val_incrPa == "") {
		$('#panelAlertasValidacion').html(
				"Debe introducir un incremento Kg/Par.");
		$('#panelAlertasValidacion').show();
		isValid = false;

	} else if (radioSelect == "pa" && isNaN(val_incrPa)) {
		$('#panelAlertasValidacion').html(
				"Debe introducir un incremento valido para Kg/Par.");
		$('#panelAlertasValidacion').show();
		isValid = false;

		// kg por hectarea
	} else if (radioSelect == "ha" && val_incrHa == "") {
		$('#panelAlertasValidacion').html(
				"Debe introducir un incremento para Kg/ha.");
		$('#panelAlertasValidacion').show();
		isValid = false;

	} else if (radioSelect == "ha" && isNaN(val_incrHa)) {
		$('#panelAlertasValidacion').html(
				"Debe introducir un incremento valido para Kg/ha.");
		$('#panelAlertasValidacion').show();
		isValid = false;

		// kha
	} else if (radioSelect == "kha" && val_incrkha == "") {
		$('#panelAlertasValidacion')
				.html(
						"Debe introducir un incremento valido para Kilos totales por Ha.");
		$('#panelAlertasValidacion').show();
		isValid = false;
	} else if (radioSelect == "kha" && isNaN(val_incrkha)) {
		$('#panelAlertasValidacion')
				.html(
						"Debe introducir un incremento valido para Kilos totales por Ha.");
		$('#panelAlertasValidacion').show();
		isValid = false;

		// cualquier otro caso
	} else {
		$('#panelAlertasValidacion').hide();
	}

	return isValid;
}

// funcion que habilita y deshabilita los campos de incrementar
// en funcion de cual este seleccionado. Esta se llama desde esta jsp
function onchange_incrGe(option) {
	// hectareas
	if (option == 'ha') {
		$('#txt_incrHaGe').removeAttr('readonly');
		$('#txt_incrPaGe').attr('readonly', 'readonly');
		$('#txt_incrPaGe').val("");
		$('#txt_incrKilosHaGe').attr('readonly', 'readonly');
		$('#txt_incrKilosHaGe').val("");
		// parcelas
	} else if (option == 'pa') {
		$('#txt_incrPaGe').removeAttr('readonly');
		$('#txt_incrHaGe').attr('readonly', 'readonly');
		$('#txt_incrHaGe').val("");
		$('#txt_incrKilosHaGe').attr('readonly', 'readonly');
		$('#txt_incrKilosHaGe').val("");
		// kilos totales por ha
	} else if (option == 'kha') {
		$('#txt_incrKilosHaGe').removeAttr('readonly');
		$('#txt_incrHaGe').attr('readonly', 'readonly');
		$('#txt_incrHaGe').val("");
		$('#txt_incrPaGe').attr('readonly', 'readonly');
		$('#txt_incrPaGe').val("");
	}
}

// ************* POPUP INCREMENTOS***************

// Abre el popup de incrementos.(camposincrementos.jsp)
function abrirIncremento(idCapitalAsegurado) {

	// Reseteamos el input del incremento
	$('#incr').val('');

	// guardamos el idCapitalAsegurado
	$('#listaIds').val(idCapitalAsegurado);
	$("#visorIncremento").fadeIn('normal');
	$('#overlay').show();
}

// funcion que cierra el popup de incrementos (camposincrementos.jsp)
function cerrarIncremento() {

	$('#visorIncremento').fadeOut('normal');
	$('#sms_error_incr').hide();
	$('#modificarPorcentajes_popup_error').hide();
	$('#overlay').hide();
}

// funcion que incrementa el capital asegurado de la parcela,
// cuando el usuario pincha "el lapiz de editar la parcela"
// Esta funcion se llama desde camposIncremento.jsp

function incrementarDesdePopup() {
	var incr = 0;

	// Recuperamos el incremento y el tipo de incremento que se va aplicar
	radioSelect = $("input[name='incr']:checked").val();
	if (radioSelect == "pa") {
		incr = parseFloat($('#txt_incrPa').val().replace(",", "."));
		tipo = "pa";
	} else if (radioSelect == "ha") {
		incr = parseFloat($('#txt_incrHa').val().replace(",", "."));
		tipo = "ha";
	} else if (radioSelect == "kha") {
		incr = parseFloat($('#txt_incrKilosHa').val().replace(",", "."));
		tipo = "kha";
	}

	// validamos lo que el usuario a introducido
	if (validateIncrFormPopUp()) {

		// alert

		$('#tipoInc').val(tipo);
		$('#incrGen').val(incr);
		$('#method').val('doIncrementar');
		$('#main3').attr('target', '');
		$('#main3').submit();
	}
}
// valida que el incremento introducido en el popup (camposIncremento.jsp) es
// valido
function validateIncrFormPopUp() {

	$('#modificarPorcentajes_popup_error').hide();

	var isValid = true;

	var radioSelect = $("input[name='incr']:checked").val();
	var val_incrHa = $('#txt_incrHa').val();
	var val_incrPa = $('#txt_incrPa').val();
	var val_incrkha = $('#txt_incrKilosHa').val();

	// kr por parcela
	if (radioSelect == "pa" && val_incrPa == "") {
		$('#sms_error_incr').html("Debe introducir un incremento Kg/Par.");
		$('#sms_error_incr').show();
		isValid = false;

	} else if (radioSelect == "pa" && isNaN(val_incrPa)) {
		$('#sms_error_incr').html(
				"Debe introducir un incremento valido para Kg/Par.");
		$('#sms_error_incr').show();
		isValid = false;

		// kg por hectarea
	} else if (radioSelect == "ha" && val_incrHa == "") {
		$('#sms_error_incr').html("Debe introducir un incremento para Kg/ha.");
		$('#sms_error_incr').show();
		isValid = false;

	} else if (radioSelect == "ha" && isNaN(val_incrHa)) {
		$('#sms_error_incr').html(
				"Debe introducir un incremento valido para Kg/ha.");
		$('#sms_error_incr').show();
		isValid = false;

		// kha
	} else if (radioSelect == "kha" && val_incrkha == "") {
		$('#sms_error_incr').html(
				"Debe introducir un incremento para Kilos totales por ha.");
		$('#sms_error_incr').show();
		isValid = false;
	} else if (radioSelect == "kha" && isNaN(val_incrkha)) {
		$('#sms_error_incr')
				.html(
						"Debe introducir un incremento valido para Kilos totales por ha.");
		$('#sms_error_incr').show();
		isValid = false;

		// cualquier otro caso
	} else {
		$('#modificarPorcentajes_popup_error').hide();
		$('#sms_error_incr').hide();

	}

	return isValid;
}
// funcion que habilita y deshabilita los campos de incrementar
// en funcion de cual este seleccionado. Esta se llama desde
// camposIncremento.jsp
function onchange_incr(option) {
	// hectareas
	if (option == 'ha') {
		$('#txt_incrHa').removeAttr('readonly');
		$('#txt_incrPa').attr('readonly', 'readonly');
		$('#txt_incrPa').val("");
		$('#txt_incrKilosHa').attr('readonly', 'readonly');
		$('#txt_incrKilosHa').val("");
		// parcelas
	} else if (option == 'pa') {
		$('#txt_incrPa').removeAttr('readonly');
		$('#txt_incrHa').attr('readonly', 'readonly');
		$('#txt_incrHa').val("");
		$('#txt_incrKilosHa').attr('readonly', 'readonly');
		$('#txt_incrKilosHa').val("");
		// kilos totales por ha
	} else if (option == 'kha') {
		$('#txt_incrKilosHa').removeAttr('readonly');
		$('#txt_incrHa').attr('readonly', 'readonly');
		$('#txt_incrHa').val("");
		$('#txt_incrPa').attr('readonly', 'readonly');
		$('#txt_incrPa').val("");
	}
}

// ///******************* MANTENER CHECKS **************

function onClickInCheck2(idCheck) {
	//alert("onClickInCheck2");
	if (idCheck) {
		var __ids = $('#listaIds').val();

		if (document.getElementById(idCheck).checked == true) {
			
			addCheck2(__ids, idCheck);

		} else {
			subtractCheck2(__ids, idCheck);
		}

	}
}
function onClickInCheckAnexo(idCheck) {

	if (idCheck) {
		
		
		
		
		var __ids = $('#listaIds').val();
	
		
		if (document.getElementById("check_" + idCheck).checked == true) {
		
			addCheck2(__ids, idCheck);
		} else {
			subtractCheck2(__ids, idCheck);
		}
	}
}
function addCheck2(ids, check) {


	//alert("ids: "+ids);
	//alert("check: "+check);
	if(ids != null){
		 if (ids.length>1){
			 ids = ids + "," + check;
		 }else{
			 ids = check;
		 }
	}
		      
	$('#listaIds').val(ids);
	numero_check_seleccionados();
}

function subtractCheck2(ids, check) {
	var newList = "";

	
	
	if(ids != null){
		var array_ids = ids.split(',');
		for(var i = 0; i < array_ids.length; i++){
		     if(array_ids[i] != check && array_ids[i] != "" && array_ids[i] != null && array_ids[i] != undefined){
		         newList = newList + array_ids[i] + ",";
		         //alert(newList);
		     }
		} 
	}
	if (newList.length>0){
		var esDelimitador = newList.substring(newList.length-1,newList.length);
		//alert(esDelimitador);
		if (esDelimitador == ','){
			newList = newList.substring(0,newList.length-1);
		}
	
	}	

	$('#listaIds').val(newList);
	numero_check_seleccionados();
}

function marcar_todos() {
	
	
	$("input[type=checkbox]").each(function() {
		$(this).attr('checked', true);
	});
	var frm = document.getElementById("main3");
	// frm.listaIds.value=frm.listaIds.value +","+ frm.listaIdCapAseg.value;

	frm.listaIds.value = frm.listaIdCapAseg.value;
	frm.marcarTodos.value = "true"; 
	numero_check_seleccionados();
}
function desmarcar_todos() {

	
	$("input[type=checkbox]").each(function() {
		$(this).attr('checked', false);
	});

	var frm = document.getElementById("main3");
	frm.marcarTodos.value = "false";
	
	
	frm.listaIds.value =  "";
	numero_check_seleccionados();
}
function check_checks(ids) {

	if (ids.length > 0) {
		var array_ids = ids.split(',');
		for ( var i = 0; i < array_ids.length; i++) {
			var idCheck = array_ids[i];
			
			if (idCheck != "") {
				$('#' + idCheck).attr('checked', true);
			}
		}
	}
}
// DAA 13/09/2013
function numero_check_seleccionados() {
	var ids = $('#listaIds').val();
	if (ids.length>0){
		var array_ids = ids.split(',');
		$('#sel').text(array_ids.length);
	}else{
		$('#sel').text('0');
	}
//	if ($('#marcarTodos').val() == 'true') {
//
//		$('#sel').text(array_ids.length);
//
//	} else {
//		$('#sel').text(array_ids.length - 1);
//	}
}