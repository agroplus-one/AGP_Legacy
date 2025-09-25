<table class="contenImportes" id="tablaImportes" width="100%" style="border: 0px;" align="left" cellpadding="0" cellspacing="1">
	<thead>
		<tr>
			<c:choose>
				<c:when test="${plan < '2015'}">
					<th>
						<img src="jsp/img/icono1.gif" alt="Desglose" title="Desglose" onclick="javascript:showdata('data${compCount}');" onmouseover="this.style.cursor='pointer';" />
					</th>
					<th class="literalbordeCabecera" style="">IMPORTES</th>
					<th class="literalbordeCabecera" style="">BONIF./DESC.</th>
					<th class="literalbordeCabecera" style="">CONSORCIO</th>
					<th class="literalbordeCabecera" style="">SUBV.ENESA</th>
					<th class="literalbordeCabecera" style="">SUBV.CCAA</th>
				</c:when>
				<c:otherwise>
					<td>
						<img src="jsp/img/icono1.gif" alt="Desglose" title="Desglose" onclick="javascript:showdata('fieldset${compCount}');" onmouseover="this.style.cursor='pointer';" />
					</td>
					<td class="literalbordeCabecera" style="" width="25%">IMPORTES</td>
					<td class="literalbordeCabecera" style="" width="25%">BONIFICACIÓN/RECARGO</td>
					<td class="literalbordeCabecera" style="" width="25%">SUBV.ENESA</td>
					<td class="literalbordeCabecera" style="" width="25%">SUBV.CCAA</td>
				</c:otherwise>
			</c:choose>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${object.vistaImportesPorGrupoNegocio}" var="gruposNeg">
			<c:set var="compCountGn" value="${compCountGn + 1}" />
			<tr><td colspan=6>
				<c:if test="${gruposNeg.codGrupoNeg != 'TOTALES'}">		
				<fieldset name="fieldset${compCount}|${compCountGn}" style="border:1px solid #4682B4; padding:2px" id="${gruposNeg.codGrupoNeg}&${object.comparativaSeleccionada}">				
					<legend class="tituloFieldset">${gruposNeg.descGrupNeg}</legend>	
					<table width="100%">									
					<c:choose>
						<c:when test="${plan < '2015'}">
							<tr id="data${compCount}|${compCountGn}">
								<!-- <td></td> -->
								<td valign="top" align="center" width="20%">
									<table >
										<tr class="literalbordeImportes">
											<input type="hidden" name="primaComercial${compCount}|${compCountGn}" id="primaComercial${compCount}|${compCountGn}" value="${gruposNeg.primaComercial}" />
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Prima Comercial:</td>
											<td class="literalbordeImportesImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.primaComercial}</td>
										</tr>
										<tr class="literalbordeImportes">
											<input type="hidden" name="primaNeta${compCount}|${compCountGn}" id="primaNeta${compCount}|${compCountGn}" value="${gruposNeg.primaNeta}" />
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Prima Neta Bonif/Rec:</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.primaNeta}</td>
										</tr>
										<tr class="literalbordeImportes">
											<input type="hidden" name="costeNeto${compCount}|${compCountGn}" id="costeNeto${compCount}|${compCountGn}" value="${gruposNeg.costeNeto}" />
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Coste Neto:</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.costeNeto}</td>
										</tr>
									</table>
								</td>
								<td valign="top" align="center" width="22%">
									<table>
										<c:if test="${gruposNeg.bonifAsegurado != 'N/D'}">
											<input type="hidden" name="bonifAsegurado${compCount}|${compCountGn}" id="bonifAsegurado${compCount}|${compCountGn}" value="${gruposNeg.bonifAsegurado}" />
											<tr class="literalbordeImportes">
												<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Bonif. Aseg. (${gruposNeg.pctBonifAsegurado}):</td>
												<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.bonifAsegurado}</td>
											</tr>
										</c:if>
										<c:if test="${gruposNeg.recargoAsegurado != 'N/D'}">
											<input type="hidden" name="recargoAsegurado${compCount}|${compCountGn}" id="recargoAsegurado${compCount}|${compCountGn}" value="${gruposNeg.recargoAsegurado}" />
											<tr class="literalbordeImportes">
												<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Recargo. Aseg. (${gruposNeg.pctRecargoAsegurado}):</td>
												<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.recargoAsegurado}</td>
											</tr>
										</c:if>
										<input type="hidden" name="bonifMedidaPreventiva${compCount}|${compCountGn}" id="bonifMedidaPreventiva${compCount}|${compCountGn}" value="${gruposNeg.bonifMedidaPreventiva}" />
										<tr class="literalbordeImportes">
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Bonif. M. Prev. (${gruposNeg.pctMedidaPreventiva}):</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.bonifMedidaPreventiva}</td>
										</tr>
										<input type="hidden" name="descuentoContColectiva${compCount}|${compCountGn}" id="descuentoContColectiva${compCount}|${compCountGn}" value="${gruposNeg.descuentoContColectiva}" />
										<tr class="literalbordeImportes">
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Descuentos (${gruposNeg.pctDescContColectiva}):</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.descuentoContColectiva}</td>
										</tr>
									</table>
								</td>
								<td valign="top" align="center" width="20%">
									<table>
										<input type="hidden" name="consorcioReaseguro${compCount}|${compCountGn}" id="consorcioReaseguro${compCount}|${compCountGn}" value="${gruposNeg.consorcioReaseguro}" />
										<tr class="literalbordeImportes">
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Reaseguro Consorcio:</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.consorcioReaseguro}</td>
										</tr>
										<input type="hidden" name="consorcioRecargo${compCount}|${compCountGn}" id="consorcioRecargo${compCount}|${compCountGn}" value="${gruposNeg.consorcioRecargo}" />
										<tr class="literalbordeImportes">
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Recargo Consorcio:</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.consorcioRecargo}</td>
										</tr>
									</table>
								</td>
								<td valign="top" align="center" width="20%">
									<table>
										<c:set var="enesaCount" value="0" />
										<c:forEach var="subEnesa" items="${gruposNeg.subvEnesa}">
											<c:set var="enesaCount" value="${enesaCount + 1}" />
											<tr class="literalbordeImportes">
												<input type="hidden" name="keyEnesa${compCount}|${compCountGn}|${enesaCount}" id="keyEnesa${compCount}|${compCountGn}|${enesaCount}" value="${subEnesa.key}" />
												<input type="hidden" name="valueEnesa${compCount}|${compCountGn}|${enesaCount}" id="valueEnesa${compCount}|${compCountGn}|${enesaCount}" value="${subEnesa.value}" />
												<td class="literalbordeImportes" align="left" style="border: 0px;text-align: left;">${subEnesa.key}:</td>
												<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${subEnesa.value}</td>
											</tr>
										</c:forEach>
										<input type="hidden" name="numEnesa${compCountGn}" id="numEnesa${compCountGn}" value="${enesaCount}" />
									</table>
								</td>
								<td valign="top" align="center" width="20%">
									<table>
										<c:set var="ccaaCount" value="0" />
										<c:forEach var="subCCAA" items="${gruposNeg.subvCCAA}">
											<c:set var="ccaaCount" value="${ccaaCount + 1}" />
											<tr class="literalbordeImportes">
												<input type="hidden" name="keyCCAA${compCount}|${compCountGn}|${ccaaCount}" id="keyCCAA${compCount}|${compCountGn}|${ccaaCount}" value="${subCCAA.key}" />
												<input type="hidden" name="valueCCAA${compCount}|${compCountGn}|${ccaaCount}" id="valueEnesa${compCount}|${compCountGn}|${ccaaCount}" value="${subCCAA.value}" />
												<td class="literalbordeImportes" align="left" style="border: 0px;text-align: left;">${subCCAA.key}:</td>
												<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;">${subCCAA.value}</td>
												<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;">
												</tr>
										</c:forEach>
										<input type="hidden" name="numCCAA${compCount}|${compCountGn}" id="numCCAA${compCount}|${compCountGn}" value="${ccaaCount}" />
									</table>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<!-- POLIZAS >= 2015 -->
							<tr id="data${compCount}-${compCountGn}">
								<!-- <td></td> -->
								<td valign="top" align="center" width="25%">
									<table>
										<tr class="literalbordeImportes">
											<input type="hidden" name="primaComercial${compCount}|${compCountGn}" id="primaComercial${compCount}|${compCountGn}" value="${gruposNeg.primaComercial}" />
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap; text-align: left;">Prima Comercial:</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap; text-align: right;">${gruposNeg.primaComercial}</td>
										</tr>
										<tr class="literalbordeImportes">
											<input type="hidden" name="primaNeta${compCount}|${compCountGn}" id="primaNeta${compCount}|${compCountGn}" value="${gruposNeg.primaNeta}" />
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Prima Comercial Neta :</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.primaNeta}</td>
										</tr>
										<tr class="literalbordeImportes">
											<input type="hidden" name="recargoConsorcio${compCount}|${compCountGn}" id="recargoConsorcio${compCount}|${compCountGn}" value="${gruposNeg.recargoConsorcio}" />
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Recargo Consorcio:</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.recargoConsorcio}</td>
										</tr>
										<tr class="literalbordeImportes">
											<input type="hidden" name="reciboPrima${compCount}|${compCountGn}" id="reciboPrima${compCount}|${compCountGn}" value="${gruposNeg.reciboPrima}" />
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap; text-align: left;">Recibo Prima:</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.reciboPrima}</td>
										</tr>
										<tr class="literalbordeImportes">
											<input type="hidden" name="costeTomador${compCount}|${compCountGn}" id="costeTomador${compCount}|${compCountGn}" value="${gruposNeg.costeTomador}" />
											<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Coste Tomador:</td>
											<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.costeTomador}</td>
										</tr>
									</table>
								</td>
								<td valign="top" align="center"  width="25%">
			
									<table>
										<c:set var="boniRecargo1Count" value="0" />
										<c:forEach var="boniRecargo1" items="${gruposNeg.boniRecargo1}">
											<c:set var="boniRecargo1Count" value="${boniRecargo1Count + 1}" />
											<tr class="literalbordeImportes">
												<input type="hidden" name="keyboniRecargo1${compCount}|${compCountGn}|${boniRecargo1Count}" id="keyboniRecargo1${compCount}|${compCountGn}|${boniRecargo1Count}" value="${boniRecargo1.key}" />
												<input type="hidden" name="valueboniRecargo1${compCount}|${compCountGn}|${boniRecargo1Count}" id="valueboniRecargo1${compCount}|${compCountGn}|${boniRecargo1Count}" value="${boniRecargo1.value}" />
												<td class="literalbordeImportes" align="left" style="border: 0px; text-align: left;">${boniRecargo1.key}:</td>
												<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${boniRecargo1.value}</td>
											</tr>
										</c:forEach>
										<input type="hidden" name="numboniRecargo1${compCount}|${compCountGn}" id="numboniRecargo1${compCount}|${compCountGn}" value="${boniRecargo1Count}" />
									</table>
									<table>
										<!--  <input type="text" name="recargoAval" id="recargoAval" value="${gruposNeg.recargoAval}"/>-->
										<c:if test="${gruposNeg.recargoAval ne 'N/D'}">
											<input type="hidden" name="recargoAval${compCount}|${compCountGn}" id="recargoAval${compCount}|${compCountGn}" value="${gruposNeg.recargoAval}" />
											<tr class="literalbordeImportes">
												<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap; text-align: left;">Importe Recargo Aval:</td>
												<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.recargoAval}</td>
											</tr>
			
											<input type="hidden" name="recargoFraccionamiento${compCount}|${compCountGn}" id="recargoFraccionamiento${compCount}|${compCountGn}" value="${gruposNeg.recargoFraccionamiento}" />
											<tr class="literalbordeImportes">
												<td class="literalbordeImportes" align="left" style="border: 0px; white-space: nowrap;text-align: left;">Importe Recargo Fraccionamiento:</td>
												<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${gruposNeg.recargoFraccionamiento}</td>
											</tr>
										</c:if>
									</table>
								</td>
								<td valign="top" align="center"  width="25%">
									<table>
										<c:set var="enesaCount" value="0" />
										<c:forEach var="subEnesa" items="${gruposNeg.subvEnesa}">
											<c:set var="enesaCount" value="${enesaCount + 1}" />
											<tr class="literalbordeImportes">
												<input type="hidden" name="keyEnesa${compCount}|${compCountGn}|${enesaCount}" id="${compCount}|keyEnesa${compCountGn}|${enesaCount}" value="${subEnesa.key}" />
												<input type="hidden" name="valueEnesa${compCount}|${compCountGn}|${enesaCount}" id="valueEnesa${compCount}|${compCountGn}|${enesaCount}" value="${subEnesa.value}" />
												<td class="literalbordeImportes" align="left" style="border: 0px;text-align: left;">${subEnesa.key}:</td>
												<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${subEnesa.value}</td>
											</tr>
										</c:forEach>
										<input type="hidden" name="numEnesa${compCount}|${compCountGn}" id="numEnesa${compCount}|${compCountGn}" value="${enesaCount}" />
									</table>
								</td>
								<td valign="top" align="center"  width="25%">
									<table>
										<c:set var="ccaaCount" value="0" />
										<c:forEach var="subCCAA" items="${gruposNeg.subvCCAA}">
											<c:set var="ccaaCount" value="${ccaaCount + 1}" />
											<tr class="literalbordeImportes">
												<input type="hidden" name="keyCCAA${compCount}|${compCountGn}|${ccaaCount}" id="keyCCAA${compCount}|${compCountGn}|${ccaaCount}" value="${subCCAA.key}" />
												<input type="hidden" name="valueCCAA${compCount}|${compCountGn}|${ccaaCount}" id="valueEnesa${compCount}|${compCountGn}|${ccaaCount}" value="${subCCAA.value}" />
												<td class="literalbordeImportes" align="left" style="border: 0px;text-align: left;">${subCCAA.key}:</td>
												<td class="literalbordeImportes" align="right" style="border: 0px; color: blue; font-weight: normal; white-space: nowrap;text-align: right;">${subCCAA.value}</td>
											</tr>
										</c:forEach>
										<input type="hidden" name="numCCAA${compCount}|${compCountGn}" id="numCCAA${compCount}|${compCountGn}" value="${ccaaCount}" />
									</table>
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
					</table>
				</fieldset>	
				</c:if>
				</td></tr>				
			</c:forEach>			
	</tbody> 
</table>