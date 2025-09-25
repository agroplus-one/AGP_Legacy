<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>

<html>
<head>
    
	<title>Incidencias</title>
	
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
	<script type="text/javascript" src="jsp/moduloUtilidades/incidenciasAgro/IncidenciasAgro.js"></script>
	
	<script type="text/javascript">
	function cargarFiltro() {
	 	<c:if test="${origenLlamada != 'menuGeneral'}">	
			<c:forEach items="${sessionScope.listaIncidencias_LIMIT.filterSet.filters}" var="filtro">	
				<c:if test="${filtro.property == 'codentidad'}">
					$('#entidad').val('${filtro.value}');
				</c:if>		
				<c:if test="${filtro.property == 'oficina'}">
					$('#oficina').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'entmediadora'}">
					$('#entmediadora').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'subentmediadora'}">
					$('#subentmediadora').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'delegacion'}">
					$('#delegacion').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'codplan'}">
					$('#plan').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'codlinea'}">
					$('#linea').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'codestado'}">
					$('#codestado').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'codestadoagro'}">
					$('#codestadoagro').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'nifcif'}">
					$('#nifcif').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'tiporef'}">
					$('#tiporef').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'idcupon'}">
					$('#idcupon').val('${filtro.value}');
				</c:if>	
				<c:if test="${filtro.property == 'codasunto'}">
					$('#asunto').val('${filtro.value}');
				</c:if>
				// MODIF TAM (31.05.2018)** Inicio 
				<c:if test="${filtro.property == 'referencia'}">
					$('#referencia').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'codusuario'}">
					$('#codusuario').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'numero'}">
					$('#numero').val('${filtro.value}');
				</c:if>
				<c:if test="${filtro.property == 'tipoinc'}">
					$('#tipoinc').val('${filtro.value}');
				</c:if>
			</c:forEach>
		</c:if>
	}
	
	function subStrEntidad(){
		var entidadMediadora = document.getElementById('entidad').value;
		if(document.getElementById('entidad').value.length == 4){
		  entidadMediadora = document.getElementById('entidad').value.substr(1);
		}
		document.getElementById('entidadSubstr').value = entidadMediadora
	}

	//Pet. 57627 ** MODIF TAM (11.11.2019) ** Inicio //
	function validarTipoInc(){
		var tipoinc = document.getElementById('tipoinc').value;
		
		if (tipoinc == 'A' || tipoinc == 'R'){
			// ocultamos la lista de asuntos y mostramos la de motivos (Anulaciones y Rescisiones)
			document.getElementById("asunto").style.display = 'none';
			document.getElementById("nombAsunto").style.display='none';
			
			document.getElementById("nombMotivo").style.display='inline';
			document.getElementById("listmotivo").style.display = 'inline';
			document.getElementById("listmotivo").val('');

		}else{
			// ocultamos la lista de motivos y mostramos la de asuntos (Incidencias)
			document.getElementById("nombMotivo").style.display='none';
			document.getElementById("listmotivo").style.display = 'none';
			
			document.getElementById("asunto").style.display = 'inline';
			document.getElementById("nombAsunto").style.display='inline';
			document.getElementById("asunto").val('');
			
		}
		
	}
	
	</script>	
	
	<style> 
	    #divImprimir { 
	        display: none; 
	    } 
	</style> 
	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4'); cargarFiltro();">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<!-- botones de la pagina -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td width="33%">&nbsp;</td>
				<td width="33%" align="center">
					<a class="bot" href="javascript:consultarListAgro();">Consulta Agroseguro</a>
					<a class="bot" id="btnDoc" href="javascript:crearNuevaIncidencia()">Crear nueva incidencia</a>	
				</td>
				<td width="33%" align="right"> 
					<c:if test="${origenLlamada == 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a>
					</c:if>
					<c:if test="${origenLlamada != 'menuGeneral'}">
						<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a> 
					</c:if>
					<a class="bot" id="btnLimpiar"href="javascript:limpiar()">Limpiar</a>
				</td>
			</tr>
		</table>
	</div>
	
	
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">INCIDENCIAS</p>
	
		<!-- Form principal  -->
		<form:form id="formIncidencias" name="formIncidencias" action="utilidadesIncidencias.run" method="post" commandName="VistaIncidenciasAgro">
			
			<input type="hidden" name="usuarioSession" id="usuarioSession" value="${usuarioSession}"/>		
			<input type="hidden" name="method" id="method" value="doConsulta"/>
			
			<input type="hidden" name="origenLlamada" id="origenLlamada"/>
			<form:hidden path="idincidencia" id="idincidencia"/>
			<input type="hidden" name="tipoincBorrado" id="tipoincBorrado"/>
		        
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
			<input type="hidden" name="grupoOficinas" id="grupoOficinas" value="${grupoOficinas}"/>
			<input type="hidden" name="perfilUsuario" id="perfilUsuario" value="${perfil}" />
			
	        <!-- Campos para fechas Desde y Hasta -->
	        <input type="hidden" name="fechaEnvioDesdeId.day" value="">
			<input type="hidden" name="fechaEnvioDesdeId.month" value="">
			<input type="hidden" name="fechaEnvioDesdeId.year" value="">		
			<input type="hidden" name="fechaEnvioHastaId.day" value="">
			<input type="hidden" name="fechaEnvioHastaId.month" value="">
			<input type="hidden" name="fechaEnvioHastaId.year" value="">		
			
			<input type="hidden" name="listaIdsMarcados" id="listaIdsMarcados" value=""/>
			<input type="hidden" name="listaIdsTodos" id="listaIdsTodos" value="${listaIdsTodos}"/>
	        <input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
	        <input type="hidden" name="entidadSubstr" id="entidadSubstr" value="">
	        <input type="hidden" id="externo" value="${externo}" />
 	        <input type="hidden" id="codestadoSel" value="${codestadoSel}" />
	        
			<input type="hidden" name="checkTodo" id="checkTodo" value="${checkTodo}"/>
			<input type="hidden" name="marcaTodos" id="marcaTodos" value="false"/>
			
			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
						
			<fieldset class="panel2 isrt" style="width:auto">
				<legend class="literal">Filtro</legend>
				<div style="width: 100%; float: left;">
					<div>
						<div>
							<div style="width: 100%;">
								<span>
									<label for="entidad" class="literal">Entidad</label>
									<c:if test="${perfil == 0 || perfil == 5}">
										<form:input path="codentidad" size="3" maxlength="4" cssClass="dato" id="entidad" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');javascript:validarEntidad()"/>
										<input class="dato"	id="desc_entidad" size="25" value="${nomEntidad}" readonly="true"/>										
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
									</c:if>
									<c:if test="${perfil > 0 && perfil < 5}">
										<form:input path="codentidad" size="3" maxlength="4" cssClass="dato" readonly="true" id="entidad" tabindex="1"/>
										<input class="dato"	id="desc_entidad" size="25" value="${nomEntidad}" readonly="true"/>
									</c:if>
								</span>
								<span>
									<label for="oficina" class="literal">Oficina</label>
									<c:if test="${perfil != 3 && perfil != 4}">
										<form:input path="oficina" size="3" maxlength="4" cssClass="dato" id="oficina" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
										<input class="dato" value="${nomOficina}" id="desc_oficina" size="18" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
									</c:if>
									<c:if test="${perfil == 3 and externo == 1}">
										<form:input path="oficina" size="3" maxlength="4" cssClass="dato" readonly="true" id="oficina" tabindex="2"/>
										<input class="dato" value="${nomOficina}" id="desc_oficina" size="18" readonly="true"/>
									</c:if>
									<c:if test="${perfil == 3 and externo == 0}">
										<form:input path="oficina" size="3" maxlength="4" cssClass="dato" readonly="true" id="oficina" tabindex="2"/>
										<input class="dato" id="desc_oficina" size="18" value="${nomOficina}" readonly="true"/>
									</c:if>						
									<c:if test="${perfil == 4}">	
										<form:input path="oficina" size="3" maxlength="4" cssClass="dato" id="oficina" tabindex="2" readonly="true"/>
										<input class="dato" value="${nomOficina}" id="desc_oficina" size="18" readonly="true"/>
									</c:if>						
								</span>
								<span>
									<label for="entmediadora" class="literal">E-S Med</label>
									<c:if test="${externo == 0 and perfil != 4}"> <!--  es interno -->
											<form:input	path="entmediadora" size="3" maxlength="4"	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');subStrEntidad();" tabindex="3"/>
											<form:input	path="subentmediadora" size="3" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="4"/>
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
									</c:if>
									<c:if test="${(externo == 1 and perfil == 1) or (externo == 1 and perfil == 3) or perfil == 4}"> <!--  es externo -->
										<form:input	path="entmediadora" size="3" maxlength="4"	cssClass="dato" id="entmediadora" tabindex="3"  readonly="true"/>
										<form:input	path="subentmediadora" size="3" maxlength="4" cssClass="dato" id="subentmediadora" tabindex="4"  readonly="true"/>
									</c:if>
								</span>
								<span>
									<label for="delegacion" class="literal">Delegaci&oacute;n</label>
									<c:if test="${externo == 1}"><!--  es externo -->
										<c:if test="${perfil != 3}">
											<form:input	path="delegacion" size="3" maxlength="4" cssClass="dato" id="delegacion" tabindex="5" />
										</c:if>
										<c:if test="${perfil == 3}">
											<form:input	path="delegacion" size="3" maxlength="4" cssClass="dato" id="delegacion" tabindex="5" readonly="true"/>
										</c:if>
									</c:if>
									<c:if test="${externo == 0}"> <!--  es interno -->
										<form:input	path="delegacion" size="3" maxlength="4" cssClass="dato" id="delegacion" tabindex="5" />
									</c:if>									
								</span>
								<span>
									<label for="delegacion" class="literal">Usuario</label>
									<c:if test="${perfil != 4}">
										<form:input path="codusuario" id="codusuario" size="9" maxlength="19" cssClass="dato" tabindex="6" onchange="this.value=this.value.toUpperCase();"/>
										<img  border="1" id="iconoLupa" src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('UsuarioFiltros','principio', '', '');" alt="Buscar Usuario"/>
									</c:if>
									<c:if test="${perfil == 4}">
										<form:input path="codusuario" id="codusuario" size="9" maxlength="19" cssClass="dato" readonly="true" tabindex="6" />
									</c:if>
								</span>
							</div>
							<br>
							<div style="width: 100%;">
								<span>
									<label for="plan" class="literal">Plan</label>
									<form:input path="codplan" size="4" maxlength="4" cssClass="dato" id="plan" tabindex="7"/>
								</span>
								<span>
									<label for="linea" class="literal">L&iacute;nea</label>
									<form:input path="codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="8" onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
									<input class="dato"	id="desc_linea" size="30" value="${nomLinea}" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />							
								</span>
								<span>
									<label for="referencia" class="literal">Póliza</label>
									<form:input path="referencia" size="10" maxlength="7" cssClass="dato" id="referencia" tabindex="9" onchange="this.value=this.value.toUpperCase();"/>
								</span>
								<span>
									<label for="tiporef" class="literal">Tipo Pol.</label>
									<form:select path="tiporef" cssClass="dato" cssStyle="width:120px" id="tiporef" tabindex="10">
										<option value="" >Todos</option>
										<option value="P"<c:if test="${tiporefSel == 'P'}">selected</c:if>>Principal</option>
										<option value="C"<c:if test="${tiporefSel == 'C'}">selected</c:if>>Complementaria</option>
									</form:select>									
								</span>
								<span>
									<label for="nifcif" class="literal">CIF/NIF Aseg.</label>
									<form:input path="nifcif" id="nifcif" size="10" maxlength="9" cssClass="dato" tabindex="11" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
								</span>
							</div>
							
							<br>
							<div style="width: 100%;">
								<span style="width: 70%;">	
									<span style="width: 22%;">	
										<label for="numero" class="literal">Nº Incidencia</label>
										<form:input path="numero" size="7" maxlength="7" cssClass="dato" id="numero" tabindex="12"/>
									</span>
									<span style="width: 22%;">
										<label for="idcupon" class="literal">Cupón</label>
										<form:input	path="idcupon" size="15" maxlength="15" cssClass="dato" id="idcupon" tabindex="13" onchange="this.value=this.value.toUpperCase();"/>
									</span>
									<span">
										   <label for="asunto" id="nombAsunto" class="literal">Asunto</label>
										   <form:select path="codasunto" cssClass="dato" tabindex="14" id="asunto" >
											   <form:option value="">Todos</form:option>
											   <c:forEach items="${listaAsuntos}" var="asunto">
												   <c:choose>
													   <c:when test="${not empty asuntoInc and asuntoInc.codasunto eq asunto.codasunto}">
														   <option value="${asunto.id.codasunto}" selected="selected">${asunto.descripcion}</option>
													   </c:when>
													   <c:otherwise>
														   <option value="${asunto.id.codasunto}">${asunto.descripcion}</option>
													   </c:otherwise>
												   </c:choose>
											   </c:forEach>
										   </form:select>
										   <label style="display:none" for="listmotivo" id="nombMotivo"class="literal">Motivo</label>
										   <form:select path="codmotivo" cssClass="dato" cssStyle="width:300px" tabindex="14" id="listmotivo" >
											   <form:option value="0">Todos</form:option>
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
										   </form:select>
									</span>
								</span>	
								<span style="width:30%;">
									<label for="codestado" class="literal" style="width:36%;">Estado Agroplus</label>
									<form:select path="codestado" cssClass="dato" tabindex="15" cssStyle="width:155px" id="codestado">
										<option value="" >Todos</option>
										<option value="0"<c:if test="${codestadoSel == '0'}">selected</c:if>>Enviada Err&oacute;nea</option>
										<option value="1"<c:if test="${codestadoSel == '1'}">selected</c:if>>Enviada Correcta</option>
									</form:select>									
								</span>
							</div>	

							<br>
							<div style="width: 100%;">
								<span style="width: 70%;">
									<span style="width: 30%;">	
										<label for="fechaEnvioDesdeId"" class="literal">Fecha Envío desde</label>
										<spring:bind path="fechaEnvioDesde">
							    			<input type="text" tabindex="16" id="fechaEnvioDesdeId" name="fechaEnvioDesdeId" class="dato" size="11" maxlength="10"
							    			onblur="if (!ComprobarFecha(this, document.formIncidencias, 'Fecha Envio Desde')) this.value='';" 
							    			value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fechaEnvioDesdeId}" />"/>
							    		</spring:bind>
							    		<a id="btn_fechaEnvioDesde" name="btn_fechaEnvioDesde"><img src="jsp/img/calendar.gif"/></a>
									</span>
									<span style="width: 20%;">	
										<label for="fechaEnvioHastaId"" class="literal">hasta</label>
										<spring:bind path="fechaEnvioHasta">
							    			<input type="text" tabindex="17" id="fechaEnvioHastaId" name="fechaEnvioHastaId" class="dato" size="11" maxlength="10"
							    			onblur="if (!ComprobarFecha(this, document.formIncidencias, 'Fecha Envio Hasta')) this.value='';" 
							    			value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fechaEnvioHastaId}" />"/>
							    		</spring:bind>
						    			<a id="btn_fechaEnvioHasta" name="btn_fechaEnvioHasta"><img src="jsp/img/calendar.gif"/></a>
									</span>
									<span style="width: 45%;">	
										<label for="tipoincidencia" class="literal"">Tipología</label>
										<form:select path="tipoinc" cssClass="dato" cssStyle="width:155px" tabindex="18" id="tipoinc" onchange="javascript:validarTipoInc();" >
										<form:option value="">Todos</form:option>
											<option value="I"<c:if test="${tipoincSel == 'I'}">selected</c:if>>Incidencia</option>
											<option value="R"<c:if test="${tipoincSel == 'R'}">selected</c:if>>Rescisión</option>
											<option value="A"<c:if test="${tipoincSel == 'A'}">selected</c:if>>Anulación</option>
										</form:select>
									</span>		
								</span>
							  	<span style="width:30%;">
							  		<span>
										<label for="linea" class="literal"">Estado Agroseguro</label>
										<form:select path="codestadoagro" cssClass="dato" cssStyle="width:155px" tabindex="19" id="codestadoagro">
											<form:option value="">Todos</form:option>
											<c:forEach var="i" begin="0" end="${fn:length(listaEstadosInc) - 1 }">
												<form:option value="${listaEstadosInc[i].codestado}">${listaEstadosInc[i].descripcion}</form:option>
											</c:forEach>
										</form:select>
									</span>									
								</span>
							</div>
					</div>
				</div>
			</fieldset>
		</form:form>
	
		<!-- Grid Jmesa --> 
		<div id="grid">
	  		${listaIncidenciasAgro}		  							               
		</div> 	
		
	</div>
	
	<form id="formLimpiar" name="formLimpiar" action="utilidadesIncidencias.run" method="post">
		<input type="hidden" name="method" id="method" value="doConsulta"/>
		<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
	</form>
	
	<form id="formEditar" name="formEditar" action="aportarDocIncidencia.run" method="post">
		<input type="hidden" name="method" id="method" value="doCargar"/>
		<input type="hidden" name="incidenciaId" id="incidenciaIdEditar"/>
		<input type="hidden" name="origenEnvio" id="origenEnvioEditar" value="baseDatos"/>
		<!-- Pet. 50775 * Resolución Incidencias (06.06.2018)-->
		<input type="hidden" name="origen" id="origen" value="editarInc" />
		<!-- (18.06.2018) -->
		<input type="hidden" name="idincidencia" id="idincidenciaEditar" />
		<input type="hidden" name="codentidad" id="entidadEditar" />
		<input type="hidden" name="oficina"id="oficinaEditar" />
		<input type="hidden" name="entmediadora" id="entmediadoraEditar" />
		<input type="hidden" name="subentmediadora" id="subentmediadoraEditar" />
		<input type="hidden" name="delegacion" id="delegacionEditar" />
		<input type="hidden" name="referencia" id="referenciaEditar" />
		<input type="hidden" name="numero" id="numeroEditar" />
		<input type="hidden" name="codplan" id="planEditar" />
		<input type="hidden" name="codlinea" id="lineaEditar" />
		<input type="hidden" name="codestado" id="codestadoEditar">
		<input type="hidden" name="codestadoagro" id="codestadoagroEditar">
		<input type="hidden" name="nifcif" id="nifcifEditar" />
		<input type="hidden" name="tiporef" id="tiporefEditar" />
		<input type="hidden" name="idcupon" id="idcuponEditar" />
		<input type="hidden" name="asunto" id="asuntoEditar" />
		<input type="hidden" name="fechaEnvioDesdeId" id="fechaEnvioDesdeIdEditar" />
   		<input type="hidden" name="fechaEnvioHastaId" id="fechaEnvioHastaIdEditar" />									    		
		<!-- (13.06.2018) -->
		<input type="hidden" name="tipoEnvio" id="tipoEnvio" value="${tipoEnvio}">
		<input type="hidden" name="opcionBusqueda" id="opcionBusqueda" value="${tipoBusqueda}">
        <input type="hidden" name="poliza_plan" id="poliza_plan" value="${plan}">
        <input type="hidden" name="plan" id="plan" value="${plan}">
        <input type="hidden" name="referencia" id="referencia" value="${referencia}">
        <input type="hidden" name="linea" id="linea" value="${linea}">
   	    <input type="hidden" name="nifcif" id="nifcif" value="${nifcif}">
		<!-- Pet. 50775 * Resolución Incidencias (06.06.2018)-->
		
		<input type="hidden" name="idincidenciaConsulta" value="${idInc}">
		<input type="hidden" name="planConsulta" value="${plan}">
		<input type="hidden" name="tipoincConsulta" value="${tipoinc}">
		
	</form>
	
	<form id="formConsultar" name="formConsultar" action="aportarDocIncidencia.run" method="post">
		<input type="hidden" name="method" id="method" value="doConsultar"/>
		<input type="hidden" name="origenEnvio" id="origenEnvioConsulta" value="agroseguro"/>
		<!-- Pet. 50775 * Resolución Incidencias (06.06.2018)-->
		<input type="hidden" name="origen" id="origen" value="consultaInc" />
		<!-- Pet. 50775 * Resolución Incidencias (06.06.2018)-->
		<input type="hidden" name="referencia" id="referenciaConsulta" />
		<input type="hidden" name="idincidencia" id="idincidenciaConsulta" />
		<input type="hidden" name="nifcif" id="nifcifConsulta" /> 
		
		<input type="hidden" name="codentidad" id="entidadConsulta" />
		<input type="hidden" name="oficina"id="oficinaConsulta" />
		<input type="hidden" name="entmediadora" id="entmediadoraConsulta" />
		<input type="hidden" name="subentmediadora" id="subentmediadoraConsulta" />
		<input type="hidden" name="delegacion" id="delegacionConsulta" />

		<input type="hidden" name="numero" id="numeroConsulta" />
		<input type="hidden" name="codplan" id="planConsulta" />
		<input type="hidden" name="codlinea" id="lineaConsulta" />
		<input type="hidden" name="codestado" id="codestadoConsulta">
		<input type="hidden" name="codestadoagro" id="codestadoagroConsulta">
		
		<input type="hidden" name="tiporef" id="tiporefConsulta" />
		<input type="hidden" name="idcupon" id="idcuponConsulta" />
		<input type="hidden" name="asunto" id="asuntoConsulta"  value="${asuntoInc.id.codasunto}"/>
		<input type="hidden" name="fechaEnvioDesdeId" id="fechaEnvioDesdeIdConsulta" />
   		<input type="hidden" name="fechaEnvioHastaId" id="fechaEnvioHastaIdConsulta" />	
   		<input type="hidden" name="codusuario"	id="codUsuarioConsulta" />	
        <input type="hidden" name="tipoinc"	id="tipoincConsulta" />   								    		
	</form>
	
	<!-- (05.07.2018) ** MODIF TAM  -->
	<form id="formConsListAgro" name="formConsListAgro" action="listaIncidenciasAgro.run" method="post">
		<input type="hidden" name="method" id="method" value="doCargar"/>
		<input type="hidden" name="idincidencia" id="idincidenciaConsList" />
		<input type="hidden" name="codentidad" id="entidadConsList" />
		<input type="hidden" name="oficina"id="oficinaConsList" />
		<input type="hidden" name="entmediadora" id="entmediadoraConsList" />
		<input type="hidden" name="subentmediadora" id="subentmediadoraConsList" />
		<input type="hidden" name="delegacion" id="delegacionConsList" />
		<input type="hidden" name="referencia" id="referenciaConsList" />
		<input type="hidden" name="numero" id="numeroConsList" />
		<input type="hidden" name="codplan" id="planConsList" />
		<input type="hidden" name="codlinea" id="lineaConsList" />
		<input type="hidden" name="codestado" id="codestadoConsList">
		<input type="hidden" name="codestadoagro" id="codestadoagroConsList">
		<input type="hidden" name="nifcif" id="nifcifConsList" />
		<input type="hidden" name="tiporef" id="tiporefConsList" />
		<input type="hidden" name="idcupon" id="idcuponConsList" />
		<input type="hidden" name="asunto" id="asuntoConsList" />
		<input type="hidden" name="fechaEnvioDesdeId" id="fechaEnvioDesdeIdConsList" />
   		<input type="hidden" name="fechaEnvioHastaId" id="fechaEnvioHastaIdConsList" />
   		<input type="hidden" name="tipoinc" id="tipoincConsList" />									    		
	</form>
	<!-- (05.07.2018) ** MODIF TAM - FIN -->
	
	<form id="formNuevaIncidencia" name="formNuevaIncidencia" action="aportarDocIncidencia.run" method="post">
		<input type="hidden" name="method" id="method" value="doCargar"/>
		<input type="hidden" name="origenEnvio" id="origenEnvio" value="baseDatos"/>
		<input type="hidden" name="origen" id="origen" value="altaInc"/>
		<input type="hidden" name="codentidad" id="codentidad" value=""/>
		<input type="hidden" name="referenciaCons" id="referenciaCons" value=""/>
		<input type="hidden" name="oficina" id="oficina" value=""/>
		<input type="hidden" name="entmediadora" id="entmediadora" value=""/>
		<input type="hidden" name="subentmediadora" id="subentmediadora" value=""/>
		<input type="hidden" name="delegacion" id="delegacion" value=""/>
		<input type="hidden" name="codplan" id="codplan" value=""/>
		<input type="hidden" name="codlinea" id="codlinea" value=""/>
		<input type="hidden" name="codestado" id="codestado" value=""/>
		<input type="hidden" name="codestadoagro" id="codestadoagro" value=""/>
		<input type="hidden" name="nifcifCons" id="nifcifCons" value=""/>
		<input type="hidden" name="tiporef" id="tiporef" value=""/>
		<input type="hidden" name="idcupon" id="idcupon" value=""/>
		<input type="hidden" name="asunto" id="asunto" value=""/>
		<input type="hidden" name="fechaEnvioDesdeId" id="fechaEnvioDesdeId" value=""/>
		<input type="hidden" name="fechaEnvioHastaId" id="fechaEnvioHastaId" value=""/>
		<input type="hidden" name="numIncidencia" id="numIncidencia" value=""/>
		<input type="hidden" name="codUsuarioVolver" id="codUsuarioVolver" value=""/>
		<input type="hidden" name="tipoincVolver" id="tipoincVolver" value=""/>
	</form>
	
	<form id="formConsulAnulyResc" name="formConsulAnulyResc"  action="anulacionyRescisionPol.run" method="post">
	    <input type="hidden" id="method" name="method" />
	    <input type="hidden" name="idincidenciaConsulta" id="idincidenciaConsulta" />
		<input type="hidden" name="origenEnvio" id="origenEnvioConsulta" value="agroseguro"/>
		<input type="hidden" name="origen" id="origen" value="consultaInc" /> 
		<input type="hidden" name="referenciaAyR" id="referenciaConAyR" />
		<input type="hidden" name="idincidenciaAyR" id="idincidenciaConAyR" />
		<input type="hidden" name="nifcifAyR" id="nifcifConAyR" /> 
		
		<input type="hidden" name="codentidadAyR" id="entidadConAyR" />
		<input type="hidden" name="oficinaAyR" id="oficinaConAyR" />
		<input type="hidden" name="entmediadoraAyR" id="entmediadoraConAyR" />
		<input type="hidden" name="subentmediadoraAyR" id="subentmediadoraConAyR" />
		<input type="hidden" name="delegacionAyR" id="delegacionConAyR" />

		<input type="hidden" name="numeroAyR" id="numeroConAyR" />
		<input type="hidden" name="codplanAyR" id="planConAyR" />
		<input type="hidden" name="codlineaAyR" id="lineaConAyR" />
		<input type="hidden" name="codestadoAyR" id="codestadoConAyR">
		<input type="hidden" name="codestadoagroAyR" id="codestadoagroConAyR">
		
		<input type="hidden" name="tiporefAyR" id="tiporefConAyR" />
		<input type="hidden" name="idcuponAyR" id="idcuponConAyR" />
		<input type="hidden" name="asuntoAyR" id="asuntoConAyR"  value="${asuntoInc.id.codasunto}"/>
		<input type="hidden" name="fechaEnvioDesdeIdAyR" id="fechaEnvioDesdeIdConAyR" />
   		<input type="hidden" name="fechaEnvioHastaIdAyR" id="fechaEnvioHastaIdConAyR" />	
   		<input type="hidden" name="codusuarioAyR"	id="codUsuarioConAyR" />	
        <input type="hidden" name="tipoincAyR"	id="tipoincConAyR" />   	
	</form>
	
	<!-- Grid Jmesa -->
		<div id="grid">${listadoSiniestros}</div>
		
		<!-- Formulario para exportar a excel el listado -->
		<form name="exportToExcel" id="exportToExcel"
			action="utilidadesIncidencias.run" method="post">
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