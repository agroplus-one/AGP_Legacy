// Muestra el mensaje indicando que el informe seleccionado no tiene datos de informe asociados
function mostrarMsgNoDatos() {
	$("#panelAlertasValidacion").html('El informe no se puede generar ya que no tiene ningún dato de informe asociado.');
	$("#panelAlertasValidacion").show();
}

// Muestra el mensaje indicando que el informe seleccionado tiene alguna tabla no relacionada
function mostrarMsgNoRelTablas() {
	$("#panelAlertasValidacion").html('El informe no se puede generar ya que existen tablas no relacionadas.');
	$("#panelAlertasValidacion").show();
}

// Realiza la llamada al controlador antes de lanzar el informe para comprobar:
// 1- si el informe seleccionado tiene datos de informe
// 2- que todas sus tablas estén relacionadas
// 3- que tenga al menos una condición dada de alta
function ajaxCheckInforme(id){
	limpiaAlertas();
	if (id == '' || id == undefined){
		if ($('#idInforme').val() != ''){
			var id = $('#idInforme').val();
		}else{
			var frm = document.getElementById('main');
			var id = frm.idInforme.value;
		}
	}
	$.ajax({
		url:          "generacionInforme.run",
		data:         "method=verificarInforme&idInforme=" + id,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
			alert("Error al comprobar los datos del informe: " + quepaso);
		},
		success: function(resultado){
			switch(parseInt(resultado.datos)){
				case 0: // No tiene datos de informe asociados
					mostrarMsgNoDatos();
					break;
				case 1: // hay al menos una tabla que no está relacionada
				  	mostrarMsgNoRelTablas();
				 	break;
				case 2: // no hay condiciones creadas en el informe
					if ($('#condicionesOk').val() != "true"){
						if(confirm('¿No existen condiciones creadas, desea visualizar el informe igualmente?')){
							$('#condicionesOk').val('true');
							elegirFormato(id);
						}
					}else{
						elegirFormato(id);
					}
				  	break;
				case 3: // comprobaciones correctas, se ejecuta el informe
					elegirFormato (id);
					break;
				default:
				  	break;
			}
		},
		type: "GET"
	});
}
