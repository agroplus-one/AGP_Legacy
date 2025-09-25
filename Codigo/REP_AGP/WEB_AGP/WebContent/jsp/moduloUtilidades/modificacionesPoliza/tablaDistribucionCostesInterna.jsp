<%@ include file="/jsp/common/static/taglibs.jsp" %>


				<!-- IMPORTES -->
				<td valign="top" align="center" width="25%" >
				
					<table>
						<tr class="literalborde">
							<td class="literalborde" style="border: 0px; text-align: left;">
								<c:if test="${param.tipoDC == 1}">Diferencia </c:if>Prima Comercial:
							</td>
							<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
								<c:if test="${param.tipoDC == 0}">${distribucionCostes.primaComercial}</c:if>
								<c:if test="${param.tipoDC == 1}">${diferenciaCostes.primaComercial}</c:if>
							</td>
						</tr>
						<tr class="literalborde">
							<td class="literalborde" style="border: 0px; text-align: left;">
								<c:if test="${param.tipoDC == 1}">Diferencia </c:if>Prima Comercial Neta :
							</td>
							<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
								<c:if test="${param.tipoDC == 0}">${distribucionCostes.primaComercialNeta}</c:if>
								<c:if test="${param.tipoDC == 1}">${diferenciaCostes.primaComercialNeta}</c:if>
							</td>
						</tr>
						<tr class="literalborde">
							<td class="literalborde" style="border: 0px; text-align: left;">
								<c:if test="${param.tipoDC == 1}">Diferencia </c:if>Recargo Consorcio:
							</td>
							<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
								<c:if test="${param.tipoDC == 0}">${distribucionCostes.recargoConsorcio}</c:if>
								<c:if test="${param.tipoDC == 1}">${diferenciaCostes.recargoConsorcio}</c:if>
							</td>
						</tr>
						<tr class="literalborde">
							<td class="literalborde" style="border: 0px; text-align: left;">
								<c:if test="${param.tipoDC == 1}">Diferencia </c:if>Recibo Prima:
							</td>
							<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
								<c:if test="${param.tipoDC == 0}">${distribucionCostes.reciboPrima}</c:if>
								<c:if test="${param.tipoDC == 1}">${diferenciaCostes.reciboPrima}</c:if>
							</td>
						</tr>
						<tr class="literalborde">
							<td class="literalborde"  style="border: 0px; text-align: left;">
								<c:if test="${param.tipoDC == 1}">Diferencia </c:if>Coste Tomador:
							</td>
							<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
								<c:if test="${param.tipoDC == 0}">${distribucionCostes.costeTomador}</c:if>
								<c:if test="${param.tipoDC == 1}">${diferenciaCostes.costeTomador}</c:if>
							</td>
						</tr>
					</table>
				</td>
				
				<!-- BONIFICACIÓN/RECARGO -->
				<td valign="top" align="center" width="25%" >
					<table>
						<!-- RECARGOS POR AVAL Y FRACCIONAMIENTO -->
						<c:if test="${param.tipoDC == 0}">							
							<c:if test="${not empty distribucionCostes.recargoAval}">
								<tr class="literalborde">
									<td class="literalborde" style="border: 0px; text-align: left;">
										Recargo Aval:
									</td>
									<td class="literalborde"  style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
										${distribucionCostes.recargoAval}
									</td>
								</tr>
							</c:if>
							<c:if test="${not empty distribucionCostes.recargoFraccionamiento}">
								<tr class="literalborde">
									<td class="literalborde" style="border: 0px; text-align: left;">
										Recargo Fraccionamiento:
									</td>
									<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
										${distribucionCostes.recargoFraccionamiento}
									</td>
								</tr>
							</c:if>
						</c:if>
						<c:if test="${param.tipoDC == 1}">
							<c:if test="${not empty diferenciaCostes.recargoAval}">
								<tr class="literalborde">
									<td class="literalborde" style="border: 0px; text-align: left;">
										Diferencia Recargo Aval:
									</td>
									<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
										${diferenciaCostes.recargoAval}
									</td>
								</tr>
							</c:if>
							<c:if test="${not empty diferenciaCostes.recargoFraccionamiento}">
								<tr class="literalborde">
									<td class="literalborde" style="border: 0px; text-align: left;">
										Diferencia Recargo Fraccionamiento:
									</td>
									<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
										${diferenciaCostes.recargoFraccionamiento}
									</td>
								</tr>
							</c:if>
						</c:if>
						
						<!-- BONIFICACIONES Y RECARGOS DEVUELTOS POR LA DISTRIBUCIÓN DE COSTES -->
						<c:if test="${param.tipoDC == 0}">
							<c:forEach items="${distribucionCostes.anexoModBonifRecargoses}" var="bonifRecargo">
								<tr class="literalborde">
									<td class="literalborde" style="border: 0px; text-align: left;">
										${bonifRecargo.descripcion}
									</td>
									<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
										${bonifRecargo.importe}
									</td>
								</tr>
							</c:forEach>
						</c:if>
						<c:if test="${param.tipoDC == 1}">
							<c:forEach items="${diferenciaCostes.anexoModBonifRecargoses}" var="bonifRecargo">
								<tr class="literalborde">
									<td class="literalborde" style="border: 0px; text-align: left;">
										Diferencia ${bonifRecargo.descripcion}
									</td>
									<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
										${bonifRecargo.importe}
									</td>
								</tr>
							</c:forEach>
						</c:if>
						
					</table>
				</td>
				
				<!-- SUBVENCIONES ENESA -->
				<td valign="top" align="center" width="25%" >
					<table>
						<c:if test="${param.tipoDC == 0}">
							<c:forEach items="${distribucionCostes.anexoModSubvEnesas}" var="subvEnesa">
								<tr class="literalborde">
									<td class="literalborde" style="border: 0px; text-align: left;">
										${subvEnesa.descripcion}
									</td>
									<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
										${subvEnesa.importe}
									</td>
								</tr>
							</c:forEach>
						</c:if>
						<c:if test="${param.tipoDC == 1}">
							<c:forEach items="${diferenciaCostes.anexoModSubvEnesas}" var="subvEnesa">
								<tr class="literalborde">
									<td class="literalborde" style="border: 0px; text-align: left;">
										Diferencia ${subvEnesa.descripcion}
									</td>
									<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
										${subvEnesa.importe}
									</td>
								</tr>
							</c:forEach>
						</c:if>
					</table>
				</td>
				
				
				<!-- SUBVENCIONES CCAA -->
				<td  valign="top" align="center" width="25%" >
					<table>
						<c:if test="${param.tipoDC == 0}">
							<c:forEach items="${distribucionCostes.anexoModSubvCCAAs}" var="subvCCAA">
								<tr class="literalborde">
									<td class="literalborde" style="border: 0px; text-align: left;">
										${subvCCAA.descripcion}
									</td>
									<td class="literalborde" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
										${subvCCAA.importe}
									</td>
								</tr>
							</c:forEach>
						</c:if>
						<c:if test="${param.tipoDC == 1}">
							<c:forEach items="${diferenciaCostes.anexoModSubvCCAAs}" var="subvCCAA">
								<tr class="literalborde">
									<td class="literalborde" align="left" style="border: 0px; text-align: left;">
										Diferencia ${subvCCAA.descripcion}
									</td>
									<td class="literalborde" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">
										${subvCCAA.importe}
									</td>
								</tr>
							</c:forEach>
						</c:if>
					</table>
				</td>
<!-- style="border: 0px;text-align: left;" -->


