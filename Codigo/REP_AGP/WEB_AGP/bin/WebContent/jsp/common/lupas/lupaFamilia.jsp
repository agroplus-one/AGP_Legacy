<div id="visor_Familia" class="window" style="display: none; width: 70%; top: 20%; left: 15%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('Familia');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE FAMILIAS</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						C&oacute;d. Familia
						<input type="text" id="filtro_codFamilia" value="" size="5" maxlength="9"
							onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('Familia','principio','','');}" />
					</td>
					<td class="literal" nowrap="nowrap">
						Nombre Familia
						<input type="text" id="filtro_Familia" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('Familia','principio','','');}" />&nbsp;
					</td>	
					<td>	
						<a class="bot" href="javascript:lupas.muestraTabla('Familia','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_Familia" colspan="3"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_Familia" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_Familia" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_Familia" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_Familia" value="" />
<input type="hidden" id="posicion_Familia" value="-${numEle }" />