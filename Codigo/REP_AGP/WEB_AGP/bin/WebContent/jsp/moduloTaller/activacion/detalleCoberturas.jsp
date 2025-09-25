<!-- 
/*
**************************************************************************************************
*
*  CREACION:
*  ------------
*
* REFERENCIA  FECHA       AUTOR             DESCRIPCION
* ----------  ----------  ----------------  ------------------------------------------------------
* P000015034  14/07/2010  Ernesto Laura		Página para la consulta y activación de planes    
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

<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>


<html>
	<head>
		<title>Agroplus - Activaci&oacute;n de L&iacute;neas</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link type="text/css" rel="stylesheet"  href="jsp/css/apli_ie.css" />
		<link type="text/css" rel="stylesheet"  href="jsp/css/agroplus.css" />
		<link type="text/css" rel="stylesheet"  href="jsp/css/displaytag.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"  ></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"   ></script>
		<script type="text/javascript" src="jsp/moduloTaller/activacion/activacionlineas.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.validate.js"></script>
		<script type="text/javascript" src="jsp/js/additional-methods.js"></script>	
		<script type="text/javascript" src="jsp/js/setMenuLink.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.selectboxes.js"></script>
		<script type="text/javascript" src="jsp/js/jquery.displaytag-ajax-1.2.js" ></script>
		<script type="text/javascript" src="jsp/js/commonAjax.js"></script>
		<script type="text/javascript" src="jsp/js/cuadroCoberturas.js"></script>
		
		<script type="text/javascript">
		
		function volver(volver){
			
			if(volver == "coberturas"){
				var frm = document.getElementById('frmdetCob');
				frm.volver.value ="1";
				document.forms.frmdetCob.submit();
			}else{
			  document.getElementById('operacionVolver').value = "volverClase";
			  $("#method").val("volverClase");
			  $("#frmdetCob").validate().cancelSubmit = true;			
			  $("#frmdetCob").submit();
			}
		}
		
		function showdata(id) {
			ID1 = document.getElementById(id+".1");
			if(ID1.style.display == '') {
				ID1.style.display = 'none';
			}
			else {
				ID1.style.display = '';
			}
			ID2 = document.getElementById(id+".2");
			if(ID2.style.display == '') {
				ID2.style.display = 'none';
			}
			else {
				ID2.style.display = '';
			}
			ID3 = document.getElementById(id);
			if(ID3.style.display == '') {
				ID3.style.display = 'none';
			}
			else {
				ID3.style.display = '';
			}
		}
		function getModulos(idDiv, codmodulo, idTabla, idlinea, ganadoc){
			if(!document.getElementById(idTabla)){
				modulosTaller(idDiv, codmodulo, idTabla, idlinea, ganadoc);
			}
		}
	
	    </script>

	</head>
	
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">	
	
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	
	<c:choose>   
	    <c:when test="${detCoberturas.tipoMenu=='menuTaller'}"><%@ include file="/jsp/common/static/menuLateralTaller.jsp"%></c:when>
	    <c:when test="${detCoberturas.tipoMenu=='menuGeneral'}"><%@ include file="/jsp/common/static/menuGeneral.jsp"%><script>SwitchMenu('sub3');</script></c:when>
	    <c:when test="${tipoMenu=='menuTaller'}"><%@ include file="/jsp/common/static/menuLateralTaller.jsp"%></c:when>
	    <c:when test="${tipoMenu=='menuGeneral'}"><%@ include file="/jsp/common/static/menuGeneral.jsp"%><script>SwitchMenu('sub3');</script></c:when>
	    <c:otherwise><%@ include file="/jsp/common/static/menuGeneral.jsp"%></c:otherwise>
	</c:choose> 
	<%@ include file="/jsp/common/static/datosCabeceraTaller.jsp"%>
	
	<div id="buttons">
	<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="right">
					<table cellspacing="2" cellpadding="0" border="0">
						<tbody>
							<tr>
								<td>
									<a class="bot" href="javascript:volver('${detCoberturas.volver}')">Volver</a>
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
	<form action="activacionlineas.html" method="post" id="frmdetCob" name="frmdetCob">
	   <input type="hidden" name="action" id="action" value=""/>
	   <input type="hidden" name="modulo" id="modulo" value="" />
	   <input type="hidden" name="operacion" id="operacionVolver" value=""/>
	   <input type="hidden" name="volver" value=""/>
	</form>
	<div class="conten" style="padding:3px;width:97%">
	<p class="titulopag" align="left">Detalle de coberturas</p>
	<fieldset>
			<table border="0" width="100%">
				<tr>
					<td width="20%" class="literal">Plan: ${detCoberturas.plan}</td>
					<td width="80%" class="literal">L&iacute;nea: ${detCoberturas.lineaDesc}</td>
				</tr>
			</table>
	</fieldset>

	 <form method="post" name="frmAux" id="frmAux">
	         <c:choose>
				<c:when test="${detCoberturas.listaModulos!=null}">
					<c:forEach items="${detCoberturas.listaModulos}" var="modulo" varStatus="i"><!-- hacemos una varible contador para asignar mas tarde al check un id distinto para cada iteración  -->
						<fieldset>
							<legend class="literal">MODULO ${modulo.id.codmodulo} - ${modulo.desmodulo}</legend>
							<table width="100%" id="data${modulo.id.codmodulo}.1">
							<tr>
								<td>
									<a href="#" onclick="javascript:showdata('data${modulo.id.codmodulo}');getModulos('data${modulo.id.codmodulo}','${modulo.id.codmodulo}','data${modulo.id.codmodulo}.3','${modulo.linea.lineaseguroid}', ${modulo.linea.esLineaGanadoCount});" title="Mostrar condiciones de coberturas">
										<img src="jsp/img/folderclose.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Mostrar condiciones de coberturas
									</a>                                 
								</td>
							</tr>
							</table>
							<table width="100%" style="display:none" id="data${modulo.id.codmodulo}.2">
								<tr>
									<td>
										<a href="#" onclick="javascript:showdata('data${modulo.id.codmodulo}');" title="Ocultar condiciones de coberturas">
											<img src="jsp/img/folderopen.gif" alt="Desglose" title="Desglose" />&nbsp;&nbsp;Ocultar condiciones de coberturas
										</a> 
									</td>
								</tr>
							</table>
							<div id="data${modulo.id.codmodulo}" style="display:none">							
								<img id="ajaxLoading_data${modulo.id.codmodulo}" src="jsp/img/cargando.gif" width="70%" style="cursor:hand;cursor:pointer;display:none" />	
							</div>
							
							<div id="prueba"></div>								
							
							<table width="100%">
								<tr>					
									<td width="50%" class="literal" align="left0">Admite compl: 
										<c:if test="${modulo.totcomplementarios > 0}">
											<label id="totcomplementarios_si" class="literal">Sí</label>
										</c:if>
										<c:if test="${modulo.totcomplementarios <= 0}">
											<label id="totcomplementarios_no" class="literal">No</label>
										</c:if>							
									</td>
								</tr>
							  </table>		
							</fieldset>
						</c:forEach>
				</c:when>
				<c:otherwise>
					No hay módulos disponibles.
				</c:otherwise>
			</c:choose>
	
	 </form>
	
	
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	</body>
</html>