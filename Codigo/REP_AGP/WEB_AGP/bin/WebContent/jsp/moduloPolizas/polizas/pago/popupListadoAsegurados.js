function mostrarListadoAsegurados(listaAsegurados) {
	
	console.log(listaAsegurados)
	
	$.each( listaAsegurados, function() {
		cargarAsegurado(this);
	});
	
	$('#overlay').show();
	$('#listadoAsegurados').show();
}

function cerrarListadoAsegurados() {
	$('#registrosAsegurados').empty();
	$('#listadoAsegurados').hide();
}


function setearAsegurado(idAsegurado) {
	$("#idInternoPe").val(idAsegurado);
	cerrarListadoAsegurados();
	// ACTUALIZAMOS LOS CAMPOS DE DOC ENTREGADA
	$('#notaPreviaInput').val("true");
	$('#IPIDInput').val("true");
	$('#RGPDInput').val("true");
	// Y CONTINUAMOS
	confirmaSW();
}

function cargarAsegurado(asegurado) {
	
	$('#registrosAsegurados').append('<tr class="filaAsegurado" onclick="setearAsegurado('+asegurado.idInternoPe+')"><td class="literal">'+asegurado.idExterno+
			'</td><td class="literal">'+asegurado.nombre+' '+asegurado.primerApellido+' '+asegurado.segundoApellido+
			'</td><td class="literal">'+asegurado.codigoOficina+'</td><td class="literal">'+asegurado.fechaNacimiento+
			'</td><td id="idAsegurado" class="literal">'+asegurado.idInternoPe+'</td><td class="literal">'+asegurado.indicadorAcuerdoRuralvia+'</td></tr>');
}