<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/jsp/common/static/taglibs.jsp"%>
<jsp:directive.page import="org.displaytag.*" />


<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>


<html>
<head>
        <meta http-equiv="Content-Type" content="text/html;">
        <title>Resultados Validacion</title>

        <%@ include file="/jsp/common/static/metas.jsp"%>

		<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css" />
    	<link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
		<link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />

		<script type="text/javascript" src="jsp/js/util.js" ></script>
		<script type="text/javascript" src="jsp/js/menuapli.js"></script>
		<script type="text/javascript" src="jsp/js/jquery-v1.4.2.js" ></script>
</head>


<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
		<%@ include file="/jsp/common/static/cabecera.jsp"%>
		<%@ include file="/jsp/common/static/menuGeneral.jsp"%>
		<%@ include file="/jsp/common/static/datosCabecera.jsp"%>
		
		<!-- Contenido de la página -->
		<div class="conten" style="padding:3px;width:97%">
			<p class="titulopag" align="left">WebServices</p>
			<form action="webservices.html" method="post" name="main" id="main">
				<c:forEach items="${resultados.errores}" var="error">					
					<c:forEach items="${error.value}" var="descError">
						<c:out value="${error.key}"/>
						<c:forEach items="${descError}" var="descripcion">
							<c:out value="${descripcion}"/>
						</c:forEach>						
					</c:forEach>
				</c:forEach>
			</form>
		</div>	
</body>
</html>