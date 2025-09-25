
<c:if test="${not empty requestScope.alerta }">
	<div id="panelAlertas" style="width:1000px;color:black;border:1px solid #DD3C10;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8;margin-left:auto;margin-right:auto;">
			<c:out value="${requestScope.alerta }"/>
	</div>
</c:if>
<c:if test="${empty requestScope.alerta and not empty requestScope.mensaje }">
	<div id="panelInformacion" style="width:500px;color:black;border:1px solid #FFCD00;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FCF6CF;margin-left:auto;margin-right:auto;">
		<c:out value="${requestScope.mensaje }"/>
	</div>
</c:if>
<c:if test="${empty requestScope.alerta and not empty requestScope.mensaje1 }">
	<div id="panelInformacion" style="width:500px;color:black;border:1px solid #FFCD00;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FCF6CF;margin-left:auto;margin-right:auto;">
		<c:out value="${requestScope.mensaje1 }"/>
	</div>
</c:if>
<c:if test="${empty requestScope.alerta and not empty requestScope.mensaje2 }">
	<div id="panelInformacion" style="width:500px;color:black;border:1px solid #FFCD00;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FCF6CF;margin-left:auto;margin-right:auto;">
		<c:out value="${requestScope.mensaje2 }"/>
	</div>
</c:if>
<c:if test="${empty requestScope.alerta and not empty requestScope.mensaje3 }">
	<div id="panelInformacion" style="width:500px;color:black;border:1px solid #FFCD00;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FCF6CF;margin-left:auto;margin-right:auto;">
		<c:out value="${requestScope.mensaje3 }"/>
	</div>
</c:if>

<c:if test="${not empty requestScope.alerta2 }">
	<c:forEach items="${requestScope.alerta2}" var="cadena">
		<div id="panelAlertas" style="width:500px;color:black;border:1px solid #DD3C10;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8;margin-left:auto;margin-right:auto;">
		<c:out value="${cadena }"/>
		</div>	
		<div height="100px"></div>
	</c:forEach>
</c:if>
<c:if test="${not empty requestScope.subvencionesAsegurado.alerta2 }">
	<c:forEach items="${requestScope.subvencionesAsegurado.alerta2}" var="cadena">
		<div id="panelAlertas" style="width:1000px;color:black;border:1px solid #DD3C10;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8;margin-left:auto;margin-right:auto;">
		<c:out value="${cadena }"/>
		</div>	
		<div height="100px"></div>
	</c:forEach>
</c:if>
<c:if test="${not empty requestScope.alertaLargo}">
	<div id="panelAlertas" style="width:500px;height:100px;color:black;border:1px solid #DD3C10;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8;align:center;margin-left:auto;margin-right:auto;">
		<c:out value="${alertaLargo}"/>
		</div>	
	
	
</c:if>

<c:if test="${not empty requestScope.siniestrosInfoAlertas }">
	<c:forEach items="${requestScope.siniestrosInfoAlertas}" var="cadena">
		<div id="panelAlertas" style="width:100%;color:black;border:1px solid #DD3C10;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8;margin-top:20px;margin-left:auto;margin-right:auto;">
			<c:out value="${cadena}"/>
		</div>
	</c:forEach>
</c:if>

<c:if test="${not empty requestScope.alertaCol }">
	<div id="panelAlertas"
		style="width: 550px; color: black; border: 1px solid #DD3C10; display: block; font-size: 12px; font-style: italic; font-weight: bold; line-height: 20px; background-color: #FFEBE8; margin-top: 20px; margin-left: auto; margin-right: auto; text-align: justify; padding-left: 20px;">
		<c:forEach items="${requestScope.alertaCol}" var="cadena">
			<li><c:out value="${cadena}" /></li>
		</c:forEach>
	</div>
</c:if>

<c:if test="${not empty requestScope.mensajeCol }">
	<div id="panelInformacion"
		style="width: 550px; color: black; border: 1px solid #FFCD00; display: block; font-size: 12px; font-style: italic; font-weight: bold; line-height: 20px; background-color: #FCF6CF; margin-left: auto; margin-right: auto; text-align: justify; padding-left: 20px;">
		<c:forEach items="${requestScope.mensajeCol}" var="cadena">
			<li><c:out value="${cadena}" /></li>
		</c:forEach>
	</div>
</c:if>