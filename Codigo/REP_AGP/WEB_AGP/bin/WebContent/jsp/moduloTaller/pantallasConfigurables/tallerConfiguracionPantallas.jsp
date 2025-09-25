<%@ include file="/jsp/common/static/taglibs.jsp"%>
<jsp:directive.page import="org.displaytag.*" />
<fmt:setBundle basename="displaytag"/>
<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>

<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>

<html>
<head>
    <title>Agroplus - Relacion campos</title>
    
    <%@ include file="/jsp/common/static/metas.jsp"%>
		
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css"/>
    <link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
    <link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    <link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />

	<!--  GDLD-78692 ** MODIF TAM (28.12.2021) * Resolución Defecto Nº9 */ -->
	<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>

    <script type="text/javascript" src="jsp/js/util.js" ></script>
    <script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
    <script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
    <script type="text/javascript" src="jsp/js/util.js"></script>  
    <script type="text/javascript" src="jsp/moduloTaller/relacionCampos/relacionCampos.js" ></script>
    <script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
    <script type="text/javascript" src="jsp/js/menuapli.js"></script>
    
    <script type="text/javascript" src="jsp/moduloTaller/pantallasConfigurables/tallerConfiguracionPantallas.js" ></script>  
    <script type="text/javascript" src="jsp/moduloTaller/pantallasConfigurables/tallerConfiguracionPantallasAjax.js" ></script>
	
	<style type="text/css"> 
        .scrollable{ overflow: auto; width: 250px; height: 40px; border: 1px silver solid; } 
        .scrollable select{ border: none; } 
    </style>
</head>
	
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">	

	<%@ include file="/jsp/common/static/cabecera.jsp"%>	
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>	
	
	<form:form name="controlesPantalla" id="controlesPantalla" action="tallerConfiguracionPantallas.html" method="post" commandName="formBean">
		
		<input type="hidden" id="lineaseguroid" value="${idLinea}"/>
		<input type="hidden" id="codPlan" value="${codPlan}"/>
		<input type="hidden" id="codLinea" value="${codLinea}"/>
		<input type="hidden" id="idPantalla" value="${idPantalla}"/>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:99%;height:650px;text-align:left;">
		    <p class="titulopag">Taller Configuración pantallas</p>
		    
		    <center><%@ include file="/jsp/common/static/avisoErrores.jsp"%></center>
			
			<div style="border:1px solid; border-color:#CCCCCC; width:100%">
				<table border="0">
				<tr>
					<td class="literal">Plan:&nbsp;&nbsp;${codPlan}</td>
					<td class="literal">L&iacute;nea:&nbsp;&nbsp;${codLinea}</td>
					<td class="literal">Pantalla:&nbsp;&nbsp;${descPantalla}</td>
				</tr>
				</table>
			</div>
			
			<table border="0" width="100%">
			<tr>
				<td valign="top">
					<fieldset>
						<legend class="literal">
							<label for="slctUsos">Uso:&nbsp;</label>
							<select id="slctUsos" onchange="cambiarUso();" class="dato consulta-group">
								<option value="-1">-- Seleccione opci&oacute;n --</option>
								<c:forEach items="${usosLst}" var="usoVO">
									<c:if test="${usoVO.id == 31}">
										<option value="${usoVO.id}" selected="selected">${usoVO.descripcion}</option>
									</c:if>
									<c:if test="${usoVO.id != 31}">
										<option value="${usoVO.id}">${usoVO.descripcion}</option>
									</c:if>
								</c:forEach>
							</select>
						</legend>
						<div style="height:350px;border:1px solid; border-color:#CCCCCC; overflow-x:hidden; overflow-y:scroll;">
							<img src="jsp/img/cargando_mini.gif" id="ajaxLoading_componentesContainer">
							<div id="componentesContainer" style="display:none;"></div>
						</div>
					</fieldset>
				</td>
				<td>					
					<div id="buttons">						
						<a class="bot" id="btnLimpiar" href="#" onclick="limpiar();">Limpiar</a>
						<a class="bot" id="btnGuardar" href="#" onclick="guardar();">Guardar</a>
						<a class="bot" id="btnRecargar" href="#" onclick="reloadData();">Recargar configuraci&oacute;n</a>
						<a class="bot" id="btnVolver" href="#" onclick="volver();">Volver</a>
					</div>
					
					<table border="0" id="controlsContainerToolBar" style="margin-top:10px;">
					<tr>
						<td class="literal">
							<a href="/rsi_agp/tallerConfiguracionPantallas.html?method=doPreview&idPantalla=${idPantalla}" id="lnkPrevisualizar" target="_blank">
								<img alt="Previsualizar dise&ntilde;o" src="jsp/img/displaytag/eye.png" />
								<label for="lnkPrevisualizar" style="cursor:hand;">Previsualizar</label>
							</a>							
						</td>
						<td class="literal">
							<a href="#" onclick="cleanBox_click();" id="lnkLimpiar">
								<img alt="Limpiar dise&ntilde;ador" src="jsp/img/displaytag/delete.png" />
								<label for="lnkLimpiar" style="cursor:hand;">Limpiar</label>
							</a>							
						</td>
						<td class="literal">
							<a href="#" onclick="igualarVertical();" id="lnkAlinearV">
								<img alt="Alinear campos en columna" src="jsp/img/displaytag/alinear.png" />
								<label for="lnkAlinearV" style="cursor:hand;">Alinear vertical</label>
							</a>							
						</td>
						<td class="literal">
							<a href="#" onclick="igualarHorizontal();" id="lnkAlinearH">
								<img alt="Alinear campos en fila" src="jsp/img/displaytag/alinear.png" />
								<label for="lnkAlinearH" style="cursor:hand;" >Alinear horizontal</label>
							</a>							
						</td>
					</tr>
					</table>	
					<div style="height:205px; border:1px solid; border-color:#CCCCCC; margin-top:5px;">
						<img src="jsp/img/cargando_mini.gif" id="ajaxLoading_controlsContainer"/>
						<div id="controlsContainer" style="height:205px; display:none; position:relative; ; top:0px; left:0px;" ondragover="allowDrop(event);" ondrop="anhadirControl(event);"></div>
					</div>
					<div id="pnlUbicacion" style="border:1px solid; border-color:#CCCCCC; margin-top:5px; padding:5px;">
						<table cellspacing="5px;">
						<tr>
							<td class="literal" style="text-align:right;">
								<label for="txtNomCampo">Campo:&nbsp;</label>
							</td>
							<td>
								<input type="text" id="txtNomCampo" size="25" disabled="disabled" class="dato" />
							</td>
						</tr>
						<tr>
							<td class="literal" style="text-align:right;">
								<label for="txtUbTipo">Tipo:&nbsp;</label>
							</td>
							<td>
								<input type="text" id="txtUbTipo" size="25" disabled="disabled" class="dato" />
							</td>
							<td class="literal" style="text-align:right;">
								<label for=txtUbTamano>Tama&ntilde;o:&nbsp;</label>
							</td>
							<td>
								<input type="text" id="txtUbTamano" size="2" disabled="disabled" class="dato" />
							</td>
							<td class="literal" style="text-align:right;">
								<label for="txtUbNumero">Ubicaci&oacute;n:&nbsp;</label>
							</td>
							<td>
								<input type="text" id="txtUbNumero" size="2" disabled="disabled" class="dato" />
								<input type="text" id="txtUbDatos" size="25" disabled="disabled" class="dato" />
							</td>
						</tr>
						<tr>
							<td class="literal" style="text-align:right;">
								<label for="txtUbDestino">Tabla asociada:&nbsp;</label>
							</td>
							<td>
								<input type="text" id="txtUbDestino" size="8" disabled="disabled" class="dato" />
							</td>
							<td class="literal" style="text-align:right;">
								<label for="txtOcurrencia">M&uacute;ltiple:&nbsp;</label>
							</td>
							<td>
								<input type="text" id="txtOcurrencia" size="2" disabled="disabled" class="dato" />
							</td>
						</tr>
						<tr>
							<td class="literal" style="text-align:right;">
								<label for="txaUbExplicacion">Descripci&oacute;n:&nbsp;</label>
							</td>
							<td colspan="5">
								<textarea rows="3" cols="120" id="txaUbExplicacion" disabled="disabled" class="dato" style="resize:none;"></textarea>
							</td>
						</tr>
						</table>
					</div>
					<div id="pnlDisenhoPantalla" style="border:1px solid; border-color:#CCCCCC; margin-top:5px; padding:5px;">
						<table cellspacing="5px;">
						<tr>
							<td class="literal" style="text-align:right;">
								<label for="txtDisNomMostrado">Nombre mostrado:&nbsp;</label>
							</td>
							<td>
								<input type="text" id="txtDisNomMostrado" size="23" class="dato" onchange="cambiaDatosControl();" />
							</td>
							<td class="literal" style="text-align:right;">
								<label for="txDisAncho">Ancho:&nbsp;</label>
							</td>
							<td>
								<input type="number" id="txDisAncho" name="txDisAncho" class="dato" onchange="modificarAncho();" style="width:55px;"/>
							</td>
							<td class="literal" style="text-align:right;">
								<label for="txDisALto">Alto:&nbsp;</label>
							</td>
							<td>
								<input type="number" id="txDisALto" name="txDisALto" class="dato" onchange="modificarAlto();" style="width:55px;"/>
							</td>
							<td>
								<input type="checkbox" id="chk_mostrarSiempre" onclick="cambiaDatosControl();" />
								<label class="literal" for="chk_mostrarSiempre">Mostrar siempre</label>
							</td>
							<td colspan="2">
								<input type="checkbox" id="chk_mostrarCargaPac" onclick="cambiaDatosControl();" />
								<label class="literal" for="chk_mostrarCargaPac">Mostrar carga PAC/COPY</label>								
							</td>
						</tr>
						<tr>
							<td class="literal" style="text-align:right;">
								<label for="cmbOrigenDatos">Origen de datos:&nbsp;</label>
							</td>
							<td>
								<select id="cmbOrigenDatos" style="width:170px;" class="dato" onchange="cambiaDatosControl();">
									<option value="-1">-- Seleccione opci&oacute;n --</option>
									<c:forEach items="${origenDatosLst}" var="origenDatoVO">
										<option value="${origenDatoVO.id}">${origenDatoVO.descripcion}</option>
									</c:forEach>
								</select>
							</td>
							<td class="literal" style="text-align:right;">
								<label for="cmbDisTipo">Tipo:&nbsp;</label>
							</td>
							<td colspan="3">
								<select id="cmbDisTipo" style="width:170px;" class="dato" onchange="cambiaTipoControl();">
									<option value="-1">-- Seleccione opci&oacute;n --</option>
									<c:forEach items="${tipoCampoLst}" var="tipoCampoVO">
										<option value="${tipoCampoVO.id}">${tipoCampoVO.descripcion}</option>
									</c:forEach>
								</select>
							</td>
							<td>
								<input type="checkbox" id="chk_campoDeshabilitado" onclick="cambiaDatosControl();" />
								<label class="literal" for="chk_campoDeshabilitado">Campo deshabilitado</label>
							</td>
							<td class="literal" style="text-align:right;">
								<label for="txtValorCargaPac">Valor carga PAC/COPY:&nbsp;</label>
							</td>
							<td>
								<!--  GDLD-78692 ** MODIF TAM (28.12.2021) * Resolución Defecto Nº9 */ -->
								<input type="text" id="txtValorCargaPac" size="9" class="dato" onblur="if (!ComprobarFecha(this, document.main3, 'Valor carga PAC/COPY')) this.value='';" onchange="cambiaDatosControl();" />
							</td>
						</tr>
					</div>
				</td>
			</tr>
			</table>			
		</div>
	
	</form:form>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
</body>
</html>