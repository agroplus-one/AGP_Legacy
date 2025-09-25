<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>

<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>

<html>
<head>
	<title>Mantenimiento de Subentidades Mediadoras</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<script type="text/javascript" src="jsp/js/util.js"></script>
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
    <script type="text/javascript" src="jsp/js/calendar.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	<script type="text/javascript" src="jsp/moduloAdministracion/subentidadesMediadoras/subentidadesMediadoras.js" ></script>
	<script type="text/javascript" src="jsp/js/iban.js" ></script>
	<%@ include file="/jsp/js/draggable.jsp"%>

<script type="text/javascript" charset="ISO-8859-1">

	$(document).ready(function(){
		<c:if test="${activarModoModificar == 'true'}">	
			$('#method').val("doEdita");
			$("#btnAlta").hide();
			$("#btnModificar").show();
			$("#entmediadora").attr("readonly",true);
			$("#subentmediadora").attr("readonly",true);	
		</c:if>		
	
	});	
	
	function volver(){
	
		<c:if test="${procedencia == 'incidenciasComisionesUnificadas'}">
			$(window.location).attr('href', 'incidenciasUnificado.run?rand=' + UTIL.getRand() + 
					'&idFicheroUnificado='+$('#idFichero').val()+	
					'&origenLlamada=subentidadesMediadoras'+
					'&method=doConsulta');
		</c:if>
		
		<c:if test="${procedencia == 'incidenciasComisiones'}">
			$(window.location).attr('href', 'incidencias.html?rand=' + UTIL.getRand() + 
				'&idFichero='+$('#idFichero').val()+
				'&tipo='+$('#tipoFichero').val()+
				'&codplan='+$('#plan').val()+
				'&method=doConsulta');
		</c:if>
	
	}
	
	
</script>	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cifnifseleccionado();generales.fijarFila();comprobarMostrarBotonCargoCuenta();javascript:generales.separaCuentaIBAN('${iban}');">

	<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<a class="bot" id="btnAlta" href="javascript:generales.uneCuentaIBAN();alta()">Alta</a> 
					<a class="bot" id="btnModificar" style="display: none" href="javascript:generales.uneCuentaIBAN();editar()">Modificar</a>
					<a class="bot" id="btnConsultar" href="javascript:generales.uneCuentaIBAN();consultar();">Consultar</a> 
					<a class="bot" id="btnLimpiar" href="javascript:limpiarFiltro()">Limpiar</a>					
					<c:if test="${procedencia == 'incidenciasComisiones' || procedencia=='incidenciasComisionesUnificadas'}">
						<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
					</c:if>	
				</td>
			</tr>
		</table>
	</div>
	
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Administración de Subentidades Mediadoras</p>
		<form:form name="main" id="main" action="subentidadMediadora.html" method="post" commandName="subentidadMediadoraBean">		
			<input type="hidden" id="method" name="method" />	
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value=""/>
			<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
			<input type="hidden" name="entidadMedSubstr" id="entidadMedSubstr" value="">
			<input type="hidden" name="idFichero" id="idFichero" value="${idFichero }"/>
			<input type="hidden" name="tipoFichero" id="tipoFichero" value="${tipoFichero}"/>
			<input type="hidden" name="fechab.day" value="">
			<input type="hidden" name="fechab.month" value="">
			<input type="hidden" name="fechab.year" value="">
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<div id="panelInfo_adicional" style="width:800px;height:20px;color:black;border:1px solid #FFCD00;display:none;
				font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FCF6CF">
			</div>
			
			<div class="panel2 isrt" style="margin-left:auto;margin-right:auto;">
				<table width="100%">		
					<tr>
						<td class="literal"  style="width:120px">Entidad</td>
						<td colspan="2">
							<form:input path="entidad.codentidad" size="4" maxlength="4" cssClass="dato" id="entidad" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_entidad');" />
							<form:input path="entidad.nomentidad" cssClass="dato" id="desc_entidad" size="40" readonly="true" />
							<span id="lupaEntidad"><img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" /></span>
							<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio"> *</label>
						</td>
					</tr>
					<tr>	
						<td class="literal">Entidad mediadora</td>
						<td>
							<form:input	path="id.codentidad" size="4" maxlength="4"	cssClass="dato" tabindex="2" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora', 'desc_subentmediadora');UTIL.subStrEntidad();" />
							<span id="lupaEntidadMediadora"><img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('EntidadMediadora','principio', '', '');" alt="Buscar Entidad Mediadora" title="Buscar Entidad Mediadora" /></span>
							<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio">*</label>
						</td>
						<td class="literal" align="right"> Subentidad mediadora</td>
						<td align="right">
							<form:input	path="id.codsubentidad" size="4" maxlength="4" cssClass="dato" tabindex="3" id="subentmediadora" />
							<span id="lupaSubEntidad"><img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" /></span>
							<label class="campoObligatorio" id="campoObligatorio_subentmediadora" title="Campo obligatorio">*</label>
						</td>
						<td class="literal" align="right">Tipo mediador</td>
						<td align="center">
							<form:input path="" cssClass="dato"	id="desc_tipomediador" size="40" readonly="true" />
						</td>
					</tr>		
				</table>
			</div>
			
			<div class="panel2 isrt" style="margin:0 auto;">
				<table width="100%" border ="2">
					<tr>
						<td class="literal">Tipo ident.</td>
						<td class="literal">
							<form:select path="tipoidentificacion" cssClass="dato" id="tipoIdentificacion" tabindex="4" cssStyle="width:70"  onchange="cifnifseleccionado();">
								<form:option value="">Todos</form:option>
								<form:option value="CIF">CIF</form:option>
								<form:option value="NIF">NIF</form:option>
								<form:option value="NIE">NIE</form:option>
							</form:select>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_tipoIdentificacion"> *</label>
						</td>
						<td></td>
						<td class="literal" align="right">CIF/NIF/NIE</td>
						<td class="literal" align="left" style="padding:0.0em 1em">
							<form:input path="nifcif" size="10" maxlength="9" cssClass="dato" id="nifcif"  tabindex="5" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_nifcif"> *</label>
						</td>		
						<td class="literal"  align="right" colspan="2">Pago directo a mediador</td>
						<td class="literal">
							<form:checkbox path="pagodirecto" id="pagodirecto" value="1" tabindex="6"/>						
						</td>						
					</tr>
					<tr align="left">
						<td class="literal">Nombre</td>
						<td class="literal">
							<form:input path="nombre" size="20"	maxlength="20" cssClass="dato" id="nombre" onchange="this.value=this.value.toUpperCase();" tabindex="7"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_nombre"> *</label>
						</td>
						<td class="literal" colspan="5" align="right">Permitir cargo en cuenta</td>
						<td class="literal" >
							<form:select path="cargoCuenta" cssClass="dato"	cssStyle="width:70" id="cargoCuenta" tabindex="8">
										<form:option value="">Todos</form:option>
										<form:option value="0">NO</form:option>
										<form:option value="1">SI</form:option>
							</form:select>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_cargoCuenta"> *</label>
							&nbsp;
							<a class="bot" style="display:none" id="btnEntidadesCargoCuenta" href="javascript:showPopUpEntidadesCargoCuenta()"><img alt="Entidades cargo cuenta" src="jsp/img/boton-rsi.png"></a> 
						</td>
						
					</tr>
					<tr align="left">
						<input type="hidden" id="iban" name="iban" size="32" maxlength="32" class="dato" />
						<td class="literal">1º Apellido</td>
						<td  class="literal">
							<form:input path="apellido1" size="40" maxlength="40" cssClass="dato" id="apellido1"  onchange="this.value=this.value.toUpperCase();" tabindex="9"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_apellido1"> *</label>
						</td>
						<td class="literal" colspan="2" align="center">2º Apellido</td>
						<td class="literal" style="padding:0.0em 1em">
							<form:input path="apellido2" size="38" maxlength="40" cssClass="dato" id="apellido2" onchange="this.value=this.value.toUpperCase();" tabindex="10"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_apellido2"> *</label>
						</td>
						<td class="literal" align="right" colspan="2">Forzar revisión A.M. y R.C.</td>
						<td class="literal" align="left">
							<form:select path="forzarRevisionAM" cssClass="dato" cssStyle="width:70" id="forzarRevisionAM" tabindex="11">
								<form:option value="">Todos</form:option>
								<form:option value="0">NO</form:option>
								<form:option value="1">SI</form:option>
							</form:select>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_forzarRevisionAM"> *</label>
						</td>
					</tr>
					<tr align="left"> 
						<td class="literal" >Razón social</td>
						<td class="literal" colspan="2">
							<form:input path="nomsubentidad" size="50" maxlength="40" cssClass="dato" id="razonsocial"  onchange="this.value=this.value.toUpperCase();" tabindex="12"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_razonsocial"> *</label>
						</td>
						<td class="literal" align="center">Cod. postal</td>
						<td class="literal" colspan="2" style="padding:0.0em 1em">
							<form:input path="codpostal" size="5" maxlength="5" cssClass="dato" id="cp"  tabindex="13"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_cp"> *</label>
						</td>
						<td class="literal" align="right">Calcular RC Ganado</td>
						<td class="literal" colspan="4">
							<form:select path="calcularRcGanado" cssClass="dato" cssStyle="width:70" id="calcularRcGanado" tabindex="14">
								<form:option value="">Todos</form:option>
								<form:option value="0">NO</form:option>
								<form:option value="1">SI</form:option>
							</form:select>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_calcularRcGanado"> *</label>
						</td>
					</tr>
					<tr align="left"> 
						<td class="literal">IBAN</td>
						<td class="literal" colspan="2">				    
							<input path="text" id="cuenta1" name="cuenta1" size="4" maxlength="4" class="dato" tabindex="15" onkeyup="autotab(this, document.main.cuenta2);" onchange="this.value=this.value.toUpperCase();"/>
							<input type="text" id="cuenta2" name="cuenta2" size="4" maxlength="4" class="dato" tabindex="16" onKeyup="autotab(this, document.main.cuenta3);"/>
							<input type="text" id="cuenta3" name="cuenta3" size="4" maxlength="4" class="dato" tabindex="17" onKeyup="autotab(this, document.main.cuenta4);"/>
							<input type="text" id="cuenta4" name="cuenta4" size="4" maxlength="4" class="dato" tabindex="18" onKeyup="autotab(this, document.main.cuenta5);"/>
							<input type="text" id="cuenta5" name="cuenta5" size="4" maxlength="4" class="dato" tabindex="19" onKeyup="autotab(this, document.main.cuenta6);"/>
							<input type="text" id="cuenta6" name="cuenta6" size="4" maxlength="4" class="dato" tabindex="20"/>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_cuenta"> *</label>
						</td>
						<td class="literal" align="center">Fecha Baja</td>
                        <td class="literal" colspan="2" style="padding:0.0em 1em">
		                    <spring:bind path="fechabaja">
		                    	 <input type="text" name="fechabaja" id="fechabaja" size="11" maxlength="10" class="dato" tabindex="21"
		                    	 		value="<fmt:formatDate pattern="dd/MM/yyyy" value="${subentidadMediadoraBean.fechabaja}" />" />
		                    </spring:bind>
	                    	 <input type="button" id="btn_fechabaja" name="btn_fechabaja" class="miniCalendario" style="cursor: pointer;" /> 
                        </td>
                        <td class="literal" align="right">SW Confirmación</td>
						<td class="literal" colspan="4">
						    <form:select path="swConfirmacion" cssClass="dato" cssStyle="width:70" id="swConfirmacion" tabindex="14">
								<form:option value="">Todos</form:option>
								<form:option value="0">NO</form:option>
								<form:option value="1">SI</form:option>
							</form:select>
						
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_swConfirmacion"> *</label>
						</td>
					</tr>
					<tr>
					
						<td class="literal" align="right">Email</td>
						<td colspan="2">
							<form:input id="email" path="email" cssClass="dato" tabindex="15" size="35" maxlength="50"/>
							
						</td>
											
						<td class="literal" align="right">Email 2</td>
						<td style="padding:0.0em 1em" class="literal" colspan="2">
							<form:input id="email2" path="email2" cssClass="dato" maxlength="50" tabindex="16" size="35"/>
						</td>
						<td class="literal" align="right">Con gastos de adquisición</td>
						<td class="literal" colspan="4">
						    <form:select path="indGastosAdq" cssClass="dato" cssStyle="width:70" id="indGastosAdq" tabindex="17">
								<form:option value="">Todos</form:option>
								<form:option value="0">NO</form:option>
								<form:option value="1">SI</form:option>
							</form:select>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_indGastosAdq"> *</label>
							
						</td>
					</tr>
					<tr>
					
						<td class="literal" align="right"></td>
						<td colspan="2">
							
							
						</td>
											
						<td class="literal" align="right"></td>
						<td style="padding:0.0em 1em" class="literal" colspan="2">
						</td>
						<td class="literal" align="right">Firma en tableta</td>
						<td class="literal" colspan="4">
						    <form:select path="firmaTableta" cssClass="dato" cssStyle="width:70" id="firmaTableta" tabindex="18">
								<form:option value="">Todos</form:option>
								<form:option value="0">NO</form:option>
								<form:option value="1">SI</form:option>
							</form:select>
							<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_firmaTableta"> *</label>
							
						</td>
					</tr>
				</table>
			</div>

		</form:form>
		<form name="revisarForm" id="revisarForm" action="incidencias.html">
			<input type="hidden" name="method" id="methodVolver"/>
			<input type="hidden" id="revisar_idFichero" name="idFichero" /> 
			<input	type="hidden" id="revisar_tipoFichero" name="tipo" /> 
		</form>
		<div id="grid">
			<display:table requestURI=""  id="listaSubentidades" class="LISTA" summary="subentidadMediadora" 
							sort="list" pagesize="${numReg}" 
							name="${listSubentidadMediadora}" 
							decorator="com.rsi.agp.core.decorators.ModelTableDecoratorSubentidadesMediadoras" 
							excludedParams="method" style="width:85%;border-collapse: collapse">
				<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="subentSelec" sortable="false" style="width:40px;text-align:center"/>
				<display:column class="literal" headerClass="cblistaImg" title="Entidad" property="entidad" sortable="true" style="width:50px;text-align:center"/>
				<display:column class="literal" headerClass="cblistaImg" title="E-S Mediadora" property="subentEntMed" sortable="true" style="width:60px;text-align:center" comparator="com.rsi.agp.core.comparators.TableComparatorEntSubMed"/>
				<display:column class="literal" headerClass="cblistaImg" title="Nombre/Razón" property="subentNombreRazonSocial" sortable="true" style="width:150px;"/>            
				<display:column class="literal" headerClass="cblistaImg" title="Tipo" property="subentTipoMediador" sortable="true" style="width:150px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Cargo en Cuenta" property="cargoCuenta" sortable="true" style="width:80px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Adquisición" property="indGastosAdq" sortable="true" style="width:100px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Fecha Baja" property="fechaBaja" sortable="true" style="width:80px;" format="{0,date,dd/MM/yyyy}"/>
				<display:column class="literal" headerClass="cblistaImg" title="Con gastos de adquisición" property="indGastosAdq" sortable="true" style="width:80px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Firma en tableta" property="firmaTableta" sortable="true" style="width:80px;"/>
			</display:table>	
		</div>
</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/static/overlayESMediadoras.jsp"%>
	
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidadMediadora.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidadCargoCuenta.jsp"%>
	
	<!-- Pop-up EntidadesCargoCuenta -->
	<div id="divEntidadesCargoCuenta" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px; width:800px; left:20%; z-index:11">
		<!--  header popup -->
		<div id="header-popup" style="padding:0.4em 1em;position:relative;
		                              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
		                              background:#525583;height:15px">
			<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Entidades para cargo en cuenta</div>
			<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				      font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				<span onclick="cerrarPopUpEntidadesCargoCuenta()">x</span>
			</a>
		</div>

		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelAlertas_cargoCuenta" style="width:100%;height:20px;color:black;border:1px solid #DD3C10;display:block;
				font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8">
			</div>
			<div id="panelInformacion" class="panelInformacion">
				
				<table>
					<tr>
						<td colspan="2">&nbsp;</td>
						<td rowspan="2" width="15">&nbsp;</td>
						<td rowspan="2" valign="bottom" align="center">
							<span id="agregar"><img src="jsp/img/add.png" style="cursor:hand;margin:3px;" onclick="addEntidadCargoCuenta();" alt="Añadir Entidad" title="Añadir Entidad" /></span>
							<br/>
							<span id="eliminar"><img src="jsp/img/displaytag/delete.png" style="cursor:hand;margin:3px;" onclick="deleteEntidadCargoCuenta();" alt="Borrar Entidad" title="Borrar Entidad" /></span>
						</td>
						<td rowspan="2">
							<select multiple="multiple" id="listaEntidadesCargoCuenta" size="3" class="dato" style="width:300px">
							</select>
						</td>
					</tr>
			        <tr>
			            <td class="literal">Entidad</td>
			            <td>
							<input id="entidadCargoCuenta" size="4" maxlength="4" class="dato" onchange="javascript:lupas.limpiarCampos('entidadCargoCuenta_desc');" />
							<input id="entidadCargoCuenta_desc" class="dato" size="40" readonly="readonly"/>
							<span id="lupaEntidadCargoCuenta"><img src="jsp/img/magnifier.png"style="cursor:hand;" onclick="javascript:lupas.muestraTabla('EntidadCargoCuenta','principio', '', '');javascript:cambiaZIndexOverlay();" alt="Buscar Entidad" title="Buscar Entidad"/></span>
							<label class="campoObligatorio" id="campoObligatorio_entidadCargoCuenta" title="Campo obligatorio"> *</label>
						</td>
			        </tr>
	   			</table>

				<div style="margin-top:15px;clear: both">
					<a class="bot" href="javascript:botonAceptarEntidadesCargoCuenta()">Aceptar</a>
				    <a class="bot" href="javascript:cerrarPopUpEntidadesCargoCuenta()">Cancelar</a>
				</div>
			</div>
		</div>
	</div>
	
</body>
</html>