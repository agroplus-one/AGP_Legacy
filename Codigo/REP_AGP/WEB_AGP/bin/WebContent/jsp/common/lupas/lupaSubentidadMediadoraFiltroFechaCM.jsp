<div id="visor_SubentidadMediadoraFiltroFechaCM" class="window" style="display: none; width: 70%; top: 20%; left: 16%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('SubentidadMediadoraFiltroFechaCM');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE SUBENTIDADES MEDIADORAS PARA EL CAMBIO MASIVO</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Código:&nbsp;&nbsp;
						<input type="text" id="filtro_codSubentidadMediadoraFiltroFechaCM" value="" size="3" maxlength="4"
							onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('SubentidadMediadoraFiltroFechaCM','principio','','');}" />
					</td>
					<td class="literal" nowrap="nowrap">
						Subentidad Mediadora:&nbsp;&nbsp;
						<input type="text" id="filtro_SubentidadMediadoraFiltroFechaCM" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('SubentidadMediadoraFiltroFechaCM','principio','','');}" />&nbsp;
					</td>	
					<td>
						<a class="bot" href="javascript:lupas.muestraTabla('SubentidadMediadoraFiltroFechaCM','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_SubentidadMediadoraFiltroFechaCM" colspan="3"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_SubentidadMediadoraFiltroFechaCM" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_SubentidadMediadoraFiltroFechaCM" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_SubentidadMediadoraFiltroFechaCM" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_SubentidadMediadoraFiltroFechaCM" value="" />
<input type="hidden" id="posicion_SubentidadMediadoraFiltroFechaCM" value="-${numEle }" />