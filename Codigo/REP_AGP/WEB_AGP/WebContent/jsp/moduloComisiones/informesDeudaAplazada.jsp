<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>


<html>
<head>
<title>Consulta de comisiones de deuda aplazada</title>
<%@ include file="/jsp/common/static/metas.jsp"%>

<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
<!--<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa-pdf.css" />-->
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
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
<script type="text/javascript"
	src="jsp/moduloComisiones/informesDeudaAplazada.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
<script type="text/javascript" src="jsp/js/jquery.alerts.js"></script>
<%@ include file="/jsp/js/draggable.jsp"%>

<script type="text/javascript">
	function cargarFiltro() {
		<c:forEach items="${sessionScope.consulta_LIMIT.filterSet.filters}" var="filtro">
		//alert(${filtro.property});
		var inputText = document.getElementById('${filtro.property}');
		//alert(inputText);
		if (null != inputText) {
			inputText.value = '${filtro.value}';
		}
		</c:forEach>
	}
</script>
<style>
    #divImprimir {
        display: none;
    }
</style>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="cargarFiltro();SwitchSubMenu('sub9', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr align="left">
				<td align="right"><a class="bot" id="btnGenerar"
					href="javascript:consultar()">Consultar</a> <a class="bot"
					id="btnLimpiar" href="javascript:limpiar()">Limpiar</a></td>
			</tr>
		</table>
	</div>

	<div class="conten" style="padding: 3px; width: 100%">

		<p class="titulopag" align="left">Consultas de Comisiones de deuda
			aplazada</p>



		<form:form name="main3" id="main3" action="informesDeudaAplazada.run"
			method="post" commandName="infDeudaBean">
			<input type="hidden" name="idInforme" id="idInforme"
				value="${idInforme}" />
			<input type="hidden" id="method" name="method" value="" />
			<input type="hidden" name="grupoEntidades" id="grupoEntidades"
				value="${grupoEntidades}" />
			<input type="hidden" id="origenLlamada" name="origenLlamada"
				value="${origenLlamada}" />
			<input type="hidden" id="perfil" name="perfil" value="${perfil}" />
			<input type="hidden" id="externo" name="externo" value="${externo}" />
			<input type="hidden" id="entMed" name="entMed" value="${entMed}" />
			<input type="hidden" id="subEntMed" name="subEntMed"
				value="${subEntMed}" />
			<input type="hidden" name="fechaemi.day" value="">
			<input type="hidden" name="fechaemi.month" value="">
			<input type="hidden" name="fechaemi.year" value="">
			<input type="hidden" name="fechaacep.day" value="">
			<input type="hidden" name="fechaacep.month" value="">
			<input type="hidden" name="fechaacep.year" value="">
			<input type="hidden" name="fechacie.day" value="">
			<input type="hidden" name="fechacie.month" value="">
			<input type="hidden" name="fechacie.year" value="">

			<!--  <input type="hidden" id="campoOperador" name="campoOperador" value="lt,codLinea"/>				
				<input type="hidden" id="valorOperador" name="valorOperador" value="2015"/>-->

			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>

			<fieldset>
				<legend class="literal">Filtro</legend>
				<table style="margin: 0 auto;">
					<tr>
						<td class="literal" width="6%">Entidad</td>
						<td colspan="3" width="40%"><c:choose>
								<c:when test="${perfil eq '0' || perfil eq '5'}">
									<form:input path="codentidad" size="4" maxlength="4"
										cssClass="dato" id="entidad" tabindex="1"
										onchange="javascript:lupas.limpiarCampos('desc_entidad');" />
									<input class="dato" id="desc_entidad" size="40" readonly="true"
										value="${nomEntidad}" />
									<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');"
										alt="Buscar Entidad" title="Buscar Entidad" />
								</c:when>
								<c:when test="${perfil eq '1'}">
									<form:input path="codentidad" size="4" maxlength="4"
										cssClass="dato" readonly="true" id="entidad" tabindex="1" />
									<input class="dato" id="desc_entidad" size="40" readonly="true"
										value="${nomEntidad}" />
								</c:when>

							</c:choose></td>
						<td class="literal" width="3%">Plan</td>
						<td width="5%"><form:input path="plan" size="4" maxlength="4"
								cssClass="dato" id="plan" tabindex="3"
								onchange="javascript:lupas.limpiarCampos('lineaOpe', 'desc_linea');" />
						</td>
						<td class="literal" width="7%">Línea</td>
						<td colspan="3" width="30%"><form:input path="linea" size="3"
								maxlength="3" cssClass="dato" id="linea" tabindex="4"
								onchange="javascript:lupas.limpiarCampos('desc_linea');" /> <input
							class="dato" id="desc_linea" size="40" readonly="true"
							value="${nomLinea}" /> <img src="jsp/img/magnifier.png"
							style="cursor: hand;"
							onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');"
							alt="Buscar Línea" title="Buscar Línea" /></td>
						<td class="literal" width="7%">E-S Med.</td>
						<c:if test="${perfil !=1}">
							<td width="14%"><form:input path="entmediadora" size="3"
									maxlength="4" cssClass="dato" id="entmediadora" tabindex="6"
									onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
								<form:input path="subentmediadora" size="3" maxlength="4"
									cssClass="dato" id="subentmediadora" tabindex="7" /> <img
								src="jsp/img/magnifier.png" style="cursor: hand;"
								onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"
								alt="Buscar SubEntidad Mediadora"
								title="Buscar SubEntidad Mediadora" /></td>
						</c:if>
						<c:if test="${perfil== 1}">
							<c:if test="${externo == 0}">
								<td width="14%"><form:input path="entmediadora" size="3"
										maxlength="4" cssClass="dato" id="entmediadora" tabindex="6"
										onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
									<form:input path="subentmediadora" size="3" maxlength="4"
										cssClass="dato" id="subentmediadora" tabindex="7" /> <img
									src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"
									alt="Buscar SubEntidad Mediadora"
									title="Buscar SubEntidad Mediadora" /></td>
							</c:if>
							<c:if test="${externo == 1}">
								<td width="14%"><form:input path="entmediadora" size="3"
										maxlength="4" cssClass="dato" id="entmediadora" tabindex="6"
										readonly="true"
										onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
									<form:input path="subentmediadora" size="3" maxlength="4"
										cssClass="dato" id="subentmediadora" tabindex="7"
										readonly="true" /></td>
							</c:if>
						</c:if>

					</tr>
					<tr>
						<td class="literal" width="6%">Colectivo</td>
						<td width="15%"><form:input path="idcolectivo" size="8"
								maxlength="7" cssClass="dato" id="idcolectivo" tabindex="8" /></td>
						<td class="literal" width="5%">Recibo</td>
						<td width="9%"><form:input path="recibo" size="7"
								maxlength="7" cssClass="dato" id="recibo" tabindex="9" /></td>
						<td class="literal" width="4%">Fase</td>
						<td width="4%"><form:input path="fase" size="4" maxlength="4"
								cssClass="dato" id="fase" tabindex="10" /></td>

						<td class="literal" width="7%">F. Emisión</td>
						<td width="12%"><spring:bind path="fechaEmisionRecibo">
								<input type="text" name="fechaEmisionRecibo" id="fechaemi"
									size="10" maxlength="10" class="dato" tabindex="11"
									onchange="if (!ComprobarFecha(this, document.main3, 'Fecha emision')) this.value='';"
									value="<fmt:formatDate pattern="dd/MM/yyyy" value="${infDeudaBean.fechaEmisionRecibo}"/>" />
							</spring:bind> <input type="button" id="btn_fechaemisionrecibo"
							name="btn_fechaemisionrecibo" class="miniCalendario"
							style="cursor: pointer;" /></td>
						<td class="literal" width="6%">F. Acep.</td>
						<td width="12%"><spring:bind path="fechaAceptacion">
								<input type="text" name="fechaAceptacion" id="fechaacep"
									size="10" maxlength="10" class="dato" tabindex="12"
									onchange="if (!ComprobarFecha(this, document.main3, 'Fecha aceptacion')) this.value='';"
									value="<fmt:formatDate pattern="dd/MM/yyyy" value="${infDeudaBean.fechaAceptacion}"/>" />
							</spring:bind> <input type="button" id="btn_fechaaceptacion"
							name="btn_fechaaceptacion" class="miniCalendario"
							style="cursor: pointer;" /></td>
						<td class="literal" width="7%">F. Cierre</td>

						<td width="16%"><spring:bind path="fechaCierre">
								<input type="text" name="fechaCierre" id="fechacie" size="10"
									maxlength="10" class="dato" tabindex="13"
									onchange="if (!ComprobarFecha(this, document.main3, 'Fecha cierre')) this.value='';"
									value="<fmt:formatDate pattern="dd/MM/yyyy" value="${infDeudaBean.fechaCierre}"/>" />
							</spring:bind> <input type="button" id="btn_fechacierre" name="btn_fechacierre"
							class="miniCalendario" style="cursor: pointer;" /></td>
					</tr>
				</table>
			</fieldset>
		</form:form>

		<!-- Grid Jmesa -->
		<div id="grid" style="width: 98%; margin: 0 auto;">
			${consultaDeudaAplazada}</div>

		<!-- Formulario para exportar a excel el listado -->
		<form name="exportToExcel" id="exportToExcel"
			action="informesDeudaAplazada.run" method="post">
			<input type="hidden" name="method" id="method"
				value="doExportToExcel" />
		</form>

		<div style="width: 20%; text-align: center; margin: 0 auto;"
			id="divImprimir">
			<a
				style="font-family: tahoma, verdana, arial; color: #626262; font-size: 11px;">Exportar</a>
			<a id="btnImprimirExcel" style="text-decoration: none;"
				href="javascript:exportToExcel()"> <img
				src="jsp/img/jmesa/excel.gif" />
			</a>
		</div>


	</div>



	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>

	<%@ include
		file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>

</body>
</html>