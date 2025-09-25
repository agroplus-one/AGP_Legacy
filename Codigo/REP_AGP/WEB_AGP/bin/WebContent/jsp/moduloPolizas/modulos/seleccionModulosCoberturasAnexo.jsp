<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Selección de comparativa</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
				
		<script type="text/javascript" src="jsp/js/util.js"></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		
		<!-- Funciones JS para la gestión de módulos y comparativas -->
		<%@ include file="utlSeleccionModulosCoberturas.jsp"%>
	
	</head>	
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">	
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="../../common/static/datosCabeceraTaller.jsp" %>
		
	<!-- BOTONERA -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td>
									<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
								</td>
								<c:if test="${requestScope.modoLectura=='false'}">
									<td>
										<a class="bot" id="btnContinuar" href="javascript:continuar(false);">Guardar</a>
									</td>	
									&nbsp;
									<td>
										<a class="bot" id="btnGuardarEnviar" href="javascript:continuar(false,true);">Guardar y enviar</a>
									</td>											
								</c:if>
							</tr>
						</tbody>
					</table>
				</td>
			</tr>
		</table>
	</div>	
	
	<!-- Volver a la pantalla de listado de explotaciones de anexo -->
	<form:form name="formVolver" id="formVolver" action="listadoExplotacionesAnexo.html" method="post" commandName="anexo">
			<input type="hidden" id="methodVolver" name="method" value="doPantallaListaExplotacionesAnexo">
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
			<input type="hidden" name="anexoModificacionId" id="anexoModificacionId" value="${anexo.id}"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="datosExplotacionAnexo"/>
			<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
			
			<c:if test="${requestScope.modoLectura=='true'}">
				<input type="hidden" name="modoLectura" id="modoLectura" value="true"/>
			</c:if>
			<input type="hidden" name="primerAcceso" id="primerAcceso" value=""/>
	</form:form>
	
	
	<!-- Contenido de la página -->
	<form:form method="post" name="formComparativas" id="formComparativas" action="seleccionComparativasAnexoSW.html" commandName="anexo">
	
		<form:hidden path="id" id="idAnexoGuardarComparativas"/>
		<form:hidden path="cupon.id" id="idCuponGuardarComparativas"/>
		<form:hidden path="poliza.idpoliza" id="idPolizaGuardarComparativas"/>
		<input type="hidden" id="method" name="method" value="doGuardarComparativasAnexo" />
		<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}"/>
		<input type="hidden" name="validarAnexo" id="validarAnexo" />
		<input type="hidden" name="redireccion" value="explotaciones" />
		
		<!-- Campos ocultos para enviar los datos al controlador para almacenarlos -->
		<input type="hidden" name="renovElegidas" id="renovElegidas" value=""/>
		<input type="hidden" name="coberturasElegidas" id="coberturasElegidas" value="" size="100"/>
		
		<input type="hidden" name="msgVinculaciones" id="msgVinculaciones" value=""/>
		<input type="hidden" id="modoLectura" name="modoLectura" value="${requestScope.modoLectura}">
		<input type="hidden" name="vieneDeImportes" id="vieneDeImportes" value="${vieneDeImportes}">
		<input type="hidden" name="msgBasicasElegibles" id="msgBasicasElegibles" value=""/>
		
		<!-- Pet. 63485 ** MODIF TAM (04/09/2020) ** Inicio -->
		<input type="hidden" name="maxComparativas" id="maxComparativas" value="1"/>
		<input type="hidden" name="listaVinculacionesAgri" id="listaVinculacionesAgri" value="${listaVinculacionesAgri}"/>
		<input type="hidden" name="camposBloqVincAgri" id="camposBloqVincAgri" value="${camposBloqVincAgri}"/>
		<input type="hidden" name="contComparativas" id="contComparativas" value="1"/>
		<!-- Pet. 63485 ** MODIF TAM (04/09/2020) ** Fin -->
		
		<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
		
		<div class="conten">
			<p class="titulopag" align="left">Modificación de coberturas</p>
			
			<!-- Cabecera de mensajes de validación -->
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<!-- Tabla de comparativas correspondiente al anexo -->
			<c:set var="modulo" value="${moduloViewAnexo}"/>
			<c:set var="msgDescModulo" value="Coberturas del Anexo: "/>
			<c:set var="tabPlz" value="false"/>
			<c:set var="renovableSoloLectura" value="true"/>
			
			<c:if test="${modulo ne null}">
				<%@ include file="/jsp/moduloPolizas/modulos/tablaCoberturasModulo.jsp"%>
			</c:if>
			
			
			
			<!-- Carga las coberturas elegidas y guardadas en BBDD para el anexo -->
			<c:forEach items="${listaCoberturasElegidas}" var="cobElegida">
				<!-- Si es un riesgo cubierto elegible -->
				<c:if test="${cobElegida.codconcepto eq 363}">
					<c:set var="idCheck" value="check_${cobElegida.idComparativa}_${cobElegida.codmodulo}_${cobElegida.filamodulo}_${cobElegida.codconceptoppalmod}_${cobElegida.codriesgocubierto}_${cobElegida.codconcepto}_${cobElegida.filacomparativa}:${cobElegida.codvalor}"/>
					<script>
						listaRiesgosElegidos.push ('${idCheck}');
					</script>
				</c:if>
				<!-- Si es una condición de cobertura -->
				<c:if test="${(cobElegida.codconcepto ne 363) and (cobElegida.codconcepto ne 1079)}">
				    <c:set var="idSelect" value="select_${cobElegida.idComparativa}_${cobElegida.codmodulo}_${cobElegida.filamodulo}_${cobElegida.codconceptoppalmod}_${cobElegida.codriesgocubierto}_${cobElegida.codconcepto}_${cobElegida.filacomparativa}:${cobElegida.codvalor}"/>
					
					<script>
						listaCondicionesCoberturasElegidas.push ('${idSelect}');
					</script>
				</c:if>
				<!-- Si es una tipologia del asegurado -->
				<c:if test="${cobElegida.codconcepto eq 1079}">
				    <c:set var="idTipologia" value="tipologiaAseguradoAnex_${cobElegida.idComparativa}:${cobElegida.codvalor}"/>
					
					<script>
						tipologiaAsegurado = '${idTipologia}';
					</script>
				</c:if>
				
			</c:forEach>
			
			
			<!-- Tabla de comparativas correspondiente a la póliza asociada al anexo-->
			<c:set var="modulo" value="${moduloViewPoliza}"/>
			<c:set var="msgDescModulo" value="Coberturas de la Póliza: "/>
			<c:set var="tabPlz" value="true"/>
			<c:set var="renovableSoloLectura" value="true"/>
			
			<c:if test="${modulo ne null}">
				<%@ include file="/jsp/moduloPolizas/modulos/tablaCoberturasModulo.jsp"%>
			</c:if>
	
			<!-- Si no se han elegido coberturas del anexo anteriormente, se marcan las elegidas en la póliza -->
			<c:if test="${fn:length(listaCoberturasElegidas) == 0}">
				<script>
					cargaElegidosPolizaEnAnexo();
				</script>
			</c:if>
			
		</div>
	</form:form>
	
	<%@ include file="/jsp/js/draggable.jsp"%>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
</body>
</html>