<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Agroplus - Fraccionamiento del pago</title>

<%@ include file="/jsp/common/static/metas.jsp"%>
		
<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />


<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
<script type="text/javascript"
	src="jsp/moduloTaller/mtoImportesFrac/mtoImportesFrac.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/jquery.alerts.js"></script>
<%@ include file="/jsp/js/draggable.jsp"%>

</head>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0">
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuLateralTaller.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp"%>
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td align="right"><a class="bot" id="btnAlta"
									href="javascript:alta()">Alta</a> <a class="bot"
									id="btnModificar" style="display: none;"
									href="javascript:editar()">Modificar</a> <a class="bot"
									id="btnConsultar" href="javascript:consultar();">Consultar</a>
									<a class="bot" id="btnLimpiar" href="javascript:limpiar()">Limpiar</a>

								</td>
							</tr>
						</tbody>
					</table> <!-- FIN TABLA BARRA DE BOTONES-->
				</td>
			</tr>
		</table>
	</div>
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 99%">
		<p class="titulopag" align="left">MANTENIMIENTO DE IMPORTES PARA
			EL FRACCIONAMIENTO DEL PAGO</p>
		<form:form name="main" id="main" action="mtoImportesFrac.run"
			method="post" commandName="importesFraccBean">
			<input type="hidden" name="origenLlamada" id="origenLlamada"
				value="${origenLlamada}" />
			<input type="hidden" id="method" name="method" />
			<!--  <input type="hidden" id="lineaseguroid" name="lineaseguroid" />-->
			<form:hidden path="id" id="id" />
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<div class="panel2 isrt" style="margin:0 auto; text-align:center;">
				<table cellspacing="5" cellpadding="15" border="0"
					style="margin:0 auto;">
					<tr>
						<td class="literal" align="right">Plan&nbsp;&nbsp;&nbsp; <form:input
								path="linea.codplan" size="4" cssClass="dato" id="plan"
								maxlength="4" /> <label class="campoObligatorio"
							id="campoObligatorio_plan" title="Campo obligatorio"> *</label>


							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Línea <form:input path="linea.codlinea"
								size="6" maxlength="4" cssClass="dato" id="linea"
								onchange="javascript:lupas.limpiarCampos('desc_linea');" />
								 <form:input
								path="linea.nomlinea" cssClass="dato" id="desc_linea" size="50"  readonly="true" />
							<!--  <span id="lupaLinea"><img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Linea" /></span>-->

							<img src="jsp/img/magnifier.png" style="cursor: hand;"
							onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');"
							alt="Buscar Linea" title="Buscar Linea" /> <label class="campoObligatorio"
							id="campoObligatorio_linea" title="Campo obligatorio"> *</label>

						</td>
						<td class="literal">
							<div align="center" class="literal">

								E-S Med


								<%-- 							<c:if test="${externo == 0 and perfil!=4}"> <!--  es interno --> --%>
								<form:input path="subentidadMediadora.id.codentidad" size="4"
									maxlength="4" cssClass="dato" id="entmediadora"
									onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();"
									/>
								<form:input path="subentidadMediadora.id.codsubentidad" size="4"
									maxlength="4" cssClass="dato" id="subentmediadora"  />
								<input type="hidden" id="entidad" name="entidad" value="" /> <img
									src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"
									alt="Buscar SubEntidad Mediadora" />
								<%-- 							</c:if> --%>
								<%-- 							<c:if test="${externo == 1 or (externo==0 and perfil==4)}"> <!--  es externo --> --%>
								<%-- 								<form:input	path="colectivo.subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" tabindex="3"  readonly="true"/> --%>
								<%-- 								<form:input	path="colectivo.subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="4"  readonly="true"/> --%>
								<%-- 							</c:if> --%>
							</div>
						</td>
					</tr>
					<tr>
						<td class="literal" align="right"  >Importe mínimo para el
							fraccionamiento&nbsp;&nbsp;&nbsp;
							 <form:input
								path="importe" cssClass="dato" size="15" id="val_fracc" /> <label
							class="campoObligatorio" id="campoObligatorio_val_fracc"
							title="Campo obligatorio"> *</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Tipo
							fraccionamiento&nbsp;&nbsp;&nbsp;
						</td>
						<td class="literal" align="left" ><form:select path="tipo"
								cssClass="dato" id="tipoFracc"
								onchange="javascript:showPctRecargo();" cssStyle="width:100px">
								<form:option value="">Todos</form:option>
								<form:option value="0">SAECA</form:option>
								<form:option value="1">Agroseguro</form:option>
							</form:select> <label class="campoObligatorio" title="Campo obligatorio"
							id="campoObligatorio_tipoFracc"> *</label> &nbsp; % Recargo
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <form:input
								path="pctRecargo" cssClass="dato" size="4" id="val_recargo"
								maxlength="6" onfocus="javascript:showPctRecargo()" /> <label
							class="campoObligatorio" id="campoObligatorio_val_recargo"
							title="Campo obligatorio"> *</label>
						</td>
					</tr>
				</table>
			</div>

		</form:form>
		<div id="grid" style="width: 95%; margin: 0 auto;">
			${consultaImportesFracc}</div>
	</div>
	<%@ include file="../../common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include
		file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
</body>
</html>