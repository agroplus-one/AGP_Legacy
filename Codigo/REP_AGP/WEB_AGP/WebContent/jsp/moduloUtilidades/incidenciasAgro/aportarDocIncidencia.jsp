<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Aportar Documentación Incidencia</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<!-- Estilos -->
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
		<script type="text/javascript" src="jsp/js/lineas.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		
		<%@ include file="/jsp/moduloUtilidades/incidenciasAgro/aportarDocIncidenciaJavascipt.jsp"%>
		
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4')">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td width="5">&nbsp;</td>
					<td align="right">
						<c:if test="${not consulta}">
							<a class="bot" id="btnEnviar" href="#" onclick="enviar()">Enviar</a>
						</c:if>
						<c:choose>
							<c:when test="${consulta}">
								<a class="bot" id="btnVolver" href="#" onclick="volver()">Volver</a>
							</c:when>
							<c:otherwise>
								<c:if test="${origenEnvio eq 'agroseguro'}">
									<c:if test="${origen eq 'incidenciasAgro'}">
										<a class="bot" id="btnVolver" href="#" onclick="volverIncAgr()">Cancelar</a>
										<!-- <a class="bot" id="btnCancelar" href="listaIncidenciasAgro.run?method=doConsultar&tipoBusqueda=${doc.iddocumento}&idInc=${doc.incidencias.idincidencia}">Cancelar</a> -->
									</c:if>
									<c:if test="${origen eq 'impresionIncidencias'}">
										<a class="bot" id="btnVolver" href="#" onclick="volverImprimirInc()">Cancelar</a>
									</c:if>	
									<c:if test="${(origen ne 'incidenciasAgro' && origen ne'impresionIncidencias')}">
										<a class="bot" id="btnCancelar" href="utilidadesIncidencias.run?method=doConsulta&origenLlamada=menuGeneral">Cancelar</a>
									</c:if>	
								</c:if>
								<c:if test="${origenEnvio eq 'baseDatos'}">
									<c:if test="${origen ne 'altaInc'}">
										<a class="bot" id="btnVolver" href="#" onclick="volver()">Cancelar</a>
									</c:if>	
									<c:if test="${origen eq 'altaInc'}">
										<a class="bot" id="btnCancelar" href="aportarDocIncidencia.run?method=doCancelar&idInc=${idInc}">Cancelar</a>
									</c:if>
								</c:if>	
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</table>
		</div>
		<div class="conten" style="WIDTH: 101%; PADDING-BOTTOM: 3px; PADDING-TOP: 3px; PADDING-LEFT: 3px; PADDING-RIGHT: 3px;">
			<p class="titulopag" align="left">Documentación Incidencia</p>
		
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<form:form id="formAgregar" name="formAgregar" action="aportarDocIncidencia.run" method="post" commandName="docIncForm" enctype="multipart/form-data">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="origenEnvio" id="origenEnvio" value="${origenEnvio}">
				<!-- Pet. 50775 ** MODIF TAM (06.06.2018) ** Inicio  --> 
				<input type="hidden" name="origen" id="origen" value="${origen}">
				<input type="hidden" name="tipoEnvio" id="tipoEnvio" value="${tipoEnvio}">
				<input type="hidden" name="opcionBusqueda" id="opcionBusqueda" value="${tipoBusqueda}">
        	    <input type="hidden" name="poliza_plan" id="poliza_plan" value="${plan}">
        	    <input type="hidden" name="plan_aseg" id="plan_aseg" value="${plan}">
        	    <input type="hidden" name="linea_aseg" id="linea_aseg" value="${linea}">
        	    <input type="hidden" name="referencia" id="referencia" value="${referencia}">
        	    <input type="hidden" name="nifcif" id="nifcif" value="${nifcif}">
        	    <input type="hidden" name="anhoincidencia" id="anhoincidencia" value="${anhoincidencia}">
        	    <input type="hidden" name="idenvio" id="idenvio" value="${idenvio}">
        	    <input type="hidden" name="fechaEnvio" id="fechaEnvio" value="${fechaEnvio}">
        	    <!-- (06.07.2018) -->
				<input type="hidden" name="lineaVolver" id="lineaVolver" value="${lineaVolver}"/>
				<input type="hidden" name="referenciaVolver" id="referenciaVolver" value="${referenciaVolver}"/>
				<input type="hidden" name="fechaEnvVolver" id="fechaEnvVolver" value="${fechaEnvVolver}"/>
			
				<input type="hidden" name="nomLineaVolver" id=nomLineaVolver value="${nomLineaVolver}"/>
				<input type="hidden" name="idPolizaVolver" id="idPolizaVolver" value="${idPolizaVolver}"/>
				<input type="hidden" name="nombreCompleto" id="nombreCompleto" value="${nombreCompleto}"/>
				<input type="hidden" name="moduloVolver" id="moduloVolver" value="${moduloVolver}"/>
				<input type="hidden" name="codPlanVolver" id="codPlanVolver" value="${codPlanVolver}"/>   
        	    
				<!-- inc. Tatiana (28.05.2018) -->
				<form:hidden path="incidencias.idincidencia" id="idincidencia" />
				<!--<form:hidden path="incidencias.numincidencia" id="numinc" />-->
				<input type="hidden" name="extensiones" id="extensiones" value="${extensiones}"/>
				<c:if test="${not empty documentos}">
   					<input type="hidden" name="haydocumentos" id="haydocumentos" value="haydocumentos"/>
				</c:if>
				<fieldset class="panel2 isrt">
					<div style="width: 100%; text-align: center;">
						<div style="height:15px; text-align: center;">
							<c:if test="${origenEnvio eq 'agroseguro'}">
								<%@ include file="/jsp/moduloUtilidades/incidenciasAgro/aportarDocIncidencia_origen_agroseguro.jspf" %>
							</c:if>
							<c:if test="${origenEnvio eq 'baseDatos'}">
							     <%--MODIF TAM (12.06.2018): Si entramos a consultar datos mostramos las 4 opciones. --%>
							     <c:if test="${origen eq 'altaInc'}">
							    	<%@ include file="/jsp/moduloUtilidades/incidenciasAgro/aportarDocIncidencia_origen_basedatos.jspf" %>
							     </c:if>	
								 <c:if test="${origen ne 'altaInc'}">
								    <%@ include file="/jsp/moduloUtilidades/incidenciasAgro/aportarDocIncidencia_origen_agroseguro.jspf" %>
								 </c:if>   
							</c:if>
						</div>
						<br/>
					</div>
				</fieldset>	
				<br/>
				<br/>				
				<fieldset class="panel2 isrt" style="text-align:left">
					<span style="text-align:left; vertical-align:top">
						<label for="codasunto" class="literal">Asunto: </label>
						<c:choose>
							<c:when test="${consulta}">
								<input class="dato" style="vertical-align: top;" name="descAsunto" id="descAsunto" disabled="disabled" size="50" value="${descAsunto}"/>
							</c:when>
							<c:otherwise>
								<select class="dato asegurado" id="codasunto" name="codasunto" style="vertical-align: top;">
									<c:choose>
										<c:when test="${not empty asuntoInc}">
											<option value="${asuntoInc.id.codasunto}">${asuntoInc.descripcion}</option>
										</c:when>
										<c:otherwise>
											<option value=""/>
										</c:otherwise>
									</c:choose>
									<c:forEach items="${listaAsuntos}" var="asunto">
										<option value="${asunto.id.codasunto}">${asunto.descripcion}</option>
									</c:forEach>
								</select>
								<label class="campoObligatorio"	id="campoObligatorio_codasunto" title="Campo obligatorio"> *</label>
							</c:otherwise>
						</c:choose>	
					</span>
							
					<span style="text-align:left; text-align:bottom">
					<label for="observaciones" class="literal">Observaciones:</label>
						<c:choose >
							<c:when test="${consulta}">
								<c:choose>
									<c:when test="${not empty docIncForm.incidencias.observaciones}">
										<textarea cols="110" rows="3" class="dato" width="65%" disabled="disabled" id="observaciones">
											${docIncForm.incidencias.observaciones}
										</textarea>
									</c:when>
									<c:otherwise>
										<textarea cols="110" rows="3" class="dato" width="65%" disabled="disabled" id="observaciones" ></textarea>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<form:textarea cols="110" rows="3" path="incidencias.observaciones"  cssClass="dato" id="observaciones" />						
							</c:otherwise>
						</c:choose>										
					</span>									
				</fieldset>
				
				<br/>
				<br/>
				
				<fieldset class="panel2 isrt" style="text-align:center">
					<c:if test="${not consulta}">
						<div>
							<span style="vertical-align:top;">
								<label for="fichero" class="literal">Fichero:</label>
								<input type="file" class="dato" id="fichero" name="fichero" />
							</span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<span style="vertical-align:top;">
								<a class="bot" id="btnAgregar" href="#" onclick="agregar()">Agregar</a>
							</span>	
						</div>
						<br/>
					</c:if>
					
					<c:choose>
						<c:when test="${not empty documentos}">
							<div id="grid" style="width:35%; float:center; padding-left: 5px; margin:0 auto;">
								<table class="LISTA">
									<thead>
										<tr style="border-bottom:1px solid black;">
											<th style="border-bottom:1px solid black;" class="cblistaC">Acciones</th>
											<th style="border-bottom:1px solid black;" class="cblistaC">Ficheros adjuntos</th>
											<!-- <th class="cblistaC">Extensión</th> -->
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${documentos}" var="doc">
											<c:set var="urlDescargar" value="aportarDocIncidencia.run?method=doDescargarDoc&idDoc=${doc.iddocumento}&idInc=${doc.incidencias.idincidencia}" />
											<c:set var="idBtnBorrar" value="btnBorrar${doc.iddocumento}"/>
											<c:set var="idBtnDescargar" value="btnDescargar${doc.iddocumento}"/>
											<c:set var="funcionBorrar" value="borrar(${doc.iddocumento}, ${doc.incidencias.idincidencia})"/>
											<tr>
												<td class="contenlistaC">
													<c:if test="${not consulta}">
														<a id="${idBtnBorrar}" href="#" onclick="${funcionBorrar}">
														    <!-- Pet. 50775 ** MODIF TAM -->
															<%-- <img alt="Descargar" src="jsp/img/displaytag/delete.png">--%>
															<img alt="Eliminar" src="jsp/img/displaytag/delete.png">
														</a>
													</c:if>
													<a id="${idBtnDescargar}" href="${urlDescargar}">
														<img alt="Descargar" src="jsp/img/displaytag/download.png">
													</a>
												</td>
												<td class="contenlistaC">${doc.nombre}${doc.tiposDocInc.extension}</td>
												<!-- <td class="contenlistaC">${doc.tiposDocInc.extension}</td> -->
											</tr>
										</c:forEach>
									</tbody>
								</table>		  							               
							</div>
						</c:when>
						<c:otherwise>
							<div id="grid" style="text-align: center; padding-left: 5px;">
								No hay documentos que mostrar.
							</div>
						</c:otherwise>
					</c:choose>
					
				</fieldset>	
			</form:form>
		</div>
			
		<form id="formBorrar" name="formBorrar" action="aportarDocIncidencia.run" method="post">
			<input type="hidden" name="method" id="methodBorrar" value="doEliminarDoc" />
			<input type="hidden" name="origenEnvio" id="origenEnvio" value="${origenEnvio}" />
			<input type="hidden" name="origen" id="origen" value="${origen}" />
			<input type="hidden" name="idInc" id="idIncBorrar" />
			<input type="hidden" name="idDoc" id="idDocBorrar" />
			<input type="hidden" name="extensiones" id="extensiones" value="${extensiones}" />
			<input type="hidden" name="codAsuntoBor" id="codAsuntoBor" value="${asuntoInc.id.codasunto}" />
			<input type="hidden" name="tipoEnvioBor" id="tipoEnvioBor" value="${tipoEnvio}" />
			<input type="hidden" name="opcionBusquedaBor" id="opcionBusquedaBor" value="${tipoBusqueda}" />
			
			
			<!-- Añadido DNF (13.08.2018) -->
			<input type="hidden" name="referenciaVolver" id="referenciaVolver" value="${referenciaVolver}"/>
			<input type="hidden" name="lineaVolver" id="lineaVolver" value="${lineaVolver}"/>
			<input type="hidden" name="codPlanVolver" id="codPlanVolver" value="${codPlanVolver}"/>
			<input type="hidden" name="idPolizaVolver" id="idPolizaVolver" value="${idPolizaVolver}"/>
			<input type="hidden" name="nomLineaVolver" id="nomLineaVolver" value="${nomLineaVolver}"/>
			<input type="hidden" name="moduloVolver" id="moduloVolver" value="${moduloVolver}"/>
			
			
		</form>
		
		<form id="formVolver" name="formVolver" action="aportarDocIncidencia.run" method="post">
			<input type="hidden" name="method" id="method" value="doVolver" />
			<input type="hidden" name="origenLlamada" value="" />
			<input type="hidden" name="idincidencia" id="idincidenciaConsulta" value="${vuelta.idincidencia}" />
			<input type="hidden" name="codentidad" id="entidadConsulta" value="${vuelta.codentidad}" />
			<input type="hidden" name="referencia" id="referenciaConsulta" value="${vuelta.referencia}" />
			<input type="hidden" name="oficina"id="oficinaConsulta" value="${vuelta.oficina}" />
			<input type="hidden" name="entmediadora" id="entmediadoraConsulta" value="${vuelta.entmediadora}" />
			<input type="hidden" name="subentmediadora" id="subentmediadoraConsulta" value="${vuelta.subentmediadora}" />
			<input type="hidden" name="delegacion" id="delegacionConsulta" value="${vuelta.delegacion}" />
			<input type="hidden" name="codplan" id="planConsulta" value="${vuelta.codplan}" />
			<input type="hidden" name="codlinea" id="lineaConsulta" value="${vuelta.codlinea}" />
			<input type="hidden" name="codestado" id="codestadoConsulta" value="${vuelta.codestado}" />
			<input type="hidden" name="codestadoagro" id="codestadoagroConsulta" value="${vuelta.codestadoagro}" />
			<input type="hidden" name="nifcif" id="nifcifConsulta" value="${vuelta.nifcif}" />
			<input type="hidden" name="tiporef" id="tiporefConsulta" value="${vuelta.tiporef}" />
			<input type="hidden" name="idcupon" id="idcuponConsulta" value="${vuelta.idcupon}" />
			<input type="hidden" name="asunto" id="asuntoConsulta" value="${vuelta.asunto}" />
			<input type="hidden" name="fechaEnvioDesdeId" id="fechaEnvioDesdeIdConsulta" value="${fechaEnvioDesdeStr}" />
	   		<input type="hidden" name="fechaEnvioHastaId" id="fechaEnvioHastaIdConsulta" value="${fechaEnvioHastaStr}" />				    		
	   		<input type="hidden" name="numIncidencia" id="numIncidenciaConsulta" value="${vuelta.numero}" />
	   		<input type="hidden" name="codusuario" id="codUsuarioConsulta" value="${vuelta.codusuario}" />
	   		<input type="hidden" name="tipoinc" id="tipoincConsulta" value="${vuelta.tipoinc}" />
		</form>
		
		<!--  MODIF TAM (04.06.2018) -->
		<!-- Formulario para vconsultar la relacion de incidencias asociadas a una poliza en agroseguro -->
		<form id="formVolverInc" style="height:98%; text-align:center"  name="formVolverInc" action="listaIncidenciasAgro.run" method="post"> 
        	<input type="hidden" id="method" name="method" value="doConsultar" />
        	<input type="hidden" name="opcionBusqueda" id="opcionBusqueda" value="${tipoBusqueda}" />
        	<input type="hidden" name="origen" id="origen" value="${origen}" />
        	<input type="hidden" name="poliza_plan" id="poliza_plan" value="${plan}" />
        	<input type="hidden" name="plan" id="plan" value="${plan}" />
        	<input type="hidden" name="poliza_referencia" id="poliza_referencia" value="${referencia}" />
        	<input type="hidden" name="linea" id="linea" value="${linea}" />
        	<input type="hidden" name="asegurado_nifcif" id="asegurado_nifcif" value="${nifcif}" />
        </form>
        
        <!-- MODIF TAM (04.07.2018) -->
		<!-- Formulario para volver a la consulta de relación de incidencias asociadas a una póliza en Agroseguro -->
		<form name="impresionIncidenciasMod" id="impresionIncidenciasMod" action="impresionIncidenciasMod.html" method="post" commandName="anexoModificacion">
			<input type="hidden" id="methodImpIncMod" name="method" value="doVolverIncidencias" />
			<input type="hidden" name="lineaVolver" id="lineaVolver" value="${lineaVolver}" />
			<input type="hidden" name="referenciaVolver" id="referenciaVolver" value="${referenciaVolver}" />
			<input type="hidden" name="fechaEnvVolver" id="fechaEnvVolver" value="${fechaEnvVolver}" />
			
			<input type="hidden" name="nomLineaVolver" id=nomLineaVolver value="${nomLineaVolver}" />
			<input type="hidden" name="idPolizaVolver" id="idPolizaVolver" value="${idPolizaVolver}" />
			<input type="hidden" name="nombreCompleto" id="nombreCompleto" value="${nombreCompleto}" />
			<input type="hidden" name="moduloVolver" id="moduloVolver" value="${moduloVolver}" />
			<input type="hidden" name="codPlanVolver" id="codPlanVolver" value="${codPlanVolver}" />
			
		</form>
        	
        
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	</body>
</html>