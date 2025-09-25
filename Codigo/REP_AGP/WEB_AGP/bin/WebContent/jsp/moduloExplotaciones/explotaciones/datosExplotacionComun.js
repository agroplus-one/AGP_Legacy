//Funciones comunes a datosExplotacion y datosExplotacionAnexo

function isValidEspecie(){
	var res=true;
	if( $('#listaCodEspecies').val()!=""){
		var lista=limpiaLista($('#listaCodEspecies')); 
		if(lista.length>0){
			res = contieneValorenLista($('#especie').val(), lista);
		}
	}
	
	return res;
} 

function isValidRegimen(){
	var res=true;
	if( $('#listaCodRegimenes').val()!=""){
		var lista=limpiaLista($('#listaCodRegimenes')); 
		if(lista.length>0){
			res = contieneValorenLista($('#regimen').val(), lista);
		}
	}
	
	return res;
} 

function isValidGrupoRaza(){
	var res=true;
	
	if( $('#listaCodGruposRazas').val()!=""){
		var lista=limpiaLista($('#listaCodGruposRazas')); 
		if(lista.length>0){
			res = contieneValorenLista($('#codgrupoRaza').val(), lista);
		}
	}
	
	return res;
} 

function isValidTipoAnimal(){
	var res=true;
	if( $('#listaCodTiposAnimal').val()!=""){
		var lista=limpiaLista($('#listaCodTiposAnimal')); 
		if(lista.length>0){
			res = contieneValorenLista($('#codtipoanimal').val(), lista);
		}
	}
	
	return res;
} 

function isValidTipoCapital(){
	var res=true;
	if( $('#listaCodTiposCapital').val()!=""){
		var lista=limpiaLista($('#listaCodTiposCapital')); 
		if(lista.length>0){
			res = contieneValorenLista($('#codtipocapital').val(), lista);
		}
	}
	
	return res;
} 

function isValidProvincia(){
	var res=true;
	if( $('#listaCodProvincias').val()!=""){
		var lista=limpiaLista($('#listaCodProvincias')); 
		if(lista.length>0){
			res = contieneValorenLista($('#provincia').val(), lista);
		}
	}
	return res;
} 

function isValidComarca(){
	var res=true;
	if( $('#listaCodComarcas').val()!=""){
		var lista=limpiaLista($('#listaCodComarcas')); 
		if(lista.length>0){
			res = contieneValorenLista($('#comarca').val(), lista);
		}
	}
	return res;
} 

function isValidTermino(){
	var res=true;
	if( $('#listaCodTerminos').val()!=""){
		var lista=limpiaLista($('#listaCodTerminos')); 
		if(lista.length>0){
			res = contieneValorenLista($('#termino').val(), lista);
		}
	}
	return res;
} 

function contieneValorenLista(valor, lista){
	var res=false;	
	var ind;
	 for(ind=0; ind<lista.length; ind++)
	    {
		     if (lista[ind] == valor){
		    	 res=true;
			       break; 
		     }
	    }
	 return res;
}

/**
 * quita los paréntesis de la cadena con la lista y devuelve un array
 */
function limpiaLista(strlista){
	var cadena1 = strlista.val().replace("(", "");
	var cadena2 = cadena1.replace(")", "");
	var lista = cadena2.split(",");
	return lista;
}

/**
 * Comprueba si el panel de Grupos de Raza está habilitado
 */
function isHabilitadoPanelGrupoRaza(){
	try {
		data = $('#divGrupoRaza').data();
		if (data["blockUI.isBlocked"] == 1) return false;
		else return true;
	} catch (ex) {
		return true;
	}
}

/**
 * Comprueba si hay que validar el campo Precio
 * @returns {Boolean}
 */
function validarPrecio() {
	return (!isAccionIrPanelGruposRaza && !isAccionCalcular);
}



/**
 * Oculta el popup de precios por módulo
 */
function cerrarPopUpPreciosPorModulo() {
	lupas.closeWindow('divPreciosPorModulo');
	//$('#datosIdentificativos').unblock();
}

/**
 * Mostrar el popup de precios por módulo
 */
function mostrarPopUpPreciosPorModulo() {
	if($('#pMin').val()==''){
		
		pMax = $('#precio').val()!=''?$('#precio').val():0;
		pMin = 0;
		$("#idtbody_popupPreciosModulo").html( "<tr>" +	
				   "<td class='literalborde' width='30%' align='center'>" + $('#codModuloPrecio').val()+ "</td>" + 
				   "<td class='literalborde' width='35%' align='center'>" + pMin + "</td>" + 
				   "<td class='literalborde' width='35%' align='center'>" + pMax + "</td>" +
				   "</tr>" );
		}
	lupas.openWindow('divPreciosPorModulo');
//	$('overlay').show();
//	$('#divPreciosPorModulo').fadeIn('normal');
}

/**
 * Carga en la pantalla los datos asociados al grupo de raza de explotación seleccionado
 */
function editarGrupoRazaExplotacion (duplicar, id, codgruporaza, nomgruporaza, codtipocapital, nomtipocapital, codtipoanimal, nomtipoanimal, numanimales, idinput){
	$('#codgrupoRaza').val(codgruporaza);
	$('#desGrupoRaza').val(nomgruporaza);
	$('#codtipocapital').val(codtipocapital);
	$('#desTipoCapital').val(nomtipocapital);
	$('#codtipoanimal').val(codtipoanimal);
	$('#desTipoAnimal').val(nomtipoanimal);
	var idinput = '#' + idinput;
	var precio = $(idinput).val();
	$('#precio').val(precio);
	$('#pMin').val('');
	
	$('#numanimales').val(numanimales);
	
	
	if(!duplicar){
		$('#gruporazaid').val(id);		
		$('#accion').val('editar');
	}else{
		$('#gruporazaOrginal').val(id);
		$('#accion').val('');
	}
	
	var idsearch = 'gr_' + id;
	var obj = document.getElementsByName(idsearch);
	if (obj.length == 0){
		$('input[id^="cod_cpto_"]').val('');
		$('input[id^="des_cpto_"]').val('');
	}
	var element = []; var concepto = []; var cod = []; var name = [];
	var str = '';
	for (var i = 0; i < obj.length; i++) { 
	      str = obj.item(i).value;
	      element[i] = str;
	};
	for (var i = 0; i < element.length; i++) { 
		var split = element[i].split('_');
		concepto[i] = split[0];
		cod[i] = split[1];
		name[i] = split[2];
	}
	$('input[id^="cod_cpto_"]').val('');
	$('input[id^="des_cpto_"]').val('');
	for (var i = 0; i < concepto.length; i++) {
		$('#cod_cpto_' + concepto[i]).val(cod[i]);
		$('#des_cpto_' + concepto[i]).val(name[i]);
	}
	
}

/**
 * Limpia los valores del formulario
 */
function limpiar(){
	$('#datosIdentificativos :input').val("");
	var backup = $("#contenedorCob").html();
	$('#divGrupoRaza :input').val("");
	$("#contenedorCob").html(backup);
}

/**
 * Pinta el valor y la descripción del dato variable asociado al código de concepto indicado
 * @param cpto
 * @param valor
 * @param descripcion
 */
function pintarValorDV(cpto, valor, descripcion, codgruporaza, codtipocapital, codtipoanimal) {
	var codgraza = $('#codgrupoRaza').val();
	var codtcapital = $('#codtipocapital').val();
	var codtanimal = $('#codtipoanimal').val();
	
	if(codgraza == codgruporaza && codtcapital == codtipocapital && codtanimal == codtipoanimal){
		$('#cod_cpto_'+cpto).val(valor);
		
		//Si el valor del datos variable tiene descripción se pinta en el input correspondiente
		if (descripcion != null && descripcion != ''){
			$('#des_cpto_'+cpto).val(descripcion);
		}
	}
}

function comprobarInputNumAnimales(codTipoCapital, nombreFormulario){
	var frm = document.getElementById(nombreFormulario);
	var lista = frm.listaTCapNoDepNumAni.value;
	if(lista!=null && lista!=""){
		var codConcepto = getCodConceptoGrupoNegocio(codTipoCapital, lista);
		if(codConcepto!=null){
			
			//var inputNumero = frm.numanimales;
			if(codConcepto==1097){
				frm.numanimales.readOnly=false;
				frm.validaNumAni.value="true";
			}
			if(codConcepto==1065 || codConcepto==1071 || codConcepto== 1072 || codConcepto== 1076){
				frm.numanimales.value=0;				
				frm.numanimales.readOnly=true;
				frm.validaNumAni.value="false";
			}
		}
	}	
}

function isValidNumAnimales(valor){
	var res=true;
	if( valor == null || valor.length == 0 || /^\s+$/.test(valor) ) {
		res= false;
	}else{
		if( isNaN(valor) ) {
			  res= false;
		}
	}
	return res;
}

function getCodConceptoGrupoNegocio(codTipoCapital, lista){
	//17-1097;18-1065;23-1065;
	var res=null;
	var listaTc = lista.split(";");
	for(var i=0; i< listaTc.length; i++) { 
		var patron=codTipoCapital + "-";
		if(listaTc[i].indexOf(patron) > -1){
			var listaCod =listaTc[i].split("-");
			res=listaCod[1];
			break;
		}
	}
	return res;
}

function limpiarAlertas(){
	 $('#panelAlertasValidacion').html("");
	 $('#panelAlertasValidacion').hide();
	 $('#panelAlertasCoberturas').html("");
	 $('#panelAlertasCoberturas').hide();
	 ocultaAsteriscos();
}


function montarCoberturasExplotacionNew(form,sinDesc){
// obj[0]=id, obj[1]=modulo, obj[2]=fila, obj[3]=CPM, obj[4]=desc CPM, obj[5]=RC, obj[6]=desc RC, obj[7]=vinculada(S/N), obj[8]=elegible(S/N), obj[9]=tipoCobertura
	var cobExistentes = "";    
   
    var tieneSelects = false;
    var datVarTemp = '';
    
    // PROCESAMOS SELECTS
    $("select[id*='seleccionDatVar']").each(function(){
    	comboValSeleccionado = $(this).attr('value');
    	tieneSelects = true;
    	//alert("comboValSeleccionadoOOOOO: "+comboValSeleccionado);
    	
    	var coberturas = comboValSeleccionado.split('#');
    	var cobs = coberturas[0].split('|');
    	var datVar = coberturas[1].split('|');
    	//alert(datVar[0]);
    	//alert("probandooooo para recoger: seleccionDatVar_"+cobs[1]+"_"+cobs[2]+"_"+cobs[3]+"_"+cobs[5]+"_"+datVar[0]);
    	if (document.getElementById('seleccionDatVar_'+cobs[1]+'_'+cobs[2]+'_'+cobs[3]+'_'+cobs[5]+'_'+datVar[0]) != null){
    		
	    	var myOpts = document.getElementById('seleccionDatVar_'+cobs[1]+'_'+cobs[2]+'_'+cobs[3]+'_'+cobs[5]+'_'+datVar[0]).options;
	 	    for(h=0;h<myOpts.length; h++){
	 	    	//alert("OPCION "+h+": " +myOpts[h].value); //=> Value of each option
	 	    	var datos = myOpts[h].value.split('#');
	 	    	var datosZero = datos[0].split('|');
		    	var datosUno  = datos[1].split('|');
	 	    	if (sinDesc){
		 	    	//alert("INIT0: "+datos[0]);
			    	//alert("INIT1: "+datosUno[2]);	
			    	datos[0] = datosZero[0]+"|"+datosZero[1]+"|"+datosZero[2]+"|"+datosZero[3]+"|P|"+datosZero[5]+"|P|"+datosZero[7]+"|"+datosZero[8]+"|"+datosZero[9];
			    	if (datosUno.length >3){//      [2] != 'undefined' && datosUno[2] != 'null'){
			    		datos[1] = datosUno[0]+"|P|"+datosUno[2]+"|P|"+datosUno[4];
			    	}else{
			    		datos[1] = datosUno[0]+"|P||P|";
			    	}
			    	//alert("FIN0: "+datos[0]);
			    	//alert("FIN1: "+datos[1]);
	 	    	}
		    	
	 	    	// buscar el chekbox asociado y ver si está elegido o no
	 	    	//alert("buscar este datos[0]: "+datos[0]);
	 	    	for(i=0;i<form.elements.length; i++){
		 	   	    if(form[i].type == 'checkbox') {
		 	   	    	//alert("checkbox: "+form[i].value);
		 	   	    	var checkBuscar = form[i].value.split('#');
		 	   	    	//alert("comparo con: "+checkBuscar);
		 	   	    	var checkZero = checkBuscar[0].split('|');
			 	   	    if (checkZero[1] == datosZero[1] && checkZero[2] == datosZero[2] && checkZero[3] == datosZero[3] && checkZero[5] == datosZero[5]){
			 	   	    	if (form[i].checked == true){
			 	   	    		//alert("checked");
				 	   	    	datos[0] = datos[0]+"|S#";   		
					    	}else{
					    		//alert("No checked");
					    		datos[0] = datos[0]+"|N#";
					    	}
			 	   	    	break;
			 	   	    }
		 	   	    }
	 	    	}

	 	    	datVarTemp = datos[1].split('|');
		    	//alert("CAMPO VACIO: "+datVarTemp[1]);
	 	    	if (myOpts[h].value == comboValSeleccionado){
	 	    		
	 	    		if (datVarTemp[1] !='X'){
	 	    			datos[1] = datos[1]+"|S";
	 	    		}
	 	    	}else{
	 	    		datos[1] = datos[1]+"|N";    	
	 	    	}
	 	    	//alert("datos 0y1: "+datos[0] + datos[1]);
	 	    	//alert("datVarTemp: "+datVarTemp);
	 	    	if (datVarTemp == ''){
	 	    		cobExistentes+= datos[0] + datos[1]+";";
	 	    		//alert ("datVarTemp: vacio. datos[0]: "+ datos[0] + "datos[1]: "+datos[1]);
	 	    	}else{
		 	    	if (datVarTemp[1] !='X' && datVarTemp !="undefined"){
		 	    		cobExistentes+= datos[0] + datos[1]+";";
		 	    		//alert ("datos[0]: "+ datos[0] + "datos[1]: "+datos[1]);
		 	    	}else{
		 	    		datVarTemp[1] = '';
		 	    	}
	 	    	}
	 	    	if (datos !="undefined"){
	 	    		//alert ("datos[0]: "+ datos[0] + "datos[1]: "+datos[1]);
	 	    	}
	 	    }
    	}
    	
    });
    
    // PROCESAMOS CHECKS
    for(i=0;i<form.elements.length; i++){
	    if(form[i].type == 'checkbox') {
	    	//alert("checkbox: "+form[i].value);
	    	var coberturas = form[i].value.split('#');
	    	
	    	if (sinDesc){
		    	//alert("INIT0: "+coberturas[0]);
		    	//alert("INIT1: "+coberturas[1]);
		    	var cobZero = coberturas[0].split('|');
		    	var cobUno  = coberturas[1].split('|');
		    	coberturas[0] = cobZero[0]+"|"+cobZero[1]+"|"+cobZero[2]+"|"+cobZero[3]+"|P|"+cobZero[5]+"|P|"+cobZero[7]+"|"+cobZero[8]+"|"+cobZero[9];
		    	coberturas[1] = cobUno[0]+"|P|"+cobUno[2]+"|"+cobUno[3];
		    	//alert("FIN0: "+coberturas[0]);
		    	//alert("FIN1: "+coberturas[1]);
	    	}
	    	
	    	
	    	if (form[i].checked == true){	    			    		
	    		coberturas[0] = coberturas[0]+"|S#";    		
	    	}else{
	    		coberturas[0] = coberturas[0]+"|N#";
	    	}
	    	cobExistentes+= coberturas[0] + coberturas[1]+";";
	    	//alert(coberturas[0]);
	    	//alert(coberturas[1]);
	    	//alert("cobExistentes temp: "+cobExistentes);
	    }
    }
    
   //alert("COB FINAL:"+cobExistentes);
   return cobExistentes;
}

function montarCoberturasExplotacion(form){
    // coberturas existentes
	var cobExistentes = "";    
    for(i=0;i<form.elements.length; i++){
	    if(form[i].type == 'checkbox') {
	    	if (form[i].checked == true){
	    		form[i].value = form[i].value+"|S";    		
	    	}else{
	    		form[i].value = form[i].value+"|N";
	    	}
	    	cobExistentes+= form[i].value +";";
	    }
    }
    //alert(cobExistentes);
    return cobExistentes;
}

/**
 * Habilita o deshabilita el combo de seleccionados dependiendo del checkeo de la cobertura asociada
 * @param obj
 */
function comprobarChecksCoberturas(nombreForm){
	//alert(nombreForm);
	//comprobar checks de coberturas por si hay que deshabilitar el select asociado
	var form = document.getElementById(nombreForm);	
	for(i=0;i<form.elements.length; i++){
	    if(form[i].type == 'checkbox') {
	    	var cob  = form[i].value.split('|');
	    	if (cob[8] == 'S'){
	    		grabaChekCob2(form[i]);
	    	}
	    }
	}
	//alert("fin comprobarChecksCoberturas");
}


//comprueba si el check de cobertura pasado como parametro tiene un select asociado para habilitarlo o no
function grabaChekCob2(obj){
	//alert("grabachekcob2"+obj.value);
	//# Valores posibles del array de coberturas#
	// obj[0]=id, obj[1]=modulo, obj[2]=fila, obj[3]=CPM, obj[4]=desc CPM, obj[5]=RC, obj[6]=desc RC, obj[7]=vinculada(S/N), obj[8]=elegible(S/N), obj[9]=tipoCobertura
	var cob  = obj.value.split('|');
	var mod  = cob[1];
	var fila = cob[2];
	var cpm  = cob[3];
	var rc   = cob[5];
	 // RECORREMOS LOS SELECTS
		$("select[id*='seleccionDatVar']").each(function(){
	    	comboValSeleccionado = $(this).attr('value');
	    	tieneSelects = true;
	    	//alert("comboValSeleccionado: "+comboValSeleccionado);
	    	var coberturas = comboValSeleccionado.split('#');
	    	var datVar = coberturas[0].split('|');
	    	//alert (" COMPARAR:" +mod +"|"+fila +"|"+cpm +"|"+rc +"#"+datVar[1] +"|"+datVar[2] +"|"+datVar[3] +"|"+datVar[5]);
	    	if (mod == datVar[1] && fila == datVar[2] && cpm == datVar[3] && rc == datVar[5]){
	    		//alert("encontrado dato variable..");
	    		if (obj.checked == true){
	    			$(this).attr('disabled','');
	    		}else{
	    			$(this).attr('disabled','disabled');
	    		}
			}
	    });
}

function dameCoberturaFila(fila,modulo, form){
	 for(h=0;h<form.elements.length; h++){
		 if(form[h].type == 'checkbox') {
			 var cob = form[h].value.split('|');
			 if (cob[2] == fila && cob[1] == modulo){
				 //alert("encontrada");
				 var encontrada = form[h];
				 h=1000;
				 return encontrada;
			 }
		 }
	 }
}

//# Valores posibles del array de coberturas#
// obj[0]=id, obj[1]=modulo, obj[2]=fila, obj[3]=CPM, obj[4]=desc CPM, obj[5]=RC, obj[6]=desc RC, obj[7]=vinculada(S/N), obj[8]=elegible(S/N), obj[9]=tipoCobertura
function grabaChekCob(form){ 
	  // CONTROL COBERTURAS
	  for(i=0;i<form.elements.length; i++){
		    var mensajeError = "";
		    if(form[i].type == 'checkbox') {
		    	var cob = form[i].value.split('|');
		    	
		    	//Cobertura elegible obligatoriamente
		    	// obj[8]=elegible(S), obj[9]=tipoCobertura(B)
		    	//alert(" revisando: "+cob[8]);
		    	if(form[i].checked==false){
		    		var cobElegible=cob[8];	    	var cobTipo=cob[9];
		    		if(cobElegible!=null && cobTipo!=null){
		    			if(cobElegible=='S' && cobTipo=='B'){
		    				mensajeError = mensajeError + " La cobertura  "+cob[6]+" es obligatoria ";
		    			}
		    		}
		    	}
		    	//alert(" revisando cobertura vinculación: "+cob[7]);
		    	if (cob[7] != '' && cob[7] !='null'){
		    		//cob[7] ="S.4.S.1-S.4.N.2";
		    		//alert(cob[7]);
		    		var vinc = cob[7].split('#');
		    		for(j=0;j<vinc.length; j++){
		    			var vinci = vinc[j].split('.');
		    			
		    			var vinculacion  = vinci[0];
		    			var fila         = vinci[1];
		    			var vinc_elegida = vinci[2];
		    			var modulo 		 = vinci[3];
		    			var columna 	 = vinci[4];
		    			var valor   	 = vinci[5];
		    			//alert("desglose vinc: "+vinci[0] +" " + vinci[1] +" "+vinci[2]+" "+vinci[3]+" "+vinci[4]+" "+vinci[5]);;
	    				var cobFila = dameCoberturaFila(fila,modulo, form);
	    				//alert (cobFila);			
	    				if (typeof cobFila != 'undefined'){
	    					//alert("cob encontrada: "+cobFila.value);
			    			if (vinculacion == 'S' && form[i].checked == true){			
			    				if (vinc_elegida == 'S'){
			    					//alert("-S S- la vinculada ha de estar marcada");
			    					if(cobFila.checked == false){ // S S
			    						var cobEncontrada = cobFila.value.split('|');
				    					mensajeError = mensajeError +(" Para poder contratar  "+cob[6]+" debe elegir la garantía "+ cobEncontrada[6]+". ");
			    					}
			    				}
			    				if (vinc_elegida == 'N'){
			    					//alert("-S N- la vinculada ha de estar desmarcada");
			    					if(cobFila.checked == true){ // S N
			    						var cobEncontrada = cobFila.value.split('|');
				    					mensajeError = mensajeError +(" Para poder contratar "+cob[6]+" no puede elegir la garantía "+ cobEncontrada[6]+". ");
			    					}
			    				}
			    			}  				
			    			
			    			if (vinculacion == 'N' && form[i].checked == false){
			    				if (vinc_elegida == 'S'){
			    					//alert("-N S- la vinculada ha de estar marcada");
			    					if(cobFila.checked == false){ // N S
			    						var cobEncontrada = cobFila.value.split('|');
				    					mensajeError = mensajeError +(" Si no elige "+cob[6]+" debe elegir la garantía "+ cobEncontrada[6]+". ");
			    					}
			    				}
			    				if (vinc_elegida == 'N'){
			    					//alert("-N N-la vinculada ha de estar desmarcada");
			    					if(cobFila.checked == true){ // N N
			    						var cobEncontrada = cobFila.value.split('|');
				    					mensajeError = mensajeError +(" Si no elige "+cob[6]+" no puede elegir la garantía "+ cobEncontrada[6]+". ");
			    					}
			    				}
			    			}
		    			}
		    			
		    		}
		    		//alert("fin vinc .length");
		    	}
		    		
		    }
		    if (mensajeError != ""){
		    	break;
		    }
 
	  }	    
	 
	  // revisamos las vinculaciones de datosvariables
	    $("select[id*='seleccionDatVar']").each(function(){
	    	var selecthabilitado = $(this).is(':enabled');
	    	if (selecthabilitado){
		    	comboValSeleccionado = $(this).attr('value');
		    	tieneSelects = true;
		    	//alert("comboValSeleccionado: "+comboValSeleccionado);
		    	var coberturas = comboValSeleccionado.split('#');
		    	var cobObj = coberturas[0].split('|');
		    	var datoss = coberturas[1].split('|');
		    	//alert(coberturas[1]);
		    	if (cobObj[7] != '' && cobObj[7] !='null'){
		    		//alert (" datvar 7:" +cobObj[7]);
		    		var vinc = cobObj[7].split('#');
		    		for(j=0;j<vinc.length; j++){
		    			var vinci = vinc[j].split('.');
		    			var vinculacion  = vinci[0];
		    			var fila         = vinci[1];
		    			var vinc_elegida = vinci[2];
		    			var modulo 		 = vinci[3];
		    			var columna 	 = vinci[4];
		    			var valor   	 = vinci[5];
		    			
		    			var vincEncontrada = false;
		    			//alert("desglose vinc: "+vinci[0] +" " + vinci[1] +" "+vinci[2]+" "+vinci[3]+" "+vinci[4]+" "+vinci[5]);
		    			//alert("BUSCANDO FILA: "+fila+ " mod: "+modulo+" col: "+columna );
						var cobFilaCol = dameCoberturaFilaCol(fila,modulo,columna);
						//alert(cobFilaCol);
						if (typeof cobFilaCol != 'undefined' && cobFilaCol != "undefined" && cobFilaCol != ""){
							var objBuscado = cobFilaCol[0].split('|');
							var datBuscado = cobFilaCol[1].split('|');
			    			if (typeof cobFilaCol[1] != 'undefined'){
			    				vincEncontrada = true;
								//alert("datBuscado[2]: "+datBuscado[2] + " comparando con valor: "+ valor);
								if (datBuscado[2] != valor){
									// mostramos los asteriscos
									//$('#campoObligatorio_'+modulo+'_'+fila+'_'+columna).show();
									$('#campoObligatorio_'+cobObj[1]+'_'+cobObj[2]+'_'+datoss[4]).show();
	
									// buscamos la descripción del valor a mostrar
									descValor = dameDescValor(fila,modulo,columna,valor);
									// mostramos mensaje
									mensajeError = mensajeError +(" * Si elige en la cobertura: \""+cobObj[4]+"\" con riesgo \""+ cobObj[6]+"\" la opción \""+datoss[3]+"\"en "+datoss[1]+"");								
									mensajeError = mensajeError +(" ha de elegir en la cobertura: \""+objBuscado[4]+"\" con riesgo \""+ objBuscado[6]+"\" la opción \""+descValor+"\" en "+datBuscado[1]+"<BR>");
									//alert(mensajeError);
								}
			    			}
						}
		    		}
		    		// vincEncontrada = false, mostrar mensaje error
		    	
		    		if (!vincEncontrada && (typeof datoss[3] != 'undefined')){
		    			//alert(datoss[3]);
		    			//alert(typeof datoss[3] != 'undefined');
		    			$('#campoObligatorio_'+cobObj[1]+'_'+cobObj[2]+'_'+datoss[4]).show();
		    			
		    			// recorremos todos los selects
		    			var descEncontrada = "";
		    			var cobAElegir = "";
		    			var riesgoAElegir = "";
		    			var garantiaAElegir = "";
		    			 $("select[id*='seleccionDatVar']").each(function(){
		    				seleccionDatVar = $(this).attr('value');
		    			   	//alert("seleccionDatVar: "+seleccionDatVar);
		    			   	var coberturas = seleccionDatVar.split('#');
		    			   	var obj = coberturas[0].split('|');
		    			   	var dat = coberturas[1].split('|');
		    			   	//alert (dat[1]);
		    			   	
		    			   	if (dat[1] =="X"){
		    			   		id = ($(this).attr('id'));
		    			   		$("#"+id+" option").each(function() {
		    				   		sDatVar = $(this).attr('value');
		    					   	//alert("sDatVar: "+sDatVar);
		    					   	var cobs = sDatVar.split('#');
		    					   	var objT = cobs[0].split('|');
		    					   	var datT = cobs[1].split('|');
		    					   	//buscamos la descripcion del valor
		    					   	//alert(datT[2] +" == " +valor+" "+objT[2]+" == "+fila);
		    				   		if (datT[2] == valor && objT[2] == fila){
		    				   			//alert("ENCONt.. sDatVar: "+sDatVar);
		    				   			//alert(" DESCRIPCION: " +datT[3]);
		    				   			descEncontrada = datT[3];
		    				   			//alert($(this).text());
				    				   	cobAElegir = objT[4];
				    				   	riesgoAElegir = objT[6];
				    				   	garantiaAElegir = datT[1];
		    				   		}   	  			
		    				   	});		    				   	
		    			   	}		    			   	
		    			 });
		    			// mostramos mensaje
							mensajeError = mensajeError +(" * Si elige en la cobertura: \""+cobObj[4]+"\" con riesgo \""+ cobObj[6]+"\" la opción \""+datoss[3]+"\"en "+datoss[1]+"");								
							mensajeError = mensajeError +(" ha de elegir en la cobertura: \""+cobAElegir+"\" con riesgo \""+ riesgoAElegir+"\" la opción \""+descEncontrada+"\" en "+garantiaAElegir+"<BR>");
		    		}
		    	}
	    	}
	    });
	  
	  
	  if (mensajeError != ""){
		  $('#panelAlertasCoberturas').html(mensajeError);
		  $('#panelAlertasCoberturas').show();
	  }else{
		  return true;
	  }
}

function dameCoberturaFilaCol(fila,modulo,columna){
	 var encontrada = "";
	 $("select[id*='seleccionDatVar']").each(function(){
		seleccionDatVar = $(this).attr('value');
    	//alert("seleccionDatVar: "+seleccionDatVar);
    	var coberturas = seleccionDatVar.split('#');
    	var obj = coberturas[0].split('|');
    	var dat = coberturas[1].split('|');
    	//alert("comparo: obj[1]"+obj[1]+ " con mod: "+modulo+" obj[2]: "+ obj[2]+ " con fila: "+fila + " dat[4]: "+dat[4]+ " con columna: "+columna);
    	if (obj[1] == modulo && obj[2] == fila && dat[4]== columna && dat[4]!="undefined" && columna !="undefined"){
    		//alert("DATO ENCONTRADO: "+coberturas[1]);
    		encontrada = coberturas;
    	}
	 });
	 return encontrada;
}



function dameDescValor(fila,modulo,columna,valor){
	 var valorEncontrado = "";
	 // recorremos todos los selects
	 $("select[id*='seleccionDatVar']").each(function(){
		seleccionDatVar = $(this).attr('value');
	   	//alert("seleccionDatVar: "+seleccionDatVar);
	   	var coberturas = seleccionDatVar.split('#');
	   	var obj = coberturas[0].split('|');
	   	var dat = coberturas[1].split('|');
	   	if (obj[1] == modulo && obj[2] == fila && dat[4]== columna && dat[4]!="undefined" && columna !="undefined"){	   		
	   		id = ($(this).attr('id'));
		   	// recorremos los options del select
		   	$("#"+id+" option").each(function() {
		   		sDatVar = $(this).attr('value');
			   	//alert("seleccionDatVar: "+seleccionDatVar);
			   	var cobs = sDatVar.split('#');
			   	var objT = cobs[0].split('|');
			   	var datT = cobs[1].split('|');
			   	//buscamos la descripcion del valor
		   		if (datT[2] == valor){
		   			//alert(" DESCRIPCION: " +datT[3]);
		   			valorEncontrado = datT[3];
		   			//alert($(this).text());
		   		}   	  			
		   	});
	   	}
	 });
	 return valorEncontrado;
}

function ocultaAsteriscos(){
	$("label[id*='campoObligatorio_']").each(function(){
		$(this).hide();		
	});
}



/**
 * Crea la tabla del popup de precios por módulo con los datos recibidos del cálculo y establece como precio de la explotación
 * el máximo precio devuelto
 * @param list
 */
function cargarPopUpPreciosPorModulo (list) {
	
	var tienePrecio = false;
	var pintarCoberturas = "";
	var cobDatoVar = false;
	var codConceptoAux;
	var pintarVacioNewSel = false;
	var modulo = "";
	var fila = "";
	var columna = "";
	var nuevoDatVar = false;

	
		for (i=0; i<list.length; i++) {
	
			pMax = '-'; pMin = '-';
			
			// Se establece como precio de la explotación el precio máximo del módulo actual
			if (i == 0) {
				// Contro de nulos sobre el precio máximo
				if (list[i].precioMax == 'null') {
					$('#precio').val('');
				}
				else {
					$('#precio').val(list[i].precioMax);
					pMax = list[i].precioMax;
					tienePrecio = true;
				}
				
				// Contro de nulos sobre el precio mínimo
				if (list[i].precioMin != 'null') {
					pMin = list[i].precioMin;
					$('#pMin').val(pMin);
					
				}
				$("#idtbody_popupPreciosModulo").html( "<tr>" +	
						   "<td class='literalborde' width='30%' align='center'>" + list[i].codmodulo + "</td>" + 
						   "<td class='literalborde' width='35%' align='center'>" + pMin + "</td>" + 
						   "<td class='literalborde' width='35%' align='center'>" + pMax + "</td>" +
						   "</tr>" );
				
			}
			
			//alert("list[i].cpm: "+list[i].cpm);
			if (!isNaN(list[i].cpm)) {// SON COBERTURAS			
				//alert("mod: "+list[i].codmodulo+" CPM: "+list[i].cpm+ " CPMdesc: "+list[i].cpmDescripcion+" RC: "+list[i].riesgoCubierto+" RC desc: "+list[i].rcDescripcion+" Elegible: "+list[i].elegible+" Elegida: "+" ## "+list[i].elegida+" dvCodCpto: "+list[i].dvCodConcepto +" dv_desc: " + list[i].dvDescripcion +" dv_valor: "+list[i].dvValor +" dv_valor_desc: "+list[i].dvValorDescripcion +" dv_Elegido: "+list[i].dvElegido);
				//alert("dvCodConcepto:" + list[i].dvCodConcepto);
				if (isNaN(list[i].dvCodConcepto)) {
					nuevoDatVar = false;
					//alert("No tiene codConcepto: cobertura normal");
					if (cobDatoVar){
						pintarCoberturas = pintarCoberturas +"</select><label class='campoObligatorio' align='center' id='campoObligatorio_"+modulo+"_"+fila+"_"+columna+"'>*</label></td></tr>";
						cobDatoVar = false;
					}
					pintarCoberturas = pintarCoberturas +("<tr><td class='literalbordeCob' align='center'>"+ list[i].cpmDescripcion + "&nbsp;</td>" + "<td class='literalbordeCob' align='center'>"+ list[i].rcDescripcion  + "&nbsp;</td>");
				
					if (list[i].elegible == 'S'){
						pintarVacioNewSel = false;
						pintarCoberturas = pintarCoberturas + ("<td class='literalbordeCob align='center' >" +
								"<input type='checkbox' name='cob' id='"+list[i].id+"'  class='dato' value='"+list[i].id+"|"+list[i].codmodulo+"|"+list[i].fila+"|"+list[i].cpm+"|"+list[i].cpmDescripcion+"|"+list[i].riesgoCubierto+"|"+list[i].rcDescripcion+"|"+list[i].vinculada+"|"+list[i].elegible+"|"+list[i].tipoCobertura+"#"+list[i].dvCodConcepto+"|"+list[i].dvDescripcion+"|"+list[i].dvValor+"|"+list[i].dvValorDescripcion+"|"+list[i].dvColumna+"' onclick='grabaChekCob2(this)'");																																																																							
						if (list[i].elegida == 'S'){							
							pintarCoberturas = pintarCoberturas + " checked='checked'";
						}
						pintarCoberturas = pintarCoberturas + ">&nbsp;</td>";							
						//pintarCoberturas = pintarCoberturas + "<input type='hidden' name='cobC' id='"+i+list[i].id+"'  class='dato' value='"+list[i].id+"jeje|"+list[i].codmodulo+"|"+list[i].fila+"|"+list[i].cpm+"|"+list[i].cpmDescripcion+"|"+list[i].riesgoCubierto+"|"+list[i].rcDescripcion+"|"+list[i].vinculada+"|"+list[i].elegible+"|"+list[i].tipoCobertura+"' />";	
					}else{ // elegible= N
						pintarCoberturas = pintarCoberturas + ("<td class='literalbordeCob align='center'>" +
								"<input type='checkbox' name='cob' style='display:none' id='"+list[i].id+"'  class='dato' value='"+list[i].id+"|"+list[i].codmodulo+"|"+list[i].fila+"|"+list[i].cpm+"|"+list[i].cpmDescripcion+"|"+list[i].riesgoCubierto+"|"+list[i].rcDescripcion+"|"+list[i].vinculada+"|"+list[i].elegible+"|"+list[i].tipoCobertura+"#"+list[i].dvCodConcepto+"|"+list[i].dvDescripcion+"|"+list[i].dvValor+"|"+list[i].dvValorDescripcion+"|"+list[i].dvColumna+"' onclick='grabaChekCob2(this)'");
						pintarCoberturas = pintarCoberturas + ">&nbsp;</td>";
						pintarVacio = true;
						pintarVacioNewSel = true;
					}
				}else{
					//alert("SI tiene codConcepto: Dato Variable");
					//guardamos el codconcepto para ver si cambia
					//alert(list[i].dvCodConcepto);
					//alert(cobDatoVar);
					if (!cobDatoVar || list[i].dvCodConcepto != codConceptoAux){
						if (nuevoDatVar){
							pintarCoberturas = pintarCoberturas +"</select><label class='campoObligatorio' align='center' id='campoObligatorio_"+modulo+"_"+fila+"_"+columna+"'>*</label></td>";
							nuevoDatVar = false;
						}

						pintarCoberturas = pintarCoberturas + 
						"<td class='literalbordeCob'  align='left'>"+list[i].dvDescripcion+
						"&nbsp<br><select name='selDV_"+list[i].dvCodConcepto+"'  class='datoCob' style='width:70' id='seleccionDatVar_"+list[i].codmodulo+"_"+list[i].fila+"_"+list[i].cpm+"_"+list[i].riesgoCubierto+"_"+list[i].dvCodConcepto+"'>";					
						cobDatoVar  = true;
						pintarVacio = true;
						nuevoDatVar = true;
						modulo 		= list[i].codmodulo;
						fila 		= list[i].fila;
						columna 	= list[i].dvColumna;
						//alert("seleccionDatVar_"+list[i].codmodulo+"_"+list[i].fila+"_"+list[i].cpm+"_"+list[i].riesgoCubierto+"_"+list[i].dvCodConcepto);
					}
					//alert("Elegida: "+list[i].elegida+" dvCodCpto: "+list[i].dvCodConcepto +" dv_desc: " + list[i].dvDescripcion +" dv_valor: "+list[i].dvValor +" dv_valor_desc: "+list[i].dvValorDescripcion +" dv_Elegido: "+list[i].dvElegido);
					if (pintarVacio && pintarVacioNewSel){
						pintarCoberturas = pintarCoberturas + "<option value='"+list[i].id+"|"+list[i].codmodulo+"|"+list[i].fila+"|"+list[i].cpm+"|"+list[i].cpmDescripcion+"|"+list[i].riesgoCubierto+"|"+list[i].rcDescripcion+"|"+list[i].vinculada+"|"+list[i].elegible+"|"+list[i].tipoCobertura+"#"+list[i].dvCodConcepto+"|X'></option>";
						pintarVacio = false;
					}
					pintarCoberturas = pintarCoberturas + "<option value='"+list[i].id+"|"+list[i].codmodulo+"|"+list[i].fila+"|"+list[i].cpm+"|"+list[i].cpmDescripcion+"|"+list[i].riesgoCubierto+"|"+list[i].rcDescripcion+"|"+list[i].vinculada+"|"+list[i].elegible+"|"+list[i].tipoCobertura+"#"+list[i].dvCodConcepto+"|"+list[i].dvDescripcion+"|"+list[i].dvValor+"|"+list[i].dvValorDescripcion+"|"+list[i].dvColumna+"'";
					nuevoDatVar = true;
					if (list[i].dvElegido == 'S'){	
						pintarCoberturas = pintarCoberturas + " selected='selected' >"+list[i].dvValorDescripcion+"</option>";
					}else{
						pintarCoberturas = pintarCoberturas + ">"+list[i].dvValorDescripcion+"</option>";
					}	
					codConceptoAux = list[i].dvCodConcepto;
				}
			}
		} // FIN list

		//alert(nuevoDatVar);
		if (nuevoDatVar == true){
			pintarCoberturas = pintarCoberturas +"</select><label class='campoObligatorio' align='center' id='campoObligatorio_"+modulo+"_"+fila+"_"+columna+"'>*</label></td>";
			nuevoDatVar = false;
		}

	
	
	if (pintarCoberturas == ""){
		$("#contenedorCob").html("");
		$("#contenedorCoberturas").hide();
	}else{
		$("#contenedorCoberturas").show();	
		$("#contenedorCob").html(pintarCoberturas);
	}
	limpiarAlertas();
	
	//alert("tienePrecio: "+tienePrecio);
	return tienePrecio;
}

function alertaPreciosDiferentes(data){
	list = data.match(/,+|"[^"]+"/g);
	for(i = 0; i < list.length; i++){
		str = list[i].replace(/['"]+/g, '');
		if(str === 'errorPrecio'){
			alert(list[i + 1].replace(/['"]+/g, ''));
			break;
		}
	}
}