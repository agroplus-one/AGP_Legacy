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
		<title>Agroplus - Resultado importaciones</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script src="jsp/js/jquery-v1.4.2.js" type="text/javascript"></script>
		<script src="jsp/js/util.js" type="text/javascript"></script>		
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
								<td>&nbsp;
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
	<div class="conten">
		<table width="97%">
			<tr>
				<td align="center">${importaciones.result}</td>
			</tr>
		</table>
	</div> 
	<%@ include file="/jsp/common/static/piePagina.jsp"%>
	</body>
</html>

