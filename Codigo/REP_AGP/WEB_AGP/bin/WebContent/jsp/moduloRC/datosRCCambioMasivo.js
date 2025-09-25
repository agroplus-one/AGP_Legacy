function cambioMasivo() {
	limpiarCambioMasivoDatosRC();
	var listaIdsMarcados = $('#listaIdsMarcados').val();
	//alert(listaIdsMarcados.length);
	if(listaIdsMarcados.length>0){
		$('#listaIdsMarcados_cm').val(listaIdsMarcados);
		$('#overlayCambioMasivo').show();
		$('#panelCambioMasivoDatosRC').show();
	}
	else{	
		showPopUpAviso("Debe seleccionar como mínimo un Dato para RC.");
	}	
}

function cerrarCambioMasivoDatosRC() {
	limpiarCambioMasivoDatosRC();
	$('#panelCambioMasivoDatosRC').hide();
	$('#overlayCambioMasivo').hide();
}

function limpiarCambioMasivoDatosRC() {
	$('#txt_mensaje_cm').html('');
	$('#tasaCM').val('');
	$('#franquiciaCM').val('');
	$('#primaMinimaCM').val('');
}

function aplicarCambioMasivoDatosRC() {
	if ($("#formDatosRCCM").valid()) {
		if ($('#tasaCM').val() == '' && $('#franquiciaCM').val() == '' && $('#primaMinimaCM').val() == '') {
			$('#txt_mensaje_cm').html("* Debe seleccionar al menos un cambio");
			$('#txt_mensaje_cm').show();
		} else {
			$.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({
				overlayCSS : {
					backgroundColor : '#525583'
				},
				baseZ : 2000
			});
			$('#formDatosRCCM').submit();
			cerrarCambioMasivoDatosRC();
		}
	}
}