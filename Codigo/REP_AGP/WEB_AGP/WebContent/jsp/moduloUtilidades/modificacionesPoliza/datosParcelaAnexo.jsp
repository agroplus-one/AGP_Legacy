<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
    <title>Agroplus - Datos parcela (Anexo)</title>
    
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
	<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
	<script type="text/javascript" src="jsp/js/lupaGenericaDV.js"></script>
	
	<script type="text/javascript" src="jsp/common/lupas/lupaPrecioProduccion/lupaPrecioProduccion.js"></script>
	
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/datosParcela.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/datosVariablesParcela.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/datosParcelaAjax.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/datosParcelaCoberturaAjax.js"></script>
	<script type="text/javascript" src="jsp/moduloPolizas/polizas/datosParcelaCobertura.js"></script>
	
	<script type="text/javascript" src="jsp/js/lupa308.js"></script>	
	
	<%@ include file="/jsp/js/draggable.jsp"%>
	
	<style type="text/css"> 
        .scrollable{ overflow: auto; width: 250px; height: 40px; border: 1px silver solid; } 
        .scrollable select { border: none; } 
    </style>
</head>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
	
	<%@ include file="/jsp/common/static/cabecera.jsp"%>	
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	
	<c:choose>
		<c:when test="${modoLectura == 'modoLectura'}">
			<%@ include file="/jsp/common/static/datosCabeceraConsulta.jsp" %>	
		</c:when>
		<c:otherwise>
			<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
		</c:otherwise>
	</c:choose>
	
	<c:set var="isAnexo">true</c:set>
	<%@ include file="/jsp/moduloPolizas/polizas/datosParcelaComun.jsp"%>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<form:form name="listadoParcelas" id="listadoParcelas" action="parcelasAnexoModificacion.html" method="post" commandName="capitalAseguradoModificadaBean">
		<input type="hidden" id="method" name="method" value="doConsulta" />
		<input type="hidden" id="lineaseguroid" name="lineaseguroid" value="${capitalAseguradoModificadaBean.parcela.anexoModificacion.poliza.linea.lineaseguroid}" />
		<input type="hidden" id="idAnexo" name="idAnexo" value="${capitalAseguradoModificadaBean.parcela.anexoModificacion.id}" />
		<input type="hidden" id="idCuponStr" name="idCuponStr" value="${capitalAseguradoModificadaBean.parcela.anexoModificacion.cupon.idcupon}" />
		<input type="hidden" id="idCupon" name="idCupon" value="${capitalAseguradoModificadaBean.parcela.anexoModificacion.cupon.id}" />
		<input type="hidden" id="idPoliza" name="idPoliza" value="${capitalAseguradoModificadaBean.parcela.anexoModificacion.poliza.idpoliza}" />		
		<input type="hidden" id="modoLectura" name="modoLectura" value="${modoLectura}" />		
		<input type="hidden" id="tipoListadoGrid" name="tipoListadoGrid" value="${requestScope.tipoListadoGrid}" />
		<input type="hidden" id="idsRowsChecked" name="idsRowsChecked" value="${requestScope.idsRowsChecked}" />
		<input type="hidden" id="idsCapAsegRowsChecked" name="idsCapAsegRowsChecked" value="${requestScope.idsCapAsegRowsChecked}" />
		<input type="hidden" id="tipoListadoGrid" name="tipoListadoGrid" value="${requestScope.tipoListadoGrid}" />
		<input type="hidden" id="d-5909046-s" name="d-5909046-s" value="${requestScope.d5909046s}" />
		<input type="hidden" id="d-5909046-o" name="d-5909046-o" value="${requestScope.d5909046o}" />
		<input type="hidden" id="vieneDeListadoAnexosMod" name="vieneDeListadoAnexosMod" value="${requestScope.vieneDeListadoAnexosMod}" />
		<input type="hidden" id="vieneDeCoberturasAnexo" name="vieneDeCoberturasAnexo" value="${requestScope.vieneDeCoberturasAnexo}" />
		<input type="hidden" id="marcarTodosChecks" name="marcarTodosChecks" value="${requestScope.marcarTodosChecks}" />
		<input type="hidden" id="isClickInListado" name="isClickInListado" value="${requestScope.isClickInListado}" />
		<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
		
	</form:form>
</body>
</html>