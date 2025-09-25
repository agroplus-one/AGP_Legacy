/**
 Creacion de la funcion operacionInformeDetalle que:
 Reciba un parametro de entrada que indique si se desea el informe de la poliza o de la situacion actualizada.
 Realice la comprobacion de que únicamente haya una poliza seleccionada en el listado.
 Realice una llamada al método doInformeDetalle del nuevo controlador InformesDetalleController.java.
*/


/**
 * 
 * @param tipo poliza | situacion_actualizada
 * @returns
 */
function operacionInformeDetalle(tipo) {
	
	if (tipo === null || tipo === undefined) {
		console.error('informesDetalle.js [operacionInformeDetalle]: no has establecido el tipo de informe');
		return;
	}
	
	
	limpiarPaneles();
	var list = "";
	var contador = 0;
	$('#filtro').val('consulta');
	$("input[type=checkbox]").each(function(){
		if($(this).attr('checked')){
			list = list + $(this).val();
			contador++;
		}
	});
	if(contador > 0){
		if (contador > 1){// hay mas de una poliza marcada			
			$('#divAviso').show();
			$('#txt_info_check_multiple').show();
			$('#overlay').show();
		}else{// solo una poliza marcada
			var arrayValores = list.split("#"); 
			var idPoliza = arrayValores[0];
			getDoInformeDetalle(idPoliza, tipo);
		}
	}else{// no hay polizas seleccionadas		
		$('#divAviso').show();
		$('#txt_info_none').show();
		$('#overlay').show();
	}
}


function getDoInformeDetalle(idPoliza, tipo) {
	
	$('#informeIdPoliza').val(idPoliza);
	$('#informeTipo').val(tipo);
	$('#doInforme').submit();
}