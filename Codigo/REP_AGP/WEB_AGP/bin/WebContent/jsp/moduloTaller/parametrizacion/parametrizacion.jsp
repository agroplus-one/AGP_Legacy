<%@ page language="java" contentType="text/html;"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>

<html>
	<head>
		<title>Parametrizaci&oacute;n</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />

		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />

		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>

		<%@ include file="/jsp/js/draggable.jsp"%>
		<script type="text/javascript" src="jsp/moduloTaller/parametrizacion/parametrizacion.js" ></script>
											
		
		
		
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
					
						<!-- <a class="bot" href="javascript:generales.enviarForm('main')">Actualizar</a> -->
						<a class="bot" href="javascript:enviarForm()">Actualizar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%; vertical-align: top;">
			<p class="titulopag" align="left">Parametrización</p>
			<form:form name="main3" id="main3" action="parametrizacion.html" method="post" commandName="parametrizacionBean">
				<input type="hidden" name="operacion" value="actualizar" />
				<form:hidden path="id"/>
				<form:hidden path="estadoPlzRenovPago"/>
				<form:hidden path="estadoPlzRenovCarga"/>
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<table width="100%">
					<tr style="vertical-align: top;">
						<td width="40%">
							<table width="100%">
								<tr>
									<td width="100%">
										<div class="panel2 isrt" style="width: 100%">
											<fieldset >
												<fieldset class="fieldset_alone" style="width: 80%">
													<legend class="literal">Validaci&oacute;n y C&aacute;lculo de las p&oacute;lizas</legend>
													<form:radiobutton id="validacion" path="validacion" value="1" label="Servicio&nbsp;Web&nbsp;Remoto" />
												</fieldset>		
											</fieldset>					
										</div>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<div class="panel2 isrt" style="width: 100%">
										<fieldset>
											<fieldset class="fieldset_alone" style="width: 80%">
												<legend class="literal">Confirmaci&oacute;n de las p&oacute;lizas</legend>
												<form:radiobutton id="confirmacion" path="confirmacion" value="0" label="Servicio&nbsp;Web" />
												<form:radiobutton id="confirmacion" path="confirmacion" value="1" label="Https" />
											</fieldset>	
											<br />
											<br />
											<br />
											<!-- <fieldset> -->
												<span class="literal">Timeout&nbsp;Servicio&nbsp;Web&nbsp;Remoto:
													<form:input path="timeout" cssClass="dato" size="4" maxlength="4" onkeypress="soloNumeroEntero(event);"/>
														(segundos)
												</span>
											<!-- </fieldset> -->
											<br />
											<br />
											<!-- <fieldset> -->
												<span class="literal">N&uacute;mero&nbsp;M&aacute;x.&nbsp;Comparativas&nbsp;en&nbsp;Contrataci&oacute;n:
													<form:input path="maxcomparativas" cssClass="dato" size="4" maxlength="2" onkeypress="soloNumeroEntero(event);"/>
												</span>
											<!-- </fieldset>	 -->	
											<br />
											<br />
											<span class="literal">Password&nbsp;Buz&oacute;n&nbsp;Infov&iacute;a:
												<input id="passwordBuzonInfovia" name="passwordBuzonInfovia" type="password" class="dato" size="14"  maxlength="10" value ="${passwordBuzonInfovia}"/>
											</span>	
										</fieldset>
										</div>
									</td>
								</tr>
								
								<tr>
									<td colspan="2">
										<div class="panel2 isrt" style="width: 100%">
										<fieldset>
											<fieldset class="fieldset_alone">
												<legend class="literal">Comisiones</legend>
													% Retención <form:input path="pctRetencion" id="pctRetencion" cssClass="dato" size="3" onkeypress="numberFrom0To100(event);"/>
													<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_pctRetencion"> *</label>									
											</fieldset>
										</fieldset>
										</div>
									</td>
								</tr>
								
							</table>
						</td>
						<td width="60%">
							<table width="100%">
								<tr>
									<td colspan="2" width="100%">
										<div class="panel2 isrt" style="width: 100%">
											<fieldset>
												<fieldset class="fieldset_alone"  style="width: 60%">
													<legend class="literal">C&aacute;lcular situaci&oacute;n actual en servicio de c&aacute;lculo de anexos</legend>
													<form:radiobutton id="calcSitAct" path="calculoSitActSwCalculoAm" value="0" label="No" />
													<form:radiobutton id="calcSitAct" path="calculoSitActSwCalculoAm" value="1" label="S&iacute;" />
												</fieldset>
											</fieldset>
										</div>
									</td>
								</tr>
								
								<tr>
									<td width="50%">
										<div class="panel1 isrt" style="width: 100%">
											<fieldset class="fieldset_alone" style="width: auto; text-align: center;">
												<legend class="literal">Env&iacute;o de P&oacute;lizas Renovables a Pagos</legend>
												Desde hace&nbsp;		<form:input path="numDiasDesdePagoRenov" id="numDiasDesdePagoRenov" cssClass="dato" size="2" maxlength="3" onkeypress="soloNumeroEntero(event);"/>
												<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_numDiasDesdePagoRenov"> *</label>
												hasta dentro de &nbsp; 	<form:input path="numDiasHastaPagoRenov" id="numDiasHastaPagoRenov" cssClass="dato" size="2" maxlength="3" onkeypress="soloNumeroEntero(event);"/>&nbsp;d&iacute;as
												<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_numDiasHastaPagoRenov"> *</label>									
											</fieldset>
										</div>
									</td>
									<td width="50%">
										<div class="panel1 isrt" style="width: 100%">
											<fieldset class="fieldset_alone" style="width: auto; text-align: center;">
												<legend class="literal">D&iacute;as para asignaci&oacute;n de fecha de vencimiento</legend>
												<form:input path="numDiasFechaVigor" id="numDiasFechaVigor" cssClass="dato" size="2" maxlength="3" onkeypress="soloNumeroEntero(event);"/>&nbsp;d&iacute;as
												<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_numDiasFechaVigor"> *</label>																		
											</fieldset>
										</div>
									</td>	
									
								</tr>
								
								<tr><td></td></tr>
							
								<tr>
									<td align="center" width="50%">
										<div ><!-- class="panel1 isrt" -->
											<fieldset class="fieldset_alone">
												<legend class="literal">Estados para env&iacute;o de P&oacute;lizas Renovables a Pagos</legend>
														
													<select name="estadosRenovacionPagos" id="estadosRenovacionPagos" class="dato" multiple="multiple" style="height:140px;">
														<c:if test="${estadosRenovacion != null}">
															<c:forEach items="${estadosRenovacion}" var="eR">
																<option value="${eR.codigo}" id="esPa_${eR.codigo}">${eR.descripcion}</option>
																<!-- <option selected="selected">Shrubs</option> -->
															</c:forEach>	
														</c:if>
													</select>
													<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_estadosRenovacionPagos"> *</label>															
											</fieldset>
										</div>
									</td>
									<td align="center" width="50%">
										<div style="border:10px" ><!-- class="panel1 isrt" -->
											<fieldset ><!-- class="fieldset_alone" -->
												<legend class="literal">Estados para carga de Renovables como P&oacute;lizas</legend>
													<select name="estadosRenovacionPolizas" id="estadosRenovacionPolizas" class="dato" multiple="multiple" style="height:140px;">
														<c:if test="${estadosRenovacion != null}">
															<c:forEach items="${estadosRenovacion}" var="eR">
																<option value="${eR.codigo}" id="esCa_${eR.codigo}">${eR.descripcion}</option>
																<!-- <option selected="selected">Shrubs</option> -->
															</c:forEach>	
														</c:if>
													</select>
													<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_estadosRenovacionPolizas"> *</label>																		
											</fieldset>
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<div style="border:10px" >
								<fieldset >
									<legend class="literal">Configuraci&oacute;n Agroplus</legend>
										<select name="configAgpNemo" id="configAgpNemo" class="dato" style="width:450px;" onchange="loadAgpValor();">
											<option value="">Seleccione...</option>
											<c:forEach items="${nemosConfigAgp}" var="nemo">
												<option value="${nemo.agpNemo}">${nemo.agpNemo} - ${nemo.agpDescripcion}</option>
											</c:forEach>
										</select>
										<input id="configAgpValor" name="configAgpValor" type="text" class="dato" size="60" maxlength="2000" value=""/>
										<a class="bot" href="#" onclick="updateAgpValor(); return false;">Modificar</a>																		
								</fieldset>
							</div>
						</td>
					</tr>
				</table>
				
			</form:form>
		</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
	</body>
</html>
