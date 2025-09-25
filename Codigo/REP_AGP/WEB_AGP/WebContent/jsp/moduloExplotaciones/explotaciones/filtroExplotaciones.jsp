<div class="panel2 isrt">
	<fieldset>
		<legend class="literal">Datos de Filtro</legend>
		<fieldset style="float: left; margin-bottom: 10px">
			<legend class="literal">Ubicación</legend>
			<table align="center">
				<tr>
					<td class="literal" style="padding-left: 3px;"><abbr
						title="Provincia">Prov.</abbr></td>
					<td class="literal" style="padding-left: 3px;"><input
						type="text" id="provincia" name="provincia" size="2" maxlength="2"
						class="dato"
						onchange="javascript:lupas.limpiarCampos('desc_provincia','comarca','desc_comarca','termino','desc_termino','subtermino');"
						tabindex="1"
						<c:if test="${requestScope.provincia !=null }">
										value='${requestScope.provincia}'
									</c:if> />
						<input class="dato" id="desc_provincia" name="desc_provincia"
						size="16" readonly="readonly"
						<c:if test="${requestScope.desc_provincia !=null }">
										value='${requestScope.desc_provincia}'
									</c:if> />
						<img src="jsp/img/magnifier.png" style="cursor: hand;"
						onclick="javascript:lupas.muestraTabla('ProvinciaIN','principio', '', '');"
						alt="Buscar Provincia" title="Buscar Provincia" /></td>
					<td class="literal" style="padding-left: 3px;"><abbr
						title="Comarca">Com.</abbr></td>
					<td class="literal" style="padding-left: 3px;"><input
						type="text" id="comarca" name="comarca" size="2" maxlength="2"
						class="dato"
						onchange="javascript:lupas.limpiarCampos('desc_comarca','termino','desc_termino','subtermino');"
						tabindex="2"
						<c:if test="${requestScope.comarca !=null }">
											value='${requestScope.comarca}'
										</c:if> />
						<input class="dato" id="desc_comarca" name="desc_comarca"
						size="16" readonly="readonly"
						<c:if test="${requestScope.desc_comarca !=null }">
										value='${requestScope.desc_comarca}'
									</c:if> />
						<img src="jsp/img/magnifier.png" style="cursor: hand;"
						onclick="javascript:lupas.muestraTabla('ComarcaIN','principio', '', '');"
						alt="Buscar Comarca" title="Buscar Comarca" /></td>
					<td class="literal" style="padding-left: 3px;"><abbr
						title="Término">Térm.</abbr></td>
					<td class="literal" style="padding-left: 3px;"><input
						type="text" id="termino" name="termino" size="3" maxlength="3"
						class="dato"
						onchange="javascript:lupas.limpiarCampos('desc_termino','subtermino');"
						tabindex="3"
						<c:if test="${requestScope.termino !=null }">
										value='${requestScope.termino}'
									</c:if> />
						<input class="dato" id="desc_termino" name="desc_termino"
						size="26" readonly="readonly"
						<c:if test="${requestScope.desc_termino !=null }">
										value='${requestScope.desc_termino}'
									</c:if> />
					</td>
					<td class="literal" style="padding-left: 3px;"><abbr
						title="Subtérmino">Subt.</abbr></td>
					<td class="literal" style="padding-left: 3px;"><input
						type="text" id="subtermino" name="subtermino" size="1"
						maxlength="1" class="dato"
						onchange="this.value=this.value.toUpperCase();" tabindex="4"
						<c:if test="${requestScope.subtermino !=null }">
										value='${requestScope.subtermino}'
									</c:if> />
						<img src="jsp/img/magnifier.png" style="cursor: hand;"
						onclick="javascript:lupas.muestraTabla('TerminoIN','principio', '', '');"
						alt="Buscar Término" title="Buscar Término" /></td>
				</tr>
			</table>
		</fieldset>
		<fieldset>
			<legend class="literal">Coordenadas</legend>
			<table align="center">
				<tr>
					<td class="literal"><abbr title="Latitud">Lat.</abbr></td>
					<td class="literal"><input type="text" id="latitud"
						name="latitud" size="6" maxlength="6" class="dato"
						onchange="this.value=this.value.toUpperCase();" tabindex="5"
						<c:if test="${requestScope.latitud !=null }">
										value='${requestScope.latitud}'
									</c:if> /></td>
					<td class="literal"><abbr title="Longitud">Lon.</abbr></td>
					<td class="literal"><input type="text" id="longitud"
						name="longitud" size="6" maxlength="6" class="dato"
						onchange="this.value=this.value.toUpperCase();" tabindex="6"
						<c:if test="${requestScope.longitud !=null }">
										value='${requestScope.longitud}'
									</c:if> /></td>
				</tr>
			</table>
		</fieldset>
		<table align="center" style="margin-bottom: 10px;">
			<tr>
				<td class="literal" style="padding-left: 15px;">REGA</td>
				<td class="literal"><input type="text" name="rega" size="18"
					id="rega" maxlength="14" class="dato"
					onchange="this.value=this.value.toUpperCase();" tabindex="7"
					<c:if test="${requestScope.rega !=null }">
									value='${requestScope.rega}'
								</c:if> />
				</td>
				<td class="literal" style="padding-left: 15px;">Sigla</td>
				<td class="literal"><input type="text" name="sigla" size="3"
					id="sigla" maxlength="3" class="dato"
					onchange="this.value=this.value.toUpperCase();" tabindex="8"
					<c:if test="${requestScope.sigla !=null }">
									value='${requestScope.sigla}'
								</c:if> />
				</td>
				<td class="literal" style="padding-left: 15px;">Subexplotación</td>
				<td class="literal"><input type="text" name="subexplotacion"
					size="3" id="subexplotacion" maxlength="3" class="dato"
					onchange="this.value=this.value.toUpperCase();" tabindex="9"
					<c:if test="${requestScope.subexplotacion  !=null }">
									value='${requestScope.subexplotacion}'
								</c:if> />
				</td>
				<td class="literal" style="padding-left: 15px;">Especie</td>
				<td class="literal"><input type="text" id="especie"
					name="especie" size="3" maxlength="3" class="dato"
					onchange="javascript:lupas.limpiarCampos('desc_especie');"
					tabindex="10"
					<c:if test="${requestScope.especie !=null }">
									value='${requestScope.especie}'
								</c:if> />
					<input class="dato" id="desc_especie" name="desc_especie" size="16"
					readonly="readonly"
					<c:if test="${requestScope.desc_especie !=null }">
									value='${requestScope.desc_especie}'
								</c:if> />
					<img src="jsp/img/magnifier.png" style="cursor: hand;"
					onclick="javascript:lupas.muestraTabla('EspecieIN','principio', '', '');"
					alt="Buscar Especie" title="Buscar Especie" /></td>
				<td class="literal" style="padding-left: 15px;">Régimen</td>
				<td class="literal"><input type="text" id="regimen"
					name="regimen" size="3" maxlength="3" class="dato"
					onchange="javascript:lupas.limpiarCampos('desc_regimen');"
					tabindex="11"
					<c:if test="${requestScope.regimen !=null }">
									value='${requestScope.regimen}'
								</c:if> />
					<input class="dato" id="desc_regimen" name="desc_regimen" size="16"
					readonly="readonly"
					<c:if test="${requestScope.desc_regimen !=null }">
									value='${requestScope.desc_regimen}'
								</c:if> />
					<img src="jsp/img/magnifier.png" style="cursor: hand;"
					onclick="javascript:lupas.muestraTabla('RegimenIN','principio', '', '');"
					alt="Buscar Régimen" title="Buscar Régimen" /></td>
			</tr>
		</table>
	</fieldset>
</div>