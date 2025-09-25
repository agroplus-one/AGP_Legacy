<div id="visor_VistaEspecieIN" class="window" style="display: none; width: 36%; top: 20%; left: 32%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('VistaEspecieIN');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE ESPECIES</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Especie:&nbsp;&nbsp;
						<input type="text" id="filtro_VistaEspecieIN" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('VistaEspecieIN','principio','','');}" />&nbsp;
						<a class="bot" href="javascript:lupas.muestraTabla('VistaEspecieIN','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_VistaEspecieIN"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_VistaEspecieIN" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_VistaEspecieIN" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_VistaEspecieIN" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_VistaEspecieIN" value="" />
<input type="hidden" id="posicion_VistaEspecieIN" value="-${numEle }" />
<input type="hidden" id="campoRestriccion_VistaEspecieIN" value="id.codespecie" />
<input type="hidden" id="valorRestriccion_VistaEspecieIN" value="${listaCodEspecies}" />
<input type="hidden" id="operadorRestriccion_VistaEspecieIN" value="in" />
<input type="hidden" id="campoRestriccion_VistaEspecieNE" value="id.codespecie" />
<input type="hidden" id="valorRestriccion_VistaEspecieNE" value="999" />
<input type="hidden" id="operadorRestriccion_VistaEspecieNE" value="ne" />