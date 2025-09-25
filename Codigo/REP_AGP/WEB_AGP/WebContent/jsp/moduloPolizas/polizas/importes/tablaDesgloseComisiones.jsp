<tr>
	<td colspan="4">
			<table align="left">
				<tr>
					<td colspan="4" >
						<c:forEach items="${object.vistaImportesPorGrupoNegocio}" var="comsGruposNeg">
							<c:if test="${comsGruposNeg.comMediadorE != 'N/D' || comsGruposNeg.comMediadorE_S != 'N/D'}">											
								<c:set var="compCountComsGn" value="${compCountComsGn + 1}" />
									<td>
										<table>
											<c:if test="${comsGruposNeg.comMediadorE != 'N/D'}">
												<tr>
													<td class="literal">Comisión Entidad ${comsGruposNeg.descGrupNeg}:</td><td class="detaldatoD"> ${comsGruposNeg.comMediadorE}</td>
												</tr>
											</c:if>
											<c:if test="${comsGruposNeg.comMediadorE_S != 'N/D'}">
												<tr>
													<td class="literal" >Comisión E-S Mediadora ${comsGruposNeg.descGrupNeg}:</td><td class="detaldatoD"> ${comsGruposNeg.comMediadorE_S}</td>
												</tr>
											</c:if>
											
										</table>
									</td>
							</c:if>
						</c:forEach>
						<c:forEach items="${object.vistaImportesPorGrupoNegocio}" var="comsGruposNeg">
							<c:if test="${comsGruposNeg.codGrupoNeg eq 'TOTALES'}">											
								<c:set var="compCountComsGn" value="${compCountComsGn + 1}" />
									<td>
										<table>
										<c:if test="${comsGruposNeg.totalMediadorE != 'N/D'}">
											<tr>
													<td class="literal">Total:</td><td class="detaldatoD">${comsGruposNeg.totalMediadorE}</td>
											</tr>
										</c:if>
										<c:if test="${comsGruposNeg.totalMediadorE_S != 'N/D'}">
											<tr>
													<td class="literal">Total:</td><td class="detaldatoD"> ${comsGruposNeg.totalMediadorE_S}</td>
											</tr>
										</c:if>
									</table>
									</td>
							</c:if>
						</c:forEach>
					</td>
				</tr>
			</table>
	</td>
</tr>