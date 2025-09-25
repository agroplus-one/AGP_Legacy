<div id="visor_CambiarTitular" class="window" style="display: none; width: 60%; top: 20%; left: 10%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('CambiarTitular');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">CAMBIAR TITULAR</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						NIF/CIF&nbsp;<input	class="dato" type="text" id="filtro_nifcif_CambiarTitular" size="10" maxlength="10" value="" 
						onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('CambiarTitular','principio','','');}" />
						
						&nbsp;&nbsp;&nbsp;Nombre/Razón Social&nbsp;<input class="dato" type="text" id="filtro_fullName_CambiarTitular" size="40" maxlength="100" value=""
						onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('CambiarTitular','principio','','');}" />&nbsp;
						<a class="bot" href="javascript:lupas.muestraTabla('CambiarTitular','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_CambiarTitular" onclick="cambiarTitular();"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_CambiarTitular" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_CambiarTitular" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_CambiarTitular" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_CambiarTitular" value="" />
<input type="hidden" id="posicion_CambiarTitular" value="${numEle}" />
<input type="hidden" id="codEntidadLupa" name="codEntidadLupa" value="" />
<input type="hidden" id="codEntMedLupa" name="codEntMedLupa" value="" />
<input type="hidden" id="codSubentidadLupa" name="codSubentidadLupa" value="" />