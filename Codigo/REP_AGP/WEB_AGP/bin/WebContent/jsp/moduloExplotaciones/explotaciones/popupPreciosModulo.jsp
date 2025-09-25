<!--  popup Precios por módulo - Explotaciones -->
<div id="visor_divPreciosPorModulo" class="parcelasRepWindow" style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; z-index: 1005">
	<!--  header popup -->
	<div id="header-popup"
		style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
		<div
			style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">
			Precios por m&oacute;dulo </div>
		<a
			style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
			<span onclick="javascript:cerrarPopUpPreciosPorModulo()">x</span>
		</a>
	</div>

	<!--  body popup -->
	<div class="panelInformacion_content">
		<div id="panelInformacion" class="panelInformacion">
			<fieldset style="width:100%;">
				<table width="80%">
					<thead>
						<tr>
							<th class="literalbordeCabecera" width="30%">M&oacute;dulo</th>
							<th class="literalbordeCabecera" width="35%">Precio M&iacute;n.</th>
							<th class="literalbordeCabecera" width="35%">Precio M&aacute;x.</th>						
						</tr>
					</thead>
					<tbody id="idtbody_popupPreciosModulo">
						
					</tbody>
				</table>
			</fieldset>

			<div style="margin-top: 15px; clear: both">
				<a class="bot" href="javascript:cerrarPopUpPreciosPorModulo()">Cerrar</a>
			</div>
		</div>
	</div>
</div>

