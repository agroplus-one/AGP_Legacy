<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="common/static/taglibs.jsp"%>
<%@ include file="/jsp/common/static/setHeader.jsp"%>
<html>
	<head>
		<title>Estado de la base de datos</title>
		<%@ include file="/jsp/common/static/metas.jsp"%>
		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
	</head>
	<body bgcolor=#FFFFFF leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
	<%@ include file="common/static/cabecera.jsp"%>
		
		<div class="centered-message">
			<c:choose>
				<c:when test="${status}">
					<p>
						<img alt="La base de datos responde correctamente" src="jsp/img/displaytag/accept.png" />
						La base de datos responde correctamente
					</p>
				</c:when>
				<c:otherwise>
					<p>
						<img alt="La base de datos NO responde" src="jsp/img/displaytag/cancel.png" /> 
						La base de datos NO responde
					</p>
				</c:otherwise>
			</c:choose>
		</div>
	</body>
</html>