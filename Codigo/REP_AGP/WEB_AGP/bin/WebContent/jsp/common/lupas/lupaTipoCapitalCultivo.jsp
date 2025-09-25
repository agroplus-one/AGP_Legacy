<div id="visor_TipoCapitalCultivo" class="window" style="display: none; width: 35%; top: 20%; left: 32%; background-color: white; border: 1px solid black; border-color: black; position: absolute; z-index: 1006;">
	<div id="buttons" align="right" style="padding: 3px;background-color:#e5e5e5">
		<a class="bot"	style="padding:3px;text-decoration:none;font-weight: bold; font-size: 11px;filter:alpha(opacity=50);-moz-opacity:.50;opacity:.50;" href="javascript:lupas.closeWindow('TipoCapitalCultivo');">X</a>
	</div>
	<div class="conten">
		<p class="titulopag" align="center" style="margin:0px;padding:0px;font-size:12px;">LISTA DE TIPOS DE CAPITAL</p>
		<div class="panel2 isrt" style="width:99%;">
			<table width="90%" align="center" id="tablaContenedora">
				<tr align="left">
					<td class="literal" nowrap="nowrap">
						Descripción:&nbsp;&nbsp;
						<input type="text" id="filtro_TipoCapitalCultivo" value="" onchange="this.value = this.value.toUpperCase();"
						onkeypress="if(event.keyCode=='13'){lupas.muestraTabla('TipoCapitalCultivo','principio','','');}" />&nbsp;
						<a class="bot" href="javascript:lupas.muestraTabla('TipoCapitalCultivo','principio','','')">Consultar</a>
					</td>
				</tr>
				<tr align="center">
					<td id="tabla_visor_TipoCapitalCultivo"></td>
				</tr>
			</table>
		</div>
		<div id="paginacion_TipoCapitalCultivo" class="literal" style="border: 1px solid #CCC; padding-bottom:5px">
			<table width="90%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="registros_TipoCapitalCultivo" align="center" class="literal"></td>
				</tr>
				<tr>
					<td id="paginas_TipoCapitalCultivo" align="center" class="literal" valign="middle"><img src="jsp/img/ajax-loading.gif"/></td>
				</tr>
			</table>
		</div>
	</div>
</div>
				
<input type="hidden" name="cultivoLupaTCCul" id="cultivoLupaTCCul" value=""/>				
<input type="hidden" name="codmoduloLupaTCCul" id="codmoduloLupaTCCul" value=""/>
<input type="hidden" id="maxima_TipoCapitalCultivo" value="" />
<input type="hidden" id="posicion_TipoCapitalCultivo" value="-${numEle }" />

<script type="text/javascript">
	function muestraLupaTipoCapitalCultivo(){
	
		//limpiamos el div que muestra los registros de la lupa
		document.getElementById("tabla_visor_TipoCapitalCultivo").innerHTML="";
		document.getElementById("registros_TipoCapitalCultivo").innerHTML="";
		
		var codmodulo = $('#codmodulo').val();
		var cultivo = $('#cultivo').val();
		
		if(codmodulo == '99999'){
			$('#codmoduloLupaTCCul').val('');
		}else{
			$('#codmoduloLupaTCCul').val(codmodulo);			
		}
		if(cultivo == '999'){
			$('#cultivoLupaTCCul').val('');
		}else{
			$('#cultivoLupaTCCul').val(cultivo);
		}
			
		lupas.muestraTabla('TipoCapitalCultivo','principio', 'id.codtipocapital', 'ASC');	
}

</script>