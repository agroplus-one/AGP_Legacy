<div id="visor_VistaRegimenIN" class="window" style="display: none; width: 36%; top: 20%; left: 32%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('VistaRegimenIN');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE REGÍMENES</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Regimen:&nbsp;&nbsp;
						<input type="text" id="filtro_VistaRegimenIN" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('VistaRegimenIN','principio','','');}" />&nbsp;
						<a class="bot" href="javascript:lupas.muestraTabla('VistaRegimenIN','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_VistaRegimenIN"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_VistaRegimenIN" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_VistaRegimenIN" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_VistaRegimenIN" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_VistaRegimenIN" value="" />
<input type="hidden" id="posicion_VistaRegimenIN" value="-${numEle }" />
<input type="hidden" id="campoRestriccion_VistaRegimenIN" value="id.codregimen" />
<input type="hidden" id="valorRestriccion_VistaRegimenIN" value="${listaCodRegimens}" />
<input type="hidden" id="operadorRestriccion_VistaRegimenIN" value="in" />
<input type="hidden" id="campoRestriccion_VistaRegimenNE" value="id.codregimen" />
<input type="hidden" id="valorRestriccion_VistaRegimenNE" value="999" />
<input type="hidden" id="operadorRestriccion_VistaRegimenNE" value="ne" />