<%@ include file="/jsp/common/static/taglibs.jsp"%>
<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>

<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
	<head>
		<title>Carga de Parcelas</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/cargaParcelas.js"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<a class="bot" id="btnVolver" href="javascript:volver()">Volver</a>
						<a class="bot" id="btnCargar" href="javascript:cargar()">Continuar</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Carga de Parcelas</p>
			<form:form name="main" id="main" action="seleccionPoliza.html" method="post" commandName="polizaBean">
				<input type="hidden" name="operacion" id="operacion" value="cargarOrigenDatos" />
				<input type="hidden" name="recalcular" id="recalcular" value="" />
				<input type="hidden" name="method" id="method" value="" />
				<form:hidden path="idpoliza" id="idpoliza" />
				<form:hidden path="clase" id="clase" />
				<form:hidden path="colectivo.id" id="idcolectivo" />
				<form:hidden path="asegurado.id" id="idasegurado" />
				<form:hidden path="asegurado.nifcif" id="nifasegurado" />
				<form:hidden path="asegurado.entidad.codentidad" id="codentidad" />
				<form:hidden path="linea.codplan" id="codplan" />
				<form:hidden path="linea.codlinea" id="codlinea" />
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="isrt" align="center" style="vertical-align: middle;width: 70%;margin: 0 auto;" >
					<fieldset class="fieldset_alone" style="width: 100%; height: 110px" >
					   <legend >Selección del origen de los datos a cargar en la póliza</legend>
						<table width="100%" height="80%">
							<tr align="left" style="vertical-align: middle;">
								<td class="literal" style="padding-left: 150px;vertical-align: middle;">
									<c:if test="${hayParcelasPac == 'true' }">
										<input type="hidden" name="listaIdAseguradoPac" id="listaIdAseguradoPac" value="${listaIdAseguradoPac}" />
										<input type="radio" name="seleccionOrigen" id="seleccionOrigen" value="doParcelasPac"> Parcelas de PAC
									</c:if><c:if test="${hayParcelasPac != 'true' }">
										<input type="radio" name="seleccionOrigen" id="seleccionOrigen" disabled="disabled"> Parcelas de PAC
									</c:if>
								</td>
							</tr>
							<tr align="left" style="vertical-align: middle;">
								<td class="literal" style="padding-left: 150px;vertical-align: middle;">
									<c:if test="${hayParcelasCsv == 'true' }">
										<input type="hidden" name="listaIdAseguradoCsv" id="listaIdAseguradoCsv" value="${listaIdAseguradoCsv}" />
										<input type="radio" name="seleccionOrigen" id="seleccionOrigen" value="doParcelasCsv"> Parcelas de CSV
									</c:if><c:if test="${hayParcelasCsv != 'true' }">
										<input type="radio" name="seleccionOrigen" id="seleccionOrigen" disabled="disabled"> Parcelas de CSV
									</c:if>
								</td>
							</tr>
							<tr align="left">
								<td class="literal" style="padding-left: 150px;vertical-align: middle;">
									<c:if test="${haySitActualizada == 'true' }">
										<input type="radio" name="seleccionOrigen" id="seleccionOrigen" value="doSituacionAct">  Situación Actualizada de Agroseguro
									</c:if><c:if test="${haySitActualizada != 'true' }">
										<input type="radio" name="seleccionOrigen" id="seleccionOrigen" disabled="disabled"> Situación Actualizada de Agroseguro
									</c:if>
								</td>
							</tr>
							<!-- ASF 29/10/2012 Mejora 216 -->
							<tr align="left">
								<td class="literal" style="padding-left: 150px;vertical-align: middle;">
									<c:if test="${hayPolAnterior == 'true' }">
										<input type="radio" name="seleccionOrigen" id="seleccionOrigen" value="doPolizaAnterior">  Póliza original de los últimos 3 planes
									</c:if><c:if test="${hayPolAnterior != 'true' }">
										<input type="radio" name="seleccionOrigen" id="seleccionOrigen" disabled="disabled"> Póliza original de los últimos 3 planes
									</c:if>
								</td>
							</tr>
							<!-- DAA 18/04/2012 -->
							<tr align="left" style="vertical-align: middle;">
								<td class="literal" style="padding-left: 150px;vertical-align: middle;">
									<c:if test="${hayMultiPoliza == 'true' }">
										<input type="radio" name="seleccionOrigen" id="seleccionOrigen" value="doMultiClase"> Póliza Existente del Plan actual
									</c:if><c:if test="${hayMultiPoliza != 'true' }">
										<input type="radio" name="seleccionOrigen" id="seleccionOrigen" disabled="disabled"> Póliza Existente del Plan actual 
									</c:if>
								</td>
							</tr>
							<!-- ASF 29/10/2012 Mejora 217 -->
							<tr align="left" style="vertical-align: middle;">
								<td class="literal" style="padding-left: 150px;vertical-align: middle;">
									<input type="radio" name="seleccionOrigen" id="seleccionOrigen" value="doNoCargar"> No cargar ninguna
								</td>
							</tr>
						</table>
					</fieldset>
				</div>	
			</form:form>
			<br/>
		</div>
		<br/>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/moduloPolizas/polizas/popupRecalcular.jsp"%>
	</body>
</html>