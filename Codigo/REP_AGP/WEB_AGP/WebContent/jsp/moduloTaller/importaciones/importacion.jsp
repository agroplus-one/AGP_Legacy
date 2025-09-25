<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Agroplus - Importaci&oacute;n</title>
		
        <%@ include file="/jsp/common/static/metas.jsp"%>
		
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/util.js"></script>  
		<script type="text/javascript" src="jsp/moduloTaller/importaciones/importaciones.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		
	</head>
	
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	<div id="buttons">
	<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td>&nbsp;
								</td>
							</tr>
						</tbody>
					</table>
					<!-- FIN TABLA BARRA DE BOTONES-->
				</td>
			</tr>
	</table>
	</div>	
	<!-- Contenido de la página -->
	<div class="conten" style="padding:3px;width:97%">
		<form action="importacion.html" method="post" name="frmImportacion" id="frmImportacion">		
		<input type="hidden" name="operacion" id="operacion" value="" />
		<!-- Hiddens para tratar cada una de las importaciones -->
		<input type="hidden" name="seleccionadosOrganizador" value="${importaciones.listas.listaOrganizador}"/>
		<input type="hidden" name="seleccionadosCondGeneral" value="${importaciones.listas.listaCondGenetal}"/>
		<input type="hidden" name="seleccionadosCondPL" value="${importaciones.listas.listaCondPL}"/>
		<input type="hidden" name="rutaTodo" value="${importaciones.rutaImpTodo}"/>
		<input type="hidden" name="rutaOrg" value="${importaciones.rutaImpOrganizador}"/>
		<input type="hidden" name="rutaGeneral" value="${importaciones.rutaImpGeneral}"/>
		<input type="hidden" name="rutaPL" value="${importaciones.rutaImpPL}"/>
		
		<!-- Hiddens para tratar las importaciones de manera genérica -->
		<input type="hidden" name="tablas" id="tablas" value=""/>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		<table style="margin:0 auto">
			<tr>
				<td class="literal">Plan: </td>
				<td><input type="text" id="plan" name="plan" class="dato" size="5" maxlength="4" value="${importaciones.datos.plan}"></td>
				<td class="literal">L&iacute;nea: </td>
				<td><input type="text" id="linea" name="linea" class="dato" size="5" maxlength="3" value="${importaciones.datos.linea }"></td>
				<td class="literal">Ubicaci&oacute;n Raiz: </td>
				<td><input type="text" id="ruta" name="ruta" class="dato" readonly="true" size="50" maxlength="50" value="${importaciones.rutaImpTodo}"></td>				
			</tr>
			<tr>
				<td class="literal" colspan="4"><input type="radio" class="literal" name="chkTipoImportacion" value="1" checked="true" onclick="javascript:importaciones.habilitaTodo();">Importar todo</td>
				<td colspan="2" rowspan="2" align="left" valign="middle" width="250"><a class="bot" href="javascript:importaciones.importar();">Importar</a></td>					
			</tr>
		</table>
		<!-- IMPORTACION DEL ORGANIZADOR -->		
		<fieldset>
			<legend class="literal"><input type="radio" class="literal" name="chkTipoImportacion" value="2" onclick="javascript:importaciones.habilitaOrg();"> Importaci&oacute;n del Organizador</legend>
			<div id="divOrganizador">
			<table width="100%">
				<tr>
					<td width="50%" align="center" class="literal">
					     <input type="radio" class="literal" name="chkOrganizador" id="chkOrganizador_1" value="1" checked="true" onclick="javascript:document.getElementById('tsOrganizador').disabled=true;">Todas las tablas
					</td>
					<td class="literal">					    
				        <input type="radio" class="literal" name="chkOrganizador" id="chkOrganizador_2" value="2" onclick="javascript:document.getElementById('tsOrganizador').disabled=false;">Tablas seleccionadas					    
					<td/>
				</tr>
				<tr>
					<td/>
					<td>
					    <div style="padding-left:22px;padding-top:5px;">
						    <select name="tsOrganizador" id="tsOrganizador" class="dato" multiple>
							    <c:forEach items="${importaciones.lstorganizador}" var="tablaO">
								    <option value="${tablaO.fichero}-${tablaO.codtablacondicionado}">${tablaO.fichero}-${tablaO.destablacondicionado}</option>
							    </c:forEach>
						    </select>
						</div>					
					</td>
					<td/>
				</tr>
			</table>
			</div>
		</fieldset>
		
		<!-- IMPORTACION DE CONDICIONADO -->
		<fieldset>
			<legend class="literal">Importaci&oacute;n del Condicionado</legend>
			<table width="100%">
				<tr>
					<td width="80%">
						<!-- CONDICIONADO GENERAL -->						
						<fieldset>
						<legend class="literal"><input type="radio" class="literal" name="chkTipoImportacion" value="3" onclick="javascript:importaciones.habilitaCond();"> Condicionado General</legend>
						<div id="divCondGeneral">
						<table width="100%">
							<tr>
								<td width="50%" align="center" class="literal"><input type="radio" class="literal" name="chkCondGeneral" id="chkCondGeneral_1" value="1" checked="true" onclick="javascript:document.getElementById('tsCondGeneral').disabled=true;">Todas las tablas</td>
								<td class="literal"><input type="radio" class="literal" name="chkCondGeneral" id="chkCondGeneral_2" value="2" onclick="javascript:document.getElementById('tsCondGeneral').disabled=false;">Tablas seleccionadas</td>
							</tr>
							<tr>
								<td/>
								<td align="right">
									<div style="padding-left:22px;padding-top:5px;">
									<select name="tsCondGeneral" id="tsCondGeneral" class="dato" multiple>
									<c:forEach items="${importaciones.lstcondgeneral}" var="tablaCG">
										<option value="${tablaCG.fichero}-${tablaCG.codtablacondicionado }">${tablaCG.fichero}-${tablaCG.destablacondicionado}</option>
									</c:forEach>
									</select>
									</div>	
								</td>
							</tr>
						</table>	
						</div>						
						</fieldset>
					</td>
					<td/>
				</tr>			
				<!-- CONDICIONADO PLAN/LINEA -->
				<tr>
					<td>					
						<fieldset>
						<legend class="literal"><input type="radio" class="literal" name="chkTipoImportacion" value="4" onclick="javascript:importaciones.habilitaPL();">Condicionado Plan/L&iacute;nea</legend>
						<div id="divPL">						
						<table width="100%">						
							<tr>
								<td width="50%" align="center" class="literal"><input type="radio" class="literal" id="chkCondPL_1" name="chkCondPL" value="1" checked="true" onclick="javascript:document.getElementById('tsCondPL').disabled=true;">Todas las tablas</td>
								<td class="literal"><input type="radio" class="literal" id="chkCondPL_2" name="chkCondPL" value="2" onclick="javascript:document.getElementById('tsCondPL').disabled=false;">Tablas seleccionadas</td>
							</tr>
							<tr>
								<td/>
								<td align="right">
									<div style="padding-left:22px;padding-top:5px;"> 
									<select name="tsCondPL" id="tsCondPL" class="dato" multiple>
									<c:forEach items="${importaciones.lstcondpl}" var="tablaPL">
										<option value="${tablaPL.fichero}-${tablaPL.codtablacondicionado}">${tablaPL.fichero}-${tablaPL.destablacondicionado }</option>
									</c:forEach>
									</select>	
									</div> 
								</td>
							</tr>
						</table>	
						</div>												
						</fieldset>					
					</td>
					<td/>	
				</tr>						
			</table>
		</fieldset>
	</form>
	</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	</body>
</html>
