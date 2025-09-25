$(document).ready(function () {

	var URL = UTIL.antiCacheRand(document.getElementById("formCarga").action);
	document.getElementById("formCarga").action = URL;

	$("input[type=file]").filestyle({
		image: "jsp/img/boton_examinar.png",
		imageheight: 22,
		imagewidth: 82,
		width: 250
	});
	
	if($('#perfil').val()== '0'){ 
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
	        inputField        : "fechavalidez",
	        button            : "btn_fechavalidez",
	        ifFormat          : "%d/%m/%Y",
	        daFormat          : "%d/%m/%Y",
	        align             : "Br"			        	        
	  	});
	}

	if ($('#esPlanGenerico').val() == true || $('#esPlanGenerico').val() == "true"){
		$("#divlineaPlanGenerico").show();
		$("#divlineaPlanNormal").hide();
	}else{
		$("#divlineaPlanGenerico").hide();
		$("#divlineaPlanNormal").show();
	}
	
	if($('#origenLlamada').val()== 'editar' || $('#origenLlamada').val()== 'modificar' || $('#origenLlamada').val()== 'cargar'){
		$("#btnModificar").show();
		$("#btnCargar").hide();
		$("#btnCargar").hide();
		$("#fileDiv").hide();
	}else{
		$("#btnModificar").hide();
	}
	
	if($('#origenLlamada').val()== 'cargar' || $('#origenLlamada').val()== 'modificar'){
		var plan = $('#plan').val();
		if (plan == 9999){
			var codLineaGen = $('#lineaGen').val();
			var nombLineaGen = $('#nomblineaGen').val();
			
			$('#lineaCondicionado').val(codLineaGen);
			$('#nom_linea').val(nombLineaGen);
		}
	}
	
	if ($('#btntipodoc').is(':visible')) {
		var boton = document.getElementById('btntipodoc');
		if (!document.getElementById('docAgroseguroTipo').value == ''){
			boton.innerHTML = 'Modificar'; 
		}else{
			boton.innerHTML = 'Alta';
		}
	}

	$('#formCarga').validate({
		onfocusout: function (element) {
			if ($('#method').val() == "doCarga") {
				this.element(element);
			}
		},
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		highlight: function (element, errorClass) {
			$("#campoObligatorio_" + element.id).show();
		},
		unhighlight: function (element, errorClass) {
			$("#campoObligatorio_" + element.id).hide();
		},
		rules: {
			"codlinea" :{ digits: true, range: [0, 999]},
			"codplan": { required: true, digits: true, range: [0, 9999] },
			"docAgroseguroTipo.id": { altaModificacion: ['docAgroseguroTipo.id'], required: true },
			"descripcion": { invalidChars: true, requiredCondicional: ['descripcion'], maxlength: 255},
			"file": { requiredFile: true, validaExtension: ['file'], validaLongitudFichero: ['file'] },
			"listaPerfiles" : {perfilesOblig:['']}
		},
		messages: {
			"codlinea": { digits: "El campo L�nea s�lo puede contener d�gitos", range: "El campo L�nea s�lo puede contener d�gitos entre 0 y 999" },
			"codplan": { required: "El campo Plan es obligatorio", digits: "El campo Plan s�lo puede contener d�gitos", range: "El campo Plan s�lo puede contener d�gitos entre 0 y 9999" },
			"docAgroseguroTipo.id": { required: "El campo Tipo documento es obligatorio" },
			"descripcion": { invalidChars: "El campo Descripci\u00F3n no puede contener d\u00EDgitos ni caracteres especiales", requiredCondicional: "El campo Descripci�n es obligatorio", maxlength: "El campo Descripci�n debe contener como m�ximo 255 caracteres" },
			"file": { requiredFile: "El campo Fichero es obligatorio", validaExtension: "Extensi�n de fichero no permitida", validaLongitudFichero: "El campo Fichero debe contener como m�ximo 256 caracteres" },
			"listaPerfiles" : {perfilesOblig: "El campo Perfil es obligatorio"}
		}
	});

	jQuery.validator.addMethod("validaExtension", function (value, element) {
		var extPermitida = false;
		if($('#origenLlamada').val()!= 'modificar'){
			var ext = element.value.split('.').pop();
			var arr = $('#extensionesPermitidas').val().split('|');
			for (var i = 0; i < arr.length; i++) {
				if (ext == arr[i]) {
					extPermitida = true;
					break;
				}
			}
		}else{
			extPermitida = true;
		}	
		return extPermitida;
	});

	jQuery.validator.addMethod("validaLongitudFichero", function (value, element) {
		var result = false;
		var nomfichero = element.value.split('\\').pop();
		if (nomfichero.length <= 256) {
			result = true;
		}
		return result;
	});

	jQuery.validator.addMethod("requiredCondicional", function (value, element) {
		var result = true;
		var tipo = $('#docAgroseguroTipo').val();
		if (tipo == 3 || tipo == 4) {
			result = element.value != '';
		}
		return result;
	});
	
	jQuery.validator.addMethod("requiredFile", function (value, element) {
		var result = true;
		var origenLlamada = $('#origenLlamada').val();
		if($('#origenLlamada').val()== 'modificar'){
			result = true;
		}else{
			result = element.value != '';
		}
		return result;
	});
	
	/* P0079014 ** MODIF TAM (18.04.2022) ** Resoluci�n Defecto N� 4 * Inicio */
	jQuery.validator.addMethod("requiredLinea", function (value, element) {
		var result = true;
		var linea = $('#linea').val();
		var plan = $('#plan').val();
		var lineaCond = $('#lineaCondicionado').val();
		
		if (plan != 9999){ 
			result = element.value != '';
		}else{
			if (lineaCond == ""){
				result = false;
			}
		}
		return result;
	});
	/* P0079014 ** MODIF TAM (18.04.2022) ** Resoluci�n Defecto N� 4 * Fin */
	
	/* P0079014 ** MODIF TAM (05.05.2022) ** Resoluci�n Defecto N� 29 * Inicio */
	jQuery.validator.addMethod("invalidChars", function(value, element, params) { 
		var validado = true;
		var invalidChars = "\":;<>[]%!?()+*_=$&";
		for(var i = 0; i< value.length;i++){
			if(invalidChars.indexOf(value.charAt(i))!= -1){							
				validado = false;
				break;
			}
		}
		return (this.optional(element) || validado);	
	});
	/* P0079014 ** MODIF TAM (05.05.2022) ** Resoluci�n Defecto N� 29 * Fin */
	
	/* P0079014 ** MODIF TAM (18.04.2022) ** Resoluci�n Defecto N� 5 * Inicio */
	jQuery.validator.addMethod("perfilesOblig", function(value, element) {
    	return perfilObligatorio();
    });
	/* P0079014 ** MODIF TAM (18.04.2022) ** Resoluci�n Defecto N� 5 * Fin */
	
	
	var hayDescripcion;
	var esLineaGenerica;
	jQuery.validator.addMethod('altaModificacion', function (value, element) {
		hayDescripcion = esLineaGenerica = true;
		if (element.value == 6) {
			hayDescripcion = $('#descripcion').val() != '';
			esLineaGenerica = $('#linea').val() == 999;
			result = hayDescripcion && esLineaGenerica;
		}
		return hayDescripcion && esLineaGenerica;
	}, function () {
		if (!hayDescripcion && !esLineaGenerica) {
			return 'Descripci\u00F3n y L\u00EDnea gen\u00E9rica (999) obligatorias';
		}
		if (!hayDescripcion) {
			return 'Descripci\u00F3n obligatoria';
		}	
		if (!esLineaGenerica) {
			return 'L\u00EDnea gen\u00E9rica (999) obligatoria';
		}
	});
});

/* P0079014 ** MODIF TAM (18.04.2022) ** Resoluci�n Defecto N� 5 * Inicio */
function perfilObligatorio() {
	var perfOblig = true;
	var listaPerfiles = $('#listaPerfiles').val();

	if($('#perfil').val()== '0'){
		if ($('#listaPerfiles').val() == null) {
			perfOblig = false;			
		}
	}

	return perfOblig;
}
/* P0079014 ** MODIF TAM (18.04.2022) ** Resoluci�n Defecto N� 5 * Fin */

function comprobarCampos(incluirJmesa) {	
	if (incluirJmesa) {
		jQuery.jmesa.removeAllFiltersFromLimit('docsAgroseguro');
	}
	var resultado = false;
	if ($('#plan').val() != '' && /^([0-9])*$/.test($('#plan').val())) {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('docsAgroseguro', 'codplan', $('#plan').val());
		}
		resultado = true;
	}
	
	var plan = $('#plan').val();
	var lineaCond = $('#lineaCondicionado').val();
	
	if (plan != 9999){ 
		if ($('#linea').val() != '' && /^([0-9])*$/.test($('#linea').val())) {
			if (incluirJmesa) {
				jQuery.jmesa.addFilterToLimit('docsAgroseguro', 'codlinea', $('#linea').val());
			}
			resultado = true;
		}
	}else{
		if ($('#lineaCondicionado').val() != '' && /^([0-9])*$/.test($('#linea').val())) {
			if (incluirJmesa) {
				jQuery.jmesa.addFilterToLimit('docsAgroseguro', 'codlinea', $('#lineaCondicionado').val());
			}
			resultado = true;
		}
		
	}	
	
	if ($('#docAgroseguroTipo').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('docsAgroseguro', 'docAgroseguroTipo.id', $('#docAgroseguroTipo').val());
		}
		resultado = true;
	}
	/* Pet. 79014 ** MODIF TAM (28.03.2022) */
	if($('#perfil').val()== '0'){
		if ($('#fechavalidez').val() != '') {
			if (incluirJmesa) {
				jQuery.jmesa.addFilterToLimit('docsAgroseguro', 'fechavalidez', $('#fechavalidez').val());
			}
			resultado = true;
		}
	}
	
	if($('#perfil').val()== '0'){ 
		if ($('#entidad').val() != '') {
			if (incluirJmesa) {
				jQuery.jmesa.addFilterToLimit('docsAgroseguro', 'codentidad', $('#entidad').val());
			}
			resultado = true;
		}
	}else{
		resultado = true;
	}	
	
	if($('#perfil').val()== '0'){
		if ($('select[name="listaPerfiles"] option:selected').length != 0){
			if (incluirJmesa) {
				var select= document.getElementById('listaPerfiles');
				var perfilesSelecc= perfilSelecc(select, "limit");
			
				jQuery.jmesa.addFilterToLimit('docsAgroseguro', 'codDocAgroseguroPerfiles', perfilesSelecc);
			}
			resultado = true;
		}
	}else{
		if (incluirJmesa) {
			
			if($('#externo').val()!= '0'){
				var perfilLim = "1"+$('#perfil').val() + ",";
				jQuery.jmesa.addFilterToLimit('docsAgroseguro', 'codDocAgroseguroPerfiles', perfilLim);
			}else {
				var perfilLim = $('#perfil').val() + ",";
				jQuery.jmesa.addFilterToLimit('docsAgroseguro', 'codDocAgroseguroPerfiles', perfilLim);
			}	
		}	
		resultado = true;
	}	
	
	/* Pet. 79014 ** MODIF TAM (28.03.2022) */
	if ($('#descripcion').val() != '') {
		if (incluirJmesa) {
			jQuery.jmesa.addFilterToLimit('docsAgroseguro', 'descripcion', $('#descripcion').val());
		}
		resultado = true;
	}
	return resultado;
}

function consultarInicial() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	
	var plan = $('#plan').val();
	
	if (plan == 9999){
		var lineaCond = $('#lineaCondicionado').val();
		$('#lineaGen').val(lineaCond);
	}	

	$('#method').val('doConsulta');
	$("#btnModificar").hide();
	
	if (comprobarCampos(false)) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$("#formCarga").validate().cancelSubmit = true;
		$('#formCarga').submit();
	} else {
		$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo o el contenido de alguno de los campos de filtro no es v\u00E1lido");
		$('#panelAlertasValidacion').show();
	}
}

function consultar() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$("#btnModificar").hide();
	
	
	$('#method').val('doConsulta');
	if (comprobarCampos(true)) {
		onInvokeAction('docsAgroseguro', 'filter');
	} else {
		$('#panelAlertasValidacion').html("Es necesario filtrar al menos por un campo o el contenido de alguno de los campos de filtro no es v\u00E1lido");
		$('#panelAlertasValidacion').show();
	}
	$("#btnModificar").hide();
}

function cargar() {

	$('#origenLlamada').val('cargar');
	limpiaAlertas();
	$("#btnModificar").hide();
	
	
	$('#method').val('doCarga');
	if ($('#formCarga').valid()) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });

		$('#formCarga').submit();
	}
}

function limpiar() {
	$('#limpiar').submit();
}

/**
 * Limpia las posibles alertas y mensajes que se estuviesen mostrando
 */
function limpiaAlertas() {
	$("#panelInformacion").hide();
	$("#panelAlertasValidacion").hide();
	$("#panelAlertas").hide();
	$("label[id^='campoObligatorio_']").each(function () {
		$(this).hide();
	});
}

function onInvokeAction(id) {
	var to = document.getElementById("adviceFilter");
	to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('formCarga');
	$.get('documentacionAgroseguro.run?ajax=true&origenLlamada='
		+ frm.origenLlamada.value + '&' + parameterString, function (data) {
			$("#grid").html(data);
			comprobarChecks();
		});
}

function borrar(id) {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doBorrar');
	$('#id').val(id);
	$("#btnModificar").hide();
	
	if (confirm('Est\u00E1 a punto de eliminar el documento. \u00BFEst\u00E1 seguro?')) {
		$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		$('#formCarga').validate().cancelSubmit = true;
		$('#formCarga').submit();
	}
}

function descargar(id) {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doDescargarFichero');
	$('#id').val(id);
	$('#formCarga').validate().cancelSubmit = true;
	$('#formCarga').submit();
}

function borradoMasivo() {
	$('#origenLlamada').val('');
	limpiaAlertas();
	$('#method').val('doBorradoMasivo');
	if ($('#listaIdsMarcados').val() != '') {
		if (confirm('Est\u00E1 a punto de eliminar uno o varios documentos. \u00BFEst\u00E1 seguro?')) {
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$('#formCarga').validate().cancelSubmit = true;
			$('#formCarga').submit();
		}
	} else {
		showPopUpAviso("Debe seleccionar como m\u00EDnimo un documento.");
	}
}

function showPopUpAviso(mensaje) {
	$('#txt_mensaje_aviso').html(mensaje);
	$('#panelInformacion2').show();
	$('#popUpAvisos').show();
	$('#overlay').show();
}

function hidePopUpAviso() {
	$('#popUpAvisos').hide();
	$('#overlay').hide();
}

function marcarTodos() {
	if ($('#checkTodos').length) {
		if ($('#checkTodos').attr('checked') == true) {
			var listaIdsTodos = $("#listaIdsTodos").val();
			$('#listaIdsMarcados').val(listaIdsTodos);
			$('#marcaTodos').val('true');
			comprobarChecks();
		} else {
			$('#listaIdsMarcados').val('');
			$('#marcaTodos').val('false');
			$("input[type=checkbox]").each(function () {
				$(this).attr('checked', false);
			});
		}
	}
}

function comprobarChecks() {
	var listaIdsMarcados = $('#listaIdsMarcados').val();
	var cadena = [];
	cadena = listaIdsMarcados.split(",");
	if (listaIdsMarcados.length > 0) {
		$("input[type=checkbox]").each(function () {
			if ($("#marcaTodos").val() == "true") {
				if ($(this).attr('id') != "checkTodos") {
					$(this).attr('checked', true);
				}
			}
			else {
				for (var i = 0; i < cadena.length - 1; i++) {
					var idcheck = "check_" + cadena[i];
					if ($(this).attr('id') == idcheck) {
						$(this).attr('checked', true);
					}
				}
			}
		});
	}
	if ($('#marcaTodos').val() == "true") {
		if ($('#checkTodos').length) $('#checkTodos').attr('checked', true);
	} else {
		if ($('#checkTodos').length) $('#checkTodos').attr('checked', false);
	}
}

function listaCheckId(id) {
	var listaIdsMarcados = "";
	var listaFinalIds = "";
	var cadena = [];

	if ($('#check_' + id).attr('checked') == true) {
		listaIdsMarcados = $('#listaIdsMarcados').val() + id + ",";
		$('#listaIdsMarcados').val(listaIdsMarcados);
	} else {
		listaIdsMarcados = $('#listaIdsMarcados').val();
		cadena = listaIdsMarcados.split(",");

		for (var i = 0; i < cadena.length - 1; i++) {
			if (cadena[i] != id) {
				listaFinalIds = listaFinalIds + cadena[i] + ",";
			}
		}
		$('#listaIdsMarcados').val(listaFinalIds);
		$('#marcaTodos').val('false');
		comprobarChecks();
	}
}

/* Pet. 79014 ** MODIF TAM (23.03.2022) ** Inicio */

function validarboton(valor){
	var boton = document.getElementById('btntipodoc');
	if (!document.getElementById('docAgroseguroTipo').value == ''){
		boton.innerHTML = 'Modificar'; 
	}else{
		boton.innerHTML = 'Alta';
	}
}

function editar(id, plan, linea, nombLinea, tipoDoc, nombDoc, ent, nombEnt, desc, fechaVal, StrPerfSel){
	var frm = document.getElementById('formCarga');
	
	$('#origenLlamada').val('editar');
	limpiaAlertas();
	
	$("#btnModificar").show();
	$("#btnCargar").hide();
	$("#btnCargar").hide();
	$("#fileDiv").hide();
	
	$('#id').val(id);

	frm.target="";
	frm.plan.value = plan;
	frm.entidad.value = ent;
	frm.desc_entidad.value = nombEnt;
	frm.descripcion.value = desc;

	if (plan == 9999){
		$("#divlineaPlanGenerico").show();
		$("#divlineaPlanNormal").hide();

		frm.lineaCondicionado.value = linea;
		frm.nom_linea.value = nombLinea;
	}else{
		$("#divlineaPlanGenerico").hide();
		$("#divlineaPlanNormal").show();

		frm.linea.value = linea;
		frm.nomlinea.value = nombLinea;
	}

	frm.docAgroseguroTipo.value = tipoDoc;
	frm.fechavalidez.value = fechaVal;
	frm.listaPerfiles.value = StrPerfSel;
	
	frm.listPerSelD = StrPerfSel;
	
	seleccionaPerfiles(StrPerfSel);
}

function modificar(){
	$('#origenLlamada').val('modificar');
	
	if ($('#formCarga').valid()) {

		var plan = $('#plan').val();
		
		if (plan == 9999){
			var lineaCond = $('#lineaCondicionado').val();
			$('#lineaGen').val(lineaCond);
		}	

		var select= document.getElementById('listaPerfiles');
		var perfilesSelecc= perfilSelecc(select, "modificar");
		
		if(perfilesSelecc!=''){
			$("#perfilSel").val(perfilesSelecc);
		}else{	
			$("#perfilSel").val(null);
		}
		
		limpiaAlertas();
	
		$('#method').val('doModificar');
		$('#formCarga').validate().cancelSubmit = true;
		$('#formCarga').submit();
	}
	
}

function perfilSelecc(select, opcion){
	var valores='';
	var todosPerfiles = false;
	/* 1�: comprobamos si la opci�n Todos se ha seleccionado */
	for ( var i = 0, l = select.options.length, o; i < l; i++ ){
		o = select.options[i];
		if(o.selected){
			if (o.value == ""){
				todosPerfiles = true;
			}
		}
	}
	
	if (opcion == "limit"){
		if (todosPerfiles == true){
			valores = ",";
		}else{
			for ( var i = 0, l = select.options.length, o; i < l; i++ ){
				o = select.options[i];
				if(o.selected){
						valores=valores + o.value + ',';
				}
			}
		}
	}else{
		for ( var i = 0, l = select.options.length, o; i < l; i++ ){
			o = select.options[i];
			if(o.selected){
					valores=valores + o.value + ',';
			}
		}
	}
	
	var perfilSelec = valores.substring(0, valores.length - 1);
	return perfilSelec;
}

function ejecutarbotonTipoDoc(){
	
	$('#panelAlertasValidacion').html("");
	$('#panelAlertasValidacion').hide();
	$('#panelAlertasValidacion_tipoDocA').html("");
	$('#panelAlertasValidacion_tipoDocA').hide();
	$('#panelAlertasValidacion_tipoDocM').html("");
	$('#panelAlertasValidacion_tipoDocM').hide();
 		
	var valboton = document.getElementById('btntipodoc').innerHTML;
	
	if (valboton == 'Alta'){
		$('#divAltaTipoDoc').show();
	}else{
		
		/* Si vamos a modificar enviamos el idTipoDoc y la descripci�n seleccionada */
		var combo = document.getElementById("docAgroseguroTipo");
		var selected = combo.options[combo.selectedIndex].text;
		var idtipoDoc = document.getElementById('docAgroseguroTipo').value;

		$('#descTipoDocM').val(selected);
		$('#idTipoDoc').val(idtipoDoc);
		
		$('#divModifTipoDoc').show();
		// Abrimos nueva ventana popup en modo Modificaci�n
	}
}

function cerrarPopUpTipoDocumAl(){
 	$.unblockUI();
 	$('#divAltaTipoDoc').hide();
}

function cerrarPopUpTipoDocumMo(){
 	$.unblockUI();
 	$('#divModifTipoDoc').hide();
}


function doModifTipoDocu(){
	
	if ($('#descTipoDocM').val() == '') {
		$('#panelAlertasValidacion_tipoDocM').html("Debe rellenar todos los datos para proceder a la modificaci�n.");
		$('#divModifTipoDoc').show();
	} else {
		
		var idTipoDoc = $('#idTipoDoc').val();
		var descripcion =  $('#descTipoDocM').val();
		var tipoOper ='M';
		
		/* Defecto 35 ** MODIF TAM (08.09.2021) ***/
		/* deshabilitamos el bot�n Aplicar, para que no le puedan dar mas de una vez, hasta que no termine la primera ejecuci�n*/
		$('#btnAplicarModif').hide();
		ajaxAltaModifTipoDoc(idTipoDoc, descripcion, tipoOper);
	 	 	
	 }
}

function doAltaTipoDocu(){
	
	if ($('#descTipoDoc').val() == '') {
		$('#panelAlertasValidacion_tipoDocA').html("Debe rellenar todos los datos para proceder al alta.");
		$('#divAltaTipoDoc').show();
	} else {
		
		var idTipoDoc = $('#idTipoDoc').val();
		var descripcion =  $('#descTipoDoc').val();
		var tipoOper ='A';
		
		/* deshabilitamos el bot�n Aplicar, para que no le puedan dar mas de una vez, hasta que no termine la primera ejecuci�n*/
		$('#btnAplicarAlta').hide();
		
		ajaxAltaModifTipoDoc(idTipoDoc, descripcion, tipoOper);
	 }
		
 }
 
 function ajaxAltaModifTipoDoc(idTipoDoc, descripcion, tipoOper){
	//validar si la poliza existe en BBDD
	//en SeleccionPolizaManager hay este metodo public final Poliza getPolizaById(final Long idPoliza)
	//creamos una llamada ajax
	 
	 /* Limpiamos los campos de descripci�n */
	 $('#descTipoDoc').val('');
	 $('#descTipoDocM').val('');
			
	$.ajax({
		url:           "documentacionAgroseguro.run",
		data:         "method=doAltaModifTipoDoc&idTipoDoc=" +idTipoDoc+ "&desc=" +descripcion+ "&operacion=" +tipoOper,
		async:        false,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		success: function(resultado){

			$.unblockUI();
			if (resultado.result == 'OK'){
				$("#panelMensajeValidacion").show();
				$("#formCarga").validate().cancelSubmit = true;
				$('#formCarga').submit();
				$('#docAgroseguroTipo').val('');	
			}
		},
		complete: function(resultado){
			if (tipoOper == 'A'){
				$('#divAltaTipoDoc').hide();
			}else{
				$('#divModifTipoDoc').hide();
			}			
		},
		error: function(objeto, quepaso, otroobj){
			alert("Error en al Alta/Modificaci�n del Tipo documento: " + quepaso , 'Error');
		},

		type: "GET"
	});
 }
 
function obtenernombreLinea(codlinea){
	$.ajax({
		url:           "documentacionAgroseguro.run",
		data:         "method=doObtenerNombLinea&codLinea=" +codlinea,
		async:        true,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		success: function(resultado){
			$.unblockUI();
			if (resultado.result == 'OK'){
				var descripcion = resultado.descripcion;
				$('#nom_linea').val(descripcion);	
				
					
			}
		},

		type: "GET"
	});
}

function comprobarPlanGenerico(codPlan){
	 if (codPlan == "9999"){
		 $("#esPlanGenerico").val(true);
		 $('#divlineaPlanNormal').hide();
		 $('#divlineaPlanGenerico').show();
		 $.unblockUI();
		 
	 }else{
		 $("#esPlanGenerico").val(false);
		 $('#divlineaPlanNormal').show();
		 $('#divlineaPlanGenerico').hide();
		 $.unblockUI();
	 }	 
}

function seleccionaPerfiles(perfiles){
	if (perfiles != ""){
		if(perfiles!=null && perfiles!=""){	
			var perfilesArray=perfiles.split(";");
		}

		for ( var i = 0, l = perfilesArray.length, o; i < l; i++ ){
			var option= document.getElementById("esPerfil_"+ perfilesArray[i]);
			if(option!=null){
				option.selected = true;
				
			}
		}
	}else{
		var sel = document.getElementById("listaPerfiles"); 

		for (var i = 0; i < sel.length; i++) {
		    sel[i].selected=false;
		}

	}
	
}

/* Pet. 79014 ** MODIF TAM (23.03.2022) ** Inicio */


function onInvokeExportAction(id) { 
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	location.href = 'documentacionAgroseguro.run?ajax=false&export=true&' + parameterString;
}
