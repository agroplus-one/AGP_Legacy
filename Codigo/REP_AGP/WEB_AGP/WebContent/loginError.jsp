<%@ include file="jsp/common/static/taglibs.jsp"%>


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
	<%@ include file="jsp/common/static/cabecera.jsp"%>
	<form id="theForm" name="theForm" method="post" action="">
		<br/>
		<br/>
		<table width="75%" align="center">
			<tr><td class="literal">
			<strong>Error al autenticar el usuario.</strong> 
			<br /><br />
			<strong>Mensaje: </strong> <span class="detalnumNI">${SPRING_SECURITY_LAST_EXCEPTION.message }</span>
			<br/>
			<br/>
			<jsp:useBean id="now" class="java.util.Date" />
				Póngase en contacto con el administrador indicando el mensaje de error y la fecha y hora en la que se produjo (<fmt:formatDate value="${now}" pattern="EEEE, dd MMMM yyyy, HH:mm" />).
			<br /><br />Pulse <a href="#" onclick="top.window.close();">aqu&iacute;</a> para volver a iniciar sesi&oacute;n.
			</td></tr>
		</table>
		
	</form>

	</body>
</html>