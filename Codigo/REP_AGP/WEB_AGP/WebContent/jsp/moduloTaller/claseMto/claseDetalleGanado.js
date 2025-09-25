$(document).ready(function(){
			
    var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    document.getElementById("main3").action = URL;
    
    if ($('#id').val() != null && $('#id').val() != ''){
		$('#btnModificar').show();
	}
	$('#modulo').focus();
 
	$('#main3').validate({					
		
		onfocusout: function(element) {
			if ( ($('#method').val() == "doEditar") || ($('#method').val() == "doAlta") ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",			
		wrapper : "li", highlight : function(element,errorClass) 
		{
			$("#campoObligatorio_" + element.id).show();
		},
	unhighlight : function(element,errorClass) 
		{
			$("#campoObligatorio_" + element.id).hide();
		},
		
		
		rules: {
			"codmodulo":{required: true},
		 	"codespecie":{required: true, digits: true},
		 	"codregimen":{required: true, digits: true},
		 	"codgruporaza":{required: true, digits: true},
		 	"codtipoanimal":{required: true, digits: true},		 	
		 	"codtipocapital":{required: true, digits: true},
		 	"codprovincia":{required: true, digits: true},
		 	"codcomarca":{required: true, digits: true},
		 	"codtermino":{required: true, digits: true},		 	
		 	"subtermino":{subterminoOK: true}
		},
		messages: {
			"codmodulo":{required: "El campo Módulo es obligatorio "},
		 	"codespecie":{required: "El campo Especie es obligatorio ", digits: "El campo Especie sólo puede contener dígitos"},
		 	"codregimen":{required: "El campo Régimen es obligatorio ", digits: "El campo Régimen sólo puede contener dígitos"},
		 	"codgruporaza":{required: "El campo Grupo de Raza es obligatorio ", digits: "El campo Grupo de raza sólo puede contener dígitos"},
		 	"codtipoanimal":{required: "El campo Tipo de Animal es obligatorio ", digits: "El campo Tipo de Animal sólo puede contener dígitos"},		 	
		 	"codtipocapital":{required: "El campo Tipo de capital es obligatorio ", digits: "El campo Tipo de capital sólo puede contener dígitos"},
		 	"codprovincia":{required: "El campo Provincia es obligatorio", digits: "El campo Provincia sólo puede contener dígitos"},
	 		"codcomarca":{required: "El campo Comarca es obligatorio", digits: "El campo Comarca sólo puede contener dígitos"},
	 		"codtermino":{required: "El campo Término es obligatorio ", digits: "El campo Término sólo puede contener dígitos"},
		 	"subtermino":{subterminoOK: "El campo Subtérmino sólo puede contener una letra, un número o un espacio en blanco"}
		}
	});
	
	jQuery.validator.addMethod("subterminoOK", function(value, element, params) {
		 		return ($('#subtermino').val().match(/^[\w ]+$/) != null);	
		 	}
	);
	
	
	$('#frmImportar').validate({			
		
		errorLabelContainer: "#panelAlertasValidacionFile",
		wrapper: "li",	  				 
		rules: {
			"file":{required: true, valExtensionCsv:true}
		},
		messages: {
			"file":{required: "Debe seleccionar un archivo", valExtensionCsv:"El archivo no tiene una extensión válida"}
		}  
	});

	jQuery.validator.addMethod("valExtensionCsv", function(value, element, params) {
		var name = $('#file').val();
		name = name.split('\\');
		var aux1 =(name[name.length-1]).substring((name[name.length-1].length - 3),(name[name.length-1].length) );
		if ((aux1 == 'CSV')||(aux1 == 'csv')){
			return true;
		}else{
			return false;
		}
	});
	
	$('#main').validate({			
		
		errorLabelContainer: "#panelAlertasValidacionCambio",
		wrapper: "li",	  				 
		rules: {			
			"codprovincia":{required: true, digits: true},
		 	"codcomarca":{required: true, digits: true},
		 	"codtermino":{required: true, digits: true},
		 	"codtipocapital":{required: true, digits: true},
		 	"subtermino":{subtermino_cmOK: true}	 	
		},
		messages: {
			"codprovincia":{required: "El campo Provincia es obligatorio", digits: "El campo Provincia sólo puede contener dígitos"},
	 		"codcomarca":{required: "El campo Comarca es obligatorio", digits: "El campo Comarca sólo puede contener dígitos"},
		 	"codtermino":{required: "El campo Término es obligatorio", digits: "El campo Término sólo puede contener dígitos"},
		 	"codtipocapital":{required: "El campo Tipo de capital es obligatorio", digits: "El campo Tipo de capital sólo puede contener dígitos"},
		 	"subtermino":{subtermino_cmOK: "El campo Subtérmino debe contener una letra, un número o un espacio en blanco"}
		}  
	});
	jQuery.validator.addMethod("subtermino_cmOK", function(value, element, params) {
		
		var valido = false;
		if($('#subtermino_cm').val()!=''){
			valido = ($('#subtermino_cm').val().match(/^[\w ]+$/) != null);
		}
 		return valido;	
 	}
	);
});


		


function consultar(){

	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	$("#vieneDeConsultar").val('true');
		
	//DAA 08/02/2013
	$('#marcaTodos').val('false');
	$('#listaIdsMarcados').val('');
	$('#origenLlamada').val('Consultar')
	$("input[type=checkbox]").each(function(){
		$(this).attr('checked',false);
	});
	
	
	//Llamamos al mï¿½todo comprobarCampos para aï¿½adir los valores al "limit"
	comprobarCampos();
	onInvokeAction('consultaClaseDetalleGanado','filter');	
}


function comprobarCampos(){
	jQuery.jmesa.removeAllFiltersFromLimit('consultaClaseDetalleGanado');
   	var resultado = false;
   	
   	if ($('#detalleid').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','clase.id', $('#detalleid').val());
   		resultado = true;   		
   	}
   	
   	if ($('#modulo').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','codmodulo', $('#modulo').val());
   		resultado = true;
   	}
 	if ($('#provincia').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','codprovincia', $('#provincia').val());
   		resultado = true;
   	}
   	if ($('#comarca').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','codcomarca', $('#comarca').val());
   		resultado = true;
   	}
   	if ($('#termino').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','codtermino', $('#termino').val());
   		resultado = true;
   	}
   	if ($('#subtermino').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','subtermino', $('#subtermino').val());
   		resultado = true;
   	}
   	
 	if ($('#especie').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','codespecie', $('#especie').val());
   		resultado = true;
   	}
 	if ($('#regimen').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','codregimen', $('#regimen').val());
   		resultado = true;
   	}
 	if ($('#codgrupoRaza').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','codgruporaza', $('#codgrupoRaza').val());
   		resultado = true;
   	}
 	if ($('#codtipoanimal').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','codtipoanimal', $('#codtipoanimal').val());
   		resultado = true;
   	}
 	if ($('#codtipocapital').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaClaseDetalleGanado','codtipocapital', $('#codtipocapital').val());
   		resultado = true;
   	}
 	
   	return resultado;
}


function limpiar(){
	$('#modulo').val('');
	$('#especie').val('');
	$('#desc_especie').val('');
	$('#regimen').val('');
	$('#desc_regimen').val('');
	$('#codgrupoRaza').val('');
	$('#desGrupoRaza').val('');
	$('#codtipoanimal').val('');
	$('#desTipoAnimal').val('');
	$('#codtipocapital').val('');
	$('#desTipoCapital').val('');
	$('#provincia').val('');
	$('#desc_provincia').val('');
	$('#comarca').val('');
	$('#desc_comarca').val('');
	$('#termino').val('');
	$('#desc_termino').val('');
	$('#subtermino').val('');
	
	/*consultar();*/
	consultarLimpiar();
}

function consultarLimpiar(){
	//Hacemos la funcion consultar() pero sin origen llamada para que limpie los filtros
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	$("#vieneDeConsultar").val('true');
		
	//DAA 08/02/2013
	$('#marcaTodos').val('false');
	$('#listaIdsMarcados').val('');
	$('#origenLlamada').val('')
	$("input[type=checkbox]").each(function(){
		$(this).attr('checked',false);
	});

	//Llamamos al metodo comprobarCampos para anadir los valores al "limit"
	comprobarCampos();
	onInvokeAction('consultaClaseDetalleGanado','filter');
}

function modificar(){
	$("#method").val("doModificar");
	$("#origenLlamada").val("doModificar");
	$("#main3").submit();	
	
}

function borrar(id) {
	$('#main3').validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar el detalle?')){		
		$("#origenLlamada").val("doBorrar");
		$("#method").val("doBorrar");
		$("#id").val(id);
		$("#main3").submit();
	}	
}



function alta() {
	//comprobarCampos();
	$("#method").val("doAlta");
	$("#origenLlamada").val("doAlta");
	$("#main3").submit();	
}

function cambioMasivo(){
	limpiarCambioMasivo();
	$('#main').validate().cancelSubmit = true;
	var listaIdsMarcados = $('#listaIdsMarcados').val();
	
	if(listaIdsMarcados.length>0){
		$('#listaIdsMarcados_cm').val(listaIdsMarcados);
		$('#overlayCambioMasivo').show();
		$('#panelCambioMasivoClaseDetalleGanado').show();
		$('#overlay').show();
	}
	else{
		 showPopUpAviso("Debe seleccionar como mínimo un detalle.");
		 $('#overlay').show();
	}	
}

function editar(id, idClase, lineaSeguroId, codProvincia, codComarca, codTermino, codSubtermino, codModulo, 
		codEspecie, descEspecie, codRegimen, descRegimen, codGrupoRaza, descGrupoRaza,  codTipoAnimal, 
		descTipoAnimal, codTipoCapital, descTipoCapital, descProvincia, descComarca, descTermino){
	
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
	
	
	var frm = document.getElementById('main3');
	frm.target="";
	frm.id.value=id;
	frm.provincia.value = codProvincia;
	frm.comarca.value = codComarca;
	frm.termino.value = codTermino;
	frm.subtermino.value = codSubtermino;
	frm.modulo.value=codModulo;
	frm.especie.value=codEspecie;
	frm.desc_especie.value=descEspecie;
	frm.regimen.value=codRegimen;
	frm.desc_regimen.value=descRegimen;
	frm.codgrupoRaza.value=codGrupoRaza;
	frm.desGrupoRaza.value=descGrupoRaza;
	frm.codtipoanimal.value=codTipoAnimal;
	frm.desTipoAnimal.value=descTipoAnimal
	frm.codtipocapital.value=codTipoCapital;
	frm.desTipoCapital.value=descTipoCapital;
	frm.desc_provincia.value=descProvincia;
	frm.desc_comarca.value=descComarca;
	frm.desc_termino.value=descTermino;
		
	$('#btnModificar').show();
}

function borrar(id) {
	$('#main3').validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar el detalle?')){
		$("#origenLlamada").val("doBorrar");
		$("#method").val("doBorrar");
		$("#id").val(id);
		$("#main3").submit();
	}	
}


function listaCheckId(id){
	var listaIdsMarcados = "";
	var listaFinalIds = "";
	var cadena=[];
	
	if($('#check_' + id).attr('checked')==true){
		listaIdsMarcados = $('#listaIdsMarcados').val() + id +",";
		$('#listaIdsMarcados').val(listaIdsMarcados);
	}else{
		listaIdsMarcados = $('#listaIdsMarcados').val();
		cadena= listaIdsMarcados.split(",");
		
		for (var i=0;i<cadena.length -1;i++){
			if(cadena[i]!=id){
				listaFinalIds = listaFinalIds + cadena[i] + ",";
			}		
		}
		$('#listaIdsMarcados').val(listaFinalIds);
		$('#marcaTodos').val('false');
		comprobarChecks();	
	}
}

function marcarTodos(){
	if($('#checkTodos').attr('checked')==true){
		var listaIdsTodos = $("#listaIdsTodos").val();
		$('#listaIdsMarcados').val(listaIdsTodos);
		$('#marcaTodos').val('true');
		comprobarChecks();	
	}
	else{
		$('#listaIdsMarcados').val('');
		$('#marcaTodos').val('false');
		$("input[type=checkbox]").each(function(){
			$(this).attr('checked',false);
		});
	}	
		
}
   
function comprobarChecks(){

	var listaIdsMarcados = $('#listaIdsMarcados').val();
	var cadena=[];
	cadena= listaIdsMarcados.split(",");
	
	if (listaIdsMarcados.length>0){
		$("input[type=checkbox]").each(function(){
			if( $("#marcaTodos").val() == "true" ){
				if($(this).attr('id') != "checkTodos"){
					$(this).attr('checked',true);
				}
			}
			else{
				for (var i=0;i<cadena.length -1;i++){
					var idcheck = "check_" + cadena[i];
					if($(this).attr('id') == idcheck){
						$(this).attr('checked',true);
					}			
				}	
			}
		});
	}
	if($('#marcaTodos').val()=="true"){
		$('#checkTodos').attr('checked',true);
	}else{
		$('#checkTodos').attr('checked',false);
	}
}	


function onInvokeAction(id) {
	
	//comprobarCampos();
	
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main3');
    $.get('claseDetalleGanado.run?origenLlamada='+$('#origenLlamada').val()+'&vieneDeConsultar='+$('#vieneDeConsultar').val()+'&vieneDeCargaClases='+$('#vieneDeCargaClases').val()+'&detalleid='+$('#detalleid').val()+ '&ajax=true&' + decodeURIComponent(parameterString), function(data) {
        $("#grid").html(data);
        comprobarChecks();
		});
}



//funciones del popup de importación ----------------------------------------------------------------------------------------------------------------
function importar(){
	limpiaPanelAlertasValidacionFile();
	$('#frmImportar').validate().cancelSubmit = true;
	showPopUpImportar();
}

function limpiaPanelAlertasValidacionFile(){
	$("#file").val('');
	$("#panelAlertasValidacionFile").hide();
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
}

function showPopUpImportar(){
	$('#divImportar').fadeIn('normal');
	$('#overlay').show();
}

function cerrarPopUpImportar(){
	$('#divImportar').fadeOut('normal');
	$('#overlay').hide();
}
function doImportar(){
	limpiaPanelAlertasValidacionFile();	
	if ($('#frmImportar').valid()){
		cerrarPopUpImportar();
		$.blockUI.defaults.message = '<h4> Importando archivo de clases de detalle de ganado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#frmImportar').submit();
	}
}


// -----------------------------------------------------------------------------------------------------------------------------------


