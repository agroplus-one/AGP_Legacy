<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<jsp:directive.page import="org.displaytag.*" />

<html>
<head>
<title>Comparativa de importes</title>
<%@ include file="/jsp/common/static/metas.jsp"%>

<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />

<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
<script type="text/javascript" src="jsp/js/iban.js"></script>
<script type="text/javascript" src="jsp/js/jquery.alerts.js"></script>

<script type="text/javascript" src="jsp/js/iban.js"></script>
<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/importes.js"></script>
<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/popupFinanciacion.js"></script>
<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/datosAval.js"></script>
<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/popupFinanciacionAgroseguro.js"></script>
<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/popupOficina.js"></script>

<%@ include file="/jsp/js/draggable.jsp"%>

<script src="jsp/moduloPolizas/modulos/elecmodulos.js"
	type="text/javascript"></script>

<style type="text/css">
.itemList {
	border: 1px solid #CCC;
	padding: 3px;
	margin-left: 20px;
	float: left;
	height: 50px
}

.a {
	float: left
}

.tituloFieldset {
	font-family: tahoma, verdana, arial;
	font-size: 11px;
	font-weight: bold;
	color: #626262;
	vertical-align: middle;
}

.contenImportes {
	background-color: #ffffff;
	border-width: 1px;
	border-style: solid;
	border-color: #E5E5E5;
	text-align: center;
	padding: 1px;
}

.literalbordeImportes {
	font-family: tahoma, verdana, arial;
	font-size: 11px;
	font-weight: bold;
	color: #626262;
	vertical-align: top;
	padding-top: 2px;
	padding-bottom: 2px;
	padding-left: 1px;
	background-color: #ffffff;
	border-width: 1px;
	border-style: solid;
	border-color: #004539;
}
</style>


<!-- <style type="text/css">
	.tituloFieldset {font-family: tahoma, verdana, arial; font-size: 11px;  font-weight: bold; color: #626262; vertical-align: middle;}
	.contenImportes {background-color: #ffffff; border-width: 1px; border-style: solid; border-color: #E5E5E5; text-align: center; padding:1px; }
	.literalbordeImportes {font-family: tahoma, verdana, arial; font-size: 11px;  font-weight: bold; color: #626262; vertical-align: top;padding-top: 2px;padding-bottom: 2px;padding-left: 1px;background-color: #ffffff; border-width: 1px; border-style: solid; border-color: #004539;}
</style>
 -->


<!-- Pasar a 'importes.js' -->
<script type="text/javascript">

function volverComparativas() {
	$('#formComparativas').submit();
}



</script>

</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="eligeMenu()">

	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<c:choose>
		<c:when
			test="${modoLectura == 'modoLectura' && vieneDeUtilidades == 'true'}">
			<%@ include file="/jsp/common/static/datosCabeceraConsulta.jsp"%>
		</c:when>
		<c:otherwise>
			<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
		</c:otherwise>
	</c:choose>

	<!-- Buttons -->
	<div id="buttons">
		<table width="100%" cellspacing="2" cellpadding="2" border="0">

			<tr align="left">

				<td align="left">
					<a class="bot" href="#" id="btnVolver" onclick="javascript:generales.enviarForm('consulta','consulta_idpoliza','${idpoliza}');">
						<c:if test="${isLineaGanado eq true}">Explotaciones</c:if> 
						<c:if test="${isLineaGanado eq false}">Parcelas</c:if>
					</a> 
					&nbsp;
					<!-- ESC-12910 DNF 16/03/2021 -->
					<c:if test="${modoLectura == 'modoLectura'}">
						<c:if test="${isFechaEnvioPosteriorSep2020 eq true}">
							<a class="bot" href="#" id="btnComparativas" onclick="javascript:volverComparativas();">Comparativas</a>
						</c:if> 
					</c:if>
					<!-- FIN ESC-12910 -->
				</td>

				<td align="center">
					<!-- <a class="bot" href="#" id="btnFormaPago" onclick="javascript:showPopupFormaPago()" style="display: none">Forma Pago</a> -->
					<a class="bot" href="#" id="btnFormaPagoLectura"
					onclick="javascript:showFormaPago()" style="display: none">Forma Pago</a>
					<!-- Pet. 54046 ** MODIF TAM (28.06.2018) ** Inicio --> <!-- Se añade un nuevo botón para la consulta/modificación de la oficina -->
					<!--  ESC-5979 (10.05.2019) Se oculta el botón de oficinaas si estamos en consulta -->
					<c:if test="${modoLectura != 'modoLectura'}">
						<a class="bot" href="#" id="btnOficina"	onclick="javascript:showOficinas();" >Oficina</a>
					</c:if>	
					<!-- Pet. 54046 ** MODIF TAM (28.06.2018) ** Inicio --> 
					<a class="bot" href="#" id="btnPagosLectura"
					onclick="javascript:datosPago()" style="display: none">C/c-Oficina</a>
					<%-- <a class="bot" href="#" id="btnPagos" onclick="javascript:getModuleAndSave('noRevision','irAPagos','consulta_idpoliza','${idpoliza}', $('#importe').val());" style="display: none">C/c-Oficina</a> --%>

					<c:if test="${isLineaGanado eq false}">
						<a class="bot" href="#" id="btnRevProdPrecio"
							onclick="javascript:getModuleAndSave('revision','grabar','consulta_idpoliza','${idpoliza}');"
							style="display: none">Revisi&oacute;n Prod/Precio</a>
					</c:if> <a class="bot" href="#" id="btnDescuentos"
					onclick="javascript:openPopupDescuentos();" style="display: none">Descuentos</a>
					<a class="bot" href="#" id="btnRecargos"
					onclick="javascript:openPopupRecargos();" style="display: none">Recargos</a>
				</td>

						
				<td align="center">
					<!-- ocultamos el botón para ganado hasta finalizar desarrollo -->
					<c:if test="${estadoPoliza == 1 }">
						<a class="bot" href="#" id="btnImprimirComp" onclick="javascript:imprimirComparativas();">Imprimir Comparativas</a>
					</c:if> 
					
					<!-- PET.70105.FII DNF 22/02/2021 eliminamos el boton imprimir e imprimir reducida si la linea de contratacion es > 01/02/2021-->
					<!-- ******* DNF INCIDENCIAS PET.70105.FII OCULTO LOS BOTONES DE IMPRIMIR E IMPRIMIR REDUCIDAS
					<c:if test="${lineaContrataSup2021 == 'false'}">	
					
						<a class="bot" href="#" id="btnImprimir" onclick="javascript:imprimir();" style="display: none">Imprimir</a>
						<c:if test="${not isLineaGanado}">
							<a class="bot" href="#" id="btnImprimirReducida" onclick="javascript:imprimirReducida();" style="display: none">Imprimir Reducida</a>
						</c:if>
						
					</c:if>	
					 ******* -->
				</td>
				<td align="right">
				
					<!-- PET.70105.FII DNF 22/02/2021 renombramos el boton confirmar poliza por continuar si la linea de contratacion es > 01/02/2021-->
					<!-- ******* DNF INCIDENCIAS PET.70105.FII SE APLICA PAR TODAS LAS LINEAS DE CONTRATACION
					c:if test="${lineaContrataSup2021 == 'true'}"	
					 *******-->		
					 <c:if test="${modoLectura != 'modoLectura'}">
						<a class="bot" href="#" id="btnContinuar"
						onclick="javascript:continuarPpal('${plan}','noRevision','continuar','consulta_idpoliza','${idpoliza}');"
						>Continuar</a>
					</c:if>	
					
				
					<!-- PET.70105.FII DNF 22/02/2021 eliminamos el boton grabacion provisional y de confirmar poliza si la linea de contratacion es > 01/02/2021-->
					<!-- ******* DNF INCIDENCIAS PET.70105.FII OCULTO LOS BOTONES DE GRABACION PROVISIONAL Y CONFIRMAR POLIZA
					<c:if test="${lineaContrataSup2021 == 'false'}">	
						
						<a class="bot" href="#" id="btnGrProvisional"
						onclick="javascript:getModuleAndSave('noRevision','grProvisional','consulta_idpoliza','${idpoliza}');"
						style="display: none">Grabaci&oacute;n Provisional</a>
					
					
						<a class="bot" href="#" id="btnGrDefinitiva"
						onclick="javascript:showPopUpAval('${idpoliza}', '${plan}');"
						style="display: none">Confirmar P&oacute;liza</a>
					</c:if>
					 ******* -->
				
					<a class="bot" href="javascript:salir();" id="btnSalir"
					style="display: none">Salir</a>
				</td>
			</tr>
		</table>
	</div>
	<!-- Contenido de la página -->
	<form name="consultaDetallePoliza" id="consultaDetallePoliza"
		action="consultaDetallePoliza.html" method="post">
		<input type="hidden" name="method" id="method" value="doVerPagos" />
		<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}" />
		<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" /> 
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" /> 
		<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}" /> 
		<input type="hidden" name="grProvisionalOK" id="grProvisionalOK" value="${grProvisionalOK}" /> 
		<input type="hidden" name="mpPagoM" id="mpPagoM" value="${mpPagoM}" /> 
		<input type="hidden" name="mpPagoC" id="mpPagoC" value="${mpPagoC}" /> 
		<input type="hidden" name="formaDePago" id="formaDePago" value="${formaDePago}" />
	</form>
	<form name="main" action="" method="post" id="main">
		<input type="hidden" name="operacion" id="operacion" value="editar" />
		<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura }" /> 
		<input type="hidden" name="idpoliza" id="consulta_idpoliza" value="${idpoliza}" /> 
		<input type="hidden" name="countImportes" id="countImportes" value="${countImportes}" />
		<input type="hidden" name="grProvisionalOK" id="grProvisionalOK" value="${grProvisionalOK}" />
	</form>
	<form name="grabar" action="webservices.html" method="post" id="grabar">
		<input type="hidden" name="operacion" id="operacion" value="grabar" />
		<input type="hidden" name="idpoliza" id="consulta_idpoliza" value="${idpoliza}" /> 
		<input type="hidden" name="modSeleccionado" id="modSeleccionado" value="" /> 
		<input type="hidden" name="noRevPrecioProduccion" id="noRevPrecioProduccion" value="true" />
		<input type="hidden" name="importeSeleccionado" id="importeSeleccionado" value="" /> 
		<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}" /> 
		<input type="hidden" name="origenllamada" id="origenllamada" value="pago" />
		<input type="hidden" name="grProvisionalOK" id="grProvisionalOK" value="${grProvisionalOK}" />
	</form>
	<form name="irAPagos" action="webservices.html" method="post" id="irAPagos">
		<input type="hidden" name="operacion" id="operacion" value="grabar" />
		<input type="hidden" name="idpoliza" id="consulta_idpoliza" value="${idpoliza}" /> 
		<input type="hidden" name="modSeleccionado" id="modSeleccionado" value="" /> 
		<input type="hidden" name="noRevPrecioProduccion" id="noRevPrecioProduccion" value="" />
		<input type="hidden" name="importeSeleccionado" id="importeSeleccionado" value="" /> 
		<input type="hidden" name="revPagos" id="revPagos" value="true" /> 
		<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}" /> 
		<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" /> 
		<input type="hidden" name="origenllamada" id="origenllamada" value="pago" /> 
		<input type="hidden" name="grProvisionalOK" id="grProvisionalOK" value="${grProvisionalOK}" /> 
		<input type="hidden" name="esAgrSend" id="esAgrSend" value="" /> 
		<input type="hidden" name="esSaecaVal" id="esSaecaVal" value="" />
	</form>
	<form name="grProvisional" action="webservices.html" method="post" id="grProvisional">
		<input type="hidden" name="operacion" id="operacion" value="grabar" />
		<input type="hidden" name="idpoliza" id="consulta_idpoliza" value="${idpoliza}" /> 
		<input type="hidden" name="modSeleccionado" id="modSeleccionado" value="" /> 
		<input type="hidden" name="noRevPrecioProduccion" id="noRevPrecioProduccion" value="" />
		<input type="hidden" name="importeSeleccionado" id="importeSeleccionado" value="" /> 
		<input type="hidden" name="grabacionProvisional" id="grabacionProvisional" value="true" />
		<input type="hidden" name="grProvisionalOK" id="grProvisionalOK" value="${grProvisionalOK}" />
		<input type="hidden" name="pagosIncompletos" id="pagosIncompletos" value="${pagosIncompletos}" /> 
		<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}" /> 
		<input type="hidden" name="origenllamada" id="origenllamada" value="pago" /> 
		<input type="hidden" name="netoTomadorFinanciadoAgr" id="netoTomadorFinanciadoAgr" />
		<input type="hidden" name="enviarIBANFinanciadoAgr" id="enviarIBANFinanciadoAgr" />
		<input type="hidden" name="pctRecargoFinanciadoAgr" id="pctRecargoFinanciadoAgr" />
		<input type="hidden" name="totalCosteTomadorAFinanciar_grP" id="totalCosteTomadorAFinanciar_grP" />
		<input type="hidden" name="selectedSumaAseg" id="selectedSumaAseg" value="${selectedSumaAseg}" />
		<input type="hidden" name="fechaEfectoRC" id="fechaEfectoRC" value="${fechaEfectoRC}" />
	</form>
	<form name="consulta" action="seleccionPoliza.html" method="post"
		id="consulta">
		<input type="hidden" name="operacion" id="operacion" value="listParcelas" /> 
		<input type="hidden" name="idpoliza" id="consulta_idpoliza" value="${idpoliza}" /> 
		<input type="hidden" name="cambioProvisional" id="cambioProvisional" value="true" /> 
		<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" /> 
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" />
	</form>
	<!-- MPM 03/05/12 -->
	<form name="pasarADefinitiva" id="pasarADefinitiva" action="pasoADefinitiva.html" method="post" commandName="polizaDefinitiva">
		<input type="hidden" name="method" id="method" value="doPasarADefinitiva" /> 
		<form:hidden path="polizaDefinitiva.idpoliza" id="idpoliza" />
		<input type="hidden" name="resultadoValidacion" id="resultadoValidacion" /> 
		<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion" /> 
		<input type="hidden" name="actualizarSbp" id="actualizarSbp" /> 
		<input type="hidden" name="cicloPoliza" id="cicloPoliza" value="cicloPoliza" />
		<input type="hidden" name="imprimirReducida" id="imprimirReducida" />
		<input type="hidden" name="numeroAval" id="numeroAval" value="" /> 
		<input type="hidden" name="importeAval" id="importeAval" value="" /> 
		<input type="hidden" name="esSaecaDef" id="esSaecaDef" value="" />
	</form>
	<!-- Formulario para volver a la pantalla de comparativas - Sólo para pólizas de ganado -->
	<form:form method="post" name="formComparativas" id="formComparativas" action="seleccionComparativasSW.html" commandName="polizaDefinitiva">
		<form:hidden path="idpoliza" id="idpolizaComparativas" />
		<input type="hidden" name="vieneDeImportes" id="vieneDeImportesComparativas" value="true" />
		<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" />
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" />
	</form:form>

	<div class="conten">
		<!-- DAA 03/05/12 -->
		<p class="titulopag" align="left">Comparativa de Importes</p>
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
				<td colspan="3" class="detalI" id="numeroCuentaId2">"${empty numeroCuenta2 ? numeroCuenta : numeroCuenta2}"</td>
			</tr>
		</table>

		<form name="importes" method="post" id="importes">

			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}" /> 
			<input type="hidden" id="method" name="method" value="" />
			<input type="radio" id="modElegido" name="modElegido" value="" style="display: none"/>
			<input type="hidden" name="tieneSubvenciones" id="tieneSubvenciones" value="${tieneSubvenciones}" />
			<!-- Algunos hidden se crean porque son necesarios para recuperar la distribución de costes para generar el informe de comparativas -->
			<input type="hidden" name="displaypopUpAmbCont" id="displaypopUpAmbCont" value="${popUpAmbiCont}" /> 
			<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura }" /> 
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" /> 
			<input type="hidden" name="externo" id="externo" value="${externo}" /> 
			<input type="hidden" name="numeroCuenta" id="numeroCuenta" value="${numeroCuenta}" /> 
			<input type="hidden" name="numeroCuenta2" id="numeroCuenta2" value="${numeroCuenta2}" /> 
			<input type="hidden" name="grProvisional" id="grProvisional" value="${grProvisional}" /> 
			<input type="hidden" name="grDefinitiva" id="grDefinitiva" value="${grDefinitiva}" /> 
			<input type="hidden" name="grDefinitivaKO" id="grDefinitivaKO" value="${grDefinitivaKO}" /> 
			<input type="hidden" name="grProvisionalKO" id="grProvisionalKO" value="${grProvisionalKO}" />
			<input type="hidden" name="mpPagoM" id="mpPagoM" value="${mpPagoM}" />
			<input type="hidden" name="mpPagoC" id="mpPagoC" value="${mpPagoC}" />
			<input type="hidden" name="pefil" id="pefil" value="${pefil}" /> 
			<input type="hidden" name="fechaPagoString" id="fechaPagoString" value="${fechaPagoString}" /> 
			<input type="hidden" name="tipoPagoGuardado" id="tipoPagoGuardado" value="${tipoPagoGuardado}" /> 
			<input type="hidden" name="plan" id="plan" value="${plan}" /> 
			<input type="hidden" name="muestraBotonDescuentos" id="muestraBotonDescuentos" value="${muestraBotonDescuentos}" /> 
			<input type="hidden" name="muestraBotonRecargos" id="muestraBotonRecargos" value="${muestraBotonRecargos}" /> 
			<input type="hidden" name="descMaximo" id="descMaximo" value="${descMaximo}" /> 
			<input type="hidden" name="validaRango" id="validaRango" value="${validaRango}" /> 
			<input type="hidden" name="validComps" 	id="validComps" value="${validComps}" /> 
			<input type="hidden" name="isPagoFraccionado" id="isPagoFraccionado" value="${isPagoFraccionado}" /> 
			<input type="hidden" name="importe1" id="importe1" value="${importe1}" />
			
			<input type="hidden" name="fechaEfectoId.day" value="" />
			<input type="hidden" name="fechaEfectoId.month" value="" />
			<input type="hidden" name="fechaEfectoId.year" value="" />
			
			<input type="hidden" name="filaComparativa" id="filaComparativa" />

			<c:forEach items="${resultado}" var="object">
				<c:set var="compCount" value="${compCount + 1}" />

				<input type="hidden" name="idModulo${compCount}" id="idModulo${compCount}" value="${object.idModulo}" />
				<input type="hidden" name="descModulo${compCount}" id="descModulo${compCount}" value="${object.descModulo}" />
				<input type="hidden" name="esAgr" id="esAgr" value="${object.esFraccAgr}" />
				<input type="hidden" name="pctMinFinanc${compCount}" id="pctMinFinanc${compCount}" value="${object.pctMinFinanSobreCosteTomador}" />

				<fieldset style="width:97%">
					<legend class="literal">M&Oacute;DULO ${object.idModulo} - ${object.descModulo}</legend>

					${object.comparativaCompleta}

					<table width="100%">
						<tr>
							<td colspan="8"><%@ include
									file="/jsp/moduloPolizas/polizas/importes/importesGN.jsp"%></td>
						</tr>
						<tr>
							<td colspan="8"><c:if test="${object.esFraccAgr == 'false'}">
									<table id="tomador" width="100%" bgcolor="lightblue"
										style="border: solid 1px;">

										<tr>
											<td
												style="color: #FF0000; text-align: left; font-weight: bold;">NETO
												TOMADOR :</td>

											<c:choose>
												<c:when test="${object.periodoFracc == null}">
													<td
														style="color: #FF0000; text-align: right; font-weight: bold;">${object.importeTomador}</td>
													<input type="hidden" name="importeC${compCount}"
														id="importeC${compCount}" value="${object.importeTomador}" />
												</c:when>
												<c:otherwise>
													<td
														style="color: #FF0000; text-align: right; font-weight: bold;">${object.totalCosteTomador}</td>
													<input type="hidden" name="importeC${compCount}"
														id="importeC${compCount}"
														value="${object.totalCosteTomador}" />
												</c:otherwise>
											</c:choose>

											<input type="hidden" id="idEnv${compCount}"
												name="idEnv${compCount}" value="${object.idEnvioComp}">
										</tr>
									</table>
								</c:if> <c:if test="${object.esFraccAgr == 'true'}">

									<table id="tomador" width="100%" bgcolor="lightblue"
										style="border: solid 1px;">
										<tr>
											<td
												style="color: #FF0000; text-align: left; font-weight: bold;">NETO
												TOMADOR :</td>
											<td
												style="color: #FF0000; text-align: right; font-weight: bold;">${object.importeTomador}</td>
											<input type="hidden" name="importeC${compCount}"
												id="importeC${compCount}" value="${object.importeTomador}" />
											<input type="hidden" id="idEnv${compCount}"
												name="idEnv${compCount}" value="${object.idEnvioComp}">
										</tr>
									</table>
								</c:if> <c:if
									test="${object.muestraBotonFinanciar == 'true' && object.esFraccAgr == 'true'}">

									<input type="hidden"
										name="netoTomadorFinanciadoAgr${compCount}"
										id="netoTomadorFinanciadoAgr${compCount}" />
									<input type="hidden" name="enviarIBANFinanciadoAgr${compCount}"
										id="enviarIBANFinanciadoAgr${compCount}" />
									<table id="tomadorFinanciado${compCount}" width="100%"
										bgcolor="lightblue" style="border: solid 1px; display: none;">
										<!--  Sólo se muestra cuando hay posibilidad de financiación con Agroseguro -->
										<tr>
											<td
												style="color: #FF0000; text-align: left; font-weight: bold;">NETO
												TOMADOR FINANCIADO (1ª Fracci&oacute;n):</td>
											<td id="impFraccAgr${compCount}"
												style="color: #FF0000; text-align: right; font-weight: bold;"></td>
										</tr>
									</table>
								</c:if> <!-- Distribución de costes unificada (de momento solo ganado) -->
								<c:if
									test="${object.importePagoFraccAgr != null && object.importePagoFraccAgr != object.importeTomador}">

									<table id="tomadorFinanciado${compCount}" width="100%"
										bgcolor="lightblue" style="border: solid 1px;">
										<!--  Sólo se muestra cuando hay posibilidad de financiación con Agroseguro -->
										<tr>
											<td
												style="color: #FF0000; text-align: left; font-weight: bold;">NETO
												TOMADOR FINANCIADO (1ª Fracci&oacute;n):</td>
											<td id="impFraccAgr${compCount}"
												style="color: #FF0000; text-align: right; font-weight: bold;">${object.importePagoFraccAgr}</td>
										</tr>
									</table>
								</c:if> <!-- financiación agro >2015 -- No unificado --> <c:if
									test="${object.importePagoFracc != null && object.importePagoFracc != object.importeTomador}">

									<table id="tomadorFinanciado${compCount}" width="100%"
										bgcolor="lightblue" style="border: solid 1px;">
										<!--  Sólo se muestra cuando hay posibilidad de financiación SAECA -->
										<tr>
											<td
												style="color: #FF0000; text-align: left; font-weight: bold;">NETO
												TOMADOR FINANCIADO (1ª Fracci&oacute;n):</td>
											<td id="impFraccAgr${compCount}"
												style="color: #FF0000; text-align: right; font-weight: bold;">${object.importePagoFracc}</td>
										</tr>
									</table>
								</c:if>


								<table id="modulo" width="100%">
									<tr>
										<c:if test="${isLineaGanado eq false}">
											<td class="literal" align="left">Admite Complemento:
												${object.admiteComplementario}</td>
										</c:if>
										<!-- esto se pone para que la pantalla salga igual en agro y en ganado -->
										<c:if test="${isLineaGanado eq true}">
											<td class="literal" align="left">&nbsp;</td>
										</c:if>
										<!-- si se quita, el botón de fianaciar cambia de posicion en las de ganado-->
										<input type="hidden" name="modElegido${compCount}"
											id="modElegido${compCount}"
											value="${object.comparativaSeleccionada}" />


										<c:if test="${object.muestraBotonFinanciar == 'true'}">
											<td align="right"><c:if
													test="${object.esFraccAgr == 'false'}">
													<input type="hidden"
														name="totalCosteTomadorAFinanciar${compCount}"
														id="totalCosteTomadorAFinanciar${compCount}"
														value="${object.totalCosteTomadorAFinanciar}" />
													<!-- Financiación SAECA -->
													<c:if
														test="${modoLectura != 'modoLectura' && object.periodoFracc == null && grProvisionalOK!='true'}">
														<a class="bot" href="#"
															id="btnFinanciar_${object.comparativaSeleccionada}"
															onclick="javascript:financiar('${object.totalCosteTomadorAFinanciar}', '${object.comparativaSeleccionada}', '${object.pctMinFinanSobreCosteTomador}');">Financiar</a>
													</c:if>
													<c:if
														test="${modoLectura == 'modoLectura' || grProvisionalOK=='true'}">
														<a class="bot" href="#" id="btnFinanciarLectura"
															onclick="javascript:financiarLectura('${object.importeTomador}', '${object.comparativaSeleccionada}', '${periodoFracc}', '${valorOpcionFracc}', '${opcionFracc}');">Financiar</a>
													</c:if>
												</c:if> <c:if test="${object.esFraccAgr == 'true'}">
													<!-- Financiación Agroseguro -->
													<a class="bot" href="#" id="btnFinanciar"
														onclick="javascript:financiarAgr('${object.importeTomador}', '${object.pctRecargoFraccAgr}', '${compCount}');">Financiar</a>
												</c:if></td>
										</c:if>
										<c:if test="${countImportes ==1}">
											<td class="literal" style="align: right; width: 11%">Elegir
												Módulo<input type="radio" id="modElegido" name="modElegido"
												checked="true" value="${object.comparativaSeleccionada}">
											</td>
										</c:if>
										<c:if test="${countImportes >=2}">
											<td class="literal" style="align: right; width: 11%">Elegir
												Módulo <input type="radio" id="modElegido" name="modElegido"
												value="${object.comparativaSeleccionada}">
											</td>
										</c:if>
									</tr>
									<c:if test="${isLineaGanado eq false}">
										<tr>
											<td class="literal" align="left">Producci&oacuten:
												${object.totalProduccion}</td>
										</tr>
										<tr>
											<td class="literal" align="left">Total superficie:
												${sessionScope.superficieTotal}</td>
										</tr>
									</c:if>
									<tr id="comisGn">
										<table align="left" width="100%" style="margin-bottom: 20px;">
											<%@ include
												file="/jsp/moduloPolizas/polizas/importes/tablaDesgloseComisiones.jsp"%>
										</table>
									</tr>

								</table>
							</td>
						</tr>
						<c:if test="${perfil == 0}">
							<tr>
								<td align="right">				
									<div style="width:20%; text-align: right; margin-left: auto; margin-right: 5px" id="botonesXml" border=0>
										<a href="javascript:descargarXmlValidacion('${object.comparativaSeleccionada}', '${isLineaGanado}', '${idpoliza}')"')" alt="Descargar XML Validaci&oacute;n" title="Descargar XML Validaci&oacute;n" style="text-decoration: unset;">
											<img width="16" height="16" alt="Descargar XML Validaci&oacute;n"" title="Descargar XML Validaci&oacute;n" src="jsp/img/jmesa/csv.gif"/>
											<span style="color: gray; font-family: tahoma, verdana, arial; font-size: 11px; font-weight: bold; vertical-align: top" >
								 				XML Validaci&oacute;n
								 			</span>
										</a>
										&nbsp;
										<a href="javascript:descargarXmlCalculo('${object.comparativaSeleccionada}', '${isLineaGanado}', '${idpoliza}')" alt="Descargar XML C&aacute;lculo" title="Descargar XML C&aacute;lculo" style="text-decoration: unset;">
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
			</c:forEach>
			
			<c:if test="${isLineaGanado eq true and not empty polizaRC}">
				<fieldset style="overflow: hidden; margin-left: 4px; border: 1px solid #4682B4;">
					<legend class="literal">RC de Ganado</legend>
					<table style="width:100%; text-align:center;">
						<tr>
							<td width="30%" valign="top">
								<fieldset style="overflow: hidden; margin-left: 4px;">
									<legend class="literal">Datos para el cálculo</legend>
									<table class="contenImportes" width="100%" style="border: 0px;" align="left" cellpadding="0" cellspacing="1">
										<thead>
											<tr>
												<th class="literalbordeCabecera">Especie para RC</th>
												<th class="literalbordeCabecera">Régimen para RC</th>
												<th class="literalbordeCabecera">Nº Animales</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td class="literalborde">${polizaRC.especiesRC.descripcion}</td>
												<td class="literalborde">${polizaRC.regimenRC.descripcion}</td>
												<td class="literalborde">${polizaRC.numanimales}</td>
											</tr>
										</tbody>
									</table>
								</fieldset>
							</td>
							<td width="30%" valign="top">
								<fieldset style="overflow: hidden; margin-left: 4px;">
									<legend class="literal">Resultado de la simulación</legend>
									<table class="contenImportes" width="100%" style="border: 0px;" align="left" cellpadding="0" cellspacing="1">
										<thead>
											<tr>
												<th class="literalbordeCabecera" style="width: 130px;">Suma Asegurada</th>
												<th class="literalbordeCabecera" style="width: 75px;">Importe</th>
												<th class="literalbordeCabecera" style="width: 25px;">&nbsp;</th>
											</tr>
										</thead>
										<tbody>
											<c:if test="${modoLectura == 'modoLectura'}">
												<tr>
													<td class="literalborde">${polizaRC.sumaAseguradaFrmtd} &euro;</td>
													<td class="literalborde">${polizaRC.importeFrmtd} &euro;</td>
													<td class="literalborde"><input type="radio" checked="checked" disabled="disabled"/></td>
												</tr>
											</c:if>
											<c:if test="${modoLectura != 'modoLectura'}">
												<c:if test="${polizaRC.estadosRC.id == 0}">
													<c:forEach items="${sumasAseguradas}" var="sumaAsegurada">
														<tr>
															<td class="literalborde">${sumaAsegurada.sumaAseguradaFrmtd} &euro;</td>
															<td class="literalborde">${sumaAsegurada.importeFrmtd} &euro;</td>
															<td class="literalborde">
																<c:if test="${radioSumaAseg != sumaAsegurada.codSumaAsegurada}">
																	<input type="radio" id="radioSumaAseg" name="radioSumaAseg" value="${sumaAsegurada.codSumaAsegurada}"/>
																</c:if>
																<c:if test="${radioSumaAseg == sumaAsegurada.codSumaAsegurada}">
																	<input type="radio" id="radioSumaAseg" name="radioSumaAseg" value="${sumaAsegurada.codSumaAsegurada}" checked="checked"/>
																</c:if>
															</td>
														</tr>
													</c:forEach>
													<tr>
														<td class="literalborde" colspan="2">No elegir ninguna</td>
														<td class="literalborde"><input type="radio" id="radioSumaAseg" name="radioSumaAseg" value="-1"/></td>
													</tr>
												</c:if>
												<c:if test="${polizaRC.estadosRC.id != 0}">
													<tr>
														<td class="literalborde">${polizaRC.sumaAseguradaFrmtd} &euro;</td>
														<td class="literalborde">${polizaRC.importeFrmtd} &euro;</td>
														<td class="literalborde"><input type="radio" checked="checked" disabled="disabled"/></td>
													</tr>
												</c:if>
											</c:if>
										</tbody>
									</table>
								</fieldset>
							</td>
 							<td width="15%" valign="top">
								<fieldset style="overflow: hidden; margin-left: 4px;">
									<legend class="literal">Fecha de Efecto</legend>
									<table>
										<tbody>
											<tr align="left">
												<c:if test="${modoLectura == 'modoLectura'}">
													<input type="text" id="fechaEfectoId_fijo" name="fechaEfectoId_fijo" class="dato" size="11" maxlength="10" readonly="readonly" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaRC.fechaEfecto}" />"/>
												</c:if>
												<c:if test="${modoLectura != 'modoLectura'}">
													<c:if test="${polizaRC.estadosRC.id != 0}">
														<input type="text" id="fechaEfectoId_fijo" name="fechaEfectoId_fijo" class="dato" size="11" maxlength="10" readonly="readonly" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaRC.fechaEfecto}" />"/>
													</c:if>
													<c:if test="${polizaRC.estadosRC.id == 0}">
														<script>
															$(document).ready(function(){
																Zapatec.Calendar.setup({
															        firstDay          : 1,
															        weekNumbers       : false,
															        showOthers        : true,
															        showsTime         : false,
															        timeFormat        : "24",
															        step              : 2,
															        range             : [1900.01, 2999.12],
															        electric          : false,
															        singleClick       : true,
															        inputField        : "fechaEfectoId",
															        button            : "btn_fechaEfectoId",
															        ifFormat          : "%d/%m/%Y",
															        daFormat          : "%d/%m/%Y",
															        align             : "Br"			        	        
															  	});
															});
														</script>
														<td>
															<input type="text" id="fechaEfectoId" name="fechaEfectoId" class="dato" size="11" maxlength="10" onchange="if (!ComprobarFecha(this, document.importes, 'Fecha Fecha de Efecto')) this.value='';" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaRC.fechaEfecto}" />"/>
														</td>
														<td>
															<a id="btn_fechaEfectoId" name="btn_fechaEfectoId"><img src="jsp/img/calendar.gif"/></a>
														</td>
													</c:if>
												</c:if>
											</tr>
										</tbody>
									</table>
								</fieldset>
							</td>
							<td width="25%" valign="middle">
								<c:if test="${polizaRC.estadosRC.id != 0}">
									<a class="bot" href="javascript:imprimirPolizaRC();" id="btnImprimirCondiciones">Imprimir</a>
									<a class="bot" href="javascript:descargarCondicionesRC();" id="btnImprimirCondiciones">Imprimir Condiciones</a>
								</c:if>
							</td>
						</tr>
					</table>			
				</fieldset>		
			</c:if>
								
			<input type="hidden" name="numcomparativas" id="numcomparativas"
				value="${compCount}" />
		</form>
	</div>
	
	<form action="documentacionAgroseguro.run" name="imprimirCondicionesRC" id ="imprimirCondicionesRC" method="post">
		<input type="hidden" id="method" name="method"  value="doDescargarCondicionesRC">
		<input type="hidden" id="especieRC" name="especieRC" value="${polizaRC.especiesRC.codespecie}">
		<input type="hidden" id="plan" name="plan"  value="${polizaRC.poliza.linea.codplan}">
	</form>
	
	<form action="pasoADefinitiva.html" name="imprimirPolizaRC" id ="imprimirPolizaRC" method="post">
		<input type="hidden" id="method" name="method"  value="doImprimirPolizaRC">
		<input type="hidden" id="idPolizaRC" name="idPolizaRC" value="${polizaRC.id}">
	</form>
	
	<form method="post" name="formUtilidades" id="formUtilidades" action="utilidadesXML.run">
		<input type="hidden" name="method" id="methodUtl" />
		<input type="hidden" name="idPoliza" id="idPoliza" value="${idpoliza}"/>
		<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" />
		<input type="hidden" name="filaComparativa" id="filaComparativa"/>
		<input type="hidden" name="operacion" id="operacion" value="validar" />
	</form>

	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%-- <%@ include	file="/jsp/moduloPolizas/polizas/importes/popupFormaPago.jsp"%> --%>
	<%@ include file="/jsp/common/lupas/lupaBanco.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>


	<div id="panelInfo" class="parcelasRepWindow"
		style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em;">
		<!--  header popup -->
		<div id="header-popup"
			style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
			<div
				style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Aviso</div>
			<a
				style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="cerrarPopUp()">x</span>
			</a>
		</div>
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="comparativasNoElegibles">Atencion! No hay
					subvenciones seleccionadas</div>
			</div>
			<div style="margin-top: 15px">
				<!-- <a class="bot" href="javascript:cerrarPopUp();" title="Cancelar">Cancelar</a> -->
				<!-- <a class="bot" href="javascript:continuar();" title="Continuar">Continuar</a> -->
				<a class="bot" href="javascript:cerrarPopUp();" title="aceptar">Aceptar</a>
			</div>
		</div>
	</div>
	<!--               -->
	<!-- POPUPS AVISO  -->
	<!--               -->

	<!-- *** popUp pasar definitiva icon row *** -->
	<div id="popUpAvisos" class="parcelasRepWindow"
		style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; z-index: 1005">
		<!--  header popup -->
		<div id="header-popup"
			style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
			<div
				style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Aviso</div>
			<a
				style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="hidePopUpAviso()">x</span>
			</a>
		</div>
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="txt_mensaje_aviso_1">sin mensaje.</div>
			</div>
			<div style="margin-top: 15px">
				<a class="bot" href="javascript:hidePopUpAviso()" title="Cancelar">Cancelar</a>
				<a class="bot" id="btn_aceptarGrabacionDefinitiva"
					href="javascript:aceptarPopUpPasoDefinitiva()" title="Cancelar">Aceptar</a>
			</div>
		</div>
	</div>
	<!-- *** popUp ámbito contratación *** -->
	<div id="popUpAmbitoContratacion" class="parcelasRepWindow"
		style="color: #333333; width: 700px; max-height: 400px; height: expression(this.scrollHeight > 400 ? '400 px ' : 'auto'); overflow: auto; left: 300px; padding: 3px">

		<!-- header -->
		<div id="header-popup"
			style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; background: #525583; height: 15px">
			<div
				style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">Aviso</div>
			<a
				style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
				<span onclick="closePopUpAmbitoCont()">x</span>
			</a>
		</div>
		<br />

		<!-- body -->
		<c:if test="${pintarTablaError == 'S'}">
			<div style="">${sessionScope.tableInfoNoDefinitiva}</div>
		</c:if>

		<!-- buttons -->
		<div style="margin-top: 10px; text-align: center;">
			<a href="javascript:closePopUpAmbitoCont();" class="bot">Cerrar</a>
			<c:if test="${perfil == 0}">
				<a href="javascript:grabarDefFueraContratacion();" class="bot">Forzar
					paso a definitiva</a>
			</c:if>
		</div>


	</div>

	<%@ include	file="/jsp/moduloPolizas/polizas/importes/popupDescuentos.jsp"%>
	<%@ include file="/jsp/moduloPolizas/polizas/importes/popupOficinas.jsp" %>
	<%@ include file="/jsp/moduloPolizas/polizas/importes/popupRecargos.jsp"%>
	<%@ include file="/jsp/moduloPolizas/polizas/importes/popupFinanciacion.jsp"%>
	<%@ include file="/jsp/moduloPolizas/polizas/importes/popupFinanciacionAgroseguro.jsp"%>
	<%@ include file="/jsp/moduloPolizas/polizas/importes/popupDatosAval.jsp"%>
	<%@ include file="/jsp/moduloPolizas/polizas/importes/controlAccesoSubvsAseg.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>


</body>
</html>