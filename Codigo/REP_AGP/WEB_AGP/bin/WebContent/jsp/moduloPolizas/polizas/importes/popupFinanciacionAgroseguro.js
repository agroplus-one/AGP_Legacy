/**
 * Oculta el popup de financiación vía Agroseguro
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
	
	// Oculta la fila de 1Âª fracción de pago con el importe correspondiente
	$('#impFraccAgr').html("");
	$('#tomadorFinanciado').hide();
	
	// Elimina el valor de los hidden de importes asociados a la financiación
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
	// Muestra la fila de 1Âª fracción de pago con el importe correspondiente
	$('#impFraccAgr'+numComp).html($('#primeraFraccAgr').html());
	$('#tomadorFinanciado'+numComp).show();
	
	// Vuelca en los hidden la primera fracción a pagar y si se va a enviar el IBAN del asegurado
	$('#netoTomadorFinanciadoAgr'+numComp).val($('#primeraFraccAgr').html());
	$('#enviarIBANFinanciadoAgr'+numComp).val($('#checkIBANAgr').is(':checked'));
	
	// Cierra el popup
	cerrarPopUpFinanciacionAgroseguro();
}

/**
 * Muestra el popup de financiación vía Agroseguro calculando previamente los importes
 * @param importeTomador Coste de la póliza antes de financiar (según devuelve el sw de cálculo)
 * @param recargo % de recargo asociado a la financiación
 * @param numComparativa Identificador de la distribución de costes que se va a financiar
 * @returns
 */
function financiarAgr (importeTomador, recargo, numComparativa) {
	
	// Pinta el neto tomador de la distribución de costes actual en el popup
	$('#totCostTomFraccAgr').html(importeTomador);
	$('#pctRecargoFinanciadoAgr').val(recargo);
	
	// Calcula el neto tomador financiado aplicando el recargo al neto tomador mostrado en la distribución de costes actual
	impTom = Number(importeTomador.replace(/\./g,"").replace(",",".")).toFixed(2);
	recAplicar = Number((Number(recargo).toFixed(2) / 100.00)).toFixed(2);
	//impTomRecargo = parseFloat (impTom)  + parseFloat (Number(impTom * recAplicar).toFixed(2));
	impTomRecargo= parseFloat(Number((Number(impTom)  + Number((impTom * recAplicar)))).toFixed(2));
	$('#totCostTomFraccRecargoAgr').html(impTomRecargo);
	
	// Calcula la primera fracción del pago (1/3 del neto tomador financiado)
	impPrimeraFracc = (impTomRecargo/3).toFixed(2);
	$('#primeraFraccAgr').html(impPrimeraFracc);
	
	//guaradamos el número de comparativa desde la que se ha pedido los datos de financiación
	$('#numCompFinanciacion').val(numComparativa);
	
	$('#panelFinanciacionAgr').show();
	$('#overlay').show();
}