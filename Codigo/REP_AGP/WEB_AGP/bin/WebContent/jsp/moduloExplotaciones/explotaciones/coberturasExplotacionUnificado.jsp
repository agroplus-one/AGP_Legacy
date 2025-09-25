<table align="left"  border="0" id="cob">
	<tbody id="contenedorCoberturas">		
		<td>
			<fieldset>
				<legend class="literal">Coberturas elegibles</legend>
					<div id="panelAlertasCoberturas" style="color:black;border:1px solid #DD3C10;display: none;
						font-size:12px;font-style:italic;font-weight:bold;line-height:20px;background-color:#FFEBE8"></div>
					<table align="left"  border="0" id="cob">
						<tbody id="contenedorCob">
							<c:set var="cobDatoVar" value="false" />
							<c:set var="pintarVacio" value="false" />
							<c:set var="codConceptoAux" value="null" />
							<c:set var="pintarVacioNewSel" value="false" />
							<c:set var="nuevoDatVar" value="false" />
							<c:set var="modulo" value="" />
							<c:set var="fila" value="" />
							<c:set var="columna" value="" />	
							<c:forEach items="${lstExpCoberturas}" var="cobExplotacion">
								<c:if test="${not empty cobExplotacion.id}">
									<c:if test="${empty cobExplotacion.dvCodConcepto }">
										<c:set var="nuevoDatVar" value="false" />										
										<c:if test="${cobDatoVar == 'true'}">
											</select>
											<label class='campoObligatorio' align="center" id='campoObligatorio_${modulo}_${fila}_${columna}'>*</label></td></tr>																		
											<c:set var="cobDatoVar" value="false" />	
										</c:if>	
											<tr>				
												<!--<td><input type="text" value="${cobExplotacion.id}|${cobExplotacion.codmodulo}|${cobExplotacion.fila}|${cobExplotacion.cpm}|${cobExplotacion.cpmDescripcion}|${cobExplotacion.riesgoCubierto}|${cobExplotacion.rcDescripcion}|${cobExplotacion.vinculada}|${cobExplotacion.elegible}|${cobExplotacion.tipoCobertura}"/><br></td>-->
												<td class="literalbordeCob"  align="center">${cobExplotacion.cpmDescripcion}&nbsp;</td>
												<td class="literalbordeCob"  align="center">${cobExplotacion.rcDescripcion}&nbsp;</td>
												
												<c:if test="${cobExplotacion.elegible == 'S' }">
													<c:set var="pintarVacioNewSel" value="false" />																																	
													<c:if test="${modoLectura!= 'modoLectura'}">														
														<td class="literalbordeCob" align="center" >								
														<c:if test="${cobExplotacion.elegida == 'S' }">
															<input align=center type='checkbox' name='cob_${cobExplotacion.id}' id='${cobExplotacion.id}'  class='dato' checked='checked' value="${cobExplotacion.id}|${cobExplotacion.codmodulo}|${cobExplotacion.fila}|${cobExplotacion.cpm}|${cobExplotacion.cpmDescripcion}|${cobExplotacion.riesgoCubierto}|${cobExplotacion.rcDescripcion}|${cobExplotacion.vinculada}|${cobExplotacion.elegible}|${cobExplotacion.tipoCobertura}#${cobExplotacion.dvCodConcepto}|${cobExplotacion.dvDescripcion}|${cobExplotacion.dvValor}|${cobExplotacion.dvValorDescripcion}|${cobExplotacion.dvColumna}" onclick="grabaChekCob2(this);"/>&nbsp;
														</c:if>
														<c:if test="${cobExplotacion.elegida == 'N'}">
															<input align=center type='checkbox' name='cob_${cobExplotacion.id}' id='${cobExplotacion.id}'  class='dato' value="${cobExplotacion.id}|${cobExplotacion.codmodulo}|${cobExplotacion.fila}|${cobExplotacion.cpm}|${cobExplotacion.cpmDescripcion}|${cobExplotacion.riesgoCubierto}|${cobExplotacion.rcDescripcion}|${cobExplotacion.vinculada}|${cobExplotacion.elegible}|${cobExplotacion.tipoCobertura}#${cobExplotacion.dvCodConcepto}|${cobExplotacion.dvDescripcion}|${cobExplotacion.dvValor}|${cobExplotacion.dvValorDescripcion}|${cobExplotacion.dvColumna}" onclick="grabaChekCob2(this);"/>&nbsp;
														</c:if>															
														</td>															
													</c:if>

													<c:if test="${modoLectura== 'modoLectura'}">									
														<td class="literalbordeCob" align="center">
														<c:if test="${cobExplotacion.elegida == 'S' }">
															<input align=center readonly="true" type='checkbox' name='cob_${cobExplotacion.id}' id='${cobExplotacion.id}'  class='dato' checked="checked" />&nbsp;
														</c:if>
														<c:if test="${cobExplotacion.elegida == 'N'}">
															<input align=center readonly="true" type='checkbox' name='cob_${cobExplotacion.id}' id='${cobExplotacion.id}'  class='dato' />&nbsp;
														</c:if>											
														</td>																
													</c:if>

												</c:if>
												<c:if test="${cobExplotacion.elegible == 'N' }">												
													<td class="literalbordeCob" align="center">								
														<input align=center  type='checkbox'  style='display:none' name='cob_${cobExplotacion.id}' id='${cobExplotacion.id}'  class='dato' value="${cobExplotacion.id}|${cobExplotacion.codmodulo}|${cobExplotacion.fila}|${cobExplotacion.cpm}|${cobExplotacion.cpmDescripcion}|${cobExplotacion.riesgoCubierto}|${cobExplotacion.rcDescripcion}|${cobExplotacion.vinculada}|${cobExplotacion.elegible}|${cobExplotacion.tipoCobertura}#${cobExplotacion.dvCodConcepto}|${cobExplotacion.dvDescripcion}|${cobExplotacion.dvValor}|${cobExplotacion.dvValorDescripcion}|${cobExplotacion.dvColumna}" onclick="grabaChekCob2(this);"/>&nbsp;													
													</td>
													<c:set var="pintarVacio" value="true" />
													<c:set var="pintarVacioNewSel" value="true" />														
												</c:if>														
									</c:if>		
									<c:if test="${not empty cobExplotacion.dvCodConcepto }">															
										<c:if test="${cobDatoVar == 'false' || cobExplotacion.dvCodConcepto != codConceptoAux}">
											<c:set var="cobDatoVar" value="true" />

											<c:if test="${nuevoDatVar == 'true'}">
												</select><label class='campoObligatorio' align="center" id='campoObligatorio_${modulo}_${fila}_${columna}'>*</label></td>
												<c:set var="nuevoDatVar" value="false" />
											</c:if>
											<td class='literalbordeCob' align='center'>${cobExplotacion.dvDescripcion}&nbsp;<br>
												<c:if test="${modoLectura!= 'modoLectura'}">																								
													<select name='selDV_${cobExplotacion.dvCodConcepto}' style='width:70' class='datoCob' id='seleccionDatVar_${cobExplotacion.codmodulo}_${cobExplotacion.fila}_${cobExplotacion.cpm}_${cobExplotacion.riesgoCubierto}_${cobExplotacion.dvCodConcepto}'>";								
													<c:set var="nuevoDatVar" value="true" />
													<c:set var="pintarVacio" value="true" />
													<c:set var="modulo" value="${cobExplotacion.codmodulo}" />
													<c:set var="fila" value="${cobExplotacion.fila}" />
													<c:set var="columna" value="${cobExplotacion.dvColumna}" />
												</c:if>
												<c:if test="${modoLectura== 'modoLectura'}">
													<select name='selDV_${cobExplotacion.dvCodConcepto}' style='width:70' class='datoCob' id='seleccionDatVar__${cobExplotacion.codmodulo}_${cobExplotacion.fila}_${cobExplotacion.cpm}_${cobExplotacion.riesgoCubierto}_${cobExplotacion.dvCodConcepto}' disabled='disabled'>";
												</c:if>
										</c:if>
																	
										<c:if test="${cobDatoVar == 'true'}">										
											<c:if test="${modoLectura!= 'modoLectura'}">
												<c:if test="${pintarVacio == 'true' && pintarVacioNewSel == 'true'}">
													<option value='${cobExplotacion.id}|${cobExplotacion.codmodulo}|${cobExplotacion.fila}|${cobExplotacion.cpm}|${cobExplotacion.cpmDescripcion}|${cobExplotacion.riesgoCubierto}|${cobExplotacion.rcDescripcion}|${cobExplotacion.vinculada}|${cobExplotacion.elegible}|${cobExplotacion.tipoCobertura}#${cobExplotacion.dvCodConcepto}|X'></option>
													<c:set var="pintarVacio" value="false" />
												</c:if>																
												<c:if test="${cobExplotacion.dvElegido == 'S' }">
													<option value='${cobExplotacion.id}|${cobExplotacion.codmodulo}|${cobExplotacion.fila}|${cobExplotacion.cpm}|${cobExplotacion.cpmDescripcion}|${cobExplotacion.riesgoCubierto}|${cobExplotacion.rcDescripcion}|${cobExplotacion.vinculada}|${cobExplotacion.elegible}|${cobExplotacion.tipoCobertura}#${cobExplotacion.dvCodConcepto}|${cobExplotacion.dvDescripcion}|${cobExplotacion.dvValor}|${cobExplotacion.dvValorDescripcion}|${cobExplotacion.dvColumna}' selected='selected' >${cobExplotacion.dvValorDescripcion}</option>		
												</c:if>
												
												<c:if test="${cobExplotacion.dvElegido == 'N' }">
													<option value='${cobExplotacion.id}|${cobExplotacion.codmodulo}|${cobExplotacion.fila}|${cobExplotacion.cpm}|${cobExplotacion.cpmDescripcion}|${cobExplotacion.riesgoCubierto}|${cobExplotacion.rcDescripcion}|${cobExplotacion.vinculada}|${cobExplotacion.elegible}|${cobExplotacion.tipoCobertura}#${cobExplotacion.dvCodConcepto}|${cobExplotacion.dvDescripcion}|${cobExplotacion.dvValor}|${cobExplotacion.dvValorDescripcion}|${cobExplotacion.dvColumna}'>${cobExplotacion.dvValorDescripcion}</option>
												</c:if>
											</c:if>
											<c:if test="${modoLectura== 'modoLectura'}">																
												<c:if test="${cobExplotacion.dvElegido == 'S' }">
													<option value='${cobExplotacion.id}' selected='selected' >${cobExplotacion.dvValorDescripcion}</option>
												</c:if>
												
											</c:if>																
										</c:if>
										<c:set var='codConceptoAux' value='${cobExplotacion.dvCodConcepto}' />
										codConceptoAux = list[i].dvCodConcepto;							
									</c:if>		
								</c:if>
							</c:forEach>
							<c:if test="${nuevoDatVar == 'true'}">
								</select><label class='campoObligatorio' align="center" id='campoObligatorio_${modulo}_${fila}_${columna}'>*</label></td>
								<c:set var="nuevoDatVar" value="false" />
							</c:if>
										
						</tbody>
					</table>
			</fieldset>
		</td>
	</tbody>
</table>