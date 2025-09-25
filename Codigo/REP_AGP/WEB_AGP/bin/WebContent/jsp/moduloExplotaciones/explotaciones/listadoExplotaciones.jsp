<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
<title>Listado de explotaciones</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />


<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.js"></script>
<script type="text/javascript" src="jsp/js/jmesa/jquery.jmesa.min.js"></script>
<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
<script type="text/javascript" src="jsp/js/terminos.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
<script type="text/javascript" src="jsp/js/lupaGenericaIN.js"></script>
<script type="text/javascript" src="jsp/js/lupaGeneriaODGanado.js"></script>
<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
<script type="text/javascript" src="jsp/js/jquery.alerts.js" ></script>

<script type="text/javascript" src="jsp/moduloExplotaciones/explotaciones/listadoExplotaciones.js"></script>

<%@ include file="/jsp/js/draggable.jsp"%>

<script>
<!--//		
// Para evitar el cacheo de peticiones al servidor
$(document).ready(function(){
	var URL = UTIL.antiCacheRand(document.getElementById("main3").action);
	document.getElementById("main3").action = URL;
	document.getElementById("provincia").focus();
});

function eligeMenu() {
	if($('#vieneDeUtilidades').val() == 'true' && $('#modoLectura').val() == 'modoLectura'){
	 	SwitchMenu('sub4');
	 }else{
	 	SwitchMenu('sub3');
	 }
}

function cargarFiltro(){
	<c:forEach items="${sessionScope.consulta_LIMIT.filterSet.filters}" var="filtro">
		var inputText = document.getElementById('${filtro.property}');
		if (null!=inputText){
			inputText.value = '${filtro.value}';
		}
	</c:forEach>
}
//-->
</script>

</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="cargarFiltro();eligeMenu('sub3');">
	<div id="overlay" class="overlay" style="width: 100%;height: 100%;top: 0px;left: 0px;position: absolute;background-color: rgb(204, 204, 204);opacity: 0.5;display: none;z-index: 1;"></div>
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
	
	<div id="buttons" style="padding: 3px; width: 97%">
		<table width="100%" cellspacing="0" cellpadding="0" border="0" >
			<tr align="left">
				<!-- <td align="center" width="34%"> -->
				<td align="left" width="15%">
				</td>
				<td align="center" width="70%">
				<c:if test="${modoLectura != 'modoLectura'}">
					<a class="bot" id="btnAlta" href="javascript:alta();" title="Alta de una nueva explotación">Alta</a>
				</c:if>
					<a class="bot" id="btnConsultar" href="javascript:consultar();" title="Consultar explotaciones">Consultar</a>
					<a class="bot" id="btnLimpiar" href="javascript:limpiar();" title="Limpiar filtro de consulta">Limpiar</a>
				</td>
				<td align="right" width="15%">
					<!-- <a class="bot" id="btnRecalcular" href="javascript:recalcular();" title="Recálculo de precio de todas las explotaciones">Recalcular precio</a>-->
					<a class="bot" id="btnVolver"   href="javascript:volver()"         title="Volver al listado de pólizas">Volver</a>
					<a class="bot" id="btnSubvenciones" href="javascript:validaDatosVariablesAjax();" title="Continuar">Continuar</a>					
				</td>
			</tr>
		</table>
	</div> 
		
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
		<p class="titulopag" align="left">Listado de explotaciones</p>

		<form name="frmSubvenciones" action="seleccionPoliza.html" method="post" id="frmSubvenciones">
			<input type="hidden" name="action" id="action" value=""/>
			<input type="hidden" name="operacion" id="operacionSV" value="continuarGanado"/>
			<input type="hidden" name="origenllamada" id="origenllamada" value="listaExplotaciones"/>
			<input type="hidden" name="idpoliza" id="idpoliza" value="${requestScope.idpoliza}" />
			<input type="hidden" name="modoLectura" id="modoLectura" value="${requestScope.modoLectura}"/>
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${requestScope.vieneDeUtilidades}"/>
		</form>
		
		<!-- Formulario para la redirección a la pantalla de alta/baja/modificación de explotaciones -->
		<form:form name="datosExplotaciones" id="datosExplotaciones" action="datosExplotaciones.html" method="post" commandName="polizaBean">
			<input type="hidden" name="method" id="methodDE"/>
			<input type="hidden" name="idExplotacion" id="idExplotacionDE"/>
			<input type="hidden" name="operacion" id="operacionDE"/>
			<input type="hidden" name="tipoParcela" id="tipoParcelaDE"/>
			<input type="hidden" name="modoLectura" id="modoLectura" value="${requestScope.modoLectura }"/>
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${requestScope.vieneDeUtilidades}"/>
			<!-- bean -->
			<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
			<form:hidden path="linea.codlinea" id="codlinea" />
			<form:hidden path="linea.codplan" id="codplan" />
			<form:hidden path="idpoliza" id="idPolizaExplotaciones" />
			<form:hidden path="clase" id="idclase"/>
		</form:form>
		
		<form:form name="main3" id="main3" action="listadoExplotaciones.html" method="post" commandName="polizaBean">	
			<!-- generales -->
			<input type="hidden" name=explotacionId id="explotacionId"/>
			<input type="hidden" name="operacion" id="operacion" />
			<input type="hidden" name="limpiar" id="limpiar"/>
			<input type="hidden" name="modoLectura" id="modoLectura" value="${requestScope.modoLectura }"/>
			<input type="hidden" name="vieneDeUtilidades" id="vieneDeUtilidades" value="${requestScope.vieneDeUtilidades }"/>
			<input type="hidden" name="origenllamada" name="origenllamada" value="listaExplotaciones"/>
			<input type="hidden" name="nifCif_cm" id="nifCif_cm" value="${sessionScope.usuario.asegurado.nifcif}"/>
			<%-- <input type="text" name="idpoliza" id="idpoliza" value="${requestScope.idpoliza}" /> --%>
			<!-- bean -->
			<form:hidden path="linea.lineaseguroid" id="lineaseguroid" />
			<form:hidden path="linea.codlinea" id="codlinea" />
			<input type="hidden" id="fechaInicioContratacion" value="${polizaBean.linea.fechaInicioContratacion}"/>
			<form:hidden path="linea.codplan" id="codplan" />
			<form:hidden path="idpoliza" id="idpoliza" />
			<form:hidden path="clase" id="idclase"/>

			<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
			<%@ include file="/jsp/moduloExplotaciones/explotaciones/filtroExplotaciones.jsp"%>
			
			<br />
			
		</form:form>
	
		<div id="grid" style="width: 98%;margin:0 auto;">
	  		${consultaExplotaciones}		  							               
		</div> 	

		<br />

	</div>	
	
	<br />
	
	<!-- POPUP INFORMACION REGA -->
	<%@ include file="/jsp/moduloExplotaciones/explotaciones/popupInformacionRega.jsp"%>
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	
	<%@ include file="/jsp/common/lupas/lupaProvinciaIN.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaComarcaIN.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaTerminoIN.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaEspecieIN.jsp"%>
	<%@ include file="/jsp/common/lupas/lupaRegimenIN.jsp"%>
</body>
</html>