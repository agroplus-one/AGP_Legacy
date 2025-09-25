var arrayOpTodos = new Array ();
var arrayCodOpTodos = new Array ();
var arrayOpNum = new Array ();
var arrayCodOpNum = new Array ();

function guardarOperador (codigo, descripcion, tipoNumerico) {
	
	// Introduce la información del operador en el array general
	arrayCodOpTodos.push(codigo);
	arrayOpTodos.push(descripcion);
	
	// Si el operador es de tipo numérico, lo introduce en el array de operadores numéricos
	if (tipoNumerico == true) {
		arrayCodOpNum.push(codigo);
		arrayOpNum.push(descripcion);
	}
}

function comprobarCampos(){
	jQuery.jmesa.removeAllFiltersFromLimit('opGenerico');
	 
 	if ($('#tablaOrigen').val() != '') 	{
 		jQuery.jmesa.addFilterToLimit('opGenerico','id.idvista', $('#tablaOrigen').val());
 	}
	if ($('#campo').val() != '') {
		jQuery.jmesa.addFilterToLimit('opGenerico','id.nombrecampo', $('#campo').val());
	}
	if ($('#operador').val() != '') {
		jQuery.jmesa.addFilterToLimit('opGenerico','id.operador', $('#operador').val()); 
	}
}

$(document).ready(function(){

	if (($('#idOpCamposPermitido').val() != null && $('#idOpCamposPermitido').val() != '')
	|| (($('#idOpCalculado').val() != null && $('#idOpCalculado').val() != ''))){
		var modif = document.getElementById('btnModificar');
		modif.style.display = "";
	}
	var URL = UTIL.antiCacheRand(document.getElementById("main").action);
	document.getElementById("main").action = URL;
	$('#tablaOrigen').val($('#tablaOrigenStr').val());
	habilitaOperadores ();
	actualizaFiltros();
	$('#main').validate({	
		
		onfocusout: function(element) {
			var frm = document.getElementById('main');
			if ( (frm.method.value == "doEdita") || (frm.method.value == "doAlta") ) {
				this.element(element);
			}
		},
								
		errorLabelContainer: "#panelAlertasValidacion",
				wrapper: "li",
		
		rules: {
			"tablaOrigen":{required: true},
			"campo":{required: true},
			"operador":{required: true}
			
		},
		messages: {
			"tablaOrigen":{required: "El campo Tabla Origen es obligatorio"},
			"campo":{required: "El campo Campo es obligatorio"},
			"operador":{required: "El campo Operador es obligatorio"}
	 	}
	});
});		


function actualizaFiltros(){
	$('#tablaOrigen').val($('#tablaOrigenStr').val());
	$('#campo').val($('#campoStr').val());
	$('#operador').val($('#operadorStr').val());
}

function grabarFiltros(){
	$('#tablaOrigenFiltroP').val($('#tablaOrigen').val());
	$('#campoFiltroP').val($('#campo').val());
	$('#operadorFiltroP').val($('#operador').val());
	var frmC = document.getElementById('opCamposCalculadosForm');
	frmC.tablaOrigenFiltroC. value = $('#tablaOrigen').val();
	frmC.campoFiltroC.value = $('#campo').val();
	frmC.operadorFiltroC.value = $('#operador').val();
}

function onInvokeAction(id) {
	limpiaAlertas();
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
    $.jmesa.setExportToLimit(id, '');
    var parameterString = $.jmesa.createParameterStringForLimit(id);
    var frm = document.getElementById('main');
    $.get('mtoOperadoresCampos.run?ajax=true&origenLlamada='+frm.origenLlamada.value+'&' + parameterString, function(data) {
        $("#grid").html(data)
 			});
}

function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();	
	$("#panelAlertas").hide();
}

function limpiar(){
	//$('#main').validate().cancelSubmit = true;
	limpiaAlertas();
	var frm = document.getElementById('main');
	frm.origenLlamada.value = "menuGeneral";
	$("#btnModificar").hide();	
	$('#tablaOrigen').val('');
	$('#campo').val('');
	$('#operador').val('');
	var modif = document.getElementById('btnAlta');
	modif.style.display = "";
	var modif = document.getElementById('btnModificar');
	modif.style.display = "none";
	consultar();
	habilitaOperadores();
}

function consultar(){
	var frm = document.getElementById('main');
	frm.origenLlamada.value = '';
	var modif = document.getElementById('btnModificar');
	modif.style.display = "none";
	limpiaAlertas();
	comprobarCampos();
	onInvokeAction('opGenerico','filter');
 	
}

function alta(){
	var frm = document.getElementById('main');
	frm.origenLlamada.value = '';
	limpiaAlertas();
	if ($('#main').valid()){
		var frm = document.getElementById('main');
	    if (frm.isCalcOPermMain3.value == "1"){ // campCalc
			var frm2 = document.getElementById('opCamposCalculadosForm');
			frm2.method.value = 'doAlta';
			actualizaCampos();
			grabarFiltros();
			$('#main').valid();
			$('#opCamposCalculadosForm').submit()
		}else{ // campo Permitido
			var frm2 = document.getElementById('opCamposPermitidosForm');
			frm2.method.value = 'doAlta';
			actualizaCampos();
			grabarFiltros();
			$('#opCamposPermitidosForm').submit();
		}
    }
}

function actualizaCampos(){
	var frm = document.getElementById('main');
	var frmP = document.getElementById('opCamposPermitidosForm');
	var frmC = document.getElementById('opCamposCalculadosForm');
	frmP.idOperador.value = frm.operador.value;
	frmC.idOperador.value = frm.operador.value;
}

function modificar(){
	var frm = document.getElementById('main');
	frm.origenLlamada.value = '';
	limpiaAlertas();
	if ($('#main').valid()){
		var frm = document.getElementById('main');
	    if (frm.isCalcOPermMain3.value == "1"){ // campCalc
			var frm2 = document.getElementById('opCamposCalculadosForm');
			frm2.method.value = 'doEdita';
			actualizaCampos();
			grabarFiltros();
			$('#opCamposCalculadosForm').submit();
		}else{ // campo Permitido
			var frm2 = document.getElementById('opCamposPermitidosForm');
			frm2.method.value = 'doEdita';
			actualizaCampos();
			grabarFiltros();
			$('#opCamposPermitidosForm').submit();
		}
	}
}	

function borrar(id, isCalcOPerm, idCalcOPerm){
	var frmTemp = document.getElementById('main');
	frmTemp.origenLlamada.value = '';
	$('#main').validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar este Operador?')){
		$.blockUI.defaults.message = '<h4> Eliminando registro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
       	if (isCalcOPerm == "1"){ // campCalc
       		var frm = document.getElementById('opCamposCalculadosForm');
			frm.method.value = 'doBaja';
			frm.idOpCalculado.value = idCalcOPerm;
       		$('#idOpCamposCalculados').val(idCalcOPerm);
       		grabarFiltros();
       		$("#opCamposCalculadosForm").submit();
       	}else{ // campPerm
       		var frm = document.getElementById('opCamposPermitidosForm');
			frm.method.value = 'doBaja';
			frm.idOpCamposPermitido.value = idCalcOPerm;
			grabarFiltros();
			$("#opCamposPermitidosForm").submit();
		}	
	}	
}
		        
function editar(idOpGenerico, isCalcOPerm, idOpCalcOPerm, idCampo, nombreCampo, idVista, tablaOrigen, idOperador){
	limpiaAlertas();
	var frm = document.getElementById('main');
	frm.isCalcOPermMain3.value = isCalcOPerm
	if (isCalcOPerm == "1"){ // campCalc
		var frm = document.getElementById('opCamposCalculadosForm');
		frm.idOpCalculado.value = idOpCalcOPerm;
		frm.idcampoCalc.value = idCampo;
   		frm.idOperador.value = idOperador;
   	}else{ // campPerm
   		var frm = document.getElementById('opCamposPermitidosForm');
   		frm.idOpCamposPermitido.value = idOpCalcOPerm;
   		frm.idVistaC.value = idCampo;
   		frm.idOperador.value = idOperador;
	}	
	$('#tablaOrigen').val(idVista);
	habilitaOperadores();
	$('#campo').val(nombreCampo);
	$("#operador").val(idOperador);
	
	var modif = document.getElementById('btnModificar');
	modif.style.display = "";
	var modif = document.getElementById('btnAlta');
	modif.style.display = "";
}

function visualizar(idOpGenerico, isCalcOPerm, idOpCalcOPerm, idCampo, nombreCampo, idVista, tablaOrigen, idOperador){
	limpiaAlertas();
	var frm = document.getElementById('main');
	frm.isCalcOPermMain3.value = isCalcOPerm
	if (isCalcOPerm == "1"){ // campCalc
		var frm = document.getElementById('opCamposCalculadosForm');
		frm.idOpCalculado.value = idOpCalcOPerm;
		frm.idcampoCalc.value = idCampo;
   		frm.idOperador.value = idOperador;
   	}else{ // campPerm
   		var frm = document.getElementById('opCamposPermitidosForm');
   		frm.idOpCamposPermitido.value = idOpCalcOPerm;
   		frm.idVistaC.value = idCampo;
   		frm.idOperador.value = idOperador;
	}	
	$('#tablaOrigen').val(idVista);
	habilitaOperadores();
	$('#campo').val(nombreCampo);
	$("#operador").val(idOperador);
	
	var modif = document.getElementById('btnModificar');
	modif.style.display = "none";
	var modif = document.getElementById('btnAlta');
	modif.style.display = "none";
}

function vaciaCampo(){
	$('#campo').val('');
	if ($('#tablaOrigen').val() == '0'){
		$("#isCalcOPermMain3").val('1');
	}else if ($('#tablaOrigen').val() != ''){
		$("#isCalcOPermMain3").val('2');
	}else{
		$("#isCalcOPermMain3").val('');
	}
	var modif = document.getElementById('btnAlta');
	modif.style.display = "";
}

function actualizaTipo(){
	var frm = document.getElementById('opCamposPermitidosForm');
	var frm2 = document.getElementById('main');
	frm.idVistaC.value = frm2.idVistaCampo.value;
}

//Función para mostrar la lupa en función de si se ha seleccionado "Campos calculados" o no.
function displayFieldList(){
	if ($('#tablaOrigen').val() == ''){
		alert("Seleccione primero una tabla Origen");
	}else if ($('#tablaOrigen').val() == '0'){
		//campos calculados
		lupas.muestraTabla('CamposCalculados','principio', '', '');
	}
	else{
		//vista campos
		lupas.muestraTabla('VistaCampo','principio', '', '');
	}
}


// Dependiendo de la opción seleccionada en el combo de tablas se cargará unos datos u otros en el combo de operadores
function habilitaOperadores () {
	
	var frm = document.getElementById('main');
	var combo = frm.operador.options;
	combo.length = 0;
	combo[0] = new Option("Todos","");
		
	// Si no es 0 el value se cargan todos los operadores
	if ($('#tablaOrigen').val() != '0') {
		for(var i=1;i<arrayCodOpTodos.length+1;i++){
			combo[i] = new Option(arrayOpTodos[i-1],arrayCodOpTodos[i-1]);
		}
	}
	// Si el value es 0 se ha seleccionado la tabla de campos calculados, por lo que solo se mostrarán los operadores numérico
	else {
		for(var i=1;i<arrayCodOpNum.length+1;i++){
			combo[i] = new Option(arrayOpNum[i-1],arrayCodOpNum[i-1]);
		}
	}
}

