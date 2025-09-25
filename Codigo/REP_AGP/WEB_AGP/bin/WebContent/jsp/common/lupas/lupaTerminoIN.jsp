<div id="visor_TerminoIN" class="window" style="display: none; width: 98%; top: 20%; left: 1%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('TerminoIN');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE TÉRMINOS</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="95%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Código:&nbsp;&nbsp;
						<input type="text" id="filtro_codTerminoIN" value="" size="3" maxlength="4"
							onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('TerminoIN','principio','','');}" />
					</td>
					<td class="literal" nowrap="nowrap">
						Término:&nbsp;&nbsp;
						<input type="text" id="filtro_TerminoIN" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('TerminoIN','principio','','');}" />&nbsp;
					</td>	
					<td>
						<a class="bot" href="javascript:lupas.muestraTabla('TerminoIN','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_TerminoIN" colspan="3"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_TerminoIN" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_TerminoIN" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_TerminoIN" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_TerminoIN" value="" />
<input type="hidden" id="posicion_TerminoIN" value="-${numEle }" />
<input type="hidden" id="campoRestriccion_TerminoIN" value="id.codtermino" />
<input type="hidden" id="valorRestriccion_TerminoIN" value="${listaCodTerminos}" />
<input type="hidden" id="operadorRestriccion_TerminoIN" value="in" />
<input type="hidden" id="campoRestriccion_TerminoNE" value="id.codtermino" />
<input type="hidden" id="valorRestriccion_TerminoNE" value="999" />
<input type="hidden" id="operadorRestriccion_TerminoNE" value="ne" />