<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Mantenimiento de sobreprecio - Prima Mínima</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/jmesa/jmesa.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />

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
		<script type="text/javascript" src="jsp/js/lupaGenerica.js"></script>
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/Sniffer.js"></script>
		<script type="text/javascript" src="jsp/js/ComprobarFechas.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/moduloPolizas/polizas/imprimir.js"></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.blockUI.js" ></script>
		<script type="text/javascript" src="jsp/js/jquery-ui-1.8.16.custom.js" ></script>
		<script type="text/javascript" src="jsp/moduloSbp/mantenimiento/primaMinima.js" ></script>
		<%@ include file="/jsp/js/draggable.jsp"%>
		
		<script type="text/javascript">
		function cargarFiltro(){
			<c:forEach items="${sessionScope.consultaPrimaMinimaSbp_LIMIT.filterSet.filters}" var="filtro">
				
				if ('${filtro.property}' == 'linea.codplan'){
					var inputText = document.getElementById('plan');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'linea.codlinea'){
					var inputText = document.getElementById('linea');
					inputText.value = '${filtro.value}';
				}else if ('${filtro.property}' == 'primaMinima'){
					var inputText = document.getElementById('primaMinima');
					inputText.value = '${filtro.value}';
				}else{
					//var inputText = document.getElementById('${filtro.property}');
					//inputText.value = '${filtro.value}';
				}
			</c:forEach>
		}														 
		</script>
 
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:SwitchSubMenu('sub10','sub8');cargarFiltro();">	
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>

		<div id="buttons">
			<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td align="right">
							<a class="bot" id="btnModificar" style="display:none" href="javascript:modificar();">Modificar</a>	
							<a class="bot" id="btnAlta" href="javascript:alta();">Alta</a>							
							<a class="bot" id="btnConsultar" href="javascript:consultar();">Consultar</a>
							<a class="bot" href="javascript:limpiar();">Limpiar</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
		<p class="titulopag" align="left">Prima M&iacute;nima</p>
		
		
			
			
			<form:form name="main3" id="main3" action="primaMinimaSbp.run" method="post" commandName="primaMinimaBean">
				
				<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
				<input type="hidden" name="method" id="method" />
				<input type="hidden" name="idPrimaMinimaSbp" id="idPrimaMinimaSbp"/>
				
				
				<div style="width:97%;margin:0 auto">
					<fieldset>
					<legend class="literal">Filtro</legend>
							<table style="margin:0 auto;">
								<tr>
				
									<td class="literal">Plan</td>
									
									<td class="literal">
										<form:input path="linea.codplan" size="5" maxlength="4" cssClass="dato" id="plan" tabindex="1" onchange="javascript:lupas.limpiarCampos('linea', 'desc_linea');"/>
									</td>
									<td class="literal">L&iacute;nea</td>
									
									<td class="literal" colspan="3">
										<form:input path="linea.codlinea" size="3" maxlength="3" cssClass="dato" id="linea" tabindex="2" onchange="javascript:lupas.limpiarCampos('desc_linea');"/>
										<form:input path="linea.nomlinea" cssClass="dato" id="desc_linea" size="40" readonly="true"/>
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('Linea','principio', '', '');" alt="Buscar Línea" title="Buscar Línea" />							
									</td>
									
									<td class="literal">Prima M&iacute;nima</td>
									<td class="literal">
										<form:input path="primaMinima" id="primaMinima" size="9" maxlength="9" cssClass="dato" tabindex="3"/>
									</td>
								</tr>	
							</table>
					</fieldset>
				</div>	
				
			</form:form>
					
		
		<div id="grid" style="width: 70%; margin:0 auto">
		  ${consultaPrimaMinimaSbp}		  							               
		</div>   
</div>
		<%@ include file="/jsp/common/static/piePagina.jsp"%>
		<%@ include file="/jsp/common/static/overlay.jsp"%>
		
		<%@ include file="/jsp/common/lupas/lupaLinea.jsp"%>
	</body>
</html>