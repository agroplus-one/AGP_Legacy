<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
<title>Acciones sobre Reduccion Capital</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />

<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jquery.alerts.js"></script>
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
	src="jsp/moduloUtilidades/reduccionCapital/listadoReduccionCapital.js"></script>
<script type="text/javascript"
	src="jsp/moduloUtilidades/reduccionCapital/operacionesReduccionCapital.js"></script>

<script type="text/javascript">
		function cargarFiltro(){
			<c:forEach items="${sessionScope.listadoReduccionCapital_LIMIT.filterSet.filters}" var="filtro">
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
					$('#riesgo').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'idestado'}">
					$('#estado').val('${filtro.value}');
				</c:if>
				
				//P0079361 FECHAS DESDE
				<c:if test="${filtro.property == 'fdanios'}">
					$('#fechadanioId').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'fenv'}">
					$('#fechaEnvioId').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'fenvpol'}">
					$('#fechaEnvioPolId').val('${filtro.value}');
				</c:if>
				//P0079361 FECHAS DESDE
				
				//P0079361 FECHAS HASTA
				/* <c:if test="${filtro.property == 'fdaniosHasta'}">
					$('#fechadanioIdHasta').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'fenvHasta'}">
					$('#fechaEnvioIdHasta').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'fenvpolHasta'}">
					$('#fechaEnvioPolIdHasta').val('${filtro.value}');
				</c:if> */
				//P0079361 FECHAS HASTA
				
				<c:if test="${filtro.property == 'delegacion'}">
					$('#delegacion').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'entmediadora'}">
					$('#entmediadora').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'subentmediadora'}">
					$('#subentmediadora').val('${filtro.value}');
				</c:if>
				
				//P0079361 tipoAnexo de donde sale y define??
 				<c:if test="${filtro.property == 'idcupon'}">
 					$('#tipoEnvioId').val('${filtro.value}');
 				</c:if>
				<c:if test="${filtro.property == 'estado'}">
					$('#estadoCuponId').val('${filtro.value}');
				</c:if>
				//P0079361
			</c:if>
			</c:forEach>
		}
		</script>
<script type="text/javascript">	
			$(document).ready(function(){
				<c:if test="${empty listadoReduccionCapital}">					
					$('#divImprimir').hide(); 									
				</c:if> 				
			});
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
					<td align="right">
					<c:if
							test="${origenLlamada == 'menuGeneral'}">
							<a class="bot" id="btnConsultar"
								href="javascript:consultarInicial();">Consultar</a>
								<script type="text/javascript">	
									$(document).ready(function(){
							        	console.log("Se limpia formulario en la consulta incial para no cachear valor en campos en la nueva llamada desde el menu lateral");
							        	omitirPrimeraBusqueda();
									});
							    </script>
						</c:if> 
						<c:if test="${origenLlamada != 'menuGeneral'}">
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							<script type="text/javascript">	
								$(document).ready(function(){
									if($("#tipoEnvioId").val()==" "){
										console.log("Se oculta el panel de resultados");
										$("#tipoEnvioId").val($.trim($("#tipoEnvioId").val()));
										$('#omitirPrimeraBusquedaBox').hide();
									}else{
										console.log("Mostrando el panel de resultados");
										$('#omitirPrimeraBusquedaBox').show();
									}
								});
							</script>
						</c:if> <%-- P0079361
						<a class="bot" id="btnConsultar" href="javascript:consultarInicial();consultar();">Consultar</a>
						P0079361 --%> <a class="bot" href="javascript:limpiar();">Limpiar</a>
					</td>
				</tr>
			</tbody>
		</table>
	</div>

	<form:form name="main" id="main"
		action="declaracionesReduccionCapital.html" method="post"
		commandName="reduccionCapitalBean">
		<input type="hidden" id="method" name="method" />
		<input type="hidden" id="idPoliza2" name="idPoliza2" />
		<input type="hidden" id="idCupon" name="idCupon" />
		<form:hidden path="id" id="id" />
		<form:hidden path="idenvio" id="idenvio" />
		<form:hidden path="poliza.idpoliza" id="idPoliza" />
		<form:hidden path="poliza.linea.codlinea" id="codlinea" />
		<form:hidden path="poliza.linea.codplan" id="codplan" />
		<form:hidden path="poliza.linea.nomlinea" id="nomlinea" />
		<form:hidden path="poliza.referencia" id="refPoliza" />
		<input type="hidden" name="vieneDeListadoRedCap"
			id="vieneDeListadoRedCap" value="true" />
		<input type="hidden" name="modoLectura" id="modoLectura" />
		<input type="hidden" name="fromUtilidades" value="${fromUtilidades}">
	</form:form>


	<form name="formulario" id="formulario"
		action="declaracionesReduccionCapital.html" method="post">
		<input type="hidden" name="method" id="method" value="" /> <input
			type="hidden" name="idRedCapital" id="idRedCapital" /> <input
			type="hidden" name="idRed" id="idRed" /> <input type="hidden"
			name="idPoliza" id="idPoliza" /> <input type="hidden" name="idPol"
			id="idPol" />

	</form>
	<!-- Formulario para vconsultar la relacion de incidencias asociadas a una poliza en agroseguro -->
	<form:form name="impresionIncidenciasMod" id="impresionIncidenciasMod" action="impresionIncidenciasMod.html" method="post" commandName="reduccionCapitalBean">
		<input type="hidden" id="method" name="method" value="doImprimirIncidencias" />
		<form:hidden path="poliza.linea.codlinea" id="codlinea"/>	
		<form:hidden path="poliza.linea.codplan" id="codplan"/>							
		<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
		<form:hidden path="poliza.referencia" id="refPoliza"/>
		<form:hidden path="poliza.idpoliza" id="idPoliza"/>
		<input type="hidden" id="nombreCompleto" name="nombreCompleto" value="${poliza.asegurado.nombreCompleto}"/>
		<form:hidden path="poliza.codmodulo" id="codmodulo"/>
		<input type="hidden" id="fechaEnvio" name="fechaEnvio" value="${fechaEnvioAnexo}"/>
		<input type="hidden" id="idCuponImpresion" name="idCuponImpresion"/>
	</form:form>

	<form name="print" id="print" action="informes.html" method="post">
		<input type="hidden" name="method" id="methodPrint"
			value="doInformeReduccionCapital" /> <input type="hidden"
			name="idReduccionCapital" id="idReduccionCapital" />

		<form name="limpiar" id="limpiar"
			action="utilidadesReduccionCapital.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta" />
			<input type="hidden" name="origenLlamada" id="origenLlamada"
				value="menuGeneral" />
		</form>


		<!-- Contenido de la página -->
		<div class="conten" style="padding: 3px; width: 100%">
			<p class="titulopag" align="left">Acciones sobre Reducción
				Capital</p>

			<form:form name="main3" id="main3"
				action="utilidadesReduccionCapital.run" method="post"
				commandName="reduccionCapitalUtilidadesBean">

				<input type="hidden" name="method" id="method" value="doConsulta" />
				<input type="hidden" name="primeraBusqueda" id="primeraBusqueda" />
				<input type="hidden" name="grupoEntidades" id="grupoEntidades"
					value="${grupoEntidades}" />
				<input type="hidden" name="grupoOficinas" id="grupoOficinas"
					value="${grupoOficinas}" />
				<input type="hidden" name="propiedadGrupo" id="propiedadGrupo"
					value="" />
				<input type="hidden" name="entidadSubstr" id="entidadSubstr"
					value="">
				<input type="hidden" id="externo" name="externo" value="${externo}" />
				<input type="hidden" name="operacion" id="operacion" value="">

				<input type="hidden" name="fechadanioId.day" value="">
				<input type="hidden" name="fechadanioId.month" value="">
				<input type="hidden" name="fechadanioId.year" value="">

				<input type="hidden" name="fechaEnvioId.day" value="">
				<input type="hidden" name="fechaEnvioId.month" value="">
				<input type="hidden" name="fechaEnvioId.year" value="">

				<input type="hidden" name="fechaEnvioPolId.day" value="">
				<input type="hidden" name="fechaEnvioPolId.month" value="">
				<input type="hidden" name="fechaEnvioPolId.year" value="">

				<!-- P0079361 input hidden fechas-->
				<input type="hidden" name="fechadanioIdHasta.day" value="">
				<input type="hidden" name="fechadanioIdHasta.month" value="">
				<input type="hidden" name="fechadanioIdHasta.year" value="">

				<input type="hidden" name="fechaEnvioIdHasta.day" value="">
				<input type="hidden" name="fechaEnvioIdHasta.month" value="">
				<input type="hidden" name="fechaEnvioIdHasta.year" value="">

				<input type="hidden" name="fechaEnvioPolIdHasta.day" value="">
				<input type="hidden" name="fechaEnvioPolIdHasta.month" value="">
				<input type="hidden" name="fechaEnvioPolIdHasta.year" value="">

				<!-- 			<input type="hidden" name="fdaniosHastaNY" value="">
			<input type="hidden" name="fenvHastaNY" value="">
			<input type="hidden" name="fenvpolHastaNY" value=""> -->
				<!-- P0079361 input hidden fechas-->


				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt">
					<fieldset>
						<legend class="literal">Filtro</legend>
						<table>
							<!-- Primera fila -->
							<tr align="left">
								<td class="literal">Entidad</td>
								<td class="literal" colspan="3" style="width: 640px;"><c:if
										test="${perfil == 0 || perfil == 5}">
										<form:input path="codentidad" size="5" maxlength="4"
											cssClass="dato" id="entidad" tabindex="1"
											onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');" />
										<form:input path="nombreEntidad" cssClass="dato"
											id="desc_entidad" size="40" readonly="true" />
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
											onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');"
											alt="Buscar Entidad" title="Buscar Entidad" />
									</c:if> <c:if test="${perfil > 0 && perfil < 5}">
										<form:input path="codentidad" size="5" maxlength="4"
											cssClass="dato" disabled="disabled" readonly="true"
											id="entidad" tabindex="1" />
										<form:input path="nombreEntidad" cssClass="dato"
											id="desc_entidad" size="40" readonly="true" />
									</c:if> Oficina <c:if
										test="${perfil == 0 || perfil ==1 || perfil == 5}">
										<form:input path="oficina" size="5" maxlength="4"
											cssClass="dato" id="oficina" tabindex="2"
											onchange="javascript:lupas.limpiarCampos('desc_oficina');" />
										<form:input path="" cssClass="dato" id="desc_oficina"
											size="20" readonly="true" />
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
											onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');"
											alt="Buscar Oficina" title="Buscar Oficina" />
									</c:if> <c:if test="${perfil > 2 && perfil < 5}">
										<form:input path="oficina" size="5" maxlength="4"
											cssClass="dato" readonly="true" id="oficina" tabindex="2" />
										<form:input path="nombreOficina" cssClass="dato"
											id="desc_oficina" size="20" readonly="true" />
									</c:if> <c:if test="${perfil == 2}">
										<form:input path="oficina" size="5" maxlength="4"
											cssClass="dato" id="oficina" tabindex="2"
											onchange="javascript:lupas.limpiarCampos('desc_oficina');" />
										<form:input path="nombreOficina" cssClass="dato"
											id="desc_oficina" size="20" readonly="true" />
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
											onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');"
											alt="Buscar Oficina" title="Buscar Oficina" />
									</c:if></td>
								<td class="literal" colspan="1">E-S Mediadora</td>
								<td class="literal"><c:if
										test="${externo == 0 and perfil !=4 }">
										<!--  es interno -->
										<form:input path="entmediadora" size="4" maxlength="4"
											cssClass="dato" id="entmediadora"
											onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();"
											tabindex="3" />
										<form:input path="subentmediadora" size="4" maxlength="4"
											cssClass="dato" id="subentmediadora" tabindex="4" />
										<img src="jsp/img/magnifier.png" style="cursor: hand;"
											onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"
											alt="Buscar SubEntidad Mediadora"
											title="Buscar SubEntidad Mediadora" />
									</c:if> <c:if test="${externo == 1 or (externo==0 and perfil==4)}">
										<!--  es externo -->
										<form:input path="entmediadora" size="4" maxlength="4"
											cssClass="dato" id="entmediadora" tabindex="3"
											readonly="true" />
										<form:input path="subentmediadora" size="4" maxlength="4"
											cssClass="dato" id="subentmediadora" tabindex="4"
											readonly="true" />

									</c:if></td>
								<td class="literal">Delegación</td>
								<td class="literal"><c:if test="${externo == 1}">
										<!--  es externo -->
										<c:if test="${perfil == 1}">
											<form:input path="delegacion" size="4" maxlength="4"
												cssClass="dato" id="delegacion" tabindex="5" />
										</c:if>
										<c:if test="${perfil == 3}">
											<form:input path="delegacion" size="4" maxlength="4"
												cssClass="dato" id="delegacion" tabindex="5" readonly="true" />
										</c:if>
									</c:if> <c:if test="${externo == 0}">
										<!--  es interno -->
										<form:input path="delegacion" size="4" maxlength="4"
											cssClass="dato" id="delegacion" tabindex="5" />
									</c:if></td>
								<td class="literal"><form:hidden path="codusuario"
										id="codusuario" /></td>

								<td class="literal">&nbsp;</td>
							</tr>
							<!-- Segunda fila -->
							<tr align="left">
								<td class="literal">Plan</td>
								<td class="literal"><form:input path="codplan" size="5"
										maxlength="4" cssClass="dato" id="plan" tabindex="8"
										onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" />
								</td>
								<td class="literal">L&iacute;nea &nbsp;</td>
								<td class="literal" colspan="6"><form:input path="codlinea"
										size="3" maxlength="3" cssClass="dato" id="linea" tabindex="9"
										onchange="javascript:lupas.limpiarCampos('desc_linea');" /> <form:input
										path="nomlinea" cssClass="dato" id="desc_linea" size="40"
										readonly="true" /> <img src="jsp/img/magnifier.png"
									style="cursor: hand;"
									onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');"
									alt="Buscar Línea" title="Buscar Línea" /></td>

							</tr>
							<!-- Tercera fila -->
							<tr>
								<td class="literal">P&oacute;liza</td>
								<td class="literal"><form:input path="referencia"
										id="poliza" size="20" maxlength="15" cssClass="dato"
										tabindex="10" onchange="this.value=this.value.toUpperCase();" />
								</td>

								<td class="literal">CIF/NIF Aseg</td>
								<td class="literal"><form:input path="nifcif" id="nifcif"
										size="25" maxlength="9" cssClass="dato" tabindex="15"
										onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);" />
								</td>
								<td class="literal">Asegurado</td>
								<td class="literal" colspan="2"><form:input path="nombre"
										id="nombre" size="35" maxlength="39" cssClass="dato"
										tabindex="16" onchange="this.value=this.value.toUpperCase();" />&nbsp;&nbsp;
								</td>
							</tr>
						</table>
						<table width="100%">
							<tr align="left">
								<td class="literal">Riesgo</td>
								<td class="literal">
									<!-- P0079361 --> <form:select path="codriesgo" id="riesgo"
										cssClass="dato" tabindex="17" cssStyle="width:160px">
										<form:option value="">Todos</form:option>
										<c:forEach items="${riesgos}" var="riesgo">
											<form:option value="${riesgo.id.codriesgo}">${riesgo.id.codriesgo} - ${riesgo.desriesgo}</form:option>
										</c:forEach>
									</form:select> <!--<form:select path="codriesgo" id="causa" cssClass="dato" tabindex="17" cssStyle="width:200px">								
										<form:option value="">Todos</form:option>	
										<c:forEach items="${causas}" var="causa">
											<form:option value="${causa.codigoCausa}">${causa.codigoCausa} - ${causa.descripcion}</form:option>
										</c:forEach>								
									</form:select>--> <!-- P0079361 -->
								<td class="literal"">Estado</td>
								<td class="literal"><form:select path="idestado"
										id="estado" cssClass="dato" tabindex="17"
										cssStyle="width:120px">
										<form:option value="">Todos</form:option>
										<c:forEach items="${estados}" var="estado">
											<form:option value="${estado.idestado}">${estado.descestado}</form:option>
										</c:forEach>
									</form:select></td>

								<td class="literal">Estado Cupón&nbsp;&nbsp;</td>
								<td class="literal"><form:select
										path="cupon.estadoCupon.id" id="estadoCuponId" cssClass="dato"
										cssStyle="width:150px">
										<form:option value="">Todos</form:option>
										<c:forEach items="${estadosCupon}" var="estadoCupon">
											<form:option value="${estadoCupon.id}">${estadoCupon.estado}</form:option>
										</c:forEach>
									</form:select></td>

								<td class="literal">Tipo R.C</td>
								<td class="literal"><form:input path="tipoEnvio"
										id="tipoEnvioId" size="15" maxlength="15" cssClass="dato"
										onchange="this.value=this.value.toUpperCase();" />
									&nbsp;(ftp, nº de cupón)</td>

								<td class="literal">&nbsp;</td>
								<td class="literal">&nbsp;</td>
							</tr>
						</table>
						<table width="100%">
							<!-- P0079361 -->
							<!-- Falta duplicar campos para poder indicar rango de fechas -->
							<tr align="left">

								<td class="literal">
									<fieldset>
										<legend class="literal">Fecha Env&iacute;o</legend>
										<table>
											<tr>
												<td class="literal">desde</td>
												<td><spring:bind path="fenvNY">
														<input type="text" name="fenv" id="fechaEnvioId" size="8"
															maxlength="10" class="dato" tabindex="14"
															onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de envio')) this.value=''"
															value="<fmt:formatDate pattern="dd/MM/yyyy" value="${reduccionCapitalUtilidadesBean.fenv}" />" />
													</spring:bind> <input type="button" id="btn_fechaEnvio"
													name="btn_fechaEnvio" class="miniCalendario"
													style="cursor: pointer;" /></td>
												<td class="literal">hasta</td>
												<td><spring:bind path="fenvHastaNY">
														<input type="text" name="fenvHasta" id="fechaEnvioIdHasta"
															size="8" maxlength="10" class="dato" tabindex="14"
															onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de envio')) this.value=''"
															value="<fmt:formatDate pattern="dd/MM/yyyy" value="${reduccionCapitalUtilidadesBean.fenvHasta}" />" />
													</spring:bind> <input type="button" id="btn_fechaEnvioHasta"
													name="btn_fechaEnvioHasta" class="miniCalendario"
													style="cursor: pointer;" /></td>
											</tr>
										</table>
									</fieldset>
								</td>

								<td class="literal">
									<fieldset>
										<legend class="literal">Fec. Daño</legend>
										<table>
											<tr>
												<td class="literal">desde</td>
												<td><spring:bind path="fdaniosNY">
														<input type="text" name="fdanios" id="fechadanioId"
															size="8" maxlength="10" class="dato" tabindex="14"
															onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de da\xf1o')) this.value=''"
															value="<fmt:formatDate pattern="dd/MM/yyyy" value="${reduccionCapitalUtilidadesBean.fdanios}" />" />
													</spring:bind> <input type="button" id="btn_fecha" name="btn_fechadanio"
													class="miniCalendario" style="cursor: pointer;" /></td>
												<td class="literal">hasta</td>
												<td><spring:bind path="fdaniosHastaNY">
														<input type="text" name="fdaniosHasta"
															id="fechadanioIdHasta" size="8" maxlength="10"
															class="dato" tabindex="14"
															onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de da\xf1o')) this.value=''"
															value="<fmt:formatDate pattern="dd/MM/yyyy" value="${reduccionCapitalUtilidadesBean.fdaniosHasta}" />" />
													</spring:bind> <input type="button" id="btn_fechaHasta"
													name="btn_fechadanioHasta" class="miniCalendario"
													style="cursor: pointer;" /></td>
											</tr>
										</table>
									</fieldset>
								</td>

								<td class="literal">
									<fieldset>
										<legend class="literal">Fec. Env&iacute;o Pol.</legend>
										<table>
											<tr>
												<td class="literal">desde</td>
												<td><spring:bind path="fenvpolNY">
														<input type="text" name="fenvpol" id="fechaEnvioPolId"
															size="8" maxlength="10" class="dato" tabindex="14"
															onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de envio de poliza')) this.value=''"
															value="<fmt:formatDate pattern="dd/MM/yyyy" value="${reduccionCapitalUtilidadesBean.fenvpol}" />" />
													</spring:bind> <input type="button" id="btn_fechaEnvioPol"
													name="btn_fechaEnvioPol" class="miniCalendario"
													style="cursor: pointer;" /></td>
												<td class="literal">hasta</td>
												<td><spring:bind path="fenvpolHastaNY">
														<input type="text" name="fenvpolHasta"
															id="fechaEnvioPolIdHasta" size="8" maxlength="10"
															class="dato" tabindex="14"
															onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha de envio de poliza')) this.value=''"
															value="<fmt:formatDate pattern="dd/MM/yyyy" value="${reduccionCapitalUtilidadesBean.fenvpolHasta}" />" />
													</spring:bind> <input type="button" id="btn_fechaEnvioPolHasta"
													name="btn_fechaEnvioPolHasta" class="miniCalendario"
													style="cursor: pointer;" /></td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			<div id="omitirPrimeraBusquedaBox">
				<!-- Grid Jmesa -->
				<div id="grid">${listadoReduccionCapital}</div>

				<!-- Formulario para exportar a excel el listado -->
				<form name="exportToExcel" id="exportToExcel"
					action="utilidadesReduccionCapital.run" method="post">
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
		</div>

		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>

		<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include
			file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
</body>
</html>