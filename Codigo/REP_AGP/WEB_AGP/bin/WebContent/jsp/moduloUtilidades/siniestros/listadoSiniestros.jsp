<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
<title>Acciones sobre Siniestros</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript"
	src="jsp/moduloPolizas/polizas/imprimir.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
<%@ include file="/jsp/js/draggable.jsp"%>
<script type="text/javascript"
	src="jsp/moduloUtilidades/siniestros/listadoSiniestros.js"></script>


<script type="text/javascript">
		function cargarFiltro(){
			var riesgoStr = '';
			<c:forEach items="${sessionScope.listadoSiniestros_LIMIT.filterSet.filters}" var="filtro">
			<c:if test="${origenLlamada != 'menuGeneral'}">
				<c:if test="${filtro.property == 'codentidad'}">
					$('#entidad').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'oficina'}">
					$('#oficina').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'codplan'}">
					$('#plan').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'codlinea'}">
					$('#linea').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'referencia'}">
					$('#poliza').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'nifcif'}">
					$('#nifcif').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'nombre'}">
					$('#nombre').val('${filtro.value}');
				</c:if>				
				<c:if test="${filtro.property == 'codriesgo'}">
					riesgoStr = '${filtro.value}' + riesgoStr;
				</c:if>
				<c:if test="${filtro.property == 'codgruposeguro'}">
					riesgoStr += ';' + '${filtro.value}';
				</c:if>
				<c:if test="${filtro.property == 'idestado'}">
					$('#estado').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'focurr'}">
					$('#fechaocurrenciaId').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'ffirma'}">
					$('#fecfirmasiniestroId').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'fenv'}">
					$('#fechaEnvioId').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'fenvpol'}">
					$('#fechaEnvioPolId').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'delegacion'}">
					$('#delegacion').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'entmediadora'}">
					$('#entmediadora').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'subentmediadora'}">
					$('#subentmediadora').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'numerosiniestro'}">
					$('#numerosiniestro').val('${filtro.value}');
				</c:if>				
			</c:if>
			</c:forEach>
			$('#riesgo').val(riesgoStr);
		}
		</script>
		
		<style>
		    #divImprimir {
		        display: none;
		    }
		</style>
		
</head>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="javascript:SwitchMenu('sub4');cargarFiltro();">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	<!-- botones de la página -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tbody>
				<tr>
					<td align="right"><c:if
							test="${origenLlamada == 'menuGeneral'}">
							<a class="bot" id="btnConsultar"
								href="javascript:consultarInicial();">Consultar</a>
						</c:if> <c:if test="${origenLlamada != 'menuGeneral'}">
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
						</c:if> <a class="bot" href="javascript:limpiar();">Limpiar</a></td>
				</tr>
			</tbody>
		</table>
	</div>


	<form name="formulario" id="formulario" action="siniestros.html"
		method="post">
		<input type="hidden" name="method" id="method" value="" /> <input
			type="hidden" name="idSiniestro" id="idSiniestro" /> <input
			type="hidden" name="idSin" id="idSin" /> <input type="hidden"
			name="idPoliza" id="idPoliza" /> <input type="hidden" name="idPol"
			id="idPol" /> <input type="hidden" name="fromUtilidades"
			id="fromUtilidades" value="false" /> <input type="hidden"
			name="modoLectura" id="modoLectura" /> <input type="hidden"
			name="origenLlamada" id="origenLlamada" value="listadoSiniestros" />
	</form>

	<form name="print" id="print" action="informes.html" method="post">
		<input type="hidden" name="method" id="methodPrint"
			value="doInformeSiniestro" /> <input type="hidden" name="idSiniestro"
			id="idSiniestro" />
	</form>

	<form name="limpiar" id="limpiar" action="utilidadesSiniestros.run"
		method="post">
		<input type="hidden" name="method" id="method" value="doConsulta" /> <input
			type="hidden" name="origenLlamada" id="origenLlamada"
			value="menuGeneral" />
	</form>


	<form name="frmPdfParte" id="frmPdfParte" action="siniestrosInformacion.html" method="post">
		<input type="hidden" name="method" id="method_parte" value="doPdfParte" /> 
		<input type="hidden" name="serieSiniestro" id="serieSiniestro" /> 
		<input type="hidden" name="numSiniestro" id="numSiniestro" /> 
		<input type="hidden" name="idPoliza" id="idPoliza_parte" /> 
		<input type="hidden" name="idSiniestro" id="idSiniestro" />
		<input type="hidden" name="numeroSiniestro" id="numeroSiniestro"/>
	</form>

	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 101%;">
		<p class="titulopag" align="left">Acciones sobre Siniestros</p>

		<form:form name="main3" id="main3" action="utilidadesSiniestros.run"
			method="post" commandName="siniestroBean">

			<input type="hidden" name="method" id="method" value="doConsulta" />
			<input type="hidden" name="primeraBusqueda" id="primeraBusqueda" />
			<input type="hidden" name="grupoEntidades" id="grupoEntidades"
				value="${grupoEntidades}" />
			<input type="hidden" name="propiedadGrupo" id="propiedadGrupo"
				value="" />
			<input type="hidden" name="grupoOficinas" id="grupoOficinas"
				value="${grupoOficinas}" />
			<input type="hidden" name="fechaocurrenciaId.day" value="">
			<input type="hidden" name="fechaocurrenciaId.month" value="">
			<input type="hidden" name="fechaocurrenciaId.year" value="">

			<input type="hidden" name="fecfirmasiniestroId.day" value="">
			<input type="hidden" name="fecfirmasiniestroId.month" value="">
			<input type="hidden" name="fecfirmasiniestroId.year" value="">

			<input type="hidden" name="fechaEnvioId.day" value="">
			<input type="hidden" name="fechaEnvioId.month" value="">
			<input type="hidden" name="fechaEnvioId.year" value="">

			<input type="hidden" name="fechaEnvioPolId.day" value="">
			<input type="hidden" name="fechaEnvioPolId.month" value="">
			<input type="hidden" name="fechaEnvioPolId.year" value="">

			<input type="hidden" name="grupoOficinas" id="grupoOficinas"
				value="${grupoOficinas}" />
			<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
			<input type="hidden" id="externo" value="${externo}" />
			<%--  <input type="text" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/> --%>

			<form:hidden path="codusuario" id="codusuario" />
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<div class="panel2 isrt" style="width: auto;">
				<fieldset width="100%">
					<legend class="literal">Filtro</legend>
					<table width="100%" border="0">
						<!-- Primera fila -->
						<tr>
							<td width="7%" class="literal">Entidad</td>
							<td width="23%">
								<c:if test="${perfil == 0 || perfil == 5}">
									<form:input path="codentidad" size="3" maxlength="4"
										cssClass="dato" id="entidad" tabindex="1"
										onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');" />
									<form:input path="nombreEntidad" cssClass="dato"
										id="desc_entidad" size="21" readonly="true" />
									<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');"
										alt="Buscar Entidad" title="Buscar Entidad" />
								</c:if> 
								<c:if test="${perfil > 0 && perfil < 5}">
									<form:input path="codentidad" size="3" maxlength="4"
										cssClass="dato" disabled="disabled" readonly="true"
										id="entidad" tabindex="1" />
									<form:input path="nombreEntidad" cssClass="dato"
										id="desc_entidad" size="21" readonly="true" />
								</c:if>
							</td>
							<td width="8%" class="literal">Oficina</td>
							<td width="17%">
								<c:if test="${perfil == 0 || perfil ==1 || perfil == 5}">
									<form:input path="oficina" size="3" maxlength="4"
										cssClass="dato" id="oficina" tabindex="2"
										onchange="javascript:lupas.limpiarCampos('desc_oficina');" />
									<form:input path="" cssClass="dato" id="desc_oficina" size="17"
										readonly="true" />
									<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');"
										alt="Buscar Oficina" title="Buscar Oficina" />
								</c:if> 
								<c:if test="${perfil >2 && perfil < 5}">
									<form:input path="oficina" size="3" maxlength="4"
										cssClass="dato" readonly="true" id="oficina" tabindex="2" />
									<form:input path="nombreOficina" cssClass="dato"
										id="desc_oficina" size="17" readonly="true" />
								</c:if> 
								<c:if test="${perfil == 2}">
									<form:input path="oficina" size="3" maxlength="4"
										cssClass="dato" id="oficina" tabindex="2"
										onchange="javascript:lupas.limpiarCampos('desc_oficina');" />
									<form:input path="nombreOficina" cssClass="dato"
										id="desc_oficina" size="17" readonly="true" />
									<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');"
										alt="Buscar Oficina" title="Buscar Oficina" />
								</c:if>
							</td>
							<td width="3%" class="literal" colspan="1">E-S Med</td>
							<td width="12%">
								<c:if test="${externo == 0 and perfil !=4 }">
									<!--  es interno -->
									<form:input path="entmediadora" size="3" maxlength="4"
										cssClass="dato" id="entmediadora"
										onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();"
										tabindex="3" />
									<form:input path="subentmediadora" size="3" maxlength="4"
										cssClass="dato" id="subentmediadora" tabindex="4" />
									<img src="jsp/img/magnifier.png" style="cursor: hand;"
										onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"
										alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
								</c:if> 
								<c:if test="${externo == 1 or (externo==0 and perfil==4)}">
									<!--  es externo -->
									<form:input path="entmediadora" size="3" maxlength="4"
										cssClass="dato" id="entmediadora" tabindex="3" readonly="true" />
									<form:input path="subentmediadora" size="3" maxlength="4"
										cssClass="dato" id="subentmediadora" tabindex="4"
										readonly="true" />

								</c:if>
							</td>
							<td width="8%" class="literal">&nbspDelegación</td>
							<td width="11%">
								<c:if test="${externo == 1}">
									<!--  es externo -->
									<c:if test="${perfil == 1}">
										<form:input path="delegacion" size="3" maxlength="4"
											cssClass="dato" id="delegacion" tabindex="5" />
									</c:if>
									<c:if test="${perfil == 3}">
										<form:input path="delegacion" size="3" maxlength="4"
											cssClass="dato" id="delegacion" tabindex="5" readonly="true" />
									</c:if>
								</c:if> 
								<c:if test="${externo == 0}">
									<!--  es interno -->
									<form:input path="delegacion" size="3" maxlength="4"
										cssClass="dato" id="delegacion" tabindex="5" />
								</c:if>
							</td>
						</tr>

						<!-- 2 fila -->
						<tr align="left">
							<td class="literal">Plan</td>
							<td><form:input path="codplan" size="5" maxlength="4"
									cssClass="dato" id="plan" tabindex="8"
									onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" />
							</td>
							<td class="literal">L&iacute;nea &nbsp;</td>
							<td colspan="6"><form:input path="codlinea" size="3"
									maxlength="3" cssClass="dato" id="linea" tabindex="9"
									onchange="javascript:lupas.limpiarCampos('desc_linea');" /> <form:input
									path="nomlinea" cssClass="dato" id="desc_linea" size="40"
									readonly="true" /> <img src="jsp/img/magnifier.png"
								style="cursor: hand;"
								onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');"
								alt="Buscar Línea" title="Buscar Línea" /></td>
						</tr>
						<!-- 3 fila -->
						<tr>
							<td class="literal">P&oacute;liza</td>
							<td><form:input path="referencia" id="poliza" size="20"
									maxlength="15" cssClass="dato" tabindex="10"
									onchange="this.value=this.value.toUpperCase();" /></td>

							<td class="literal">CIF/NIF Aseg</td>
							<td><form:input path="nifcif" id="nifcif" size="25"
									maxlength="9" cssClass="dato" tabindex="15"
									onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);" />
							</td>
							<td class="literal">Asegurado</td>
							<td colspan="3"><form:input path="nombre" id="nombre"
									size="35" maxlength="39" cssClass="dato" tabindex="16"
									onchange="this.value=this.value.toUpperCase();" />&nbsp;&nbsp;
							</td>
						</tr>
						<!-- 4 fila -->
						<tr align="left">
							<td class="literal">Riesgo</td>
							<td><form:select path="codriesgo" id="riesgo"
									cssClass="dato" tabindex="17" cssStyle="width:200px">
									<form:option value="">Todos</form:option>
									<c:forEach items="${riesgos}" var="riesgo">
										<form:option value="${riesgo.id.codriesgo}">${riesgo.id.codriesgo} - ${riesgo.desriesgo}</form:option>
									</c:forEach>
								</form:select></td>
							<td class="literal">Estado</td>
							<td><form:select path="idestado" id="estado" cssClass="dato"
									tabindex="17" cssStyle="width:200px">
									<form:option value="">Todos</form:option>
									<c:forEach items="${estados}" var="estado">
										<form:option value="${estado.idestado}">${estado.descestado}</form:option>
									</c:forEach>
								</form:select></td>
							<td class="literal">Num.Aviso</td>
							<td><form:input path="numerosiniestro" id="numerosiniestro"
									size="10" maxlength="15" cssClass="dato" tabindex="18" /></td>
						</tr>
						<!-- 5 fila -->
						<tr>
							<td class="literal">Fec. Firma</td>
							<td><bind> <input type="text" name="ffirma"
									id="fecfirmasiniestroId" size="10" maxlength="10" class="dato"
									tabindex="14"
									onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de firma')) this.value=''"
									value="<fmt:formatDate pattern="dd/MM/yyyy" value="${ffirma}" />" />
								</bind> <input type="button" id="btn_fecfirmasiniestro"
								name="btn_fecfirmasiniestro" class="miniCalendario"
								style="cursor: pointer;" /></td>
							<td class="literal" nowrap="nowrap">Fec. Ocurrencia</td>
							<td><bind> <input type="text" name="focurr"
									id="fechaocurrenciaId" size="10" maxlength="10" class="dato"
									tabindex="14"
									onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de ocurrencia')) this.value=''"
									value="<fmt:formatDate pattern="dd/MM/yyyy" value="${focurr}" />" />
								</bind> <input type="button" id="btn_fechaocurrencia"
								name="btn_fechaocurrencia" class="miniCalendario"
								style="cursor: pointer;" /></td>



							<td class="literal">Fec. Env&iacute;o</td>
							<td><bind> <input type="text" name="fenv"
									id="fechaEnvioId" size="10" maxlength="10" class="dato"
									tabindex="14"
									onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de envio')) this.value=''"
									value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fenv}" />" />
								</bind> <input type="button" id="btn_fechaEnvio" name="btn_fechaEnvio"
								class="miniCalendario" style="cursor: pointer;" /></td>

							<td class="literal">Fec. Env&iacute;o Pol.</td>
							<td><bind> <input type="text" name="fenvpol"
									id="fechaEnvioPolId" size="10" maxlength="10" class="dato"
									tabindex="14"
									onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de envio de poliza')) this.value=''"
									value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fenvpol}" />" />
								</bind> <input type="button" id="btn_fechaEnvioPol"
								name="btn_fechaEnvioPol" class="miniCalendario"
								style="cursor: pointer;" /></td>
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>

		<!-- Grid Jmesa -->
		<div id="grid">${listadoSiniestros}</div>
		
		<!-- Formulario para exportar a excel el listado -->
		<form name="exportToExcel" id="exportToExcel"
			action="utilidadesSiniestros.run" method="post">
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
	<%@ include	file="/jsp/moduloUtilidades/siniestros/popupListadoSiniestro.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaUsuarioFiltros.jsp"%>

</body>
</html>