<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>


<html>
	<head>
		<title>Agroplus - Situación Actual Póliza</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	</head>
	<body bgcolor=#FFFFFF leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
	<%@ include file="/jsp/common/static/cabecera.jsp"%>
	<fmt:requestEncoding value="UTF-8"/>
	<form id="theForm" name="theForm" method="post" action="login.html">
		<input type="hidden" name="OP" value="ppal"/>
		<br/>
		<br/>
		<table width="75%" align="center">
			<tr>
				<td class="literal">
					<strong>Se ha producido un error durante la llamada al servicio web de consulta de la contratación:</strong> 
					<br/><br/>
					<span class="detalnumNI"><c:out value="${error}"/></span>
					<br/><br/>
				</td>
			</tr>
		</table>
		
	</form>

	</body>
</html>