<!-- TMR 12/06/2013 -->
<!-- popupCambioClase.jsp (show in cambiopolizasdefinitivas.jsp) -->
<!-- incluir "jsp/moduloPolizas/polizas/CambioClaseMasivo.js" 			-->

<c:if test="${perfil == 0}">
	<form action="cambioClaseMasivo.html" method="post" name="frmCambioClaseMasivo" id=frmCambioClaseMasivo>
	
		<input type="hidden" name="method" id="method" value="doCambioClaseMasivo"> 
		<input type="hidden" name="listaIdsPlz" id="listaIdsPlz" value="">
	
		<!--  popup Cambio Clase Masivo de Polizas -->
		<div id="divCambioClaseMasivo" class="parcelasRepWindow"
			style="color: #333333; -moz-border-radius: 4px 4px 4px 4px; padding: 0.2em; z-index: 1005">
			<!--  header popup -->
			<div id="header-popup"
				style="padding: 0.4em 1em; position: relative; color: #FFFFFF; font-weight: bold; -moz-border-radius: 4px 4px 4px 4px; background: #525583; height: 15px">
				<div
					style="float: left; margin: 0 0 0 0; font-size: 11px; line-height: 15px">
					Cambio de Clase </div>
				<a
					style="height: 18px; margin: -10px 0 0; padding: 1px; position: absolute; right: 0.3em; top: 50%; width: 19px; font-family: arial; font-size: 13px; font-weight: bold; cursor: hand; cursor: pointer">
					<span onclick="cerrarPopUpCambioClaseMasivo()">x</span>
				</a>
			</div>
	
			<!--  body popup -->
			<div class="panelInformacion_content">
				<div id="panelInformacion" class="panelInformacion">
					<table>
						<tr>
							<td class="literal">Nueva clase:</td>
							<td>
								<input type="text" name="claseCM" id="claseCM" size="11" maxlength="10" class="dato" /> 
							</td>
						</tr>
					</table>
	
					<div style="margin-top: 15px; clear: both">
						<a class="bot" href="javascript:cerrarPopUpCambioClaseMasivo()">Cancelar</a>
						<a class="bot" href="javascript:doCambioClaseMasivo()">Cambiar Clase</a>
					</div>
				</div>
			</div>
		</div>
	
	</form>
</c:if>