<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<fmt:bundle basename="displaytag" >
	<c:set var="numReg">
		<fmt:message key="numElementsPag"/>
	</c:set>
</fmt:bundle>
<html>
<head>
	<title>Parametros Generales de Comisiones por Cultivo</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/commonAjax.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
	<script type="text/javascript" src="jsp/js/displayTagAjaxSelectedRow.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>	
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>    
    <script type="text/javascript" src="jsp/js/calendar.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
     <script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
    <script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
    <script type="text/javascript" src="jsp/moduloComisiones/parametrosComisionesCultivos.js"></script>
    <script type="text/javascript" src="jsp/moduloComisiones/parametrosComisionesCM.js" ></script>
   
    <script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
    
    <%@ include file="/jsp/js/draggable.jsp"%>
        
	<!-- <script type="text/javascript" charset="ISO-8859-1">
		
		
	</script> -->
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub6', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="left">
							&nbsp;<a class="bot" id="btnCambioMasivo" href="javascript:cambioMasivo();">Cambio Masivo</a>
								&nbsp;<a class="bot" id="btnCambioMasivo" href="javascript:showReplicarCultivos();">Replicar plan/linea</a>
				</td>
			
				<td align="right">			
					<a class="bot" id="btnAlta"  href="javascript:alta();">Alta</a>				
					<a class="bot" id="btnModif"  href="javascript:editar();" style="display:none">Modificar</a>				
					<a class="bot" id="btnConsultar"  href="javascript:consultar();">Consultar</a>				
					<a class="bot" id="btnLimpiar"  href="javascript:limpiar();">Limpiar</a>	
					<c:if test="${tipoFichero!= null && idFichero!= null && procedencia eq 'incidenciasComisionesUnificadas'}" >					
						<a class="bot" id="btnVolver"  href="javascript:returnBack();">Volver</a>
					</c:if>						
				</td>
			</tr>
		</table>
	</div>
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Parametros Generales de Comisiones</p>
		<form:form id="main3" name="main3" method="post" action="comisionesCultivos.html" commandName="CultivosEntidadesBean">
			<input type="hidden" name="method" id="method"/>
			<input type="hidden" name="primeraConsulta" id="primeraConsulta" value="true"/>
			<form:hidden path="id" id="id"/>
			<form:hidden path="usuario.codusuario" id="usuario"/>
			<form:hidden path="grupoNegocio.descripcion" id="gnDesc"/>
			<input type="hidden" name="idFichero" id="idFichero" value="${idFichero }"/>
			<input type="hidden" name="tipoFichero" id="tipoFichero" value="${tipoFichero }"/>
			<input type="hidden" name="limpiarFiltro" id="limpiarFiltro"/>
			<input type="hidden" name="idsRowsChecked" id="idsRowsChecked" value="${idsRowsChecked}"/>
			<input type="hidden" name="checkTodo" id="checkTodo" value="${checkTodo}"/>
			<input type="hidden" name="polizasString" id="polizasString" value="${polizasString}"/>
			<input type="hidden" name="tx_fechaEfecto.day" value="">
			<input type="hidden" name="tx_fechaEfecto.month" value="">
			<input type="hidden" name="tx_fechaEfecto.year" value="">
			<input type="hidden" name="procedencia"  id="procedencia" value="${procedencia}"/>
			<input type="hidden" name="consultando"  id="consultando" value="${consultando}"/>
			<input type="hidden" name="entidad"  id="entidad" value=""/>
			<input type="hidden" name="activarModoModificar"  id="activarModoModificar" value="${activarModoModificar}"/>
			<input type="hidden" name="limpiarReplicar"  id="limpiarReplicar" value="${limpiarReplicar}"/>
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			
			<div style="panel2 isrt">
				<fieldset align="center">
					<legend class="literal">Filtro</legend>
					<table  align="center">
						<tr>
							<td class="literal">Plan</td>
							<td class="literal" width="80px">
								<form:input path="linea.codplan" id="plan" size="5" maxlength="4" cssClass="dato"/>
								<label class="campoObligatorio" id="campoObligatorio_plan" title="Campo obligatorio">*</label>
							</td>
							<td class="literal">Línea</td>
							<td class="literal" width="20px">
								<form:input path="linea.codlinea" id="linea" size="5" maxlength="3" cssClass="dato" onchange=";borrarNombreLinea();"/>
							</td>
							<td>	
								<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
								<label class="campoObligatorio"	id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
							</td>
							
							<td class="literal">E-S Med</td>
							<td>
								<form:input	path="subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
								<form:input	path="subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" />
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />																
							</td>
						</tr>						
					</table>
					<table  align="center">
						<tr>
							<td class="literal">Grupo Negocio</td>
							<td>
								<form:select path="grupoNegocio.grupoNegocio" id="grupoNegocio" cssClass="dato" cssStyle="width:80px">								
									<form:option value=""></form:option>
									<c:forEach items="${listGruposNegocio}" var="grupoNg">
										<form:option value="${grupoNg.grupoNegocio}">${grupoNg.descripcion}</form:option>
									</c:forEach>															
								</form:select>
						
								<label class="campoObligatorio" id="campoObligatorio_grupoNegocio" title="Campo obligatorio">*</label>	
							</td>
							<td class="literal">% Comisión máximo</td>
							<td class="literal" width="80px">
								<form:input path="pctgeneralentidad" id="pctgeneralentidad" size="6" maxlength="6" cssClass="dato" onchange="this.value = this.value.replace(',', '.');"/>
								<label class="campoObligatorio" id="campoObligatorio_pctgeneralentidad" title="Campo obligatorio">*</label>
							</td>
							<td class="literal">% Administración</td>
							<td class="literal" width="80px">
								<form:input path="pctadministracion" id="pctadministracion" size="6" maxlength="6" cssClass="dato" readonly="false" onchange="this.value = this.value.replace(',', '.')"/>
								<label class="campoObligatorio"	id="campoObligatorio_pctadministracion" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">% Adquisición</td>
							<td class="literal" width="80px">
								<form:input path="pctadquisicion" id="pctadquisicion" size="6" maxlength="6" cssClass="dato" readonly="false" onchange="this.value = this.value.replace(',', '.')"/>
								<label class="campoObligatorio"	id="campoObligatorio_pctadquisicion" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">Fecha efecto</td>
							<td>
								<spring:bind path="fechaEfecto">
									<input type="text" name="fechaEfecto"  id="tx_fechaEfecto" size="11" maxlength="10" class="dato"
										onchange="if (!ComprobarFecha(this, document.main3, 'Fecha efecto')) this.value='';"
										value="<fmt:formatDate pattern="dd/MM/yyyy" value="${CultivosEntidadesBean.fechaEfecto}"/>"/>									
								</spring:bind>
								<input type="button" id="btn_fechaEfecto" name="btn_fechaEfecto" class="miniCalendario" style="cursor: pointer;"/>
								<label class="campoObligatorio"	id="campoObligatorio_tx_fechaEfecto" title="Campo obligatorio"> *</label>
							</td>	
						</tr>
					</table>
				</fieldset>
			</div>			
		</form:form>
		
		<form name="historicoForm" id="historicoForm" action="historicoComCultivos.html">
			<input type="hidden" id="idHistorico" name="id"/>
			<input type="hidden" id="planF" name="planF"/>
			<input type="hidden" id="lineaF" name="lineaF"/>
			<input type="hidden" id="desc_lineaF" name="desc_lineaF"/>
			<input type="hidden" id="pctgeneralentidadF" name="pctgeneralentidadF"/>
			<input type="hidden" id="pctadministracionF" name="pctadministracionF"/>
			<input type="hidden" id="pctadquisicionF" name="pctadquisicionF"/>	
			<input type="hidden" id="fechaEfectoF" name="fechaEfectoF"/>
			<input type="hidden" id="grupoNegHis" name="grupoNegHis"/>
			<input type="hidden" id="entmediadoraHis" name="entmediadoraHis"/>
			<input type="hidden" id="subentmediadoraHis" name="subentmediadoraHis"/>
		</form>
		
		<div id="grid" align="center">
			<display:table requestURI="" class="LISTA" summary="comisiones" defaultorder="descending"
					pagesize="${numReg}" name="${listCE}" id="Pcomisiones"
					decorator="com.rsi.agp.core.decorators.ModelTableDecoratorComisionesCultivosEntidades" sort="list"
					style="width:90%;border-collapse:collapse;"  >
					
						
					<c:if test="${!altaLinea999}">
					<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" 	title="Acciones"			property="admActions"    style="width:80px;text-align:center" sortable="false"/>
					</c:if>
					
					<display:column class="literal" headerClass="cblistaImg" title="Plan"   				property="codPlan"              sortProperty="linea.codplan" style="width:50px;text-align:center" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Línea"            		property="codLinea"  		     sortProperty="linea.codlinea" style="width:50px;text-align:center" sortable="true"/>
					<%-- <display:column class="literal" headerClass="cblistaImg" title="E-S Med" property="entSubEnt" sortProperty="esMed.id.codentidad" sortable="true" style="width:80px;text-align:center"/> --%>
					<%-- <display:column class="literal" headerClass="cblistaImg" title="E-S Med."            	property="entSubEntMed"  	sortProperty="subentidadMediadora.id.codentidad" sortable="true" style="width:80px;text-align:center"/> --%>
					<display:column class="literal" headerClass="cblistaImg" title="E-S Med."            	property="entSubEntMed"  	sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorComisionesEntSubMed" style="width:80px;text-align:center"/>
					<display:column class="literal" headerClass="cblistaImg" title="G.N."            		property="grupoNegocio"  	sortable="true" style="width:50px;text-align:center" comparator="com.rsi.agp.core.comparators.TableComparatorGrupoNegocio"/>
					<display:column class="literal" headerClass="cblistaImg" title="% Comisión máximo"  	property="pctgeneralentidad" style="text-align:right" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
					<display:column class="literal" headerClass="cblistaImg" title="% Administración"       property="pctadministracion" style="text-align:right" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
					<display:column class="literal" headerClass="cblistaImg" title="% Adquisición"          property="pctadquisicion"    style="text-align:right" sortable="true" comparator="com.rsi.agp.core.comparators.TableComparatorPorcentajes"/>
					<display:column class="literal" headerClass="cblistaImg" title="Fec. Efecto"            property="fechaEfecto" 		 style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
					<display:column class="literal" headerClass="cblistaImg" title="Fec. Baja"            	property="fechaBaja" 		 style="text-align:center" sortable="true" format="{0,date,dd/MM/yyyy}"/>
					
			<display:footer>
				<form:form name="frmcheck" id="frmcheck">
					<tr style="background-color: #e5e5e5">
						<td class="literal" colspan="19" style="text-align: left " >
							<span style="width:30px;">&nbsp;</span><input type="checkbox" id="checkTodo" name="checkTodo" 
							class="dato" onclick="this.checked  ? marcar_todos() : desmarcar_todos() " />Marcar Todos</td>
						<td class="literal" colspan="17"></td>
					</tr>
				</form:form>
			</display:footer>
			
			</display:table>
		</div>
		
		<div class="imprimirDisplayTag" id="divImprimir">
			<a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Imprimir</a>	
			<a id="btnExportarExcel" style="text-decoration:none;" href="javascript:exportarExcel()">
				<img src="jsp/img/jmesa/excel.gif"/>
			</a>	
		</div>
		
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>
	<%@ include file="/jsp/common/static/overlayBaja.jsp"%>
	
	<!-- panel avisos -->
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
					<div id="txt_info" style="width: 70%" >Por cada plan,debe introducir una distribución para la línea genérica 999.</div>
					<div id="txt_info_none" style="width: 70%;display:none" >No hay registros de parámetros generales seleccionados</div>
				</div>
		</div>
	</div>
	
	
	
	<!-- PANEL CAMBIO MASIVO COMISIONES -->
		<!-- ***************************-->
		<div id="panelCambioMasivoUsuarios" class="panelCambioMasivo" style="left: 18%; width: 68%; color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
			    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Cambio masivo</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					  <span onclick="cerrarCambioMasivoUsuarios()">x</span>
				</a>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content">
				<div id="panelInformacionCM" class="">
					<div id="txt_mensaje_cm" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:20px;"></div>								
						<form:form name="main" id="main" method="post" action="comisionesCultivos.html" commandName="CultivosEntidadesBean">					
							<input type="hidden" name="method" id="method" value="doCambioMasivo"/>
							<input type="hidden" name="listaIdsMarcados_cm" id="listaIdsMarcados_cm" value=""/>	
							
							<input type="hidden" name="planFiltro" id="planFiltro" size="5" maxlength="4"class="dato"/>
							<input type="hidden" name="lineaFiltro" id="lineaFiltro" size="5" maxlength="3" class="dato"/>
							<input type="hidden" name="desc_lineaFiltro" id="desc_lineaFiltro" size="5" maxlength="3" class="dato"/>						
							<input type="hidden" name="pctComFiltro" id="pctComFiltro" size="5" maxlength="3" class="dato"/>
							<input type="hidden" name="pctAdqFiltro" id="pctAdqFiltro" size="5" maxlength="3" class="dato"/>
							<input type="hidden" name="pctAdmFiltro" id="pctAdmFiltro" size="5" maxlength="3" class="dato"/>
							<input type="hidden" name="fecEfectoFiltro" id="fecEfectoFiltro" size="8" maxlength="8" class="dato"/>	
							<input type="hidden" name="entmediadoraFiltro" id="entmediadoraFiltro" size="4" maxlength="4"class="dato"/>
							<input type="hidden" name="subentmediadoraFiltro" id="subentmediadoraFiltro" size="4" maxlength="4"class="dato"/>
							<input type="hidden" name="grupoNegocioFiltro" id="grupoNegocioFiltro" size="2" maxlength="2"class="dato"/>
							
													
					  		<form:hidden path="pctgeneralentidad" id="pctgeneralentidadCM_sel" />
					  		<form:hidden path="pctadministracion" id="pctadministracionCM_sel" />
					  		<form:hidden path="pctadquisicion"    id="pctadquisicionCM_sel" />
					  		<form:hidden path="fechaEfecto"    id="fechaEfectoCM_sel" />
					  		
							<div class="panel2 isrt" style="width:95%">
							
							
							<fieldset style="border:0px" align="center">
			    				<div align="center" id="panelAlertasValidacion_cm" name="panelAlertasValidacion_cm" class="errorForm_cm"></div>
							</fieldset>
							
							<fieldset>
							<table style="border:2px" align="center" style="width: 100%">							
								<tr>
									<td class="literal">% Comisión máximo</td>
									<td class="literal" width="80px">
										<input type="text" id="pctgeneralentidadCM" name ="pctgeneralentidadCM" size="6" maxlength="6" class="dato" onchange="limpiarPaneles();"/>
										<label class="campoObligatorio" id="campoObligatorio_pctgeneralentidadCM" title="Campo obligatorio">*</label>
									</td>
									
									<td class="literal">% Administración</td>
									<td class="literal" width="80px">
										<input type="text"  id="pctadministracionCM" name="pctadministracionCM" size="6" maxlength="6" class="dato" onchange="limpiarPaneles();"/>
										<label class="campoObligatorio" id="campoObligatorio_pctadministracionCM" title="Campo obligatorio">*</label>
									</td>
									<td class="literal">% Adquisición</td>
									<td class="literal" width="80px">
										<input type="text" id="pctadquisicionCM" name="pctadquisicionCM" size="6" maxlength="6" class="dato" onchange="limpiarPaneles();"/>
										<label class="campoObligatorio" id="campoObligatorio_pctadquisicionCM" title="Campo obligatorio">*</label>
									</td>
									
									<td class="literal">Fecha efecto</td>
									<td>
										<spring:bind path="fechaEfecto">
											<input type="text" name=fechaEfectoCM  id="tx_fechaEfectoCM" size="11" maxlength="10" class="dato"
												value="<fmt:formatDate pattern="dd/MM/yyyy" value="${CultivosEntidadesBean.fechaEfecto}"/>"/>
										</spring:bind>
										<input type="button" id="btn_fechaEfectoCM" name="btn_fechaEfectoCM" class="miniCalendario" style="cursor: pointer;"/>
										<label class="campoObligatorio" id="campoObligatorio_tx_fechaEfectoCM" title="Campo obligatorio">*</label>
									</td>
								</tr>
							</table>
							</fieldset>
							</div>
				</form:form>
		
				</div>
				<div style="margin-top:15px">
						    <a class="bot" href="javascript:limpiarCambioMasivoUsuarios()" title="Limpiar">Limpiar</a>
						    <a class="bot" href="javascript:cerrarCambioMasivoUsuarios()"  title="Cancelar">Cancelar</a>
						    <a class="bot" href="javascript:aplicarCambioMasivoUsuarios()" title="Aplicar">Aplicar</a>
				</div>
			</div>
		</div>
							
		<!-- PANEL BAJA -->
		<!-- ***************************-->
		<div id="panelBajaComisiones" class="panelCambioMasivo" style="left: 35%; width: 32%; height:5%; color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">

		     <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
			    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Eliminar registro</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					  <span onclick="cerrarBaja()">x</span>
				</a>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content" style="height:3%;" >
				<div id="panelBaja" class="panelInformacion">
					<div id="txt_mensaje_baja" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:20px;"></div>								
						<form:form name="mainBaja" id="mainBaja" method="post" action="comisionesCultivos.html" commandName="CultivosEntidadesBean">					
							<input type="hidden" name="method" id="method" value="doBorrarParametrosComisiones"/>
							<input type="hidden" name="idBaja" id="idBaja" value=""/>
							<input type="hidden" name="fFec" id="fFec" value=""/>
							<!-- form:hidden path="fechaEfecto" id="fFec" / -->									
							<div class="panel2 isrt" style="width:95%">
							<fieldset style="border:0px" align="center">
			    				<div align="center" id="panelAlertasValidacion_BJ" name="panelAlertasValidacion_BJ" class="errorForm_cm"></div>
							</fieldset>
							<fieldset>
							<table style="border:2px" align="center" style="width: 100%">
								<tr align="center">
									<td class="literal" >¿Está seguro que desea eliminar el registro seleccionado?</td>
									</td>
								</tr>
							</table>	
							<table style="border:2px" align="center" style="width: 100%">									
								<tr align="center">								
									<td class="literal">Fecha efecto  
										<spring:bind path="fechaEfecto">
											<input type="text" name=fechaEfectoBaja  id="tx_fechaEfectoBaja" size="11" maxlength="10" class="dato"
												value="<fmt:formatDate pattern="dd/MM/yyyy" value="${CultivosEntidadesBean.fechaEfecto}"/>"/>
										</spring:bind>
										<label class="campoObligatorio" id="campoObligatorio_fechaEfectoBaja" title="Campo obligatorio">*</label>
										<input type="button" id="btn_fechaEfectoBaja" name="btn_fechaEfectoBaja" class="miniCalendario" style="cursor: pointer;"/>
									</td>
								</tr>
							</table>
							</fieldset>
							</div>
						</form:form>
				</div>
				<div style="height:3%;">
						    <a class="bot" href="javascript:cerrarBaja()"  title="Cancelar">Cancelar</a>
						    <a class="bot" href="javascript:aplicarBajaUnica()" title="Aplicar">Aplicar</a>
				</div>
			</div>
		</div>
						
	
	<!-- panel replicacion -->
	<div id="divReplicar" class="wrapper_popup" style="left: 25%; width: 55%; color:#333333; z-index: 1005; -moz-border-radius: 4px 4px 4px 4px; padding: 0.9em;">
		 <!--  header popup -->
		<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px" >Replicar comisiones cultivos</div>
			<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				  <span onclick="javascript:cerrarPopUpReplica()">x</span>
			</a>
		</div>
		<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion" >
				<div id="txt_mensaje_re" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:40px;"></div>
				<form action="comisionesCultivos.html" id="replicarCultivos" name="replicarCultivos" method="post"  commandName="CultivosEntidadesBean">
					<input type="hidden" name="method" id="methodReplicar" value="doReplicarCultivo"/>		
									
									
									
									
					<!-- STORE BEAN DATA -->
									
		
					<input name="linea.codplan" value="${CultivosEntidadesBean.linea.codplan}" type="hidden"/>
					<input name="linea.codlinea" value="${CultivosEntidadesBean.linea.codlinea}" type="hidden"/>
					<input name="linea.nomlinea" value="${CultivosEntidadesBean.linea.nomlinea}" type="hidden"/>
					<input name="subentidadMediadora.id.codentidad" value="${CultivosEntidadesBean.subentidadMediadora.id.codentidad}" type="hidden"/>
					<input name="subentidadMediadora.id.codsubentidad" value="${CultivosEntidadesBean.subentidadMediadora.id.codsubentidad}" type="hidden"/>
					<input name="grupoNegocio.grupoNegocio" value="${CultivosEntidadesBean.grupoNegocio.grupoNegocio}" type="hidden"/>
					<input name="pctgeneralentidad" value="${CultivosEntidadesBean.pctgeneralentidad}" type="hidden"/>
					<input name="pctadministracion" value="${CultivosEntidadesBean.pctadministracion}" type="hidden"/>
					<input name="pctadquisicion" value="${CultivosEntidadesBean.pctadquisicion}" type="hidden"/>
					
									<input type="hidden" name="fechaEfecto"
										value="<fmt:formatDate pattern="dd/MM/yyyy" value="${CultivosEntidadesBean.fechaEfecto}"/>"/>									
							
		
									
									
					<!-- END STORE BEAN DATA -->
					<div align="center" id="panelAlertasValidacion_replica" ></div>
									
   
					<div>
						<fieldset class="panel2 isrt">
							<legend class="literal">Origen</legend>
							<table>
								<tr>
									<td class="literal">Plan</td>
									<td class="literal" style="padding:0.5em 1em" >
										<input value="${planOrigen}" class="dato" name="plan_origen" size="5" maxlength="4" id="plan_re" tabindex="1" />
									</td>
									<td class="literal">Línea</td>
									<td class="literal" nowrap style="padding:0.5em 1em">
										<input value="${lineaOrigen}" class="dato" name="linea_origen" size="3" maxlength="3" id="linea_re" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_linea_re');"/>
										<input class="dato"  name="linea_origen_desc" value="${lineaOrigenDesc}"  id="desc_linea_re" size="40" disable="disable"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaReplicaOrigen','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
									</td>
								</tr>
							</table>
						</fieldset>
					</div>
					<div>
						<fieldset class="panel2 isrt">
							<legend class="literal">Destino</legend>
							<table>
								<tr>
									<td class="literal">Plan</td>
									<td class="literal" style="padding:0.5em 1em">
										<input value="${planDestino}" name="plan_destino" class="dato" size="5" maxlength="4" id="planreplica" tabindex="1" />
									</td>
									<td class="literal">Línea</td>
									<td class="literal" nowrap style="padding:0.5em 1em">
										<input value="${lineaDestino}" name="linea_destino" class="dato" size="3" maxlength="3" id="lineareplica" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_lineareplica');"/>
										<input class="dato" name="linea_destino_desc" value="${lineaDestinoDesc}" id="desc_lineareplica" size="40" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaReplica','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />	
									</td>
								</tr>
							</table>
						</fieldset>
					</div>
				</form>	
			</div>
			<div style="margin-top:15px">
			    <a class="bot" href="javascript:limpiarReplicar()" title="Limpiar">Limpiar</a>
			    <a class="bot" href="javascript:cerrarPopUpReplica()" title="Cancelar">Cancelar</a>
			    <a class="bot" href="javascript:replicarCultivos()" title="Aplicar">Aplicar</a>
			</div>
		</div>
	</div>
	
	
	
	
	
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineaReplicarErroresDestinoWS.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineaReplicarErroresOrigenWS.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
</body>
</html>