<div id="visor_FactoresTipoCapitalIN" class="window" style="display: none; width: 36%; top: 20%; left: 32%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('FactoresTipoCapitalIN');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">TIPO DE CAPITAL</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						T. Capital:&nbsp;&nbsp;
						<input type="text" id="filtro_FactoresTipoCapitalIN" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('FactoresTipoCapitalIN','principio','','');}" />&nbsp;
						<a class="bot" href="javascript:lupas.muestraTabla('FactoresTipoCapitalIN','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_FactoresTipoCapitalIN"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_FactoresTipoCapitalIN" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_FactoresTipoCapitalIN" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_FactoresTipoCapitalIN" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_FactoresTipoCapitalIN" value="" />
<input type="hidden" id="posicion_FactoresTipoCapitalIN" value="-${numEle }" />
<input type="hidden" id="campoRestriccion_FactoresTipoCapitalIN" value="id.codmodulo" />
<input type="hidden" id="valorRestriccion_FactoresTipoCapitalIN" value="${listCodModulos}" />
<input type="hidden" id="operadorRestriccion_FactoresTipoCapitalIN" value="in" />
<input type="hidden" id="campoRestriccion_FactoresTipoCapitalIN2" value="id.codvalor" />
<input type="hidden" id="valorRestriccion_FactoresTipoCapitalIN2" value="${listaTiposCapital}" />
<input type="hidden" id="operadorRestriccion_FactoresTipoCapitalIN2" value="in" />
<input type="hidden" id="campoRestriccion_FactoresTipoCapitalLT" value="id.codvalor" />
<input type="hidden" id="valorRestriccion_FactoresTipoCapitalLT" value="${filtroTipoCapitalLT}" />
<input type="hidden" id="operadorRestriccion_FactoresTipoCapitalLT" value="lt" />
<input type="hidden" id="campoRestriccion_FactoresTipoCapitalGE" value="id.codvalor" />
<input type="hidden" id="valorRestriccion_FactoresTipoCapitalGE" value="${filtroTipoCapitalGE}" />
<input type="hidden" id="operadorRestriccion_FactoresTipoCapitalGE" value="ge" />
