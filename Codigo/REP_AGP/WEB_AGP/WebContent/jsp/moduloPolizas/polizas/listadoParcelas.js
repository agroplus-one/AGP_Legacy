function onClickInCheck2(idCheck){

    if(idCheck)
	{
		var __is_checked = false;
		var __aux = idCheck.split("_");
		var __ids = $('#idsRowsChecked').val();
		
		
 
        if (document.getElementById(idCheck).checked == true){
             addCheck2(__ids, __aux[1]); 
        }else{
             subtractCheck2(__ids, __aux[1]);
             $('#marcarTodosChecks').val("no");
             $('#selTodos').attr('checked',false);
        }
        
		$('#sel').text(numero_check_seleccionados2());
	}
	
}  
		  
/**
* @params: ids --> contiene los ids de los registros seleccionados de todas las paginas
* Selecciona los checks que vienen en el param. ids y que estan en la pagina actual
* ejemplo param. ids: 3;5;6;23;10
*/
function check_checks(ids){
	
	if(ids != null)
	{
		
		 var array_ids = ids.split(';');
		 for(var i = 0; i < array_ids.length; i++)
		 {
		      var idCheck = "checkParcela_" + array_ids[i]; 
		      $('#' + idCheck).attr('checked',true); 
		 } 
	}
}
		  
function addCheck2(ids, check){

	
	if(ids != null){
		
		 ids = ids + ";" + check;
	}
		      
	$('#idsRowsChecked').val(ids);

}
		  
function subtractCheck2(ids, check){
	var newList = "";
	if(ids != null){
		var array_ids = ids.split(';');
		for(var i = 0; i < array_ids.length; i++){
		     if(array_ids[i] != check && array_ids[i] != "" && array_ids[i] != null && array_ids[i] != undefined){
		         newList = newList + array_ids[i] + ";";
		     }
		} 
	}	      
	$('#idsRowsChecked').val(newList);
}
		  
function numero_check_seleccionados2(){
		return num_checks_checked_in_list($('#idsRowsChecked').val());	    
}     

//DAA 02/07/2012 selectAllChecks, seleccionar_checks2, desseleccionar_checks2
function selectAllChecks(elem){
	
	if(elem.checked){
	    seleccionar_checks2();
	}    
	else{
		desseleccionar_checks2();
	}		
}
		        
function seleccionar_checks2(){                                         
	var __ids =  ""; 
	
	var numParcelasCheck = 0;
	var __arrayParcelas = ($('#parcelasString').val()).split(";");
	$('#marcarTodosChecks').val("si");
	
	$('superficieTotalComp').val("1");
	
	
	for(var i = 0; i < __arrayParcelas.length; i++){
		var __arrayParcela =  (__arrayParcelas[i]).split("_");
        if(__arrayParcela[1] == 'P'){
        	var __idCheck = "checkParcela_" + __arrayParcela[0]; 
			$('#' + __idCheck).attr('checked',true); // checked
			__ids = __ids + ";" + __arrayParcela[0]; 
			numParcelasCheck++;
        }		
	}
	$('#idsRowsChecked').val(__ids);
	$('#sel').text(numero_check_seleccionados2());
}

function desseleccionar_checks2(){

	var __arrayParcelas = ($('#parcelasString').val()).split(";");
	$('#marcarTodosChecks').val("no");
	
	for(var i = 0; i < __arrayParcelas.length; i++){
		var __arrayParcela =  (__arrayParcelas[i]).split("_");
        if(__arrayParcela[1] == 'P'){
        	var __idCheck = "checkParcela_" + __arrayParcela[0]; 
			$('#' + __idCheck).attr('checked',false); // no checked

        }		
	}
	$('#idsRowsChecked').val('');

	$('#sel').text(0);
}      
		         
// formato list: 2;3;5;3;n
function isInList(list, item){
	var result =  false;
	var array_ids = list.split(';');
	var id_item = item.split("_");
		           
	 for(var i = 0; i < array_ids.length; i++){
		  if(array_ids[i] != item && array_ids[i] != "" && array_ids[i] != null && array_ids[i] != undefined){
		       if(id_item[1] == array_ids[i]){
		             	result = true;
		       }
		  }
	} 
	return result;
} 
		  
function num_checks_checked_in_list(list){
	var count = 0;
	
	
	var array_ids = list.split(';');

   
	for(var i = 0; i < array_ids.length; i++){
		if( array_ids[i] != "" && array_ids[i] != null && array_ids[i] != undefined){
		      count++;
		}
	}

	return count;
}    
		       
function reset(){
	var count = 0;
	$('#panelAlertasValidacion > *').each(function(){
        var valueDisplay = $(this).css("display");
        if(valueDisplay == "block")
        count++;
     });
                         
     if(count == 0){
        $("#panelAlertasValidacion").hide();
     }
}

/**
 * Comprueba si hay parcelas con el estado "tipoEstado" checheados
 */  
function isCheckingPorTipoMod(tipoEstado){
	var __result = false;
	var __arrayParcelas = ($('#idsRowsChecked').val()).split(";");
	var __estadoParcela = "";

	for(var i = 0; i < __arrayParcelas.length; i++)
	{		
		var a = __arrayParcelas[i].split("_");
	    __estadoParcela = getEstadoParcela(a[0]);
	    	
		if(__estadoParcela == tipoEstado){
			__result = true;
		}	
	}
	return __result;
}   

function getEstadoParcela(codParcela){
	var __result = "??";
	var __parcelasString = ($('#parcelasString').val()).split(";");

	for(var i = 0; i < __parcelasString.length; i++){
        var ee = __parcelasString[i].split("_");
		if(ee[0] === codParcela){
			__result = ee[1];
		}
	}
	return __result;
}
	      
/**
 * Comprueba si hay algun check checkeado
 */ 
function isCheck(){
	var result = false;
	$("input[type='checkbox'][checked]").each(  function() {   
	    result = true; 
	});

	return result;
}

function cerrarPopUp(){
	$('#parcelasRep').hide();
	$('#overlay').hide();
}
			 


function deleteParcela(codPoliza, idParcela, isParcela){
	var msg = '\u00BFEst\u00E1 seguro de que desea eliminar el registro seleccionado?';
	$('#isParcela').val(isParcela);
	if(isParcela == 'true'){
		$.ajax({
			url: "ajaxCommon.html",
			data: "operacion=ajax_getNumInstalaciones&idParcela=" + idParcela,
			dataType: "json",
			success: function(numInstas){
				if(numInstas > 0){
					msg = 'Esta parcela tiene ' + numInstas + ' instalacione(s) dada(s) de alta. \u00BFDesa continuar?';
				}
				if(confirm(msg)){
					var frm = document.getElementById("main3");
					frm.operacion.value = "eliminarParcela";
					frm.codPoliza.value = codPoliza;
					frm.codParcela.value = idParcela;
					frm.submit();
				}
			},
			beforeSend: function(){
				//$("#ajaxLoading_selectLinea").show();
			},
			complete: function(){
				//$("#ajaxLoading_selectLinea").hide();
			},
			type: "POST"
		});
	}else{
		if(confirm(msg)){
			var frm = document.getElementById("main3");
			frm.operacion.value = "eliminarParcela";
			frm.codPoliza.value = codPoliza;
			frm.codParcela.value = idParcela;
			frm.submit();
		}
	}  
}
		    

		    

		    

		   
function tipoListado_onclick2(value){
	if(value === 'instalaciones'){
		document.main3.listado[1].checked = true;
		getListado('instalaciones');
	}else if(value === 'parcelas'){
		document.main3.listado[0].checked = true;
		getListado('parcelas');
	}else if(value === 'todas'){
		document.main3.listado[2].checked = true;
		getListado('todas');
	}
}
		     
function getListado(tipoListado) {
	document.forms.main3.target="";
	document.forms.main3.operacion.value = 'consultarParcela';
	document.forms.main3.tipoListadoGrid.value = tipoListado;
	document.forms.main3.submit();
}
		     
function radioElem_onmouseover(value){
	if(value === 'instalaciones'){
		 document.getElementById('instalaciones').style.textDecoration = 'underline';
		 document.getElementById('instalaciones').style.color = '#BAC441';       
	}else if(value === 'parcelas'){
		 document.getElementById('parcelas').style.textDecoration = 'underline';
		 document.getElementById('parcelas').style.color = '#BAC441';      
	}else if(value === 'todas'){
		  document.getElementById('todas').style.textDecoration = 'underline';
		   document.getElementById('todas').style.color = '#BAC441';     
	}
}
		     
function radioElem_onmouseout(value){
	if(value === 'instalaciones'){
		document.getElementById('instalaciones').style.textDecoration = 'none';
		document.getElementById('instalaciones').style.color = '#626262';                    
	}else if(value === 'parcelas'){
		document.getElementById('parcelas').style.textDecoration = 'none'; 
		document.getElementById('parcelas').style.color = '#626262';    
	}else if(value === 'todas'){
		document.getElementById('todas').style.textDecoration = 'none';
		document.getElementById('todas').style.color = '#626262';     
	}
}
		     
function limpiar() {
	$('#provincia').val('');
	$('#comarca').val('');
	$('#termino').val('');
	$('#subtermino').val('');
	$('#desc_provincia').val('');
	$('#desc_termino').val('');
	$('#desc_comarca').val('');
	$('#poligono').val('');
	$('#parcela').val('');
	$('#provSig').val('');
	$('#TermSig').val('');
	$('#agrSig').val('');
	$('#zonaSig').val('');
	$('#polSig').val('');
	$('#parcSig').val('');
	$('#recSig').val('');
	$('#nombre').val('');
	$('#cultivo').val('');
	$('#variedad').val('');
	$('#desc_cultivo').val('');
	$('#desc_variedad').val('');
	$('#sistemaCultivo').val('');
	$('#dessistemaCultivo').val('');
	$('#desc_capital').val('');
	$('#capital').val('');
	$('#rdtoHist').val('');
	$('#limpiar').val("true");
	

	consultar();
}
		     
function volver(){
	
	$('#operacion').val("volver");
	if($('#vieneDeUtilidades').val() == "true"){
		var URL = UTIL.antiCacheRand('utilidadesPoliza.html?operacion=volver');
	}else{
		var URL = UTIL.antiCacheRand('seleccionPoliza.html?operacion=volver');
	}	
    $("#main3").attr("action", URL);
	$("#main3").submit();
}  		

function continuar(){
	$('#parcelasRepOK').val('true');
	$('#sinInstalacionOK').val('true');
	$('#operacion').val('continuar');
	$('#main3').submit();
}
function consultar(){
	$('#operacion').val('consultarParcela');
	$('#main3').submit();
}

/**
 * Alta de parcela
 */
function alta(){
	$('#methodDP').val('doAlta');
	$('#codParcelaDP').val('-1');
	$('#operacionDP').val("guardarParcela");
	$('#datosParcela').submit();
}

/**
 * Visualizar la parcela o instalacion
 */
function visualizarDatosRegistro(tipoParcela, codParcela){
	$('#methodDP').val('doEditar');
	$('#codParcelaDP').val(codParcela);
	$('#tipoParcelaDP').val(tipoParcela);
	$('#operacionDP').val("visualizarParcela");
	$('#datosParcela').submit();
}

/**
 * Editar la parcela o instalacion
 */
function updateParcela(tipoParcela, codParcela){
	$('#methodDP').val('doEditar');
	$('#codParcelaDP').val(codParcela);
	$('#tipoParcelaDP').val(tipoParcela);
	$('#operacionDP').val("modificarParcela");
	var listaIdsStr = '';
	var __arrayParcelas = ($('#parcelasString').val()).split(";");
	for(var i = 0; i < __arrayParcelas.length; i++) {
		var __arrayParcela =  (__arrayParcelas[i]).split("_");
        if(__arrayParcela[1] == 'P'){
        	listaIdsStr += __arrayParcela[0] + ',';
        }		
	}
	$('#listaIdsStr').val(listaIdsStr);
	$('#datosParcela').submit();
}

/**
 * Alta de instalacion
 */
function altaEstructuraParcela(codPoliza, codParcela){
	$('#methodDP').val('doAlta');
	$('#codParcelaDP').val(codParcela);
	$('#operacionDP').val("altaEstructuraParcela");
	$('#tipoParcelaDP').val("E");
	$('#datosParcela').submit();
}

/**
 * Duplicar parcela seleccionada
 */
function duplicateParcela(codPoliza, codParcela){
	$('#methodDP').val('doDuplicar');
	$('#codParcelaDP').val(codParcela);
	$('#datosParcela').submit();
}

function siguiente(){
	if($('#modoLectura').val() == "modoLectura"){
		$('#method').val('doContinuar');
		$('#consultaDetallePoliza').submit();
	}else{
		$('#sinInstalacionOK').val('true');
		$('#operacion').val('continuar');
		$('#main3').submit();
	}
}

function comparativas(){
	$('#parcelasWeb').val('true');
	$('#action').val('');
	$('#frmComparativas').submit();
}
		  	 
// solo coloreo las instalaciones si en el listado 
// aparecen parcelas e instalaciones
function changeColorRow(){ 
		  	      if($('#tipoListadoGrid').val() == 'todas'){
					  var table = document.getElementById("parcela");
					  var i, j, cells, customerId, rows;
			          var rows = $('#listaParcelas_cm').find('tr' ).get();
				  	  for (i = 0, j = rows.length; i < j; ++i)
					  {     
						      cells = rows[i].getElementsByTagName('td');
						      if(cells.length > 0){ 
							       if (cells[0].innerHTML.indexOf("E@E") != -1){
							           rows[i].className = (i % 2 == 0) ? rows[i].className = "filaInstalacionPar" : "filaInstalacionImpar";
						           }
						       }
					  }
				  }
				  
				  // si edita volver a seleccionar la que estaba editando
			     if($('#selectedRow').val() == "true"){
			         setSelectedRow($('#idRowSelected').val());
			     }	  
}
 function disabledChecks(value){
 	$("input[type=checkbox]").each(function() { 				        
		if($(this).attr('id').indexOf('checkParcela_')!= -1){		        	
	    	$(this).attr('disabled',value);
	    }      
	});
	$("#selTodos").attr('disabled',value);
	
}
		
 function calculoRdtoHist(){
	     var itemsChecked = $('#idsRowsChecked').val();
	     if(itemsChecked.length > 0){
	    	//ESC-30568 / GD-18421
             console.log("btnRdtoHistPulsado: "+$('#rdtoHistPulsado').val());
             if($('#rdtoHistPulsado').val()=='' || $('#rdtoHistPulsado').val()=='false'){
                 $('#rdtoHistPulsado').attr('value','true');
             }else if($('#rdtoHistPulsado').val()=='true'){
                 $('#rdtoHistPulsado').attr('value','false');
             }
             console.log("btnRdtoHistPulsado: "+$('#rdtoHistPulsado').val());
             //ESC-30568 / GD-18421
	     	$('#operacion').val('calculoRdtoHist');
	     	$.blockUI.defaults.message = '<h4> Calculando rendimiento.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$('#main3').submit();
		}else{
			showPopUpAviso("Debe seleccionar como m\u00EDnimo una parcela.");
		}
	}
 
/* Pet. 78877 ** MODIF TAM (25.10.2021) ** Inicio */
function calcRdtoOrientativo(){ 
   var itemsChecked = $('#idsRowsChecked').val();
   if(itemsChecked.length > 0){
	 //ESC-30568 / GD-18421
       console.log("btnRdtoOrientativoPulsado: "+$('#rdtoOrientativoPulsado').val());
       if($('#rdtoOrientativoPulsado').val()=='' || $('#rdtoOrientativoPulsado').val()=='false'){
           $('#rdtoOrientativoPulsado').attr('value','true');
       }else if($('#rdtoOrientativoPulsado').val()=='true'){
           $('#rdtoOrientativoPulsado').attr('value','false');
       }
       console.log("btnRdtoOrientativoPulsado: "+$('#rdtoOrientativoPulsado').val());
       //ESC-30568 / GD-18421
 	  $('#operacion').val('calcRdtoOrientativo');
 	  $.blockUI.defaults.message = '<h4> Calculando rendimiento Orientativo.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	  $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	  $('#main3').submit();
   }else{
	  showPopUpAviso("Debe seleccionar como m\u00EDnimo una parcela.");
   }
}   
/* Pet. 78877 ** MODIF TAM (25.10.2021) ** Fin */
 
/* Pet. 643485 - FASE III DNF 23/12/2020 */ 
function calcularPrecioMasivo(){ 
	if(haveParcelaSele()){
			if(confirm('\u00BFEst\u00E1 seguro de que desea recalcular el precio de las parcelas seleccionadas?')){
				
				$('#method').val('doPrecioMasivo');
				$('#lineaseguroid').val($('#lineaseguroid').val());
				$('#listCodModulos').val($('#listCodModulos').val());
				$('#idsRowsCheckedCM').val($('#idsRowsChecked').val());
				$('#idPolizaCM').val($('#idpoliza').val());
				$('#precioMasivoForm').submit();
								
				$.blockUI.defaults.message = '<h4> Recalculando precio de las parcelas seleccionadas.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			    $.blockUI({ 
			    	overlayCSS: { backgroundColor: '#525583'},
			        baseZ: 2000
			    });
			} 
	}else{
		showPopUpAviso("Debe seleccionar como m&iacute;nimo una parcela.");
	}
}
/* fin Pet. 643485 - FASE III DNF 23/12/2020 */  

/* Pet. 63668 - FASE III DNF 05/08/2021 */ 
function danhosFauna() {
	if(haveParcelaSele()) {	
		var danhoFaunaMaxParcelas = $('#danhoFaunaMaxParcelas').val(); 
		var idsRowsChecked = $('#idsRowsChecked').val();
		var numParcelasSelected;
	
		if(idsRowsChecked.endsWith(';')){
			idsRowsChecked=idsRowsChecked.substring(0, idsRowsChecked.length-1);
		}
		
		if(idsRowsChecked.startsWith(';')){
			numParcelasSelected=idsRowsChecked.substring(1, idsRowsChecked.length).split(';').length
		}else{
			numParcelasSelected=idsRowsChecked.replace(";;",";").split(';').length;
		}
		
		var parcelasExceded = $('#idsRowsChecked').val().length > danhoFaunaMaxParcelas;

		if (numParcelasSelected > danhoFaunaMaxParcelas) {// se han seleccionado más parcelas de las permitidas	
			showPopUpAviso("Se han seleccionado m\u00E1s parcelas de las permitidas (" + danhoFaunaMaxParcelas + ").");	
		} else {
			$('#method').val('doConsulta');
			$('#idsRowsCheckedDF').val($('#idsRowsChecked').val());
			$('#idPolizaDF').val($('#idpoliza').val());
			$('#danhosFaunaForm').submit();	
		}			
	} else {// no hay ninguna parcela marcada		
		showPopUpAviso("Debe seleccionar como m\u00EDnimo una parcela.");	
	}
}
/* fin Pet. 63668 - FASE III DNF 23/12/2020 */ 



function abrePopupDanhosFauna() {
	const urlParams = new URLSearchParams(window.location.search);
	const page_type = urlParams.get("mostrarPopupDanhosFauna");
	if (page_type === "true") {
		$('#overlayDanhosFauna').show();
		$('#panelDanhosFauna').show();
	}
}

//ESC-30568 / GD-18421
$(document).ready(function(){
	console.log("btnRdtoHistPulsado: "+$('#rdtoHistPulsado').val());
    console.log("btnRdtoOrientativoPulsado: "+$('#rdtoOrientativoPulsado').val());
    if($('#rdtoOrientativoPulsado').val()=='false'){
    	$('#tipProd').attr('style','color:#626262;');
    }
    if($('#btnRdtoHistPulsado').val()=='false'){
    	$('#tipProd').attr('style','color:#626262;');
    }
})
//ESC-30568 / GD-18421