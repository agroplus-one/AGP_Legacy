

$(function(){
			$("#grid").displayTagAjax();
});
	
	

$(document).ready(function() {	

	valor = $("#grupoNegocioSel").val();
	$('#idgrupo').selectOptions(valor);
							
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
	        inputField        : "tx_fecha_comunicacion",
	        button            : "btn_fecha_comunicacion",
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
	        inputField        : "tx_fecha_retirada",
	        button            : "btn_fecha_retirada",
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
			 	"fechacomunicacion":{dateITA: true},
			 	"fechaRetirada":{dateITA: true}
			 },
			 messages: {					 	
			 	"fechacomunicacion":{dateITA: "El formato del campo Fecha Ocurrencia pago es dd/mm/YYYY"},
			 	"fechaRetirada":{dateITA: "El formato del campo Fecha Peritaci&oacute;n pago es dd/mm/YYYY"}
			 }
		});
		
});


function consultar(){
	$.blockUI.defaults.message = '<h4> Procesando petici&oacute;n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	
	$("#method").val("doConsulta");
	$('#id').val('');
	$("#main3").submit();
}

/* Funci�n que realiza el filtrado de datos con los datos insertados en la parte del Filtro*/
function consultarFiltroSin(){
	

	/* Primero comprobamos que se haya insertado alg�n valor en los campos del filtro*/
	if (comprobarCamposFiltro()){
		$.blockUI.defaults.message = '<h4> Procesando petici&oacute;n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
		
		/* Enviamos los datos insertados en el filtro*/
   		var frmmain3=document.getElementById('main3');
   		
   		var frm=document.getElementById('frmConsultaFiltro');
   		frm.grupoNeg_consFiltro.value = frmmain3.idgrupo.value;
   		frm.serie_consFiltro.value     = frmmain3.serieSin.value;
		frm.numero_consFiltro.value = frmmain3.numeroSin.value;
		frm.libro_consFiltro.value = frmmain3.libroSin.value;
		frm.fechaCom_consFiltro.value  = frmmain3.tx_fecha_comunicacion.value; 		
		frm.fechaRet_consFiltro.value  = frmmain3.tx_fecha_retirada.value;
		frm.method_ha.value = 'doConsulta';
		$("#frmConsultaFiltro").submit();
		
	}else{
		$("#panelInformacion").html("");
		$("#panelInformacion").hide();
		
		$("#panelAlertas").html("");
		$("#panelAlertas").hide();
		
		$('#panelAlertasValidacion').html("Debe Insertar alg&uacute;n valor en el Filtro.");
		$('#panelAlertasValidacion').show();
	}
}

function comprobarCamposFiltro(){
	
	var resultado = false;
	
 	if (!resultado && $("#grupoNegocio").val() != ''){
 		resultado = true;
 	}     
 	if (!resultado && $("#serieSin").val() != ''){
 		resultado = true;
 	}   
 	if (!resultado && $("#numeroSin").val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#libroSin').val() != ''){
 		resultado = true;
 	}
 	if (!resultado && $('#tx_fecha_comunicacion').val() != ''){
 		resultado = true;
 	} 
 	if (!resultado && $('#tx_fecha_retirada').val() != ''){
 		resultado = true;
 	} 

 	return resultado;
}

function limpiar(){
	/* Limpiamos el contenido de los campos del filtro*/
	$("#grupoNegocio").selectOptions("");
	$("#serieSin").val('');
	$("#numeroSin").val('');	
	$("#libroSin").val('');	
	$("#tx_fecha_comunicacion").val('');
	$("#tx_fecha_retirada").val('');
	
	/* Lanzamos de nuevo la consulta inicial */
	consultar();
}

function volver(){
	$(window.location).attr('href', 'utilidadesPoliza.html?rand=' + UTIL.getRand() +'&recogerPolizaSesion=true'); 		
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

/* recorremos los datos y generamos la tabla dinamicamente para pasarsela al popup*/ 
function pintarTabla(listaS){
	
	var reg ="";
	var cabecera = "<table style='width:90%; border-collapse:collapse;' class='LISTA'><tr><th class='cblistaImg'></th><th style='text-align:center' class='cblistaImg'> F.Ocurrencia </th> <th style='text-align:center' class='cblistaImg'> Riesgo </th> <th style='text-align:center' class='cblistaImg'> Situaci�n </th> <th style='text-align:center' class='cblistaImg'> Serie </th><th style='text-align:center' class='cblistaImg'> N�mero </th></tr>";
	var final = "</table>";
	
	for (var i = 0; i < listaS.length; i++){
		var itemSiniestro = listaS[i];
		
		reg = reg +"<tr> " +
		
		"<td style='text-align:center' class='literal'> " +
			"<a href='javascript:pdfParteSiniestro(" + itemSiniestro.serie + "," + itemSiniestro.numsiniestro + "," + itemSiniestro.idpoliza + "," + itemSiniestro.id + "," + itemSiniestro.numerosiniestro + ")'" + ">" +
				"<img src='jsp/img/displaytag/imprimir_poliza_modificada.png' alt='Pdf - Parte del siniestro'> "+
			"</a> " +
		"</td>" +
		"<td style='text-align:center' class='literal'>" + formatearFecha(itemSiniestro.focurr) +
		"</td>"+
		"<td style='text-align:center' class='literal'>" + itemSiniestro.codriesgo +" - "+ itemSiniestro.desriesgo       + "</td>" +
		"<td style='text-align:center' class='literal'>" + itemSiniestro.idestado  +" - "+ itemSiniestro.descestado      + "</td>" +
		"<td style='text-align:center' class='literal'>" + itemSiniestro.serie           + "</td>" +
		"<td style='text-align:center' class='literal'>" + itemSiniestro.numerosiniestro + "</td></tr> ";
	}
	// pintamos la tabla en el popup
	
	$('#datosPop').html(cabecera+reg+final);
	
}

function ActasTasacionGanado(){
	$.blockUI.defaults.message = '<h4> Procesando petici&oacute;n.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	
	var frm=document.getElementById('main3');
	$("#method").val("doActas");
	frm.submit();       		
}

function exportarExcel(){
	
	/* Enviamos los datos insertados en el filtro */
	var frmmain3=document.getElementById('main3');
	
	if (frmmain3.idgrupo.value != "" || frmmain3.serieSin.value != "" || frmmain3.numeroSin.value != "" || frmmain3.libroSin.value != ""
		|| frmmain3.tx_fecha_comunicacion.value != "" || frmmain3.tx_fecha_retirada.value != "" 
		) {
		
		var frm=document.getElementById('frmConsultaFiltro');
		frm.grupoNeg_consFiltro.value = frmmain3.idgrupo.value;
		frm.serie_consFiltro.value     = frmmain3.serieSin.value;
		frm.numero_consFiltro.value = frmmain3.numeroSin.value;
		frm.libro_consFiltro.value = frmmain3.libroSin.value;
		frm.fechaCom_consFiltro.value  = frmmain3.tx_fecha_comunicacion.value; 		
		frm.fechaRet_consFiltro.value  = frmmain3.tx_fecha_retirada.value;
		frm.method_ha.value = 'doExportarExcel';
		$("#frmConsultaFiltro").submit();
		
	}
	else {
	
		$("#main3").validate().cancelSubmit = true;
		$('#method').val('doExportarExcel');
		$('#main3').submit();	
	}
			
}