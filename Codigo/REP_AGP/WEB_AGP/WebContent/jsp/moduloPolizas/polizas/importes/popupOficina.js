
$(document).ready(function(){
	$("#panelOficinas").draggable();
});

function showOficinas(){	
		
	/* Restauramos los valores originales de la base de datos 
	 por si han sido modificados sin aceptar los cambios */
	$('#oficinaActual').val($('#oficinaOriginal').val());
	$('#desc_oficina').val($('#nombreOficinaOriginal').val());
	
	/* Mostramos el popUp */
	$('#panelAlertasValidacion').html("");
	$('#panelAlertasValidacion').hide();
		
	$('#panelAlertasValidacion_ofi').html("");
	$('#panelAlertasValidacion_ofi').hide();
	
	$('#panelOficinas').css('width','35%');
	$('#overlay').show();
	$('#panelOficinas').show();

}
	
function cerrarPopUpOficinas(){
	// limpiamos alertas
	$('#panelAlertasValidacion_ofi').html("");
	$('#panelAlertasValidacion_ofi').hide();
	$('#panelOficinas').hide();
	$('#overlay').hide();
}

function aplicarOficina(datos){
	
	var frm = document.getElementById('frmOficinas');
	
	if (frm.oficinaActual.value != ""){
		frm.method.value = 'cambiarOficina';

		cerrarPopUpOficinas();
		frm.submit();
	} else {
		$('#panelAlertasValidacion_ofi').html("Valor para Oficina no válido");
		$('#panelAlertasValidacion_ofi').show();
		//return false;
	}
}

function obtenerDatosOficina(){
	
	lupas.muestraTabla('Oficina','principio', '', '');	
	
}


function limpiarAlertas (){
	$('#panelAlertasValidacion_ofi').html("");
	$('#panelAlertasValidacion_ofi').hide();
	$('#panelOficinas').hide();
	$('#overlay').hide();
}

function cambioValorOficina (){
	$('#oficinaActual').val($('#oficina').val());
	$('#oficina').val("");
}