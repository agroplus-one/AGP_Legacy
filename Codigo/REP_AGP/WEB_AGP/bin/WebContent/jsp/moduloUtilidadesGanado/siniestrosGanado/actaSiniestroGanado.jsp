<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>

<html>
<head>
<title>Consulta de Actas de Ganado</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<!-- Estilos -->
<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />

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
<script type="text/javascript" src="jsp/moduloUtilidadesGanado/siniestrosGanado/actaSiniestroGanado.js"></script>

<%@ include file="/jsp/js/draggable.jsp"%>

</head>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" 
	marginheight="0" onload="SwitchMenu('sub4');">
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp"%>

	<!-- Botones para alta, imprimir y volver -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="left" width="33%">&nbsp; 
					<a class="bot" href="javascript:volverSinGan()">Volver</a>
				</td>

				<td align="center" width="33%">&nbsp;
				</td>

				<td align="right" width="33%">
					<a class="bot" href="javascript:consultarFiltroActas();">Consultar</a> 
					<a class="bot" href="javascript:limpiar()">Limpiar</a> 
				</td>
			</tr>
		</table>
	</div>

	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Consulta de Actas de Ganado</p>

		<!-- Datos de la póliza -->
		<fieldset style="width: 97%" align="center">
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
		
		<form name="frmConsultaFiltroActa" id="frmConsultaFiltroActa" action="siniestrosGanado.html" method="post">
			<input type="hidden" name="method" id="method_ha" value="doActas" /> 
			<input type="hidden" name="idSiniestro_consFilActa" id="idSiniestro_consFilActa" value="${siniestroBean.id}" /> 
			<input type="hidden" name="plan_consFilActa" id="plan_consFilActa" value="${siniestroBean.poliza.linea.codplan}" /> 
			<input type="hidden" name="linea_consFilActa" id="linea_consFilActa" value="${siniestroBean.poliza.linea.codlinea}" /> 
			<input type="hidden" name="lineaid_consFilActa" id="lineaid_consFilActa" value="${siniestroBean.poliza.linea.lineaseguroid}" /> 
			<input type="hidden" name="refpoliza_consFilActa" id="refpoliza_consFilActa" value="${siniestroBean.poliza.referencia }" /> 
			<input type="hidden" name="idPoliza_consFilActa" id="idPoliza_consFilActa" value="${siniestroBean.poliza.idpoliza}" />
			 
			<input type="hidden" name="serieActa_consFiltro" id="serieActa_consFiltro" /> 
			<input type="hidden" name="numeroActa_consFiltro" id="numeroActa_consFiltro" />
			<input type="hidden" name="libroActa_consFiltro" id="libroActa_consFiltro" /> 
			<input type="hidden" name="fechaActa_consFiltro" id="fechaActa_consFiltro" />
			<input type="hidden" name="fechaPagActa_consFiltro" id="fechaPagActa_consFiltro" />
			
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="actaSiniestroGanado" />
		</form>
		

		<form name="frmPdfGanado" id="frmPdfGanado" action="siniestrosGanado.html" method="post">
			<input type="hidden" name="method" id="method_pdf" value="doPdfActaGanado"/>
			<input type="hidden" name="serieSinGanadoPdf" id="serieSinGanadoPdf" /> 
			<input type="hidden" name="numSinGanadoPdf" id="numSinGanadoPdf" /> 
			<input type="hidden" name="letraSinGanadoPdf" id="letraSinGanadoPdf" />
			<input type="hidden" name="idPolizaPdf" id="idPolizapdf" value="${siniestroBean.poliza.idpoliza}" />
			
			<input type="hidden" name="numeroSiniestro" id="numeroSiniestro"/>
		</form>
		
		<form name="frmPdfCartaPagoGan" id="frmPdfCartaPagoGan" action="siniestrosGanado.html" method="post">
			<input type="hidden" name="method" id="method_pdf_act" value="doPdfCartaPagoGanado"/>
			<input type="hidden" name="serieSinPdfCartaGan" id="serieSinPdfCartaGan" /> 
			<input type="hidden" name="numSinPdfCartaGan"   id="numSinPdfCartaGan" /> 
			<input type="hidden" name="letraSinPdfCartaGan" id="letraSinPdfCartaGan" />
			<input type="hidden" name="idPolizaPdfCartaGan" id="idPolizaPdfCartaGan" value="${siniestroBean.poliza.idpoliza}" />
		</form>
		
		<form:form name="main3" id="main3" action="siniestrosGanado.html" method="post" commandName="siniestroBean">
			<input type="hidden" id="method" name="method" />
			<form:hidden path="id" id="id" />
			<form:hidden path="poliza.idpoliza" id="idPoliza" />
			<form:hidden path="poliza.idenvio" id="idenvio" />
			<form:hidden path="poliza.linea.codlinea" id="codlinea" />
			<form:hidden path="poliza.linea.codplan" id="codplan" />
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea" />
			<form:hidden path="poliza.referencia" id="refPoliza" />
			<input type="hidden" name="fromUtilidades" id="fromUtilidades" value="true" />
			<input type="hidden" name="altaWs" id="altaWs" />
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="declaracionesSiniestrosGanado" />
			<input type="hidden" name="serieActaCons" id="serieActaCons" />
			<input type="hidden" name="numActaCons" id="numActaCons" />
			<input type="hidden" name="libroActaCons" id="libroActaCons" />
			<input type="hidden" name="fechaActaCons" id="fechaActaCons" />
			<input type="hidden" name="fechaPagoCons" id="fechaPagoCons" />
			
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>

			<div class="panel2 isrt" style="width: 97%;" align="center" >
			
				<fieldset style="width: 99%; margin:0 auto;" align="center">
					<legend class="literal">Filtro</legend>
					<table width="100%" align="center" cellspacing="5">
						<tr>

							<td class="literal">Serie</td>
							<td >
								<input class="dato" type="text" name="serieSiniestroActa" size="5" maxlength="4" id="serieSiniestroActa" tabindex="8" value="${serieSiniestroActa}" />	
							</td>	
							
							<td class="literal">Número</td>
							<td >
								<input class="dato" type="text" name="numeroSiniestroActa" size="8" maxlength="7" id="numeroSiniestroActa" tabindex="9" value="${numeroSiniestroActa}"/>
							</td>
							
							<td class="literal">Libro</td>
							<td >
								<input class="dato" type="text" name="libroSiniestroActa" size="15" maxlength="14" id="libroSiniestroActa" tabindex="10" onchange="this.value = this.value.toUpperCase();" value="${libroSiniestroActa}"/>
							</td>
								
							<!-- FIN -->
							<td class="literal">F. Acta</td>
							<td>
								<input type="text" name="fechaActa" id="tx_fecha_acta" size="11" maxlength="10" class="dato"
									   tabindex="2" value="${fecha_acta}"/>
								<input type="button" id="btn_fecha_acta" name="btn_fecha_acta" class="miniCalendario" style="cursor: pointer;" /></td>
							
							<td class="literal">F. Pago</td>
							<td>	
								<input type="text" name="fechaPago" id="tx_fecha_pago" size="11" maxlength="10" class="dato" 
								 	   tabindex="3" value="${fecha_pago}" />
								<input type="button" id="btn_fecha_pago" name="btn_fecha_pago" class="miniCalendario" style="cursor: pointer;" />
							</td> 
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>
		
		<!-- La columna de acciones contiene edición y borrado -->
		<display:table requestURI="siniestrosGanado.html" id="sin" class="LISTA" summary="Siniestros" name="${listaSinGanadoActas}" sort="list" pagesize="${numReg}" 
			decorator="com.rsi.agp.core.decorators.ModelTableDecoratorActasSinGanado" style="width:95%;border-collapse:collapse;" >	
			<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="columnaAccActas" sortable="false" style="width:50px;text-align:center" media="html"/>
			<display:column class="literal" headerClass="cblistaImg" title="Provincia" property="columnaProv" style="width:20px;" />
			<display:column class="literal" headerClass="cblistaImg" title="Término" property="columnaTerm" style="width:60px;text-align:center"  />
			<display:column class="literal" headerClass="cblistaImg" title="Serie" property="columnaSerie" style="width:50px;text-align:center" /> 
			<display:column class="literal" headerClass="cblistaImg" title="Número" property="columnaNum" style="width:120px;text-align:center" />
			<display:column class="literal" headerClass="cblistaImg" title="Letra" property="columnaLetra" style="width:120px;" />
			<display:column class="literal" headerClass="cblistaImg" title="Estado" property="columnaEstado" style="width:120px;" />
			<display:column class="literal" headerClass="cblistaImg" title="Libro" property="columnaLibro" style="width:60px;" />
			<display:column class="literal" headerClass="cblistaImg" title="IdAnimal" property="columnaIdAnim" style="width:10px;" />
			<display:column class="literal" headerClass="cblistaImg" title="F.Acta" property="columnaFActa" format="{0,date,dd/MM/yyyy}" style="width:70px;text-align:center" />
			<display:column class="literal" headerClass="cblistaImg" title="Importe Acta" property="columnaImpActa" style="width:25px;text-align:right" />
			<display:column class="literal" headerClass="cblistaImg" title="Importe Devolver" property="columnaImpDev" style="width:25px;text-align:right" />
			<display:column class="literal" headerClass="cblistaImg" title="F.Pago" property="columnaFPago" format="{0,date,dd/MM/yyyy}" style="width:70px; text-align:center" />
		</display:table>
		
		
		<div class="imprimirDisplayTag" id="divImprimir">
			<a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Imprimir</a>	
			<a id="btnExportarExcel" style="text-decoration:none;" href="javascript:exportarExcel()">
				<img src="jsp/img/jmesa/excel.gif"/>
			</a>	
		</div>
		
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/moduloUtilidades/siniestros/popupInformacionSiniestros.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<div id="formDialogDiv" style="display: none;">
    	<%@ include file="/jsp/errorMensaje.jsp"%>
	</div>
</body>
</html>