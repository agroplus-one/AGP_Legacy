<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>

<html>
<head>
<title>Declaraciones de Siniestros</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<!-- Estilos -->
<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />


<!-- JavaScript,jQery & AJAX -->
<!--
<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>

<%@ include file="/jsp/js/draggable.jsp"%>
-->

<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/util.js" ></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>

<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>

<!-- <%@ include file="/jsp/common/static/overlay.jsp"%> -->

<%@ include file="/jsp/js/draggable.jsp"%>

<script language="javascript">
	
		$(function(){
			$("#grid").displayTagAjax();
		});
	
	
		$(document).ready(function() {	
		
		       document.getElementById('sl_riesgo_siniestros').focus();		
									
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
			        inputField        : "tx_fecha_ocurrencia",
			        button            : "btn_fecha_ocurrencia",
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
					 	"fechaocurrencia":{dateITA: true},
					 	"fecfirmasiniestro":{dateITA: true},
					 	"comunicaciones.fechaEnvio":{dateITA: true}
					 },
					 messages: {					 	
					 	"fechaocurrencia":{dateITA: "El formato del campo Fecha Ocurrencia pago es dd/mm/YYYY"},
					 	"fecfirmasiniestro":{dateITA: "El formato del campo Fecha Peritación pago es dd/mm/YYYY"},
					 	"comunicaciones.fechaEnvio":{dateITA: "El formato del campo Fecha Envío pago es dd/mm/YYYY"}
					 }
				});
	    });
	      	
	    function pasarDefinitivo(idSiniestro){
	    	$.blockUI.defaults.message = '<h4> Procesando petición.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	        $("#method").val("doPasarDefinitiva");
			$('#id').val(idSiniestro);
			$("#main3").submit();
	    }
	
		function eliminar(idSiniestro){
			if(confirm('¿Está seguro de que desea eliminar el registro seleccionado?')){
				$("#method").val("doEliminar");
				$("#id").val(idSiniestro);
				$.blockUI.defaults.message = '<h4> Eliminando regitstro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				$("#main3").submit();
			}
		} 
		//DAA 16/11/12
		function editar(idSiniestro, estado){
			if(estado!=5){
			doEditar(idSiniestro);
			}
			else{
				if(confirm('El Siniestro pasará a estado Provisional, ¿Desea Continuar?')){
					doEditar(idSiniestro);
				}
			}
		}
		
		/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Inicio */
		function bajaSiniestro(idSiniestro){
			if(confirm('¿Está seguro de que desea dar de Baja el Siniestro seleccionado?')){
				$("#method").val("doBajaSiniestro");
				$("#id").val(idSiniestro);
				$.blockUI.defaults.message = '<h4> Dando de Baja el Siniestro seleccionado.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				$("#main3").submit();
			}
		}
		/* Pet. 63473 ** MODIF TAM (26.11.2021) ** Fin */
		
		function doEditar(idSiniestro){
			$("#sl_riesgo_siniestros").selectOptions("");
			$("#sl_estado").selectOptions("");	
			$("#tx_fecha_ocurrencia").val('');
			$("#tx_fecha_peritacion").val('');		
			$("#tx_fecha_envio").val('');
			
			$("#main3").validate().cancelSubmit = true;
			$("#method").val("doEdita");
			$("#id").val(idSiniestro);
			$.blockUI.defaults.message = '<h4> Procesando petición.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$("#main3").submit();
		}
		
		function ver(idSiniestro){
		    editar(idSiniestro);
		}
				
		
		function crearSw(){
			$("#altaWs").val("true");
			crear();
		}
		
		function crear(){
		
			$("#sl_riesgo_siniestros").selectOptions("");
			$("#sl_estado").selectOptions("");
			$("#tx_fecha_ocurrencia").val('');
			$("#tx_fecha_peritacion").val('');		
			$("#tx_fecha_envio").val('');
			
			$("#main3").validate().cancelSubmit = true;
			$("#method").val("doAlta");
			$("#id").val("");
			$.blockUI.defaults.message = '<h4> Procesando petición.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
			$("#main3").submit();
			
		} 
		
		
		function hojasCampo_ActasTasacion(){
			
			$.blockUI.defaults.message = '<h4> Procesando petición.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
       		var frmmain3=document.getElementById('main3');
       		var frm=document.getElementById('frmHojasActas');
       		frm.riesgoSiniestro.value=frmmain3.sl_riesgo_siniestros.value;
    		frm.fechaocurrSiniestro.value= frmmain3.tx_fecha_ocurrencia.value;
    		frm.fechaenvioSiniestro.value=frmmain3.tx_fecha_envio.value;
    		frm.codestadoSiniestro.value=frmmain3.sl_estado.value; 		
       		
       		
       		frm.submit();       		
       		
		}
		
		function consultar(){
			$("#method").val("doConsulta");
			$('#id').val('');
			$("#main3").submit();
		}
		
		function limpiar(){
			$("#sl_riesgo_siniestros").selectOptions("");
			$("#sl_estado").selectOptions("");
			$("#tx_fecha_ocurrencia").val('');
			$("#tx_fecha_peritacion").val('');			
			$("#tx_fecha_envio").val('');
			
			consultar();
		}
		
		function imprimir(idSiniestro)	{
			$("#idSiniestro").val(idSiniestro);
			$('#print').attr('target', '_blank');
			$("#print").submit();
		} 
		
		function volver(){
			$(window.location).attr('href', 'utilidadesPoliza.html?rand=' + UTIL.getRand() +'&recogerPolizaSesion=true'); 		
		}
		
		function pintarDescRiesgo(){
				var table = document.getElementById('sin');
				if(table){
					var rowCount = table.rows.length;
					var jsonArray = ${listaRiesgos};
					
					for(var j=1; j<rowCount; j++) {
						for(var json in jsonArray){
						    for(var i in jsonArray[json]){
						    	if (table.rows[j].cells[3].innerHTML == i){
						    		table.rows[j].cells[4].innerHTML = jsonArray[json][i];
						    	}
							}
						}
		            }
				}
		}
		
		function verAcuseRecibo(idSiniestro){	
			$("#method").val("doVerRecibo");
			$("#id").val(idSiniestro);
			$("#main3").submit();
		}
		
		function pdfParteSiniestro(serieSiniestro, numSiniestro, idPoliza, idSiniestro, numeroSiniestro){		
			
			var frm=document.getElementById('frmPdfParte');
			frm.serieSiniestro.value  = serieSiniestro;
    		frm.numSiniestro.value    = numSiniestro;
    		frm.idPoliza_parte.value  = idPoliza;  
    		frm.idSiniestro.value     = idSiniestro;
    		frm.numeroSiniestro.value = numeroSiniestro;
    		frm.target="_blank";
    		frm.submit();       		       		
		}
		
		function verDetalleLineaSiniestro (serieSiniestro, numSiniestro, idSiniestro, idPoliza){ 
			
			$.ajax({
				url: "siniestrosInformacion.html?method=doVerDetalleSiniestro&serieSiniestro="+serieSiniestro+"&numSiniestro="+numSiniestro+"&idSiniestro="+idSiniestro+"&idPoliza="+idPoliza,
				data: "",
				async:true,
				dataType: "json",
				error: function(objeto, quepaso, otroobj){
					/* MODIF TAM * Resolución Incidencia nº5 * (23.10.2018) */
					$.unblockUI();
					llamadaErrorMensaje(objeto);
				},
				success: function(datos){
					/* MODIF TAM * Resolución Incidencia nº5 * (23.10.2018) */
					$.unblockUI();
					pintarTabla(datos.listaS);
					$('#listaSiniestros').val(datos.listaS);
					$('#panelInformacionSiniestro').show();
					$('#overlay').show(); 		
				},
				/* MODIF TAM * Resolución Incidencia nº5 * (23.10.2018) */
				beforeSend: function(){
			        $.blockUI.defaults.message = '<h4> Solicitando datos.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
   			        $.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
        		},
        		/* MODIF TAM * Resolución Incidencia nº5 * (23.10.2018) */
				type: "GET"
				
			});
		}
		
		function llamadaErrorMensaje(objeto){

			var mensaje =  objeto.responseText ;
			var prueba = mensaje.split("}{");
			var definitivo = prueba[0] + "}";
			
			var json = jQuery.parseJSON( definitivo ).alerta;

			$('#panelInformacionSiniestro').show();
			$('#panelInformacion').empty();
			$('#panelInformacion').append(json);
			$('#overlay').show(); 
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
			var cabecera = "<table style='width:90%; border-collapse:collapse;' class='LISTA'><tr><th class='cblistaImg'></th><th style='text-align:center' class='cblistaImg'> F.Ocurrencia </th> <th style='text-align:center' class='cblistaImg'> Riesgo </th> <th style='text-align:center' class='cblistaImg'> Situación </th> <th style='text-align:center' class='cblistaImg'> Serie </th><th style='text-align:center' class='cblistaImg'> Número </th></tr>";
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
		
		
	</script>

</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="SwitchMenu('sub4');pintarDescRiesgo()">
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp"%>



	<!-- Botones para alta, imprimir y volver -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>

				<td align="left" width="33%">&nbsp;<!-- <a class="bot" id="botonAlta" href="javascript:crear();">Alta FTP</a> -->
				</td>

				<td align="center" width="33%">&nbsp;<a class="bot"
					href="javascript:hojasCampo_ActasTasacion()">Hojas de campo y
						Actas de tasación</a>
				</td>

				<td align="right" width="33%"><a class="bot"
					href="javascript:volver()">Volver</a> <a class="bot"
					href="javascript:consultar();">Consultar</a> <a class="bot"
					href="javascript:limpiar()">Limpiar</a> <a class="bot"
					id="botonAlta" href="javascript:crearSw();">Alta</a>&nbsp;</td>
			</tr>
		</table>
	</div>

	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Declaraciones de Siniestros</p>

		<!-- Datos de la póliza -->
		<fieldset style="width: 95%">
			<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="90%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="40px">Plan:</td>
					<td width="40px" class="detalI">${siniestroBean.poliza.linea.codplan}</td>
					<td class="literal" width="50px">Línea:</td>
					<td width="200px" class="detalI">${siniestroBean.poliza.linea.codlinea
						} - ${siniestroBean.poliza.linea.nomlinea}</td>
					<td class="literal" width="75px">Asegurado:</td>
					<td width="200px" class="detalI">${siniestroBean.poliza.asegurado.nombreCompleto
						}</td>
				</tr>
				<tr>
					<td class="literal" width="40px">Póliza:</td>
					<td width="40px" class="detalI">${siniestroBean.poliza.referencia
						}</td>
					<td class="literal" width="50px">Módulo:</td>
					<td width="200px" class="detalI">${siniestroBean.poliza.codmodulo}</td>
					<td class="literal" width="70px">Fec. Envío:</td>
					<td width="100px" class="detalI"><fmt:formatDate
							pattern="dd/MM/yyyy" value="${siniestroBean.poliza.fechaenvio}" /></td>
				</tr>
			</table>
		</fieldset>
		<form name="print" id="print" action="informes.html" method="post">
			<input type="hidden" name="method" id="methodPrint"
				value="doInformeSiniestro" /> <input type="hidden"
				name="idSiniestro" id="idSiniestro" />
		</form>

		<form name="frmHojasActas" id="frmHojasActas"
			action="siniestrosInformacion.html" method="post">
			<input type="hidden" name="method" id="method_ha"
				value="doHojasCamposActasTasaciones" /> <input type="hidden"
				name="idSiniestro_ha" id="idSiniestro_ha"
				value="${siniestroBean.id}" /> <input type="hidden" name="plan_ha"
				id="plan_ha" value="${siniestroBean.poliza.linea.codplan}" /> <input
				type="hidden" name="linea_ha" id="linea_ha"
				value="${siniestroBean.poliza.linea.codlinea}" /> <input
				type="hidden" name="lineaid_ha" id="lineaid_ha"
				value="${siniestroBean.poliza.linea.lineaseguroid}" /> <input
				type="hidden" name="refpoliza_ha" id="refpoliza_ha"
				value="${siniestroBean.poliza.referencia }" /> <input type="hidden"
				name="idPoliza_ha" id="plan_ha"
				value="${siniestroBean.poliza.idpoliza}" /> <input type="hidden"
				name="riesgoSiniestro" id="riesgoSiniestro" /> <input type="hidden"
				name="fechaocurrSiniestro" id="fechaocurrSiniestro" /> <input
				type="hidden" name="fechaenvioSiniestro" id="fechaenvioSiniestro" />
			<input type="hidden" name="codestadoSiniestro"
				id="codestadoSiniestro" /> <input type="hidden"
				name="origenLlamada" id="origenLlamada"
				value="declaracionesSiniestros" />
		</form>

		<form name="frmPdfParte" id="frmPdfParte" action="siniestrosInformacion.html" method="post">
		
			<input type="hidden" name="method" id="method_parte" value="doPdfParte" /> 
			<input type="hidden" name="serieSiniestro" id="serieSiniestro" /> 
			<input type="hidden" name="numSiniestro" id="numSiniestro" /> 
			<input type="hidden" name="idPoliza" id="idPoliza_parte" />
			<input type="hidden" name="idSiniestro" id="idSiniestro" />
			<input type="hidden" name="numeroSiniestro" id="numeroSiniestro"/>
		</form>


		<form:form name="main3" id="main3" action="siniestros.html"
			method="post" commandName="siniestroBean">
			<input type="hidden" id="method" name="method" />
			<form:hidden path="id" id="id" />
			<form:hidden path="poliza.idpoliza" id="idPoliza" />
			<form:hidden path="poliza.idenvio" id="idenvio" />
			<form:hidden path="poliza.linea.codlinea" id="codlinea" />
			<form:hidden path="poliza.linea.codplan" id="codplan" />
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea" />
			<form:hidden path="poliza.referencia" id="refPoliza" />
			<input type="hidden" name="fromUtilidades" id="fromUtilidades"
				value="true" />
			<input type="hidden" name="altaWs" id="altaWs" />
			<input type="hidden" name="origenLlamada" id="origenLlamada"
				value="declaracionesSiniestros" />


			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>

			<div class="panel2 isrt" style="width: 95%">
				<fieldset>
					<legend class="literal">Filtro</legend>
					<table width="100%" align="center" cellspacing="5">
						<tr>
							<td class="literal">Riesgo Siniestro</td>
							<td><form:select id="sl_riesgo_siniestros" path="codriesgo"
									cssClass="dato" cssStyle="width:180px" tabindex="1">
									<form:option value="">-- Seleccione una opción --</form:option>
									<c:forEach items="${listaRiesgosCombo}" var="riesgo">
										<fmt:parseNumber var="aux" value="${riesgo.id.codriesgo}" />
										<form:option value="${aux}">${riesgo.id.codriesgo} - ${riesgo.desriesgo }</form:option>
									</c:forEach>
								</form:select></td>
							<td class="literal">Fec.Ocurrencia</td>
							<td><spring:bind path="fechaocurrencia">
									<input type="text" name="fechaocurrencia"
										id="tx_fecha_ocurrencia" size="11" maxlength="10" class="dato"
										tabindex="2"
										value="<fmt:formatDate pattern="dd/MM/yyyy" value="${siniestroBean.fechaocurrencia}"/>" />
								</spring:bind> <input type="button" id="btn_fecha_ocurrencia"
								name="btn_fecha_ocurrencia" class="miniCalendario"
								style="cursor: pointer;" /></td>

							<td class="literal">Fec.Envío</td>
							<td><spring:bind path="comunicaciones.fechaEnvio">
									<input type="text" name="comunicaciones.fechaEnvio"
										id="tx_fecha_envio" size="11" maxlength="10" class="dato"
										tabindex="3"
										value="<fmt:formatDate pattern="dd/MM/yyyy" value="${siniestroBean.comunicaciones.fechaEnvio}"/>" />
								</spring:bind> <input type="button" id="btn_fecha_envio"
								name="btn_fecha_envio" class="miniCalendario"
								style="cursor: pointer;" /></td>
							<td class="literal">Estado</td>
							<td><form:select id="sl_estado"
									path="estadoSiniestro.idestado" cssClass="dato"
									cssStyle="width:180px" tabindex="4">
									<form:option value="">-- Seleccione una opción --</form:option>
									<c:forEach items="${listaEstados }" var="estado">
										<form:option value="${estado.idestado }">${estado.descestado }</form:option>
									</c:forEach>
								</form:select></td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>
		<!-- La columna de acciones contiene edición y borrado -->
		<display:table requestURI="siniestros.html" id="sin" class="LISTA"
			summary="Siniestros" name="${listaSiniestros}" sort="list"
			pagesize="${numReg}"
			decorator="com.rsi.agp.core.decorators.ModelTableDecoratorSiniestros"
			style="width:100%;border-collapse:collapse;" excludedParams="method"
			defaultsort="2">
			<display:column class="literal" headerClass="cblistaImg"
				title="Acciones" property="acciones"
				style="width:80px;text-align:center" />
			<display:column class="literal" headerClass="cblistaImg"
				title="Orden" property="columnaOrden" style="width:20px;"
				sortable="true" />
			<display:column class="literal" headerClass="cblistaImg"
				title="Fec. Ocurrencia" format="{0,date,dd/MM/yyyy}"
				property="columnaOcurrencia" style="width:60px;" sortable="true" />
			<display:column class="literal" headerClass="cblistaImg"
				title="Riesgo del Siniestro" property="columnaRiesgo"
				style="width:50px;" />
			<display:column class="literal" headerClass="cblistaImg"
				title="Descripción Riesgo" property="columnaDes"
				style="width:120px;" />
			<display:column class="literal" headerClass="cblistaImg"
				title="Estado" property="columnaEstado" style="width:70px" />
			<display:column class="literal" headerClass="cblistaImg"
				title="Fec. Envío" format="{0,date,dd/MM/yyyy}"
				property="columnaEnvio" style="width:60px;" />
			<display:column class="literal" headerClass="cblistaImg"
				title="Num. Aviso" property="columnaNumSn" style="width:10px;" />
			<display:column class="literal" headerClass="cblistaImg"
				title="Fecha Baja" property="columnaFecBaja" style="width:10px;" />	
		</display:table>
		
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/moduloUtilidades/siniestros/popupInformacionSiniestros.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<div id="formDialogDiv" style="display: none;">
    	<!--<jsp:include page="/jsp/errorMensaje.jsp"/>-->
    	<%@ include file="/jsp/errorMensaje.jsp"%>
	</div>
</body>
</html>