<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/jsp/common/static/taglibs.jsp"%>


<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
	<head>
		<title>Agroplus - Página de error</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	</head>
	<body bgcolor=#FFFFFF leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
	<%@ include file="../../common/static/cabecera.jsp"%>
	<fmt:requestEncoding value="UTF-8"/>
	<form id="theForm" name="theForm" method="post" action="">
		<br/>
		<br/>
		<table width="75%" align="center">
			<tr><td class="literal">
			<strong>Se ha producido un error:</strong> 
			<span class="detalnumNI">En estos momentos no está disponible el PDF de la p&oacute;liza.</span>			
			<br/>
			<br/>
			Pulse <a href="javascript:window.close();">aqu&iacute;</a> para cerrar esta ventana.
			</td></tr>
		</table>
		
	</form>

	</body>
</html>