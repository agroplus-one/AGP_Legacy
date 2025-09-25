<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/common/static/taglibs.jsp" %>

<html>
<head>
<title>Resultados Validacion</title>

        <%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		
		<%@ include file="/jsp/js/draggable.jsp"%>
		<%@ include file="/jsp/moduloUtilidades/modificacionesPoliza/utilEnviosAM.jsp"%>
		
</head>

<script type="text/javascript">
		
function volver () {
	var frm = document.getElementById ('continuar');
	frm.volver.value="true";
	$('#method').val('doVolver');
	$('#continuar').submit();
}

</script>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" href="#" onClick="javascript:volver();">Volver</a>
						
						<c:if test="${mostrarCalcular eq false}">
							<a class="bot" href="#" onClick="javascript:confirmarAnexo(${errorTramite}, ${perfil34});">Continuar</a>
						</c:if>
						
						<c:if test="${mostrarCalcular eq true and puedeCalcular eq true}">
							<a id="calcular" class="bot" href="#" onClick="javascript:calcularAnexo();">Calcular</a>
						</c:if>
					</td>
				</tr>
			</table>
		</div>
		<form:form action="confirmacionModificacion.html" name="continuar" id="continuar"  method="post" commandName="anexoModificacion">
			<input type="hidden" id="method" name="method" value="doConfirmarAnexo"/>
			<input type="hidden" id="redireccion" name="redireccion" value="${redireccion}"/>
			<input type="hidden" id="indRevAdm" name="indRevAdm" value="N"/>
			<input type="hidden" id="volver" name="volver" />
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
			<form:hidden path="id" id="idAnexo"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<form:hidden path="cupon.id" id="idCupon"/>
			<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}"/>
		</form:form>
		
		<form:form action="calculoModificacion.html" name="calculoModificacion" id="calculoModificacion"  method="post" commandName="anexoModificacion">
			<input type="hidden" id="method" name="method" value="doCalculoModificacion"/>
			<form:hidden path="id" id="idAnexo"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<form:hidden path="cupon.id" id="idCupon"/>
			<input type="hidden" id="redireccion" name="redireccion" value="${redireccion}"/>
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
			<input type="hidden" id="errorTramite" name="errorTramite" value="${errorTramite}"/>
			<input type="hidden" id="perfil34" name="perfil34" value="${perfil34}"/>
			<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}"/>
		</form:form>
		<!-- Contenido de la página -->
		<div id="imprimirErrores">	
			<div class="conten" style="padding:3px;width:97%">
				<p class="titulopag" align="left">Validación Realizada</p>

							<c:if test="${requestScope.errorEnValidacion eq true}">
								<table width="100%">
										<tr><td class="centrado" style="font-size: 14px;color: #FF0000;" colspan="4">
											Ocurrió un error en la validación
										<td></tr>
								</table>
							</c:if>
							
							<display:table requestURI="webservices.html" 
										   class="LISTA" 
										   decorator="com.rsi.agp.core.decorators.ModelTableDecoratorListaParcelas" 
										   defaultsort="0" 
										   defaultorder="ascending"
									 	   sort="list" 
									 	   name="errores" 
									 	   list="errorList" 
									 	   id="error"
									 	   pagesize="${errLength}">
								<display:setProperty name="paging.banner.onepage" value="&nbsp;"/>
								<display:setProperty name="paging.banner.item_name" value="error"/>
								<display:setProperty name="paging.banner.items_name" value="errores"/>
								<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="Código del Error" sortable="true">
									<c:out value="${error.codigo}"/>
								</display:column>
								<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="Tipo de Error" sortable="false">
									<c:if test="${error.tipo eq 1}">
										<img src="jsp/img/displaytag/cancel.png" alt="Rechazado" title="Rechazado"/>
									</c:if>
									<c:if test="${error.tipo eq 2}">
										<img src="jsp/img/displaytag/warning.gif" alt="Con Errores" title="Con Errores"/>
									</c:if>
									<c:if test="${error.tipo eq 3}">
										<img src="jsp/img/displaytag/accept.png" alt="Correcto" title="Correcto"/>
									</c:if>
								</display:column>							
								<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="Descripción del Error">
									<c:out value="${error.descripcion}">-</c:out>
									<c:if test="${not empty error.descripcionAmpliada}">
										<br/>
										<span class="descripcionAmpliada">- <c:out value="${error.descripcionAmpliada}"/></span>
									</c:if>
									<c:if test="${error.textoAyuda}">
										<br/>
										<span class="descripcionAmpliada">- <c:out value="${error.textoAyuda}"/></span>
									</c:if>
								</display:column>
								
								<c:choose>
									<c:when test="${tipoLinea eq 'AGR'}">
										<display:column class="literal" style="text-align:left;" headerClass="cblistaImg" title="Número de Parcela">
											<c:set var="xpath" value="${fn:substring(error.localizacion.xpath, fn:indexOf(error.localizacion.xpath, '@numero=\')+9, fn:indexOf(error.localizacion.xpath, ']/')-1)}"/>
											<c:set var="xpath2" value="${fn:substring(error.localizacion.xpath, fn:indexOf(error.localizacion.xpath, '@hoja=\')+7, fn:indexOf(error.localizacion.xpath, ']/')-1)}"/>
											<c:catch var="e">
			              						<fmt:parseNumber var="j" type="number" value="${xpath2}" />
			              						<c:out value="${j}" escapeXml="false" />
			            					</c:catch>
			            					-
											<c:catch var="e">
			              						<fmt:parseNumber var="i" type="number" value="${xpath}" />
			              						<c:out value="${i}" escapeXml="false" />
			            					</c:catch>
			            					<c:out value=" " escapeXml="false" />
										</display:column>
									</c:when>
									
									<c:when test="${tipoLinea eq 'GAN'}">
										<display:column class="literal" style="text-align:left;" headerClass="cblistaImg" title="Número de Explotación">
											<c:set var="xpath" value="${fn:substring(error.localizacion.xpath, fn:indexOf(error.localizacion.xpath, '@numero=\')+9, fn:indexOf(error.localizacion.xpath, ']/')-1)}"/>
											<c:catch var="e">
			              						<fmt:parseNumber var="i" type="number" value="${xpath}" />
			              						<c:out value="${i}" escapeXml="false" />
			            					</c:catch>
			            					<c:out value=" " escapeXml="false" />
										</display:column>
									</c:when>
								</c:choose>
							</display:table>
							
							<c:if test="${empty requestScope.errorEnValidacion && requestScope.errLength eq 0}">
								<table width="100%">
										<tr><td class="centrado" style="font-size: 14px;color: #FF0000;" colspan="4">
											Validación correcta
  											<script>  
											document.getElementById('calcular').click();
  											</script>  
										<td></tr>
								</table>
							</c:if>
					
							<BR/>
							<BR/>
		</div>	
		</div>	

		
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/moduloUtilidades/anexoModificacion/popupConfirmacion.jsp"%>
		
</body>
</html>