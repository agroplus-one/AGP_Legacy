<div id="visor_RegimenIN" class="window" style="display: none; width: 60%; top: 20%; left: 20%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('RegimenIN');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE REGÍMENES</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Código:&nbsp;&nbsp;
						<input type="text" id="filtro_codRegimenIN" value="" size="3" maxlength="3"
							onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('RegimenIN','principio','','');}" />
					</td>
					<td class="literal" nowrap="nowrap">
						Régimen:&nbsp;&nbsp;
						<input type="text" id="filtro_RegimenIN" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('RegimenIN','principio','','');}" />&nbsp;
					</td>	
					<td>
						<a class="bot" href="javascript:lupas.muestraTabla('RegimenIN','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_RegimenIN" colspan="3"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_RegimenIN" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_RegimenIN" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_RegimenIN" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_RegimenIN" value="" />
<input type="hidden" id="posicion_RegimenIN" value="-${numEle }" />
<input type="hidden" id="campoRestriccion_RegimenIN" value="id.codRegimen" />
<input type="hidden" id="valorRestriccion_RegimenIN" value="${listaCodRegimenes}" />
<input type="hidden" id="operadorRestriccion_RegimenIN" value="in" />
<input type="hidden" id="campoRestriccion_RegimenNE" value="id.codRegimen" />
<input type="hidden" id="valorRestriccion_RegimenNE" value="999" />
<input type="hidden" id="operadorRestriccion_RegimenNE" value="ne" />