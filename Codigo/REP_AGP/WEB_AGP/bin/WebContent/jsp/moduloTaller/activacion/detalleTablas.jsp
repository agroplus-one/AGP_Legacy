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
		<c:choose>
			<c:when test="${detHistorico.tablasGenerales eq true}">
				<title>Agroplus - Tablas del Condicionado General</title>
			</c:when>
			<c:otherwise>
				<title>Agroplus - Hist&oacute;rico importaciones</title>
			</c:otherwise>
		</c:choose>
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script src="jsp/js/jquery-v1.4.2.js" type="text/javascript"></script>
		<script src="jsp/js/util.js" type="text/javascript"></script>
		<script src="jsp/moduloTaller/importaciones/detallehistorico.js" type="text/javascript"></script>
		<script>
			
			$(document).ready(function(){
				//DAA 17/09/2013
				if(${detHistorico.tablasGenerales}){
					document.getElementById("fieldset").style.display=("none");
				}
			});	
				
			function volver()
			{
				var frm = document.getElementById('frmDetalleImp');
				frm.operacion.value = '';
				frm.volver.value = '1';
				frm.submit();
			}
		</script>		
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
								<td><a class="bot" href="javascript:volver();">Volver</a>
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
	<c:choose>
			<c:when test="${detHistorico.tablasGenerales eq true}">
				<p class="titulopag" align="left">Tablas del Condicionado General</p>
			</c:when>
			<c:otherwise>
				<p class="titulopag" align="left">Detalle hist&oacute;rico</p>
			</c:otherwise>
		</c:choose>
	
	<form action="activacionlineas.html" method="post" id="frmDetalleImp">
	<input type="hidden" name="operacion" value=""/>
	<input type="hidden" name="tabla" value=""/>
	<input type="hidden" name="idhistorico" value="${detHistorico.id}"/>
	<input type="hidden" name="ROW" value="${detHistorico.ROW}"/>
	<input type="hidden" name="comeFrom" id="comeFrom" value="${detHistorico.comeFrom }"/>
	<input type="hidden" name="lineaSeguroId" value="${detHistorico.historico.linea.lineaseguroid}"/>
	<input type="hidden" name="codPlan" value="${detHistorico.historico.linea.codplan}"/>
	<input type="hidden" name="codLinea" value="${detHistorico.historico.linea.codlinea}"/>
	<input type="hidden" name="volver" value="true"/>
		
	<fieldset style="width:90%;margin:0 auto;" id="fieldset">
			<legend class="literal">Datos Importaci&oacute;n</legend>
			<table width="95%">
				<tr>
					<td class="literal">Plan:</td>
					<td class="detalI">${detHistorico.historico.linea.codplan}</td>
					<td class="literal">L&iacute;nea:</td>
					<td class="detalI">${detHistorico.historico.linea.codlinea}</td>
					<td class="literal">Fecha de Activaci&oacute;n:</td>
					<td class="detalI"><c:if test="${detHistorico.lineaActivacion.fechaactivacion!=null}"><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${detHistorico.lineaActivacion.fechaactivacion}"/></c:if>&nbsp;</td>
				</tr>
				<tr>
					<td class="literal">Fecha de Importaci&oacute;n:</td>
					<td class="detalI"><c:if test="${detHistorico.historico.fechaimport!=null}"><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${detHistorico.historico.fechaimport}"/></c:if></td>
					<td class="literal">Tipo de Importaci&oacute;n:</td>
					<td class="detalI">${detHistorico.historico.tipoImportacion.descripcion}</td>
					<td class="literal">Estado:</td>
					<td class="detalI">${detHistorico.historico.estado}</td>
				</tr>
				<tr>
					<td class="literal">Error:</td>
					<td class="detalI" colspan="5">${detHistorico.historico.descerror}</td>
				</tr>
			</table>
	</fieldset>
	<br/>
	<table width="100%">
		<tr><td>
		<div class="centraTabla">
        <display:table requestURI="" id="listaResultados" class="LISTA" summary="ImportacionTabla" sort="list" pagesize="10" name="${detHistorico.listaDetalle}" decorator="com.rsi.agp.core.decorators.ModelTableDecoratorDetHistorico" style="width:90%;">
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

