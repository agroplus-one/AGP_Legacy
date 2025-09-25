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
	<title>Proceso de Cierre Mensual</title>
	
	 <%@ include file="/jsp/common/static/metas.jsp"%>
	 
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
	
	<!-- JavaScript,jQery & AJAX -->
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
	<%@ include file="/jsp/js/draggable.jsp"%>
	
	<script language="javascript">

		$(function(){
			$("#grid").displayTagAjax();
		});	
		
		function volver(){						
			$('#method').val('doConsulta');				
			$('#volver').submit();
		}
					
	</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchSubMenu('sub7', 'sub5');">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">					
					<a class="bot" id="btnVolver"  href="javascript:volver();">Volver</a>				
				</td>
			</tr>
		</table>
	</div>
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Detalle de fases importadas sin cierre</p>
		<div id="grid">
			<display:table requestURI="" class="LISTA" summary="detalleCierrre" 
					pagesize="${numReg}" name="${listFicherosSinCierre}" id="detalleCierrre" 
					decorator="com.rsi.agp.core.decorators.ModelTableDecoratorCierreComisiones" 
					sort="list" style="width:60%;border-collapse:collapse;">					
					
					<display:column class="literal" headerClass="cblistaImg" title="Fase"   			property="fase"			style="text-align:center" sortable="true" />
					<display:column class="literal" headerClass="cblistaImg" title="Fecha Emisión"   	property="fecEmision"   style="text-align:center" sortable="true"	format="{0,date,dd/MM/yyyy}"/>				
					<display:column class="literal" headerClass="cblistaImg" title="Fichero"			property="fichero"    	style="text-align:center" sortable="true"/>
					<display:column class="literal" headerClass="cblistaImg" title="Tipo de Fichero"  	property="tipoFichero" 	style="text-align:center" sortable="true"/>
			</display:table>
		</div>		
	</div>
	<form name="volver" id="volver" action="cierre.html">
		<input type="hidden" id="method" name="method" />
	</form>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<!-- panel avisos -->
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
					<div id="txt_info" style="width: 70%" >Por cada plan,debe introducir una distribución para la línea genérica 999.</div>
				</div>
		</div>
	</div>	
</body>
</html>