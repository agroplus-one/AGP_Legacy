
   
$(document).ready(function(){
	   var URL = UTIL.antiCacheRand(document.getElementById("volver").action);
   document.getElementById("volver").action = URL;    
   
   var URL = UTIL.antiCacheRand(document.getElementById("importes").action);
   document.getElementById("importes").action = URL;    
   
   if ($('#muestraBotonDescuentos').val() == "true"){
    	$('#btnDescuentos').show();
   }
   if ($('#muestraBotonRecargos').val() == "true"){
    	$('#btnRecargos').show();
   }
   if($('#modoLectura').val() == 'modoLectura'){
	    $('#btnGrabacionProvisional').hide();
   		$('#btnVolver').hide();
	    $('#btnSalir').show();
	    $('#btnFormaPagoLectura').show();
	    $('#btnImprimir').show();
	    
//	    if ($('#mpPagoC').val() == "true"){
//	    	$('#btnPagosLectura').show();
//   		}
	}else{
	    $('#btnGrabacionProvisional').show();
   	    $('#btnVolver').show();   
   	    $('#btnParcelas').show();
   }
   
   
   
   if($('#tieneSubvenciones').val() == "false") {
	    $('#panelInfo').show();
		$('#overlay').show();
	}
   
   if($('#grProvisionalOK').val() == "true"){
//	   $("#grProvisionalOK").val(datos.grProvisionalOK);
//	   $("#grProvisionalOK_da").val(datos.grProvisionalOK);
	   $('#btnGrabacionProvisional').hide();
	   $('#btnGrabacionDefinitiva').show();
	   $('#btnImprimir').show();
   }
 
   if ($('#vieneDeUtilidades').val() != 'true') {
		document.getElementById('frmDatosAval').cicloPoliza.value="cicloPoliza";
	}
   
   muestraFinanciar();
   
});
	   		
function imprimir(){
	var frm = document.getElementById('imprimirCpl');
	frm.operacion.value = 'imprimirPoliza';
	frm.target="_blank";
	frm.submit();
} 
	   	
function showdata(id){
     ID = document.getElementById(id);
	    
     if(ID.style.display == '') {
          ID.style.display = 'none';
     }
     else {
          ID.style.display = '';
     }
}

//function continuar() {
//	$.blockUI.defaults.message = '<h4>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
//	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
//	$('#importes').submit();
//}


function grabacionProvisional(){
	
	$.blockUI.defaults.message = '<h4> Grabando en Provisional.<BR>Espere un momento, por favor...  <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
	
	var frm = document.getElementById('importes');
	var id = frm.idpoliza.value;
	$.ajax({
		url : "grabacionPoliza.html",
		data : "operacion=&idpoliza=" + id + "&grProvisional=true",
		async : true,
		contentType : "application/x-www-form-urlencoded",
		dataType : "json",
		cache: false,
		global : false,
		ifModified : false,
		processData : true,
		error : function(objeto, quepaso, otroobj) {
			$.unblockUI();
			alert("Error al pasar la p\u00F3liza a estado provisional: " + quepaso);
		},
		success : function(datos) {
			$.unblockUI();
			$("#panelMensajeValidacion").html(datos.mensaje);
			$("#panelMensajeValidacion").show();
			if(datos.mostrarBtnDef=="SI"){
				$('#btnGrabacionProvisional').hide();
				$('#btnGrabacionDefinitiva').show();
			}
			if(datos.grProvisionalOK=="true"){
				$("#grProvisionalOK").val(datos.grProvisionalOK);
				$("#grProvisionalOK_da").val(datos.grProvisionalOK);
			
			if(datos.periodoFracc!=""){
				$("#periodoFracc").val(datos.periodoFracc);
			}
				grabacionProvisionalOk();
			}
		},
		type : "GET"
	});
}


function grabacionProvisionalOk(){	
	$('#btnGrabacionProvisional').hide();
	muestraFinanciar();
	
	// Muestra el boton 'Cerrar' de los popups de descuentos y recargos
	$(".cerrarPopUp").each(function(){$(this).show();});
	// Oculta los botones 'Aplicar' y 'Cancelar' de los popups de descuentos y recargos
	$(".aplicarPopUp").each(function(){$(this).hide();}); 
	
	// Convierte a modo lectura las cajas de texto de los popups de descuentos y recargos
	$("[id^=recElegido-]").each(function(){$(this).attr('readonly', true);});
	$("[id^=dctoElegido-]").each(function(){$(this).attr('readonly', true);});
	
	//Escondemos el boton de Calcular la financiacion
	$('#btnCalcular_da').hide();
	//Escondemos el boton de Volver
	$('#btnVolver').hide();
	//Mostramos los botones de imprimir y salir
	$('#btnImprimir').show();
	$('#btnSalir').show();
}


function volver(){
	$('#methodVolver').val('doConsulta');
	$('#volver').submit();
}

function cerrarPopUp(){
    $('#panelInfo').hide();
    $('#overlay').hide();
}

function salir(){
	if($('#vieneDeUtilidades').val() == 'true') {
		$('#volverUtilidadesPoliza').submit();
	}
	else{
		$(window.location).attr('href', 'seleccionPoliza.html');
	}
}

function showdata(){
	ID = document.getElementById('distrCostesCpl');
	if(ID.style.display == '') {
          ID.style.display = 'none';
          $('#btnDesglose').text("Mostrar Desglose");
    }
    else {
          ID.style.display = '';
          $('#btnDesglose').text("Ocultar Desglose");
    }
}
function datosPago(){
	$('#consultaDetallePoliza').submit();
}

function getModuleAndSave(checkRevision, formulario, campo, idpoliza, importeFin) {
	if(importeFin != null){
		$('#netoTomadorFinanciadoAgr').val(importeFin);
	}
	if ($('#esAgr').val() == 'false'){
		$('#esAgrSend').val($('#esAgr').val());
		$('#esSaecaVal').val(importeFin);
	}
	
	var hiddens = document.importes.idEnvioComp;
	var radios = document.importes.modElegido;
	var seleccionado = false;
	for ( var i = 0; i < radios.length; i++) {
		if (radios[i].checked == true) {
			document.grabar.modSeleccionado.value = radios[i].value;
			document.grabar.idEnvio.value = document.importes.elements['idEnv' + (i)].value;
			document.grabar.importeSeleccionado.value = document.importes.elements['importeC' + (i)].value;

			document.irAPagos.modSeleccionado.value = radios[i].value;
			document.irAPagos.idEnvio.value = document.importes.elements['idEnv'+ (i)].value;
			document.irAPagos.importeSeleccionado.value = document.importes.elements['importeC'	+ (i)].value;

			document.grProvisional.modSeleccionado.value = radios[i].value;
			document.grProvisional.idEnvio.value = document.importes.elements['idEnv'+ (i)].value;
			
			document.grProvisional.importeSeleccionado.value = document.importes.elements['importeC' + (i)].value;
			
			seleccionado = true;
			break;
		}
	}
	
	// Si no se ha seleccionado ningun modulo
	if (!seleccionado) {
		alert("Debe seleccionar un m\u00F3dulo para continuar...");
	} else {
		$.blockUI.defaults.message = '<h4> Grabando los importes.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({
			overlayCSS : {
				backgroundColor : '#525583'
			}
		});
		if (checkRevision == 'noRevision') {
			document.grabar.noRevPrecioProduccion.value = 'true';
		}
		
		if (formulario == 'grProvisional' || formulario == 'grabar') {
		// Se lanza la llamada ajax para guardar la distribucion de costes
		ajaxGuardarDistCoste(formulario);
		}
		// Cualquier otra accion
		else {
			generales.enviarForm(formulario, campo, idpoliza);
		}
	}
}

function financiar(importeTomador, financiacionSeleccionada, pctMinFinanc) {								
	$('#pctMinFinanc_cf').val(pctMinFinanc);
	$('#costeTomador_cf').val(importeTomador);
//	$('#condicionesFraccionamiento').val($('#periodoFracc').val());
	//$('#costeTomador_cf').val(importeTomador.replace ('.',','));
	$('#financiacionSeleccionada').val(financiacionSeleccionada);
	$('#costeTomador_lb').html(importeTomador);
	$('#panelCalculofinanciacion').show();
	$('#overlay').show();
}

function financiarLectura(importeTomador, financiacionSeleccionada, periodoFracc, valorOpcionFracc, opcionFracc) {
	$('#costeTomador_lb').html(importeTomador);
	if($('#periodoFracc').val()==""){
		$('#condicionesFraccionamiento').val(periodoFracc);
	}else{
		$('#condicionesFraccionamiento').val($('#periodoFracc').val());
	}
	
	
	switch (parseInt(opcionFracc)) {
	    case 0:
	    	$('#porcentajeCheck').attr('checked', 'checked');
			$('#porcentajeCosteTomador_txt').val(valorOpcionFracc);
	        break;
	    case 1:
	    	$('#importeCheck').attr('checked', 'checked');
			$('#importeFinanciar_txt').val(valorOpcionFracc);
			break;
	    case 2:
	    	$('#importeAvalCheck').attr('checked', 'checked');
			$('#importeAval_txt').val(valorOpcionFracc);
			break;
	}

	/* $('#panelInformacion').find("input, select").attr('disabled', 'disabled'); */
	$('#condicionesFraccionamiento').attr('disabled', 'disabled');
	$('#porcentajeCheck').attr('disabled', 'disabled');
	$('#importeCheck').attr('disabled', 'disabled');
	$('#importeAvalCheck').attr('disabled', 'disabled');
	$('#porcentajeCosteTomador_txt').attr('disabled', 'disabled');
	$('#importeFinanciar_txt').attr('disabled', 'disabled');
	$('#importeAval_txt').attr('disabled', 'disabled');
	$('#btnCalcular_da').hide();
	($('#panelCalculofinanciacion')).show();
	$('#overlay').show();
}

function muestraFinanciar(){
	
	var btnFinanciarLectura = document.getElementById("btnFinanciarLectura");
	var btnFinanciar = document.getElementById("btnFinanciar");
	
	var muestraBotonFinanciar = document.getElementById("muestraBotonFinanciar").value;
	var esFraccAgr = document.getElementById("esFraccAgr").value;
	var periodoFracc = document.getElementById("periodoFracc").value;
	var grProvisionalOK  = document.getElementById("grProvisionalOK").value; 
	var modoLectura = document.getElementById("modoLectura").value; 
	
	if(muestraBotonFinanciar == "true"){
		if(modoLectura=='modoLectura'){
			btnFinanciarLectura.style.display = '';
			btnFinanciar.style.display = 'none';		
		}else{
		
			if(grProvisionalOK=="true"){
			
				if(periodoFracc!=""){
				
					btnFinanciarLectura.style.display = '';
					btnFinanciar.style.display = 'none';
				}else{
					
					btnFinanciarLectura.style.display = 'none';
					btnFinanciar.style.display = 'none';		
				}
			}else{
				
				if(periodoFracc!=""){
					btnFinanciarLectura.style.display = 'none';
					btnFinanciar.style.display = 'none';
				}else{
					btnFinanciarLectura.style.display = 'none';
					btnFinanciar.style.display = '';
				}
			}
		}
	}else{
		btnFinanciarLectura.style.display = 'none';
		btnFinanciar.style.display = 'none';		
	} 
	
	document.getElementById("frmDatosAval").muestraBotonFinanciar_da.value=muestraBotonFinanciar;
}

function enabledTypeElemntsForm(form, type, disabled){	
	var frm = document.getElementById(form);
	for(var i=0;i<frm.elements.length;i++){
		if(frm.elements[i].type == 'text'){
			frm.elements[i].disabled= disabled;			
		}
	}
}

function parcelas(){
	$("#method").val("doConsulta");
	$('#polizaCompl').submit();
}

function showFormaPago(idpoliza){
	var frm = document.getElementById("frmDatosAval");
	frm.method.value = "doMostrar";
	frm.idpoliza.value = idpoliza;
	frm.action="eleccionFormaPago.html";
	frm.submit();
}

function descargarXmlCalculo(idPoliza) {
	var frm = document.getElementById('formUtilidades');
	
	var tipo = "CALC";
	var filaComparativa = "";
	
	var idPol = document.getElementById('idPoliza').value;
	
	var filaComparativa = "";
	
	/* ESC-15883 ** MODIF TAM (16.11.2021) ** Inicio */
	if (!validaXmlAjax(filaComparativa, idPoliza, tipo)) {
		$('#panelAlertasValidacion').html("No se ha encontrado XML de Cálculo");
		$('#panelAlertasValidacion').show();
	} else {
		frm.method.value = 'doGetXMLCalculo';
		frm.submit();
	}
	/* ESC-15883 ** MODIF TAM (16.11.2021) ** Fin */
}

function descargarXmlValidacion(idPoliza) {
	
	var frm = document.getElementById('formUtilidades');
	var tipo = "VAL";
	var filaComparativa = "";
	
	/* ESC-15883 ** MODIF TAM (16.11.2021) ** Inicio */
	if (!validaXmlAjax(filaComparativa, idPoliza, tipo)) {
		$('#panelAlertasValidacion').html("No se ha encontrado XML de Validación");
		$('#panelAlertasValidacion').show();
	} else {
		frm.method.value = 'doGetXMLValidacion';
		frm.submit();
	}
	/* ESC-15883 ** MODIF TAM (16.11.2021) ** Fin */
}

/* Nueva Función para comprobar si hay xml para devolver antes de mostrar el xml */
function validaXmlAjax(filaComparativa, idPoliza, tipo){
	
	var filaComparativa;
	var valida = false;
	
	$.ajax({
        url: "utilidadesXML.run",
        data: "method=doValidarXMLAjax&idPoliza="+idPoliza+"&filaComparativa="+filaComparativa+"&valor="+tipo,
        async:        false,
		contentType:  "application/x-www-form-urlencoded",
		dataType:     "json",
		global:       false,
		ifModified:   false,
		processData:  true,
		error: function(objeto, quepaso, otroobj){
		},
		success: function(resultado){
			if (resultado.alert != null && resultado.alert != ""){
				valida = false;
			}else{
				valida = true;
			}
		},
		beforeSend : function() {
		},
		type: "GET"
	});

	return valida;		
}