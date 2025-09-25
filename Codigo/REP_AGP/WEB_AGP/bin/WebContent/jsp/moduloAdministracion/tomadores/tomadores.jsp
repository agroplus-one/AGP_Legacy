<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" buffer="100kb"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>
</fmt:bundle>
<fmt:bundle basename="agp" >
	<c:set var="numEle"><fmt:message key="visores.numElements"/></c:set>
</fmt:bundle>

<jsp:directive.page import="org.displaytag.*" />
<fmt:setBundle basename="displaytag"/>

<html>
	<head>
		<title>Mantenimiento de Tomadores</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js" charset="UTF-8"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js" charset="UTF-8"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js" ></script>
		<script type="text/javascript" src="jsp/moduloAdministracion/tomadores/tomadores.js" charset="UTF-8"></script>
		<%@ include file="/jsp/js/draggable.jsp"%>

		<script type="text/javascript" charset="UTF-8">
			$(document).ready(function(){
				<c:if test="${activarModoModificar == 'true'}">
					$('#operacion').val("modificar");
					$('#btnAlta').hide();
					$('#btnModificar').show();
				</c:if>
				<c:if test="${empty listaTomadores}">
					$('#btnImprimir').hide();
				</c:if>
			});
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub2');generales.fijarFila();">

		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons" style="margin:5px">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a>
						<a class="bot" id="btnModificar" style="display:none" href="javascript:guardarModificaciones();">Modificar</a>
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
						<a class="bot" id="btnLimpiar" href="javascript:limpiar()">Limpiar</a>
					</td>
				</tr>
			</table>
		</div>
		<!-- Contenido de la pagina -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">Administraci&oacute;n de Tomadores</p>
			<form:form name="main" id="main" action="tomador.html" method="post" commandName="tomadorBean">
				<input type="hidden" name="operacion" id="operacion" />
				<input type="hidden" name="ciftomadorAccion" id="ciftomadorAccion" />
				<input type="hidden" name="codentidadAccion" id="codentidadAccion" />
				<input type="hidden" id="perfil" value="${perfil}" />
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
					<div class="panel2 isrt">
					<table width="95%" align="left">
						<tr align="left">
							<td class="literal">Entidad
							<c:if test="${perfil == 0 || perfil == 5}">
								<form:input path="id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entidad" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_entidad');"   />
								<form:input path="entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="40" readonly="true"/>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad"/>
								<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio"> *</label>
							</c:if>
							<c:if test="${perfil > 0 && perfil < 5}">
								<form:input path="id.codentidad" size="4" maxlength="4" cssClass="dato" id="entidad" tabindex="1" readonly="true"/>
							</c:if>
							<td class="literal">CIF Tomador
								<form:input path="id.ciftomador" size="9" maxlength="9" cssClass="dato" id="ciftomador" tabindex="2" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
								<label class="campoObligatorio" id="campoObligatorio_ciftomador" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal" align="center">Raz&oacute;n social&nbsp;
								<form:input path="razonsocial" size="45" maxlength="45" cssClass="dato" id="razonSocial" tabindex="3" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" id="campoObligatorio_razonSocial" title="Campo obligatorio"> *</label>
							</td>
						</tr>
					</table>
				</div>			
				
				<div class="panel2 isrt">
					<table width="100%" border="1">
						<tr align="left">
							<td class="literal">V&iacute;a</td>
							<td class="literal">
								<form:input path="via.clave" size="2" maxlength="2" cssClass="dato" id="via" tabindex="4" onchange="javascript:mayusculas(this);javascript:lupas.limpiarCampos('desc_via');"/>
								<form:input path="via.nombre" cssClass="dato"	id="desc_via" size="15" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Via','principio', '', '');" alt="Buscar Via" title="Buscar Via" />	
								<label class="campoObligatorio" id="campoObligatorio_via" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">Domicilio</td>
							<td  class="literal">
								<form:input path="domicilio" size="30" maxlength="23" cssClass="dato" id="domicilio" tabindex="5" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" id="campoObligatorio_domicilio" title="Campo obligatorio"> *</label>
							</td>
							
							
							
							
							<td class="literal" align="right">Env&iacute;o a pagos renovables&nbsp;&nbsp;</td>
							<td>
								<form:select path="envioAPagos" cssClass="dato"	cssStyle="width:70" id="envioAPagos" >
									<form:option value="">Todos</form:option>
									<form:option value="S">Si</form:option>
									<form:option value="N">No</form:option>
								</form:select>
								<label class="campoObligatorio" id="campoObligatorio_envioAPagos" title="Campo obligatorio"> *</label>
							</td>
							
							
							
							
						</tr>
						<tr>
							<td class="literal" align="left">Nº</td>
							<td class="literal">
								<form:input path="numvia" size="5" maxlength="5" cssClass="dato" id="numero" tabindex="6"/>
								<label class="campoObligatorio" id="campoObligatorio_numero" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">Piso</td>
							<td class="literal">
								<form:input path="piso" size="5" maxlength="5" cssClass="dato" id="piso" tabindex="7"/>
							</td>
							<td class="literal" align="right">Bloque&nbsp;&nbsp;</td>
							<td class="literal">
								<form:input path="bloque" size="3" maxlength="3" cssClass="dato" id="bloque" tabindex="8"/>
							</td>
							<td class="literal" >
								Escalera&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<form:input path="escalera" size="3" maxlength="3" cssClass="dato" id="esc" tabindex="9"/>
							</td>
							<td class="literal" colspan="4">&nbsp;</td>
						</tr>
						<tr align="left">
							<td class="literal">Provincia</td>
							<td class="literal">
								<form:input path="localidad.id.codprovincia" size="2" maxlength="2" onchange="javascript:lupas.limpiarCampos('desc_provincia','localidad','desc_localidad','sublocalidad');" cssClass="dato" id="provincia" tabindex="10"/>
								<form:input path="localidad.provincia.nomprovincia" cssClass="dato"	id="desc_provincia" size="20" readonly="true"/>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');" alt="Buscar Provincia" title="Buscar Provincia" />
								<label class="campoObligatorio" id="campoObligatorio_provincia" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">Localidad</td>
							<td class="literal">
								<form:input path="localidad.id.codlocalidad" size="3" maxlength="3" cssClass="dato" id="localidad" onchange="javascript:lupas.limpiarCampos('desc_localidad','sublocalidad');" tabindex="11"/>
								<form:input path="localidad.nomlocalidad" cssClass="dato"	id="desc_localidad" size="30" readonly="true"/>
								<label class="campoObligatorio" id="campoObligatorio_localidad" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal" align="right">Sublocalidad&nbsp;&nbsp;</td>
							<td class="literal">
								<form:input path="localidad.id.sublocalidad" size="4" maxlength="4" cssClass="dato" id="sublocalidad" tabindex="12"/>
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Localidad','principio', '', '');" alt="Buscar Localidad" title="Buscar Localidad"/>
								<label class="campoObligatorio" id="campoObligatorio_sublocalidad" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">
									Cod. postal&nbsp;<form:input path="codpostalstr" size="5" maxlength="5" cssClass="dato" id="cp" tabindex="13"/>
									<label class="campoObligatorio" id="campoObligatorio_cp" title="Campo obligatorio"> *</label>
							</td>
						</tr>
					</table>
				</div>
				<div class="panel2 isrt">
					<table width="95%" align="left">
						<tr align="left">
							<td class="literal">Tel&eacute;fono
								<form:input path="telefono" size="9" maxlength="9" cssClass="dato" id="telefono" tabindex="14"/>
								<label class="campoObligatorio" id="campoObligatorio_telefono" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">M&oacute;vil
								<form:input path="movil" size="9" maxlength="9" cssClass="dato" id="movil" tabindex="15"/>
							</td>
							<td class="literal" align="center">E-mail&nbsp;&nbsp;&nbsp;
								<form:input path="email" size="50" maxlength="50" cssClass="dato" id="email" tabindex="16" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" id="campoObligatorio_email" title="Campo obligatorio"> *</label>
							</td>
							<td>&nbsp;</td>
						<tr align="left">
						</tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<!-- PTC. 78845 ** CAMPOS NUEVOS (01.03.2022) ** Inicio -->
							<td class="literal" align="center">E-mail&nbsp;2&nbsp;
								<form:input path="email2" size="50" maxlength="50" cssClass="dato" id="email2" tabindex="17" onchange="this.value=this.value.toUpperCase();"/>
							</td>
							<td class="literal" align="center">E-mail&nbsp;3&nbsp;
								<form:input path="email3" size="50" maxlength="50" cssClass="dato" id="email3" tabindex="18" onchange="this.value=this.value.toUpperCase();"/>
							</td>
							<!-- PTC. 78845 ** CAMPOS NUEVOS (01.03.2022) ** Fin -->
						</tr>
					</table>
				</div>
				<fieldset class="panel2 isrt">
					<legend><b>Datos del Representante</b></legend>
					<br>
					<table width="100%">
						<tr align="left">
							<td class="literal">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Nombre
								<form:input path="repreNombre" size="18" maxlength="20" cssClass="dato" id="repreNombre" tabindex="19" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" id="campoObligatorio_repreNombre" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">1er Apellido
								<form:input path="repreAp1" size="39" maxlength="40" cssClass="dato" id="repreAp1" tabindex="20" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" id="campoObligatorio_repreAp1" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">2º Apellido
								<form:input path="repreAp2" size="39" maxlength="40" cssClass="dato" id="repreAp2" tabindex="21" onchange="this.value=this.value.toUpperCase();"/>
								<label class="campoObligatorio" id="campoObligatorio_repreAp2" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal" align="center">DNI&nbsp;
								<form:input path="repreNif" size="9" maxlength="9" cssClass="dato" id="repreNif" tabindex="22" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
								<label class="campoObligatorio" id="campoObligatorio_repreNif" title="Campo obligatorio"> *</label>
							</td>
						</tr>
					</table>
					<br>
				</fieldset>
			</form:form>
			<!-- excludedParams="*" !! Eliminamos los parametros para que al hacer una nueva consulta no repita la anterior accion -->
		        <display:table requestURI=""  id="listaTomadores" class="LISTA" summary="tomador" sort="list" pagesize="${numReg}" name="${listaTomadores}" export="false" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorTomadores" excludedParams="operacion" style="width:99%; margin:0 auto;">
		            <display:caption title="caption" media="pdf">Relaci&oacute;n de Tomadores</display:caption>
		           	<display:setProperty name="export.pdf.filename" value="ListadoTomadores.pdf" />
		           	<display:setProperty name="export.pdf.export_amount" value="list"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="tomadorSelec" sortable="false" style="width:50px;text-align:center" media="html"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Ent." property="id.codentidad" sortable="true" />
		            <display:column class="literal" headerClass="cblistaImg" title="Cif tom." property="id.ciftomador" sortable="true" />
		            <display:column class="literal" headerClass="cblistaImg" title="Raz&oacute;n social" property="razonsocial" sortable="true" />            
		            <display:column class="literal" headerClass="cblistaImg" title="Prov." property="localidad.id.codprovincia" sortable="true"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Local." property="localidad.id.codlocalidad" sortable="true" />
		            <display:column class="literal" headerClass="cblistaImg" title="C.P." property="codpostalstr" sortable="true"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Tel&eacute;fono" property="telefono" sortable="true"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Nif Rep." property="repreNif" sortable="true"/>
		            <display:column class="literal" headerClass="cblistaImg" title="Nombre Representante" property="repNombreCompleto" sortable="false"/>
		        </display:table>
		         <div style="width:20%; margin:0 auto;">
		        	<a style="font-family: tahoma, verdana, arial;color: #626262;text-decoration:underline;font-size:11px;cursor:hand;cursor:pointer;" id="btnImprimir" href="javascript:imprimir()">Imprimir</a>	     
				</div>	
		</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLocalidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaVia.jsp"%>
	</body>
</html>