<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
	<head>
		<title>Elección de la póliza a cargar</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
			
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
		<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/elegirPolizaActualizar.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr align="left">
					<td align="right">
						<a class="bot" id="btnVolver" href="javascript:volver()">Volver</a>
						<a class="bot" id="btnCargar" href="javascript:cargar()">Cargar</a>
					</td>
				</tr>
			</table>
		</div>
		
	
		<div id="main">
			<form:form name="main" id="main" action="polizaActualizada.run" method="post" commandName="polizaBean">
				<form:hidden path="idpoliza" id="idpoliza_main"/>
				<form:hidden path="asegurado.nifcif" id="nifasegurado" />
				<form:hidden path="linea.codplan" id="codplan" />
				<form:hidden path="linea.codlinea" id="codlinea" />
				</form:form>
		</div>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
		<p class="titulopag" align="left">Elecci&oacute;n de la p&oacute;liza a cargar</p>
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		<form name="retornarAOrigenDatos" id="retornarAOrigenDatos" action="polizaController.html" method="post">
	    	<input type="hidden" name="action" id="action" value="volverAOrigenDatos"/>
	    	<input type="hidden" name="idpoliza" id="idpolizaVolver" value="${idpoliza}"/>
   		 </form>
		
		<form name="main3" id="main3" action="cargaParcelasController.html" method="post" commandName="polizaBean">
			<input type="hidden" name="idpoliza" id="idpoliza" value="${idpoliza}"/>
			<input type="hidden" name="idPolSeleccionada" id="idPolSeleccionada" value=""/>
			<input type="hidden" name="recalcular" id="recalcular" value=""/>
			<input type="hidden" name="method" id="method" value=""/>
			<input type="hidden" name="descargarCopy" id="descargarCopy" value="${descargarCopy }"/>
		<form>
			
			
			
		<!-- Grid Jmesa -->
		<div id="grid" style="width: 100%">
			 	${polizas}
		</div>   
</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		<%@ include file="/jsp/moduloPolizas/polizas/popupRecalcular.jsp"%>
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
					<div id="txt_info" style="width: 70%" >No hay ninguna Póliza seleccionada.</div>
				</div>
		    </div>
	    </div>
	</body>
</html>