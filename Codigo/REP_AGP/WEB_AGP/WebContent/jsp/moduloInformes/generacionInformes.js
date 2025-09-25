$(document).ready(function(){
	var URL = UTIL.antiCacheRand(document.getElementById("main").action);
    document.getElementById("main").action = URL;
});

function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main');
    						
    $.get('generacionInforme.run?ajax=true&origenLlamada='+frm.origenLlamada.value+'&cadenaUsuarios='+frm.cadenaUsuarios.value+'&' + parameterString, function(data) {
    $("#grid").html(data)
		});
}

function consultar(){
	$('#origenLlamada').val('');
	jQuery.jmesa.addFilterToLimit('generacionInformes','nombre', $('#nombre').val());
	limpiaAlertas();
	comprobarCampos();
	var selObj = document.getElementById('multUsers');
	onInvokeAction('generacionInformes','filter');
 }
 
 function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
}

function limpiar(){
	$('#sqlArea').val('');
	limpiaAlertas();
	$("#nombre").val('');	
	$('#titulo1').val('');
	$('#titulo2').val('');
	$('#titulo3').val('');
	$('#codusuario').val('');
	$('#origenLlamada').val('menuGeneral');
	var frm = document.getElementById('main');
	frm.origenLlamada.value = "menuGeneral";
	consultar();								
 }
 
 function comprobarCampos(){
	jQuery.jmesa.removeAllFiltersFromLimit('generacionInformes');
 	var resultado = false;
	
 	if ($('#nombre').val() != ''){
   		jQuery.jmesa.addFilterToLimit('generacionInformes','nombre', $('#nombre').val());
   		//alert("nombre");
   		resultado = true;
 	}       	
 	if ($('#titulo1').val() != ''){
   		jQuery.jmesa.addFilterToLimit('generacionInformes','titulo1', $('#titulo1').val());
   		//alert("titulo1");
   		resultado = true;
 	}
 	if ($('#titulo2').val() != ''){
		jQuery.jmesa.addFilterToLimit('generacionInformes','titulo2', $('#titulo2').val());
		//alert("titulo2");
 		resultado = true;
 	}
 	if ($('#titulo3').val() != ''){
		jQuery.jmesa.addFilterToLimit('generacionInformes','titulo3', $('#titulo3').val());
		//alert("titulo3");
 		resultado = true;
 	} 
 	if ($('#codusuario').val() != ''){
		jQuery.jmesa.addFilterToLimit('generacionInformes','usuario.codusuario', $('#codusuario').val());
		resultado = true;
 	} 		
 	return resultado;
}

function generar2(id){
	limpiaAlertas();
    var frm = document.getElementById('main');
	frm.idInforme.value=id;
    frm.method.value = 'doGenerar';
    $('#main').attr('target', '_blank');
 	$('#main').submit();
}