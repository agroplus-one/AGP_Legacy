<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Selecci&oacute;n de p&oacute;lizas para sobreprecio</title>
		
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
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloSbp/consultaSbp/consultaPolizasSbp.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
		
		
			$(document).ready(function(){
			
		        <c:if test="${perfil == 0 || perfil == 5}">
					document.getElementById("entidad").focus(); 		
				</c:if>
		        <c:if test="${perfil > 0 && perfil < 5}">
		     
					document.getElementById("codusuario").focus(); 						
				</c:if>   
				<c:if test="${perfil == 1}">
				
					
					document.getElementById("oficina").focus();
				</c:if>
				
				<c:if test="${empty listaPolizas}">					
					$('#divImprimir').hide(); 									
				</c:if> 				
			});
		    

			function cargarFiltro(){
			<c:if test="${origenLlamada != 'menuGeneral'}">	
				<c:forEach items="${sessionScope.consultaPolizasSbp_LIMIT.filterSet.filters}" var="filtro">
						if ('${filtro.property}' == 'colectivo.tomador.id.codentidad'){
							var inputText = document.getElementById('entidad');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'oficina'){
							var inputText = document.getElementById('oficina');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'usuario.delegacion'){	
							var inputText = document.getElementById('delegacion');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'colectivo.subentidadMediadora.id.codentidad'){
							var inputText = document.getElementById('entmediadora');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'colectivo.subentidadMediadora.id.codsubentidad'){
							var inputText = document.getElementById('subentmediadora');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'linea.codplan'){
							var inputText = document.getElementById('plan');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'linea.codlinea'){
							var inputText = document.getElementById('linea');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'clase'){
							var inputText = document.getElementById('clase');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'colectivo.idcolectivo'){
							var inputText = document.getElementById('colectivo');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'codmodulo'){
							var inputSelect = document.getElementById('modulo');
							inputSelect.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'estadoPoliza.idestado'){
							var selectOption = document.getElementById('estadoP');
							selectOption.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'usuario.codusuario'){
							var inputText = document.getElementById('codusuario');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'asegurado.nifcif'){
							var inputText = document.getElementById('nifCif');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'asegurado.nombre'){
							var inputText = document.getElementById('nombreAseg');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'referencia'){
							var inputText = document.getElementById('referencia');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'fechaenvio'){
							var inputText = document.getElementById('fecEnvioId');
							inputText.value = '${filtro.value}';
						}else if ('${filtro.property}' == 'id'){
						}else{
							//var inputText = document.getElementById('${filtro.property}');
							//inputText.value = '${filtro.value}';
						}
				</c:forEach>
			</c:if>
		}
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchMenu('sub8');cargarFiltro();">		
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons">
			<table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
							<c:if test="${origenLlamada == 'menuGeneral'}">
								<a class="bot" id="btnConsultar" href="javascript:consultarInicial();">Consultar</a>
							</c:if>
							<c:if test="${origenLlamada != 'menuGeneral'}">
								<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							</c:if>
							<a class="bot" href="javascript:limpiar();">Limpiar</a>
							<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<form name="limpiar" id="limpiar" action="consultaPolSbp.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>								
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
		</form>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:103%">
		<p class="titulopag" align="left">Selecci&oacute;n de p&oacute;lizas para sobreprecio</p>
			
			<form name="altaPolizaSbp" id="altaPolizaSbp" action="simulacionSbp.html" method="post" commandName="polizaSbp">	
				<input type="hidden" name="method" id="method"/>
				<form:hidden path="polizaSbp.polizaPpal.idpoliza" id="idPolizaPpal"/>	
				<form:hidden path="polizaSbp.polizaCpl.idpoliza" id="idPolizaCpl"/>
				<form:hidden path="polizaSbp.polizaPpal.estadoPoliza.idestado" id="idEstadoPpal"/>	
				<form:hidden path="polizaSbp.polizaCpl.estadoPoliza.idestado" id="idEstadoCpl"/>
				<form:hidden path="polizaSbp.incSbpComp" id="incSbpComp"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="consultaPolizasParaSbp"/>
			</form>
			
			<form name="printBorrador" id="printBorrador" action="informes.html" method="post">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idPoliza" id="idPolizaPrint"/>
				<input type="hidden" name="StrImprimirReducida" id="StrImprimirReducida" />
			</form>
			
			<form name="ListadoPolSbpForm" id="ListadoPolSbpForm" action="consultaPolizaSbp.run" method="post">
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="idPolizaSbp" id="idPolizaSbp"/>
				<input type="hidden" name="codPlan" id="codPlan"/>
				<input type="hidden" name="codLinea" id=codLinea/>
				<input type="hidden" name="referenciaSbp" id="referenciaSbp"/>
				<input type="hidden" name="idPolizaSeleccion" id="idPolizaSeleccion" value="${idPolizaSeleccion}"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="consultaPolizasParaSbp"/>
			</form>
			
			<form name="simulacionForm" id="simulacionForm" action="simulacionSbp.html" method="post">
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="idPolSbp" id="idPolSbp"/>
				<input type="hidden" name="idPolizaPpal" id="idPolizaPpal"/>	
				<input type="hidden" name="idPolizaCpl" id="idPolizaCpl"/>
				<input type="hidden" name="idEstadoPpal" id="idEstadoPpal"/>	
				<input type="hidden" name="idEstadoCpl" id="idEstadoCpl"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="consultaPolizasParaSbp"/>
				<input type="hidden" name="recogerPolSesion" id="recogerPolSesion" value="true"/>
			</form>
			
			<form name="print" id="print" action="consultaPolSbp.run" method="post">
				<input type="hidden" name="method" id="method"/>
				<input type="hidden" name="idPoliza" id="idPoliza"/>
			</form>
			
			
			
			<form:form name="polizaActualizada" id="polizaActualizada" action="polizaActualizada.html" method="post" commandName="anexMod" target="_blank" >
			<input type="hidden" id="method" name="method" value="doVerPolizaActualizada" />
			<form:hidden path="poliza.referencia" id="refPolizaPlzAct"/>
			<form:hidden path="poliza.linea.codplan" id="codPlanPlzAct"/>
			<form:hidden path="poliza.tipoReferencia" id="tipoRefPlzAct"/>
			</form:form>
			
			<form:form name="main3" id="main3" action="consultaPolSbp.run" method="post" commandName="polizaBean">
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid"/>
				<input type="hidden" name="filtro" id="filtro" value=""/>
				<form:hidden path="idenvio" id="idenvio"/>
				<input type="hidden" name="accion" value=""/>
				<input type="hidden" name="operacion" id="operacion"  value=""/>
				<input type="hidden" name="polizaOperacion" value=""/>				
				<input type="hidden" name="perfil" value="${perfil}"/>
				<input type="hidden" name="oficina2" id="sucursal"/>
				<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
				<input type="hidden" name="listCheck" id="listCheck" value=""/>
				<input type="hidden" name="listGrabDefPolizas" id="listGrabDefPolizas" value=""/>
				<input type="hidden" name="fecEnvioId.day" value=""/> 
	            <input type="hidden" name="fecEnvioId.month" value=""/> 
	            <input type="hidden" name="fecEnvioId.year" value=""/>
	            <input type="hidden" name="resultadoValidacion" value=""/>
				<input type="hidden" name="formato" value=""/> 
				<input type="hidden" name="idPoliza" id="idPolizaDelete"/>
				<input type="hidden" name="listBorradoPolizas" id="listBorradoPolizas"     value=""/>
				<input type="hidden" name="accionesSobrePolizas" id="accionesSobrePolizas" value="true"/>
				<input type="hidden" name="idsRowsChecked" id="idsRowsChecked" value="${idsRowsChecked}"/>
				<input type="hidden" name="displaypopUpAmbCont" id="displaypopUpAmbCont" value="${popUpAmbiCont}" />
				<input type="hidden" name="grabFueraContratacion" id="grabFueraContratacion"  value=""/>
				<input type="hidden" name="usuarioNuevo" id="usuarioNuevo" value=""/>
		        <input type="hidden" name="nomLinea" id="nomLinea" value="${nomLinea}"/>
		        <input type="hidden" name="listaLineasSbp" id="listaLineasSbp" value="${listaLineasSbp}"/>
		        <input type="hidden" name="codEnt" id="codEnt" value="${usuario.oficina.entidad.codentidad}"/>
		        <input type="hidden" name="descEnt" id="descEnt" value="${usuario.oficina.entidad.nomentidad}"/>
		        <input type="hidden" name="codOfi" id="codOfi" value="${usuario.oficina.id.codoficina}"/>
		        <input type="hidden" name="filtroPlan" id="filtroPlan" value="${filtroPlan}"/>
		        <input type="hidden" name="origenLlamada" id="origenLlamada" />
		        <input type="hidden" name="filtroUsuario" id="filtroUsuario" value="${filtroUsuario}"/>
		        <input type="hidden" name="grupoOficinas" id="grupoOficinas" value="${grupoOficinas}"/>
		        
		        <input type="hidden" id="externo" value="${externo}" />
				<input type="hidden" name="entMediadora" id="entMediadora" value="${entMediadora}"/>
				<input type="hidden" name="subEntmediadora" id="subEntmediadora" value="${subEntmediadora}"/>
				<input type="hidden" name="deleg" id="deleg" value="${deleg}"/>
		      		        
		        <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div style="background-color: #FFFFFF; font-family: tahoma,arial,verdana; font-size: 11px; color: #666666; text-align: left; vertical-align: top; padding-left: 5px; padding-top: 2px;">
					<fieldset>
					<legend class="literal">Filtro</legend>
						<table>
							<tr>
								<table>
									<tr>
										<td class="literal">Entidad
											<c:if test="${perfil == 0 || perfil == 5}">
												<form:input path="colectivo.tomador.id.codentidad" size="3" maxlength="4" cssClass="dato" id="entidad" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_entidad', 'oficina', 'desc_oficina');"/>
												<form:input path="colectivo.tomador.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="25" readonly="true"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Entidad','principio', '', '');" alt="Buscar Entidad" title="Buscar Entidad" />
											</c:if>
											<c:if test="${perfil > 0 && perfil < 5}">
												<form:input path="colectivo.tomador.id.codentidad" size="3" maxlength="4" cssClass="dato" disabled="disabled" readonly="true" id="entidad" tabindex="1"/>
												<form:input path="colectivo.tomador.entidad.nomentidad" cssClass="dato"	id="desc_entidad" size="25" readonly="true"/>
											</c:if>
										</td>
										<td class="literal">Oficina
											<c:if test="${perfil == 0 || perfil == 1 || perfil == 2 || perfil == 5}">
												<form:input path="oficina" size="4" maxlength="3" cssClass="dato" id="oficina" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_oficina');"/>
												<form:input path="nombreOfi" cssClass="dato" id="desc_oficina" size="18" readonly="true"/>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Oficina','principio', '', '');" alt="Buscar Oficina" title="Buscar Oficina" />
											</c:if>
											<c:if test="${(perfil > 2 && perfil < 5)}">
											
												<form:input path="oficina" size="4" maxlength="3" cssClass="dato" readonly="true" id="oficina" tabindex="2"/>
												<form:input path="nombreOfi" cssClass="dato"	id="desc_oficina" size="18" readonly="true"/>
											</c:if>	
										</td>
										<!-- Pet. 63473 ** MODIF TAM (20/12/2021) ** Inicio  -->
										<td class="literal">E-S Med
											<c:if test="${externo == 0 and perfil !=4 }"> <!--  es interno -->
												<form:input	path="colectivo.subentidadMediadora.id.codentidad" size="3" maxlength="4" tabindex="3" 	cssClass="dato" id="entmediadora" onchange="javascript:lupas.limpiarCampos('subentmediadora');UTIL.subStrEntidad();" />
												<form:input	path="colectivo.subentidadMediadora.id.codsubentidad" size="3" maxlength="4" tabindex="4" cssClass="dato" id="subentmediadora"/>
												<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:UTIL.subStrEntidad();lupas.muestraTabla('SubentidadMediadoraFiltroFecha','principio', '', '');"	alt="Buscar SubEntidad Mediadora" title="Buscar SubEntidad Mediadora" />
											</c:if>
											<c:if test="${externo == 1 or (externo==0 and perfil==4)}"> <!--  es externo -->
												<form:input	path="colectivo.subentidadMediadora.id.codentidad" size="3" maxlength="4"	cssClass="dato" id="entmediadora" tabindex="3"  readonly="true"/>
												<form:input	path="colectivo.subentidadMediadora.id.codsubentidad" size="3" maxlength="4" cssClass="dato" id="subentmediadora"  tabindex="4" readonly="true"/>
											</c:if>
										</td>	
										<td class="literal">&nbspDelegación
										
											<c:if test="${externo == 1}">
												<!--  es externo -->
												<c:if test="${perfil == 1}">
													<form:input	path="usuario.delegacion" size="3" maxlength="4" cssClass="dato" id="delegacion" tabindex="5"  />
												</c:if>
												<c:if test="${perfil == 3}">
													<form:input	path="usuario.delegacion" size="3" maxlength="4" cssClass="dato" id="delegacion" readonly="true"/>
												</c:if>
											</c:if> 
											<c:if test="${externo == 0}">
												<!--  es interno -->
												<form:input	path="usuario.delegacion" size="3" maxlength="4" cssClass="dato" id="delegacion" />
											</c:if>
										</td>
										<td class="literal">Usuario
											<c:if test="${perfil != 4}">
												<form:input path="usuario.codusuario" id="codusuario" size="8" maxlength="19" cssClass="dato" tabindex="3" onchange="this.value=this.value.toUpperCase();"/>
											</c:if>
											<c:if test="${perfil == 4}">
												<form:input path="usuario.codusuario" id="codusuario" size="8" maxlength="19" cssClass="dato" readonly="true" tabindex="3"/>
											</c:if>
										</td>
									<!-- Pet. 63473 ** MODIF TAM (20/12/2021) ** Fin  -->
									</tr>
								</table>
							</tr>
							<tr>
								<table>
									<tr>
										<td class="literal">Plan
											<td class="literal">
												<form:input path="linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan" tabindex="4" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');"/>
											</td>
										</td>	
										<td class="literal">L&iacute;nea
											<td class="literal" >
												<form:input path="linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="5" onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
												<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('LineaSbp','principio', '', '');" alt="Buscar Línea Sobreprecio" title="Buscar Línea Sobreprecio" />							
											</td>
										</td>	
										<td class="literal">Clase
											<td class="literal">
											  	<form:input path="clase" size="4" maxlength="3" cssClass="dato" id="clase" tabindex="6" />
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ClaseSbp','principio', '', '');" alt="Buscar Clase" title="Buscar Clase" />							
											</td>
										</td>
									</tr>
								</table>
							</tr>
							<tr>
								<table>
									<tr>
										<td class="literal">P&oacute;liza
											<td class="literal">
												<form:input path="referencia" id="referencia" size="15" maxlength="15" cssClass="dato" tabindex="8" onchange="this.value=this.value.toUpperCase();"/>
											</td>
										</td>	
										<td class="literal">Colectivo
											<td class="literal">
												<form:input path="colectivo.idcolectivo" id="colectivo" size="11" maxlength="9" cssClass="dato" tabindex="9"/>
											</td>
										</td>	
										<td class="literal">M&oacute;dulo
											<td class="literal">
												<form:input path="codmodulo" id="modulo" size="5" maxlength="5" cssClass="dato" tabindex="11" onchange="this.value=this.value.toUpperCase();"/>
											</td>
										</td>	
										<td class="literal">F.Envío
											<td class="literal">
							                    <spring:bind path="fechaenvio">
							                    	 <input type="text" name="fechaenvio" id="fecEnvioId" size="11" maxlength="10" class="dato" tabindex="12"
							                    	        onchange="if (!ComprobarFecha(this, document.main3, 'Fecha envio')) this.value='';"
															value="<fmt:formatDate pattern="dd/MM/yyyy" value="${polizaBean.fechaenvio}" />" />
							                    </spring:bind>
						 
						                     <input type="button" id="btn_fechaenvio" name="btn_fechaenvio" class="miniCalendario" style="cursor: pointer;" /> 
					                         </td>
					                    </td>     
									</tr>
								</table>
							</tr>
							<tr>
								<table>
									<tr>
										<td class="literal">CIF/NIF <br>Asegurado
											<td class="literal">
												<form:input path="asegurado.nifcif" id="nifcif" size="25" maxlength="9" cssClass="dato" tabindex="15" onchange="this.value=this.value.toUpperCase();this.value=generales.lpadNIF(this.value);"/>
											</td>
										</td>
										<td class="literal">Nombre <br>asegurado
											<td class="literal">
												<form:input path="asegurado.nombre" id="nombreAseg" size="25" maxlength="39" cssClass="dato" tabindex="16" onchange="this.value=this.value.toUpperCase();"/>&nbsp;&nbsp;
											</td>
										</td>	
										<td class="literal">Estado
											<td class="literal" width="205px">
												<form:select path="estadoPoliza.idestado" id="estadoP" cssClass="dato" tabindex="17" cssStyle="width:200px">
													<form:option value="">Todos</form:option>
													<c:forEach items="${estados}" var="estado">
														<form:option value="${estado.idestado}">${estado.descEstado}</form:option>
													</c:forEach>
												</form:select>
											</td>
										</td>	
									</tr>
								</table>
							</tr>		
						</table>
					</fieldset>
				</div>			
				
			</form:form>
		<jmesa:tableModel rowFilter="com.rsi.agp.core.jmesa.filter.CustomSimpleRowFilter">
		<!-- Grid Jmesa -->
		<div id="grid">
		  ${consultaPolizasSbp}
	        <div style="width:20%;text-align:center" id="divImprimir">
	        	 <a style="font-family: tahoma, verdana, arial;color: #626262;font-size:11px;">Imprimir</a>	
				 <a id="btnImprimirPdf" style="text-decoration:none;" href="javascript:imprimir(${totalListSize}, 'pdf')">
				 	<img src="jsp/img/jmesa/pdf.gif"/>
				 </a>
				 <a id="btnImprimirExcel" style="text-decoration:none;" href="javascript:imprimir(${totalListSize}, 'xls')">
				 	<img src="jsp/img/jmesa/excel.gif"/>
				 </a>
			</div>
		</div>   
</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		
		<!--               -->
		<!-- POPUPS AVISO  -->
		<!--               -->
		
		<!--  *** PopUp Alta Sbp ***  -->
		<div id="popUpAltaSbp" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Alta de Sobreprecio
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="hidePopUpAviso('popUpAltaSbp')">x</span>
				        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
						<div id="panelInformacion" class="panelInformacion">
							<div id="txt_mensaje_eleccionCplEnSbp">sin mensaje.</div>
						</div>
						<div style="margin-top:15px">
							<a class="bot" id="btn_aceptarGrabacionDefinitiva" href="javascript:alta_eleccion_Si()" title="Si incluir">SI</a>
						    <a class="bot" href="javascript:alta_eleccion_No()" title="No incluir">NO</a>
						</div>
			 </div>
		</div>
		
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
						<div id="txt_info_gp" style="width: 70%;display:none" >Existen pólizas elegidas con estado distinto a Grabación Provisional o Pendiente Validación</div>
						<div id="txt_info_none" style="width: 70%;display:none" >No hay pólizas seleccionadas.</div>
					</div>
			</div>
		</div>
			
		<!-- *** popUpAvisos *** -->
		<div id="popUpAvisos" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;">
		     <!--  header popup -->
			 <div id="header-popup" style="padding:0.4em 1em;position:relative;color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;background:#525583;height:15px">
				        <div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
				            Aviso
				        </div>
				        <a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
				                  font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				            <span onclick="hidePopUpAviso('popUpPasarDefinitivaBoton')">x</span>
				        </a>
			 </div>
			 <!--  body popup -->
			 <div class="panelInformacion_content">
						<div id="panelInformacion" class="panelInformacion">
							<div id="txt_mensaje_aviso_1">sin mensaje.</div>
						</div>
						<div style="margin-top:15px">
						    <a class="bot" href="javascript:hidePopUpAviso('popUpPasarDefinitivaBoton')" title="Cancelar">Cancelar</a>
						    <a class="bot" id="btn_aceptarGrabacionDefinitiva" href="javascript:aceptarPopUpPasoDefinitiva('popUpPasarDefinitivaBoton')" title="Cancelar">Aceptar</a>
						</div>
			 </div>
		</div>
		
		<%@ include file="/jsp/common/lupas/lupaEntidad.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaOficina.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLineaSbp.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaClaseSbp.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaSubentidadMediadoraFiltroFecha.jsp"%>
		
	</body>
</html>