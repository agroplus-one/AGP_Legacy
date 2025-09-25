<%@ include file="/jsp/common/static/taglibs.jsp" %>

<fieldset style="overflow: width: 75%;">
	<legend class="literal">
		<c:if test="${param.tipoDC == 0}">Distribuci&oacute;n de costes de la p&oacute;liza con la modificaci&oacute;n</c:if>
		<c:if test="${param.tipoDC == 1}">Diferencias con la p&oacute;liza original</c:if>
	</legend>

	<table id="oculto" width="100%" style="border: 1px;"  align="left">
		<thead>
			<tr>
				<td  width="25%" class="literalbordeCabecera">IMPORTES</td>
				<td  width="25%" class="literalbordeCabecera">BONIFICACIÓN/RECARGO</td>
				<td  width="25%" class="literalbordeCabecera">SUBV.ENESA</td>
				<td  width="25%" class="literalbordeCabecera">SUBV.CCAA</td>
			</tr>
		</thead>
		<tbody>
		<tr>
		<td colspan="4">
		
			<c:if test="${param.tipoDC == 0}">
				<c:forEach items="${distribucionCostes}" var="distribucionCostes">
					<fieldset style="overflow: width: 100%;border:1px solid lightblue; ">
					<legend class="literal">${distribucionCostes.descGrupoNegocio}</legend>
					<table border= 0 width="100%">
				
					<tr>
							<c:set var="distribucionCostes" value="${distribucionCostes}" scope="request"></c:set>									
							<jsp:include page="/jsp/moduloUtilidades/modificacionesPoliza/tablaDistribucionCostesInterna.jsp">
								<jsp:param value="0" name="tipoDC"/>
							</jsp:include>
					</tr>
					</table>	
					</fieldset>
				</c:forEach>
			</c:if>
			
			<c:if test="${param.tipoDC == 1}">
				<c:forEach items="${diferenciaCostes}" var="diferenciaCostes">
					<fieldset style="overflow: width: 100%;border:1px solid lightblue; ">
					<legend class="literal">${diferenciaCostes.descGrupoNegocio}</legend>
					<table border= 0 width="100%">
					
					<tr>
						<c:set var="diferenciaCostes" value="${diferenciaCostes}" scope="request"></c:set>
						<jsp:include page="/jsp/moduloUtilidades/modificacionesPoliza/tablaDistribucionCostesInterna.jsp">				
							<jsp:param value="1" name="tipoDC"/>
						</jsp:include>
					</tr>
					</table>	
					</fieldset>
				</c:forEach>	
			</c:if>
		<td>
		</tr>
			<!-- NETO TOMADOR -->
			<tr>
				<td colspan="4">
					<table id="tomador" width="100%" bgcolor="lightblue" style="border: solid 1px;">
						<tr>
							<td style="color: #FF0000; text-align: left; font-weight: bold;">
								<c:if test="${param.tipoDC == 1}">DIFERENCIA </c:if>NETO TOMADOR:
							</td>
							<td style="color: #FF0000; text-align: right; font-weight: bold;">
								<c:if test="${param.tipoDC == 0}">${distribucionCostes.totalCosteTomador}</c:if>
								<c:if test="${param.tipoDC == 1}">${diferenciaCostes.totalCosteTomador}</c:if>
							</td>
						</tr>
					</table>
				</td>
			</tr>

			<!-- Desglose de comisiones -->			
			<c:if test="${param.tipoDC == 0}">
				<c:set var="object" value="${desgloseComisiones}" />
				<%@ include file="/jsp/moduloPolizas/polizas/importes/tablaDesgloseComisiones.jsp" %>
			</c:if>
			
		</tbody>
	</table>
				
</fieldset>				