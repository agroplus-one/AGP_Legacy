<!-- 28.10.2020 Inico Taty  -->
<table align="left"  border="0" id="cob">
	<tbody id="contenedorCoberturas">		
		<td>
			<fieldset>
				<legend class="literal">Coberturas elegibles Parcela</legend>
					<img id="ajaxLoading_coberturas" src="jsp/img/ajax-loading.gif" style="cursor:hand; cursor:pointer; display:none" />
					<table align="left"  border="0" id="cob">
						<tbody id="contenedorCob">
							
							<c:set var="modulo" value="" />
							<c:set var="cpm" value="" />
							<c:set var="riesgoCubierto" value="" />
							<c:set var="dvCodConcepto" value="" />
							
							<!-- 28.10.2020 ** Tatiana (Fin) -->
							<c:forEach items="${lstParcCoberturas}" var="cobParcela">
								<c:if test="${not empty cobParcela.id}">
									<c:if test="cobParcela.codmodulo != modulo">
										<c:if test="${not empty modulo}">
											</tr>
										</c:if>
										<tr><td class="literalbordeCob"  align="center">${cobParcela.descModulo}</td>
										<c:set var="modulo" value="${cobParcela.codmodulo}" />
										<c:set var="cpm" value="" />
										<c:set var="riesgoCubierto" value="" />
										<c:set var="dvCodConcepto" value="" />
									</c:if>	
									
									<c:if test="${cobParcela.cpm != cpm}">
										<td class="literalbordeCob"  align="center">${cobParcela.cpmDescripcion}</td>
										<c:set var="cpm" value="${cobParcela.cpm}" />
										<c:set var="riesgoCubierto" value="" />
										<c:set var="dvCodConcepto" value="" />
									</c:if>
									
									<c:if test="${cobParcela.riesgoCubierto != riesgoCubierto}">
										<td class="literalbordeCob"  align="center">${cobParcela.rcDescripcion}</td>
										<c:set var="cpm" value="${cobParcela.cpm}" />
										<c:set var="riesgoCubierto" value="${cobParcela.riesgoCubierto}" />
										<c:set var="dvCodConcepto" value="" />
									</c:if>
									
									<c:if test="${empty cobParcela.dvCodConcepto}">
										<td class="literalbordeCob" align="center">
											<c:if test="${cobParcela.elegible == 'S'}">											
												<c:if test="${cobParcela.elegida == 'S'}">
													<c:if test="${modoLectura == 'modoLectura'}">
														<input type="checkbox" name="cob" disabled="disabled" id="riesg_cub_${cobParcela.id}" class="dato" value="${cobParcela.id}|${cobParcela.codmodulo}|${cobParcela.fila}|${cobParcela.cpm}|${cobParcela.cpmDescripcion}|${cobParcela.riesgoCubierto}|${cobParcela.rcDescripcion}|${cobParcela.vinculada}|${cobParcela.elegible}|${cobParcela.tipoCobertura}#${cobParcela.dvCodConcepto}|${cobParcela.dvDescripcion}|${cobParcela.dvValor}|${cobParcela.dvValorDescripcion}|${cobParcela.dvColumna}" onclick="grabaChekCobParcela(this)" checked="true" />
													</c:if>													
													<c:if test="${modoLectura != 'modoLectura'}">
														<input type="checkbox" name="cob" id="riesg_cub_${cobParcela.id}" class="dato" value="${cobParcela.id}|${cobParcela.codmodulo}|${cobParcela.fila}|${cobParcela.cpm}|${cobParcela.cpmDescripcion}|${cobParcela.riesgoCubierto}|${cobParcela.rcDescripcion}|${cobParcela.vinculada}|${cobParcela.elegible}|${cobParcela.tipoCobertura}#${cobParcela.dvCodConcepto}|${cobParcela.dvDescripcion}|${cobParcela.dvValor}|${cobParcela.dvValorDescripcion}|${cobParcela.dvColumna}" onclick="grabaChekCobParcela(this)" checked="true" />
													</c:if>
												</c:if>
												<c:if test="${cobParcela.elegida != 'S'}">
													<c:if test="${modoLectura == 'modoLectura'}">
														<input type="checkbox" name="cob" disabled="disabled" id="riesg_cub_${cobParcela.id}" class="dato" value="${cobParcela.id}|${cobParcela.codmodulo}|${cobParcela.fila}|${cobParcela.cpm}|${cobParcela.cpmDescripcion}|${cobParcela.riesgoCubierto}|${cobParcela.rcDescripcion}|${cobParcela.vinculada}|${cobParcela.elegible}|${cobParcela.tipoCobertura}#${cobParcela.dvCodConcepto}|${cobParcela.dvDescripcion}|${cobParcela.dvValor}|${cobParcela.dvValorDescripcion}|${cobParcela.dvColumna}" onclick="grabaChekCobParcela(this)" />
													</c:if>													
													<c:if test="${modoLectura != 'modoLectura'}">
														<input type="checkbox" name="cob" id="riesg_cub_${cobParcela.id}" class="dato" value="${cobParcela.id}|${cobParcela.codmodulo}|${cobParcela.fila}|${cobParcela.cpm}|${cobParcela.cpmDescripcion}|${cobParcela.riesgoCubierto}|${cobParcela.rcDescripcion}|${cobParcela.vinculada}|${cobParcela.elegible}|${cobParcela.tipoCobertura}#${cobParcela.dvCodConcepto}|${cobParcela.dvDescripcion}|${cobParcela.dvValor}|${cobParcela.dvValorDescripcion}|${cobParcela.dvColumna}" onclick="grabaChekCobParcela(this)" />
													</c:if>
												</c:if>
											</c:if>
											<c:if test="${cobParcela.elegible != 'S'}">
												<input type="checkbox" name="cob" style="display:none" id="riesg_cub_${cobParcela.id}" class="dato" value="${cobParcela.id}|${cobParcela.codmodulo}|${cobParcela.fila}|${cobParcela.cpm}|${cobParcela.cpmDescripcion}|${cobParcela.riesgoCubierto}|${cobParcela.rcDescripcion}|${cobParcela.vinculada}|${cobParcela.elegible}|${cobParcela.tipoCobertura}#${cobParcela.dvCodConcepto}|${cobParcela.dvDescripcion}|${cobParcela.dvValor}|${cobParcela.dvValorDescripcion}|${cobParcela.dvColumna}" />
											</c:if>
										</td>
									</c:if>
									
									<c:if test="${not empty cobParcela.dvCodConcepto}">
										<c:if test="cobParcela.dvCodConcepto != dvCodConcepto">
											<c:if test="${not empty dvCodConcepto}">
												</select></td>
											</c:if>
											<td class="literalbordeCob" align="left">${cobParcela.dvDescripcion}<br/>
												<input type="hidden" id="datVar_${cobParcela.id}" name="datVar_${cobParcela.id}" value="${cobParcela.id}|${cobParcela.codmodulo}|${cobParcela.fila}|${cobParcela.cpm}|${cobParcela.cpmDescripcion}|${cobParcela.riesgoCubierto}|${cobParcela.rcDescripcion}|${cobParcela.vinculada}|${cobParcela.elegible}|${cobParcela.tipoCobertura}#${cobParcela.dvCodConcepto}|${cobParcela.dvDescripcion}|${cobParcela.dvValor}|${cobParcela.dvValorDescripcion}|${cobParcela.dvColumna}"/>
												<c:if test="${modoLectura == 'modoLectura'}">
													<select name="selDV_${cobParcela.dvCodConcepto}" style="width:70" class="datoCob" id="seleccionDatVar__${cobParcela.codmodulo}_${cobParcela.fila}_${cobParcela.cpm}_${cobParcela.riesgoCubierto}_${cobParcela.dvCodConcepto}" disabled="disabled">
												</c:if>													
												<c:if test="${modoLectura != 'modoLectura'}">
													<select name="selDV_${cobParcela.dvCodConcepto}" style="width:70" class="datoCob" id="seleccionDatVar_${cobParcela.codmodulo}_${cobParcela.fila}_${cobParcela.cpm}_${cobParcela.riesgoCubierto}_${cobParcela.dvCodConcepto}">								
												</c:if>
												<option value='${cobParcela.id}|${cobParcela.codmodulo}|${cobParcela.fila}|${cobParcela.cpm}|${cobParcela.cpmDescripcion}|${cobParcela.riesgoCubierto}|${cobParcela.rcDescripcion}|${cobParcela.vinculada}|${cobParcela.elegible}|${cobParcela.tipoCobertura}#${cobParcela.dvCodConcepto}|X'></option>
											<c:set var="dvCodConcepto" value="${cobParcela.dvCodConcepto}" />
										</c:if>
										<c:if test="${cobParcela.dvElegido == 'S'}">
											<option value='${cobParcela.id}|${cobParcela.codmodulo}|${cobParcela.fila}|${cobParcela.cpm}|${cobParcela.cpmDescripcion}|${cobParcela.riesgoCubierto}|${cobParcela.rcDescripcion}|${cobParcela.vinculada}|${cobParcela.elegible}|${cobParcela.tipoCobertura}#${cobParcela.dvCodConcepto}|${cobParcela.dvDescripcion}|${cobParcela.dvValor}|${cobParcela.dvValorDescripcion}|${cobParcela.dvColumna}' selected='selected' >${cobParcela.dvValorDescripcion}</option>		
										</c:if>
										<c:if test="${cobParcela.dvElegido != 'S'}">
											<option value='${cobParcela.id}|${cobParcela.codmodulo}|${cobParcela.fila}|${cobParcela.cpm}|${cobParcela.cpmDescripcion}|${cobParcela.riesgoCubierto}|${cobParcela.rcDescripcion}|${cobParcela.vinculada}|${cobParcela.elegible}|${cobParcela.tipoCobertura}#${cobParcela.dvCodConcepto}|${cobParcela.dvDescripcion}|${cobParcela.dvValor}|${cobParcela.dvValorDescripcion}|${cobParcela.dvColumna}'>${cobParcela.dvValorDescripcion}</option>
										</c:if>	
									</c:if>
								</c:if>
							</c:forEach>	
							</tr>									
						</tbody>
					</table>
			</fieldset>
		</td>
	</tbody>
</table>

<!-- 28.10.2020 Fin Taty -->
							
