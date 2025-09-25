<div class="panel2 isrt" style="width: 100%;">
	<fieldset style="width: 99%;">
		<legend class="literal">Datos de Filtro</legend>
		<fieldset style="width: 79%; float: left">
			<legend class="literal">Ubicaci�n</legend>
			<table align="center">
				<tr>
					<td class="literal"><abbr title="Provincia">Prov.</abbr></td>
					<td class="literal"><input type="text" id="provincia"
						name="provincia" size="2" maxlength="2" class="dato"
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
						onclick="javascript:lupas.muestraTabla('Provincia','principio', '', '');"
						alt="Buscar Provincia" title="Buscar Provincia" /></td>
					<td class="literal"><abbr title="Comarca">Com.</abbr></td>
					<td class="literal"><input type="text" id="comarca"
						name="comarca" size="2" maxlength="2" class="dato"
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
						onclick="javascript:lupas.muestraTabla('Comarca','principio', '', '');"
						alt="Buscar Comarca" title="Buscar Comarca" /></td>
					<td class="literal"><abbr title="T�rmino">T�rm.</abbr></td>
					<td class="literal"><input type="text" id="termino"
						name="termino" size="3" maxlength="3" class="dato"
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
						<img src="jsp/img/magnifier.png" style="cursor: hand;"
						onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');"
						alt="Buscar T�rmino" title="Buscar T�rmino" /></td>
					<td class="literal"><abbr title="Subt�rmino">Subt.</abbr></td>
					<td class="literal"><input type="text" id="subtermino"
						name="subtermino" size="1" maxlength="1" class="dato"
						onchange="this.value=this.value.toUpperCase();" tabindex="4"
						<c:if test="${requestScope.subtermino !=null }">
										value='${requestScope.subtermino}'
									</c:if> />
						<img src="jsp/img/magnifier.png" style="cursor: hand;"
						onclick="javascript:lupas.muestraTabla('Termino','principio', '', '');"
						alt="Buscar T�rmino" title="Buscar T�rmino" /></td>
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
		<table align="left" style="margin: 10px; width:100%">
			<tr>
				<td class="literal" style="padding-left: 15px;">REGA</td>
				<td class="literal"><input type="text" name="rega" size="14"
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
				<td class="literal" style="padding-left: 15px;">Subexplotaci�n</td>
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
					onclick="javascript:lupas.muestraTabla('Especie','principio', '', '');"
					alt="Buscar Especie" title="Buscar Especie" /></td>
				<td class="literal" style="padding-left: 15px;">R�gimen</td>
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
					onclick="javascript:lupas.muestraTabla('Regimen','principio', '', '');"
					alt="Buscar R�gimen" title="Buscar R�gimen" /></td>
			</tr>
			<tr>
				<td colspan="10" align="center">
					<table>
						<tr>
							<td class="literal" width="50%" align="right">Tipo Modificaci�n</td>
							<td>
								<select class="dato" id="tipoModificacion" name="tipoModificacion">
									<option value="">Todos</option>
									<option value="A">Alta</option>
									<option value="B">Baja</option>
									<option value="M">Modificaci�n</option>
								</select>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</fieldset>
</div>