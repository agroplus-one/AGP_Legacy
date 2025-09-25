/**
 * 
 */


document.addEventListener("DOMContentLoaded", function() {
	var codLineaElement = document.getElementById('codlinea').value;

	if (codLineaElement == '308')
		arrObjetosLupas['VistaFechaFinGarantias'] = "com.rsi.agp.dao.tables.cpl.FechaFinGarantia308";
	else
		arrObjetosLupas['VistaFechaFinGarantias'] = "com.rsi.agp.dao.tables.cpl.FechaFinGarantia";
});
