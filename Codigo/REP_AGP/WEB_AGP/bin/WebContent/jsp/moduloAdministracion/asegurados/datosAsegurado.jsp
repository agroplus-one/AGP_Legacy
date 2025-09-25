<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>
<fmt:bundle basename="agp" >
	<c:set var="numEle"><fmt:message key="visores.numElements"/></c:set>
</fmt:bundle>

<html>
	<head>
		<title>Asegurado - Datos adicionales</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
		
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js" ></script>
		<script type="text/javascript" src="jsp/js/iban.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		<script type="text/javascript" src="jsp/moduloAdministracion/asegurados/datosAsegurado.js" charset="UTF-8" ></script>
		
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript" charset="UTF-8">
			// Para evitar el cacheo de peticiones al servidor
	        $(document).ready(function(){
	          	<c:if test="${empty listaDatosAsegurado}">
					//$('#btnConsultar').hide();
					//$('#btnVolver').hide();
					$('#btnModificar').hide();
					$('#btnAlta').show();
					
				 </c:if>
			});
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"
			onload="SwitchMenu('sub2'); javascript:generales.separaCuenta('${datoAseguradoBean.ccc }'); javascript:generales.separaCuenta('', '${datoAseguradoBean.ccc2 }');">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		
		<c:if test="${not empty asegurado }">
			<table style="border:1px solid black" width="100%"><TR>
				<c:if test="${asegurado.tipoidentificacion eq 'CIF' }">
					<td class="literal">Asegurado: ${asegurado.razonsocial }</td>
				    <td class="literal">CIF: ${asegurado.nifcif }</td>	
				</c:if>
				<c:if test="${asegurado.tipoidentificacion eq 'NIF' }">
					<td class="literal">Asegurado: ${asegurado.nombre} ${asegurado.apellido1} ${asegurado.apellido2 }</td>
				    <td class="literal">NIF: ${asegurado.nifcif }</td>	
				</c:if>	
				<c:if test="${asegurado.tipoidentificacion eq 'NIE' }">
					<td class="literal">Asegurado: ${asegurado.nombre} ${asegurado.apellido1} ${asegurado.apellido2 }</td>
				    <td class="literal">NIE: ${asegurado.nifcif }</td>	
				</c:if>				
				</TR>			
			</table>
		</c:if>
		<div id="buttons">
			<table width="97%" cellspacing="2" cellpadding="2" border="0">
				<tr>
				<td align="left">
						<a class="bot" id="btnVolver"  href="javascript:document.getElementById('aseguradoForm').submit()" >Volver</a>
					</td>
					<td align="right">
						<a class="bot" id="btnConsultar" href="javascript:generales.uneCuenta();enviar('doConsulta')">Consultar</a>
						<a class="bot" id="btnAlta" href="javascript:generales.uneCuenta();enviar('doAlta')">Alta</a>
						<a class="bot" id="btnModificar" href="javascript:generales.uneCuenta();enviar('doModificar')" style="display:none">Modificar</a>
						<a class="bot" id="btnLimpiar" href="javascript:enviar('doLimpiar')">Limpiar</a>
					</td>
					
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Datos Adicionales</p>
			<form:form name="main" id="main" action="datoAsegurado.html" method="post" commandName="datoAseguradoBean">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" id="idAsegurado" name="idAsegurado" value="${idAsegurado }" />
				<input type="hidden" name="cuentaEditada" id="cuentaEditada"/>
				<input type="hidden" name="cuentaEditada2" id="cuentaEditada2"/>
				<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza }" />
				<input type="hidden" name="volverPago" value="${volverPago }"/>
				<input type="hidden" name="idEnvio" id="idEnvio" value="${idEnvio}"/>
				<input type="hidden" name="showPopupPolAsegurados" id="showPopupPolAsegurados" value="${showPopupPolAsegurados}" />
				<input type="hidden" name="polizasAsegurado" id="polizasAsegurado" value="${polizasAsegurado}" />
				<input type="hidden" name="listIdPolizas" id="listIdPolizas" value="${listIdPolizas}" />
				<input type="hidden" name="cargaAseg" id="cargaAseg" value="${cargaAseg}" />
				<input type="hidden" name="formulario" id="formulario" value="datosAsegurados"/>
				<input type="hidden" name="lineaGen" id="lineaGen" value="${lineaGen}" />
				<input type="hidden" name="lineaCondicionadoBaja" id="lineaCondicionadoBaja" value="${lineaCondicionadoBaja}" />
				<input type="hidden" name="titCuenta" id="titCuenta" value="${titCuenta}" />
				<input type="hidden" name="destDomic" id="destDomic" value="${destDomic}" />
				
				<form:hidden path="id" id="idDatoAsegurado" />
				<form:hidden path="asegurado.discriminante" />
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt">
					<table>
						<colgroup>
							<col width="10%">
							<col width="15%">
							<col width="20%">
							<col width="*">
						</colgroup>
						<tr>
							<td class="literal">Entidad</td>
							<td class="literal">
								<form:input id="entidad" path="asegurado.entidad.codentidad" cssClass="literal" disabled="true" />
							</td>
							<td class="literal">CIF/NIF/NIE Asegurado</td>
							<td class="literal">
								<form:input path="asegurado.nifcif" cssClass="literal" disabled="true" />
							</td>
						</tr>
						<tr>
							<td class="literal">L&iacute;nea</td>
							<td class="literal">
								<c:if test="${linea999 eq 'linea999'}">
									<form:input path="lineaCondicionado.codlinea" size="3" maxlength="3" cssClass="dato" id="lineaCondicionado" tabindex="1" readonly="true"/>
								</c:if>
								<c:if test="${linea999 eq ''}">
									<form:input path="lineaCondicionado.codlinea" size="3" maxlength="3" cssClass="dato" id="lineaCondicionado" tabindex="1"/>
									<a id="lupaLinea" href="javascript:lupas.muestraTabla('LineaCondicionado','principio', '', '');"><img src="jsp/img/magnifier.png" style="cursor: hand;" alt="Buscar Línea" title="Buscar Línea" /></a>
								</c:if>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_lineaCondicionado"> *</label>
							</td>
							<td class="literal">IBAN pago prima</td>
							<td class="literal" nowrap>
								<form:hidden path="ccc" id="ccc" />
								<form:input path="iban" size="4" maxlength="4" cssClass="dato" tabindex="2" onkeyup="autotab(this, document.main.cuenta1);" onchange="this.value=this.value.toUpperCase();" id="iban"/>
								<input type="text" id="cuenta1" name="cuenta1" size="4" maxlength="4" class="dato" tabindex="2" onKeyup="autotab(this, document.main.cuenta2);"/>
								<input type="text" id="cuenta2" name="cuenta2" size="4" maxlength="4" class="dato" tabindex="3" onKeyup="autotab(this, document.main.cuenta3);"/>
								<input type="text" id="cuenta3" name="cuenta3" size="4" maxlength="4" class="dato" tabindex="4" onKeyup="autotab(this, document.main.cuenta4);"/>
								<input type="text" id="cuenta4" name="cuenta4" size="4" maxlength="4" class="dato" tabindex="5" onKeyup="autotab(this, document.main.cuenta5);"/>
								<input type="text" id="cuenta5" name="cuenta5" size="4" maxlength="4" class="dato" tabindex="6"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_iban"> *</label>
							</td>
						</tr>
						<tr>
							<td class="literal"></td>
							<td class="literal"></td>
							<td class="detalI">Destinatario de la domiciliación</td>				
							<td >			
								<form:select  cssClass="dato" id="destinatarioDomiciliacion" path="destinatarioDomiciliacion" cssStyle="width:90" onchange="javascript:comprobarDestinatario();" tabindex="7">
									<option value=""></option>			
									<option value="A">Asegurado</option>
									<option value="T">Tomador</option>										
									<option value="O">Otros</option>
								 
								</form:select>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_destinatarioDomiciliacion"> *</label>
								<span align="right" class="detalI" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Titular de la cuenta&nbsp;&nbsp;	
								
																
								 <form:input id="titularCuenta" path="titularCuenta" cssClass="dato" size="50" maxlength="100" tabindex="8" onchange="this.value = this.value.toUpperCase();"/>
								 <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_titularCuenta"> *</label></span>	
							</td>	
						</tr>
						<tr>
							<td class="literal" colspan="2">&nbsp;</td>
							<td class="literal">IBAN cobro siniestros</td>
							<td class="literal" nowrap>
								<form:hidden path="ccc2" id="ccc2" />
								<form:input path="iban2" size="4" maxlength="4" cssClass="dato" tabindex="9" onkeyup="autotab(this, document.main.cuenta6);" onchange="this.value=this.value.toUpperCase();" id="iban2"/>
								<input type="text" id="cuenta6" name="cuenta6" size="4" maxlength="4" class="dato" tabindex="10" onKeyup="autotab(this, document.main.cuenta7);"/>
								<input type="text" id="cuenta7" name="cuenta7" size="4" maxlength="4" class="dato" tabindex="11" onKeyup="autotab(this, document.main.cuenta8);"/>
								<input type="text" id="cuenta8" name="cuenta8" size="4" maxlength="4" class="dato" tabindex="12" onKeyup="autotab(this, document.main.cuenta9);"/>
								<input type="text" id="cuenta9" name="cuenta9" size="4" maxlength="4" class="dato" tabindex="13" onKeyup="autotab(this, document.main.cuenta10);"/>
								<input type="text" id="cuenta10" name="cuenta10" size="4" maxlength="4" class="dato" tabindex="14"/>
								<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_iban2"> *</label>
							</td>
						</tr>
					</table>
				</div>
			</form:form>
			<br />
			<div class="grid" style="">
				<display:table requestURI="datoAsegurado.html" id="datoAsegurado" class="LISTA" summary="colectivo" sort="list" 
								pagesize="${numReg}" name="${listaDatosAsegurado}" export="false" 
								decorator="com.rsi.agp.core.decorators.ModelTableDecoratorDatosAsegurados" style="width:95%" 
								excludedParams="method">
					<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="datosAseguradoSelec" sortable="false" style="width:50px;text-align:center"/>
					<display:column class="literal" headerClass="cblistaImg" title="Línea" property="datosAseguradoLinea" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="IBAN Pago Prima" property="datosAseguradosccc" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="Destinatario" property="destinatario" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="IBAN Cobro Siniestros" property="datosAseguradosccc2" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="Fecha Alta" property="fechaAlta" sortable="true" />	
					<display:column class="literal" headerClass="cblistaImg" title="Fecha Modificación" property="fechaModificacion" sortable="true" />						
					<display:setProperty name="export.pdf" value="true" />
					<display:setProperty name="export.pdf.filename" value="exportar.pdf" />
				</display:table>		
			</div>
			<form name="aseguradoForm" id="aseguradoForm" action="asegurado.html">
				<input type="hidden" id="method" name="method" value="doConsulta" />
				<input type="hidden" id="idAsegurado" name="idAsegurado" value="${idAsegurado}" />
				<input type="hidden" name="cargaAseg" id="cargaAseg" value="${cargaAseg}" />
			</form>
			
		
		</div>
		<br />
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLineasCondicionado.jsp"%>
		<%@ include file="/jsp/moduloAdministracion/asegurados/popupPolizasAsegurados.jsp"%>
	</body>
</html>
