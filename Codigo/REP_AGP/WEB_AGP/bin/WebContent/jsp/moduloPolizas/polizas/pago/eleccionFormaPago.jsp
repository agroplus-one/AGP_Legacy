<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
	
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Elección de la forma de pago</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>		
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/iban.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>		
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/pago/eleccionFormaPago.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/pago/popupConfirmacion.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		
		<%@ include file="/jsp/js/draggable.jsp"%>
	</head>
	
	<!-- <body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">		 -->
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"
			onload="SwitchMenu('sub3');javascript:generales.separaCuenta('${pagoPoliza.cccbanco }');">
	
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		
		<!-- ESC-9321 01/06/2020 añado la opcion de datosCabeceraConsulta.jsp que no estaba contemplada antes -->
		<c:choose>
			<c:when test="${modoLectura == 'modoLectura' && vieneDeUtilidades == 'true'}">
				<%@ include file="/jsp/common/static/datosCabeceraConsulta.jsp" %>	
			</c:when>
			<c:otherwise>
				<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
			</c:otherwise>
		</c:choose>
		<!-- FIN ESC-9321 01/06/2020 añado la opcion de datosCabeceraConsulta.jsp que no estaba contemplada antes -->

		<div id="buttons">
			<table width="100%" cellspacing="2" cellpadding="2" border="0">
				<tr>
					<!--
					<td align="left" class="ccOficina">
						<a class="bot" href="#" id="btnCcOficina"  
							onclick="javascript:verPagos();" >C/c-Oficina</a>
					</td>
					--> 
					<td align="right">
						<a class="bot" href="#" id="btnVolver" onclick="javascript:volver();">Volver</a>
						<c:if test="${modoLectura != 'modoLectura'}">
							<!-- Pet. 22208 ** MODIF TAM (23.02.2018) ** Inicio  -->
							<!-- 	<a class="bot" href="#" id="btnContinuar" onclick="javascript:aplicarFormaPago();">Continuar</a> -->
							<!-- cambiamos el literal del botón y le pasamos por parametro false -->
							<!-- <a class="bot" href="#" id="btnContinuar" onclick="javascript:aplicarFormaPago(false);">Pasar a definitiva</a> -->
							<!-- Añadimos un nuevo botón para el envío de la poliza para la confirmación a través del WS -->
							<!-- <a class="bot" href="#" id="btnSWConfirmacion" onclick="if(confirmaSW())aplicarFormaPago(true);">SW Confirmación</a> -->
							<a class="bot" href="#" id="btnSWConfirmacion" onclick="javascript:aplicarFormaPago(true);">SW Confirmación</a> 
							<!-- Pet. 22208 ** MODIF TAM (23.02.2018) ** Fin  -->	
							
						</c:if>	
					</td>
				</tr>
			</table>
		</div>
	
	<div class="conten">
		<!-- DAA 03/05/12 -->
		<p class="titulopag" align="left">Elección de la forma de pago</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<form:form method="post" name="pasarADefinitiva" id="pasarADefinitiva" action="eleccionFormaPago.html" commandName="pagoPoliza">
				<input type="hidden" id="methodPasarADef" name="method" value="doGuardar"/>
				<input type="hidden" name="tx_fechaPago.day" value=""/>
				<input type="hidden" name="tx_fechaPago.month" value=""/>
				<input type="hidden" name="tx_fechaPago.year" value=""/>
				<input type="hidden" name="idpoliza" id="idpolizaDef" value="${idpoliza}"/>
				<input type="hidden" name="numeroCuenta" id="numeroCuenta" value="${numeroCuenta}" />
				<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" />
				<input type="hidden" name="esPolPrincipal" id="esPolPrincipal" value="${esPolPrincipal}" />
				<input type="hidden" name="resultadoValidacion" id="resultadoValidacion" />
				<input type="hidden" name="actualizarSbp" id="actualizarSbp" />
				<input type="hidden" name="importe1" id="importe1" value="${pagoPoliza.importe}" />
				<input type="hidden" name="isLineaGanado" id="isLineaGanado" value="${isLineaGanado}" />
				<input type="hidden" name="cicloPoliza" id="cicloPoliza" value="${cicloPoliza}" />
				<input type="hidden" name="tipoPagoGuardado" id="tipoPagoGuardado" value="${tipoPagoGuardado}" />
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" />
				<input type="hidden" name="isPolizaFinanciada" id="isPolizaFinanciada" value="${isPolizaFinanciada}" />			
				<input type="hidden" name="costeTomador" id="costeTomador" value="${costeTomador}" />
				<input type="hidden" name="fechaPrimerPago" id="fechaPrimerPago" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPoliza.poliza.colectivo.fechaprimerpago}" />" />
				<input type="hidden" name="fechaSegundoPago" id="fechaSegundoPago" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPoliza.poliza.colectivo.fechasegundopago}" />" />
				<input type="hidden" name="importeTotal" id="importeTotal" />
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<%-- Pet. 22208 ** Inicio --%>
				<input type="hidden" name="pagada" id="pagada" value="${pagada}"/>
				<input type="hidden" name="swConfirmacion" id="swConfirmacion" value="${swConfirmacion }" /> 
				<%-- Pet. 22208 ** Fin --%>
				<%--Pet. 54046 **--%>
				<input type="hidden" name="permiteEnvIban" id="permiteEnvIban" value="${permiteEnvIban}" />
				<input type="hidden" name="indEnvIbanCpl" id="indEnvIbanCpl" value="${indEnvIbanCpl}" />
				
				<input type="hidden" name="indEnvIbanAux" id="indEnvIbanAux" value="${indEnvIbanAux}" />
				
				<input type="hidden" name="fecha1.day" value="" /> 
				<input type="hidden" name="fecha1.month" value="" /> 
				<input type="hidden" name="fecha1.year" value="" /> 
				<input type="hidden" name="fechasegundopago1.day" value="" /> 
				<input type="hidden" name="fechasegundopago1.month" value="" /> 
				<input type="hidden" name="fechasegundopago1.year" value="" />
				<input type="hidden" name="fechalimiteUsuario" id="fechalimiteUsuario" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fechalimiteUsuario}" />" />
				<input type="hidden" name="impMinimoUsuario" id="impMinimoUsuario" value="${impMinimoUsuario}" />
				<input type="hidden" name="impMaximoUsuario" id="impMaximoUsuario" value="${impMaximoUsuario}" />
				<input type="hidden" name="pctPrimerPColectivo" name="pctPrimerPColectivo" value="${pagoPoliza.poliza.colectivo.pctprimerpago}" />
				
				<input type="hidden" name="idLineaContratSuperior2019" id="idLineaContratSuperior2019" value="${lineaContratSuperior2019}"/>
				<input type="hidden" id="perfilUsu" name="perfilUsu" value="${perfilUsu}" />
				<input type="hidden" id="caracterEnvioIbanAgroseguro" name="caracterEnvioIbanAgroseguro" value="${caracterEnvioIbanAgroseguro }" />
				
				<input type="hidden" id="notaPreviaInput" name="notaPreviaInput" value="" />
				<input type="hidden" id="IPIDInput" name="IPIDInput" value="" />
				<input type="hidden" id="RGPDInput" name="RGPDInput" value="" />
				
				<form:hidden path="formapago" id="formapago"/>
			<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
	
				<!-- AMG Panel superior de datos inofrmativos y oficina propietaria -->				
						
				<table id="panelSuperior" align="center" >				
							<tr align="left">
								<td class="literal">IBAN Asegurado: </td>
								<td class="detalI" colspan = 2>
									<label id="lbl_sup_valor_IBAN" name="lbl_sup_valor_IBAN" ></label>
								</td>
							 
								<td class="literal" >
									
								</td>
								
							</tr>
							<tr align="left">
								<td class="literal">Coste Tomador: </td>
								<td class="detalI">
									<label id="lbl_sup_costeTomador" name="lbl_sup_costeTomador" ></label>
								</td>
																
								<c:if test="${perfil == 0 || perfil == 1 || perfil == 2 || perfil == 3 || perfil == 5}">
									<td class="literal" align="center" rowspan = 2 style="vertical-align:middle;">Oficina Propietaria </td>
									<td class="literal" colspan = 2 rowspan = 2 style="vertical-align:middle;">
										<c:if test="${modoLectura != 'modoLectura'}">
											<input type="hidden" id="entidad" value="${entidad}"/>
											<input name="oficina" id="oficina" size="4" tabindex="1" maxlength="4" class="dato" value="${oficina}" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
											<!-- el grupoOficinas solo se llena para perfiles 2 y este campo no lo ve el perfil 2-->
											<input type="hidden" id="grupoOficinas" value=""/>
											<form:input path="${pagoPoliza.poliza.nombreOfi}" cssClass="dato"	tabindex="-1" id="desc_oficina" size="20" readonly="true"/>
											<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');"  alt="Buscar Oficina" />
										</c:if>
										<c:if test="${modoLectura == 'modoLectura'}">
											<input type="hidden" id="entidad" value="${entidad}"/>
											<input name="oficina" id="oficina" disabled="true" size="4" maxlength="4" class="dato" value="${oficina}" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
											<!-- el grupoOficinas solo se llena para perfiles 2 y este campo no lo ve el perfil 2-->
											<input type="hidden" id="grupoOficinas" value=""/>
											<form:input path="${pagoPoliza.poliza.nombreOfi}" disabled="true" cssClass="dato"	id="desc_oficina" size="20" readonly="true"/>
										</c:if>
										
									</td>
								</c:if>
								<c:if test="${perfil == 4}">
									<td class="literal" align="center" rowspan = 2 style="vertical-align:middle;">Oficina Propietaria </td>
									<td class="literal" colspan = 2 rowspan = 2 style="vertical-align:middle;">

											<input type="hidden" id="entidad" value="${entidad}"/>
											<input name="oficina" id="oficina" disabled="true" size="4" maxlength="4" class="dato" value="${oficina}" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
											<!-- el grupoOficinas solo se llena para perfiles 2 y este campo no lo ve el perfil 2-->
											<input type="hidden" id="grupoOficinas" value=""/>
											<form:input path="${pagoPoliza.poliza.nombreOfi}" disabled="true" cssClass="dato"	id="desc_oficina" size="20" readonly="true"/>

									</td>
								</c:if>	
								
							</tr>
							<tr align="left">
								<td class="literal">Importe 1ª Fracción:</td>
								<td class="detalI">
									<c:if test="${pagoPoliza.importe != ''}">
									<label id="lbl_sup_imp_1a_fraccion" name="lbl_sup_imp_1a_fraccion" ></label>
									</c:if>
									
								</td>
	
							</tr>
		
				</table>				
				<!-- AMG FIN Panel superior de datos inofrmativos y oficina propietaria -->			
							
					<table id="tablaFormaPago" style="width:98%;">		

						<tr>
							<td  class="literal" style="width:50%;">
								<fieldset >
									<legend>
										<c:if test="${mpPagoC == true}">
											<form:radiobutton cssClass="literal" tabindex="2" id="datosCuentaId" path="tipoPago" value="0" onclick="muestraDatosCuenta()"  />Cargo en cuenta
										</c:if>
										<c:if test="${mpPagoC == false}">
											<form:radiobutton cssClass="literal" id="datosCuentaId" path="tipoPago" value="0" onclick="muestraDatosCuenta()" disabled="true"/>Cargo en cuenta	
										</c:if>
									</legend>
									
									<c:if test="${mpPagoC == true}">
									
										<div id="divCargoCuentaCompleto">
											<fieldset>
												<legend class="literal" >Forma de pago colectivo:</legend>
												
												<table style="width:100%;align:center;">
													<colgroup>
														<col width="10%" />
														<col width="12%" />
														<col width="10%" />
														<col width="12%" />
														<col width="10%" />
														<col width="10%" />
													
													</colgroup>
													<tr>
														<td></td>
											
														<td class="literal">Primer pago:</td>
														<td class="detalI">
															<label id="lbl_pct_primer_pago_col" name="lbl_pct_primer_pago_col" >${pagoPoliza.poliza.colectivo.pctprimerpago} %</label>
														</td>
														<td class="literal">Fecha:</td>
														<td class="detalI">
															<label id="lbl_fec_primer_pago_col" name="lbl_fec_primer_pago_col" >${fechaPrimerPago}</label>
														</td>
														<td></td>
													</tr>
													<tr>
														<td></td>
														<td class="literal" nowrap>Segundo pago:</td>
														<td class="detalI">
															<c:if test="${pagoPoliza.poliza.colectivo.pctsegundopago != null}">
																<label id="lbl_pct_segundo_pago_col" name="lbl_pct_segundo_pago_col" >${pagoPoliza.poliza.colectivo.pctsegundopago} %</label>
															</c:if>
															<c:if test="${pagoPoliza.poliza.colectivo.pctsegundopago == null}">
																<label id="lbl_pct_segundo_pago_col" name="lbl_pct_segundo_pago_col" ></label>
															</c:if>
														</td>
														<td class="literal">Fecha:</td>
														<td class="detalI">
															<label id="lbl_fec_segundo_pago_col" name="lbl_fec_segundo_pago_col" >${fechaSegundoPago}</label>
														</td>
														<td></td>
													</tr>
												</table>
											</fieldset>
										
											<div id="divFormaPagoCliente">
												<fieldset>
													<legend class="literal">Forma de pago cliente:</legend>
													<table>
														<c:if test="${verFormaPagoCliente == true}">											
															<tr>				
																<td class="literal" nowrap>Primer pago:</td>
																<td class="literal" nowrap>
																	<spring:bind path="pctprimerpago">
																		<form:input path="pctprimerpago" tabindex="4" id="pctprimerpagocliente" size="5" maxlength="5" cssClass="dato" cssStyle="text-align: left;" onchange="calculaImportePrimerPago();rellenaPctSegundoPago(this)"/>%
																	</spring:bind>
																</td>
																<td class="literal">Fecha:</td>
																<td class="literal" nowrap>
																	<spring:bind path="fecha">
																		<input type="text" name="fecha1" tabindex="5" id="fecha1" size="10" maxlength="10" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPoliza.fecha}" />"  onchange="if (!ComprobarFecha(this, document.pasarADefinitiva, 'Fecha primer pago')) this.value='';"/>
																	</spring:bind>
																	<input type="button" id="btn_fechaprimerpagomodificado" name="btn_fechaprimerpagomodificado"
																			class="miniCalendario" style="cursor: pointer;" />
																	<span id="errorFechaSegundoPagoModificado" />
																</td>
																<td class="literal">Importe:</td>
																<td class="literal" nowrap>
																	
																	<input type="text" name="importePrimerPagoCliente" tabindex="6" id="importePrimerPagoCliente" size="11" maxlength="10" value="${importePrimerPagoCliente}" class="dato" readonly="readonly" style="text-align: right;"/>&#8364;
																	
																</td>
																<td></td>
															</tr>
															<tr>
																<td class="literal" nowrap>Segundo pago:</td>
																<td class="literal" nowrap>
																	<spring:bind path="pctprimerpago">
																		<form:input path="pctsegundopago" tabindex="7" id="pctsegundopagocliente" size="5" maxlength="5" cssClass="dato" cssStyle="text-align: left;" onchange="calculaImporteSegundoPago();rellenaPctPrimerPago(this)"/>%
																	</spring:bind>
																</td>
																<td class="literal">Fecha:</td>
																<td class="literal" nowrap>
																	<spring:bind path="fechasegundopago">
																		<input type="text" name="fechasegundopago1" tabindex="8" id="fechasegundopago1" size="10" maxlength="10" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPoliza.fechasegundopago}" />"  onchange="if (!ComprobarFecha(this, document.pasarADefinitiva, 'Fecha segundo pago')) this.value='';"/>
																	</spring:bind>
																	<input type="button" id="btn_fechasegundopagomodificado" name="btn_fechasegundopagomodificado" class="miniCalendario" style="cursor: pointer;" />
																	<span id="errorFechaSegundoPago" />
																</td>
																<td class="literal">Importe:</td>
																<td class="literal" nowrap >
																	<input type="text" name="importeSegundoPagoCliente" tabindex="9" id="importeSegundoPagoCliente" size="11" maxlength="10" value="${importeSegundoPagoCliente}" class="dato" readonly="readonly" style="text-align: right;"/>&#8364;
																</td>
																<td></td>
															</tr>
														</c:if>
														<c:if test="${verFormaPagoCliente == false}">
															<tr>				
																<td class="literal" nowrap>Primer pago:</td>
																<td class="literal" nowrap>
																	<form:input path="pctprimerpago" disabled="true" id="pctprimerpagocliente" size="5" maxlength="5" cssClass="dato" cssStyle="text-align: left;" onchange="calculaImportePrimerPago();rellenaPctSegundoPago(this)"/>%
																</td>
																<td class="literal">Fecha:</td>
																<td class="literal" nowrap>
																	<spring:bind path="fecha">
																		<input type="text" name="fecha1" id="fecha1" disabled="true" size="10" maxlength="10" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPoliza.fecha}" />"  onchange="if (!ComprobarFecha(this, document.pasarADefinitiva, 'Fecha primer pago')) this.value='';"/>
																	</spring:bind>
																	<input type="button" id="btn_fechaprimerpagomodificado" disabled name="btn_fechaprimerpagomodificado"
																			class="miniCalendario" style="cursor: pointer;" />
																	<span id="errorFechaSegundoPagoModificado" />
																</td>
																<td class="literal">Importe:</td>
																<td class="literal" nowrap>
																	<input type="text" name="importePrimerPagoCliente" disabled="true" id="importePrimerPagoCliente" size="11" maxlength="10" value="${importePrimerPagoCliente}" class="dato" readonly="readonly" style="text-align: right;"/>&#8364;
																</td>
																<td></td>
															</tr>
															<tr>
																<td class="literal" nowrap>Segundo pago:</td>
																<td class="literal" nowrap>
																	<form:input path="pctsegundopago" disabled="true" id="pctsegundopagocliente" size="5" maxlength="5" cssClass="dato" cssStyle="text-align: left;" onchange="calculaImporteSegundoPago();rellenaPctPrimerPago(this)"/>%
																</td>
																<td class="literal">Fecha:</td>
																<td class="literal" nowrap>
																	<spring:bind path="fechasegundopago">
																		<input type="text" name="fechasegundopago1" disabled="true" id="fechasegundopago1" size="10" maxlength="10" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPoliza.fechasegundopago}" />"  onchange="if (!ComprobarFecha(this, document.pasarADefinitiva, 'Fecha segundo pago')) this.value='';"/>
																	</spring:bind>
																	<input type="button" id="btn_fechasegundopagomodificado" disabled name="btn_fechasegundopagomodificado" class="miniCalendario" style="cursor: pointer;" />
																	<span id="errorFechaSegundoPago" />
																</td>
																<td class="literal">Importe:</td>
																<td class="literal" nowrap >
																	<input type="text" name="importeSegundoPagoCliente" disabled="true" id="importeSegundoPagoCliente" size="11" maxlength="10" value="${importeSegundoPagoCliente}" class="dato" readonly="readonly" style="text-align: right;"/>&#8364;
																</td>
																<td></td>
															</tr>
														</c:if>
													</table>
												</fieldset>
											</div>
										</div>
									</c:if>
									<c:if test="${mpPagoC == false}">
										<table id="tablaNoDisponible" align="center" style="height:100px;">
											<tr>
												<td class="literal" align="center"></td>
												<td class="literal">
												<input type="hidden" name="fecha1" id="fecha1"/>
												<input type="hidden" name="fechasegundopago1" id="fechasegundopago1"/>
												Opción no disponible
												<td>
											</tr>
										</table>	
									</c:if>		
									
								
							</td>
							<td  class="literal"  style="width:50%;">
								<fieldset style="height:120px;">
									<legend> 
										<c:if test="${mpPagoM == true}">
											<form:radiobutton cssClass="literal" id="pagoManualId" tabindex="3" path="tipoPago" value="1" onclick="muestraDatosPagoManual()" />Pago Manual
										</c:if>
										<c:if test="${mpPagoM == false}">
											<form:radiobutton cssClass="literal" id="pagoManualId" path="tipoPago" value="1" onclick="muestraDatosPagoManual()" disabled="true"/>Pago Manual
										</c:if>
									</legend>
									<table id="tablaPagoManual" align="center">
										<tr>
											<td class="literal" align="left">Banco Destino</td>
											<td class="literal">
												<form:input path="banco" id="bancoDestino"  tabindex="10" cssClass="dato" size="4" maxlength="4"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" id="lupaBancoDestino" onclick="javascript:lupas.muestraTabla('Banco','principio', '', '');"  alt="Buscar Banco" />
											<td>
										</tr>
										
										<tr>
											<td class="literal" align="left">Importe</td>
											<td class="literal">
								    			<form:input path="importePago" id="importePago" tabindex="11" size="15" cssClass="dato"/>
											</td>
											
											<td class="literal">Fecha de pago</td>
											<td class="literal">
							    				 <spring:bind path="fecha">
													<input type="text" name="fecha" tabindex="12"  id="tx_fechaPago" size="8" maxlength="10" class="dato"
														value="<fmt:formatDate pattern="dd/MM/yyyy" value="${pagoPoliza.fecha}" />" 
														onchange="if (!ComprobarFecha(this, document.pasarADefinitiva, 'Fecha primer pago')) this.value='';"/>														
													
													<%-- <input type="text" name="fecha1" id="fecha1" size="11" maxlength="10" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" 
														value="${pagoPolizaBean.fecha}" />"  
														onchange="if (!ComprobarFecha(this, document.main, 'Fecha primer pago')) this.value='';"/> --%>
												
												</spring:bind>
												<input type="button" id="btn_fechaPago" name="btn_fechaPago" class="miniCalendario" style="cursor: pointer;" />
												<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_fechaPago"> *</label>
															
											</td>
										</tr>
																				
										<tr>
											<td class="literal">IBAN</td>
											<td class="literal" colspan="3">
										 		<form:hidden path="cccbanco" id="ccc" />
												<form:input path="iban" id="iban"  size="4" maxlength="4" cssClass="dato" tabindex="13" onkeyup="autotab(this, document.pasarADefinitiva.cuenta1);" onchange="this.value=this.value.toUpperCase();" />
												<input type="text" id="cuenta1" name="cuenta1" size="4" maxlength="4" class="dato" tabindex="14" onKeyup="autotab(this, document.pasarADefinitiva.cuenta2);"/>
												<input type="text" id="cuenta2" name="cuenta2" size="4" maxlength="4" class="dato" tabindex="15" onKeyup="autotab(this, document.pasarADefinitiva.cuenta3);"/>
												<input type="text" id="cuenta3" name="cuenta3" size="4" maxlength="4" class="dato" tabindex="16" onKeyup="autotab(this, document.pasarADefinitiva.cuenta4);"/>
												<input type="text" id="cuenta4" name="cuenta4" size="4" maxlength="4" class="dato" tabindex="17" onKeyup="autotab(this, document.pasarADefinitiva.cuenta5);"/>
												<input type="text" id="cuenta5" name="cuenta5" size="4" maxlength="4" class="dato" tabindex="18" "/>
												<input type="hidden" id="ccc2" name="ccc2" />
												<input type="hidden" id="iban2" name="iban2" />
												<input type="hidden" id="cuenta6" name="cuenta6" />
												<input type="hidden" id="cuenta7" name="cuenta7" />
												<input type="hidden" id="cuenta8" name="cuenta8" />
												<input type="hidden" id="cuenta9" name="cuenta9" />
												<input type="hidden" id="cuenta10" name="cuenta10" />
											</td>
										</tr>
									</table>
								</fieldset>
							</td>
						</tr>					
						<c:if test="${permiteEnvIban eq true}">
							<tr>
								<td class="literal" align="center" colspan="4">
									<div id="eIBANAgr" class="literal">
										<form:checkbox path="envioIbanAgro" id="envioIBANAgr" value="S" tabindex="19" onclick="activaDomiciliacion(this);" />	Enviar IBAN a Agroseguro
									</div>
								</td>
							</tr>
							<tr>
								<td class="literalC" align="right">
									Destinatario de la domiciliaci&oacute;n							
								<form:select path="destinatarioDomiciliacion" cssClass="dato" tabindex="20" id="destinatario" disabled="true" onchange="habilitaTitularDomic(this.value);">
									<form:option value="A">Asegurado</form:option>
									<form:option value="T">Tomador</form:option>
									<form:option value="O">Otros</form:option>										
								</form:select>								
								</td>
								<td class="literalC"  align="left">
									Titular de la cuenta									   
									<form:input path="titularCuenta" id="titularCta" size="35" maxlength="100" cssClass="dato" disabled="true"  tabindex="21" onchange="this.value = this.value.toUpperCase();" />								
								</td>							
							</tr>
							<c:if test="${tieneRC}">
								<tr>
									<td align="center" colspan="4">
										<span class="literal">RC de Ganado:</span>
										<span class="literalUtil">Suma asegurada: ${sumaAseguradaRC} &euro; &nbsp;&nbsp; Importe: ${importeRC} &euro;</span>
									</td>
								</tr>
							</c:if>
						</c:if>
					</table>
				</div>
			</div>
		</form:form>
		
		
		<input type="hidden" id="destDomicAux" name="destDomicAux" value="${pagoPoliza.destinatarioDomiciliacion}" />
		<input type="hidden" id="titularDomicAux" name="titularDomicAux" value="${pagoPoliza.titularCuenta}" />
		
		<form name="frmVolver" action="seleccionPoliza.html" method="post" id="frmVolver">				 
				<input type="hidden" id="method" name="method" value="" />
				<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" />
				<input type="hidden" name="operacion" id="operacion" value="importes" /> <!-- volver si viene de utilidades -->
				<input type="hidden" name="grProvisional" id="grProvisional" value="${grProvisional}" />
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" />
				<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}" />
				<input type="hidden" name="grProvisionalOK" id="grProvisionalOK" value="${grProvisionalOK}" />
				<input type="hidden" name="origenllamada" id="origenllamada" value="${origenllamada}" />
				<input type="hidden" name="vieneDeImportes" id="vieneDeImportesComparativas" value="${vieneDeImportes}"/>
				<input type="hidden" name="numero_da" id="numero_da" value="${numero_da}" />
				<input type="hidden" name="importe_da" id="importe_da" value="${importe_da}"/>
				<input type="hidden" name="mpPagoM" id="mpPagoM" value="${mpPagoM}" />
				<input type="hidden" name="mpPagoC" id="mpPagoC" value="${mpPagoC}" />	
				<input type="hidden" name="mpDomiAgro"	id="mpDomiAgro"	value="${mpDomiAgro}" />
				<input type="hidden" name="esPolPrincipal" id="esPolPrincipal" value="${esPolPrincipal}" />
				<input type="hidden" name="muestraBotonFinanciar" id="muestraBotonFinanciar" value="${muestraBotonFinanciar}"/>
				<input type="hidden" name="volverFormaPago" id="volverFormaPago" value="true" />				
		</form>
		
		<form name="frmCcOficina" action="consultaDetallePoliza.html" method="post" id="frmCcOficina">				 
				<input type="hidden" id="methodCcOficina" name="method" value="doVerPagos" />
				<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" />
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" />
				<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}" />
				<input type="hidden" name="formaDePago" id="formaDePago" value="${pagoPoliza.formapago}" />
				<input type="hidden" name="grProvisional" id="grProvisional" value="${grProvisional}" />
				<input type="hidden" name="numeroCuenta" id="numeroCuenta" value="${numeroCuenta}" />
				<input type="hidden" name="operacion" id="operacion" value="importes" />
				<input type="hidden" name="grProvisional" id="grProvisional" value="${grProvisional}" />
				<input type="hidden" name="origenllamada" id="origenllamada" value="${origenllamada}" />
				<input type="hidden" name="vieneDeImportes" id="vieneDeImportesComparativas" value="${vieneDeImportes}"/>
				<input type="hidden" name="cicloPoliza" id="cicloPoliza" value="${cicloPoliza}" />
				<%-- <input type="hidden" name="numero_da" id="numero_da" value="${numero_da}" />
				<input type="hidden" name="importe_da" id="importe_da" value="${importe_da}"/> --%>
		</form>
		
		<form name="validacionPagoCuenta" action="seleccionPoliza.html" method="post" id="validacionPagoCuenta">				 
				<input type="hidden" name="pctprimerpagoVal" id="pctprimerpagoval" value=""/>
				<input type="hidden" name="pctsegundopagoVal" id="pctsegundopagoVal" value=""/>
				<input type="hidden" name="fecha1Val" id="fecha1Val" value=""/>
				<input type="hidden" name="fechasegundopago1Val" id="fechasegundopago1Val" value=""/>
				<input type="hidden" name="oficinaVal" id="oficinaVal" value=""/>
		</form>
				
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaBanco.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<%@ include	file="/jsp/moduloPolizas/polizas/pago/popupConfirmacion.jsp"%>
	
	
</body>	
	
</html>