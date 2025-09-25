$(function(){
	$("#grid").displayTagAjax();
});
				
$(document).ready(function(){
			
	$('#nTotalChecks').val(0);
	$('#nChecks').val(0);
	
	var URL = UTIL.antiCacheRand($("#main3").attr("action"));
	$("#main3").attr("action", URL);
	
	$("#main3").validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li"
	});

	$('#frmCambioMasivo').validate({
		errorLabelContainer: "#panelAlertasValidacion_cm",
		wrapper: "li",
		rules: {
			"inputFechaRec" :{dateITA: true}
		},
		messages: {
			"inputFechaRec" :{dateITA:"El formato del campo Fecha Recolección es dd/mm/YYYY"}
		}
	});
	
	Zapatec.Calendar.setup({
		firstDay          : 1,
		weekNumbers       : false,
		showOthers        : true,
		showsTime         : false,
		timeFormat        : "24",
		step              : 2,
		range             : [1900.01, 2999.12],
		electric          : false,
		singleClick       : true,
		inputField        : "inputFechaRec",
		button            : "btn_FechaRec",
		ifFormat          : "%d/%m/%Y",
		daFormat          : "%d/%m/%Y",
		align             : "Br"
	});
	
	$('#sel').text(numero_check_seleccionados2());
	
});



//-------------------------------------------------------------------------
// INICIO Funciones de los botones del formulario
//-------------------------------------------------------------------------

		
function consultar(){	
	$('#method').val('doConsulta');
	$('#getParcelasBBDD').val(true);
	$("#main3").validate().cancelSubmit = true;
	$('#main3').submit();
}

function limpiar(){
	$('#tx_hoja').val('');		
	$('#tx_n').val('');
	$('#provSig').val('');
	$('#TermSig').val('');
	$('#agrSig').val('');
	$('#zonaSig').val('');
	$('#polSig').val('');
	$('#parcSig').val('');
	$('#recSig').val('');
	$('#tx_pol').val('');
	$('#tx_par').val('');
	$('#provincia').val('');
	$('#comarca').val('');
	$('#termino').val('');
	$('#subtermino').val('');
	$('#cultivo').val('');
	$('#variedad').val('');
	$('#desc_provincia').val('');
	$('#desc_comarca').val('');
	$('#desc_termino').val('');
	$('#desc_cultivo').val('');
	$('#desc_variedad').val('');
	$('#capital').val('');
	$('#desc_capital').val('');
	consultar();
}

function volver(){
	$('#idSiniestroVolver').val($('#idSiniestro').val());
	$('#idPolizaVolver').val($('#idPoliza').val());
	$('#formVolver').submit();
}

//Función para guardar las modificaciones que se van realizando en el listado de parcelas del siniestro
function aplicarCambios(){
	if($('#main3').valid()){
		if ($('#frutosCM').is(':checked')){
			$('#valorFrutosCM').val('S');
		}
		else{
			if ($('#inputFechaRec').val() != ''){
				$('#valorFrutosCM').val('N');
			}
			else{
				$('#valorFrutosCM').val('');
			}
		}
		$('#method').val('doAplicarCambios');
		$('#main3').submit();
	}
}

//Función para dejar el siniestro en definitivo
function PasarDefinitiva(){
	if($('#main3').valid()){
		$('#method').val('doAlta');
		$('#idSin').val($('#idSiniestro').val());
		$('#idPol').val($('#idPoliza').val());
		$('#pasarADefinitiva').val("true");
		$.blockUI.defaults.message = '<h4> Validando el siniestro.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	    $.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
		$('#main3').submit();
	}
}

//Guardar
function alta(){	
	if($('#main3').valid()){
		$('#method').val('doAlta');
		$('#idSin').val($('#idSiniestro').val());
		$('#idPol').val($('#idPoliza').val());
		$.blockUI.defaults.message = '<h4> Grabando los datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	    $.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
		$('#main3').submit();
	}
}

//funcion que valida un elemento independientemente, si no jquery no lo hace
function validarElemento(elemento){
	$("#main3").validate().element(elemento);  
}

// popup aviso
function showPopUpAviso(mensaje){
	$('#txt_mensaje_aviso').html(mensaje);
	$('#popUpAvisos').show();
	$('#overlay').show();
}
 
function hidePopUpPanelAvisos(){
	$('#popUpAvisos').hide();
	$('#overlay').hide();
}
	
//-------------------------------------------------------------------------
// INICIO Funciones del check del listado
//-------------------------------------------------------------------------
function onClickInCheck2(idCheck){
	if(idCheck){
		var __is_checked = false;
		var __aux = idCheck.split("_");
		var __ids = $('#idsRowsChecked').val();
		
		if (document.getElementById(idCheck).checked == true){
			addCheck2(__ids, __aux[1]);
		}else{
			subtractCheck2(__ids, __aux[1]);
		}
		$('#sel').text(numero_check_seleccionados2());
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
	$('#checkTodos').attr('checked',false);
	$('#marcaTodo').val(false);
}

//Función para marcar/desmarcar TODOS los checks.
function clickCheckTodos(checkear){
	var str = $('#listaIdsCap').val();
	
	$("input[type=checkbox]").each(function() {
		if($(this).attr('id').indexOf('checkParcela_')!= -1){
			$(this).attr('checked', checkear);
		}
	});
	
	$('#marcaTodo').val(checkear);
	
	if (checkear)
		$('#idsRowsChecked').val(str);
	else
		$('#idsRowsChecked').val('');
		
	$('#sel').text(numero_check_seleccionados2());
}

//Función para marcar los checks que estén seleccionados.
function check_checks(ids){
	if(ids.length >0){
		var array_ids = ids.split(';');
		for(var i = 0; i < array_ids.length; i++){
			var idCheck = array_ids[i];
			if (idCheck!=""){
				$('#checkParcela_' + idCheck).attr('checked',true);
			}
		}
	}
}

function numero_check_seleccionados2(){
	return num_checks_checked_in_list($('#idsRowsChecked').val());	    
}

function num_checks_checked_in_list(list){
	var count = 0;
	
	var array_ids = list.split(';');
   
	for(var i = 0; i < array_ids.length; i++){
		if(array_ids[i] != item && array_ids[i] != "" && array_ids[i] != null && array_ids[i] != undefined){
		      count++;
		}
	}

	return count;
}

//-------------------------------------------------------------------------
// FIN Funciones del check del listado
//-------------------------------------------------------------------------
