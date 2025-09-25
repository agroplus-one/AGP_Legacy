<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>

<html>
<head>
	<title>Relación de Modificaciones E Incidencias</title>
	
	<%@ include file="/jsp/common/static/metas.jsp"%>
	
	<!-- Estilos -->
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
	<!-- JavaScript,jQery & AJAX -->
	<script type="text/javascript" src="jsp/js/menuapli.js"></script>
	<script type="text/javascript" src="jsp/js/util.js" ></script>
	<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
	<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
	<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
	<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
	<script type="text/javascript" src="jsp/js/additional-methods.js"></script>
	

	<script type="text/javascript">
		$(document).ready(function(){
			var URL = UTIL.antiCacheRand($("#main").attr("action"));
			$("#main").attr("action", URL);
			
		});
		
		// Vuelve a la pantalla de declaraciones de modificación
		function volver (){
			$("#formVolver").submit();
		}
		
		// Va a la pantalla de relación de modificaciones e incidencias
		function imprimir (idCupon,anio,numero){
			$("#method").val("doImprimirPdf");
			$("#idCuponImpresion").val(idCupon);
			$("#anio").val(anio);
			$("#numero").val(numero);
			var frm = document.getElementById('main');
			frm.target="_blank";
			$("#main").submit();
		}
		
		// Va a la pantalla de cálculo de modificación en modo lectura
		function verDC (idCupon) {
			$("#idCuponVerDC").val(idCupon);
			// Modif Tam (Incidencias - 31.05.2018)* Inicio//
			$.blockUI.defaults.message = '<h4><BR>Espere un momento, por favor... <img src="jsp/img/ajax-loading.gif"/></h4>';
  			$.blockUI({ overlayCSS: { backgroundColor: '#525583' } });
  		    // Modif Tam (Incidencias - 31.05.2018)* Fin//

			$("#formDistCoste").submit();
		}
		
		function aportarDocumentacion(anio, codAsunto, asunto, codigoDocAfectado, numeroInc, estado, idEnvio, 
				referencia, tipoPoliza, fechaEstado, numDocumentos){
			$("#anioDoc").val(anio);
			$("#asuntoDoc").val(asunto);
			$("#codAsuntoDoc").val(codAsunto);
			$("#codDocAfec").val(codigoDocAfectado);
			$("#numIncidenciaDoc").val(numeroInc);
			$("#estadoDoc").val(estado);
			$("#idEnvioDoc").val(idEnvio);
			$("#referenciaDoc").val(referencia);
			$("#tipoPolizaDoc").val(tipoPoliza);
			$("#fechaEstadoDoc").val(fechaEstado);
			$("#numDoc").val(numDocumentos);
			$("#formDocumentacion").submit();
		}
		
		function verAnulacionRescision(){
			$("#method").val("doconsultaAnulResc")
			$('#codlineaAnulyResc').val($('#linea').val());
			$("#formAnulacionyRescisionPol").submit();
		}
		
	
	</script>
	
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4')">	
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<!-- Pet. 57627 ** Nuevo botón que accede a Anulación/Rescision -->
			    <td width="33%">&nbsp;</td>
				<td width="33%">
					<c:if test="${perfil == 0}">
						<a class="bot" id="btnAnulResc" href="javascript:verAnulacionRescision();">Anulaci&oacute;n/Rescisi&oacute;n</a>
					</c:if>
				</td>
				<!-- Pet. 57627 ** Nuevo botón que accede a Anulación/Rescision * Fin-->
				<td width="33%" align="right">
					<a class="bot" id="btnVolver" href="javascript:volver();">Volver</a>
				</td>
			</tr>
		</table>
	</div>
	
	<!-- Contenido de la página -->
	<div class="conten" style="padding: 3px; width: 97%">
	
		<p class="titulopag" align="left">Relación de Modificaciones E Incidencias</p>
		
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		
		<div style="panel2 isrt">
			<fieldset style="width:95%;">
				<legend class="literal">Datos de la p&oacute;liza</legend>	
					<table width="100%" align="center" cellspacing="2">
						<tr>
							<td class="literal" width="40px">Plan:</td>
							<td width="40px" class="detalI">${plan}</td>
							<td class="literal" width="50px">Línea:</td>
							<td width="250px" class="detalI">${linea} - ${nomLinea}</td>
		  					<td class="literal" width="75px">Asegurado:</td>
		 					<td width="250px" class="detalI">${nombreAsegurado}</td> 
						</tr>
						
						<tr>
							<td class="literal" width="40px">Póliza:</td>
							<td width="40px" class="detalI">${referencia}</td>
							<td class="literal" width="50px">Módulo:</td>
							<td width="300px" class="detalI">${modulo}</td>		
							<td class="literal" width="70px">Fec. Envío:</td>
							<td width="100px" class="detalI"><fmt:formatDate pattern="dd/MM/yyyy" value="${fechaEnvio}"/></td>
						</tr>
					</table>
											
			</fieldset>
			<br/><br/><br/><br />
		</div>
		<form:form name="main" id="main" action="impresionIncidenciasMod.html" method="post" commandName="impresionIncidenciasModBean" >
			<input type="hidden" id="method" name="method" />
			<input type="hidden" id="idCuponImpresion" name="idCuponImpresion"/>
			<input type="hidden" id="anio" name="anio"/>
			<input type="hidden" id="numero" name="numero"/>
			<input type="hidden" name="perfilUsuario" id="perfilUsuario" value="${perfil}" />
		</form:form>
		
		<form:form name="formDistCoste" id="formDistCoste" action="calculoModificacion.html" method="post" >
			<input type="hidden" id="method" name="method" value="doConsultaDistCosteDesdeMofidicacionesIncidencias" />
			<input type="hidden" id="modoLectura" name="modoLectura" value="modoLectura" />
			<input type="hidden" id="idPolizaVerDC" name="idPolizaVerDC" />
			<!-- Pet. 50775 ** MODIF TAM (08.05.2018) ** Inicio -->
			<input type="hidden" id="referenciaDocVuelta" name="referencia" value="${referencia}"/>
		    <input type="hidden" name="polizaOperacion" value="${idPoliza}"/>
		    <input type="hidden" id="lineaDocVuelta" name="linea" value="${linea}"/>
		    <input type="hidden" id="nombreCompleto" name="nombreCompleto" value="${nombreAsegurado}"/>
		    <!-- Pet. 50775 ** MODIF TAM (08.05.2018) ** Fin-->
			<input type="hidden" id="idCuponVerDC" name="idCuponVerDC" />
		</form:form>
		
		<form id="formVolver" name="formVolver" action="utilidadesPoliza.html" method="post">
			<input type="hidden" name="operacion" id="operacion"  value="anexoModificacion"/>
			<input type="hidden" name="polizaOperacion" value="${idPoliza}"/>
			<input type="hidden" name="recogerPolizaSesion" value="true"/>
		</form>
		
		<!-- Pet. 57627 ** Nuevo botón que accede a Anulación/Rescision -->
		<!-- Formulario para vconsultar la relacion de incidencias asociadas a una poliza en agroseguro -->
		<form id="formAnulacionyRescisionPol" name="formAnulacionyRescisionPol"  action="anulacionyRescisionPol.run" method="post">
		    <input type="hidden" id="method" name="method" value="doconsultaAnulResc" />
			<input type="hidden" id="codlineaAnulyResc"   name="codlineaAnulyResc"   value="${linea}"/>
			<input type="hidden" id="nomblineaAnulyResc"  name="nomblineaAnulyResc"  value="${nomLinea}"/>
			<input type="hidden" id="codplanAnulyResc"    name="codplanAnulyResc"    value="${plan}"/>							
			<input type="hidden" id="referenciaAnulyResc" name="referenciaAnulyResc" value="${referencia}" />
			<input type="hidden" id="tiporefAnulyResc"    name="tiporefAnulyResc"    value="${tiporef}"/>
			<input type="hidden" id="nifAsegAnulyResc"    name="nifAsegAnulyResc"    value="${nifcif}"/>
			<input type="hidden" id="idPolizaAnulyResc"	  name="idPolizaAnulyResc"   value="${idPoliza}"/>
			<input type="hidden" id="idPolIniAyR"		  name="idPolIniAyR"         value="${idPoliza}"/>
			<input type="hidden" id="origenLlamada"       name="origenLlamada"       value="impresionInc" />
		</form>
		<!-- Pet. 57627 ** Nuevo botón que accede a Anulación/Rescision * Fin -->				 
		
		<div id="grid">
	        <display:table requestURI="impresionIncidenciasMod.html" id="listaIncidencias" class="LISTA" summary="Incidencias" 
	        		name="${listaIncidencias}" sort="list" pagesize="10" excludedParams="method" defaultsort="0"
	        		decorator="com.rsi.agp.core.decorators.ModelTableDecoratorImpresionIncidencias" style="width:100%;border-collapse:collapse;">
	        	<%--Pet. 50775 ** MODIF TAM (14.05.2018) ** Resolución Incidencia * Se aumenta el tamaña de la columna de acciones--%>	
			    <%--<display:column class="literal" headerClass="cblistaImg" title="Acciones" property="acciones" sortable="false" style="width:40px;text-align:center"/> --%>
			    <display:column class="literal" headerClass="cblistaImg" title="Acciones" property="acciones" sortable="false" style="width:80px;text-align:left"/>
				<display:column class="literal" headerClass="cblistaImg" title="Año" property="anio" style="width:40px;"/>
	        	<%--Pet. 50775 ** MODIF TAM (14.05.2018) ** Resolución Incidencia * FIN --%>
				<display:column class="literal" headerClass="cblistaImg" title="Número" property="numero" style="width:80px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Asunto" property="asunto" style="width:230px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Estado" property="estado" style="width:170px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Fecha" property="fecha" style="width:80px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Documento afectado" property="documento" style="width:150px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Referencia" property="referencia" style="width:80px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Tipo Poliza" property="tipoPoliza" style="width:80px;"/>
				<display:column class="literal" headerClass="cblistaImg" title="Id. Envio" property="idEnvio" style="width:140px;"/>
				<%--Pet. 50775 ** MODIF TAM (08.06.2018) ** Resolución Incidencia * No mostrar la columna de Nº de Documentos--%>
				<%--<display:column class="literal" headerClass="cblistaImg" title="N. Documentos" property="numDocumentos" style="width:120px;"/> --%>
			</display:table>				
		</div>
	
	</div>
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	<%@ include file="/jsp/common/static/overlay.jsp"%>
	
	<form:form name="formDocumentacion" id="formDocumentacion" action="impresionIncidenciasMod.html" method="post">
		<input type="hidden" id="method" name="method" value="doAportarDocumentacion"/>
		<input type="hidden" id="origen" name="origen" value="impresionIncidencias"/>
		<input type="hidden" id="anioDoc" name="anioDoc"/>
		<input type="hidden" id="asuntoDoc" name="asuntoDoc"/>
		<input type="hidden" id="codAsuntoDoc" name="codAsuntoDoc"/>
		<input type="hidden" id="codDocAfec" name="codDocAfec"/>
		<input type="hidden" id="estadoDoc" name="estadoDoc"/>
		<input type="hidden" id="idEnvioDoc" name="idEnvioDoc"/>
		<input type="hidden" id="referenciaDoc" name="referenciaDoc"/>
		<input type="hidden" id="tipoPolizaDoc" name="tipoPolizaDoc"/>
		<input type="hidden" id="fechaEstadoDoc" name="fechaEstadoDoc"/>
		<input type="hidden" id="numDoc" name="numDoc"/>
		<input type="hidden" id="planDoc" name="planDoc" value="${plan}"/>
		<input type="hidden" id="numIncidenciaDoc" name="numIncidenciaDoc"/>
		<input type="hidden" id="referenciaDocVuelta" name="referencia" value="${referencia}"/>
		<input type="hidden" id="lineaDocVuelta" name="linea" value="${linea}"/>
		<input type="hidden" id="nomblineaDocVuelta" name="nomblineaDocVuelta" value="${nomLinea}"/>
		<input type="hidden" id="fechaEnvio" name="fechaEnvio" value="${fechaEnvio}" />
		<input type="hidden" id="idPolDocVuelta" name="idPolDocVuelta" value="${idPoliza}"/>
		<input type="hidden" id="nombreDocVuelta" name="nombreDocVuelta" value="${nombreAsegurado}"/>
		<input type="hidden" id="moduloDocVuelta" name="moduloDocVuelta" value="${modulo}"/>
		
	</form:form>
	
</body>
</html>