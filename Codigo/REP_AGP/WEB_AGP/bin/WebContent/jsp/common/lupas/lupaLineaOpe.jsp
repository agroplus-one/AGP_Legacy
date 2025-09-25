<div id="visor_LineaOpe" class="window" style="display: none; width: 50%; top: 20%; left: 28%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('LineaOpe');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE LÍNEAS</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Cód. Línea:&nbsp;&nbsp;
						<input type="text" id="filtro_codLineaOpe" value="" size="3" maxlength="3"
							onkeypress="if(event.keyCode=='13'){muestraLupaLineaOp(${mayorIgual2015});}" />
					</td>
					<td class="literal" nowrap="nowrap">
						Nombre:&nbsp;&nbsp;
							<input type="text" id="filtro_LineaOpe" value="" onchange="this.value = this.value.toUpperCase();"
							onkeypress="if(event.keyCode=='13'){muestraLupaLineaOp(${mayorIgual2015});}" />
					</td>
					<td><a class="bot" href="javascript:muestraLupaLineaOp(${mayorIgual2015})">Consultar</a></td>
				</tr>
				<tr align="center">
					<td colspan="3" id="tabla_visor_LineaOpe"></td>
				</tr>
			</table>
		</div>
			
			<div id="paginacion_LineaOpe" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
				<table width="90%" cellpadding="0" cellspacing="0">
					<tr>
						<td id="registros_LineaOpe" align="center" class="literal"></td>
					</tr>
					<tr>
						<td id="paginas_LineaOpe" align="center" class="literal" valign="middle"></td>
					</tr>
				</table>
			</div>
		</div>
	</div>

<input type="hidden" id="maxima_LineaOpe" value="" />
<input type="hidden" id="posicion_LineaOpe" value="-${numEle }" />
<input type="hidden" id="campoRestriccion" value="codplan" />
<input type="hidden" id="valorRestriccion" value="2015" />
<input type="hidden" id="operadorRestriccion" value="" />




<script type="text/javascript">
	function muestraLupaLineaOp(mayorIgual2015){		
		if($('#mayorIgual2015').val()== 'true'){
			$('#operadorRestriccion').val('ge');//mayor o igual			
		}else{
			$('#operadorRestriccion').val('lt');			
		}	
		lupas.muestraTabla('LineaOpe','principio', '', '');
	}
</script>	


