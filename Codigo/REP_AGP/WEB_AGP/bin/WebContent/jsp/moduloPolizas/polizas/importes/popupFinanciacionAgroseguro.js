/**
 * Oculta el popup de financiaci�n v�a Agroseguro
 * @returns
 */
function cerrarPopUpFinanciacionAgroseguro(){
	
	// Borra los datos previos
	$('#totCostTomFraccAgr').html("");
	$('#totCostTomFraccRecargoAgr').html("");
	$('#primeraFraccAgr').html("");
	if ($('#modoLectura').val() != 'modoLectura') {
		$('#checkIBANAgr').attr('checked', false);
	}
	// Oculta el popup
	$('#panelFinanciacionAgr').hide(); 
    $('#overlay').hide();
}

/**
 * No se financia con Agroseguro
 */
function noFinanciarAgroseguro () {
	
	// Oculta la fila de 1ª fracci�n de pago con el importe correspondiente
	$('#impFraccAgr').html("");
	$('#tomadorFinanciado').hide();
	
	// Elimina el valor de los hidden de importes asociados a la financiaci�n
	$('#netoTomadorFinanciadoAgr').val("");
	$('#enviarIBANFinanciadoAgr').val("");
	$('#pctRecargoFinanciadoAgr').val("");
	
	// Cierra el popup
	cerrarPopUpFinanciacionAgroseguro();
}

/**
 * Se financia con Agroseguro
 */
function financiarAgroseguro() {
	var frm = document.getElementById('frmFinanciacionAgr');
	var numComp = frm.numCompFinanciacion.value;
	// Muestra la fila de 1ª fracci�n de pago con el importe correspondiente
	$('#impFraccAgr'+numComp).html($('#primeraFraccAgr').html());
	$('#tomadorFinanciado'+numComp).show();
	
	// Vuelca en los hidden la primera fracci�n a pagar y si se va a enviar el IBAN del asegurado
	$('#netoTomadorFinanciadoAgr'+numComp).val($('#primeraFraccAgr').html());
	$('#enviarIBANFinanciadoAgr'+numComp).val($('#checkIBANAgr').is(':checked'));
	
	// Cierra el popup
	cerrarPopUpFinanciacionAgroseguro();
}

/**
 * Muestra el popup de financiaci�n v�a Agroseguro calculando previamente los importes
 * @param importeTomador Coste de la p�liza antes de financiar (seg�n devuelve el sw de c�lculo)
 * @param recargo % de recargo asociado a la financiaci�n
 * @param numComparativa Identificador de la distribuci�n de costes que se va a financiar
 * @returns
 */
function financiarAgr (importeTomador, recargo, numComparativa) {
	
	// Pinta el neto tomador de la distribuci�n de costes actual en el popup
	$('#totCostTomFraccAgr').html(importeTomador);
	$('#pctRecargoFinanciadoAgr').val(recargo);
	
	// Calcula el neto tomador financiado aplicando el recargo al neto tomador mostrado en la distribuci�n de costes actual
	impTom = Number(importeTomador.replace(/\./g,"").replace(",",".")).toFixed(2);
	recAplicar = Number((Number(recargo).toFixed(2) / 100.00)).toFixed(2);
	//impTomRecargo = parseFloat (impTom)  + parseFloat (Number(impTom * recAplicar).toFixed(2));
	impTomRecargo= parseFloat(Number((Number(impTom)  + Number((impTom * recAplicar)))).toFixed(2));
	$('#totCostTomFraccRecargoAgr').html(impTomRecargo);
	
	// Calcula la primera fracci�n del pago (1/3 del neto tomador financiado)
	impPrimeraFracc = (impTomRecargo/3).toFixed(2);
	$('#primeraFraccAgr').html(impPrimeraFracc);
	
	//guaradamos el n�mero de comparativa desde la que se ha pedido los datos de financiaci�n
	$('#numCompFinanciacion').val(numComparativa);
	
	$('#panelFinanciacionAgr').show();
	$('#overlay').show();
}