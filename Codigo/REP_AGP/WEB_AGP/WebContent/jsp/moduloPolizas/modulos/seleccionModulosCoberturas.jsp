<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Selección de comparativa</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
				
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		
		<!-- Funciones JS para la gestión de módulos y comparativas -->
		<%@ include file="utlSeleccionModulosCoberturas.jsp"%>
		
		<!-- JS propia de la pagina -->
		<script type="text/javascript" src="jsp/moduloPolizas/modulos/seleccionModulosCoberturas.js" ></script>	
	</head>	
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">	
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<c:choose>
			<c:when test="${modoLectura == 'modoLectura' && vieneDeUtilidades == 'true'}">
				<%@ include file="/jsp/common/static/datosCabeceraConsulta.jsp" %>	
			</c:when>
			<c:otherwise>
				<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
			</c:otherwise>
		</c:choose>
	
	<!-- BOTONERA -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td>
								   <!--  Pet. 63485 ** MODIF TAM (17.07.2020) -->
								   <!--  Si es poliza de Ganado que vuelva a Explotaciones, sino Parcelas -->
									<c:if test="${esPolizaGanado}">
										<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
									</c:if>	
									<c:if test="${not esPolizaGanado}">
										<a class="bot" id="btnVolverParc" href="javascript:volverParcela();">Volver</a>
									</c:if>	
								</td>
								<td>
									<a class="bot" id="btnContinuar" href="javascript:continuar(true);">Continuar</a>
								</td>											
							</tr>
						</tbody>
					</table>
				</td>
			</tr>
		</table>
	</div>	
	
	<!-- Volver a la pantalla de listado de explotaciones -->
	<form:form name="formVolver" id="formVolver" action="listadoExplotaciones.html" method="post" commandName="poliza">
		<input type="hidden" id="methodVolver" name="method" size="5" value="doPantallaListaExplotaciones">
		<form:hidden path="idpoliza" id="idPolizaVolver"/>
		<form:hidden path="linea.lineaseguroid" id="lineaseguroidVolver"/>
		<input type="hidden" id="origenllamadaVolver" name="origenllamada" value="datosExplotaciones">
		<input type="hidden" id="modoLectura" name="modoLectura" value="${requestScope.modoLectura}">
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}"/>	
	</form:form>
	
	<!--  Pet. 63485 ** MODIF TAM (17.07.2020) ** Inicio  -->
	<form name="formVolverParcelas" id ="formVolverParcelas" action="seleccionPoliza.html" method="post" id="consulta">
		<input type="hidden" name="operacion" id="operacion" value="listParcelas" /> 
		<input type="hidden" name="idpoliza" id="idpolizaVol"/> 
		<input type="hidden" name="cambioProvisional" id="cambioProvisional" value="true" /> 
		<input type="hidden" name="modoLectura" id="modoLectura" value="${requestScope.modoLectura}" /> 
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}" />
	</form>
	
	<form name="formVerFich" id="formVerFich" action="seleccionComparativasSW.html" commandName="poliza">
			<input type="hidden"  name="idpoliza" id="idpolizaFich"/>
			<input type="hidden"  name="codModulo" id="codModuloFich"/>
			<input type="hidden" id="verFich_method" name="method" size="5" value="doDescargarFichero">
	    </form>	
	<!--  Pet. 63485 ** MODIF TAM (17.07.2020) ** Fin  -->
	
	<form name="consultaDetallePoliza" id="consultaDetallePoliza" action="consultaDetallePoliza.html" method="post">
		<input type="hidden" name="method" id="method"/>
		<input type="hidden" name="idpoliza" id="idpoliza"/>
		<input type="hidden" name="modoLectura" id="modoLectura" value="${requestScope.modoLectura}"/>
		<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}"/>
	</form>
	
	<!-- <fieldset> -->
	<!-- Contenido de la página -->
	<div class="conten" style="padding:3px;width:97%">
		<form:form method="post" name="formComparativas" id="formComparativas" action="seleccionComparativasSW.html" commandName="poliza">
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}"/>
			<form:hidden path="idpoliza" id="idpoliza"/>
			<form:hidden path="linea.lineaseguroid" id="lineaseguroid"/>
			<input type="hidden" id="methodComparativas" name="method" value="doGuardarComparativas" />
			
			<!-- Campos ocultos para enviar los datos al controlador para almacenarlos -->
			<input type="hidden" name="renovElegidas" id="renovElegidas" value=""/>
			<input type="hidden" name="coberturasElegidas" id="coberturasElegidas" value=""/>
			
			<input type="hidden" name="msgVinculaciones" id="msgVinculaciones" value=""/>
			<input type="hidden" id="modoLectura" name="modoLectura" value="${requestScope.modoLectura}"/>
			<input type="hidden" name="vieneDeImportes" id="vieneDeImportes" value="${vieneDeImportes}"/>
			<input type="hidden" name="maxComparativas" id="maxComparativas" value="${maxComparativas}"/>
			<input type="hidden" name="contComparativas" id="contComparativas"/>
			<input type="hidden" name="modulos" id="modulos" value="${modulos}"/>
			<input type="hidden" name="listaVinculacionesAgri" id="listaVinculacionesAgri" value="${listaVinculacionesAgri}"/>
			<input type="hidden" name="camposBloqVincAgri" id="camposBloqVincAgri" value="${camposBloqVincAgri}"/>
			<input type="hidden" name="compVisiblesBBDD" id="compVisiblesBBDD" value="${compVisiblesBBDD}"/>
			<input type="hidden" name="esPolizaGanado" id="esPolizaGanado" value="${esPolizaGanado}"/>
			<p class="titulopag" align="left">Selección de comparativas</p>
		
			<!-- Cabecera de mensajes de validación -->
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<!-- Pinta una tabla de comparativas por cada módulo almacenado en el listado -->
			<c:forEach items="${listaMod}" var="modulo">
				<c:set var="tabPlz" value="false"/>
				<c:set var="renovableSoloLectura" value="false"/>
				<%@ include file="/jsp/moduloPolizas/modulos/tablaCoberturasModulo.jsp"%>			
			</c:forEach>	
			
			<!-- Carga las coberturas elegidas y guardadas en BBDD -->
			<c:forEach items="${listaCoberturasElegidas}" var="cobElegida">
				<!-- Si es un riesgo cubierto elegible -->
				<c:if test="${cobElegida.id.codconcepto eq 363}">    
					<c:set var="idCheck" value="check_${cobElegida.id.codmodulo}_${cobElegida.id.idComparativa}_${cobElegida.id.filamodulo}_${cobElegida.id.codconceptoppalmod}_${cobElegida.id.codriesgocubierto}_${cobElegida.id.codconcepto}_${cobElegida.id.filacomparativa}:${cobElegida.id.codvalor}"/>
					<script>
						listaRiesgosElegidos.push('${idCheck}');
					</script>
				</c:if>
				<!-- Si es una condición de cobertura -->
				<c:if test="${cobElegida.id.codconcepto ne 363}">
					<c:set var="idSelect" value="select_${cobElegida.id.codmodulo}_${cobElegida.id.idComparativa}_${cobElegida.id.filamodulo}_${cobElegida.id.codconceptoppalmod}_${cobElegida.id.codriesgocubierto}_${cobElegida.id.codconcepto}_${cobElegida.id.filacomparativa}:${cobElegida.id.codvalor}"/>
					<script>
						listaCondicionesCoberturasElegidas.push('${idSelect}');
					</script>
				</c:if>
			</c:forEach>
			<c:if test="${esPolizaGanado}">
				<%@ include file="/jsp/moduloPolizas/modulos/calculoGanadoRC.jsp"%>
			</c:if>	
		</form:form>
	</div>
	<!-- </fieldset> -->
	<%@ include file="/jsp/js/draggable.jsp"%>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
</body>
</html>