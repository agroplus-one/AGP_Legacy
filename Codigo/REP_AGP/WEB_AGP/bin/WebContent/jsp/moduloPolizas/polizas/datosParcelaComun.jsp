<c:set var="tabIndex">0</c:set>

<form:form name="datosParcela" id="datosParcela" action="datosParcela.html" method="post" commandName="parcelaBean">
	<input type="hidden" id="methodDP" name="method" value="" />
	<input type="hidden" id="operacion" name="operacion" value="${requestScope.operacion}" />
	<input type="hidden" id="isAnexo" name="isAnexo" value="${isAnexo}" />
	
	<input type="hidden" id="codplan" name="codplan" value="${requestScope.codPlan}" />
	<input type="hidden" id="codlinea" name="codlinea" value="${requestScope.codLinea}" />
	
	<input type="hidden" id="fechaInicioContratacion" value="${fechaInicioContratacion}"/>
	
	<input type="hidden" id="listCodModulos" name="listCodModulos" value="${requestScope.listCodModulos}" />
	<form:hidden path="codParcela" id="codParcela" />
	<form:hidden path="refIdParcela" id="refIdParcela" />
	<form:hidden path="idparcelaanxestructura" id="idparcelaanxestructura" />
	<form:hidden path="tipoParcela" id="tipoParcela" />
	<input type="hidden" id="modo" name="modo" value="${requestScope.modo}" />
	<input type="hidden" id="claseId" name="claseId" value="${requestScope.claseId}" />
	<input type="hidden" id="nifAsegurado" name="nifAsegurado" value="${requestScope.nifAsegurado}" />
	<input type="hidden" id="mustFillDVs" name="mustFillDVs" value="${requestScope.mustFillDVs}" />
	<input type="hidden" id="tieneCoberturas" name="tieneCoberturas" value="${requestScope.tieneCoberturas}" />
	<form:hidden path="capitalAsegurado.id" id="idCapitalAsegurado" />
	<input type="hidden" id="codconcepto" name="codconcepto" value="126" />
	<input type="hidden" id="listaIdsStr" name="listaIdsStr" value="${requestScope.listaIdsStr}" />
	
	<input type="hidden" id="precio_limMin" />
	<input type="hidden" id="precio_limMax" />
	<!-- IMPORTANTE QUE EL VALUE ESTE ENTRE COMILLAS SIMPLES Y NO DOBLES -->
	<input type="hidden" id="precio_listPrecios" value='${requestScope.listPrecios}' />
	<input type="hidden" id="produccion_limMin" />
	<input type="hidden" id="produccion_limMax" />
	<input type="hidden" id="produccion_rdtosLibres"  value="${requestScope.rdtosLibres}"/>
	<!-- IMPORTANTE QUE EL VALUE ESTE ENTRE COMILLAS SIMPLES Y NO DOBLES -->
	<input type="hidden" id="produccion_listProducciones" value='${requestScope.listProducciones}' />
	
	<!-- Pet. 63485 ** MODIF TAM (03.11.2020) -->
	<input type="hidden" id="checksMarcados" value=""/>
	<input type="hidden" id="datVarSeleccionados" value=""/>
	
	<input type="hidden" id="cod_cpto_lupa_factores" />
	<input type="hidden" id="valor_lupa_factores" onchange="javascript:copiaMarcadoFactoresValor();" />
	<input type="hidden" id="desc_lupa_factores" onchange="javascript:copiaMarcadoFactoresDesc();" />
	
	<input type="hidden" id="idSigParcela" name="idSigParcela" value="${requestScope.idSigParcela}" />	
	<input type="hidden" name="hayCambiosDatosAsegurado" id="hayCambiosDatosAsegurado" value="${hayCambiosDatosAsegurado}" />
	
	<!-- Contenido de la pagina -->
	<div class="conten" style="padding:3px;width:100%">
		<p class="titulopag" align="left">${isAnexo ? "Datos de la parcela de anexo" : "Datos de la parcela"}</p>
		
		<%@ include file="/jsp/common/static/avisoErrores.jsp"%>
		
		<div id="divDatosIdent" class="panel2 isrt">
			<fieldset>
				<legend class="literal">Ubicaci&oacute;n / Ident. Parcela</legend>
								
				<table align="center" width="100%">
				<thead/>
				<tbody>
					<tr>
						<td colspan="2">
							<fieldset>
								<legend class="literal">Datos de parcela</legend>
								
								<table width="100%">
								<tr>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="nombreParcela">Nombre</label>
										<form:input path="nombreParcela" id="nomParcela" size="22" maxlength="20" tabindex="${tabIndex}" onchange="this.value = this.value.toUpperCase();" />																					
									</td>	
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="cultivo">Cultivo</label>
										<form:input path="cultivo" id="cultivo" size="1" maxlength="3" tabindex="${tabIndex}"
											onchange="javascript:lupas.limpiarCampos('desc_cultivo','variedad','desc_variedad');" />												
										<form:input path="desCultivo" id="desc_cultivo" size="22" readonly="true" />
										<c:if test="${parcelaBean.tipoParcela == 'P' && modoLectura != 'modoLectura'}">
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('CultivoIN','principio', '', '');" alt="Buscar Cultivo" title="Buscar Cultivo" />
											<label class="campoObligatorio" id="campoObligatorio_cultivo" title="Campo obligatorio">*</label>
										</c:if>	
									</td>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="variedad">Variedad</label>
										<form:input path="variedad" id="variedad" size="1" maxlength="3" tabindex="${tabIndex}"
											onchange="javascript:lupas.limpiarCampos('desc_variedad');" />												
										<form:input path="desVariedad" id="desc_variedad" size="22" readonly="true" />
										<c:if test="${parcelaBean.tipoParcela == 'P' && modoLectura != 'modoLectura'}">
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('VariedadIN','principio', '', '');" alt="Buscar Variedad" title="Buscar Variedad" />
											<label class="campoObligatorio" id="campoObligatorio_variedad" title="Campo obligatorio">*</label>
										</c:if>	
									</td>									
								</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<fieldset>
								<legend class="literal">SIGPAC</legend>
																	
								<table width="100%">
								<tr>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="provinciaSigpac">Provincia</label>
										<form:input path="provinciaSigpac" id="provinciaSigpac" size="1" maxlength="2" tabindex="${tabIndex}" />
										<label class="campoObligatorio" id="campoObligatorio_provinciaSigpac" title="Campo obligatorio">*</label>																					
									</td>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="terminoSigpac">T&eacute;rmino</label>
										<form:input path="terminoSigpac" id="terminoSigpac" size="2" maxlength="3" tabindex="${tabIndex}" />
										<label class="campoObligatorio" id="campoObligatorio_terminoSigpac" title="Campo obligatorio">*</label>																					
									</td>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="agregadoSigpac">Agregado</label>
										<form:input path="agregadoSigpac" id="agregadoSigpac" size="2" maxlength="3" tabindex="${tabIndex}" />
										<label class="campoObligatorio" id="campoObligatorio_agregadoSigpac" title="Campo obligatorio">*</label>																					
									</td>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="zonaSigpac">Zona</label>
										<form:input path="zonaSigpac" id="zonaSigpac" size="1" maxlength="2" tabindex="${tabIndex}" />	
										<label class="campoObligatorio" id="campoObligatorio_zonaSigpac" title="Campo obligatorio">*</label>																				
									</td>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="poligonoSigpac">Pol&iacute;gono</label>
										<form:input path="poligonoSigpac" id="poligonoSigpac" size="2" maxlength="3" tabindex="${tabIndex}" />
										<label class="campoObligatorio" id="campoObligatorio_poligonoSigpac" title="Campo obligatorio">*</label>																					
									</td>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="parcelaSigpac">Parcela</label>
										<form:input path="parcelaSigpac" id="parcelaSigpac" size="4" maxlength="5" tabindex="${tabIndex}" />
										<label class="campoObligatorio" id="campoObligatorio_parcelaSigpac" title="Campo obligatorio">*</label>																					
									</td>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="recintoSigpac">Recinto</label>
										<form:input path="recintoSigpac" id="recintoSigpac" size="4" maxlength="5" tabindex="${tabIndex}" />
										<label class="campoObligatorio" id="campoObligatorio_recintoSigpac" title="Campo obligatorio">*</label>																					
									</td>
									<td align="right">
										<c:if test="${parcelaBean.tipoParcela == 'P' && modoLectura != 'modoLectura'}">
											<c:set var="tabIndex">${tabIndex+1}</c:set>
											<a class="bot" id="sigpacBtn" href="#" onclick="doSigPac2Agro(); return false;" tabindex="${tabIndex}">R</a>
											<img id="ajaxLoading_sigpac" src="jsp/img/ajax-loading.gif" style="cursor:hand; cursor:pointer; display:none" />
										</c:if>
									</td>
								</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td>
							<fieldset>
								<legend class="literal">Ubicaci&oacute;n</legend>									
								
								<table width="100%">
								<tr>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="provincia">Provincia</label>
										<form:input path="codProvincia" id="provincia" size="1" maxlength="2" tabindex="${tabIndex}"
											onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');" />												
										<form:input path="desProvincia" id="desc_provincia" size="14" readonly="true" />
										<c:if test="${parcelaBean.tipoParcela == 'P' && modoLectura != 'modoLectura'}">
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ProvinciaIN','principio', '', '');" alt="Buscar Provincia" title="Buscar Provincia" />
											<label class="campoObligatorio" id="campoObligatorio_provincia" title="Campo obligatorio">*</label>
										</c:if>											
									</td>
									<td class="literal">	
										<c:set var="tabIndex">${tabIndex+1}</c:set>	
										<label for="comarca">Comarca</label>								
										<form:input path="codComarca" id="comarca" size="1" maxlength="2" tabindex="${tabIndex}"
											onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');" />		
										<form:input path="desComarca" id="desc_comarca" size="14" readonly="true" />									
										<c:if test="${parcelaBean.tipoParcela == 'P' && modoLectura != 'modoLectura'}">
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('ComarcaIN','principio', '', '');" alt="Buscar Comarca" title="Buscar Comarca" />
											<label class="campoObligatorio" id="campoObligatorio_comarca" title="Campo obligatorio">*</label>
										</c:if>
									</td>
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="termino">T&eacute;rmino</label>	
										<form:input path="codTermino" id="termino" size="2" maxlength="3" tabindex="${tabIndex}"
											onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');" />											
										<form:input path="desTermino" id="desc_termino" size="22" readonly="true" />
										<c:if test="${parcelaBean.tipoParcela == 'P' && modoLectura != 'modoLectura'}">
											<label class="campoObligatorio" id="campoObligatorio_termino" title="Campo obligatorio">*</label>
										</c:if>
									</td>										
									<td class="literal">
										<c:set var="tabIndex">${tabIndex+1}</c:set>
										<label for="subtermino">Subt&eacute;rmino</label>	
										<form:input path="codSubTermino" id="subtermino" size="1" maxlength="1" tabindex="${tabIndex}"
											onchange="this.value=this.value.toUpperCase();" />
										<c:if test="${parcelaBean.tipoParcela == 'P' && modoLectura != 'modoLectura'}">
											<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('TerminoIN','principio', '', '');" alt="Buscar T&eacute;rmino" title="Buscar T&eacute;rmino" />
											<label class="campoObligatorio" id="campoObligatorio_subtermino" title="Campo obligatorio">*</label>
										</c:if>
									</td>
								</tr>
								</table>
								
							</fieldset>
						</td>						
					</tr>
					<tr>
						<td align="right" height="35px" valign="middle">
							<c:if test="${parcelaBean.tipoParcela == 'P' && modoLectura != 'modoLectura'}">
								<c:set var="tabIndex">${tabIndex+1}</c:set>
								<a class="bot" id="btnPanelDatIdent" href="javascript:irPanelCapAseg()" tabindex="${tabIndex}">&gt;&gt;</a>
								<img id="ajaxLoading_datIdent" src="jsp/img/ajax-loading.gif" style="cursor:hand; cursor:pointer; display:none" />
							</c:if>
						</td>
					</tr>
				</tbody>
				<tfoot/>
				</table>
			</fieldset>
		</div>
		
		<div id="divCapAseg" class="panel2 isrt">			
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
									<c:set var="tabIndex">${tabIndex+1}</c:set>
									<label for="tipoCapital">Tipo Capital</label>
									<form:input path="capitalAsegurado.codtipoCapital" id="capital" size="2" maxlength="3" tabindex="${tabIndex}"
										onchange="javascript:lupas.limpiarCampos('desc_capital'); getDatosConcepto('126', this.value); isAlreadySaved = false;" />												
									<form:input path="capitalAsegurado.desTipoCapital" id="desc_capital" size="25" readonly="true" />
									<c:if test="${modoLectura != 'modoLectura'}">
										<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:lupas.muestraTabla('FactoresTipoCapitalIN','principio', '', '');" alt="Buscar Tipo Capital" title="Buscar Tipo Capital" />
										<label class="campoObligatorio" id="campoObligatorio_capital" title="Campo obligatorio">*</label>
									</c:if>											
								</td>
								<td class="literal">
									<c:set var="tabIndex">${tabIndex+1}</c:set>
									<label for="superficie">Superficie (hect&aacute;reas)</label>
									<form:input path="capitalAsegurado.superficie" id="superficie" size="7" maxlength="7" tabindex="${tabIndex}" onchange="isAlreadySaved = false;" />
									<label class="campoObligatorio" id="campoObligatorio_superficie" title="Campo obligatorio">*</label>	
								</td>
								<td>
								</td>
							</tr>
							</table>
						</td>
						<td width="50px" align="right">
							<c:if test="${parcelaBean.tipoParcela == 'P' && modoLectura != 'modoLectura'}">
								<a class="bot" id="btnPanelCapAseg" href="javascript:irPanelDatIdent()">&lt;&lt;</a>
							</c:if>
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
					<tr>
						<td colspan="2">
							<table align="center" width="100%">
							<thead/>
							<tbody>
								<tr>
									<td valign="middle">										
										<table width="100%">
										<tr>
											<td class="literal">
												<c:set var="tabIndex">${tabIndex+1}</c:set>
												<label for="produccion">Producci&oacute;n</label>
												<form:input path="capitalAsegurado.produccion" id="produccion" size="5" maxlength="12" tabindex="${tabIndex}" onchange="isAlreadySaved = false;" />
												<c:if test="${parcelaBean.tipoParcela == 'P' && modoLectura != 'modoLectura'}">
													<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:muestraRangoProduccion();" alt="Mostrar Rango Producci&oacute;n" title="Mostrar Rango Producci&oacute;n" />
													<label class="campoObligatorio" id="campoObligatorio_produccion" title="Campo obligatorio">*</label>
												</c:if>	
											</td>
											<td class="literal">
												<c:set var="tabIndex">${tabIndex+1}</c:set>
												<label for="precio">Precio (&euro;)</label>
												<form:input path="capitalAsegurado.precio" id="precio" size="5" maxlength="11" tabindex="${tabIndex}" onchange="isAlreadySaved = false;" />												
												<c:if test="${modoLectura != 'modoLectura'}">
													<img src="jsp/img/magnifier.png" style="cursor: hand;" onclick="javascript:muestraRangoPrecio();" alt="Mostrar Rango Precio" title="Mostrar Rango Precio" />
													<label class="campoObligatorio" id="campoObligatorio_precio" title="Campo obligatorio">*</label>
												</c:if>											
											</td>
											<td>
												<c:if test="${modoLectura != 'modoLectura'}">
													<c:set var="tabIndex">${tabIndex+1}</c:set>
													<a class="bot" href="javascript:calcular()" tabindex="${tabIndex}">Calcular</a>
												</c:if>
											</td>
										</tr>
										</table>
									</td>
									<td width="140px" valign="middle">
										<c:if test="${modoLectura != 'modoLectura'}">
											<c:set var="tabIndex">${tabIndex+1}</c:set>
											<a class="bot" id="btnGuardarTC" href="javascript:guardarTC(saveTC)" tabindex="${tabIndex}">Guardar TC</a>												
											<c:if test="${not isAnexo}">
												<br/><br/>
												<c:set var="tabIndex">${tabIndex+1}</c:set>
												<a class="bot" id="btnGuardarTCReplicar" href="javascript:guardarTC(saveTCandRep)" tabindex="${tabIndex}">Guardar TC y replicar</a>
											</c:if>												
										</c:if>
									</td>
									<td width="450px" valign="top" rowspan="2">
										<table align="center" width="430px" border="0" id="tcList">
										<thead>
											<tr>
												<th class="literalbordeCabecera" width="18%">Acciones</th>
												<th class="literalbordeCabecera" width="32%">Tipo Capital</th>
												<th class="literalbordeCabecera" width="16%">Superficie</th>
												<th class="literalbordeCabecera" width="18%">Producci&oacute;n</th>
												<th class="literalbordeCabecera" width="16%">Precio</th>
											</tr>
											
											<c:forEach items="${parcelaBean.capitalesAsegurados}" var="capitalAsegurado">
												<tr id="tcListTr${capitalAsegurado.id}">
													<td class="${capitalAsegurado.id == parcelaBean.capitalAsegurado.id ? 'literalbordeAzul' : 'literalborde'}">														
														<c:if test="${modoLectura != 'modoLectura'}">
															<a href="javascript:editarTC(${capitalAsegurado.id})"><img src="jsp/img/displaytag/edit.png" alt="Editar" title="Editar"/></a>
															<a href="javascript:duplicarTC(${capitalAsegurado.id})"><img src="jsp/img/displaytag/duplicar.png" alt="Duplicar" title="Duplicar"/></a>
															<a href="javascript:borrarTC(${capitalAsegurado.id})"><img src="jsp/img/displaytag/delete.png" alt="Eliminar" title="Eliminar"/></a>
														</c:if>
														<c:if test="${modoLectura == 'modoLectura'}">
															<a href="javascript:editarTC(${capitalAsegurado.id})"><img src="jsp/img/displaytag/information.png" alt="Visualizar infromaci&oacute;n" title="Visualizar infromaci&oacute;n"/></a>
														</c:if>
													</td>
													<td class="${capitalAsegurado.id == parcelaBean.capitalAsegurado.id ? 'literalbordeAzul' : 'literalborde'}">
														${capitalAsegurado.desTipoCapital}
													</td>
													<td class="${capitalAsegurado.id == parcelaBean.capitalAsegurado.id ? 'literalbordeAzul' : 'literalborde'}">
														${capitalAsegurado.superficie}
													</td>
													<td class="${capitalAsegurado.id == parcelaBean.capitalAsegurado.id ? 'literalbordeAzul' : 'literalborde'}">
														${capitalAsegurado.produccion}
													</td>
													<td class="${capitalAsegurado.id == parcelaBean.capitalAsegurado.id ? 'literalbordeAzul' : 'literalborde'}">
														${capitalAsegurado.precio}														
													</td>
												</tr>
											</c:forEach>

											<tr id="tcListEmptyTr" ${empty parcelaBean.capitalesAsegurados ? '' : 'style="display:none"'}">
												<td class="literalborde">&nbsp;</td>
												<td class="literalborde">&nbsp;</td>
												<td class="literalborde">&nbsp;</td>
												<td class="literalborde">&nbsp;</td>
												<td class="literalborde">&nbsp;</td>
											</tr>
										</thead>
										<tbody> 
										</tbody>
										</table>
									</td>
								</tr>
								<tr>
									<td id="contenedorCoberturas" valign="top">
										<%@ include file="/jsp/moduloPolizas/polizas/coberturasParcela.jsp"%>
									</td>
									<td>&nbsp;</td>
								</tr>
							</tbody>
							<tfoot/>
							</table>
						</td>
					</tr>						
				</tbody>
				<tfoot/>
				</table>
			</fieldset>				
		</div>
	</div>

	</br>
	<div id="buttons">
		<table width="97%" cellspacing="0" cellpadding="0" border="0">
			<tr align="center">
				<td align="center" width="25%">
					&nbsp;
				</td>
				<td align="center" width="50%">
					<c:if test="${modoLectura != 'modoLectura'}">
						<c:set var="tabIndex">${tabIndex+1}</c:set>
						<a class="bot" id="btnGuardarSalir" href="javascript:guardarTC(savePandExit)" tabindex="${tabIndex}">Guardar y salir</a>
						<c:set var="tabIndex">${tabIndex+1}</c:set>
						<a class="bot" id="btnGuardarNuevo" href="javascript:guardarTC(savePandNew)" tabindex="${tabIndex}">Guardar y nuevo</a>
						<c:if test="${parcelaBean.tipoParcela == 'P'}">
							<c:set var="tabIndex">${tabIndex+1}</c:set>
							<a class="bot" id="btnGuardarReplicar" href="javascript:guardarReplicar()" tabindex="${tabIndex}">Guardar y replicar</a>
							<c:if test="${not isAnexo}">
								<c:set var="tabIndex">${tabIndex+1}</c:set>
								<a class="bot" id="btnGuardarSig" href="javascript:guardarIrSiguiente()" tabindex="${tabIndex}">Guardar y siguiente</a>
							</c:if>
						</c:if>
					</c:if>
				</td>
				<td align="right" width="25%">
					<c:if test="${modoLectura != 'modoLectura'}">
						<c:set var="tabIndex">${tabIndex+1}</c:set>
						<a class="bot" id="btnLimpiar" href="javascript:limpiar()" tabindex="${tabIndex}">Limpiar</a>
					</c:if>
					<c:set var="tabIndex">${tabIndex+1}</c:set>
					<a class="bot" id="btnVolver" href="javascript:volver()" tabindex="${tabIndex}">Volver</a>
				</td>
			</tr>
		</table>
	</div>
</form:form>
<br/>

<%@ include file="/jsp/common/lupas/lupaCultivoIN.jsp"%>
<%@ include file="/jsp/common/lupas/lupaVariedadIN.jsp"%>
<%@ include file="/jsp/common/lupas/lupaProvinciaIN.jsp"%>
<%@ include file="/jsp/common/lupas/lupaComarcaIN.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTerminoIN.jsp"%>
<%@ include file="/jsp/common/lupas/lupaTipoCapitalIN.jsp"%>

<%@ include file="/jsp/common/lupas/lupaPrecioProduccion/lupaPrecioProduccion.jsp"%>

<%@include file="/jsp/moduloPolizas/polizas/datosVariablesParcelaLupas.jsp" %>