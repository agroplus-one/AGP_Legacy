<div id="visor_CultivoIN" class="window" style="display: none; width: 40%; top: 20%; left: 32%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('CultivoIN');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE CULTIVOS</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Código:&nbsp;&nbsp;
						<input type="text" id="filtro_codCultivoIN" value="" size="3" maxlength="3"
							onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('CultivoIN','principio','','');}" />
					</td>
					<td class="literal" nowrap="nowrap">
						Nombre:&nbsp;&nbsp;
						<input type="text" id="filtro_CultivoIN" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('CultivoIN','principio','','');}" />&nbsp;
					</td>	
					<td>
						<a class="bot" href="javascript:lupas.muestraTabla('CultivoIN','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_CultivoIN" colspan="3"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_CultivoIN" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_CultivoIN" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_CultivoIN" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_CultivoIN" value="" />
<input type="hidden" id="posicion_CultivoIN" value="-${numEle }" />
<input type="hidden" id="campoRestriccion_CultivoIN" value="id.codcultivo" />
<input type="hidden" id="valorRestriccion_CultivoIN" value="${listaCodCultivos}" />
<input type="hidden" id="operadorRestriccion_CultivoIN" value="in" />
<input type="hidden" id="campoRestriccion_CultivoNE" value="id.codcultivo" />
<input type="hidden" id="valorRestriccion_CultivoNE" value="999" />
<input type="hidden" id="operadorRestriccion_CultivoNE" value="ne" />