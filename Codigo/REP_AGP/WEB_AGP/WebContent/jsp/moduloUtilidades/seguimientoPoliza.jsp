<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/js/generales.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<fmt:bundle basename="displaytag">
	<c:set var="numReg">
		<fmt:message key="numElementsPag" />
	</c:set>
</fmt:bundle>
<fmt:bundle basename="agp">
	<c:set var="numEle">
		<fmt:message key="visores.numElements" />
	</c:set>
</fmt:bundle>


<html>
<head>
<!-- <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">  -->
<title>Seguimiento de la contrataci&oacute;n</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<!-- Estilos -->
<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
<!-- JavaScript,jQery & AJAX -->
<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
<script type="text/javascript" src="jsp/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript"
	src="jsp/moduloUtilidades/modificacionesPoliza/declaracionesanexomodificacion.js"></script>
<script type="text/javascript"
	src="jsp/moduloUtilidades/anexoModificacion/renovacionCupon.js"></script>
<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js"></script>
<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
<script type="text/javascript" src="jsp/js/jquery.alerts.js"></script>
<script type="text/javascript" src="jsp/moduloUtilidades/utilidades.js"></script>
<%@ include file="/jsp/js/draggable.jsp"%>

<script type="text/javascript">
	$(document).ready(function(){
	
		if('${poliza.tipoReferencia}' == 'C'){
			$('#btnAlta').hide();
			$('#btnAltaCpl').show();
		}
	}); 
	
	function blockUISeguimiento() {
		$.blockUI.defaults.message = '<h4> Descargando.<BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
		$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
	}
</script>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" onload="SwitchMenu('sub4')">
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>

				<!--******BOTON ACTUALIZAR************************************************************************************************-->
				<!--Botones top right Actualizar y Volver hay que ver que funcion javascript se llama al pulsar actualizar-->
				<td align="right">
					<c:if test="${isPerfilAdministrador}">
						<a class="bot" id="btnDescargarCostesPol" href="javascript:blockUISeguimiento(); document.getElementById('xmlCostes').submit();">Descargar</a>
						<a class="bot" id="btnActualizarSegPol" href="javascript:actualizarSeguimientoPoliza(${datos.idpoliza});">Actualizar</a>
					</c:if>
					<a class="bot" id="btnVolver" href="javascript:volverSeguimientoPoliza();">Volver</a>
				</td>
			</tr>
		</table>
	</div>

	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">

		<p class="titulopag" align="left">Seguimiento de la
			Contrataci&oacute;n</p>

		<form name="seguimientoPoliza" id="seguimientoPoliza"
			action="seguimientoPoliza.html" method="post">
			<input type="hidden" name="idpoliza" id="idpoliza" value="${datos.idpoliza}" /> 
			<input type="hidden" name="method" id="method" value="${datos.method }" />
		</form>
		
		<c:if test="${isPerfilAdministrador}">
			<form name="xmlCostes" id="xmlCostes"
				action="seguimientoPoliza.html" method="post">
				<input type="hidden" name="idpoliza" id="idpoliza" value="${datos.idpoliza}" /> 
				<input type="hidden" name="method" id="method" value="doDescargarXmlCostes" />
			</form>
		</c:if>

		<form:form name="main" id="main" action="seguimientoPoliza.html"
			method="post" commandName="anexoModificacion">
			<input type="hidden" id="method" name="method" />
			<input type="hidden" id="idPoliza" name="idPoliza"
				value="${datos.idpoliza}" />
		</form:form>

		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>


		<!-- Datos de la póliza -->
		<fieldset style="width: 95%">
			<legend class="literal">Datos de la p&oacute;liza</legend>
			<table width="100%" align="center" cellspacing="2">
				<tr>
					<td class="literal" width="70px">Plan:</td>
					<td width="70px" class="detalI">${datos.polizaBean.plan}</td>
					<td class="literal" width="100px">Línea:</td>
					<td width="70px" class="detalI">${datos.polizaBean.linea} <!--301 - EXPLOTACIONES CÍTRICAS-->
					</td>
					<td class="literal" width="110px">Asegurado:</td>
					<td width="350px" class="detalI" colspan="3">
						<!-- 58714456T - SONIA FERNANDEZ MICO --> <!-- *OBJETOPOLIZA**************************************************** -->
						${datos.polizaBean.nifAsegurado} -
						<c:choose>
						   <c:when test="${!empty objetoPoliza.asegurado.nombre}">
							    ${objetoPoliza.asegurado.nombre}
								${objetoPoliza.asegurado.apellido1}
								${objetoPoliza.asegurado.apellido2}
							</c:when> 
						   <c:otherwise>
						   		${objetoPoliza.asegurado.razonsocial}
						   </c:otherwise>  
						</c:choose>
					</td>
				</tr>

				<tr>
					<td class="literal" width="70px">Póliza:</td>
					<td width="70px" class="detalI">
						${datos.polizaBean.referenciaPoliza}-${datos.polizaBean.digitoPoliza}
					</td>
					<td class="literal" width="100px">Colectivo:</td>
					<td width="70px" class="detalI">
						${datos.polizaBean.colectivo}-${datos.polizaBean.digitoColectivo}
					</td>
					<td class="literal" width="110px">Tomador:</td>
					<td width="350px" class="detalI" colspan="3">
						${datos.polizaBean.nifTomador} - ${datos.polizaBean.nombreTomador}
					</td>
				</tr>

				<tr>
					<td class="literal" width="70px">Fec. Vigor:</td>
					<td width="70px" class="detalI"><fmt:formatDate
							pattern="dd/MM/yyyy" value="${datos.polizaBean.fechaVigor}" /></td>
					<td class="literal" width="100px">Fec. Vencimiento:</td>
					<td width="70px" class="detalI"><fmt:formatDate
							pattern="dd/MM/yyyy" value="${datos.polizaBean.fechaVencimiento}" />
					</td>
					<td class="literal" width="120px">Fec. Comunicación:</td>
					<td width="140px" class="detalI"><fmt:formatDate
							pattern="dd/MM/yyyy"
							value="${datos.polizaBean.fechaComunicacion}" /></td>
					<td class="literal" width="70px">Fec. Envío:</td>
					<td width="140px" class="detalI">
						<!--<fmt:formatDate pattern="dd/MM/yyyy" value="${poliza.fechaenvio}"/>-->
						<!-- *OBJETOPOLIZA**************************************************** -->
						<fmt:formatDate pattern="dd/MM/yyyy"
							value="${objetoPoliza.fechaenvio}" />
					</td>
				</tr>

			</table>
		</fieldset>

		<!-- Estado de la contratación -->
		<fieldset style="width: 95%">
			<legend class="literal">Estado de la contrataci&oacute;n</legend>

			<div id="grid" align="center">
				<div id="centered" style="width: 60%">
					<display:table requestURI="seguimientoPoliza.html" class="LISTA"
						name="${datos.polizaBean.estados}"
						pagesize="10"
						excludedParams="method" defaultsort="5" defaultorder="ascending"
						decorator="com.rsi.agp.core.decorators.ModelTableDecoratorSeguimientoPoliza"
						style="border-collapse:collapse;">

						<display:column class="literal" headerClass="cblistaImg"
							title="Estado" property="estadoDescriContratacion" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Fecha" property="estContFecha" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Motivo anulación" property="motivoAnulResc" />

					</display:table>
				</div>
			</div>

		</fieldset>

		<!-- Información adicional -->
		<fieldset style="width: 95%">
			<legend class="literal">Información adicional</legend>

			<div id="grid" align="center">
				<div id="centered" style="width: 60%">
					<display:table requestURI="seguimientoPoliza.html" class="LISTA"
						name="${datos.polizaBean.infoAdicional}" pagesize="10"
						excludedParams="method" defaultsort="5" defaultorder="ascending"
						decorator="com.rsi.agp.core.decorators.ModelTableDecoratorSeguimientoPoliza"
						style="border-collapse:collapse;">

						<display:column class="literal" headerClass="cblistaImg" title=""
							property="infoTexto" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Fecha" property="infoFecha" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Causa" property="infoCausa" />

					</display:table>
				</div>
			</div>

		</fieldset>

		<!-- Recibos -->
		<fieldset style="width: 95%">
			<legend class="literal">Recibos</legend>

			<div id="grid" align="center">
				<div id="centered" style="width: 60%">
					<display:table requestURI="seguimientoPoliza.html" class="LISTA"
						name="${datos.polizaBean.recibo}" pagesize="10"
						excludedParams="method"  defaultorder="ascending"
						decorator="com.rsi.agp.core.decorators.ModelTableDecoratorSeguimientoPoliza"
						style="border-collapse:collapse;">

						<display:column class="literal" headerClass="cblistaImg"
							title="Número" property="reciboNumero" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Fecha emisión" property="reciboFecEmision" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Fecha impago" property="reciboFecImpago" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Fase" property="reciboFase" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Importe" property="reciboImporte" />

					</display:table>
				</div>
			</div>
		</fieldset>

		<!-- Modificaciones e incidencias -->
		<fieldset style="width: 95%">
			<legend class="literal">Modificaciones e incidencias</legend>

			<div id="grid" align="center">
				<div id="centered" style="width: 100%">
					<display:table requestURI="seguimientoPoliza.html" class="LISTA"
						name="${datos.polizaBean.incidencia}" pagesize="10"
						excludedParams="method"  defaultorder="ascending"
						decorator="com.rsi.agp.core.decorators.ModelTableDecoratorSeguimientoPoliza"
						style="border-collapse:collapse;">

						<display:column class="literal" headerClass="cblistaImg"
							title="Año" property="modIncAnio" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Número" property="modIncNumero" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Tipo" property="modIncTipo" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Asunto" property="modIncAsunto" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Estado" property="modIncEstado" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Fecha" property="modIncFecha" />
						<display:column class="literal" headerClass="cblistaImg"
							title="Documento afectado" property="modIncDocAfectado" />
						<display:column class="literal" headerClass="cblistaImg"
							title="ID Envío" property="modIncIdEnvio" />

					</display:table>
				</div>
			</div>

		</fieldset>

	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	<%@ include file="/jsp/moduloUtilidades/anexoModificacion/popupEstadoContratacion.jsp"%>


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