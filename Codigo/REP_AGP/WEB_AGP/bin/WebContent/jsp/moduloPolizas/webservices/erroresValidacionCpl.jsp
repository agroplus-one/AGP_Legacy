<%@ include file="/jsp/common/static/taglibs.jsp" %>
<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>

<%@ include file="/jsp/common/static/setHeader.jsp"%>
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
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/webservices/erroresValidacion.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/cargaDocFirmada.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
		
		<c:if test="${not empty sessionScope.codTerminal}">
			<script type="text/javascript" src="jsp/js/tableta/ntfApiLib.js"></script>			
		</c:if>
		
		<script type="text/javascript">
			$(document).ready(function() {
			
				var URL = UTIL.antiCacheRand(document.getElementById("calcular").action);
		        document.getElementById("calcular").action = URL;    
		        
				var URL = UTIL.antiCacheRand(document.getElementById("volver").action);
		        document.getElementById("volver").action = URL;    
		        
				<c:if test="${datosCorrectos}">
					$('#btnCorregir').hide();
					calcular();					
				</c:if>
				
				<!-- MPM - 21/05/12 -->
				<!-- Si no viene del paso a definitiva -->
				<c:if test="${not fromPasoADefinitiva or fromPasoADefinitiva eq null}">
					<c:choose>
						<c:when test="${ botonCorregir && botonCalcular }">
							$('#btnCorregir').show();
							$('#btnCalcular').show();
						</c:when>
									
						<c:when test="${ !botonCorregir && botonCalcular }">
							$('#btnCorregir').hide();
							$('#btnCalcular').show();
						</c:when>
						<c:when test="${ botonCorregir && !botonCalcular }">
							$('#btnCorregir').show();
							$('#btnCalcular').hide();
						</c:when>
					</c:choose>
				</c:if>
				<!-- Si viene del paso a definitiva solo se muestra el boton de imprimir -->
				<c:if test="${fromPasoADefinitiva}">
					$('#btnCorregir').hide();
					$('#btnCalcular').hide();
				</c:if>
				
				// mejora 112 Angel 01/02/2012 anhadida la opcion de ver la poliza sin opcion a editarla tambien con estado grabacion definitiva  
		        if(${estado} == 8 || ${estado} == 3){
		       		$('#btnCorregir').hide();
		       		$('#btnCalcular').html('Continuar');
		        }
				
				<c:if test="${not empty sessionScope.codTerminal && autoCompFirma}">
					uploadDocAndSign();
				</c:if>
			});
			
			$(function(){
				$("#grid").displayTagAjax();				
			});
			
			function volver(){
				$('#volver').submit();
			}
			
			function corregir(){
				$('#volver').submit();
			}
			
			function calcular(){
				$('#method').val('doCalcular');
				$.blockUI.defaults.message =  '<h4> Realizando el C\u00E1lculo de Importes.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
       			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } }); 
				$('#calcular').submit();
			}	
			
			function xmlValidationComp(){
				var frm = document.getElementById('formUtilidades');
				frm.method.value = 'doGetXMLValidacion';
				frm.submit();
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
							<a class="bot" id="btnCalcular" href="javascript:calcular();">Calcular</a>
							<a class="bot" id="btnCorregir" href="javascript:corregir();">Corregir</a>																					
							
							<!-- MPM - 21/05/12 -->
							<!-- Si no viene del paso a definitiva -->
							<c:if test="${not fromPasoADefinitiva or fromPasoADefinitiva eq null}">
							    <!-- Pet. 22208 ** MODIF TAM (11.04.2018) - Resolucion de Incidencias -->			
								<c:if test="${not swConfirmacion}">																				
								   <a class="bot" id="btnVolver"   href="javascript:volver();">Volver</a>
								</c:if>   
							</c:if>
							<!-- Si viene del paso a definitiva solo se muestran los botones de imprimir y volver -->
							<c:if test="${fromPasoADefinitiva}">
								<c:if test="${mostrarForzaDef}">
									<c:if test="${empty sessionScope.codTerminal || empty idInternoPe}">
										<a href="javascript:grabarDefFueraContratacion(${idpolizaCpl});" class="bot">Forzar confirmaci&oacute;n</a>
						        	</c:if>
							        <c:if test="${not empty sessionScope.codTerminal && not empty idInternoPe}">
							        	<a href="javascript:uploadDocAndSign();" class="bot">Forzar confirmaci&oacute;n</a>
							        </c:if>									
								</c:if>	
								 <!-- Pet. 22208 ** MODIF TAM (11.04.2018) - Resolucion de Incidencias -->			
								<c:if test="${not swConfirmacion}">										
								   <!-- La llamada del 'Volver' depende de si venimos del listado de utilidades o del ciclo de poliza -->
								   <c:if test="${cicloPoliza == 'cicloPoliza'}">
									   <a class="bot" id="btnVolver"   href="javascript:volver();">Volver</a>
								   </c:if>
								   <c:if test="${cicloPoliza eq null or cicloPoliza != 'cicloPoliza'}">
									   <a class="bot" href="#" onClick="volverListado ();">Volver</a>
								   </c:if>
								</c:if>   
								
							</c:if>
					</td>
					<!--  Pet. 22208 ** MODIF TAM (26.03.2018) ** Inicio  -->
					<td align="center">
						<!-- Si venimos del Sw Confirmacion hay que mostrar los nuevos botones de Imprimir e Imprimir Reducida -->
						<c:if test="${swConfirmacion }">
						    <a class="bot" href="#" id="btnImprimir" onclick="javascript:imprimirswConfirm();">Imprimir</a>
						</c:if>	   
					</td>
					
					<!--  Pet. 7003325 Inicio  -->
					<!-- Anhadimos un nuevo boton solo si la poliza esta en estado Enviada Correcta -->
					<td align="right">
						<c:if test="${swConfirmacion}">
							<c:if test="${cicloPoliza =='cicloPoliza'}">
								<c:if test="${estadoPoliza == 8 and empty idInternoPe and docFirmada != 'S'}">  
									<a class="bot" href="#" onClick="javascript:cargaDocFirmada('${idpolizaCpl}','${referencia}','${plan}','${tipoPoliza}','2');">Cargar documentaci&oacute;n</a>
								</c:if>
							</c:if>
						</c:if>
					</td>
					
					<td align="right">
						<c:if test="${swConfirmacion}">
						    <c:if test="${cicloPoliza =='cicloPoliza'}">
								<a class="bot" href="#" onClick="javascript:salirSwConfirmacion();">Salir</a>
							</c:if>
							<c:if test="${cicloPoliza eq null or cicloPoliza !='cicloPoliza'}">
								<a class="bot" href="#" onClick="javascript:volverListado();">Salir</a>
							</c:if>
						</c:if>
					</td>
					<!--  Pet. 22208 ** MODIF TAM (26.03.2018) ** Fin  -->
				</tr>
			</table>
		</div>
		<!-- Contenido de la pagina -->
		<div class="conten" style="padding:3px;width:97%">
			<!--  Pet. 22208 ** MODIF TAM (26.03.2018) ** Inicio -->
		    <!-- <p class="titulopag" align="left">Validacion Realizada</p> -->
			<c:if test="${not swConfirmacion}">
			   <p class="titulopag" align="left">Validaci&oacute;n Realizada</p>
			</c:if>
			<c:if test="${swConfirmacion}">
			   <p class="titulopag" align="left">Confirmaci&oacute;n de la P&oacute;liza con Agroseguro</p>
			</c:if>
			<c:if test="${swConfirmacion}">
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			</c:if>	
			<!--  Pet. 22208 ** MODIF TAM (26.03.2018) ** Fin -->
			
				<table width="100%">
					<tr>
					   <!-- Pet. 22208 ** MODIF TAM (26.03.2018) ** Fin -->
					   <!-- Incluimos la validacion para comprobar que venimos de WS de Confirmacion -->
					   <c:if test="${not swConfirmacion}">
							<td class="centrado" style="font-size: 14px;color: #FF0000;" colspan="4">
								${mensaje}
							<td>
					   </c:if>	
			           <!--  Pet. 22208 ** MODIF TAM (26.03.2018) ** Fin -->	
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
								<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="C&oacute;digo del Error" sortable="true">
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
								<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="Descripci&oacute;n del Error">
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
				<c:if test="${perfil == 0}">
					<div style="width:20%; text-align: right; margin-left: auto; margin-right: 5px" id="divImprimir"
						alt="Descargar XML Validaci&oacute;n" title="Descargar XML Validaci&oacute;n">
				 		<a id="btnImprimirExcel" style="text-decoration: unset;" href="javascript:xmlValidationComp()">
				 			<img width="16" height="16" src="jsp/img/jmesa/csv.gif"/>
				 			<span style="color: gray; font-family: tahoma, verdana, arial; font-size: 11px; font-weight: bold; vertical-align: top" >
				 				XML Validaci&oacute;n
				 			</span>
			 			</a>	
					</div>
				</c:if>
		</div>	
		<form action="webservicesCpl.html" method="post" name="calcular" id="calcular">
			<input type="hidden" name="method" id="method" />
			<input type="hidden" name="ws" id="ws" value="${WS}" />
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpolizaCpl }"/>
			<input type="hidden" name="origenllamada" id="origenllamada" value="${origenllamada}"/>
		</form>		
		<form action="polizaComplementaria.html" method="post" name="volver" id="volver">
			<input type="hidden" name="idpolizaCpl" id="idpolizaCpl" value="${idpolizaCpl }"/>
		</form>			
		<!-- MPM - 21-05-12 -->		
		<!-- Formulario para la vuelta al listado de utilidades -->
		<form name="volverUtilidades" id="volverUtilidades" action="utilidadesPoliza.html" method="post">
			<input type="hidden" name="operacion" id="operacion" value="volver" />
		</form>
		<!-- Formulario para forzar el paso a definitiva -->
		<form name="pasarADefinitiva" id="pasarADefinitiva" action="pasoADefinitiva.html" method="post" commandName="polizaDefinitiva">
			<input type="hidden" name="method" id="method" value="doPasarADefinitiva"/>
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpolizaCpl}"/>
			<input type="hidden" name="resultadoValidacion" id="resultadoValidacion"/>
			<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion"/>
			<input type="hidden" name="actualizarSbp" id="actualizarSbp"/>				
			<input type="hidden" name="esCpl" id="esCpl" value="true"/>
			<input type="hidden" name="cicloPoliza" id="cicloPoliza" value="${cicloPoliza}"/>
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" />
			<%-- Pet. 22208 ** MODIF TAM (26.03.2018) ** Inicio --%>
			<input type="hidden" name="imprimirReducida" id="imprimirReducida" />		
			<%--Pet. 22208 ** MODIF TAM (10.04.2018) - Resolucion Incidencias --%>
            <input type="hidden" name="swConfirmacion" id="swConfirmacion" value="${ForzarswConfirmacion}" />										
			<input type="hidden" id="idInternoPe" name="idInternoPe" value="${idInternoPe}" />	
            <input type="hidden" id="codTerminal" name="codTerminal" value="${sessionScope.codTerminal}" />		
            <input type="hidden" id="idDocumentum" name="idDocumentum" value="${idDocumentum}" />		
            <input type="hidden" id="codUsuario" name="codUsuario" value="${codUsuario}" />				
            <input type="hidden" id="firmaDiferida" name="firmaDiferida" value="${firmaDiferida}" />	
            <input type="hidden" id="autoCompFirma" name="autoCompFirma" value="${autoCompFirma}" />	
            <input type="hidden" id="docFirmada" name="docFirmada" value="${docFirmada}"/>				
		</form>	
		<%-- Pet. 22208 ** MODIF TAM (26.03.2018) ** Inicio --%>
		<form action="grabacionPoliza.html" method="post" name="imprimir" id="imprimir">
			<input type="hidden" id="operacion" name="operacion"/>
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpolizaCpl}"/>
		</form>
		<%-- Pet. 22208 ** MODIF TAM (26.03.2018) ** Fin  --%>
		
		<form method="post" name="formUtilidades" id="formUtilidades" action="utilidadesXML.run">
			<input type="hidden" name="method" id="methodUtl" />
			<input type="hidden" name="idPoliza" id="idPoliza" value="${idpolizaCpl}"/>
			<input type="hidden" name="vieneDeCpl" id="vieneDeCpl" value="true"/>
		</form>
		
		<!-- P0073325 - RQ.10, RQ.11 y RQ.12 -->
		<%@ include file="/jsp/moduloPolizas/polizas/cargaDocFirmada.jsp"%>
</body>
</html>