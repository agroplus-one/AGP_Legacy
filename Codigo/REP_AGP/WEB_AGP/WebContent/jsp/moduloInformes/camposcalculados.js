$(document).ready(function(){
			
	
    var URL = UTIL.antiCacheRand(document.getElementById("main").action);
    document.getElementById("main").action = URL;
    
	$('#main').validate({					
		
		onfocusout: function(element) {
			if ( ($('#method').val() == "btnModificar") || ($('#method').val() == "doAlta") ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		
		rules: {
			"nombre":{required: true},
	 		"camposPermitidosByIdoperando1.id":{required: true},
	 		"idoperador":{required: true},
	 		"camposPermitidosByIdoperando2.id":{required: true} 						 		
		},
		messages: {
			"nombre":{required: "El campo Nombre es obligatorio"},
			"camposPermitidosByIdoperando1.id":{required: "El campo Operando 1 es obligatorio"},
	 		"idoperador":{required: "El campo Operador  es obligatorio"},
	 		"camposPermitidosByIdoperando2.id":{required: "El campo Operando 2 es obligatorio"}
	 	}
	});
			
});
 

/**
 * Limpia el formulario y muestra todos los campos calculados existentes
 */ 	
function limpiar(){
	
	limpiaAlertas();
	$('#btnAlta').show();
	$('#btnModificar').hide();
	$("#id").val('');
	$("#nombre").val('');
	$("#camposPermitidosByIdoperando1").val('');
	$("#camposPermitidosByIdoperando2").val('');
	$("#idoperador").val(''); 
	jQuery.jmesa.removeAllFiltersFromLimit('consultaCamposCalculados');
	onInvokeAction('consultaCamposCalculados','clear');	
}			
 
/**
 * Muestra los datos del campo calculado en el formulario y los botones de 'Alta' y 'Modificar'
 */
function editar(id,nombre,operando1,operando2,operador,formato,decimales ){
 	
 	visualizarCampo (id,nombre,operando1,operando2,operador);
 	
	$('#btnAlta').show();
 	$('#btnModificar').show();
}
 
 
/**
 * Muestra los datos del campo calculado en el formulario y oculta los botones de 'Alta' y 'Modificar'
 */
function visualizar(id,nombre,operando1,operando2,operador){
  	
 	visualizarCampo (id,nombre,operando1,operando2,operador);
 	
 	$('#btnModificar').hide();
 	$('#btnAlta').hide();
}

/**
 * Limpia las alertas anteriores y muestra los datos del campo calculado en el formulario
 */
function visualizarCampo (id,nombre,operando1,operando2,operador) {
	limpiaAlertas();
 	$("#nombre").val(nombre);
	$("#camposPermitidosByIdoperando1").val(operando1);
	$("#camposPermitidosByIdoperando2").val(operando2);
	$("#camposPermitidosByIdoperando1").change();
	$("#camposPermitidosByIdoperando2").change();
	$("#idoperador").val(operador); 
 	$("#id").val(id);
}
 
 
 
/**
 * Realiza la llamada ajax al controller de jmesa
 */
function onInvokeAction(id) {
				
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main');
    $.get('mtoCamposCalculados.run?ajax=true&' + parameterString, function(data) {
        $("#grid").html(data)
	});
}
 

/**
 * Borra el campo calculado correspondiente al id pasado como parámetro
 */
function borrar(id){
	
	limpiaAlertas();
	$('#main').validate().cancelSubmit = true;
	$('#origenLlamada').val('borrar');
	if(confirm('¿Está seguro de que desea eliminar este campo calculado?')){
		var frm = document.getElementById('main');
		frm.method.value = 'doBaja';			
		$("#id").val(id);	
		$("#main").submit();	
	}	
}
 

/**
 * Añade al filtro de búsqueda los campos que se hayan informado en el formulario
 */ 
function comprobarCampos(){
 	
 	jQuery.jmesa.removeAllFiltersFromLimit('consultaCamposCalculados');
  
   	if ($('#nombre').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCamposCalculados','nombre', $('#nombre').val());
   	}
   	if ($('#camposPermitidosByIdoperando1').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCamposCalculados','camposPermitidosByIdoperando1.id', $('#camposPermitidosByIdoperando1').val());
   	}
   	if ($('#camposPermitidosByIdoperando2').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCamposCalculados','camposPermitidosByIdoperando2.id', $('#camposPermitidosByIdoperando2').val());
   	}       
   	if ($('#idoperador').val() != ''){
   		jQuery.jmesa.addFilterToLimit('consultaCamposCalculados','idoperador', $('#idoperador').val());
   	} 
}
	       	

/**
 * Busca los campos calculados que se ajusten al filtro de búsqueda introducido
 */	       	
function consultar(){
	limpiaAlertas();
	$('#btnModificar').hide();
	comprobarCampos();
	onInvokeAction('consultaCamposCalculados','filter');
}   
		

/**
 * Alta del campo calculado
 */		
function alta(){
	limpiaAlertas();
	var frm = document.getElementById('main');
	frm.method.value = 'doAlta';
	$('#id').val('');
	$('#main').submit();
}

/**
 * Modificación de campo calculado
 */		
function modificar(){
	limpiaAlertas();
	var frm = document.getElementById('main');
	frm.method.value = 'doModificacion';
	$('#main').submit();
}

/**
 * Limpia las alertas y mensajes
 */
function limpiaAlertas(){
 	$("#panelInformacion").val('');
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").val('');
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").val('');
	$("#panelAlertas").hide();
}

/**
 * Muestra el botón de 'Modificar' si el campo calculado está cargado
 */
function onLoadCalculado(){
	if ($("#id").val() != "") $('#btnModificar').show();	
	else $('#btnModificar').hide();
}

