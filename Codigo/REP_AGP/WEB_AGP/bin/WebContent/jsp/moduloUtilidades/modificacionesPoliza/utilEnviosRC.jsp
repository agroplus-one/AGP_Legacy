<script type="text/javascript">

/**
* Envía a calcular el anexo actual
*/
function calcularAnexo () {
	muestraCapaEspera("Calculando el R.C");
	$('#calculoModificacion').submit(); 
}

/**
 * Intenta confirmar el anexo acutal
 */
function confirmarAnexoRC(mostrarMsg, perfil34) {
	muestraCapaEspera("Confirmando el R.C");
	if (mostrarMsg == true)	mostrarMsgContratacion (perfil34);
	else {
		// Se indica que el método a instanciar es 'doConfirmar', ya que este formulario también se utiliza para volver
		$('#methodContinuar').val('doConfirmarRC');
		$('#formConfirmar').submit(); 
	}
}

function intentarConfirmar () {
	$('#indRevAdm').val('N');
	confirmarAnexoRC (false);
	ocultarMsgContratacion();
}

function forzarConfirmacion () {
	$('#indRevAdm').val('S');
	confirmarAnexoRC (false);
	ocultarMsgContratacion();
}

/**
 * Muestra el mensaje de confirmacion
 */
function mostrarMsgContratacion (perfil34){
	
	if (perfil34) {
		$('#msgPerfil34').show();
		$('#forzar').hide();
	}
	else {
		$('#msgPerfil34').hide();
		$('#forzar').show();
	}
	
    $('#panelConfirmacion').show();
    $('#overlay').show();
} 

/**
 * Oculta el mensaje de confirmacion
 */
function ocultarMsgContratacion (){
    $('#panelConfirmacion').hide();
    $('#overlay').hide();
}   


/**
 * Muestra la capa de espera con el mensaje recibido como parámetro
 * @param msg
 * @returns
 */
function muestraCapaEspera (msg) {
	$.blockUI.defaults.message = '<h4> ' + msg + '.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
}

</script>