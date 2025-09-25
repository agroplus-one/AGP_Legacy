function cerrarInfoSubvs() {
	$('#subveciones-asegurado-popup').hide();
	$('#overlay').hide();
}

function abrirInfoSubvenciones(nifcif, codPlan, codLinea){
	$.ajax({
		url: 'controlAccesoSubvenciones.html',
		data: {method: 'doControlSubvsAsegurado', nifCif: nifcif, codPlan: codPlan, codLinea: codLinea},
		dataType: 'json',
		cache: false,
		success: function(data){
			if (data.agroMsg) {
				jAlert(data.agroMsg, 'Error');
				$.unblockUI();
				$('#overlay').hide();
			} else {
				$('#resultado-subveciones-asegurado').html(data.html);
				$('#subveciones-asegurado-popup').show("normal");
				$.unblockUI();
				$('#overlay').show();
			}
		},
		beforeSend : function() {
			//muestraCapaEspera('Realizando la consulta');
			$.blockUI.defaults.message = '<h4>Realizando la consulta.<br>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		},
		error: function(objeto, quepaso, otroobj){
			jAlert("Error al realizar la llamada al WS de Agroseguro.", 'Error');
			$.unblockUI();
		},
		type: 'GET'
	});
}