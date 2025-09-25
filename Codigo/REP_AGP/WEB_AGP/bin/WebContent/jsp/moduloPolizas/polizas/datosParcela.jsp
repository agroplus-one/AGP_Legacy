<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
    <title>Agroplus - Datos parcela</title>
    
    <%@ include file="/jsp/common/static/metas.jsp"%>    
		
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css"/>
    <link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
    <link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
    
    <script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-utils.js"></script>
	<script type="text/javascript" src="jsp/js/calendar.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-setup.js"></script>
	<script type="text/javascript" src="jsp/js/calendar-sp.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>	
	<script type="text/javascript" src="jsp/js/terminos.js"></script>
	<script type="text/javascript" src="jsp/js/lupa308.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>		
	<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenericaDV.js"></script>
	
	<script type="text/javascript" src="jsp/common/lupas/lupaPrecioProduccion/lupaPrecioProduccion.js"></script>
	
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/datosParcela.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/datosVariablesParcela.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/datosParcelaAjax.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/datosParcelaCoberturaAjax.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/datosParcelaCobertura.js"></script>
	
	<%@ include file="/jsp/js/draggable.jsp"%>
	
	<style type="text/css"> 
        .scrollable{ overflow: auto; width: 250px; height: 40px; border: 1px silver solid; } 
        .scrollable select { border: none; } 
    </style>
</head>
	
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
	
	<%@ include file="/jsp/common/static/cabecera.jsp"%>	
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	
	<c:choose>
		<c:when test="${modoLectura == 'modoLectura' && vieneDeUtilidades == 'true'}">
			<%@ include file="/jsp/common/static/datosCabeceraConsulta.jsp" %>	
		</c:when>
		<c:otherwise>
			<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
		</c:otherwise>
	</c:choose>
	
	<c:set var="isAnexo">false</c:set>
	<%@ include file="/jsp/moduloPolizas/polizas/datosParcelaComun.jsp"%>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<form:form name="listadoParcelas" id="listadoParcelas" action="seleccionPoliza.html" method="post" commandName="polizaBean">
		<input type="hidden" id="operacion" name="operacion" value="listParcelas">
		<form:hidden path="idpoliza" id="idpoliza"/>
		<form:hidden path="linea.lineaseguroid" id="lineaseguroid"/>
		<input type="hidden" id="tieneParcelas" name="tieneParcelas" value="${requestScope.tieneParcelas}">
		<input type="hidden" id="modoLectura" name="modoLectura" value="${requestScope.modoLectura}">	
		<input type="hidden" id="vieneDeUtilidades" name="vieneDeUtilidades" value="${requestScope.vieneDeUtilidades}">		
		<input type="hidden" id="vieneDeParcela" name="vieneDeParcela" value="true">
		<input type="hidden" id="estaEditando" name="estaEditando" value="true">
		<input type="hidden" id="idParcelEdic" name="idParcelEdic" value="${requestScope.codParcela}">
		<input type="hidden" id="fechaInicioContratacion" value="${fechaInicioContratacion}"/>
	</form:form>
	
</body>
</html>