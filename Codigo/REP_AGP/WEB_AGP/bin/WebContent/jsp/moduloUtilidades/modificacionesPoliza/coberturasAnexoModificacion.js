function showdata(id) { 
	ID1 = document.getElementById(id + ".1"); 
	ID2 = document.getElementById(id + ".2"); 
	ID3 = document.getElementById(id); 
 
	if (ID1.style.display == '') { 
		ID1.style.display = 'none'; 
	} else { 
		ID1.style.display = ''; 
	} 
	if (ID2.style.display == '') { 
		ID2.style.display = 'none'; 
	} else { 
		ID2.style.display = ''; 
	} 
	if (ID3.style.display == '') { 
		ID3.style.display = 'none'; 
	} else { 
		ID3.style.display = ''; 
	} 
} 
 
function showCobertura(id) { 
	ID1 = document.getElementById(id + ".1"); 
	ID2 = document.getElementById(id + ".2"); 
	ID3 = document.getElementById(id); 
	ID1.style.display = 'none'; 
	ID2.style.display = ''; 
	ID3.style.display = ''; 
} 
 
function volver() { 
	$("#method").val("doVolver");
	$("#frmcontinuar").submit();
} 

function cambiarValorCombo(combo){ 
	
	var valorCombo = $('#' + combo).val();
	if(valorCombo == 1){
		$('#' + combo).val(0);
		$('#coberturasModificadas').val('true');
	}else{
		$('#' + combo).val(1);
		$('#coberturasModificadas').val('true');
	}
}

function cambiarComboAJAX(combo, valorSele){
	
	$.ajax({ 
		url : "coberturasModificacionPoliza.html",  //tengo que buscar la url de ModuloManager
		data : "method=getValElegibles&idpoliza=" + $("#idPoliza").val() + "&codmodulo=" + $("#codModuloAnexo").val() + "&idAnexo=" + $("#idAnexo").val() + "&idComboSeleccionado=" + combo + "&valorSele=" + valorSele + "&listaIdCeldaConVin=" + $("#listaIdCeldaConVin").val(), //me creo un metodo en ModuloManager
		async : true, 
		dataType : "json", 
		success : function(datos) { 
			
			
			var arrayIdsSinRepetir = [];
			
			if(null != datos && datos.listaVinculacionesComboSeleccionado != ""){
				
				
				for(var x = 0; x < datos.listaVinculacionesComboSeleccionado.length; x++){ 
					
					var cadenaIds = datos.listaVinculacionesComboSeleccionado[x];
					var arregloIdComYDescripYVal = cadenaIds.split(":");
				
	
				    var idCombo = arregloIdComYDescripYVal[0];
				    var descripcion = arregloIdComYDescripYVal[4];
				    var valor = arregloIdComYDescripYVal[3];
				    
				    var comboSelect = document.getElementById(idCombo);
				    
				    if (arrayIdsSinRepetir.length < 1){
				    	
				    	arrayIdsSinRepetir.push(idCombo);
				    	comboSelect.innerHTML = '';
				    	
				    	//var option = document.createElement("option");
					    //option.value = "0";
					    //comboSelect.add(option);
					    
				    }else{
				    	
				    	var isRepetido = repetido(arrayIdsSinRepetir, idCombo);
					    if (isRepetido == false) {
					    		
					    	comboSelect.innerHTML = '';
					    	
					    	//var option = document.createElement("option");
						    //option.value = "0";
						    //comboSelect.add(option);
					    	
					    	arrayIdsSinRepetir.push(idCombo);
				        }
				    }
				    
				    		
				    var option = document.createElement("option");
				    option.text = descripcion;
				    option.value = valor;
				    comboSelect.add(option);
				    
				    //SOLO SE DEBEN CARGAR LOS PARIENTES DIRECTOS Y LOS INDIRECTOS EN CASO DE QUE EL COMBO DEL PADRE TENGA SOLO UN ELEMENTO
				    var checkParienteDir = checkParienteDirecto(idCombo, datos.listaVinculacionesDirectasComboSeleccionado);
				    
				    if(checkParienteDir == "false"){
				    	
				    	var cantidadValPad = cantidadValoresPadre(datos.listaVinculacionesComboSeleccionado, cadenaIds); 
					    
				    	if(cantidadValPad > 1){
				    		
				    		comboSelect.innerHTML = '';
					    	
					    	var option = document.createElement("option");
						    option.value = "0";
						    comboSelect.add(option);
				    		
				    	}
				    	
				    }	    
				    
				}
				
				if(null != arrayIdsSinRepetir && arrayIdsSinRepetir != ""){
					
					for(var a = 0; a < arrayIdsSinRepetir.length; a++){ 
						
						if ($('#'+ arrayIdsSinRepetir[a] + ' option').length > 1){
							
							var comboActual = document.getElementById(arrayIdsSinRepetir[a]);
							
							var option = document.createElement("option");
						    option.value = "0";
						    option.selected = "selected";
						    comboActual.add(option);
						}
					}
				}
				
				
				
			}
			mostrarOcultarPanelAlertasValidacion("combo");
		}, 
		error: function(objeto, quepaso, otroobj){
			alert("Error: " + quepaso);
		},
		beforeSend : function() { 
		}, 
		type : "POST" 
	});
}

function checkParienteDirecto(idCombo, cadenaIdsDirectas){
	
	var resultado = "false";
	for(var check = 0; check < cadenaIdsDirectas.length; check++){ 
		
		var arregloIdComYDescripYValDir = cadenaIdsDirectas[check].split(":");

	    var idComboDir = arregloIdComYDescripYValDir[0];
	    
	    if(idCombo == idComboDir){
	    	resultado = "true";
	    	break;
	    }
	}    
	return resultado;
}

function cantidadValoresPadre(arrayVal, idCom){
	
	var valPad = 0;
	
	var idComboDescom = idCom.split(":");
	
	var idComboDescomPadreFila = idComboDescom[5];
	var idComboDescomPadreColum = idComboDescom[6];
	var idComboDescomPadreValor = idComboDescom[7];
	
	
	for(var check = 0; check < arrayVal.length; check++){ 
		
		var arregloIdComYDescripYValDir = arrayVal[check].split(":");

	    var idComboDir = arregloIdComYDescripYValDir[0];
	    var idComboDirUbicacionFila = arregloIdComYDescripYValDir[1];
	    var idComboDirUbicacionColumna = arregloIdComYDescripYValDir[2];
	    
	    if(idComboDescomPadreFila == idComboDirUbicacionFila && idComboDescomPadreColum == idComboDirUbicacionColumna){
	    	valPad = valPad + 1;
	    	
	    }
	}    
	
	return valPad;
}

function repetido(array, valor) {
    var repetido = false;
    for (var i = 0; i < array.length; i++) {
        if (array[i] == valor) {
            repetido = true;
            break;
        } else if (array[i] != valor) {

            repetido = false;
        }
    }
    return repetido;
}

function mostrarOcultarPanelAlertasValidacion(valor){
	
	var hayCobGuardadas = $('#amTieneCoberturasGuardadas').val();
	var codModuloAnexoSelec = $('#codModuloAnexo').val();
	var codModuloAnexoGuard = $('#codModuloAnexoGuardado').val();
	
	if (hayCobGuardadas == 'true' && codModuloAnexoSelec != codModuloAnexoGuard ){
		
		if($('#panelAlertasValidacion').is(":visible")){
			$('#coberturasModificadas').val('true');
		}
		else{
			mostrarPanelCambioCoberturasAnexo();		
		}
	}
	
	if (hayCobGuardadas == 'true' && codModuloAnexoSelec == codModuloAnexoGuard ){
		//Quitamos el panelAlertasValidacion
		//$('#panelAlertasValidacion').hide(); 
		
		//hay que comprobar ahora que no se hayan cambiado los combos
		if (valor == "combo"){
			mostrarPanelCambioCoberturasAnexo();
		}
	}
	if(hayCobGuardadas == "false"){
		$('#coberturasModificadas').val('true');
	}
}

function cargaModulo(idpoliza, codmodulo, idAnexo) { 

	if (codmodulo != "") { 
		
		$.ajax({ 
			url : "coberturasModificacionPoliza.html", 
			data : "method=getModulo&idpoliza=" + idpoliza + "&codmodulo=" 
					+ codmodulo + "&idAnexo=" + idAnexo, 
			async : true, 
			dataType : "json", 
			success : function(datos) { 
				
					$('#coberturaAnexo').html(datos.tablaDatos.tabla); 
					asginarEventosCombos(datos.datosVinc); 
					$('#listaIdCeldaConVin').val(datos.tablaDatos.listaIdCeldaConVin);
					$('#lstIdCeldasEleg').val(datos.tablaDatos.lstIdCeldasEleg);
					/*** 01/02/2021 DNF PET.63485.FIII seteo la lista de checks de RC elegible*/
					$('#lstIdChecksEleg').val(datos.tablaDatos.lstIdChecksEleg);
					/*** fin 01/02/2021 DNF PET.63485.FIII */
//					ponerCombosSinValor();
					
					var listaIdCeldaConVin = $('#listaIdCeldaConVin').val();
					if(listaIdCeldaConVin != "" && listaIdCeldaConVin != null){
					    var arregloIdCeldaConVin = listaIdCeldaConVin.split(",");
		
					    for(var i = 0; i < arregloIdCeldaConVin.length; i++){
					    	
					    	var id = arregloIdCeldaConVin[i];
					    	
					    	if (document.getElementById(id) != null){
//					    		document.getElementById(id).disabled = true;
					    	//	document.getElementById(id).style.width = "49px";
					    	}
					    }
					}
				
			}, 
			beforeSend : function() { 
				$("#ajaxLoading_coberturaAnexo").show(); 
			}, 
			complete : function() { 
				var modoLectura = '${modoLectura}'; 
				if ($('#modulo').val() == "" || $('#modulo').val() == null) { 
					if (codmodulo == '${modPol}') { 
						seleccionarCoberturasPolizaElegidas(); 
					} else {
						
						seleccionarCoberturasElegidas(); 
						ponerCombosSinValor();
						mostrarValidoValorFilaVincCorresp();		
					} 
				} else { 
					// Si el modulo elegido para cargar y el de la poliza son el 
					// mismo, se cargan las coberturas de la poliza 
					// antes que la del anexo 
					if ($('#modulo').val() == '${modPol}') { 
						seleccionarCoberturasPolizaElegidas(); 
					} 
					seleccionarCoberturasElegidas(); 
					ponerCombosSinValor();
					mostrarValidoValorFilaVincCorresp();
				} 
				// DAA 05/07/2013 
				$("#ajaxLoading_coberturaAnexo").hide(); 
				showdata('coberturaAnexo'); 
 			
				mostrarOcultarPanelAlertasValidacion();
				
			}, 
			type : "POST" 
		});
	} else { 
		$('#coberturaAnexo').html(""); 
		$('#cabeceraAnex').html(""); 
	} 
} 

function ponerCombosSinValor(){

	var ids = $('#lstIdCeldasEleg').val();
	var hayCobGuardadas = $('#amTieneCoberturasGuardadas').val();
	
	var codModuloAnexoSelec = $('#codModuloAnexo').val();
	var codModuloAnexoGuard = $('#codModuloAnexoGuardado').val();
	
	if(null != ids && ids != ""){
	
		if(hayCobGuardadas == 'false' || (hayCobGuardadas == 'true' && codModuloAnexoSelec != codModuloAnexoGuard )){
			
			var cadena = ids;
		    separador = ",";
		    var arregloIdCoberturasDisponibles = cadena.split(separador);
		    var cantidad = arregloIdCoberturasDisponibles.length;
	
		    for(var i = 0; i < cantidad; i++){
		    	
		    	var id = arregloIdCoberturasDisponibles[i];
		    	
		    	var indexCategoriaSelected = $('#' + id).val();
		    		
		    	var x = document.getElementById(id);
		    	
		    	var option = document.createElement("option");
		    	option.value = 0;
		    	option.selected = "true";
		    	//option.disabled = "true";
		    	//option.hidden = "true";
		    	x.add(option);
		    }
			
		}
	}
}

function mostrarValidoValorFilaVincCorresp(){

	var ids = $('#listaIdCeldaConVin').val();
	if(ids != "" && ids != null){
		var cadena = ids;
	    separador = ",";
	    var arregloIdComYDescripYVal = cadena.split(separador);
	    var cantidad = arregloIdComYDescripYVal.length;
	
	    for(var i = 0; i < cantidad; i++){
	    	
	    	var id = arregloIdComYDescripYVal[i];
	    	
	    	var indexCategoriaSelected = $('#' + id).val();
	    	var valor = $('#' + id + ' option:selected').text();
	        
	    	var html = $('#' + id).html("<option value='" +indexCategoriaSelected+ "'>"+ valor +"</option>"); 
	    }
	}		
}

function cargardatosmoduloanexo(idpoliza, variable, idAnexo) { 

	$('#codModuloAnexo').val(variable);
	
	ID1 = document.getElementById("coberturaAnexo.1"); //mostrar
	ID2 = document.getElementById("coberturaAnexo.2"); //ocultar
	ID3 = document.getElementById("coberturaAnexo"); //tabla
	
	if (ID1.style.display == 'none') {  //mostrar coberturas esta activo
		ID1.style.display = ''; 
		ID2.style.display = 'none';
		ID3.style.display = 'none';
	} 
	
	cargaModulo(idpoliza, variable, idAnexo)
} 
/* Pet. 63485-Fase II ** MODIF TAM (15.09.2020) ** Inicio */ 

function comprobarValoresCombos(){
	
	var ids = $('#lstIdCeldasEleg').val();
	if(ids != "" && ids != null){	
	    var arregloIdCombosEleg = ids.split(",");
	    for(var i = 0; i < arregloIdCombosEleg.length ; i++){
		    	
	    	var id = arregloIdCombosEleg[i];
	    	var indexCategoriaSelected = $('#' + id).val();
		    		
	    	if(indexCategoriaSelected == "0"){
	    		$('#panelAlertasValidacion').html("Debe seleccionar las coberturas Elegibles"); 
				$('#panelAlertasValidacion').show();
		    	return false;
	    	}
	    }
	}
    return true;
}

function grabar() {
	
	var frm = document.getElementById('mainform');
	
	if ($("#mainform").valid()) {
	
		frm.method.value = 'doContinua';
	    $("input:disabled").attr("disabled","");
	    $("select:disabled").attr("disabled","");
	    $.blockUI.defaults.message = '<h4> Procesando coberturas.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	       //DAA 05/07/2013
	    $("#modulo").val("${modPol}");
	    frm.submit();
	}    
}

function grabarYContinuar(){
	
	var idPoliza              = $("#idPoliza").val();
	var modoLectura           = $("#modoLectura").val();
	var coberturasModificadas = $("#coberturasModificadas").val();
	var codModuloAnexo        = $("#codModuloAnexo").val();
	var idAnexo               = $("#idAnexo").val();
	var idCupon               = $("#idCupon").val();
	var lstIdCeldasEleg       = $("#lstIdCeldasEleg").val();
	
	var valorDatos = "&idPoliza=" + idPoliza + "&modoLectura=" + modoLectura + "&coberturasModificadas=" + coberturasModificadas + "&codModuloAnexo=" + codModuloAnexo+ "&idAnexo=" +idAnexo ;
	
	if(lstIdCeldasEleg != "" && lstIdCeldasEleg != null){
	    var arreglolstIdCeldasEleg = lstIdCeldasEleg.split(",");

	    for(var i = 0; i < arreglolstIdCeldasEleg.length; i++){
	    	
	    	var id = arreglolstIdCeldasEleg[i];
	    	var valor = $('#' + id).val();
	    	valorDatos = valorDatos + "&" + id + "=" + valor;
	    	
	    }
	}
	
	/*** 01/02/2021 DNF PET.63485.FIII anadimos tambien la lista de RC elegibles para que queden grabados*/
	var lstIdChecksEleg       = $("#lstIdChecksEleg").val();
	if(lstIdChecksEleg != "" && lstIdChecksEleg != null){
	    var arreglolstIdChecksEleg = lstIdChecksEleg.split(",");

	    for(var i = 0; i < arreglolstIdChecksEleg.length; i++){
	    	
	    	var idCE = arreglolstIdChecksEleg[i];
	    	var valorCE = $('#' + idCE).val();
	    	valorDatos = valorDatos + "&" + idCE + "=" + valorCE;
	    	
	    }
	}
	/*** 01/02/2021 DNF PET.63485.FIII */
	
	if ($("#mainform").valid()) {
		
		//if(null != coberturasModificadas && coberturasModificadas != ""){
		
			$.ajax({ 
				url : "coberturasModificacionPoliza.html", 
				data : "method=grabacionCoberturasAM" + valorDatos,
				async : true, 
				dataType : "json", 
				success : function(datos) { 
					
					if(null != datos){
						if(null != datos.isGrabacionCorrecta && datos.isGrabacionCorrecta == "true"){
							//validacion y calculo del anexo para el metodo enviar de parcelasAnexoModificacionSW.js
							muestraCapaEspera ("Validando el A.M");
							validacionesPreviasEnvioAjax(datos.idCupon, idAnexo);
						}
						else{
							$('#panelAlertasValidacion').html("Error: " + datos.alerta); 
							$('#panelAlertasValidacion').show();
						}
					}
				}, 
				beforeSend : function() { 
					muestraCapaEspera ("Procesando coberturas.");
				}, 
				complete : function() { 
				}, 
				type : "POST" 
			});
		//}else{
		//	$('#panelAlertasValidacion').html("El Anexo de modificaci&oacute;n ya tiene los datos grabados"); 
		//	$('#panelAlertasValidacion').show();
		//}
	}	
}
 
/******************************************************************************* 
 * Asginamos a todo los combos un evento 
 ******************************************************************************/ 
function asginarEventosCombos(vinculados) { 
	$("select").each(function() { 
		$($(this)).bind("change", function(e) { 
			combosVinculados($(this).attr('id'), vinculados); 
		}); 
	}); 
} 
 
function combosVinculados(id, vinculados) { 
	var idVinc = "vinculados." + id; 
	var valor = $("#" + id).val(); 
	var texto = $("#" + id).find("option[value=" + valor + "]").text(); 
	buscarCombo(idVinc, texto); 
} 
 
function buscarCombo(id, valor) { 
	$("select").each(function() { 
		if ($(this).attr('id') == id) { 
			var aux = $("#" + id + " option:contains('" + valor + "')").val(); 
			$(this).val(aux); 
		} 
	}); 
} 
// DAA 05/07/2013 
// creamos un array con las coberturas elegibles de la poliza y del anexo 
var arrayCobElePoliza = new Array(); 
var arrayCobEleAnexo = new Array(); 
 
function montarArrayCoberturasElegibles(cobElegiblesPoliza, cobElegiblesAnexo) { 
	if (cobElegiblesPoliza != '') { 
		arrayCobElePoliza = cobElegiblesPoliza.split(";"); 
	} 
	if (cobElegiblesAnexo != '') { 
		arrayCobEleAnexo = cobElegiblesAnexo.split(";"); 
	} 
} 

function mostrarPanelCambioCoberturasAnexo() {
	if ($('#modoLectura').val() != 'true') {
		$('#panelAlertasValidacion').html("Coberturas cambiadas. Se enviar&aacute;n en el Anexo de Modificaci&oacute;n"); 
		$('#panelAlertasValidacion').show(); 
		$('#coberturasModificadas').val('true'); 
	}		
}
 
// DAA 05/07/2013 
function controlCambioCoberturas() { 
	
		for ( var i = 0; i < arrayCobElePoliza.length; i++) { 
			if (arrayCobElePoliza[i] != arrayCobEleAnexo[i]) { 
				mostrarPanelCambioCoberturasAnexo(); 
				break; 
			} else { 
				$('#panelAlertasValidacion').hide(); 
			} 
		} 
} 
 
// DAA 05/07/2013 
function actualizaArrayCobEleAnexo(combo) { 
	var name = combo.slice(12); 
	for ( var i = 0; i < arrayCobEleAnexo.length; i++) { 
		if (arrayCobEleAnexo[i].indexOf(name) == 0) { 
			arrayCobEleAnexo[i] = name + "#" + $("#" + combo).val(); 
		} 
	} 
} 
 
function actualizaArrayCobEleAnexoCheck(idCheck, checked) { 
	var name = idCheck.slice(11); 
	for ( var i = 0; i < arrayCobEleAnexo.length; i++) { 
		if (arrayCobEleAnexo[i].indexOf(name) == 0 && checked) { 
			arrayCobEleAnexo[i] = name + "#-1"; 
		} else if (arrayCobEleAnexo[i].indexOf(name) == 0 && !checked) { 
			arrayCobEleAnexo[i] = name + "#-2"; 
		} 
	} 
}
var listaAnexosSelec = "";
function controlCamCoberturas(){
	
	
	seleccionarCoberturasElegidas();
	
	if(listaAnexosSelec == ""){
		$('#panelAlertasValidacion').hide(); 
	}else{
		var arreglolistaAnexosSelec = listaAnexosSelec.split(",");
	
	    for(var x = 0; x < arreglolistaAnexosSelec.length; x++){
	    	
	    	arrayCobEleAnexo[x] = arreglolistaAnexosSelec[x];
	    }
		
	    arrayCobElePoliza.sort();
	    arrayCobEleAnexo.sort();
	    
		for ( var i = 0; i < arrayCobElePoliza.length; i++) { 
			if (arrayCobElePoliza[i] != arrayCobEleAnexo[i]) { 
				mostrarPanelCambioCoberturasAnexo(); 
				break; 
			} else { 
				$('#panelAlertasValidacion').hide(); 
			} 
		}
	
	}
}