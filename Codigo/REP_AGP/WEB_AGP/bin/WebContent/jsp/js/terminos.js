function initializeTermino() {
    initializeTerminos('Termino');
}

function initializeTerminoIN() {
    initializeTerminos('TerminoIN');
}

function initializeTerminoCM() {
	initializeTerminos('TerminoCM');
}

function initializeTerminos(termino) {
    const FECHA_LIMITE = new Date(2023, 2, 1);
    const COD_LINEA_LIMITE = 400;

    arrCamposBeanFiltros[termino] = new Array('id.codtermino', 'nomtermino');
    
    let arrCampos = ['id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'comarca.nomcomarca', 'id.codtermino', 'nomtermino', 'id.subtermino'];
    arrCamposBean[termino] = arrCampos;
    arrCamposBeanDevolver[termino] = arrCampos;

    var fechaInicioContratacionElement = document.getElementById('fechaInicioContratacion');
    var codLineaElement = document.getElementById('codlinea');

    console.log('fecha de contratacion ' + fechaInicioContratacionElement.value);
    console.log('cod linea: ' + codLineaElement.value);
    
    // Verificar si los elementos existen
    if (!fechaInicioContratacionElement || !codLineaElement) {
        console.log('Elementos fechaInicioContratacion y/o codLinea no encontrados para ${termino}');
        return;
    }
    
    var fechaInicioContratacion = fechaInicioContratacionElement.value;
    var codLinea = codLineaElement.value;

    if (fechaInicioContratacion && codLinea) {
        var fechaInicio = new Date(fechaInicioContratacion);
        codLinea = Number(codLinea);
        
        console.log('fecha Inicio contratacion '+fechaInicio);
        console.log('codLinea '+codLinea);
        
        var fechaInicioSinTiempo = new Date(fechaInicio.getFullYear(), fechaInicio.getMonth(), fechaInicio.getDate());
        var fechaLimiteSinTiempo = new Date(FECHA_LIMITE.getFullYear(), FECHA_LIMITE.getMonth(), FECHA_LIMITE.getDate());

        if (fechaInicioSinTiempo >= fechaLimiteSinTiempo && codLinea < COD_LINEA_LIMITE) {
        	console.log('Fecha de contratación posterior al 01/03/2023, se recoge la descripción SIGPAC');
            arrCampos = ['id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'comarca.nomcomarca', 'id.codtermino', 'nombreSIGPAC', 'id.subtermino'];
            arrCamposBeanFiltros[termino] = new Array('id.codtermino', 'nombreSIGPAC');
        } else if (fechaInicioSinTiempo >= fechaLimiteSinTiempo && codLinea >= COD_LINEA_LIMITE) {
        	console.log('Fecha de contratación posterior al 01/03/2023, se recoge la descripción REGA');
            arrCampos = ['id.codprovincia', 'provincia.nomprovincia', 'id.codcomarca', 'comarca.nomcomarca', 'id.codtermino', 'nombreREGA', 'id.subtermino'];
            arrCamposBeanFiltros[termino] = new Array('id.codtermino', 'nombreREGA');
        } else {
        	console.log('Fecha de contratación anterior al 01/03/2023, se recoge la descripción normal');
        }
      
        arrCamposBean[termino] = arrCampos;
        arrCamposBeanDevolver[termino] = arrCampos;
        console.log('arrCamposBean[${termino}] y arrCamposBeanDevolver[${termino}] inicializados');
    } 
    else {
    	console.log('Elementos fechaInicioContratacion y/o codLinea son null para ${termino}');
    }
    
}

document.addEventListener('DOMContentLoaded', function() {
    initializeTermino();
    initializeTerminoIN();
    initializeTerminoCM();
});
