<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/moduloPolizas/polizas/popupRecalcular.jsp"%>
<%@ include file="/jsp/moduloUtilidades/popupImportacionPoliza.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg">
		<fmt:message key="numElementsPag"/>
	</c:set>
</fmt:bundle>
<fmt:bundle basename="agp">
	<c:set var="numRegImpresion"><fmt:message key="impresionnumReg"/></c:set>
	<c:set var="alertaImpresion"><fmt:message key="listados.msgError"/></c:set>
</fmt:bundle>

<html>
	<head>
		<title>Acciones sobre p&oacute;lizas</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />

		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
        <script type="text/javascript" src="jsp/js/calendar.js"></script>
        <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
        <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<c:if test="${perfil == 0}">
			<script type="text/javascript" src="jsp/moduloPolizas/polizas/pagoMasivo.js"></script>
			<script type="text/javascript" src="jsp/moduloPolizas/polizas/cambioClase.js"></script>
			<script type="text/javascript" src="jsp/moduloPolizas/polizas/cambiarTitular.js"></script>
			<script type="text/javascript" src="jsp/moduloPolizas/polizas/cambiarModulo.js"></script>
		</c:if>		
		<script type="text/javascript" src="jsp/moduloUtilidades/informesDetalle.js"></script>
		<script type="text/javascript" src="jsp/moduloUtilidades/utilidades.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/restaurarParams.js"></script>
		<script type="text/javascript" src="jsp/js/utilesIBAN.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/cambiarIBAN.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/datosAval.js"></script>
		<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
        <script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
        <script type="text/javascript" src="jsp/moduloPolizas/polizas/cargaDocFirmada.js" ></script>
        
		<%@ include file="/jsp/js/draggable.jsp"%>

		<script type="text/javascript" charset="ISO-8859-1">
		
			$(document).ready(function(){
				<c:if test="${empty listaPolizas}">					
					$('#divImprimir').hide(); 									
				</c:if> 				
			});
			
			function setDefaultEntidad(){
				$('#entidad').val("${usuario.oficina.entidad.codentidad}");
				$('#desc_entidad').val("${usuario.oficina.entidad.nomentidad}");
			}
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">		
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons" width="100%">
			<table width="100%" cellspacing="2" cellpadding="2">
				<!-- <tbody> -->
					<tr >
						<td align="left" colspan="2">
							<a class="bot" id="btnBorradoMasivo" href="javascript:showPopUpMasivosUtilidades();">Operaciones Masivas</a>
							
							<!-- DNF PET-63482 15/04/2021  ADD BOTON ALTA POLIZA-->
							<c:if test="${perfil == 0}">
								<a class="bot" id="btnAltaPoliza" style="margin-Left: 40%;" href="javascript:showPopUpImportacionPoliza();">Alta Poliza</a>
							</c:if>
							<!-- FIN DNF PET-63482 15/04/2021 -->
							
						</td>
						<td align="right" width="25%" colspan="2">
						 	<c:if test="${externo == 0}"><!--  es interno -->
						 		<c:if test="${perfil == 0 || perfil == 1 || perfil == 5}">
									<a class="bot" id="btnRestaurarParams" href="javascript:restaurarParams();">Restaurar Params.</a>
								</c:if>
							</c:if>
							<c:if test="${perfil != 4}">
								<a class="bot" id="btnSobreprecio" href="javascript:altaSbp();">Sobreprecio</a>
							</c:if>
						</td>
						<td align="right" width="25%" colspan="2">
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							<a class="bot" href="javascript:limpiar();">Limpiar</a>
						</td>
					</tr>
					
				<!-- </tbody>  -->
			</table>
		</div> 
		
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px; width:102%">
		<p class="titulopag" align="left">Acciones sobre p&oacute;lizas</p>
		
			<form name="doInforme" id="doInforme" action="informesDetalle.html" method="post" >	
				<input type="hidden" name="method" id="method" value="doInformesDetalle"/>
				<input type="hidden" name="idPoliza" id="informeIdPoliza"/>	
				<input type="hidden" name="tipo" id="informeTipo"/>	
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="utilidadesPoliza"/>
			</form>
			
			<form name="altaPolizaSbp" id="altaPolizaSbp" action="simulacionSbp.html" method="post" commandName="polizaSbp">	
				<input type="hidden" name="method" id="method"/>
				<form:hidden path="polizaSbp.polizaPpal.idpoliza" id="idPolizaPpal"/>	
				<form:hidden path="polizaSbp.polizaCpl.idpoliza" id="idPolizaCpl"/>
				<form:hidden path="polizaSbp.polizaPpal.estadoPoliza.idestado" id="idEstadoPpal"/>	
				<form:hidden path="polizaSbp.polizaCpl.estadoPoliza.idestado" id="idEstadoCpl"/>
				<form:hidden path="polizaSbp.incSbpComp" id="incSbpComp"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="utilidadesPoliza"/>
			</form>
			
			<!-- Formulario para redirigir a ListadoPolizasSbp -->
			<form name="ListadoPolSbpForm" id="ListadoPolSbpForm" action="consultaPolizaSbp.run" method="post">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idPolizaSbp" id="idPolizaSbp"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="utilidadesPoliza"/>
			</form>
			
			<!-- MPM - 04-05-12 -->
			<!-- Formulario para el paso a definitiva simple -->
			<form name="pasarADefinitiva" id="pasarADefinitiva" action="pasoADefinitiva.html" method="post" commandName="polizaDefinitiva">
				<input type="hidden" name="method" id="method" value="doPasarADefinitiva"/>
				<form:hidden path="polizaDefinitiva.idpoliza" id="idpoliza"/>
				<input type="hidden" name="resultadoValidacion" id="resultadoValidacion"/>
				<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion"/>
				<input type="hidden" name="actualizarSbp" id="actualizarSbp"/>				
				<input type="hidden" name="esCpl" id="esCpl" value="false"/>
				<input type="hidden" name="cicloPoliza" id="cicloPoliza" value=""/>
				<input type="hidden" name="fpago" id="fpago" />		
				<input type="hidden" name="numeroAval" id="numeroAval" value="" />
				<input type="hidden" name="importeAval" id="importeAval" value="" />
				<input type="hidden" name="esSaeca" id="esSaeca" value=""/>						
			</form>
			<!-- Formulario para el paso a definitiva múltiple -->
			<form name="pasarADefinitivaMultiple" id="pasarADefinitivaMultiple" action="pasoADefinitiva.html" method="post">
				<input type="hidden" name="method" id="method" value="doPasarADefinitivaMultiple"/>				
				<input type="hidden" name="resultadoValidacion" id="resultadoValidacion"/>
				<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion"/>
				<input type="hidden" name="actualizarSbp" id="actualizarSbp"/>				
				<input type="hidden" name="esCpl" id="esCpl" value="false"/>				
			<input type="hidden" name="idsRowsChecked" id="idsRowsChecked"/>
				<input type="hidden" name="fpago" id="fpago" />
				<input type="hidden" name="esSaeca" id="esSaeca" value=""/>
			</form>
			<!-- fin MPM - 04-05-12 -->
			
			<!-- Formulario para ver detallePoliza en modo lectura -->
			<form name="consultaDetallePoliza" id="consultaDetallePoliza" action="consultaDetallePoliza.html" method="post">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idpoliza" id="idpoliza"/>
				<input type="hidden" name="modoLectura" id="modoLectura" value="modoLectura"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="true"/>
				<input type="hidden" name="operacion" id="operacion_detallePol" value="listParcelas"/>
			</form>
			
			<!-- Formulario para editar una poliza desde utilidades -->
			<form name="editaPolizaUtilidades" id="editaPolizaUtilidades" action="editaPolizaUtilidades.html" method="post">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idpoliza" id="idpoliza"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="true"/>
				<input type="hidden" name="operacion" id="operacion" value=""/>
			</form>
			<!-- Formulario para editar una poliza Complementaria desde utilidades -->
			<form name="polizaCompl" id="polizaCompl" action="polizaComplementaria.html" method="post">	
				<input type="hidden" name="refPol" id="refPol" />	
				<input type="hidden" name="lineaseguroidCpl" id="lineaseguroidCpl" />
				<input type="hidden" name="idpolizaCpl" id="idpolizaCpl" />		
				<input type="hidden" name="method" id="method"/>
				<!-- input type="hidden" name="idPol" id="idPol" value="${idPolPr}"/ -->
				<input type="hidden" name="modoLecturaCpl" id="modoLecturaCpl" value=""/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="true"/>
			</form>
			
			<!-- Formulario para seguimiento del Estado de la póliza -->
			<form name="seguimientoPoliza" id="seguimientoPoliza" action="seguimientoPoliza.html" method="post">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idpoliza" id="idpoliza"/>
			</form>
			
			<!-- Pet. 43417 ** MODIF TAM (17.09.2021) ** Inicio -->
			<!-- Formulario para redirigir a ListadoPolizasSbp -->
			<form name="SiniestrosGanado" id="SiniestrosGanado" action="siniestrosGanado.html" method="post">
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="idPolizaSinGan" id="idPolizaSinGan"/>
			</form>
			
			<!-- Formulario para polizas de sobreprecio -->
			<form name="seleccionPoliza" id="seleccionPoliza" action="seleccionPoliza.html" method="post">
				<input type="hidden" name="operacion" id="operacion"/>
				<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${vieneDeUtilidades}"/>
				<input type="hidden" name="borrarPolizaSbp" id="borrarPolizaSbp" value=""/>
			</form>
			<form:form name="main3" id="main3" action="utilidadesPoliza.html" method="post" commandName="polizaBean">
				
				<input type="hidden" name="isganado" id="isganado" value=""/>				
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid"/>
				<input type="hidden" name="filtro" id="filtro" value=""/>
				<form:hidden path="idenvio" id="idenvio"/>
				<input type="hidden" name="accion" id="accion" value=""/>
				<input type="hidden" name="operacion" id="operacion"  value=""/>
				<input type="hidden" name="polizaOperacion" value=""/>				
				<input type="hidden" id="perfil" name="perfil" value="${perfil}"/>
				<input type="hidden" name="vieneDeLimpiar" id="vieneDeLimpiar"/>
				<input type="hidden" name="oficina2" id="sucursal"/>
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<input type="hidden" name="listCheck" id="listCheck" value=""/>
				<input type="hidden" name="listGrabDefPolizas" id="listGrabDefPolizas" value=""/>
				<input type="hidden" name="fecEnvioId.day" value=""/> 
	            <input type="hidden" name="fecEnvioId.month" value=""/> 
	            <input type="hidden" name="fecEnvioId.year" value=""/>
	            <input type="hidden" name="fecVigorId.day" value=""/> 
	            <input type="hidden" name="fecVigorId.month" value=""/> 
	            <input type="hidden" name="fecVigorId.year" value=""/>        
	            <input type="hidden" name="grupoOficinas" id="grupoOficinas" value="${grupoOficinas}"/> 
	            
	            <input type="hidden" name="resultadoValidacion" value=""/>
				<input type="hidden" name="formato" value=""/> 
				<input type="hidden" name="idPoliza" id="idPolizaDelete"/>
				<input type="hidden" name="listBorradoPolizas" id="listBorradoPolizas"     value=""/>
				<input type="hidden" name="accionesSobrePolizas" id="accionesSobrePolizas" value="true"/>
				<!-- DAA 14/05/2012 Marcar Todos-->
				<input type="hidden" name="checkTodo" id="checkTodo" value="${checkTodo}"/>
				<input type="hidden" name="polizasString" id="polizasString" value="${polizasString}"/>
				<input type="hidden" name="idsRowsChecked" id="idsRowsChecked" value="${idsRowsChecked}"/>
				
				<input type="hidden" name="displaypopUpAmbCont" id="displaypopUpAmbCont" value="${popUpAmbiCont}" />
				<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion"  value=""/>
				<input type="hidden" name="usuarioNuevo" id="usuarioNuevo" value=""/>
				<input type="hidden" name="actualizarSbp" id="actualizarSbp" value=""/>
				<input type="hidden" name="entCambioOficina" id="entCambioOficina" value=""/>
				<input type="hidden" name="codentCO" id="codentCO" value=""/>
				<input type="hidden" name="nomentCO" id="nomentCO" value=""/>
				<input type="hidden" name="codoficinaCO" id="codoficinaCO" value=""/>
				<input type="hidden" name="descoficinaCO" id="descoficinaCO" value=""/>
				<!-- DAA 09/08/2012 LupaClase -->
				<input type="hidden" name="descripcion" id="descripcion" />
				<!-- 20/04/2015 LupaCambioTitular -->
				<input type="hidden" name="idAseguradoCambioTitular" id="idAseguradoCambioTitular" value=""/>
				
				<input type="hidden" name="nuevoIbanCompleto" id="nuevoIbanCompleto" value=""/>
				
				<!-- MPM 04/09/2012 Control en la impresion -->
				<input type="hidden" name=numRegImpresion id="numRegImpresion" value="${numRegImpresion}" />
				<input type="hidden" name=alertaImpresion id="alertaImpresion" value="${alertaImpresion}" />
		        <input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
		        <input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
		        <input type="hidden" id="externo" value="${externo}" />
		        
		        <!-- Campos para fechas Desde y Hasta -->
		        <input type="hidden" name="fechaEnvioDesdeId.day" value="">
				<input type="hidden" name="fechaEnvioDesdeId.month" value="">
				<input type="hidden" name="fechaEnvioDesdeId.year" value="">		
				<input type="hidden" name="fechaEnvioHastaId.day" value="">
				<input type="hidden" name="fechaEnvioHastaId.month" value="">
				<input type="hidden" name="fechaEnvioHastaId.year" value="">		
				<input type="hidden" name="fechaVigorDesdeId.day" value="">
				<input type="hidden" name="fechaVigorDesdeId.month" value="">
				<input type="hidden" name="fechaVigorDesdeId.year" value="">
				<input type="hidden" name="fechaVigorHastaId.day" value="">
				<input type="hidden" name="fechaVigorHastaId.month" value="">
				<input type="hidden" name="fechaVigorHastaId.year" value="">
				<input type="hidden" name="fechaPagoDesdeId.day" value="">
				<input type="hidden" name="fechaPagoDesdeId.month" value="">
				<input type="hidden" name="fechaPagoDesdeId.year" value="">
				<input type="hidden" name="fechaPagoHastaId.day" value="">
				<input type="hidden" name="fechaPagoHastaId.month" value="">
				<input type="hidden" name="fechaPagoHastaId.year" value="">
		        
		        <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div style="background-color: #FFFFFF; font-family: tahoma,arial,verdana; font-size: 11px; color: #666666; text-align: left; vertical-align: top; padding-left: 5px; padding-top: 2px;">
					<fieldset>
					<legend class="literal">Filtro</legend>
						<table>
							<tr>
								<table>
									<tr>
										<td class="literal">Entidad
										<!-- td class="literal" colspan="8" -->
											<c:if test="${perfil == 0 || perfil == 5}">
												<form:input path="colectivo.tomador.id.codentidad" size="4" maxlength="4" cssClass="dato" id="entidad" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');"/>
												<form:input path="colectivo.tomador.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="25" readonly="true"/>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
											</c:if>
											<c:if test="${perfil > 0 && perfil < 5}">
												<form:input path="colectivo.tomador.id.codentidad" size="4" maxlength="4" cssClass="dato" readonly="true" id="entidad" tabindex="1"/>
												<form:input path="colectivo.tomador.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="25" readonly="true"/>
											</c:if>
										
										<td class="literal">Oficina
											<c:if test="${perfil == 0 || perfil ==1 || perfil == 5 }">
												<form:input path="oficina" size="4" maxlength="4" cssClass="dato" id="oficina" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
												<form:input path="" cssClass="dato"	id="desc_oficina" size="18" readonly="true"/>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
											</c:if>
											<c:if test="${perfil == 2}">
												<form:input path="oficina" size="4" maxlength="4" cssClass="dato" id="oficina" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
												<form:input path="nombreOfi" cssClass="dato"	id="desc_oficina" size="18" readonly="true"/>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
											</c:if>
											<c:if test="${perfil > 2 && perfil < 5}">
												<form:input path="oficina" size="4" maxlength="4" cssClass="dato" readonly="true" id="oficina" tabindex="2"/>
												<form:input path="nombreOfi" cssClass="dato"	id="desc_oficina" size="18" readonly="true"/>
											</c:if>	
										</td>
										<td class="literal">E-S Med	
											<c:if test="${externo == 0 and perfil!=4}"> <!--  es interno -->
												
													<form:input	path="colectivo.subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" tabindex="3"/>
													<form:input	path="colectivo.subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="4"/>
													<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
											</c:if>
											<c:if test="${externo == 1 or (externo==0 and perfil==4)}"> <!--  es externo -->
												<form:input	path="colectivo.subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" tabindex="3"  readonly="true"/>
												<form:input	path="colectivo.subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="4"  readonly="true"/>
												
											</c:if>
										</td>
										<td class="literal">Delegación
											<c:if test="${externo == 1}"><!--  es externo -->
												<c:if test="${perfil == 1}">
													<form:input	path="usuario.delegacion" size="4" maxlength="4" cssClass="dato" id="delegacion" tabindex="5" />
												</c:if>
												<c:if test="${perfil == 3}">
													<form:input	path="usuario.delegacion" size="4" maxlength="4" cssClass="dato" id="delegacion" tabindex="5" readonly="true"/>
												</c:if>
											</c:if>
											<c:if test="${externo == 0}"> <!--  es interno -->
												<form:input	path="usuario.delegacion" size="4" maxlength="4" cssClass="dato" id="delegacion" tabindex="5" />
											</c:if>
										</td>
										<td class="literal">Usuario
											<c:if test="${perfil != 4}">
												<form:input path="usuario.codusuario" id="codusuario" size="8" maxlength="19" cssClass="dato" tabindex="6" onchange="this.value=this.value.toUpperCase();"/>
												<img  border="1" id="iconoLupa" src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('UsuarioFiltros','principio', '', '');" alt="Buscar Usuario"/>
											</c:if>
											<c:if test="${perfil == 4}">
												<form:input path="usuario.codusuario" id="codusuario" size="8" maxlength="19" cssClass="dato" readonly="true" tabindex="7"/>
											</c:if>
										</td>
									</tr>		
								</table>
							</tr>
							<tr>
								<table>
									<tr>
										<td class="literal">Plan
											<form:input path="linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan" tabindex="8"/>
										</td>
										
										<td class="literal">L&iacute;nea
											<form:input path="linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="9" onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
											<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />							
										</td>
										
										<td class="literal">Clase
										  	<form:input path="clase" size="4" maxlength="3" cssClass="dato" id="clase" tabindex="10" />
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Clase','principio', '', '');" alt="Buscar Clase" title="Buscar Clase" />							
										</td>
										
										<td class="literal">M&oacute;dulo
											<form:input path="codmodulo" id="modulo" size="5" maxlength="5" cssClass="dato" tabindex="11" onchange="this.value=this.value.toUpperCase();"/>
										</td>
										
										<td class="literal">RC
											<select name="seleccionRC"  class="dato"	style="width:70" id="seleccionRC" tabindex="12">
												<option value="" >Todos</option>
												<option value="S"<c:if test="${opcionRC == 'S'}">selected</c:if>>Si</option>
												<option value="N"<c:if test="${opcionRC == 'N'}">selected</c:if>>No</option>
											</select>
										</td>
										
										<td class="literal">STR
											<select name="seleccionSTR"  class="dato"	style="width:70" id="seleccionSTR" tabindex="13">
												<option value="" >Todos</option>
												<option value="S"<c:if test="${opcionSTR == 'S'}">selected</c:if>>Si</option>
												<option value="N"<c:if test="${opcionSTR == 'N'}">selected</c:if>>No</option>
											</select>
										</td>
										
										<td class="literal">MOD
											<select name="seleccionMOD"  class="dato"	style="width:70" id="seleccionMOD" tabindex="14">
												<option value="" >Todos</option>
												<option value="S"<c:if test="${opcionMOD == 'S'}">selected</c:if>>Si</option>
												<option value="N"<c:if test="${opcionMOD == 'N'}">selected</c:if>>No</option>
											</select>
										</td>
									</tr>
								</table>
							</tr>
							<tr>
								<table>
									<tr>
										<td class="literal">P&oacute;liza
											<form:input path="referencia" id="poliza" size="8" maxlength="15" cssClass="dato" tabindex="15" onchange="this.value=this.value.toUpperCase();"/>
										</td>
										
										<td class="literal">Colectivo
											<form:input path="colectivo.idcolectivo" id="colectivo" size="8" maxlength="9" cssClass="dato" tabindex="16"/>
											<form:input path="colectivo.dc" size="3" id="dc" maxlength="1" cssClass="dato" tabindex="17"/>
										</td>
										
										
				                         
				                         <td class="literal">Forma Pago
											<select name="seleccionPago"  class="dato"	style="width:140" id="seleccionPago" tabindex="18">
												<option value="" >Todos</option>
												<option value="0"<c:if test="${opcionPago == '0'}">selected</c:if>>Cargo en Cuenta</option>
												<option value="1"<c:if test="${opcionPago == '1'}">selected</c:if>>Pago Manual</option>
												<option value="2"<c:if test="${opcionPago == '2'}">selected</c:if>>Domiciliado Agroseguro</option>
											</select>
										</td>
										<td class="literal">Estado Pago
											<form:select path="estadoPagoAgp.id" id="estadosPolizaPago" cssClass="dato" tabindex="19" cssStyle="width:160px">
												<form:option value="">Todos</form:option>
												<c:forEach items="${estadosPolizaPago}" var="est">
													<form:option value="${est.id}">${est.abreviado} - ${est.descripcion}</form:option>
												</c:forEach>
											</form:select>
										</td>
										<td class="literal">Financiada
											<select name="seleccionFinanciada"  class="dato"	style="width:70" id="seleccionFinanciada" tabindex="20">
												<option value="" >Todos</option>
												<option value="S"<c:if test="${opcionFinanciada == 'S'}">selected</c:if>>Si</option>
												<option value="N"<c:if test="${opcionFinanciada == 'N'}">selected</c:if>>No</option>
											</select>
										</td>		
										<td class="literal">IBAN
											<select name="seleccionIBAN"  class="dato"	style="width:70" id="seleccionIBAN" tabindex="20">
												<option value="" >Todos</option>
												<option value="S"<c:if test="${opcionIBAN == 'S'}">selected</c:if>>Si</option>
												<option value="N"<c:if test="${opcionIBAN == 'N'}">selected</c:if>>No</option>
											</select>
										</td>								
									</tr>
								</table>
							</tr>
							<tr>
								<table>
									<tr align="left">
										<td class="literal">CIF/NIF Aseg.
											<form:input path="asegurado.nifcif" id="nifcif" size="9" maxlength="9" cssClass="dato" tabindex="21" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
										</td>
									
										<td class="literal">Nombre Aseg.
										
											<form:input path="asegurado.nombre" id="nombre" size="23" maxlength="39" cssClass="dato" tabindex="22" onchange="this.value=this.value.toUpperCase();"/>&nbsp;&nbsp;
										</td>
										<td class="literal">Estado
											<form:select path="estadoPoliza.idestado" id="estadoP" cssClass="dato" tabindex="23" cssStyle="width:190px">
												<form:option value="">Todos menos pend.validación</form:option>
													<c:forEach items="${estados}" var="estado">
														<form:option value="${estado.idestado}">${estado.descEstado}</form:option>
													</c:forEach>
											</form:select>
										</td>
										<td class="literal">Rnv
											<select name="seleccionRnv" class="dato" style="width:60" id="seleccionRnv" tabindex="24">
												<option value="" >Todos</option>
												<option value="S"<c:if test="${opcionRnv == 'S'}">selected</c:if>>Si</option>
												<option value="N"<c:if test="${opcionRnv == 'N'}">selected</c:if>>No</option>
											</select>
										</td>									
										<td class="literal">RyD
											<select name="seleccionRyD"  class="dato"	style="width:60" id="seleccionRyD" tabindex="25">
												<option value="" >Todos</option>
												<option value="S"<c:if test="${opcionRyD == 'S'}">selected</c:if>>Si</option>
												<option value="N"<c:if test="${opcionRyD == 'N'}">selected</c:if>>No</option>
											</select>
										</td>
										<td class="literal">Nº Sol.
											<form:input path="idpoliza" id="nsol" size="10" maxlength="15" cssClass="dato" tabindex="15" onchange="this.value=this.value.toUpperCase();"/>
										</td>						
									</tr>
									<!--   P007335 - RQ.04, RQ.05 y RQ.06  Inicio -->
                                    <tr>
                                            <td class="literal">Canal        
                                                    <select name="canalFirma"  class="dato"        style="width:120" id="canalFirma" tabindex="26">                                                                
                                                            <option value="">Todos</option>                
                                                            <c:forEach items="${canalFirma}" var="canal">
                                                                    <option value="${canal.idCanal}" <c:if test="${canal.idCanal == canalIdCanal}">selected</c:if>>${canal.nombreCanal}</option>
                                                            </c:forEach>
                                                    </select>
                                            </td>        
                                            <td class="literal">Doc. firmada
                                                    <select name="docFirmada"  class="dato"        style="width:60" id="docFirmada" tabindex="27">
                                                            <option value="" >Todos</option>
                                                            <option value="S"<c:if test="${docFirmada == 'S'}">selected</c:if>>Si</option>
                                                            <option value="N"<c:if test="${docFirmada == 'N'}">selected</c:if>>No</option>
                                                    </select>
                                            </td>                                                
                                            </td>                                        
                                    </tr>                                    
                                    <!--   P007335 - RQ.04, RQ.05 y RQ.06  Fin -->
								</table>
							</tr>
							
							<tr>
								<table width="100%">
									<tr>
										<td >
											<fieldset>
												<legend class="literal" >Fecha Envío</legend>
												<table>
											    	<tr>
											    		<td class="literal">desde</td>
											    		<td><p class="txt">
											    			<spring:bind path="fechaEnvioDesde">
											    				<input type="text" id="fechaEnvioDesdeId" tabindex="26" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Envio Desde')) this.value='';" name="fechaEnvioDesde" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaBean.fechaEnvioDesde}" />"  size="8" maxlength="10" />
											    			</spring:bind>
											    			<input type="button" id="btn_fechaEnvioDesde" name="btn_fechaEnvioDesde" class="miniCalendario" style="cursor: pointer;" />
											    			</p>
														</td>
														<td class="literal">&nbsp;&nbsp;hasta</td>
											    		<td><p class="txt">
											    			<spring:bind path="fechaEnvioHasta">
											    				<input type="text" id="fechaEnvioHastaId" tabindex="27" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Envio Hasta')) this.value='';" name="fechaEnvioHasta" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaBean.fechaEnvioHasta}" />"  size="8" maxlength="10" />
											    			</spring:bind>
											    			<input type="button" id="btn_fechaEnvioHasta" name="btn_fechaEnvioHasta" class="miniCalendario" style="cursor: pointer;" />
											    			</p>
														</td>
											    	</tr>
											    </table>
											</fieldset>
										</td>
										<td >
											<fieldset>
												<legend class="literal" >Fecha Vigor</legend>
												<table>
											    	<tr>
											    		<td class="literal">desde</td>
											    		<td><p class="txt">
											    			<spring:bind path="fechaVigorDesde">
											    				<input type="text" id="fechaVigorDesdeId" tabindex="28" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Vigor Desde')) this.value='';" name="fechaVigorDesde" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaBean.fechaVigorDesde}" />"  size="8" maxlength="10" />											    				
											    			</spring:bind>
											    			<input type="button" id="btn_fechaVigorDesde" name="btn_fechaVigorDesde" class="miniCalendario" style="cursor: pointer;" />
											    			</p>
														</td>
														<td class="literal">&nbsp;&nbsp;hasta</td>
											    		<td><p class="txt">
											    			<spring:bind path="fechaVigorHasta">
											    				<input type="text" id="fechaVigorHastaId" tabindex="29" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Vigor Hasta')) this.value='';" name="fechaVigorHasta" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaBean.fechaVigorHasta}" />"  size="8" maxlength="10" />											    				
											    			</spring:bind>
											    			<input type="button" id="btn_fechaVigorHasta" name="btn_fechaVigorHasta" class="miniCalendario" style="cursor: pointer;" />
											    			</p>
														</td>
											    	</tr>
											    </table>
											</fieldset>
										</td>
										<td >
											<fieldset>
												<legend class="literal" >Fecha Pago</legend>
												<table>
											    	<tr>
											    		<td class="literal">desde</td>
											    		<td><p class="txt">
											    			<spring:bind path="fechaPagoDesde">
											    				<input type="text" id="fechaPagoDesdeId" tabindex="30" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Pago Desde')) this.value='';" name="fechaPagoDesde" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaBean.fechaPagoDesde}" />"  size="8" maxlength="10" />										    				
											    			</spring:bind>
											    			<input type="button" id="btn_fechaPagoDesde" name="btn_fechaPagoDesde" class="miniCalendario" style="cursor: pointer;" />
											    			</p>
														</td>
														<td class="literal">&nbsp;&nbsp;hasta</td>
											    		<td><p class="txt">
											    			<spring:bind path="fechaPagoHasta">
											    				<input type="text" id="fechaPagoHastaId" tabindex="31" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Pago Hasta')) this.value='';" name="fechaPagoHasta" class="dato" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaBean.fechaPagoHasta}" />"  size="8" maxlength="10" />
											    			</spring:bind>
											    			<input type="button" id="btn_fechaPagoHasta" name="btn_fechapagoHasta" class="miniCalendario" style="cursor: pointer;" />
											    			</p>
														</td>
											    	</tr>
											    </table>
											</fieldset>
										</td>
									</tr>
								</table>
							</tr>
								
								
								
								
								
								
								
								
								
								
							
							
						</table>
					</fieldset>
				</div>			
				
			</form:form>
		<!-- DAA 10/01/2013 -->
		<c:if test="${tablaPolizas != 'vacio'}">
			<div id="grid">
					<display:table requestURI="utilidadesPoliza.html" id="listaResultados" class="LISTA" summary="Poliza" 
			                       pagesize="${numReg}" size="${totalListSize}"  name="${listaPolizas}" 
			                       decorator="com.rsi.agp.core.decorators.ModelTableDecoratorPolizas" style="width:100%;border-collapse:collapse;margin:0 auto;" 
			                       excludedParams="operacion resultadoValidacion polizaOperacion polizasString" partialList="true"
			                       keepStatus="true"> 
			                       
				        <display:setProperty name="pagination.sort.param" value="sort"/>
						<display:setProperty name="pagination.sortdirection.param" value="dir"/>
						<display:setProperty name="paging.banner.one_item_found" value='' />
			            <display:column class="literalUtil" headerClass="cblistaImg" title="Acciones" property="polSelec" style="width:80px;text-align:center" />
						
						<display:column class="literalUtil" headerClass="cblistaImg" title="Ent." property="polEntidad" sortProperty="col.tomador.id.codentidad" sortable="true" style="width:40px; padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="Ofi." property="oficina" sortProperty="oficina" sortable="true" style="width:40px;padding-left:2px;"/>			
						<display:column class="literalUtil" headerClass="cblistaImg" title="Usuario" property="polUsuario" sortProperty="usuario.codusuario" sortable="true" style="width:70px;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="Plan" property="polPlan" sortProperty="lin.codplan" sortable="true" style="width:50px;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="Linea" property="polLinea" sortProperty="lin.codlinea" sortable="true" style="width:50px;padding-left:2px;"/>						
						<display:column class="literalUtil" headerClass="cblistaImg" title="Clase." property="polClase" sortProperty="clase" sortable="true" style="width:70px;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="Poliza" property="polPoliza" sortProperty="referencia" sortable="true" style="width:70px;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="Importe" property="importe" sortProperty="importe" sortable="false" style="width:50px; text-align:right;padding-left:2px;" />
						<display:column class="literalUtil" headerClass="cblistaImg" title="Mod." property="polModulo" sortProperty="codmodulo" sortable="true" style="width:50px; text-align:center;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="CIF/NIF" property="polCifNif" sortProperty="ase.nifcif" sortable="true" style="width:100px;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="Nombre Asegurado" property="polNombreAseg" sortable="false" style="width:150px;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="Estado" property="polEstado" sortProperty="estadoPoliza.descEstado" sortable="true" style="width:50px;padding-left:2px;"/>	
						<display:column class="literalUtil" headerClass="cblistaImg" title="F.Envío" property="polFechaEnvio" sortable="true" sortProperty="fechaenvio" style="width:100px;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="PG" property="pg" sortable="false" sortProperty="pg" style="width:75px;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="F.Pago" property="polFechaPago" sortable="true" sortProperty="fechaPago" style="width:65px;padding-left:2px;"/>
						
						<display:column class="literalUtil" headerClass="cblistaImg" title="Fin." property="polEsFinanciada" sortable="false" style="width:15px;text-align:center;padding-left:2px;"/>
						
						<%-- <display:column class="literal" headerClass="cblistaImg" title="RC" property="polRc" sortable="false" style="width:15px;text-align:center"/> --%>						
						<display:column class="literalUtil" headerClass="cblistaImg" title="STR" property="polStr" sortable="false" style="width:15px;text-align:center;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="MOD" property="polMOD" sortable="false" style="width:15px;text-align:center;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="Rnv" property="polRnv" sortable="false" style="width:15px;text-align:center;padding-left:2px;"/>					
						
						<display:column class="literalUtil" headerClass="cblistaImg" title="RyD" property="polEsRyD" sortable="false" style="width:15px;text-align:center;padding-left:2px;"/>
						<display:column class="literalUtil" headerClass="cblistaImg" title="Nº Sol." property="polNSolicitud" sortProperty="idpoliza" sortable="true" style="width:70px;padding-left:2px;"/>
						
						
						<!-- DAA 14/05/2012 Marcar Todos-->
						
						<display:footer>
							<form:form name="frmcheck" id="frmcheck">
								<tr style="background-color: #e5e5e5">
									<td class="literal" colspan="19" style="text-align: left">
										<span style="width:30px;">&nbsp;</span><input type="checkbox" id="checkTodo" name="checkTodo" 
										class="dato" onclick="this.checked  ? marcar_todos() : desmarcar_todos() " />Marcar Todos</td>
									<td class="literal" colspan="17"></td>
								</tr>
							</form:form>
						</display:footer>
												
			        </display:table>
			
			        <div style="width:20%;text-align:center;margin:0 auto;" id="divImprimir">
			        	 <a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Imprimir</a>	
						 <a id="btnImprimirPdf" style="text-decoration:none;" href="javascript:imprimir(${totalListSize}, 'pdf')">
						 	<img src="jsp/img/jmesa/pdf.gif"/>
						 </a>
						 <a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:imprimir(${totalListSize}, 'xls')">
						 	<img src="jsp/img/jmesa/excel.gif"/>
						 </a>
					</div>		        
			</div>
		</c:if>   
</div>

		
		 		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/moduloPolizas/polizas/importes/popupDatosAval.jsp"%>
		
		<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaClase.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCambiarOficina.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCambiarTitular.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaUsuarioFiltros.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaUsuarioFiltrosRenovables.jsp"%>
		<!--   P007335 - RQ.04, RQ.05 y RQ.06  Inicio -->
        <%@ include file="/jsp/moduloPolizas/polizas/cargaDocFirmada.jsp"%>
        <!--   P007335 - RQ.04, RQ.05 y RQ.06  Fin -->
		
		<!--  *** panel avisos ***  -->
		<div id="divAviso" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
	       <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;
			                                  color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
			                                  background:#525583;height:15px">
			        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
			            Aviso
			        </div>
			        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
			                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
			            <span onclick="cerrarPopUp()">x</span>
			        </a>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content">
					<div id="panelInformacion" class="panelInformacion">
						<div id="txt_info_saeca" style="width: 70%;display:none" >Solo se permite pasar a definitiva de manera individual las pólizas con financiación SAECA</div>
						<div id="txt_info_gp" style="width: 70%;display:none" >Existen pólizas elegidas con estado distinto a Grabación Provisional</div>
						<div id="txt_info_gf" style="width: 70%;display:none" >Existen pólizas elegidas con estado distinto a Grabación Definitiva</div>
						<div id="txt_info_ec" style="width: 70%;display:none" >Existen pólizas elegidas con estado distinto a Enviada Correcta o Emitida</div>
						<div id="txt_info_none" style="width: 70%;display:none" >No hay pólizas seleccionadas.</div>
						<div id="txt_info_DistintaEnt" style="width: 70%;display:none" >Existen pólizas elegidas con distinta Entidad.</div>
						<div id="txt_info_pol_anuladas" style="width: 70%;display:none" >Existen pólizas elegidas con estado Anulada.</div>
						<div id="txt_info_check_multiple" style="width: 70%;display:none" >Solo se permite seleccionar una póliza.</div>
						<div id="txt_info_bm" style="width: 70%;display:none" >No es posible borrar pólizas en estado diferente a 'Pendiente validación' y 'Grabación provisional'.</div>
						<div id="txt_info_check_grabDefUser" style="width: 70%;display:none" >El usuario actual no dispone de permisos para realizar cargos en cuenta en el paso a 'definitiva'.</div>
					</div>
			</div>
		</div>
		
		<div id="popUpAltaSbp" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Alta de Sobreprecio
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="hidePopUpAviso('popUpAltaSbp')">x</span>
				        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
						<div id="panelInformacion" class="panelInformacion">
							<div id="txt_mensaje_eleccionCplEnSbp">sin mensaje.</div>
						</div>
						<div style="margin-top:15px">
							<a class="bot" id="btn_aceptarGrabacionDefinitiva" href="javascript:alta_eleccion_Si()" title="Si incluir">SI</a>
						    <a class="bot" href="javascript:alta_eleccion_No()" title="No incluir">NO</a>
						</div>
			 </div>
		</div>
		
		
		<!-- *** popUpAvisos *** -->
		<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Aviso
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="hidePopUpAviso('popUpPasarDefinitivaBoton')">x</span>
				        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
						<div id="panelInformacion" class="panelInformacion">
							<div id="txt_mensaje_aviso_1">sin mensaje.</div>
						</div>
						<div style="margin-top:15px">
						    <a class="bot" href="javascript:hidePopUpAviso('popUpPasarDefinitivaBoton')" title="Cancelar">Cancelar</a>
						    <a class="bot" id="btn_aceptarGrabacionDefinitiva" href="javascript:aceptarPopUpPasoDefinitiva('popUpPasarDefinitivaBoton')" title="Cancelar">Aceptar</a>
						</div>
			 </div>
		</div>
		
		<!-- *** popUp cambio usuario *** -->
			<div id="divCambioUsuario" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px">
				<!--  header popup -->
				<div id="header-popup" style="padding:0.4em 1em;position:relative;
				                              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
				                              background:#525583;height:15px">
					<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
						Cambiar de usuario
					</div>
					<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
						      font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
						<span onclick="cerrarPopUpUsuario()">x</span>
					</a>
				</div>
				<div id="cambioUsuarioPopUpError" class="literal" style="color:red;display:none;text-align:center;background-color:white">
			       No ha introducido ningún usuario
			    </div>
				<!--  body popup -->
				<div class="panelInformacion_content">
					<div id="panelInformacion" class="panelInformacion">
						
						<table>
					        <tr>
					            <td class="literal">Usuario nuevo:</td>
					            <td>
					            	<input type="text" id="inputUsuario" name="inputUsuario" size="8" maxlength="8" value="">
					            	<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_usuarioNuevo"> *</label>
					            </td>
					        </tr>
			   			</table>
						
						<div style="margin-top:15px;clear: both">
						    <a class="bot" href="javascript:cerrarPopUpUsuario()">Cancelar</a>
							<a class="bot" href="javascript:cambiarUsuario()">Cambiar usuario</a>
						</div>
					</div>
				</div>
			</div>
		
		<!-- *** popUp cambio IBAN *** -->
			<div id="divCambioIBAN" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px">
				<!--  header popup -->
				<div id="header-popup" style="padding:0.4em 1em;position:relative;
				                              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
				                              background:#525583;height:15px">
					<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Cambiar IBAN</div>
					<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
						      font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
						<span onclick="cerrarPopUpCambioIBAN()">x</span>
					</a>
				</div>
				<div id="cambioIBANPopUpError" class="literal" style="color:red;display:none;text-align:center;background-color:white">
			       IBAN incorrecto
			    </div>
				<!--  body popup -->
				<div class="panelInformacion_content">
					<div id="panelInformacion" class="panelInformacion">
						
						<table>
					        <tr>
					            <td class="literal">Nuevo IBAN:</td>
					            <td>
									<input type="hidden" id="ibanCompleto" name="ibanCompleto"/>
									<input type="text" id="iban" name="iban" size="4" maxlength="4" class="dato" 	   	onKeyup="autotab(this, document.getElementById('cuenta1'));" onChange="this.value=this.value.toUpperCase();"/>
									<input type="text" id="cuenta1" name="cuenta1" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('cuenta2'));"/>
									<input type="text" id="cuenta2" name="cuenta2" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('cuenta3'));"/>
									<input type="text" id="cuenta3" name="cuenta3" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('cuenta4'));"/>
									<input type="text" id="cuenta4" name="cuenta4" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.getElementById('cuenta5'));"/>
									<input type="text" id="cuenta5" name="cuenta5" size="4" maxlength="4" class="dato"/>
									<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_ccc"> *</label>
					            </td>
					        </tr>
			   			</table>
						
						<div style="margin-top:15px;clear: both">
							<a class="bot" href="javascript:cambiarIBAN()">Aceptar</a>
						    <a class="bot" href="javascript:cerrarPopUpCambioIBAN()">Cancelar</a>
						</div>
					</div>
				</div>
			</div>
		

		<!-- DAA 28/05/2013 Pago Masivo -->
		<%@ include file="/jsp/moduloUtilidades/operacionesMasivas.jsp"%>
		<%@ include file="/jsp/moduloPolizas/polizas/popupPagoMasivo.jsp"%>
		<%@ include file="/jsp/moduloPolizas/polizas/popupCambioClase.jsp"%>
		<%@ include file="/jsp/moduloPolizas/polizas/popupRestaurarParams.jsp"%>
		<%@ include file="/jsp/moduloPolizas/polizas/popupEleccionImpresion.jsp"%>
		
		
		<!-- *** popUp pasar definitiva icon row *** -->
		<div id="popUpPasarDefinitivaIconRow" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Aviso
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="hidePopUpAviso('popUpPasarDefinIconRow')">x</span>
				        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
						<div id="panelInformacion" class="panelInformacion">
							<div id="txt_mensaje_aviso_2">sin mensaje.</div>
						</div>
						<div style="margin-top:15px">
						    <a class="bot" href="javascript:hidePopUpAviso('popUpPasarDefinIconRow')" title="Cancelar">Cancelar</a>
						    <a class="bot" id="btn_aceptarGrabacionDefinitiva" href="javascript:aceptarPopUpPasoDefinitiva('popUpPasarDefinIconRow')" title="Cancelar">Aceptar</a>
						</div>
			 </div>
		</div>
		
		
		<!-- *** popUp pasar definitiva complementariaicon row *** -->
		<div id="popUpPasarDefinitivaIconRowCpl" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Aviso
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="hidePopUpAviso('popUpPasarDefinIconRowCpl')">x</span>
				        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
						<div id="panelInformacion" class="panelInformacion">
							<div id="txt_mensaje_aviso_3">sin mensaje.</div>
						</div>
						<div style="margin-top:15px">
						    <a class="bot" href="javascript:hidePopUpAviso('popUpPasarDefinIconRow')" title="Cancelar">Cancelar</a>
						    <a class="bot" id="btn_aceptarGrabacionDefinitiva" href="javascript:aceptarPopUpPasoDefinitiva('popUpPasarDefinIconRowCpl')" title="Cancelar">Aceptar</a>
						</div>
			 </div>
		</div>

		<!-- *** popUp ámbito contratación *** -->
		<div id="popUpAmbitoContratacion" class="parcelasRepWindow" style="color:#333333;width:700px;max-height:400px; 
            height:expression(this.scrollHeight > 400? '400px' : 'auto' );overflow:auto;left:300px;padding:3px">
            
	            <!-- header -->
	            <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;background:#525583;height:15px">
					        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
					            Aviso
					        </div>
					        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
					                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					            <span onclick="closePopUpAmbitoCont()">x</span>
					        </a>
				 </div>
				 <br />
				 
				<!-- body -->
				<div id="tableNoDefinitiva" style="">
			       ${tableInfoNoDefinitiva}
	            </div>
	            
	            <!-- buttons -->
	            <div style="margin-top:10px;text-align:center;">
	    			<table>
	    				<tr>
		    				<td>
		    					<a href="javascript:closePopUpAmbitoCont();" class="bot">Cerrar</a>
		    				<td>
		    				<td class="literal" width="213px" align="center">
				    			<c:if test="${perfil == 0 && botonPerfil0 == 0}">
				    				<a href="javascript:grabarDefFueraContratacion(${idpoliza});" class="bot">Forzar paso a definitiva</a>
				    			</c:if>
		    					<c:if test="${perfil == 0 && botonPerfil0 == 1}">
		    						<a href="javascript:getChecksSelDefMult();grabarDefFueraContratacionMult(document.main3.listGrabDefPolizas.value);" 
		    								class="bot" id="btn_forzarDef" style="display:none">Forzar paso a definitiva</a>
		    					</c:if>
			    			</td>
			    			<c:if test="${perfil == 0 && botonPerfil0 == 1}">
				    			<td class="literal" align="right">
				    				Marcar Todos   <input type="checkbox" id="marcaTodosDefMul" name="marcaTodosDefMul" class="dato" 
				    						onclick="this.checked  ? marcatodosPopUp() : desmarcatodosPopUp() " />
				    			</td>
			    			</c:if>
		    			</tr>
		    		</table>
	    			
	            </div>   
           </div>
         
    </body>
</html>