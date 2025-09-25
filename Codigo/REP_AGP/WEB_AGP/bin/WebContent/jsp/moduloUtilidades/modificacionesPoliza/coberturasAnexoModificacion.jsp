<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%> 
<%@ include file="/jsp/common/static/taglibs.jsp"%> 
<%@ include file="/jsp/common/static/setHeader.jsp"%> 
<html> 
	<head> 
		<title>Modificaci&oacute;n de Coberturas</title> 
		 
		<%@ include file="/jsp/common/static/metas.jsp"%> 
		 
		<!-- Estilos --> 
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" /> 
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" /> 
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" /> 
		 
		<!-- JavaScript,jQery & AJAX --> 
		<script type="text/javascript" src="jsp/js/menuapli.js"></script> 
		<script type="text/javascript" src="jsp/js/util.js" ></script> 
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script> 
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script> 
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script> 
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script> 
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script> 
		<script type="text/javascript" src="jsp/moduloUtilidades/modificacionesPoliza/parcelasAnexoModificacionSw.js"></script>
		<script type="text/javascript" src="jsp/moduloUtilidades/modificacionesPoliza/coberturasAnexoModificacion.js"></script> 
		<%@ include file="/jsp/moduloUtilidades/modificacionesPoliza/coberturasAnexoModificacionJsDinamico.jspf"%> 
	</head> 
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4')">	 
			<%@ include file="/jsp/common/static/cabecera.jsp"%> 
			<%@ include file="/jsp/common/static/menuGeneral.jsp"%> 
			<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%> 
	 
		<div id="buttons"> 
			<table width="97%" cellspacing="0" cellpadding="0" border="0"> 
				<tr> 
					<td align="right"> 
						<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a> 
						<c:if test="${modoLectura ne 'true'}">
							<a class="bot" id="btnGrabar" href="javascript:grabar();">Grabar</a>
							<a class="bot" id="btnGrabarCont" href="javascript:grabarYContinuar();">Grabar y Continuar</a> 
						</c:if>	
					</td> 
				</tr> 
			</table> 
		</div> 
		<!-- Contenido de la pagina --> 
		<div class="conten" id="div_coberturas"> 
		
			<form action="subvencionAseguradoAnexoMod.html" method="post" name="frmcontinuar" id="frmcontinuar">
				<input type="hidden" id="method" name="method"/>
				<input type="hidden" name="subvsSeleccionadas" value="">
				<input type="hidden" name="idAnexoModificacion" value="${idAnexo}">
				<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}"/>
				<input type="hidden" id="operacion" name="operacion" value=""/>
				<input type="hidden" id="vieneDeListadoAnexosMod" name="vieneDeListadoAnexosMod" value="${vieneDeListadoAnexosMod}"/>
				<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
			</form>		
		
			<form name="validarAnexo" id="validarAnexo" action="confirmacionModificacion.html" method="post">
				<input type="hidden" id="methodValidarAnexo" name="method" value="doValidarAnexo" />
				<input type="hidden" id="redireccion" name="redireccion" value="parcelas"/>
				<input type="hidden" id="idCuponValidar" name="idCuponValidar"/> 
				<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
			</form>
		
			<form:form name="mainform" id="mainform" action="coberturasModificacionPoliza.html" method="post" commandName="anexoModificacion" > 
				<input type="hidden" id="method" name="method"/> 
				<input type="hidden" id="codModuloAnexo" name="codModuloAnexo" value="${codModuloAnexo}"/>
				<input type="hidden" id="codModuloAnexoGuardado" name="codModuloAnexoGuardado" value="${codModuloAnexo }"/>
				<input type="hidden" id="tipoModo" name="tipoModo" value="${tipoModo}"/> 
				<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}"/> 
				<input type="hidden" id="idAnexo" name="idAnexo" value="${idAnexo}"/> 
				<input type="hidden" id="vieneDeListadoAnexosMod" name="vieneDeListadoAnexosMod" value="${vieneDeListadoAnexosMod}"/> 
				<input type="hidden" id="coberturasModificadas" name="coberturasModificadas"/> 
				<input type="hidden" id="listaIdCeldaConVin" name="listaIdCeldaConVin" /> 
				<input type="hidden" id="lstIdCeldasEleg" name="lstIdCeldasEleg" />
				<input type="hidden" id="lstIdChecksEleg" name="lstIdChecksEleg" />
				<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
				 
				<c:set var="anexoConCobertuGrabadas" value="${anexoModificacion.coberturas}" /> 
				<c:if test="${empty anexoConCobertuGrabadas}">
					<input type="hidden" id="amTieneCoberturasGuardadas" name="amTieneCoberturasGuardadas" value="false"/>
				</c:if>	
				<c:if test="${not empty anexoConCobertuGrabadas}">
					<input type="hidden" id="amTieneCoberturasGuardadas" name="amTieneCoberturasGuardadas" value="true"/>
				</c:if> 
				 
				<form:hidden path="id" id="id"/> 
				<form:hidden path="poliza.idpoliza" id="idPoliza"/> 
				<form:hidden path="poliza.linea.lineaseguroid" id="lineaseguroid"/> 
				<form:hidden path="codmodulo" id="modulo"/> 
				 
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%> 
				
				<p class="titulopag" align="left">Modificaci&oacute;n de Coberturas</p> 
				<table width="97%" border="0"> 
					<tr> 
						<td> 
							<fieldset> 
								<legend id="cabeceraAnex" class="literal">Cobertura del Anexo &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;M&Oacute;DULO
								<select class='dato' style='align:left' id='modulo' name='comboModulo' onChange='javascript:cargardatosmoduloanexo($("#idPoliza").val(), this.value , "${idAnexo}");'>
									<c:set var="valorDefectoCombo" value="${codModuloAnexo}" />
									<c:forEach items="${modulos}" var="varMod"> 
									
										<c:if test="${valorDefectoCombo eq varMod.id.codmodulo}">
											<option value="${varMod.id.codmodulo}" selected> ${varMod.id.codmodulo} - ${varMod.desmodulo}</option>
										</c:if>	
										<c:if test="${valorDefectoCombo ne varMod.id.codmodulo}">
											<option value="${varMod.id.codmodulo}" > ${varMod.id.codmodulo} - ${varMod.desmodulo}</option>		
										</c:if>
									
									</c:forEach>	
								</select>
								
								</legend></legend> 
								
								<table width="100%" id="coberturaAnexo.1"> 
									<!--  Pet. 63485-Fase II ** MODIF TAM (15.09.2020) ** Fin --> 
									<tr> 
										<td> 			
											<a href="#" onclick="javascript:cargaModulo($('#idPoliza').val(),$('#codModuloAnexo').val(),'${idAnexo}');" title="Mostrar condiciones de coberturas">
												<img src="jsp/img/folderclose.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Mostrar condiciones de coberturas 
											</a> 
											</br> 
											<img id="ajaxLoading_coberturaAnexo" src="jsp/img/ajax-loading.gif" style="cursor:hand;cursor:pointer;display:none" /> 
										</td> 
									</tr> 
								</table> 
								<table width="100%" style="display:none" id="coberturaAnexo.2"> 
									<tr> 
										<td> 
											<a href="#" onclick="javascript:showdata('coberturaAnexo');" title="Ocultar condiciones de coberturas"> 
												<img src="jsp/img/folderopen.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Ocultar condiciones de coberturas 
											</a>  
										</td> 
									</tr> 
								</table> 
								<div id="coberturaAnexo" style="display:none"></div> 
							</fieldset> 
						</td> 
					</tr> 
					<tr> 
						<td> 
							<fieldset> 
								<legend class="literal">Cobertura de la P&oacute;liza &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MODULO ${tablaPolizaCabecera}</legend> 
								<table width="100%" id="coberturaPoliza.1"> 
									<tr> 
										<td> 
											<a href="#" onclick="javascript:showdata('coberturaPoliza');" title="Mostrar condiciones de coberturas"> 
												<img src="jsp/img/folderclose.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Mostrar condiciones de coberturas 
											</a>  
										</td> 
									</tr> 
								</table> 
								<table width="100%" style="display:none" id="coberturaPoliza.2"> 
									<tr> 
										<td> 
											<a href="#" onclick="javascript:showdata('coberturaPoliza');" title="Ocultar condiciones de coberturas"> 
												<img src="jsp/img/folderopen.gif" alt="Desglose" alt="Desglose" />&nbsp;&nbsp;Ocultar condiciones de coberturas 
											</a>  
										</td> 
									</tr> 
								</table> 
								<div id="coberturaPoliza" style="display:none">${tablaPoliza}</div> 
							</fieldset> 
						</td> 
					</tr> 
				</table> 
			</form:form> 
		</div> 
		<%@ include file="/jsp/common/static/piePagina.jsp"%> 
	</body> 
</html>