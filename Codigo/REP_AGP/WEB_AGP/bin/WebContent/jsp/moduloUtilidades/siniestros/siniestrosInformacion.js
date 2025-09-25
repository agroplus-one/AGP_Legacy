	
	$(document).ready(function() {	
		var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
	    document.getElementById("main3").action = URL;
		
//	    limpiar();
	    prineraEntrada();
	    $("#main3").validate({
			  errorLabelContainer : "#panelAlertasValidacion",
			 
			  wrapper : "li", highlight : function(element,errorClass) {
					$("#campoObligatorio_" + element.id).show();
				},
			  unhighlight : function(element,errorClass) {
					$("#campoObligatorio_" + element.id).hide();
				},
			  rules: {

				"numHojaCampo": {digits: true}, 
				"tipoHoja": {digits: true}, 
				"situacionHoja": {digits: true}, 
				"serie": {digits: true}, 
				"numActa": {digits: true}, 
				"situacionActa": {digits: true}
				   },
			  messages: {
				"numHojaCampo":  {digits: "El campo N�mero de las Hojas de campo s&oacute;lo puede contener d&iacute;gitos"},
				"tipoHoja":{digits: "El campo Tipo de hoja s&oacute;lo puede contener d&iacute;gitos"},
				"situacionHoja":{digits: "El campo Situaci�n de las Hojas de campo s&oacute;lo puede contener d&iacute;gitos"},
				"serie":{digits: "El campo Serie de las Actas de tasaci�n s&oacute;lo puede contener d&iacute;gitos"},
				"numActa":{digits: "El campo N�mero Acta s&oacute;lo puede contener d&iacute;gitos"},
				"situacionActa":{digits: "El campo Situaci�n del Acta s&oacute;lo puede contener d&iacute;gitos"}
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
	        inputField        : "tx_fechaTasacion",
	        button            : "btn_fechatasacion",
	        ifFormat          : "%d/%m/%Y",
	        daFormat          : "%d/%m/%Y",
	        align             : "Br"			        	        
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
	        inputField        : "tx_fechaActa",
	        button            : "btn_fechaActa",
	        ifFormat          : "%d/%m/%Y",
	        daFormat          : "%d/%m/%Y",
	        align             : "BL"			        	        
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
	        inputField        : "tx_fechaPago",
	        button            : "btn_fechaPago",
	        ifFormat          : "%d/%m/%Y",
	        daFormat          : "%d/%m/%Y",
	        align             : "BL"			        	        
	  	});
	});


function pdfHoja(refPoliza,codPlan,numHojaCampo, tipoHoja){
	var frm = document.getElementById('frmPdfHojaCampo');
//	document.getElementById('refPoliza').value= refPoliza;
//	document.getElementById('codPlan').value= codPlan;
//	document.getElementById('numHojaCampo').value= numHojaCampo;
//	document.getElementById('tipoHoja').value= tipoHoja;
	frm.refPoliza_hc.value= refPoliza;
	frm.codPlan_hc.value= codPlan;
	frm.numHojaCampo_hc.value= numHojaCampo;
	frm.tipoHoja_hc.value= tipoHoja;
	frm.target="_blank";
	frm.submit();
}

function pdfActa(serie, numActa){
	var frm = document.getElementById('frmPdfActaTasacion');
	frm.serie_AT.value= serie;
	frm.numActa_AT.value= numActa;
	frm.target="_blank";
	frm.submit();
}

	

function prineraEntrada(){
	var frm = document.getElementById('main3');
	var optHC = main3.tipoRegistroHC;
	var optAT = main3.tipoRegistroAT;
	if (!optHC.checked){
		limpiaHojasCampo();
		disabledHojasCampo(true);
	}
	if (!optAT.checked){
		limpiaActasTasacion();
		disabledActasTasacion(true);	
	}
	
}	
	
	
	
	
	
	function consultar(){	
		var frm = document.getElementById('main3');
		frm.target="";
		frm.method.value="doConsulta";	
		$('#origenLlamada').val('consultar');	
		frm.submit();
	}
	
	
	
	function volver(){
		$(window.location).attr('href', 'utilidadesPoliza.html?rand=' + UTIL.getRand() +'&recogerPolizaSesion=true'); 		
		
//		var frm=document.getElementById("frmVolver");
//		frm.submit();
	}	
					
	
	
	// ****************** 
	function limpiar(){
		limpiaHojasCampo();
		disabledHojasCampo(true);
		limpiaActasTasacion();
		disabledActasTasacion(true);	
		consultar();
	}	
	
	function seleccionHojasCampo(){
		limpiaActasTasacion();
		disabledActasTasacion(true);
		disabledHojasCampo(false);
	}
	
	function disabledHojasCampo(disabled){
		document.getElementById("numHojaCampo").disabled=disabled;
		document.getElementById("tx_fechaTasacion").disabled=disabled;
		document.getElementById("tipoHoja").disabled=disabled;
		document.getElementById("tipoHojaDesc").disabled=disabled;
		document.getElementById("situacionHoja").disabled=disabled;
		document.getElementById("situacionHojaDesc").disabled=disabled;
		document.getElementById("btn_fechatasacion").disabled=disabled;
		document.getElementById("lupaTipoHoja").disabled=disabled;
		document.getElementById("lupaSituacionHoja").disabled=disabled;
		
	}
	
	function limpiaHojasCampo(){
		document.getElementById("tipoRegistroHC").checked=false;
		document.getElementById("numHojaCampo").value="";
		document.getElementById("tx_fechaTasacion").value="";
		document.getElementById("tipoHoja").value="";
		document.getElementById("tipoHojaDesc").value="";
		document.getElementById("situacionHoja").value="";
		document.getElementById("situacionHojaDesc").value="";
	}
	
	function seleccionActasTasacion(){
		limpiaHojasCampo();
		disabledHojasCampo(true);
		disabledActasTasacion(false);
	}
	
	function disabledActasTasacion(disabled){
		document.getElementById("serie").disabled=disabled;
		document.getElementById("numActa").disabled=disabled;
		document.getElementById("tx_fechaActa").disabled=disabled;
		document.getElementById("situacionActa").disabled=disabled;
		document.getElementById("situacionActaDes").disabled=disabled;
		document.getElementById("tx_fechaPago").disabled=disabled;
		document.getElementById("btn_fechaActa").disabled=disabled;
		document.getElementById("lupaSituacionActa").disabled=disabled;
		document.getElementById("btn_fechaPago").disabled=disabled;
	}
	
	function limpiaActasTasacion(){
		document.getElementById("tipoRegistroAT").checked=false;
		document.getElementById("serie").value="";
		document.getElementById("numActa").value="";
		document.getElementById("tx_fechaActa").value="";
		document.getElementById("situacionActa").value="";
		document.getElementById("situacionActaDes").value="";
		document.getElementById("tx_fechaPago").value="";
	}
// *********************************************************
	
	function onInvokeAction(id) {
		var to = document.getElementById("adviceFilter");
		to.innerHTML = "<img src='jsp/img/ajax-loading.gif' align='absmiddle'>";
		$.jmesa.setExportToLimit(id, '');
		var parameterString = $.jmesa.createParameterStringForLimit(id);
		var frm = document.getElementById('main3');
		$.get('siniestrosInformacion.html?ajax=true&' +decodeURIComponent(parameterString),
			function(data) {
				$("#grid").html(data);
			});
	}
	
	function onInvokeExportAction(id) { 
		var parameterString = $.jmesa.createParameterStringForLimit(id);
		location.href = 'siniestrosInformacion.html?ajax=false&export=true&' + parameterString;
	}
	