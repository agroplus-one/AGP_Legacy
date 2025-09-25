<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>

<html>
<head>
<title>Declaraciones de Siniestros de Ganado</title>

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
<script type="text/javascript" src="jsp/moduloUtilidadesGanado/siniestrosGanado/declaracionesSiniestroGanado.js"></script>

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
					<a class="bot" href="javascript:volver()">Volver</a>
				</td>

				<td align="center" width="33%">&nbsp;
					<c:if test="${mostrarActa eq true}"> 
						<a class="bot" href="javascript:ActasTasacionGanado()">Actas</a>
					</c:if> 
				</td>

				<td align="right" width="33%">
					<a class="bot" href="javascript:consultarFiltroSin();">Consultar</a> 
					<a class="bot" href="javascript:limpiar()">Limpiar</a> 
				</td>
			</tr>
		</table>
	</div>

	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Declaraciones de Siniestros de Ganado</p>

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
		
		<form name="frmHojasActas" id="frmHojasActas" action="siniestrosInformacion.html" method="post">
			<input type="hidden" name="method" id="method_ha" value="doHojasCamposActasTasaciones" /> 
			<input type="hidden" name="idSiniestro_ha" id="idSiniestro_ha" value="${siniestroBean.id}" /> 
			<input type="hidden" name="plan_ha" id="plan_ha" value="${siniestroBean.poliza.linea.codplan}" /> 
			<input type="hidden" name="linea_ha" id="linea_ha" value="${siniestroBean.poliza.linea.codlinea}" /> 
			<input type="hidden" name="lineaid_ha" id="lineaid_ha" value="${siniestroBean.poliza.linea.lineaseguroid}" /> 
			<input type="hidden" name="refpoliza_ha" id="refpoliza_ha" value="${siniestroBean.poliza.referencia }" /> 
			<input type="hidden" name="idPoliza_ha" id="plan_ha" value="${siniestroBean.poliza.idpoliza}" /> 
			<input type="hidden" name="riesgoSiniestro" id="riesgoSiniestro" /> 
			<input type="hidden" name="fechaocurrSiniestro" id="fechaocurrSiniestro" /> 
			<input type="hidden" name="fechaenvioSiniestro" id="fechaenvioSiniestro" />
			<input type="hidden" name="codestadoSiniestro" id="codestadoSiniestro" /> 
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="declaracionesSiniestrosGanado" />
		</form>
		
		<form name="frmConsultaFiltro" id="frmConsultaFiltro" action="siniestrosGanado.html" method="post">
			<input type="hidden" name="method" id="method_ha" value="doConsulta" /> 
			<input type="hidden" name="idSiniestro_consFil" id="idSiniestro_consFil" value="${siniestroBean.id}" /> 
			<input type="hidden" name="plan_consFil" id="plan_consFil" value="${siniestroBean.poliza.linea.codplan}" /> 
			<input type="hidden" name="linea_consFil" id="linea_consFil" value="${siniestroBean.poliza.linea.codlinea}" /> 
			<input type="hidden" name="lineaid_consFil" id="lineaid_consFil" value="${siniestroBean.poliza.linea.lineaseguroid}" /> 
			<input type="hidden" name="refpoliza_consFil" id="refpoliza_consFil" value="${siniestroBean.poliza.referencia }" /> 
			<input type="hidden" name="idPoliza_consFil" id="idPoliza_consFil" value="${siniestroBean.poliza.idpoliza}" />
			 
			<input type="hidden" name="grupoNeg_consFiltro" id="grupoNeg_consFiltro" /> 
			<input type="hidden" name="serie_consFiltro" id="serie_consFiltro" /> 
			<input type="hidden" name="numero_consFiltro" id="numero_consFiltro" />
			<input type="hidden" name="libro_consFiltro" id="libro_consFiltro" /> 
			<input type="hidden" name="fechaCom_consFiltro" id="fechaCom_consFiltro" />
			<input type="hidden" name="fechaRet_consFiltro" id="fechaRet_consFiltro" />
			
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="declaracionesSiniestrosGanado" />
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
			<input type="hidden" name="grupoNegocioSel" id="grupoNegocioSel" value="${grupoNegocioSel}"/>

			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>

			<div class="panel2 isrt" style="width: 95%;" align="center" >
			
				<fieldset style="width: 98%; margin:0 auto;" align="center">
					<legend class="literal">Filtro</legend>
					<table width="100%" align="center" cellspacing="5">
						<tr>
							<!-- INI -->
							<td class="literal">Grupo Negocio</td>
							<td >			
								<select id="idgrupo" name="idgrupo" class="dato"  style="width: 100px" value="${idgrupo}">
									<option value=""></option>
									<c:forEach items="${gruposNegocio}" var="grupoNegocio">
										<option value="${grupoNegocio.grupoNegocio}">${grupoNegocio.descripcion}</option>
									</c:forEach>
								</select>
							</td>
							
							<td class="literal">Serie</td>
							<td >
								<input class="dato" type="text" name="serieSin" size="5" maxlength="4" id="serieSin" tabindex="8" value="${serieSin}" />		
							</td>	
							
							<td class="literal">Número</td>
							<td >
								<input class="dato" type="text" name="numeroSin" size="8" maxlength="7" id="numeroSin" tabindex="9" value="${numeroSin}"/>		
							</td>
							
							<td class="literal">Libro</td>
							<td >
								<input class="dato" type="text" name="libroSin" size="15" maxlength="14" id="libroSin" tabindex="10" onchange="this.value = this.value.toUpperCase();" value="${libroSin}"/>		
							</td>
								
							<!-- FIN -->
							<td class="literal">Fec. Comunicación</td>
							<td>
								<input type="text" name="fechacomunicacion" id="tx_fecha_comunicacion" size="11" maxlength="10" class="dato"
									   tabindex="2" value="${fecha_comunicacion}" />
								<input type="button" id="btn_fecha_comunicacion" name="btn_fecha_comunicacion" class="miniCalendario" style="cursor: pointer;" /></td>
							
							<td class="literal">Fec.Retirada</td>
							<td>	
								<input type="text" name="fechaRetirada" id="tx_fecha_retirada" size="11" maxlength="10" class="dato" 
								 	   tabindex="3" value="${fecha_retirada}" />
								<input type="button" id="btn_fecha_retirada" name="btn_fecha_retirada" class="miniCalendario" style="cursor: pointer;" />
							</td> 
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>
		<!-- La columna de acciones contiene edición y borrado -->
		<display:table requestURI="siniestrosGanado.html" id="sin" class="LISTA" summary="Siniestros" name="${listaSiniestroGanado}" sort="list" pagesize="${numReg}" 
			decorator="com.rsi.agp.core.decorators.ModelTableDecoratorSiniestrosGanado" style="width:95%;border-collapse:collapse;" >
			<display:column class="literal" headerClass="cblistaImg" title="G.N" property="columnaGrNegocio" style="width:30px" />
			<display:column class="literal" headerClass="cblistaImg" title="Provincia" property="columnaProv" style="width:20px;text-align:center"/>
			<display:column class="literal" headerClass="cblistaImg" title="Término" property="columnaTerm" style="width:60px;text-align:center"/> 
			<display:column class="literal" headerClass="cblistaImg" title="Serie" property="columnaSerie" style="width:50px;text-align:center" /> 
			<display:column class="literal" headerClass="cblistaImg" title="Número" property="columnaNum" style="width:50px;" />
			<display:column class="literal" headerClass="cblistaImg" title="F.Comunicación" property="columnaFCom" format="{0,date,dd/MM/yyyy}" style="width:15px;text-align:center" /> 
			<display:column class="literal" headerClass="cblistaImg" title="Libro" property="columnaLibro" style="width:60px;" />
			<display:column class="literal" headerClass="cblistaImg" title="IdAnimal" property="columnaIdAnim" style="width:10px;" />
			<display:column class="literal" headerClass="cblistaImg" title="Perito" property="columnaPer" style="width:100px;" />
			<display:column class="literal" headerClass="cblistaImg" title="Tlf. Perito" property="columnaTlfPer" style="width:15px;" />
			<display:column class="literal" headerClass="cblistaImg" title="Tasado" property="columnaTas" style="width:10px;" />
			<display:column class="literal" headerClass="cblistaImg" title="F. Retirada" property="columnaFRet" format="{0,date,dd/MM/yyyy}" style="width:15px;text-align:center" />
			<display:column class="literal" headerClass="cblistaImg" title="Kg" property="columnaKg" style="width:10px;text-align:right" />
			<display:column class="literal" headerClass="cblistaImg" title="Coste Retirada" property="columnaCosRet" style="width:10px;text-align:right" />
			<display:column class="literal" headerClass="cblistaImg" title="Pago Gestora" property="columnaPagGest" style="width:10px;text-align:right" />
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