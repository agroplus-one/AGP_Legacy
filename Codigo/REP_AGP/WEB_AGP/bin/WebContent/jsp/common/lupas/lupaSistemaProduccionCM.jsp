<%-- <div id="visor_VistaSistemaProduccionCM" class="window" style="display: none; width: 40%; top: 20%; left: 20%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('VistaSistemaProduccionCM');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">SISTEMA DE PRODUCCI&Oacute;N</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Sistema de producci&oacute;n:&nbsp;&nbsp;
						<input type="text" id="filtro_VistaSistemaProduccionCM" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('VistaSistemaProduccionCM','principio','','');}" />&nbsp;
						<a class="bot" href="javascript:lupas.muestraTabla('VistaSistemaProduccionCM','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_VistaSistemaProduccionCM"></td> 
				</tr>
			</table>
		</div>
		<div id="paginacion_VistaSistemaProduccionCM" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_VistaSistemaProduccionCM" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_VistaSistemaProduccionCM" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_VistaSistemaProduccionCM" value="" />
<input type="hidden" id="posicion_VistaSistemaProduccionCM" value="-${numEle }" /> --%>



<div id="visor_VistaSistemaProduccionCM_IN" class="window" style="display: none; width: 40%; top: 20%; left: 20%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('VistaSistemaProduccionCM_IN');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">SISTEMA DE PRODUCCI&Oacute;N</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Sistema de producci&oacute;n:&nbsp;&nbsp;
						<input type="text" id="filtro_VistaSistemaProduccionCM_IN" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('VistaSistemaProduccionCM_IN','principio','','');}" />&nbsp;
						<a class="bot" href="javascript:lupas.muestraTabla('VistaSistemaProduccionCM_IN','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_VistaSistemaProduccionCM_IN"></td> 
				</tr>
			</table>
		</div>
		<div id="paginacion_VistaSistemaProduccionCM_IN" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_VistaSistemaProduccionCM_IN" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_VistaSistemaProduccionCM_IN" align="center" class="literal" valign="middle"></td>
				</tr>
			</table>
		</div>
	</div>
</div>

<input type="hidden" id="maxima_VistaSistemaProduccionCM_IN" value="" />
<input type="hidden" id="posicion_VistaSistemaProduccionCM_IN" value="-${numEle }" />
<input type="hidden" id="campoRestriccion_VistaSistemaProduccionCM_IN" value="id.codmodulo" />
<input type="hidden" id="valorRestriccion_VistaSistemaProduccionCM_IN" value="${datos.listCodModulos_cm }" />
<input type="hidden" id="operadorRestriccion_VistaSistemaProduccionCM_IN" value="in" />

