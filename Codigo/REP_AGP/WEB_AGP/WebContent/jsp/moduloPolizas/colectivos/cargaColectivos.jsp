<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="agp" >
	<c:set var="numEle"><fmt:message key="visores.numElements"/></c:set>
</fmt:bundle>

<html>
	<head>
		<title>Carga de Colectivos</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloPolizas/colectivos/cargaColectivos.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
				
		<script type="text/javascript">
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');generales.fijarFila()">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" href="javascript:consultar();">Consultar</a>
						<a class="bot" href="javascript:limpiar();">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la pagina -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Carga de Colectivos</p>
			<form:form name="main" id="main" action="cargaColectivo.html" method="post" commandName="colectivoBean">
				<input type="hidden" name="operacion" />
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
				<input type="hidden" id="alta" value=""> 
				<input type="hidden" name="fechaIni.day" value=""> 
				<input type="hidden" name="fechaIni.month" value=""> 
				<input type="hidden" name="fechaIni.year" value=""> 
				<input type="hidden" name="fechaFin.day" value=""> 
				<input type="hidden" name="fechaFin.month" value=""> 
				<input type="hidden" name="fechaFin.year" value="">
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
				<input type="hidden" id="externo" value="${externo}" />
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
				
				<form:hidden path="pctdescuentocol" id="pctdescuentocol" />
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt">
					<table width="95%">		
						<tr>
							<td class="literal"  style="width:120px">Entidad</td>
							<td>
								<c:if test="${perfil == 0 || perfil == 5}">
									<form:input path="tomador.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entidad" onchange="javascript:lupas.limpiarCampos('desc_entidad');" tabindex="1"/>
									<form:input path="tomador.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="40" readonly="true"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
								</c:if> 
								<c:if test="${perfil > 0 && perfil < 5}">
									<form:input path="tomador.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entidad" readonly="true" tabindex="1"/>
									<form:input path="tomador.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="40" readonly="true"/>
								</c:if> 
								<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">Plan</td>
							<td>
								<form:input path="linea.codplan" size="4"maxlength="4" cssClass="dato" id="plan" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" tabindex="2"/> 
								<label class="campoObligatorio" id="campoObligatorio_plan"	title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">Línea</td>
							<td>
								<form:input path="linea.codlinea" size="3"	maxlength="3" cssClass="dato" id="linea" onchange="javascript:lupas.limpiarCampos('desc_linea');" tabindex="3"/>
								<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
								<label class="campoObligatorio"	id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
							</td>
						</tr>
						<tr>
							<td class="literal">CIF Tomador</td>
							<td colspan="5">
								<form:input	path="tomador.id.ciftomador" size="9" maxlength="9" cssClass="dato"	id="tomador" onchange="javascript:this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);lupas.limpiarCampos('desc_tomador');" tabindex="4"/> 
								<form:input path="tomador.razonsocial" cssClass="dato"	id="desc_tomador" size="40" readonly="true"/>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Tomador','principio', '', '');" alt="Buscar Tomador" title="Buscar Tomador" />
								<label class="campoObligatorio"	id="campoObligatorio_tomador" title="Campo obligatorio"> *</label>
							</td>
						</tr>
					</table>
					<table>
						<tr>
							<td class="literal"  style="width:120px">Colectivo</td>
							<td>
								<form:input path="idcolectivo" size="10"maxlength="7" cssClass="dato" id="colectivo" tabindex="5"/> 
								<form:input	path="dc" size="2" maxlength="1" cssClass="dato" id="dc" tabindex="6"/> 
								<label	class="campoObligatorio" id="campoObligatorio_colectivo" title="Campo obligatorio"> *</label> 
								<label class="campoObligatorio"	id="campoObligatorio_dc" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">Nombre colectivo</td>
							<td colspan="3">
								<form:input path="nomcolectivo"	size="50" maxlength="50" cssClass="dato" id="nomcolectivo" onchange="this.value=this.value.toUpperCase();" tabindex="7"/> 
								<label	class="campoObligatorio" id="campoObligatorio_nomcolectivo"	title="Campo obligatorio"> *</label>
							</td>
						</tr>
						<tr>
						<c:if test="${externo == 0 and perfil !=4}"> <!--  es interno y perfil distinto de 4-->
							<td class="literal" style="width:120px">Entidad mediadora</td>
							<td>
								<form:input	path="subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora', 'desc_subentmediadora');UTIL.subStrEntidad();" tabindex="8"/>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('EntidadMediadora','principio', '', '');"	alt="Buscar Entidad Mediadora" title="Buscar Entidad Mediadora" />
								<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio">*</label>
							</td>
							<td class="literal"> Subentidad mediadora</td>
							<td >
								<form:input	path="subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" onchange="javascript:lupas.limpiarCampos('desc_subentmediadora');" tabindex="9"/>
								<form:input path="subentidadMediadora.nomsubentidad" cssClass="dato"	id="desc_subentmediadora" size="40" readonly="true"/>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadora','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
								<label class="campoObligatorio" id="campoObligatorio_subentmediadora" title="Campo obligatorio">*</label>
							</td>
						</c:if>
						<c:if test="${externo == 1 or perfil == 4}"> <!--  es externo o perfil 4-->
							<td class="literal" style="width:120px">Entidad mediadora</td>
							<td>
								<form:input	path="subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" readonly="true" tabindex="8"/>
								<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio">*</label>
							</td>
							<td class="literal"> Subentidad mediadora</td>
							<td >
								<form:input	path="subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" readonly="true" tabindex="9"/>
								<form:input path="subentidadMediadora.nomsubentidad" cssClass="dato"	id="desc_subentmediadora" size="40" readonly="true"/>
								<label class="campoObligatorio" id="campoObligatorio_subentmediadora" title="Campo obligatorio">*</label>
							</td>
						
						</c:if>							
							<td class="literal" style="width:50px">Activo</td>
							<td class="detalI"><p id="activo"></p></td>				
						</tr>
					</table>
					</div>
				<div class="panel2 isrt">
					<fieldset>
						<table width="100%">
							<colgroup>
								<col width="15%"/>
								<col width="15%"/>
								<col width="15%"/>
								<col width="15%"/>
								<col width="12%"/>
								<col width="*"/>
							</colgroup>
							<tr align="left">
								<td rowspan="2" class="literal">Forma de pago</td>
								<td class="literal">% Primer pago</td>
								<td class="literal">
									<form:input path="pctprimerpago" size="5" maxlength="5" cssClass="dato" id="pctprimerpago" readonly="true" tabindex="11"/>
								</td>
								<td class="literal">Fecha primer pago</td>
								<td class="literal">
									<spring:bind path="fechaprimerpago">
										<!-- <c:set var="fechaFormateada">
											<fmt:formatDate pattern="dd-MM-yyyy" value="${colectivoBean.fechaprimerpago }" />
										</c:set>
										<spring:transform var="fechaprimerpago"	value="${fechaFormateada }" />-->
										<input type="text" name="fechaprimerpago" tabindex="12"
										id="fechaIni" size="11" maxlength="10" class="dato"	value="${fechaprimerpago }" readonly="true"/>
									</spring:bind>
								</td>								
							</tr>
							
							<tr align="left">
								<td class="literal">% Segundo pago</td>
								<td class="literal">
									<form:input path="pctsegundopago" size="5" maxlength="5" cssClass="dato" id="pctsegundopago" readonly="true" tabindex="13"/>
								</td>
								<td class="literal">Fecha segundo pago</td>
								<td class="literal">
									<spring:bind path="fechasegundopago">
										<!-- <c:set var="fechaFormateada">
											<fmt:formatDate pattern="dd-MM-yyyy" value="${colectivoBean.fechasegundopago }" />
										</c:set>
										<spring:transform var="fechasegundopago"	value="${fechaFormateada }" />-->
										<input type="text" name="fechasegundopago" tabindex="14"
										id="fechaFin" size="11" maxlength="10" class="dato"	value="${fechasegundopago }" readonly="true"/>
									</spring:bind>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			<br />
			<!-- Aqui tiene que ir el grid de datos -->
			<div id="grid">
				<display:table requestURI="cargaColectivo.html" class="LISTA" summary="colectivo" pagesize="${num}" partialList="true" 
							   name="${listaColectivos}" id="colectivo" size="${totalListSize}"
							   decorator="com.rsi.agp.core.decorators.ModelTableDecoratorColectivos" style="width:95%">
					
					<display:setProperty name="pagination.sort.param" value="sort"/>
					<display:setProperty name="pagination.sortdirection.param" value="dir"/>	
					
					<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="cargaColectivoSelec" sortable="false" style="width:60px;text-align:center"/>
					<display:column class="literal" headerClass="cblistaImg" title="Ent." property="tomador.id.codentidad" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Plan" property="linea.codplan" sortProperty="lin.codplan" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="L&iacute;nea" property="linea.codlinea" sortProperty="lin.codlinea" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Ref. Col." property="colId" sortProperty="idcolectivo" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Nombre Colectivo" property="nomcolectivo" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="E-S Mediadora" property="esMediadora" sortProperty="SM.entidadMediadora" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="Fecha Baja" property="colFechaBaja" sortProperty="fechabaja" sortable="true" />
					
				</display:table>
			</div>	
			<form name="consulta" id="consulta" action="cargaColectivo.html" method="post">
				<input type="hidden" name="operacion" value="editar" />
				<input type="hidden" name="id" id="consulta_id" />
			</form>
			<form name="cargar" id="cargar" action="cargaColectivo.html" method="post">
				<input type="hidden" name="operacion" value="cargar" />
				<input type="hidden" name="id" id="cargar_id" />
			</form>
		</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaTomador.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaEntidadMediadora.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSubentidadMediadora.jsp"%>

	</body>
</html>