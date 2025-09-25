<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Consulta Agroseguro</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
	
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
		
		<script type="text/javascript" src="jsp/moduloUtilidades/incidenciasAgro/listaIncidenciasAgro.js"></script>
		

	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4')">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" id="btnConsultar" href="#" onclick="consultar()">Consultar</a>
						<a class="bot" id="btnLimpiar" href="listaIncidenciasAgro.run?method=doCargar">Limpiar</a>
						<a class="bot" id="btnVolver" href="#" onclick="volver()">Volver</a>
					</td>
				</tr>
			</table>
		</div>
		
		<div class="conten" style="padding: 3px; width: 97%">
			<!-- MODIF TAM (17.07.2018) - Incidencias RGA V03 -->
			<!-- <p class="titulopag" align="left">Incidencias Agroseguro</p> -->
			<p class="titulopag" align="left">Consulta de Incidencias en Agroseguro</p> 
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<!-- Pet. 50775 ** MODIF TAM (04.05.2018) * Resolución de Incidencias -->
			<div class="panel2 isrt" style="vertical-align:top; width:95%; margin:0 auto;">
				<fieldset style="width:100%;">
					<legend class="literal">Filtro</legend>
					<!--  <form id="formConsulta" style="width: 98%;"  name="formConsulta" action="listaIncidenciasAgro.run" method="post">-->
					<form id="formConsulta" style="height:98%; text-align:center"  name="formConsulta" action="listaIncidenciasAgro.run" method="post"> 
						<input type="hidden" id="method" name="method" value="doConsultar" />
						
						<!-- campso padra no perder el filtro de bússqsueda de la vetnana de Incidencias -->
	   					<input type="hidden" name="idincidencia" id="idincidenciaConsList" value="${vuelta.idincidencia}"/>
						<input type="hidden" name="codentidad" id="entidadConsList" value="${vuelta.codentidad}"/>
						<input type="hidden" name="referencia" id="referenciaConsList" value="${vuelta.referencia}"/>
						<input type="hidden" name="oficina"id="oficinaConsList" value="${vuelta.oficina}"/>
						<input type="hidden" name="entmediadora" id="entmediadoraConsList" value="${vuelta.entmediadora}"/>
						<input type="hidden" name="subentmediadora" id="subentmediadoraConsList" value="${vuelta.subentmediadora}"/>
						<input type="hidden" name="delegacion" id="delegacionConsList" value="${vuelta.delegacion}"/>
						<input type="hidden" name="codplan" id="planConsList" value="${vuelta.codplan}"/>
						<input type="hidden" name="codlinea" id="lineaConsList" value="${vuelta.codlinea}"/>
						<input type="hidden" name="codestado" id="codestadoConsList" value="${vuelta.codestado}"/>
						<input type="hidden" name="codestadoagro" id="codestadoagroConsList" value="${vuelta.codestadoagro}"/>
						<input type="hidden" name="nifcif" id="nifcifConsList" value="${vuelta.nifcif}"/>
						<input type="hidden" name="tiporef" id="tiporefConsList" value="${vuelta.tiporef}"/>
						<input type="hidden" name="idcupon" id="idcuponConsList" value="${vuelta.idcupon}"/>
						<input type="hidden" name="asunto" id="asuntoConsList" value="${vuelta.asunto}"/>
						<input type="hidden" name="fechaEnvioDesdeId" id="fechaEnvioDesdeIdConsList" value="${fechaEnvioDesdeStr}"/>
	   					<input type="hidden" name="fechaEnvioHastaId" id="fechaEnvioHastaIdConsList" value="${fechaEnvioHastaStr}"/>				    		
	   									    		
						<!-- POLIZA -->
							<fieldset style="width:30%; text-align:left; float:left">
								<legend>
									<c:choose>
										<c:when test="${not empty planPoliza}">
												<input type="radio" class="literal" value="p" id="poliza"  name="opcionBusqueda" checked="checked" /> 
												<span class="literal">Póliza</span>
											</c:when>
											<c:otherwise>
												<input type="radio" class="literal" value="p" id="poliza" name="opcionBusqueda"/>
												<span class="literal">Póliza</span>
											</c:otherwise>
										</c:choose>
									</legend>
								
									<label class="literal" sfor="planPoliza">Plan</label>
										<input type="text" class="dato poliza" style="width:15%" maxlength="4" id="poliza_plan" name="poliza_plan" value="${planPoliza}"/>
									<label class="campoObligatorio"	id="campoObligatorio_poliza_plan" title="Campo obligatorio"> *</label>
									<label class="literal" for="referencia">Referencia</label>
										<input type="text" class="dato poliza"  size="10" id="poliza_referencia" maxlength="7" name="poliza_referencia" value="${referencia}" onchange="this.value=this.value.toUpperCase();"/>
									<label class="campoObligatorio"	id="campoObligatorio_poliza_referencia" title="Campo obligatorio"> *</label>	
									<br/>
								</fieldset>	
						<!-- ASEGURADO -->								
								<fieldset style="width:65%; text-align:left; float:right">	
									<legend>
										<c:choose>
											<c:when test="${not empty planAsegurado}">
												<input type="radio" class="literal" value="a" id="asegurado" name="opcionBusqueda" checked="checked"/>
												<span class="literal">Asegurado</span>
											</c:when>
											<c:otherwise>
												<input type="radio" class="literal" value="a" id="asegurado" name="opcionBusqueda"/>
												<span class="literal">Asegurado</span>
											</c:otherwise>
										</c:choose>
									</legend>
									<label class="literal" for="planAsegurado">Plan</label>
										<input type="text" class="dato asegurado"  style="width:65px" maxlength="4" id="plan" name="plan" value="${planAsegurado}"/>
									<label class="campoObligatorio"	id="campoObligatorio_plan" title="Campo obligatorio"> *</label>
									<label for="linea" class="literal">L&iacute;nea</label>
										<input type="text" class="dato asegurado" style="width:55px" maxlength="3" id="linea" name="linea" onchange="javascript:lupas.limpiarCampos('desc_linea', '');" value="${linea}"/>
										<input class="dato"	id="desc_linea" size="30" name="nomlinea" value="${nomLinea}" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />							
									<label class="campoObligatorio"	id="campoObligatorio_linea" title="Campo obligatorio"> *</label> 
										<input type="hidden" class="dato" id="desc_linea" name="desc_linea" >
									<label class="literal" for="nifcif">CIF/NIF Aseg.</label>
										<input type="text"  size="10" class="dato asegurado" maxlength="9" id="asegurado_nifcif" name="asegurado_nifcif" value="${nifcif}" onchange="this.value=this.value.toUpperCase();"/>
									<label class="campoObligatorio"	id="campoObligatorio_asegurado_nifcif" title="Campo obligatorio"> *</label>
								</fieldset>	
					</form>
				</fieldset>
			</div>
			<div id="grid">
				<c:if test="${empty cargaPagina}">
			        <display:table requestURI="listaIncidenciasAgro.run?method=doConsultar" id="listaIncidencias" class="LISTA" summary="Incidencias" 
			        		name="${listaIncidencias}" sort="list" pagesize="10" excludedParams="method" defaultsort="0"
			        		decorator="com.rsi.agp.core.decorators.ModelTableDecoratorImpresionIncidencias" style="width:100%;border-collapse:collapse;">
			        	<%--Pet. 50775 ** MODIF TAM (14.05.2018) ** Resolución Incidencia * Se aumenta el tamaña de la columna de acciones--%>	
			           	<%--<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="acciones" sortable="false" style="width:40px;text-align:center"/> --%>
			           	<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="acciones" sortable="false" style="width:80px;text-align:left"/>
						<display:column class="literal" headerClass="cblistaImg" title="Año" property="anio" style="width:40px;"/>
						<display:column class="literal" headerClass="cblistaImg" title="Número" property="numero" style="width:80px;"/>
						<display:column class="literal" headerClass="cblistaImg" title="Asunto" property="asunto" style="width:230px;"/>
						<display:column class="literal" headerClass="cblistaImg" title="Estado" property="estado" style="width:170px;"/>
						<display:column class="literal" headerClass="cblistaImg" title="Fecha" property="fecha" style="width:80px;"/>
						<display:column class="literal" headerClass="cblistaImg" title="Documento afectado" property="documento" style="width:150px;"/>
						<display:column class="literal" headerClass="cblistaImg" title="Referencia" property="referencia" style="width:80px;"/>
						<display:column class="literal" headerClass="cblistaImg" title="Tipo Poliza" property="tipoPoliza" style="width:80px;"/>
						<display:column class="literal" headerClass="cblistaImg" title="Id. Envio" property="idEnvio" style="width:140px;"/>
						<%-- MODIF TAM (08.06.2018) ** Se vuelve a poner la columna del TipoPoliza y se quita la de Nº Documentos--%>
						<%--<display:column class="literal" headerClass="cblistaImg" title="N. Documentos" property="numDocumentos" style="width:120px;"/> --%>
					</display:table>

				</c:if>	
			</div>
		</div>
		

		
		<form name="formDistCoste" id="formDistCoste" action="calculoModificacion.html" method="post" >
			<input type="hidden" id="method" name="method" value="doConsultaDistCosteDesdeIncidencias" />
			<input type="hidden" id="modoLectura" name="modoLectura" value="modoLectura" />
			<input type="hidden" id="idCuponVerDC" name="idCuponVerDC"/>
			<input type="hidden" id="planDC" name="plan" />
			<input type="hidden" id="referenciaDC" name="referenciaDC" />
			<input type="hidden" id="lineaDC" name="linea" />
			<input type="hidden" id="nifcifDC" name="nifcif" />
			<input type="hidden" id="tipoBusquedaDC" name="tipoBusqueda" />
			<!-- Pet. 50775 ** MODIF TAM (10.05.2018) ** Inicio -->
			<c:if test="${not empty planPoliza}">
				<input type="hidden" id="poliza_plan" name="poliza_plan" value="${planPoliza}"/>
				<input type="hidden" id="poliza_referencia" name="poliza_referencia" value="${referencia}"/>
			</c:if>
			<c:if test="${not empty planAsegurado}">
				<input type="hidden" id="plan" name="plan" value="${planAsegurado}"/>
			</c:if>
			<c:if test="${not empty planPoliza}">
				<input type="hidden" id="opcionBusqueda" name="opcionBusqueda" value="p"/>
			</c:if>
			<c:if test="${not empty planAsegurado}">
				<input type="hidden" id="opcionBusqueda" name="opcionBusqueda" value="a"/>
			</c:if>
		</form>
	
		<form name="formImpresion" id="formImpresion" action="impresionIncidenciasMod.html" method="post" >
			<input type="hidden" id="method" name="method" value="doImprimirPdf"/>
			<input type="hidden" id="idCuponImpresion" name="idCuponImpresion"/>
			<input type="hidden" id="anio" name="anio"/>
			<input type="hidden" id="numero" name="numero"/>
		</form>
		
		<form name="formDocumentacion" id="formDocumentacion" action="listaIncidenciasAgro.run" method="post">
			<input type="hidden" id="method" name="method" value="doAportarDocumentacion"/>
			<input type="hidden" id="origen" name="origen" value="incidenciasAgro"/>
			<input type="hidden" id="anioDoc" name="anioDoc"/>
			<input type="hidden" id="asuntoDoc" name="asuntoDoc"/>
			<input type="hidden" id="codAsuntoDoc" name="codAsuntoDoc"/>
			<input type="hidden" id="codDocAfec" name="codDocAfec"/>
			<input type="hidden" id="estadoDoc" name="estadoDoc"/>
			<input type="hidden" id="idEnvioDoc" name="idEnvioDoc"/>
			<input type="hidden" id="referenciaDoc" name="referenciaDoc"/>
			<input type="hidden" id="tipoPolizaDoc" name="tipoPolizaDoc"/>
			<input type="hidden" id="fechaEstadoDoc" name="fechaEstadoDoc"/>
			<input type="hidden" id="numDoc" name="numDoc"/>
			<input type="hidden" id="numIncidenciaDoc" name="numIncidenciaDoc"/>
			<input type="hidden" id="tipoBusquedaDocVuelta" name="tipoBusqueda" />
			<c:if test="${not empty planPoliza}">
				<input type="hidden" id="planDocVuelta" name="plan" value="${planPoliza}"/>
				<input type="hidden" id="planDoc" name="planDoc" value="${planPoliza}"/>
			</c:if>
			<c:if test="${not empty planAsegurado}">
				<input type="hidden" id="planDocVuelta" name="plan" value="${planAsegurado}"/>
				<input type="hidden" id="planDoc" name="planDoc" value="${planAsegurado}"/>
			</c:if>
			
			<input type="hidden" id="referenciaDocVuelta" name="referencia_pol" value="${referencia}"/>
			<input type="hidden" id="lineaDocVuelta" name="linea" value="${linea}"/>
			<input type="hidden" id="nifcifDocVuelta" name="nifcif" value="${nifcif}"/>
		</form>
		
		<form id="formVolver" name="formVolver" action="aportarDocIncidencia.run" method="post">
			<input type="hidden" name="method" id="method" value="doVolver" />
			<input type="hidden" name="origenLlamada" value="" />
			<input type="hidden" name="origen" id="origen" value="VolvConsLisInc" />
			<input type="hidden" name="idincidencia" id="idincidenciaConsList" value="${vuelta.idincidencia}"/>
			<input type="hidden" name="codentidad" id="entidadConsList" value="${vuelta.codentidad}"/>
			<input type="hidden" name="referencia" id="referenciaConsList" value="${vuelta.referencia}"/>
			<input type="hidden" name="oficina"id="oficinaConsList" value="${vuelta.oficina}"/>
			<input type="hidden" name="entmediadora" id="entmediadoraConsList" value="${vuelta.entmediadora}"/>
			<input type="hidden" name="subentmediadora" id="subentmediadoraConsList" value="${vuelta.subentmediadora}"/>
			<input type="hidden" name="delegacion" id="delegacionConsList" value="${vuelta.delegacion}"/>
			<input type="hidden" name="codplan" id="planConsList" value="${vuelta.codplan}"/>
			<input type="hidden" name="codlinea" id="lineaConsList" value="${vuelta.codlinea}"/>
			<input type="hidden" name="codestado" id="codestadoConsList" value="${vuelta.codestado}"/>
			<input type="hidden" name="codestadoagro" id="codestadoagroConsList" value="${vuelta.codestadoagro}"/>
			<input type="hidden" name="nifcif" id="nifcifConsList" value="${vuelta.nifcif}"/>
			<input type="hidden" name="tiporef" id="tiporefConsList" value="${vuelta.tiporef}"/>
			<input type="hidden" name="idcupon" id="idcuponConsList" value="${vuelta.idcupon}"/>
			<input type="hidden" name="numero" id="numIncidenciaConsulta" value="${vuelta.numero}" />
			<input type="hidden" name="asunto" id="asuntoConsList" value="${vuelta.asunto}"/>
			<input type="hidden" name="fechaEnvioDesdeId" id="fechaEnvioDesdeIdConsList" value="${fechaEnvioDesdeStr}"/>
	   		<input type="hidden" name="fechaEnvioHastaId" id="fechaEnvioHastaIdConsList" value="${fechaEnvioHastaStr}"/>				    		
		</form>

		

		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	</body>
</html>