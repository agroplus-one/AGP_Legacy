<%@ page contentType="text/html;" %>
<%@ include file="/jsp/common/static/taglibs.jsp"%>

	<legend class="literal">Modulo ${moduloPoliza.id.codmodulo} - ${moduloPoliza.desmodulo}</legend>
	<table width="100%" border="0">
		<tr>
			<td class="literalbordeCabecera" align="center" width="15%">GARANTIA</td>
			<td class="literalbordeCabecera" align="center" width="15%">RIESGOS CUBIERTOS</td>
			<td class="literalbordeCabecera" width="70%">
				<table width="100%">
					<tr><td colspan="5" class="literalbordeCabecera" align="center">CONDICIONES COBERTURAS</td></tr>
					<tr>
						<td class="literalbordeCabecera" align="center" width="20%">% CAPITAL ASEGURADO</td>
						<td class="literalbordeCabecera" align="center" width="20%">C&Aacute;LCULO INDEMNIZACI&Oacute;N</td>
						<td class="literalbordeCabecera" align="center" width="20%">% M&Iacute;NIMO INDEMNIZABLE</td>
						<td class="literalbordeCabecera" align="center" width="20%">TIPO FRANQUICIA</td>
						<td class="literalbordeCabecera" align="center" width="20%">% FRANQUICIA</td>
					</tr>
				</table>
			</td>
		</tr>
		<c:forEach items="${moduloPoliza.riesgoCubiertoModulos}" var="riesgoCbrtoMod">
		<tr>
			<td class="literalborde" align="center" width="15%">
				${riesgoCbrtoMod.conceptoPpalModulo.desconceptoppalmod}
			</td>
			<td class="literalborde" align="center" width="15%">
				<c:if test="${riesgoCbrtoMod.elegible == 'S'}" >
					<font color="red">ELEGIBLE</font>${br}
				</c:if>
				${riesgoCbrtoMod.riesgoCubierto.desriesgocubierto}
				<c:if test="${riesgoCbrtoMod.elegible == 'S'}" >
					${br}
					<input type="checkbox" disabled="disabled"
					id="checkPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}"
					name="checkPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}"/>
				</c:if>
			</td>
			<td class="literalborde" align="center" width="70%">
				<table width="100%">
				<c:set var="br" value="<br/>"/>
				<c:set var="capAseg" value=""/>
				<c:set var="calculo" value=""/>
				<c:set var="minIndem" value=""/>
				<c:set var="tipoFranq" value=""/>
				<c:set var="pctFranq" value=""/>
				<c:set var="elegCapAseg" value=""/>
				<c:set var="elegCalculo" value=""/>
				<c:set var="elegMinIndem" value=""/>
				<c:set var="elegTipoFranq" value=""/>
				<c:set var="elegPctFranq" value=""/>
				<c:set var="obsCapAseg" value=""/>
				<c:set var="obsCalculo" value=""/>
				<c:set var="obsMinIndem" value=""/>
				<c:set var="obsTipoFranq" value=""/>
				<c:set var="obsPctFranq" value=""/>
				<c:set var="condCapAseg" value=""/>
				<c:set var="condCalculo" value=""/>
				<c:set var="condMinIndem" value=""/>
				<c:set var="condTipoFranq" value=""/>
				<c:set var="condPctFranq" value=""/>
				<c:set var="selectCapAseg" value=""/>
				<c:set var="selectCalculo" value=""/>
				<c:set var="selectMinIndem" value=""/>
				<c:set var="selectTipoFranq" value=""/>
				<c:set var="selectPctFranq" value=""/>
				
				<c:forEach items="${riesgoCbrtoMod.caracteristicaModulos}" var="caractModulo">							
				<c:forEach items="${caractModulo.vinculacionValoresModulosForFkVincValModCaracMod1}" var="vincValores">
					<!-- % CAPITAL ASEGURADO -->
					<c:if test="${!empty vincValores.capitalAseguradoElegibleByPctcapitalasegeleg.descapitalaseg && vincValores.capitalAseguradoElegibleByPctcapitalasegeleg.descapitalaseg != ''}">
						<c:if test="${caractModulo.tipovalor == 'E'}" >
							<c:set var="elegCapAseg" value="ELEGIBLE${br}"/>
						</c:if>
						
						<c:if test="${!empty capAseg && capAseg != ''}" >
							<c:set var="capAseg" value="${capAseg}${br}"/>
						</c:if>
						
						<c:choose>
							<c:when test='${!autorizado && vincValores.capitalAseguradoElegibleByPctcapitalasegeleg.pctcapitalaseg eq 5}'>	
								<c:set var="capAseg" value="${capAseg}<strike>${vincValores.capitalAseguradoElegibleByPctcapitalasegeleg.descapitalaseg}</strike>"/>
							</c:when>	
							<c:otherwise>
								<c:set var="capAseg" value="${capAseg}${vincValores.capitalAseguradoElegibleByPctcapitalasegeleg.descapitalaseg}"/>
								<c:set var="selectCapAseg" value="${selectCapAseg}${vincValores.capitalAseguradoElegibleByPctcapitalasegeleg.pctcapitalaseg},${vincValores.capitalAseguradoElegibleByPctcapitalasegeleg.descapitalaseg};"/>
							</c:otherwise>
						</c:choose>
						
						<c:if test="${caractModulo.observaciones != ''}" >
							<c:set var="obsCapAseg" value="${br}${caractModulo.observaciones}"/>
						</c:if>
					</c:if>
					<!-- CÁLCULO INDEMNIZACIÓN -->
					<c:if test="${!empty vincValores.calculoIndemnizacionByCalcindemneleg.descalculo && vincValores.calculoIndemnizacionByCalcindemneleg.descalculo != ''}">
						<c:if test="${caractModulo.tipovalor == 'E'}" >
							<c:set var="elegCalculo" value="ELEGIBLE${br}"/>
						</c:if>
						
						<c:if test="${!empty calculo && calculo != ''}" >
							<c:set var="calculo" value="${calculo}${br}"/>
						</c:if>
						
						<c:choose>
							<c:when test='${!autorizado && vincValores.calculoIndemnizacionByCalcindemneleg.codcalculo eq 5}'>	
								<c:set var="calculo" value="${calculo}<strike>${vincValores.calculoIndemnizacionByCalcindemneleg.descalculo}</strike>"/>
							</c:when>	
							<c:otherwise>
								<c:set var="calculo" value="${calculo}${vincValores.calculoIndemnizacionByCalcindemneleg.descalculo}"/>
								<c:set var="selectCalculo" value="${selectCalculo}${vincValores.calculoIndemnizacionByCalcindemneleg.codcalculo},${vincValores.calculoIndemnizacionByCalcindemneleg.descalculo};"/>
							</c:otherwise>
						</c:choose>
						
						<c:if test="${caractModulo.observaciones != ''}" >
							<c:set var="obsCalculo" value="${br}${caractModulo.observaciones}"/>
						</c:if>
					</c:if>
					<!-- % MÍNIMO INDEMNIZABLE -->
					<c:if test="${!empty vincValores.minimoIndemnizableElegibleByPctminindemneleg.desminindem && vincValores.minimoIndemnizableElegibleByPctminindemneleg.desminindem != ''}">
						<c:if test="${caractModulo.tipovalor == 'E'}" >
							<c:set var="elegMinIndem" value="ELEGIBLE${br}"/>										
						</c:if>
						
						<c:if test="${!empty minIndem && minIndem != ''}" >
							<c:set var="minIndem" value="${minIndem}${br}"/>
						</c:if>
						
						<c:choose>
							<c:when test='${!autorizado && vincValores.minimoIndemnizableElegibleByPctminindemneleg.pctminindem eq 4}'>	
								<c:set var="minIndem" value="${minIndem}<strike>${vincValores.minimoIndemnizableElegibleByPctminindemneleg.desminindem}</strike>"/>
							</c:when>	
							<c:otherwise>
								<c:set var="minIndem" value="${minIndem}${vincValores.minimoIndemnizableElegibleByPctminindemneleg.desminindem}"/>
								<c:set var="selectMinIndem" value="${selectMinIndem}${vincValores.minimoIndemnizableElegibleByPctminindemneleg.pctminindem},${vincValores.minimoIndemnizableElegibleByPctminindemneleg.desminindem};"/>
							</c:otherwise>
						</c:choose>
						
						<c:if test="${caractModulo.observaciones != ''}" >
							<c:set var="obsMinIndem" value="${br}${caractModulo.observaciones}"/>
						</c:if>
					</c:if>
					<!-- TIPO FRANQUICIA -->
					<c:if test="${!empty vincValores.tipoFranquiciaByTipofranquiciaeleg.destipofranquicia && vincValores.tipoFranquiciaByTipofranquiciaeleg.destipofranquicia != ''}">
						<c:if test="${caractModulo.tipovalor == 'E'}" >
							<c:set var="elegTipoFranq" value="ELEGIBLE${br}"/>
						</c:if>
						
						<c:if test="${!empty tipoFranq && tipoFranq != ''}" >	
							<c:set var="tipoFranq" value="${tipoFranq}${br}"/>
						</c:if>
						
						<c:choose>
							<c:when test='${!autorizado && vincValores.tipoFranquiciaByTipofranquiciaeleg.codtipofranquicia eq 5}'>
								<c:set var="tipoFranq" value="${tipoFranq}<strike>${vincValores.tipoFranquiciaByTipofranquiciaeleg.destipofranquicia}</strike>" />
							</c:when>	
							<c:otherwise>
								<c:set var="tipoFranq" value="${tipoFranq}${vincValores.tipoFranquiciaByTipofranquiciaeleg.destipofranquicia}"/>
								<c:set var="selectTipoFranq" value="${selectTipoFranq}${vincValores.tipoFranquiciaByTipofranquiciaeleg.codtipofranquicia},${vincValores.tipoFranquiciaByTipofranquiciaeleg.destipofranquicia};"/>
							</c:otherwise>
						</c:choose>
						
						<c:if test="${caractModulo.observaciones != ''}" >
							<c:set var="obsTipoFranq" value="${br}${caractModulo.observaciones}"/>
						</c:if>
					</c:if>
					<!-- % FRANQUICIA -->
					<c:if test="${!empty vincValores.pctFranquiciaElegibleByCodpctfranquiciaeleg.despctfranquiciaeleg && vincValores.pctFranquiciaElegibleByCodpctfranquiciaeleg.despctfranquiciaeleg != ''}">
						<c:if test="${caractModulo.tipovalor == 'E'}" >
							<c:set var="elegPctFranq" value="ELEGIBLE${br}"/>
						</c:if>
						
						<c:if test="${!empty pctFranq && pctFranq != ''}" >
							<c:set var="pctFranq" value="${pctFranq}${br}"/>
						</c:if>
													     
						<c:choose>
							<c:when test='${!autorizado && vincValores.pctFranquiciaElegibleByCodpctfranquiciaeleg.codpctfranquiciaeleg eq 5}'>	
								<c:set var="pctFranq" value="${pctFranq}<strike>${vincValores.pctFranquiciaElegibleByCodpctfranquiciaeleg.despctfranquiciaeleg}</strike>"/>
							</c:when>	
							<c:otherwise>
								<c:set var="pctFranq" value="${pctFranq}${vincValores.pctFranquiciaElegibleByCodpctfranquiciaeleg.despctfranquiciaeleg}"/>
								<c:set var="selectPctFranq" value="${selectPctFranq}${vincValores.pctFranquiciaElegibleByCodpctfranquiciaeleg.codpctfranquiciaeleg},${vincValores.pctFranquiciaElegibleByCodpctfranquiciaeleg.despctfranquiciaeleg};"/>
							</c:otherwise>
						</c:choose>								
						
						<c:if test="${caractModulo.observaciones != ''}" >
							<c:set var="obsPctFranq" value="${br}${caractModulo.observaciones}"/>
						</c:if>
					</c:if>
				</c:forEach>
				</c:forEach> 
					<tr>
						<td class="literalborde" align="center" width="20%"><font color="red">${elegCapAseg}</font>
							<c:choose>
							<c:when test="${elegCapAseg == ''}" >
								${capAseg}
							</c:when> <c:otherwise>
							<select class="dato" style="width:110px" disabled="disabled"
								id="selectPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}_362"
								name="selectPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}_362">
								<c:forTokens items="${selectCapAseg }" var="option" delims=";">
									<c:forTokens items="${option}" delims="," var="opt"
										varStatus="status">
										<c:out value="${status.count }" />
										<c:choose>
											<c:when test="${status.count == 1}">
												<c:set var="cod" value="${opt}" />
											</c:when>
											<c:otherwise>
												<c:set var="desc" value="${opt}" />
											</c:otherwise>
										</c:choose>
									</c:forTokens>
									<option value="${cod}">${desc}</option>
								</c:forTokens>
							</select>
						</c:otherwise></c:choose>
							${obsCapAseg}${condCapAseg}&nbsp;</td>
						<td class="literalborde" align="center" width="20%"><font color="red">${elegCalculo}</font>
							<c:choose>
							<c:when test="${elegCalculo == ''}" >
								${calculo}
							</c:when> <c:otherwise>
								<select class="dato" style="width:110px" disabled="disabled"
									id="selectPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}_174" 
									name="selectPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}_174">
									<c:forTokens items="${selectCalculo }" var="option" delims=";">
										<c:forTokens items="${option}" delims="," var="opt" varStatus="status">
											<c:out value="${status.count }"/>
											<c:choose><c:when test="${status.count == 1}">
												<c:set var="cod" value="${opt}"/>
											</c:when> <c:otherwise>
												<c:set var="desc" value="${opt}"/>
											</c:otherwise></c:choose>
										</c:forTokens>
										<option value="${cod}">${desc}</option>
									</c:forTokens>
								</select>
							</c:otherwise></c:choose>
							${obsCalculo}${condCalculo}&nbsp;</td>
						<td class="literalborde" align="center" width="20%"><font color="red">${elegMinIndem}</font>
							<c:choose>
							<c:when test="${elegMinIndem == ''}" >
								${minIndem}
							</c:when> <c:otherwise>
								<select class="dato" style="width:110px" disabled="disabled"
									id="selectPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}_121"
									name="selectPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}_121">
									<c:forTokens items="${selectMinIndem }" var="option" delims=";">
										<c:forTokens items="${option}" delims="," var="opt" varStatus="status">
											<c:out value="${status.count }"/>
											<c:choose><c:when test="${status.count == 1}">
												<c:set var="cod" value="${opt}"/>
											</c:when> <c:otherwise>
												<c:set var="desc" value="${opt}"/>
											</c:otherwise></c:choose>
										</c:forTokens>
										<option value="${cod}">${desc}</option>
									</c:forTokens>
								</select>
							</c:otherwise></c:choose>
							${obsMinIndem}${condMinIndem}&nbsp;</td>
						<td class="literalborde" align="center" width="20%"><font color="red">${elegTipoFranq}</font>
							<c:choose>
							<c:when test="${elegTipoFranq == ''}" >
								${tipoFranq}
							</c:when> <c:otherwise>
								<select class="dato" style="width:110px" disabled="disabled"
									id="selectPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}_170"
									name="selectPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}_170">
									<c:forTokens items="${selectTipoFranq }" var="option" delims=";">
										<c:forTokens items="${option}" delims="," var="opt" varStatus="status">
											<c:out value="${status.count }"/>
											<c:choose><c:when test="${status.count == 1}">
												<c:set var="cod" value="${opt}"/>
											</c:when> <c:otherwise>
												<c:set var="desc" value="${opt}"/>
											</c:otherwise></c:choose>
										</c:forTokens>
										<option value="${cod}">${desc}</option>
									</c:forTokens>
								</select>
							</c:otherwise></c:choose>
							${obsTipoFranq}${condTipoFranq}&nbsp;
						</td>
						<td class="literalborde" align="center" width="20%"><font color="red">${elegPctFranq}</font>
							<c:choose>
							<c:when test="${elegPctFranq == ''}" >
								${pctFranq}
							</c:when> <c:otherwise>
								<select class="dato" style="width:110px" disabled="disabled"
									id="selectPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}_120"
									name="selectPoliza_${riesgoCbrtoMod.conceptoPpalModulo.codconceptoppalmod}_${riesgoCbrtoMod.riesgoCubierto.id.codriesgocubierto}_120">
									<c:forTokens items="${selectPctFranq }" var="option" delims=";">
										<c:forTokens items="${option}" delims="," var="opt" varStatus="status">
											<c:out value="${status.count }"/>
											<c:choose><c:when test="${status.count == 1}">
												<c:set var="cod" value="${opt}"/>
											</c:when> <c:otherwise>
												<c:set var="desc" value="${opt}"/>
											</c:otherwise></c:choose>
										</c:forTokens>
										<option value="${cod}">${desc}</option>
									</c:forTokens>
								</select>
							</c:otherwise></c:choose>
							${obsPctFranq}${condPctFranq}&nbsp;
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</c:forEach>
</table>