<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de Clases</title>
		
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
		<script type="text/javascript" src="jsp/moduloTaller/claseMto/claseMto.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
			 
			function cargarFiltro(){
					
				<c:forEach items="${sessionScope.consultaClaseMto_LIMIT.filterSet.filters}" var="filtro">
					
					<c:if test="${filtro.property == 'linea.codplan'}">
						$('#plan').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'linea.codlinea'}">
						$('#linea').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'clase'}">
						$('#clase').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'descripcion'}">
						$('#descripcion').val('${filtro.value}');
					</c:if>
					<c:if test="${filtro.property == 'maxpolizas'}">
						$('#maxpolizas').val('${filtro.value}');
					</c:if>		
					<c:if test="${filtro.property == 'comprobarAac'}">
						$('#comprobarAac').val('${filtro.value}');
					</c:if>			
					<c:if test="${filtro.property == 'rdtoHistorico'}">
						$('#rdtoHistorico').val('${filtro.value}');
					</c:if>	
					<c:if test="${filtro.property == 'comprobarRce'}">
						$('#comprobarRce').val('${filtro.value}');
					</c:if>	
				</c:forEach>
			}
			// DAA 25/01/2013    
			function cargaMenu(){
				<c:if test="${vieneDeCicloPoliza eq true}">
					SwitchMenu('sub3');
				</c:if>
			}

		</script>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="cargarFiltro();cargaMenu()">		
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		
		<c:choose>
			<c:when test="${vieneDeCicloPoliza eq true}">
			    <%@ include file="/jsp/common/static/menuGeneral.jsp"%>
				<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
			</c:when>
			<c:otherwise>
			    <%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
				<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
			</c:otherwise>
		</c:choose>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
							<c:choose>
								<c:when test="${vieneDeCicloPoliza eq true}">
									<a class="bot" id="btnCoberturas" href="javascript:coberturas('${usuario.colectivo.linea.lineaseguroid}')">Coberturas</a>
									<a class="bot" href="javascript:limpiarCarga();">Limpiar</a>		
								</c:when>
								<c:otherwise>	
									<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar();">Modificar</a>
									<a class="bot" id="btnReplicar" href="javascript:replicar()">Replicar</a>	
									<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>
									<a class="bot" href="javascript:limpiar();">Limpiar</a>				
								</c:otherwise>
							</c:choose>
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>							
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
		<c:choose>
			<c:when test="${vieneDeCicloPoliza eq true}">
			    <p class="titulopag" align="left">Carga de Clases</p>
			</c:when>
			<c:otherwise>
			    <p class="titulopag" align="left">Mantenimiento de Clases</p>
			</c:otherwise>
		</c:choose>
		
		
			
			<form:form name="main3" id="main3" action="claseMto.run" method="post" commandName="claseMtoBean">
				<form:hidden path="id" id="id" />
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
				
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="planreplica" id="planreplica" value="${planreplica}"/>
				<input type="hidden" name="lineareplica" id="lineareplica" value="${lineareplica}"/>
				<input type="hidden" name="desc_lineareplica" id="desc_lineareplica" value="${desc_lineareplica}"/>
				<input type="hidden" name="origenLlamada" id="origenLlamada" value="${origenLlamada}"/>
				<input type="hidden" name="vieneDeConsultar" id="vieneDeConsultar" value="true"/>
				<input type="hidden" name="vieneDeCicloPoliza" id="vieneDeCicloPoliza" value="${vieneDeCicloPoliza}"/>
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div style="padding:3px;width:97%;margin:0 auto;" >
					<fieldset>
					<legend class="literal" style="text-align: left;">Filtro</legend>
							<table style="margin: 0 auto;">
								<tr>
									<td class="literal">Plan</td>
									<td class="literal">
												<c:choose>
													<c:when test="${vieneDeCicloPoliza eq true}">
													    <form:input path="linea.codplan" size="4" maxlength="4" cssClass="dato" id="plan" tabindex="1" readonly="true"/>
													</c:when>
													<c:otherwise>
													    <form:input path="linea.codplan" size="4" maxlength="4" cssClass="dato" id="plan" tabindex="1"/>
													</c:otherwise>
												</c:choose>
									</td>
									
									<td class="literal" align="right">L&iacute;nea</td>
									<td class="literal" >
										<c:choose>
											<c:when test="${vieneDeCicloPoliza eq true}">
											    <form:input path="linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="2" readonly="true"/>
											    <form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
											</c:when>
											<c:otherwise>
											    <form:input path="linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
												<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
												<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />
											</c:otherwise>
										</c:choose>
									
																				
									</td>
								</tr>
								<tr>	
									<td class="literal">Clase</td>
									<td class="literal" colspan="3">
										<form:input path="clase" size="4" maxlength="3" cssClass="dato" id="clase" tabindex="3" onchange="javascript:lupas.limpiarCampos('descripcion');"/>
										<form:input path="descripcion" size="100" maxlength="100" cssClass="dato" id="descripcion" tabindex="4" onchange="this.value=this.value.toUpperCase();"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Clase','principio', '', '');" alt="Buscar Clase" title="Buscar Clase" />
									</td>
								</tr>
								<tr>	
									<td class="literal" valign ="top" colspan="6">Máximo de Pólizas por clase&nbsp;&nbsp;
									
										<form:input path="maxpolizas" size="3" maxlength="3" cssClass="dato" id="maxpolizas" tabindex="5" />
										
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Incluir Riesgo Cubierto Elegido&nbsp;&nbsp;
										
										<!-- comprobarAac -->
										<form:select path="comprobarRce" id="comprobarRce" cssClass="dato" cssStyle="width:65px" tabindex="6">								
											<form:option value="">Todos</form:option>
											<form:option value="S">Si</form:option>
											<form:option value="N">No</form:option>
										</form:select>
										
										
									</td>
								</tr>
								<tr>	
									<td class="literal" valign ="top" colspan="6">Comprobar autorización a la Contratación&nbsp;&nbsp;
										
										<form:select path="comprobarAac" id="comprobarAac" cssClass="dato" cssStyle="width:65px" tabindex="6">								
											<form:option value="">Todos</form:option>
											<form:option value="S">Si</form:option>
										</form:select>
										
										<c:choose>
											<c:when test="${vieneDeCicloPoliza== null || vieneDeCicloPoliza eq false}">
												
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Rdto. Histórico/Orientativo&nbsp;&nbsp;
												
												<form:select path="rdtoHistorico" id="rdtoHistorico" cssClass="dato" tabindex="7">								
													<form:option value="">Todos</form:option>
													<form:option value="0">No</form:option>
													<form:option value="1">Si</form:option>
												</form:select>
												
											</c:when>
										</c:choose>
									</td>
								</tr>
								
								
							</table>
					</fieldset>
				</div>
			</form:form>
			
			<form:form name="main" id="main" action="claseDetalle.run" method="post" commandName="claseDetalleBean">
				
				<form:hidden path="clase.id" id="detalleid"/>
				<form:hidden path="clase.linea.codplan" id="detalleplan"/>
				<form:hidden path="clase.linea.codlinea" id="detallelinea"/>
				<form:hidden path="clase.linea.lineaseguroid" id="detallelineaseguroid"/>
				<form:hidden path="clase.linea.esLineaGanadoCount" id="esLineaGanado"/>
				<form:hidden path="clase.clase" id="detalleclase"/>
				<form:hidden path="clase.descripcion" id="detalledesc"/>
				<input type="hidden" name="fechaInicioContratacion" id="fechaInicioContratacion" value=""/>
				
				
				
				<input type="hidden" name="method" id="method" value="doConsulta"/>
				<c:if test="${vieneDeCicloPoliza eq true}">
			    	<input type="hidden" name="origenLlamada" id="origenLlamada" value="cargaClases" />
			    	<input type="hidden" name="vieneDeCargaClases" id="vieneDeCargaClases" value="true" />
				</c:if>
				
			</form:form>
			
			<form:form name="mainGanado" id="mainGanado" action="claseDetalleGanado.run" method="post" commandName="claseDetalleGanadoBean">
				
				<form:hidden path="clase.id" id="detalleganadoid"/>
				<form:hidden path="clase.linea.codplan" id="detalleganadoplan"/>
				<form:hidden path="clase.linea.codlinea" id="detalleganadolinea"/>
				<form:hidden path="clase.linea.lineaseguroid" id="detalleganadolineaseguroid"/>
				<form:hidden path="clase.linea.esLineaGanadoCount" id="esLineaGanadoSG"/>
				<form:hidden path="clase.clase" id="detalleganadoclase"/>
				<form:hidden path="clase.descripcion" id="detalleganadodesc"/>
				<input type="hidden" name="fechaInicioContratacionGan" id="fechaInicioContratacionGan" value=""/>
				
				 <input type="hidden" name="method" id="methodSG" value="doConsulta"/>
				<c:if test="${vieneDeCicloPoliza eq true}">
			    	<input type="hidden" name="origenLlamada" id="origenLlamadaSG" value="cargaClases" />
			    	<input type="hidden" name="vieneDeCargaClases" id="vieneDeCargaClases" value="true" />
				</c:if>
				
			</form:form>
			
			<!-- Grid Jmesa -->
			<div id="grid" style="width: 90%; margin:0 auto;">
				 ${consultaClaseMto}
			</div>
	</div>
		
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<!--               -->
		<!-- POPUPS AVISO  -->
		<!--               -->
		
		<!--  *** panel avisos ***  -->
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
		<%@ include file="/jsp/common/lupas/lupaLineaReplicar.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaClase.jsp"%>

	</body>
</html>
