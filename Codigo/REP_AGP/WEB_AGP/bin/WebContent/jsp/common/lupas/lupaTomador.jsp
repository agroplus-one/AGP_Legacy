<div id="visor_Tomador" class="window" style="display: none; width: 70%; top: 20%; left: 15%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('Tomador');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE TOMADORES</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						CIF:&nbsp;&nbsp;
						<input type="text" id="filtro_codTomador" value="" size="5" maxlength="9"
							onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('Tomador','principio','','');}" />
					</td>
					<td class="literal" nowrap="nowrap">
						Razón social:&nbsp;&nbsp;
						<input type="text" id="filtro_Tomador" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('Tomador','principio','','');}" />&nbsp;
					</td>	
					<td>	
						<a class="bot" href="javascript:lupas.muestraTabla('Tomador','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_Tomador" colspan="3"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_Tomador" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_Tomador" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_Tomador" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_Tomador" value="" />
<input type="hidden" id="posicion_Tomador" value="-${numEle }" />