<!-- 
/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  28/06/2010  Ernesto Laura		Página para la consulta del historico de importaciones    
*
 **************************************************************************************************
*/
-->
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<jsp:directive.page import="org.displaytag.*" />


<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
	<head>
		<title>Agroplus - Hist&oacute;rico importaciones</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script src="jsp/js/jquery-v1.4.2.js" type="text/javascript"></script>
		<script src="jsp/js/util.js" type="text/javascript"></script>
		<script src="jsp/moduloTaller/importaciones/detallehistorico.js" type="text/javascript"></script>
	</head>
	
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">	
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<%@ include file="/jsp/common/static/menuLateralTaller.jsp"%>
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
	<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td><a class="bot" href="javascript:detallehistorico.volver();">Volver</a>
								</td>
							</tr>
						</tbody>
					</table>
					<!-- FIN TABLA BARRA DE BOTONES-->
				</td>
			</tr>
	</table>
	</div>	
	<!-- Contenido de la página -->
	<div class="conten" style="padding:3px;width:97%">
	<p class="titulopag" align="left">Detalle hist&oacute;rico</p>
	<form action="importacion.html" method="post" id="frmDetalleImp">
	<input type="hidden" name="operacion" value=""/>
	<input type="hidden" name="tabla" value=""/>
	<input type="hidden" name="idhistorico" value="${detHistorico.id}"/>
	<input type="hidden" name="comeFrom" id="comeFrom" value="${detHistorico.comeFrom }"/>
	<input type="hidden" name="lineaSeguroId" value="${detHistorico.listaDetalle.linea.lineaseguroid}"/>
	<input type="hidden" name="codPlan" value="${detHistorico.listaDetalle.linea.codplan}"/>
	<input type="hidden" name="codLinea" value="${detHistorico.listaDetalle.linea.codlinea}"/>
	
	<fieldset style="width:90%;margin:0 auto;">
			<legend class="literal">Datos Importaci&oacute;n</legend>
			<table width="95%">
				<tr>
					<td class="literal">Plan:</td>
					<td class="detalI">${detHistorico.listaDetalle.linea.codplan}</td>
					<td class="literal">L&iacute;nea:</td>
					<td class="detalI">${detHistorico.listaDetalle.linea.codlinea}</td>
					<td class="literal">Fecha de Activaci&oacute;n:</td>
					<td class="detalI">
						<c:if test="${detHistorico.listaDetalle.linea.fechaactivacion!=null}">
							<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${detHistorico.listaDetalle.linea.fechaactivacion}"/>
						</c:if>&nbsp;</td>											
				</tr>
				<tr>
					<td class="literal">Fecha de Importaci&oacute;n:</td>
					<td class="detalI">					
						<c:if test="${detHistorico.listaDetalle.fechaimport!=null}">
							<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${detHistorico.listaDetalle.fechaimport}"/>
						</c:if>&nbsp;</td>					
					<td class="literal">Tipo de Importaci&oacute;n:</td>
					<td class="detalI">${detHistorico.listaDetalle.tipoImportacion.descripcion}</td>
					<td class="literal">Estado:</td>
					<td class="detalI">${detHistorico.listaDetalle.estado}</td>
				</tr>
				<tr>
					<td class="literal">Error:</td>
					<td class="detalI" colspan="5">${detHistorico.listaDetalle.descerror}</td>
				</tr>
			</table>
	</fieldset>
	<br/>
	<table width="100%">
		<tr><td>
		<div class="centraTabla">
        <display:table requestURI="importacion.html" id="listaResultados" class="LISTA" summary="ImportacionTabla" sort="list" pagesize="10" name="${detHistorico.listaDetalle.importacionTablas}" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorDetHistorico" style="width:90%;">
			<display:column class="accionesDisplayTagColumn" headerClass="cblistaImg" title="Acciones" property="impSelec" sortable="false" style="width:58px;text-align:center"/>
            <display:column class="literalDisplayTagDescLargo" headerClass="cblistaImg" title="Tabla" property="impTabla" sortable="true" style="width:180px;"/>
            <display:column class="literalDisplayTagDescCorto" headerClass="cblistaImg" title="Estado" property="impEstado" sortable="true" style="width:60px;"/>            
            <display:column class="literalDisplayTagDescLargo" headerClass="cblistaImg" title="Detalle" property="impDetalle" sortable="false"/> 
            <display:column class="literalDisplayTagDescCorto" headerClass="cblistaImg" title="Fichero" property="impFichero" sortable="true" style="width:80px;"/>           
        </display:table>		
        </div>
		</td></tr>
	</table>
	</form>
	</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	</body>
</html>

