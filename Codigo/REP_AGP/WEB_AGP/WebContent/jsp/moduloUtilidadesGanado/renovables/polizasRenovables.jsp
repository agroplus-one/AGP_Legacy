<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>
<fmt:bundle basename="agp">
	<c:set var="numRegImpresion"><fmt:message key="impresionnumReg"/></c:set>
	<c:set var="alertaImpresion"><fmt:message key="listados.msgError"/></c:set>
</fmt:bundle>
<html>
<input type="hidden" id="numRegImpresionHidden" value="${numRegImpresion}" />
<input type="hidden" id="alertaImpresionHidden" value="${alertaImpresion}" />
<input type="hidden" id="totalListSizeHidden" value="${totalListSize}" />


<head>
<title>Listado de P&oacute;lizas Renovables</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<!-- Estilos -->
<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />

<!-- JavaScript,jQery & AJAX -->
<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/util.js"></script>

<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
<script type="text/javascript" src="jsp/js/jquery.alerts.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
<script type="text/javascript" src="jsp/moduloUtilidadesGanado/renovables/polizasRenovables.js"></script>
<script type="text/javascript" src="jsp/moduloUtilidadesGanado/renovables/imprimirPolizaRenovable.js"></script>


<%@ include file="/jsp/moduloUtilidadesGanado/renovables/popupRenovables.jsp"%>
<%@ include file="/jsp/moduloUtilidadesGanado/renovables/popupEnvioIBAN.jsp"%>
<%@ include file="/jsp/moduloUtilidadesGanado/renovables/popupEleccionImpresionPolRen.jsp"%>
<!--  Pet. 63482 ** MODIF TAM (19.04.2021) -->
<%@ include file="/jsp/moduloUtilidadesGanado/renovables/popupAltaPolizaRen.jsp"%>
	
<%@ include	file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTomador.jsp"%>
<%@ include file="/jsp/common/lupas/lupaLineaAltaRenov.jsp"%>


<%@ include file="/jsp/js/draggable.jsp"%>
<script type="text/javascript">
			 
			 function cargarFiltro(){
				 //<c:if test="${origenLlamada != 'menuGeneral'}">	
					<c:forEach items="${sessionScope.consultaPolRenovables_LIMIT.filterSet.filters}" var="filtro">
						<c:if test="${filtro.property == 'codentidadmed'}">
						$('#entmediadora').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'codsubentmed'}">
						$('#subentmediadora').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'tomador'}">
							$('#tomador').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'plan'}">
							$('#plan').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'linea'}">
							$('#linea').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'referencia'}">
						$('#referencia').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'refcol'}">
						$('#colectivo').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'nifAsegurado'}">
						$('#nifAsegurado').val('${filtro.value}');
						</c:if>
						
						<c:if test="${filtro.property == 'estagroseguro'}">
						var selectOption = document.getElementById('estadoRenovacionAgroseguro');
						selectOption.value = '${filtro.value}';
						</c:if>
						
						/* <c:if test="${filtro.property == 'estadoRenovacionAgroplus.codigo'}">
						var selectOption = document.getElementById('estadoRenovacionAgroplus');
						selectOption.value = '${filtro.value}';
						</c:if> */
						
						<c:if test="${filtro.property == 'estadoIban'}">
						var selectOption = document.getElementById('polizaRenovableEstadoEnvioIBAN');
						selectOption.value = '${filtro.value}';
						</c:if>
						
						/* <c:if test="${filtro.property == 'gastosRenovacions.grupoNegocio'}">
						var selectOption = document.getElementById('polRenGrupoNegocio');
						alert("cargaFiltro grNg": + selectOption);
						selectOption.value = '${filtro.value}';
						</c:if> */
						
					</c:forEach>
				//</c:if>
			} 
			
			function cargarEstados(){
				var frm = document.getElementById('main3');
				if (frm.estAgroplus.value != null && frm.estAgroplus.value != ''){
					var selectOption = document.getElementById('estadoRenovacionAgroplus');
					selectOption.value = frm.estAgroplus.value;
				}
				if (frm.estAgroseguro.value != null && frm.estAgroseguro.value != ''){
					var selectOption = document.getElementById('estadoRenovacionAgroseguro');
					selectOption.value = frm.estAgroseguro.value;
				}
				if (frm.estEnvioIBAN.value != null && frm.estEnvioIBAN.value != ''){
					var selectOption = document.getElementById('polizaRenovableEstadoEnvioIBAN');
					selectOption.value = frm.estEnvioIBAN.value;
				}
				if (frm.grupoNegocio.value != null && frm.grupoNegocio.value != ''){
					var selectOption = document.getElementById('polRenGrupoNegocio');
					selectOption.value = frm.grupoNegocio.value;
				}
				
			} 
			 
			 function returnBack()
				{
					$(window.location).attr('href', 'incidencias.html?rand=' + UTIL.getRand() + 
							'&codplan='+$('#plan').val()+
							'&method=doConsulta');	
					
				}

</script>
		
		
<script language=javascript type="text/javascript">

	$(document).ready(function() {
		var frmRenEnvioIBAN = document.getElementById('frmRenEnvioIBAN');
		if (frmRenEnvioIBAN.mostrarResultadoEnvioIBAN.value == "true") {
			abrirPopUpResEnvioIBAN();	
		}
	});
		
	</script>		

<style>
#divImprimir {
    display: none;
}
</style>

</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="SwitchMenu('sub13');cargarFiltro();cargarEstados();">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<div width="100%"><%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%></div>

	<div id="buttons" width="100%">
		<table width="97%" cellspacing="2" cellpadding="2" border="0">
			<tr>
				<td align="left">
					&nbsp;<a class="bot" id="btnGastosMasivos" title="Gastos Masivos"
					href="javascript:abrirPopUpGastosMasivo(false);">Gastos Masivos</a>
					<c:if test="${perfil == 0}">
					&nbsp;<a class="bot" id="btnGastosMasivos" title="Gastos Masivos"
					href="javascript:abrirPopUpGastosMasivo(true);">Gastos Masivos P0</a>
					</c:if>
					<a class="bot" id="btnEnvioIBAN" title="Env&iacute;o IBAN"
					href="javascript:abrirPopUpEnvioIBAN();">Env&iacute;o IBAN</a>
				</td>
				<!--  Pet. 63482 ** MODIF TAM (16/04/2021) * Ini-->
				<td align="center">
					<c:if test="${perfil == 0}">
						&nbsp;<a class="bot" id="btnAltaRenovables" title="Alta Renovable"
						href="javascript:abrirPopUpAltaRenovable();">Alta Renovable</a>
					</c:if>
				</td>
				<!--  Pet. 63482 ** MODIF TAM (16/04/2021) * Fin -->
				<td align="right">				
					<c:if test="${origenLlamada == 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a>
					</c:if> 
					<c:if test="${origenLlamada != 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
					</c:if>
					<!--<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>-->
					
					<a class="bot" id="btnLimpiar" href="javascript:limpiar();">Limpiar</a>
				</td>
			</tr>
		</table>
	</div>
	
	
	<div class="conten" style="padding: 3px; width: 103%">
		<p class="titulopag" align="left">Listado de P&oacute;lizas Renovables</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>

		<form:form id="main3" name="main3" method="post" action="polizasRenovables.run" commandName="polizaRenovableBean">
			<input type="hidden" name="method" id="method" />
			<input type="hidden" id="perfil" name="perfil" value="${perfil}" />
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}" />
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
			<input type="hidden" name="entidadH" id="entidadH" />
			<input type="hidden" name="oficinaH" id="oficinaH" />
			<input type="hidden" name="entmediadoraH" id="entmediadoraH" />
			<input type="hidden" name="subentmediadoraH" id="subentmediadoraH" />
			<input type="hidden" name="nomEntidadH" id="nomEntidadH" />
			<input type="hidden" name="nomOficinaH" id="nomOficinaH" />
			<input type="hidden" name="delegacionH" id="delegacionH" />
			<input type="hidden" name="pctDescMaxH" id="pctDescMaxH" />
			<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
			<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
			<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>		
			<input type="hidden" name="fechaCargaIni.day" value="" />
			<input type="hidden" name="fechaCargaIni.month" value="" />
			<input type="hidden" name="fechaCargaIni.year" value="" />		
			<input type="hidden" name="fechaCargaFin.day" value="" />
			<input type="hidden" name="fechaCargaFin.month" value="" />
			<input type="hidden" name="fechaCargaFin.year" value="" />		
			<input type="hidden" name="fechaRenoIni.day" value="" />
			<input type="hidden" name="fechaRenoIni.month" value="" />
			<input type="hidden" name="fechaRenoIni.year" value="" />
			<input type="hidden" name="fechaRenoFin.day" value="" />
			<input type="hidden" name="fechaRenoFin.month" value="" />
			<input type="hidden" name="fechaRenoFin.year" value="" />
			<input type="hidden" name="fechaEnvioIBANIni.day" value="" />
			<input type="hidden" name="fechaEnvioIBANIni.month" value="" />
			<input type="hidden" name="fechaEnvioIBANIni.year" value="" />
			<input type="hidden" name="fechaEnvioIBANFin.day" value="" />
			<input type="hidden" name="fechaEnvioIBANFin.month" value="" />
			<input type="hidden" name="fechaEnvioIBANFin.year" value="" />
			<input type="hidden" name="estAgroplus"   value="${estAgroplus}"/>		
			<input type="hidden" name="estAgroseguro" value="${estAgroseguro}"/>
			<input type="hidden" name="estEnvioIBAN"  value="${estEnvioIBAN}"/>
			<input type="hidden" name="grupoNegocio"    value="${grupoNegocio}"/>
			<input type="hidden" name="lstErroresEnvioIBAN" id="lstErroresEnvioIBAN" value="${lstErroresEnvioIBAN}" />
			<form:hidden path="id" id="id" />

			<fieldset style="margin:0 auto; padding:1;">
				<legend class="literal">Filtro</legend>
				<table style="margin:0 auto;">
					<tr>
						<td class="literal">Entidad</td>
						<td>
							<c:if test="${perfil == 0 || perfil == 5}">
								<nobr>
								<form:input path="codentidad" size="4"
									maxlength="4" cssClass="dato" id="entidad"
									onchange="javascript:lupas.limpiarCampos('desc_entidad', 'entmediadora', 'subentmediadora');" />
								<input class="dato" id="desc_entidad" size="30" readonly="true" tabindex="-1" value="${nomEntidad}"/> 
								<img src="jsp/img/magnifier.png" style="cursor: hand;"
									onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');"
									alt="Buscar Entidad" title="Buscar Entidad" />
								<label class="campoObligatorio" id="campoObligatorio_entidad"
									title="Campo obligatorio">*</label>
								</nobr>
							</c:if>
							<c:if test="${perfil == 1}">
								<form:input path="codentidad" size="4" maxlength="4" cssClass="dato" readonly="true" id="entidad" />
								<input class="dato" id="desc_entidad" size="40" readonly="true" tabindex="-1" value="${nomEntidad}"/>
							</c:if>
						</td>
						
						<td class="literal">E-S Med</td>
						<c:if test="${(perfil == 0 || perfil == 5) || (perfil == 1 && externo == 0)}">
							<td colspan="3">
								<nobr>
								<form:input	path="codentidadmed" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
								<form:input	path="codsubentmed" size="4" maxlength="4" cssClass="dato" id="subentmediadora" />
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
								</nobr>
							</td>
						</c:if>
						<c:if test="${perfil == 1 && externo == 1}">
							<td colspan="3">
								<nobr>
								<form:input	path="codentidadmed" size="4" maxlength="4"	cssClass="dato" readonly="true" tabindex="-1" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
								<form:input	path="codsubentmed" size="4" maxlength="4" cssClass="dato" readonly="true" tabindex="-1" id="subentmediadora" />
								</nobr>
							</td>
						</c:if>
						<td></td>
						<td class="literal">Tomador</td>
						
						<td>
							<nobr>
							<form:input	path="nifTomador" size="9" maxlength="9" cssClass="dato" id="tomador" onchange="javascript:this.value = this.value.toUpperCase();lupas.limpiarCampos('desc_tomador');" /> 
							<input class="dato"	id="desc_tomador" size="28" readonly="true" tabindex="-1" value="${razonSocial}"/>
							<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Tomador','principio', '', '');" alt="Buscar Tomador" title="Buscar Tomador" />
							<label class="campoObligatorio"	id="campoObligatorio_tomador" title="Campo obligatorio"> *</label>
							</nobr>
						</td>
					</tr>

					<tr>
						<td class="literal">Plan</td>
						<td>
							<form:input path="plan" size="4" maxlength="4" 
								cssClass="dato" id="plan"
								onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" />
							<label class="campoObligatorio" id="campoObligatorio_plan"
							title="Campo obligatorio"> *</label>
						</td>
						<td class="literal">L&iacute;nea</td>
						<nobr>
						<td colspan="3">
							<nobr>
							<form:input path="linea" size="3" maxlength="3" cssClass="dato" id="linea" 
								onchange="javascript:lupas.limpiarCampos('desc_linea');" />
							<input path="linea.nomlinea" class="dato" id="desc_linea" size="30" readonly="true" tabindex="-1"/>
							</nobr>
						</td>
								
						<td>
							<img src="jsp/img/magnifier.png"
							style="cursor: hand;"
							onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');"
							alt="Buscar L&iacute;nea" title="Buscar L&iacute;nea" />
							<label class="campoObligatorio" id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
						</td>
						
						<td class="literal">Grupo Negocio</td>
						
						<td >			
							<select id="polRenGrupoNegocio" class="dato"  style="width: 100px">
								<option value=""></option>
								<c:forEach items="${gruposNegocio}" var="grupoNegocio">
									<option value="${grupoNegocio.grupoNegocio}">${grupoNegocio.descripcion}</option>
								</c:forEach>
							</select>
						</td>	
												
					</tr>

					<tr>
						<td class="literal">P&oacute;liza</td>
						<td>
							<form:input path="referencia" size="9" maxlength="9" 
								cssClass="dato" id="referencia" onchange="this.value = this.value.toUpperCase();"/>
							<label class="campoObligatorio" id="campoObligatorio_referencia"
								title="Campo obligatorio"> *</label>
						</td>

						<td class="literal">Colectivo</td>
						<td colspan="3">
							<form:input path="refcol" size="9" 
								maxlength="9" cssClass="dato" id="colectivo" onchange="this.value = this.value.toUpperCase();"/>
							<label class="campoObligatorio" id="campoObligatorio_Colectivo"
								title="Campo obligatorio"> *</label>
						</td>
						<td></td>
						<td class="literal">NIF/CIF Asegurado</td>
						
						<td>
							<form:input path="nifAsegurado" size="9" maxlength="9" 
								cssClass="dato" id="nifAsegurado" onchange="this.value = this.value.toUpperCase();"/> <label
								class="campoObligatorio" id="campoObligatorio_NifAsegurado"
								title="Campo obligatorio"> *</label>
						</td>
							
					</tr>
					
					<input type="hidden" name="method" id="method" value="doCambioMasivo"/>
					<input type="hidden" name="listaIdsMarcados_cm" id="listaIdsMarcados_cm" value=""/>

					<tr>
						<td class="literal">Estado Agroplus</td>
						<td>
							<select	 id="estadoRenovacionAgroplus" class="dato"  style="width: 200px">
								<option value="">Todos</option>
								<c:forEach items="${estadosRenAgroplus}" var="estadoAgroplus">
									<option value="${estadoAgroplus.codigo}">${estadoAgroplus.descripcion}</option>
								</c:forEach>
							</select>
						</td> 											
						<td class="literal">Estado Agroseguro</td>
							
						<td colspan="3">
							<select id="estadoRenovacionAgroseguro" class="dato"  style="width: 175px">
								<option value="">Todos</option>
								<c:forEach items="${estadosRenAgroseguro}" var="estadoAgroseguro">
									<option value="${estadoAgroseguro.codigo}">${estadoAgroseguro.descripcion}</option>
								</c:forEach>
							</select>
						</td>
						<td></td>
						<td class="literal">Estado env&iacute;o IBAN</td>
						
						<td >			
							<select id="polizaRenovableEstadoEnvioIBAN" class="dato"  style="width: 140px">
								<option value="">Todos</option>
								<c:forEach items="${estadosRenEnvioIBAN}" var="estadoEnvioIBAN">
									<option value="${estadoEnvioIBAN.codigo}">${estadoEnvioIBAN.descripcion}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					
					<tr>
						<td colspan="2">
							<fieldset>
								<legend class="literal" >Fecha Carga</legend>
								<table style="width:100%">
							    	<tr>
							    		<td class="literal">desde</td>
							    		<td><p class="txt">
							    			<input type="text" id="fechaCargaIni" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Desde')) this.value='';" name="fechaCargaIni" class="dato" value="${fecCargaIni}" size="9" maxlength="10" />
							    			<!--  <a id="btn_fechaCargaIni" name="btn_fechaCargaIni"><img src="jsp/img/calendar.gif"/></a>-->
							    			<input type="button" id="btn_fechaCargaIni" name="btn_fechaCargaIni" class="miniCalendario" style="cursor: pointer;" />
							    			</p>
										</td>
										<td class="literal">&nbsp;&nbsp;hasta</td>
							    		<td><p class="txt">
							    			<input type="text" id="fechaCargaFin" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Hasta')) this.value='';" name="fechaCargaFin" class="dato" value="${fecCargaFin}" size="9" maxlength="10" />
							    			<!-- <a id="btn_fechaCargaFin" name="btn_fechaCargaFin"><img src="jsp/img/calendar.gif"/></a>-->
							    			<input type="button" id="btn_fechaCargaFin" name="btn_fechaCargaFin" class="miniCalendario" style="cursor: pointer;" />
							    			</p>
										</td>
							    	</tr>
							    </table>
							</fieldset>
						</td>
						<td colspan="4">
							<fieldset>
								<legend class="literal" >Fecha Renovaci&oacute;n</legend>
								<table style="width:100%">
							    	<tr>
							    		<td class="literal" style="padding-left:1px;">desde</td>
							    		<td><p class="txt">      			
							    			<input type="text" id="fechaRenoIni" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Desde')) this.value='';" name="fechaRenoIni" class="dato" value="${fecRenoIni}" size="9" maxlength="10" />
							    			<!-- <a id="btn_fechaRenoIni" name="btn_fechaRenoIni"><img src="jsp/img/calendar.gif"/></a>-->
							    			<input type="button" id="btn_fechaRenoIni" name="btn_fechaRenoIni" class="miniCalendario" style="cursor: pointer;" />
							    			</p>
										</td>
										<td class="literal" style="padding-left:1px;">&nbsp;&nbsp;hasta</td>
							    		<td><p class="txt">
							    			<input type="text" id="fechaRenoFin" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Hasta')) this.value='';" name="fechaRenoFin" class="dato" value="${fecRenoFin}" size="9" maxlength="10" />
							    			<!-- <a id="btn_fechaRenoFin" name="btn_fechaRenoFin"><img src="jsp/img/calendar.gif"/></a> -->
							    			<input type="button" id="btn_fechaRenoFin" name="btn_fechaRenoFin" class="miniCalendario" style="cursor: pointer;" />
							    			</p>
										</td>
							    	</tr>
							    </table>
							</fieldset>
						</td>
						<td colspan="3">
							<fieldset>
								<legend class="literal" >Fecha env&iacute;o IBAN</legend>
								<table style="width:100%">
							    	<tr>
							    		<td class="literal">desde</td>
							    		<td><p class="txt">      			
							    			<input type="text" id="fechaEnvioIBANIni" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Desde')) this.value='';" name="fechaEnvioIBANIni" class="dato" value="${fecEnvioIBANIni}" size="9" maxlength="10" />
							    			<!-- <a id="btn_fechaEnvioIBANIni" name="btn_fechaEnvioIBANIni"><img src="jsp/img/calendar.gif"/></a>-->
							    			<input type="button" id="btn_fechaEnvioIBANIni" name="btn_fechaEnvioIBANIni" class="miniCalendario" style="cursor: pointer;" />
							    			</p>
										</td>
										<td class="literal">&nbsp;&nbsp;hasta</td>
							    		<td><p class="txt">
							    			<input type="text" id="fechaEnvioIBANFin" onblur="if (!ComprobarFecha(this, document.main3, 'Fecha Hasta')) this.value='';" name="fechaEnvioIBANFin" class="dato" value="${fecEnvioIBANFin}" size="9" maxlength="10"/>
							    			<!-- <a id="btn_fechaEnvioIBANFin" name="btn_fechaEnvioIBANFin"><img src="jsp/img/calendar.gif"/></a> -->
							    			<input type="button" id="btn_fechaEnvioIBANFin" name="btn_fechaEnvioIBANFin" class="miniCalendario" style="cursor: pointer;" />
							    			</p>
										</td>
							    	</tr>
							    </table>
							</fieldset>
						</td>
					</tr>
	
				</table>
			</fieldset>
		</form:form>
		<!-- Grid Jmesa -->
		<div id="grid" style="width:100%;margin:5 auto;">${polRenovables}</div>
		
		<!-- Formulario para exportar a excel el listado -->
		<form name="exportToExcel" id="exportToExcel" action="polizasRenovables.run" method="post">
			<input type="hidden" name="method" id="method" value="doExportToExcel"/>
		</form>
		
		<div style="width:20%;text-align:center;margin:0 auto;" id="divImprimir">
				<a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Exportar</a>	
					<a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:exportToExcel()">
						<img src="jsp/img/jmesa/excel.gif"/>
					</a>
		</div>		
		
	</div>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>
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
			            <span onclick="cerrarPopUpAvisos()">x</span>
			        </a>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content" id="panelInfor">
					<div id="panelInformacion2" class="panelInformacion">
						<div id="txt_info" style="width: 70%" ></div>
						<div id="txt_info_none" style="width: 70%;" >No hay p&oacute;lizas seleccionadas.</div>
					</div>
			</div>
			
			
			
			
		</div>
		
		<div id="divAcuse" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
	       <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;
			                                  color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
			                                  background:#525583;height:15px">
			        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
			            Acuse de recibo
			        </div>
			        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
			                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
			            <span onclick="cerrarPopUpAvisos()">x</span>
			        </a>
			</div>
			<!--  body popup -->
			<!--  body popup -->
			<div class="panelInformacion_content" id="panelInfor">
					<div id="panelInformacion2" class="panelInformacion">
						<div id="txt_info" style="width: 70%" ></div>
						<div id="txt_info_acuse" style="width: 70%;" ></div>
					</div>
			</div>
			<!-- Botones popup --> 
		    <div style="margin-top:5px" align="center">
				<a class="bot" href="javascript:cerrarPopUpAvisos()">Cerrar</a>
			</div>
		</div>
	
	
	

</body>

</html>