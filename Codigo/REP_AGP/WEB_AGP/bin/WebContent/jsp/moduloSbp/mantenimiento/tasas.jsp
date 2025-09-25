<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de sobreprecio - Tasas</title>
		
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
		<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	    <script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.filestyle.js"></script>
		<script type="text/javascript" src="jsp/moduloSbp/mantenimiento/tasas.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>

	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub10','sub8');bloqueaInputs();">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
		<!-- botones de la página -->
		<div id="buttons" >
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
							<a class="bot" id="btnImportar" href="javascript:importar()">Importar</a>
							<a class="bot" id="btnReplicar" href="javascript:replicar()">Replicar</a>
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
		<form name="limpiar" id="limpiar" action="tasasSbp.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>								
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
		</form>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:103%" >
		<p class="titulopag" align="left">Tasas de Sobreprecio</p>					
			
			<form:form name="main3" id="main3" action="tasasSbp.run" method="post" commandName="tasaSbpBean">
				<form:hidden path="id" id="id"/>
				<form:hidden path="linea.lineaseguroid" id="lineaseguroid"/>
					            
	            <input type="hidden" name="method" id="method" />	            								
	            <!--  input type="hidden" name="origenLlamada" id="origenLlamada" value="consultaPolizasParaSbp"/ -->
				<input type="hidden" name="origenLlamada" id="origenLlamada" />
				<input type="hidden" name="planreplica" id="planreplica" value="${planreplica}"/>
				<input type="hidden" name="lineareplica" id="lineareplica" value="${lineareplica}"/>
				<input type="hidden" name="desc_lineareplica" id="desc_lineareplica" value="${desc_lineareplica}"/>
				
		        <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div style="width:100%;margin:0 auto">
					<fieldset>
					<legend class="literal">Filtro</legend>
						<table style="margin:0 auto;">
							<tr align="left">
								<td class="literal">Plan</td>
								<td class="literal">
									<form:input path="linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan" tabindex="1" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');"/>
								</td>
								<td class="literal">L&iacute;nea</td>
								<td class="literal" colspan="3">
									<form:input path="linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
									<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="30" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />							
								</td>
								<td class="literal">Provincia</td>
								<td class="literal">
									<form:input  id="provincia" path="comarca.id.codprovincia" cssClass="dato" size="2" maxlength="2" tabindex="1" onchange="javascript:lupas.limpiarCampos('desc_provincia', 'comarca', 'desc_comarca');"/>
									<input class="dato"	id="desc_provincia" name="desc_provincia" size="20" readonly="readonly"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');"	alt="Buscar Provincia" title="Buscar Provincia"/>	
								</td>									
							</tr>
							<tr align="left">
								<td class="literal">Comarca</td>
								<td class="literal">
									<form:input path="comarca.id.codcomarca" size="3" maxlength="2" cssClass="dato" id="comarca"  onchange="javascript:lupas.limpiarCampos('desc_comarca');" tabindex="1" /> 
									<form:input path="" cssClass="dato"	id="desc_comarca" size="18" readonly="true"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');" alt="Buscar Comarca" title="Buscar Comarca" />
									<label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_comarca"> *</label>
								</td>
								<td class="literal">Cultivo</td>
								<td colspan="1" class="literal">
									<form:input  id="cultivo" path="cultivo.id.codcultivo" cssClass="dato" size="3" maxlength="3" onchange="javascript:lupas.limpiarCampos('desc_cultivo');" tabindex="1"/>
									<form:input cssClass="dato" path="" id="desc_cultivo" size="18" readonly="true"/>
									<img src="jsp/img/magnifier.png"style="cursor: hand;" onclick="javascript:lupas.muestraTabla('CultivoSbp','principio', '', '');"	alt="Buscar Cultivo" title="Buscar Cultivo" />
								    <label class="campoObligatorio" title="Campo obligatorio" id="campoObligatorio_cultivo"> *</label>
								</td>
								<td class="literal">Tasa de incendio</td>
								<td class="literal">
									<form:input path="tasaIncendio" size="9" maxlength="9" cssClass="dato" id="tasaIncendio" tabindex="1"/>
								</td>
								<td class="literal">Tasa de pedrisco</td>
								<td class="literal">
									<form:input path="tasaPedrisco" size="9" maxlength="9" cssClass="dato" id="tasaPedrisco" tabindex="1"/>
								</td>						
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			
			<!-- Grid Jmesa -->
			<div id="grid">
		  		${tasasSbp}		  							               
			</div> 	
	</div>
			
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<!-- 	DAA 26/04/2013 		   -->	
	<!-- POPUP Importar Tasas Sbp  -->
	<form:form name="frmImportar" id="frmImportar" action="tasasSbp.run" method="post" commandName="tasaSbpBean" enctype="multipart/form-data">
	
		<input type="hidden" name="method" id="methodImportar" value ="doImportar"/>		
	
		<div id="divImportarTasas" class="wrapper_popup" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;top: 170px">
				<div id="header-popup" style="padding:0.4em 1em;position:relative; color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px; background:#525583;height:15px">
						<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
							Importar tasas de Sobreprecio
						</div>
						<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px; font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
							<span onclick="cerrarPopUpImportarTasas()">x</span>
						</a>
				</div>
				<div class="panelInformacion_content">
						<div style="height:30px">
							<div id="panelAlertasValidacionFile" name="panelAlertasValidacionFile" class="errorForm_cm" ></div>
						</div>
						<div id="tablaInformacion" class="panelInformacion" style="text-align:center">
							<input type="file" class="dato" id="file" name="file" onchange="limpiaPanelAlertasValidacion();"/>
						</div>	
						<div style="margin-top:15px;clear: both">
							 <a class="bot" href="javascript:cerrarPopUpImportarTasas()">Cancelar</a>
							 <a class="bot" href="javascript:doImportar()">Importar</a>
						</div>
				</div>
		</div>
	</form:form>
	
	
	<!--               -->
	<!-- POPUPS AVISO  -->
	<!--               -->
	
	<!-- *** popUp detalle Errores *** -->
	<div id="divMensajeError" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;left: 30%;">
		
		<div id="header-popup" style="padding:0.4em 1em;position:relative;
		   				              color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px;
						              background:#525583;height:15px">
			<div style="float:left;margin:0 0 0 0;font-size:11px;line-height:15px">
			 	Detalle del mensaje
			</div>
			<a style="height:18px;margin:-10px 0 0;padding:1px;position:absolute;right:0.3em;top:50%;width:19px;
			          font-family:arial;font-size:13px;font-weight:bold;cursor:hand;cursor:pointer">
				<span onclick="cerrarPopUp()">x</span>
			</a>
		</div>
	<!--  body popup -->
		<div class="panelInformacion_content">
			<div id="panelInformacion" class="panelInformacion">
				<div id="mensajeError"></div>
	<!-- buttons -->			
				<div style="margin-top:15px;clear: both">
					<a class="bot" href="javascript:cerrarPopUp()">Aceptar</a>				
				</div>
			</div>
		</div>
	</div>
	<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaProvincia.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaComarca.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaCultivoSbp.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaLineaReplicar.jsp"%>
</body>
</html>