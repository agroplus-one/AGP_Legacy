
function subvenciones(){
	$('#frmSubvenciones').submit();
}

function consultar(){
	$('#main3').submit();
}

function limpiar() {
	$('#provincia').val('');
	$('#comarca').val('');
	$('#termino').val('');
	$('#subtermino').val('');
	$('#desc_provincia').val('');
	$('#desc_termino').val('');
	$('#desc_comarca').val('');
	$('#latitud').val('');
	$('#longitud').val('');
	$('#rega').val('');
	$('#sigla').val('');
	$('#subexplotacion').val('');
	$('#especie').val('');
	$('#desc_especie').val('');
	$('#regimen').val('');
	$('#desc_regimen').val('');
	$('#limpiar').val("true");
	consultar();
}
 
function alta(){
	$('#methodDE').val('doPantallaAltaExplotacion');
	$('#codParcelaDE').val('-1');
	$('#operacionDE').val("pantallaAltaExplotacion");
	$('#datosExplotaciones').submit();
}

function editar (id, esModoLectura ) {
	$('#methodDE').val('doEditar');
	$('#idExplotacionDE').val(id);
	if(esModoLectura!=null && esModoLectura=='true'){
		$('#modoLectura').val('modoLectura');		
	}	
	$('#datosExplotaciones').submit();
}


function duplicar(id){
	if (confirm('\u00BFEst\u00E1 seguro de que desea duplicar esta explotaci\u00F3n?')) {
		$('#operacion').val('duplicarExplotacion');
		$('#explotacionId').val(id);
		$('#main3').submit();
	}
} 

function borrar(id){
	if (confirm('\u00BFEst\u00E1 seguro de que desea borrar esta explotaci\u00F3n?')) {
		$('#operacion').val('borrarExplotacion');
		$('#explotacionId').val(id);
		$('#main3').submit();
	}
}

function recalcular(){
	if (confirm('\u00BFEst\u00E1 seguro de que desea recalcular el precio de todas las explotaciones?')) {
		$('#operacion').val('recalcularPrecios');
		$('#explotacionId').val('-1');
		$('#main3').submit();
	}
}


function onInvokeAction(id) {	
	var to = document.getElementById("adviceFilter");
	if (to) {
		to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
	}
	var panelInformacion = document.getElementById("panelInformacion");
	if (panelInformacion) {
		panelInformacion.style.display = 'none';
	}
	var panelAlertas = document.getElementById("panelAlertas");
	if (panelAlertas) {
		panelAlertas.style.display = 'none';
	}
	$.jmesa.setExportToLimit(id, '');
	var parameterString = $.jmesa.createParameterStringForLimit(id);
	var frm = document.getElementById('main3');
	$.get('listadoExplotaciones.html?ajax=true&idPoliza='+ $("#idpoliza").val() + '&modoLectura=' + $('#modoLectura').val()+'&'+ decodeURIComponent(parameterString), function(data) {
		$("#grid").html(data);
	});
}


function validaDatosVariablesAjax(){
	
	if($('#modoLectura').val() == 'modoLectura'){
		subvenciones();
	}else{
		$.ajax({
	            url: "validacionesPolizaAjax.html",
	            data: "method=doValidarDatosVariables&idPoliza="+$("#idpoliza").val(),
	            async:true,
	            cache: false,
	            beforeSend: function(objeto){
	            },
	            complete: function(objeto, exito){
	            },
	            contentType: "application/x-www-form-urlencoded",
	            dataType: "json",
	            error: function(objeto, quepaso, otroobj){
	                alert("Error al comprobar la coherencia de los datos variables de las explotaciones: " + quepaso);
	            },
	            global: true,
	            ifModified: false,
	            processData:true,
	            success: function(datos){
	            	if(datos.datosVariablesValidos.valueOf() == "true"){
	            		subvenciones();
	            	}else{
	            		$('#panelInformacion').hide();
	            		$('#panelAlertasValidacion').html(datos.mensaje);
	            		$('#panelAlertasValidacion').show();
	            	}
	            	
	            },
	            type: "POST"
	        });
	}
}

function verInformacionRega(codigoRega) {
	limpiarAlertas();
	$.ajax({
        url: "listadoExplotaciones.html",
        data: "method=doInfoRega&codigoRega="+codigoRega+"&plan="+$('#codplan').val()+"&linea="+$('#codlinea').val(),
        async:true,
        cache: false,
        beforeSend: function() {
        	$.blockUI.defaults.message = '<h4>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
    		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
        	limpiaInfoRega();
        },
        complete: function() {
        	$.unblockUI();
        },
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        error : function(jqXHR, exception) {
			if (jqXHR.status === 0) {
	            msg = 'Verifique la conexi\u00F3n.';
	        } else if (jqXHR.status == 404) {
	            msg = 'P\u00E1gina no encontrada [404].';
	        } else if (jqXHR.status == 500) {
	            msg = 'Error interno del servidor [500].';
	        } else if (exception === 'parsererror') {
	            msg = 'Fallo en el tratamiento del JSON esperado.';
	        } else if (exception === 'timeout') {
	            msg = 'Tiempo de espera agotado.';
	        } else if (exception === 'abort') {
	            msg = 'Petici\u00F3n Ajax cancelada.';
	        } else {
	            msg = 'Error no esperado: ' + jqXHR.responseText;
	        }
			$('#panelAlertasValidacion').html(msg);
			$('#panelAlertasValidacion').show();
		},
        global: true,
        ifModified: false,
        processData:true,
        success : function(datos) {
        	if (datos.errorMsgs.length > 0) {
				var errorMsg = '';
				for (i = 0; i < datos.errorMsgs.length; i++) {
					errorMsg += datos.errorMsgs[i] + '<br/>';
				}
				$('#panelAlertasValidacion').html(errorMsg);
				$('#panelAlertasValidacion').show();
			} else {
				$('#explotacionRegistrada').html(datos.informacionRega.explotacionRegistrada);
				$('#fechaEfecto').html(datos.informacionRega.fechaEfecto);
				$('#fechaVersionCenso').html(datos.informacionRega.fechaVersionCenso);
				$.each(datos.informacionRega.lineas, function (index, value) {
					$('#panelCensoLineas').append(pintaTablaLinea(value));
				});				
				$('#panelInformacionRega').show();
	        	$('#overlay').show();
			}
        },
        type: "POST"
    });	
}

function pintaTablaLinea(jsonLinea) {
	var tabla = '<div style="padding-bottom: 35px">';
	tabla += '<div style="border: 1px solid black;"><strong style="font-size:11px;">Linea: ' + jsonLinea.id + ' -  ' + jsonLinea.descriptivo + '</strong></div>'
	tabla += '<table width="100%"><tr><th class="literalbordeCabecera">Especie</th><th class="literalbordeCabecera">R\u00E9gimen</th><th class="literalbordeCabecera">Censo</th></tr></thead>';
	for (var j = 0; j < jsonLinea.especies.length; j++) {
		if (jsonLinea.especies[j].regimenes.length == 0) {
			tabla += '<tr><td class=literalborde style="TEXT-ALIGN: left">' + jsonLinea.especies[j].especie + ' - ' + jsonLinea.especies[j].descriptivo + '</td><td class=literalborde style="TEXT-ALIGN: left"></td><td class=literalborde style="TEXT-ALIGN: left"></td></tr>';
		} else {
			for (var i = 0; i < jsonLinea.especies[j].regimenes.length; i++) {
				tabla += '<tr><td class=literalborde style="TEXT-ALIGN: left">' + jsonLinea.especies[j].especie + ' - ' + jsonLinea.especies[j].descriptivo + '</td><td class=literalborde style="TEXT-ALIGN: left">' + jsonLinea.especies[j].regimenes[i].regimen + ' - ' + jsonLinea.especies[j].regimenes[i].descriptivo + '</td><td class=literalborde style="TEXT-ALIGN: left">' + jsonLinea.especies[j].regimenes[i].censo + '</td></tr>';
			}
		}
	}
	tabla += '<thead>';
	tabla += '</table></div></div>';	
	return tabla;
}

function limpiaInfoRega() {
	$('#explotacionRegistrada').html('');
	$('#fechaEfecto').html('');
	$('#fechaVersionCenso').html('');
	$('#panelCensoLineas').html('');
}

function limpiarAlertas() {
	$('#panelAlertasValidacion').html('');
	$('#panelAlertasValidacion').hide();
	$('label[id*="campoObligatorio_"]').each(function() {
		$(this).hide();
	});
	$('#panelMensajeValidacion').html('');
	$('#panelMensajeValidacion').hide();
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