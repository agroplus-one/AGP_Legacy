/**
 * Crea la tabla del popup de Coberturas de la parcela
 * el maximo precio devuelto
 * @param list
 */
function mostrarCoberturasParc(listaCobParcelas, modoLectura) {
	
	var pintarCoberturas = "";
	
	var modulo = "";
	var cpm = "";
	var riesgoCubierto = "";
	var dvCodConcepto = "";
	
	var hasSelect = false;
	
	var contador = 1;
	var newList = "";
	var codCptAux = "";
	var list = "";
	
	$.each(listaCobParcelas, function(index, value){
		
		if (modoLectura == "modoLectura"){
		
			if (!isNaN(value.cpm)) {// SON COBERTURAS
			
				var cmp = value.cpm;
				/* Datos variables */
				if (!isNaN(value.dvCodConcepto)) {
					if (codCptAux != ""){
						if (value.dvCodConcepto != codCptAux ){
							list = codCptAux + "-" + contador;
							newList = newList + list + ";";
						
						}else{
							contador = contador + 1;
						}
					}	
					codCptAux = value.dvCodConcepto;
				}
			}		
		}else{
			contador = 5;	
		}
		
	});
	
	//Guardamos en la lista el ultimo valor
	list = codCptAux + "-" + contador;
	newList = newList + list + ";";
		
	$.each(listaCobParcelas, function(index, value){
		
		if (modulo != value.codmodulo) {
			if (modulo != '') {
				if (hasSelect) {
					pintarCoberturas = pintarCoberturas + "</select></td>";
					hasSelect = false;
				}
				pintarCoberturas = pintarCoberturas + "</tr>";
			}
			pintarCoberturas = pintarCoberturas + "<tr><td class='literalbordeCob'  align='center'>" + value.descModulo + "</td>";
			modulo = value.codmodulo;
			cpm = '';
			riesgoCubierto = '';
			dvCodConcepto = '';
		}
		
		if (cpm != value.cpm) {
			if (hasSelect) {
				pintarCoberturas = pintarCoberturas + "</select></td>";
				hasSelect = false;
			}
			pintarCoberturas = pintarCoberturas + "<td class='literalbordeCob' align='center'>"+ value.cpmDescripcion + " </td>";
			cpm = value.cpm;
			riesgoCubierto = '';
			dvCodConcepto = '';
		}
		
		if (riesgoCubierto != value.riesgoCubierto) {
			if (hasSelect) {
				pintarCoberturas = pintarCoberturas + "</select></td>";
				hasSelect = false;
			}
			pintarCoberturas = pintarCoberturas + "<td class='literalbordeCob' align='center'>"+ value.rcDescripcion  + " </td>";
			riesgoCubierto = value.riesgoCubierto;
			dvCodConcepto = '';
		}
		
		if (isNaN(value.dvCodConcepto)) {
			if (value.elegible == 'S') {
				if (modoLectura =="modoLectura") {
					pintarCoberturas = pintarCoberturas + ("<td class='literalbordeCob' align='center' >" +
							"<input type='checkbox' disabled='disabled' name='cob' id='riesg_cub_"+value.id+"'  class='dato' value='"+value.id+"|"+value.codmodulo+"|"+value.fila+"|"+value.cpm+"|"
							+value.cpmDescripcion+"|"+value.riesgoCubierto+"|"+value.rcDescripcion+"|"+value.vinculada+"|"+value.elegible+"|"+value.tipoCobertura+"#"+value.dvCodConcepto+"|"
							+value.dvDescripcion+"|"+value.dvValor+"|"+value.dvValorDescripcion+"|"+value.dvColumna+"' onclick='grabaChekCobParcela(this)'");	
				} else {
					pintarCoberturas = pintarCoberturas + ("<td class='literalbordeCob' align='center' >" +
							"<input type='checkbox' name='cob' id='riesg_cub_"+value.id+"'  class='dato' value='"+value.id+"|"+value.codmodulo+"|"+value.fila+"|"+value.cpm+"|"
							+value.cpmDescripcion+"|"+value.riesgoCubierto+"|"+value.rcDescripcion+"|"+value.vinculada+"|"+value.elegible+"|"+value.tipoCobertura+"#"+value.dvCodConcepto+"|"
							+value.dvDescripcion+"|"+value.dvValor+"|"+value.dvValorDescripcion+"|"+value.dvColumna+"' onclick='grabaChekCobParcela(this)'");	
					
				}
																																																																											
				if (value.elegida == 'S') {							
					pintarCoberturas = pintarCoberturas + " checked='true'";
				}
				
				pintarCoberturas = pintarCoberturas + "> </td>";	
			} else { 
				// elegible= N
				pintarCoberturas = pintarCoberturas + ("<input type='checkbox' style='display:none' name='cob' id='riesg_cub_"+value.id+"' class='dato' value='"+value.id+"|"+value.codmodulo+"|"+value.fila+"|"+value.cpm+"|"+value.cpmDescripcion+"|"+value.riesgoCubierto+"|"+value.rcDescripcion+"|"+value.vinculada+"|"+value.elegible+"|"+value.tipoCobertura+"#"+value.dvCodConcepto+"|"+value.dvDescripcion+"|"+value.dvValor+"|"+value.dvValorDescripcion+"|"+value.dvColumna+"' />");				
			}
		} else {
			if (contador > 1) {
				if (dvCodConcepto != value.dvCodConcepto) {
					if (dvCodConcepto != '') {
						pintarCoberturas = pintarCoberturas + "</select></td>";
					}					
					if (modoLectura =="modoLectura") {
						pintarCoberturas = pintarCoberturas + 
						"<td class='literalbordeCob'  align='left'>"+value.dvDescripcion+
						" <br><input type='hidden' id='datVar_"+value.id+"' name='datVar_"+value.id+"' value='"+value.id+"|"+value.codmodulo+"|"+value.fila+"|"+value.cpm+"|"+value.cpmDescripcion+"|"+value.riesgoCubierto+"|"+value.rcDescripcion+"|"+value.vinculada+"|"+value.elegible+"|"+value.tipoCobertura+"#"+value.dvCodConcepto+"|"+value.dvDescripcion+"|"+value.dvValor+"|"+value.dvValorDescripcion+"|"+value.dvColumna+"' />" +
						" <select name='selDV_"+value.dvCodConcepto+"'  class='datoCob' style='width: 70%'  disabled='disabled' id='seleccionDatVar_"+value.codmodulo+"_"+value.fila+"_"+value.cpm+"_"+value.riesgoCubierto+"_"+value.dvCodConcepto+"_"+value.dvCodConcepto+"'>";					
					} else {
						pintarCoberturas = pintarCoberturas + 
						"<td class='literalbordeCob'  align='left'>"+value.dvDescripcion+
						" <br><input type='hidden' id='datVar_"+value.id+"' name='datVar_"+value.id+"' value='"+value.id+"|"+value.codmodulo+"|"+value.fila+"|"+value.cpm+"|"+value.cpmDescripcion+"|"+value.riesgoCubierto+"|"+value.rcDescripcion+"|"+value.vinculada+"|"+value.elegible+"|"+value.tipoCobertura+"#"+value.dvCodConcepto+"|"+value.dvDescripcion+"|"+value.dvValor+"|"+value.dvValorDescripcion+"|"+value.dvColumna+"' />" +
						" <select name='selDV_"+value.dvCodConcepto+"'  class='datoCob' style='width: 70%'  style='display:none' onchange='funcSelected(this.id,this.value)' id='seleccionDatVar_"+value.codmodulo+"_"+value.fila+"_"+value.cpm+"_"+value.riesgoCubierto+"_"+value.dvColumna+"'>";
					}
					pintarCoberturas = pintarCoberturas + "<option value='"+value.id+"|"+value.codmodulo+"|"+value.fila+"|"+value.cpm+"|"+value.cpmDescripcion+"|"+value.riesgoCubierto+"|"+value.rcDescripcion+"|"+value.vinculada+"|"+value.elegible+"|"+value.tipoCobertura+"#"+value.dvCodConcepto+"|X'></option>";
					hasSelect = true;
					dvCodConcepto = value.dvCodConcepto;
				}
				pintarCoberturas = pintarCoberturas + "<option value='"+value.id+"|"+value.codmodulo+"|"+value.fila+"|"+value.cpm+"|"+value.cpmDescripcion+"|"+value.riesgoCubierto+"|"+value.rcDescripcion+"|"+value.vinculada+"|"+value.elegible+"|"+value.tipoCobertura+"#"+value.dvCodConcepto+"|"+value.dvDescripcion+"|"+value.dvValor+"|"+value.dvValorDescripcion+"|"+value.dvColumna+"'";
				if (value.dvElegido == 'S') {	
					pintarCoberturas = pintarCoberturas + " selected='selected' >"+value.dvValorDescripcion+"</option>";
				} else {
					pintarCoberturas = pintarCoberturas + ">"+value.dvValorDescripcion+"</option>";
				}
			} else {
				pintarCoberturas = pintarCoberturas +"<td class='literalbordeCob'  align='left'>"+value.dvDescripcion + " <br>" + value.dvValorDescripcion	+" <br></td>";
			}
		}
	});
	
	if (pintarCoberturas == ""){
		$("#contenedorCob").html("");
		$('#contenedorCoberturas').hide();
	}else{
		if (hasSelect) {
			pintarCoberturas = pintarCoberturas + "</select></td>";
			hasSelect = false;
		}		
		pintarCoberturas = pintarCoberturas + "</tr>";
		$('#tieneCoberturas').val('true');
		$('#contenedorCoberturas').show();	
		$("#contenedorCob").html(pintarCoberturas);
	}
	
	comprobarChecksMarcados();
}


function grabaChekCobParcela(obj){
	
	var checksMarcados = "";
	var contador = 1;
	$('#contenedorCob :input[id^="riesg_cub_"]').each(function() {
		var datos = $(this).attr('value');
		var listaDatos = datos.split('|');
		
		var isChecked = +$(this).attr('checked')
		
		if (isChecked == true){
			checksMarcados = checksMarcados + listaDatos[0] + '|' + 'S' + '#';
		}else{
			checksMarcados = checksMarcados + listaDatos[0] + '|' + 'N' + '#';
		}
		
	});
	
	$('#checksMarcados').val(checksMarcados);
	
	comprobarChecksMarcados();
	
}

function montarCoberturasParcelasNew(form){
	
		var cobExistentes = "";    
	   
	    var tieneSelects = false;
	    var datVarTemp = '';
	    var checksMarcados = $('#checksMarcados').val();
	    
	    var listacheck = checksMarcados.split("#");
	    
	    var contador = 1;
		$('#contenedorCob :input[id^="riesg_cub_"]').each(function() {
			var datos = $(this).attr('value');
			var listaDatos = datos.split('|');
			
			/* Recorro el array de los valores de los checks de coberturas */
			for (j=0; j<listacheck.length; j++){
		    	
		    	var valorcheck = listacheck[j].split("|");
		    	var nombrecheck = $(this).attr('id');
		    	
    			var nombvalorcheck = "riesg_cub_" + valorcheck[0];
    			
    			if (nombrecheck == "riesg_cub_" + valorcheck[0]){
    				if (valorcheck[1] == 'S'){
    					
						$('#'+ nombrecheck).attr('disabled',false);
						$('#'+ nombrecheck).attr('checked',true);
    				}else{
						$('#'+ nombrecheck).attr('checked',false);
    				}
    			}	
			}			
		});
		
		var contador2 = 1;
		var datVarSeleccionados = $('#datVarSeleccionados').val();
		var listadatVar = datVarSeleccionados.split("+");
		
		$("select[id*='seleccionDatVar_']").each(function(){
			var datos = $(this).attr('value');
			console.log('id: ' + $(this).attr('id'));
			var listaDatosSel = datos.split('|');
			var idSelect = listaDatosSel[0];
			console.log('idSelect: ' + idSelect);
			/* Recorro el array de los valores de los checks de coberturas */
			for (i=0; i<listadatVar.length; i++){
				var datos2 = listadatVar[i].split('-');
				var idlista = datos2[0];
				var valorlista = datos2[1];
				console.log('idlista: ' + idlista);
				console.log('valorlista: ' + valorlista);
				if (idSelect == idlista && valorlista == 'S'){
					var datoSelec = datos2[2];
					console.log('Seleccionamos: ' + datoSelec);
					$(this).val(datoSelec);
					break;
				}
			}			
		});		
	}


function comprobarChecksCoberturas(nombreForm){
	//comprobar checks de coberturas por si hay que deshabilitar el select asociado
	var form = document.getElementById(nombreForm);	
	for(i=0;i<form.elements.length; i++){
	    if(form[i].type == 'checkbox') {
	    	var cob  = form[i].value.split('|');
	    	if (cob[8] == 'S'){
	    		grabaChekCobParcela(form[i]);
	    	}
	    }
	}
}

function funcSelected(obj1, obj2){
	var cob  = obj2.split('|');
	var mod  = cob[1];
	var fila = cob[2];
	var cpm  = cob[3];
	var rc   = cob[5];

	var vinculacion = cob[7];
	
	if (cob[7] != '' && cob[7] !='null'){
		var vinc = cob[7].split('#');
		for(j=0;j<vinc.length; j++){
			var vinci = vinc[j].split('.');
			
			var vinculacion  = vinci[0];
			var fila         = vinci[1];
			var vinc_elegida = vinci[2];
			var modulo 		 = vinci[3];
			var columna 	 = vinci[4];
			var valor   	 = vinci[5];
			
			/*obtenemos el nombre del campo vinculado*/
			var nombVinc = obj1.substring(0, obj1.length-1);
			nombVinc = nombVinc + vinci[4];
			
			$("select[id*='seleccionDatVar_']").each(function(){
				var datosDatVar = $(this).attr('value');
				var idDatVar = $(this).attr('id');
				
				/* Hemos encontrado el combo vinculado*/
				if (idDatVar == nombVinc){
					id = ($(this).attr('id'));
					
				   	// recorremos los options del select
				   	$("#"+id+" option").each(function() {
				   		sDatVar = $(this).attr('value');
					   	var cobs = sDatVar.split('#');
					   	
					   	var objT = cobs[0].split('|');
					   	var datT = cobs[1].split('|');
					   	var datT_0 = datT[0];
					   	var datT_1 = datT[1];
					   	
					   	if (datT_1 != 'X'){
					   		var datT_2 = datT[2];
					   		var datT_3 = datT[3];
					   		if (datT_2 == valor){
					   			$('#'+idDatVar).val(sDatVar);
					   		}
					   	}					   	
					   	//buscamos la descripcion del valor
				   	});
					
				}
			});
			
			
			
			if (typeof cobFila != 'undefined'){
				if (vinculacion == 'S' && form[i].checked == true){			
    				if (vinc_elegida == 'S'){
    					if(cobFila.checked == false){ // S S
    						var cobEncontrada = cobFila.value.split('|');
	    					mensajeError = mensajeError +(" Para poder contratar  "+cob[6]+" debe elegir la garant\u00EDa "+ cobEncontrada[6]+". ");
    					}
    				}
    				if (vinc_elegida == 'N'){
    					if(cobFila.checked == true){ // S N
    						var cobEncontrada = cobFila.value.split('|');
	    					mensajeError = mensajeError +(" Para poder contratar "+cob[6]+" no puede elegir la garant\u00EDa "+ cobEncontrada[6]+". ");
    					}
    				}
    			}  				
    			
    			if (vinculacion == 'N' && form[i].checked == false){
    				if (vinc_elegida == 'S'){
    					if(cobFila.checked == false){ // N S
    						var cobEncontrada = cobFila.value.split('|');
	    					mensajeError = mensajeError +(" Si no elige "+cob[6]+" debe elegir la garant\u00EDa "+ cobEncontrada[6]+". ");
    					}
    				}
    				if (vinc_elegida == 'N'){
    					if(cobFila.checked == true){ // N N
    						var cobEncontrada = cobFila.value.split('|');
	    					mensajeError = mensajeError +(" Si no elige "+cob[6]+" no puede elegir la garant\u00EDa "+ cobEncontrada[6]+". ");
    					}
    				}
    			}
			}
			
		}
	}	
	comprobarChecksMarcados();
}

function dameCoberturaFila(fila,modulo, form){
	 for(h=0;h<form.elements.length; h++){
		 if(form[h].type == 'checkbox') {
			 var cob = form[h].value.split('|');
			 if (cob[2] == fila && cob[1] == modulo){
				 var encontrada = form[h];
				 h=1000;
				 return encontrada;
			 }
		 }
	 }
}

function comprobarChecksMarcados(){
	var tieneSelects = false;
    var datVarTemp = '';
    var checksMarcados = $('#checksMarcados').val();
    var listacheck = checksMarcados.split("#");
    
    var contador = 1;
	$('#contenedorCob :input[id^="riesg_cub_"]').each(function() {
		var datos = $(this).attr('value');
		var listaDatos = datos.split('|');
		
		var nomb= listaDatos[0];
		var modulo = listaDatos[1];
		var cptoPpal = listaDatos[3];
		var riesgo = listaDatos[5];		
		var nombrecheck = "riesg_cub_"+nomb;
		
		var isChecked = $(nombrecheck).attr('checked');
		
		if ($('#' + nombrecheck).is(':checked')) {
			$("select[id*='seleccionDatVar_']").each(function(){
				var datosDatVar = $(this).attr('value');	
				var listaDatos = datosDatVar.split('|');
				
				var modulo_datVar= listaDatos[1];
				var cptoPpal_datVar = listaDatos[3];
				var riesgo_datVar = listaDatos[5];
				
				if (modulo == modulo_datVar && cptoPpal == cptoPpal_datVar && riesgo == riesgo_datVar){
					$(this).attr('disabled','');
				}
				
			});	
			
		}else{
			$("select[id*='seleccionDatVar_']").each(function(){
				var datosDatVar = $(this).attr('value');	
				var listaDatos = datosDatVar.split('|');
				
				var modulo_datVar= listaDatos[1];
				var cptoPpal_datVar = listaDatos[3];
				var riesgo_datVar = listaDatos[5];
				
				if (modulo == modulo_datVar && cptoPpal == cptoPpal_datVar && riesgo == riesgo_datVar){
					$(this).val('');
					$(this).attr('disabled','disabled');
					
				}
				
			});	
			
		}
		
	});
	
}
function guardarValoresCobParcelas(){
	var datVarSeleccionados = "";
	
	$("select[id*='seleccionDatVar_']").each(function(){
		
		var datosDatVar = $(this).attr('value');	
		var listaDatos = datosDatVar.split('|');
		var datSelect = $(this).val();
		
		if (datSelect ==""){
			datVarSeleccionados = datVarSeleccionados + listaDatos[0] + '-' + 'N' + '-' + '#' + '+';
		}else{
			datVarSeleccionados = datVarSeleccionados + listaDatos[0] + '-' + 'S' + '-' + datSelect + '+';;
		}
		
		$('#datVarSeleccionados').val(datVarSeleccionados);		
	});	
} 