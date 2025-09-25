<%@ page language="java" import="java.util.*, com.rsi.agp.core.webapp.util.*"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Importe Póliza Complementaria</title>
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
		
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
		<script type="text/javascript" src="jsp/js/calendar.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>		
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>	
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/iban.js" ></script>	
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/importesCpl.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/datosAval.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/popupFinanciacion.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/popupFinanciacionAgroseguro.js"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
	   	

	</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3')">
			<%@ include file="/jsp/common/static/cabecera.jsp"%>
			<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
			<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="left">
					<a class="bot" href="#" id="btnParcelas" onclick="javascript:parcelas()" 
						style="display: none">Parcelas
					</a>
				</td>
				<td align="center">
					<!-- 	<a class="bot" href="#" id="btnFormaPago" onclick="javascript:showPopupFormaPago()" 
								style="display: none">Forma Pago
							</a>  -->
					<%-- <a class="bot" href="#" id="btnPagosLectura" onclick="javascript:datosPago()" style="display: none">C/c-Oficina</a>
					<a class="bot" href="#" id="btnPagos" 
						onclick="javascript:getModuleAndSave('noRevision','irAPagos','consulta_idpoliza','${idpoliza}', $('#importe').val());" 
						style="display: none">C/c-Oficina</a> --%>
						
					<a class="bot" href="#" id="btnFormaPagoLectura" onclick="javascript:showFormaPago(${idpolizaCpl})" style="display: none">Forma Pago</a>
					
					
					 
					<a class="bot" href="#" id="btnDescuentos" onclick="javascript:openPopupDescuentos();" style="display: none">Descuentos</a> 
					<a class="bot" href="#" id="btnRecargos" onclick="javascript:openPopupRecargos();" style="display: none">Recargos</a>
				</td>
				
				<!-- PET.70105.FII DNF 22/02/2021 eliminamos el boton de imprimir si la linea de contratacion es > 01/02/2021 -->
				<!-- ******* DNF INCIDENCIAS PET.70105.FII OCULTO EL BOTÓN DE IMPRIMIR		
				<c:if test="${lineaContrataSup2021 == 'false'}">	
					<td align="center">
						<a class="bot" id="btnImprimir" href="javascript:imprimir()" style="display: none">Imprimir</a>
					</td>
				</c:if>
				 ******* -->
							
				<td align="right">
				<!-- PET.70105.FII DNF 22/02/2021 eliminamos el boton grabacion provisional y confirmar poliza si la linea contratada es > 01/02/2021
				y creamos el nuevo continuar para las lineas contratadas > 01/02/2021 -->
					<!-- ******* DNF INCIDENCIAS PET.70105.FII OCULTAMOS EL BOTON DE GRAB.PROV Y EL DE CONFIRMAR POLIZA	
					<c:if test="${lineaContrataSup2021 == 'false'}">
					
						<a class="bot" id="btnGrabacionProvisional" href="javascript:grabacionProvisional();" style="display: none">Grabaci&oacute;n Provisional</a> 
						<a class="bot" id="btnGrabacionDefinitiva" href="javascript:ajax_muestraDatosAval('${idpolizaCpl}');" style="display: none">Confirmar P&oacute;liza</a>
						
					</c:if>
					 ******* -->
					 <!-- ******* DNF INCIDENCIAS PET.70105.FII SE APLICA PARA TODAS LAS LINEAS DE CONTRATACION
					<c:if test="${lineaContrataSup2021 == 'true'}">
						<a class="bot" href="#" id="btnContinuar" onclick="javascript:ajax_muestraDatosAval('${idpolizaCpl}');">Continuar</a>
					</c:if>
					 ******* -->
					<c:if test="${modoLectura != 'modoLectura'}">
						<a class="bot" href="#" id="btnContinuar" onclick="javascript:ajax_muestraDatosAval('${idpolizaCpl}');">Continuar</a>
					</c:if>
					
					<a class="bot" id="btnSalir" href="javascript:salir();" style="display: none">Salir</a> 
					<a class="bot" id="btnVolver" href="javascript:volver();" style="display: none">Volver</a>
				</td>
				
			</tr>
		</table>
	</div>	
	
	
	<div class="conten">
		<p class="titulopag" align="left">Importe Póliza Complementaria</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		<table align="right">
			<tr>
				<td>
					<a href="#" onclick="abrirInfoSubvenciones('${dataNifcif}', ${dataCodplan}, ${dataCodlinea});">
						<img alt="Detalle Asegurado" src="jsp/img/displaytag/detalle_asegurado.png">
					</a>
				</td>
				<td class="literal">IBAN Pago Prima:</td>
				<td colspan="3" class="detalI" id="numeroCuentaId">"${numeroCuenta}"</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td class="literal">IBAN Cobro Siniestros:</td>
				<td colspan="3" class="detalI" id="numeroCuentaId2">"${numeroCuenta2}"</td>
			</tr>
		</table>
		<br/>
		<br/>
		<form name="consultaDetallePoliza" id="consultaDetallePoliza" action="consultaDetallePoliza.html" method="post">
			<input type="hidden" name="method" id="method" value="doVerPagos" /> <input type="hidden" name="idpoliza" id="idpoliza" value="${idpolizaCpl}" /> <input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" /> <input type="hidden"
				name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}"
			/> <input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}" />
		</form>

		<form name="volver" method="post" id="volver" action="polizaComplementaria.html">
			<input type="hidden" id="idpolizaCpl" name="idpolizaCpl" value="${idpolizaCpl}" /> <input type="hidden" id="modoLecturaVolver" name="modoLectura" value="${modoLectura }" /> <input type="hidden" id="methodVolver" name="method" /> <input type="hidden"
				name="tieneSubvenciones" id="tieneSubvenciones" value="${tieneSubvenciones}"
			/>
		</form>

		<form action="grabacionPoliza.html" method="post" name="main" id="imprimirCpl">
			<input type="hidden" id="operacion" name="operacion" /> <input type="hidden" id="idpoliza" name="idpoliza" value="${idpolizaCpl }" />
		</form>

		<form action="utilidadesPoliza.html" method="post" name="volverUtilidadesPoliza" id="volverUtilidadesPoliza">
			<input type="hidden" id="recogerPolizaSesion" name="recogerPolizaSesion" value="true" />
		</form>

		<form:form name="importes" method="post" id="importes" action="webservicesCpl.html" commandName="fluxCondensator">
			<input type="hidden" id="method" name="method" value="doValidar" />
			<input type="hidden" id="idpoliza" name="idpoliza" value="${idpolizaCpl }" />
			<input type="hidden" id="origenllamada" name="origenllamada" value="pago" />
			<form:hidden path="primaComercial" />
			<form:hidden path="primaNeta" />
			<form:hidden path="costeNeto" />
			<form:hidden path="bonifAsegurado" />
			<form:hidden path="recargoAsegurado" />
			<form:hidden path="bonifMedidaPreventiva" />
			<form:hidden path="descuentoContColectiva" />
			<form:hidden path="pctBonifAsegurado" />
			<form:hidden path="pctRecargoAsegurado" />
			<form:hidden path="pctMedidaPreventiva" />
			<form:hidden path="pctDescContColectiva" />
			<form:hidden path="consorcioReaseguro" />
			<form:hidden path="consorcioRecargo" />
			<form:hidden path="importeTomador" />
			<form:hidden path="pctMinFinanSobreCosteTomador" />
			<input type="hidden" id="ocultarBtnGrabar" name="ocultarBtnGrabar" value="${ocultarBtnGrabar }" />
			<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}" />
			<input type="hidden" id="vieneDeUtilidades" name="vieneDeUtilidades" value="${vieneDeUtilidades}" />
			<input type="hidden" name="cicloPoliza" id="cicloPoliza" value="cicloPoliza" />
			<input type="hidden" name="validaRango" id="validaRango" value="${validaRango}" />
			<input type="hidden" name="descMaximo" id="descMaximo" value="${descMaximo}" />
			<input type="hidden" name="mpPagoM" id="mpPagoM" value="${mpPagoM}" />
			<input type="hidden" name="mpPagoC" id="mpPagoC" value="${mpPagoC}" />
			<input type="hidden" name="numeroCuenta" id="numeroCuenta" value="${numeroCuenta}" />
			<input type="hidden" name="numeroCuenta2" id="numeroCuenta2" value="${numeroCuenta2}" />
			<input type="hidden" name="importe1" id="importe1" value="${importe1}" />
			<input type="hidden" name="banDestino" id="banDestino" value="${banDestino}" />
			<input type="hidden" name="import" id="import" value="${import}" />
			<input type="hidden" name="fecPago" id="fecPago" value="${fecPago}" />
			<input type="hidden" name="metodoDePago" id="metodoDePago" value="${metodoDePago}" />
			<input type="hidden" name="plan" id="plan" value="${plan}" />
			<input type="hidden" name="muestraBotonDescuentos" id="muestraBotonDescuentos" value="${muestraBotonDescuentos}" />
			<input type="hidden" name="muestraBotonRecargos" id="muestraBotonRecargos" value="${muestraBotonRecargos}" />
			<input type="hidden" name="isPagoFraccionado" id="isPagoFraccionado" value="${isPagoFraccionado}" />
			<input type="hidden" name="grProvisionalOK" id="grProvisionalOK" value="${grProvisionalOK}" />
			<input type="hidden" name="muestraBotonFinanciar" id="muestraBotonFinanciar" value="${fluxCondensator.muestraBotonFinanciar}"/>
			<input type="hidden" name="esFraccAgr" id="esFraccAgr" value="${fluxCondensator.esFraccAgr}"/>
			<input type="hidden" name="periodoFracc" id="periodoFracc" value="${fluxCondensator.periodoFracc}"/>
			
			<fieldset style="overflow: hidden; margin-left: 4px;">
				<legend class="literal">MÓDULO ${fluxCondensator.idModulo} - ${ fluxCondensator.descModulo}</legend>
				<table class="conten" id="oculto" width="100%" style="border: 0px;" align="left">
					<div align="left" style="padding-left: 7px;">
						<a class="bot" href="#" id="btnDesglose" onclick="javascript:showdata(this);">Ocultar Desglose</a>
					</div>
					<tr>
						<td colspan="4">
							<table id="oculto" width="100%" style="border: 0px;" align="left">
								<thead>
									<tr>
										<c:choose>
											<c:when test="${plan < '2015'}">
												<th><img src="jsp/img/icono1.gif" alt="Desglose" title="Desglose" onclick="javascript:showdata('distrCostesCpl');" onmouseover="this.style.cursor='pointer';" /></th>
												<th class="literalbordeCabecera" style="">IMPORTES</th>
												<th class="literalbordeCabecera" style="">BONIF./DESC.</th>
												<th class="literalbordeCabecera" style="">CONSORCIO</th>
												<th class="literalbordeCabecera" style="">SUBV.ENESA</th>
												<th class="literalbordeCabecera" style="">SUBV.CCAA</th>
											</c:when>
											<c:otherwise>
												<th><img src="jsp/img/icono1.gif" alt="Desglose" title="Desglose" onclick="javascript:showdata('data${compCount}');" onmouseover="this.style.cursor='pointer';" /></th>
												<th class="literalbordeCabecera" style="">IMPORTES</th>
												<th class="literalbordeCabecera" style="">BONIFICACIÓN/RECARGO</th>
												<th class="literalbordeCabecera" style="">SUBV.ENESA</th>
												<th class="literalbordeCabecera" style="">SUBV.CCAA</th>
											</c:otherwise>
										</c:choose>
									</tr>
								</thead>
								<tbody>
									<c:choose>
										<c:when test="${plan < '2015'}">
											<tr id="distrCostesCpl">
												<td></td>
												<td valign="top" align="center">
													<table>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Prima Comercial:</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="primaComercial">${fluxCondensator.primaComercial}</form:label></td>
														</tr>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Prima Neta Bonif/Rec:</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="primaNeta">${fluxCondensator.primaNeta}</form:label></td>
														</tr>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Coste Neto:</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="costeNeto">${fluxCondensator.costeNeto}</form:label></td>
														</tr>
													</table>
												</td>
												<td valign="top" align="center">
													<table>
														<c:if test="${fluxCondensator.bonifAsegurado != 'N/D'}">
															<tr class="literalborde">
																<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Bonif. Aseg.<form:label path="pctBonifAsegurado">(${fluxCondensator.pctBonifAsegurado})</form:label>:
																</td>
																<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="bonifAsegurado">${fluxCondensator.bonifAsegurado}</form:label></td>
															</tr>
														</c:if>
														<c:if test="${fluxCondensator.recargoAsegurado != 'N/D'}">
															<tr class="literalborde">
																<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Recargo. Aseg.<form:label path="pctRecargoAsegurado">(${fluxCondensator.pctRecargoAsegurado})</form:label>:
																</td>
																<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="recargoAsegurado">${fluxCondensator.recargoAsegurado}</form:label></td>
															</tr>
														</c:if>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Bonif. M. Prev.<form:label path="pctMedidaPreventiva">(${fluxCondensator.pctMedidaPreventiva})</form:label>:
															</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="bonifMedidaPreventiva">${fluxCondensator.bonifMedidaPreventiva}</form:label></td>
														</tr>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Descuentos <form:label path="pctDescContColectiva">(${fluxCondensator.pctDescContColectiva})</form:label>:
															</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="descuentoContColectiva">${fluxCondensator.descuentoContColectiva}</form:label></td>
														</tr>
													</table>
												</td>
												<td valign="top" align="center">
													<table>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Reaseguro Consorcio:</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="consorcioReaseguro">${fluxCondensator.consorcioReaseguro}</form:label></td>
														</tr>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Recargo Consorcio:</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="consorcioRecargo">${fluxCondensator.consorcioRecargo}</form:label></td>
														</tr>
													</table>
												</td>
												<td valign="top" align="center">
													<table>
														<c:forEach var="subEnesa" items="${fluxCondensator.subvEnesa}">
															<tr class="literalborde">
																<td class="literalborde" align="left" style="border: 0px;">${subEnesa.key}:</td>
																<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;">${subEnesa.value}</td>
															</tr>
														</c:forEach>

													</table>
												</td>
												<td valign="top" align="center">
													<table>
														<c:forEach var="subCCAA" items="${fluxCondensator.subvCCAA}">
															<tr class="literalborde">
																<td class="literalborde" align="left" style="border: 0px;">${subCCAA.key}:</td>
																<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;">${subCCAA.value}</td>
															</tr>
														</c:forEach>
													</table>
												</td>
											</tr>
										</c:when>
										<c:otherwise>
											<!-- POLIZAS >= 2015 -->
											<tr id="distrCostesCpl">
												<td></td>
												<td valign="top" align="center">
													<table>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Prima Comercial:</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="primaComercial">${fluxCondensator.primaComercial}</form:label></td>
														</tr>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Prima Comercial Neta:</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="primaNeta">${fluxCondensator.primaNeta}</form:label></td>
														</tr>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Recargo Consorcio:</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="recargoConsorcio">${fluxCondensator.recargoConsorcio}</form:label></td>
														</tr>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Recibo Prima:</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="reciboPrima">${fluxCondensator.reciboPrima}</form:label></td>
														</tr>
														<tr class="literalborde">
															<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Coste Tomador:</td>
															<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="costeTomador">${fluxCondensator.costeTomador}</form:label></td>
														</tr>
													</table>
												</td>
												<td valign="top" align="center">
													<table>
														<c:forEach var="boniReca" items="${fluxCondensator.boniRecargo1}">
															<tr class="literalborde">
																<td class="literalborde" align="left" style="border: 0px;">${boniReca.key}:</td>
																<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;">${boniReca.value}</td>
															</tr>
														</c:forEach>
														<c:if test="${fluxCondensator.recargoAval ne 'N/D'}">
															<tr class="literalborde">
																<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Importe Recargo Aval:</td>
																<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="costeTomador">${fluxCondensator.recargoAval}</form:label></td>
															</tr>
															<tr class="literalborde">
																<td class="literalborde" align="left" style="border: 0px; white-space: nowrap;">Importe Recargo Fraccionamiento:</td>
																<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;"><form:label path="costeTomador">${fluxCondensator.recargoFraccionamiento}</form:label></td>
															</tr>
														</c:if>
													</table>
												</td>
												<td valign="top" align="center">
													<table>
														<c:forEach var="subEnesa" items="${fluxCondensator.subvEnesa}">
															<tr class="literalborde">
																<td class="literalborde" align="left" style="border: 0px;">${subEnesa.key}:</td>
																<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;">${subEnesa.value}</td>
															</tr>
														</c:forEach>

													</table>
												</td>
												<td valign="top" align="center">
													<table>
														<c:forEach var="subCCAA" items="${fluxCondensator.subvCCAA}">
															<tr class="literalborde">
																<td class="literalborde" align="left" style="border: 0px;">${subCCAA.key}:</td>
																<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;">${subCCAA.value}</td>
															</tr>
														</c:forEach>
													</table>
												</td>
										</c:otherwise>
									</c:choose>
								</tbody>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="4">
							<table id="tomador" width="100%" bgcolor="lightblue" style="border: solid 1px;">
								<tr>
									<td style="color: #FF0000; text-align: left; font-weight: bold;">NETO TOMADOR:</td>
									<c:choose>
										<c:when test="${fluxCondensator.periodoFracc==null}">
											<td style="color: #FF0000; text-align: right; font-weight: bold;">${fluxCondensator.importeTomador}</td>
										</c:when>
										<c:otherwise>
											<td style="color: #FF0000; text-align: right; font-weight: bold;">${fluxCondensator.totalCosteTomador}</td>
										</c:otherwise>
									</c:choose>
								</tr>
							</table>
							<!-- financiación agro >2015 -- No unificado -->
							<c:if test="${fluxCondensator.importePagoFracc != null && fluxCondensator.importePagoFracc != fluxCondensator.importeTomador}">
								<table id="tomadorFinanciado" width="100%" bgcolor="lightblue" style="border: solid 1px;">
									<!--  Sólo se muestra cuando hay posibilidad de financiación SAECA -->
									<tr>
										<td style="color: #FF0000; text-align: left; font-weight: bold;">NETO TOMADOR FINANCIADO (1ª Fracci&oacute;n):</td>
										<td id="impFraccAgr" style="color: #FF0000; text-align: right; font-weight: bold;">${fluxCondensator.importePagoFracc}</td>
									</tr>
								</table>
							</c:if>
							<table id="modulo" width="100%">
								<%-- <c:if test="${fluxCondensator.muestraBotonFinanciar == 'true'}"> --%>
									<tr>
										<td class="literal" align="right">
											<%-- <c:if test="${fluxCondensator.esFraccAgr == 'false'}">	 --%>											
											<!-- Financiación SAECA -->
												<%-- <c:if test="${modoLectura != 'modoLectura' && (fluxCondensator.periodoFracc == null)}"> --%>
													<a class="bot" href="#" id="btnFinanciar" style="display: none"
														 onclick="javascript:financiar('${fluxCondensator.totalCosteTomador}', '${fluxCondensator.comparativaSeleccionada}', '${fluxCondensator.pctMinFinanSobreCosteTomador}');">Financiar</a>
												<%-- </c:if> --%>
												<%-- <c:if test="${modoLectura == 'modoLectura'}"> --%>
													<!-- Se le pasa totalCosteTomador, puesto que vendrá relleno si se ha financiado -->
													<a class="bot" href="#" id="btnFinanciarLectura" style="display: none"
														onclick="javascript:financiarLectura('${fluxCondensator.costeTomador}', '${fluxCondensator.comparativaSeleccionada}', '${periodoFracc}', '${valorOpcionFracc}', '${opcionFracc}');">Financiar</a>
												<%-- </c:if> --%>
											<%-- </c:if>	 --%>							
										</td>
									</tr>
								<%-- </c:if> --%>
								
								<table align="left">
								<c:if test="${fluxCondensator.comMediadorE != 'N/D'}">
									<tr>
										<td class="literal">Comisión Entidad:</td><td class="detaldatoD"> ${fluxCondensator.comMediadorE}</td>
										<td class="literal">Total:</td><td class="detaldatoD"> ${fluxCondensator.comMediadorE}</td>
									</tr>
								</c:if>
								<c:if test="${fluxCondensator.comMediadorE_S != 'N/D'}">
									<tr>
										<td class="literal">Comisión E-S Mediadora:</td><td class="detaldatoD">  ${fluxCondensator.comMediadorE_S}</td>
										<td class="literal">Total:</td><td class="detaldatoD">  ${fluxCondensator.comMediadorE_S}</td>
									</tr>
								</c:if>
								</table>
							</table>
						</td>
					</tr>
					<c:if test="${perfil == 0}">
						<tr>
							<td align="right">								
								<div style="width:20%; text-align: right; margin-left: auto; margin-right: 5px" id="botonesXml" border=0>
									<a href="javascript:descargarXmlValidacion('${idpolizaCpl}')"')"alt="Descargar XML Validaci&oacute;n" title="Descargar XML Validaci&oacute;n" style="text-decoration: unset;">
										<img width="16" height="16" alt="Descargar XML Validaci&oacute;n"" title="Descargar XML Validaci&oacute;n" src="jsp/img/jmesa/csv.gif"/>
										<span style="color: gray; font-family: tahoma, verdana, arial; font-size: 11px; font-weight: bold; vertical-align: top" >
							 				XML Validaci&oacute;n
							 			</span>
									</a>
									&nbsp;
									<a href="javascript:descargarXmlCalculo('${idpolizaCpl}')"')" alt="Descargar XML C&aacute;lculo" title="Descargar XML C&aacute;lculo" style="text-decoration: unset;">
										<img width="16" height="16" alt="Descargar XML C&aacute;lculo" title="Descargar XML C&aacute;lculo" src="jsp/img/jmesa/csv.gif"/>
										<span style="color: gray; font-family: tahoma, verdana, arial; font-size: 11px; font-weight: bold; vertical-align: top" >
							 				XML C&aacute;lculo
							 			</span>
									</a>
								</div>
							</td>
						</tr>
					</c:if>
				</table>
			</fieldset>
		</form:form>
	</div>
	
	<!-- para botón de volver a la página de parcelas -->
	<form name="polizaCompl" id="polizaCompl" action="polizaComplementaria.html" method="post">	
		<input type="hidden" name="refPol" id="refPol" />	
		<input type="hidden" name="lineaseguroidCpl" id="lineaseguroidCpl" />
		<input type="hidden" name="idpolizaCpl" id="idpolizaCpl" value="${idpolizaCpl}"/>		
		<input type="hidden" name="method" id="method"/>
		<%-- <input type="text" name="idPol" id="idPol" value="${idpoliza}"/> --%>
		<input type="hidden" name="modoLecturaCpl" id="modoLecturaCpl" value=""/>
	</form>
	<!-- **************************************************************************************** -->
	
	<form method="post" name="formUtilidades" id="formUtilidades" action="utilidadesXML.run">
		<input type="hidden" name="method" id="methodUtl" />
		<input type="hidden" name="idPoliza" id="idPoliza" value="${idpolizaCpl}"/>	
		<input type="hidden" name="vieneDeCpl" id="vieneDeCpl" value="true"/>
	</form>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<div id="panelInfo" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;">
		<!--  header popup -->
		<div id="header-popup" style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
			<div style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Aviso</div>
			<a style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer"> <span onclick="cerrarPopUp()">x</span>
			</a>
		</div>
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="comparativasNoElegibles">Atencion! No hay subvenciones seleccionadas</div>
			</div>
			<div style="margin-top: 15px">
				<a class="bot" href="javascript:cerrarPopUp();" title="aceptar">Aceptar</a>
			</div>
		</div>
	</div>
	
<%@ include file="/jsp/moduloPolizas/polizas/importes/popupDescuentos.jsp"%>
<%@ include file="/jsp/moduloPolizas/polizas/importes/popupRecargos.jsp"%>
<%@ include file="/jsp/common/lupas/lupaBanco.jsp"%>
<%@ include file="/jsp/moduloPolizas/polizas/importes/popupFinanciacion.jsp"%>
<%@ include	file="/jsp/moduloPolizas/polizas/importes/popupFinanciacionAgroseguro.jsp"%>
<%@ include	file="/jsp/moduloPolizas/polizas/importes/popupDatosAval.jsp"%>
<%@ include file="/jsp/moduloPolizas/polizas/importes/controlAccesoSubvsAseg.jsp"%>

<c:if test="${fluxCondensator.opcionFracc == null}">
	<script>$('input:radio[name=opcion_cf]')[0].checked = true;</script>
</c:if>
<c:if test="${fluxCondensator.opcionFracc != null}">
	<script>
		$('input:radio[name=opcion_cf]')[${fluxCondensator.opcionFracc}].checked = true;
	</script>
	<c:if test="${fluxCondensator.opcionFracc == 0}">
		<script>$('#porcentajeCosteTomador_txt').val(${fluxCondensator.valorOpcionFracc});</script>
	</c:if>
	<c:if test="${fluxCondensator.opcionFracc == 1}">
		<script>$('#importeFinanciar_txt').val(${fluxCondensator.valorOpcionFracc});</script>
	</c:if>
	<c:if test="${fluxCondensator.opcionFracc == 2}">
		<script>$('#importeAval_txt').val(${fluxCondensator.valorOpcionFracc});</script>
	</c:if>
</c:if>

</body>
</html>