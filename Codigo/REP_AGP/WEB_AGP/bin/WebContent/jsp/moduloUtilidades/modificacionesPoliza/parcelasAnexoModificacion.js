$(function(){
	
	// --------------------------------------------------
	//         paginacion por AJAX
	// --------------------------------------------------
	$("#grid").displayTagAjax();
}).ajaxSend(function(){
	
	//checkSeleccionados();
}).ajaxComplete(function() {
   	changeColorRow();
   	check_checks($('#idsCapAsegRowsChecked').val());
   	$('#sel').text(numero_check_seleccionados());
   	checkTodos();
});
    
var estadosCapitalesAsegurados = new Array();

$(document).ready(function(){
	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
	document.getElementById("main3").action = URL;
	document.getElementById('provincia').focus();
	
	/* Pet. 78691 ** MODIF TAM (22/12/2021) */
	$('#sistemaCultivo').val($('#sist_cultivo').val());
	$('#dessistemaCultivo').val($('#des_sist_cultivo').val());
	/* Pet. 78691 ** MODIF TAM (22/12/2021) */
	
	// --------------------------------------------------
	//                    validaciones
	// ----------------------------------------------------
	$('#main3').validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		highlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).show();
		},
		unhighlight: function(element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
			
			var count = 0;
			
			$('#panelAlertasValidacion > *').each(function(){
				if($(this).css("display") == "block")
					count++;
			});
			
			if(count == 1 || count == 0)
				$("#panelAlertasValidacion").hide();
		},
		rules: {
			"parcela.hoja"           	 :{required: false,digits: true, maxlength:5},
			"parcela.numero"         	 :{required: false,digits: true, maxlength:5},
			"parcela.poligono"       	 :{required: false,digits: true, maxlength:3},
			"parcela.parcela_1"      	 :{required: false,digits: true, maxlength:5},
			"parcela.codprovsigpac"  	 :{required: false,digits: true, maxlength:2},
			"parcela.codtermsigpac"  	 :{required: false,digits: true, maxlength:3},
			"parcela.agrsigpac"      	 :{required: false,digits: true, maxlength:3},
			"parcela.zonasigpac"     	 :{required: false,digits: true, maxlength:3},
			"parcela.poligonosigpac" 	 :{required: false,digits: true, maxlength:3},
			"parcela.parcelasigpac"  	 :{required: false,digits: true, maxlength:5},
			"parcela.recintosigpac"  	 :{required: false,digits: true, maxlength:3},
			"parcela.codprovincia"       :{required: false,digits: true, maxlength:3},
			"parcela.codcomarca"         :{required: false,digits: true, maxlength:3},
			"parcela.codtermino"         :{required: false,digits: true, maxlength:3},
			"parcela.subtermino"         :{required: false,lettersonly: true},
			"tipoCapital.codtipocapital" :{required: false,digits: true, maxlength:3},
			"parcela.codcultivo"         :{required: false,digits: true, maxlength:3},
			"parcela.codvariedad"        :{required: false,digits: true, maxlength:3},
			"superficie"                 :{required: false,VALDECIMAL82:true},
			"produccion"                 :{required: false,VALDECIMAL82:true}
		},
		messages: {
			"parcela.hoja"           	 :{digits: "El campo Hoja solo puede contener d&iacute;gitos",              maxlength: "El campo Hoja debe contener como m&aacute;ximo 5 d&iacute;gito"},
			"parcela.numero"         	 :{digits: "El campo N&uacute;mero solo puede contener d&iacute;gitos",            maxlength: "El campo N&uacute;mero debe contener como m&aacute;ximo 5 d&iacute;gitos"},
			"parcela.poligono"       	 :{digits: "El campo Poligono(Id.cat.) solo puede contener d&iacute;gitos", maxlength: "El campo Poligono(Id.cat.) debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"parcela.parcela_1"      	 :{digits: "El campo Parcela(Id.cat.) solo puede contener d&iacute;gitos",  maxlength: "El campo Parcela(Id.cat.) debe contener  como m&aacute;ximo 5 d&iacute;gitos"},
			"parcela.codprovsigpac"  	 :{digits: "El campo Prov(Sigpac) solo puede contener d&iacute;gitos",      maxlength: "El campo Prov(Sigpac) debe contener como m&aacute;ximo 2 d&iacute;gitos"},
			"parcela.codtermsigpac"  	 :{digits: "El campo Term(Sigpac) solo puede contener d&iacute;gitos",      maxlength: "El campo Term(Sigpac) debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"parcela.agrsigpac"      	 :{digits: "El campo Agr(Sigpac) solo puede contener d&iacute;gitos",       maxlength: "El campo Agr(Sigpac) debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"parcela.zonasigpac"     	 :{digits: "El campo Zona(Sigpac) solo puede contener d&iacute;gitos",      maxlength: "El campo Zona(Sigpac) debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"parcela.poligonosigpac" 	 :{digits: "El campo Pol(Sigpac) solo puede contener d&iacute;gitos",       maxlength: "El campo Pol(Sigpac) debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"parcela.parcelasigpac"  	 :{digits: "El campo Parc(Sigpac) solo puede contener d&iacute;gitos",      maxlength: "El campo Parc(Sigpac) debe contener como m&aacute;ximo 5 d&iacute;gitos"},
			"parcela.recintosigpac"  	 :{digits: "El campo Rec(Sigpac) solo puede contener d&iacute;gitos",       maxlength: "El campo Rec(Sigpac) debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"parcela.codprovincia"       :{digits: "El campo Provincia solo puede contener d&iacute;gitos",         maxlength: "El campo Provincia debe contener como m&aacute;ximo 2 d&iacute;gitos"},
			"parcela.codcomarca"         :{digits: "El campo Comarca solo puede contener d&iacute;gitos",           maxlength: "El campo Comarca debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"parcela.codtermino"         :{digits: "El campo Termino solo puede contener d&iacute;gitos",           maxlength: "El campo Termino debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"parcela.subtermino"         :{lettersonly: "El campo Subtermino no puede contener d&iacute;gitos"},
			"tipoCapital.codtipocapital" :{digits: "El campo Tipo capital solo puede contener d&iacute;gitos",      maxlength: "El campo Tipo capital debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"parcela.codcultivo"         :{digits: "El campo Cultivo solo puede contener d&iacute;gitos",           maxlength: "El campo Cultivo debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"parcela.codvariedad"        :{digits: "El campo Variedad solo puede contener d&iacute;gitos",         maxlength: "El campo Variedad debe contener como m&aacute;ximo 3 d&iacute;gitos"},
			"superficie"                 :{VALDECIMAL82: "El campo Superficie debe contener como m&aacute;ximo 8 d&iacute;gitos de parte entera un punto y 2 d&iacute;gitos de parte decimal"},
			"produccion"                 :{VALDECIMAL82: "El campo Producci&oacute;n debe contener como m&aacute;ximo 8 d&iacute;gitos de parte entera un punto y 2 d&iacute;gitos de parte decimal"}
		}
	});

    jQuery.validator.addMethod("VALDECIMAL82", function(value, element) {
    	if(value.length == 0)
    		return true;
    	else
    		return /^\d{0,8}([.]\d{1,2})?$/.test(value);
    });

});
	         
function onClickInCheck(idCheck, idCapAsegCheck) {
    if(idCapAsegCheck) {
		var __aux = idCheck.split("_");
		var __auxCapAseg = idCapAsegCheck.split("_");
		var __ids = $('#idsRowsChecked').val();
		var __idsCapAseg = $('#idsCapAsegRowsChecked').val();
		
        if(document.getElementById(idCapAsegCheck).checked == true) {
             addCheck(__ids, __idsCapAseg,  __aux[1], __auxCapAseg[1]);
        }
        else {
             subtractCheck(__ids, __idsCapAseg, __auxCapAseg[1]);
             $('#marcarTodosChecks').val("no");
             $('#selTodos').attr('checked',false);
        }
        
		$('#sel').text(numero_check_seleccionados());
	}
}

/**
* @params: ids --> contiene los ids de los registros seleccionados de todas las paginas
* Selecciona los checks que vienen en el param. ids y que estan en la pagina actual
* ejemplo param. ids: 3;5;6;23;10
*/
function check_checks(idsCapAseg) {
	if(idsCapAseg != null && idsCapAseg != "") {
		var array_ids_capAseg = idsCapAseg.split(';');
		for(var i = 0; i < array_ids_capAseg.length; i++) {
			if (array_ids_capAseg[i] != "") {
				var idCapAsegCheck = "checkParcela_" + array_ids_capAseg[i];
				$('#' + idCapAsegCheck).attr('checked',true);
			}
		}
	}
}

function addCheck(ids, idsCapAseg, check, checkCapAseg) {
	
	if(ids != null) {
		 ids = ids + ";" + check;
	}
	if(idsCapAseg != null) {
		idsCapAseg = idsCapAseg + ";" + checkCapAseg;
	}

	$('#idsRowsChecked').val(ids);
	$('#idsCapAsegRowsChecked').val(idsCapAseg);

}

function subtractCheck(ids, idsCapAseg, checkCapAseg) {
	var newList = "";
	var newListCapAseg = "";
	
	if(idsCapAseg != null) {
		var array_ids = ids.split(';');
		var array_ids_capAseg = idsCapAseg.split(';');
		
		for(var i = 0; i < array_ids_capAseg.length; i++) {
		     if(array_ids_capAseg[i] != checkCapAseg && array_ids_capAseg[i] != "" && array_ids_capAseg[i] != null && array_ids_capAseg[i] != undefined) {
		         newList = newList + array_ids[i] + ";";
		         newListCapAseg = newListCapAseg + array_ids_capAseg[i] + ";";
		     }
		}
	}
	
	$('#idsRowsChecked').val(newList);
	$('#idsCapAsegRowsChecked').val(newListCapAseg);
}

function numero_check_seleccionados() {
	return num_checks_checked_in_list($('#idsCapAsegRowsChecked').val());
}

function selectAllChecks(elem){
	
	if(elem.checked){
	    seleccionar_checks();
	}    
	else{
		desseleccionar_checks();
	}		
}
function seleccionar_checks() {
	var __ids =  "";
	var __arrayParcelas = ($('#parcelasString').val()).split(";");
	var __arrayCapAseg = ($('#capAsegString').val()).split(";");
	var __idsCapAseg = "";
	
	$('#marcarTodosChecks').val("si");
	
	for(var i = 0; i < __arrayCapAseg.length; i++) {
		var __arrayParcela =  (__arrayParcelas[i]).split("_");		
		var __idCapAsegCheck = "checkParcela_" + __arrayCapAseg[i];
		$('#' + __idCapAsegCheck).attr('checked',true);
		__idsCapAseg = __idsCapAseg + ";" + __arrayCapAseg[i];
		__ids = __ids + ";" + __arrayParcela[0];			
	}
	
	$('#idsRowsChecked').val(__ids);
	$('#idsCapAsegRowsChecked').val(__idsCapAseg);
	$('#sel').text(__arrayCapAseg.length-1);
}

function desseleccionar_checks() {
	var __arrayCapAseg = ($('#capAsegString').val()).split(";");
	
	$('#marcarTodosChecks').val("no");
	
	for(var i = 0; i < __arrayCapAseg.length; i++) {
	    	var __idCapAsegCheck = "checkParcela_" + __arrayCapAseg[i];
			$('#' + __idCapAsegCheck).attr('checked',false);
	}
	
	$('#idsRowsChecked').val('');
	$('#idsCapAsegRowsChecked').val('');
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
		  
function num_checks_checked_in_list(listCapAseg) {
	var count = 0;
	var array_ids_capAseg = listCapAseg.split(';');
   
	for(var i = 0; i < array_ids_capAseg.length; i++) {
		if( array_ids_capAseg[i] != "" && array_ids_capAseg[i] != null && array_ids_capAseg[i] != undefined) {
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
 * --------------------------------------------------------------------------
 *                           OPERACIONES BOTONES
 * --------------------------------------------------------------------------
 */	
       
/**
 * Alta parcela
 */ 
function altaParcela(){
	$("#panelInformacion").hide();
	$('#method').val('doAlta');
	$('#tipoParcela').val('P');
	$('#main3').submit();
}
	      
/**
 * Consultar parcela
 */ 
function consultarParcelas(){
	$('#idsRowsChecked').val('');
	$("#panelInformacion").hide();
	$("#itemCombo").val($("#parcela.tipomodificacion").val());
	$('#method').val('doConsulta');
	$('#main3').submit();
}
	      
/**
 *  Eliminar parcela
 */
function eliminarParcela(codPoliza,codParcela){

	 if(!isCheckingPorTipoMod("A") && !isCheckingPorTipoMod("B")){
		 if((codPoliza != "" && codParcela != "" )||(isCheck())){
			if (confirm("\u00BFEst\u00E1 seguro que desea dar de baja la(s) parcela(s)?","")) {
				if(codPoliza != "" && codParcela != ""){
					 $('#codPoliza').val(codPoliza);
					 $('#codParcela').val(codParcela);
				} 
				//UTIL.openModalWindow(); --> en el caso que se necesite ventana emergente preguntando causa baja,eliminar las sigu ientes lineas y descomentar
				baja();
			}
		}else{
			alert("Debe seleccionar como m\u00EDnimo un registro.");
		}
	}else{
		if(codParcela != "" && codParcela !=""){
			$('#codPoliza').val(codPoliza);
			$('#codParcela').val(codParcela);
			baja();
		}else{
			alert("No est\u00E1 permitido dar de baja parcelas en estado A o B.");
		}	
	}
}

function baja(){
	$('#method').val('doBaja');
	$.blockUI.defaults.message = '<h4> Realizando la baja de los capitales.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$('#main3').submit();
}

	      
/**
 * Deshacer cambios parcela
 */ 
function deshacerCambiosParcela(codParcela){
	 var doSubmit = false;
 
	  if(!isCheckingPorTipoMod(" "))
	  {
		  if(codParcela != ""){       
			  doSubmit = true;
			  $('#codParcela').val(codParcela);
		  }else{
			  if(isCheck())
			       doSubmit = true;
			  else
			       doSubmit = false;
		  }
			          
		  // Ir al server y deshacer
		  if(doSubmit){
			  if (confirm("\u00BFEst\u00E1 seguro que desea deshacer la(s) parcela(s)?")) 
			  {
				$('#method').val('doDeshaz');
				$.blockUI.defaults.message = '<h4> Deshaciendo los datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				$('#main3').submit();
			  }
		  }
		  else{
			    alert("Debe seleccionar como m\u00EDnimo un registro.");
		  }
	  }
	  else{
		 alert("No est\u00E1 permitido deshacer parcelas sin estado.");
	  }
}

/**
 * --------------------------------------------------------------------------
 *                   VALIDACIONES OPERACIONES BOTONES
 * --------------------------------------------------------------------------
 */
   
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
	var resultado = false;
	
	$("input[type='checkbox']").each(  function(k, v) {   		
		if ($('#' + v.id).is(':checked')) {
			resultado = true; 	    
		}
	});

	return resultado;
}

function tipoListado_onclick(value){
	
	$('#isClickInListado').val('si');
	
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
	$('#tipoListadoGrid').val(tipoListado);
	$('#main3').submit();
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

/**
 * Editar parcela
 */ 
function editarParcela(codPoliza,codParcela){
	$('#codPoliza').val(codPoliza);
	$('#codParcela').val(codParcela);
	$('#method').val('doEdita');
	$('#main3').submit();
}

/**
 * Visualizar datos capital aseguado de anexo
 */ 
function visualizarDatosRegistro(codPoliza, codParcela){
    $('#method').val('doEdita');
    $('#codPoliza').val(codPoliza);
	$('#codParcela').val(codParcela);
	$('#modoLectura').val('modoLectura');
    $('#main3').submit();
}

/**
 * Boton volver
 */ 
function volver(){
	$('#method').val('doVolver');
	$('#main3').submit();
}

/**
 * Boton volver Anexo Modificacion
 */ 
function volverAnexo(){
	$('#method').val('doVolverAnexo');
	$('#main3').submit();
}

function volverAnexoListado(){
	$('#volverUtilidadesAnexos').submit();
}
/**
 * Limpiar formulario e ir al server a por la lista de capitales asegurados
 */ 
function limpiar(){
	$('#txt_hoja').val('');
	$('#txt_numero').val('');
	$('#txt_poligono').val('');
	$('#txt_parcela').val('');     
	$('#txt_provsigpac').val('');             
	$('#txt_termsigpac').val('');
	$('#txt_agrsigpac').val('');
	$('#txt_zonasigpac').val('');
	$('#txt_polsigpac').val('');
	$('#txt_parcsigpac').val('');
	$('#txt_recsigpac').val('');
	$('#txt_nombreParcela').val('');
	$('#provincia').val('');
	$('#txt_desProvincia').val('');
	$('#comarca').val('');
	$('#txt_desComarca').val('');
	$('#termino').val('');
	$('#txt_desTermino').val('');
	$('#subtermino').val('');
	$('#cultivo').val('');
	$('#txt_desCultivo').val('');
	$('#variedad').val('');
	$('#txt_desVariedad').val('');
	$('#capital').val('');
	$('#txt_desTipoCapital').val('');
	$('#txt_superficie').val('');
	$('#txt_produccion').val('');
	$('#comarca').val('');
	$('#termino').val('');
	$('#subtermino').val('');
	$('#desc_provincia').val('');
	$('#desc_comarca').val('');
	$('#desc_termino').val('');
	$('#desc_cultivo').val('');
	$('#desc_variedad').val('');
	$('#desc_capital').val('');
	$('#rdtoHist').val('');
	/* GDLD-78691 ** MODIF TAM (30/12/2021) Defecto Nº 2 */
	$('#sistemaCultivo').val('');
	$('#dessistemaCultivo').val('');
	
	$('#sist_cultivo').val('');
	$('#des_sist_cultivo').val('');
	/* GDLD-78691 ** MODIF TAM (30/12/2021) Defecto Nº 2 */
	
	$("select#sl_tipoModificacion").val('T');  
	//falta reset sl_planesDestino
	consultarParcelas();
}
	     
/**
 * Marca/desmarcar todos los checkboxs 
 */ 
var checked_status = false;
function checkAllToogle(){
	if(checked_status){
		$("input[type='checkbox']:not([disabled='disabled'])").attr('checked', false);
		checked_status = false;
	}
	else{
		$("input[type='checkbox']:not([disabled='disabled'])").attr('checked', true);
		checked_status = true;
	}	 
}
	     
/**
 * Consultar parcela
 */ 
function subvenciones(){
	$.blockUI.defaults.message = '<h4> Grabando los datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	$('#frmSubvenciones').submit();
}
		
function altaEstructuraAnexoParcela(codPoliza, codParcela){
	$('#codPoliza').val(codPoliza);
	$('#codParcela').val(codParcela);
	$('#tipoParcela').val('E');
	$('#method').val('doAlta');
	$('#main3').submit();
}
		
function changeColorRow(){
	var table = document.getElementById('listaParcelasModificadas');
	var i, j, cells, customerId, rows;

	if(table != null){
		if(table.getElementsByTagName('tr')!= null){
			rows = table.getElementsByTagName('tr');
		}
		for (i = 0, j = rows.length; i < j; ++i){
			cells = rows[i].getElementsByTagName('td');
			if(cells.length > 0){
				if (cells[0].innerHTML.indexOf("E@E") != -1)
					rows[i].className = (i % 2 == 0) ? rows[i].className = "filaInstalacionPar" : "filaInstalacionImpar";
			}
		}
	}
}

function checkTodos() {
	if($('#marcarTodosChecks').val() == 'si') {
		$('#selTodos').attr('checked',true);
	}else {
		$('#selTodos').attr('checked',false);
	}	
}

/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (24/02/2021) * Inicio */
function showPopUpCambioIBAN(){
	/*alert("Dentro de showPopUpCambioIBAN [INIT]");*/
	
	$('#panelAlertasValidacion').html('');
	$('#panelAlertasValidacion').hide();
	
	$('#panelInformacion').html('');
	$('#panelInformacion').hide();
	
	var ibanAsegOriginal = $('#ibanAsegOriginal').val();
	
	var ibanAsegFormateado = ibanAsegOriginal.substr(0, 4)+" "+ibanAsegOriginal.substr(4, 4)+" "+ibanAsegOriginal.substr(8, 4)+" "+ibanAsegOriginal.substr(12, 4)+" "+ibanAsegOriginal.substr(16, 4)+" "+ibanAsegOriginal.substr(20, 4);
	$('#ibanAntiguo').text(ibanAsegFormateado);
 	$('#divCambioIBAN').fadeIn('normal');
 	$('#cambioIBANPopUpError').hide();
 	
 	/* Pet. 70105 ** MODIF TAM (11/03/2021) */
 	/* RGA solicita que se visualice siempre el contenido de las cuentas modificadas, no solo en consulta */
 	var ibanAsegModificado = $('#ibanAsegModificado').val();
 	$('#iban').val(ibanAsegModificado.substr(0, 4));
	$('#cuenta1').val(ibanAsegModificado.substr(4, 4));
	$('#cuenta2').val(ibanAsegModificado.substr(8, 4));
	$('#cuenta3').val(ibanAsegModificado.substr(12, 4));
	$('#cuenta4').val(ibanAsegModificado.substr(16, 4));
	$('#cuenta5').val(ibanAsegModificado.substr(20, 4));
	/* Pet. 70105 ** MODIF TAM (11/03/2021) * Fin */

	
 	var iban2AsegOriginal = $('#iban2AsegOriginal').val();
	var iban2AsegFormateado = iban2AsegOriginal.substr(0, 4)+" "+iban2AsegOriginal.substr(4, 4)+" "+iban2AsegOriginal.substr(8, 4)+" "+iban2AsegOriginal.substr(12, 4)+" "+iban2AsegOriginal.substr(16, 4)+" "+iban2AsegOriginal.substr(20, 4);
	$('#iban2Antiguo').text(iban2AsegFormateado);
 	$('#divCambioIBAN').fadeIn('normal');
 	$('#cambioIBANPopUpError').hide();
 	
 	
 	/* Pet. 70105 ** MODIF TAM (11/03/2021) */
 	/* RGA solicita que se visualice siempre el contenido de las cuentas modificadas, no solo en consulta */
	var iban2AsegModificado = $('#iban2AsegModificado').val();
	$('#iban2').val(iban2AsegModificado.substr(0, 4));
	$('#iban2_cuenta1').val(iban2AsegModificado.substr(4, 4));
	$('#iban2_cuenta2').val(iban2AsegModificado.substr(8, 4));
	$('#iban2_cuenta3').val(iban2AsegModificado.substr(12, 4));
	$('#iban2_cuenta4').val(iban2AsegModificado.substr(16, 4));
	$('#iban2_cuenta5').val(iban2AsegModificado.substr(20, 4));
	/* Pet. 70105 ** MODIF TAM (11/03/2021) * Fin */
 	
	$('#overlay').show(); 
	
}	


function cambiarIBAN(){
	//alert ("Dentro de cambiarIBAN [INIT]");
	var ibanAntiguo = $('#ibanAsegOriginal').val().replace(/ /g,'');
	var ibanNuevo = $('#iban').val() + $('#cuenta1').val()+ $('#cuenta2').val() + $('#cuenta3').val() + $('#cuenta4').val()+ $('#cuenta5').val();
	
	var iban2Antiguo = $('#iban2AsegOriginal').val().replace(/ /g,'');
	var iban2Nuevo = $('#iban2').val() + $('#iban2_cuenta1').val()+ $('#iban2_cuenta2').val() + $('#iban2_cuenta3').val()+ $('#iban2_cuenta4').val()+ $('#iban2_cuenta5').val();
	
	if (ibanAntiguo == ibanNuevo && iban2Antiguo == iban2Nuevo){
		//alert ("Dentro de cambiarIBAN [INIT] - Entramos en el primer if");
		$('#cambioIBANPopUpError').html('IBAN Pago Prima e IBAN cuenta Cobro Siniestro coincidentes');
		$('#cambioIBANPopUpError').show();
		vaciarNuevoIBAN();
		vaciarNuevoIBAN2();
	}else if (ibanAntiguo!=ibanNuevo && iban2Antiguo!=iban2Nuevo){
		//alert ("Dentro de cambiarIBAN [INIT] - Entramos en el primer else if");
	    //alert("Valor de IbanNuevo:-"+ibanNuevo+"-");
	    //alert("Valor de Iban2Nuevo:-"+iban2Nuevo+"-");
		
	    if (ibanNuevo != "" && iban2Nuevo !=""){
		   //alert ("if");	
		   //alert ("Validar IbanNuevo:"+validarCampoIBAN(ibanNuevo));
		   //alert ("Validar Iban2Nuevo:"+validarCampoIBAN(iban2Nuevo));
			if (!validarCampoIBAN(ibanNuevo) || !validarCampoIBAN(iban2Nuevo)){
				 $('#cambioIBANPopUpError').html('IBAN incorrecto');
				 $('#cambioIBANPopUpError').show();
				 return;
			}
		}else if (ibanNuevo != "" && iban2Nuevo ==""){
			//alert ("else if(1)");
			//alert("Valor de validar:"+validarCampoIBAN(ibanNuevo));
			if (!validarCampoIBAN(ibanNuevo) ){
				 $('#cambioIBANPopUpError').html('IBAN incorrecto');
				 $('#cambioIBANPopUpError').show();
				 return;
			}
			
		}else if (ibanNuevo == "" && iban2Nuevo !=""){
			//alert ("else if(2)");
			if (!validarCampoIBAN(iban2Nuevo) ){
				 $('#cambioIBANPopUpError').html('IBAN incorrecto');
				 $('#cambioIBANPopUpError').show();
				 return;
			}
			
		}
	    //alert ("Continuamos");
		if (validarCampoIBAN(ibanNuevo) && validarCampoIBAN(iban2Nuevo)){
			//alert ("Dentro de cambiarIBAN [INIT] - Entramos en el primer if (1)");
			$('#cambioIBANPopUpError').hide();
			if (confirm('ï¿½Seguro que desea cambiar los IBAN de la pï¿½liza?')){
				$('#ibanCompleto').val(ibanNuevo);
				$('#iban2Completo').val(iban2Nuevo);
				/* Le pasamos el parametro P para que identificar que es el Iban de la pï¿½liza */
				cambiarIbanAjax();
			}
		}else if(!validarCampoIBAN(ibanNuevo) && validarCampoIBAN(iban2Nuevo)){
			  if (ibanNuevo != ""){
				  if (!validarCampoIBAN(ibanNuevo)){
					   $('#cambioIBANPopUpError').html('IBAN incorrecto');
					   $('#cambioIBANPopUpError').show();   
				   }
			  }else{
				  if (iban2Nuevo != "" && validarCampoIBAN(iban2Nuevo)){
					if (confirm('ï¿½Seguro que desea cambiar los IBAN de la pï¿½liza?')){
						$('#ibanCompleto').val(ibanNuevo);
						$('#iban2Completo').val(iban2Nuevo);
						/* Le pasamos el parametro P para que identificar que es el Iban de la pï¿½liza */
						cambiarIbanAjax();
					}
				  }
			  } 
				
		}else{
			if(validarCampoIBAN(ibanNuevo) && !validarCampoIBAN(iban2Nuevo)){
				if (iban2Nuevo != ""){
					$('#cambioIBANPopUpError').html('IBAN Cuenta Cobro Siniestro incorrecto');
					$('#cambioIBANPopUpError').show();
				}else{
					if (confirm('ï¿½Seguro que desea cambiar los IBAN de la pï¿½liza?')){
						$('#ibanCompleto').val(ibanNuevo);
						$('#iban2Completo').val(iban2Nuevo);
						/* Le pasamos el parametro P para que identificar que es el Iban de la pï¿½liza */
						cambiarIbanAjax();
					}
				}
			}else{
				if (confirm('ï¿½Seguro que desea cambiar los IBAN de la pï¿½liza?')){
					$('#ibanCompleto').val(ibanNuevo);
					$('#iban2Completo').val(iban2Nuevo);
					/* Le pasamos el parametro P para que identificar que es el Iban de la pï¿½liza */
					cambiarIbanAjax();
				}
			}
		}
	}else{
	    //alert ("Entra en el else");
		if(ibanAntiguo!=ibanNuevo){
			if(validarCampoIBAN(ibanNuevo)){
				$('#cambioIBANPopUpError').hide();
				if (confirm('ï¿½Seguro que desea cambiar el IBAN de la pï¿½liza?')){
					$('#ibanCompleto').val(ibanNuevo);
					/* Le pasamos el parametro P para que identificar que es el Iban de la pï¿½liza */
					cambiarIbanAjax();
				}
			}else{
				if (ibanNuevo != ""){
					//alert("Error iban incorrecto 1");
					$('#cambioIBANPopUpError').html('IBAN incorrecto');
					$('#cambioIBANPopUpError').show();
				}
			}
		}else{
			if (ibanNuevo != ""){
				vaciarNuevoIBAN();
				$('#cambioIBANPopUpError').html('IBAN coincidente');
				$('#cambioIBANPopUpError').show();
			}
		}
				
		/* Validamos el Iban Cuenta Cobro Siniestro */
		//alert ("Valor de iban2Antiguo:-"+iban2Antiguo +"- comparamos con iban2Nuevo:-"+iban2Nuevo+"-");
		
		if(iban2Antiguo!=iban2Nuevo){
			if(validarCampoIBAN(iban2Nuevo)){
				$('#cambioIBANPopUpError').hide();
				if (confirm('ï¿½Seguro que desea cambiar el IBAN cuenta Cobro Siniestro de la pï¿½liza?')){
					$('#iban2Completo').val(iban2Nuevo);
					//alert ("Valor de iban2Completo:"+$('#iban2Completo').val());
					/* Le pasamos el parametro 'S' para que identificar que es el Iban del Siniestro */
					cambiarIbanAjax();
				}
		
			}else{
				if(iban2Nuevo != '' ){
					$('#cambioIBANPopUpError').html('IBAN Cuenta Cobro Siniestro incorrecto');
					$('#cambioIBANPopUpError').show();
				}
			}
		}else{
			if (iban2Nuevo != ""){
				vaciarNuevoIBAN2();
				$('#cambioIBANPopUpError').html('IBAN Cuenta Cobro Siniestro coincidente');
				$('#cambioIBANPopUpError').show();
			}
		}
	}
	
	//alert ("Dentro de cambiarIBAN [END]");
}


function cambiarIbanAjax(){
	
	//alert ("Dentro de cambiarIbanAjax [INIT]");

	var form = document.forms['main3'];
	var idAnexo = form['idAnexoModificacion'].value;
	//alert ("Valor de idAnexo:"+idAnexo);
	
	var nuevoIBAN = $('#ibanCompleto').val();
	//alert ("Valor de nuevoIBAN:"+nuevoIBAN);
	
	var nuevoIBAN2 = $('#iban2Completo').val();
	//alert ("Valor de nuevoIBAN:"+nuevoIBAN2);
	
	$.ajax({
        url: "anexoModificacionUtilidades.run",
        data: "method=doCambiarIBAN&idAnexo="+idAnexo + "&nuevoIBAN="+nuevoIBAN + "&nuevoIBAN2="+nuevoIBAN2,
        async:true,
        cache: false,
        beforeSend: function(objeto){
        },
        complete: function(objeto, exito){
        },
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        error: function(objeto, quepaso, otroobj){
            alert("Error al intentar cambiar el IBAN del anexo: " + quepaso);
        },
        global: true,
        ifModified: false,
        processData:true,
        success: function(datos){
        	if(datos.cambioIBANValido.valueOf() == "true"){
            	$('#ibanAsegModificado').val(nuevoIBAN);
            	$('#iban2AsegModificado').val(nuevoIBAN2);
            	$('#panelMensajeValidacion').html('Cambio de IBAN realizado');
            	$('#panelMensajeValidacion').show();
             	$('#divCambioIBAN').fadeOut('normal');
            	$('#overlay').hide();
        	}else{
        		$('#cambioIBANPopUpError').html('Se produjo un error al intentar cambiar el IBAN');
        		$('#cambioIBANPopUpError').show();
        	}
        },
        type: "POST"
    });
	

	//alert ("Dentro de cambiarIbanAjax [END]");
}
/*  Pet. 70105 - Fase III (REQ.05) - MODIF TAM (24/02/2021) * Fin */


function cerrarPopUpCambioIBAN(){
	$('#divCambioIBAN').fadeOut('normal');
	$('#overlay').hide();
}

function vaciarNuevoIBAN(){
	$('#iban').val('');
	$('#cuenta1').val('');
	$('#cuenta2').val('');
	$('#cuenta3').val('');
	$('#cuenta4').val('');
	$('#cuenta5').val('');
	$('#ibanCompleto').val('');
}

function vaciarNuevoIBAN2(){
	$('#iban2').val('');
	$('#iban2_cuenta1').val('');
	$('#iban2_cuenta2').val('');
	$('#iban2_cuenta3').val('');
	$('#iban2_cuenta4').val('');
	$('#iban2_cuenta5').val('');
	$('#iban2Completo').val('');
}
