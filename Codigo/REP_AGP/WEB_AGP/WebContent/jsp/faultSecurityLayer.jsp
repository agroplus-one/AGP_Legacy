<%@page isErrorPage="true" %>
<%@ include file="common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>



<html>
	<head>
		<title>Agroplus - P&aacute;gina de error</title>
		
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
		<script language="javascript"> 
		function toggle() {
			var ele = document.getElementById("exception");
			var text = document.getElementById("button");
			if(ele.style.display == "block") {
		    	ele.style.display = "none";
				text.innerHTML = "Mostrar";
		  	}
			else {
				ele.style.display = "block";
				text.innerHTML = "Ocultar";
			}
		} 
		</script>
		
	</head>
	<body bgcolor=#FFFFFF leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
	<%@ include file="common/static/cabecera.jsp"%>
	<form id="theForm" name="theForm" method="post" action="">
		<br/>
		<br/>
		<table width="75%" align="center">
			<tr>
				<td colspan="3"><img src="jsp/img/error.gif"/><strong>&nbsp;&nbsp;Se ha producido un error en la aplicaci&oacute;n.<br /><br />
				<br />
				</td>
			</tr>
			<tr>
				<td class="literal">
				<a id="button" href="javascript:toggle();">Mostrar</a> 
				<div id="exception" style="display: none">${exception.message }</div>
				<br/>
				<br/>
				<jsp:useBean id="now" class="java.util.Date" />
				P&oacute;ngase en contacto con el administrador indicando el mensaje de error y la fecha y hora en la que se produjo (<fmt:formatDate value="${now}" pattern="EEEE, dd MMMM yyyy, HH:mm" />).
				
				<br><br>Por favor, cierre la ventana e inicie nuevamente la sesi&oacute;n para continuar.
				</td>
			</tr>
		</table>
		
	</form>

	</body>
</html>