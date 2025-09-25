<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Consulta de hojas de campo y actas de tasacion</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>

	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/util.js"></script>
	
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
	<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/siniestros/siniestrosInformacion.js"></script>
	<%@ include file="/jsp/js/draggable.jsp"%>
	
	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4')">
	<%@ include file="../../common/static/cabecera.jsp"%>
	<%@ include file="../../common/static/menuGeneral.jsp"%>
	<%@ include file="../../common/static/datosCabeceraTaller.jsp" %>	

	<div id="buttons">
		<table width="98%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right" >
					<a class="bot" href="javascript:consultar();">Consultar</a>
					<a class="bot" href="javascript:limpiar()">Limpiar</a>		
					<a class="bot" href="javascript:volver()">Volver</a>	
				</td>
			</tr>
		</table>
	</div>
	
	<!-- variables para el botón de volver a la página de declaración de siniestros -->
	<form name="frmVolverSiniestros" id="frmVolver" action="siniestros.html" method="post">
		<input type="hidden" name="method" id="method_vo" value="doConsulta"/>
		<input type="hidden" name="idPoliza" id="idPoliza" value="${idPoliza_ha}"/>
		<input type="hidden" name="riesgoSiniestro" id="riesgoSiniestro" value="${riesgoSiniestro}"/>
		<input type="hidden" name="fechaocurrSiniestro" id="fechaocurrSiniestro" value="${fechaocurrSiniestro}"/>
		<input type="hidden" name="fechaenvioSiniestro" id="fechaenvioSiniestro" value="${fechaenvioSiniestro}"/>
		<input type="hidden" name="codestadoSiniestro" id="codestadoSiniestro" value="${codestadoSiniestro}"/> 		
		<input type="hidden" name="origenLlamada" id="origenLlamada" value="siniestrosInformacion"/>																	   	
	</form>
	
	<!-- formulario para la llamada al servicio para recuperar el pdf de la hoja de campo -->
	<form name="frmPdfHojaCampo" id="frmPdfHojaCampo" action="siniestrosInformacion.html" method="post">
		<input type="hidden" name="method" id="method_vo" value="doPdfHojaCampo"/>
		<input type="hidden" name="refPoliza_hc" id="refPoliza_hc" value=""/>
		<input type="hidden" name="codPlan_hc" id="codPlan_hc" value=""/>
		<input type="hidden" name="numHojaCampo_hc" id="numHojaCampo_hc" value=""/>
		<input type="hidden" name="tipoHoja_hc" id="tipoHoja_hc" value=""/>	
		<input type="hidden" name="origenLlamada" value="consultar">
		<input type="hidden" name="idPoliza_ha" id="idPoliza_ha" value="${idPoliza_ha}"/>																		   	
	</form>
	
	<!-- formulario para la llamada al servicio para recuperar el pdf del Acta de tasación -->
	<form name="frmPdfActaTasacion" id="frmPdfActaTasacion" action="siniestrosInformacion.html" method="post">
		<input type="hidden" name="method" id="method_vo" value="doPdfActaTasacion"/>
		<input type="hidden" name="serie_AT" id="serie_AT" value=""/>
		<input type="hidden" name="numActa_AT" id="numActa_AT" value=""/>	
		<input type="hidden" name="origenLlamada" value="consultar">
		<input type="hidden" name="idPoliza_ha" id="idPoliza_ha" value="${idPoliza_ha}"/>																			   	
	</form>
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 98%">
		<p class="titulopag" align="left">Consulta de Hojas de Campo y Actas de Tasacion</p>
		
		<!-- Datos de la póliza -->
		<fieldset style="width:98%">
			<legend class="literal">Datos de la p&oacute;liza</legend>
				<table width="94%" align="center" cellspacing="2">
					<tr>
						<td class="literal" width="40px">Plan:</td>
						<td width="40px" class="detalI">${vistaBean.poliza.linea.codplan}</td>
						<td class="literal" width="50px">Línea:</td>
						<td width="200px" class="detalI">${vistaBean.poliza.linea.codlinea } - ${vistaBean.poliza.linea.nomlinea}</td>
	  					<td class="literal" width="75px">Asegurado:</td>
	 					<td width="200px" class="detalI">${vistaBean.poliza.asegurado.nombreCompleto }</td>
					</tr>
					<tr>
						<td class="literal" width="40px">Póliza:</td>
						<td width="40px" class="detalI">${vistaBean.poliza.referencia }</td>
						<td class="literal" width="50px">Módulo:</td>
						<td width="200px" class="detalI">${vistaBean.poliza.codmodulo}</td>		
						<td class="literal" width="70px">Fec. Envío:</td>
						<td width="100px" class="detalI"><fmt:formatDate pattern="dd/MM/yyyy" value="${vistaBean.poliza.fechaenvio}"/></td>
					</tr>
				</table>
		</fieldset>
		
		<form:form name="main3" id="main3" action="siniestrosInformacion.html" method="post" commandName="vistaBean" >
			<input type="hidden" id="method" name="method" />
			<input type="hidden" name="origenLlamada" value="consultar">
			<input type="hidden" name="idPoliza_ha" id="idPoliza_ha" value="${idPoliza_ha}"/>
			<input type="hidden" name="tx_fechaTasacion.day" value="">
			<input type="hidden" name="tx_fechaTasacion.month" value=""> 
			<input type="hidden" name="tx_fechaTasacion.year" value="">
			<input type="hidden" name="tx_fechaActa.day" value="">
			<input type="hidden" name="tx_fechaActa.month" value=""> 
			<input type="hidden" name="tx_fechaActa.year" value="">
			<input type="hidden" name="tx_fechaPago.day" value="">
			<input type="hidden" name="tx_fechaPago.month" value=""> 
			<input type="hidden" name="tx_fechaPago.year" value="">		
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<div class="panel2 isrt" style="width:98%">
				<fieldset> <!-- style="width:100%" -->
					<legend class="literal">Filtro</legend>
					
					<table width="100%">
						<tr>
							<td width="55%">
								<fieldset>
									<legend class="literal">
									
										<form:radiobutton cssClass="literal" id="tipoRegistroHC" path="tipoRegistro" 
											value="0" onclick="javascript:seleccionHojasCampo();"/>&nbsp;Hojas de campo
									</legend>
									<div>
										<table width="100%">
											<tr>
												<td class="literal" >Número</td>
												<td colspan="1"><form:input path="numHojaCampo" size="8" maxlength="8" cssClass="dato" id="numHojaCampo" /></td>
												<td class="literal" >F. Tasación</td>
												<td colspan="2">										
													<spring:bind path="fechaTasacion">
														<input type="text" name="fechaTasacion"  id="tx_fechaTasacion" size="10" maxlength="10" class="dato"  
															onchange="if (!ComprobarFecha(this, document.main3, 'Fecha tasacion')) this.value='';"
															value="<fmt:formatDate pattern="dd/MM/yyyy" value="${vistaBean.fechaTasacion}"/>"/>									
													</spring:bind>
													<input type="button" id="btn_fechatasacion" name="btn_fechatasacion" class="miniCalendario"  style="cursor: pointer;"/>										
												</td>
											</tr>
											<tr>
												<td class="literal" >Tipo hoja</td>
												<td>
													<form:input	path="tipoHoja" size="2" maxlength="4"	cssClass="dato" id="tipoHoja" onchange="javascript:lupas.limpiarCampos('tipoHojaDesc', 'situacionHoja', 'situacionHojaDesc');" />
													<form:input	path="tipoHojaDesc" size="15" maxlength="40" cssClass="dato" id="tipoHojaDesc" />
													<img src="jsp/img/magnifier.png" style="cursor:  hand;" id="lupaTipoHoja" onclick="javascript:lupas.muestraTabla('TipoHoja','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
												</td>
												<td class="literal">Situación</td>
												<td>
													<form:input	path="situacionHoja" size="3" maxlength="4"	cssClass="dato" id="situacionHoja"  onchange="javascript:lupas.limpiarCampos('situacionHojaDesc');" />
													<form:input	path="situacionHojaDesc" size="20" maxlength="40" cssClass="dato" id="situacionHojaDesc" />
													<img src="jsp/img/magnifier.png" style="cursor: hand;" id="lupaSituacionHoja" onclick="javascript:lupas.muestraTabla('SituacionHojaCampo','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
												</td>
											</tr>
										</table>
									</div>
								</fieldset>
							</td>
							<td width="45%">
								<fieldset>
									<legend class="literal">
										<!-- <input type="radio" class="literal" name="chkActasTasacion" id="chkActasTasacion" value="2"  onclick="javascript:seleccionActasTasacion();"> Actas de tasación -->
										<form:radiobutton cssClass="literal" id="tipoRegistroAT" path="tipoRegistro" value="1" onclick="javascript:seleccionActasTasacion();"/>&nbsp;Actas de tasación
									</legend>
									<div>
										<table>
											<tr>
												<td class="literal" width="50px">Serie</td>
												<td colspan="1"><form:input	path="serie" size="3" maxlength="4" cssClass="dato" id="serie" /></td>
												<td class="literal" width="50px">Número</td>
												<td><form:input	path="numActa" size="4" maxlength="7" cssClass="dato" tabindex="11" id="numActa" /></td>
												<td class="literal" width="50px">Fecha</td>
												<td>										
													<spring:bind path="fechaActa">
														<input type="text" name="fechaActa"  id="tx_fechaActa" size="10" maxlength="10" class="dato"  
															onchange="if (!ComprobarFecha(this, document.main3, 'Fecha acta')) this.value='';"
															value="<fmt:formatDate pattern="dd/MM/yyyy" value="${vistaBean.fechaActa}"/>"/>									
													</spring:bind>
													<input type="button" id="btn_fechaActa" name="btn_fechaActa" class="miniCalendario"  style="cursor: pointer;"/>										
												</td>
											</tr>
											<tr>
												<td class="literal" width="50px">Situación</td>
												<td colspan="3">
													<form:input	path="situacionActa" size="3" maxlength="2"	cssClass="dato" id="situacionActa"  onchange="javascript:lupas.limpiarCampos('situacionActaDes');" />
													<form:input	path="situacionActaDes" size="20" maxlength="40" cssClass="dato" id="situacionActaDes" />
													<img src="jsp/img/magnifier.png" style="cursor: hand;" id="lupaSituacionActa" onclick="javascript:lupas.muestraTabla('SituacionActa','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
												</td>
												<td class="literal" width="50px">F. pago</td>
												<td colspan="2">										
													<spring:bind path="fechaPago">
														<input type="text" name="fechaPago"  id="tx_fechaPago" size="10" maxlength="10" class="dato"  
															onchange="if (!ComprobarFecha(this, document.main3, 'Fecha pago')) this.value='';"
															value="<fmt:formatDate pattern="dd/MM/yyyy" value="${vistaBean.fechaPago}"/>"/>									
													</spring:bind>
													<input type="button" id="btn_fechaPago" name="btn_fechaPago" class="miniCalendario"  style="cursor: pointer;"/>										
												</td>
											</tr>
										</table>
									</div>
								</fieldset>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
		
		</form:form>
		<!-- Grid Jmesa -->
		<div id="grid" style="width: 98%">
	  		${consultaSiniestrosInformacion}		  							               
		</div> 	
		
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>					  
	<%@ include file="/jsp/common/lupas/lupaTipoHoja.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSituacionHojaCampo.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSituacionActaTasacion.jsp"%>
	
</body>
</html>