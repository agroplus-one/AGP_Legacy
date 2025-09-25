<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag" >
	<c:set var="numReg">
		<fmt:message key="numElementsPag"/>
	</c:set>
</fmt:bundle>
<html>
	<head>
		<title>Mantenimiento de Comisiones por E-S Mediadora</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script> 
		
		<script type="text/javascript" src="jsp/moduloTaller/mtoComisionesRenov/mtoComisionesRenov.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		                                    
		
		<script type="text/javascript">
			 
			 function cargarFiltro(){
				 if($('#origenLlamada').val() == 'menuGeneral') {
						$('#codplan').val('');
				 }
				 <c:if test="${origenLlamada != 'menuGeneral'}">	
					<c:forEach items="${sessionScope.consultaComisionesRenov_LIMIT.filterSet.filters}" var="filtro">
						<c:if test="${filtro.property == 'codplan'}">
							$('#plan').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'codlinea'}">
							$('#linea').val('${filtro.value}');
						</c:if>
						
						<c:if test="${filtro.property == 'codentidad'}">
							$('#entidad').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'codentmed'}">
							$('#entmediadora').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'codsubmed'}">
							$('#subentmediadora').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'idgrupo'}">
							$('#idgrupo').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'codmodulo'}">
							$('#codmodulo').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'refimporte'}">
							$('#refimporte').val('${filtro.value}');
						</c:if>
					
						<c:if test="${filtro.property == 'impDesde'}">
							$('#impDesde').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'impHasta'}">
							$('#impHasta').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'comision'}">
							$('#comision').val('${filtro.value}');
						</c:if>
						
					</c:forEach>
				</c:if>
			} 
		
		</script>

	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cargarFiltro();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons" >
			<table width="97%" cellspacing="10" cellpadding="0" border="0">
				<tbody>
					<tr>
						<%--<td align="left">
							&nbsp;<a class="bot" id="btnReplicar" href="javascript:replicar_old()">Replicar</a>
							
						</td> --%>
						<td align="left">
							&nbsp;<a class="bot" id="btnReplicar" href="javascript:replicar()">Replicar</a>
						</td>  
						<td align="right">
							<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>
							<a class="bot" id="btnModificar" style="display: none" href="javascript:guardarModificaciones()">Modificar</a>
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							<a class="bot" href="javascript:limpiar();">Limpiar</a>							
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<form name="limpiar" id="limpiar" action="mtoComisionesRenov.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>								
			<input type="hidden" name="origenLlamadaLimp" id="origenLlamadaLimp" value="menuGeneral"/>
		</form>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
		<p class="titulopag" align="left">MANTENIMIENTO DE COMISIONES POR E-S MEDIADORA</p>					
			
			<form:form name="main3" id="main3" action="mtoComisionesRenov.run" method="post" commandName="comisionesRenovBean">
				
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>	
				<input type="hidden" id="perfil" name="perfil" value="${perfil}"/>
				<input type="hidden" id="externo" value="${externo}" />
				<input type="hidden" name="grupoNegocio"  id="grupoNegocio"  value="${grupoNegocio}"/>
			    <input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
				
				<input type="hidden" name="plan_orig" id="plan_orig" value=""/>
				<input type="hidden" name="linea_orig" id="linea_orig" value=""/>
				<input type="hidden" name="plan_dest" id="plan_dest" value=""/>
				<input type="hidden" name="linea_dest" id="linea_dest" value=""/>
				
				<!-- Modif 07.03.2019 -->
				<%--<input type="hidden" name="planreplica" id="planreplica" value="${planreplica}"/>
			    <input type="hidden" name="lineareplica" id="lineareplica" value="${lineareplica}"/> --%>
				<!-- Modif 07.03.2019 -->
				
				<form:hidden path="id" id="id"/>
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div class="panel2 isrt" style="width: 98%">
				
					<table width="100%">
						<tr>						
							<td class="literal" style="text-align:right; padding-right:10px;">Plan</td>
							<td>
								<form:input path="codplan" size="4" maxlength="4" cssClass="dato" id="plan" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" /> 
								<label class="campoObligatorio" id="campoObligatorio_plan"	title="Campo obligatorio"> *</label>
							</td>
							<td class="literal" style="text-align:right; padding-right:10px;">Línea</td>
							<td colspan="5">
								<form:input path="codlinea" size="3" maxlength="3" cssClass="dato" id="linea" onchange="javascript:lupas.limpiarCampos('desc_linea');" />								
								<form:input path="nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
								<label class="campoObligatorio"	id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
							</td>
						</tr>
						<tr>
							<td class="literal" style="text-align:right; padding-right:10px;">Entidad</td>
							<td colspan="3">
								<c:if test="${perfil == 0 || perfil == 5}">
									<form:input path="codentidad" size="4" maxlength="4" cssClass="dato" id="entidad" onchange="javascript:lupas.limpiarCampos('desc_entidad');" />
									<form:input path="nombreEntidad" cssClass="dato" id="desc_entidad" size="40" readonly="true"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
								</c:if> 
								<c:if test="${perfil > 0 && perfil < 5}">
									<form:input path="codentidad" size="4" maxlength="4" cssClass="dato" id="entidad" readonly="true" tabindex="1"/>
									<form:input path="nombreEntidad" cssClass="dato" id="desc_entidad" size="40" readonly="true"/>
								</c:if> 
								<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio"> *</label>
							</td>
							<c:if test="${externo == 0}"> <!--  es interno -->
								<td class="literal" style="text-align:right; padding-right:10px;">Entidad mediadora</td>
								<td>
									<form:input	path="codentmed" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora', 'desc_subentmediadora');UTIL.subStrEntidad();" />
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('EntidadMediadora','principio', '', '');" alt="Buscar Entidad Mediadora" title="Buscar Entidad Mediadora" />
									<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio">*</label>
								</td>
								<td class="literal" style="text-align:right; padding-right:10px;">Subentidad mediadora</td>
								<td>
									<form:input	path="codsubmed" size="4" maxlength="4" cssClass="dato" id="subentmediadora" onchange="javascript:lupas.limpiarCampos('desc_subentmediadora');" />
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
									<label class="campoObligatorio" id="campoObligatorio_subentmediadora" title="Campo obligatorio">*</label>
								</td>
							</c:if>
							
					    </tr>
					    <tr>
							<td class="literal" style="text-align:right; padding-right:10px;">Grupo Negocio</td>
							<td>			
								<form:select path="idgrupo" cssClass="dato"	cssStyle="width:100" id="idgrupo" >
									<form:option value=""></form:option>
									<c:forEach items="${gruposNegocio}" var="grupoNegocio">
										<form:option value="${grupoNegocio.grupoNegocio}">${grupoNegocio.descripcion}</form:option>
									</c:forEach>
								</form:select>
								<label class="campoObligatorio" id="campoObligatorio_idgrupo" title="Campo obligatorio"> *</label>
							</td>
							<td class="literal" style="text-align:right; padding-right:10px;">M&oacute;dulo</td>
							<td>			
								<form:input path="codmodulo" size="4" maxlength="5" cssClass="dato" id="codmodulo" onchange="$(this).val($(this).val().toUpperCase());"/> 
								<label class="campoObligatorio" id="campoObligatorio_modulo" title="Campo obligatorio"> *</label>
							</td>	
						</tr>
					    <tr>					    	
							<td class="literal" style="text-align:right; padding-right:10px;">Importe de Referencia</td>
							<td>
								<form:select path="refimporte" cssClass="dato" cssStyle="width:130" id="refimporte" >
									<form:option value=""></form:option>
									<form:option value="C">Coste Tomador</form:option>
									<form:option value="P">Prima Comercial</form:option>
								</form:select>
								<label class="campoObligatorio" id="campoObligatorio_refimporte" title="Campo obligatorio"> *</label>
							</td>
							 
							<td class="literal" align="right" style="text-align:right; padding-right:10px;">Valor desde (&ge;)</td>
							<td>  
							 	<form:input path="impDesde" cssClass="dato" size="15" maxlength="11" id="impDesde" /> 
							 	<label class="campoObligatorio" id="campoObligatorio_impdesde" title="Campo obligatorio">*</label>
							</td>
							<td class="literal" align="right" style="text-align:right; padding-right:10px;" >Valor hasta (&lt;)</td>
							<td> 
							 	<form:input path="impHasta"  cssClass="dato" size="15" maxlength="11" id="impHasta" /> 
							 	<label class="campoObligatorio" id="campoObligatorio_impHasta" title="Campo obligatorio">*</label>
							</td>
							<td class="literal" style="text-align:right; padding-right:10px;" align="left">Comisión </td>
							<td align="left">
								<form:input path="comision" cssClass="dato" size="4" id="comision" maxlength="5" /> 
								<label class="campoObligatorio" id="campoObligatorio_comision" title="Campo obligatorio"> *</label>
					    </tr>
					    
					</table>
				</div>
			</form:form>
			
			<form:form name="frmBorrar" id="frmBorrar" action="mtoComisionesRenov.run" method="post" commandName="comisionesRenovBean">
				<input type="hidden" name="method" id="methodBorrar" />
				<form:hidden path="id" id="idBorrar" />
				<form:hidden path="codplan" id="codPlanB" />
 				<form:hidden path="codlinea" id="codLineaB" />
				<form:hidden path="codentmed" id="entMedB" />
				<form:hidden path="codsubmed" id="subEntMedB" />
				<form:hidden path="idgrupo" id="idGrupoB" />
				<form:hidden path="codmodulo" id="codModuloB" />
				<form:hidden path="codentidad" id="entidadB" />
				<form:hidden path="refimporte" id="refImporteB" />
				<form:hidden path="impDesde" id="impDesdeB" />
				<form:hidden path="impHasta" id="impHastaB" />
				<form:hidden path="comision" id="comisionB" />
				
				<input type="hidden" name="codPlanBorrar" id="codPlanBorrar" />
 				<input type="hidden" name="codLineaBorrar" id="codLineaBorrar" />
 				<input type="hidden" name="entidadBorrar" id="entidadBorrar" />
				<input type="hidden" name="entMedBorrar" id="entMedBorrar" />
				<input type="hidden" name="subEntMedBorrar" id="subEntMedBorrar" />
				<input type="hidden" name="idGrupoBorrar" id="idGrupoBorrar" />
				<input type="hidden" name="codModuloBorrar" id="codModuloBorrar" />
				
				<input type="hidden" name="refImporteBorrar" id="refImporteBorrar" />
				<input type="hidden" name="impDesdeBorrar" id="impDesdeBorrar" />
				<input type="hidden" name="impHastaBorrar" id="impHastaBorrar" />
				<input type="hidden" name="comisionBorrar" id="comisionBorrar" />
				
			</form:form>
			
			<form:form name="frmModificar" id="frmModificar" action="mtoComisionesRenov.run" method="post" commandName="comisionesRenovBean">
				<input type="hidden" name="method" id="modificar" />
				<form:hidden path="id" id="idModif" />
				<form:hidden path="codplan" id="codPlanModif" />
				<form:hidden path="codlinea" id="codLineaModif" />
				<form:hidden path="codentmed" id="entMedModif" />
				<form:hidden path="codsubmed" id="subEntMedModif" />
				<form:hidden path="idgrupo" id="idGrupoModif" />
				<form:hidden path="codmodulo" id="codModuloModif" />
				<form:hidden path="codentidad" id="EntidadModif" />
				<form:hidden path="refimporte" id="refImporteModif" />
				<form:hidden path="impDesde" id="impDesdeModif" />
				<form:hidden path="impHasta" id="impHastaModif" />
				<form:hidden path="comision" id="comisionModif" />
			</form:form>
			
			<!-- Grid Jmesa -->
						
			<div id="grid" align="center" style="width: 75%;margin:0 auto;">
		  		${consultaComisionesRenov}
			</div>
			
	</div>
	
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>
		<%@ include file="/jsp/common/static/overlayReplicaComisRenov.jsp"%>
		
		<!-- ************* -->
		<!-- POPUP  AVISO  -->
		<!-- ************* -->
		
		<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
		        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Aviso</div>
		        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
		                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
		            <span onclick="hidePopUpAviso()">x</span>
		        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
				<div id="panelInformacion2" class="panelInformacion">
					<div id="txt_mensaje_aviso"></div>
				</div>
				<div style="margin-top:15px">
				 	    <a class="bot" id="btn_hidePopUpAviso" href="javascript:hidePopUpAviso()" title="Aceptar">Aceptar</a>
				</div>
			 </div>
		</div>
		
		
		<!-- Pruebas Tatiana (12.04.2019) -->
		<!-- ************************************ -->
		<!-- NUEVO PANEL REPLICA COMIS RENOVABLES -->
		<!-- ************************************ -->
		<div id="panelReplicaComisRenov" class="wrapper_popup" style="left: 25%; width: 55%; style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.9em;">

		     <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
			    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px" >Replicar Comisiones en Renovables</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					  <span onclick="cerrarReplicaComisRenov()">x</span>
				</a>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion" >
					<div id="txt_mensaje_re" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:40px;"></div>
						<form:form name="main2" id="main2" action="mtoComisionesRenov.run" method="post" commandName="comisionesRenovBean">
														   
							<input type="hidden" name="method" id="method" value="doReplicar"/>
								  						
							<div>
								<fieldset class="panel2 isrt">
									<legend class="literal">Origen</legend>
									<table>
										<tr>
											<td class="literal">Plan</td>
											<td class="literal" style="padding:0.5em 1em" >
												<input class="dato" name="plan_re" size="5" maxlength="4" id="plan_re" tabindex="1" />
											</td>
											<td class="literal">Línea</td>
											<td class="literal" nowrap style="padding:0.5em 1em">
												<input class="dato" name="linea_re" size="3" maxlength="3" id="linea_re" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_linea_re');"/>
												<input class="dato" name="desc_linea_re" id="desc_linea_re" size="40" readonly="true"/>
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
												<input class="dato" size="5" maxlength="4" id="planreplica" tabindex="1" />
											</td>
											<td class="literal">Línea</td>
											<td class="literal" nowrap style="padding:0.5em 1em">
												<input class="dato" size="3" maxlength="3" id="lineareplica" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_lineareplica');"/>
												<input class="dato" id="desc_lineareplica" size="40" readonly="true"/>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaReplica','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
												
											</td>
										</tr>
									</table>
								</fieldset>
							</div>
						
						</form:form>	
				</div>
				<div style="margin-top:15px">
						    <a class="bot" href="javascript:limpiarReplicaComisRenov()" title="Limpiar">Limpiar</a>
						    <a class="bot" href="javascript:cerrarReplicaComisRenov()" title="Cancelar">Cancelar</a>
						    <a class="bot" href="javascript:aplicarReplicaComisRenov()" title="Aplicar">Aplicar</a>
				</div>
			</div>
		</div>
	
		
	<%@ include file="/jsp/common/lupas/lupaLineaReplicarErroresDestinoWS.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineaReplicarErroresOrigenWS.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaErroresWsTipos.jsp"%>
	<!-- Fin Pruebas Tatiana (12.04.2019) -->
		
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidadMediadora.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
<%-- 	<%@ include file="/jsp/common/lupas/lupaPlanLineaReplicar.jsp"%> --%>

</body>
</html>