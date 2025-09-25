$(document).ready(function(){
	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
	document.getElementById("main3").action = URL;
	
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
        inputField        : "condiciontxt",
        button            : "btn_fecha",
        ifFormat          : "%d/%m/%Y",
        daFormat          : "%d/%m/%Y",
        align             : "Br"			        	        
  	});
	
	$('#main3').validate({
		onfocusout: function(element) {
			if ( ($('#method').val() == "modificar") || ($('#method').val() == "doAlta") ) {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		rules: {
			datoinformeid:{required: true},
			listaOperador:{required: true},
			condicion:{required: true}
		},
		messages: {
			datoinformeid:{required: "El campo Columna es obligatorio"},
			listaOperador:{required: "El campo Operador  es obligatorio"},
			condicion:{required: "El campo Valor es obligatorio"}
		}
	});
});

/**
 * Carga el listado de operadores permitidos para el campo seleccionado
 */
function cargarSelecOperador(){

	$("#listaOperador").empty();
	var idCampo = $("#listaCampo").val();
	var lstDatos = idCampo.split('-');
	
	var datoInformeId = $("#datoInformesId").val();
	var permitidOcalculado = $("#permitidocalculado").val() ;
	if(idCampo != ''){
		$.ajax({
			url: "mtoCondicionCampos.run",
			data: "method=ajax_getOperador&datoInfoId=" + datoInformeId+"&permOcal="+permitidOcalculado,
			async:false,
			beforeSend: function(objeto){
				document.getElementById("ajaxLoading").style.display = '';
			},
			complete: function(objeto, exito){
				document.getElementById("ajaxLoading").style.display = 'none';
			},
			contentType: "application/x-www-form-urlencoded",
			dataType: "text",
			error: function(objeto, quepaso, otroobj){
				alert("Error: " + quepaso);
			},
			global: true,
			ifModified: false,
			processData:true,
			success: function(datos){
				var list = eval(datos);
				var sl = document.getElementById("listaOperador");
				
				if(sl && list){
					if(list.length > 0){
						var opt = document.createElement('OPTION');
						opt.innerHTML = " -- Seleccione un opci&oacute;n -- ";
						opt.value = "";
						sl.appendChild(opt);
						for(var i = 0; i < list.length; i++){
							var opt = document.createElement('OPTION');
							opt.innerHTML = list[i].nodeText;
							opt.value = list[i].value;
							sl.appendChild(opt);
						}
					}
					else{
						var opt = document.createElement('OPTION');
						opt.innerHTML = " -- Sin opción seleccionable -- ";
						opt.value = "smsAviso";
						sl.appendChild(opt);
					}
				}
			},
			type: "POST"
		});
	}
}
	
	
	
/**
 * Redirección a la pantalla de datos del informe
 */	
function datosInformes(){
	var frm = document.getElementById('main3');
	frm.method.value = 'doConsulta';	
	$("#redireccion").val('datoInformes');
	$("#origenLlamada").val('condiciones');
	$('#main3').validate().cancelSubmit = true;
	$('#main3').submit(); 
}

/**
 * Redirección a la pantalla de clasificación y ruptura
 */
function clasificacionRuptura(){ 
	var frm = document.getElementById('main3');
    frm.method.value = 'doConsulta';	
	$("#redireccion").val('clasificacionYRuptura');
	$("#origenLlamada").val('condiciones');
	$('#main3').validate().cancelSubmit = true;
	$('#main3').submit(); 
}

/**
 * Redirección a la pantalla de informes
 */
function volver(){ 
	var frm = document.getElementById('main3');
    frm.method.value = 'doConsulta';	
	$("#redireccion").val('informes');
	$("#origenLlamada").val('condiciones');
	$('#recogerInformeSesion').val("true");
	$('#main3').validate().cancelSubmit = true;
	$('#main3').submit(); 
}

/**
 * Visualiza los datos recibidos en el formulario
 */
function visualizarRegistro (id,isCalculado,campoid,datoInformesId,operadorId,idOperadorCondicion,condicion,tipo,od) {
	// Se borra el campo múltiple
	$('#multCondicion').empty();
	
	// Si el campo es de tipo fecha se muestra el calendario
	if (tipo == $('#tipoFecha').val()) $('#btn_fecha').css({'display':'block'})
	else $('#btn_fecha').css({'display':'none'})

	// Limpia las alertas que se estén mostrando
	limpiaAlertas();
	
	$('#main3').validate().cancelSubmit = true;
	
	// Rellena los hidden y el formulario con los datos del registro seleccionado
	var campo;
	if(isCalculado == 1){
		$("#campoId").val(campoid);
		$("#permitidocalculado").val(1);
		$("#modificarValidCalculado").val('true');
	}else{
		$("#campoId").val(campoid);
		$("#modificarValidCalculado").val('false');
		$("#permitidocalculado").val(2);
	}
	$("#datoInformesId").val(datoInformesId);
	$("#tipo").val(tipo);
	$("#listaCampo").val(isCalculado + "-" + datoInformesId + "-" + tipo + "-" + od);
	$("#listaCampo").change();
	$("#id").val(id);
	$("#condicion").val(condicion);
	$("#idOperadorCondicion").val(idOperadorCondicion);  
	$("#idOperador").val(operadorId); 
	$("#listaOperador").val(operadorId +'-'+ idOperadorCondicion);
	
	// 	Rellena el combo múltiple
	var selObj = document.getElementById('multCondicion');
	var ultimoDato="false";
	if(condicion != ""){
		while(ultimoDato=="false"){
			if(condicion.indexOf(',') == -1){
				condicionValue = condicion;		
				selObj.options[selObj.length] = new Option(condicionValue,condicionValue);
				ultimoDato="true";
			}
			else{
				condicionValue = condicion.substr(0,condicion.indexOf(','));
				condicion = condicion.substr(condicion.indexOf(',')+1,condicion.length);
				selObj.options[selObj.length] = new Option(condicionValue,condicionValue);
			}
		}
	}
	
	var lCampo = document.getElementById('multCondicion');
	lCampo.value = datoInformesId+'-'+isCalculado+'-'+tipo;
}
	

/**
 * Muestra los datos del registro seleccionado en el formulario y habilita el botón de 'Modificar'
 */		
function editar(id,isCalculado,campoid,datoInformesId,operadorId,idOperadorCondicion,condicion,tipo,od){
	
	// Muestra los datos en el formulario
	visualizarRegistro (id,isCalculado,campoid,datoInformesId,operadorId,idOperadorCondicion,condicion,tipo,od);
	
	// Muestra el botón 'Modificar'     
	$('#btnModificar').show();
}

/**
 * Muestra los datos del registro seleccionado en el formulario y oculta el botón de 'Modificar'
 */	
function visualizar(id,isCalculado,campoid,datoInformesId,operadorId,idOperadorCondicion,condicion,tipo, od){
	
	// Muestra los datos en el formulario
	visualizarRegistro (id,isCalculado,campoid,datoInformesId,operadorId,idOperadorCondicion,condicion,tipo,od);
	
	// Oculta el botón 'Modificar'     
	$('#btnModificar').show();
}


/**
 * Muestra la alerta de validación indicada por el parámetro
 */
function showAlertaValidacion (num) {
	$('#panelInformacion').hide();
	
	if (num == 1) $('#panelAlertasValidacion').html("Para el operador seleccionado se deben introducir dos valores");
	else if (num == 2) $('#panelAlertasValidacion').html("Para el operador seleccionado se deben introducir como mínimo dos valores");
	else if (num == 3) $('#panelAlertasValidacion').html("Para el operador seleccionado se debe introducir un solo valor");
	
	$('#panelAlertasValidacion').show();	
}

/**
 * Devuelve los valores contenidos en el panel en una cadena separados por ','
 */
function getValueContenido (selObj) {
	var valueContenidoEn='';
	 			
	var i;
	for (i=0; i<selObj.options.length; i++) {
		if(i == 0){
			valueContenidoEn = selObj.options[i].value;
		}else {
			valueContenidoEn = valueContenidoEn +","+ selObj.options[i].value;
		}
	}
	
	return valueContenidoEn;
}

/**
 * Realiza las acciones previas a la modificación de condición y envia el formulario
 */
function enviarModificacion (selObj, frm) {
	$("#condicion").hide();
	$("#condicion").val(getValueContenido (selObj));
	frm.method.value = 'modificar';
	selObj.selectedIndex=0;
	$('#main3').submit();
}				

/**
 * Realiza las acciones previas al alta de condición y envia el formulario
 */
function enviarAlta (selObj, frm) {
	$("#condicion").hide();
	$("#condicion").val(getValueContenido (selObj));
	$("#multCondicion").hide();
	$("#iconoBorrar").hide();
	$("#iconoAnadir").hide();
	$("#datoInfoId").val( $("#datoInformesId").val());
	$("#permOcal").val( $("#permitidocalculado").val());
	selObj.selectedIndex=0;
	frm.method.value = 'doAlta';
	$('#main3').submit();
}

/**
 * Realiza las validaciones previas al envío de datos al servidor para el alta
 */		
function alta(){
	
	$("#alerta").val('');
	$("#mensaje").val('');
	limpiaAlertas();
	
	// Se comprueba si hay algún valor introducido en el panel de valores para pasar las validaciones jquery
	var selObj = document.getElementById('multCondicion');
	// Si el número de elementos es mayor que 0, se rellena el hidden con el tamaño para que pase la validación
	if (selObj.options.length > 0) $("#condicion").val(selObj.options.length);
	// Si es 0, el hidden estará vacío y no pasará la validación
	else $("#condicion").val('');
	
	if ($('#main3').valid()){
		
		var frm = document.getElementById('main3');
	    var campoid,operadorIdCalculadosOpermitidos,operadorId;
	    var listaCampoVal= $("#listaCampo").val();
	    var lstDatos = listaCampoVal.split('-');
	    var listaOperadorVal = $("#listaOperador").val();
	    
	    var idOperadorCondicion	= listaOperadorVal.substr(listaOperadorVal.indexOf('-')+1,listaOperadorVal.length);
	    var idOperador	= listaOperadorVal.substr(0,listaOperadorVal.indexOf('-'));
	    $("#idOperadorCondicion").val(idOperadorCondicion);
	    $("#idOperador").val(idOperador);   
	    
	    // Operador 'Entre' - Deber haber dos elementos
		if(idOperador == 4){
			// El número de elementos no es correcto
			if (selObj.options.length  != 2) showAlertaValidacion (1);
			// El número de elementos es correcto, se continúa con el alta
	 		else enviarAlta (selObj, frm);
	  	}
	  	// Operador 'Contenido en' - Debe haber como mínimo dos elementos
	 	else if(idOperador == 3){
	 		// El número de elementos no es correcto
	 		if (selObj.options.length  < 2) showAlertaValidacion (2);
	 		// El número de elementos es correcto, se continúa con el alta
	 		else enviarAlta (selObj, frm);
	 	}
	 	// Resto de operadores - Sólo puede haber un elemento
	 	else {
	 		// El número de elementos no es correcto
	 		if (selObj.options.length  != 1) showAlertaValidacion (3);
	 		// El número de elementos es correcto, se continúa con el alta
	 		else enviarAlta (selObj, frm);
		}
	}
	
}
		
/**
 * Borra el registro seleccionado
 */		
function borrar(id,permitidocalculado){
	
	$('#main3').validate().cancelSubmit = true;
	if(confirm('¿Está seguro de que desea eliminar esta condición del informe?')){
		var frm = document.getElementById('main3');
		frm.method.value = 'doBaja';			
		$.blockUI.defaults.message = '<h4> Eliminando la condición del informe seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
       	if(permitidocalculado == 1){
			$("#permitidocalculado").val(1);
		}
		else{
			$("#permitidocalculado").val(2);
		}
		$("#id").val(id);	
		$("#origenLlamada").val('borrar');
		$("#main3").submit();	
	}	
}

/**
 * Limpia el formulario y muestra todas las condiciones del informe
 */				
function limpiar(){
	limpiaAlertas();
	$('#btn_fecha').css({'display':'none'})
	$('#btnModificar').hide();
	$("#listaCampo").val('');
	$("#listaOperador").empty();
	$("#camposcalculadosid").val('');
 	$("#campospermitidosid").val('');
 	$('#multCondicion').empty();
 	$("#condiciontxt").val('');
 	$("#condicion").val('');
 	$('#panelInformacion').hide();
	$('#panelAlertasValidacion').hide();
	$("#panelAlertas").hide();
	$("#idOperador").val('');
	$("#datoInformesId").val('');
	$("#idOperadorCondicion").val('');
	
	// Muestra el input de Valor	
	configurarInput();
	
	jQuery.jmesa.removeAllFiltersFromLimit('mtoCondicionesCampos');
	onInvokeAction('mtoCondicionesCampos','clear');			
}
		 
		
/**
 * Realiza las validaciones previas al envío de datos al servidor para la modificación
 */				
function modificar(){
	
	$("#alerta").val('');
	$("#mensaje").val('');
	limpiaAlertas();
	
	// Se comprueba si hay algún valor introducido en el panel de valores para pasar las validaciones jquery
	var selObj = document.getElementById('multCondicion');
	// Si el número de elementos es mayor que 0, se rellena el hidden con el tamaño para que pase la validación
	if (selObj.options.length > 0) $("#condicion").val(selObj.options.length);
	// Si es 0, el hidden estará vacío y no pasará la validación
	else $("#condicion").val('');
	
	// Valida que los datos introducidos en el formulario son correctos
	if ($('#main3').valid()){
		
		var frm = document.getElementById('main3');
		var campoid;
	    var listaCampoVal= $("#listaCampo").val();
	    var lstDatos = listaCampoVal.split('-');
	    
	    
	    var listaOperadorVal = $("#listaOperador").val();
	    var selObj = document.getElementById('multCondicion');
		var idOperadorCondicion	= listaOperadorVal.substr(listaOperadorVal.indexOf('-')+1,listaOperadorVal.length);
	    var idOperador	= listaOperadorVal.substr(0,listaOperadorVal.indexOf('-'));	
	   
	    
	    $("#idOperadorCondicion").val(idOperadorCondicion);
	    $("#idOperador").val(idOperador);   
		
		if(lstDatos[0] == 1){
			strDatoInforme	= lstDatos[1];
			campoid	= lstDatos[0];
			$("#permitidocalculado").val(1);
			$("#tipo").val(lstDatos[2]);
		}
	    else if(lstDatos[0] == 2){
	    	strDatoInforme	= lstDatos[1];
	    	campoid	= lstDatos[0];	
			$("#permitidocalculado").val(2);
			$("#tipo").val(lstDatos[2]);
	    }
	    
	    if(($("#modificarValidCalculado").val() == "true" & $("#permitidocalculado").val() =="1") || ($("#modificarValidCalculado").val() == "false" & $("#permitidocalculado").val() =="2")){
	    	
		    	operadorId	= listaOperadorVal.substr(0,listaOperadorVal.indexOf('-'));  		
		    	operadorIdCalculadosOpermitidos	= listaOperadorVal.substr(listaOperadorVal.indexOf('-')+1,listaOperadorVal.length);
		    	$("#idOperadorCondicion").val(operadorIdCalculadosOpermitidos);
		    	
		    	// Operador 'Entre' - Deber haber dos elementos
				if(idOperador == 4){
					// El número de elementos no es correcto
					if (selObj.options.length  != 2) showAlertaValidacion (1);
					// El número de elementos es correcto, se continúa con el alta
			 		else enviarModificacion (selObj, frm);
			  	}
			  	// Operador 'Contenido en' - Debe haber como mínimo dos elementos
			 	else if(idOperador == 3){
			 		// El número de elementos no es correcto
			 		if (selObj.options.length  < 2) showAlertaValidacion (2);
			 		// El número de elementos es correcto, se continúa con el alta
			 		else enviarModificacion (selObj, frm);
			 	}
			 	// Resto de operadores - Sólo puede haber un elemento
			 	else {
			 		// El número de elementos no es correcto
			 		if (selObj.options.length  != 1) showAlertaValidacion (3);
			 		// El número de elementos es correcto, se continúa con el alta
			 		else enviarModificacion (selObj, frm);
				}
	    			
		}
		else{
			$('#panelAlertasValidacion').html("Los tipos de campos son diferentes");
			$('#panelAlertasValidacion').show();
		}
	}
}

/**
 * Actualiza los hidden con los datos del operador seleccionado
 */
function actualizaIdoperador(listaOperador){
	var idOperadorCondicion	= listaOperador.substr(listaOperador.indexOf('-')+1,listaOperador.length);
	var idOperador	= listaOperador.substr(0,listaOperador.indexOf('-'));
	$("#idOperadorCondicion").val(idOperadorCondicion);
	$('#idOperador').val(idOperador);
}
	    
/**
 * Introduce en el filtro todos los campos que se hayan informado en el formulario
 */	    
function comprobarCampos(){
	jQuery.jmesa.removeAllFiltersFromLimit('mtoCondicionesCampos');
	
	if ($("#datoInformesId").val() != ''){
		jQuery.jmesa.addFilterToLimit('mtoCondicionesCampos','datoinformeid', $("#datoInformesId").val());
	}
	
	if ($("#idOperadorCondicion").val() != ''){
		jQuery.jmesa.addFilterToLimit('mtoCondicionesCampos','idtablaoperadores', $('#idOperadorCondicion').val());
	}

	if ($('#idOperador').val() != ''){
		jQuery.jmesa.addFilterToLimit('mtoCondicionesCampos','idoperador', $('#idOperador').val());
	}
	
	if ($('#condicion').val() != ''){
		jQuery.jmesa.addFilterToLimit('mtoCondicionesCampos','condicion', $('#condicion').val());
	}
}

/**
 * Añade el valor introducido (a través de la caja de texto o del combo) a la condición
 */		
function anadirCondicion(){
	var repetido = false;
	var validar = true;
	var selObj = document.getElementById('multCondicion');
	selObj.selectedIndex=0;
	var i;
	var count = 0;
	
	// Si se ha seleccionado a través del combo no es necesario validar
	if ($('#condiciontxtcombo').css('display') == 'block') {
		$('#condiciontxt').val($('#condiciontxtcombo').val());
		validar = false;
	}
	// Si se ha seleccionado a través de input
	else {
		// se quitan los acentos
		$('#condiciontxt').val(quitaAcentos($('#condiciontxt').val()));
		// Se pasa a mayúsculas el valor introducido
		$('#condiciontxt').val($('#condiciontxt').val().toUpperCase());
	}
	
	
	if (!validar || validarCampo()){
		if($('#condiciontxt').val() != ""){
			for (i=0; i<selObj.options.length; i++) {
				if (selObj.options[i].value == $('#condiciontxt').val()){
					repetido = true;
					break;
				}
			}
			if (!repetido){
				var selObj = document.getElementById('multCondicion');
				selObj.options[selObj.length] = new Option($('#condiciontxt').val(),$('#condiciontxt').val());
				selObj.selectedIndex=0;
				$('#condiciontxt').val('');
			}
		}
	}
}

/**
 * Comprueba que el valor introducido es numérico si el tipo del campo seleccionado también lo es
 */
function validarCampo(){
	if ($('#condiciontxt').val() != '' && $('#tipo').val() == $('#tipoNumerico').val()){
	 	var condiciontxtOk = false;
	 	
	 	try {		 	
	 		var auxCondiciontxt =  parseFloat($('#condiciontxt').val());
	 		if(!isNaN(auxCondiciontxt)){
				$('#condiciontxt').val(auxCondiciontxt);
				condiciontxtOk = true;
			}
		}
		catch (ex) {}
		
		// Si ha habido error en la validación muestra el mensaje
		if (!condiciontxtOk) {
			$('#panelAlertasValidacion').html("El campo Valor Solo puede contener números");
			$('#panelAlertasValidacion').show();
			return false;
		}
	}
	return true;
}

/**
 * Borra la opción seleccionada del combo múltiple
 */			
function borrarCondicion(){
	var selObj = document.getElementById('multCondicion');
	var i;
	for (i=0; i<selObj.options.length; i++) {
		if (selObj.options[i].selected) {
			selObj.options[i] = null;
			i--;
		}
	}
}	

/**
 * Validación del formato de la fecha introducida
 */
function validarFecha(obj,texto){
	if ($('#tipo').val() == $('#tipoFecha').val()){
		if (!ComprobarFecha(obj, document.main3,texto)) obj.value='';
	}
}
			
function onLoadCampoValue(){
	idOp = $("#idOperador").val();
	idOpCondicion = $("#idOperadorCondicion").val();
	condicion = $("#condicion").val();
	alerta = $("#alerta").val();
	mensaje = $("#mensaje").val(); 

	
	if($("#datoInformesId").val() != "" && $("#permitidocalculado").val() != "" && $("#tipo").val() != ""){
		$("#listaCampo").val($("#permitidocalculado").val()+'-'+$("#datoInformesId").val()+'-'+$("#tipo").val());
		}
	$("#listaCampo").change();
  	if(idOp != "" &&  idOpCondicion != ""){
  		$("#listaOperador").val(idOp +"-"+ idOpCondicion );
	}
	
	if (alerta != "" ){
		$("#panelAlertas").val(alerta);
		$("#panelAlertas").show();
	}
	if (mensaje != "" ){
		$("#panelInformacion").val(mensaje);
		$("#panelInformacion").show();
	}
		
	var strCondicion;
	var condicionValue;
	var ultimoDato="false";
	var selObj = document.getElementById('multCondicion');
	strCondicion = condicion;
	if(strCondicion != ""){
		while(ultimoDato=="false"){
			if(strCondicion.indexOf(',') == -1){
				condicionValue = strCondicion;
				selObj.options[selObj.length] = new Option(condicionValue,condicionValue);
				ultimoDato="true";
			}else{
				condicionValue = strCondicion.substr(0,strCondicion.indexOf(','));
				strCondicion = strCondicion.substr(strCondicion.indexOf(',')+1,strCondicion.length);
				selObj.options[selObj.length] = new Option(condicionValue,condicionValue);
			}
		}
	}
	$("multCondicion").show;
	$("#condicion").val(condicionValue);
	if($("#id").val() != ""){
		$("#btnModificar").show();
	}else{
		$("#btnModificar").hide();
	}
}

function consultar(){
	var valueContenidoEn='';
	var selObj = document.getElementById('multCondicion');
	var i;
	for (i=0; i<selObj.options.length; i++) {
		if(i == 0){
			valueContenidoEn = selObj.options[i].value;
		}else {
			valueContenidoEn = selObj.options[i].value+ ","+valueContenidoEn;
		}
	}
	
	$("#condicion").val(valueContenidoEn);
	comprobarCampos();
	$("#btnModificar").hide();
	onInvokeAction('mtoCondicionesCampos','filter');
}

function onInvokeAction(id) {
	var to=document.getElementById("adviceFilter");
	to.innerHTML="<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	$.get('mtoCondicionCampos.run?ajax=true&idInforme='+$("#informeid").val() +'&datoInformesId='+$("#datoInformesId").val() +'&'+'&permitidOcalculado='+$("#permitidocalculado").val() +'&' + parameterString, 
	function(data) {
		$("#grid").html(data)
	});
}

// Comprueba si el campo seleccionado tiene origen de datos asociado o se pueden introducir valores libres en la condición 
function checkOrigenDatos (idCampo) {
	
	// Si no se ha seleccionado ninguna opción válida
	if (idCampo == null || idCampo == '') {
		configurarInput();
		return;
	}
	
	// Parte la cadena pasada como parámetro para obtener los códigos
	var lstDatos = idCampo.split('-');
	
	// El indicador de origen de datos viene en la cuarta posición
	if (!isNaN (lstDatos[3]) && lstDatos[3] > $('#odValorLibre').val()) { // Si tiene indicador de origen de datos 		
		obtenerOrigenDatos (lstDatos[3]);
	}
	else { // Si no tiene indicador de origen de datos muestra el input
		configurarInput();
	}
	
	// Obtiene la lista de operadores
	obtenerListaOperadores (lstDatos);
}


// Realiza una llamada ajax para obtener el listado de valores posibles para el origen de datos seleccionado 
function obtenerOrigenDatos(od){

	if(od != ''){
		$.ajax({
			url: "mtoCondicionCampos.run",
			data: "method=ajax_getOrigenDatos&od=" + od,
			async:false,
			contentType: "application/x-www-form-urlencoded",
			dataType: "text",
			error: function(objeto, quepaso, otroobj){
				alert("Error: " + quepaso);
			},
			global: true,
			ifModified: false,
			processData:true,
			success: function(datos){
				var list = eval(datos);
				configurarCombo (list);
			},
			type: "POST"
		});
	}
}

// Muestra y rellena el combo 'Valor' con los datos obtenidos del servidor y oculta y vacía el input
function configurarCombo (list) {
	// Oculta el input de 'Valor'
	$('#condiciontxt').val('');
	$('#condiciontxt').css({'display':'none'});
	// Muestra el combo de 'Valor'
	$('#condiciontxtcombo').empty();
	$('#condiciontxtcombo').css({'display':'block'});
	
	// Rellena el combo con los datos incluidos en la lista
	var sl = document.getElementById("condiciontxtcombo");
	if(sl && list){
		if(list.length > 0){
			for(var i = 0; i < list.length; i++){
				var opt = document.createElement('OPTION');
				opt.innerHTML = list[i].idEstado + ' - ' + list[i].descEstado;
				opt.value = list[i].idEstado;
				sl.appendChild(opt);
			}
		}
		else{
			var opt = document.createElement('OPTION');
			opt.innerHTML = " -- Sin opción seleccionable -- ";
			opt.value = "";
			sl.appendChild(opt);
		}
	}
}

// Muestra el input de 'Valor' y oculta y vacía el combo
function configurarInput () {
	// Muestra el input de 'Valor'
	$('#condiciontxt').val('');
	$('#condiciontxt').css({'display':'block'});
	// Oculta el combo de 'Valor'
	$('#condiciontxtcombo').empty();
	$('#condiciontxtcombo').css({'display':'none'});
}

function obtenerListaOperadores(lstDatos){

	if (lstDatos[2] == $('#tipoFecha').val()){
		$('#btn_fecha').css({'display':'block'})
	}else{
		$('#btn_fecha').css({'display':'none'})
	}
	$('#tipo').val(lstDatos[2]);
	
	limpiaAlertas();
	var frm = document.getElementById('main3');
	var strDatoInforme;
	if (lstDatos[0] == 1){
		strDatoInforme	= lstDatos[1];
		$("#permitidocalculado").val(1);
	}
	else if (lstDatos[0] == 2){
		strDatoInforme	= lstDatos[1];
		$("#permitidocalculado").val(2);
	}else{
		$("#permitidocalculado").val(0);
	}
	
	$("#datoInformesId").val(strDatoInforme);
	$('#multCondicion').empty();
	$('#idOperador').val('');
	$("#condiciontxt").val('');
	$("#condicion").val('');
	cargarSelecOperador();
}

	function generar(){
	
		
		var frm = document.getElementById('main3');
		var frmGen = document.getElementById('generarInformeForm');
		frmGen.idInforme.value = frm.idInforme.value;
		frmGen.method.value = 'doGenerar';
		$('#generarInformeForm').attr('target', '_blank');
		$('#generarInformeForm').submit();
}
		
		
 	function limpiaAlertas(){
	 	
	 	$("#panelInformacion").val('');
		$("#panelInformacion").hide();
		$("#panelAlertasValidacion").val('');
		$("#panelAlertasValidacion").hide();	
		$("#panelAlertas").val('');
		$("#panelAlertas").hide();
	 	
	 }		