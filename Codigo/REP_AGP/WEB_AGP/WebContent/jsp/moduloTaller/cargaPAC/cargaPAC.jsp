<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head> 
		<title>Agroplus - Carga de la PAC</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<script type="text/javascript" src="jsp/js/util.js"></script>
    	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
    	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
		<script type="text/javascript" src="jsp/moduloTaller/cargaPAC/cargaPAC.js"></script>
		
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub2');">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
		<div id="buttons" style="margin: 5px">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="left">
						<a class="bot" id="botonBorradoMasivo" href="javascript:borradoMasivo();">Borrado masivo</a>
					</td>
					<td align="right">						
						<a class="bot" id="botonConsultar" href="javascript:consultar();">Consultar</a>
						<a class="bot" id="botonVer" href="javascript:limpiar();">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		
		<div class="conten" style="padding:3px;width:100%">
		<p class="titulopag" align="left">Carga de ficheros de la PAC</p>
		
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		<!-- Panel de carga de ficheros -->
		<form:form name="formCarga" id="formCarga" action="cargaPAC.html" method="post" enctype="multipart/form-data" commandName="formPacCargasBean">
			<input type="hidden" id="method" name="method" value="doCargar"/>
			<div class="panel2 isrt" style="width: 97%;margin:0 auto;" align="center">
				<fieldset>
				  <legend class="literal">Carga TXT</legend>
				  
				  	<table width="95%" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="literal" width="13%" align="right">Plan:&nbsp;&nbsp;</td>
							<td width="7%">
								<form:input	path="plan" size="4" maxlength="4" cssClass="dato" id="planCarga" tabindex="1"/>
								<label class="campoObligatorio" id="campoObligatorio_planCarga" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal" width="7%">E-S Med:</td>
							<td width="15%">
								<input type="hidden" id="entidad_cm" name="entidad_cm" value="${entidadCarga}"/>
								<input type="hidden" id="campoRestriccion_EntidadIN" value="entidad.codentidad" />
								<input type="hidden" id="valorRestriccion_EntidadIN" value="${grupoEntidades}"/>
								<input type="hidden" id="operadorRestriccion_EntidadIN" value="in" />
								<c:if test="${filtroMediador eq false}">
									<form:input	path="entMed" size="4" maxlength="4" cssClass="dato" id="entmediadora_cm" onchange="javascript:lupas.limpiarCampos('subentmediadora_cm');UTIL.subStrEntidad();" tabindex="2"/>
									<label class="campoObligatorio" id="campoObligatorio_entmediadora_cm" title="Campo obligatorio"> *</label>
									<form:input	path="subentMed" size="4" maxlength="4" cssClass="dato" id="subentmediadora_cm" tabindex="3"/>
									<label class="campoObligatorio" id="campoObligatorio_subentmediadora_cm" title="Campo obligatorio"> *</label>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraEntidadIN','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
								</c:if>
								
								<c:if test="${filtroMediador eq true}">
									<form:input readonly="true" path="entMed" size="4" maxlength="4" cssClass="dato" id="entmediadora_cm" onchange="javascript:lupas.limpiarCampos('subentmediadora_cm');UTIL.subStrEntidad();" tabindex="2"/>
									<label class="campoObligatorio" id="campoObligatorio_entmediadora_cm" title="Campo obligatorio"> *</label>
									<form:input	readonly="true" path="subentMed" size="4" maxlength="4" cssClass="dato" id="subentmediadora_cm" tabindex="3"/>
									<label class="campoObligatorio" id="campoObligatorio_subentmediadora_cm" title="Campo obligatorio"> *</label>
								</c:if>
							</td>  
							<td width="10%" class="literal">Fichero PAC: </td>
							<td width="35%">
								<input type="file" class="dato" id="fileCarga" name="file" tabindex="4"/>
								<label class="campoObligatorio" id="campoObligatorio_file" title="Campo obligatorio"> *</label>
							</td>
							<td width="13%">
								<a class="bot" id="botonCargar" href="javascript:cargar();">Cargar</a>
							</td>
						</tr>
					</table>
				 </fieldset>
				 
				 
			</div>
		</form:form>
		
		<!-- Formulario para limpiar la pantalla -->
		<form:form name="formLimpiar" id="formLimpiar" action="cargaPAC.html" method="post" commandName="pacCargasBean">
			<input type="hidden" id="method" name="method" value="doConsulta"/>
		</form:form>
		
		<!-- Formulario para ver el contenido de un archivo -->
		<form:form name="formVerContenido" id="formVerContenido" action="cargaPAC.html" method="post" commandName="pacCargasBean">
			<input type="hidden" id="method" name="method" value="doVerContenidoArchivo"/>
			<form:hidden path="id" id="idVerContenido"/>
			<form:hidden path="nombreFichero" id="nomFicheroVerContenido"/>
			<input type="hidden" id="origenLlamada" name="origenLlamada" value="consulta"/>
		</form:form>
		
		<!-- Formulario para el borrado masivo o individual -->
		<form:form name="formBorrado" id="formBorrado" action="cargaPAC.html" method="post" commandName="pacCargasBean">
			<input type="hidden" id="method" name="method" value="doEliminar"/>
			<input type="hidden" id="origenLlamada" name="origenLlamada" value="consulta"/>		
		</form:form>
		
		<!-- Almacena los ids de todos los registros que muestra el listado (para la funcionalidad de 'Marcar todos') -->
		<input type="hidden" id="idsRowsTodos" name="idsRowsTodos" value="${idsRowsTodos}"/>
		
		<!-- Panel de búsqueda de cargas -->
		<form:form name="formConsulta" id="formConsulta" action="cargaPAC.html" method="post" commandName="pacCargasBean">
			<input type="hidden" id="methodConsultar" name="method" value="doConsulta"/>
			<input type="hidden" id="origenLlamada" name="origenLlamada" value="consulta"/>
			<input type="hidden" id="idsRowsChecked" name="idsRowsChecked" value="${idsRowsChecked}"/>
			<input type="hidden" name="grupoEntidadesFiltro" id="grupoEntidades" value="${grupoEntidades}"/>
			
			<div class="panel2 isrt" style="width: auto">
				<fieldset>
				  <legend class="literal">Filtro</legend>
				  	<table width="100%" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td class="literal" width="">Entidad:</td>
							<td>
								<c:if test="${filtroEntidad eq false}">
									<form:input path="entidad" size="5" maxlength="4" cssClass="dato" id="entidad" tabindex="5" onchange="javascript:lupas.limpiarCampos('desc_entidad');"/>
								</c:if>
								<c:if test="${filtroEntidad eq true}">
									<form:input path="entidad" size="5" readonly="true" maxlength="4" cssClass="dato" id="entidad" tabindex="6" onchange="javascript:lupas.limpiarCampos('desc_entidad');"/>
								</c:if>
								
								<form:input path="nomentidad" cssClass="dato" id="desc_entidad" size="30" readonly="true"/>
								
								<c:if test="${filtroEntidad eq false}">
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
								</c:if>						
							</td>
							<td class="literal">E-S Med:</td>
							<td>
								<c:if test="${filtroMediador eq false}">
									<form:input	path="entMed" size="4" maxlength="4" cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" tabindex="7"/>
									<form:input	path="subentMed" size="4" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="8"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
								</c:if>
								<c:if test="${filtroMediador eq true}">
									<form:input	path="entMed" size="4" readonly="true" maxlength="4" cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" tabindex="9"/>
									<form:input	path="subentMed" size="4" readonly="true" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="10"/>
								</c:if>
							</td>
							<td class="literal">Plan:</td>
							<td>
								<form:input	path="plan" size="4" maxlength="4" cssClass="dato" id="plan" tabindex="11"/>
							</td>
							<td class="literal">L&iacute;nea:</td>
							<td>
								<form:input path="linea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="12" onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
								<form:input path="nomlinea" cssClass="dato" id="desc_linea" size="35" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />							
							</td>
						</tr>
						<tr>
							<td class="literal">Fichero PAC:</td>
							<td>
								<form:input path="nombreFichero" cssClass="dato" id="nombreFichero" size="41" tabindex="13"/>
							</td>
						</tr>
					</table>
				 </fieldset>
			</div>
		</form:form>
		
		<c:if test="${listCargasPAC != null}">
			<div class="grid" style="">
		        <display:table requestURI="cargaPAC.html" id="listCargasPAC" class="LISTA" summary="CargaPAC" name="${listCargasPAC}" sort="list" pagesize="10" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorCargaPAC" 
			     		        style="width:85%" excludedParams="method,idsRowsChecked" defaultsort="1">
		        		       
		        		       
		            <display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" property="acciones" sortable="false" style="width:8%;text-align:center;"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Entidad" property="entidad" sortable="true" style="width:10%"/>
		            <display:column class="literal" headerClass="cblistaImg" title="E-S Med." property="entSubMediadora" sortable="true" style="width:10%"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Plan" property="plan" sortable="true" style="width:10%"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Línea" property="linea" sortable="true" style="width:10%"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Fichero" property="nombreFichero" sortable="true" style="width:42%"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Fec.Carga" property="fechaCarga" format="{0, date, dd/MM/yyyy}"  sortable="true" style="width:10%"/>
		            
		            <display:footer>
					<tr style="background-color: #e5e5e5">
						<td class="literal" colspan="19" align="left" style="text-align: left">
							<input type="checkbox" id="checkTodo" name="checkTodo" class="dato" onclick="this.checked  ? marcarTodos() : desmarcarTodos() " />Marcar Todos</td>
					</tr>
					</display:footer>
		        </display:table>				
			</div>
		</c:if>
		
		
		
		
		
	</div>
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraEntidadIN.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		
	</body>
</html>