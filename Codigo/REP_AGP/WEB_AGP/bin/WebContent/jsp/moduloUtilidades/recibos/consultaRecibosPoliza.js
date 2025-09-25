$(document).ready(function(){
		 
	$("#grid").displayTagAjax();
	
	$('#main3').validate({
		errorLabelContainer: "#panelAlertasValidacion",
		wrapper: "li",
		onfocusout: false,
		rules: {
			"recibo.fecemisionrecibo":{dateITA: true},
			"recibo.codfase" : {digits:true},
			"recibo.codrecibo" : {digits:true}
		},
		messages: {
			"recibo.fecemisionrecibo":{dateITA: "El formato del campo Fecha Emisi�n es dd/mm/YYYY"},
			"recibo.codfase" : {digits: "El campo Fase s�lo puede contener d�gitos"},
			"recibo.codrecibo" : {digits: "El campo N� Recibo s�lo puede contener d�gitos"}
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
		inputField        : "fechaemision",
		button            : "btn_fecha_emision",
		ifFormat          : "%d/%m/%Y",
		daFormat          : "%d/%m/%Y",
		align             : "Br"
	});
	
});

function verDetalle(idRecibo){
	
		var frm = document.getElementById('main3');
		frm.method.value = 'doVerDetalle';
		frm.id.value=idRecibo;
		frm.target="";
		frm.submit();
	
}

function consultar(){
	if ($('#main3').valid()){
		var frm = document.getElementById('main3');
		frm.method.value = 'doConsulta';
		frm.id.value="";
		frm.target="";
		frm.submit();
	}
}

function limpiar(){
	$("#plan").val('');
	$("#linea").val('');
	$("#desc_linea").val('');
	$("#colectivo").val('');
	$("#dccolectivo").val('');
	$("#nombreAseg").val('');
	$("#cifnif").val('');
	$("#fase").val('');
	$("#fechaemision").val('');
	$("#nrecibo").val('');
	$("#codTipificacionRecibo").val('');
	consultar();
}

function imprimir(idRecibo){
	var frm = document.getElementById('main3');
	frm.method.value = 'doVerPDFPoliza';
	frm.id.value = idRecibo;
	frm.action = frm.action + '?rand=' + Math.random();
	frm.target="_blank";
	frm.submit();
}

function imprimirPolizaOrigen (){
	var frm = document.getElementById('main3');
	frm.method.value = 'doVerPDFPolizaOrigen';
	frm.action = frm.action + '?rand=' + Math.random();
	frm.target="_blank";
	frm.submit();
}
//DAA 04/01/2013
function imprimirBorradorReducido(reducido){
	if(reducido){
		$('#StrImprimirReducida').val('true');
	}else{
		$('#StrImprimirReducida').val('false');
	}
	if ($("#method").val == 'C'){
		$("#method").val('doInformePolizaComplementaria');
	} else {
		$("#method").val('doInformePoliza');
	}
	
	var frm = document.getElementById('print');
	frm.action = frm.action + '?rand=' + Math.random();
	$('#print').attr('target', '_blank');
	$("#print").submit();
	
}

function volver(){
	$(window.location).attr('href', 'utilidadesPoliza.html?rand=' + UTIL.getRand() +'&recogerPolizaSesion=true');
}
