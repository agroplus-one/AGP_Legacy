<%@ include file="/jsp/common/static/taglibs.jsp" %>
<!-- Rehecho con JSTL el 01/09/2017 -->

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
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/webservices/erroresValidacion.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/cargaDocFirmada.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
		
		<c:if test="${not empty sessionScope.codTerminal}">
			<script type="text/javascript" src="jsp/js/tableta/ntfApiLib.js"></script>
			
			<script type="text/javascript">	
		
				$(document).ready(function() {
					<c:if test="${autoCompFirma}">
						uploadDocAndSign();
					</c:if>
				});
			</script>
		</c:if>

	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<!-- MPM - 21/05/12 -->
						<!-- Si no viene del paso a definitiva -->
						<c:if test="${not fromPasoADefinitiva or fromPasoADefinitiva eq null}">
							<table border="0" width="100%">
								<td align="left" width="34%" >
									<c:if test="${bBotonCorregir}">
										<a class="bot" href="#" onClick="javascript:doSubmit('consulta');">Corregir</a>
									</c:if>	
									<c:if test="${esLineaGanado}">
										&nbsp;<a class="bot" href="#" id="btnComparativas" onclick="javascript:volverComparativas();">Comparativas</a>
									</c:if>
									<c:if test="${not esLineaGanado}">
									    <c:if test="${not swConfirmacion}">
											&nbsp;<a class="bot" href="#" id="btnComparativas" onclick="javascript:volverComparativas();">Comparativas</a>
										</c:if>	
									</c:if>	
								</td>
								<!-- Pet. 22208 ** MODIF TAM (05.03.2018) ** Inicio  -->
								<%--MODIF TAM (03.04.2018) - Resolucion de Incidencias --%>
								<td align="left" width="33%" >
								    <c:if test="${swConfirmacion }">
										<a class="bot" href="#" id="btnImprimir" onclick="javascript:imprimirswConfirm();">Imprimir</a>								    
										<c:if test="${not isLineaGanado}">
											<a class="bot" href="#" id="btnImprimirReducida" onclick="javascript:imprimirReducida();">Imprimir Reducida</a>
										</c:if>
									</c:if>	   
								</td>
								<td align="right" width="33%">
									
									<!--  Si el valor de swConfiramcion == true, hay que añadir un nuevo boton de salir. -->
									<c:if test="${not swConfirmacion}">
									   <c:if test="${bBotonCalculo or usuarioPerfilCero}">
										   <a class="bot" href="#" onClick="javascript:doWorkCalculo();">Calcular</a>
									   </c:if>
									   <a class="bot" href="#" onClick="javascript:imprimirErr('imprimirErrores');">Ver errores</a>
									   <a class="bot" href="#" onClick="javascript:doSubmit('consulta');">Volver</a>
									</c:if>
									
									<!-- Pet. 22208 ** MODIF TAM (05.03.2018) ** Fin -->
								</td>
							</table>
						</c:if>
						
						<!-- Si viene del paso a definitiva solo se muestran los botones de imprimir y volver -->
						<c:if test="${fromPasoADefinitiva}">
							<c:if test="${mostrarForzaDef}">
								<c:if test="${empty sessionScope.codTerminal || empty idInternoPe}">
									<a href="javascript:grabarDefFueraContratacion(${idpoliza});" class="bot">Forzar confirmaci&oacute;n</a>
					        	</c:if>
						        <c:if test="${not empty sessionScope.codTerminal && not empty idInternoPe}">
						       		<a href="javascript:uploadDocAndSign();" class="bot">Forzar confirmaci&oacute;n</a>
						        </c:if>								
							</c:if>
							<a class="bot" href="#" onClick="javascript:imprimirErr('imprimirErrores');">Ver errores</a>
							<!-- La llamada del 'Volver' depende de si venimos del listado de utilidades o del ciclo de poliza -->
							<c:if test="${cicloPoliza =='cicloPoliza'}">
								<a class="bot" href="#" onClick="javascript:doSubmit('consulta');">Volver</a>
							</c:if>
							<c:if test="${cicloPoliza eq null or cicloPoliza !='cicloPoliza'}">
								<a class="bot" href="#" onClick="javascript:volverUtilidades();">Volver</a>
							</c:if>
						</c:if>
					</td>
					<!--  Pet. 7003325 Inicio  -->
					<!-- Añadimos un nuevo boton solo si la poliza esta en estado Enviada Correcta -->
					<td align="right">
						<c:if test="${swConfirmacion}">
							<c:if test="${cicloPoliza =='cicloPoliza'}">
								<c:if test="${estadoPoliza == 8 and empty idInternoPe and docFirmada != 'S'}">  
									<a class="bot" href="#" onClick="javascript:cargaDocFirmada('${idpoliza}','${referencia}','${plan}','${tipoPoliza}','2');">Cargar documentaci&oacute;n</a>
								</c:if>
							</c:if>			
						</c:if>
					</td>
					<!--  Pet. 22208 ** MODIF TAM (05.03.2018) ** Fin  -->
					<!--  Pet. 22208 ** MODIF TAM (05.03.2018) ** Inicio  -->
					<!-- Añadimos un nuevo boton solo si no es linea de ganado de "imprimir Reducida -->
					<td align="right">
						<c:if test="${swConfirmacion}">
						    <!-- MODIF TAM (11.04.2018) - Resolucion de Incidencias --!>
							<!--  <a class="bot" href="#" onClick="javascript:volverUtilidades();">Salir</a></a>-->
							<c:if test="${cicloPoliza =='cicloPoliza'}">
								<a class="bot" href="#" onClick="javascript:salirSwConfirmacion();">Salir</a>
							</c:if>
							<c:if test="${cicloPoliza eq null or cicloPoliza !='cicloPoliza'}">
								<a class="bot" href="#" onClick="javascript:volverUtilidades();">Salir</a>
							</c:if>
						</c:if>
					</td>
					<!--  Pet. 22208 ** MODIF TAM (05.03.2018) ** Fin  -->
				</tr>
			</table>
		</div>
		<!-- Contenido de la pagina -->
		<div id="imprimirErrores">	
			<div class="conten" style="padding:3px;width:97%">
			    <!--  Pet. 22208 ** MODIF TAM (02.03.2018) ** Inicio -->
			    <c:if test="${not swConfirmacion}">
				   <p class="titulopag" align="left">Validaci&oacute;n Realizada</p>
				</c:if>
			    <c:if test="${swConfirmacion}">
				   <p class="titulopag" align="left">Confirmaci&oacute;n de la P&oacute;liza con Agroseguro</p>
				</c:if>
			    
			    <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			    
			    <!--  Pet. 22208 ** MODIF TAM (02.03.2018) ** Fin -->
			    				
				<!--// DAA 26/04/12 -->
				<c:forEach items="${resultado}" var="acusePolizaEntry" varStatus="loopResultado">
					<c:set var="comparativaPolizaId" value="${acusePolizaEntry.key}"/>
					<c:set var="resultadoWS" value="${acusePolizaEntry.value}"/>

					<c:set var="keyForCattleContent" value="${comparativaPolizaId.idpoliza}${comparativaPolizaId.lineaseguroid}${comparativaPolizaId.codmodulo}${comparativaPolizaId.idComparativa}"/>
					<c:set var="keyCodModulo" value="${keyForCattleContent}modulo"/>
					<c:set var="keyDesModulo" value="${keyForCattleContent}descModulo"/>
					
					<fieldset style="width:97%">
						<legend class="literal">M&Oacute;DULO ${cabeceras[keyCodModulo]} - ${cabeceras[keyDesModulo]}</legend>
						${cabeceras[keyForCattleContent]}				            					
	
				        <table width="100%">
							<tr>
								<td class="centrado" style="font-size: 14px;color: #FF0000;" colspan="4">
									<c:out value="${sErrorHeader[loopResultado.index]}"/>
								<td>
							</tr>
						</table>
						
						<form name="dummy" action="" method="post" id="">
							<input type="hidden" name="calculable${keyForCattleContent}" value="${arrBotonCalculo[loopResultado.index] or usuarioPerfilCero}"/>
						</form>
						
						<c:set var="acuse" value="${resultadoWS.acuseRecibo}" />
						<c:forEach items="${acuse.documentoArray}" var="documento" varStatus="loopAcuse">
							<c:set var="errores" value="${documento.errorArray}" scope="request"/>
							<c:set var="errLength" value="${fn:length(documento.errorArray)}" scope="request"/>
							<display:table requestURI="webservices.html" class="LISTA" 
										   decorator="com.rsi.agp.core.decorators.ModelTableDecoratorListaParcelas" 
										   defaultsort="0" defaultorder="ascending" sort="list" 
									 	   name="errores" list="errorList" id="error" pagesize="${errLength}">
								<display:setProperty name="paging.banner.onepage" value="&nbsp;"/>
								<display:setProperty name="paging.banner.item_name" value="error"/>
								<display:setProperty name="paging.banner.items_name" value="errores"/>
								<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="C&oacute;digo del Error" sortable="true">
									<c:out value="${error.codigo}">-</c:out>
								</display:column>
								<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="Tipo de Error" sortable="false">
									<c:if test="${error.tipo eq 1}">
										<img src="jsp/img/displaytag/cancel.png" alt="Rechazado" title="Rechazado" />
									</c:if>
									<c:if test="${error.tipo eq 2}">
										<img src="jsp/img/displaytag/warning.gif" alt="Con Errores" title="Con Errores"/>
									</c:if>
									<c:if test="${error.tipo eq 3}">
										<img src="jsp/img/displaytag/accept.png" alt="Correcto" title="Correcto"/>
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
								<display:column class="literal" style="text-align:left;" headerClass="cblistaImg" title="${columnaNumero}">
									<c:set var="xpath" value="${fn:substring(error.localizacion.xpath, fn:indexOf(error.localizacion.xpath, '@numero=\')+9, fn:indexOf(error.localizacion.xpath, ']/')-1)}"/>
									<c:set var="xpath2" value="${fn:substring(error.localizacion.xpath, fn:indexOf(error.localizacion.xpath, '@hoja=\')+7, fn:indexOf(error.localizacion.xpath, ']/')-1)}"/>
									<c:if test="${not esLineaGanado}">
										<c:catch var="e">
		              						<fmt:parseNumber var="j" type="number" value="${xpath2}" />
		              						<c:out value="${j}" escapeXml="false" />
		            					</c:catch>
		           					-			            					
		           					</c:if>
									<c:catch var="e">
		             						<fmt:parseNumber var="i" type="number" value="${xpath}" />
		             						<c:out value="${i}" escapeXml="false" />			              						
		           					</c:catch>
		           					<c:out value=" " escapeXml="false" />
								</display:column>
								<%-- Pet. 22208 ** MODIF TAM (10.04.2018) - Resolucion de Incidencias *I* --%>
								<%-- Si venimos del Sw confirmacion no hay que mostrar la columna de acciones --%>
							    <c:if test="${not swConfirmacion }">
								<%-- Pet. 22208 ** MODIF TAM (10.04.2018) - Resolucion de Incidencias *F*--%>
	 								<display:column class="literal" style="text-align:left;" headerClass="cblistaImg" title="Acciones">
										<c:if test="${not esLineaGanado}">
											<a href="javascript:updateParcela('${idpoliza}','${j}','${i}')">
												<c:if test="${not empty i && not empty j}">
													<img src="jsp/img/displaytag/edit.png" alt="Editar" title="Editar" />
												</c:if>
											</a>
										</c:if>
										<c:if test="${esLineaGanado}">
											<a href="javascript:updateExplotacion('${idpoliza}','${i}')">
												<c:if test="${not empty i}">
													<img src="jsp/img/displaytag/edit.png" alt="Editar" title="Editar" />
												</c:if>
											</a>
										</c:if>
									</display:column>
								</c:if>														
							</display:table>
						</c:forEach>
						<c:if test="${perfil == 0}">
							<div style="width:20%; text-align: right; margin-left: auto; margin-right: 5px" id="divImprimir"
								alt="Descargar XML Validaci&oacute;n" title="Descargar XML Validaci&oacute;n">
								<c:if test="${not esLineaGanado}">
						 			<a id="btnImprimirExcel" style="text-decoration: unset;" href="javascript:xmlValidation('${comparativaPolizaId.filacomparativa}')">
						 		</c:if>
						 		<c:if test="${esLineaGanado}">
						 			<a id="btnImprimirExcel" style="text-decoration: unset;" href="javascript:xmlValidation('${comparativaPolizaId.idComparativa}')">
						 		</c:if>	
						 			<img width="16" height="16" src="jsp/img/jmesa/csv.gif"/>
						 			<span style="color: gray; font-family: tahoma, verdana, arial; font-size: 11px; font-weight: bold; vertical-align: top" >
						 				XML Validaci&oacute;n
						 			</span>
					 			</a>	
							</div>
						</c:if>
					</fieldset>
				</c:forEach>
			</div>	
		</div>	
		<form action="webservices.html" method="post" name="main" id="main">
			<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}"/>
			<input type="hidden" name="operacion" value="validar" />
			<input type="hidden" name="idpoliza" id="consulta_idPoliza" value="${idpoliza}"/>
			<input type="hidden" name="origenllamada" id="origenllamada" value="${origenllamada}"/>
			<input type="hidden" name="validComps" id="validComps" value=""/>
		</form>
		<%-- Pet. 22208 ** MODIF TAM (12.03.2018) ** Inicio --%>
		<form action="grabacionPoliza.html" method="post" name="imprimir" id="imprimir">
			<input type="hidden" id="operacion" name="operacion"/>
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
		</form>
		
		<!-- MODIF TAM (11.04.2018) -Resolucion Incidencias -->
		<form action="grabacionPoliza.html" method="post" name="mainSalir" id="mainSalir">
			<input type="hidden" id="operacion" name="operacion"/>
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
		</form>
		
		<%-- Pet. 22208 ** MODIF TAM (12.03.2018) ** Fin  --%>
		
		<form name="consulta" id="consulta" action="seleccionPoliza.html" method="post">
			<input type="hidden" name="operacion" value="listParcelas" />
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
			<input type="hidden" name="numhoja" id="numhoja" value=""/>
			<input type="hidden" name="numparcela" id="numparcela" value=""/>
			<input type="hidden" name="cambioProvisional" id="cambioProvisional" value="true"/>
		</form>
		<form name="consultaRev" id="consultaRev" action="revProduccionPrecio.html" method="post">
			<input type="hidden" name="operacion" value="" />
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
		</form>	
		<!-- Formulario para el paso a definitiva simple -->
		<form name="pasarADefinitiva" id="pasarADefinitiva" action="pasoADefinitiva.html" method="post" commandName="polizaDefinitiva">
			<input type="hidden" name="method" id="method" value="doPasarADefinitiva"/>
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
			<input type="hidden" name="resultadoValidacion" id="resultadoValidacion"/>
			<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion"/>
			<input type="hidden" name="firmada" id="firmada"/>
			<input type="hidden" name="actualizarSbp" id="actualizarSbp"/>
			<input type="hidden" name="esCpl" id="esCpl" value="false"/>
			<input type="hidden" name="cicloPoliza" id="cicloPoliza" value="${cicloPoliza}"/>	
			<input type="hidden" name="imprimirReducida" id="imprimirReducida" />
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" />
			<%--Pet. 22208 ** MODIF TAM (04.04.2018) - Resolucion Incidencias --%>
            <input type="hidden" name="swConfirmacion" id="swConfirmacion" value="${ForzarswConfirmacion}" />
            <input type="hidden" id="idInternoPe" name="idInternoPe" value="${idInternoPe}" />	
            <input type="hidden" id="codTerminal" name="codTerminal" value="${sessionScope.codTerminal}" />
            <input type="hidden" id="idDocumentum" name="idDocumentum" value="${idDocumentum}" />	
            <input type="hidden" id="codUsuario" name="codUsuario" value="${codUsuario}" />	
            <input type="hidden" id="firmaDiferida" name="firmaDiferida" value="${firmaDiferida}" />
            <input type="hidden" id="autoCompFirma" name="autoCompFirma" value="${autoCompFirma}" />
            <input type="hidden" id="docFirmada" name="docFirmada" value="${docFirmada}"/>						
		</form>	
		<!-- MPM - 21-05-12 -->
		<!-- Formulario para la vuelta al listado de utilidades -->
		<form name="volver" id="volver" action="utilidadesPoliza.html" method="post">
			<input type="hidden" name="operacion" id="operacion" value="volver" />
		</form>
		<!-- MPM - 08-03-13 -->		
		<!-- Formulario la redireccion a la pantalla de datos de parcela -->
		<form:form name="datosParcela" id="datosParcela" action="datosParcela.html" method="post">
				<input type="hidden" name="method" id="methodDP" value="doEditarErrores"/>
				<input type="hidden" name="idpoliza" id="idpolizaDP"/>
				<input type="hidden" name="numhoja" id="numhojaDP"/>
				<input type="hidden" name="numparcela" id="numparcelaDP"/>
		</form:form>
		
		<form:form name="datosExplotacion" id="datosExplotacion" action="datosExplotaciones.html" method="post">
			<input type="hidden" name="method" id="methodDE" value="doEditar"/>
			<input type="hidden" name="idPolizaExplotaciones" id="idPolizaExplotaciones" />
			<input type="hidden" name="numexplotacion" id="numexplotacion"/>
			<input type="hidden" name="origenLlamada" id="origenllamada" value="erroresValidacion"/>
		</form:form>
		
		<!-- Formulario para volver a la pantalla de comparativas - Solo para polizas de ganado -->
		<form:form method="post" name="formComparativas" id="formComparativas" action="seleccionComparativasSW.html" commandName="poliza">
			<input type="hidden" name="idpoliza" id="idpolizaComparativas" value="${idpoliza}"/>
			<input type="hidden" name="vieneDeImportes" id="vieneDeImportesComparativas" value="false"/>
			<input type="hidden" name="modoLectura" id="modoLectura" value="${modoLectura}" />
		</form:form>
		
		<form method="post" name="formUtilidades" id="formUtilidades" action="utilidadesXML.run">
			<input type="hidden" name="method" id="methodUtl" />
			<input type="hidden" name="idPoliza" id="idPoliza" value="${idpoliza}"/>
			<input type="hidden" name="filaComparativa" id="filaComparativa"/>
			<input type="hidden" name="operacion" value="validar" />
		</form>
		
		<c:if test="${calcular}">
		    <script>
		   		doWorkCalculo();
		    </script>
		</c:if>
		
		<!-- P0073325 - RQ.10, RQ.11 y RQ.12 -->
		<%@ include file="/jsp/moduloPolizas/polizas/cargaDocFirmada.jsp"%>
	
	</body>
</html>
