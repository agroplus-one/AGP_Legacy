<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<meta charset="utf-8">
  		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Pólizas de RC de Ganado</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<!-- <link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" /> -->
		
		<script type="text/javascript" src="jsp/js/menuapli.js" ></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/moduloRC/polizasRC.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript">
			function cargarFiltro() {
			 	<c:if test="${origenLlamada ne 'menuGeneral'}">	
					<c:forEach items="${sessionScope.listaPolizasRC_LIMIT.filterSet.filters}" var="filtro">
						<c:if test="${filtro.property eq 'entidad'}">
							$('#entidad').val('${filtro.value}');
						</c:if>	
						<c:if test="${filtro.property eq 'oficina'}">
							$('#oficina').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'usuario'}">
							$('#usuario').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'plan'}">
							$('#plan').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'linea'}">
							$('#linea').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'refcolectivo'}">
							$('#refcolectivo').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'refpoliza'}">
							$('#refpoliza').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'nifcif'}">
							$('#nifcif').val('${filtro.value}');
						</c:if>						
						<c:if test="${filtro.property eq 'clase'}">
							$('#clase').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'estadopol'}">
							$('#estadopol').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'fecenviorc'}">
							$('#fecenviorcId').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'sumaasegurada'}">
							$('#sumaAsegurada').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'importe'}">
							$('#importe').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'estadorc'}">
							$('#estadorc').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'errorrc'}">
							$('#errorrc').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property eq 'refomega'}">
							$('#refomega').val('${filtro.value}');
						</c:if>
						
 						<c:if test="${filtro.property eq 'nsolicitud'}">
 							$('#nsolicitud').val('${filtro.value}');
 						</c:if>

					</c:forEach>
				</c:if>
			};
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub14'); cargarFiltro();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td width="5">&nbsp;</td>
					<td align="right">
						<c:choose>
							<c:when test="${origenLlamada eq 'menuGeneral'}">
								<a class="bot" id="btnConsultar" href="javascript:consultarInicial()">Consultar</a>
							</c:when>
							<c:otherwise>
								<a class="bot" id="btnConsultar" href="javascript:consultar()">Consultar</a>
							</c:otherwise>
						</c:choose>
						<a class="bot" id="btnLimpiar" href="javascript:limpiar()">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		
		<!-- Formulario principal de la pagina -->
		<div class="conten" style="padding: 3px; width: 97%">
			<p class="titulopag" align="left">LISTADO DE PÓLIZAS DE RC DE GANADO</p>
			
			<form name="limpiar" id="limpiar" action="listadoRCGanado.run" method="post">								
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
			</form>
		
			<form:form id="main3" name="main3" action="listadoRCGanado.run" method="POST" commandName="vistaPolizasRC">
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>

			<c:set var="nombreOficina" value="${sessionScope.usuario.oficina.nomoficina}" />
				<c:set var="codigoOficina" value="${sessionScope.usuario.oficina.id.codoficina}" />
				<c:set var="nombreEntidad" value="${sessionScope.usuario.oficina.entidad.nomentidad}" />
				<c:set var="codigoEntidad" value="${sessionScope.usuario.oficina.entidad.codentidad}" />
				<c:set var="codUsuario" value="${sessionScope.usuario.codusuario}" /> 
				<c:set var="perfil" value="${sessionScope.usuario.tipousuario}" />
				
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="origenLlamada" id="origenLlamada" />
				
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<input type="hidden" name="grupoOficinas" id="grupoOficinas" value="${grupoOficinas}"/>
				<input type="hidden" name="perfilUsuario" id="perfilUsuario" value="${perfil}" />
				
				<input type="hidden" name="nomEntidadUsuario" id="nomEntidadUsuario" value="${nombreEntidad}" />
				<input type="hidden" name="codEntidadUsuario" id="codEntidadUsuario" value="${codigoEntidad}" />
				<input type="hidden" name="nomOficinaUsuario" id="nomOficinaUsuario" value="${nombreOficina}" />
				<input type="hidden" name="codOficinaUsuario" id="codOficinaUsuario" value="${codigoOficina}" />
				<input type="hidden" name="codUsuario" id="codUsuario" value="${codUsuario}" />
				
				<input type="hidden" name="fecenviorcId.day" value="" />
				<input type="hidden" name="fecenviorcId.month" value="" />
				<input type="hidden" name="fecenviorcId.year" value="" />
				
				<form:hidden path="sumaasegurada" id="sumaAsegurada" />
				<form:hidden path="importe" id="importe" />
				
				<form:hidden path="codespecierc" id="especierc" />
				<form:hidden path="idpoliza" id="idpoliza" />
				<!--<form:hidden path="nsolicitud" id="nsolicitud"/>-->
				
				<div class="panel2">
					<fieldset>
						<legend class="literal">Filtro</legend>
						<table style="margin:0 auto;">
							<tr align="left">
								<td class="literal">Entidad</td>
								<td colspan="3">	
									<c:if test="${perfil eq 0 or perfil eq 5}">
										<form:input path="entidad" tabindex="1" size="4" maxlength="4" cssClass="dato" id="entidad" onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');" />
										<input value="${nomEntidad}" class="dato" id="desc_entidad" name="desc_entidad" size="40" type="text" />
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
									</c:if>
									<c:if test="${perfil gt 0 && perfil lt 5}">
										<form:input path="entidad" tabindex="1" size="4" maxlength="4" cssClass="dato" disabled="disabled" readonly="true" id="entidad" />
										<input value="${nomEntidad}" class="dato" id="desc_entidad" name="desc_entidad" size="40" readonly />
									</c:if>
								</td>
								<td class="literal">Oficina</td>
								<td>
									<c:if test="${perfil == 0 || perfil == 1 || perfil == 5 }">
										<form:input path="oficina" tabindex="2" size="5" maxlength="4" cssClass="dato" id="oficina" onchange="javascript:lupas.limpiarCampos('desc_oficina');" />
										<input value="" class="dato" id="desc_oficina" name="desc_oficina" size="20" readonly="true" />
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
									</c:if>
									<c:if test="${perfil == 2}">
										<form:input path="oficina" tabindex="2" size="5" maxlength="4" cssClass="dato" id="oficina" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
										<input value="${nombreOficina}" class="dato" id="desc_oficina" name="desc_oficina" size="20" />
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
									</c:if>
									<c:if test="${perfil == 3}">
										<form:input path="oficina" tabindex="2" size="5" maxlength="4" cssClass="dato" readonly="true" id="oficina" />
										<input value="${nombreOficina}" class="dato" id="desc_oficina" name="desc_oficina" size="20" readonly />
									</c:if>
									<c:if test="${perfil == 4}">
										<form:input path="oficina" tabindex="2" size="5" maxlength="4" cssClass="dato" readonly="true" id="oficina" />
										<input value="" class="dato" id="desc_oficina" name="desc_oficina" size="20" readonly />
									</c:if>
								</td>
								<td class="literal">Usuario</td>
								<td >
									<c:if test="${perfil ne 4}">
										<form:input path="usuario" tabindex="3" size="8" maxlength="8" cssClass="dato" id="usuario" onchange="this.value=this.value.toUpperCase();"/>
									</c:if>
									<c:if test="${perfil eq 4}">
										<form:input path="usuario" readonly="true" tabindex="3" size="8" maxlength="8" cssClass="dato" id="usuario" onchange="this.value=this.value.toUpperCase();"/>
									</c:if>
									<label class="campoObligatorio" id="campoObligatorio_usuario" title="Campo obligatorio"> *</label>
								</td>
							</tr>
							<tr align="left">
								<td class="literal">Plan</td>
								<td>
									<form:input path="plan" tabindex="4" id="plan" size="4" maxlength="4" cssClass="dato" />
									<label class="campoObligatorio" id="campoObligatorio_plan" title="Campo obligatorio"> *</label>
								</td>
								<td class="literal">Línea</td>
								<td colspan="3">
									<form:input path="linea" tabindex="5" size="3" maxlength="3" cssClass="dato" id="linea" onchange="javascript:lupas.limpiarCampos('desc_linea', 'modulo');" />
									<input path="" class="dato" id="desc_linea" size="40" readonly="true" />
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
								</td>
								<td class="literal">Clase</td>
								<td >
									<form:input path="clase" tabindex="6" id="clase" size="4" maxlength="3" cssClass="dato" />
									<label class="campoObligatorio" id="campoObligatorio_clase" title="Campo obligatorio"> *</label>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Clase','principio', '', '');" alt="Buscar Clase" title="Buscar Clase" />							
								</td>
							</tr>
							<tr align="left">
								<td class="literal">Póliza</td>
								<td>
									<form:input path="refpoliza" tabindex="7" id="refpoliza" size="7" maxlength="7" cssClass="dato" />
									<label class="campoObligatorio" id="campoObligatorio_refpoliza" title="Campo obligatorio"> *</label>	
								</td>
								<td class="literal">Colectivo</td>
								<td>
									<form:input path="refcolectivo" tabindex="8" id="refcolectivo" size="7" maxlength="7" cssClass="dato" />
									<label class="campoObligatorio" id="campoObligatorio_refcolectivo" title="Campo obligatorio"> *</label>
								</td>
								<td class="literal">Estado Poliza</td>
								<td>
									<form:select path="estadopol" tabindex="9" cssClass="dato" cssStyle="width:200px" id="estadopol">
										<form:option value="">Todos</form:option>
										<c:forEach items="${estadoPoliza}" var="estado">
											<form:option value="${estado.idestado}">${estado.abreviatura} - ${estado.descEstado}</form:option>
										</c:forEach>
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_estadopol" title="Campo obligatorio"> *</label>
								</td>
								<td class="literal">Módulo</td>
								<td>
									<form:input path="modulo" tabindex="10" id="modulo" size="3" maxlength="5" cssClass="dato" />
									<label class="campoObligatorio" id="campoObligatorio_modulo" title="Campo obligatorio"> *</label>
								</td>
							</tr>
							<tr align="left">
								<td class="literal">NIF/CIF<br/>Asegurado</td>
								<td>		
									<form:input path="nifcif" tabindex="11" id="nifcif" size="9" maxlength="9" cssClass="dato" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
									<label class="campoObligatorio" id="campoObligatorio_nifcif" title="Campo obligatorio"> *</label>
								</td>
								<td class="literal">F. Envio RC</td>
								<td>
									<spring:bind path="fecenviorc">
							    		<input type="text" tabindex="12" id="fecenviorcId" name="fecenviorcId" class="dato" size="11" maxlength="10"
							    		onchange="if (!ComprobarFecha(this, document.main3, 'Fecha Envio RC')) this.value='';" 
							    		value="<fmt:formatDate pattern="dd/MM/yyyy" value="${vistaPolizasRC.fecenviorc}" />"/>
							    	</spring:bind>
						    		<a id="btn_fecenviorc" name="btn_fecenviorc"><img src="jsp/img/calendar.gif"/></a>	
								</td>
								<td class="literal">Estado RC</td>
								<td>
									<form:select path="estadorc" tabindex="13" cssClass="dato" cssStyle="width:220px" id="estadorc">
										<form:option value="">Todos</form:option>
										<c:forEach items="${estadosRC}" var="item">
											<form:option value="${item.id}">${item.abreviatura} - ${item.descripcion}</form:option>
										</c:forEach>
									</form:select>
									<label class="campoObligatorio"	id="campoObligatorio_estadorc" title="Campo obligatorio"> *</label>
								</td>
							</tr>
							<tr align="left">
								<td class="literal">Detalle</td>
								<td colspan="3">
									<form:select path="errorrc"  tabindex="14"cssClass="dato" cssStyle="width:350px" id="errorrc">	
										<form:option value="">Todos</form:option>
										<c:forEach items="${erroresRC}" var="item">
											<form:option value="${item.id}">${item.descripcion}</form:option>
										</c:forEach>
									</form:select>
									<label class="campoObligatorio"	id="campoObligatorio_errorrc" title="Campo obligatorio"> *</label>
								</td>
								<td class="literal">Ref. OMEGA</td>
								<td>
									<form:input path="refomega" tabindex="15" id="refomega" size="9" maxlength="9" cssClass="dato" />
									<label class="campoObligatorio" id="campoObligatorio_refomega" title="Campo obligatorio"> *</label>
								</td>
								  
								<td class="literal">Nº Solicitud</td>
								<td>
									<form:input path="nsolicitud" tabindex="16" id="nsolicitud" size="15" maxlength="15" cssClass="dato" />
									<label class="campoObligatorio" id="campoObligatorio_nsolicitud" title="Campo obligatorio"> *</label>
								</td>								
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			<!-- Grid Jmesa -->
			<div id="grid">
				${listaPolizasRC}
			</div>
		</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaClase.jsp"%>
	</body>
</html>