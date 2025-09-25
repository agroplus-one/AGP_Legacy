<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>


<html>
<head>	
	<title>CONSULTA DE COMISIONES 2015+</title>
<%@ include file="/jsp/common/static/metas.jsp"%>
<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />

<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>

<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/jquery.progressbar.js"></script>
<script type="text/javascript" src="jsp/js/jquery.progressbar.min.js"></script>

<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<!--  <script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>-->
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<!--  <script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script> -->
<script type="text/javascript" src="jsp/js/ajaxfileupload.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
									
<script type="text/javascript" src="jsp/moduloComisionesUnificado/InformeComisionesUnificado.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>





	    <%@ include file="/jsp/js/draggable.jsp"%>
	   
	  <script type="text/javascript">
		  	function cargarFiltro(){
				<c:forEach items="${sessionScope.consulta_LIMIT.filterSet.filters}" var="filtro">
					//alert(${filtro.property});
					var inputText = document.getElementById('${filtro.property}');
					//alert(inputText);
					if (null!=inputText){
						inputText.value = '${filtro.value}';
					}
				</c:forEach>
			}
	   </script>
	   <style>
    #divImprimir {
        display: none;
    }
</style>
	   
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="cargarFiltro();SwitchSubMenu('sub9', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	<%@ include file="/jsp/js/generales.jsp"%>
	<div id="buttons">
		<table width="98%" cellspacing="0" cellpadding="0" border="0">
			<tr align="left">
				<td align="right"> 
					<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
					<a class="bot" id="btnLimpiar" href="javascript:limpiar();">Limpiar</a>
				</td>
			</tr>
		</table>
	</div>
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 103%">
		<p class="titulopag" align="left" id="titulo">Consulta de comisiones 2015+</p>
		<form:form name="main3" id="main3" action="informesComisiones2015.run" method="post" commandName="informeBean">
			<input type="hidden" id="perfil" value="${perfil}" />
			<input type="hidden" id="externo" value="${externo}" />
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
			<input type="hidden" name="grupoOficinas" id="grupoOficinas" value=""/>
			<input type="hidden" id="origenLlamada" name="origenLlamada" />
			
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<div style="">
				<fieldset>
					<legend class="literal">Consulta</legend>
					<!-- *****************************PERFIL 0 ********************************************************************** -->
					<c:if test="${perfil ==0}">
						<table align="center" >
							<tr>
								<td class="literal">Entidad</td>
								<td class="literal" colspan="4">
									<form:input path="codentidad" size="4" maxlength="4" cssClass="dato" id="entidad" onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');"/>
									<form:input path="nomentidad" cssClass="dato"	id="desc_entidad" size="35" readonly="true" tabindex="2"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
								</td>
								<td class="literal" align="right" valign="middle">Oficina&nbsp;</td>
								<td class="literal" colspan="3">
										<form:input path="oficina" size="4" maxlength="4" cssClass="dato" id="oficina"  onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
										<form:input path="nomoficina" cssClass="dato"	id="desc_oficina" size="31" readonly="true" tabindex="2"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />								
								</td>
								<td class="literal">E-S Med</td>
								<td class="literal" colspan="2">
									<form:input	path="entmediadora" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
									<form:input	path="subentmediadora" size="4" maxlength="4" cssClass="dato" id="subentmediadora" />
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />								
								</td>
							</tr>
							<tr>
								<td class="literal">Plan</td>
								<td class="literal">
									<form:input path="plan" size="4"maxlength="4" cssClass="dato" id="plan" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" />
								</td>
								
								<td ></td><td class="literal">Línea</td>
								<td class="literal" colspan="4">
									<form:input path="linea" size="3"	maxlength="3" cssClass="dato" id="linea" onchange="javascript:lupas.limpiarCampos('desc_linea');" />
									<form:input path="nomlinea" cssClass="dato"	id="desc_linea" size="40" readonly="true" tabindex="2"/>																		
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
									
								</td> 
								<td></td><td class="literal">Ref. Póliza</td>
								<td class="literal" colspan="2">
									<form:input path="referencia" size="8" maxlength="7" cssClass="dato" id="refPoliza" />
								</td>							
							</tr>
							<tr>
								<td class="literal">Colectivo</td>
								<td class="literal" colspan="2">
									<form:input path="idcolectivo" size="8" maxlength="7" cssClass="dato" id="idcolectivo" />
								</td>
								<td class="literal">NIF/CIF Aseg.</td>
								<td class="literal" colspan="2">
									<form:input path="nifcif" size="9" maxlength="9"  cssClass="dato" id="nifAsegurado" onchange="this.value = this.value.toUpperCase();"/>
								</td>
								<td class="literal">Recibo</td>
								<td class="literal" colspan="2">
									<form:input path="recibo" size="8" maxlength="7" cssClass="dato" id="recibo" />
								</td>
								<td class="literal">Fase</td>
								<td class="literal" colspan="2">
									<form:input path="fase" size="5" maxlength="4" cssClass="dato" id="fase" />
								</td>
							</tr>
							<tr>
								<td class="literal">Fec. Carga</td>
								<td class="literal">
									<form:select path="opcionfechaCarga" cssClass="dato" id="opcionfechaCarga" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td class="literal"><form:input path="entreFechaCarga" id="entreFechaCarga" cssClass="dato" size="13"/></td>
								
								<td class="literal" >Fec. Emisi&oacute;n</td>
								<td class="literal">
									<form:select path="opcionfechaEmisionRecibo" cssClass="dato" id="opcionfechaEmisionRecibo" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td class="literal"><form:input path="entreFechaEmisionRecibo" id="entreFechaEmisionRecibo" cssClass="dato" size="13"/></td>
								
								<td class="literal">Fec. Aceptaci&oacute;n</td>
								<td class="literal">
									<form:select path="opcionfechaAceptacion" cssClass="dato" id="opcionfechaAceptacion" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td class="literal"><form:input path="entreFechaAceptacion" id="entreFechaAceptacion" cssClass="dato" size="13"/></td>
								
								<td class="literal">Fec. Cierre</td>
								<td class="literal">
									<form:select path="opcionfechaCierre" cssClass="dato" id="opcionfechaCierre" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td class="literal"><form:input path="entreFechaCierre" id="entreFechaCierre" cssClass="dato" size="13"/></td>
							
							</tr>
							<tr>
								<td class="literal">Fec. Vigor</td>
								<td class="literal">
									<form:select path="opcionfechaVigor" cssClass="dato" id="opcionfechaVigor" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>
								<td class="literal"><form:input path="entreFechaVigor" id="entreFechaVigor" cssClass="dato" size="13"/></td>
							</tr>
						</table>
					</c:if>
					<!-- ***************************** FIN PERFIL 0 ********************************************************************** -->
					<!-- *****************************PERFIL 1 INTERNO Y PERFIL 5 ********************************************************************** -->
					<c:if test="${perfil == 5 or (externo==0 and perfil==1)}">
						<table align="center" >
							<tr>
							    <td class="literal">Entidad</td>							
																									
								<c:if test="${perfil != '5'}">									
									<td colspan="4">
										<form:input path="codentidad" size="4" maxlength="4"	cssClass="dato" id="entidad" readonly="true" tabindex="1"/>
										<form:input path="nomentidad" cssClass="dato"	id="desc_entidad" size="35" readonly="true" tabindex="2"/>
									</td>
								</c:if>
								<c:if test="${perfil eq '5'}">	
									<td colspan="4">
										<form:input path="codentidad" size="4" maxlength="4" cssClass="dato" id="entidad" onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');"/>
										<form:input path="nomentidad" cssClass="dato"	id="desc_entidad" size="35" readonly="true" tabindex="2"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
									</td>
								</c:if>
							
								<td class="literal" align="right" valign="middle">Oficina&nbsp;</td>
								<td colspan="3">
										<form:input path="oficina" size="4" maxlength="4" cssClass="dato" id="oficina"  onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
										<form:input path="nomoficina" cssClass="dato"	id="desc_oficina" size="32" readonly="true" tabindex="2"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />								
								</td>
								<td class="literal">E-S Med</td>
								<td  colspan="2">								
									<form:input	path="entmediadora" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
									<form:input	path="subentmediadora" size="4" maxlength="4" cssClass="dato" id="subentmediadora" />
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
								</td>
							</tr>
							<tr>
								<td class="literal">Plan</td>
								<td>
									<form:input path="plan" size="4"maxlength="4" cssClass="dato" id="plan" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" />
								</td>
								<td></td><td class="literal">Línea</td>
								<td colspan="4">
									<form:input path="linea" size="3"	maxlength="3" cssClass="dato" id="linea" onchange="javascript:lupas.limpiarCampos('desc_linea');" />
									<form:input path="nomlinea" cssClass="dato"	id="desc_linea" size="40" readonly="true" tabindex="2"/>																
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
									
								</td> 
								<td></td><td class="literal">Ref. Póliza</td>
								<td colspan="2">
									<form:input path="referencia" size="8" maxlength="7" cssClass="dato" id="refPoliza" />
								</td>								
							</tr>
							<tr>
								<td class="literal">Colectivo</td>
								<td colspan="2">
									<form:input path="idcolectivo" size="8" maxlength="7" cssClass="dato" id="idcolectivo" />
								</td>
								<td class="literal">NIF/CIF Aseg.</td>
								<td colspan="2">
									<form:input path="nifcif" size="9" maxlength="9"  cssClass="dato" id="nifAsegurado" onchange="this.value = this.value.toUpperCase();"/>
								</td>
								<td class="literal">Recibo</td>
								<td colspan="2">
									<form:input path="recibo" size="8" maxlength="7" cssClass="dato" id="recibo" />
								</td>
								<td class="literal">Fase</td>
								<td colspan="2">
									<form:input path="fase" size="5" maxlength="4" cssClass="dato" id="fase" />
								</td>	 
							</tr>
							<tr>
								<td class="literal">Fec. Carga</td>
								<td>
									<form:select path="opcionfechaCarga" cssClass="dato" id="opcionfechaCarga" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td><form:input path="entreFechaCarga" id="entreFechaCarga" cssClass="dato" size="13"/></td>															
								
								<td class="literal">Fec. Emisi&oacute;n</td>
								<td>
									<form:select path="opcionfechaEmisionRecibo" cssClass="dato" id="opcionfechaEmisionRecibo" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td><form:input path="entreFechaEmisionRecibo" id="entreFechaEmisionRecibo" cssClass="dato" size="13"/></td>	
								
								<td class="literal">Fec. Aceptaci&oacute;n</td>
								<td>
									<form:select path="opcionfechaAceptacion" cssClass="dato" id="opcionfechaAceptacion" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td><form:input path="entreFechaAceptacion" id="entreFechaAceptacion" cssClass="dato" size="13"/></td>
								<td class="literal">Fec. Cierre</td>
								<td>
									<form:select path="opcionfechaCierre" cssClass="dato" id="opcionfechaCierre" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td><form:input path="entreFechaCierre" id="entreFechaCierre" cssClass="dato" size="13"/></td>
							</tr>
							<tr>
								<td class="literal">Fec. Vigor</td>
								<td>
									<form:select path="opcionfechaVigor" cssClass="dato" id="opcionfechaVigor" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>
								<td><form:input path="entreFechaVigor" id="entreFechaVigor" cssClass="dato" size="13"/></td>
							</tr>
						</table>
					</c:if>
					<!-- ***************************** FIN PERFIL 1 INTERNO Y PERFIL 5 ********************************************************************** -->
					
					<!-- *****************************PERFIL 1 EXTERNO ********************************************************************** -->
					<c:if test="${(externo==1 and perfil==1)}">
						<table align="center" >
							<tr>
								<td class="literal">Entidad</td>
								<td colspan="4">
									<form:input path="codentidad" size="4" maxlength="4"	cssClass="dato" id="entidad" readonly="true" tabindex="1"/>
									<form:input path="nomentidad" cssClass="dato"	id="desc_entidad" size="35" readonly="true" tabindex="2"/>
								</td>
								<td class="literal" align="right" valign="middle">Oficina&nbsp;</td>
								<td colspan="3">
										<form:input path="oficina" size="4" maxlength="4" cssClass="dato" id="oficina"  onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
										<form:input path="nomoficina" cssClass="dato"	id="desc_oficina" size="32" readonly="true" tabindex="2"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />								
								</td>
								<td class="literal">E-S Med</td>
								<td  colspan="2">								
									<form:input	path="entmediadora" size="4" maxlength="4"	cssClass="dato" id="entmediadora" readonly="true"/>
									<form:input	path="subentmediadora" size="4" maxlength="4" cssClass="dato" id="subentmediadora" readonly="true"/>									
								</td>
							</tr>
							</tr>
								<td class="literal">Plan</td>
								<td>
									<form:input path="plan" size="4"maxlength="4" cssClass="dato" id="plan" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" />
								</td>
								<td></td><td class="literal">Línea</td>
								<td colspan="4">
									<form:input path="linea" size="3"	maxlength="3" cssClass="dato" id="linea" onchange="javascript:lupas.limpiarCampos('desc_linea');" />
									<form:input path="nomlinea" cssClass="dato"	id="desc_linea" size="55" readonly="true" tabindex="2"/>															
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
									
								</td> 
								<td></td><td class="literal">Ref. Póliza</td>
								<td colspan="2">
									<form:input path="referencia" size="8" maxlength="7" cssClass="dato" id="refPoliza" />
								</td>
								
							</tr>
							<tr>
								<td class="literal">Colectivo</td>
								<td colspan="2">
									<form:input path="idcolectivo" size="8" maxlength="7" cssClass="dato" id="idcolectivo" />
								</td>
								<td class="literal">NIF/CIF Aseg.</td>
								<td colspan="2">
									<form:input path="nifcif" size="9" maxlength="9"  cssClass="dato" id="nifAsegurado" onchange="this.value = this.value.toUpperCase();"/>
								</td>
								<td class="literal">Recibo</td>
								<td colspan="2">
									<form:input path="recibo" size="8" maxlength="7" cssClass="dato" id="recibo" />
								</td>
								<td class="literal">Fase</td>
								<td colspan="2">
									<form:input path="fase" size="5" maxlength="4" cssClass="dato" id="fase" />
								</td>
							</tr>
							<tr>
								<td class="literal">Fec. Carga</td>
								<td>
									<form:select path="opcionfechaCarga" cssClass="dato" id="opcionfechaCarga" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td><form:input path="entreFechaCarga" id="entreFechaCarga" cssClass="dato" size="13"/></td>												
								
								<td class="literal">Fec. Emisión</td>
								<td>
									<form:select path="opcionfechaEmisionRecibo" cssClass="dato" id="opcionfechaEmisionRecibo" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td><form:input path="entreFechaEmisionRecibo" id="entreFechaEmisionRecibo" cssClass="dato" size="13"/></td>	
								
								 <td class="literal">Fec. Aceptación</td>
								<td>
									<form:select path="opcionfechaAceptacion" cssClass="dato" id="opcionfechaAceptacion" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td><form:input path="entreFechaAceptacion" id="entreFechaAceptacion" cssClass="dato" size="13"/></td>
								<td class="literal">Fec. Cierre</td>
								<td>
									<form:select path="opcionfechaCierre" cssClass="dato" id="opcionfechaCierre" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>	
								<td><form:input path="entreFechaCierre" id="entreFechaCierre" cssClass="dato" size="13"/></td>		
							</tr>
							<tr>
								<td class="literal">Fec. Vigor</td>
								<td>
									<form:select path="opcionfechaVigor" cssClass="dato" id="opcionfechaVigor" cssStyle="width:56" >
											<form:option value="" ></form:option>
											<form:option value="eq">Igual</form:option>
											<form:option value="lt-gt">Entre</form:option>											
									</form:select>
								</td>
								<td><form:input path="entreFechaVigor" id="entreFechaVigor" cssClass="dato" size="13"/></td>
							</tr>
						</table>
					</c:if>
					<!-- ***************************** FIN PERFIL 1 EXTERNO ********************************************************************** -->
				</fieldset>
			</div>
		</form:form>
		
		<div id="grid" style="width: 100%; margin:0 auto;">
  			${consultaInformeComisionesUnificado}		  							               
		</div> 	
		
		<!-- Formulario para exportar a excel el listado -->
		<form name="exportToExcel" id="exportToExcel"
			action="informesComisiones2015.run" method="post">
			<input type="hidden" name="method" id="method"
				value="doExportToExcel" />
		</form>
		
		<div style="width: 20%; text-align: center; margin: 0 auto;"
			id="divImprimir">
			<a
				style="font-family: tahoma, verdana, arial; color: #626262; font-size: 11px;">Exportar</a>
			<a id="btnImprimirExcel" style="text-decoration: none;"
				href="javascript:exportToExcel()"> <img
				src="jsp/img/jmesa/excel.gif" />
			</a>
		</div>
		
	</div>
		
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
</body>


</html>