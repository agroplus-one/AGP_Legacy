<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de Errores Web Service</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		
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
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloTaller/errorWs/errorWs.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
			 
			function cargarFiltro(){
					
				<c:forEach items="${sessionScope.consultaErrorWsAccion_LIMIT.filterSet.filters}" var="filtro">
					<c:if test="${filtro.property == 'errorWs.catalogo'}">
						$('#catalogo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'errorWs.coderror'}">
						$('#coderror').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'errorWs.descripcion'}">
						$('#desc_error').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'errorWs.errorWsTipo.codigo'}">
						$('#codigoTipo').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'linea.codplan'}">
						$('#plan').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'linea.codlinea'}">
						$('#linea').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'servicio'}">
						$('#servicio').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'ocultar'}">
						$('#ocultar').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'entidad.codentidad'}">
						$('#entidad').val('${filtro.value}');
					</c:if>
				</c:forEach>
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cargarFiltro();">		
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">		
							<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar();">Modificar</a>	
							<a class="bot" id="btnReplicar" href="javascript:replicar()">Replicar</a>
							<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>	
							<a class="bot" id="btnCambioMasivo" href="javascript:cambioMasivo();" title="Cambio masivo">Cambio masivo</a>			
							
							<c:if test="${origenLlamada == 'menuGeneral'}">
								<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a>
							</c:if>			
							<c:if test="${origenLlamada != 'menuGeneral'}">
								<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							</c:if>
							
							<a class="bot" href="javascript:limpiar();">Limpiar</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
		<p class="titulopag" align="left">Errores Web Service</p>
			
			<form:form name="main3" id="main3" action="errorWsAccion.run" method="post" commandName="errorWsAccionBean">
				<form:hidden path="id" id="id" />
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
				<input type="hidden" name="origenLlamada" id="origenLlamada" />
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
				<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
				<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>
				
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="nuevoError" id="nuevoError" />
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div>
					<fieldset style="width:70%;margin:0 auto;">
						<legend class="literal">Filtro</legend>
						<table>
							<tr>
								<td valign="top">
									<table>
										<tr>
											<td class="literal">Error</td>
											<td class="literal" nowrap>
												<form:select path="errorWs.id.catalogo" cssClass="dato" id="catalogo" cssStyle="width:105"  tabindex="1">
													<form:option value="">Todos</form:option>
													<form:option value="P">Póliza</form:option>
													<form:option value="S">Siniestro</form:option>
												</form:select>
												<form:input path="errorWs.id.coderror" cssClass="dato" id="coderror" tabindex="2" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_error');lupas.limpiarCampos('codigoTipo');"/>
												<form:input path="errorWs.descripcion" cssStyle="width:300" cssClass="dato" id="desc_error" size="60" tabindex="3"/>
												<form:select path="errorWs.errorWsTipo.codigo" cssClass="dato" cssStyle="width:105" id="codigoTipo" tabindex="4">
													<form:option value="">Todos</form:option>
													<c:forEach items="${errorWsTipos}" var="errorWsTipo">
														<form:option value="${errorWsTipo.codigo}">${errorWsTipo.descripcion}</form:option>
													</c:forEach>
												</form:select>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ErrorWs','principio', '', '');" alt="Buscar Error" title="Buscar Error" />
											</td>
										</tr>
									</table>
								</td>
								<td valign="top">
									<table>
										<tr>
											<td class="literal">Entidad</td>
											<td class="literal" nowrap>
												<form:input path="entidad.codentidad" size="5" maxlength="4" cssClass="dato" id="entidad" tabindex="5" onchange="javascript:lupas.limpiarCampos('desc_entidad');"/>
												<form:input path="entidad.nomentidad" cssStyle="width:210" cssClass="dato"	id="desc_entidad" size="40" readonly="true"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
												<label class="campoObligatorio" id="campoObligatorio_entidad" title="Campo obligatorio"> *</label>
											</td>
										
										</tr>
									</table>
								</td>
								<td></td>
								<td rowspan="2">
									<form:select id="listaPerfiles" path="listaPerfiles" cssClass="dato" multiple="true" cssStyle="height:105;width:95" tabindex="10">
										<c:forEach items="${listaPerfil}" var="perfil">
											<form:option value="${perfil.id}"> ${perfil.descripcion}</form:option>
										</c:forEach>
									</form:select>
								</td>
							</tr>
							<tr>
								<td>
									<table>
										<tr>
											<td class="literal">Plan</td>
											<td class="literal">
												<form:input path="linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan" tabindex="6" />
												<label class="literal">Línea</label>
												<form:input path="linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="7" onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
									
												<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
											</td>
										</tr>
									</table>
								</td>
								<td>
									<table style="width:100%">
										<tr>
											<td class="literal">Servicio</td>
											<td>
												<form:select path="servicio" cssClass="dato" cssStyle="width:100" id="servicio" tabindex="8">
													<form:option value="">Todos</form:option>
													<form:option value="VA">Validacion</form:option>
													<form:option value="PD">Pasar a Definitiva</form:option>
													<form:option value="AM">Anexo Mod.</form:option>
													<form:option value="SN">Siniestro</form:option>
													<form:option value="RC">Anexo R.C.</form:option>
												</form:select>
											</td>
											<td class="literal">Ocultar</td>
											<td>
												<form:select path="ocultar" cssClass="dato" cssStyle="width:70" id="ocultar" tabindex="9">
													<form:option value="">Todos</form:option>
													<form:option value="S">Si</form:option>
													<form:option value="N">No</form:option>
												</form:select>
											</td>											
										</tr>							
									</table>
								</td>
								<td><label class="literal" style="margin-right:10px;">Forzar</label></td>
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			
			
			
			<!-- Grid Jmesa -->
			<div id="grid" style="width: 95%;margin: 0 auto;">
				 ${consultaErrorWsAccion}
			</div>
		</div>
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/common/static/overlayCambioMasivo.jsp"%>
		<%@ include file="/jsp/common/static/overlayReplicaErrores.jsp"%>
		
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
				<div id="panelInformacion" class="panelInformacion">
					<div id="txt_mensaje_aviso"></div>
				</div>
				<div style="margin-top:15px">
				 	    <a class="bot" id="btn_hidePopUpAviso" href="javascript:hidePopUpAviso()" title="Aceptar">Aceptar</a>
				</div>
			 </div>
		</div>
		
		
		<!-- DAA 12/02/2013 ************-->
		<!-- PANEL CAMBIO MASIVO ERRORES -->
		<!-- ***************************-->
		
		<div id="panelCambioMasivoErrorWs" class="wrapper_popup" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">

		     <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
			    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">Cambio masivo</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					  <span onclick="cerrarCambioMasivoErrorWs()">x</span>
				</a>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<div id="txt_mensaje_cm" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:20px;"></div>
						<form:form name="main" id="main" action="errorWsAccion.run" method="post" commandName="errorWsAccionBean">
						
							<input type="hidden" name="method" id="method" value="doCambioMasivo"/>
							<input type="hidden" name="listaIdsMarcados_cm" id="listaIdsMarcados_cm" value=""/>
	  						
	  							  						
							<div class="panel2 isrt" style="width:70%">
							<fieldset>
							<table style="border:2px">
								<tr>
								<td class="literal" style="vertical-align:'super';" style="padding:0.0em 1em">Ocultar</td>
									<td class="literal" style="vertical-align:'super';">
										<form:select path="ocultar" cssClass="dato" cssStyle="width:70" id="ocultar_cm" tabindex="2" onchange="javascript:comprobarBotonOcultar();$('#txt_mensaje_cm').html('');">
											<form:option value=""></form:option>
											<form:option value="S">Si</form:option>
											<form:option value="N">No</form:option>
										</form:select>
									</td>
									<td class="literal" style="vertical-align:'super';" style="padding:0.0em 1em">Forzar</td>
										<td class="literal">
											<form:select id="listaPerfiles_cm" path="listaPerfiles" cssClass="dato" multiple="true" cssStyle="height:105;width:95">
												<c:forEach items="${listaPerfil}" var="perfil">
													<form:option value="${perfil.id}"> ${perfil.descripcion}</form:option>
												</c:forEach>
											</form:select>
										</td>
								</tr>
							</table>
							</fieldset>
							</div>
						
						</form:form>	
				</div>
				<div style="margin-top:15px">
						    <a class="bot" href="javascript:limpiarCambioMasivoErrorWs()" title="Limpiar">Limpiar</a>
						    <a class="bot" href="javascript:cerrarCambioMasivoErrorWs()" title="Cancelar">Cancelar</a>
						    <a class="bot" href="javascript:aplicarCambioMasivoErrorWs()" title="Aplicar">Aplicar</a>
				</div>
			</div>
		</div>
		
		<!-- ****** OFC 07/08/2017 ***** -->
		<!-- NUEVO PANEL REPLICA ERRORES -->
		<!-- *************************** -->
		<div id="panelReplicaErrores" class="wrapper_popup" style="left: 25%; width: 55%; style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.9em;">

		     <!--  header popup -->
			<div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
			    <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px" >Replicar Errores</div>
				<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
					  <span onclick="cerrarReplicaErrores()">x</span>
				</a>
			</div>
			<!--  body popup -->
			<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion" >
					<div id="txt_mensaje_re" style="color:red;display:block;font-size:12px;font-style:italic;font-weight:bold;line-height:40px;"></div>
						<form:form name="main2" id="main2" action="errorWsAccion.run" method="post" commandName="errorWsAccionBean">
						
							<input type="hidden" name="plan_orig" id="plan_orig" value=""/>
							<input type="hidden" name="linea_orig" id="linea_orig" value=""/>
							<input type="hidden" name="plan_dest" id="plan_dest" value=""/>
							<input type="hidden" name="linea_dest" id="linea_dest" value=""/>
							
							<input type="hidden" name="servicio_orig" id="servicio_orig" value=""/>
							<input type="hidden" name="servicio_dest" id="servicio_dest" value=""/>
							
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
											<td class="literal">Servicio</td>
											<td class="literal" style="padding:0.5em 1em">
												<select class="dato" style="width:100" id="servicio_re" tabindex="1">
													<option value="">Todos</option>
													<option value="VA">Validacion</option>
													<option value="PD">Pasar a Definitiva</option>
													<option value="AM">Anexo Mod.</option>
													<option value="SN">Siniestro</option>
												</select>										
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
											<td class="literal">Servicio</td>
											<td class="literal" style="padding:0.5em 1em">
												<select class="dato" style="width:100" id="servicio_re_2" tabindex="1">
													<option value="">Todos</option>
													<option value="VA">Validacion</option>
													<option value="PD">Pasar a Definitiva</option>
													<option value="AM">Anexo Mod.</option>
													<option value="SN">Siniestro</option>
												</select>										
											</td>
										</tr>
									</table>
								</fieldset>
							</div>
						
						</form:form>	
				</div>
				<div style="margin-top:15px">
						    <a class="bot" href="javascript:limpiarReplicaErrores()" title="Limpiar">Limpiar</a>
						    <a class="bot" href="javascript:cerrarReplicaErrores()" title="Cancelar">Cancelar</a>
						    <a class="bot" href="javascript:aplicarReplicaErrores()" title="Aplicar">Aplicar</a>
				</div>
			</div>
		</div>
		
		<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLineaReplicarErroresDestinoWS.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLineaReplicarErroresOrigenWS.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaErroresWsTipos.jsp"%>

	</body>
</html>