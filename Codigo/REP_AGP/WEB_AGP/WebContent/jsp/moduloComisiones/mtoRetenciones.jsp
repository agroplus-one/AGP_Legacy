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
		<script type="text/javascript" src="jsp/moduloComisiones/mtoRetenciones.js" ></script>		
		<%@ include file="/jsp/js/draggable.jsp"%>
	<script type="text/javascript">
			 
			 function cargarFiltro(){
				 <c:if test="${origenLlamada != 'menuGeneral'}">	
					<c:forEach items="${sessionScope.consultaRetenciones_LIMIT.filterSet.filters}" var="filtro">
				
						<c:if test="${filtro.property == 'anyo'}">
							$('#anyo').val('${filtro.value}');
						</c:if>
						<c:if test="${filtro.property == 'retencion'}">
							$('#retencion').val('${filtro.value}');
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

				<td align="right">			
					<a class="bot" id="btnAlta"  href="javascript:alta();">Alta</a>				
					<a class="bot" id="btnModif" style="display:none" href="javascript:editar();">Modificar</a>				
					<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>					
					<a class="bot" id="btnLimpiar"  href="javascript:limpiar();">Limpiar</a>		
				</td>
			</tr>
		</table>
	</div>
	<div class="conten" style="padding: 3px; width: 100%">
		<p class="titulopag" align="left">Mantenimiento de Retenciones</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		<form:form id="main3" name="main3" method="post" action="mtoRetenciones.run" commandName="retencionBean">
			<input type="hidden" name="method" id="method"/>			
			<input type="hidden" id="perfil" name="perfil" value="${perfil}"/>
			<input type="hidden" name="grupoEntidades" id="grupoEntidades" value="${grupoEntidades}"/>
			<input type="hidden" name="origenLlamada" id="origenLlamada" />
			<input type="hidden" name="idBorrar" id="idBorrar"/>
			<input type="hidden" name="idRetencion" id="idRetencion"/>		
			<div style="panel2 isrt">
				<fieldset style="width:70%; margin:0 auto;">
					<table width="70%" style="margin:0 auto;"> 
						<tr>
							<td class="literal" width="8%">Ejercicio</td>
							<td width="6%">
								<form:input	path="anyo" size="4" maxlength="4" cssClass="dato" id="anyo" />
								<label class="campoObligatorio" id="campoObligatorio_anyo" title="Campo obligatorio">*</label>
							</td>
							<td class="literal" width="8%">Retención</td>
							<td width="6%">
								<form:input	path="retencion" size="6" maxlength="6" cssClass="dato" id="retencion" onchange="this.value = this.value.replace(',', '.')"/>
								<label class="campoObligatorio" id="campoObligatorio_retencion" title="Campo obligatorio">*</label>
							</td>
						</tr>
					</table>					
				</fieldset>
			</div>
		</form:form>
		
		<!-- Grid Jmesa -->
		<div id="grid" style="width: 55%; margin:0 auto;">
	  		${consultaRetenciones}		  							               
		</div> 	
	</div>
	
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
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
	
</body>

</html>