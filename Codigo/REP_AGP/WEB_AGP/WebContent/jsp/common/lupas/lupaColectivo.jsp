<div id="visor_Colectivo" class="window" style="display: none; width: 50%; top: 20%; left: 32%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('Colectivo');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE COLECTIVOS</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Nombre Colectivo&nbsp;<input	class="dato" type="text" id="filtro_Colectivo" size="40" maxlength="40" value=""
						onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('Colectivo','principio','','');}" />&nbsp;
						<a class="bot" href="javascript:lupas.muestraTabla('Colectivo','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_Colectivo"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_Colectivo" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_Colectivo" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_Colectivo" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_Colectivo" value="" />
<input type="hidden" id="posicion_Colectivo" value="-${numEle }" />