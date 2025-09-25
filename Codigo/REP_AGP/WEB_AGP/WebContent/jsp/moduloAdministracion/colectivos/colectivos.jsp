<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>

<html>
<head>
<title>Mantenimiento de Colectivos</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />

<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
<script type="text/javascript" src="jsp/js/calendar.js"></script>
<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
<script type="text/javascript" src="jsp/moduloAdministracion/colectivos/colectivos.js" ></script>
<script type="text/javascript" src="jsp/js/iban.js" ></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
<%@ include file="/jsp/js/draggable.jsp"%>

<script type="text/javascript">

	$(document).ready(function(){
		<c:if test="${addBotonVolver}">
			$('#btnVolver').show();
		</c:if>
		        
		<c:if test="${empty listaColectivos}">					
			$('#btnImprimir').hide(); 				
		</c:if>	
		MuestraActivo();
	});
	
	var tiempo=10;
	var contador=0;

	
</script>

</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="SwitchMenu('sub2');generales.fijarFila();muestraCuenta();">

<%@ include file="/jsp/common/static/cabecera.jsp"%>
<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

<div id="buttons">
<table width="97%" cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td align="right">
			<a class="bot" id="btnVolver"href="javascript:volver()" style="display:none">Volver</a>
			<a class="bot" id="btnAlta" href="javascript:alta()">Alta</a> 
			<a class="bot" id="btnModificar" style="display: none" href="javascript:guardarModificaciones()">Modificar</a>
			<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a> 
			<a class="bot" id="btnLimpiar"href="javascript:limpiarDatos()">Limpiar</a>
		</td>
	</tr>
</table>
</div>
<!-- Contenido de la p&aacute;gina -->
<div class="conten" style="padding: 3px; width: 97%">
<p class="titulopag" align="left">Administraci&oacute;n de Colectivos</p>

<form:form name="imprimir" id="imprimir" action="informes.html" method="post" target="_blank">
	<input type="hidden" name="method" id="method" value="doInformeColectivoAlta"/>
	<input type="hidden" name="id" id="idImprimir"/>
</form:form>

<form:form name="main" id="main" action="colectivo.html" method="post" commandName="colectivoBean">
	<form:hidden path="id" id="id" />
	<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
	<form:hidden path="isCRM" id="isCRM"/>
	<input type="hidden" name="listaEntCRM" id="listaEntCRM" value="${listaEntCRM}"/>
	<input type="hidden" name="operacion" id="operacion" />
	<input type="hidden" name="fechaIni.day" value="">
	<input type="hidden" name="fechaIni.month" value="">
	<input type="hidden" name="fechaIni.year" value="">
	<input type="hidden" name="fechaFin.day" value="">
	<input type="hidden" name="fechaFin.month" value="">
	<input type="hidden" name="fechaFin.year" value="">
	<input type="hidden" id="perfil" value="${perfil}" />
	<input type="hidden" id="externo" value="${externo}" />
	<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
	<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
	<input type="hidden" name="idFicheroComisiones" id="idFicheroComisiones" value="${idFicheroComisiones}"/>
	<input type="hidden" name="tipoFicheroComisiones" id="tipoFicheroComisiones" value="${tipoFicheroComisiones}"/>
	<input type="hidden" name="vengoDComisiones" id="vengoDComisiones" value="${vengoDComisiones}"/>
	<input type="hidden" name="procedencia" id="procedencia" value="${procedencia}"/>
	<input type="hidden" id="estado" value="${estado}" />
	<input type="hidden" id="idCol" name="idCol" value="${idCol}" />
	<input type="hidden" id="repreNombre" value="${repreNombre}" />
	<input type="hidden" id="repreAp1" value="${repreAp1}" />
	<input type="hidden" id="repreAp2" value="${repreAp2}" />
	<input type="hidden" id="repreNif" value="${repreNif}" />
	<input type="hidden" id="estadoInforme" value="${estadoInforme}" />
	
	<input type="hidden" id="idColPopUp" name="idColPopUp" value="${idColPopUp}" />
	
	<form:hidden path="pctdescuentocol" id="pctdescuentocol" />
	
	
	<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
	<%@ include file="/jsp/common/static/avisoErroresLocal.jsp"%>
	
	<div id="panelInformacionColectivos" style="width:500px;height:20px;color:black;border:1px solid #DD3C10;display:none;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8">
	</div>
	
	<div class="panel2 isrt" style="width:96%;padding: 8px;">
	
	<table width="100%">
		<tr>
			<td class="literal"  style="width:105px">Entidad</td>
			<td>
				<c:if test="${perfil == 0 || perfil == 5}">
					<form:input path="tomador.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entidad" onchange="javascript:lupas.limpiarCampos('desc_entidad');" />
					<form:input path="tomador.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="40" readonly="true"/>
					<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
				</c:if> 
				<c:if test="${perfil > 0 && perfil < 5}">
					<form:input path="tomador.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entidad" readonly="true" tabindex="1"/>
					<form:input path="tomador.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="40" readonly="true"/>
				</c:if> 
				<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio"> *</label>
			</td>
			<td class="literal">Plan</td>
			<td>
				<form:input path="linea.codplan" size="4"maxlength="4" cssClass="dato" id="plan" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');controlDesctoRecargo();" /> 
				<label class="campoObligatorio" id="campoObligatorio_plan"	title="Campo obligatorio"> *</label>
			</td>
			<td class="literal">L&iacute;nea</td>
			<td>
				<form:input path="linea.codlinea" size="3"	maxlength="3" cssClass="dato" id="linea" onchange="javascript:lupas.limpiarCampos('desc_linea');" />
				<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
				<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar L&iacute;nea" title="Buscar L&iacute;nea" />
				<label class="campoObligatorio"	id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
			</td>
		</tr>
		<tr>
			<td class="literal">CIF Tomador</td>
			<td colspan="5">
				<form:input	path="tomador.id.ciftomador" size="9" maxlength="9" cssClass="dato"	id="tomador" onchange="javascript:this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);lupas.limpiarCampos('desc_tomador');"/> 
				<form:input path="tomador.razonsocial" cssClass="dato"	id="desc_tomador" size="40" readonly="true"/>
				<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Tomador','principio', '', '');" alt="Buscar Tomador" title="Buscar Tomador" />
				<label class="campoObligatorio"	id="campoObligatorio_tomador" title="Campo obligatorio"> *</label>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td class="literal"  style="width:105px">Colectivo</td>
			<td>
				<form:input path="idcolectivo" size="5"maxlength="7" cssClass="dato" id="colectivo" /> 
				<form:input	path="dc" size="1" maxlength="1" cssClass="dato" id="dc" /> 
				<label	class="campoObligatorio" id="campoObligatorio_colectivo" title="Campo obligatorio"> *</label> 
				<label class="campoObligatorio"	id="campoObligatorio_dc" title="Campo obligatorio"> *</label>
			</td>
			<td class="literal">Nombre colectivo</td>
			<td>
				<form:input path="nomcolectivo"	size="30" maxlength="50" cssClass="dato" id="nomcolectivo" onchange="this.value=this.value.toUpperCase();" /> 
				<label	class="campoObligatorio" id="campoObligatorio_nomcolectivo"	title="Campo obligatorio"> *</label>
			</td>
			<td class="literal">Enviar IBAN</td>
			<td>
				<form:select path="envioIbanAgro" cssClass="dato" cssStyle="width:176" id="envioIbanAgro" >
					<form:option value="">Todos</form:option>
					<form:option value="O">Dom. Agroseguro Obligatorio</form:option>
					<form:option value="S">Dom. Agroseguro Opcional</form:option>
					<form:option value="N">No domiciliar</form:option>
				</form:select>
				<label class="campoObligatorio" id="campoObligatorio_envioIbanAgro"	title="Campo obligatorio"> *</label>
			</td>
		
		<td rowspan="2" align="right">

				<fieldset style="width:150px;border: 1px solid;padding: 5px;text-align:left;">
					<legend class="literal">Descuento/Recargo</legend>	
					<table align="center" border="0">		
						<tr>
							<td class="literal">
								<form:radiobutton cssClass="literal" id="tipoDescRecarg" path="tipoDescRecarg" value="" onclick="onClicktipoDescRecarg()" disabled="true"/>Ninguno
							</td>
						</tr>
						<tr>
							<td class="literal"><form:radiobutton cssClass="literal" id="tipoDescRecarg" path="tipoDescRecarg" value="0" onclick="onClicktipoDescRecarg()" disabled="true"/>Descuento</td>
							<td rowspan="2" style="vertical-align:middle;">&nbsp;<form:input	path="pctDescRecarg" size="4" maxlength="6" cssClass="dato" id="pctDescRecarg" onchange="this.value = this.value.replace(',', '.')" />&nbsp;</td>	
						</tr>
						<tr>
							<td class="literal"><form:radiobutton cssClass="literal" id="tipoDescRecarg" path="tipoDescRecarg" value="1" onclick="onClicktipoDescRecarg()" disabled="true"/>Recargo</td>
						</tr>	
				</table>
				</fieldset>	
		</td> 
			
		</tr>
		<tr>
			<c:if test="${externo == 0}"> <!--  es interno -->
				<td class="literal" style="width:105px">Entidad mediadora</td>
				<td>
					<form:input	path="subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora', 'desc_subentmediadora');UTIL.subStrEntidad();" />
					<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('EntidadMediadora','principio', '', '');" alt="Buscar Entidad Mediadora" title="Buscar Entidad Mediadora" />
					<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio">*</label>
				</td>
				<td class="literal"> Subentidad mediadora</td>
				<td >
					<form:input	path="subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" onchange="javascript:lupas.limpiarCampos('desc_subentmediadora');" />
					<form:input path="subentidadMediadora.nomsubentidad" cssClass="dato"	id="desc_subentmediadora" size="20" readonly="true"/>
					<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
					<label class="campoObligatorio" id="campoObligatorio_subentmediadora" title="Campo obligatorio">*</label>
				</td>
			</c:if>
			<c:if test="${externo == 1}"> <!--  es externo -->
				<td class="literal" style="width:120px">Entidad mediadora</td>
					<td>
						<form:input	path="subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" readonly="true" />
						<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio">*</label>
					</td>
				<td class="literal"> Subentidad mediadora</td>
					<td >
						<form:input	path="subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" readonly="true" />
						<form:input path="subentidadMediadora.nomsubentidad" cssClass="dato"	id="desc_subentmediadora" size="20" readonly="true"/>
						<label class="campoObligatorio" id="campoObligatorio_subentmediadora" title="Campo obligatorio">*</label>
					</td>
			</c:if>
			<c:if test="${perfil == 0}">
				<td class="literal">Activo</td>
				<td>
					<form:select path="activo" cssClass="dato"	cssStyle="width:70" id="activo" >
						<!--Solo los usuarios de perfil 0 pueden poner el colectivo en activo-->
						<form:option value="">Todos</form:option>
						<form:option value="1">Si</form:option>
						<form:option value="0">No</form:option>
					</form:select>
				</td>
			</c:if>
			<c:if test="${perfil != 0}">
				<td class="literal">Activo:</td>
				<td class="detalI" style="width:20px"><span id="display_activo"></span></td>
				<form:hidden path="activo" id="activo" />
				
			</c:if>
						
		</tr>
	</table>
	</div>
	<div class="panel2 isrt" style="width:96%;padding: 8px;">
	<fieldset>
	<table width="100%">
		<colgroup>
			<col width="15%" />
			<col width="15%" />
			<col width="15%" />
			<col width="15%" />
			<col width="12%" />
			<col width="*" />
		</colgroup>
		<tr align="left">
			<td rowspan="2" class="literal">Forma de pago</td>
			<td class="literal">% Primer pago</td>
			<td class="literal">
				<form:input path="pctprimerpago" size="5" maxlength="3" onchange="calculaPctPagos();" cssClass="dato" id="pctprimerpago" onblur="validarElemento(document.getElementById('pctsegundopago'));"/>
				<label class="campoObligatorio" id="campoObligatorio_pctprimerpago"	title="Campo obligatorio"> *</label>
			</td>
			<td class="literal">Fecha primer pago</td>
			<td class="literal"  style="width:120px">
				
					<spring:bind path="fechaprimerpago">
					<input type="text" name="fechaprimerpago" id="fechaIni" size="11" maxlength="10" class="dato"  onblur="if (!ComprobarFecha(this, document.main, 'Fecha fin')) this.value='';validarElemento(this);"
																value="<fmt:formatDate pattern="dd/MM/yyyy" 
																value="${colectivoBean.fechaprimerpago}" />" />
					</spring:bind>
				 
				<input type="button" id="btn_fechaIni" name="btn_fechaIni" class="miniCalendario" style="cursor: pointer;" /> 
				<label	class="campoObligatorio" id="campoObligatorio_fechaIni"	title="Campo obligatorio"> *</label>
			</td>
		</tr>
		<tr align="left">
			<td class="literal">% Segundo pago</td>
			<td class="literal">
				<form:input path="pctsegundopago" size="5" readonly="true" maxlength="4" cssClass="dato" id="pctsegundopago"  onblur="validarElemento(document.getElementById('fechaFin'));" /> 
				<label	class="campoObligatorio" id="campoObligatorio_pctsegundopago" title="Campo obligatorio"> *</label>
			</td>
			<td class="literal">Fecha segundo pago</td>
			<td class="literal"  style="width:120px">
				
					<spring:bind path="fechasegundopago">
						<input type="text" name="fechasegundopago" id="fechaFin" size="11" maxlength="10" class="dato"  value="<fmt:formatDate pattern="dd/MM/yyyy" value="${colectivoBean.fechasegundopago}" />"
																					onblur="if (!ComprobarFecha(this, document.main, 'Fecha fin')) this.value='';	(this);" />
					</spring:bind>
				
				<input type="button" id="btn_fechaFin" name="btn_fechaFin" class="miniCalendario" style="cursor: pointer;" /> 
				<label	class="campoObligatorio" id="campoObligatorio_fechaFin"	title="Campo obligatorio"> *</label>
			</td>
		</tr>
	</table>
	</fieldset>
	</div>
	<div class="panel2 isrt" style="width:96%;padding: 8px;">
	<fieldset>
	<table width="100%">	
		
		<tr align="left">
			<td  class="literal">Fecha cambio</td>
			<td class="literal"  >
				
					<spring:bind path="fechacambio">
					<input type="text" name="fechacambio" id="fechaCambio" readonly="true" size="11" maxlength="10" class="dato" 
																value="<fmt:formatDate pattern="dd/MM/yyyy" 
																value="${colectivoBean.fechacambio}" />" />
					</spring:bind>				 
				
				<label	class="campoObligatorio" id="campoObligatorio_fechaIni"	title="Campo obligatorio"> *</label>
			</td>
			<td  class="literal">Fecha efecto</td>
			<td class="literal"  >
				
					<spring:bind path="fechaefecto">						
						<input type="text" name="fechaefecto" id="fechaEfecto" size="11" maxlength="10" class="dato" tabindex="17"
																value="<fmt:formatDate pattern="dd/MM/yyyy" 
																value="${colectivoBean.fechaefecto}" />" />
																					
					</spring:bind>
				
				<input type="button" id="btn_fechaEfecto" name="btn_fechaEfecto" class="miniCalendario" style="cursor: pointer;display: none;" /> 
				<label	class="campoObligatorio" id="campoObligatorio_fechaFin"	title="Campo obligatorio"> *</label>
			</td>
			<td class="literal">IBAN</td>
			<td class="literal" >
				<input type="hidden" name="ccc" id="ccc" value="${ccc}"/>
				
				<form:input path="iban" size="4" maxlength="4" cssClass="dato" onkeyup="autotab(this, document.main.cuenta1);" onchange="this.value=this.value.toUpperCase();unificarCcc();"/>
				<input type="text" id="cuenta1" name="cuenta1" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.main.cuenta2);" onchange="unificarCcc();"/>
				<input type="text" id="cuenta2" name="cuenta2" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.main.cuenta3);" onchange="unificarCcc();"/>
				<input type="text" id="cuenta3" name="cuenta3" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.main.cuenta4);" onchange="unificarCcc();"/>
				<input type="text" id="cuenta4" name="cuenta4" size="4" maxlength="4" class="dato"  onKeyup="autotab(this, document.main.cuenta5);" onchange="unificarCcc();"/>
				<input type="text" id="cuenta5" name="cuenta5" size="4" maxlength="4" class="dato"  onchange="unificarCcc();"/>
				<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_ccc"> *</label>
			</td>
		</tr>
		<tr align="left" >
			<td class="literal">Observaciones<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tx_observaciones"> *</label></td>
			<td colspan="5">
				<form:textarea id="tx_observaciones" path="observaciones" cols="140" rows="2" cssClass="dato" tabindex="22" onchange="this.value=this.value.toUpperCase();" onkeypress="return limita(200);"/>
			</td>
		</tr>
	</table>
	</fieldset>
	</div>
</form:form> <br />

<form name="historicoForm" id="historicoForm" action="historicoColectivo.html">
	<input type="hidden" id="idHistorico" name="idHistorico" />
</form>


<!-- Aqui tiene que ir el grid de datos -->
<div id="grid">
	<!-- Aqui tiene que ir el grid de datos --> 
	<display:table requestURI="" id="listaColectivos" class="LISTA" summary="colectivo" pagesize="${numReg}" 
			       name="${listaColectivos}" size="${totalListSize}" partialList="true" excludedParams="operacion, listaEntCRM, id" style="width:99%"
				   decorator="com.rsi.agp.core.decorators.ModelTableDecoratorColectivos">
				   
			
		<display:setProperty name="pagination.sort.param" value="sort"/>
		<display:setProperty name="pagination.sortdirection.param" value="dir"/>	
		
		<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="colectivoSelec" sortable="false"	style="width:90px;text-align:center" media="html" />
		<display:column class="literal" headerClass="cblistaImg" title="Ent." property="tomador.id.codentidad" sortable="true" />
		<display:column class="literal" headerClass="cblistaImg" title="Plan" property="linea.codplan" sortProperty="lin.codplan" sortable="true" />
		<display:column class="literal" headerClass="cblistaImg" title="L&iacute;nea" property="linea.codlinea" sortProperty="lin.codlinea" sortable="true" />
		<display:column class="literal" headerClass="cblistaImg" title="Ref. Col." property="colId"  sortProperty="idcolectivo" sortable="true" />
		<display:column class="literal" headerClass="cblistaImg" title="Nombre Colectivo" property="nomcolectivo" sortable="true" />
		<display:column class="literal" headerClass="cblistaImg" title="E-S Mediad." property="colEntMed" sortProperty="SM.id.codentidad" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorHojaNumero"/>
		<display:column class="literal" headerClass="cblistaImg" title="CIF Tomador" property="tomador.id.ciftomador" sortProperty="tom.id.ciftomador" sortable="true" />
		<display:column class="literal" headerClass="cblistaImg" title="Fec.Mod" property="colFechaCambio" sortProperty="fechacambio" sortable="true" />
		<display:column class="literal" headerClass="cblistaImg" title="Fec.Efecto" property="colFechaEfecto" sortProperty="fechaefecto" sortable="true" />
		<display:column class="literal" headerClass="cblistaImg" title="Fec.Baja" property="colFechaBaja" sortProperty="fechabaja" sortable="true" />
		<display:column class="literal" headerClass="cblistaImg" title="Activo" property="colectivoActivo" sortable="false"/>
		<display:column class="literal" headerClass="cblistaImg" title="Env&iacute;o IBAN" property="colEnvioIbanAgro" sortProperty="envioIbanAgro" sortable="true" />
		<%-- PTC. 78845 ** CAMPOS NUEVOS (28.02.2022) ** Inicio --%>
		<display:column class="literal" headerClass="cblistaImg" title="Estado Agroseguro" property="estadoAgroseguro" sortable="true"/>
		<display:column class="literal" headerClass="cblistaImg" title="Fecha Env&iacute;o" property="fechaEnvio" sortable="true"/>
		<%-- PTC. 78845 ** CAMPOS NUEVOS (28.02.2022) ** Fin --%>
	</display:table>
	<div style="width: 20%; margin:0 auto;">
		<a style="font-family: tahoma, verdana, arial; color: #626262; text-decoration: underline; font-size: 11px; cursor: hand; cursor: pointer;"
			id="btnImprimir" href="javascript:imprimir(${totalListSize })">Imprimir</a>
	</div>
</div>
	
</div>
<br />
<%@ include file="/jsp/common/static/piePagina.jsp"%>
<%@ include file="/jsp/common/static/overlay.jsp"%>

<!--               -->
<!-- POPUPS AVISO  -->
<!--               -->
	
<!-- *** popUp imprimir Informe Alta *** -->
		<div id="imprimirAlta" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; top: 165px; z-index:1000000008;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Imprimir Informe Alta Colectivo
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="cerrarPopUp()">x</span>
				        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
						<div id="panelInformacion" class="panelInformacion">
							�Desea imprimir el informe?
							<div id="txt_mensaje_imprimirAlta"></div>
						</div>
						<div style="margin-top:15px">
						  <a class="bot" id="btn_aceptarImprimirAlta" href="javascript:imprimirAltaOK()" title="imprimir">SI</a>
						    <a class="bot" href="javascript:cerrarPopUp()" title="No imprimir">NO</a>
						</div>
			 </div>
		</div>


<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTomador.jsp"%>
<%@ include file="/jsp/common/lupas/lupaEntidadMediadora.jsp"%>
<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
<%@ include file="/jsp/moduloAdministracion/colectivos/popupActivarColectivos.jsp"%>	
<!--  -->
<script>
muestraCuenta();
</script>
</body>
</html>