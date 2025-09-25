<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>

<html>
<head>
	<title>Documentación de Agroseguro</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	<%@ include file="/jsp/moduloDocumentacion/popupTipoDocumento.jsp"%>
	 
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
    <script type="text/javascript" src="jsp/js/calendar.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>	
	<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
	
	<script type="text/javascript" src="jsp/moduloDocumentacion/documentacionAgroseguro.js" charset="UTF-8"></script>
	
	<script type="text/javascript" charset="UTF-8">
		 function cargarFiltro() {			 
		 	if($('#origenLlamada').val() == 'menuGeneral') {
				$('#fechaLimiteId').val('');
			}				 
			<c:if test="${origenLlamada != 'menuGeneral'}">	
				<c:forEach items="${sessionScope.docsAgroseguro_LIMIT.filterSet.filters}" var="filtro">				
					<c:if test="${filtro.property == 'codplan'}">
						$('#plan').val('${filtro.value}');
					</c:if>		
					<c:if test="${filtro.property == 'codlinea'}">
						$('#linea').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'codentidad'}">
						$('#entidad').val('${filtro.value}');
					</c:if>
					<c:if test="${perfil == 0}">
						<c:if test="${filtro.property == 'fechavalidez'}">
							$('#fechavalidez').val('${filtro.value}');
						</c:if>
					</c:if>	
					<c:if test="${filtro.property == 'docAgroseguroTipo.id'}">
						$('#docAgroseguroTipo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'descripcion'}">
						$('#descripcion').val('${filtro.value}');
					</c:if>
				</c:forEach>
			</c:if>
		}
	</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cargarFiltro();">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<!-- botones de la página -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td width="5">&nbsp;</td>
				<td align="left">
					<c:if test="${esUsuarioAdmin eq true}">
						<a class="bot" id="btnBorradoMasivo" href="javascript:borradoMasivo();">Borrado masivo</a>
					</c:if>
				</td>
				<td align="right">
				 
					<c:if test="${origenLlamada == 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a>
					</c:if>
					<c:if test="${origenLlamada != 'menuGeneral'}">
						<a class="bot" id="btnModificar" href="javascript:modificar();">Modificar</a>
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
					</c:if>					
					<a class="bot" id="btnLimpiar"href="javascript:limpiar()">Limpiar</a>
				</td>
			</tr>
		</table>
	</div>
	
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Documentación de Agroseguro</p>
	
		<!-- Form principal -->
		<form:form id="formCarga" name="formCarga" action="documentacionAgroseguro.run" method="post" commandName="docAgroseguroBean" enctype="multipart/form-data">
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>	
			
			<input type="hidden" name="usuarioSession" id="usuarioSession" value="${usuarioSession}"/>		
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
			<input type="hidden" name="extensionesPermitidas" id="extensionesPermitidas" value="${extensionesPermitidas}"/>
			<input type="hidden" name="id" id="id" value="${id}"/>
			
			<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
			<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>
			<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
			
			<!--  Pet. 79014 ** MODIF TAM (22.03.2022) ** Inicio -->
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
            <input type="hidden" name="perfil" id="perfil" value="${perfil}"/>
            <input type="hidden" name="externo" id="externo" value="${externo}" />
            <input type="hidden" name="oper" id="oper"  value="${oper}"/>
            <input type="hidden" name="perfilSel" id="perfilSel" value="${perfilSel}"/>
            <input type="hidden" name="listPerSelD" id="listPerSelD" value="${listPerSelD}"/>
            <input type="hidden" name="esPlanGenerico" id="esPlanGenerico" value="${esPlanGenerico}"/>
            <input type="hidden" name="lineaGen" id="lineaGen" value="${lineaGen}"/>
            <input type="hidden" name="nomblineaGen" id="nomblineaGen" value="${nomblineaGen}"/>
			<!--  Pet. 79014 ** MODIF TAM (22.03.2022) ** Fin -->		
			
			<fieldset class="panel2 isrt" style="width:98%;margin:0 auto;">
				<legend class="literal">Datos del documento</legend>
				<div style="width: 90%; float: left;">				
					<table cellspacing="5px" >
						<tr>
							<table>
								<tr>
									<td>
										<label for="plan" class="literal">Plan </label>
										<form:input path="codplan" size="4" maxlength="4" cssClass="dato" id="plan" tabindex="1" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');comprobarPlanGenerico(this.value)" />
										<label class="campoObligatorio" id="campoObligatorio_plan" title="Campo obligatorio"> *</label> 
									</td>
									<td>
										<div id="divlineaPlanGenerico">
											<label for="linea" class="literal">Línea </label>
											<input id="lineaCondicionado" name="lineaCondicionado.codlinea" size="3" maxlength="3" class="dato" tabindex="2" onchange="javascript:obtenernombreLinea(this.value)" />
											<input id="nom_linea" name="nomb_linea" size="25" class="dato" tabindex="3" onchange="javascript:lupas.limpiarCampos('nomb_linea');" />
											<img id="lupaLineaGen" src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaCondicionado','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
										</div>
										<div id="divlineaPlanNormal"> 
											<label for="linea" class="literal">Línea </label>
											<form:input path="codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_linea');" />
											<form:input path="nomlinea" tabindex="3" cssClass="dato" id="desc_linea" size="25" readonly="true" />
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
										</div>	
									</td>
									<td>
										<label class="campoObligatorio" id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
									</td>
									<td>
										<label for="docAgroseguroTipo" class="literal">Tipo doc. </label>								
										<form:select path="docAgroseguroTipo.id" cssClass="dato" tabindex="4" cssStyle="width:190px" id="docAgroseguroTipo" onchange="validarboton(this.value);"> 
											<form:option value="">Todos</form:option>
											<c:forEach var="i" begin="0" end="${fn:length(listaTiposDoc) - 1 }">
												<form:option value="${listaTiposDoc[i].id}">${listaTiposDoc[i].descripcion}</form:option>
											</c:forEach>
										</form:select>
										<label class="campoObligatorio" id="campoObligatorio_docAgroseguroTipo" title="Campo obligatorio"> *</label>
										<c:if test="${perfil == 0}">
											<a class="bot" id="btntipodoc" href="javascript:ejecutarbotonTipoDoc();">Alta</a>
										</c:if>	
									</td>
									<!--  Pet. 79014 ** MODIF TAM (22.03.2022) ** Inicio -->
									<td colspan="4">
										<label for="docAgroseguroTipo" class="literal">Entidad </label>
										<c:if test="${perfil == 0 || perfil == 5}">
											<form:input path="codentidad" size="4" maxlength="4" tabindex="5" cssClass="dato" id="entidad" onchange="javascript:lupas.limpiarCampos('desc_entidad');" />
											<form:input path="nombreEntidad" cssClass="dato" id="desc_entidad" size="25" readonly="true"/>
											<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
										</c:if> 
										<c:if test="${perfil > 0 && perfil < 5}">
											<form:input path="codentidad" size="4" tabindex="5" maxlength="4" cssClass="dato" id="entidad" readonly="true" />
											<form:input path="nombreEntidad" cssClass="dato" id="desc_entidad" size="25" readonly="true"/>
										</c:if> 
										<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio"> *</label>
									</td>
									<!--  Pet. 79014 ** MODIF TAM (22.03.2022) ** Fin -->
								</tr>
							</table>
						</tr>	
					</table>
					<table cellspacing="5px" >
						<tr>
							<table>
								<tr>
									<td align="left">
										<label for="descripcion" class="literal">Descripción </label>			
										<form:input path="descripcion" cssClass="dato" id="descripcion" size="65" tabindex="6" onchange="this.value = this.value.toUpperCase();"/>
									</td>
									<!--  Pet. 79014 ** MODIF TAM (22.03.2022) ** Inicio -->
									<!--  Solo visible para perfiles = 0 -->
									<td >
										<c:if test="${perfil == 0}">
				                        	<td class="literal" >
				                        		<label for="descripcion" class="literal">Fecha Validez</label>
						                    	<spring:bind path="fechavalidez">
						                    	 	<input type="text" name="fechavalidez" id="fechavalidez" size="11" maxlength="10" class="dato" tabindex="7"
						                    	 		value="<fmt:formatDate pattern="dd/MM/yyyy" value="${docAgroseguroBean.fechavalidez}" />" />
						                    	</spring:bind>
						                    	<input type="button" id="btn_fechavalidez" name="btn_fechavalidez" class="miniCalendario" style="cursor: pointer;" />
						                    </td>	
										</c:if>	
									</td>
								</tr>
							</table>
						</tr>		
					</table>
					<table>
						<tr>
							<td>
								<div width="65%" id="fileDiv">
									<c:if test="${esUsuarioAdmin eq true}">
										<label for="file" class="literal">Fichero </label>		
										<input size="65" type="file" class="dato" id="file" name="file" tabindex="8" readonly="true" />	
										<label class="campoObligatorio" id="campoObligatorio_file" title="Campo obligatorio"> *</label>
									</c:if>
								</div>
							</td>
						</tr>
					</table>
					<table width="97%" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td align="center">
								<c:if test="${esUsuarioAdmin eq true}">
									<div style="width:15%; margin-top:10px;margin-bottom: 10px;" align="center">
										<a class="bot" id="btnCargar" href="javascript:cargar();">Cargar</a>
									</div>
								</c:if>
							</td>
						</tr>		
					</table>				
				</div>
				<!--  Pet. 79014 ** MODIF TAM (23.03.2022) -->
				<div style="width:10%; float: left;">
					<!--  Solo visible para perfiles = 0 -->
					<c:if test="${perfil == 0}">
		    			<label for="listaPerfiles" align="center" class="literal">Perfil </label>
		    			<form:select id="listaPerfiles" path="listaPerfiles" cssClass="dato" multiple="true" cssStyle="height:105;width:95" tabindex="9">
							<c:forEach items="${listaPerfil}" var="perfil">
								<form:option value="${perfil.id}" id="esPerfil_${perfil.id}"> ${perfil.descripcion}</form:option>
							</c:forEach>
						</form:select>
					</c:if>	
				</div>
				<!--  Pet. 79014 ** MODIF TAM (23.03.2022) -->
			</fieldset>
		</form:form>
		
		<!-- Grid Jmesa -->
		<div id="grid">
	  		${docsAgroseguro}		  							               
		</div> 	
		
	</div>	
		<form name="limpiar" id="limpiar" action="documentacionAgroseguro.run" method="post">
		<input type="hidden" name="method" id="method" value="doConsulta"/>								
		<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
	</form>
	
	<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
	     <!--  header popup -->
		 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
	        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Aviso</div>
	        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
	                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
	            <span onclick="hidePopUpAviso()">x</span>
	        </a>
		 </div>
		 <!--  body popup -->
		 <div class="panelInformacion_content">
			<div id="panelInformacion2" class="panelInformacion">
				<div id="txt_mensaje_aviso"></div>
			</div>
			<div style="margin-top:15px">
			 	    <a class="bot" id="btn_hidePopUpAviso" href="javascript:hidePopUpAviso()" title="Aceptar">Aceptar</a>
			</div>
		 </div>
	</div>
		
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineasCondicionado.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidadMediadora.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	
</body>
</html>