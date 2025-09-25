function duplica(codModulo) {
	var maxComp = parseInt($('#maxComparativas').val());
	var contComp = parseInt($('#contComparativas').val());
	if (parseInt(contComp) + parseInt(1) > maxComp) {
		jAlert("Maximo numero de comparativas alcanzado", "Error");
	} else {
		var contComp = parseInt(contComp) + parseInt(1);
		$('#contComparativas').val(contComp);
		// buscar la siguiente comparativa oculta por modulo y mostrarla
		for (i = 2; i <= maxComp; i++) {
			var compMod = codModulo + '_' + i;
			if ($('#compDIV_' + compMod).is(':visible')) {
				
			} else {
				$('#compDIV_' + compMod).show();
				habilitarCamposComparativa(codModulo, i);
				bloquearCamposconVinculacion();
				break;
			}
		}
		ActivarBotonDuplicaMaxComp();
	}
}

function borrarComparativa(contComp, codModulo) {
	deshabilitarBotonesComparativa(contComp, codModulo);
	$('#compDIV_' + codModulo + '_' + contComp).hide();
	var contComp = parseInt($('#contComparativas').val());
	$('#contComparativas').val(parseInt(contComp) - parseInt(1));
	ActivarBotonDuplicaMaxComp();
}