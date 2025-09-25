<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>

<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>


<html>
	<head>
		<title>Mantenimiento de familias</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<script type="text/javascript" src="jsp/js/util.js"></script>
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
    <script type="text/javascript" src="jsp/js/calendar.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	<script type="text/javascript" src="jsp/moduloTaller/mtoFamilias/mtoFamilias.js" ></script>
	<%@ include file="/jsp/js/draggable.jsp"%>
		

	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		
		<!-- botones de la pagina -->
		<div id="buttons" >
			<table width="97%" cellspacing="2" cellpadding="2" border="0">
				<tbody>
					<tr>
					
						<td align="right">
							<c:if test="${showModificar == 'true'}">	
								<a class="bot" id="btnModificar"  href="javascript:editar();">Modificar</a>
							</c:if>	
							<c:if test="${showModificar != 'true'}">	
								<a class="bot" id="btnModificar" style="display:none"  href="javascript:editar();">Modificar</a>
							</c:if>
							<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							<a class="bot" href="javascript:limpiarFiltro();">Limpiar</a>							
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		

		<!-- Contenido de la pagina -->
		<div class="conten" style="padding:3px;width:97%" >
		<p class="titulopag" align="left">FAMILIAS</p>
			
			<form:form name="main" id="mainForm" action="mtoFamilias.run" method="post" commandName="familiaBean">
				
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="origenLlamada" id="origenLlamada" />	
				<input value="${familiaBean.id.codFamilia}" type="hidden" name="codFamiliaInicial" id="codFamiliaInicial"/>
				<input value="${familiaBean.id.codGrupoFamilia}" type="hidden" name="grupoInicial" id="grupoInicial"/>
				<input value="${familiaBean.id.grupoNegocio}" type="hidden" name="grupoNegocioInicial" id="grupoNegocioInicial"/>
				<input value="${familiaBean.id.codLinea}" type="hidden" name="lineaInicial" id="lineaInicial"/>

				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div id="panelInfo_adicional" style="width:800px;height:20px;color:black;border:1px solid #FFCD00;display:none;
					font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FCF6CF">
				</div>
				
				<div class="panel2 isrt" style="width: 75%;margin:0 auto;">
					<fieldset>
					<legend class="literal">Filtro</legend>
						<table>
							<tr align="left">
								<td class="literal" style="" >Familia</td>
								<td class="literal" rowSpan="1"  nowrap>
									<form:input path="id.codFamilia" id="codFamilia" size="4" maxlength="3" cssClass="dato" tabindex="1" onchange="this.value=this.value.toUpperCase();lupas.limpiarCampos('nomFamilia')"/>
									<label class="campoObligatorio" id="campoObligatorio_codFamilia" title="Campo obligatorio"> *</label>
									
									<form:input id="nomFamilia" path="familia.nomFamilia"  cssClass="dato" tabindex="-1" size="55" maxlength="255" onchange="this.value=quitaAcentos(this.value);this.value=this.value.toUpperCase();"/>
									<label class="campoObligatorio" id="campoObligatorio_nomFamilia" title="Campo obligatorio"> *</label>
									
									<img  border="1" id="iconoLupa" src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Familia','principio', '', '');" alt="Buscar Familia"/>
								</td>
								<td class="literal" align="left">Grupo</td>
								<td class="literal">
									<form:select path="id.codGrupoFamilia" tabindex="2" cssClass="dato"	cssStyle="width:170px" id="grupo">
											<form:option value="">Todos</form:option>
											<c:forEach items="${grupos}" var="grupo">
												<form:option value="${grupo.codGrupo}">${grupo.nomGrupo}</form:option>
											</c:forEach>	
											
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_grupo" title="Campo obligatorio"> *</label>
								</td>
							</tr>
							<tr style="height:25px"></tr>
							<tr align="left">
							
								<td class="literal">Línea</td>
								<td>
									<form:input path="id.codLinea" size="6"	maxlength="3" cssClass="dato" id="lineaCondicionado" onchange="javascript:lupas.limpiarCampos('nomLineaCondicionado');" tabindex="3"/>
									 <form:input path="linea.deslinea" cssClass="dato" id="nomLineaCondicionado" size="50"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaCondicionado','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
									<label class="campoObligatorio"	id="campoObligatorio_lineaCondicionado" title="Campo obligatorio"> *</label>
								</td>
							
							
								<td class="literal">Grupo Negocio</td>
								<td class="literal">
									<form:select path="id.grupoNegocio" tabindex="2" cssClass="dato"	cssStyle="width:140px" id="grupoNegocio">
										<form:option value="">Todos</form:option>
										<c:forEach items="${gruposNegocio}" var="grupoNegocio">
											<form:option value="${grupoNegocio.grupoNegocio}">${grupoNegocio.descripcion}</form:option>
										</c:forEach>	
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_grupoNegocio" title="Campo obligatorio"> *</label>
								</td>
							</tr>
							
						</table>
					</fieldset>
				</div>
			</form:form>
			
			<form:form name="frmBorrar" id="frmBorrar" action="mtoFamilias.run" method="post" commandName="familiaBean">
				<!-- TODO plan, linea, grupoNegocio, codFamilia -->
				<input type="hidden" name="method" value="doBorrar" />
				<form:hidden path="id.codFamilia" id="codFamiliaBorrar"/>
				<form:hidden path="id.codLinea" id="codLineaBorrar"/>
				<form:hidden path="id.codGrupoFamilia" id="codGrupoFamiliaBorrar"/>
				<form:hidden path="id.grupoNegocio" id="grupoNegocioBorrar"/>
			</form:form> 
			
			
						
		<div id="grid">
		
			<display:table requestURI=""  id="listaFamilias" class="LISTA" summary="familia" 
							sort="list" pagesize="${numReg}" 
							name="${listFamilias}" 
							decorator="com.rsi.agp.core.decorators.ModelTableDecoratorMtoFamilias" 
							excludedParams="method" style="width:95%; margin:0 auto;">
							
				<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="acciones" sortable="false" style="width:40px"/>
				<display:column class="literal" headerClass="cblistaImg" title="Cod. Familia" property="codFamilia" sortable="true" style="width:90px;text-align:center"/>
				<display:column class="literal" headerClass="cblistaImg" title="Descripción Familia" property="nomFamilia" sortable="true" style="" />
				<display:column class="literal" headerClass="cblistaImg" title="Linea" property="codLinea" sortable="true" style="width:70px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Grupo Neg." property="grupoNegocio" sortable="true" style="width:90px;text-align:center"/>
				<display:column class="literal" headerClass="cblistaImg" title="Grupo" property="grupo" sortable="true" style="width:180px;text-align:center"/>
				
			</display:table>
			
		</div>
			
			
	</div>

	


	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<%@ include file="/jsp/common/lupas/lupaFamilia.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineasCondicionado.jsp"%>




</body>
</html>