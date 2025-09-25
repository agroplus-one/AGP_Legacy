$(function(){
		$("#grid").displayTagAjax();
});
	
	
$(document).ready(function() {	

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
	        inputField        : "tx_fecha_acta",
	        button            : "btn_fecha_acta",
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
	        inputField        : "tx_fecha_pago",
	        button            : "btn_fecha_pago",
	        ifFormat          : "%d/%m/%Y",
	        daFormat          : "%d/%m/%Y",
	        align             : "Br"			        	        
      	});
      	
      	
      	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
    	document.getElementById("main3").action = URL;    
     
		$('#main3').validate({
			errorLabelContainer: "#panelAlertasValidacion",
			wrapper: "li",
			 onfocusout: false,		 			  				 
			 rules: {					 
			 	"fechaActa":{dateITA: true},
			 	"fechaPago":{dateITA: true}
			 },
			 messages: {					 	
			 	"fechaActa":{dateITA: "El formato del campo Fecha Ocurrencia pago es dd/mm/YYYY"},
			 	"fechaPago":{dateITA: "El formato del campo Fecha Peritaci�n pago es dd/mm/YYYY"}
			 }
		});
});
	      	




function consultarFiltroActas(){
	/* Primero comprobamos que se haya insertado alg�n valor en los campos del filtro*/
	if (comprobarCamposFiltroActa()){
		$.blockUI.defaults.message = '<h4> Procesando petici�n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		
		/* Enviamos los datos insertados en el filtro*/
   		var frmmain3=document.getElementById('main3');
   		
   		var frm=document.getElementById('frmConsultaFiltroActa');
   		frm.serieActa_consFiltro.value = frmmain3.serieSiniestroActa.value;
   		frm.numeroActa_consFiltro.value     = frmmain3.numeroSiniestroActa.value;
		frm.libroActa_consFiltro.value = frmmain3.libroSiniestroActa.value;
		frm.fechaActa_consFiltro.value  = frmmain3.tx_fecha_acta.value; 		
		frm.fechaPagActa_consFiltro.value  = frmmain3.tx_fecha_pago.value;
		frm.method_ha.value = 'doActas';

		$("#frmConsultaFiltroActa").submit();
		
	}else{
		$("#panelInformacion").html("");
		$("#panelInformacion").hide();
		
		$("#panelAlertas").html("");
		$("#panelAlertas").hide();
		
		$('#panelAlertasValidacion').html("Debe Insertar alg�n valor en el Filtro.");
		$('#panelAlertasValidacion').show();
	}

}

function comprobarCamposFiltroActa(){
	
	var resultado = false;
	
 	if (!resultado && $("#serieSiniestroActa").val() != ''){
 		resultado = true;
 	}
 	
 	if (!resultado && $("#numeroSiniestroActa").val() != ''){
 		resultado = true;
 	}
 	
 	if (!resultado && $("#libroSiniestroActa").val() != ''){
 		resultado = true;
 	}
 	
 	if (!resultado && $('#tx_fecha_acta').val() != ''){
 		resultado = true;
 	}
 	
 	if (!resultado && $('#tx_fecha_pago').val() != ''){
 		resultado = true;
 	} 

 	return resultado;
}

function limpiar(){
	$("#serieSiniestro").val('');
	$("#numeroSiniestro").val('');
	$("#libroSiniestro").val('');
	
	$("#tx_fecha_acta").val('');
	$("#tx_fecha_pago").val('');
	
	consultar();
}

function consultar(){
	$.blockUI.defaults.message = '<h4> Procesando petici�n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	
	$("#method").val("doActas");
	$("#main3").submit();
}

function volverSinGan(){	
	$("#method").val("doConsulta");
	$("#main3").submit();

}

function imprimirPdfActa(serieSinGanado, numSinGanado, letraSinGanado){
	
	var frm=document.getElementById('frmPdfGanado');
	frm.method_pdf.value="doPdfActaGanado";
	
	frm.serieSinGanadoPdf.value  = serieSinGanado;
	frm.numSinGanadoPdf.value    = numSinGanado;
	frm.letraSinGanadoPdf.value  = letraSinGanado;
	frm.target="_blank";
	frm.submit();       		       		
}

function imprimirCartaPago(serieSinGanCarta, numSinGanCarta, letraSinGanCarta){
	
	var frm=document.getElementById('frmPdfCartaPagoGan');
	frm.method_pdf_act.value="doPdfCartaPagoGanado";
	
	frm.serieSinPdfCartaGan.value  = serieSinGanCarta;
	frm.numSinPdfCartaGan.value    = numSinGanCarta;
	frm.letraSinPdfCartaGan.value  = letraSinGanCarta;
	frm.target="_blank";
	frm.submit();       		       		
}

function formatearFecha(fecha){
	var hoy = fecha.split(" ");
	var fecha = hoy[2] +" "+ hoy[1] + " "+ hoy[5];
	var fechaVar = new Date(fecha);
	
	var dia = fechaVar.getDate();
	var mes = fechaVar.getMonth() +1;
	var ano = fechaVar.getFullYear();
	
	
	if(mes < 10){
		mes = "0" + mes;
	}
	return dia + "/" + mes + "/" + ano;
}

function exportarExcel(){
	
	if (comprobarCamposFiltroActa()){
		
		/* Enviamos los datos insertados en el filtro */
		var frmmain3=document.getElementById('main3');
		
		var frm=document.getElementById('frmConsultaFiltroActa');
		frm.serieActa_consFiltro.value = frmmain3.serieSiniestroActa.value;
		frm.numeroActa_consFiltro.value     = frmmain3.numeroSiniestroActa.value;
		frm.libroActa_consFiltro.value = frmmain3.libroSiniestroActa.value;
		frm.fechaActa_consFiltro.value  = frmmain3.tx_fecha_acta.value; 		
		frm.fechaPagActa_consFiltro.value  = frmmain3.tx_fecha_pago.value;
		frm.method_ha.value = 'doExportarActasExcel';
		$("#frmConsultaFiltroActa").submit();
		
	}
	else {
	
		$("#main3").validate().cancelSubmit = true;
		$('#method').val('doExportarActasExcel');
		$('#main3').submit();			
	}
}

