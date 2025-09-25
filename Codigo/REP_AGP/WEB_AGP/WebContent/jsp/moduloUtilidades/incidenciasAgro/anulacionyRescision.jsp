<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>

<html>
<head>
    
	<title>Solicitud de Anulación/Rescisión a Agroseguro</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
	<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
	<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>	
	<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
    <script type="text/javascript" src="jsp/js/calendar.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/restaurarParams.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/cambiarTitular.js"></script>
	<script type="text/javascript" src="jsp/js/utilesIBAN.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/cambiarIBAN.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/importes/datosAval.js"></script>
	<script type="text/javascript" src="jsp/moduloUtilidades/incidenciasAgro/anulacionyRescision.js"></script>
	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<!-- botones de la pagina -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td width="33%" align="left"/>
				<td width="33%" align="center"/>
				<td width="33%" align="right"> 
					<a class="bot" id="btnEnviar" href="javascript:enviarAnulyResc();">Enviar</a>
					<a class="bot" id="btnLimpiar"href="javascript:Volver()">Volver</a>
				</td>
			</tr>
		</table>
	</div>
	
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">SOLICITUD DE ANULACIÓN / RESCISIÓN A AGROSEGURO</p>
		
		<form id="formAnulyResc" name="formAnulyResc" action="anulacionyRescisionPol.run" method="post">
		
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<input type="hidden" name="usuarioSession" id="usuarioSession" value="${codUsuario}"/>		
			<input type="hidden" name="method" id="method" value="doConsulta"/>
			<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
			
			<input type="hidden" name="origenLlamada"  id="origenLlamada"   value="${origenLlamada}" />
			<input type="hidden" name="grupoEntidades" id="grupoEntidades"  value="${grupoEntidades}"/>
			<input type="hidden" name="grupoOficinas"  id="grupoOficinas"   value="${grupoOficinas}"/>
			<input type="hidden" name="perfilUsuario"  id="perfilUsuario"   value="${perfil}" />
			<input type="hidden" name="tiporefSel"     id="tiporefSel"      value="${tiporefSel}" />
			<input type="hidden" name="ventanaVolver"  id="ventanaVolver"   value="${origenLlamada}" />
			<input type="hidden" name="referenciaAyR"  id="referenciaAyR"   value="${referencia}" />
			<input type="hidden" name="codPlanAyR"     id="codPlanAyR"      value="${codPlan}" />
			<input type="hidden" name="codlineaAyR"    id="codlineaAyR"     value="${codlinea}" />
			<input type="hidden" name="nifCifAyR"      id="nifCifAyR"       value="${nifcif}" />
			<input type="hidden" name="motivoAyR"      id="motivoAyR"       value="${motivos.codmotivo}" />
			<input type="hidden" name="tipoAyR"        id="tipoAyR"         value="${tipoAnuResc}" />
			<input type="hidden" name="idPolizaAyR"    id="idPolizaAyR"     value="${idPoliza}" />
			<input type="hidden" name="idPolIniAyR"    id="idPolIniAyR"     value="${idPolIniAyR}"/>
			<input type="hidden" name="idIncAyR"	   id="idIncAyR"        value="${idIncAyR}"/>
			
			<!-- Enviamos los datos, para luego poder volver a la ventana de Incidencias -->
			<input type="hidden" name="idincConsVuelta" 			id="idincConsVuelta" />
			<input type="hidden" name="entidadConsVuelta" 			id="entidadConsVuelta" />
			<input type="hidden" name="refConsVuelta" 				id="refConsVuelta" />
			<input type="hidden" name="oficinaConsVuelta"			id="oficinaConsVuelta" />
			<input type="hidden" name="entmediadoraConsVuelta" 		id="entmediadoraConsVuelta" />
			<input type="hidden" name="subentmediadoraConsVuelta" 	id="subentmediadoraConsVuelta" />
			<input type="hidden" name="delegacionConsVuelta" 		id="delegacionConsVuelta" />
			<input type="hidden" name="codplanConsVuelta" 			id="codplanConsVuelta" />
			<input type="hidden" name="codlineaConsVuelta" 			id="codlineaConsVuelta" />
			<input type="hidden" name="codestadoConsVuelta" 		id="codestadoConsVuelta" />
			<input type="hidden" name="codestadoagroConsVuelta" 	id="codestadoagroConsVuelta" />
			<input type="hidden" name="nifcifConsVuelta" 			id="nifcifConsVuelta" />
			<input type="hidden" name="tiporefConsVuelta" 			id="tiporefConsVuelta" />
			<input type="hidden" name="idcuponConsVuelta" 			id="idcuponConsVuelta" />
			<input type="hidden" name="asuntoConsVuelta" 			id="asuntoConsVuelta" />
			<input type="hidden" name="fechaEnvioDesdeIdConsVuelta" id="fechaEnvioDesdeIdConsVuelta" />
	   		<input type="hidden" name="fechaEnvioHastaIdConsVuelta" id="fechaEnvioHastaIdConsVuelta" />
	   		<input type="hidden" name="numIncidenciaConsVuelta" 	id="numIncidenciaConsVuelta" />
	   		<input type="hidden" name="codusuarioConsVuelta" 		id="codusuarioConsVuelta" />
	   		<input type="hidden" name="tipoincConsVuelta" 			id="tipoincConsVuelta" />
			
			<fieldset class="panel2 isrt">
				<legend class="literal"></legend>
				<div style="float: left;">
					<div style="width: 100%;">
						<table width="100%">
							<tr>
								<td style="width:200px; float:center" colspan="2" rowspan="3" align="right">
									 <fieldset style="width:100px; border: 1px solid; float: center">
										<legend class="literal">Tipología: </legend>
										 <table align="center" border="0">		
											<tr>
												<td class="literal">
												<input type="radio" name="tipoAnuResc" id="tipoAnu" value="A" />Anulación
											</tr>		
											<tr>
												<td class="literal">
												<input type="radio" name="tipoAnuResc" id="tipoResc" value="R" />Rescisión</td>
											</tr>	
										</table> 
									</fieldset> 
								</td> 
								<td class="literal">Plan
									<input name="linea.codplan" size="5" maxlength="4" Class="dato" id="plan" tabindex="8" value="${codPlan}"/>
								</td>
								
								<td class="literal">L&iacute;nea
									<input name="linea.codlinea" size="3" maxlength="3" Class="dato" id="linea" tabindex="9" onchange="javascript:lupas.limpiarCampos('desc_linea');" value="${codlinea}" />
									<input name="linea.nomlinea" Class="dato" id="desc_linea" size="40" readonly="true" value="${nombLinea}"/>
									<img src="jsp/img/magnifier.png" id="lupalinea" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />							
								</td>
							</tr>
							<tr>
								<td>
									<label style="width:90px" for="referencia" class="literal">Referencia</label>
									<input size="10" maxlength="7" Class="dato" id="referencia" tabindex="9" onchange="this.value=this.value.toUpperCase();" value ="${referencia}"/>
								</td>
								<td>
									<label style="width:90px" for="tiporef" Class="literal">Tipo Pol.</label>
									<select id="tiporef"  Class="dato" cssStyle="width:120px"tabindex="10">
											<option value=""></option>
											<option value="P"<c:if test="${tiporefSel == 'P'}">selected</c:if>>Principal</option>
											<option value="C"<c:if test="${tiporefSel == 'C'}">selected</c:if>>Complementaria</option>
									</select>
								</td>
								<td>
									<label style="width:90px" for="nifcif" Class="literal">CIF/NIF Aseg.</label>
									<input id="nifcif" size="10" maxlength="9" Class="dato" tabindex="11" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);" value="${nifcif}"/>
								</td>
							</tr>
							<tr>
								<td colspan ="2">
									<label style="width:90px" for="listmotivo" class="literal">Motivo</label>
									<select Class="dato" tabindex="14" id="listmotivo" style="width:550px">
										<option value="">Todos</option>
										<c:forEach items="${listaMotivos}" var="listmotivo">
											<c:choose>
												<c:when test="${not empty motivos and motivos.codmotivo eq listmotivo.codmotivo}">
													<option value="${listmotivo.codmotivo}" selected="selected">${listmotivo.descripcion}</option>
												</c:when>
												<c:otherwise>
													<option value="${listmotivo.codmotivo}">${listmotivo.descripcion}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</select>	
									
								</td>
							</tr>
						</table>
					
					</div>
				</div>
			</fieldset>
		</form>	
	</div>
	
	<form id="formLimpiar" name="formLimpiar" action="utilidadesIncidencias.run" method="post">
		<input type="hidden" name="method" id="method" value="doConsulta"/>
		<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
	</form>
	
	<!-- Formulario para vconsultar la relacion de incidencias asociadas a una poliza en agroseguro -->
		<form:form name="VolverimpresionIncidenciasMod" id="VolverimpresionIncidenciasMod" action="impresionIncidenciasMod.html" method="post" commandName="anexoModificacion">
			<input type="hidden" id="methodImpIncMod" name="method" value="doImprimirIncidencias" />
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>	
			<form:hidden path="poliza.linea.codplan" id="codplan"/>							
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
			<form:hidden path="poliza.referencia" id="refPoliza"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<input type="hidden" id="nombreCompleto" name="nombreCompleto" value="${poliza.asegurado.nombreCompleto}"/>
			<form:hidden path="poliza.codmodulo" id="codmodulo"/>
			<input type="hidden" id="fechaEnvio" name="fechaEnvio" value="${poliza.fechaenvio}"/>
			<input type="hidden" id="idCuponImpresion" name="idCuponImpresion"/>
		</form:form>
		
		<form:form name="main" id="volverUtilidadesPol" action="declaracionesModificacionPoliza.html" method="post" commandName="anexoModificacion" >
			<input type="hidden" id="method" name="method" />
			<input type="hidden" id="idPoliza" name="idPoliza" value="${idPoliza}"/>
			<input type="hidden" id="tipoModo" name="tipoModo" value="${tipoModo}"/>
			<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}"/>
			<input type="hidden" id="idAnexoCaducado" name="idAnexoCaducado"/> 
			
			<form:hidden path="id" id="id"/>
			<form:hidden path="poliza.idenvio" id="idenvio"/>
			<form:hidden path="poliza.linea.codlinea" id="codlinea"/>	
			<form:hidden path="poliza.linea.codplan" id="codplan"/>							
			<form:hidden path="poliza.linea.nomlinea" id="nomlinea"/>
			<form:hidden path="poliza.referencia" id="refPoliza"/>
			<form:hidden path="poliza.idpoliza" id="idPoliza"/>
			<form:hidden path="poliza.tipoReferencia" id="tipoReferencia"/>
			
			<form:hidden path="cupon.id" id="idCuponNum"/>
			<form:hidden path="cupon.idcupon" id="idCupon"/>
			<form:hidden path="cupon.cuponPrevio.cuponPpalPrevio" id="idCuponPpalPrevio"/>
			<form:hidden path="cupon.cuponPrevio.estadoCuponPpalPrevio.id" id="estadoCuponPpalPrevio"/>
			<form:hidden path="cupon.cuponPrevio.cuponCplPrevio" id="idCuponCplPrevio"/>
			<form:hidden path="cupon.cuponPrevio.estadoCuponCplPrevio.id" id="estadoCuponCplPrevio"/>
			
			<form:hidden path="cupon.estadoPlzPpalAgroseguro.id" id="idEstadoPlzPpalAgroseguro"/>
			<form:hidden path="cupon.estadoPlzPpalAgroseguro.estado" id="estadoPlzPpalAgroseguro"/>
		</form:form>
		
		<form id="formVolverInc" name="formVolverInc" action="aportarDocIncidencia.run" method="post">
			<input type="hidden" name="method" id="method" value="doVolver" />
			<input type="hidden" name="origenLlamada" value="" />
			<input type="hidden" name="idincidencia" id="idincidenciaConsulta" value="${vuelta.idincidencia}" />
			<input type="hidden" name="codentidad" id="entidadConsulta" value="${vuelta.codentidad}" />
			<input type="hidden" name="referencia" id="referenciaConsulta" value="${vuelta.referencia}" />
			<input type="hidden" name="oficina"id="oficinaConsulta" value="${vuelta.oficina}" />
			<input type="hidden" name="entmediadora" id="entmediadoraConsulta" value="${vuelta.entmediadora}" />
			<input type="hidden" name="subentmediadora" id="subentmediadoraConsulta" value="${vuelta.subentmediadora}" />
			<input type="hidden" name="delegacion" id="delegacionConsulta" value="${vuelta.delegacion}" />
			<input type="hidden" name="codplan" id="planConsulta" value="${vuelta.codplan}" />
			<input type="hidden" name="codlinea" id="lineaConsulta" value="${vuelta.codlinea}" />
			<input type="hidden" name="codestado" id="codestadoConsulta" value="${vuelta.codestado}" />
			<input type="hidden" name="codestadoagro" id="codestadoagroConsulta" value="${vuelta.codestadoagro}" />
			<input type="hidden" name="nifcif" id="nifcifConsulta" value="${vuelta.nifcif}" />
			<input type="hidden" name="tiporef" id="tiporefConsulta" value="${vuelta.tiporef}" />
			<input type="hidden" name="idcupon" id="idcuponConsulta" value="${vuelta.idcupon}" />
			<input type="hidden" name="asunto" id="asuntoConsulta" value="${vuelta.asunto}" />
			<input type="hidden" name="fechaEnvioDesdeId" id="fechaEnvioDesdeIdConsulta" value="${fechaEnvioDesdeStr}" />
	   		<input type="hidden" name="fechaEnvioHastaId" id="fechaEnvioHastaIdConsulta" value="${fechaEnvioHastaStr}" />				    		
	   		<input type="hidden" name="numIncidencia" id="numIncidenciaConsulta" value="${vuelta.numero}" />
	   		<input type="hidden" name="codusuario" id="codUsuarioConsulta" value="${vuelta.codusuario}" />
	   		<input type="hidden" name="tipoinc" id="tipoincConsulta" value="${vuelta.tipoinc}" />
		</form>
		
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaCambiarOficina.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaUsuarioFiltros.jsp"%>
</body>
</html>