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
	<title>Mantenimiento de Descuentos por Oficina/E-S Mediadora</title>
	
	 <%@ include file="/jsp/common/static/metas.jsp"%>
	 
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	
	<!-- JavaScript,jQery & AJAX -->
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
		<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
		<script type="text/javascript" src="jsp/js/lineas.js"></script>
		<script type="text/javascript" src="jsp/moduloComisiones/mtoDescuentos.js" ></script>		
		<%@ include file="/jsp/js/draggable.jsp"%>
	<script type="text/javascript">
			 
			 function cargarFiltro(){
				 <c:if test="${origenLlamada != 'menuGeneral'}">	
					<c:forEach items="${sessionScope.consultaDescuentos_LIMIT.filterSet.filters}" var="filtro">
						<c:if test="${filtro.property == 'subentidadMediadora.entidad.codentidad'}">
							$('#entidad').val('${filtro.value}');
						</c:if>
						<!-- Se controla si el código de oficina es -1 (todas las oficinas) para dejar vacío tanto el código como la descripción de la oficina -->
						<c:if test="${filtro.property == 'oficina.id.codoficina'}">
							<c:if test="${filtro.value != '-1'}">
								$('#oficina').val('${filtro.value}');
							</c:if>
							<c:if test="${filtro.value == '-1'}">
								$('#oficina').val('');
								$('#desc_oficina').val('');
							</c:if>
						</c:if>
						<c:if test="${filtro.property == 'subentidadMediadora.id.codentidad'}">
							$('#entmediadora').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'subentidadMediadora.id.codsubentidad'}">
							$('#subentmediadora').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'delegacion'}">
							$('#delegacion').val('${filtro.value}');
						</c:if>						
						<%-- <c:if test="${filtro.property == 'permitirRecargo'}">
							$('#permitirRecargo').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'verComisiones'}">
							$('#verComisiones').val('${filtro.value}');
						</c:if> --%>
						<c:if test="${filtro.property == 'pctDescMax'}">
							$('#pctDescMax').val('${filtro.value}');
						</c:if>
						
						<c:if test="${filtro.property == 'linea.codplan'}">
							$('#plan').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'linea.codlinea'}">
							$('#linea').val('${filtro.value}');
						</c:if>
					</c:forEach>
				</c:if>
			} 
			 
			 function returnBack(){
				 
				 <c:if test="${origenLlamada == 'incidenciasComisionesUnificadas'}">
					 $(window.location).attr('href', 'incidenciasUnificado.run?rand=' + UTIL.getRand() + 
								'&origenLlamada=mtoDescuentos'+
								'&method=doConsulta');	
				 </c:if>
				 
 				<c:if test="${origenLlamada != 'incidenciasComisionesUnificadas'}">
		 				$(window.location).attr('href', 'incidencias.html?rand=' + UTIL.getRand() + 
								'&idFichero='+$('#idFicheroComisiones').val()+
								'&tipo='+$('#tipoFicheroComisiones').val()+
								'&codplan='+$('#plan').val()+
								'&method=doConsulta');	
				 </c:if>
				}
			 
		</script>

		
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cargarFiltro();SwitchSubMenu('sub6', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr align="center">
				<td align="left">
					&nbsp;<a class="bot" id="btnReplicar" href="javascript:replicar()">Replicar</a>
					<a class="bot" id="btnCambioMasivo" href="javascript:cambioMasivo();" title="Cambio masivo">Cambio masivo</a>
				</td>
				<td align="right">			
					<a class="bot" id="btnAlta"  href="javascript:alta();">Alta</a>				
					<a class="bot" id="btnModif" style="display:none" href="javascript:editar();">Modificar</a>				
					 
					 <%-- 
					 <c:if test="${origenLlamada == 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a>
					</c:if> 
					<c:if test="${origenLlamada != 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
					</c:if>
					--%>		
					
					<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							
					<a class="bot" id="btnLimpiar"  href="javascript:limpiar();">Limpiar</a>		
					<c:if test="${origenLlamada == 'incidenciasComisiones' || origenLlamada == 'incidenciasComisionesUnificadas'}">
						<a class="bot" id="btnVolver"  href="javascript:returnBack();">Volver</a>
					</c:if>	
				</td>
			</tr>
		</table>
	</div>
	<div class="conten" style="padding: 3px; width: 100%">
		<p class="titulopag" align="left">Mantenimiento de Descuentos por Oficina/E-S Mediadora</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		<form:form id="main3" name="main3" method="post" action="mtoDescuentos.run" commandName="descuentosBean">
			<input type="hidden" name="method" id="method"/>			
			<input type="hidden" id="perfil" name="perfil" value="${perfil}"/>
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
			<input type="hidden" name="grupoOficinas" id="grupoOficinas" value=""/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" />	
			<input type="hidden" name="entidadH" id="entidadH" />	
			<input type="hidden" name="oficinaH" id="oficinaH" />	
			<input type="hidden" name="entmediadoraH" id="entmediadoraH" />	
			<input type="hidden" name="subentmediadoraH" id="subentmediadoraH" />
			<input type="hidden" name="nomEntidadH" id="nomEntidadH" />
			<input type="hidden" name="nomOficinaH" id="nomOficinaH" />
			<input type="hidden" name="delegacionH" id="delegacionH" />
			<input type="hidden" name="pctDescMaxH" id="pctDescMaxH" />
			<input type="hidden" name="permitirRecargoH" id="permitirRecargoH" />
			<input type="hidden" name="verComisionesH" id="verComisionesH" />
			<input type="hidden" name="planH" id="planH" />
			<input type="hidden" name="lineaH" id="lineaH" />
			<input type="hidden" name="nomlineaH" id="nomlineaH" />
			<input type="hidden" name="planreplica" id="planreplica" value="${planreplica}"/>
			<input type="hidden" name="lineareplica" id="lineareplica" value="${lineareplica}"/>
			<input type="hidden" name="entidadreplica" id="entidadreplica"/>
			<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
			<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
			<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>
			
			<form:hidden path="id" id="id"/>
			
			<div style="panel2 isrt">
				<fieldset>
					<table width="100%"> 
						<tr>
							
							<td class="literal" width="5%">Entidad</td>
							<td colspan="3" width="34%">
								<c:if test="${perfil == 0 || perfil == 5}">
									<form:input path="subentidadMediadora.entidad.codentidad" size="4" maxlength="4" cssClass="dato" id="entidad" onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');"/>
									<form:input path="subentidadMediadora.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="30" readonly="true"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
									<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio">*</label>
								</c:if>
								<c:if test="${perfil == 1}">
									<form:input path="subentidadMediadora.entidad.codentidad" size="4" maxlength="4" cssClass="dato" readonly="true" id="entidad"/>
									<form:input path="subentidadMediadora.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="30" readonly="true"/>
								</c:if>
								
							</td>
							<td class="literal" width="5%">Oficina</td>
							<td  width="21%">
									<form:input path="oficina.id.codoficina" size="4" maxlength="4" cssClass="dato" id="oficina"  onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
									<form:input path="oficina.nomoficina" cssClass="dato"	id="desc_oficina" size="20" readonly="true"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
									<%-- <label class="campoObligatorio" id="campoObligatorio_oficina" title="Campo obligatorio">*</label> --%>
							</td>
							<td class="literal" width="7%">E-S Med</td>
							<td width="14%">
								<form:input	path="subentidadMediadora.id.codentidad" size="4" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
								<form:input	path="subentidadMediadora.id.codsubentidad" size="4" maxlength="4" cssClass="dato" id="subentmediadora" />
								<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
								<label class="campoObligatorio" id="campoObligatorio_entmediadora" title="Campo obligatorio">*</label>
							</td>
							
							<td class="literal" width="8%">Delegación</td>
							<td width="6%">
								<form:input	path="delegacion" size="4" maxlength="4" cssClass="dato" id="delegacion" />
								<label class="campoObligatorio" id="campoObligatorio_delegacion" title="Campo obligatorio">*</label>
							</td>
						</tr>
					</table>					
					<table width="100%">
						<tr><td></td></tr>
						<tr>
							<td class="literal" width="5%">Plan</td>
							<td>
								<form:input path="linea.codplan" size="4"maxlength="4" cssClass="dato" id="plan" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');" /> 
								<label class="campoObligatorio" id="campoObligatorio_plan"	title="Campo obligatorio"> *</label>
							</td>
							<td class="literal">Línea</td>
							<td>
								<form:input path="linea.codlinea" size="3"	maxlength="3" cssClass="dato" id="linea" onchange="javascript:lupas.limpiarCampos('desc_linea');" />
								<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="30" readonly="true"/>
								<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
								<label class="campoObligatorio"	id="campoObligatorio_linea" title="Campo obligatorio"> *</label>
							</td> 
						
						
							<td class="literal">% Descuento Máximo  </td>
							<td>
	                      		<form:input	path="pctDescMax" size="4" maxlength="5" cssClass="dato" id="pctDescMax" onchange="this.value = this.value.replace(',', '.')" />
			                	<label class="campoObligatorio" id="campoObligatorio_pctDescMax" title="Campo obligatorio">*</label>
	                        </td>
	                        
	                        <td class="literal">¿Permitir recargos?</td>
								<td>
									<form:select  cssClass="dato" id="permitirRecargo" path="permitirRecargo">										
										<form:option value=""></form:option>
										<form:option value="1">Si</form:option>
										<form:option value="0">No</form:option>								
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_permitirRecargo" title="Campo obligatorio">*</label>
								</td>
	                        
	                        <td class="literal">¿Ver comisiones?</td>
								<td>
									<form:select  cssClass="dato" id="verComisiones" path="verComisiones">
										<form:option value=""></form:option>
										<form:option value="0">Ninguna</form:option>
										<form:option value="1">Entidad</form:option>										
										<form:option value="2">E-S Med.</form:option>
										<form:option value="3">Todas</form:option>
									</form:select>
									<label class="campoObligatorio" id="campoObligatorio_verComisiones" title="Campo obligatorio">*</label>
								</td>
	                        
						</tr>
					</table>
				</fieldset>
			</div>
		</form:form>
		<!-- Grid Jmesa -->
		<div id="grid" style="width: 98%; margin: 0 auto;">
	  		${consultaDescuentos}		  							               
		</div> 	
	</div>
	
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>
		
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
	
	<!-- 19/02/2015 ************-->
		<!-- PANEL CAMBIO MASIVO -->
		<!-- ***************************-->
		
	<div id="panelCambioMasivo" class="panelCambioMasivo" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;width: 68%;">

		     <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
			    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Cambio masivo</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					  <span onclick="cerrarCambioMasivo()">x</span>
				</a>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content">
				<div id="panelInformacion_cm" class="panelInformacion">
					<div id="txt_mensaje_cm" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:20px;"></div>
						<form:form name="frmCambioMasivo" id="frmCambioMasivo" action="mtoDescuentos.run" method="post" commandName="descuentosBean">
						
							<input type="hidden" name="method" id="method" value="doCambioMasivo"/>
							<input type="hidden" name="listaIdsMarcados_cm" id="listaIdsMarcados_cm" value=""/>
	  						
	  						
	  						<div class="panel2 isrt" style="width:95%">
								<fieldset>
								   <table style="border:2px" style="width: 100%">
									<tr>
										<td class="literal" >% Descuento Máximo</td>  
											<td><form:input	size="4" maxlength="5" cssClass="dato" id="pctDescMax_cm" path="pctDescMax" onchange="this.value = this.value.replace(',', '.')" />
										</td>
										<td >&nbsp;</td>
										<td class="literal" >¿Permitir recargos?</td>
										<td >										
											<form:select  cssClass="dato" id="permitirRecargo_cm" path="permitirRecargo" cssStyle="width:60"  onchange="$('#txt_mensaje_cm').html('');">										
												<form:option value=""></form:option>
												<form:option value="1">Si</form:option>
												<form:option value="0">No</form:option>								
											</form:select>									
										</td>
		                        		<td  >&nbsp;</td>
		                        		<td class="literal" >¿Ver comisiones?</td>
										<td >
											<form:select  cssClass="dato" id="verComisiones_cm" path="verComisiones" cssStyle="width:80"  onchange="$('#txt_mensaje_cm').html('');">
												<form:option value=""></form:option>
												<form:option value="0">Ninguna</form:option>
												<form:option value="1">Entidad</form:option>										
												<form:option value="2">E-S Med.</form:option>
												<form:option value="3">Todas</form:option>
											</form:select>
										</td>					
									</tr>
								</table>
								</fieldset>
							</div>	  						
							
						
						</form:form>	
				</div>
				<div style="margin-top:15px">
					<a class="bot" href="javascript:limpiarCambioMasivo()" title="Limpiar">Limpiar</a>
					<a class="bot" href="javascript:cerrarCambioMasivo()" title="Cancelar">Cancelar</a>
					<a class="bot" href="javascript:aplicarCambioMasivo()" title="Aplicar">Aplicar</a>
				</div>
			</div>
	</div> 
	

	<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineaReplicar.jsp"%>
	
</body>

</html>