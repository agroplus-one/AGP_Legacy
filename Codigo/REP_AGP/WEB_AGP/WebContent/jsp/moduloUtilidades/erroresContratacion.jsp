<%@ include file="/jsp/common/static/taglibs.jsp" %>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
<head>
<title>Visualizacion Acuse Recibo</title>

<%@ include file="/jsp/common/static/metas.jsp"%>

<!-- Estilos -->
<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

<script type="text/javascript" src="jsp/js/util.js"></script>
<script type="text/javascript" src="jsp/js/menuapli.js"></script>
<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js"></script>
<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
<script type="text/javascript">
		
		function volver() {
			//alert($('#fromUtilidades').val());
			//alert($('#operacion').val());
			if($('#operacion').val() == 'siniestro'){
				if ($('#fromUtilidades').val() == 'true') {
					$("#volverListado").submit();
				}
				else {
					$(window.location).attr('href', 'siniestros.html?method=doConsulta' + '&idPoliza=' + ${idPoliza});
				} 
				
			}
			if($('#operacion').val() == 'anexoModificacion'){
				if ($('#vieneDeListadoAnexosMod').val() == 'true') {
					$("#volverUtilidadesAnexo").submit();
				}else{
					$(window.location).attr('href', 'declaracionesModificacionPoliza.html?method=doConsulta' + '&idPoliza=' + ${idPoliza});
				}
			}
			if($('#operacion').val() == 'reduccionCapital'){
				if ($('#fromUtilidades').val() == 'true') {
					$(window.location).attr('href', 'declaracionesReduccionCapital.html?method=doConsulta' + '&idPoliza=' + ${idPoliza});
					//$("#volverListado").submit();
				}else {
		 			$(window.location).attr('href', 'utilidadesReduccionCapital.run?method=doConsulta' + '&idPoliza=' + ${idPoliza});
		 			
		 		}
		 	}  
		 	if($('#operacion').val() == 'poliza'){
		 		$(window.location).attr('href', 'utilidadesPoliza.html?operacion=volver'+ '&polizaOperacion=' + ${idPoliza});
		 	} 
		 	if($('#operacion').val() == 'selecpoliza'){
		 		$(window.location).attr('href', 'seleccionPoliza.html?operacion=volver'+ '&polizaOperacion=' + ${idPoliza});
		 	} 
		 	
		 	
		}
</script>


</head>

<c:if test="${operacion ==  'selecpoliza'}">
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub3');">
</c:if>
<c:if test="${operacion !=  'selecpoliza'}">
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="SwitchMenu('sub4');">
</c:if>


<%@ include file="/jsp/common/static/cabecera.jsp"%>
<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

<div id="buttons">
<table width="97%" cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td align="right"><a class="bot" id="botonVolver" href="javascript:volver();">Volver</a></td>
	</tr>
</table>
</div>

	<form name="volverListado" id="volverListado" action="utilidadesSiniestros.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>			
			<input type="hidden" name="volver" id="volver" value="volver"/>
			<input type="hidden" name="fromUtilidades" id="fromUtilidades" value="${fromUtilidades}"/>
	</form>
	<form name="volverUtilidadesAnexo" id="volverUtilidadesAnexo" action="anexoModificacionUtilidades.run" method="post">
			<input type="hidden" name="method" id="method" value="doConsulta"/>			
			<input type="hidden" name="volver" id="volver" value="volver"/>
			<input type="hidden" name="vieneDeListadoAnexosMod" id="vieneDeListadoAnexosMod" value ="${vieneDeListadoAnexosMod}"/>
	</form>
	

<!-- Contenido de la página -->
<div class="conten" style="padding: 3px; width: 97%">
<p class="titulopag" align="left">ACUSE DE RECIBO DE LA CONFIRMACIÓN DEL ANEXO</p>
<c:if test="${errLength > 0}">
	<display:table requestURI="" class="LISTA"
		decorator="com.rsi.agp.core.decorators.ModelTableDecoratorListaParcelas" defaultsort="0" defaultorder="ascending" sort="list" name="${errores}"
		list="errorList" id="error" pagesize="${errLength}">
		<display:setProperty name="paging.banner.onepage" value="&nbsp;" />
		<display:setProperty name="paging.banner.item_name" value="error" />
		<display:setProperty name="paging.banner.items_name" value="errores" />
		<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="Código del Error" sortable="true">
			<c:out value="${error.codigo}" />
		</display:column>
		<display:column class="literal" style="text-align:left" headerClass="cblistaImg" title="Tipo de Error" sortable="false">
			<c:if test="${error.tipo eq 3}">
				<img src="jsp/img/displaytag/accept.png" alt="Correcto" title="Correcto" />
			</c:if>
			<c:if test="${error.tipo eq 1}">
				<img src="jsp/img/displaytag/cancel.png" alt="Rechazado" title="Rechazado" />
			</c:if>
			<c:if test="${error.tipo eq 2}">
				<img src="jsp/img/displaytag/warning.gif" alt="Con Errores" title="Con Errores" />
			</c:if>
		</display:column>
		<display:column class="literal" style="text-align:left;" maxLength="150" headerClass="cblistaImg" title="Descripción del Error">
			<c:out value="${error.descripcion}" />
		</display:column>
		<display:column class="literal" style="text-align:left;" headerClass="cblistaImg" title="Número de Parcela">
			<c:set var="xpath" value="${fn:substring(error.localizacion.xpath, fn:indexOf(error.localizacion.xpath, 'numero=')+7, fn:indexOf(error.localizacion.xpath, ']/'))}" />
			<c:set var="xpath2" value="${fn:substring(error.localizacion.xpath, fn:indexOf(error.localizacion.xpath, 'hoja=')+5, fn:indexOf(error.localizacion.xpath, 'and')-1)}" />
							
			<c:catch var="e">
         						<fmt:parseNumber var="j" type="number" value="${xpath2}" />
         						<c:out value="${j}" escapeXml="false" />
       					</c:catch>
       					-
			<c:catch var="e">
         						<fmt:parseNumber var="i" type="number" value="${xpath}" />
         						<c:out value="${i}" escapeXml="false" />
       					</c:catch>
       					<c:out value=" " escapeXml="false" />
		</display:column>
	</display:table>
</c:if> <c:if test="${empty errLength}">
	<form id="theForm" name="theForm" method="post" action="login.html">
	<br />
	<br />
	<table width="30%" align="center">
		<tr>
			<td class="literal"><strong>Aviso:</strong> 
			<span class="detalnumNI">No se han encontrado datos del Acuse de Recibo.</span> <br />
			<br />
			</td>
		</tr>
	</table>
	</form>
</c:if></div>

<form name="main" action="">
<input type="hidden" name="idPoliza" id="idPoliza" value="${idPoliza}" />
<input type="hidden" name="operacion" id="operacion" value="${operacion}" />
</form>

</body>
</html>