<%@ include file="/jsp/common/static/taglibs.jsp"%>
<jsp:directive.page import="org.displaytag.*" />
<fmt:setBundle basename="displaytag"/>
<c:set var="numReg"><fmt:message key="numElementsPag"/></c:set>

<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", 0);
%>

<html>
<head>
    <title>Agroplus - Relacion campos</title>
    
    <%@ include file="/jsp/common/static/metas.jsp"%>
		
	<link rel="stylesheet" type="text/css" href="jsp/css/apli_ie.css"/>
    <link rel="stylesheet" type="text/css" href="jsp/css/agroplus.css" />
    <link rel="stylesheet" type="text/css" href="jsp/css/displaytag.css" />
    <link rel="stylesheet" type="text/css" href="jsp/css/jquery.alerts.css" />
	<link rel="stylesheet" type="text/css" href="jsp/css/calendar.css" />
    
    <style type="text/css"> 
        .scrollable{ overflow: auto; width: 250px; height: 40px; border: 1px silver solid; } 
        .scrollable select{ border: none; } 
    </style>
</head>
	
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">	
	<!-- Contenido de la pagina -->
	<div class="conten" style="padding:3px;width:100%">
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		<div class="panel2 isrt">	
			<fieldset>
				<legend class="literal">Capitales Asegurados</legend>
				<table align="center" width="100%">
				<thead/>
				<tbody>
					<tr>
						<td>
							<table width="70%" align="center">
							<tr>
								<td class="literal">
									<label for="tipoCapital">Tipo Capital</label>
									<input type="text" size="2" readonly="readonly" />												
									<input type="text" size="25" readonly="readonly" />
									<img src="jsp/img/magnifier.png" style="cursor: hand;" alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />
								</td>
								<td class="literal">
									<label for="superficie">Superficie (hect&aacute;reas)</label>
									<input type="text" size="7" readonly="readonly" />
								</td>
								<td>
								</td>
							</tr>
							</table>
						</td>
						<td width="50px" align="right">
							<a class="bot" id="btnPanelCapAseg" href="#">&lt;&lt;</a>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<fieldset>
								<legend class="literal">Datos variables</legend>
								<div id="contenedorDV" width="100%" style="height:${alturaPanelDV}px;position:relative;" align="left">
									<c:forEach items="${listaDV}" var="dv">
										<%@include file="/jsp/moduloPolizas/polizas/datosVariablesParcela.jsp" %>
									</c:forEach>
								</div>
							</fieldset>
						</td>
					</tr>
				</tbody>
				</table>
			</fieldset>
		</div>
	</div>
</body>
</html>