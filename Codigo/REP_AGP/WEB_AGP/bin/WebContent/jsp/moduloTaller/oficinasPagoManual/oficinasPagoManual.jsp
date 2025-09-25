<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
<title>Mantenimiento de oficinas con pago manual</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />

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
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>


<script type="text/javascript"
	src="jsp/moduloTaller/oficinasPagoManual/oficinasPagoManual.js"></script>
<script type="text/javascript"
	src="jsp/moduloTaller/oficinasPagoManual/oficinasPagoManualCambioMasivo.js"></script>

<%@ include file="/jsp/js/draggable.jsp"%>

<script type="text/javascript">

			 function cargarFiltro(){
				 <c:if test="${origenLlamada != 'menuGeneral'}">	
					<c:forEach items="${sessionScope.consultaOficinasPagoManual_LIMIT.filterSet.filters}" var="filtro">
						<c:if test="${filtro.property == 'id.codentidad'}">
							$('#entidad').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'id.codoficina'}">
							$('#oficina').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'pagoManual'}">
							$('#pagoManual').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'nomoficina'}">
							$('#desc_oficina').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'codZona'}">
							// Se deben seleccionar los que vengan en el filtro
							var zonasArr = '${filtro.value}'.split(',');
							var cmbObj = document.getElementById('listaZonas2');
							for (var i = 0; i < zonasArr.length; i++) {
								for ( var j = 0; j < cmbObj.options.length; j++) {									
									var optObj = cmbObj.options[j];
									if (optObj.value == zonasArr[i]) {
										optObj.selected = true;
									}
								}								
							}
						</c:if>
					</c:forEach>
				</c:if>
			} 
		
		</script>

<style>
#divImprimir {
	display: none;
}
</style>

</head>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="cargarFiltro();">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	<!-- botones de la página -->
	<div id="buttons">
		<table width="97%" cellspacing="10" cellpadding="0" border="0">
			<tbody>
				<tr>
					<td align="left"><a class="bot" id="btnCambioMasivo"
						href="javascript:cambioMasivo();">Cambio Masivo</a> <a class="bot"
						id="btnAdiccionMasiva" href="javascript:adiccionMasiva();">Adición
							Masiva</a></td>
					<td align="right"><c:if test="${showModificar == 'true'}">
							<a class="bot" id="btnModificar" href="javascript:modificar();">Modificar</a>
						</c:if> <c:if test="${showModificar != 'true'}">
							<a class="bot" id="btnModificar" style="display: none"
								href="javascript:modificar();">Modificar</a>
						</c:if> <a class="bot" id="btnAlta" href="javascript:alta();">Alta</a> <a
						class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
						<a class="bot" href="javascript:limpiar();">Limpiar</a></td>
				</tr>
			</tbody>
		</table>
	</div>
	<form name="limpiar" id="limpiar" action="pagoManual.run" method="post">
		<input type="hidden" name="method" id="method" value="doConsulta" /> <input
			type="hidden" name="origenLlamada" id="origenLlamada"
			value="menuGeneral" />
	</form>
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">OFICINAS CON PAGO MANUAL</p>

		<form:form name="main3" id="main3" action="pagoManual.run"
			method="post" commandName="oficinaBean">

			<input type="hidden" name="method" id="method" />
			<input type="hidden" name="origenLlamada" id="origenLlamada" />
			<input type="hidden" name="grupoEntidades" id="grupoEntidades"
				value="${grupoEntidades}" />
			<input type="hidden" name="grupoOficinas" id="grupoOficinas" value="" />
			<input type="hidden" name="listaZonas" id="listaZonas"
				value="${listaZonas}" />

			<!-- Cambio masivo -->
			<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados"
				value="" />
			<input type="hidden" name="marcaTodos" id="marcaTodos" value="false" />
			<input type="hidden" name="listaIdsTodos" id="listaIdsTodos"
				value="${listaIdsTodos}" />
			<input type="hidden" name="zonaSel" id="zonaSel" value="${zonaSel}" />
			<input type="hidden" name="zonaSelModif" id="zonaSelModif"
				value="${zonaSelModif}" />
			<input type="hidden" name="zonaSelAlta" id="zonaSelAlta"
				value="${zonaSelAlta}" />
			<input type="hidden" name="entidadAnterior" id="entidadAnterior"
				value="${entidadAnterior}" />

			<form:hidden path="idgrupo" id="idgrupo" />

			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<div>
				<fieldset style="width: 81%; margin: 0 auto;">
					<legend class="literal">Filtro</legend>
					<table style="margin: 0 auto;">
						<tr align="left">
							<td class="literal">Entidad</td>
							<td class="literal" colspan="3"><form:input
									path="id.codentidad" size="5" maxlength="4" cssClass="dato"
									id="entidad" tabindex="1"
									onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');obtenerZonasEntidad(this.value)" />
								<form:input path="entidad.nomentidad" cssClass="dato"
									id="desc_entidad" size="40" readonly="true" /> <img
								src="jsp/img/magnifier.png" style="cursor: hand;"
								onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');"
								alt="Buscar Entidad" title="Buscar Entidad" /></td>
							<td class="literal">Oficina</td>
							<td class="literal"><form:input path="id.codoficina"
									size="5" maxlength="4" cssClass="dato" id="oficina"
									tabindex="2"
									onchange="javascript:lupas.limpiarCampos('desc_oficina');" /> <form:input
									path="nomoficina" cssClass="dato" id="desc_oficina" size="20"
									tabindex="3" onchange="this.value=this.value.toUpperCase();" />
								<img src="jsp/img/magnifier.png" style="cursor: hand;"
								onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');"
								alt="Buscar Oficina" title="Buscar Oficina" /></td>
							<td class="literal">Permitir pago manual</td>
							<td class="literal"><form:select path="pagoManual"
									cssClass="dato" cssStyle="width:70" id="pagoManual"
									tabindex="4">
									<form:option value="">Todos</form:option>
									<form:option value="1">Si</form:option>
									<form:option value="0">No</form:option>
								</form:select></td>
							<td class="literal" style="vertical-align: 'super';"
								style="padding:0.0em 1em">Zonas</td>
							<td class="literal"><select name="listaZonas2"
								id="listaZonas2" class="dato" multiple="multiple"
								style="height: 140px;">
									<c:if test="${listaZonasEnt != null}">
										<c:forEach items="${listaZonasEnt}" var="zon">
											<option onclick="obtenerEntidadZona(this.value);"
												value="${zon.id.codentidad}-${zon.id.codzona}"
												id="esZona_${zon.id.codentidad}-${zon.id.codzona}">${zon.nomzona}</option>
										</c:forEach>
									</c:if>
							</select> <!--  Pet. 63701 ** MODIF TAM (18.06.2021) ** Fin -->
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>

		<form:form name="frmBorrar" id="frmBorrar" action="pagoManual.run"
			method="post" commandName="oficinaBean">
			<form:hidden path="id.codentidad" id="entidadBorrar" />
			<form:hidden path="id.codoficina" id="oficinaBorrar" />
			<form:hidden path="nomoficina" id="nomoficinaBorrar" />
			<form:hidden path="pagoManual" id="pagoManualBorrar" />
			<form:hidden path="entidad.nomentidad" id="nomentidadBorrar" />
			<input type="hidden" name="method" id="methodBorrar" />
		</form:form>

		<!-- Grid Jmesa -->
		<div id="grid" style="width: 80%; margin: 0 auto;">
			${consultaOficinasPagoManual}</div>
				
		<!-- Formulario para exportar a excel el listado -->
		<form name="exportToExcel" id="exportToExcel"
			action=pagoManual.run method="post">
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
	<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>
	<!-- ************* -->
	<!-- POPUP  AVISO  -->
	<!-- ************* -->

	<div id="popUpAvisos" class="parcelasRepWindow"
		style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;">
		<!--  header popup -->
		<div id="header-popup"
			style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
			<div
				style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Aviso</div>
			<a
				style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="hidePopUpAviso()">x</span>
			</a>
		</div>
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion2" class="panelInformacion">
				<div id="txt_mensaje_aviso"></div>
			</div>
			<div style="margin-top: 15px">
				<a class="bot" id="btn_hidePopUpAviso"
					href="javascript:hidePopUpAviso()" title="Aceptar">Aceptar</a>
			</div>
		</div>
	</div>

	<!-- **************************** -->
	<!-- PANEL CAMBIO MASIVO OFICINAS  -->
	<!-- **************************** -->
	<div id="panelCambioMasivoOficinas" class="wrapper_popup"
		style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;">

		<!--  header popup -->
		<div id="header-popup"
			style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
			<div
				style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Cambio
				masivo</div>
			<a
				style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cerrarCambioMasivoOficinas()">x</span>
			</a>
		</div>
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacionCM" class="panelInformacion">
				<div id="txt_mensaje_cm"
					style="color: red; display: block; font-size: 12px; font-style: italic; font-weight: bold; line-height: 20px;"></div>
				<form:form name="main" id="main" action="pagoManual.run"
					method="post" commandName="oficinaBean">

					<input type="hidden" name="method" id="method"
						value="doCambioMasivo" />
					<input type="hidden" name="listaIdsMarcados_cm"
						id="listaIdsMarcados_cm" value="" />
					<input type="hidden" name="zonaSelcm" id="zonaSelcm"
						value="${zonaSelcm}" />
					<input type="hidden" name="adiccionMasiva" id="adiccionMasiva"
						value="${adiccionMasiva}" />
					<input type="hidden" name="showPagoManual" id="showPagoManual"
						value="${showPagoManual}" />


					<div class="panel2 isrt" style="width: 70%">
						<fieldset>
							<table style="margin: 0 auto;">
								<tr>
									<td class="literal" id="txtpagoManual" name="txtpagoManual"
										style="display:" width="25%">Permitir pago manual</td>
									<td class="literal" id="valPagoManual" name="valPagoManual"
										style="display:" width="20%"><form:select
											path="pagoManual" cssClass="dato" cssStyle="width:70"
											id="pagoManual" tabindex="4">
											<form:option value="1">Si</form:option>
											<form:option value="0">No</form:option>
										</form:select></td>

									<td class="literal" width="10%"
										style="vertical-align: 'super';" cssStyle="width:70"
										style="padding:0.0em 1em">Zonas</td>
									<td class="literal" width="30%"><select
										multiple="multiple" name="listaZonascm" id="listaZonascm"
										size="3" class="dato"
										style="height: 140px; border-width: 1px; border-style: solid; border-color: #004539;"
										value="${listaZonasEnt_CM}">
											<c:forEach items="${listaZonascm}" var="zona">
												<option value="${zona.id.codentidad}-${zona.id.codzona}"
													id="esZona_${zona.id.codentidad}-${zona.id.codzona}">${zona.nomzona}</option>
											</c:forEach>
									</select></td>
								</tr>
							</table>
						</fieldset>
					</div>

				</form:form>
			</div>
			<div style="margin-top: 15px">
				<a class="bot" href="javascript:cerrarCambioMasivoOficinas()"
					title="Cancelar">Cancelar</a> <a class="bot"
					href="javascript:aplicarCambioMasivoOficinas()" title="Aplicar">Aplicar</a>
			</div>
		</div>
	</div>

	<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>


</body>
</html>