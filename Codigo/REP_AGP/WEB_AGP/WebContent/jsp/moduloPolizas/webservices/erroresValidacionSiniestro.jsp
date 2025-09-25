<%@ include file="/jsp/common/static/taglibs.jsp" %>
<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>

<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
<title>RESULTADO DE LA VALIDACIÓN DEL SINIESTRO</title>

        <%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript">	
				
			$(document).ready(function() {
			
				continuar();
			});
			
			$(function(){
				$("#grid").displayTagAjax();				
			});
			
			function volver(){
				if($('#origenllamadaVolver').val()=="listadoSiniestros"){
					var frm=document.getElementById("volverListadoSiniestros");
					$('#method').val("doConsulta");
					$('#volverListadoSiniestros').submit();
				}else{//declaracionesSiniestro
					var frm=document.getElementById("volverDeclaracionesSiniestro");
					$('#method').val("doConsulta");
					$('#volverDeclaracionesSiniestro').submit();
					
				}				
			}
			
			function corregir(){
				$('#volverParcelasSiniestradas').submit();
			}
			
					
			function confirmar(){
				$.blockUI.defaults.message = '<h4> Procesando petición.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
	       		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
				var frm=document.getElementById("frmConfirmar");
				frm.method.value='doConfirmarSiniestro';
				$('#frmConfirmar').submit();				
			}
			
			function continuar(){
				if( $('#validacionOk').val()=="true"){
					var frm=document.getElementById("frmConfirmar");
					frm.idPoliza.value=${idPoliza};
					frm.idSiniestro.value=${idSiniestro};
					confirmar();
				}
			}
			
		</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">		
						<c:if test="${mostrarCorregir}">			
							<a class="bot" id="btnCorregir" href="javascript:corregir();">Corregir</a>																					
						</c:if>
						<c:if test="${mostrarConfirmar}"> 					
							<!-- <a href="javascript:confirmar();" class="bot">Continuar con la Confirmación</a>  -->
							<a href="javascript:confirmar();" class="bot">Continuar</a>
						</c:if>
						<c:if test="${mostrarForzarConfirmar}">
							<!-- <a href="javascript:confirmar();" class="bot">Forzar la Confirmación</a> -->					
						</c:if>
						<c:if test="${validacionOk}">
							<!-- <a href="javascript:continuar();" class="bot">Continuar</a> -->
							<a href="javascript:volver();" class="bot">Volver</a>
						</c:if>
						
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Validación Realizada</p>
				<table width="100%">
					<tr>
						<td class="centrado" style="font-size: 14px;color: #FF0000;" colspan="4">
							${mensaje}
						<td>
					</tr>
				</table>
				<div id="grid">
					<c:if test="${ not empty errores }">
						<display:table requestURI="webservices.html" 
									   class="LISTA" 
									   decorator="com.rsi.agp.core.decorators.ModelTableDecoratorListaParcelas" 
								 	   sort="list" 
								 	   name="${errores}" 
								 	   list="errorList" 
								 	   id="error"
								 	   pagesize="${errLength }">
								<display:setProperty name="paging.banner.onepage" value="&nbsp;"/>
								<display:setProperty name="paging.banner.item_name" value="error"/>
								<display:setProperty name="paging.banner.items_name" value="errores"/>
								<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="Código del Error" sortable="true">
									<c:out value="${error.codigo}"/>
								</display:column>
								<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="Tipo de Error" sortable="false">
									<c:if test="${error.tipo eq 3}">
										<img src="jsp/img/displaytag/accept.png" alt="Correcto" title="Correcto"/>
									</c:if>
									<c:if test="${error.tipo eq 1}">
										<img src="jsp/img/displaytag/cancel.png" alt="Rechazado" title="Rechazado"/>
									</c:if>
									<c:if test="${error.tipo eq 2}">
										<img src="jsp/img/displaytag/warning.gif" alt="Con Errores" title="Con Errores"/>
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
								<display:column class="literal" style="text-align:left;" headerClass="cblistaImg" title="Hoja - Nº">
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
						</display:table>
					</c:if>
				</div>
		</div>	
		<%-- <form action="webservicesCpl.html" method="post" name="calcular" id="calcular">
			<input type="hidden" name="method" id="method" />
			<input type="hidden" name="ws" id="ws" value="${WS}" />
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpolizaCpl }"/>
			<input type="text" name="origenllamada" id="origenllamada" value="${origenlLamada}"/>
		</form>		 --%>
		<form action="polizaComplementaria.html" method="post" name="volver" id="volver">
			<input type="hidden" name="idpolizaCpl" id="idpolizaCpl" value="${idpolizaCpl }"/>
		</form>			
		<!-- Formulario para volver a declaración de siniestros -->
		<form name="volverDeclaracionesSiniestro" id="volverDeclaracionesSiniestro" action="siniestros.html" method="post">
			<input type="hidden" name="operacion" id="operacion" value="volver" />
			<input type="hidden" name="idPoliza" id="idPoliza" value="${idPoliza}" />
			<input type="hidden" name="method" id="method"/> 
			<input type="hidden" name="riesgoSiniestro" id="riesgoSiniestro" value="${riesgoSiniestro}"/>
			<input type="hidden" name="fechaocurrSiniestro" id="fechaocurrSiniestro" value="${fechaocurrSiniestro}"/>
			<input type="hidden" name="fechaenvioSiniestro" id="fechaenvioSiniestro" value="${fechaenvioSiniestro}"/>
			<input type="hidden" name="codestadoSiniestro" id="codestadoSiniestro" value="${codestadoSiniestro}"/> 
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="erroresValidacionSiniestros"/>			
		</form>
		
		<!-- Formulario para forzar el paso a definitiva -->
		<form name="frmConfirmar" id="frmConfirmar" action="siniestros.html" method="post">
			<input type="hidden" name="method" id="method"/> 
			<input type="hidden" name="idPoliza" id="idPoliza" value="${idPoliza}"/>
			<input type="hidden" name="idSiniestro" id="idSiniestro" value="${idSiniestro}"/>
			<input type="hidden" name="validacionOk" id="validacionOk" value="${validacionOk}"/>			
			<input type="hidden" name="origenllamada" id="origenllamada" value="erroresValidacionSiniestros"/>		
			<input type="hidden" name="origenllamadaVolver" id="origenllamadaVolver" value="${origenLlamada}"/>							
		</form>	
		<!-- Formulario para volver a listado de siniestros -->
		<form name="volverListadoSiniestros" id="volverListadoSiniestros" action="utilidadesSiniestros.run" method="post">
			<input type="hidden" name="operacion" id="operacion" value="volver" />
			<input type="hidden" name="idPoliza" id="idPoliza" value="${idPoliza}" />
			<input type="hidden" name="volver" id="volver" value="erroresValidacion" />
			<input type="hidden" name="method" id="method"/> 
			<%-- <input type="hidden" name="idSiniestro" id="idSiniestro" value="${idSiniestro}" /> --%>
		</form>
		<!-- Formulario para volver a Alta de parcelas siniestradas -->
		<form name="volverParcelasSiniestradas" id="volverParcelasSiniestradas" action="parcelasSiniestradas.html" method="post">
			<input type="hidden" name="operacion" id="operacion" value="volver" />
			<input type="hidden" name="idPoliza" id="idPoliza" value="${idPoliza}" />
			<input type="hidden" name="idSiniestro" id="idSiniestro" value="${idSiniestro}" />
		</form>
</body>
</html>