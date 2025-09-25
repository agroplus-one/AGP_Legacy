
<c:if test="${empty requestScope.activacion.alerta2 and not empty requestScope.activacion.mensaje }">
	<div id="panelInformacion" style="width:500px;height:20px;color:black;border:1px solid #FFCD00;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FCF6CF;margin:0 auto;">
		<c:out value="${requestScope.activacion.mensaje }"/>
	</div>
</c:if>
<c:if test="${not empty requestScope.activacion.alerta2 }">
	<c:forEach items="${requestScope.activacion.alerta2}" var="cadena">
		<div id="panelAlertas" style="width:1000px;height:20px;color:black;border:1px solid #DD3C10;display:block;
			font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8;margin:0 auto;">
		<c:out value="${cadena }"/>
		</div>	
		<div height="100px"></div>
	</c:forEach>
</c:if>