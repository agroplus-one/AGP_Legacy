$(document).ready(function(){
	$("#panelCalculofinanciacion").draggable();	
	//var porcentaje = document.getElementById("porcentajeCheck").checked=true;
	seleccionaImporte();
		
});	
		

function cerrarPopUpCalculoFinanciacion(){
    
	// limpiamos alertas
	$('#panelAlertasValidacion_cf').html("");
	$('#panelAlertasValidacion_cf').hide();
	//vaciamos campos
	
	$('#porcentajeCosteTomador_txt').val("");
	$('#importeFinanciar_txt').val("");
	$('#importeAval_txt').val("");
	
	//cerramos div "inferiores"
	$('#panelCalculofinanciacion').hide();
    $('#overlay').hide();
}

function seleccionaImporte(){
	$('#panelAlertasValidacion_cf').html("");
	$('#panelAlertasValidacion_cf').hide();	
	
	var porcentaje = document.getElementById("porcentajeCheck");
	var impFinan = document.getElementById("importeCheck");
	var impAval = document.getElementById("importeAvalCheck");
	
	var porcentajeTxt = document.getElementById("porcentajeCosteTomador_txt");
	var impFinanTxt = document.getElementById("importeFinanciar_txt");
	var impAvalTxt = document.getElementById("importeAval_txt");
	//document.getElementById(varActivas[i]).disabled = false;
	if (porcentaje.checked){
		//$('#importeFinanciar_txt').val("");
		//$('#importeAval_txt').val("");
		impFinanTxt.value="";
		impFinanTxt.disabled = true;
		impAvalTxt.value="";
		impAvalTxt.disabled=true;
		porcentajeTxt.disabled=false;
	}else if (impFinan.checked){
		//$('#porcentajeCosteTomador_txt').val("");
		//$('#importeAval_txt').val("");
		porcentajeTxt.value="";
		porcentajeTxt.disabled = true;
		impAvalTxt.value="";
		impAvalTxt.disabled = true;
		impFinanTxt.disabled=false;
	}else if(impAval.checked){
		//$('#porcentajeCosteTomador_txt').val("");
		//$('#importeFinanciar_txt').val("");
		porcentajeTxt.value="";
		porcentajeTxt.disabled = true;
		impFinanTxt.value="";
		impFinanTxt.disabled = true;
		impAvalTxt.disabled=false;
	}
}

function calcularFinanciacion(){
	var resVal = validaFrmFinanciacion();
	if(resVal==""){
		$('#overlay').hide();
		$('#panelCalculofinanciacion').hide();
		$.blockUI.defaults.message = '<h4> Realizando el c\u00E1lculo de financiaci\u00F3n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		
		if($('#esFinanciacionCpl').val() == "false"){			
			//alert ('financiacion principal');
			$('#origenllamada_cf').val("financiacion");
			$('#method_cf').val("doFinanciar");
			$('#frmCalculoFinanciacion').attr('action','financiacionService.html');
		}
		else {
			//alert ('financiacion complementaria');
			$('#operacion_cf').val("");
			$('#origenllamada_cf').val("financiacion");
			$('#method_cf').val("doCalcular");
			$('#frmCalculoFinanciacion').attr('action','webservicesCpl.html');
		}
		$('#frmCalculoFinanciacion').submit();
		
	}else{
		$('#panelAlertasValidacion_cf').html(resVal);
		$('#panelAlertasValidacion_cf').show();
	}
}


function validaFrmFinanciacion(){
	var porcentajeChk = document.getElementById("porcentajeCheck");
	var impFinanChk =  document.getElementById("importeCheck");
	var impAvalChk = document.getElementById("importeAvalCheck");
	var pctMinFinanc=document.getElementById("pctMinFinanc_cf");
	
	var	limitePctImporteAval =(100 - pctMinFinanc.value);
	
	
	//alert(pctMinFinanc.value);
	//alert(limitePctImporteAval);
	
	if (porcentajeChk.checked){
		var imp = document.getElementById("porcentajeCosteTomador_txt").value;
		if(isNullOrEmpty(imp)){
			return "El campo porcentaje del tomador es obligatorio al estar seleccionado.";
		}
		if(!valorEntero3Decimal2(imp)){
			return "El campo porcentaje del tomador es debe de ser un valor num\u00E9rico de hasta tres d\u00EDgitos enteros y dos decimales.";
		}
		if(!validaRangoPorcentajeTomador(imp, pctMinFinanc.value)){
			return "El valor del campo porcentaje del tomador debe estar comprendido entre " + pctMinFinanc.value + " y 100";
		}
	}else if (impFinanChk.checked){
		var imp = document.getElementById("importeFinanciar_txt").value;
		if(isNullOrEmpty(imp)){
			return "El campo importe de financiaci\u00F3n es obligatorio al estar seleccionado.";
		}
		if(!valorEntero9Decimal2(imp)){
			return "El campo importe de financiaci\u00F3n debe de ser un valor num\u00E9rico de hasta 9 d\u00EDgitos en su parte entera y dos decimales";
		}
		if(!validaValorImporteFinanciacionMin(imp, pctMinFinanc.value)){
			return "El campo importe debe ser mayor o igual al "+ pctMinFinanc.value + "% del importe neto del tomador";
		}
		if(!validaValorImporteFinanciacionMax(imp)){
			return "El campo importe debe ser menor o igual al 100% del importe neto del tomador";
		}
	}else if(impAvalChk.checked){
		var imp = document.getElementById("importeAval_txt").value;
		if(isNullOrEmpty(imp)){
			return "El campo importe del aval es obligatorio al estar seleccionado.";
		}
		if(!valorEntero9Decimal2(imp)){
			return "El campo importe del aval debe de ser un valor num\u00E9rico de hasta 9 d\u00EDgitos en su parte entera y dos decimales";
		}
		if(!validaImporteAval(imp, limitePctImporteAval)){
			return "El campo Importe Aval debe ser menor o igual al " + limitePctImporteAval + "% del importe neto del tomador";
		}
	}
	return "";
}

function isNullOrEmpty(str) { 
	return (!str || /^\s*$/.test(str)); 
}

function valorEntero3Decimal2(value){
    	return /^\d{0,3}([.]\d{1,2})?$/.test(value);	    	
}

function valorEntero9Decimal2(value) {
    	return /^\d{0,9}([.]\d{1,2})?$/.test(value);
}	 

function validaRangoPorcentajeTomador(value,pctMinFinanc){
	var valueStr=value.replace(",",".");
	var valueFloat= parseFloat(valueStr);
	
	var pctMinStr=pctMinFinanc.replace(",",".");
	var pctMinFloat= parseFloat(pctMinStr);
	return (valueFloat>=pctMinFloat && valueFloat<=100);
}

function validaValorImporteFinanciacionMin(value, pctMinFinanc) {
	var pctMinStr=pctMinFinanc.replace(",",".");
	var pctMinFloat= parseFloat(pctMinStr);
	pctMinFloat= parseFloat(pctMinStr/100).toFixed(4);
	
	var impTomFrm = document.getElementById("costeTomador_cf");
	var impTomStr =impTomFrm.value;
	impTomStr=impTomStr.replace(/\./g,"");
	impTomStr=impTomStr.replace(",",".");
	var impTom= parseFloat(impTomStr);
	var imp = impTom * pctMinFloat;
	imp = imp.toFixed(2);
    return +value>=+imp;  
}

function validaValorImporteFinanciacionMax(value) {
	var impTomFrm = document.getElementById("costeTomador_cf");
	var impTomStr =impTomFrm.value;
	impTomStr=impTomStr.replace(/\./g,"");
	impTomStr=impTomStr.replace(",",".");
	var impTom= parseFloat(impTomStr);
    return +value<=+impTom;  
}

function validaImporteAval(value, porcentaje) {
	 var impTomFrm = document.getElementById("costeTomador_cf");
	 var pct=porcentaje;
	 pct=pct/100;
	 pct = pct.toFixed(2);
//	 var porcentaje = 0.67;
	 var impTomStr =impTomFrm.value;
	 impTomStr=impTomStr.replace(/\./g,"");
	 impTomStr=impTomStr.replace(",",".");
	 var impTom= parseFloat(impTomStr);
	 var imp = impTom * pct;
	 imp = imp.toFixed(2);
	 return +value<=+imp;	 
	
}