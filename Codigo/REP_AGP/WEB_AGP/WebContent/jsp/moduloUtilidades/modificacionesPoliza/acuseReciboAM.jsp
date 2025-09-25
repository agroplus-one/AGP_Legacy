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
		
		<script type="text/javascript">
		
		function volver () {
			var frm = document.getElementById ('formVolver');
			frm.volver.value="true";
			$('#formVolver').submit();
		}
		
		</script>
		
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" href="#" onClick="javascript:volver(); ">Volver</a>						
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div id="imprimirErrores">	
			<div class="conten" style="padding:3px;width:97%">
				<p class="titulopag" align="left">ACUSE DE RECIBO DE LA CONFIRMACIÓN DEL ANEXO</p>

							<c:if test="${not empty mensaje}">
								<table width="100%">
										<tr><td class="centrado" style="font-size: 14px;color: #FF0000;" colspan="4">
											${mensaje}
										<td></tr>
								</table>
							</c:if>
							
							<c:if test="${requestScope.errLength gt 0}">
								
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
										<c:out value="${error.descripcion}"/>
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
							
							</c:if>
					
							<BR/>
							<BR/>
		</div>	
		</div>	
		
		<form action="confirmacionModificacion.html" name="formVolver" id="formVolver"  method="post">
			<input type="hidden" id="method" name="method" value="doVolver"/>
			<input type="hidden" id="redireccion" name="redireccion" value="${redireccion}"/>
			<input type="hidden" id="idAnexo" name="idAnexo" value="${idAnexo}"/>
			<input type="hidden" id="idPoliza" name="idPoliza" value="${idPoliza}"/>
			<input type="hidden" id="idCupon" name="idCupon" value="${idCupon}"/>
			<input type="hidden" id="volver" name="volver" />			
		</form>
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		
</body>
</html>