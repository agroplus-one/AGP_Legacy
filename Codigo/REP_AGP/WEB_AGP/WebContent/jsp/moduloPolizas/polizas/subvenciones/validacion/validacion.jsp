<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>

<html>
<head>
        <meta http-equiv="Content-Type" content="text/html;">
		<title>Resultado Validación</title>
		
		<%@ include file="/jsp/common/static/metas.jsp"%>
		
		
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css"/>
    <link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
    <link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />		
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0">

<%@ include file="../../common/static/cabecera.jsp"%>
<%@ include file="../../common/static/menuLateralTaller.jsp"%>
<%@ include file="../../common/static/datosCabeceraTaller.jsp"%>

	<!-- Buttons -->
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td align="right">
						<table cellspacing="2" cellpadding="0" border="0">
							<tbody>
								<tr>
							<!--    <td>
										<a class="bot" href="">Volver</a>
									</td> -->
									<td>
										<a class="bot" href="javascript:elecmodulos.continuar();">Continuar</a>
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
	<form name="main" action="" method="post" id="">
		<input type="hidden" name="" value""/>
		<input type="hidden" name="" id="">		
	</form>
	
<div class="conten"><!-- pintamos el mensaje de error -->

	 <c:forEach items="${resultados.errores}" var="valida">
		<c:out value="${valida.value}" />
  	</c:forEach> 

	<%@ include file="/jsp/common/static/piePagina.jsp"%>

</body>
</html>