<%@ include file="/jsp/common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Agroplus - Acceso para pruebas</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	</head>
	<body bgcolor=#FFFFFF leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
	<%@ include file="jsp/common/static/cabecera.jsp"%>
	
	<!-- INTERACTIVE PAGE -->
	<form id="theForm" name="theForm" method="post" action="j_spring_security_check">
		<input type="hidden" name="OP" value="back_door" />
		<table width="30%">
			<colgroup>
				<col align="left" width="40%" class="literal">
			</colgroup>
			<tr><td>Acceso Privado a la aplicaci&oacute;n Agroplus</td></tr>
			<tr><td></td></tr>
			<tr><td></td></tr>
		</table>

		<c:if test="${param.login_error == 1}">
			<table width="30%">
				<tr>
					<td class="literal">
						<span class="detalnumNI">El usuario indicado no existe o no tiene acceso a Agroplus</span>
					</td>
				</tr>
			</table>
		</c:if>

		<table width="25%">
			<colgroup>
				<col align="left" width="5%">
				<col align="left" width="10%">
			</colgroup>
			<tr>
				<td><input type="text" id="p_usuario" name="j_username" size="10" maxlength="7" class="dato"/></td>
				<td><a class="bot" href="javascript:theForm.submit();">Login</a></td>
			</tr>

		</table>
		
	</form>
	<!-- END INTERACTIVE PAGE -->
	</body>
</html>