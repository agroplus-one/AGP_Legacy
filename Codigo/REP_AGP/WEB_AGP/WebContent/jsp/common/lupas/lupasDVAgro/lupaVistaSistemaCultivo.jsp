<div id="visor_VistaSistemaCultivo" class="window" style="display: none; width: 40%; top: 20%; left: 20%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('VistaSistemaCultivo');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">SISTEMA CULTIVO</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Descripci&oacute;n:&nbsp;&nbsp;
						<input type="text" id="filtro_VistaSistemaCultivo" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('VistaSistemaCultivo','principio','','');}" />&nbsp;
						<a class="bot" href="javascript:lupas.muestraTabla('VistaSistemaCultivo','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_VistaSistemaCultivo"></td> 
				</tr>
			</table>
		</div>
		<div id="paginacion_VistaSistemaCultivo" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_VistaSistemaCultivo" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_VistaSistemaCultivo" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_VistaSistemaCultivo" value="" />
<input type="hidden" id="posicion_VistaSistemaCultivo" value="-${numEle}" />
<input type="hidden" id="campoRestriccion_VistaSistemaCultivo" value="id.codmodulo" />
<input type="hidden" id="valorRestriccion_VistaSistemaCultivo" value="${listCodModulos}" />
<input type="hidden" id="operadorRestriccion_VistaSistemaCultivo" value="in" />