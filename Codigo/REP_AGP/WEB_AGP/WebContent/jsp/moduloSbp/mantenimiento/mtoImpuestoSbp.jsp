<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de sobreprecio - Impuestos</title>
		
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
		<script type="text/javascript" src="jsp/moduloSbp/mantenimiento/mtoImpuestoSbp.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
				<script type="text/javascript">
			 
			 function cargarFiltro(){
				 <c:if test="${origenLlamada != 'menuGeneral'}">	
					<c:forEach items="${sessionScope.consultaMtoImpuestoSbp_LIMIT.filterSet.filters}" var="filtro">
						<c:if test="${filtro.property == 'codplan'}">
							$('#codplan').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'impuestoSbp.codigo'}">
							$('#codimpuesto').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'impuestoSbp.descripcion'}">
							$('#nomimpuesto').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'baseSbp.base'}">
							$('#nombase').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'valor'}">
							$('#valor').val('${filtro.value}');
						</c:if>
						
					</c:forEach>
				</c:if>
			} 
		
		</script>

	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub10','sub8');cargarFiltro();">
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
		<form name="limpiar" id="limpiar" action="mtoImpuestoSbp.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>								
			<input type="hidden" name="origenLlamada" id="origenLlamada" value="menuGeneral"/>
		</form>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%" >
		<p class="titulopag" align="left">Impuestos de Sobreprecio</p>					
			
			<form:form name="main3" id="main3" action="mtoImpuestoSbp.run" method="post" commandName="mtoImpuestoSbpBean">
				
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="planreplica" id="planreplica" />		
				<input type="hidden" name="origenLlamada" id="origenLlamada" />	
				<form:hidden path="id" id="id"/>
							
		        <%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<div style="width:97%;margin:0 auto;">
					<fieldset>
					<legend class="literal">Filtro</legend>
						<table style="margin:0 auto;">
							<tr>
								<td class="literal">Plan</td>
								<td class="literal">
									<form:input path="codplan" id="codplan" size="5" maxlength="4" cssClass="dato" tabindex="1" />
								</td>
								<td class="literal">Impuesto</td>
								<td class="literal">
									<form:input path="impuestoSbp.codigo" id="codimpuesto" size="5" maxlength="5" cssClass="dato" tabindex="1" onchange="javascript:this.value=this.value.toUpperCase();lupas.limpiarCampos('nomimpuesto');"/>
									<form:input path="impuestoSbp.descripcion" id="nomimpuesto" size="30" maxlength="30" cssClass="dato" tabindex="1" onchange="javascript:this.value=this.value.toUpperCase();"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ImpuestoSbp','principio', '', '');"	alt="Buscar Impuesto" title="Buscar Impuesto" />
								</td>
								<td class="literal">Valor</td>
								<td class="literal">
									<form:input path="valor" id="valor" size="7" maxlength="7"cssClass="dato" tabindex="1" />&nbsp;%
								</td>
								<td class="literal">Base</td>
								<td class="literal">
									<form:input path="baseSbp.base" id="nombase" size="30" maxlength="30"cssClass="dato" tabindex="1" onchange="javascript:this.value=this.value.toUpperCase();"/>
									<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('BaseSbp','principio', '', '');"	alt="Buscar Base" title="Buscar Base" />
								</td>									
							</tr>
						</table>
					</fieldset>
				</div>
			</form:form>
			
			<form:form name="frmBorrar" id="frmBorrar" action="mtoImpuestoSbp.run" method="post" commandName="mtoImpuestoSbpBean">
				<form:hidden path="id" id="idBorrar" />
				<input type="hidden" name="method" id="methodBorrar" />
			</form:form>
			
			<!-- Grid Jmesa -->
			<div id="grid" style="width: 80%;margin:0 auto;">
		  		${consultaMtoImpuestoSbp}		  							               
			</div> 	
	</div>
			
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaImpuestoSbp.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaBaseSbp.jsp"%>
	
	
	<!--               -->
	<!-- POPUPS AVISO  -->
	<!--               -->
	
	<!-- *** popUp detalle Errores *** -->
	<div id="divMensajeError" class="parcelasRepWindow" style="color:#333333;-moz-border-radius:4px 4px 4px 4px;padding:0.2em;left: 30%;">
		
		<div id="header-popup" style="padding:0.4em 1em;position:relative; color:#FFFFFF;font-weight:bold;-moz-border-radius:4px 4px 4px 4px; background:#525583;height:15px">
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
	
	<%@ include file="/jsp/moduloRC/popUpReplicarPlan.jsp" %>	
</body>
</html>