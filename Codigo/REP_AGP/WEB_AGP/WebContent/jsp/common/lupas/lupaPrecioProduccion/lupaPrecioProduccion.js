function abrirVisorRangoPrecios(datos) {
	$('#visor_PrecioProduccion .titulopag').html('RANGO DE PRECIOS');
	$('#tabla_visor_PrecioProduccion tr:[id^="trPrecioProduccion"]').remove();
	createTableRangos(datos, 1);
	$('#visor_PrecioProduccion').show();	
}

function abrirVisorRangoProducciones(datos) {
	$('#visor_PrecioProduccion .titulopag').html('L\u00CDMITES DE RENDIMIENTOS');
	$('#tabla_visor_PrecioProduccion tr:[id^="trPrecioProduccion"]').remove();
	createTableRangos(datos, 2);
	$('#visor_PrecioProduccion').show();
}

function cerrarPrecioProduccion() {
	$('#visor_PrecioProduccion .titulopag').html('');
	$('#tabla_visor_PrecioProduccion tr:[id^="trPrecioProduccion"]').remove();
	$('#visor_PrecioProduccion').hide();
}

function createTableRangos(datos, tipoRango) {
	$.each(JSON.parse(datos), function(index, value) {
		var trHtml = '<tr id="trPrecioProduccion' + index + '" style="background-color: white;">';
		trHtml += '<td class="literal">' + value.codModulo + '</td>';
		trHtml += '<td class="literal">' + value.desModulo + '</td>';
		trHtml += '<td class="literal">' + value.limMin + '</td>';
		trHtml += '<td class="literal">' + value.limMax + '</td>';
		trHtml += '</tr>';
		$('#tabla_visor_PrecioProduccion tr:last').after(trHtml);
		$('#trPrecioProduccion' + index).mouseenter(function() {
			$(this).css('background-color', '#CCCCCC').css('cursor', 'hand');
		}).mouseleave(function() {
			$(this).css('background-color', 'white').css('cursor', 'default');
		}).click(function() {
			switch (tipoRango) {
			case 1:
				$('#precio').val(value.limMax);
				break;
			case 2:
				$('#produccion').val(value.limMax);
				break;
			default:
				break;
			}
			cerrarPrecioProduccion();
		});
	});
}