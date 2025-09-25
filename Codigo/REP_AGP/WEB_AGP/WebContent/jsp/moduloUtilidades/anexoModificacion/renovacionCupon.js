/**
 * Realiza la llamada para comprobar si el anexo por cup�n caducado es editable o no
 * @param idAnexo
 * @param idPoliza
 */
function editarAMCuponCaducado (idAnexo, idPoliza, referencia, plan) {
		
	var frm = document.getElementById('main');
	// Realiza la llamada al SW
	$.ajax({
		url:          UTIL.antiCacheRand("declaracionesModificacionPoliza.html"),
		data:         "method=isEditableAMCuponCaducado&idAnexo=" + idAnexo + "&idPoliza=" + idPoliza,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			alert("Error: " + quepaso);
		},
		success: function(resultado){
			renovarAMCuponCaducado (resultado.isEditableAMCuponCaducado, idAnexo, idPoliza, referencia, plan);
		},
		type: "GET"
	});	
}

/**
 * Procesa la respuesta de llamada de la funci�n 'editarAMCuponCaducado'
 * @param isEditableAMCuponCaducado
 */
function renovarAMCuponCaducado (isEditableAMCuponCaducado, idAnexo, idPoliza, referencia, plan) {
	
	if (isEditableAMCuponCaducado == '0') {
		jConfirm('El cupón asociado al anexo está caducado. Se va a proceder a solicitar un nuevo cupón. ¿Desea continuar?',
				'Diálogo de Confirmación', function(r) {
			if (r==true) {
					solicitarCuponAMSW(idAnexo, idPoliza, referencia, plan);
				}
			}
		);
	}
	else if (isEditableAMCuponCaducado == '1') {
		alert ('No se puede editar debido a que existe otro anexo por cupón en provisional y con el cupón en activo para la póliza asociada');
	}
	else if (isEditableAMCuponCaducado == '2') {
		alert ('No se puede editar debido a que existe otro anexo por ftp enviado correcto en fecha posterior a la del anexo en cuestión');
	}
	else if (isEditableAMCuponCaducado == '3') {
		alert ('No se puede editar debido a que existe otro anexo por cupón enviado correcto en fecha posterior a la del anexo en cuestión');
	}
	else {
		alert ('Ha ocurrido un error al comprobar si el anexo es editable');
	}
}

/**
 * Realiza la llamada al SW de Solicitud de Modificacion
 */
function solicitarCuponAMSW (idAnexo, idPoliza, referencia, plan) {
	
	// Muestra la capa informativa
	muestraCapaEspera ("Solicitando nuevo cup&oacute;n");
	
	// Se rellena el hidden que indica que se va a renovar el cup�n de un AM caducado
	$("#idAnexoCaducado").val('S');
	
	// Realiza la llamada al SW
	$.ajax({
		url:          UTIL.antiCacheRand("solicitudModificacion.html"),
		data:         "method=doSolicitudModificacion&referencia=" + referencia + "&codPlan=" + plan,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			// Se borra el campo del anexo caducado para hacer posible que se de un alta nueva 
			$("#idAnexoCaducado").val('');
			
			quitaCapaEspera ();
			alert("Error en la llamda al SW de Solicitud de Modificacion: " + quepaso);
		},
		success: function(resultado){
			doEditarAMCuponCaducado (resultado, idAnexo, idPoliza);
		},
		type: "GET"
	});	
	
}

/**
 * Realiza la llamada para actualizar el antiguo cup�n con el nuevo y editar el anexo
 * @param idCupon
 * @param idAnexoMod
 * @param idPoliza
 */
function doEditarAMCuponCaducado (resultado, idAnexoMod,idPoliza){	
	
	// Comprueba si se ha recibido correctamente el id de cup�n o ha ocurrido alg�n error
	if (resultado.error != null && resultado.error != '') {
		quitaCapaEspera ();
		
		// Se borra el campo del anexo caducado para hacer posible que se de un alta nueva 
		$("#idAnexoCaducado").val('');
		
		alert (resultado.error);
	}
	else {
		$("#method").val("doEdita");
		$("#id").val(idAnexoMod);
		$("#idPoliza").val(idPoliza);
		$("#idCupon").val(resultado.idCupon);
		$("#main").submit();
	}
	
}

/**
 * Muestra la capa de espera con el mensaje recibido como par�metro
 * @param msg
 * @returns
 */
function muestraCapaEspera (msg) {
	$.blockUI.defaults.message = '<h4> ' + msg + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}

/**
 * Elimina la capa de espera
 */
function quitaCapaEspera () {
	$.unblockUI();
}