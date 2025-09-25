<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de sobreprecio - Fechas de contratación</title>
		
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
		<script type="text/javascript" src="jsp/moduloSbp/mantenimiento/fechasContratacion.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
		function cargarFiltro(){
			
			
			<c:forEach items="${sessionScope.consultaFechasContratacion_LIMIT.filterSet.filters}" var="filtro">
			
				if ('${filtro.property}' == 'linea.codplan'){
					var inputText = document.getElementById('plan');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'linea.codlinea'){
					var inputText = document.getElementById('linea');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'cultivo'){
					var inputText = document.getElementById('cultivo');
					inputText.value = '${filtro.value}';	
				}else if ('${filtro.property}' == 'fechainicio'){
					var inputText = document.getElementById('fechainicio');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'fechafin'){
					var inputText = document.getElementById('fechafin');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'fechaFinGarantia'){
					var inputText = document.getElementById('fechaFinGarantia');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'fechaFinSuplementos'){
					var inputText = document.getElementById('fechaFinSuplementos');
					inputText.value = '${filtro.value}';
				}else{
					//var inputText = document.getElementById('${filtro.property}');
					//inputText.value = '${filtro.value}';
				}
			</c:forEach>
		}
		</script>
	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchSubMenu('sub10','sub8');cargarFiltro();">		
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
					
							<c:if test="${showModificar == 'true'}">	
								<a class="bot" id="btnModificar"  href="javascript:modificar();">Modificar</a>
							</c:if>	
							<c:if test="${showModificar != 'true'}">	
								<a class="bot" id="btnModificar" style="display:none"  href="javascript:modificar();">Modificar</a>
							</c:if>
							<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>				
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
		<form name="limpiar" id="limpiar" action="periodoContSbp.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>								
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
		</form>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
		
		<p class="titulopag" align="left">Fechas de contrataci&oacute;n</p>
			
			<form:form name="main3" id="main3" action="periodoContSbp.run" method="post" commandName="fechaContratacionSbp">
				<input type="hidden" name="method" id="method" />
				<!-- input type="hidden" name="idFechaContatacionSbp" id="idFechaContatacionSbp"/-->
				<input type="hidden" name="origenLlamada" id="origenLlamada" />
				<input type="hidden" name="showModificar" id="showModificar" />
				
				<input type="hidden" name="fechaI.day" value="">
				<input type="hidden" name="fechaI.month" value="">
				<input type="hidden" name="fechaI.year" value="">
				
				<input type="hidden" name="fechaF.day" value="">
				<input type="hidden" name="fechaF.month" value="">
				<input type="hidden" name="fechaF.year" value="">
				
				<input type="hidden" name="fechaFinG.day" value="">
				<input type="hidden" name="fechaFinG.month" value="">
				<input type="hidden" name="fechaFinG.year" value="">
				
				<input type="hidden" name="fechaFinS.day" value="">
				<input type="hidden" name="fechaFinS.month" value="">
				<input type="hidden" name="fechaFinS.year" value="">
				<form:hidden path="id" id="id" />
				
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				
				<div style="width:97%;margin:0 auto;">
					<fieldset>
					<legend class="literal">Filtro</legend>
							<table align="left" style="margin:0 auto;">
								<tr align="left">
									<td class="literal">Plan</td>
									<td class="literal">
										<form:input path="linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan" tabindex="1" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');"/>
									</td>
									<td class="literal">L&iacute;nea</td>
									<td class="literal">
										<form:input path="linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
										<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="30" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />										
									</td>
									<td class="literal">Cultivo</td>
									<td class="literal">
										<form:input  id="cultivo" path="cultivo.id.codcultivo" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_cultivo');" tabindex="3"/>
										<form:input cssClass="dato" path="cultivo.descultivo" id="desc_cultivo" size="25" readonly="true"/>
										<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('CultivoSbp','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
									</td>
								</tr>
							</table>
							<table align="left" style="margin:0 auto;">
								<tr>
									<td class="literal">Inicio de contratación</td>
									<td class="literal">
									
					                    <spring:bind path="fechainicio">
					                    	 <input type="text" name="fechainicio" id="fechainicio" size="8" maxlength="10" class="dato" tabindex="12"
					                    	 		onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Inicio de Contratación')) this.value=''"
													value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fechaContratacionSbp.fechainicio}" />" />
					                    </spring:bind>
				 
				                     <input type="button" id="btn_fechaInicio" name="btn_fechaInicio" class="miniCalendario" style="cursor: pointer;" /> 
			                         </td>
			                         <td class="literal">Final de contratación</td>
			                         <td class="literal">
					                    <spring:bind path="fechafin">
					                    	 <input type="text" name="fechafin" id="fechafin" size="8" maxlength="10" class="dato" tabindex="12"
					                    	 		onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Final de contratación')) this.value=''"
													value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fechaContratacionSbp.fechafin}" />" />
					                    </spring:bind>
				 
				                     <input type="button" id="btn_fechaFin" name="btn_fechaFin" class="miniCalendario" style="cursor: pointer;" /> 
			                         </td>
								
						 	     	 <td class="literal">Fin de Garantía</td>
			                         <td class="literal">
					                    <spring:bind path="fechaFinGarantia">
					                    	 <input type="text" name="fechaFinGarantia" id="fechaFinGarantia" size="8" maxlength="10" class="dato" tabindex="13"
					                    	 		onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha Fin Garantia')) this.value=''"
													value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fechaContratacionSbp.fechaFinGarantia}" />" />
					                    </spring:bind>
				 
				                     <input type="button" id="btn_fechaFinGarantia" name="btn_fechaFinGarantia" class="miniCalendario" style="cursor: pointer;" /> 
			                         </td>
			                         
			                         <td class="literal">Fin de envío de Suplementos</td>
			                         <td class="literal">
					                    <spring:bind path="fechaFinSuplementos">
					                    	 <input type="text" name="fechaFinSuplementos" id="fechaFinSuplementos" size="8" maxlength="10" class="dato" tabindex="14"
					                    	 		onblur="limpiaAlertas(); if (!ComprobarFecha(this, document.main3, 'Fecha Fin Suplementos')) this.value=''"
													value="<fmt:formatDate pattern="dd/MM/yyyy" value="${fechaContratacionSbp.fechaFinSuplementos}" />" />
					                    </spring:bind>
				 
				                     <input type="button" id="btn_fechaFinSuplementos" name="btn_fechaFinSuplementos" class="miniCalendario" style="cursor: pointer;" /> 
			                         </td>					
								</tr>
								
							</table>
					</fieldset>
				</div>
			</form:form>
			
			<!-- Grid Jmesa -->
			<div id="grid" style="width: 80%;margin:0 auto;">
				 ${consultaFechasContratacionSbp}		 
			      	        
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
		
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
		<%@ include file="/jsp/common/lupas/lupaCultivoSbp.jsp"%>
	</body>
</html>